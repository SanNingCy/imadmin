/**
 * 广场朋友圈 - 朋友圈列表
 * - GET    /admin/piamom/moment/page
 * - GET    /admin/piamom/moment/queryById?id=
 * - DELETE /admin/piamom/moment/delete?id=
 * - POST   /admin/piamom/moment/updateTop  { id, isTop }
 */

var imPiamomMomentListDrawerMomentId = null;
var imPiamomMomentListDrawerInited = false;

function imPiamomMomentListQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("piamom-moment-list-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPiamomMomentListOpenDetail(id) {
    $.ajax({
        url: imPiamomApi + "/moment/queryById",
        type: "GET",
        data: { id: id },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (!(res && (res.success === true || res.code === 200))) {
                return $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
            }
            var info = imPiamomResolveData(res, "moment") || {};
            $("#pmd-id").text(info.id != null ? info.id : "-");
            $("#pmd-userId").text(info.userId || "-");
            $("#pmd-userIdno").text(info.userIdno || "-");
            $("#pmd-userNickname").text(info.userNickname || "-");
            $("#pmd-content").text(info.content || "-");
            $("#pmd-images").html(imPiamomFormatMedia(info.imageUrls, "detail-" + id));
            $("#pmd-stats").text(
                (info.viewCount != null ? info.viewCount : 0) + " / "
                + (info.likeCount != null ? info.likeCount : 0) + " / "
                + (info.commentCount != null ? info.commentCount : 0) + " / "
                + (info.quoteCount != null ? info.quoteCount : 0)
            );
            $("#pmd-top").html(imPiamomFormatTop(info.isTop));
            $("#pmd-status").html(imPiamomFormatStatus(info.status));
            $("#pmd-createdAt").text(info.createdAt || "-");
            layer.open({
                type: 1,
                title: "朋友圈动态详情",
                area: ["680px", "520px"],
                shadeClose: true,
                content: $("#piamom-moment-detail-modal"),
                success: function () {
                    imPiamomBindMediaImagesIn($("#piamom-moment-detail-modal"));
                }
            });
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imPiamomMomentListUpdateTop(id, isTop) {
    var actionText = String(isTop) === "1" ? "置顶" : "取消置顶";
    $.modal.confirm("确定" + actionText + "该动态吗？", function () {
        $.ajax({
            url: imPiamomApi + "/moment/updateTop",
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

function imPiamomMomentListRemove(id) {
    $.modal.confirm("确定删除该动态吗？", function () {
        $.ajax({
            url: imPiamomApi + "/moment/delete",
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

function imPiamomMomentListInitDrawerTables() {
    if (imPiamomMomentListDrawerInited) {
        return;
    }
    imPiamomMomentListDrawerInited = true;

    imInitTable({
        id: "piamom-drawer-comment-table",
        url: imPiamomApi + "/moment/comment/page",
        showSearch: false,
        showRefresh: false,
        showToggle: false,
        showColumns: false,
        queryParams: function (params) {
            return imPiamomBuildDrawerQueryParams(params, imPiamomMomentListDrawerMomentId);
        },
        responseHandler: imPageResponse,
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "userIdno", title: "ID号", width: 100 },
            { field: "userNickname", title: "昵称", width: 100 },
            { field: "parentId", title: "父评论ID", width: 90 },
            { field: "content", title: "内容", formatter: function (v) { return imFormatText(v, 40); } },
            { field: "createdAt", title: "时间", width: 150 }
        ]
    });

    imInitTable({
        id: "piamom-drawer-like-table",
        url: imPiamomApi + "/moment/like/page",
        showSearch: false,
        showRefresh: false,
        showToggle: false,
        showColumns: false,
        queryParams: function (params) {
            return imPiamomBuildDrawerQueryParams(params, imPiamomMomentListDrawerMomentId);
        },
        responseHandler: imPageResponse,
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "userId", title: "用户ID", width: 160, formatter: function (v) { return imFormatText(v, 24); } },
            { field: "userIdno", title: "ID号", width: 100 },
            { field: "userNickname", title: "昵称", width: 100 },
            { field: "createdAt", title: "时间", width: 150 }
        ]
    });
}

function imPiamomMomentListRefreshDrawerTables() {
    $("#piamom-drawer-comment-table").bootstrapTable("refresh");
    $("#piamom-drawer-like-table").bootstrapTable("refresh");
}

function imPiamomMomentListOpenInteractionDrawer(momentId) {
    imPiamomMomentListDrawerMomentId = momentId;
    $("#piamom-interaction-title").text("朋友圈 #" + momentId + " 互动数据");
    imPiamomMomentListInitDrawerTables();
    layer.open({
        type: 1,
        title: false,
        area: ["920px", "560px"],
        shadeClose: true,
        content: $("#piamom-moment-interaction-drawer"),
        success: function () {
            imPiamomMomentListRefreshDrawerTables();
        }
    });
}

function imPiamomMomentListInitTable(canView, canEdit, canDelete) {
    imInitTable(imApplyListMediaTableOptions({
        url: imPiamomApi + "/moment/page",
        formId: "piamom-moment-list-form",
        queryParams: imPiamomMomentListQueryParams,
        responseHandler: imPageResponse,
        modalName: "朋友圈动态",
        columns: [
            { field: "id", title: "ID", sortable: true, width: 70 },
            { field: "userIdno", title: "ID号", sortable: true, width: 110 },
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
                    return '<a href="javascript:void(0)" onclick="imPiamomMomentListOpenDetail(\'' + row.id + '\')" title="' + safe + '">' + short + "</a>";
                }
            },
            imBuildListMediaColumn("imageUrls", { title: "图片", cachePrefix: "moment" }),
            { field: "viewCount", title: "浏览", sortable: true, width: 70 },
            { field: "likeCount", title: "点赞", sortable: true, width: 70 },
            { field: "commentCount", title: "评论", sortable: true, width: 70 },
            { field: "quoteCount", title: "引用", sortable: true, width: 70 },
            { field: "isTop", title: "置顶", sortable: true, width: 80, formatter: imPiamomFormatTop },
            { field: "status", title: "状态", sortable: true, width: 90, formatter: imPiamomFormatStatus },
            { field: "createdAt", title: "发布时间", sortable: true, width: 160 },
            {
                title: "操作",
                align: "center",
                width: 260,
                formatter: function (value, row) {
                    var actions = [];
                    var isTop = String(row.isTop) === "1";
                    if (canView || canEdit) {
                        actions.push('<a class="btn btn-warning btn-xs" href="javascript:void(0)" onclick="imPiamomMomentListUpdateTop(\'' + row.id + '\',' + (isTop ? 0 : 1) + ')"><i class="fa fa-thumb-tack"></i>' + (isTop ? "取消置顶" : "置顶") + "</a> ");
                    }
                    actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imPiamomMomentListOpenInteractionDrawer(\'' + row.id + '\')"><i class="fa fa-comments"></i>评论/点赞</a> ');
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomMomentListRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.length ? actions.join("") : "-";
                }
            }
        ]
    }));
}
