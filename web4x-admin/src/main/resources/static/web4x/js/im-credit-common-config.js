/**
 * IM 信用分公共配置（头像角标展示配置）
 * - GET    /admin/creditScore/avatarDisplay/page
 * - GET    /admin/creditScore/avatarDisplay/queryById/{id}
 * - PUT    /admin/creditScore/avatarDisplay/update
 * - DELETE /admin/creditScore/avatarDisplay/remove?id=
 */

var imCreditCommonConfigApi = ctx + "admin/creditScore/avatarDisplay";

var IM_CREDIT_COMMON_CONFIG_NUM_FIELDS = [
    "newUserDays",
    "newUserJoinDays",
    "newUserCreateGroupDays",
    "newUserCreateGroupMemberCount",
    "createGroupCount",
    "friendApplyCount",
    "newUserDaysV2",
    "newInGroupDays"
];

function imCreditCommonConfigQueryParams(params) {
    return imCreditQueryParams("credit-common-config-form", params);
}

function imCreditCommonConfigInitSelects() {
    $(".credit-common-config-flag").each(function () {
        var $sel = $(this);
        if ($sel.children().length) return;
        $sel.append('<option value="">请选择</option>');
        $.each(IM_CREDIT_FLAG_MAP, function (k, label) {
            $sel.append('<option value="' + k + '">' + label + "</option>");
        });
    });

    var $loginVerify = $("#credit-common-config-loginSecondVerifyMinPass");
    if ($loginVerify.length && !$loginVerify.children().length) {
        $loginVerify.append('<option value="">请选择</option>');
        $.each(IM_CREDIT_LOGIN_VERIFY_MAP, function (k, label) {
            $loginVerify.append('<option value="' + k + '">' + label + "</option>");
        });
    }
}

function imCreditCommonConfigFormatCount(val, unit) {
    var num = Number(val);
    return Number.isFinite(num) ? String(num) + " " + unit : "-";
}

function imCreditCommonConfigFormatLianghao(val, row) {
    var v = row.isLianghao != null ? row.isLianghao : row.is_lianghao;
    return imCreditFormatFlag(v);
}

function imCreditCommonConfigFormatVip(val, row) {
    var v = row.isVip != null ? row.isVip : row.is_vip;
    return imCreditFormatFlag(v);
}

function imCreditCommonConfigResetModal() {
    $("#credit-common-config-id").val("");
    $("#credit-common-config-modal-form :input").not("[type=hidden]").val("");
}

function imCreditCommonConfigFillModal(info) {
    info = info || {};
    $("#credit-common-config-id").val(info.id || "");
    $("#credit-common-config-id-display").val(info.id || "");
    $("#credit-common-config-newUserDays").val(info.newUserDays != null ? info.newUserDays : "");
    $("#credit-common-config-newUserJoinDays").val(info.newUserJoinDays != null ? info.newUserJoinDays : "");
    $("#credit-common-config-newUserCreateGroupDays").val(info.newUserCreateGroupDays != null ? info.newUserCreateGroupDays : "");
    $("#credit-common-config-newUserCreateGroupMemberCount").val(info.newUserCreateGroupMemberCount != null ? info.newUserCreateGroupMemberCount : "");
    $("#credit-common-config-createGroupCount").val(info.createGroupCount != null ? info.createGroupCount : "");
    $("#credit-common-config-friendApplyCount").val(info.friendApplyCount != null ? info.friendApplyCount : "");
    $("#credit-common-config-newUserDaysV2").val(info.newUserDaysV2 != null ? info.newUserDaysV2 : "");
    $("#credit-common-config-newInGroupDays").val(info.newInGroupDays != null ? info.newInGroupDays : "");
    $("#credit-common-config-isLianghao").val(info.isLianghao != null ? String(info.isLianghao) : (info.is_lianghao != null ? String(info.is_lianghao) : ""));
    $("#credit-common-config-isVip").val(info.isVip != null ? String(info.isVip) : (info.is_vip != null ? String(info.is_vip) : ""));
    $("#credit-common-config-loginSecondVerifyMinPass").val(info.loginSecondVerifyMinPass != null ? String(info.loginSecondVerifyMinPass) : "");
    $("#credit-common-config-createTime").val(info.createTime || "");
    $("#credit-common-config-updateTime").val(info.updateTime || "");
}

function imCreditCommonConfigSetReadOnly(readOnly) {
    imCreditSetFormReadOnly($("#credit-common-config-modal-form"), readOnly, [
        "credit-common-config-id-display",
        "credit-common-config-createTime",
        "credit-common-config-updateTime"
    ]);
}

function imCreditCommonConfigShowModal(mode, readOnly) {
    layer.open({
        type: 1,
        title: mode === "edit" ? "修改头像角标展示配置" : "查看头像角标展示配置",
        area: ["720px", "90%"],
        shadeClose: true,
        content: $("#credit-common-config-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imCreditCommonConfigSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imCreditCommonConfigOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imCreditCommonConfigResetModal();
    imCreditCommonConfigInitSelects();
    imCreditCommonConfigSetReadOnly(readOnly);

    if (!id) {
        $.modal.alertWarning("缺少记录ID");
        return;
    }

    imCreditAjax({
        url: imCreditCommonConfigApi + "/queryById/" + id,
        type: "GET",
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                imCreditCommonConfigFillModal(imCreditResolveEntity(res, "config"));
                imCreditCommonConfigShowModal(mode, readOnly);
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
            }
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imCreditCommonConfigParseIntVal(selector) {
    var val = $(selector).val();
    if (val === "" || val == null) return null;
    return parseInt(val, 10);
}

function imCreditCommonConfigValidate() {
    var i;
    var fieldId;
    var val;
    var labels = {
        "credit-common-config-newUserDays": "新用户信用分底色天数",
        "credit-common-config-newUserJoinDays": "新用户加人天数",
        "credit-common-config-newUserCreateGroupDays": "新人建群天数",
        "credit-common-config-newUserCreateGroupMemberCount": "新人建群人数",
        "credit-common-config-createGroupCount": "建群数量",
        "credit-common-config-friendApplyCount": "加好友申请次数",
        "credit-common-config-newUserDaysV2": "注册天数发送链接天数",
        "credit-common-config-newInGroupDays": "新入群天数"
    };

    for (i = 0; i < IM_CREDIT_COMMON_CONFIG_NUM_FIELDS.length; i++) {
        fieldId = "credit-common-config-" + IM_CREDIT_COMMON_CONFIG_NUM_FIELDS[i];
        val = $("#" + fieldId).val();
        if (val === "" || val == null) {
            $.modal.alertWarning("请输入" + labels[fieldId]);
            return false;
        }
        if (parseInt(val, 10) < 0) {
            $.modal.alertWarning(labels[fieldId] + "须为大于等于0的整数");
            return false;
        }
    }

    if ($("#credit-common-config-isLianghao").val() === "") {
        $.modal.alertWarning("请选择是否是靓号配置");
        return false;
    }
    if ($("#credit-common-config-isVip").val() === "") {
        $.modal.alertWarning("请选择是否是会员配置");
        return false;
    }
    if ($("#credit-common-config-loginSecondVerifyMinPass").val() === "") {
        $.modal.alertWarning("请选择登录二次验证至少通过几项");
        return false;
    }
    return true;
}

function imCreditCommonConfigSave(layerIndex) {
    var id = $("#credit-common-config-id").val();
    if (!id) return $.modal.alertWarning("缺少记录ID");
    if (!imCreditCommonConfigValidate()) return;

    var isLianghao = imCreditCommonConfigParseIntVal("#credit-common-config-isLianghao") || 0;
    var isVip = imCreditCommonConfigParseIntVal("#credit-common-config-isVip") || 0;

    var payload = {
        id: id,
        newUserDays: imCreditCommonConfigParseIntVal("#credit-common-config-newUserDays") || 0,
        newUserJoinDays: imCreditCommonConfigParseIntVal("#credit-common-config-newUserJoinDays") || 0,
        newUserCreateGroupDays: imCreditCommonConfigParseIntVal("#credit-common-config-newUserCreateGroupDays") || 0,
        newUserCreateGroupMemberCount: imCreditCommonConfigParseIntVal("#credit-common-config-newUserCreateGroupMemberCount") || 0,
        createGroupCount: imCreditCommonConfigParseIntVal("#credit-common-config-createGroupCount") || 0,
        friendApplyCount: imCreditCommonConfigParseIntVal("#credit-common-config-friendApplyCount") || 0,
        newUserDaysV2: imCreditCommonConfigParseIntVal("#credit-common-config-newUserDaysV2") || 0,
        newInGroupDays: imCreditCommonConfigParseIntVal("#credit-common-config-newInGroupDays") || 0,
        isLianghao: isLianghao,
        isVip: isVip,
        is_lianghao: isLianghao,
        is_vip: isVip,
        loginSecondVerifyMinPass: imCreditCommonConfigParseIntVal("#credit-common-config-loginSecondVerifyMinPass") || 0
    };

    imCreditAjax({
        url: imCreditCommonConfigApi + "/update",
        type: "PUT",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess((res && res.msg) ? res.msg : "修改成功");
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

function imCreditCommonConfigRemove(id) {
    $.modal.confirm("确定删除该配置吗？", function () {
        imCreditAjax({
            url: imCreditCommonConfigApi + "/remove",
            type: "DELETE",
            data: { id: id },
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "删除成功");
                    $("#bootstrap-table").bootstrapTable("refresh");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "删除失败");
                }
            },
            error: function () {
                $.modal.alertWarning("删除失败");
            }
        });
    });
}

function imCreditCommonConfigInitTable(canView, canEdit, canDelete) {
    imCreditCommonConfigInitSelects();
    imInitTable({
        url: imCreditCommonConfigApi + "/page",
        formId: "credit-common-config-form",
        queryParams: imCreditCommonConfigQueryParams,
        responseHandler: imPageResponse,
        modalName: "公共配置",
        columns: [
            { field: "id", title: "ID", sortable: true, width: 80 },
            { field: "newUserDays", title: "新用户信用分底色天数", sortable: true, formatter: imCreditFormatDay },
            { field: "newUserJoinDays", title: "新用户加人天数", sortable: true, formatter: imCreditFormatDay },
            { field: "newUserCreateGroupDays", title: "新人建群天数", sortable: true, formatter: imCreditFormatDay },
            { field: "newUserCreateGroupMemberCount", title: "新人建群人数", sortable: true, formatter: function (v) { return imCreditCommonConfigFormatCount(v, "人"); } },
            { field: "createGroupCount", title: "建群数量", sortable: true, formatter: function (v) { return imCreditCommonConfigFormatCount(v, "个"); } },
            { field: "friendApplyCount", title: "加好友申请次数", sortable: true, formatter: function (v) { return imCreditCommonConfigFormatCount(v, "次"); } },
            { field: "newUserDaysV2", title: "注册天数发送链接天数", sortable: true, formatter: imCreditFormatDay },
            { field: "newInGroupDays", title: "新入群天数", sortable: true, formatter: imCreditFormatDay },
            { field: "isLianghao", title: "是否是靓号配置", sortable: true, formatter: imCreditCommonConfigFormatLianghao },
            { field: "isVip", title: "是否是会员配置", sortable: true, formatter: imCreditCommonConfigFormatVip },
            { field: "loginSecondVerifyMinPass", title: "登录二次验证至少通过几项", sortable: true, formatter: imCreditFormatLoginVerify },
            { field: "createTime", title: "创建时间", sortable: true },
            { field: "updateTime", title: "更新时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imCreditCommonConfigOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imCreditCommonConfigOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imCreditCommonConfigRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
