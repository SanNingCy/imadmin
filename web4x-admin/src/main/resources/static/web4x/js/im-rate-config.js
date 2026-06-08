/**
 * IM 平台费率设置（接口：/admin/asset/rateConfig/*）
 */
var imRateConfigApi = ctx + "admin/asset/rateConfig";

var IM_RATE_PAYMENT_TYPE = {
    "1": "入金wx",
    "2": "提现",
    "3": "IM内部"
};

function imRateConfigQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("rate-config-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imRateConfigResolveEntity(res, key) {
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

function imRateConfigFormatPaymentType(value) {
    return IM_RATE_PAYMENT_TYPE[String(value)] || value || "-";
}

function imRateConfigFormatRate(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    if (isNaN(num)) {
        return value;
    }
    return (num * 100).toFixed(2) + "%";
}

function imRateConfigResetModal() {
    $("#rate-config-id").val("");
    $("#rate-config-paymentType").val("1");
    $("#rate-config-rate").val("");
}

function imRateConfigFillModal(info) {
    $("#rate-config-id").val(info.id || "");
    $("#rate-config-paymentType").val(info.paymentType != null ? String(info.paymentType) : "1");
    $("#rate-config-rate").val(info.rate != null ? info.rate : "");
}

function imRateConfigSetReadOnly(readOnly, isAdd) {
    $("#rate-config-modal-form :input").prop("disabled", readOnly);
    $("#rate-config-paymentType").prop("disabled", readOnly || !isAdd);
}

function imRateConfigShowModal(mode, readOnly) {
    var titles = { add: "新建费率", edit: "修改费率", view: "查看费率" };
    layer.open({
        type: 1,
        title: titles[mode] || "平台费率",
        area: ["480px", "280px"],
        shadeClose: true,
        content: $("#rate-config-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imRateConfigSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imRateConfigOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    var isAdd = String(mode) === "add";
    imRateConfigResetModal();
    imRateConfigSetReadOnly(readOnly, isAdd);

    if (id) {
        $.ajax({
            url: imRateConfigApi + "/" + id,
            type: "GET",
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imRateConfigFillModal(imRateConfigResolveEntity(res, "config"));
                    imRateConfigSetReadOnly(readOnly, false);
                    imRateConfigShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imRateConfigShowModal(mode, readOnly);
    }
}

function imRateConfigSave(layerIndex, mode) {
    var paymentType = $("#rate-config-paymentType").val();
    var rate = $.trim($("#rate-config-rate").val());
    if (!paymentType) {
        return $.modal.alertWarning("请选择费率类型");
    }
    if (!rate) {
        return $.modal.alertWarning("请输入费率");
    }

    var payload = {
        id: $("#rate-config-id").val() || undefined,
        paymentType: Number(paymentType),
        rate: rate
    };

    var isAdd = String(mode) === "add";
    $.ajax({
        url: isAdd ? imRateConfigApi + "/save" : imRateConfigApi + "/update",
        type: isAdd ? "POST" : "PUT",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess(res.msg || "保存成功");
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

function imRateConfigInitTable(canView, canAdd, canEdit) {
    imInitTable({
        url: imRateConfigApi + "/page",
        formId: "rate-config-form",
        modalName: "平台费率设置",
        sortName: "createTime",
        sortOrder: "desc",
        queryParams: imRateConfigQueryParams,
        columns: [
            { field: "id", title: "id", sortable: true },
            {
                field: "rate",
                title: "费率",
                sortable: true,
                formatter: function (value) {
                    return imRateConfigFormatRate(value);
                }
            },
            {
                field: "paymentType",
                title: "费率类型",
                sortable: true,
                formatter: function (value) {
                    return imRateConfigFormatPaymentType(value);
                }
            },
            { field: "createTime", title: "创建时间", sortable: true },
            { field: "updateTime", title: "更新时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imRateConfigOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imRateConfigOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
