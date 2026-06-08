/**
 * 会议管理模块公共工具
 */
var imLiveApi = ctx + "admin/live";

var IM_LIVE_STATUS_MAP = { 0: "禁用", 1: "启用" };

var IM_LIVE_ORDER_STATUS_MAP = {
    pending_create: "待创建",
    active: "进行中",
    destroyed: "房间已销毁"
};

function imLiveQueryParams(formId, params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON(formId);
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imLiveResolveEntity(res, key) {
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

function imLiveResolveList(res, key) {
    if (!res) {
        return [];
    }
    if (res[key]) {
        return res[key];
    }
    if (res.data && res.data[key]) {
        return res.data[key];
    }
    return [];
}

function imLiveFormatStatus(val) {
    var num = Number(val);
    var label = IM_LIVE_STATUS_MAP[num];
    if (label == null) {
        return val == null || val === "" ? "-" : String(val);
    }
    var cls = num === 1 ? "label-success" : "label-default";
    return '<span class="label ' + cls + '">' + label + "</span>";
}

function imLiveFormatOrderStatus(val) {
    if (val == null || val === "") {
        return "-";
    }
    return IM_LIVE_ORDER_STATUS_MAP[String(val)] || String(val);
}

function imLiveFormatAmount(val) {
    if (val == null || val === "") {
        return "-";
    }
    var num = Number(val);
    return Number.isFinite(num) ? String(val) : "-";
}

function imLiveAjax(options) {
    options = options || {};
    options.beforeSend = imTableBeforeSend;
    options.dataType = options.dataType || "json";
    return $.ajax(options);
}

function imLiveSetFormReadOnly($form, readOnly) {
    $form.find(":input").each(function () {
        if (readOnly) {
            $(this).prop("readonly", true).prop("disabled", true);
        } else {
            $(this).prop("readonly", false).prop("disabled", false);
        }
    });
}

function imLiveFillStatusSelect($select, includeEmpty, emptyLabel) {
    if (!$select || !$select.length) {
        return;
    }
    $select.empty();
    if (includeEmpty !== false) {
        $select.append('<option value="">' + (emptyLabel || "全部") + "</option>");
    }
    $select.append('<option value="1">启用</option>');
    $select.append('<option value="0">禁用</option>');
}

function imLiveDelete(apiPath, id, confirmMsg, tableId) {
    tableId = tableId || "bootstrap-table";
    $.modal.confirm(confirmMsg || "确定删除该记录吗？", function () {
        imLiveAjax({
            url: apiPath + "/delete",
            type: "DELETE",
            data: { id: id },
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess(res.msg || "删除成功");
                    $("#" + tableId).bootstrapTable("refresh");
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
