/**
 * 广场朋友圈 - 朋友圈点赞列表
 * - GET    /admin/piamom/moment/like/page
 * - DELETE /admin/piamom/moment/like/delete?id=
 */

function imPiamomMomentLikeQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("piamom-moment-like-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPiamomMomentLikeRemove(id) {
    $.modal.confirm("确定删除该点赞记录吗？", function () {
        $.ajax({
            url: imPiamomApi + "/moment/like/delete",
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

function imPiamomMomentLikeInitTable(canDelete) {
    imPiamomInitTable({
        url: imPiamomApi + "/moment/like/page",
        formId: "piamom-moment-like-form",
        queryParams: imPiamomMomentLikeQueryParams,
        responseHandler: imPageResponse,
        modalName: "朋友圈点赞",
        columns: [
            { field: "id", title: "ID", sortable: true, width: 80 },
            { field: "momentId", title: "动态ID", sortable: true, width: 90 },
            { field: "userId", title: "用户ID", sortable: true, cellStyle: imEllipsisCell(180), formatter: function (v) { return imFormatText(v, 28); } },
            { field: "userIdno", title: "ID号", sortable: true, width: 110 },
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
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomMomentLikeRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>';
                }
            }
        ]
    });
}
