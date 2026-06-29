/**
 * IM 信用分模块公共常量与工具
 */
var IM_CREDIT_TYPE_MAP = {
    1: "在线时长",
    2: "注册天数",
    5: "平台贡献",
    6: "违规扣除",
    7: "系统添加",
    8: "系统扣减",
    9: "邀请好友",
    10: "持有IDOC",
    11: "担保交易",
    12: "获得背书"
};

var IM_CREDIT_STATUS_MAP = { 0: "未开通", 1: "已开通" };
var IM_CREDIT_FLAG_MAP = { 0: "否", 1: "是" };
var IM_CREDIT_CONSTITUTE_MAP = { 0: "不展示", 1: "展示" };
var IM_CREDIT_LOGIN_VERIFY_MAP = { 0: "全部验证", 1: "任选其一" };

function imCreditApi(path) {
    return ctx + "admin/creditScore/" + path;
}

function imCreditQueryParams(formId, params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON(formId);
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imCreditResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imCreditFormatType(val) {
    var num = Number(val);
    return IM_CREDIT_TYPE_MAP[num] || (val == null || val === "" ? "-" : String(val));
}

function imCreditFormatPercent(val) {
    var num = Number(val);
    return Number.isFinite(num) ? (num * 100).toFixed(2) + "%" : "-";
}

function imCreditFormatScore(val) {
    var num = Number(val);
    return Number.isFinite(num) ? num.toFixed(2) : "-";
}

function imCreditFormatPrice(val) {
    return imCreditFormatUsdtPrice(val);
}

function imCreditFormatOdicPrice(val) {
    var num = Number(val);
    return Number.isFinite(num) ? num.toFixed(2) + " ODIC" : "-";
}

function imCreditFormatUsdtPrice(val) {
    var num = Number(val);
    return Number.isFinite(num) ? num.toFixed(2) + " USDT" : "-";
}

function imCreditFormatFlag(val) {
    var num = Number(val);
    return IM_CREDIT_FLAG_MAP[num] != null ? IM_CREDIT_FLAG_MAP[num] : "-";
}

function imCreditFormatConstitute(val) {
    var num = Number(val);
    return IM_CREDIT_CONSTITUTE_MAP[num] != null ? IM_CREDIT_CONSTITUTE_MAP[num] : "-";
}

function imCreditFormatCreditStatus(val) {
    var num = Number(val);
    return IM_CREDIT_STATUS_MAP[num] != null ? IM_CREDIT_STATUS_MAP[num] : "-";
}

function imCreditFormatDay(val) {
    var num = Number(val);
    return Number.isFinite(num) ? String(num) : "-";
}

function imCreditFormatLoginVerify(val) {
    var num = Number(val);
    return IM_CREDIT_LOGIN_VERIFY_MAP[num] != null ? IM_CREDIT_LOGIN_VERIFY_MAP[num] : "-";
}

function imCreditEllipsis(val, max) {
    if (val == null || val === "") return "-";
    var text = String(val);
    max = max || 30;
    if (text.length <= max) return text;
    return '<span title="' + text.replace(/"/g, "&quot;") + '">' + text.substring(0, max) + "...</span>";
}

function imCreditAjax(options) {
    options = options || {};
    options.beforeSend = imTableBeforeSend;
    options.dataType = options.dataType || "json";
    return $.ajax(options);
}

function imCreditTypeSelectOptions($select, includeEmpty) {
    if (!$select || !$select.length) return;
    $select.empty();
    if (includeEmpty !== false) {
        $select.append('<option value="">全部</option>');
    }
    $.each(IM_CREDIT_TYPE_MAP, function (k, label) {
        $select.append('<option value="' + k + '">' + label + "</option>");
    });
}

function imCreditFillDetailTable($tbody, rows) {
    $tbody.empty();
    if (!rows || !rows.length) {
        $tbody.append('<tr><td colspan="20" class="text-center text-muted">暂无数据</td></tr>');
        return;
    }
    $.each(rows, function (_, row) {
        var html = "<tr>";
        $.each(row, function (_, cell) {
            html += "<td>" + (cell == null || cell === "" ? "-" : cell) + "</td>";
        });
        html += "</tr>";
        $tbody.append(html);
    });
}

function imCreditRenderTypesPreview(types, maxCount) {
    if (!types || !types.length) return "-";
    maxCount = maxCount || 3;
    var html = [];
    var preview = types.slice(0, maxCount);
    preview.forEach(function (item) {
        html.push(
            '<div class="credit-type-tag"><span class="label label-info">' + imCreditFormatType(item.type) +
            '</span> <span>' + imCreditFormatScore(item.currentScore) + " / " + imCreditFormatScore(item.maxLimit) + "</span></div>"
        );
    });
    if (types.length > maxCount) {
        html.push('<div class="text-muted">+' + (types.length - maxCount) + "</div>");
    }
    return html.join("");
}

function imCreditSetFormReadOnly($form, readOnly, excludeIds) {
    excludeIds = excludeIds || [];
    $form.find(":input").each(function () {
        var id = this.id;
        if (excludeIds.indexOf(id) >= 0) return;
        if (readOnly) {
            $(this).prop("readonly", true).prop("disabled", true);
        } else {
            $(this).prop("readonly", false).prop("disabled", false);
        }
    });
    excludeIds.forEach(function (id) {
        $("#" + id).prop("readonly", true);
    });
}
