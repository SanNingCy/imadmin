/**
 * IM 系统字典（/sys/dict）
 * - GET    /sys/dict/type/list
 * - GET    /sys/dict/queryById?id=
 * - POST   /sys/dict/save
 * - DELETE /sys/dict/delete?ids=
 * - GET    /sys/dict/getDictValue?dictTypeId=
 * - GET    /sys/dict/queryDictValue?dictValueId=
 * - POST   /sys/dict/saveDictValue
 * - DELETE /sys/dict/deleteDictValue?ids=
 * - GET    /sys/dict/listData
 * - GET    /sys/dict/getDictMap
 */
var imDictApi = ctx + "sys/dict";

function imDictGetUrlParam(name) {
    var match = new RegExp("[?&]" + name + "=([^&#]*)").exec(window.location.search);
    return match ? decodeURIComponent(match[1].replace(/\+/g, " ")) : null;
}

function imDictAjax(options) {
    options = options || {};
    var originBeforeSend = options.beforeSend;
    options.beforeSend = function (xhr) {
        if (typeof imTableBeforeSend === "function") {
            imTableBeforeSend(xhr);
        }
        if (typeof originBeforeSend === "function") {
            originBeforeSend(xhr);
        }
    };
    return $.ajax(options);
}

function imDictResolveEntity(res, key) {
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

function imDictIsSuccess(res) {
    return !!(res && (res.success === true || res.code === 200));
}

function imDictQueryParams(formId, params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON(formId);
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imDictFormatDate(val) {
    if (!val) {
        return "-";
    }
    return String(val).replace("T", " ").substring(0, 19);
}

function imDictSaveType(payload, done) {
    imDictAjax({
        url: imDictApi + "/save",
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
        dataType: "json",
        success: function (res) {
            if (imDictIsSuccess(res)) {
                if (typeof done === "function") {
                    done(true, res);
                }
            } else if (typeof done === "function") {
                done(false, res);
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "保存失败");
            }
        },
        error: function () {
            if (typeof done === "function") {
                done(false, null);
            } else {
                $.modal.alertWarning("保存失败");
            }
        }
    });
}

function imDictSaveValue(payload, done) {
    imDictAjax({
        url: imDictApi + "/saveDictValue",
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
        dataType: "json",
        success: function (res) {
            if (imDictIsSuccess(res)) {
                if (typeof done === "function") {
                    done(true, res);
                }
            } else if (typeof done === "function") {
                done(false, res);
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "保存失败");
            }
        },
        error: function () {
            if (typeof done === "function") {
                done(false, null);
            } else {
                $.modal.alertWarning("保存失败");
            }
        }
    });
}

function imDictRemoveTypes(ids, done) {
    imDictAjax({
        url: imDictApi + "/delete",
        type: "DELETE",
        data: { ids: ids },
        dataType: "json",
        success: function (res) {
            if (imDictIsSuccess(res)) {
                if (typeof done === "function") {
                    done(true, res);
                } else {
                    $.modal.msgSuccess(res.msg || "删除成功");
                }
            } else if (typeof done === "function") {
                done(false, res);
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "删除失败");
            }
        },
        error: function () {
            if (typeof done === "function") {
                done(false, null);
            } else {
                $.modal.alertWarning("删除失败");
            }
        }
    });
}

function imDictRemoveValues(ids, done) {
    imDictAjax({
        url: imDictApi + "/deleteDictValue",
        type: "DELETE",
        data: { ids: ids },
        dataType: "json",
        success: function (res) {
            if (imDictIsSuccess(res)) {
                if (typeof done === "function") {
                    done(true, res);
                } else {
                    $.modal.msgSuccess(res.msg || "删除成功");
                }
            } else if (typeof done === "function") {
                done(false, res);
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "删除失败");
            }
        },
        error: function () {
            if (typeof done === "function") {
                done(false, null);
            } else {
                $.modal.alertWarning("删除失败");
            }
        }
    });
}

function imDictLoadTypeOptions($select, selectedId, includeAll, done) {
    if (!$select || !$select.length) {
        if (typeof done === "function") {
            done();
        }
        return;
    }
    imDictAjax({
        url: imDictApi + "/type/list",
        type: "GET",
        data: { pageNo: 1, pageSize: 1000 },
        dataType: "json",
        success: function (res) {
            if (!imDictIsSuccess(res)) {
                if (typeof done === "function") {
                    done();
                }
                return;
            }
            var page = imResolvePage(res);
            var list = page ? (page.list || []) : [];
            $select.empty();
            if (includeAll) {
                $select.append('<option value="">所有</option>');
            }
            $.each(list, function (_, item) {
                var text = item.description ? (item.description + " (" + item.type + ")") : item.type;
                var selected = selectedId && String(selectedId) === String(item.id) ? ' selected="selected"' : "";
                $select.append('<option value="' + item.id + '"' + selected + '>' + imEscapeHtml(text) + "</option>");
            });
            if ($select.hasClass("select2-hidden-accessible")) {
                $select.trigger("change");
            }
            if (typeof done === "function") {
                done();
            }
        },
        error: function () {
            if (typeof done === "function") {
                done();
            }
        }
    });
}

function imDictResolveValueList(res) {
    if (!imDictIsSuccess(res)) {
        return [];
    }
    var page = imResolvePage(res);
    if (!page) {
        return [];
    }
    var list = page.list;
    if (list == null) {
        return [];
    }
    if (typeof list === "string") {
        try {
            list = JSON.parse(list);
        } catch (e) {
            return [];
        }
    }
    return $.isArray(list) ? list : [];
}

function imDictResolveMap(res) {
    if (!imDictIsSuccess(res)) {
        return {};
    }
    if (res.dictList) {
        return res.dictList;
    }
    if (res.data && res.data.dictList) {
        return res.data.dictList;
    }
    return {};
}

function imDictLoadMap(done) {
    imDictAjax({
        url: imDictApi + "/getDictMap",
        type: "GET",
        dataType: "json",
        success: function (res) {
            if (typeof done === "function") {
                done(imDictResolveMap(res));
            }
        },
        error: function () {
            if (typeof done === "function") {
                done({});
            }
        }
    });
}

function imDictGetLabel(dictMap, type, value, emptyText) {
    if (value == null || value === "") {
        return emptyText != null ? emptyText : "-";
    }
    var list = dictMap && dictMap[type];
    if (!list || !list.length) {
        return String(value);
    }
    var valStr = String(value);
    for (var i = 0; i < list.length; i++) {
        if (String(list[i].value) === valStr) {
            return list[i].label;
        }
    }
    return String(value);
}

function imDictFillSelectByType($select, dictMap, type, includeAll) {
    if (!$select || !$select.length) {
        return;
    }
    var list = (dictMap && dictMap[type]) || [];
    $select.empty();
    if (includeAll !== false) {
        $select.append('<option value="">所有</option>');
    }
    $.each(list, function (_, item) {
        $select.append(
            '<option value="' + imEscapeHtml(String(item.value)) + '">'
            + imEscapeHtml(item.label) + "</option>"
        );
    });
}

function imDictLoadValues(dictTypeId, done) {
    if (!dictTypeId) {
        if (typeof done === "function") {
            done([]);
        }
        return;
    }
    imDictAjax({
        url: imDictApi + "/getDictValue",
        type: "GET",
        data: { dictTypeId: dictTypeId },
        dataType: "json",
        success: function (res) {
            if (typeof done === "function") {
                done(imDictResolveValueList(res));
            }
        },
        error: function () {
            if (typeof done === "function") {
                done([]);
            }
        }
    });
}

function imDictFindTypeById(id, done) {
    imDictAjax({
        url: imDictApi + "/queryById",
        type: "GET",
        data: { id: id },
        dataType: "json",
        success: function (res) {
            if (imDictIsSuccess(res) && typeof done === "function") {
                done(imDictResolveEntity(res, "dictType"));
            } else if (typeof done === "function") {
                done(null);
            }
        },
        error: function () {
            if (typeof done === "function") {
                done(null);
            }
        }
    });
}

function imDictFindValueById(dictValueId, done) {
    imDictAjax({
        url: imDictApi + "/queryDictValue",
        type: "GET",
        data: { dictValueId: dictValueId },
        dataType: "json",
        success: function (res) {
            if (imDictIsSuccess(res) && typeof done === "function") {
                done(imDictResolveEntity(res, "dictValue"));
            } else if (typeof done === "function") {
                done(null);
            }
        },
        error: function () {
            if (typeof done === "function") {
                done(null);
            }
        }
    });
}

function imDictConfirmRemove(message, removeFn) {
    $.modal.confirm(message || "确认删除选中的数据吗？", function () {
        removeFn();
    });
}

function imDictSuccessCallback(res) {
    var parent = (typeof activeWindow === "function") ? activeWindow() : window;
    $.modal.msgSuccess((res && res.msg) ? res.msg : "保存成功");
    $.modal.close();
    if (!parent || !parent.$) {
        return;
    }
    if (typeof parent.imDictDataRefreshTable === "function") {
        parent.imDictDataRefreshTable(false);
        return;
    }
    if (parent.$("#bootstrap-table").length && parent.$("#bootstrap-table").bootstrapTable) {
        parent.$("#bootstrap-table").bootstrapTable("refresh");
    }
}
