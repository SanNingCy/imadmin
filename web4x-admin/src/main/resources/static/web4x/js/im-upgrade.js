/**
 * IM 版本更新管理页面逻辑（只负责前端渲染/交互）
 * 后端接口：
 * - /upgrade/upgrade/list
 * - /upgrade/upgrade/queryById?id=xxx
 * - /upgrade/upgrade/save
 * - /upgrade/upgrade/delete?ids=1,2
 * - /api/uploadFile (表单 key: file)
 */

var imUpgradeApi = ctx + "upgrade/upgrade";

function imUpgradeQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("upgrade-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imUpgradeFormatUrl(val) {
    if (!val) return "";
    var html = [];
    val.split("|").forEach(function (item) {
        if (!item) return;
        var name = decodeURIComponent(item.substring(item.lastIndexOf("/") + 1));
        html.push('<a href="' + item + '" target="_blank" rel="noreferrer">' + name + "</a>");
    });
    return html.join(" ");
}

function imUpgradeFormatQr(val) {
    return imFormatListMedia(val, "upgrade-qrcode-preview");
}

function imUpgradeFormatContent(val) {
    if (!val) return "";
    var text = String(val);
    var short = text.length > 80 ? text.substring(0, 80) + "..." : text;
    return '<span title="' + text.replace(/"/g, "&quot;") + '">' + short + "</span>";
}

function imUpgradeResolveEntity(res, key) {
    if (!res) {
        return {};
    }
    if (res[key]) {
        return res[key];
    }
    if (res.data && res.data[key]) {
        return res.data[key];
    }
    return {};
}

/** 展示已上传安装包；编辑模式可删除 */
function imUpgradeRefreshFileDisplay(readOnly) {
    var url = $("#upgrade-url").val();
    var $wrap = $("#upgrade-file-wrap");
    if (!url) {
        $wrap.hide();
        return;
    }
    var name = url.substring(url.lastIndexOf("/") + 1);
    try {
        name = decodeURIComponent(name);
    } catch (e) {
        // ignore
    }
    $("#upgrade-file-link").attr("href", url).text(name);
    $wrap.show();
    $("#upgrade-file-remove").toggle(!readOnly);
}

/** 删除已选安装包（仅清空表单，保存时生效） */
function imUpgradeRemoveFile() {
    $("#upgrade-url").val("");
    $("#upgrade-qrCode").val("");
    $("#upgrade-qr-preview").empty();
    imUpgradeRefreshFileDisplay(false);
}

function imUpgradeOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    $("#upgrade-modal-form :input").prop("disabled", readOnly);
    $("#upgrade-upload-btn").toggle(!readOnly);

    $("#upgrade-id").val(id || "");
    $("#upgrade-number").val("");
    $("#upgrade-version").val("");
    $("#upgrade-type").val("");
    $("#upgrade-url").val("");
    $("#upgrade-qrCode").val("");
    $("#upgrade-content").val("");
    $("#upgrade-file-wrap").hide();
    $("#upgrade-qr-preview").empty();

    if (id) {
        $.ajax({
            url: imUpgradeApi + "/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            xhrFields: { withCredentials: true },
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    var info = imUpgradeResolveEntity(res, "upgrade");
                    $("#upgrade-number").val(info.number || "");
                    $("#upgrade-version").val(info.version || "");
                    $("#upgrade-type").val(info.type != null ? String(info.type) : "");
                    $("#upgrade-url").val(info.url || "");
                    $("#upgrade-qrCode").val(info.qrCode || "");
                    $("#upgrade-content").val(info.content || "");

                    imUpgradeRefreshFileDisplay(readOnly);
                    if (info.qrCode) {
                        $("#upgrade-qr-preview").html(
                            '<img src="' +
                                info.qrCode +
                                '" style="width:120px;height:120px;object-fit:contain;" alt="二维码"/>'
                        );
                    }
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
                imUpgradeShowLayerModal(mode, readOnly);
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
                imUpgradeShowLayerModal(mode, readOnly);
            }
        });
    } else {
        imUpgradeShowLayerModal(mode, readOnly);
    }
}

function imUpgradeShowLayerModal(mode, readOnly) {
    var titles = { add: "新建版本", edit: "修改版本", view: "查看版本" };
    var title = titles[mode] || "版本更新";

    layer.open({
        type: 1,
        title: title,
        area: ["560px", "640px"],
        shadeClose: true,
        content: $("#upgrade-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imUpgradeSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imUpgradeSave(layerIndex) {
    var url = $("#upgrade-url").val();
    var number = $("#upgrade-number").val();
    if (!number) {
        $.modal.alertWarning("升级编号不能为空");
        return;
    }
    if (!url) {
        $.modal.alertWarning("请上传安装包");
        return;
    }

    var payload = {
        id: $("#upgrade-id").val() || undefined,
        number: number,
        version: $("#upgrade-version").val(),
        type: $("#upgrade-type").val(),
        url: url,
        qrCode: $("#upgrade-qrCode").val(),
        content: $("#upgrade-content").val()
    };

    $.ajax({
        url: imUpgradeApi + "/save",
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess(res.msg || "保存成功");
                layer.close(layerIndex);
                $("#bootstrap-table").bootstrapTable("refresh");
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "保存失败");
            }
        },
        error: function () {
            $.modal.alertWarning("保存失败");
        }
    });
}

function imUpgradeRemove(ids) {
    $.modal.confirm("确定删除选中的版本记录吗？", function () {
        $.ajax({
            url: imUpgradeApi + "/delete",
            type: "DELETE",
            dataType: "json",
            data: { ids: ids },
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess(res.msg || "删除成功");
                    $("#bootstrap-table").bootstrapTable("refresh");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "删除失败");
                }
            },
            error: function () {
                $.modal.alertWarning("删除失败");
            }
        });
    });
}

function imUpgradeRemoveSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || rows.length === 0) {
        $.modal.alertWarning("请至少选择一条记录");
        return;
    }
    var ids = rows.map(function (row) { return row.id; }).join(",");
    imUpgradeRemove(ids);
}

function imUpgradeEditSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || rows.length !== 1) {
        $.modal.alertWarning("请选择一条记录");
        return;
    }
    imUpgradeOpenModal("edit", rows[0].id);
}

function imUpgradeUploadFile(input) {
    var file = input.files && input.files[0];
    if (!file) return;

    var fd = new FormData();
    fd.append("file", file);

    $.modal.loading("正在上传，请稍候...");
    $.ajax({
        url: ctx + "api/uploadFile",
        type: "POST",
        data: fd,
        processData: false,
        contentType: false,
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            $.modal.closeLoading();
            if (res && (res.success === true || res.code === 200)) {
                // uploadFile 返回结构：{ url, qrCode } 或 data: { url, qrCode }
                var data = res.data || res;
                if (!data.url && data.data) {
                    data = data.data;
                }
                $("#upgrade-url").val(data.url || "");
                $("#upgrade-qrCode").val(data.qrCode || "");
                imUpgradeRefreshFileDisplay(false);

                if (data.qrCode) {
                    $("#upgrade-qr-preview").html(
                        '<img src="' +
                            data.qrCode +
                            '" style="width:120px;height:120px;object-fit:contain;" alt="二维码"/>'
                    );
                } else {
                    $("#upgrade-qr-preview").empty();
                }
                $.modal.msgSuccess("上传成功");
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "上传失败");
            }
        },
        error: function () {
            $.modal.closeLoading();
            $.modal.alertWarning("上传失败");
        },
        complete: function () {
            input.value = "";
        }
    });
}

function imUpgradeExport() {
    imExportExcel("upgrade-form", {
        exportUrl: imUpgradeApi + "/export",
        modalName: "版本更新",
        id: "bootstrap-table",
        exportMethod: "get"
    });
}

/** 初始化版本列表表格（列定义放独立 js，避免 Thymeleaf 内联脚本转义导致语法错误） */
function imUpgradeInitTable(canView, canEdit, canDelete) {
    imInitListMediaPreview();
    imInitTable({
        url: imUpgradeApi + "/list",
        formId: "upgrade-form",
        queryParams: imUpgradeQueryParams,
        responseHandler: imPageResponse,
        modalName: "版本更新",
        escape: false,
        onPostBody: function () {
            imBindListMediaPreview($("#bootstrap-table"));
        },
        columns: [
            { checkbox: true },
            {
                field: "number",
                title: "升级编号",
                sortable: true,
                formatter: function (value, row) {
                    if (!canView) {
                        return value;
                    }
                    return '<a href="javascript:void(0)" onclick="imUpgradeOpenModal(\'view\',\'' + row.id + '\')">' + value + "</a>";
                }
            },
            { field: "version", title: "版本号", sortable: true },
            {
                field: "url",
                title: "地址",
                formatter: function (value) {
                    return imUpgradeFormatUrl(value);
                }
            },
            {
                field: "qrCode",
                title: "二维码",
                width: 100,
                escape: false,
                cellStyle: function () {
                    return { css: { "text-align": "left", "vertical-align": "middle" } };
                },
                formatter: function (value, row) {
                    return imFormatListMedia(value, "upgrade-qrcode-" + row.id);
                }
            },
            {
                field: "content",
                title: "更新内容",
                class: "upgrade-content-cell",
                formatter: function (value) {
                    return imUpgradeFormatContent(value);
                }
            },
            {
                field: "type",
                title: "强制更新",
                align: "center",
                formatter: function (value) {
                    return String(value) === "1" ? "是" : "否";
                }
            },
            { field: "updateDate", title: "更新时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imUpgradeOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imUpgradeOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imUpgradeRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}

