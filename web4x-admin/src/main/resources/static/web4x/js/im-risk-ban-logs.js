/**
 * IM 封禁记录查询（风控-安全管理）
 * - GET    /member/member/banList
 * - DELETE /member/member/feng?ids=
 * - DELETE /member/member/jie?ids=
 */

var imRiskBanLogsApi = ctx + "member/member";

function imRiskBanLogsQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("ban-logs-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imRiskBanLogsFormatSex(val) {
    if (String(val) === "1") return "男";
    if (String(val) === "2") return "女";
    return "-";
}

function imRiskBanLogsFormatState(val) {
    if (String(val) === "1") {
        return '<span class="label label-danger">已封禁</span>';
    }
    return '<span class="label label-success">正常</span>';
}

function imRiskBanLogsEllipsis(val) {
    if (val == null || val === "") return "-";
    var text = String(val);
    return '<span class="ban-log-ellipsis-inner" title="' + text.replace(/"/g, "&quot;") + '">' + text + "</span>";
}

function imRiskBanLogsRequest(method, ids, successMsg) {
    $.ajax({
        url: imRiskBanLogsApi + (method === "feng" ? "/feng" : "/jie"),
        type: "DELETE",
        data: { ids: ids },
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

function imRiskBanLogsFeng(id) {
    $.modal.confirm("确定封禁该用户吗？", function () {
        imRiskBanLogsRequest("feng", id, "封禁成功");
    });
}

function imRiskBanLogsJie(id) {
    $.modal.confirm("确定解除封禁吗？", function () {
        imRiskBanLogsRequest("jie", id, "解封成功");
    });
}

function imRiskBanLogsSelectedIds() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || !rows.length) {
        $.modal.alertWarning("请至少选择一条记录");
        return null;
    }
    return rows.map(function (row) { return row.id; }).join(",");
}

function imRiskBanLogsBatchFeng() {
    var ids = imRiskBanLogsSelectedIds();
    if (!ids) return;
    $.modal.confirm("确定批量封禁所选用户吗？", function () {
        imRiskBanLogsRequest("feng", ids, "批量封禁成功");
    });
}

function imRiskBanLogsBatchJie() {
    var ids = imRiskBanLogsSelectedIds();
    if (!ids) return;
    $.modal.confirm("确定批量解封所选用户吗？", function () {
        imRiskBanLogsRequest("jie", ids, "批量解封成功");
    });
}

function imRiskBanLogsInitTable(canBan, canUnban) {
    imInitListMediaPreview();
    imInitTable({
        url: imRiskBanLogsApi + "/banList",
        formId: "ban-logs-form",
        queryParams: imRiskBanLogsQueryParams,
        responseHandler: imPageResponse,
        modalName: "封禁记录",
        escape: false,
        onPostBody: function () {
            imBindListMediaPreview($("#bootstrap-table"));
        },
        columns: [
            { checkbox: true },
            { field: "eqno", title: "设备号", sortable: true, class: "ban-log-ellipsis", formatter: imRiskBanLogsEllipsis },
            { field: "idno", title: "id号", sortable: true, class: "ban-log-ellipsis", formatter: imRiskBanLogsEllipsis },
            { field: "nickname", title: "昵称", sortable: true },
            {
                field: "icon",
                title: "头像",
                width: 80,
                escape: false,
                ellipsis: false,
                cellStyle: function () {
                    return { css: { "text-align": "center", "vertical-align": "middle" } };
                },
                formatter: function (value, row) {
                    return imFormatListMedia(value, "ban-log-icon-" + row.id);
                }
            },
            { field: "sex", title: "性别", sortable: true, formatter: imRiskBanLogsFormatSex },
            { field: "sign", title: "签名", sortable: true, class: "ban-log-ellipsis", formatter: imRiskBanLogsEllipsis },
            { field: "state", title: "状态", sortable: true, formatter: imRiskBanLogsFormatState },
            { field: "createDate", title: "注册时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    var isNormal = String(row.state) === "0";
                    if (isNormal && canBan) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imRiskBanLogsFeng(\'' + row.id + '\')"><i class="fa fa-ban"></i>封禁</a>');
                    }
                    if (!isNormal && canUnban) {
                        actions.push('<a class="btn btn-warning btn-xs" href="javascript:void(0)" onclick="imRiskBanLogsJie(\'' + row.id + '\')"><i class="fa fa-unlock"></i>解封</a>');
                    }
                    return actions.length ? actions.join(" ") : "-";
                }
            }
        ]
    });
}
