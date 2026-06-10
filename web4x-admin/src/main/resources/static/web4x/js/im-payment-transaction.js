/**
 * IM 出金交易记录（接口：/admin/asset/paymentTransaction/*）
 */
var imPaymentTransactionApi = ctx + "admin/asset/paymentTransaction";

var IM_PAYMENT_TX_STATUS = {
    "1": { text: "成功", cls: "success" },
    "2": { text: "失败", cls: "danger" }
};

function imPaymentTransactionQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("payment-transaction-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPaymentTransactionFormatStatus(value) {
    var item = IM_PAYMENT_TX_STATUS[String(value)];
    if (!item) {
        return value != null && value !== "" ? value : "-";
    }
    return '<span class="badge badge-' + item.cls + '">' + item.text + "</span>";
}

function imPaymentTransactionFormatMoney(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    return isNaN(num) ? value : num.toFixed(2);
}

function imPaymentTransactionResetSearch() {
    imTransferFnResetSearch("payment-transaction-form", "#transfer-fn-toggle", "#transfer-fn-label");
}

function imPaymentTransactionInitTable() {
    imInitTable({
        url: imPaymentTransactionApi + "/page",
        formId: "payment-transaction-form",
        modalName: "IM出金交易记录",
        sortName: "createTime",
        sortOrder: "desc",
        queryParams: imPaymentTransactionQueryParams,
        columns: [
            { field: "id", title: "ID", sortable: true },
            { field: "idno", title: "ID号", sortable: true },
            { field: "nickname", title: "用户昵称", sortable: true },
            { field: "transactionNumber", title: "交易流水号", sortable: true },
            {
                field: "actualAmount",
                title: "实际到账金额",
                sortable: true,
                formatter: function (value) {
                    return imPaymentTransactionFormatMoney(value);
                }
            },
            {
                field: "amount",
                title: "转账金额",
                sortable: true,
                formatter: function (value) {
                    return imPaymentTransactionFormatMoney(value);
                }
            },
            {
                field: "rateAmount",
                title: "手续费金额",
                sortable: true,
                formatter: function (value) {
                    return imPaymentTransactionFormatMoney(value);
                }
            },
            {
                field: "paymentStatus",
                title: "交易状态",
                sortable: true,
                formatter: function (value) {
                    return imPaymentTransactionFormatStatus(value);
                }
            },
            { field: "createTime", title: "创建时间", sortable: true },
            { field: "updateTime", title: "更新时间", sortable: true }
        ]
    });
}

function imPaymentTransactionInitPage(canEditFn) {
    imTransferFnInitPage(canEditFn, "#transfer-fn-toggle", "#transfer-fn-label");
}
