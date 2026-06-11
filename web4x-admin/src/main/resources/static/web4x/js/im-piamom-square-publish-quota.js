/**
 * 广场朋友圈 - 广场发帖信用分额度
 * - GET    /admin/piamom/squarePublishQuota/list
 * - GET    /admin/piamom/squarePublishQuota/queryById?id=
 * - POST   /admin/piamom/squarePublishQuota/save|update
 * - DELETE /admin/piamom/squarePublishQuota/delete?id=
 */

function imPiamomPublishQuotaResetModal() {
    $("#publish-quota-id").val("");
    $("#publish-quota-creditMin").val("");
    $("#publish-quota-creditMax").val("");
    $("#publish-quota-dailyLimit").val("");
    $("#publish-quota-sortOrder").val("");
    $("#publish-quota-status").val("1");
}

function imPiamomPublishQuotaFillModal(info) {
    $("#publish-quota-id").val(info.id || "");
    $("#publish-quota-creditMin").val(info.creditMin != null ? info.creditMin : "");
    $("#publish-quota-creditMax").val(info.creditMax != null ? info.creditMax : "");
    $("#publish-quota-dailyLimit").val(info.dailyLimit != null ? info.dailyLimit : "");
    $("#publish-quota-sortOrder").val(info.sortOrder != null ? info.sortOrder : "");
    $("#publish-quota-status").val(info.status != null ? String(info.status) : "1");
}

function imPiamomPublishQuotaShowModal(mode, readOnly) {
    var titles = {
        add: "新增发帖额度",
        edit: "编辑发帖额度",
        view: "查看发帖额度"
    };
    layer.open({
        type: 1,
        title: titles[mode] || "发帖额度",
        area: ["520px", "420px"],
        shadeClose: true,
        content: $("#publish-quota-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imPiamomPublishQuotaSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imPiamomPublishQuotaOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imPiamomPublishQuotaResetModal();
    imPiamomSetFormReadOnly($("#publish-quota-modal-form"), readOnly);

    if (id) {
        $.ajax({
            url: imPiamomApi + "/squarePublishQuota/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imPiamomPublishQuotaFillModal(imPiamomResolveData(res, "quota") || {});
                    imPiamomPublishQuotaShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imPiamomPublishQuotaShowModal(mode, readOnly);
    }
}

function imPiamomPublishQuotaCollectPayload() {
    var creditMin = $("#publish-quota-creditMin").val();
    var dailyLimit = $("#publish-quota-dailyLimit").val();
    var sortOrder = $("#publish-quota-sortOrder").val();
    var status = $("#publish-quota-status").val();

    if (creditMin === "" || creditMin == null) return $.modal.alertWarning("请输入信用分下限"), null;
    if (dailyLimit === "" || dailyLimit == null) return $.modal.alertWarning("请输入每日可发帖数"), null;
    if (sortOrder === "" || sortOrder == null) return $.modal.alertWarning("请输入排序权重"), null;
    if (status === "" || status == null) return $.modal.alertWarning("请选择状态"), null;

    var payload = {
        creditMin: parseInt(creditMin, 10),
        dailyLimit: parseInt(dailyLimit, 10),
        sortOrder: parseInt(sortOrder, 10),
        status: parseInt(status, 10)
    };

    var creditMax = $("#publish-quota-creditMax").val();
    if (creditMax !== "" && creditMax != null) {
        payload.creditMax = parseInt(creditMax, 10);
    }
    return payload;
}

function imPiamomPublishQuotaSave(layerIndex, mode) {
    var payload = imPiamomPublishQuotaCollectPayload();
    if (!payload) {
        return;
    }

    var url = imPiamomApi + "/squarePublishQuota/save";
    if (mode === "edit") {
        url = imPiamomApi + "/squarePublishQuota/update";
        payload.id = $("#publish-quota-id").val();
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

function imPiamomPublishQuotaRemove(id) {
    $.modal.confirm("确定删除该额度档位吗？", function () {
        $.ajax({
            url: imPiamomApi + "/squarePublishQuota/delete",
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

function imPiamomPublishQuotaInitTable(canView, canEdit, canDelete) {
    imPiamomInitTable({
        url: imPiamomApi + "/squarePublishQuota/list",
        showSearch: false,
        pagination: false,
        sidePagination: "client",
        responseHandler: imPiamomListResponse,
        modalName: "发帖额度",
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "creditMin", title: "信用分下限", width: 110 },
            { field: "creditMax", title: "信用分上限", width: 110, formatter: imPiamomFormatCreditMax },
            { field: "dailyLimit", title: "每日额度", width: 100 },
            { field: "sortOrder", title: "排序", width: 80 },
            { field: "status", title: "状态", width: 90, formatter: imPiamomFormatQuotaStatus, escape: false },
            { field: "createdAt", title: "创建时间", width: 160 },
            {
                title: "操作",
                align: "center",
                width: 220,
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imPiamomPublishQuotaOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imPiamomPublishQuotaOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>编辑</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomPublishQuotaRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.length ? actions.join("") : "-";
                }
            }
        ]
    });
}
