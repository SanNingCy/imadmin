/**
 * IM 提现配置
 * - GET    /admin/asset/withdrawConfig/page
 * - GET    /admin/asset/withdrawConfig/queryById/{id}
 * - POST   /admin/asset/withdrawConfig/save
 * - PUT    /admin/asset/withdrawConfig/update
 * - DELETE /admin/asset/withdrawConfig/remove?id=
 */

var imWithdrawApi = ctx + "admin/asset/withdrawConfig";

function imWithdrawQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("withdraw-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imWithdrawResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imWithdrawFormatStatus(val) {
    return Number(val) === 1 ? "已启用" : "已关闭";
}

function imWithdrawResetModal() {
    $("#withdraw-id").val("");
    $("#withdraw-minAmount").val("");
    $("#withdraw-maxAmount").val("");
    $("#withdraw-status").val("1");
}

function imWithdrawFillModal(info) {
    $("#withdraw-id").val(info.id || "");
    $("#withdraw-minAmount").val(info.minAmount != null ? info.minAmount : "");
    $("#withdraw-maxAmount").val(info.maxAmount != null ? info.maxAmount : "");
    $("#withdraw-status").val(info.status != null ? String(info.status) : "1");
}

function imWithdrawSetReadOnly(readOnly) {
    $("#withdraw-modal-form :input").prop("disabled", readOnly);
}

function imWithdrawShowModal(mode, readOnly) {
    var titles = { add: "新建提现配置", edit: "修改提现配置", view: "查看提现配置" };
    layer.open({
        type: 1,
        title: titles[mode] || "提现配置",
        area: ["480px", "320px"],
        shadeClose: true,
        content: $("#withdraw-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imWithdrawSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imWithdrawOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imWithdrawResetModal();
    imWithdrawSetReadOnly(readOnly);

    if (id) {
        $.ajax({
            url: imWithdrawApi + "/queryById/" + encodeURIComponent(id),
            type: "GET",
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imWithdrawFillModal(imWithdrawResolveEntity(res, "config"));
                    imWithdrawShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imWithdrawShowModal(mode, readOnly);
    }
}

function imWithdrawSave(layerIndex, mode) {
    var minAmount = $("#withdraw-minAmount").val();
    var maxAmount = $("#withdraw-maxAmount").val();
    var status = $("#withdraw-status").val();
    if (minAmount === "" || minAmount == null) return $.modal.alertWarning("请输入最低提现金额");
    if (maxAmount === "" || maxAmount == null) return $.modal.alertWarning("请输入最高提现金额");
    if (status === "" || status == null) return $.modal.alertWarning("请选择状态");

    var payload = {
        minAmount: parseFloat(minAmount),
        maxAmount: parseFloat(maxAmount),
        status: parseInt(status, 10)
    };

    var url = imWithdrawApi + "/save";
    var method = "POST";
    if (mode === "edit") {
        url = imWithdrawApi + "/update";
        method = "PUT";
        payload.id = $("#withdraw-id").val();
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

function imWithdrawRemove(id) {
    $.modal.confirm("确定删除该配置吗？", function () {
        $.ajax({
            url: imWithdrawApi + "/remove",
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

function imWithdrawInitTable(canView, canEdit, canDelete) {
    imInitTable({
        url: imWithdrawApi + "/page",
        formId: "withdraw-form",
        queryParams: imWithdrawQueryParams,
        responseHandler: imPageResponse,
        modalName: "提现配置",
        columns: [
            { field: "id", title: "ID", sortable: true },
            { field: "minAmount", title: "最低提现金额", sortable: true },
            { field: "maxAmount", title: "最高提现金额", sortable: true },
            { field: "status", title: "状态", sortable: true, formatter: imWithdrawFormatStatus },
            { field: "createTime", title: "创建时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imWithdrawOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imWithdrawOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imWithdrawRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
