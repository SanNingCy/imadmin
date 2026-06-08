/**
 * IM 登录记录审计
 * - GET /loginlog/loginLog/list
 * - GET /loginlog/loginLog/queryById?id=
 */

var imSecurityLoginAuditApi = ctx + "loginlog/loginLog";

function imSecurityLoginAuditQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("login-audit-form");
    if (formValues.idno) query["u.idno"] = formValues.idno;
    if (formValues.nickname) query["u.nickname"] = formValues.nickname;
    if (formValues.lianghao) query["u.lianghao"] = formValues.lianghao;
    return imOmitEmptyParams(query);
}

function imSecurityLoginAuditResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imSecurityLoginAuditNested(row, field) {
    if (!row || !row.u) return "-";
    var val = row.u[field];
    return val == null || val === "" ? "-" : val;
}

function imSecurityLoginAuditOpenView(id) {
    $.ajax({
        url: imSecurityLoginAuditApi + "/queryById",
        type: "GET",
        data: { id: id },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                var info = imSecurityLoginAuditResolveEntity(res, "loginLog");
                var u = info.u || {};
                $("#login-audit-user").val(u.phone || u.nickname || u.idno || "-");
                $("#login-audit-ip").val(info.ip || "");
                $("#login-audit-ipcity").val(info.ipcity || "");
                layer.open({
                    type: 1,
                    title: "查看登录记录",
                    area: ["480px", "280px"],
                    shadeClose: true,
                    content: $("#login-audit-modal"),
                    btn: ["关闭"],
                    yes: function (index) {
                        layer.close(index);
                    }
                });
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
            }
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imSecurityLoginAuditInitTable(canView) {
    imInitTable({
        url: imSecurityLoginAuditApi + "/list",
        formId: "login-audit-form",
        queryParams: imSecurityLoginAuditQueryParams,
        responseHandler: imPageResponse,
        modalName: "登录记录",
        columns: [
            {
                field: "u.idno",
                title: "idno",
                sortable: true,
                formatter: function (value, row) {
                    return imSecurityLoginAuditNested(row, "idno");
                }
            },
            {
                field: "u.nickname",
                title: "用户名",
                sortable: true,
                formatter: function (value, row) {
                    return imSecurityLoginAuditNested(row, "nickname");
                }
            },
            {
                field: "u.lianghao",
                title: "靓号",
                sortable: true,
                formatter: function (value, row) {
                    return imSecurityLoginAuditNested(row, "lianghao");
                }
            },
            { field: "ip", title: "ip地址", sortable: true },
            { field: "ipcity", title: "城市", sortable: true },
            { field: "createDate", title: "创建时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    if (!canView) return "-";
                    return '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imSecurityLoginAuditOpenView(\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a>';
                }
            }
        ]
    });
}
