/**
 * IM 用户谷歌安全验证（风控-安全管理）
 * - GET  /member/twoFactor/list
 * - POST /member/twoFactor/reset
 */

var imRiskGoogleVerifyApi = ctx + "member/twoFactor";

function imRiskGoogleVerifyQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("google-verify-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imRiskGoogleVerifyFormatState(val) {
    return String(val) === "1" ? "禁用" : "正常";
}

function imRiskGoogleVerifyEllipsis(val) {
    if (val == null || val === "") return "-";
    var text = String(val);
    return '<span class="gv-ellipsis-inner" title="' + text.replace(/"/g, "&quot;") + '">' + text + "</span>";
}

function imRiskGoogleVerifyReset(id) {
    $.modal.confirm("确定重置该用户的谷歌验证码吗？", function () {
        $.ajax({
            url: imRiskGoogleVerifyApi + "/reset",
            type: "POST",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "重置成功");
                    $("#bootstrap-table").bootstrapTable("refresh");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "重置失败");
                }
            },
            error: function () {
                $.modal.alertWarning("重置失败");
            }
        });
    });
}

function imRiskGoogleVerifyInitTable(canReset) {
    imInitFixedOperateTable({
        url: imRiskGoogleVerifyApi + "/list",
        formId: "google-verify-form",
        queryParams: imRiskGoogleVerifyQueryParams,
        responseHandler: imPageResponse,
        modalName: "谷歌安全验证",
        columns: [
            { field: "acount", title: "账户名", sortable: true, class: "gv-ellipsis", formatter: imRiskGoogleVerifyEllipsis },
            { field: "idno", title: "用户ID", sortable: true, class: "gv-ellipsis", formatter: imRiskGoogleVerifyEllipsis },
            { field: "nickname", title: "用户昵称", sortable: true, class: "gv-ellipsis", formatter: imRiskGoogleVerifyEllipsis },
            { field: "state", title: "状态", sortable: true, formatter: imRiskGoogleVerifyFormatState },
            { field: "twoFactorCode", title: "谷歌验证码", sortable: true, class: "gv-ellipsis", formatter: imRiskGoogleVerifyEllipsis },
            { field: "twoFactorTime", title: "谷歌验证码时间", sortable: true },
            { field: "createDate", title: "创建时间", sortable: true },
            {
                field: "operate",
                title: "操作",
                align: "center",
                width: 160,
                formatter: function (value, row) {
                    if (!canReset) return "-";
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imRiskGoogleVerifyReset(\'' + row.id + '\')"><i class="fa fa-refresh"></i>重置谷歌验证码</a>';
                }
            }
        ]
    });
}
