/**
 * IM 密保问题管理
 * - GET    /mibaofaq/mibaoFaq/list
 * - GET    /mibaofaq/mibaoFaq/queryById?id=
 * - POST   /mibaofaq/mibaoFaq/save
 * - DELETE /mibaofaq/mibaoFaq/delete?ids=
 */

var imSecurityQaApi = ctx + "mibaofaq/mibaoFaq";

function imSecurityQaQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("qa-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imSecurityQaResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imSecurityQaResetModal() {
    $("#qa-id").val("");
    $("#qa-sort").val("");
    $("#qa-title").val("");
}

function imSecurityQaFillModal(info) {
    $("#qa-id").val(info.id || "");
    $("#qa-sort").val(info.sort != null ? info.sort : "");
    $("#qa-title").val(info.title || "");
}

function imSecurityQaSetModalReadOnly(readOnly) {
    $("#qa-modal-form :input").prop("disabled", readOnly);
}

function imSecurityQaShowModal(mode, readOnly) {
    var titles = { add: "新建密保问题", edit: "修改密保问题", view: "查看密保问题" };
    layer.open({
        type: 1,
        title: titles[mode] || "密保问题",
        area: ["480px", "260px"],
        shadeClose: true,
        content: $("#qa-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imSecurityQaSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imSecurityQaOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imSecurityQaResetModal();
    imSecurityQaSetModalReadOnly(readOnly);

    if (id) {
        $.ajax({
            url: imSecurityQaApi + "/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imSecurityQaFillModal(imSecurityQaResolveEntity(res, "mibaoFaq"));
                    imSecurityQaShowModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imSecurityQaShowModal(mode, readOnly);
    }
}

function imSecurityQaSave(layerIndex) {
    var title = $.trim($("#qa-title").val());
    if (!title) return $.modal.alertWarning("问题不能为空");

    var sortVal = $("#qa-sort").val();
    var payload = {
        id: $("#qa-id").val() || undefined,
        sort: sortVal !== "" ? sortVal : undefined,
        title: title
    };

    $.ajax({
        url: imSecurityQaApi + "/save",
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

function imSecurityQaRemove(id) {
    $.modal.confirm("确定删除该记录吗？", function () {
        $.ajax({
            url: imSecurityQaApi + "/delete",
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

function imSecurityQaInitTable(canView, canEdit, canDelete) {
    imInitTable({
        url: imSecurityQaApi + "/list",
        formId: "qa-form",
        queryParams: imSecurityQaQueryParams,
        responseHandler: imPageResponse,
        modalName: "密保问题",
        columns: [
            { field: "sort", title: "排序", sortable: true },
            { field: "title", title: "问题", sortable: true },
            { field: "updateDate", title: "更新时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imSecurityQaOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imSecurityQaOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imSecurityQaRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.length ? actions.join("") : "-";
                }
            }
        ]
    });
}
