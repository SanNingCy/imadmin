/**
 * IM 信用分类型配置
 * - GET    /admin/creditScore/typeConfig/page
 * - GET    /admin/creditScore/typeConfig/queryById/{id}
 * - POST   /admin/creditScore/typeConfig/save
 * - PUT    /admin/creditScore/typeConfig/update
 * - PUT    /admin/creditScore/typeConfig/updateStatus?id=&status=
 * - DELETE /admin/creditScore/typeConfig/remove?id=
 */

var imCreditTypeApi = imCreditApi("typeConfig");

var imCreditTypePerms = { canEdit: false };

function imCreditTypeQueryParams(params) {
    return imCreditQueryParams("credit-type-form", params);
}

function imCreditTypeFormatStatus(val, row) {
    var enabled = Number(val) === 1;
    var label = enabled ? "启用" : "禁用";
    var cls = enabled ? "label-success" : "label-default";
    if (!imCreditTypePerms.canEdit) {
        return '<span class="label ' + cls + '">' + label + "</span>";
    }
    var nextStatus = enabled ? 0 : 1;
    return '<a class="label ' + cls + '" href="javascript:void(0)" onclick="imCreditTypeToggleStatus(\'' +
        row.id + "'," + nextStatus + ')">' + label + "</a>";
}

function imCreditTypeResetModal() {
    $("#credit-type-id").val("");
    $("#credit-type-type").val("");
    $("#credit-type-maxLimit").val("");
    $("#credit-type-score").val("");
    $("#credit-type-status").val("1");
    $("#credit-type-orderNum").val("");
    $("#credit-type-constituteShow").val("1");
}

function imCreditTypeFillModal(info) {
    $("#credit-type-id").val(info.id || "");
    $("#credit-type-type").val(info.type != null ? String(info.type) : "");
    $("#credit-type-maxLimit").val(info.maxLimit != null ? info.maxLimit : "");
    $("#credit-type-score").val(info.score != null ? info.score : "");
    $("#credit-type-status").val(info.status != null ? String(info.status) : "1");
    $("#credit-type-orderNum").val(info.orderNum != null ? info.orderNum : "");
    $("#credit-type-constituteShow").val(info.constituteShow != null ? String(info.constituteShow) : "1");
}

function imCreditTypeShowModal(mode, readOnly) {
    var titles = {
        add: "新增信用分类型配置",
        edit: "修改信用分类型配置",
        view: "查看信用分类型配置"
    };
    layer.open({
        type: 1,
        title: titles[mode] || "信用分类型配置",
        area: ["520px", "460px"],
        shadeClose: true,
        content: $("#credit-type-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imCreditTypeSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imCreditTypeOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imCreditTypeResetModal();
    imCreditSetFormReadOnly($("#credit-type-modal-form"), readOnly);

    if (id) {
        imCreditAjax({
            url: imCreditTypeApi + "/queryById/" + encodeURIComponent(id),
            type: "GET",
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imCreditTypeFillModal(imCreditResolveEntity(res, "config"));
                    imCreditTypeShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imCreditTypeShowModal(mode, readOnly);
    }
}

function imCreditTypeCollectPayload() {
    var typeVal = $("#credit-type-type").val();
    var maxLimitVal = $("#credit-type-maxLimit").val();
    var scoreVal = $("#credit-type-score").val();
    var statusVal = $("#credit-type-status").val();
    var orderNumVal = $("#credit-type-orderNum").val();
    var constituteShowVal = $("#credit-type-constituteShow").val();

    if (!typeVal) return $.modal.alertWarning("请选择类型"), null;
    if (maxLimitVal === "" || maxLimitVal == null) return $.modal.alertWarning("请输入总上限"), null;
    if (scoreVal === "" || scoreVal == null) return $.modal.alertWarning("请输入基础分数"), null;
    if (statusVal === "" || statusVal == null) return $.modal.alertWarning("请选择状态"), null;
    if (orderNumVal === "" || orderNumVal == null) return $.modal.alertWarning("请输入排序值"), null;
    if (constituteShowVal === "" || constituteShowVal == null) return $.modal.alertWarning("请选择构成展示"), null;

    return {
        type: parseInt(typeVal, 10),
        maxLimit: parseFloat(maxLimitVal),
        score: parseFloat(scoreVal),
        status: parseInt(statusVal, 10),
        orderNum: parseInt(orderNumVal, 10),
        constituteShow: parseInt(constituteShowVal, 10)
    };
}

function imCreditTypeSave(layerIndex, mode) {
    var payload = imCreditTypeCollectPayload();
    if (!payload) return;

    var url = imCreditTypeApi + "/save";
    var method = "POST";
    if (mode === "edit") {
        url = imCreditTypeApi + "/update";
        method = "PUT";
        payload.id = $("#credit-type-id").val();
    }

    imCreditAjax({
        url: url,
        type: method,
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
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

function imCreditTypeToggleStatus(id, status) {
    imCreditAjax({
        url: imCreditTypeApi + "/updateStatus",
        type: "PUT",
        data: { id: id, status: status },
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess(res.msg || "状态更新成功");
                $("#bootstrap-table").bootstrapTable("refresh");
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "状态更新失败");
            }
        },
        error: function () {
            $.modal.alertWarning("状态更新失败");
        }
    });
}

function imCreditTypeRemove(id) {
    $.modal.confirm("确定删除该配置吗？", function () {
        imCreditAjax({
            url: imCreditTypeApi + "/remove",
            type: "DELETE",
            data: { id: id },
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

function imCreditTypeInitTable(canView, canEdit, canDelete) {
    imCreditTypePerms.canEdit = !!canEdit;

    var $searchType = $("#credit-type-search-type");
    $searchType.empty();
    imCreditTypeSelectOptions($searchType, true);

    var $modalType = $("#credit-type-type");
    $modalType.empty();
    $modalType.append('<option value="">请选择</option>');
    imCreditTypeSelectOptions($modalType, false);

    imInitTable({
        url: imCreditTypeApi + "/page",
        formId: "credit-type-form",
        queryParams: imCreditTypeQueryParams,
        responseHandler: imPageResponse,
        modalName: "信用分类型配置",
        columns: [
            {
                field: "type",
                title: "类型",
                sortable: true,
                formatter: function (val) { return imCreditFormatType(val); }
            },
            {
                field: "maxLimit",
                title: "总上限",
                sortable: true,
                formatter: imCreditFormatScore
            },
            {
                field: "score",
                title: "基础分数",
                sortable: true,
                formatter: imCreditFormatScore
            },
            {
                field: "status",
                title: "状态",
                sortable: true,
                formatter: imCreditTypeFormatStatus
            },
            {
                field: "constituteShow",
                title: "构成展示",
                sortable: true,
                formatter: imCreditFormatConstitute
            },
            { field: "orderNum", title: "排序值", sortable: true },
            { field: "createTime", title: "创建时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imCreditTypeOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imCreditTypeOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imCreditTypeRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
