/**
 * IM 会员开通套餐
 * - GET    /admin/ops/vipOpenPlan/page
 * - GET    /admin/ops/vipOpenPlan/queryById/{id}
 * - POST   /admin/ops/vipOpenPlan/save
 * - PUT    /admin/ops/vipOpenPlan/update
 * - PUT    /admin/ops/vipOpenPlan/updateStatus
 * - DELETE /admin/ops/vipOpenPlan/remove?id=
 */

var imVipOpenPlanApi = ctx + "admin/ops/vipOpenPlan";

function imVipOpenPlanQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("vip-open-plan-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imVipOpenPlanResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imVipOpenPlanFormatStatus(val) {
    return Number(val) === 1 ? "已启用" : "已停用";
}

function imVipOpenPlanFormatPrice(val) {
    if (val == null || val === "") return "-";
    var num = Number(val);
    return Number.isFinite(num) ? num.toFixed(2) : val;
}

function imVipOpenPlanResetModal() {
    $("#vip-open-plan-id").val("");
    $("#vip-open-plan-planName").val("");
    $("#vip-open-plan-durationDays").val("");
    $("#vip-open-plan-price").val("");
    $("#vip-open-plan-sortOrder").val("0");
    $("#vip-open-plan-status").val("1");
}

function imVipOpenPlanFillModal(info) {
    $("#vip-open-plan-id").val(info.id || "");
    $("#vip-open-plan-planName").val(info.planName != null ? info.planName : "");
    $("#vip-open-plan-durationDays").val(info.durationDays != null ? info.durationDays : "");
    $("#vip-open-plan-price").val(info.price != null ? info.price : "");
    $("#vip-open-plan-sortOrder").val(info.sortOrder != null ? info.sortOrder : "0");
    $("#vip-open-plan-status").val(info.status != null ? String(info.status) : "1");
}

function imVipOpenPlanSetReadOnly(readOnly) {
    $("#vip-open-plan-modal-form :input").prop("disabled", readOnly);
}

function imVipOpenPlanShowModal(mode, readOnly) {
    var titles = { add: "新建会员开通套餐", edit: "修改会员开通套餐", view: "查看会员开通套餐" };
    layer.open({
        type: 1,
        title: titles[mode] || "会员开通套餐",
        area: ["520px", "420px"],
        shadeClose: true,
        content: $("#vip-open-plan-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imVipOpenPlanSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imVipOpenPlanToggleAddButton(total) {
    var $btn = $("#vip-open-plan-add-btn");
    if (!$btn.length) {
        return;
    }
    if (Number(total) > 0) {
        $btn.hide();
    } else {
        $btn.show();
    }
}

function imVipOpenPlanOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    if (String(mode) === "add") {
        var tableData = $("#bootstrap-table").bootstrapTable("getData") || [];
        var total = $("#bootstrap-table").bootstrapTable("getOptions").totalRows;
        if (Number(total) > 0 || tableData.length > 0) {
            return $.modal.alertWarning("仅允许配置一个会员开通套餐，请修改现有套餐或先删除后再新建");
        }
    }
    imVipOpenPlanResetModal();
    imVipOpenPlanSetReadOnly(readOnly);

    if (id) {
        $.ajax({
            url: imVipOpenPlanApi + "/queryById/" + encodeURIComponent(id),
            type: "GET",
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imVipOpenPlanFillModal(imVipOpenPlanResolveEntity(res, "plan"));
                    imVipOpenPlanShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imVipOpenPlanShowModal(mode, readOnly);
    }
}

function imVipOpenPlanSave(layerIndex, mode) {
    var planName = $("#vip-open-plan-planName").val();
    var durationDays = $("#vip-open-plan-durationDays").val();
    var price = $("#vip-open-plan-price").val();
    var sortOrder = $("#vip-open-plan-sortOrder").val();
    var status = $("#vip-open-plan-status").val();

    if (!planName || !String(planName).trim()) return $.modal.alertWarning("请输入套餐名称");
    if (durationDays === "" || durationDays == null) return $.modal.alertWarning("请输入会员天数");
    if (parseInt(durationDays, 10) < 1) return $.modal.alertWarning("会员天数须大于0");
    if (price === "" || price == null) return $.modal.alertWarning("请输入USDT价格");
    if (sortOrder === "" || sortOrder == null) return $.modal.alertWarning("请输入排序");
    if (status === "" || status == null) return $.modal.alertWarning("请选择状态");

    var payload = {
        planName: String(planName).trim(),
        durationDays: parseInt(durationDays, 10),
        price: parseFloat(price),
        sortOrder: parseInt(sortOrder, 10),
        status: parseInt(status, 10)
    };

    var url = imVipOpenPlanApi + "/save";
    var method = "POST";
    if (mode === "edit") {
        url = imVipOpenPlanApi + "/update";
        method = "PUT";
        payload.id = $("#vip-open-plan-id").val();
    }

    $.ajax({
        url: url,
        type: method,
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

function imVipOpenPlanRemove(id) {
    $.modal.confirm("确定删除该套餐吗？", function () {
        $.ajax({
            url: imVipOpenPlanApi + "/remove",
            type: "DELETE",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess(res.msg || "删除成功");
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

function imVipOpenPlanInitTable(canView, canEdit, canDelete) {
    imInitTable({
        url: imVipOpenPlanApi + "/page",
        formId: "vip-open-plan-form",
        queryParams: imVipOpenPlanQueryParams,
        responseHandler: imPageResponse,
        modalName: "会员开通套餐",
        onLoadSuccess: function (data) {
            var total = data && data.total != null ? data.total : 0;
            if (!total && data && data.rows) {
                total = data.rows.length;
            }
            imVipOpenPlanToggleAddButton(total);
        },
        columns: [
            { field: "id", title: "ID", sortable: true, width: 80 },
            { field: "planName", title: "套餐名称", sortable: true },
            { field: "durationDays", title: "会员天数", sortable: true },
            { field: "price", title: "USDT价格", sortable: true, formatter: imVipOpenPlanFormatPrice },
            { field: "sortOrder", title: "排序", sortable: true },
            { field: "status", title: "状态", sortable: true, formatter: imVipOpenPlanFormatStatus },
            { field: "updateTime", title: "更新时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imVipOpenPlanOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imVipOpenPlanOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imVipOpenPlanRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
