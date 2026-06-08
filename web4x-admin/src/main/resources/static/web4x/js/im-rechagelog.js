/**
 * IM 充值记录（接口：/rechagelog/rechageLog/*）
 */
var imRechageLogApi = ctx + "rechagelog/rechageLog";

var IM_RECHAGE_STATE = {
    "1": { text: "待审核", cls: "warning" },
    "2": { text: "通过", cls: "success" },
    "3": { text: "驳回", cls: "danger" },
    "4": { text: "已锁定", cls: "default" }
};

function imRechageLogQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("rechage-log-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imRechageLogMemberField(row, field) {
    if (row && row.u && row.u[field] != null && row.u[field] !== "") {
        return row.u[field];
    }
    return "-";
}

function imRechageLogFormatState(value) {
    var item = IM_RECHAGE_STATE[String(value)];
    if (!item) {
        return value != null && value !== "" ? value : "-";
    }
    return '<span class="badge badge-' + item.cls + '">' + item.text + "</span>";
}

function imRechageLogFormatMoney(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    if (isNaN(num)) {
        return value;
    }
    return num.toFixed(2);
}

function imRechageLogFormatText(value, maxLen) {
    if (value == null || value === "") {
        return "-";
    }
    var text = String(value);
    if (maxLen && text.length > maxLen) {
        return '<span title="' + text.replace(/"/g, "&quot;") + '">' + text.substring(0, maxLen) + "...</span>";
    }
    return text;
}

function imRechageLogFormatPingz(value) {
    if (value == null || value === "") {
        return "-";
    }
    var url = String(value);
    if (/^https?:\/\//i.test(url) || url.indexOf("/") === 0) {
        return '<a href="' + url + '" target="_blank" title="查看凭证"><img src="' + url + '" style="max-height:36px;max-width:60px;" onerror="this.outerHTML=\'查看凭证\'"/></a>';
    }
    return imRechageLogFormatText(url, 20);
}

function imRechageLogInitTable() {
    imInitTable({
        url: imRechageLogApi + "/list",
        exportUrl: imRechageLogApi + "/export",
        formId: "rechage-log-form",
        modalName: "充值记录",
        sortName: "createDate",
        sortOrder: "desc",
        queryParams: imRechageLogQueryParams,
        columns: [
            { field: "id", title: "ID", sortable: true },
            {
                field: "u.id",
                title: "用户ID",
                sortable: true,
                formatter: function (value, row) {
                    return imRechageLogMemberField(row, "id");
                }
            },
            {
                field: "u.nickname",
                title: "用户昵称",
                sortable: true,
                formatter: function (value, row) {
                    return imRechageLogMemberField(row, "nickname");
                }
            },
            {
                field: "money",
                title: "金额",
                sortable: true,
                formatter: function (value) {
                    return imRechageLogFormatMoney(value);
                }
            },
            { field: "title", title: "标题", sortable: true },
            { field: "sname", title: "收款人", sortable: true },
            {
                field: "sno",
                title: "收款卡号",
                sortable: true,
                formatter: function (value) {
                    return imRechageLogFormatText(value, 20);
                }
            },
            {
                field: "pingz",
                title: "打款凭证",
                sortable: false,
                formatter: function (value) {
                    return imRechageLogFormatPingz(value);
                }
            },
            {
                field: "state",
                title: "审核状态",
                sortable: true,
                formatter: function (value) {
                    return imRechageLogFormatState(value);
                }
            },
            { field: "createDate", title: "创建时间", sortable: true },
            { field: "updateDate", title: "更新时间", sortable: true }
        ]
    });
}

$(function () {
    imRechageLogInitTable();
});