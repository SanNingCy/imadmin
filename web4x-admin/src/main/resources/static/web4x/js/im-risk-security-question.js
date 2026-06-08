/**
 * IM 用户密保问题（风控-安全管理）
 * - GET  /member/mibao/list
 * - GET  /member/mibao/queryById?id=
 * - POST /member/mibao/update
 * - POST /member/mibao/reset
 */

var imRiskSecurityQuestionApi = ctx + "member/mibao";

function imRiskSecurityQuestionQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("security-question-form");
    if (formValues.title) query.mbname = formValues.title;
    if (formValues.idno) query.idno = formValues.idno;
    if (formValues.lianghao) query.lianghao = formValues.lianghao;
    return imOmitEmptyParams(query);
}

function imRiskSecurityQuestionResolveMember(res) {
    if (!res) return {};
    if (res.member) return res.member;
    if (res.data && res.data.member) return res.data.member;
    return {};
}

function imRiskSecurityQuestionEscapeHtml(text) {
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

function imRiskSecurityQuestionEllipsis(val) {
    if (val == null || val === "") return "-";
    var text = String(val);
    var safe = imRiskSecurityQuestionEscapeHtml(text);
    return '<span class="sq-ellipsis-inner" title="' + safe + '">' + safe + "</span>";
}

function imRiskSecurityQuestionFormatState(val) {
    return String(val) === "1" ? "禁用" : "正常";
}

function imRiskSecurityQuestionResetModal() {
    $("#sq-member-id").val("");
    $("#sq-idno").val("");
    $("#sq-mbname").val("");
    $("#sq-mbda").val("");
}

function imRiskSecurityQuestionSetReadOnly(readOnly) {
    $("#security-question-modal-form :input").prop("disabled", readOnly);
}

function imRiskSecurityQuestionShowModal(mode, readOnly) {
    var titles = { view: "查看密保问题", edit: "修改密保问题" };
    layer.open({
        type: 1,
        title: titles[mode] || "密保问题",
        area: ["480px", "280px"],
        shadeClose: true,
        content: $("#security-question-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imRiskSecurityQuestionSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imRiskSecurityQuestionOpenModal(mode, memberId, idno) {
    var readOnly = mode === "view";
    imRiskSecurityQuestionResetModal();
    $("#sq-member-id").val(memberId || "");
    $("#sq-idno").val(idno || "");
    imRiskSecurityQuestionSetReadOnly(readOnly);

    $.ajax({
        url: imRiskSecurityQuestionApi + "/queryById",
        type: "GET",
        data: { id: memberId },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                var info = imRiskSecurityQuestionResolveMember(res);
                $("#sq-mbname").val(info.mbname || "");
                $("#sq-mbda").val(info.mbda || "");
                if (!idno && info.idno) $("#sq-idno").val(info.idno);
                imRiskSecurityQuestionSetReadOnly(readOnly);
                imRiskSecurityQuestionShowModal(mode, readOnly);
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
            }
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imRiskSecurityQuestionSave(layerIndex) {
    var idno = $.trim($("#sq-idno").val());
    var mbname = $.trim($("#sq-mbname").val());
    var mbda = $.trim($("#sq-mbda").val());
    if (!idno) return $.modal.alertWarning("用户ID号不能为空");
    if (!mbname) return $.modal.alertWarning("问题不能为空");
    if (!mbda) return $.modal.alertWarning("答案不能为空");

    $.ajax({
        url: imRiskSecurityQuestionApi + "/update",
        type: "POST",
        data: { id: idno, mbname: mbname, mbda: mbda },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess((res && res.msg) ? res.msg : "保存成功");
                layer.close(layerIndex);
                $("#bootstrap-table").bootstrapTable("refresh");
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "保存失败");
            }
        },
        error: function () {
            $.modal.alertWarning("保存失败");
        }
    });
}

function imRiskSecurityQuestionReset(memberId) {
    $.modal.confirm("确认重置该用户的密保吗？", function () {
        $.ajax({
            url: imRiskSecurityQuestionApi + "/reset",
            type: "POST",
            data: { id: memberId },
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

function imRiskSecurityQuestionInitTable(canView, canEdit, canReset) {
    imInitTable({
        url: imRiskSecurityQuestionApi + "/list",
        formId: "security-question-form",
        queryParams: imRiskSecurityQuestionQueryParams,
        responseHandler: imPageResponse,
        modalName: "用户密保问题",
        columns: [
            { field: "acount", title: "账户名", sortable: true, class: "sq-ellipsis", formatter: function (v) { return imRiskSecurityQuestionEllipsis(v); } },
            { field: "idno", title: "用户ID号", sortable: true, class: "sq-ellipsis", formatter: function (v) { return imRiskSecurityQuestionEllipsis(v); } },
            { field: "lianghao", title: "靓号", sortable: true, formatter: function (v) { return v || "-"; } },
            { field: "nickname", title: "用户名", sortable: true, class: "sq-ellipsis", formatter: function (v) { return imRiskSecurityQuestionEllipsis(v); } },
            { field: "state", title: "状态", sortable: true, formatter: imRiskSecurityQuestionFormatState },
            { field: "mbname", title: "密保问题", sortable: true, class: "sq-ellipsis", formatter: function (v) { return imRiskSecurityQuestionEllipsis(v); } },
            { field: "mbda", title: "密保答案", sortable: true, class: "sq-ellipsis", formatter: function (v) { return imRiskSecurityQuestionEllipsis(v); } },
            { field: "password", title: "登录密码", sortable: true, class: "sq-ellipsis", formatter: function (v) { return imRiskSecurityQuestionEllipsis(v); } },
            { field: "paypwd", title: "支付密码", sortable: true, class: "sq-ellipsis", formatter: function (v) { return imRiskSecurityQuestionEllipsis(v); } },
            { field: "createDate", title: "创建时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imRiskSecurityQuestionOpenModal(\'view\',\'' + row.id + '\',\'' + (row.idno || "") + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imRiskSecurityQuestionOpenModal(\'edit\',\'' + row.id + '\',\'' + (row.idno || "") + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canReset) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imRiskSecurityQuestionReset(\'' + row.id + '\')"><i class="fa fa-refresh"></i>重置密保</a>');
                    }
                    return actions.length ? actions.join("") : "-";
                }
            }
        ]
    });
}
