/**
 * IM 全局敏感词（风控-安全管理）
 * - GET  /admin/sensitive/list
 * - POST /admin/sensitive/add
 * - POST /admin/sensitive/edit
 * - POST /admin/sensitive/del
 */

var imRiskSensitiveWordApi = ctx + "admin/sensitive";

function imRiskSensitiveWordQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("sensitive-word-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imRiskSensitiveWordParseWords(value) {
    var set = {};
    String(value || "")
        .split(/[\n,，]/)
        .forEach(function (item) {
            item = $.trim(item);
            if (item) set[item] = true;
        });
    return Object.keys(set);
}

function imRiskSensitiveWordEllipsis(val, max) {
    if (!val) return "-";
    var text = String(val);
    max = max || 40;
    if (text.length <= max) return text;
    return '<span title="' + text.replace(/"/g, "&quot;") + '">' + text.substring(0, max) + "...</span>";
}

function imRiskSensitiveWordResetModal() {
    $("#sw-id").val("");
    $("#sw-id-display").val("");
    $("#sw-word").val("");
    $("#sw-words").val("");
    $("#sw-createDate").val("");
    $("#sw-updateDate").val("");
}

function imRiskSensitiveWordToggleBlocks(mode) {
    if (mode === "add") {
        $("#sw-add-block").show();
        $("#sw-edit-block").hide();
    } else {
        $("#sw-add-block").hide();
        $("#sw-edit-block").show();
    }
}

function imRiskSensitiveWordSetReadOnly(readOnly) {
    $("#sensitive-word-modal-form :input").prop("disabled", readOnly);
    if (readOnly) {
        $("#sw-id-display, #sw-createDate, #sw-updateDate").prop("disabled", true);
    }
}

function imRiskSensitiveWordShowModal(mode, readOnly) {
    var titles = { add: "新增全局敏感词", edit: "修改全局敏感词", view: "查看全局敏感词" };
    layer.open({
        type: 1,
        title: titles[mode] || "全局敏感词",
        area: [mode === "add" ? "560px" : "520px", mode === "add" ? "420px" : "380px"],
        shadeClose: true,
        content: $("#sensitive-word-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imRiskSensitiveWordSave(index, mode);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imRiskSensitiveWordFindRow(id) {
    var rows = $("#bootstrap-table").bootstrapTable("getData") || [];
    for (var i = 0; i < rows.length; i++) {
        if (String(rows[i].id) === String(id)) {
            return rows[i];
        }
    }
    return null;
}

function imRiskSensitiveWordOpenById(mode, id) {
    var record = imRiskSensitiveWordFindRow(id);
    if (!record) {
        return $.modal.alertWarning("未找到记录");
    }
    imRiskSensitiveWordOpenModal(mode, record);
}

function imRiskSensitiveWordOpenModal(mode, record) {
    var readOnly = mode === "view";
    imRiskSensitiveWordResetModal();
    imRiskSensitiveWordToggleBlocks(mode);
    imRiskSensitiveWordSetReadOnly(readOnly);

    if (record) {
        $("#sw-id").val(record.id || "");
        $("#sw-id-display").val(record.id || "");
        $("#sw-word").val(record.word || "");
        $("#sw-createDate").val(record.createDate || "");
        $("#sw-updateDate").val(record.updateDate || "");
    }

    imRiskSensitiveWordShowModal(mode, readOnly);
}

function imRiskSensitiveWordSave(layerIndex, mode) {
    if (mode === "add") {
        var words = imRiskSensitiveWordParseWords($("#sw-words").val());
        if (!words.length) return $.modal.alertWarning("请输入敏感词");
        $.ajax({
            url: imRiskSensitiveWordApi + "/add",
            type: "POST",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify({ words: words }),
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "新增成功");
                    layer.close(layerIndex);
                    $("#bootstrap-table").bootstrapTable("refresh");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "新增失败");
                }
            },
            error: function () {
                $.modal.alertWarning("新增失败");
            }
        });
        return;
    }

    var word = $.trim($("#sw-word").val());
    var id = $("#sw-id").val();
    if (!id) return $.modal.alertWarning("ID不能为空");
    if (!word) return $.modal.alertWarning("敏感词不能为空");

    $.ajax({
        url: imRiskSensitiveWordApi + "/edit",
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({ id: id, word: word }),
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess((res && res.msg) ? res.msg : "修改成功");
                layer.close(layerIndex);
                $("#bootstrap-table").bootstrapTable("refresh");
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "修改失败");
            }
        },
        error: function () {
            $.modal.alertWarning("修改失败");
        }
    });
}

function imRiskSensitiveWordRemove(word) {
    $.modal.confirm("确定删除该敏感词吗？", function () {
        $.ajax({
            url: imRiskSensitiveWordApi + "/del",
            type: "POST",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify({ words: [word] }),
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

function imRiskSensitiveWordInitTable(canView, canEdit, canDelete) {
    imInitTable({
        url: imRiskSensitiveWordApi + "/list",
        formId: "sensitive-word-form",
        queryParams: imRiskSensitiveWordQueryParams,
        responseHandler: imPageResponse,
        modalName: "全局敏感词",
        columns: [
            { field: "id", title: "ID", sortable: true },
            { field: "word", title: "敏感词", sortable: true, formatter: function (v) { return imRiskSensitiveWordEllipsis(v, 50); } },
            { field: "createDate", title: "创建时间", sortable: true },
            { field: "updateDate", title: "更新时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imRiskSensitiveWordOpenById(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imRiskSensitiveWordOpenById(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete && row.word) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imRiskSensitiveWordRemove(' + JSON.stringify(String(row.word)) + ')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.length ? actions.join("") : "-";
                }
            }
        ]
    });
}
