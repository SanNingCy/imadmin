/**
 * IM 信用分明细记录
 * - GET /admin/creditScore/log/pageByUser
 * - GET /admin/creditScore/log/queryById/{id}
 */

var imCreditLogApi = ctx + "admin/creditScore/log";

function imCreditLogQueryParams(params) {
    return imCreditQueryParams("credit-log-form", params);
}

function imCreditLogResponseHandler(res) {
    var parsed = res;
    if (typeof res === "string") {
        try {
            parsed = JSON.parse(res);
        } catch (e) {
            parsed = null;
        }
    }
    if (parsed) {
        imCreditLogUpdateSummary(imCreditResolveEntity(parsed, "summary"));
    } else {
        imCreditLogUpdateSummary(null);
    }
    return imPageResponse(res);
}

function imCreditLogUpdateSummary(summary) {
    var $box = $("#credit-log-summary");
    if (!summary || (!summary.idno && !summary.lianghao && summary.totalCreditScore == null)) {
        $box.hide();
        return;
    }
    $("#credit-log-summary-idno").text(summary.idno || "-");
    $("#credit-log-summary-lianghao").text(summary.lianghao || "-");
    $("#credit-log-summary-total").text(imCreditFormatScore(summary.totalCreditScore));
    $box.show();
}

function imCreditLogResetSearch() {
    $.form.reset();
    imCreditLogUpdateSummary(null);
}

function imCreditLogFillDetail(detail) {
    $("#cld-type").text(imCreditFormatType(detail.type));
    $("#cld-subtype").text(detail.subtype != null ? detail.subtype : "-");
    $("#cld-idno").text(detail.idno || "-");
    $("#cld-lianghao").text(detail.lianghao || "-");
    $("#cld-totalCreditScore").text(imCreditFormatScore(detail.totalCreditScore));
    $("#cld-score").text(imCreditFormatScore(detail.score));
    $("#cld-logDesc").text(detail.logDesc || "-");
    $("#cld-vipBonusRate").text(imCreditFormatPercent(detail.vipBonusRate));
    $("#cld-lianghaoBonusRate").text(imCreditFormatPercent(detail.lianghaoBonusRate));
    $("#cld-baseScore").text(imCreditFormatScore(detail.baseScore));
    $("#cld-remark").text(detail.remark || "-");
    $("#cld-userId").text(detail.userId || "-");
    $("#cld-createTime").text(detail.createTime || "-");
}

function imCreditLogOpenDetail(id) {
    imCreditAjax({
        url: imCreditLogApi + "/queryById/" + id,
        type: "GET",
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                imCreditLogFillDetail(imCreditResolveEntity(res, "detail"));
                layer.open({
                    type: 1,
                    title: "信用分明细详情",
                    area: ["640px", "560px"],
                    shadeClose: true,
                    content: $("#credit-log-detail-modal"),
                    btn: ["关闭"],
                    yes: function (index) {
                        layer.close(index);
                    }
                });
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
            }
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imCreditLogInitTable(canView) {
    imCreditTypeSelectOptions($("#credit-log-type"), true);

    imInitTable({
        url: imCreditLogApi + "/pageByUser",
        formId: "credit-log-form",
        queryParams: imCreditLogQueryParams,
        responseHandler: imCreditLogResponseHandler,
        modalName: "信用分明细",
        columns: [
            { field: "type", title: "类型", sortable: true, formatter: imCreditFormatType },
            { field: "idno", title: "idno", sortable: true, formatter: function (v) { return imCreditEllipsis(v, 20); } },
            { field: "lianghao", title: "靓号", sortable: true, formatter: function (v) { return imCreditEllipsis(v, 20); } },
            { field: "totalCreditScore", title: "总信用分", sortable: true, formatter: imCreditFormatScore },
            { field: "score", title: "变动分数", sortable: true, formatter: imCreditFormatScore },
            { field: "logDesc", title: "描述", formatter: function (v) { return imCreditEllipsis(v, 30); } },
            { field: "vipBonusRate", title: "VIP加成率(%)", formatter: imCreditFormatPercent },
            { field: "lianghaoBonusRate", title: "靓号加成率(%)", formatter: imCreditFormatPercent },
            { field: "baseScore", title: "基础分", sortable: true, formatter: imCreditFormatScore },
            { field: "remark", title: "备注", formatter: function (v) { return imCreditEllipsis(v, 20); } },
            { field: "createTime", title: "创建时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    if (!canView) return "-";
                    return '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imCreditLogOpenDetail(\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a>';
                }
            }
        ]
    });
}
