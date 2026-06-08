/**
 * 会议代币系数配置 - /admin/live/roomConfig/page + /admin/live/billingRule/*
 */
var imLiveRoomConfigApi = imLiveApi + "/roomConfig";
var imLiveBillingRuleApi = imLiveApi + "/billingRule";

var imLiveRoomConfigState = {
    tierOptions: [],
    durationOptions: []
};

function imLiveRoomConfigCalcToken(coefficient, people, minutes) {
    var coef = Number(coefficient);
    var p = Number(people);
    var m = Number(minutes);
    if (!Number.isFinite(coef) || !Number.isFinite(p) || !Number.isFinite(m)) {
        return null;
    }
    return coef / 100 * p * (m / 60);
}

function imLiveRoomConfigFormatHours(minutes) {
    var hours = minutes / 60;
    return hours.toFixed(2) + "小时(" + minutes + "分钟)";
}

function imLiveRoomConfigSortDurations(list) {
    return list.slice().sort(function (a, b) {
        var av = Number(a.value) || 0;
        var bv = Number(b.value) || 0;
        if (av >= 60 && bv >= 60) {
            return av - bv;
        }
        if (av >= 60) {
            return -1;
        }
        if (bv >= 60) {
            return 1;
        }
        return bv - av;
    });
}

function imLiveRoomConfigRenderBudget() {
    var coefficient = $.trim($("#live-room-config-coefficient").val());
    var $container = $("#live-room-config-budget");
    $container.empty();

    if (!coefficient) {
        $container.append('<div class="text-muted text-center">请输入代币比例系数后查看预算</div>');
        return;
    }

    var people = 100;
    var durations = imLiveRoomConfigState.durationOptions.slice();
    if (!durations.length) {
        durations = [
            { name: "1小时", value: 60 },
            { name: "2小时", value: 120 },
            { name: "3小时", value: 180 },
            { name: "20分钟", value: 11 },
            { name: "5分钟", value: 5 }
        ];
    }
    durations = imLiveRoomConfigSortDurations(durations);

    durations.forEach(function (item) {
        var minutes = item.value;
        var token = imLiveRoomConfigCalcToken(coefficient, people, minutes);
        if (token == null) {
            return;
        }
        var hoursText = imLiveRoomConfigFormatHours(minutes);
        var formula;
        if (minutes >= 60) {
            formula = coefficient + " / 100 x " + people + "人 x " + hoursText + " 所需代币:";
        } else {
            formula = coefficient + " / 100 x " + hoursText + " 所需代币:";
        }
        $container.append(
            '<div class="live-budget-item">' +
            '<span>' + formula + "</span>" +
            '<span class="amount">' + token.toFixed(4) + " ODIC</span>" +
            "</div>"
        );
    });
}

function imLiveRoomConfigFillForm(vo) {
    $("#live-room-config-id").val(vo.id || "");
    $("#live-room-config-coefficient").val(
        vo.stepConsumptionToken != null ? vo.stepConsumptionToken : ""
    );
    $("#live-room-config-renewal").val(vo.renewalRules || "");
    imLiveRoomConfigState.tierOptions = vo.userTierOptions || [];
    imLiveRoomConfigState.durationOptions = vo.timeOline || [];
    imLiveRoomConfigRenderBudget();
}

function imLiveRoomConfigLoad() {
    imLiveAjax({
        url: imLiveRoomConfigApi + "/page",
        type: "GET",
        data: { pageNo: 1, pageSize: 1 },
        success: function (res) {
            var page = imResolvePage(res);
            if (res && (res.success === true || res.code === 200) && page && page.list && page.list.length) {
                imLiveRoomConfigFillForm(page.list[0]);
            } else if (res && !(res.success === true || res.code === 200)) {
                $.modal.alertWarning((res && res.msg) ? res.msg : "加载配置失败");
            }
        },
        error: function () {
            $.modal.alertWarning("加载配置失败");
        }
    });
}

function imLiveRoomConfigSave() {
    var id = $("#live-room-config-id").val();
    var coefficient = $.trim($("#live-room-config-coefficient").val());
    var renewal = $.trim($("#live-room-config-renewal").val());

    if (!coefficient) {
        return $.modal.alertWarning("请输入代币比例系数");
    }

    var payload = {
        unitPrice: coefficient,
        roundingRule: renewal || undefined
    };
    var url = imLiveBillingRuleApi + "/save";
    if (id) {
        url = imLiveBillingRuleApi + "/update";
        payload.id = id;
    }

    imLiveAjax({
        url: url,
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess(res.msg || "保存成功");
                imLiveRoomConfigLoad();
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "保存失败");
            }
        },
        error: function () {
            $.modal.alertWarning("保存失败");
        }
    });
}

function imLiveRoomConfigInit() {
    $("#live-room-config-coefficient").on("input change", imLiveRoomConfigRenderBudget);
    imLiveRoomConfigLoad();
}
