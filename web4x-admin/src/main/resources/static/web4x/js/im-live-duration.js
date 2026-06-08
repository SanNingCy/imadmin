/**
 * 会议室时长配置 - /admin/live/duration/*
 */
var imLiveDurationApi = imLiveApi + "/duration";

function imLiveDurationQueryParams(params) {
    return imLiveQueryParams("live-duration-form", params);
}

function imLiveDurationResetModal() {
    $("#live-duration-id").val("");
    $("#live-duration-name").val("");
    $("#live-duration-value").val("");
    $("#live-duration-status").val("1");
    $("#live-duration-remark").val("");
}

function imLiveDurationFillModal(info) {
    $("#live-duration-id").val(info.id || "");
    $("#live-duration-name").val(info.durationName || "");
    $("#live-duration-value").val(info.durationValue != null ? info.durationValue : "");
    $("#live-duration-status").val(info.status != null ? String(info.status) : "1");
    $("#live-duration-remark").val(info.remark || "");
}

function imLiveDurationShowModal(mode, readOnly) {
    var titles = {
        add: "新建时长配置",
        edit: "修改时长配置",
        view: "查看时长配置"
    };
    layer.open({
        type: 1,
        title: titles[mode] || "时长配置",
        area: ["520px", "420px"],
        shadeClose: true,
        content: $("#live-duration-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imLiveDurationSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imLiveDurationOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imLiveDurationResetModal();
    imLiveSetFormReadOnly($("#live-duration-modal-form"), readOnly);

    if (id) {
        imLiveAjax({
            url: imLiveDurationApi + "/queryById",
            type: "GET",
            data: { id: id },
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imLiveDurationFillModal(imLiveResolveEntity(res, "config"));
                    imLiveDurationShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imLiveDurationShowModal(mode, readOnly);
    }
}

function imLiveDurationCollectPayload() {
    var name = $.trim($("#live-duration-name").val());
    var value = $("#live-duration-value").val();
    var status = $("#live-duration-status").val();
    if (!name) {
        return $.modal.alertWarning("请输入时长名称"), null;
    }
    if (value === "" || value == null) {
        return $.modal.alertWarning("请输入时长值"), null;
    }
    if (status === "" || status == null) {
        return $.modal.alertWarning("请选择状态"), null;
    }
    return {
        durationName: name,
        durationValue: parseInt(value, 10),
        status: parseInt(status, 10),
        remark: $.trim($("#live-duration-remark").val()) || undefined
    };
}

function imLiveDurationSave(layerIndex, mode) {
    var payload = imLiveDurationCollectPayload();
    if (!payload) {
        return;
    }
    var url = imLiveDurationApi + "/save";
    if (mode === "edit") {
        url = imLiveDurationApi + "/update";
        payload.id = $("#live-duration-id").val();
    }
    imLiveAjax({
        url: url,
        type: "POST",
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

function imLiveDurationInitTable() {
    imLiveFillStatusSelect($("#live-duration-search-status"), true, "请选择状态");
    imLiveFillStatusSelect($("#live-duration-status"), false);

    imInitTable({
        url: imLiveDurationApi + "/page",
        formId: "live-duration-form",
        queryParams: imLiveDurationQueryParams,
        sortName: "durationSort",
        sortOrder: "asc",
        modalName: "时长配置",
        columns: [
            { field: "durationName", title: "时长名称", sortable: true },
            { field: "durationValue", title: "时长值(分钟)", sortable: true },
            { field: "durationSort", title: "排序", sortable: true },
            {
                field: "status",
                title: "状态",
                sortable: true,
                formatter: imLiveFormatStatus
            },
            { field: "createTime", title: "创建时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var id = row.id;
                    return [
                        '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imLiveDurationOpenModal(\'view\',\'' + id + '\')"><i class="fa fa-search"></i>查看</a> ',
                        '<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imLiveDurationOpenModal(\'edit\',\'' + id + '\')"><i class="fa fa-edit"></i>修改</a> ',
                        '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imLiveDelete(imLiveDurationApi,\'' + id + '\',\'确定删除该时长配置吗？\')"><i class="fa fa-remove"></i>删除</a>'
                    ].join("");
                }
            }
        ]
    });
}
