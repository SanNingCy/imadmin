/**
 * IM 内部转账交易记录（接口：/admin/asset/paymentTransactionIm/*）
 */
var imPaymentTransactionImApi = ctx + "admin/asset/paymentTransactionIm";
var imButtonConfigApi = ctx + "buttonConfig/buttonConfig";
var IM_TRANSFER_FN_KEY = "transfer";

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

function imPaymentTransactionImResolveEntity(res, key) {
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

function imPaymentTransactionImSetTransferFnLabel(enabled) {
    $("#transfer-im-fn-label")
        .text(enabled ? "开启" : "关闭")
        .toggleClass("is-on", enabled);
}

function imPaymentTransactionImLoadTransferFn(canEditFn) {
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
                var cfg = imPaymentTransactionImResolveEntity(res, "buttonConfig");
                var enabled = Number(cfg.buttonStatus) === 1;
                $("#transfer-im-fn-toggle").prop("checked", enabled);
                imPaymentTransactionImSetTransferFnLabel(enabled);
            }
        }
    });
}

function imPaymentTransactionImBindTransferFn(canEditFn) {
    if (!canEditFn) {
        return;
    }
    $("#transfer-im-fn-toggle").on("change", function () {
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
                    imPaymentTransactionImSetTransferFnLabel(checked);
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
    imPaymentTransactionImLoadTransferFn(canEditFn);
    imPaymentTransactionImBindTransferFn(canEditFn);
}
