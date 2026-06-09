/**
 * 广场朋友圈 - 举报记录审核
 * - GET  /admin/piamom/reportRecord/page
 * - GET  /admin/piamom/reportRecord/queryById?id=
 * - POST /admin/piamom/reportRecord/audit  { id, auditStatus, auditUserId }
 */

var imPiamomReportRecordDetailLayerIndex = null;
var imPiamomReportRecordDetailId = null;
var imPiamomReportRecordDetailTargetType = null;
var imPiamomReportRecordDetailTargetId = null;
var imPiamomReportRecordDetailAuditStatus = null;
var imPiamomReportRecordCanAudit = false;
var imPiamomReportRecordDrawerInited = false;
var imPiamomReportRecordDrawerTargetType = null;
var imPiamomReportRecordDrawerTargetId = null;

function imPiamomReportRecordQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("piamom-report-record-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPiamomReportRecordResolveImages(record) {
    if (record.imageUrlList && record.imageUrlList.length) {
        return record.imageUrlList;
    }
    return imPiamomParseImageUrls(record.imageUrls);
}

function imPiamomReportRecordResolvePostMedia(record) {
    var images = record.postImageUrlList && record.postImageUrlList.length
        ? record.postImageUrlList
        : imPiamomParseImageUrls(record.postImageUrls);
    var videos = record.postVideoList && record.postVideoList.length
        ? record.postVideoList
        : imPiamomParseImageUrls(record.postVideo);
    return { images: images, videos: videos };
}

function imPiamomReportRecordFillDetail(record) {
    var id = record.id != null ? record.id : "-";
    $("#rrd-id").text(id);
    $("#rrd-auditStatus").html(imPiamomFormatAuditStatus(record.auditStatus));
    $("#rrd-reporterNickname").text(record.reporterNickname || "-");
    $("#rrd-reporterIdno").text(record.reporterIdno || "-");
    $("#rrd-reportTypeName").text(record.reportTypeName || "-");
    $("#rrd-target").text(
        imPiamomFormatTargetType(record.targetType) + " / " + (record.targetId != null ? record.targetId : "-")
    );
    $("#rrd-reason").text(record.reason || "-");
    $("#rrd-images").html(imPiamomFormatMedia(imPiamomReportRecordResolveImages(record), "report-evidence-" + id));
    $("#rrd-publisherNickname").text(record.publisherNickname || "-");
    $("#rrd-publisherIdno").text(record.publisherIdno || "-");
    $("#rrd-postId").text(record.postId != null ? record.postId : "-");
    $("#rrd-postStatus").html(
        record.postStatus == null ? "-" : imPiamomFormatOdicStakeStatus(record.postStatus)
    );
    $("#rrd-postVisibleStatus").html(
        record.postVisibleStatus == null ? "-" : imPiamomFormatVisibleStatus(record.postVisibleStatus)
    );
    $("#rrd-postContent").text(record.postContent || "-");

    var media = imPiamomReportRecordResolvePostMedia(record);
    var postVideo = media.videos.length ? media.videos.join(",") : "";
    $("#rrd-postMedia").html(
        imPiamomFormatSquareMedia(
            media.images.length ? media.images : record.postImageUrls,
            postVideo,
            "report-post-" + id
        )
    );
}

function imPiamomReportRecordUpdateDetailActions() {
    $("#report-record-detail-actions").show();
    if (imPiamomReportRecordDetailTargetId) {
        $("#report-record-btn-interaction").show();
    } else {
        $("#report-record-btn-interaction").hide();
    }
    if (imPiamomReportRecordCanAudit && String(imPiamomReportRecordDetailAuditStatus) === "0") {
        $("#report-record-btn-approve").show();
        $("#report-record-btn-reject").show();
    } else {
        $("#report-record-btn-approve").hide();
        $("#report-record-btn-reject").hide();
    }
}

function imPiamomReportRecordCloseDetail() {
    if (imPiamomReportRecordDetailLayerIndex != null) {
        layer.close(imPiamomReportRecordDetailLayerIndex);
        imPiamomReportRecordDetailLayerIndex = null;
    }
}

function imPiamomReportRecordOpenDetailInteraction() {
    imPiamomReportRecordOpenInteractionDrawer(
        imPiamomReportRecordDetailTargetType,
        imPiamomReportRecordDetailTargetId
    );
}

function imPiamomReportRecordOpenDetail(id) {
    $.ajax({
        url: imPiamomApi + "/reportRecord/queryById",
        type: "GET",
        data: { id: id },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (!(res && (res.success === true || res.code === 200))) {
                return $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
            }
            var record = imPiamomResolveData(res, "record") || {};
            imPiamomReportRecordDetailId = record.id;
            imPiamomReportRecordDetailTargetType = record.targetType;
            imPiamomReportRecordDetailTargetId = record.targetId;
            imPiamomReportRecordDetailAuditStatus = record.auditStatus;

            imPiamomReportRecordFillDetail(record);
            imPiamomReportRecordUpdateDetailActions();
            imPiamomReportRecordDetailLayerIndex = layer.open({
                type: 1,
                title: "举报记录详情",
                area: ["820px", "640px"],
                shadeClose: true,
                content: $("#report-record-detail-modal"),
                success: function () {
                    imPiamomBindMediaImagesIn($("#report-record-detail-modal"));
                },
                end: function () {
                    imPiamomReportRecordDetailLayerIndex = null;
                }
            });
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imPiamomReportRecordAudit(auditStatus) {
    var auditUserId = $.trim($("#report-record-audit-user-id").val());
    if (!imPiamomReportRecordDetailId) {
        return $.modal.alertWarning("缺少举报记录ID");
    }
    if (!auditUserId) {
        return $.modal.alertWarning("缺少管理员用户ID");
    }

    var confirmText = String(auditStatus) === "1"
        ? "确定审核通过吗？通过后将下架被举报内容。"
        : "确定驳回该举报吗？";

    $.modal.confirm(confirmText, function () {
        $.ajax({
            url: imPiamomApi + "/reportRecord/audit",
            type: "POST",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify({
                id: imPiamomReportRecordDetailId,
                auditStatus: auditStatus,
                auditUserId: auditUserId
            }),
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "审核成功");
                    imPiamomReportRecordCloseDetail();
                    $("#bootstrap-table").bootstrapTable("refresh");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "审核失败");
                }
            },
            error: function () {
                $.modal.alertWarning("审核失败");
            }
        });
    });
}

function imPiamomReportRecordBuildDrawerQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    if (imPiamomReportRecordDrawerTargetType === "square") {
        query.squareId = imPiamomReportRecordDrawerTargetId;
    } else {
        query.momentId = imPiamomReportRecordDrawerTargetId;
    }
    return query;
}

function imPiamomReportRecordInitDrawerTables() {
    if (imPiamomReportRecordDrawerInited) {
        return;
    }
    imPiamomReportRecordDrawerInited = true;

    imInitTable({
        id: "report-record-drawer-comment-table",
        url: imPiamomApi + "/moment/comment/page",
        showSearch: false,
        showRefresh: false,
        showToggle: false,
        showColumns: false,
        queryParams: imPiamomReportRecordBuildDrawerQueryParams,
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
        id: "report-record-drawer-like-table",
        url: imPiamomApi + "/moment/like/page",
        showSearch: false,
        showRefresh: false,
        showToggle: false,
        showColumns: false,
        queryParams: imPiamomReportRecordBuildDrawerQueryParams,
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

function imPiamomReportRecordRefreshDrawerTables() {
    var commentUrl = imPiamomReportRecordDrawerTargetType === "square"
        ? imPiamomApi + "/square/comment/page"
        : imPiamomApi + "/moment/comment/page";
    var likeUrl = imPiamomReportRecordDrawerTargetType === "square"
        ? imPiamomApi + "/square/like/page"
        : imPiamomApi + "/moment/like/page";

    $("#report-record-drawer-comment-table").bootstrapTable("refreshOptions", { url: commentUrl });
    $("#report-record-drawer-like-table").bootstrapTable("refreshOptions", { url: likeUrl });
    $("#report-record-drawer-comment-table").bootstrapTable("refresh");
    $("#report-record-drawer-like-table").bootstrapTable("refresh");
}

function imPiamomReportRecordOpenInteractionDrawer(targetType, targetId) {
    if (!targetType || !targetId) {
        return;
    }
    imPiamomReportRecordDrawerTargetType = String(targetType).toLowerCase();
    imPiamomReportRecordDrawerTargetId = targetId;
    var typeLabel = imPiamomFormatTargetType(targetType);
    $("#report-record-interaction-title").text(typeLabel + " #" + targetId + " 互动数据");
    imPiamomReportRecordInitDrawerTables();
    layer.open({
        type: 1,
        title: false,
        area: ["920px", "560px"],
        shadeClose: true,
        content: $("#report-record-interaction-drawer"),
        success: function () {
            imPiamomReportRecordRefreshDrawerTables();
        }
    });
}

function imPiamomReportRecordInitTable(canView, canAudit) {
    imPiamomReportRecordCanAudit = !!canAudit;
    imPiamomInitMediaEvents();
    imInitTable({
        url: imPiamomApi + "/reportRecord/page",
        formId: "piamom-report-record-form",
        queryParams: imPiamomReportRecordQueryParams,
        responseHandler: imPageResponse,
        modalName: "举报记录",
        escape: false,
        onPostBody: function () {
            imPiamomBindMediaImagesIn($("#bootstrap-table"));
        },
        columns: [
            { field: "id", title: "ID", sortable: true, width: 70 },
            { field: "reporterIdno", title: "举报人IDNO", width: 110 },
            { field: "reporterNickname", title: "举报人昵称", width: 120, formatter: function (v) { return imFormatText(v, 16); } },
            { field: "reportTypeName", title: "举报类型", width: 160, formatter: function (v) { return imFormatText(v, 20); } },
            { field: "reason", title: "原因", formatter: function (v) { return imFormatText(v, 30); } },
            {
                field: "imageUrls",
                title: "举证图",
                width: 200,
                escape: false,
                cellStyle: function () {
                    return { css: { "text-align": "left", "vertical-align": "middle" } };
                },
                formatter: function (v, row) {
                    var urls = row.imageUrlList && row.imageUrlList.length ? row.imageUrlList : v;
                    var max = typeof IM_LIST_MEDIA_COMPACT_MAX !== "undefined" ? IM_LIST_MEDIA_COMPACT_MAX : 4;
                    return imPiamomFormatMedia(urls, "report-list-" + row.id, max);
                }
            },
            { field: "targetType", title: "对象类型", width: 90, formatter: imPiamomFormatTargetType },
            { field: "targetId", title: "对象ID", width: 90 },
            { field: "auditStatus", title: "审核状态", width: 100, formatter: imPiamomFormatAuditStatus, escape: false },
            { field: "publisherIdno", title: "发布者IDNO", width: 110 },
            { field: "postId", title: "帖子ID", width: 90 },
            {
                field: "postStatus",
                title: "帖子质押",
                width: 100,
                formatter: function (v) {
                    return v == null ? "-" : imPiamomFormatOdicStakeStatus(v);
                },
                escape: false
            },
            {
                field: "postVisibleStatus",
                title: "帖子展示",
                width: 100,
                formatter: function (v) {
                    return v == null ? "-" : imPiamomFormatVisibleStatus(v);
                },
                escape: false
            },
            {
                title: "操作",
                align: "center",
                width: 120,
                formatter: function (value, row) {
                    if (!canView && !canAudit) {
                        return "-";
                    }
                    var label = canAudit ? "详情/审核" : "查看";
                    return '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imPiamomReportRecordOpenDetail(\'' + row.id + '\')"><i class="fa fa-search"></i>' + label + "</a>";
                }
            }
        ]
    });
}
