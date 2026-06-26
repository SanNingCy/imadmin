/**
 * IM 链上支付订单（接口：/admin/asset/chainPayOrder/*）
 */
var imChainPayOrderApi = ctx + "admin/asset/chainPayOrder";

var IM_CHAIN_PAY_SCENE = {
    credit_activate: "开通信用分",
    meeting_create: "创建会议",
    meeting_extend: "延长会议",
    vip_open: "开通会员"
};

var IM_CHAIN_PAY_STATUS = {
    "0": { text: "待支付", cls: "warning" },
    "1": { text: "已支付", cls: "info" },
    "2": { text: "业务完成", cls: "success" },
    "3": { text: "失败", cls: "danger" },
    "4": { text: "过期", cls: "default" }
};

var IM_CHAIN_PAY_RECONCILE = {
    "0": { text: "未对账", cls: "warning" },
    "1": { text: "已对账", cls: "success" }
};

function imChainPayOrderQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    return $.extend(query, imOmitEmptyParams($.common.formToJSON("chain-pay-order-form")));
}

function imChainPayOrderText(value, maxLen) {
    if (value == null || value === "") {
        return "-";
    }
    var text = String(value);
    if (maxLen && text.length > maxLen) {
        return '<span title="' + text.replace(/"/g, "&quot;") + '">' + text.substring(0, maxLen) + "...</span>";
    }
    return text;
}

function imChainPayOrderFormatScene(value) {
    return IM_CHAIN_PAY_SCENE[value] || imChainPayOrderText(value);
}

function imChainPayOrderFormatBadge(map, value) {
    var item = map[String(value)];
    if (!item) {
        return value != null && value !== "" ? value : "-";
    }
    return '<span class="badge badge-' + item.cls + '">' + item.text + "</span>";
}

function imChainPayOrderFormatAmount(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    var amountText = Number.isFinite(num) ? num.toFixed(2) : String(value);
    return amountText + " USDT";
}

function imChainPayOrderFormatOdic(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    var amountText = Number.isFinite(num) ? num.toFixed(2) : String(value);
    return amountText + " ODIC";
}

function imChainPayOrderFillDetail(order) {
    order = order || {};
    $("#cpod-id").text(order.id || "-");
    $("#cpod-orderId").text(order.orderId || "-");
    $("#cpod-userId").text(order.userId || "-");
    $("#cpod-idno").text(order.idno || "-");
    $("#cpod-nickname").text(order.nickname || "-");
    $("#cpod-scene").text(imChainPayOrderFormatScene(order.scene));
    $("#cpod-chainId").text(order.chainId != null ? order.chainId : "-");
    $("#cpod-amount").text(imChainPayOrderFormatAmount(order.amount));
    $("#cpod-odicAmount").text(imChainPayOrderFormatOdic(order.odicAmount));
    $("#cpod-rawAmount").text(order.rawAmount || "-");
    $("#cpod-tokenSymbol").text(order.tokenSymbol || "-");
    $("#cpod-tokenAddress").text(order.tokenAddress || "-");
    $("#cpod-status").html(imChainPayOrderFormatBadge(IM_CHAIN_PAY_STATUS, order.status));
    $("#cpod-reconcileStatus").html(imChainPayOrderFormatBadge(IM_CHAIN_PAY_RECONCILE, order.reconcileStatus));
    $("#cpod-txHash").text(order.txHash || "-");
    $("#cpod-userAddress").text(order.userAddress || "-");
    $("#cpod-expireTime").text(order.expireTime || "-");
    $("#cpod-payTime").text(order.payTime || "-");
    $("#cpod-completeTime").text(order.completeTime || "-");
    $("#cpod-remark").text(order.remark || "-");
    $("#cpod-bizPayload").text(order.bizPayload || "-");
    $("#cpod-createTime").text(order.createTime || "-");
    $("#cpod-updateTime").text(order.updateTime || "-");
}

function imChainPayOrderOpenDetail(id) {
    if (!id) {
        return;
    }
    $.ajax({
        url: imChainPayOrderApi + "/queryById",
        type: "GET",
        data: { id: id },
        beforeSend: imTableBeforeSend,
        dataType: "json",
        success: function (res) {
            if (!res || !(res.success === true || res.code === 200)) {
                $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                return;
            }
            var order = res.order || (res.data && res.data.order) || {};
            imChainPayOrderFillDetail(order);
            layer.open({
                type: 1,
                title: "链上支付订单详情",
                area: ["860px", "620px"],
                shadeClose: true,
                content: $("#chain-pay-order-detail-modal"),
                btn: ["关闭"],
                yes: function (index) {
                    layer.close(index);
                }
            });
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imChainPayOrderInitTable() {
    imInitTable({
        url: imChainPayOrderApi + "/page",
        formId: "chain-pay-order-form",
        modalName: "链上支付订单",
        sortName: "createTime",
        sortOrder: "desc",
        queryParams: imChainPayOrderQueryParams,
        columns: [
            { field: "orderId", title: "链上订单号", sortable: true, formatter: function (v) { return imChainPayOrderText(v, 20); } },
            { field: "userId", title: "用户ID", sortable: false },
            { field: "idno", title: "ID号", sortable: false },
            { field: "nickname", title: "昵称", sortable: false },
            { field: "scene", title: "业务场景", sortable: true, formatter: imChainPayOrderFormatScene },
            { field: "amount", title: "支付金额(USDT)", sortable: true, formatter: imChainPayOrderFormatAmount },
            { field: "odicAmount", title: "支付金额(ODIC)", sortable: true, formatter: imChainPayOrderFormatOdic },
            { field: "chainId", title: "链ID", sortable: true },
            { field: "status", title: "订单状态", sortable: true, formatter: function (v) { return imChainPayOrderFormatBadge(IM_CHAIN_PAY_STATUS, v); } },
            { field: "reconcileStatus", title: "对账状态", sortable: true, formatter: function (v) { return imChainPayOrderFormatBadge(IM_CHAIN_PAY_RECONCILE, v); } },
            { field: "txHash", title: "交易Hash", sortable: true, formatter: function (v) { return imChainPayOrderText(v, 16); } },
            { field: "payTime", title: "支付时间", sortable: true },
            { field: "createTime", title: "创建时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var id = row.id;
                    return '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imChainPayOrderOpenDetail(\'' + id + '\')"><i class="fa fa-search"></i>查看</a>';
                }
            }
        ]
    });
}

$(function () {
    imChainPayOrderInitTable();
});
