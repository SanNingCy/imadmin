/**
 * 会议室价格配置 - /admin/live/fixedPrice/*
 */
var imLiveFixedPriceApi = imLiveApi + "/fixedPrice";

function imLiveFixedPriceQueryParams(params) {
    return imLiveQueryParams("live-fixed-price-form", params);
}

function imLiveFixedPriceFillSelect($select, list, placeholder) {
    if (!$select || !$select.length) {
        return;
    }
    var current = $select.val();
    $select.empty();
    $select.append('<option value="">' + (placeholder || "请选择") + "</option>");
    (list || []).forEach(function (item) {
        var id = item.id;
        var label = item.name || item.durationName || item.tierName || id;
        $select.append('<option value="' + id + '">' + label + "</option>");
    });
    if (current) {
        $select.val(String(current));
    }
}

function imLiveFixedPriceLoadOptions(callback) {
    $.when(
        imLiveAjax({ url: imLiveFixedPriceApi + "/options/duration", type: "GET" }),
        imLiveAjax({ url: imLiveFixedPriceApi + "/options/tier", type: "GET" })
    ).done(function (durationRes, tierRes) {
        var durationList = imLiveResolveList(durationRes[0], "list");
        var tierList = imLiveResolveList(tierRes[0], "list");
        imLiveFixedPriceFillSelect(
            $("#live-fixed-price-search-duration"),
            durationList,
            "请选择会议时长"
        );
        imLiveFixedPriceFillSelect(
            $("#live-fixed-price-search-tier"),
            tierList,
            "请选择会议室人数"
        );
        imLiveFixedPriceFillSelect(
            $("#live-fixed-price-duration"),
            durationList,
            "请选择会议室时长"
        );
        imLiveFixedPriceFillSelect(
            $("#live-fixed-price-tier"),
            tierList,
            "请选择会议室人数"
        );
        if (typeof callback === "function") {
            callback();
        }
    });
}

function imLiveFixedPriceResetModal() {
    $("#live-fixed-price-id").val("");
    $("#live-fixed-price-duration").val("");
    $("#live-fixed-price-tier").val("");
    $("#live-fixed-price-value").val("");
    $("#live-fixed-price-status").val("1");
    $("#live-fixed-price-remark").val("");
}

function imLiveFixedPriceFillModal(info) {
    $("#live-fixed-price-id").val(info.id || "");
    $("#live-fixed-price-duration").val(info.durationId != null ? String(info.durationId) : "");
    $("#live-fixed-price-tier").val(info.tierId != null ? String(info.tierId) : "");
    $("#live-fixed-price-value").val(info.fixedPrice != null ? info.fixedPrice : "");
    $("#live-fixed-price-status").val(info.status != null ? String(info.status) : "1");
    $("#live-fixed-price-remark").val(info.remark || "");
}

function imLiveFixedPriceShowModal(mode, readOnly) {
    var titles = {
        add: "新建配置价格",
        edit: "修改配置价格",
        view: "查看配置价格"
    };
    layer.open({
        type: 1,
        title: titles[mode] || "配置价格",
        area: ["520px", "460px"],
        shadeClose: true,
        content: $("#live-fixed-price-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imLiveFixedPriceSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imLiveFixedPriceOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imLiveFixedPriceResetModal();
    imLiveSetFormReadOnly($("#live-fixed-price-modal-form"), readOnly);

    var open = function () {
        if (id) {
            imLiveAjax({
                url: imLiveFixedPriceApi + "/queryById",
                type: "GET",
                data: { id: id },
                success: function (res) {
                    if (res && (res.success === true || res.code === 200)) {
                        imLiveFixedPriceFillModal(imLiveResolveEntity(res, "config"));
                        imLiveFixedPriceShowModal(mode, readOnly);
                    } else {
                        $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                    }
                },
                error: function () {
                    $.modal.alertWarning("获取详情失败");
                }
            });
        } else {
            imLiveFixedPriceShowModal(mode, readOnly);
        }
    };

    imLiveFixedPriceLoadOptions(open);
}

function imLiveFixedPriceCollectPayload() {
    var durationId = $("#live-fixed-price-duration").val();
    var tierId = $("#live-fixed-price-tier").val();
    var fixedPrice = $.trim($("#live-fixed-price-value").val());
    var status = $("#live-fixed-price-status").val();

    if (!durationId) {
        return $.modal.alertWarning("请选择会议室时长"), null;
    }
    if (!tierId) {
        return $.modal.alertWarning("请选择会议室人数"), null;
    }
    if (!fixedPrice) {
        return $.modal.alertWarning("请输入固定配置价格"), null;
    }
    if (status === "" || status == null) {
        return $.modal.alertWarning("请选择状态"), null;
    }

    return {
        durationId: parseInt(durationId, 10),
        tierId: parseInt(tierId, 10),
        fixedPrice: fixedPrice,
        status: parseInt(status, 10),
        remark: $.trim($("#live-fixed-price-remark").val()) || undefined
    };
}

function imLiveFixedPriceSave(layerIndex, mode) {
    var payload = imLiveFixedPriceCollectPayload();
    if (!payload) {
        return;
    }
    var url = imLiveFixedPriceApi + "/save";
    if (mode === "edit") {
        url = imLiveFixedPriceApi + "/update";
        payload.id = $("#live-fixed-price-id").val();
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

function imLiveFixedPriceInitTable() {
    imLiveFillStatusSelect($("#live-fixed-price-search-status"), true, "请选择状态");
    imLiveFillStatusSelect($("#live-fixed-price-status"), false);
    imLiveFixedPriceLoadOptions();

    imInitTable({
        url: imLiveFixedPriceApi + "/page",
        formId: "live-fixed-price-form",
        queryParams: imLiveFixedPriceQueryParams,
        sortName: "createTime",
        sortOrder: "desc",
        modalName: "价格配置",
        columns: [
            { field: "durationName", title: "会议时长", sortable: true },
            { field: "durationId", title: "会议室时长ID", sortable: true },
            { field: "tierName", title: "会议室人数", sortable: true },
            { field: "tierId", title: "会议室人数ID", sortable: true },
            {
                field: "fixedPrice",
                title: "固定配置价格",
                sortable: true,
                formatter: imLiveFormatAmount
            },
            {
                field: "status",
                title: "会议室状态",
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
                        '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imLiveFixedPriceOpenModal(\'view\',\'' + id + '\')"><i class="fa fa-search"></i>查看</a> ',
                        '<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imLiveFixedPriceOpenModal(\'edit\',\'' + id + '\')"><i class="fa fa-edit"></i>修改</a> ',
                        '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imLiveDelete(imLiveFixedPriceApi,\'' + id + '\',\'确定删除该价格配置吗？\')"><i class="fa fa-remove"></i>删除</a>'
                    ].join("");
                }
            }
        ]
    });
}
