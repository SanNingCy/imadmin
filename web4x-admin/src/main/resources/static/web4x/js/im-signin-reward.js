/**
 * IM 签到奖励配置（接口：/signset/signSet/*、/signset/signSet/item/*）
 */
var imSignSetApi = ctx + "signset/signSet";
var imSignSetItemApi = ctx + "signset/signSet/item";

function imSignSetQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    return imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
}

function imSignSetResolveEntity(res, key) {
    if (!res) {
        return {};
    }
    if (res[key]) {
        return res[key];
    }
    if (res.data && res.data[key]) {
        return res.data[key];
    }
    return {};
}

function imSignSetFormatMoney(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    return isNaN(num) ? value : num.toFixed(2);
}

function imSignSetResetMainModal() {
    $("#signset-id").val("");
    $("#signset-min").val("");
    $("#signset-max").val("");
    $("#signset-daymoney").val("");
}

function imSignSetFillMainModal(info) {
    $("#signset-id").val(info.id || "");
    $("#signset-min").val(info.min != null ? info.min : "");
    $("#signset-max").val(info.max != null ? info.max : "");
    $("#signset-daymoney").val(info.daymoney != null ? info.daymoney : "");
}

function imSignSetSetMainReadOnly(readOnly) {
    $("#signset-main-modal-form :input").prop("disabled", readOnly);
}

function imSignSetShowMainModal(mode, readOnly) {
    var titles = { edit: "修改签到奖励", view: "查看签到奖励" };
    layer.open({
        type: 1,
        title: titles[mode] || "签到奖励配置",
        area: ["520px", "320px"],
        shadeClose: true,
        content: $("#signset-main-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imSignSetSaveMain(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imSignSetOpenMainModal(mode, id) {
    var readOnly = String(mode) === "view";
    imSignSetResetMainModal();
    imSignSetSetMainReadOnly(readOnly);

    $.ajax({
        url: imSignSetApi + "/queryById",
        type: "GET",
        data: { id: id },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                imSignSetFillMainModal(imSignSetResolveEntity(res, "signSet"));
                imSignSetShowMainModal(mode, readOnly);
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
            }
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imSignSetSaveMain(layerIndex) {
    var min = $.trim($("#signset-min").val());
    var max = $.trim($("#signset-max").val());
    var daymoney = $.trim($("#signset-daymoney").val());
    if (!min || !max || !daymoney) {
        return $.modal.alertWarning("请填写完整配置");
    }

    $.ajax({
        url: imSignSetApi + "/save",
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
            id: $("#signset-id").val() || undefined,
            min: min,
            max: max,
            daymoney: daymoney,
            signSetItemList: []
        }),
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

function imSignSetResetItemModal() {
    $("#signset-item-id").val("");
    $("#signset-item-sid").val("");
    $("#signset-item-day").val("");
    $("#signset-item-money").val("");
}

function imSignSetFillItemModal(item, sid) {
    $("#signset-item-id").val(item.id || "");
    $("#signset-item-sid").val(sid || "");
    $("#signset-item-day").val(item.day != null ? item.day : "");
    $("#signset-item-money").val(item.money != null ? item.money : "");
}

function imSignSetShowItemModal(mode) {
    var titles = { add: "新增连签奖励", edit: "修改连签奖励" };
    layer.open({
        type: 1,
        title: titles[mode] || "连签奖励",
        area: ["480px", "260px"],
        shadeClose: true,
        content: $("#signset-item-modal"),
        btn: ["保存", "取消"],
        yes: function (index) {
            imSignSetSaveItem(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imSignSetOpenItemModal(mode, sid, item) {
    imSignSetResetItemModal();
    if (String(mode) === "edit" && item) {
        imSignSetFillItemModal(item, sid);
    } else {
        $("#signset-item-sid").val(sid);
    }
    imSignSetShowItemModal(mode);
}

function imSignSetSaveItem(layerIndex, mode) {
    var day = $.trim($("#signset-item-day").val());
    var money = $.trim($("#signset-item-money").val());
    var sid = $("#signset-item-sid").val();
    if (!sid) {
        return $.modal.alertWarning("缺少主配置ID");
    }
    if (!day || !money) {
        return $.modal.alertWarning("请填写连签天数和追加金额");
    }

    var payload = {
        id: $("#signset-item-id").val() || undefined,
        sid: { id: sid },
        day: Number(day),
        money: money
    };

    var isAdd = String(mode) === "add";
    $.ajax({
        url: isAdd ? imSignSetItemApi + "/save" : imSignSetItemApi + "/update",
        type: isAdd ? "POST" : "PUT",
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

function imSignSetDeleteItem(id) {
    $.modal.confirm("确定删除该连签奖励吗？", function () {
        $.ajax({
            url: imSignSetItemApi + "/delete",
            type: "DELETE",
            data: { id: id },
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

function imSignSetRenderDetail(index, row, $detail, canEdit) {
    $.ajax({
        url: imSignSetApi + "/queryById",
        type: "GET",
        data: { id: row.id },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (!(res && (res.success === true || res.code === 200))) {
                $detail.html('<div class="signset-detail-wrap text-danger">加载失败</div>');
                return;
            }
            var signSet = imSignSetResolveEntity(res, "signSet");
            var items = signSet.signSetItemList || [];
            var html = ['<div class="signset-detail-wrap">'];
            if (canEdit) {
                html.push('<div class="signset-detail-toolbar">');
                html.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imSignSetOpenItemModal(\'add\',\'' + row.id + '\')"><i class="fa fa-plus"></i> 新增连签奖励</a>');
                html.push("</div>");
            }
            html.push('<table class="signset-detail-table"><thead><tr>');
            html.push("<th>连签天数</th><th>追加金额</th>");
            if (canEdit) {
                html.push("<th>操作</th>");
            }
            html.push("</tr></thead><tbody>");
            if (!items.length) {
                html.push('<tr><td colspan="' + (canEdit ? 3 : 2) + '">暂无连签奖励</td></tr>');
            } else {
                items.forEach(function (item) {
                    html.push("<tr>");
                    html.push("<td>" + (item.day != null ? item.day : "-") + "</td>");
                    html.push("<td>" + imSignSetFormatMoney(item.money) + "</td>");
                    if (canEdit) {
                        var itemJson = encodeURIComponent(JSON.stringify(item));
                        html.push('<td>');
                        html.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imSignSetOpenItemModal(\'edit\',\'' + row.id + '\',JSON.parse(decodeURIComponent(\'' + itemJson + '\')))"><i class="fa fa-edit"></i>修改</a> ');
                        html.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imSignSetDeleteItem(\'' + item.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                        html.push("</td>");
                    }
                    html.push("</tr>");
                });
            }
            html.push("</tbody></table></div>");
            $detail.html(html.join(""));
        },
        error: function () {
            $detail.html('<div class="signset-detail-wrap text-danger">加载失败</div>');
        }
    });
}

function imSignSetInitTable(canView, canEdit) {
    imInitTable({
        url: imSignSetApi + "/list",
        modalName: "签到奖励配置",
        sortName: "updateDate",
        sortOrder: "desc",
        queryParams: imSignSetQueryParams,
        detailView: true,
        onExpandRow: function (index, row, $detail) {
            imSignSetRenderDetail(index, row, $detail, canEdit);
        },
        columns: [
            {
                field: "min",
                title: "最低奖励金额",
                sortable: true,
                formatter: function (value) {
                    return imSignSetFormatMoney(value);
                }
            },
            {
                field: "max",
                title: "最高奖励金额",
                sortable: true,
                formatter: function (value) {
                    return imSignSetFormatMoney(value);
                }
            },
            {
                field: "daymoney",
                title: "连签每日金额",
                sortable: true,
                formatter: function (value) {
                    return imSignSetFormatMoney(value);
                }
            },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imSignSetOpenMainModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imSignSetOpenMainModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}