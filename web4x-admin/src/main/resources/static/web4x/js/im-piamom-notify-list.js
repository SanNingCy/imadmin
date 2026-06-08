/**
 * 广场朋友圈 - 互动消息
 * - GET /admin/piamom/notify/page
 * - GET /admin/piamom/notify/queryById?id=
 */

function imPiamomNotifyListQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("piamom-notify-list-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPiamomNotifyListFillDetail(info) {
    $("#pnd-id").text(info.id != null ? info.id : "-");
    $("#pnd-receiver").text(
        (info.receiverNickname || "-") + " (" + (info.receiverIdno || "-") + ")"
    );
    $("#pnd-fromUser").text(
        (info.fromUserNickname || "-") + " (" + (info.fromUserIdno || "-") + ")"
    );
    $("#pnd-msgType").text(imPiamomFormatNotifyMsgType(info.msgType));
    $("#pnd-target").text(
        imPiamomFormatNotifyTargetType(info.targetType) + " / " + (info.targetId != null ? info.targetId : "-")
    );
    $("#pnd-refId-label").text(String(info.targetType) === "report" ? "帖子ID" : "关联ID");
    $("#pnd-refId").text(info.refId != null ? info.refId : "-");
    $("#pnd-isRead").html(imPiamomFormatReadStatus(info.isRead));
    $("#pnd-content").text(info.content || "-");
    $("#pnd-createdAt").text(info.createdAt || "-");
}

function imPiamomNotifyListOpenDetail(id) {
    $.ajax({
        url: imPiamomApi + "/notify/queryById",
        type: "GET",
        data: { id: id },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (!(res && (res.success === true || res.code === 200))) {
                return $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
            }
            var info = imPiamomResolveData(res, "notify") || {};
            imPiamomNotifyListFillDetail(info);
            layer.open({
                type: 1,
                title: "互动消息详情",
                area: ["640px", "480px"],
                shadeClose: true,
                content: $("#piamom-notify-detail-modal"),
                btn: ["关闭"],
                yes: function (index) {
                    layer.close(index);
                }
            });
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imPiamomNotifyListInitTable(canView) {
    imInitTable({
        url: imPiamomApi + "/notify/page",
        formId: "piamom-notify-list-form",
        queryParams: imPiamomNotifyListQueryParams,
        responseHandler: imPageResponse,
        modalName: "互动消息",
        escape: false,
        columns: [
            { field: "id", title: "ID", sortable: true, width: 70 },
            { field: "receiverIdno", title: "接收人IDNO", width: 110 },
            { field: "receiverNickname", title: "接收人昵称", width: 120, formatter: function (v) { return imFormatText(v, 16); } },
            { field: "fromUserIdno", title: "触发人IDNO", width: 110 },
            { field: "fromUserNickname", title: "触发人昵称", width: 120, formatter: function (v) { return imFormatText(v, 16); } },
            { field: "msgType", title: "消息类型", width: 140, formatter: imPiamomFormatNotifyMsgType },
            { field: "targetType", title: "对象类型", width: 90, formatter: imPiamomFormatNotifyTargetType },
            { field: "targetId", title: "对象ID", width: 90 },
            { field: "refId", title: "关联ID", width: 90 },
            { field: "isRead", title: "已读", width: 80, formatter: imPiamomFormatReadStatus, escape: false },
            { field: "createdAt", title: "时间", sortable: true, width: 160 },
            {
                title: "操作",
                align: "center",
                width: 80,
                formatter: function (value, row) {
                    if (!canView) {
                        return "-";
                    }
                    return '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imPiamomNotifyListOpenDetail(\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a>';
                }
            }
        ]
    });
}
