/**
 * IM 内部转账交易记录（接口：/admin/asset/paymentTransactionIm/*）
 */
var imPaymentTransactionImApi = ctx + "admin/asset/paymentTransactionIm";

var IM_PAYMENT_TX_IM_STATUS = {
    "1": { text: "成功", cls: "success" },
    "2": { text: "失败", cls: "danger" }
};

function imPaymentTransactionImQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("payment-transaction-im-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPaymentTransactionImFormatStatus(value) {
    var item = IM_PAYMENT_TX_IM_STATUS[String(value)];
    if (!item) {
        return value != null && value !== "" ? value : "-";
    }
    return '<span class="badge badge-' + item.cls + '">' + item.text + "</span>";
}

function imPaymentTransactionImFormatMoney(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    return isNaN(num) ? value : num.toFixed(2);
}

function imPaymentTransactionImResetSearch() {
    imTransferFnResetSearch("payment-transaction-im-form", "#transfer-im-fn-toggle", "#transfer-im-fn-label");
}

function imPaymentTransactionImInitTable() {
    imInitTable({
        url: imPaymentTransactionImApi + "/page",
        formId: "payment-transaction-im-form",
        modalName: "IM内部转账交易记录",
        sortName: "createTime",
        sortOrder: "desc",
        queryParams: imPaymentTransactionImQueryParams,
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
                    return imPaymentTransactionImFormatMoney(value);
                }
            },
            {
                field: "amount",
                title: "转账金额",
                sortable: true,
                formatter: function (value) {
                    return imPaymentTransactionImFormatMoney(value);
                }
            },
            {
                field: "rateAmount",
                title: "手续费金额",
                sortable: true,
                formatter: function (value) {
                    return imPaymentTransactionImFormatMoney(value);
                }
            },
            {
                field: "paymentStatus",
                title: "交易状态",
                sortable: true,
                formatter: function (value) {
                    return imPaymentTransactionImFormatStatus(value);
                }
            },
            { field: "createTime", title: "创建时间", sortable: true },
            { field: "updateTime", title: "更新时间", sortable: true }
        ]
    });
}

function imPaymentTransactionImInitPage(canEditFn) {
    imTransferFnInitPage(canEditFn, "#transfer-im-fn-toggle", "#transfer-im-fn-label");
}
