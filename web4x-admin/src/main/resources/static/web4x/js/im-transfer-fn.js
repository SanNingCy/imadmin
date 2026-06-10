/**
 * IM 功能开关（buttonConfig）- 列表页搜索区共用
 */
var imButtonConfigApi = ctx + "buttonConfig/buttonConfig";
var IM_TRANSFER_FN_KEY = "transfer";

function imButtonFnResolveEntity(res, key) {
    if (!res) {
        return {};
    }
    if (res[key]) {
        return res[key];
    }
    if (res.data && res.data[key]) {
        return res.data[key];
    }
    return {};
}

function imButtonFnSetLabel(labelSelector, enabled) {
    $(labelSelector)
        .text(enabled ? "开启" : "关闭")
        .toggleClass("is-on", enabled);
}

function imButtonFnLoad(canEditFn, buttonKey, toggleSelector, labelSelector) {
    if (!canEditFn) {
        return;
    }
    $.ajax({
        url: imButtonConfigApi + "/queryByButtonKey",
        type: "GET",
        data: { buttonKey: buttonKey },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                var cfg = imButtonFnResolveEntity(res, "buttonConfig");
                var enabled = Number(cfg.buttonStatus) === 1;
                $(toggleSelector).prop("checked", enabled);
                imButtonFnSetLabel(labelSelector, enabled);
            }
        }
    });
}

function imButtonFnBind(canEditFn, buttonKey, buttonName, toggleSelector, labelSelector) {
    if (!canEditFn) {
        return;
    }
    $(toggleSelector).on("change", function () {
        var checked = $(this).is(":checked");
        var self = this;
        $.ajax({
            url: imButtonConfigApi + "/updateKey",
            type: "POST",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify({
                buttonKey: buttonKey,
                buttonName: buttonName,
                buttonStatus: checked ? "1" : "0"
            }),
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imButtonFnSetLabel(labelSelector, checked);
                    $.modal.msgSuccess(res.msg || (checked ? "开启成功" : "关闭成功"));
                } else {
                    $(self).prop("checked", !checked);
                    $.modal.alertWarning((res && res.msg) ? res.msg : "操作失败");
                }
            },
            error: function () {
                $(self).prop("checked", !checked);
                $.modal.alertWarning("操作失败");
            }
        });
    });
}

function imButtonFnResetSearch(formId, toggleSelector, labelSelector) {
    var $toggle = $(toggleSelector);
    var enabled = $toggle.length ? $toggle.is(":checked") : false;
    $.form.reset(formId);
    if ($toggle.length) {
        $toggle.prop("checked", enabled);
        imButtonFnSetLabel(labelSelector, enabled);
    }
}

function imButtonFnInitPage(canEditFn, buttonKey, buttonName, toggleSelector, labelSelector) {
    imButtonFnLoad(canEditFn, buttonKey, toggleSelector, labelSelector);
    imButtonFnBind(canEditFn, buttonKey, buttonName, toggleSelector, labelSelector);
}

function imTransferFnResetSearch(formId, toggleSelector, labelSelector) {
    imButtonFnResetSearch(formId, toggleSelector, labelSelector);
}

function imTransferFnInitPage(canEditFn, toggleSelector, labelSelector) {
    imButtonFnInitPage(canEditFn, IM_TRANSFER_FN_KEY, "转账功能", toggleSelector, labelSelector);
}
