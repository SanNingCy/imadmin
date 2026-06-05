/**
 * IM 投诉原因配置（接口复用 web4x-im）
 * - GET    /reason/reason/list
 * - GET    /reason/reason/queryById?id=xxx
 * - POST   /reason/reason/save
 * - DELETE /reason/reason/delete?ids=1,2
 */

var imReasonApi = ctx + "reason/reason";

function imReasonQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("reason-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imReasonEllipsis(val, max) {
    if (!val) return "";
    var text = String(val);
    max = max || 40;
    var short = text.length > max ? text.substring(0, max) + "..." : text;
    return '<span title="' + text.replace(/"/g, "&quot;") + '">' + short + "</span>";
}

function imReasonResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imReasonResetModalForm() {
    $("#reason-id").val("");
    $("#reason-title").val("");
}

function imReasonFillModal(info) {
    $("#reason-id").val(info.id || "");
    $("#reason-title").val(info.title || "");
}

function imReasonSetModalReadOnly(readOnly) {
    $("#reason-modal-form :input").prop("disabled", readOnly);
}

function imReasonShowLayerModal(mode, readOnly) {
    var titles = { add: "新建原因", edit: "修改原因", view: "查看原因" };
    layer.open({
        type: 1,
        title: titles[mode] || "投诉原因",
        area: ["480px", "220px"],
        shadeClose: true,
        content: $("#reason-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imReasonSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imReasonOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imReasonResetModalForm();
    imReasonSetModalReadOnly(readOnly);

    if (id) {
        $.ajax({
            url: imReasonApi + "/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imReasonFillModal(imReasonResolveEntity(res, "reason"));
                    imReasonShowLayerModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imReasonShowLayerModal(mode, readOnly);
    }
}

function imReasonSave(layerIndex) {
    var title = $.trim($("#reason-title").val());
    if (!title) return $.modal.alertWarning("原因不能为空");

    var payload = {
        id: $("#reason-id").val() || undefined,
        title: title
    };

    $.ajax({
        url: imReasonApi + "/save",
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

function imReasonRemove(ids) {
    $.modal.confirm("确定删除选中的记录吗？", function () {
        $.ajax({
            url: imReasonApi + "/delete",
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

function imReasonRemoveSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || !rows.length) {
        return $.modal.alertWarning("请至少选择一条记录");
    }
    imReasonRemove(rows.map(function (row) { return row.id; }).join(","));
}

function imReasonEditSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || rows.length !== 1) {
        return $.modal.alertWarning("请选择一条记录");
    }
    imReasonOpenModal("edit", rows[0].id);
}

function imReasonInitTable(canView, canEdit, canDelete) {
    imInitTable({
        url: imReasonApi + "/list",
        formId: "reason-form",
        queryParams: imReasonQueryParams,
        responseHandler: imPageResponse,
        modalName: "投诉原因",
        columns: [
            { checkbox: true },
            {
                field: "title",
                title: "原因",
                sortable: true,
                formatter: function (value, row) {
                    if (!canView) return imReasonEllipsis(value, 40);
                    return '<a href="javascript:void(0)" onclick="imReasonOpenModal(\'view\',\'' + row.id + '\')">' + imReasonEllipsis(value, 40) + "</a>";
                }
            },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imReasonOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imReasonOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imReasonRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
