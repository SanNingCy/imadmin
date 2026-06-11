/**
 * 会议室订单 - /admin/live/order/*
 */
var imLiveOrderApi = imLiveApi + "/order";

function imLiveOrderQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("live-order-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imLiveOrderResetSearch() {
    $.form.reset();
}

function imLiveOrderInitTable() {
    imInitTable({
        url: imLiveOrderApi + "/page",
        formId: "live-order-form",
        queryParams: imLiveOrderQueryParams,
        sortName: "createTime",
        sortOrder: "desc",
        modalName: "会议室订单",
        columns: [
            {
                field: "orderNo",
                title: "订单号",
                sortable: true,
                cellStyle: imEllipsisCell(180),
                formatter: function (val) { return imFormatText(val, 24); }
            },
            {
                field: "userIdno",
                title: "用户ID",
                sortable: true,
                formatter: function (val, row) {
                    return val || row.userId || "-";
                }
            },
            {
                field: "tidnoGroup",
                title: "群ID号",
                sortable: true,
                formatter: function (val, row) {
                    return val || row.groupIdNo || "-";
                }
            },
            {
                field: "channelId",
                title: "会议室ID",
                sortable: true,
                cellStyle: imEllipsisCell(160),
                formatter: function (val) { return imFormatText(val, 20); }
            },
            {
                field: "channelName",
                title: "会议室名称",
                sortable: true,
                cellStyle: imEllipsisCell(200),
                formatter: function (val) { return imFormatText(val, 28); }
            },
            { field: "durationValue", title: "会议时长(分钟)", sortable: true },
            { field: "tierValue", title: "人数上限", sortable: true },
            {
                field: "totalAmount",
                title: "订单总金额",
                sortable: true,
                formatter: imLiveFormatAmount
            },
            {
                field: "liveStatus",
                title: "状态",
                sortable: true,
                formatter: imLiveFormatOrderStatus
            },
            { field: "beginTime", title: "开始时间", sortable: true },
            { field: "endTime", title: "结束时间", sortable: true }
        ]
    });
}
