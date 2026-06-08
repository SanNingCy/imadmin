/**
 * IM 入金记录（接口：/admin/asset/paymentRecord/*）
 */
var imPaymentRecordApi = ctx + "admin/asset/paymentRecord";

var IM_PAYMENT_STATUS = {
    "1": { text: "成功", cls: "success" },
    "2": { text: "失败", cls: "danger" }
};

function imPaymentRecordQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("payment-record-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPaymentRecordFormatStatus(value) {
    var item = IM_PAYMENT_STATUS[String(value)];
    if (!item) {
        return value != null && value !== "" ? value : "-";
    }
    return '<span class="badge badge-' + item.cls + '">' + item.text + "</span>";
}

function imPaymentRecordFormatMoney(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    if (isNaN(num)) {
        return value;
    }
    return num.toFixed(2);
}

function imPaymentRecordFormatText(value, maxLen) {
    if (value == null || value === "") {
        return "-";
    }
    var text = String(value);
    if (maxLen && text.length > maxLen) {
        return '<span title="' + text.replace(/"/g, "&quot;") + '">' + text.substring(0, maxLen) + "...</span>";
    }
    return text;
}

function imPaymentRecordInitTable() {
    imInitTable({
        url: imPaymentRecordApi + "/page",
        formId: "payment-record-form",
        modalName: "入金记录",
        sortName: "createTime",
        sortOrder: "desc",
        queryParams: imPaymentRecordQueryParams,
        columns: [
            { field: "id", title: "ID", sortable: true },
            { field: "userId", title: "用户ID", sortable: false },
            { field: "idno", title: "ID号", sortable: true },
            { field: "nickname", title: "用户昵称", sortable: true },
            {
                field: "transactionNumber",
                title: "交易流水号",
                sortable: true,
                formatter: function (value) {
                    return imPaymentRecordFormatText(value, 24);
                }
            },
            {
                field: "partnerNumber",
                title: "合作方单号(wx方)",
                sortable: true,
                formatter: function (value) {
                    return imPaymentRecordFormatText(value, 24);
                }
            },
            {
                field: "amount",
                title: "入金金额",
                sortable: true,
                formatter: function (value) {
                    return imPaymentRecordFormatMoney(value);
                }
            },
            {
                field: "paymentAddress",
                title: "付款地址",
                sortable: true,
                formatter: function (value) {
                    return imPaymentRecordFormatText(value, 20);
                }
            },
            {
                field: "receivingAddress",
                title: "收款地址",
                sortable: true,
                formatter: function (value) {
                    return imPaymentRecordFormatText(value, 20);
                }
            },
            {
                field: "paymentStatus",
                title: "交易状态",
                sortable: true,
                formatter: function (value) {
                    return imPaymentRecordFormatStatus(value);
                }
            },
            { field: "createTime", title: "创建时间", sortable: true },
            { field: "updateTime", title: "更新时间", sortable: true }
        ]
    });
}

$(function () {
    imPaymentRecordInitTable();
});