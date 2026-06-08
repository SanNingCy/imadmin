/**
 * IM 提现申请（接口：/admin/asset/withdrawApply/*）
 */
var imWithdrawApplyApi = ctx + "admin/asset/withdrawApply";
var imWithdrawApplyListApi = (typeof imWithdrawApplyListUrl !== "undefined" && imWithdrawApplyListUrl)
    ? imWithdrawApplyListUrl
    : imWithdrawApplyApi + "/page";
var imButtonConfigApi = ctx + "buttonConfig/buttonConfig";
var IM_WITHDRAW_FN_KEY = "withdraw";
var IM_WITHDRAW_AUDIT_KEY = "withdraw_audit";

var IM_WITHDRAW_COIN = {
    "1": "积分",
    "2": "代币"
};

var IM_WITHDRAW_STATUS = {
    "0": { text: "发起提现", cls: "info" },
    "1": { text: "正在提现", cls: "primary" },
    "2": { text: "提现成功", cls: "success" },
    "3": { text: "提现失败", cls: "danger" },
    "4": { text: "申请中", cls: "warning" },
    "5": { text: "拒绝提现", cls: "danger" }
};

var imWithdrawApplyNeedGoogle = false;

function imWithdrawApplySetWithdrawFnLabel(enabled) {
    $("#withdraw-fn-label")
        .text(enabled ? "开启" : "关闭")
        .toggleClass("is-on", !!enabled);
}

function imWithdrawApplyQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("withdraw-apply-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imWithdrawApplyResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imWithdrawApplyFormatCoin(value) {
    return IM_WITHDRAW_COIN[String(value)] || value || "-";
}

function imWithdrawApplyFormatStatus(value) {
    var item = IM_WITHDRAW_STATUS[String(value)];
    if (!item) {
        return value != null && value !== "" ? value : "-";
    }
    return '<span class="badge badge-' + item.cls + '">' + item.text + "</span>";
}

function imWithdrawApplyFormatMoney(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    if (isNaN(num)) {
        return value;
    }
    return num.toFixed(2);
}

function imWithdrawApplyEscapeHtml(text) {
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

/** 限制 td 宽度，防止长文本撑开表格 */
function imWithdrawApplyEllipsisCell(maxWidth) {
    return function () {
        return {
            css: {
                "max-width": maxWidth + "px",
                "width": maxWidth + "px",
                "overflow": "hidden",
                "white-space": "nowrap",
                "text-overflow": "ellipsis"
            }
        };
    };
}

/** 限制单元格展示长度，悬浮 title 显示完整内容 */
function imWithdrawApplyFormatText(value, maxLen, maxWidth) {
    if (value == null || value === "") {
        return "-";
    }
    var text = String(value);
    var display = text;
    if (maxLen && text.length > maxLen) {
        display = text.substring(0, maxLen) + "...";
    }
    var width = maxWidth || 120;
    var style = "display:block;max-width:100%;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;";
    return '<span class="im-withdraw-apply-ellipsis" style="' + style + '" title="' + imWithdrawApplyEscapeHtml(text) + '">'
        + imWithdrawApplyEscapeHtml(display) + "</span>";
}

function imWithdrawApplyLoadAuditMode() {
    $.ajax({
        url: imButtonConfigApi + "/queryByButtonKey",
        type: "GET",
        data: { buttonKey: IM_WITHDRAW_AUDIT_KEY },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                var cfg = imWithdrawApplyResolveEntity(res, "buttonConfig");
                imWithdrawApplyNeedGoogle = Number(cfg.buttonStatus) === 1;
            }
        }
    });
}

function imWithdrawApplyLoadWithdrawFn(canEditFn) {
    if (!canEditFn) {
        return;
    }
    $.ajax({
        url: imButtonConfigApi + "/queryByButtonKey",
        type: "GET",
        data: { buttonKey: IM_WITHDRAW_FN_KEY },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                var cfg = imWithdrawApplyResolveEntity(res, "buttonConfig");
                var enabled = Number(cfg.buttonStatus) === 1;
                $("#withdraw-fn-toggle").prop("checked", enabled);
                imWithdrawApplySetWithdrawFnLabel(enabled);
            }
        }
    });
}

function imWithdrawApplyBindWithdrawFn(canEditFn) {
    if (!canEditFn) {
        return;
    }
    $("#withdraw-fn-toggle").on("change", function () {
        var checked = $(this).is(":checked");
        var self = this;
        $.ajax({
            url: imButtonConfigApi + "/updateKey",
            type: "POST",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify({
                buttonKey: IM_WITHDRAW_FN_KEY,
                buttonName: "提现功能",
                buttonStatus: checked ? "1" : "0"
            }),
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imWithdrawApplySetWithdrawFnLabel(checked);
                    $.modal.msgSuccess(res.msg || (checked ? "开启成功" : "关闭成功"));
                } else {
                    $(self).prop("checked", !checked);
                    $.modal.alertWarning((res && res.msg) ? res.msg : "操作失败");
                }
            },
            error: function () {
                $(self).prop("checked", !checked);
                $.modal.alertWarning("操作失败");
            }
        });
    });
}

function imWithdrawApplyFillView(info) {
    $("#wa-view-id").text(info.id != null ? info.id : "-");
    $("#wa-view-userId").text(info.userId || "-");
    $("#wa-view-idno").text(info.idno || "-");
    $("#wa-view-nickname").text(info.nickname || "-");
    $("#wa-view-transactionNumber").text(info.transactionNumber || "-");
    $("#wa-view-coinId").text(imWithdrawApplyFormatCoin(info.coinId));
    $("#wa-view-amount").text(imWithdrawApplyFormatMoney(info.amount));
    $("#wa-view-actualAmount").text(imWithdrawApplyFormatMoney(info.actualAmount));
    $("#wa-view-rateAmount").text(imWithdrawApplyFormatMoney(info.rateAmount));
    $("#wa-view-withdrawalId").text(info.withdrawalId != null ? info.withdrawalId : "-");
    $("#wa-view-status").html(imWithdrawApplyFormatStatus(info.status));
    $("#wa-view-receivingAddress").text(info.receivingAddress || "-");
    $("#wa-view-withdrawalHash").text(info.withdrawalHash || "-");
    $("#wa-view-remark").text(info.remark || "-");
    $("#wa-view-createTime").text(info.createTime || "-");
    $("#wa-view-updateTime").text(info.updateTime || "-");
}

function imWithdrawApplyOpenView(id) {
    $.ajax({
        url: imWithdrawApplyApi + "/" + encodeURIComponent(id),
        type: "GET",
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                imWithdrawApplyFillView(imWithdrawApplyResolveEntity(res, "apply"));
                layer.open({
                    type: 1,
                    title: "提现申请详情",
                    area: ["720px", "560px"],
                    shadeClose: true,
                    content: $("#withdraw-apply-view-modal"),
                    btn: ["关闭"],
                    yes: function (index) {
                        layer.close(index);
                    }
                });
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "查询失败");
            }
        },
        error: function () {
            $.modal.alertWarning("查询失败");
        }
    });
}

function imWithdrawApplyOpenAudit(id) {
    $("#wa-audit-id").val(id);
    $("#wa-audit-status").val("0");
    $("#wa-audit-googleCode").val("");
    $("#wa-audit-remark").val("");
    layer.open({
        type: 1,
        title: "提现审核",
        area: ["520px", "360px"],
        shadeClose: true,
        content: $("#withdraw-apply-audit-modal"),
        btn: ["提交", "取消"],
        yes: function (index) {
            imWithdrawApplySubmitAudit(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imWithdrawApplySubmitAudit(layerIndex) {
    var id = $("#wa-audit-id").val();
    var status = $("#wa-audit-status").val();
    var remark = $.trim($("#wa-audit-remark").val());
    var googleCode = $.trim($("#wa-audit-googleCode").val());

    if (!id) {
        return $.modal.alertWarning("记录ID不能为空");
    }
    if (String(status) === "5" && !remark) {
        return $.modal.alertWarning("拒绝时备注不能为空");
    }
    if (imWithdrawApplyNeedGoogle && !googleCode) {
        return $.modal.alertWarning("请输入谷歌验证码");
    }

    $.ajax({
        url: imWithdrawApplyApi + "/status",
        type: "PUT",
        data: {
            id: id,
            status: status,
            remark: remark,
            inputGoogleCode: googleCode
        },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess(res.msg || "审核成功");
                layer.close(layerIndex);
                $("#bootstrap-table").bootstrapTable("refresh");
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "审核失败");
            }
        },
        error: function () {
            $.modal.alertWarning("审核失败");
        }
    });
}

function imWithdrawApplyInitTable(canView, canReview) {
    imInitTable({
        url: imWithdrawApplyListApi,
        formId: "withdraw-apply-form",
        modalName: "提现申请",
        sortName: "createTime",
        sortOrder: "desc",
        queryParams: imWithdrawApplyQueryParams,
        columns: [
            {
                field: "id",
                title: "ID",
                sortable: true,
                width: 80,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(80),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 10, 80);
                }
            },
            {
                field: "userId",
                title: "用户id",
                sortable: true,
                width: 80,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(80),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 8, 80);
                }
            },
            {
                field: "idno",
                title: "ID号",
                sortable: true,
                width: 90,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(90),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 10, 90);
                }
            },
            {
                field: "nickname",
                title: "用户昵称",
                sortable: true,
                width: 100,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(100),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 8, 100);
                }
            },
            {
                field: "transactionNumber",
                title: "提现交易号",
                sortable: true,
                width: 130,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(130),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 16, 130);
                }
            },
            {
                field: "coinId",
                title: "种类",
                sortable: true,
                width: 70,
                widthUnit: "px",
                formatter: function (value) {
                    return imWithdrawApplyFormatCoin(value);
                }
            },
            {
                field: "amount",
                title: "提现金额",
                sortable: true,
                width: 90,
                widthUnit: "px",
                formatter: function (value) {
                    return imWithdrawApplyFormatMoney(value);
                }
            },
            {
                field: "actualAmount",
                title: "实际提现金额",
                sortable: true,
                width: 110,
                widthUnit: "px",
                formatter: function (value) {
                    return imWithdrawApplyFormatMoney(value);
                }
            },
            {
                field: "rateAmount",
                title: "费率金额",
                sortable: true,
                width: 90,
                widthUnit: "px",
                formatter: function (value) {
                    return imWithdrawApplyFormatMoney(value);
                }
            },
            {
                field: "withdrawalId",
                title: "外部提现ID",
                sortable: true,
                width: 100,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(100),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 12, 100);
                }
            },
            {
                field: "status",
                title: "提现状态",
                sortable: true,
                width: 90,
                widthUnit: "px",
                formatter: function (value) {
                    return imWithdrawApplyFormatStatus(value);
                }
            },
            {
                field: "receivingAddress",
                title: "收款地址",
                sortable: true,
                width: 140,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(140),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 14, 140);
                }
            },
            {
                field: "withdrawalHash",
                title: "提现hash",
                sortable: true,
                width: 140,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(140),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 14, 140);
                }
            },
            {
                field: "remark",
                title: "备注",
                sortable: true,
                width: 110,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(110),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 12, 110);
                }
            },
            {
                field: "createTime",
                title: "创建时间",
                sortable: true,
                width: 150,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(150),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 19, 150);
                }
            },
            {
                field: "updateTime",
                title: "更新时间",
                sortable: true,
                width: 150,
                widthUnit: "px",
                cellStyle: imWithdrawApplyEllipsisCell(150),
                formatter: function (value) {
                    return imWithdrawApplyFormatText(value, 19, 150);
                }
            },
            {
                title: "操作",
                align: "center",
                width: 130,
                widthUnit: "px",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imWithdrawApplyOpenView(\'' + row.id + '\')"><i class="fa fa-search"></i>查询</a> ');
                    }
                    if (canReview && Number(row.status) === 4) {
                        actions.push('<a class="btn btn-warning btn-xs" href="javascript:void(0)" onclick="imWithdrawApplyOpenAudit(\'' + row.id + '\')"><i class="fa fa-check"></i>审核</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}

function imWithdrawApplyInitPage(canView, canReview, canEditFn) {
    imWithdrawApplyLoadAuditMode();
    imWithdrawApplyLoadWithdrawFn(canEditFn);
    imWithdrawApplyBindWithdrawFn(canEditFn);
    imWithdrawApplyInitTable(canView, canReview);
}