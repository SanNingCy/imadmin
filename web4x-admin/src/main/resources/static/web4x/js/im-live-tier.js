/**
 * 会议室人员配置 - /admin/live/tier/*
 */
var imLiveTierApi = imLiveApi + "/tier";

function imLiveTierQueryParams(params) {
    return imLiveQueryParams("live-tier-form", params);
}

function imLiveTierResetModal() {
    $("#live-tier-id").val("");
    $("#live-tier-name").val("");
    $("#live-tier-value").val("");
    $("#live-tier-status").val("1");
}

function imLiveTierFillModal(info) {
    $("#live-tier-id").val(info.id || "");
    $("#live-tier-name").val(info.tierName || "");
    $("#live-tier-value").val(info.tierValue != null ? info.tierValue : "");
    $("#live-tier-status").val(info.status != null ? String(info.status) : "1");
}

function imLiveTierShowModal(mode, readOnly) {
    var titles = {
        add: "新建人数档位",
        edit: "修改人数档位",
        view: "查看人数档位"
    };
    layer.open({
        type: 1,
        title: titles[mode] || "人数档位",
        area: ["520px", "340px"],
        shadeClose: true,
        content: $("#live-tier-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imLiveTierSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imLiveTierOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imLiveTierResetModal();
    imLiveSetFormReadOnly($("#live-tier-modal-form"), readOnly);

    if (id) {
        imLiveAjax({
            url: imLiveTierApi + "/queryById",
            type: "GET",
            data: { id: id },
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imLiveTierFillModal(imLiveResolveEntity(res, "config"));
                    imLiveTierShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imLiveTierShowModal(mode, readOnly);
    }
}

function imLiveTierCollectPayload() {
    var name = $.trim($("#live-tier-name").val());
    var value = $("#live-tier-value").val();
    var status = $("#live-tier-status").val();
    if (!name) {
        return $.modal.alertWarning("请输入人数档位名称"), null;
    }
    if (value === "" || value == null) {
        return $.modal.alertWarning("请输入人数上限"), null;
    }
    if (status === "" || status == null) {
        return $.modal.alertWarning("请选择状态"), null;
    }
    return {
        tierName: name,
        tierValue: parseInt(value, 10),
        status: parseInt(status, 10)
    };
}

function imLiveTierSave(layerIndex, mode) {
    var payload = imLiveTierCollectPayload();
    if (!payload) {
        return;
    }
    var url = imLiveTierApi + "/save";
    if (mode === "edit") {
        url = imLiveTierApi + "/update";
        payload.id = $("#live-tier-id").val();
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

function imLiveTierInitTable() {
    imLiveFillStatusSelect($("#live-tier-search-status"), true, "请选择状态");
    imLiveFillStatusSelect($("#live-tier-status"), false);

    imInitTable({
        url: imLiveTierApi + "/page",
        formId: "live-tier-form",
        queryParams: imLiveTierQueryParams,
        sortName: "tierSort",
        sortOrder: "asc",
        modalName: "人数档位",
        columns: [
            { field: "tierName", title: "人数档位名称", sortable: true },
            { field: "tierValue", title: "人数上限", sortable: true },
            {
                field: "status",
                title: "状态",
                sortable: true,
                formatter: imLiveFormatStatus
            },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var id = row.id;
                    return [
                        '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imLiveTierOpenModal(\'view\',\'' + id + '\')"><i class="fa fa-search"></i>查看</a> ',
                        '<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imLiveTierOpenModal(\'edit\',\'' + id + '\')"><i class="fa fa-edit"></i>修改</a> ',
                        '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imLiveDelete(imLiveTierApi,\'' + id + '\',\'确定删除该人数档位吗？\')"><i class="fa fa-remove"></i>删除</a>'
                    ].join("");
                }
            }
        ]
    });
}
