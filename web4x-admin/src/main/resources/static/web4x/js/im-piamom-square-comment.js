/**
 * 广场朋友圈 - 广场评论列表
 * - GET    /admin/piamom/square/comment/page
 * - DELETE /admin/piamom/square/comment/delete?id=
 */

function imPiamomSquareCommentQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("piamom-square-comment-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPiamomSquareCommentRemove(id) {
    $.modal.confirm("确定删除该评论吗？", function () {
        $.ajax({
            url: imPiamomApi + "/square/comment/delete",
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

function imPiamomSquareCommentInitTable(canDelete) {
    imPiamomInitTable({
        url: imPiamomApi + "/square/comment/page",
        formId: "piamom-square-comment-form",
        queryParams: imPiamomSquareCommentQueryParams,
        responseHandler: imPageResponse,
        modalName: "广场评论",
        columns: [
            { field: "id", title: "ID", sortable: true, width: 80 },
            { field: "squareId", title: "帖子ID", sortable: true, width: 90 },
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
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomSquareCommentRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>';
                }
            }
        ]
    });
}
