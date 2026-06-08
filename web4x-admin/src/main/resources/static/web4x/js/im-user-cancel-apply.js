/**
 * IM 用户注销申请
 * - GET  /zhuxiao/zhuxiao/list
 * - POST /zhuxiao/zhuxiao/examine  (id, state, reason?)
 */

var imCancelApplyApi = ctx + "zhuxiao/zhuxiao";

var IM_CANCEL_APPLY_STATE = {
    "1": "待审核",
    "2": "已通过",
    "3": "已驳回"
};

function imCancelApplyQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("cancel-apply-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imCancelApplyFormatState(val) {
    return IM_CANCEL_APPLY_STATE[String(val)] || val || "-";
}

function imCancelApplyEllipsis(val, max) {
    if (!val) return "-";
    var text = String(val);
    max = max || 40;
    var short = text.length > max ? text.substring(0, max) + "..." : text;
    return '<span title="' + text.replace(/"/g, "&quot;") + '">' + short + "</span>";
}

function imCancelApplyExamine(payload, successMsg) {
    $.ajax({
        url: imCancelApplyApi + "/examine",
        type: "POST",
        data: payload,
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess((res && res.msg) ? res.msg : successMsg);
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

function imCancelApplyApprove(id) {
    $.modal.confirm("确定审核通过该注销申请吗？", function () {
        imCancelApplyExamine({ id: id, state: "2" }, "审核成功");
    });
}

function imCancelApplyOpenReject(id) {
    $("#cancel-apply-reject-id").val(id || "");
    $("#cancel-apply-reject-reason").val("");
    layer.open({
        type: 1,
        title: "驳回原因填写",
        area: ["480px", "280px"],
        shadeClose: true,
        content: $("#cancel-apply-reject-modal"),
        btn: ["保存", "取消"],
        yes: function (index) {
            imCancelApplySubmitReject(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imCancelApplySubmitReject(layerIndex) {
    var id = $("#cancel-apply-reject-id").val();
    var reason = $.trim($("#cancel-apply-reject-reason").val());
    if (!id) return $.modal.alertWarning("缺少记录ID");
    if (!reason) return $.modal.alertWarning("请输入驳回原因");

    $.ajax({
        url: imCancelApplyApi + "/examine",
        type: "POST",
        data: { id: id, state: "3", reason: reason },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess((res && res.msg) ? res.msg : "驳回成功");
                layer.close(layerIndex);
                $("#bootstrap-table").bootstrapTable("refresh");
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "驳回失败");
            }
        },
        error: function () {
            $.modal.alertWarning("驳回失败");
        }
    });
}

function imCancelApplyInitTable(canApprove, canReject) {
    imInitTable({
        url: imCancelApplyApi + "/list",
        formId: "cancel-apply-form",
        queryParams: imCancelApplyQueryParams,
        responseHandler: imPageResponse,
        modalName: "注销申请",
        columns: [
            { field: "eqid", title: "设备号", sortable: true },
            { field: "acc", title: "账号名", sortable: true },
            { field: "idno", title: "id号", sortable: true },
            { field: "state", title: "审核状态", sortable: true, formatter: imCancelApplyFormatState },
            { field: "reason", title: "驳回原因", sortable: true, formatter: function (v) { return imCancelApplyEllipsis(v); } },
            { field: "createDate", title: "申请时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    if (String(row.state) !== "1") {
                        return "-";
                    }
                    var actions = [];
                    if (canApprove) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imCancelApplyApprove(\'' + row.id + '\')"><i class="fa fa-check"></i>审核通过</a> ');
                    }
                    if (canReject) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imCancelApplyOpenReject(\'' + row.id + '\')"><i class="fa fa-times"></i>驳回</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
