/**
 * 广场朋友圈 - 广场列表
 * - GET    /admin/piamom/square/page
 * - GET    /admin/piamom/square/queryById?id=
 * - DELETE /admin/piamom/square/delete?id=
 * - POST   /admin/piamom/square/updateTop  { id, isTop }
 * - POST   /admin/piamom/square/updateStakeStatus?id=&odicStakeStatus=
 */

var imPiamomSquareListDrawerSquareId = null;
var imPiamomSquareListDrawerInited = false;

function imPiamomSquareListQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("piamom-square-list-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPiamomSquareListOpenDetail(id) {
    $.ajax({
        url: imPiamomApi + "/square/queryById",
        type: "GET",
        data: { id: id },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (!(res && (res.success === true || res.code === 200))) {
                return $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
            }
            var info = imPiamomResolveData(res, "square") || {};
            $("#psd-id").text(info.id != null ? info.id : "-");
            $("#psd-userIdno").text(info.userIdno || "-");
            $("#psd-userNickname").text(info.userNickname || "-");
            $("#psd-type").text(imPiamomFormatSquareType(info.type));
            $("#psd-content").text(info.content || "-");
            $("#psd-media").html(imPiamomFormatSquareMedia(info.imageUrls, info.video, "detail-" + id));
            $("#psd-stake").html(imPiamomFormatOdicStakeLine(info.odicStake, info.odicStakeStatus));
            $("#psd-commentGate").text(imPiamomFormatCommentGateFlags(info.commentGateFlags));
            $("#psd-commentCreditMin").text(info.commentCreditMin != null ? info.commentCreditMin : "-");
            $("#psd-top").html(imPiamomFormatTop(info.isTop));
            $("#psd-status").html(imPiamomFormatStatus(info.status));
            $("#psd-createdAt").text(info.createdAt || "-");
            layer.open({
                type: 1,
                title: "广场帖子详情",
                area: ["720px", "560px"],
                shadeClose: true,
                content: $("#piamom-square-detail-modal"),
                success: function () {
                    imPiamomBindMediaImagesIn($("#piamom-square-detail-modal"));
                }
            });
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imPiamomSquareListUpdateTop(id, isTop) {
    var actionText = String(isTop) === "1" ? "置顶" : "取消置顶";
    $.modal.confirm("确定" + actionText + "该帖子吗？", function () {
        $.ajax({
            url: imPiamomApi + "/square/updateTop",
            type: "POST",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify({ id: id, isTop: isTop }),
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "操作成功");
                    $("#bootstrap-table").bootstrapTable("refresh");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "操作失败");
                }
            },
            error: function () {
                $.modal.alertWarning("操作失败");
            }
        });
    });
}

function imPiamomSquareListRefundStake(id) {
    $.modal.confirm("确定将该帖质押状态改为「已退还」吗？", function () {
        $.ajax({
            url: imPiamomApi + "/square/updateStakeStatus",
            type: "POST",
            data: { id: id, odicStakeStatus: 1 },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "更新成功");
                    $("#bootstrap-table").bootstrapTable("refresh");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "更新失败");
                }
            },
            error: function () {
                $.modal.alertWarning("更新失败");
            }
        });
    });
}

function imPiamomSquareListRemove(id) {
    $.modal.confirm("确定删除该帖子吗？", function () {
        $.ajax({
            url: imPiamomApi + "/square/delete",
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

function imPiamomSquareListInitDrawerTables() {
    if (imPiamomSquareListDrawerInited) {
        return;
    }
    imPiamomSquareListDrawerInited = true;

    imInitTable({
        id: "piamom-square-drawer-comment-table",
        url: imPiamomApi + "/square/comment/page",
        showSearch: false,
        showRefresh: false,
        showToggle: false,
        showColumns: false,
        queryParams: function (params) {
            return imPiamomBuildSquareDrawerQueryParams(params, imPiamomSquareListDrawerSquareId);
        },
        responseHandler: imPageResponse,
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "userIdno", title: "IDNO", width: 100 },
            { field: "userNickname", title: "昵称", width: 100 },
            { field: "parentId", title: "父评论ID", width: 90 },
            { field: "content", title: "内容", formatter: function (v) { return imFormatText(v, 40); } },
            { field: "createdAt", title: "时间", width: 150 }
        ]
    });

    imInitTable({
        id: "piamom-square-drawer-like-table",
        url: imPiamomApi + "/square/like/page",
        showSearch: false,
        showRefresh: false,
        showToggle: false,
        showColumns: false,
        queryParams: function (params) {
            return imPiamomBuildSquareDrawerQueryParams(params, imPiamomSquareListDrawerSquareId);
        },
        responseHandler: imPageResponse,
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "userId", title: "用户ID", width: 160, formatter: function (v) { return imFormatText(v, 24); } },
            { field: "userIdno", title: "IDNO", width: 100 },
            { field: "userNickname", title: "昵称", width: 100 },
            { field: "createdAt", title: "时间", width: 150 }
        ]
    });
}

function imPiamomSquareListRefreshDrawerTables() {
    $("#piamom-square-drawer-comment-table").bootstrapTable("refresh");
    $("#piamom-square-drawer-like-table").bootstrapTable("refresh");
}

function imPiamomSquareListOpenInteractionDrawer(squareId) {
    imPiamomSquareListDrawerSquareId = squareId;
    $("#piamom-square-interaction-title").text("广场帖子 #" + squareId + " 互动数据");
    imPiamomSquareListInitDrawerTables();
    layer.open({
        type: 1,
        title: false,
        area: ["920px", "560px"],
        shadeClose: true,
        content: $("#piamom-square-interaction-drawer"),
        success: function () {
            imPiamomSquareListRefreshDrawerTables();
        }
    });
}

function imPiamomSquareListInitTable(canView, canEdit, canDelete) {
    imInitTable(imApplyListMediaTableOptions({
        url: imPiamomApi + "/square/page",
        formId: "piamom-square-list-form",
        queryParams: imPiamomSquareListQueryParams,
        responseHandler: imPageResponse,
        modalName: "广场帖子",
        columns: [
            { field: "id", title: "ID", sortable: true, width: 70 },
            { field: "userIdno", title: "IDNO", sortable: true, width: 110 },
            { field: "userNickname", title: "昵称", sortable: true, width: 120, formatter: function (v) { return imFormatText(v, 16); } },
            {
                field: "content",
                title: "内容",
                sortable: true,
                cellStyle: imEllipsisCell(200),
                formatter: function (v, row) {
                    if (!v) {
                        return "-";
                    }
                    var safe = imEscapeHtml(String(v));
                    var short = safe.length > 30 ? safe.substring(0, 30) + "..." : safe;
                    return '<a href="javascript:void(0)" onclick="imPiamomSquareListOpenDetail(\'' + row.id + '\')" title="' + safe + '">' + short + "</a>";
                }
            },
            imBuildListMediaColumn("imageUrls", {
                title: "媒体",
                width: 220,
                cachePrefix: "square",
                format: function (v, row, cacheKey, max) {
                    return imPiamomFormatSquareMedia(v, row.video, cacheKey, max);
                }
            }),
            { field: "type", title: "类型", sortable: true, width: 80, formatter: imPiamomFormatSquareType },
            { field: "odicStake", title: "质押ODIC", sortable: true, width: 100 },
            { field: "odicStakeStatus", title: "质押状态", sortable: true, width: 100, formatter: imPiamomFormatOdicStakeStatus, escape: false },
            { field: "viewCount", title: "浏览", sortable: true, width: 70 },
            { field: "likeCount", title: "点赞", sortable: true, width: 70 },
            { field: "commentCount", title: "评论", sortable: true, width: 70 },
            { field: "isTop", title: "置顶", sortable: true, width: 80, formatter: imPiamomFormatTop, escape: false },
            { field: "status", title: "状态", sortable: true, width: 90, formatter: imPiamomFormatStatus, escape: false },
            { field: "createdAt", title: "发布时间", sortable: true, width: 160 },
            {
                title: "操作",
                align: "center",
                width: 320,
                formatter: function (value, row) {
                    var actions = [];
                    var isTop = String(row.isTop) === "1";
                    var staking = String(row.odicStakeStatus) === "0";
                    if (canView || canEdit) {
                        actions.push('<a class="btn btn-warning btn-xs" href="javascript:void(0)" onclick="imPiamomSquareListUpdateTop(\'' + row.id + '\',' + (isTop ? 0 : 1) + ')"><i class="fa fa-thumb-tack"></i>' + (isTop ? "取消置顶" : "置顶") + "</a> ");
                        if (staking) {
                            actions.push('<a class="btn btn-primary btn-xs" href="javascript:void(0)" onclick="imPiamomSquareListRefundStake(\'' + row.id + '\')"><i class="fa fa-exchange"></i>改质押</a> ');
                        }
                    }
                    actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imPiamomSquareListOpenInteractionDrawer(\'' + row.id + '\')"><i class="fa fa-comments"></i>评论/点赞</a> ');
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomSquareListRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.length ? actions.join("") : "-";
                }
            }
        ]
    }));
}
