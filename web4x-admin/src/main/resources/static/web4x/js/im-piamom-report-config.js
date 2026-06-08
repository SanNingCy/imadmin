/**
 * 广场朋友圈 - 举报类型配置
 * - GET    /admin/piamom/reportConfig/list
 * - GET    /admin/piamom/reportConfig/queryById?id=
 * - POST   /admin/piamom/reportConfig/save|update
 * - DELETE /admin/piamom/reportConfig/delete?id=
 */

function imPiamomReportConfigResetModal() {
    $("#report-config-id").val("");
    $("#report-config-reportType").val("");
    $("#report-config-description").val("");
    $("#report-config-status").val("1");
}

function imPiamomReportConfigFillModal(info) {
    $("#report-config-id").val(info.id || "");
    $("#report-config-reportType").val(info.reportType || "");
    $("#report-config-description").val(info.description || "");
    $("#report-config-status").val(info.status != null ? String(info.status) : "1");
}

function imPiamomReportConfigShowModal(mode, readOnly) {
    var titles = {
        add: "新增举报类型",
        edit: "编辑举报类型",
        view: "查看举报类型"
    };
    layer.open({
        type: 1,
        title: titles[mode] || "举报类型",
        area: ["520px", "380px"],
        shadeClose: true,
        content: $("#report-config-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imPiamomReportConfigSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imPiamomReportConfigOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imPiamomReportConfigResetModal();
    imPiamomSetFormReadOnly($("#report-config-modal-form"), readOnly);

    if (id) {
        $.ajax({
            url: imPiamomApi + "/reportConfig/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imPiamomReportConfigFillModal(imPiamomResolveData(res, "config") || {});
                    imPiamomReportConfigShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imPiamomReportConfigShowModal(mode, readOnly);
    }
}

function imPiamomReportConfigCollectPayload() {
    var reportType = $.trim($("#report-config-reportType").val());
    var status = $("#report-config-status").val();

    if (!reportType) {
        return $.modal.alertWarning("请输入举报类型名称"), null;
    }
    if (status === "" || status == null) {
        return $.modal.alertWarning("请选择状态"), null;
    }

    return {
        reportType: reportType,
        description: $("#report-config-description").val(),
        status: parseInt(status, 10)
    };
}

function imPiamomReportConfigSave(layerIndex, mode) {
    var payload = imPiamomReportConfigCollectPayload();
    if (!payload) {
        return;
    }

    var url = imPiamomApi + "/reportConfig/save";
    if (mode === "edit") {
        url = imPiamomApi + "/reportConfig/update";
        payload.id = $("#report-config-id").val();
    }

    $.ajax({
        url: url,
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess((res && res.msg) ? res.msg : "保存成功");
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

function imPiamomReportConfigRemove(id) {
    $.modal.confirm("确定删除该举报类型吗？", function () {
        $.ajax({
            url: imPiamomApi + "/reportConfig/delete",
            type: "DELETE",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "删除成功");
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

function imPiamomReportConfigInitTable(canView, canEdit, canDelete) {
    imInitTable({
        url: imPiamomApi + "/reportConfig/list",
        showSearch: false,
        pagination: false,
        sidePagination: "client",
        responseHandler: imPiamomListResponse,
        modalName: "举报类型",
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "reportType", title: "举报类型", formatter: function (v) { return imFormatText(v, 24); } },
            { field: "description", title: "说明", formatter: function (v) { return imFormatText(v, 40); } },
            { field: "status", title: "状态", width: 90, formatter: imPiamomFormatQuotaStatus, escape: false },
            { field: "createdAt", title: "创建时间", width: 160 },
            {
                title: "操作",
                align: "center",
                width: 220,
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imPiamomReportConfigOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imPiamomReportConfigOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>编辑</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomReportConfigRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.length ? actions.join("") : "-";
                }
            }
        ]
    });
}
