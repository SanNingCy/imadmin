/**
 * 广场朋友圈 - 广场点赞列表
 * - GET    /admin/piamom/square/like/page
 * - DELETE /admin/piamom/square/like/delete?id=
 */

function imPiamomSquareLikeQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("piamom-square-like-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPiamomSquareLikeRemove(id) {
    $.modal.confirm("确定删除该点赞记录吗？", function () {
        $.ajax({
            url: imPiamomApi + "/square/like/delete",
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

function imPiamomSquareLikeInitTable(canDelete) {
    imInitTable({
        url: imPiamomApi + "/square/like/page",
        formId: "piamom-square-like-form",
        queryParams: imPiamomSquareLikeQueryParams,
        responseHandler: imPageResponse,
        modalName: "广场点赞",
        columns: [
            { field: "id", title: "ID", sortable: true, width: 80 },
            { field: "squareId", title: "帖子ID", sortable: true, width: 90 },
            { field: "userId", title: "用户ID", sortable: true, width: 160, formatter: function (v) { return imFormatText(v, 24); } },
            { field: "userIdno", title: "IDNO", sortable: true, width: 110 },
            { field: "userNickname", title: "昵称", sortable: true, width: 120 },
            { field: "createdAt", title: "时间", sortable: true, width: 160 },
            {
                title: "操作",
                align: "center",
                width: 90,
                formatter: function (value, row) {
                    if (!canDelete) {
                        return "-";
                    }
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomSquareLikeRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>';
                }
            }
        ]
    });
}
