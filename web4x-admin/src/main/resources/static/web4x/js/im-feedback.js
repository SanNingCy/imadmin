/**
 * IM 意见反馈（接口复用 web4x-im）
 * - GET    /feedback/feedback/list
 * - GET    /feedback/feedback/queryById?id=xxx
 * - POST   /feedback/feedback/reply
 * - POST   /feedback/feedback/adopt
 * - DELETE /feedback/feedback/delete?ids=1,2
 */

var imFeedbackApi = ctx + "feedback/feedback";

var IM_FEEDBACK_STATUS = { 0: "待处理", 1: "已回复", 2: "已采纳" };

function imFeedbackQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("feedback-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imFeedbackEllipsis(val, max) {
    if (!val) return "";
    var text = String(val);
    max = max || 40;
    var short = text.length > max ? text.substring(0, max) + "..." : text;
    return '<span title="' + text.replace(/"/g, "&quot;") + '">' + short + "</span>";
}

function imFeedbackResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imFeedbackMemberField(row, field) {
    if (row && row[field]) return row[field];
    if (row && row.member && row.member[field]) return row.member[field];
    return "-";
}

function imFeedbackFormatStatus(value) {
    return IM_FEEDBACK_STATUS[Number(value)] || value || "-";
}

function imFeedbackRenderImages(images) {
    if (!images) return "-";
    var html = [];
    String(images).split("|").forEach(function (src) {
        if (!src) return;
        html.push('<a href="' + src + '" target="_blank" rel="noreferrer"><img src="' + src + '" style="width:50px;height:50px;object-fit:cover;margin-right:4px;" alt="img"/></a>');
    });
    return html.length ? html.join("") : "-";
}

function imFeedbackFillDetail(info) {
    $("#fb-nickname").text(imFeedbackMemberField(info, "nickname"));
    $("#fb-idno").text(imFeedbackMemberField(info, "idno"));
    $("#fb-phone").text(info.phone || "-");
    $("#fb-content").text(info.content || "-");
    $("#fb-reply").text(info.reply || "-");
    $("#fb-replyDate").text(info.replyDate || "-");
    $("#fb-status").text(imFeedbackFormatStatus(info.status));
    $("#fb-rewardAmount").text(info.rewardAmount != null ? info.rewardAmount : "-");
    $("#fb-createDate").text(info.createDate || "-");
    $("#fb-images").html(imFeedbackRenderImages(info.images));
}

function imFeedbackOpenDetail(id) {
    $.ajax({
        url: imFeedbackApi + "/queryById",
        type: "GET",
        data: { id: id },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                imFeedbackFillDetail(imFeedbackResolveEntity(res, "feedback"));
                layer.open({
                    type: 1,
                    title: "反馈详情",
                    area: ["640px", "520px"],
                    shadeClose: true,
                    content: $("#feedback-detail-modal"),
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

function imFeedbackOpenAction(mode, id) {
    $("#feedback-action-id").val(id || "");
    $("#feedback-action-mode").val(mode);
    $("#feedback-reply").val("");
    $("#feedback-rewardAmount").val("");
    $("#feedback-reply-group").toggle(mode === "reply");
    $("#feedback-reward-group").toggle(mode === "adopt");

    layer.open({
        type: 1,
        title: mode === "reply" ? "回复意见反馈" : "采纳意见反馈",
        area: ["480px", "320px"],
        shadeClose: true,
        content: $("#feedback-action-modal"),
        btn: ["保存", "取消"],
        yes: function (index) {
            imFeedbackSubmitAction(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imFeedbackSubmitAction(layerIndex) {
    var id = $("#feedback-action-id").val();
    var mode = $("#feedback-action-mode").val();
    if (!id) return $.modal.alertWarning("缺少反馈ID");

    var url;
    var payload;

    if (mode === "reply") {
        var reply = $.trim($("#feedback-reply").val());
        if (!reply) return $.modal.alertWarning("请输入回复内容");
        url = imFeedbackApi + "/reply";
        payload = { id: id, reply: reply };
    } else {
        var reward = $("#feedback-rewardAmount").val();
        if (reward === "" || reward == null) return $.modal.alertWarning("请输入奖励金额");
        if (Number(reward) < 0) return $.modal.alertWarning("奖励金额不能小于0");
        url = imFeedbackApi + "/adopt";
        payload = { id: id, rewardAmount: String(reward) };
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
                $.modal.msgSuccess(res.msg || "操作成功");
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

function imFeedbackRemove(ids) {
    $.modal.confirm("确定删除选中的记录吗？", function () {
        $.ajax({
            url: imFeedbackApi + "/delete",
            type: "DELETE",
            data: { ids: ids },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess(res.msg || "删除成功");
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

function imFeedbackInitTable(canView, canEdit, canDelete) {
    imInitTable({
        url: imFeedbackApi + "/list",
        formId: "feedback-form",
        queryParams: imFeedbackQueryParams,
        responseHandler: imPageResponse,
        modalName: "意见反馈",
        columns: [
            {
                field: "nickname",
                title: "昵称",
                sortable: true,
                formatter: function (v, row) { return imFeedbackMemberField(row, "nickname"); }
            },
            {
                field: "idno",
                title: "ID号",
                sortable: true,
                formatter: function (v, row) { return imFeedbackMemberField(row, "idno"); }
            },
            {
                field: "content",
                title: "内容",
                sortable: true,
                formatter: function (v) { return imFeedbackEllipsis(v, 40); }
            },
            { field: "phone", title: "联系方式", sortable: true },
            {
                field: "reply",
                title: "回复内容",
                sortable: true,
                formatter: function (v) { return imFeedbackEllipsis(v, 30); }
            },
            { field: "replyDate", title: "回复时间", sortable: true },
            {
                field: "status",
                title: "状态",
                sortable: true,
                formatter: function (v) { return imFeedbackFormatStatus(v); }
            },
            {
                field: "rewardAmount",
                title: "奖励金额",
                sortable: true,
                formatter: function (v) { return v != null ? v : "-"; }
            },
            { field: "createDate", title: "反馈时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imFeedbackOpenDetail(\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (Number(row.status) === 0) {
                        actions.push('<a class="btn btn-primary btn-xs" href="javascript:void(0)" onclick="imFeedbackOpenAction(\'reply\',\'' + row.id + '\')"><i class="fa fa-reply"></i>回复</a> ');
                    }
                    if (Number(row.status) === 1) {
                        actions.push('<a class="btn btn-warning btn-xs" href="javascript:void(0)" onclick="imFeedbackOpenAction(\'adopt\',\'' + row.id + '\')"><i class="fa fa-check"></i>采纳</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imFeedbackRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
