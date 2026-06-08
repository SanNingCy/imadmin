/**
 * IM 会员码管理
 * - GET    /vipcode/vipCode/list
 * - GET    /vipcode/vipCode/queryById?id=
 * - GET    /vipcode/vipCode/getKeyCardType
 * - POST   /vipcode/vipCode/save
 * - POST   /vipcode/vipCode/batchSheng
 * - POST   /vipcode/vipCode/syncFromIm
 * - DELETE /vipcode/vipCode/delete?ids=
 */

var imSecurityVipCodeApi = ctx + "vipcode/vipCode";
var imSecurityVipCodeKeyCardTypes = [];

function imSecurityVipCodeQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("vip-code-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imSecurityVipCodeResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imSecurityVipCodeResolveList(res) {
    if (!res) return [];
    if ($.isArray(res)) return res;
    if ($.isArray(res.data)) return res.data;
    if (res.data && $.isArray(res.data.list)) return res.data.list;
    return [];
}

function imSecurityVipCodeFormatYesNo(val) {
    if (String(val) === "1") return "是";
    if (String(val) === "0") return "否";
    return "-";
}

function imSecurityVipCodeFormatSyncStatus(val) {
    if (String(val) === "1") return "已同步";
    if (String(val) === "0") return "未同步/失败";
    return "-";
}

function imSecurityVipCodeFillTypeSelect($select, includeAll) {
    $select.empty();
    if (includeAll) {
        $select.append('<option value="">全部</option>');
    } else {
        $select.append('<option value="">请选择类型</option>');
    }
    imSecurityVipCodeKeyCardTypes.forEach(function (item) {
        $select.append('<option value="' + item.id + '">' + item.typeName + "</option>");
    });
}

function imSecurityVipCodeLoadKeyCardTypes(callback) {
    $.ajax({
        url: imSecurityVipCodeApi + "/getKeyCardType",
        type: "GET",
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                imSecurityVipCodeKeyCardTypes = imSecurityVipCodeResolveList(res);
                imSecurityVipCodeFillTypeSelect($("#vip-code-search-type"), true);
                imSecurityVipCodeFillTypeSelect($("#vip-code-type"), false);
                imSecurityVipCodeFillTypeSelect($("#vip-code-batch-type"), false);
                if (typeof callback === "function") callback();
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "加载会员卡类型失败");
            }
        },
        error: function () {
            $.modal.alertWarning("加载会员卡类型失败");
        }
    });
}

function imSecurityVipCodeFindType(id) {
    var found = null;
    imSecurityVipCodeKeyCardTypes.forEach(function (item) {
        if (String(item.id) === String(id)) found = item;
    });
    return found;
}

function imSecurityVipCodeResetModal() {
    $("#vip-code-id").val("");
    $("#vip-code-code").val("");
    $("#vip-code-day").val("");
    $("#vip-code-isdui").val("0");
    $("#vip-code-type").val("");
}

function imSecurityVipCodeFillModal(info) {
    $("#vip-code-id").val(info.id || "");
    $("#vip-code-code").val(info.code || "");
    $("#vip-code-day").val(info.day != null ? info.day : "");
    $("#vip-code-isdui").val(info.isdui != null ? String(info.isdui) : "0");
    $("#vip-code-type").val(info.type != null ? info.type : "");
    if (String(info.isdui) === "1") {
        $("#vip-code-isdui").prop("disabled", true);
    } else {
        $("#vip-code-isdui").prop("disabled", false);
    }
}

function imSecurityVipCodeSetModalReadOnly(readOnly) {
    $("#vip-code-modal-form :input").prop("disabled", readOnly);
    if (!readOnly && String($("#vip-code-isdui").val()) === "1") {
        $("#vip-code-isdui").prop("disabled", true);
    }
}

function imSecurityVipCodeShowModal(mode, readOnly) {
    var titles = { add: "新建会员码", edit: "编辑会员码", view: "查看会员码" };
    layer.open({
        type: 1,
        title: titles[mode] || "会员码",
        area: ["520px", "360px"],
        shadeClose: true,
        content: $("#vip-code-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imSecurityVipCodeSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imSecurityVipCodeOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imSecurityVipCodeResetModal();
    imSecurityVipCodeSetModalReadOnly(readOnly);

    var open = function () {
        if (id) {
            $.ajax({
                url: imSecurityVipCodeApi + "/queryById",
                type: "GET",
                data: { id: id },
                dataType: "json",
                beforeSend: imTableBeforeSend,
                success: function (res) {
                    if (res && (res.success === true || res.code === 200)) {
                        imSecurityVipCodeFillModal(imSecurityVipCodeResolveEntity(res, "vipCode"));
                        imSecurityVipCodeSetModalReadOnly(readOnly);
                        imSecurityVipCodeShowModal(mode, readOnly);
                    } else {
                        $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                    }
                },
                error: function () {
                    $.modal.alertWarning("获取详情失败");
                }
            });
        } else {
            imSecurityVipCodeShowModal(mode, readOnly);
        }
    };

    if (!imSecurityVipCodeKeyCardTypes.length) {
        imSecurityVipCodeLoadKeyCardTypes(open);
    } else {
        open();
    }
}

function imSecurityVipCodeSave(layerIndex) {
    var code = $.trim($("#vip-code-code").val());
    var day = $("#vip-code-day").val();
    var type = $("#vip-code-type").val();
    if (!code) return $.modal.alertWarning("兑换码不能为空");
    if (!day) return $.modal.alertWarning("会员天数不能为空");
    if (!type) return $.modal.alertWarning("请选择类型");

    var typeItem = imSecurityVipCodeFindType(type);
    var payload = {
        id: $("#vip-code-id").val() || undefined,
        code: code,
        day: String(day),
        isdui: $("#vip-code-isdui").val() || "0",
        syncStatus: "0",
        type: String(type),
        typeName: typeItem ? typeItem.typeName : ""
    };

    $.ajax({
        url: imSecurityVipCodeApi + "/save",
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

function imSecurityVipCodeOpenBatchModal() {
    var open = function () {
        $("#vip-code-batch-count").val("");
        $("#vip-code-batch-day").val("");
        $("#vip-code-batch-type").val("");
        layer.open({
            type: 1,
            title: "批量生成会员码",
            area: ["520px", "320px"],
            shadeClose: true,
            content: $("#vip-code-batch-modal"),
            btn: ["保存", "取消"],
            yes: function (index) {
                imSecurityVipCodeBatchSave(index);
            },
            btn2: function (index) {
                layer.close(index);
            }
        });
    };
    if (!imSecurityVipCodeKeyCardTypes.length) {
        imSecurityVipCodeLoadKeyCardTypes(open);
    } else {
        open();
    }
}

function imSecurityVipCodeBatchSave(layerIndex) {
    var count = $("#vip-code-batch-count").val();
    var day = $("#vip-code-batch-day").val();
    var type = $("#vip-code-batch-type").val();
    if (!count) return $.modal.alertWarning("生成数量不能为空");
    if (!day) return $.modal.alertWarning("会员天数不能为空");
    if (!type) return $.modal.alertWarning("请选择类型");

    var typeItem = imSecurityVipCodeFindType(type);
    var payload = {
        count: parseInt(count, 10),
        day: parseInt(day, 10),
        type: String(type),
        typeName: typeItem ? typeItem.typeName : "",
        syncStatus: "0"
    };

    $.ajax({
        url: imSecurityVipCodeApi + "/batchSheng",
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess(res.msg || "生成成功");
                layer.close(layerIndex);
                $("#bootstrap-table").bootstrapTable("refresh");
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "生成失败");
            }
        },
        error: function () {
            $.modal.alertWarning("生成失败");
        }
    });
}

function imSecurityVipCodeSyncFromIm() {
    $.modal.confirm("将同步会员码到链桥平台，单次最多同步 500 条。确定继续吗？", function () {
        $.ajax({
            url: imSecurityVipCodeApi + "/syncFromIm",
            type: "POST",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify([]),
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess(res.msg || "同步成功");
                    $("#bootstrap-table").bootstrapTable("refresh");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "同步失败");
                }
            },
            error: function () {
                $.modal.alertWarning("同步失败");
            }
        });
    });
}

function imSecurityVipCodeRemove(id) {
    $.modal.confirm("确定删除该记录吗？", function () {
        $.ajax({
            url: imSecurityVipCodeApi + "/delete",
            type: "DELETE",
            data: { ids: id },
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

function imSecurityVipCodeInitTable(canView, canEdit, canDelete) {
    imSecurityVipCodeLoadKeyCardTypes(function () {
        imInitTable({
            url: imSecurityVipCodeApi + "/list",
            formId: "vip-code-form",
            queryParams: imSecurityVipCodeQueryParams,
            responseHandler: imPageResponse,
            modalName: "会员码",
            columns: [
                { field: "typeName", title: "类型名称", sortable: true },
                { field: "code", title: "兑换码", sortable: true },
                { field: "day", title: "会员天数", sortable: true },
                {
                    field: "isdui",
                    title: "是否已被兑换",
                    sortable: true,
                    formatter: function (v) { return imSecurityVipCodeFormatYesNo(v); }
                },
                {
                    field: "u.idno",
                    title: "用户",
                    sortable: true,
                    formatter: function (value, row) {
                        return row.u && row.u.nickname ? row.u.nickname : "-";
                    }
                },
                { field: "idno", title: "用户ID", sortable: true },
                {
                    field: "syncStatus",
                    title: "同步状态",
                    formatter: function (v) { return imSecurityVipCodeFormatSyncStatus(v); }
                },
                { field: "duiTime", title: "兑换时间", sortable: true },
                { field: "createDate", title: "添加时间", sortable: true },
                {
                    title: "操作",
                    align: "center",
                    formatter: function (value, row) {
                        var actions = [];
                        if (canView) {
                            actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imSecurityVipCodeOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                        }
                        if (canEdit) {
                            actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imSecurityVipCodeOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>编辑</a> ');
                        }
                        if (canDelete) {
                            actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imSecurityVipCodeRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                        }
                        return actions.length ? actions.join("") : "-";
                    }
                }
            ]
        });
    });
}
