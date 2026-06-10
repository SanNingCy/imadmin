/**
 * IM 转账功能开关（出金/内部转账交易记录页共用）
 */
var imButtonConfigApi = ctx + "buttonConfig/buttonConfig";
var IM_TRANSFER_FN_KEY = "transfer";

function imTransferFnResolveEntity(res, key) {
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

function imTransferFnSetLabel(labelSelector, enabled) {
    $(labelSelector)
        .text(enabled ? "开启" : "关闭")
        .toggleClass("is-on", enabled);
}

function imTransferFnLoad(canEditFn, toggleSelector, labelSelector) {
    if (!canEditFn) {
        return;
    }
    $.ajax({
        url: imButtonConfigApi + "/queryByButtonKey",
        type: "GET",
        data: { buttonKey: IM_TRANSFER_FN_KEY },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                var cfg = imTransferFnResolveEntity(res, "buttonConfig");
                var enabled = Number(cfg.buttonStatus) === 1;
                $(toggleSelector).prop("checked", enabled);
                imTransferFnSetLabel(labelSelector, enabled);
            }
        }
    });
}

function imTransferFnBind(canEditFn, toggleSelector, labelSelector) {
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
                buttonKey: IM_TRANSFER_FN_KEY,
                buttonName: "转账功能",
                buttonStatus: checked ? "1" : "0"
            }),
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imTransferFnSetLabel(labelSelector, checked);
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

function imTransferFnResetSearch(formId, toggleSelector, labelSelector) {
    var $toggle = $(toggleSelector);
    var enabled = $toggle.length ? $toggle.is(":checked") : false;
    $.form.reset(formId);
    if ($toggle.length) {
        $toggle.prop("checked", enabled);
        imTransferFnSetLabel(labelSelector, enabled);
    }
}

function imTransferFnInitPage(canEditFn, toggleSelector, labelSelector) {
    imTransferFnLoad(canEditFn, toggleSelector, labelSelector);
    imTransferFnBind(canEditFn, toggleSelector, labelSelector);
}
