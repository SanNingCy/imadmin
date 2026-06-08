/**
 * IM 用户封禁管理
 * - GET    /member/member/list
 * - DELETE /member/member/feng?ids=
 * - DELETE /member/member/jie?ids=
 */

var imUserBanApi = ctx + "member/member";

function imUserBanQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("ban-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imUserBanFormatSex(val) {
    if (String(val) === "1") return "男";
    if (String(val) === "2") return "女";
    return "-";
}

function imUserBanEscapeHtml(text) {
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

function imUserBanFormatEllipsis(val) {
    if (val == null || val === "") return "-";
    var text = String(val);
    var safe = imUserBanEscapeHtml(text);
    return '<span class="ban-ellipsis-inner" title="' + safe + '">' + safe + "</span>";
}

function imUserBanFormatState(val) {
    if (String(val) === "1") {
        return '<span class="label label-danger">永久封禁</span>';
    }
    return '<span class="label label-success">正常</span>';
}

function imUserBanFormatIcon(val) {
    if (!val) return "-";
    var html = [];
    String(val).split("|").forEach(function (src) {
        src = $.trim(src);
        if (src) {
            html.push('<img class="ban-avatar" src="' + src.replace(/"/g, "&quot;") + '" alt="avatar"/>');
        }
    });
    return html.length ? html.join("") : "-";
}

function imUserBanRequest(method, ids, successMsg) {
    $.ajax({
        url: imUserBanApi + (method === "feng" ? "/feng" : "/jie"),
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

function imUserBanFeng(id) {
    $.modal.confirm("确定封禁该用户吗？", function () {
        imUserBanRequest("feng", id, "封禁成功");
    });
}

function imUserBanJie(id) {
    $.modal.confirm("确定解除封禁吗？", function () {
        imUserBanRequest("jie", id, "解封成功");
    });
}

function imUserBanSelectedIds() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || !rows.length) {
        $.modal.alertWarning("请至少选择一条记录");
        return null;
    }
    return rows.map(function (row) { return row.id; }).join(",");
}

function imUserBanBatchFeng() {
    var ids = imUserBanSelectedIds();
    if (!ids) return;
    $.modal.confirm("确定批量封禁所选用户吗？", function () {
        imUserBanRequest("feng", ids, "批量封禁成功");
    });
}

function imUserBanBatchJie() {
    var ids = imUserBanSelectedIds();
    if (!ids) return;
    $.modal.confirm("确定批量解封所选用户吗？", function () {
        imUserBanRequest("jie", ids, "批量解封成功");
    });
}

function imUserBanInitTable(canBan, canUnban) {
    imInitTable({
        url: imUserBanApi + "/list",
        formId: "ban-form",
        queryParams: imUserBanQueryParams,
        responseHandler: imPageResponse,
        modalName: "用户",
        columns: [
            { checkbox: true },
            { field: "eqno", title: "设备号", sortable: true, width: 160, class: "ban-ellipsis", formatter: imUserBanFormatEllipsis },
            { field: "city", title: "地区", sortable: true, width: 100, class: "ban-ellipsis", formatter: imUserBanFormatEllipsis },
            { field: "acount", title: "账号", sortable: true, width: 120, class: "ban-ellipsis", formatter: imUserBanFormatEllipsis },
            { field: "idno", title: "idno", sortable: true, width: 120, class: "ban-ellipsis", formatter: imUserBanFormatEllipsis },
            { field: "nickname", title: "昵称", sortable: true },
            { field: "icon", title: "头像", formatter: imUserBanFormatIcon },
            { field: "sex", title: "性别", sortable: true, formatter: imUserBanFormatSex },
            { field: "sign", title: "签名", sortable: true, width: 120, class: "ban-ellipsis", formatter: imUserBanFormatEllipsis },
            { field: "state", title: "状态", sortable: true, formatter: imUserBanFormatState },
            { field: "createDate", title: "注册时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    var isNormal = String(row.state) === "0";
                    if (isNormal && canBan) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imUserBanFeng(\'' + row.id + '\')"><i class="fa fa-ban"></i>封禁</a>');
                    }
                    if (!isNormal && canUnban) {
                        actions.push('<a class="btn btn-warning btn-xs" href="javascript:void(0)" onclick="imUserBanJie(\'' + row.id + '\')"><i class="fa fa-unlock"></i>解封</a>');
                    }
                    return actions.join(" ");
                }
            }
        ]
    });
}
