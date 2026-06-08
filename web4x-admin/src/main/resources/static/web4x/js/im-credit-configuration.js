/**
 * IM 信用分基础配置
 * - GET    /admin/creditScore/config/page
 * - GET    /admin/creditScore/config/queryById/{id}
 * - PUT    /admin/creditScore/config/update
 * - DELETE /admin/creditScore/config/remove?id=
 */

var imCreditConfigurationApi = ctx + "admin/creditScore/config";

function imCreditConfigurationQueryParams(params) {
    return imCreditQueryParams("credit-config-form", params);
}

function imCreditConfigurationResetModal() {
    $("#credit-config-id").val("");
    $("#credit-config-modal-form :input").not("[type=hidden]").val("");
}

function imCreditConfigurationFillModal(info) {
    info = info || {};
    $("#credit-config-id").val(info.id || "");
    $("#credit-config-initScore").val(info.initScore != null ? info.initScore : "");
    $("#credit-config-vipBonusRate").val(info.vipBonusRate != null ? info.vipBonusRate : "");
    $("#credit-config-lianghaoBonusRate").val(info.lianghaoBonusRate != null ? info.lianghaoBonusRate : "");
    $("#credit-config-price").val(info.price != null ? info.price : "");
    $("#credit-config-scoreInfo").val(info.scoreInfo || "");
}

function imCreditConfigurationSetReadOnly(readOnly) {
    imCreditSetFormReadOnly($("#credit-config-modal-form"), readOnly);
}

function imCreditConfigurationShowModal(mode, readOnly) {
    layer.open({
        type: 1,
        title: mode === "edit" ? "修改信用分基础配置" : "查看信用分基础配置",
        area: ["860px", "90%"],
        shadeClose: true,
        content: $("#credit-config-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imCreditConfigurationSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imCreditConfigurationOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imCreditConfigurationResetModal();
    imCreditConfigurationSetReadOnly(readOnly);

    if (!id) {
        $.modal.alertWarning("缺少记录ID");
        return;
    }

    imCreditAjax({
        url: imCreditConfigurationApi + "/queryById/" + id,
        type: "GET",
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                imCreditConfigurationFillModal(imCreditResolveEntity(res, "config"));
                imCreditConfigurationShowModal(mode, readOnly);
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
            }
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imCreditConfigurationValidate() {
    var initScore = $("#credit-config-initScore").val();
    var vipBonusRate = $("#credit-config-vipBonusRate").val();
    var lianghaoBonusRate = $("#credit-config-lianghaoBonusRate").val();
    var price = $("#credit-config-price").val();

    if (initScore === "" || initScore == null) {
        $.modal.alertWarning("请输入初始分");
        return false;
    }
    if (vipBonusRate === "" || vipBonusRate == null) {
        $.modal.alertWarning("请输入会员加成");
        return false;
    }
    if (lianghaoBonusRate === "" || lianghaoBonusRate == null) {
        $.modal.alertWarning("请输入靓号加成");
        return false;
    }
    if (price === "" || price == null) {
        $.modal.alertWarning("请输入价格");
        return false;
    }
    return true;
}

function imCreditConfigurationSave(layerIndex) {
    var id = $("#credit-config-id").val();
    if (!id) return $.modal.alertWarning("缺少记录ID");
    if (!imCreditConfigurationValidate()) return;

    var payload = {
        id: id,
        initScore: parseFloat($("#credit-config-initScore").val()),
        vipBonusRate: parseFloat($("#credit-config-vipBonusRate").val()),
        lianghaoBonusRate: parseFloat($("#credit-config-lianghaoBonusRate").val()),
        price: parseFloat($("#credit-config-price").val()),
        scoreInfo: $("#credit-config-scoreInfo").val()
    };

    imCreditAjax({
        url: imCreditConfigurationApi + "/update",
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

function imCreditConfigurationRemove(id) {
    $.modal.confirm("确定删除该配置吗？", function () {
        imCreditAjax({
            url: imCreditConfigurationApi + "/remove",
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

function imCreditConfigurationInitTable(canView, canEdit, canDelete) {
    imInitTable({
        url: imCreditConfigurationApi + "/page",
        formId: "credit-config-form",
        queryParams: imCreditConfigurationQueryParams,
        responseHandler: imPageResponse,
        modalName: "信用分基础配置",
        columns: [
            { field: "id", title: "id", sortable: true, width: 100 },
            { field: "initScore", title: "初始分", sortable: true, formatter: imCreditFormatScore },
            { field: "vipBonusRate", title: "会员加成", sortable: true, formatter: imCreditFormatPercent },
            { field: "lianghaoBonusRate", title: "靓号加成", sortable: true, formatter: imCreditFormatPercent },
            { field: "price", title: "价格", sortable: true, formatter: imCreditFormatPrice },
            { field: "createTime", title: "创建时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imCreditConfigurationOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imCreditConfigurationOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imCreditConfigurationRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
