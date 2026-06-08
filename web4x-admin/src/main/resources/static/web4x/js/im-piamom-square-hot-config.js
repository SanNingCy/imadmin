/**
 * 广场朋友圈 - 广场热门配置
 * - GET    /admin/piamom/hotConfig/list
 * - GET    /admin/piamom/hotConfig/queryById?id=
 * - POST   /admin/piamom/hotConfig/save|update
 * - DELETE /admin/piamom/hotConfig/delete?id=
 */

function imPiamomHotConfigResetModal() {
    $("#hot-config-id").val("");
    $("#hot-config-likeThreshold").val("");
    $("#hot-config-viewThreshold").val("");
    $("#hot-config-totalOdicStake").val("");
    $("#hot-config-stakeTip").val("");
    $("#hot-config-stakeRuleTitle").val("");
    $("#hot-config-stakeRuleContent").val("");
    $("#hot-config-auditRuleTitle").val("");
    $("#hot-config-auditRuleContent").val("");
    $("#hot-config-complaintNotice").val("");
    $("#hot-config-creditMin").val("");
    $("#hot-config-creditMax").val("");
    $("#hot-config-status").val("1");
}

function imPiamomHotConfigFillModal(info) {
    $("#hot-config-id").val(info.id || "");
    $("#hot-config-likeThreshold").val(info.likeThreshold != null ? info.likeThreshold : "");
    $("#hot-config-viewThreshold").val(info.viewThreshold != null ? info.viewThreshold : "");
    $("#hot-config-totalOdicStake").val(info.totalOdicStake != null ? info.totalOdicStake : "");
    $("#hot-config-stakeTip").val(info.stakeTip || "");
    $("#hot-config-stakeRuleTitle").val(info.stakeRuleTitle || "");
    $("#hot-config-stakeRuleContent").val(info.stakeRuleContent || "");
    $("#hot-config-auditRuleTitle").val(info.auditRuleTitle || "");
    $("#hot-config-auditRuleContent").val(info.auditRuleContent || "");
    $("#hot-config-complaintNotice").val(info.complaintNotice || "");
    $("#hot-config-creditMin").val(info.creditMin != null ? info.creditMin : "");
    $("#hot-config-creditMax").val(info.creditMax != null ? info.creditMax : "");
    $("#hot-config-status").val(info.status != null ? String(info.status) : "1");
}

function imPiamomHotConfigShowModal(mode, readOnly) {
    var titles = {
        add: "新增热门配置",
        edit: "编辑热门配置",
        view: "查看热门配置"
    };
    layer.open({
        type: 1,
        title: titles[mode] || "热门配置",
        area: ["760px", "620px"],
        shadeClose: true,
        content: $("#hot-config-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imPiamomHotConfigSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imPiamomHotConfigOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imPiamomHotConfigResetModal();
    imPiamomSetFormReadOnly($("#hot-config-modal-form"), readOnly);

    if (id) {
        $.ajax({
            url: imPiamomApi + "/hotConfig/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imPiamomHotConfigFillModal(imPiamomResolveData(res, "config") || {});
                    imPiamomHotConfigShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imPiamomHotConfigShowModal(mode, readOnly);
    }
}

function imPiamomHotConfigCollectPayload() {
    var likeThreshold = $("#hot-config-likeThreshold").val();
    var viewThreshold = $("#hot-config-viewThreshold").val();
    var totalOdicStake = $("#hot-config-totalOdicStake").val();
    var status = $("#hot-config-status").val();

    if (likeThreshold === "" || likeThreshold == null) return $.modal.alertWarning("请输入点赞阈值"), null;
    if (viewThreshold === "" || viewThreshold == null) return $.modal.alertWarning("请输入浏览阈值"), null;
    if (totalOdicStake === "" || totalOdicStake == null) return $.modal.alertWarning("请输入质押 ODIC 总额"), null;
    if (status === "" || status == null) return $.modal.alertWarning("请选择状态"), null;

    var payload = {
        likeThreshold: parseInt(likeThreshold, 10),
        viewThreshold: parseInt(viewThreshold, 10),
        totalOdicStake: parseFloat(totalOdicStake),
        stakeTip: $("#hot-config-stakeTip").val(),
        stakeRuleTitle: $("#hot-config-stakeRuleTitle").val(),
        stakeRuleContent: $("#hot-config-stakeRuleContent").val(),
        auditRuleTitle: $("#hot-config-auditRuleTitle").val(),
        auditRuleContent: $("#hot-config-auditRuleContent").val(),
        complaintNotice: $("#hot-config-complaintNotice").val(),
        status: parseInt(status, 10)
    };

    var creditMin = $("#hot-config-creditMin").val();
    var creditMax = $("#hot-config-creditMax").val();
    if (creditMin !== "" && creditMin != null) {
        payload.creditMin = parseInt(creditMin, 10);
    }
    if (creditMax !== "" && creditMax != null) {
        payload.creditMax = parseInt(creditMax, 10);
    }
    return payload;
}

function imPiamomHotConfigSave(layerIndex, mode) {
    var payload = imPiamomHotConfigCollectPayload();
    if (!payload) {
        return;
    }

    var url = imPiamomApi + "/hotConfig/save";
    if (mode === "edit") {
        url = imPiamomApi + "/hotConfig/update";
        payload.id = $("#hot-config-id").val();
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

function imPiamomHotConfigRemove(id) {
    $.modal.confirm("确定删除该配置吗？", function () {
        $.ajax({
            url: imPiamomApi + "/hotConfig/delete",
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

function imPiamomHotConfigInitTable(canView, canEdit, canDelete) {
    imInitTable({
        url: imPiamomApi + "/hotConfig/list",
        showSearch: false,
        pagination: false,
        sidePagination: "client",
        responseHandler: imPiamomListResponse,
        modalName: "热门配置",
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "likeThreshold", title: "点赞阈值", width: 100 },
            { field: "viewThreshold", title: "浏览阈值", width: 100 },
            { field: "totalOdicStake", title: "质押ODIC", width: 100 },
            { field: "creditMin", title: "信用分下限", width: 100 },
            { field: "creditMax", title: "信用分上限", width: 100, formatter: imPiamomFormatCreditMax },
            { field: "status", title: "状态", width: 90, formatter: imPiamomFormatQuotaStatus, escape: false },
            { field: "createdAt", title: "创建时间", width: 160 },
            {
                title: "操作",
                align: "center",
                width: 220,
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imPiamomHotConfigOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imPiamomHotConfigOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>编辑</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomHotConfigRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.length ? actions.join("") : "-";
                }
            }
        ]
    });
}
