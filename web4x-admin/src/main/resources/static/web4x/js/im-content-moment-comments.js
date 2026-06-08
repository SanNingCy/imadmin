/**
 * IM 动态评论管理
 * - GET    /dyomm/dyComm/list
 * - DELETE /dyomm/dyComm/delete?ids=
 */

var imContentMomentCommentsApi = ctx + "dyomm/dyComm";

function imContentMomentCommentsGetQueryParam(name) {
    var match = window.location.search.match(new RegExp("[?&]" + name + "=([^&]*)"));
    return match ? decodeURIComponent(match[1].replace(/\+/g, " ")) : "";
}

function imContentMomentCommentsQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("moment-comments-form");
    if (formValues.idno) query.idno = formValues.idno;
    if (formValues.lianghao) query["u.lianghao"] = formValues.lianghao;
    if (formValues.dyId) query["dy.id"] = formValues.dyId;
    return imOmitEmptyParams(query);
}

function imContentMomentCommentsEscapeHtml(text) {
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

function imContentMomentCommentsFormatEllipsis(val) {
    if (val == null || val === "") return "-";
    var text = String(val);
    var safe = imContentMomentCommentsEscapeHtml(text);
    return '<span class="comment-ellipsis-inner" title="' + safe + '">' + safe + "</span>";
}

function imContentMomentCommentsRemove(id) {
    $.modal.confirm("确定删除该评论吗？", function () {
        $.ajax({
            url: imContentMomentCommentsApi + "/delete",
            type: "DELETE",
            data: { ids: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "删除成功");
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

function imContentMomentCommentsInitTable(canDelete) {
    var dyId = imContentMomentCommentsGetQueryParam("dyId");
    if (dyId) {
        $("#moment-comments-form input[name='dyId']").val(dyId);
    }

    imInitTable({
        url: imContentMomentCommentsApi + "/list",
        formId: "moment-comments-form",
        queryParams: imContentMomentCommentsQueryParams,
        responseHandler: imPageResponse,
        modalName: "动态评论",
        columns: [
            {
                field: "u.idno",
                title: "用户IDNO",
                sortable: true,
                class: "comment-ellipsis",
                formatter: function (value, row) {
                    return imContentMomentCommentsFormatEllipsis(row.u ? row.u.idno : "-");
                }
            },
            {
                field: "u.lianghao",
                title: "靓号",
                sortable: true,
                formatter: function (value, row) {
                    var val = row.u ? row.u.lianghao : "";
                    return val || "-";
                }
            },
            {
                field: "u.nickname",
                title: "用户",
                sortable: true,
                formatter: function (value, row) {
                    return row.u && row.u.nickname ? row.u.nickname : "-";
                }
            },
            {
                field: "dy.id",
                title: "动态ID",
                sortable: true,
                class: "comment-ellipsis",
                formatter: function (value, row) {
                    return imContentMomentCommentsFormatEllipsis(row.dy ? row.dy.id : "-");
                }
            },
            {
                field: "dy.info",
                title: "动态",
                sortable: true,
                class: "comment-ellipsis",
                formatter: function (value, row) {
                    return imContentMomentCommentsFormatEllipsis(row.dy ? row.dy.info : "-");
                }
            },
            {
                field: "to.nickname",
                title: "回复给谁",
                sortable: true,
                formatter: function (value, row) {
                    return row.to && row.to.nickname ? row.to.nickname : "-";
                }
            },
            {
                field: "title",
                title: "内容",
                sortable: true,
                class: "comment-ellipsis",
                formatter: function (v) { return imContentMomentCommentsFormatEllipsis(v); }
            },
            { field: "createDate", title: "添加时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    if (!canDelete) return "-";
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imContentMomentCommentsRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>';
                }
            }
        ]
    });
}
