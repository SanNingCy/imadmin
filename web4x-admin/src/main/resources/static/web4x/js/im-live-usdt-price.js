/**

 * 会议室 USDT 定价 - 复用时长/人数/固定价格表，支持手动输入新增时长与人数

 */

var imLiveUsdtPriceApi = imLiveApi + "/usdtPrice";

var imLiveUsdtPriceTierSeq = 0;

var imLiveUsdtDurationOptions = [];

var imLiveUsdtTierOptions = [];



function imLiveUsdtPriceQueryParams(params) {

    return imLiveQueryParams("live-usdt-price-form", params);

}



function imLiveUsdtPriceFillSelect($select, list, placeholder, includeEmpty) {

    if (!$select || !$select.length) {

        return;

    }

    var current = $select.val();

    $select.empty();

    if (includeEmpty !== false) {

        $select.append('<option value="">' + (placeholder || "请选择") + "</option>");

    }

    (list || []).forEach(function (item) {

        var label = item.name || item.durationName || item.tierName || item.id;

        var value = item.value != null ? item.value : "";

        $select.append('<option value="' + item.id + '" data-value="' + value + '">' + label + "</option>");

    });

    if (current) {

        $select.val(String(current));

    }

}



function imLiveUsdtPriceLoadOptions(callback) {

    $.when(

        imLiveAjax({ url: imLiveUsdtPriceApi + "/options/duration", type: "GET" }),

        imLiveAjax({ url: imLiveUsdtPriceApi + "/options/tier", type: "GET" })

    ).done(function (durationRes, tierRes) {

        imLiveUsdtDurationOptions = imLiveResolveList(durationRes[0], "list");

        imLiveUsdtTierOptions = imLiveResolveList(tierRes[0], "list");

        imLiveUsdtPriceFillSelect($("#live-usdt-price-search-duration"), imLiveUsdtDurationOptions, "请选择会议时长");

        imLiveUsdtPriceFillSelect($("#live-usdt-price-search-tier"), imLiveUsdtTierOptions, "请选择会议人数");

        imLiveUsdtPriceFillSelect($("#live-usdt-price-duration-pick"), imLiveUsdtDurationOptions, "快捷选择已有会议时长");

        imLiveUsdtPriceFillSelect($("#live-usdt-price-tier-pick"), imLiveUsdtTierOptions, "快捷选择已有人数档位");

        if (typeof callback === "function") {

            callback();

        }

    });

}



function imLiveUsdtPriceFindOption(list, id) {

    if (!id) {

        return null;

    }

    for (var i = 0; i < (list || []).length; i++) {

        if (String(list[i].id) === String(id)) {

            return list[i];

        }

    }

    return null;

}



function imLiveUsdtPriceGetDurationMinutes() {

    var minutes = Number($("#live-usdt-price-duration-minutes").val());

    if (Number.isFinite(minutes) && minutes > 0) {

        return minutes;

    }

    var pickId = $("#live-usdt-price-duration-pick").val();

    var opt = imLiveUsdtPriceFindOption(imLiveUsdtDurationOptions, pickId);

    return opt && opt.value != null ? Number(opt.value) : null;

}



function imLiveUsdtPriceGetPeopleCount($scope) {

    if ($scope && $scope.length) {

        var manual = Number($scope.find(".live-usdt-tier-people").val());

        if (Number.isFinite(manual) && manual > 0) {

            return manual;

        }

        var pickId = $scope.find(".live-usdt-tier-pick").val();

        var opt = imLiveUsdtPriceFindOption(imLiveUsdtTierOptions, pickId);

        return opt && opt.value != null ? Number(opt.value) : null;

    }

    var people = Number($("#live-usdt-price-people-count").val());

    if (Number.isFinite(people) && people > 0) {

        return people;

    }

    var tierPickId = $("#live-usdt-price-tier-pick").val();

    var tierOpt = imLiveUsdtPriceFindOption(imLiveUsdtTierOptions, tierPickId);

    return tierOpt && tierOpt.value != null ? Number(tierOpt.value) : null;

}



function imLiveUsdtPriceMarkSaleEdited($sale) {

    $sale.data("userEdited", true);

}



function imLiveUsdtPriceApplySaleDefault($sale, cost) {

    if ($sale.data("userEdited") === true) {

        return;

    }

    if (cost == null) {

        return;

    }

    // 时长/人数变化时始终同步为最新成本价（避免输入「20」时中间态「2」留下 0.16）

    $sale.val(cost);

}



function imLiveUsdtPriceResetSaleEdited($sale) {

    $sale.removeData("userEdited");

}



function imLiveUsdtPriceUpdateProfitDisplay($sale, cost, $profit, $lossTip) {

    var saleRaw = $.trim($sale.val());

    if (saleRaw === "" || cost == null) {

        $profit.text("-");

        $lossTip.hide();

        return;

    }

    var sale = Number(saleRaw);

    if (!Number.isFinite(sale)) {

        $profit.text("-");

        $lossTip.hide();

        return;

    }

    var profit = Math.round((sale - cost) * 10000) / 10000;

    $profit.text(profit + " USDT");

    if (sale < cost) {

        $profit.removeClass("live-usdt-profit-text").addClass("text-danger");

        $lossTip.show();

    } else {

        $profit.removeClass("text-danger").addClass("live-usdt-profit-text");

        $lossTip.hide();

    }

}



function imLiveUsdtPriceUpdateSingleCalc() {

    var minutes = imLiveUsdtPriceGetDurationMinutes();

    var people = imLiveUsdtPriceGetPeopleCount();

    var cost = imLiveCalcUsdtCost(minutes, people);

    var $sale = $("#live-usdt-price-sale");



    if (cost == null) {

        $("#live-usdt-price-cost-display").text("-");

        $("#live-usdt-price-profit-display").text("-");

        $("#live-usdt-price-loss-tip").hide();

        return;

    }



    $("#live-usdt-price-cost-display").text(cost + " USDT");

    imLiveUsdtPriceApplySaleDefault($sale, cost);

    imLiveUsdtPriceUpdateProfitDisplay($sale, cost, $("#live-usdt-price-profit-display"), $("#live-usdt-price-loss-tip"));

}



function imLiveUsdtPriceBuildTierPickHtml() {

    var html = '<option value="">快捷选择已有人数档位</option>';

    (imLiveUsdtTierOptions || []).forEach(function (item) {

        var label = item.name || item.tierName || item.id;

        var value = item.value != null ? item.value : "";

        html += '<option value="' + item.id + '" data-value="' + value + '">' + label + "</option>";

    });

    return html;

}



function imLiveUsdtPriceTierRowHtml(seq) {

    return ''

        + '<div class="live-usdt-tier-row clearfix" data-tier-seq="' + seq + '">'

        + '  <div class="form-group">'

        + '    <label class="col-sm-3 control-label"><span class="text-danger">*</span> 会议人数：</label>'

        + '    <div class="col-sm-8">'

        + '      <input type="number" class="form-control live-usdt-tier-people live-usdt-calc-trigger" min="1" step="1" placeholder="输入人数"/>'

        + '      <select class="form-control live-usdt-tier-pick live-usdt-calc-trigger live-usdt-pick-select" style="margin-top:6px;">' + imLiveUsdtPriceBuildTierPickHtml() + '</select>'

        + '    </div>'

        + '  </div>'

        + '  <div class="form-group">'

        + '    <label class="col-sm-3 control-label">成本价(USDT)：</label>'

        + '    <div class="col-sm-8"><p class="form-control-static live-usdt-cost-text live-usdt-tier-cost">-</p></div>'

        + '  </div>'

        + '  <div class="form-group">'

        + '    <label class="col-sm-3 control-label"><span class="text-danger">*</span> 会议价格(USDT)：</label>'

        + '    <div class="col-sm-8">'

        + '      <input type="text" class="form-control live-usdt-tier-sale live-usdt-sale-input live-usdt-calc-trigger" inputmode="decimal" placeholder="默认等于成本价，可手动修改"/>'

        + '      <div class="live-usdt-loss-tip live-usdt-tier-loss">该价格为亏损价格</div>'

        + '    </div>'

        + '  </div>'

        + '  <div class="form-group" style="margin-bottom:6px;">'

        + '    <label class="col-sm-3 control-label">利润(USDT)：</label>'

        + '    <div class="col-sm-8"><p class="form-control-static live-usdt-profit-text live-usdt-tier-profit">0</p></div>'

        + '  </div>'

        + '  <div class="form-group" style="margin-bottom:0;">'

        + '    <div class="col-sm-offset-3 col-sm-8">'

        + '      <button type="button" class="btn btn-danger btn-xs live-usdt-tier-remove" onclick="imLiveUsdtPriceRemoveTierRow(' + seq + ')"><i class="fa fa-remove"></i> 删除此人数</button>'

        + '    </div>'

        + '  </div>'

        + '</div>';

}



function imLiveUsdtPriceUpdateTierRowCalc($row) {

    var minutes = imLiveUsdtPriceGetDurationMinutes();

    var people = imLiveUsdtPriceGetPeopleCount($row);

    var cost = imLiveCalcUsdtCost(minutes, people);

    var $sale = $row.find(".live-usdt-tier-sale");



    if (cost == null) {

        $row.find(".live-usdt-tier-cost").text("-");

        $row.find(".live-usdt-tier-profit").text("-");

        $row.find(".live-usdt-tier-loss").hide();

        return;

    }



    $row.find(".live-usdt-tier-cost").text(cost + " USDT");

    imLiveUsdtPriceApplySaleDefault($sale, cost);

    imLiveUsdtPriceUpdateProfitDisplay($sale, cost, $row.find(".live-usdt-tier-profit"), $row.find(".live-usdt-tier-loss"));

}



function imLiveUsdtPriceRefreshAllCalc() {

    var mode = $("#live-usdt-price-mode").val();

    if (mode === "edit") {

        imLiveUsdtPriceUpdateSingleCalc();

        return;

    }

    $("#live-usdt-price-tier-list .live-usdt-tier-row").each(function () {

        imLiveUsdtPriceUpdateTierRowCalc($(this));

    });

}



function imLiveUsdtPriceAddTierRow() {

    imLiveUsdtPriceTierSeq += 1;

    $("#live-usdt-price-tier-list").append(imLiveUsdtPriceTierRowHtml(imLiveUsdtPriceTierSeq));

    imLiveUsdtPriceRefreshAllCalc();

}



function imLiveUsdtPriceRemoveTierRow(seq) {

    $("#live-usdt-price-tier-list .live-usdt-tier-row[data-tier-seq='" + seq + "']").remove();

    if (!$("#live-usdt-price-tier-list .live-usdt-tier-row").length) {

        imLiveUsdtPriceAddTierRow();

    }

}



function imLiveUsdtPriceResetModal() {

    $("#live-usdt-price-id").val("");

    $("#live-usdt-price-mode").val("add");

    $("#live-usdt-price-duration-minutes").val("");

    $("#live-usdt-price-duration-pick").val("");

    $("#live-usdt-price-people-count").val("");

    $("#live-usdt-price-tier-pick").val("");

    $("#live-usdt-price-sale").val("");

    imLiveUsdtPriceResetSaleEdited($("#live-usdt-price-sale"));

    $("#live-usdt-price-cost-display").text("-");

    $("#live-usdt-price-profit-display").text("0");

    $("#live-usdt-price-loss-tip").hide();

    $("#live-usdt-price-status").val("1");

    $("#live-usdt-price-remark").val("");

    $("#live-usdt-price-tier-list").empty();

    $("#live-usdt-price-tier-panel").show();

    $("#live-usdt-price-single-panel").hide();

    $("#live-usdt-price-add-tier").show();

    imLiveUsdtPriceAddTierRow();

}



function imLiveUsdtPriceFillModal(info) {

    $("#live-usdt-price-id").val(info.id || "");

    $("#live-usdt-price-mode").val("edit");

    $("#live-usdt-price-duration-minutes").val(info.durationMinutes != null ? info.durationMinutes : "");

    $("#live-usdt-price-duration-pick").val(info.durationId != null ? String(info.durationId) : "");

    $("#live-usdt-price-people-count").val(info.peopleCount != null ? info.peopleCount : "");

    $("#live-usdt-price-tier-pick").val(info.tierId != null ? String(info.tierId) : "");

    var $sale = $("#live-usdt-price-sale");

    $sale.val(info.salePriceUsdt != null ? info.salePriceUsdt : "");

    imLiveUsdtPriceMarkSaleEdited($sale);

    $("#live-usdt-price-status").val(info.status != null ? String(info.status) : "1");

    $("#live-usdt-price-remark").val(info.remark || "");

    $("#live-usdt-price-tier-panel").hide();

    $("#live-usdt-price-single-panel").show();

    imLiveUsdtPriceUpdateSingleCalc();

}



function imLiveUsdtPriceShowModal(mode, readOnly) {

    var titles = {

        add: "新建 USDT 定价",

        edit: "编辑 USDT 定价",

        view: "查看 USDT 定价"

    };

    layer.open({

        type: 1,

        title: titles[mode] || "USDT 定价",

        area: ["680px", "640px"],

        shadeClose: true,

        content: $("#live-usdt-price-modal"),

        btn: readOnly ? ["关闭"] : ["保存", "取消"],

        yes: function (index) {

            if (readOnly) {

                layer.close(index);

                return;

            }

            imLiveUsdtPriceSave(index, mode);

        },

        btn2: function (index) {

            layer.close(index);

        }

    });

}



function imLiveUsdtPriceOpenModal(mode, id) {

    var readOnly = String(mode) === "view";

    imLiveUsdtPriceLoadOptions(function () {

        imLiveUsdtPriceResetModal();

        imLiveSetFormReadOnly($("#live-usdt-price-modal-form"), readOnly);

        if (readOnly) {

            $("#live-usdt-price-add-tier").hide();

            $(".live-usdt-tier-remove").hide();

        }



        if (id) {

            imLiveAjax({

                url: imLiveUsdtPriceApi + "/queryById",

                type: "GET",

                data: { id: id },

                success: function (res) {

                    if (res && (res.success === true || res.code === 200)) {

                        imLiveUsdtPriceFillModal(imLiveResolveEntity(res, "config"));

                        imLiveUsdtPriceShowModal(mode, readOnly);

                    } else {

                        $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");

                    }

                },

                error: function () {

                    $.modal.alertWarning("获取详情失败");

                }

            });

        } else {

            imLiveUsdtPriceShowModal(mode, readOnly);

        }

    });

}



function imLiveUsdtPriceParseSale(val) {

    var num = Number($.trim(val));

    return Number.isFinite(num) && num > 0 ? num : null;

}



function imLiveUsdtPriceCollectTiers() {

    var tiers = [];

    var peopleCounts = {};

    var valid = true;

    $("#live-usdt-price-tier-list .live-usdt-tier-row").each(function () {

        var $row = $(this);

        var people = imLiveUsdtPriceGetPeopleCount($row);

        var sale = imLiveUsdtPriceParseSale($row.find(".live-usdt-tier-sale").val());

        if (!people) {

            valid = false;

            return false;

        }

        if (peopleCounts[people]) {

            $.modal.alertWarning("不能重复配置相同人数");

            valid = false;

            return false;

        }

        peopleCounts[people] = true;

        if (!sale) {

            valid = false;

            return false;

        }

        tiers.push({

            peopleCount: people,

            salePriceUsdt: sale

        });

    });

    if (!valid) {

        $.modal.alertWarning("请完整填写人数与会议价格");

        return null;

    }

    if (!tiers.length) {

        $.modal.alertWarning("请至少添加一条人数价格配置");

        return null;

    }

    return tiers;

}



function imLiveUsdtPriceSave(layerIndex, mode) {

    var durationMinutes = imLiveUsdtPriceGetDurationMinutes();

    var status = $("#live-usdt-price-status").val();

    var remark = $.trim($("#live-usdt-price-remark").val());



    if (!durationMinutes) {

        return $.modal.alertWarning("请输入有效的会议时长");

    }

    if (status === "" || status == null) {

        return $.modal.alertWarning("请选择状态");

    }



    if (mode === "edit") {

        var peopleCount = imLiveUsdtPriceGetPeopleCount();

        var salePriceUsdt = imLiveUsdtPriceParseSale($("#live-usdt-price-sale").val());

        if (!peopleCount) {

            return $.modal.alertWarning("请输入有效的会议人数");

        }

        if (!salePriceUsdt) {

            return $.modal.alertWarning("请输入有效的会议价格");

        }

        imLiveAjax({

            url: imLiveUsdtPriceApi + "/update",

            type: "POST",

            contentType: "application/json;charset=UTF-8",

            data: JSON.stringify({

                id: $("#live-usdt-price-id").val(),

                durationMinutes: durationMinutes,

                peopleCount: peopleCount,

                salePriceUsdt: salePriceUsdt,

                status: parseInt(status, 10),

                remark: remark || undefined

            }),

            success: function (res) {

                if (res && (res.success === true || res.code === 200)) {

                    $.modal.msgSuccess(res.msg || "保存成功");

                    layer.close(layerIndex);

                    $("#bootstrap-table").bootstrapTable("refresh");

                    imLiveUsdtPriceLoadOptions();

                } else {

                    $.modal.alertWarning((res && res.msg) ? res.msg : "保存失败");

                }

            },

            error: function () {

                $.modal.alertWarning("保存失败");

            }

        });

        return;

    }



    var tiers = imLiveUsdtPriceCollectTiers();

    if (!tiers) {

        return;

    }

    imLiveAjax({

        url: imLiveUsdtPriceApi + "/batchSave",

        type: "POST",

        contentType: "application/json;charset=UTF-8",

        data: JSON.stringify({

            durationMinutes: durationMinutes,

            status: parseInt(status, 10),

            remark: remark || undefined,

            tiers: tiers

        }),

        success: function (res) {

            if (res && (res.success === true || res.code === 200)) {

                $.modal.msgSuccess(res.msg || "保存成功");

                layer.close(layerIndex);

                $("#bootstrap-table").bootstrapTable("refresh");

                imLiveUsdtPriceLoadOptions();

            } else {

                $.modal.alertWarning((res && res.msg) ? res.msg : "保存失败");

            }

        },

        error: function () {

            $.modal.alertWarning("保存失败");

        }

    });

}



function imLiveUsdtPriceInitTable() {

    imLiveFillStatusSelect($("#live-usdt-price-search-status"), true, "请选择状态");

    imLiveFillStatusSelect($("#live-usdt-price-status"), false);

    imLiveUsdtPriceLoadOptions();



    $(document).on("input change", ".live-usdt-calc-trigger", function () {

        imLiveUsdtPriceRefreshAllCalc();

    });



    $(document).on("change", "#live-usdt-price-duration-pick", function () {

        var opt = imLiveUsdtPriceFindOption(imLiveUsdtDurationOptions, $(this).val());

        if (opt && opt.value != null) {

            $("#live-usdt-price-duration-minutes").val(opt.value);

        }

        imLiveUsdtPriceRefreshAllCalc();

    });



    $(document).on("change", "#live-usdt-price-tier-pick", function () {

        var opt = imLiveUsdtPriceFindOption(imLiveUsdtTierOptions, $(this).val());

        if (opt && opt.value != null) {

            $("#live-usdt-price-people-count").val(opt.value);

        }

        imLiveUsdtPriceRefreshAllCalc();

    });



    $(document).on("change", ".live-usdt-tier-pick", function () {

        var $row = $(this).closest(".live-usdt-tier-row");

        var opt = imLiveUsdtPriceFindOption(imLiveUsdtTierOptions, $(this).val());

        if (opt && opt.value != null) {

            $row.find(".live-usdt-tier-people").val(opt.value);

        }

        imLiveUsdtPriceUpdateTierRowCalc($row);

    });



    $(document).on("input", ".live-usdt-sale-input", function () {

        imLiveUsdtPriceMarkSaleEdited($(this));

        imLiveUsdtPriceRefreshAllCalc();

    });



    imInitTable({

        url: imLiveUsdtPriceApi + "/page",

        formId: "live-usdt-price-form",

        queryParams: imLiveUsdtPriceQueryParams,

        sortName: "createTime",

        sortOrder: "desc",

        modalName: "USDT定价",

        escape: false,

        columns: [

            { field: "durationName", title: "时长名称", sortable: true },

            { field: "durationMinutes", title: "会议时长(分)", sortable: true },

            { field: "peopleCount", title: "会议人数", sortable: true },

            {

                field: "costPriceUsdt",

                title: "成本价",

                sortable: true,

                formatter: imLiveFormatUsdt

            },

            {

                field: "salePriceUsdt",

                title: "会议价格",

                sortable: true,

                formatter: imLiveFormatUsdt

            },

            {

                field: "profitUsdt",

                title: "利润",

                sortable: true,

                formatter: function (val, row) {

                    if (val == null || val === "") {

                        return "-";

                    }

                    var num = Number(val);

                    if (!Number.isFinite(num)) {

                        return "-";

                    }

                    var cls = num < 0 ? "text-danger" : "live-usdt-profit-text";

                    return '<span class="' + cls + '">' + imLiveFormatUsdt(val) + "</span>";

                }

            },

            { field: "status", title: "状态", sortable: true, formatter: imLiveFormatStatus },

            { field: "createTime", title: "创建时间", sortable: true },

            {

                title: "操作",

                align: "center",

                formatter: function (value, row) {

                    var id = row.id;

                    return [

                        '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imLiveUsdtPriceOpenModal(\'view\',\'' + id + '\')"><i class="fa fa-search"></i>查看</a> ',

                        '<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imLiveUsdtPriceOpenModal(\'edit\',\'' + id + '\')"><i class="fa fa-edit"></i>修改</a> ',

                        '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imLiveDelete(imLiveUsdtPriceApi,\'' + id + '\',\'确定删除该 USDT 定价吗？\')"><i class="fa fa-remove"></i>删除</a>'

                    ].join("");

                }

            }

        ]

    });

}

