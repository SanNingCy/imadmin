/**
 * IM 常见问题（接口复用 web4x-im）
 * - GET    /faq/faq/list
 * - GET    /faq/faq/queryById?id=xxx
 * - POST   /faq/faq/save
 * - DELETE /faq/faq/delete?ids=1,2
 */

var imFaqApi = ctx + "faq/faq";

var IM_FAQ_LANG_OPTIONS = [
    { label: "英语", value: "en" },
    { label: "简体中文", value: "zh-CN" },
    { label: "繁体中文", value: "zh-TW" },
    { label: "印地语", value: "hi-IN" },
    { label: "西班牙语", value: "es-ES" },
    { label: "法语", value: "fr-FR" },
    { label: "阿拉伯语", value: "ar-EG" },
    { label: "孟加拉语", value: "bn-IN" },
    { label: "俄语", value: "ru-RU" },
    { label: "葡萄牙语", value: "pt-BR" },
    { label: "印尼语", value: "id-ID" },
    { label: "乌尔都语", value: "ur-PK" },
    { label: "日语", value: "ja-JP" },
    { label: "德语", value: "de-DE" },
    { label: "旁遮普语", value: "pa-IN" },
    { label: "爪哇语", value: "jv-ID" },
    { label: "菲律宾语", value: "in-ID" },
    { label: "越南语", value: "vi-VN" },
    { label: "韩语", value: "ko-KR" },
    { label: "土耳其语", value: "tr-TR" },
    { label: "意大利语", value: "it-IT" },
    { label: "泰语", value: "th-TH" },
    { label: "马来语", value: "ms-MY" },
    { label: "波兰语", value: "pl-PL" },
    { label: "罗马尼亚语", value: "ro-RO" },
    { label: "老挝语", value: "lo-LA" },
    { label: "蒙古语", value: "mn-MN" },
    { label: "豪萨语", value: "ha-NG" },
    { label: "斯瓦希里语", value: "sw-KE" },
    { label: "缅甸语", value: "my-MM" },
    { label: "尼泊尔语", value: "ne-NP" },
    { label: "祖鲁语", value: "zu-ZA" },
    { label: "希伯来语", value: "he-IL" },
    { label: "希腊语", value: "el-GR" },
    { label: "乌克兰语", value: "uk-UA" }
];

var IM_FAQ_LANG_LABEL_MAP = {};
IM_FAQ_LANG_OPTIONS.forEach(function (item) {
    IM_FAQ_LANG_LABEL_MAP[item.value] = item.label;
});

function imFaqQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("faq-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imFaqEllipsis(val, max) {
    if (!val) return "";
    var text = String(val);
    max = max || 40;
    var short = text.length > max ? text.substring(0, max) + "..." : text;
    return '<span title="' + text.replace(/"/g, "&quot;") + '">' + short + "</span>";
}

function imFaqResolveEntity(res, key) {
    if (!res) return {};
    if (res[key]) return res[key];
    if (res.data && res.data[key]) return res.data[key];
    return {};
}

function imFaqFillLangSelect($select, includeAll) {
    $select.empty();
    if (includeAll) {
        $select.append('<option value="">所有</option>');
    }
    IM_FAQ_LANG_OPTIONS.forEach(function (item) {
        $select.append('<option value="' + item.value + '">' + item.label + " (" + item.value + ")</option>");
    });
}

function imFaqFormatLang(value) {
    if (!value) return "-";
    var label = IM_FAQ_LANG_LABEL_MAP[value];
    return label ? label + " " : value;
}

function imFaqResetModalForm() {
    $("#faq-id").val("");
    $("#faq-title").val("");
    $("#faq-sort").val("");
    $("#faq-lang").val("");
    $("#faq-content").val("");
}

function imFaqFillModal(info) {
    $("#faq-id").val(info.id || "");
    $("#faq-title").val(info.title || "");
    $("#faq-sort").val(info.sort != null ? info.sort : "");
    $("#faq-lang").val(info.lang || "");
    $("#faq-content").val(info.content || "");
}

function imFaqSetModalReadOnly(readOnly) {
    $("#faq-modal-form :input").prop("disabled", readOnly);
}

function imFaqShowLayerModal(mode, readOnly) {
    var titles = { add: "新建FAQ", edit: "修改FAQ", view: "查看FAQ" };
    layer.open({
        type: 1,
        title: titles[mode] || "常见问题",
        area: ["560px", "520px"],
        shadeClose: true,
        content: $("#faq-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imFaqSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imFaqOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    imFaqResetModalForm();
    imFaqSetModalReadOnly(readOnly);

    if (id) {
        $.ajax({
            url: imFaqApi + "/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    imFaqFillModal(imFaqResolveEntity(res, "faq"));
                    imFaqShowLayerModal(mode, readOnly);
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
            }
        });
    } else {
        imFaqShowLayerModal(mode, readOnly);
    }
}

function imFaqSave(layerIndex) {
    var title = $.trim($("#faq-title").val());
    if (!title) return $.modal.alertWarning("标题不能为空");

    var sortVal = $("#faq-sort").val();
    var payload = {
        id: $("#faq-id").val() || undefined,
        title: title,
        sort: sortVal !== "" ? parseInt(sortVal, 10) : null,
        lang: $("#faq-lang").val() || null,
        content: $("#faq-content").val()
    };

    $.ajax({
        url: imFaqApi + "/save",
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

function imFaqRemove(ids) {
    $.modal.confirm("确定删除选中的记录吗？", function () {
        $.ajax({
            url: imFaqApi + "/delete",
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

function imFaqRemoveSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || !rows.length) {
        return $.modal.alertWarning("请至少选择一条记录");
    }
    imFaqRemove(rows.map(function (row) { return row.id; }).join(","));
}

function imFaqEditSelected() {
    var rows = $("#bootstrap-table").bootstrapTable("getSelections");
    if (!rows || rows.length !== 1) {
        return $.modal.alertWarning("请选择一条记录");
    }
    imFaqOpenModal("edit", rows[0].id);
}

function imFaqInitTable(canView, canEdit, canDelete) {
    imFaqFillLangSelect($("#faq-search-lang"), true);
    imFaqFillLangSelect($("#faq-lang"), false);

    imInitTable({
        url: imFaqApi + "/list",
        formId: "faq-form",
        queryParams: imFaqQueryParams,
        responseHandler: imPageResponse,
        modalName: "常见问题",
        columns: [
            { checkbox: true },
            {
                field: "title",
                title: "标题",
                sortable: true,
                formatter: function (value, row) {
                    if (!canView) return imFaqEllipsis(value, 30);
                    return '<a href="javascript:void(0)" onclick="imFaqOpenModal(\'view\',\'' + row.id + '\')">' + imFaqEllipsis(value, 30) + "</a>";
                }
            },
            { field: "sort", title: "排序", sortable: true },
            {
                field: "lang",
                title: "语言编码",
                sortable: true,
                formatter: function (v) { return imFaqFormatLang(v); }
            },
            { field: "updateDate", title: "更新时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imFaqOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imFaqOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imFaqRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    });
}
