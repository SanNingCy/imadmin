/**
 * IM 出金交易记录（接口：/admin/asset/paymentTransaction/*）
 */
var imPaymentTransactionApi = ctx + "admin/asset/paymentTransaction";
var imButtonConfigApi = ctx + "buttonConfig/buttonConfig";
var IM_TRANSFER_FN_KEY = "transfer";

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

function imPaymentTransactionResolveEntity(res, key) {
    if (!res) {
        return {};
    }
    if (res[key]) {
        return res[key];
    }
    if (res.data && res.data[key]) {
        return res.data[key];
    }
    return {};
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

function imPaymentTransactionSetTransferFnLabel(enabled) {
    $("#transfer-fn-label")
        .text(enabled ? "开启" : "关闭")
        .toggleClass("is-on", enabled);
}

function imPaymentTransactionLoadTransferFn(canEditFn) {
    if (!canEditFn) {
        return;
    }
    $.ajax({
        url: imButtonConfigApi + "/queryByButtonKey",
        type: "GET",
        data: { buttonKey: IM_TRANSFER_FN_KEY },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                var cfg = imPaymentTransactionResolveEntity(res, "buttonConfig");
                var enabled = Number(cfg.buttonStatus) === 1;
                $("#transfer-fn-toggle").prop("checked", enabled);
                imPaymentTransactionSetTransferFnLabel(enabled);
            }
        }
    });
}

function imPaymentTransactionBindTransferFn(canEditFn) {
    if (!canEditFn) {
        return;
    }
    $("#transfer-fn-toggle").on("change", function () {
        var checked = $(this).is(":checked");
        var self = this;
        $.ajax({
            url: imButtonConfigApi + "/updateKey",
            type: "POST",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify({
                buttonKey: IM_TRANSFER_FN_KEY,
                buttonName: "转账功能",
                buttonStatus: checked ? "1" : "0"
            }),
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imPaymentTransactionSetTransferFnLabel(checked);
                    $.modal.msgSuccess(res.msg || (checked ? "开启成功" : "关闭成功"));
                } else {
                    $(self).prop("checked", !checked);
                    $.modal.alertWarning((res && res.msg) ? res.msg : "操作失败");
                }
            },
            error: function () {
                $(self).prop("checked", !checked);
                $.modal.alertWarning("操作失败");
            }
        });
    });
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
    imPaymentTransactionLoadTransferFn(canEditFn);
    imPaymentTransactionBindTransferFn(canEditFn);
}
