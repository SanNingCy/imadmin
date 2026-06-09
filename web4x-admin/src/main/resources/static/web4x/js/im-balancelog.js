/**
 * IM 用户余额明细（接口：/balancelog/balanceLog/*）
 */
var imBalanceLogApi = ctx + "balancelog/balanceLog";

var IM_BALANCE_TYPE_DICT = "balance_type";
var IM_BALANCE_TYPE_LABEL_MAP = {};

var IM_BALANCE_STATE = {
    "1": { text: "收入", cls: "success" },
    "0": { text: "支出", cls: "danger" }
};

function imBalanceLogQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("balance-log-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imBalanceLogMemberField(row, field) {
    if (row && row.u && row.u[field] != null && row.u[field] !== "") {
        return row.u[field];
    }
    return "-";
}

function imBalanceLogBuildTypeLabelMap(values) {
    IM_BALANCE_TYPE_LABEL_MAP = {};
    $.each(values || [], function (_, item) {
        var val = item && item.value != null ? String(item.value) : "";
        var label = item && item.label != null ? String(item.label) : "";
        if (val !== "" && label !== "") {
            IM_BALANCE_TYPE_LABEL_MAP[val] = label;
        }
    });
}

function imBalanceLogFormatType(value) {
    if (value == null || value === "") {
        return "-";
    }
    var key = String(value);
    return IM_BALANCE_TYPE_LABEL_MAP[key] || key;
}

function imBalanceLogFillTypeSelect(values) {
    var $select = $("#balance-log-form select[name='type']");
    if (!$select.length) {
        return;
    }
    $select.find("option:not([value=''])").remove();
    $.each(values || [], function (_, item) {
        if (item.value == null || item.label == null) {
            return;
        }
        $("<option></option>")
            .val(String(item.value))
            .text(String(item.label))
            .appendTo($select);
    });
}

function imBalanceLogLoadTypeDict(done) {
    imDictLoadMap(function (dictMap) {
        var values = (dictMap && dictMap[IM_BALANCE_TYPE_DICT]) || [];
        if (typeof done === "function") {
            done(values);
        }
    });
}

function imBalanceLogFormatState(value) {
    var item = IM_BALANCE_STATE[String(value)];
    if (!item) {
        return value || "-";
    }
    return '<span class="badge badge-' + item.cls + '">' + item.text + "</span>";
}

function imBalanceLogFormatMoney(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    if (isNaN(num)) {
        return value;
    }
    return num.toFixed(2);
}

function imBalanceLogInitTable() {
    imInitTable({
        url: imBalanceLogApi + "/list",
        exportUrl: imBalanceLogApi + "/export",
        formId: "balance-log-form",
        modalName: "余额明细",
        sortName: "createDate",
        sortOrder: "desc",
        queryParams: imBalanceLogQueryParams,
        columns: [
            {
                field: "u.idno",
                title: "ID号",
                sortable: true,
                cellStyle: imEllipsisCell(90),
                formatter: function (value, row) {
                    return imFormatText(imBalanceLogMemberField(row, "idno"), 10);
                }
            },
            {
                field: "u.nickname",
                title: "用户昵称",
                sortable: true,
                cellStyle: imEllipsisCell(100),
                formatter: function (value, row) {
                    return imFormatText(imBalanceLogMemberField(row, "nickname"), 10);
                }
            },
            {
                field: "title",
                title: "专题",
                sortable: true,
                cellStyle: imEllipsisCell(120),
                formatter: function (value) {
                    return imFormatText(value, 14);
                }
            },
            {
                field: "type",
                title: "类型",
                sortable: true,
                formatter: function (value) {
                    return imBalanceLogFormatType(value);
                }
            },
            {
                field: "state",
                title: "收支类型",
                sortable: true,
                formatter: function (value) {
                    return imBalanceLogFormatState(value);
                }
            },
            {
                field: "money",
                title: "金额",
                sortable: true,
                formatter: function (value) {
                    return imBalanceLogFormatMoney(value);
                }
            },
            {
                field: "beforeBalance",
                title: "变更前余额",
                sortable: false,
                formatter: function (value) {
                    return imBalanceLogFormatMoney(value);
                }
            },
            {
                field: "afterBalance",
                title: "变更后余额",
                sortable: false,
                formatter: function (value) {
                    return imBalanceLogFormatMoney(value);
                }
            },
            {
                field: "currentBalance",
                title: "当前余额",
                sortable: true,
                sortName: "u.balance",
                formatter: function (value) {
                    return imBalanceLogFormatMoney(value);
                }
            },
            {
                field: "info",
                title: "备注",
                sortable: true,
                cellStyle: imEllipsisCell(160),
                formatter: function (value) {
                    return imFormatText(value, 20);
                }
            },
            {
                field: "createDate",
                title: "创建时间",
                sortable: true,
                cellStyle: imEllipsisCell(150),
                formatter: function (value) {
                    return imFormatText(value, 19);
                }
            }
        ]
    });
}

function imBalanceLogInitPage() {
    imBalanceLogLoadTypeDict(function (values) {
        imBalanceLogBuildTypeLabelMap(values);
        imBalanceLogFillTypeSelect(values);
        imBalanceLogInitTable();
    });
}

$(function () {
    imBalanceLogInitPage();
});
