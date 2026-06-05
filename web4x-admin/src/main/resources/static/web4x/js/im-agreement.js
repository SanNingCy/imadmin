/**
 * IM 平台协议管理（接口复用 web4x-im）
 * - GET  /agreement/agreement/list
 * - GET  /agreement/agreement/queryById?id=xxx
 * - POST /agreement/agreement/save
 */

var imAgreementApi = ctx + "agreement/agreement";

function imAgreementQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("agreement-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imAgreementEllipsis(val, max) {
    if (!val) return "";
    var text = String(val);
    max = max || 40;
    var short = text.length > max ? text.substring(0, max) + "..." : text;
    return '<span title="' + text.replace(/"/g, "&quot;") + '">' + short + "</span>";
}

function imAgreementResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imAgreementInitModalDatetime() {
    $("#agreement-modal .agreement-datetime").datetimepicker({
        format: "yyyy-mm-dd hh:ii:ss",
        autoclose: true,
        todayBtn: true
    });
}

function imAgreementResetModalForm() {
    $("#agreement-id").val("");
    $("#agreement-title").val("");
    $("#agreement-content").val("");
    $("#agreement-updateDate").val("");
}

function imAgreementFillModal(info) {
    $("#agreement-id").val(info.id || "");
    $("#agreement-title").val(info.title || "");
    $("#agreement-content").val(info.content || "");
    $("#agreement-updateDate").val(info.updateDate || "");
}

function imAgreementSetModalReadOnly(readOnly) {
    $("#agreement-modal-form :input").prop("disabled", readOnly);
}

function imAgreementShowLayerModal(mode, readOnly) {
    var titles = { edit: "修改协议", view: "查看协议" };
    layer.open({
        type: 1,
        title: titles[mode] || "平台协议",
        area: ["560px", "520px"],
        shadeClose: true,
        content: $("#agreement-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        success: function () {
            imAgreementInitModalDatetime();
        },
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imAgreementSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imAgreementOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imAgreementResetModalForm();
    imAgreementSetModalReadOnly(readOnly);

    if (id) {
        $.ajax({
            url: imAgreementApi + "/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imAgreementFillModal(imAgreementResolveEntity(res, "agreement"));
                    imAgreementShowLayerModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imAgreementShowLayerModal(mode, readOnly);
    }
}

function imAgreementSave(layerIndex) {
    var id = $("#agreement-id").val();
    var title = $.trim($("#agreement-title").val());
    if (!id) return $.modal.alertWarning("缺少记录ID");
    if (!title) return $.modal.alertWarning("标题不能为空");

    var payload = {
        id: id,
        title: title,
        content: $("#agreement-content").val(),
        updateDate: $.trim($("#agreement-updateDate").val()) || undefined
    };

    $.ajax({
        url: imAgreementApi + "/save",
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

function imAgreementEditSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || rows.length !== 1) {
        return $.modal.alertWarning("请选择一条记录");
    }
    imAgreementOpenModal("edit", rows[0].id);
}

function imAgreementInitTable(canView, canEdit) {
    imInitTable({
        url: imAgreementApi + "/list",
        formId: "agreement-form",
        queryParams: imAgreementQueryParams,
        responseHandler: imPageResponse,
        modalName: "平台协议",
        columns: [
            { checkbox: true },
            {
                field: "title",
                title: "标题",
                sortable: true,
                formatter: function (value, row) {
                    if (!canView) return imAgreementEllipsis(value, 30);
                    return '<a href="javascript:void(0)" onclick="imAgreementOpenModal(\'view\',\'' + row.id + '\')">' + imAgreementEllipsis(value, 30) + "</a>";
                }
            },
            {
                field: "content",
                title: "内容",
                formatter: function (v) { return imAgreementEllipsis(v, 60); }
            },
            { field: "updateDate", title: "更新时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imAgreementOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imAgreementOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
