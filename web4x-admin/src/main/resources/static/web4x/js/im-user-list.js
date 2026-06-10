/**

 * IM 用户列表

 * - GET    /member/member/list

 * - GET    /member/member/queryById

 * - POST   /member/member/save

 * - POST   /member/member/changeMoney

 * - DELETE /member/member/delete

 * - DELETE /member/member/clearpwd

 */



var imUserListApi = ctx + "member/member";

var imUserListUploadApi = ctx + "sys/file/webupload/upload?uploadPath=member";

var imUserListMemberMode = "view";



function imUserListQueryParams(params) {

    var pageSize = params.limit;

    var pageNo = params.offset / params.limit + 1;

    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);

    var formValues = $.common.formToJSON("user-list-form");

    return $.extend(query, imOmitEmptyParams(formValues));

}



function imUserListResolveEntity(res, key) {

    if (!res) return {};

    if (res[key]) return res[key];

    if (res.data && res.data[key]) return res.data[key];

    return {};

}



function imUserListFormatSex(val) {

    if (String(val) === "1") return "男";

    if (String(val) === "2") return "女";

    return "-";

}



function imUserListEscapeHtml(text) {

    return String(text)

        .replace(/&/g, "&amp;")

        .replace(/"/g, "&quot;")

        .replace(/</g, "&lt;")

        .replace(/>/g, "&gt;");

}



function imUserListFormatEllipsis(val) {

    if (val == null || val === "") return "-";

    var text = String(val);

    var safe = imUserListEscapeHtml(text);

    return '<span class="user-list-ellipsis-inner" title="' + safe + '">' + safe + "</span>";

}



function imUserListFormatState(val) {

    if (String(val) === "1") {

        return '<span class="label label-danger">永久封禁</span>';

    }

    return '<span class="label label-success">正常</span>';

}



function imUserListFormatYesNo(val) {

    if (String(val) === "1") {

        return '<span class="label label-success">是</span>';

    }

    if (String(val) === "0") {

        return '<span class="label label-default">否</span>';

    }

    return "-";

}



function imUserListImageCellStyle() {
    return { css: { "text-align": "left", "vertical-align": "middle" } };
}



function imUserListCopyText(text) {

    if (!text) return;

    if (navigator.clipboard && navigator.clipboard.writeText) {

        navigator.clipboard.writeText(String(text)).then(function () {

            $.modal.msgSuccess("复制成功");

        }).catch(function () {

            $.modal.msgWarning("复制失败");

        });

        return;

    }

    var input = document.createElement("textarea");

    input.value = String(text);

    document.body.appendChild(input);

    input.select();

    try {

        document.execCommand("copy");

        $.modal.msgSuccess("复制成功");

    } catch (e) {

        $.modal.msgWarning("复制失败");

    }

    document.body.removeChild(input);

}



function imUserListInitMemberDatetime() {
    $("#user-list-member-modal .im-modal-laydate").removeAttr("data-laydate-bound");
    imBindLaydate("#user-list-member-modal");
}

function imUserListSetMemberReadOnly(readOnly) {

    $("#user-list-member-form input:not(#ul-idno):not(#ul-icon):not(#ul-qrcode), #user-list-member-form select, #user-list-member-form textarea").each(function () {

        var $el = $(this);

        if ($el.attr("id") === "ul-idno" || $el.attr("id") === "ul-balance") {

            $el.prop("readonly", true);

            return;

        }

        if (readOnly) {

            $el.prop("readonly", true).prop("disabled", true);

        } else {

            $el.prop("readonly", false).prop("disabled", false);

        }

    });

    $("#ul-idno, #ul-balance").prop("readonly", true);

    $("#ul-icon-add-btn, #ul-qrcode-add-btn").toggleClass("disabled", readOnly);

    $("#ul-icon-add-btn input, #ul-qrcode-add-btn input").prop("disabled", readOnly);

    $(".ul-image-remove").toggle(!readOnly);

    if (readOnly) {

        $("#ul-icon-add-btn, #ul-qrcode-add-btn").hide();

    } else {

        $("#ul-icon-add-btn").toggle(!$("#ul-icon").val());

        $("#ul-qrcode-add-btn").toggle(!$("#ul-qrcode").val());

    }

}



function imUserListFirstImageUrl(val) {

    if (!val) return "";

    return String(val).split("|")[0].trim();

}



function imUserListResolveUploadUrl(res) {

    if (!res) return "";

    if (res.url) return res.url;

    if (res.data && res.data.url) return res.data.url;

    return "";

}



function imUserListRefreshImageField(field, url) {

    var $hidden = $("#ul-" + field);

    var $preview = $("#ul-" + field + "-preview");

    $hidden.val(url || "");

    if (url) {

        var displayUrl = imUserListFirstImageUrl(url);

        var safeUrl = displayUrl.replace(/"/g, "&quot;");

        $preview.addClass("has-image").html(

            '<img src="' + safeUrl + '" alt="' + field + '"/>' +

            '<a class="ul-image-remove" href="javascript:void(0)" onclick="imUserListClearImage(\'' + field + '\')" title="删除"><i class="fa fa-times"></i></a>'

        );

    } else {

        $preview.removeClass("has-image").empty();

    }

    var readOnly = $("#ul-" + field + "-add-btn").hasClass("disabled");

    $("#ul-" + field + "-add-btn").toggle(!readOnly && !url);

}



function imUserListClearImage(field) {

    if ($("#ul-" + field + "-add-btn").hasClass("disabled")) return;

    imUserListRefreshImageField(field, "");

}



function imUserListUploadImage(field, input) {

    if ($("#ul-" + field + "-add-btn").hasClass("disabled")) return;

    var file = input.files && input.files[0];

    if (!file) return;

    var fd = new FormData();

    fd.append("file", file);

    $.modal.loading("正在上传图片...");

    $.ajax({

        url: imUserListUploadApi,

        type: "POST",

        data: fd,

        processData: false,

        contentType: false,

        dataType: "json",

        beforeSend: imTableBeforeSend,

        success: function (res) {

            $.modal.closeLoading();

            if (res && (res.success === true || res.code === 200)) {

                var url = imUserListResolveUploadUrl(res);

                if (!url) {

                    $.modal.alertWarning("上传失败：未返回图片地址");

                    return;

                }

                imUserListRefreshImageField(field, url);

                $.modal.msgSuccess("上传成功");

            } else {

                $.modal.alertWarning((res && res.msg) ? res.msg : "上传失败");

            }

        },

        error: function () {

            $.modal.closeLoading();

            $.modal.alertWarning("上传失败");

        },

        complete: function () {

            input.value = "";

        }

    });

}



function imUserListFillMemberForm(info) {

    $("#user-list-member-id").val(info.id || "");

    $("#ul-eqno").val(info.eqno || "");

    $("#ul-idno").val(info.idno || "");

    $("#ul-nickname").val(info.nickname || "");

    $("#ul-sex").val(info.sex != null ? String(info.sex) : "");

    $("#ul-state").val(info.state != null ? String(info.state) : "0");

    $("#ul-balance").val(info.balance != null ? info.balance : "");

    $("#ul-isvip").val(info.isvip != null ? String(info.isvip) : "0");

    $("#ul-viptime").val(info.viptime || "");

    $("#ul-sign").val(info.sign || "");

    imUserListRefreshImageField("icon", info.icon || "");

    imUserListRefreshImageField("qrcode", info.qrcode || "");

    $("#ul-mbname").val(info.mbname || "");

    $("#ul-mbda").val(info.mbda || "");

    $("#ul-eqid").val(info.eqid || "");

}



function imUserListCollectMemberPayload() {

    return {

        id: $("#user-list-member-id").val(),

        eqno: $("#ul-eqno").val(),

        idno: $("#ul-idno").val(),

        nickname: $("#ul-nickname").val(),

        sex: $("#ul-sex").val(),

        state: $("#ul-state").val(),

        isvip: $("#ul-isvip").val(),

        viptime: $("#ul-viptime").val(),

        sign: $("#ul-sign").val(),

        icon: $("#ul-icon").val(),

        qrcode: $("#ul-qrcode").val(),

        mbname: $("#ul-mbname").val(),

        mbda: $("#ul-mbda").val(),

        eqid: $("#ul-eqid").val()

    };

}



function imUserListOpenMember(id, mode) {

    imUserListMemberMode = mode;

    $.ajax({

        url: imUserListApi + "/queryById",

        type: "GET",

        data: { id: id },

        dataType: "json",

        beforeSend: imTableBeforeSend,

        success: function (res) {

            if (!(res && (res.success === true || res.code === 200))) {

                $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");

                return;

            }

            imUserListFillMemberForm(imUserListResolveEntity(res, "member"));

            imUserListSetMemberReadOnly(mode === "view");

            var title = mode === "view" ? "查看用户" : "修改用户";

            var buttons = mode === "view" ? ["关闭"] : ["保存", "取消"];

            layer.open({

                type: 1,

                title: title,

                area: ["900px", "620px"],

                shadeClose: true,

                content: $("#user-list-member-modal"),

                btn: buttons,

                success: function () {
                    if (imUserListMemberMode !== "view") {
                        imUserListInitMemberDatetime();
                    }
                },

                yes: function (index) {

                    if (mode === "view") {

                        layer.close(index);

                        return;

                    }

                    imUserListSaveMember(index);

                },

                btn2: function (index) {

                    if (mode === "view") {

                        return true;

                    }

                    layer.close(index);

                    return false;

                }

            });

        },

        error: function () {

            $.modal.alertWarning("获取详情失败");

        }

    });

}



function imUserListSaveMember(layerIndex) {

    var payload = imUserListCollectMemberPayload();

    if (!payload.nickname) {

        $.modal.alertWarning("昵称不能为空");

        return;

    }

    if (!payload.icon) {

        $.modal.alertWarning("请上传头像");

        return;

    }

    if (!payload.qrcode) {

        $.modal.alertWarning("请上传名片二维码");

        return;

    }

    $.ajax({

        url: imUserListApi + "/save",

        type: "POST",

        contentType: "application/json",

        data: JSON.stringify(payload),

        dataType: "json",

        beforeSend: imTableBeforeSend,

        success: function (res) {

            if (res && (res.success === true || res.code === 200)) {

                $.modal.msgSuccess((res && res.msg) ? res.msg : "保存成功");

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



function imUserListOpenBalance(id) {

    $("#user-list-balance-id").val(id);

    $("#ul-btype").val("");

    $("#ul-money").val("");

    layer.open({

        type: 1,

        title: "变更余额",

        area: ["480px", "260px"],

        shadeClose: true,

        content: $("#user-list-balance-modal"),

        btn: ["保存", "取消"],

        yes: function (index) {

            var btype = $("#ul-btype").val();

            var money = $("#ul-money").val();

            if (!btype) {

                $.modal.alertWarning("请选择类型");

                return;

            }

            if (money === "" || money == null) {

                $.modal.alertWarning("请输入金额");

                return;

            }

            $.ajax({

                url: imUserListApi + "/changeMoney",

                type: "POST",

                contentType: "application/json",

                data: JSON.stringify({

                    id: $("#user-list-balance-id").val(),

                    btype: String(btype),

                    money: Number(money)

                }),

                dataType: "json",

                beforeSend: imTableBeforeSend,

                success: function (res) {

                    if (res && (res.success === true || res.code === 200)) {

                        $.modal.msgSuccess((res && res.msg) ? res.msg : "变更成功");

                        layer.close(index);

                        $("#bootstrap-table").bootstrapTable("refresh");

                    } else {

                        $.modal.alertWarning((res && res.msg) ? res.msg : "变更失败");

                    }

                },

                error: function () {

                    $.modal.alertWarning("变更失败");

                }

            });

        }

    });

}



function imUserListDelete(id) {

    $.modal.confirm("确定删除该用户吗？", function () {

        $.ajax({

            url: imUserListApi + "/delete",

            type: "DELETE",

            data: { ids: id },

            dataType: "json",

            beforeSend: imTableBeforeSend,

            success: function (res) {

                if (res && (res.success === true || res.code === 200)) {

                    $.modal.msgSuccess((res && res.msg) ? res.msg : "删除成功");

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



function imUserListClearPwd(id) {

    $.modal.confirm("确定清除支付密码吗？", function () {

        $.ajax({

            url: imUserListApi + "/clearpwd",

            type: "DELETE",

            data: { id: id },

            dataType: "json",

            beforeSend: imTableBeforeSend,

            success: function (res) {

                if (res && (res.success === true || res.code === 200)) {

                    $.modal.msgSuccess((res && res.msg) ? res.msg : "清除成功");

                    $("#bootstrap-table").bootstrapTable("refresh");

                } else {

                    $.modal.alertWarning((res && res.msg) ? res.msg : "清除失败");

                }

            },

            error: function () {

                $.modal.alertWarning("清除失败");

            }

        });

    });

}



function imUserListSelectedIds() {

    var rows = $("#bootstrap-table").bootstrapTable("getSelections");

    if (!rows || !rows.length) {

        $.modal.alertWarning("请至少选择一条记录");

        return null;

    }

    return rows.map(function (row) { return row.id; }).join(",");

}



function imUserListBatchDelete() {

    var ids = imUserListSelectedIds();

    if (!ids) return;

    $.modal.confirm("确定批量删除所选用户吗？", function () {

        $.ajax({

            url: imUserListApi + "/delete",

            type: "DELETE",

            data: { ids: ids },

            dataType: "json",

            beforeSend: imTableBeforeSend,

            success: function (res) {

                if (res && (res.success === true || res.code === 200)) {

                    $.modal.msgSuccess((res && res.msg) ? res.msg : "批量删除成功");

                    $("#bootstrap-table").bootstrapTable("refresh");

                } else {

                    $.modal.alertWarning((res && res.msg) ? res.msg : "批量删除失败");

                }

            },

            error: function () {

                $.modal.alertWarning("批量删除失败");

            }

        });

    });

}



function imUserListOpenTransaction(userId, idno) {
    var queryParams = null;
    if (userId) {
        queryParams = { uid: userId };
        if (idno) {
            queryParams.idno = idno;
        }
    }
    var menuUrls = [
        ctx + "balancelog/balanceLog",
        ctx + "balance"
    ];
    if (typeof openMenuPage === "function" && openMenuPage(menuUrls, queryParams)) {
        return;
    }
    var url = ctx + "balancelog/balanceLog";
    if (queryParams) {
        var parts = [];
        $.each(queryParams, function (key, val) {
            if (val != null && val !== "") {
                parts.push(encodeURIComponent(key) + "=" + encodeURIComponent(val));
            }
        });
        if (parts.length) {
            url += "?" + parts.join("&");
        }
    }
    window.open(url, "_blank");
}



function imUserListInitTable(canView, canEdit, canDelete, canChangeBalance, canTransaction, canClear) {

    imInitListMediaPreview();

    imInitTable({

        url: imUserListApi + "/list",

        formId: "user-list-form",

        queryParams: imUserListQueryParams,

        responseHandler: imPageResponse,

        modalName: "用户",

        escape: false,

        fixedColumns: true,

        fixedRightNumber: 1,

        operateWidth: 450,

        operateMinWidth: 450,

        onPostBody: function () {
            imBindListMediaPreview($("#bootstrap-table"));
        },

        columns: [

            { checkbox: true },

            { field: "eqno", title: "设备号", sortable: true, width: 160, class: "user-list-ellipsis", formatter: imUserListFormatEllipsis },

            { field: "city", title: "地区", sortable: true, width: 100, class: "user-list-ellipsis", formatter: imUserListFormatEllipsis },

            {

                field: "idno",

                title: "idno",

                sortable: true,

                formatter: function (val) {

                    if (!val) return "-";

                    return '<span class="user-list-copy" onclick="imUserListCopyText(\'' + String(val).replace(/'/g, "\\'") + '\')">' + val + "</span>";

                }

            },

            { field: "lianghao", title: "靓号", sortable: true },

            { field: "endtime", title: "靓号到期时间", sortable: true },

            {
                field: "qrcode",
                title: "名片二维码",
                width: 80,
                escape: false,
                ellipsis: false,
                cellStyle: imUserListImageCellStyle,
                formatter: function (value, row) {
                    return imFormatListMedia(value, "user-list-qrcode-" + row.id);
                }
            },

            {
                field: "qrcode2",
                title: "登录二维码",
                width: 80,
                escape: false,
                ellipsis: false,
                cellStyle: imUserListImageCellStyle,
                formatter: function (value, row) {
                    return imFormatListMedia(value, "user-list-qrcode2-" + row.id);
                }
            },

            { field: "nickname", title: "昵称", sortable: true },

            {
                field: "icon",
                title: "头像",
                width: 80,
                escape: false,
                ellipsis: false,
                cellStyle: imUserListImageCellStyle,
                formatter: function (value, row) {
                    return imFormatListMedia(value, "user-list-icon-" + row.id);
                }
            },

            { field: "sex", title: "性别", sortable: true, formatter: imUserListFormatSex },

            { field: "sign", title: "签名", sortable: true, width: 120, class: "user-list-ellipsis", formatter: imUserListFormatEllipsis },

            { field: "state", title: "状态", sortable: true, formatter: imUserListFormatState },

            { field: "balance", title: "余额", sortable: true },

            { field: "isvip", title: "是否会员", sortable: true, formatter: imUserListFormatYesNo },

            { field: "viptime", title: "vip到期时间", sortable: true },

            { field: "mbname", title: "密保问题" },

            { field: "mbda", title: "密保答案" },

            { field: "createDate", title: "注册时间", sortable: true },

            { field: "lastLoginDate", title: "最近一次登录时间", sortable: true },

            { field: "model", title: "手机型号", sortable: true },

            {

                field: "operate",

                title: "操作",

                align: "center",

                width: 450,

                formatter: function (value, row) {

                    var actions = [];

                    if (canView) {

                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imUserListOpenMember(\'' + row.id + '\',\'view\')"><i class="fa fa-eye"></i>查看</a>');

                    }

                    if (canEdit) {

                        actions.push('<a class="btn btn-primary btn-xs" href="javascript:void(0)" onclick="imUserListOpenMember(\'' + row.id + '\',\'edit\')"><i class="fa fa-edit"></i>修改</a>');

                    }

                    if (canChangeBalance) {

                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imUserListOpenBalance(\'' + row.id + '\')"><i class="fa fa-money"></i>变更余额</a>');

                    }

                    if (canTransaction) {

                        actions.push('<a class="btn btn-default btn-xs" href="javascript:void(0)" onclick="imUserListOpenTransaction(\'' + row.id + '\',\'' + String(row.idno || "").replace(/'/g, "\\'") + '\')"><i class="fa fa-list"></i>交易明细</a>');

                    }

                    if (canClear) {

                        actions.push('<a class="btn btn-warning btn-xs" href="javascript:void(0)" onclick="imUserListClearPwd(\'' + row.id + '\')"><i class="fa fa-eraser"></i>清除支付密码</a>');

                    }

                    if (canDelete) {

                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imUserListDelete(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');

                    }

                    return actions.join(" ");

                }

            }

        ]

    });

}

