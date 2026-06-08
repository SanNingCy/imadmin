/**
 * IM 公告管理配置（前端页面，接口复用 web4x-im）
 * - POST /notif/admin/list
 * - GET  /notif/admin/get/{id}
 * - POST /notif/admin/save
 * - DELETE /notif/admin/delete/{ids}
 * - POST /notif/admin/notice/upload
 */

var imAnnounceApi = ctx + "notif/admin";
var imAnnounceMemberApi = ctx + "member/member";
var imAnnounceSelectedUsers = [];
var imAnnounceEditorReady = false;
var imAnnouncePendingContent = "";

var IM_ANNOUNCE_STATUS = { 0: "关闭", 1: "开启" };
var IM_ANNOUNCE_FORCE = { 0: "否", 1: "是" };
var IM_ANNOUNCE_TYPE = { 1: "首页", 2: "消息页", 3: "设置页" };
var IM_ANNOUNCE_UNIT = { 1: "秒", 2: "分钟", 3: "小时", 4: "天" };

function imAnnounceQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("announce-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imAnnounceEllipsis(val, max) {
    if (!val) return "";
    var text = String(val);
    max = max || 40;
    var short = text.length > max ? text.substring(0, max) + "..." : text;
    return '<span title="' + text.replace(/"/g, "&quot;") + '">' + short + "</span>";
}

function imAnnounceFormatTargetUsers(row) {
    var idnos = row.targetUserIdnos;
    if (idnos && idnos.length) {
        return imAnnounceEllipsis(idnos.join(", "), 60);
    }
    return "-";
}

function imAnnounceFormatInterval(row) {
    if (row.intervalNumber == null) return "";
    var unit = IM_ANNOUNCE_UNIT[row.intervalUnit] || "";
    return row.intervalNumber + unit;
}

function imAnnounceResolveData(res) {
    if (!res) return null;
    if (res.data) return res.data;
    return res;
}

function imAnnounceNormalizeTargetUserIds(value) {
    if (Array.isArray(value)) {
        return value.map(String).filter(Boolean);
    }
    if (typeof value === "string" && value) {
        try {
            var parsed = JSON.parse(value);
            if (Array.isArray(parsed)) {
                return parsed.map(String).filter(Boolean);
            }
        } catch (e) {
            return value.split(",").map(function (s) { return $.trim(s); }).filter(Boolean);
        }
    }
    return [];
}

function imAnnounceRenderTargetUsers() {
    var $box = $("#announce-target-users-display");
    $box.empty();
    if (!imAnnounceSelectedUsers.length) {
        return;
    }
    imAnnounceSelectedUsers.forEach(function (user) {
        $box.append(
            '<span class="label label-info">' +
                (user.label || user.id) +
                "</span> "
        );
    });
    $("#announce-targetUserIds-json").val(JSON.stringify(imAnnounceSelectedUsers.map(function (u) { return u.id; })));
}

function imAnnounceClearTargetUsers() {
    imAnnounceSelectedUsers = [];
    imAnnounceRenderTargetUsers();
}

function imAnnounceInitSearchDatetime() {
    $("#announce-form .time-input").datetimepicker({
        format: "yyyy-mm-dd hh:ii:ss",
        autoclose: true,
        todayBtn: true
    });
}

function imAnnounceInitModalDatetime() {
    $("#announce-modal .announce-datetime").datetimepicker({
        format: "yyyy-mm-dd hh:ii:ss",
        autoclose: true,
        todayBtn: true
    });
}

function imAnnounceInitEditor(readOnly) {
    if (!imAnnounceEditorReady) {
        $("#announce-content-editor").summernote({
            height: 260,
            lang: "zh-CN",
            placeholder: "请输入公告内容",
            followingToolbar: false,
            dialogsInBody: true,
            callbacks: {
                onImageUpload: function (files) {
                    imAnnounceSendSummernoteFile(files[0], this);
                }
            }
        });
        imAnnounceEditorReady = true;
    }
    if (readOnly) {
        $("#announce-content-editor").summernote("disable");
    } else {
        $("#announce-content-editor").summernote("enable");
    }
}

function imAnnounceSendSummernoteFile(file, editor) {
    var data = new FormData();
    data.append("file", file);
    $.ajax({
        type: "POST",
        url: ctx + "common/upload",
        data: data,
        cache: false,
        contentType: false,
        processData: false,
        dataType: "json",
        success: function (result) {
            if (result.code === web_status.SUCCESS || result.code === 200) {
                $(editor).summernote("insertImage", result.url, result.fileName || "image");
            } else {
                $.modal.alertError(result.msg || "图片上传失败");
            }
        },
        error: function () {
            $.modal.alertWarning("图片上传失败");
        }
    });
}

function imAnnounceRefreshThumb(url) {
    var $preview = $("#announce-thumb-preview");
    if (!url) {
        $preview.empty();
        return;
    }
    $preview.html('<img src="' + url + '" alt="缩略图"/>');
}

function imAnnounceUploadThumb(input) {
    var file = input.files && input.files[0];
    if (!file) return;
    var fd = new FormData();
    fd.append("file", file);
    $.modal.loading("正在上传图片...");
    $.ajax({
        url: ctx + "common/upload",
        type: "POST",
        data: fd,
        processData: false,
        contentType: false,
        dataType: "json",
        success: function (res) {
            $.modal.closeLoading();
            if (res && (res.code === web_status.SUCCESS || res.code === 200)) {
                $("#announce-imageUrl").val(res.url || "");
                imAnnounceRefreshThumb(res.url);
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

function imAnnounceResetModalForm() {
    $("#announce-id").val("");
    $("#announce-noticeTitle").val("");
    $("#announce-previewContent").val("");
    $("#announce-status").val("");
    $("#announce-forcePopup").val("");
    $("#announce-number").val("");
    $("#announce-intervalNumber").val("");
    $("#announce-intervalUnit").val("");
    $("#announce-noticeType").val("");
    $("#announce-startTime").val("");
    $("#announce-endTime").val("");
    $("#announce-imageUrl").val("");
    imAnnounceClearTargetUsers();
    imAnnounceRefreshThumb("");
    imAnnouncePendingContent = "";
    if (imAnnounceEditorReady) {
        $("#announce-content-editor").summernote("code", "");
    }
}

function imAnnounceFillModal(info) {
    $("#announce-id").val(info.id || "");
    $("#announce-noticeTitle").val(info.noticeTitle || "");
    $("#announce-previewContent").val(info.previewContent || "");
    $("#announce-status").val(info.status != null ? String(info.status) : "");
    $("#announce-forcePopup").val(info.forcePopup != null ? String(info.forcePopup) : "");
    $("#announce-number").val(info.number != null ? info.number : "");
    $("#announce-intervalNumber").val(info.intervalNumber != null ? info.intervalNumber : "");
    $("#announce-intervalUnit").val(info.intervalUnit != null ? String(info.intervalUnit) : "");
    $("#announce-noticeType").val(info.noticeType != null ? String(info.noticeType) : "");
    $("#announce-startTime").val(info.startTime || "");
    $("#announce-endTime").val(info.endTime || "");
    $("#announce-imageUrl").val(info.imageUrl || "");
    imAnnounceRefreshThumb(info.imageUrl || "");

    var userIds = imAnnounceNormalizeTargetUserIds(info.targetUserIds);
    var idnos = info.targetUserIdnos || [];
    imAnnounceSelectedUsers = userIds.map(function (uid, idx) {
        return { id: uid, label: idnos[idx] || uid };
    });
    imAnnounceRenderTargetUsers();

    imAnnouncePendingContent = info.content || "";
    if (imAnnounceEditorReady) {
        $("#announce-content-editor").summernote("code", imAnnouncePendingContent);
        imAnnouncePendingContent = "";
    }
}

function imAnnounceSetModalReadOnly(readOnly) {
    $("#announce-modal-form :input").not("#announce-content-editor").prop("disabled", readOnly);
    $("#announce-pick-users-btn, #announce-clear-users-btn, #announce-thumb-upload-btn").toggle(!readOnly);
    if (imAnnounceEditorReady) {
        if (readOnly) {
            $("#announce-content-editor").summernote("disable");
        } else {
            $("#announce-content-editor").summernote("enable");
        }
    }
}

function imAnnounceShowLayerModal(mode, readOnly) {
    var titles = { add: "新建公告", edit: "修改公告", view: "查看公告" };
    layer.open({
        type: 1,
        title: titles[mode] || "公告",
        area: ["960px", "90%"],
        shadeClose: true,
        content: $("#announce-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        success: function () {
            imAnnounceInitModalDatetime();
            imAnnounceInitEditor(readOnly);
            if (imAnnouncePendingContent && imAnnounceEditorReady) {
                $("#announce-content-editor").summernote("code", imAnnouncePendingContent);
                imAnnouncePendingContent = "";
            }
        },
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imAnnounceSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imAnnounceOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imAnnounceResetModalForm();
    imAnnounceSetModalReadOnly(readOnly);

    if (id) {
        $.ajax({
            url: imAnnounceApi + "/get/" + encodeURIComponent(id),
            type: "GET",
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imAnnounceFillModal(imAnnounceResolveData(res) || {});
                    imAnnounceShowLayerModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imAnnounceShowLayerModal(mode, readOnly);
    }
}

function imAnnounceWrapContentHtml(html) {
    return "<!doctype html><html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><title>announcement</title></head><body>" + html + "</body></html>";
}

function imAnnounceUploadContentHtml(html) {
    var blob = new Blob([imAnnounceWrapContentHtml(html)], { type: "text/html" });
    var file = new File([blob], new Date().getTime() + "_notice.html", { type: "text/html" });
    var fd = new FormData();
    fd.append("file", file);
    return $.ajax({
        url: imAnnounceApi + "/notice/upload",
        type: "POST",
        data: fd,
        processData: false,
        contentType: false,
        dataType: "json",
        beforeSend: imTableBeforeSend
    });
}

function imAnnounceSave(layerIndex) {
    var noticeTitle = $.trim($("#announce-noticeTitle").val());
    var previewContent = $.trim($("#announce-previewContent").val());
    var status = $("#announce-status").val();
    var forcePopup = $("#announce-forcePopup").val();
    var number = $("#announce-number").val();
    var intervalNumber = $("#announce-intervalNumber").val();
    var intervalUnit = $("#announce-intervalUnit").val();
    var startTime = $.trim($("#announce-startTime").val());
    var endTime = $.trim($("#announce-endTime").val());
    var content = imAnnounceEditorReady ? $("#announce-content-editor").summernote("code") : "";

    if (!noticeTitle) return $.modal.alertWarning("公告标题不能为空");
    if (!previewContent) return $.modal.alertWarning("预览内容不能为空");
    if (!status) return $.modal.alertWarning("请选择公告状态");
    if (forcePopup === "") return $.modal.alertWarning("请选择是否强弹");
    if (number === "" || number == null) return $.modal.alertWarning("通知次数不能为空");
    if (intervalNumber === "" || intervalNumber == null) return $.modal.alertWarning("弹窗间隔数不能为空");
    if (!intervalUnit) return $.modal.alertWarning("请选择弹窗间隔单位");
    if (!startTime) return $.modal.alertWarning("开始时间不能为空");
    if (!endTime) return $.modal.alertWarning("结束时间不能为空");
    if (!content || content === "<p><br></p>") return $.modal.alertWarning("公告内容不能为空");

    $.modal.loading("正在保存...");
    imAnnounceUploadContentHtml(content).done(function (uploadRes) {
        if (!(uploadRes && (uploadRes.success === true || uploadRes.code === 200))) {
            $.modal.closeLoading();
            $.modal.alertWarning((uploadRes && uploadRes.msg) ? uploadRes.msg : "富文本上传失败");
            return;
        }
        var uploadData = uploadRes.data || uploadRes;
        var contentUrl = uploadData.url || (uploadRes.data && uploadRes.data.url);
        if (!contentUrl) {
            $.modal.closeLoading();
            $.modal.alertWarning("富文本上传失败：未返回 URL");
            return;
        }

        var targetUserIds = imAnnounceNormalizeTargetUserIds($("#announce-targetUserIds-json").val());
        var payload = {
            id: $("#announce-id").val() || undefined,
            noticeTitle: noticeTitle,
            previewContent: previewContent,
            status: status,
            forcePopup: parseInt(forcePopup, 10),
            number: parseInt(number, 10),
            intervalNumber: parseInt(intervalNumber, 10),
            intervalUnit: parseInt(intervalUnit, 10),
            noticeType: $("#announce-noticeType").val() ? parseInt($("#announce-noticeType").val(), 10) : null,
            startTime: startTime,
            endTime: endTime,
            imageUrl: $("#announce-imageUrl").val() || null,
            content: content,
            contentUrl: contentUrl,
            targetUserIds: targetUserIds.length ? targetUserIds : null
        };

        $.ajax({
            url: imAnnounceApi + "/save",
            type: "POST",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify(payload),
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                $.modal.closeLoading();
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess(res.msg || "保存成功");
                    layer.close(layerIndex);
                    $("#bootstrap-table").bootstrapTable("refresh");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "保存失败");
                }
            },
            error: function () {
                $.modal.closeLoading();
                $.modal.alertWarning("保存失败");
            }
        });
    }).fail(function () {
        $.modal.closeLoading();
        $.modal.alertWarning("富文本上传失败");
    });
}

function imAnnounceRemove(ids) {
    $.modal.confirm("确定删除选中的公告吗？", function () {
        $.ajax({
            url: imAnnounceApi + "/delete/" + encodeURIComponent(ids),
            type: "DELETE",
            dataType: "json",
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

function imAnnounceRemoveSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || !rows.length) {
        return $.modal.alertWarning("请至少选择一条记录");
    }
    imAnnounceRemove(rows.map(function (row) { return row.id; }).join(","));
}

function imAnnounceEditSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || rows.length !== 1) {
        return $.modal.alertWarning("请选择一条记录");
    }
    imAnnounceOpenModal("edit", rows[0].id);
}

function imAnnounceSearchUsers() {
    $("#announce-user-table").bootstrapTable("refresh");
}

function imAnnounceOpenUserPicker() {
    layer.open({
        type: 1,
        title: "选择目标用户",
        area: ["760px", "520px"],
        shadeClose: true,
        content: $("#announce-user-picker"),
        btn: ["确定", "取消"],
        success: function () {
            if (!$("#announce-user-table").data("bootstrap.table")) {
                $("#announce-user-table").bootstrapTable({
                    url: imAnnounceMemberApi + "/list",
                    method: "get",
                    sidePagination: "server",
                    pagination: true,
                    pageSize: 10,
                    pageList: [10, 20, 50],
                    clickToSelect: true,
                    maintainSelected: true,
                    queryParams: function (params) {
                        var pageNo = params.offset / params.limit + 1;
                        var q = imBuildPageQuery(pageNo, params.limit);
                        var search = $.common.formToJSON("announce-user-search-form");
                        return $.extend(q, imOmitEmptyParams(search));
                    },
                    responseHandler: imPageResponse,
                    columns: [
                        { checkbox: true },
                        { field: "idno", title: "账号" },
                        { field: "nickname", title: "昵称" }
                    ],
                    ajaxOptions: {
                        beforeSend: imTableBeforeSend
                    }
                });
            } else {
                $("#announce-user-table").bootstrapTable("refresh");
            }
            var selectedIds = imAnnounceSelectedUsers.map(function (u) { return u.id; });
            $("#announce-user-table").bootstrapTable("checkBy", { field: "id", values: selectedIds });
        },
        yes: function (index) {
            var rows = $("#announce-user-table").bootstrapTable("getSelections") || [];
            imAnnounceSelectedUsers = rows.map(function (row) {
                return {
                    id: String(row.id),
                    label: row.idno || row.nickname || String(row.id)
                };
            });
            imAnnounceRenderTargetUsers();
            layer.close(index);
        }
    });
}

function imAnnounceInitTable(canView, canEdit, canDelete) {
    imAnnounceInitSearchDatetime();
    imInitListMediaPreview();
    imInitTable({
        url: imAnnounceApi + "/list",
        method: "post",
        formId: "announce-form",
        queryParams: imAnnounceQueryParams,
        responseHandler: imPageResponse,
        modalName: "公告",
        escape: false,
        onPostBody: function () {
            imBindListMediaPreview($("#bootstrap-table"));
        },
        columns: [
            { checkbox: true },
            {
                field: "id",
                title: "id",
                sortable: true,
                formatter: function (value, row) {
                    if (!canView) return imAnnounceEllipsis(value, 16);
                    return '<a href="javascript:void(0)" onclick="imAnnounceOpenModal(\'view\',\'' + row.id + '\')">' + imAnnounceEllipsis(value, 16) + "</a>";
                }
            },
            {
                field: "noticeTitle",
                title: "公告标题",
                sortable: true,
                formatter: function (v) { return imAnnounceEllipsis(v, 30); }
            },
            {
                field: "forcePopup",
                title: "是否强制弹出",
                formatter: function (v) { return IM_ANNOUNCE_FORCE[v] || v; }
            },
            {
                field: "contentUrl",
                title: "公告富文本URL",
                formatter: function (v) { return imAnnounceEllipsis(v, 36); }
            },
            {
                field: "previewContent",
                title: "预览内容",
                class: "announce-content-cell",
                formatter: function (v) { return imAnnounceEllipsis(v, 40); }
            },
            {
                field: "targetUserIdnos",
                title: "目标用户",
                formatter: function (v, row) { return imAnnounceFormatTargetUsers(row); }
            },
            {
                field: "status",
                title: "公告状态",
                formatter: function (v) { return IM_ANNOUNCE_STATUS[v] || v; }
            },
            {
                field: "imageUrl",
                title: "公告缩略图",
                width: 100,
                escape: false,
                cellStyle: function () {
                    return { css: { "text-align": "left", "vertical-align": "middle" } };
                },
                formatter: function (v, row) {
                    return imFormatListMedia(v, "announce-thumb-" + row.id);
                }
            },
            {
                field: "noticeType",
                title: "公告位置",
                formatter: function (v) { return IM_ANNOUNCE_TYPE[v] || v || ""; }
            },
            {
                field: "intervalNumber",
                title: "弹窗时间间隔",
                formatter: function (v, row) { return imAnnounceFormatInterval(row); }
            },
            { field: "createTime", title: "创建时间", sortable: true },
            { field: "updateTime", title: "更新时间", sortable: true },
            { field: "startTime", title: "开始时间", sortable: true },
            { field: "endTime", title: "结束时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imAnnounceOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imAnnounceOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imAnnounceRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
