/**
 * 广场朋友圈 - 朋友圈评论列表
 * - GET    /admin/piamom/moment/comment/page
 * - DELETE /admin/piamom/moment/comment/delete?id=
 */

function imPiamomMomentCommentQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("piamom-moment-comment-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPiamomMomentCommentRemove(id) {
    $.modal.confirm("确定删除该评论吗？", function () {
        $.ajax({
            url: imPiamomApi + "/moment/comment/delete",
            type: "DELETE",
            data: { id: id },
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

function imPiamomMomentCommentInitTable(canDelete) {
    imInitTable({
        url: imPiamomApi + "/moment/comment/page",
        formId: "piamom-moment-comment-form",
        queryParams: imPiamomMomentCommentQueryParams,
        responseHandler: imPageResponse,
        modalName: "朋友圈评论",
        columns: [
            { field: "id", title: "ID", sortable: true, width: 80 },
            { field: "momentId", title: "动态ID", sortable: true, width: 90 },
            { field: "userIdno", title: "ID号", sortable: true, width: 110 },
            { field: "userNickname", title: "昵称", sortable: true, width: 120 },
            { field: "parentId", title: "父评论ID", sortable: true, width: 100 },
            { field: "content", title: "内容", sortable: true, cellStyle: imEllipsisCell(260), formatter: function (v) { return imFormatText(v, 50); } },
            { field: "createdAt", title: "时间", sortable: true, width: 160 },
            {
                title: "操作",
                align: "center",
                width: 90,
                formatter: function (value, row) {
                    if (!canDelete) {
                        return "-";
                    }
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomMomentCommentRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>';
                }
            }
        ]
    });
}
