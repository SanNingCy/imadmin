/**
 * IM 签到记录
 * - GET /signlog/signLog/list
 */

var imSecuritySignLogsApi = ctx + "signlog/signLog";

function imSecuritySignLogsQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("sign-logs-form");
    if (formValues.idno) query["u.idno"] = formValues.idno;
    if (formValues.isSign !== undefined && formValues.isSign !== "") query.isSign = formValues.isSign;
    if (formValues.date) query.date = formValues.date;
    return imOmitEmptyParams(query);
}

function imSecuritySignLogsFormatYesNo(val) {
    if (String(val) === "1") return "是";
    if (String(val) === "0") return "否";
    return "-";
}

function imSecuritySignLogsInitTable() {
    imInitTable({
        url: imSecuritySignLogsApi + "/list",
        formId: "sign-logs-form",
        queryParams: imSecuritySignLogsQueryParams,
        responseHandler: imPageResponse,
        modalName: "签到记录",
        sortName: "createDate",
        sortOrder: "desc",
        columns: [
            {
                field: "u.idno",
                title: "用户ID号",
                sortable: false,
                formatter: function (value, row) {
                    if (row.u && row.u.idno) return row.u.idno;
                    return row.idno || "-";
                }
            },
            {
                field: "u.nickname",
                title: "用户",
                sortable: false,
                formatter: function (value, row) {
                    if (row.u && row.u.nickname) return row.u.nickname;
                    return row.nickname || "-";
                }
            },
            {
                field: "isSign",
                title: "是否签到",
                sortable: false,
                formatter: function (v) { return imSecuritySignLogsFormatYesNo(v); }
            },
            { field: "money", title: "金额", sortable: false },
            { field: "date", title: "日期", sortable: false },
            { field: "createDate", title: "创建时间", sortable: true }
        ]
    });
}
