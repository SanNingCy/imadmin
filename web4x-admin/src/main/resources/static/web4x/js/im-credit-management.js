/**
 * IM 用户信用分管理
 * - GET  /admin/creditScore/user/details/page
 * - GET  /admin/creditScore/user/details/queryByUserIdWithTypes/{userId}
 * - POST /admin/creditScore/user/activateCredit
 * - POST /admin/creditScore/user/addScore
 * - POST /admin/creditScore/user/reduceScore
 * - GET  /admin/creditScore/typeConfig/listEnabled
 */

var imCreditMgmtApi = ctx + "admin/creditScore/user";

function imCreditMgmtQueryParams(params) {
    return imCreditQueryParams("credit-mgmt-form", params);
}

function imCreditMgmtFormatCreditStatus(val) {
    var num = Number(val);
    if (num === 1) {
        return '<span class="label label-success">' + imCreditFormatCreditStatus(val) + "</span>";
    }
    if (num === 0) {
        return '<span class="label label-default">' + imCreditFormatCreditStatus(val) + "</span>";
    }
    return imCreditFormatCreditStatus(val);
}

function imCreditMgmtFormatTypeStatus(val) {
    var num = Number(val);
    if (num === 1) return '<span class="label label-success">启用</span>';
    if (num === 0) return '<span class="label label-default">禁用</span>';
    return "-";
}

function imCreditMgmtLoadEnabledTypes($select) {
    if (!$select || !$select.length) return;
    imCreditAjax({
        url: imCreditApi("typeConfig/listEnabled"),
        type: "GET",
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                var list = res.list || (res.data && res.data.list) || [];
                $select.empty().append('<option value="">请选择</option>');
                list.forEach(function (item) {
                    var label = imCreditFormatType(item.type);
                    if (item.subtype != null && item.subtype !== "") {
                        label += " / " + item.subtype;
                    }
                    $select.append('<option value="' + item.type + '">' + label + "</option>");
                });
            }
        }
    });
}

function imCreditMgmtFillDetail(detail) {
    $("#cmd-idno").text(detail.idno || "-");
    $("#cmd-lianghao").text(detail.lianghao || "-");
    $("#cmd-totalCreditScore").text(imCreditFormatScore(detail.totalCreditScore));
    $("#cmd-creditStatus").html(imCreditMgmtFormatCreditStatus(detail.creditStatus));
    $("#cmd-userId").text(detail.userId || "-");

    var $tbody = $("#credit-mgmt-detail-types-body");
    $tbody.empty();
    var types = detail.types || [];
    if (!types.length) {
        $tbody.append('<tr><td colspan="8" class="text-center text-muted">暂无类型数据</td></tr>');
        return;
    }
    types.forEach(function (item) {
        var html = "<tr>";
        html += "<td>" + imCreditFormatType(item.type) + "</td>";
        html += "<td>" + (item.subtype != null ? item.subtype : "-") + "</td>";
        html += "<td>" + imCreditFormatScore(item.currentScore) + "</td>";
        html += "<td>" + imCreditFormatScore(item.baseScore) + "</td>";
        html += "<td>" + imCreditFormatScore(item.maxLimit) + "</td>";
        html += "<td>" + imCreditMgmtFormatTypeStatus(item.status) + "</td>";
        html += "<td>" + imCreditFormatConstitute(item.constituteShow) + "</td>";
        html += "<td>" + (item.updateTime || "-") + "</td>";
        html += "</tr>";
        $tbody.append(html);
    });
}

function imCreditMgmtOpenDetail(userId) {
    imCreditAjax({
        url: imCreditMgmtApi + "/details/queryByUserIdWithTypes/" + encodeURIComponent(userId),
        type: "GET",
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                imCreditMgmtFillDetail(imCreditResolveEntity(res, "detail"));
                layer.open({
                    type: 1,
                    title: "用户信用分详情",
                    area: ["860px", "560px"],
                    shadeClose: true,
                    content: $("#credit-mgmt-detail-modal"),
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

function imCreditMgmtActivate(userId) {
    $.modal.confirm("确定为该用户开通信用分吗？", function () {
        imCreditAjax({
            url: imCreditMgmtApi + "/activateCredit",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({ userId: userId }),
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "开通成功");
                    $("#bootstrap-table").bootstrapTable("refresh");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "开通失败");
                }
            },
            error: function () {
                $.modal.alertWarning("开通失败");
            }
        });
    });
}

function imCreditMgmtOpenScoreModal(mode, userId) {
    $("#credit-mgmt-score-userId").val(userId || "");
    $("#credit-mgmt-score-mode").val(mode);
    $("#credit-mgmt-score-value").val("");
    $("#credit-mgmt-score-desc").val("");
    imCreditMgmtLoadEnabledTypes($("#credit-mgmt-score-type"));

    layer.open({
        type: 1,
        title: mode === "add" ? "增加信用分" : "减少信用分",
        area: ["480px", "360px"],
        shadeClose: true,
        content: $("#credit-mgmt-score-modal"),
        btn: ["保存", "取消"],
        yes: function (index) {
            imCreditMgmtSubmitScore(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imCreditMgmtSubmitScore(layerIndex) {
    var mode = $("#credit-mgmt-score-mode").val();
    var userId = $("#credit-mgmt-score-userId").val();
    var type = $("#credit-mgmt-score-type").val();
    var score = $("#credit-mgmt-score-value").val();
    var desc = $.trim($("#credit-mgmt-score-desc").val());

    if (!userId) {
        $.modal.alertWarning("用户ID不能为空");
        return;
    }
    if (!type) {
        $.modal.alertWarning("请选择类型");
        return;
    }
    if (!score || Number(score) <= 0) {
        $.modal.alertWarning("请输入有效分数");
        return;
    }
    if (String(type) === "5" && !desc) {
        $.modal.alertWarning("平台贡献(type=5)必须填写描述");
        return;
    }

    var url = imCreditMgmtApi + (mode === "add" ? "/addScore" : "/reduceScore");
    imCreditAjax({
        url: url,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            userId: userId,
            type: Number(type),
            score: Number(score),
            desc: desc || null
        }),
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess((res && res.msg) ? res.msg : "操作成功");
                layer.close(layerIndex);
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

function imCreditMgmtResponseHandler(res) {
    return imPageResponse(res);
}

function imCreditMgmtSafeFormat(fn, fallback) {
    return function (value, row, index) {
        try {
            return fn(value, row, index);
        } catch (e) {
            console.error("credit mgmt formatter error", e);
            return fallback != null ? fallback : "-";
        }
    };
}

function imCreditMgmtInitTable(canView, canEdit) {
    imCreditTypeSelectOptions($("#credit-mgmt-type"), true);
    imCreditMgmtLoadEnabledTypes($("#credit-mgmt-score-type"));

    imInitTable({
        url: imCreditMgmtApi + "/details/page",
        formId: "credit-mgmt-form",
        queryParams: imCreditMgmtQueryParams,
        responseHandler: imCreditMgmtResponseHandler,
        uniqueId: "userId",
        escape: false,
        modalName: "用户信用分",
        columns: [
            { field: "idno", title: "ID号", sortable: true, formatter: imCreditMgmtSafeFormat(function (v) { return imCreditEllipsis(v, 20); }) },
            { field: "lianghao", title: "靓号", sortable: true, formatter: imCreditMgmtSafeFormat(function (v) { return imCreditEllipsis(v, 20); }) },
            { field: "totalCreditScore", title: "总信用分", sortable: true, formatter: imCreditMgmtSafeFormat(imCreditFormatScore) },
            { field: "creditStatus", title: "信用分状态", sortable: true, formatter: imCreditMgmtSafeFormat(imCreditMgmtFormatCreditStatus) },
            {
                field: "types",
                title: "类型预览",
                formatter: imCreditMgmtSafeFormat(function (value) {
                    return imCreditRenderTypesPreview(value);
                })
            },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    var userId = row.userId;
                    if (!userId) return "-";

                    if (canEdit && String(row.creditStatus) !== "1") {
                        actions.push(
                            '<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imCreditMgmtActivate(\'' +
                            userId + '\')"><i class="fa fa-check"></i>开通信用分</a>'
                        );
                    }
                    if (canView) {
                        actions.push(
                            '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imCreditMgmtOpenDetail(\'' +
                            userId + '\')"><i class="fa fa-search"></i>查看详情</a>'
                        );
                    }
                    if (canEdit) {
                        actions.push(
                            '<a class="btn btn-primary btn-xs" href="javascript:void(0)" onclick="imCreditMgmtOpenScoreModal(\'add\',\'' +
                            userId + '\')"><i class="fa fa-plus"></i>增加信用分</a>'
                        );
                        actions.push(
                            '<a class="btn btn-warning btn-xs" href="javascript:void(0)" onclick="imCreditMgmtOpenScoreModal(\'reduce\',\'' +
                            userId + '\')"><i class="fa fa-minus"></i>减少信用分</a>'
                        );
                    }
                    return actions.length ? actions.join(" ") : "-";
                }
            }
        ]
    });
}
