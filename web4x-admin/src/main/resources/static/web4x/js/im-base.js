/**
 * IM 基础平台配置
 * - GET  /customer/customer/list
 * - GET  /customer/customer/queryById?id=
 * - POST /customer/customer/save
 */

var imBaseApi = ctx + "customer/customer";
var imBasePreserved = {};

function imBaseQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    return imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
}

function imBaseYesNo(val) {
    return String(val) === "1" ? "是" : "否";
}

function imBaseLoginType(val) {
    var map = { "1": "设备号登录", "2": "用户名密码登录", "3": "手机号密码登录", "4": "手机号、用户名密码登录" };
    return map[String(val)] || "-";
}

function imBaseSignType(val) {
    var map = { "1": "随机发放", "2": "连签发放" };
    return map[String(val)] || "-";
}

function imBaseResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imBaseInitYesNoSelects() {
    $(".base-yesno").each(function () {
        var $sel = $(this);
        if ($sel.children().length) return;
        $sel.append('<option value="">请选择</option><option value="1">是</option><option value="0">否</option>');
    });
}

function imBaseResetModal() {
    $("#base-id").val("");
    $("#base-modal-form :input").not("[readonly]").val("");
    imBasePreserved = {};
}

function imBaseCollectPreserved(info) {
    imBasePreserved = {
        robname: info.robname,
        mintixian: info.mintixian,
        hasInvit: info.hasInvit,
        showFx: info.showFx,
        showXcx: info.showXcx,
        openBj: info.openBj,
        openCh: info.openCh,
        sname: info.sname,
        sno: info.sno,
        skm: info.skm,
        hongbao: info.hongbao,
        zhuanzhang: info.zhuanzhang,
        tx1: info.tx1,
        tx2: info.tx2,
        tx3: info.tx3,
        cz: info.cz,
        czimg: info.czimg,
        sendadd: info.sendadd,
        showkf: info.showkf,
        kfurl: info.kfurl,
        showonline: info.showonline,
        showyidu: info.showyidu,
        showmsgtime: info.showmsgtime,
        topsyan: info.topsyan,
        openneibu: info.openneibu,
        ipwhite: info.ipwhite,
        changeeq: info.changeeq,
        ipcount: info.ipcount,
        regimgtype: info.regimgtype,
        regimg: info.regimg,
        regvideo: info.regvideo,
        reginfo: info.reginfo,
        newadd: info.newadd,
        serchmin: info.serchmin,
        serchnum: info.serchnum,
        mgc: info.mgc
    };
}

function imBaseFillModal(info) {
    imBaseCollectPreserved(info || {});
    $("#base-id").val(info.id || "");
    $("#base-loginType").val(info.loginType != null ? String(info.loginType) : "");
    $("#base-openMibao").val(info.openMibao != null ? String(info.openMibao) : "");
    $("#base-openhb").val(info.openhb != null ? String(info.openhb) : "");
    $("#base-openzz").val(info.openzz != null ? String(info.openzz) : "");
    $("#base-tonghua").val(info.tonghua != null ? String(info.tonghua) : "");
    $("#base-namemgc").val(info.namemgc || "");
    $("#base-showsign").val(info.showsign != null ? String(info.showsign) : "");
    $("#base-showqianbao").val(info.showqianbao != null ? String(info.showqianbao) : "");
    $("#base-eqcount").val(info.eqcount != null ? info.eqcount : "");
    $("#base-aikey").val(info.aikey || "");
    $("#base-filetype").val(info.filetype || "");
    $("#base-signtype").val(info.signtype != null ? String(info.signtype) : "");
    $("#base-line").val(info.line != null ? String(info.line) : "");
    $("#base-maxadd").val(info.maxadd != null ? info.maxadd : "");
    $("#base-createDate").val(info.createDate || info.create_date || "");
    $("#base-updateDate").val(info.updateDate || info.update_date || "");
}

function imBaseSetReadOnly(readOnly) {
    $("#base-modal-form :input").not("#base-createDate,#base-updateDate").prop("disabled", readOnly);
}

function imBaseShowModal(mode, readOnly) {
    layer.open({
        type: 1,
        title: mode === "edit" ? "修改基础配置" : "查看基础配置",
        area: ["960px", "90%"],
        shadeClose: true,
        content: $("#base-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imBaseSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imBaseOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imBaseResetModal();
    imBaseInitYesNoSelects();
    imBaseSetReadOnly(readOnly);

    if (id) {
        $.ajax({
            url: imBaseApi + "/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imBaseFillModal(imBaseResolveEntity(res, "customer"));
                    imBaseShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imBaseShowModal(mode, readOnly);
    }
}

function imBaseSave(layerIndex) {
    var id = $("#base-id").val();
    if (!id) return $.modal.alertWarning("缺少记录ID");

    var payload = $.extend({}, imBasePreserved, {
        id: id,
        loginType: $("#base-loginType").val(),
        openMibao: $("#base-openMibao").val(),
        openhb: $("#base-openhb").val(),
        openzz: $("#base-openzz").val(),
        tonghua: $("#base-tonghua").val(),
        namemgc: $("#base-namemgc").val(),
        showsign: $("#base-showsign").val(),
        showqianbao: $("#base-showqianbao").val(),
        eqcount: $("#base-eqcount").val() !== "" ? parseInt($("#base-eqcount").val(), 10) : null,
        aikey: $("#base-aikey").val(),
        filetype: $("#base-filetype").val(),
        signtype: $("#base-signtype").val(),
        line: $("#base-line").val(),
        maxadd: $("#base-maxadd").val() !== "" ? parseInt($("#base-maxadd").val(), 10) : null
    });

    $.ajax({
        url: imBaseApi + "/save",
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess(res.msg || "保存成功");
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

function imBaseInitTable(canView, canEdit) {
    imBaseInitYesNoSelects();
    imInitTable({
        url: imBaseApi + "/list",
        formId: "base-form",
        queryParams: imBaseQueryParams,
        responseHandler: imPageResponse,
        modalName: "基础配置",
        columns: [
            { field: "loginType", title: "登录方式", sortable: true, formatter: function (v) { return imBaseLoginType(v); } },
            { field: "openMibao", title: "密保问题", sortable: true, formatter: imBaseYesNo },
            { field: "openhb", title: "红包功能", sortable: true, formatter: imBaseYesNo },
            { field: "openzz", title: "转账功能", sortable: true, formatter: imBaseYesNo },
            { field: "tonghua", title: "语音通话", sortable: true, formatter: imBaseYesNo },
            { field: "namemgc", title: "昵称敏感词", sortable: true },
            { field: "showsign", title: "签到红包", sortable: true, formatter: imBaseYesNo },
            { field: "showqianbao", title: "我的钱包", sortable: true, formatter: imBaseYesNo },
            { field: "eqcount", title: "设备注册数", sortable: true },
            { field: "aikey", title: "智能客服key", sortable: true },
            { field: "filetype", title: "文件类型", sortable: true },
            { field: "signtype", title: "签到方式", sortable: true, formatter: imBaseSignType },
            { field: "line", title: "是否上线", sortable: true, formatter: imBaseYesNo },
            { field: "maxadd", title: "最多可加人", sortable: true },
            { field: "createDate", title: "添加时间", sortable: true },
            { field: "updateDate", title: "更新时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imBaseOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imBaseOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
