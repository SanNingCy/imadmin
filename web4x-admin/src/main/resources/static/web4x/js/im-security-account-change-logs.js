/**
 * IM 账户变更日志
 * - GET /memberChangeLog/memberChangeLog/list
 */

var imSecurityAccountChangeApi = ctx + "memberChangeLog/memberChangeLog";

var IM_SECURITY_CHANGE_TYPE_MAP = {
    nickname: "修改昵称",
    password: "修改密码",
    phone: "修改手机号",
    paypwd: "修改支付密码"
};

function imSecurityAccountChangeQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("account-change-form");
    if (formValues.uidno) query.uIdno = formValues.uidno;
    if (formValues.changeType) query.changeType = formValues.changeType;
    return imOmitEmptyParams(query);
}

function imSecurityAccountChangeFormatType(val) {
    return IM_SECURITY_CHANGE_TYPE_MAP[String(val)] || val || "-";
}

function imSecurityAccountChangeInitTable() {
    imInitTable({
        url: imSecurityAccountChangeApi + "/list",
        formId: "account-change-form",
        queryParams: imSecurityAccountChangeQueryParams,
        responseHandler: imPageResponse,
        modalName: "账户变更日志",
        columns: [
            {
                field: "u.idno",
                title: "用户ID号",
                sortable: true,
                formatter: function (value, row) {
                    return row.u && row.u.idno ? row.u.idno : "-";
                }
            },
            {
                field: "changeType",
                title: "修改类型",
                sortable: true,
                formatter: function (v) { return imSecurityAccountChangeFormatType(v); }
            },
            { field: "oldValue", title: "旧数据", sortable: true },
            { field: "newValue", title: "新数据", sortable: true },
            { field: "ip", title: "ip地址", sortable: true },
            { field: "ipcity", title: "城市", sortable: true },
            { field: "createDate", title: "创建时间", sortable: true }
        ]
    });
}
