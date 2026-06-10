/**
 * IM 功能开关管理
 * - GET    /buttonConfig/buttonConfig/list
 * - GET    /buttonConfig/buttonConfig/queryById?id=
 * - POST   /buttonConfig/buttonConfig/save
 * - POST   /buttonConfig/buttonConfig/updateKey
 * - DELETE /buttonConfig/buttonConfig/delete?ids=
 */

var imFeaturesApi = ctx + "buttonConfig/buttonConfig";

function imFeaturesQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize);
    var formValues = $.common.formToJSON("features-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imFeaturesResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imFeaturesFormatStatus(val) {
    return Number(val) === 1 ? "已开启" : "已关闭";
}

function imFeaturesResetModal() {
    $("#features-id").val("");
    $("#features-buttonName").val("");
    $("#features-buttonKey").val("").prop("disabled", false);
    $("#features-buttonStatus").val("1");
}

function imFeaturesFillModal(info) {
    $("#features-id").val(info.id || "");
    $("#features-buttonName").val(info.buttonName || "");
    $("#features-buttonKey").val(info.buttonKey || "");
    $("#features-buttonStatus").val(info.buttonStatus != null ? String(info.buttonStatus) : "1");
}

function imFeaturesSetReadOnly(readOnly, isAdd) {
    $("#features-modal-form :input").prop("disabled", readOnly);
    $("#features-buttonKey").prop("disabled", readOnly || !isAdd);
}

function imFeaturesShowModal(mode, readOnly) {
    var titles = { add: "新建开关", edit: "修改开关", view: "查看开关" };
    layer.open({
        type: 1,
        title: titles[mode] || "功能开关",
        area: ["480px", "320px"],
        shadeClose: true,
        content: $("#features-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imFeaturesSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imFeaturesOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    var isAdd = String(mode) === "add";
    imFeaturesResetModal();
    imFeaturesSetReadOnly(readOnly, isAdd);

    if (id) {
        $.ajax({
            url: imFeaturesApi + "/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imFeaturesFillModal(imFeaturesResolveEntity(res, "buttonConfig"));
                    imFeaturesSetReadOnly(readOnly, false);
                    imFeaturesShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imFeaturesShowModal(mode, readOnly);
    }
}

function imFeaturesSave(layerIndex) {
    var buttonName = $.trim($("#features-buttonName").val());
    var buttonKey = $.trim($("#features-buttonKey").val());
    var buttonStatus = $("#features-buttonStatus").val();
    if (!buttonName) return $.modal.alertWarning("请输入按钮名称");
    if (!buttonKey) return $.modal.alertWarning("请输入权限标识");
    if (buttonStatus === "") return $.modal.alertWarning("请选择开关状态");

    var payload = {
        id: $("#features-id").val() || undefined,
        buttonName: buttonName,
        buttonKey: buttonKey,
        buttonStatus: buttonStatus
    };

    $.ajax({
        url: imFeaturesApi + "/save",
        type: "POST",
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

function imFeaturesToggleStatus(buttonKey, buttonName, checked) {
    $.ajax({
        url: imFeaturesApi + "/updateKey",
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
                $.modal.msgSuccess(res.msg || (checked ? "开启成功" : "关闭成功"));
                $("#bootstrap-table").bootstrapTable("refresh");
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "操作失败");
            }
        },
        error: function () {
            $.modal.alertWarning("操作失败");
        }
    });
}

function imFeaturesRemove(ids) {
    $.modal.confirm("确定删除选中的记录吗？", function () {
        $.ajax({
            url: imFeaturesApi + "/delete",
            type: "DELETE",
            data: { ids: ids },
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

function imFeaturesRemoveSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || !rows.length) {
        return $.modal.alertWarning("请至少选择一条记录");
    }
    imFeaturesRemove(rows.map(function (row) { return row.id; }).join(","));
}

function imFeaturesEditSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || rows.length !== 1) {
        return $.modal.alertWarning("请选择一条记录");
    }
    imFeaturesOpenModal("edit", rows[0].id);
}

function imFeaturesInitTable(canView, canEdit, canDelete) {
    imInitTable({
        url: imFeaturesApi + "/list",
        formId: "features-form",
        queryParams: imFeaturesQueryParams,
        responseHandler: imPageResponse,
        modalName: "功能开关",
        columns: [
            { checkbox: true },
            { field: "buttonName", title: "按钮名称" },
            { field: "buttonKey", title: "权限标识" },
            { field: "buttonStatus", title: "开关", formatter: imFeaturesFormatStatus },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imFeaturesOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        var on = Number(row.buttonStatus) === 1;
                        var toggleLabel = on ? "关闭" : "开启";
                        var toggleIcon = on ? "fa-toggle-off" : "fa-toggle-on";
                        actions.push('<a class="btn btn-warning btn-xs" href="javascript:void(0)" onclick="imFeaturesToggleStatus(\'' + row.buttonKey + '\',\'' + (row.buttonName || "").replace(/'/g, "\\'") + '\',' + (!on) + ')"><i class="fa ' + toggleIcon + '"></i>' + toggleLabel + '</a> ');
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imFeaturesOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imFeaturesRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
