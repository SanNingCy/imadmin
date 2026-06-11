/** 去掉空字符串/null，避免搜索表单空字段污染 IM 列表查询与分页 */
function imOmitEmptyParams(obj) {
    var result = {};
    if (!obj) {
        return result;
    }
    $.each(obj, function (key, val) {
        if (val == null || val === '') {
            return;
        }
        if (typeof val === 'string' && $.trim(val) === '') {
            return;
        }
        result[key] = val;
    });
    return result;
}

/** 金额/小数输入：仅保留数字与一个小数点 */
function imSanitizeDecimalInput(value) {
    if (value == null) {
        return "";
    }
    var text = String(value).replace(/[^\d.]/g, "");
    var dotIndex = text.indexOf(".");
    if (dotIndex === -1) {
        return text;
    }
    return text.slice(0, dotIndex + 1) + text.slice(dotIndex + 1).replace(/\./g, "");
}

/** 校验金额查询条件是否为合法小数 */
function imIsValidDecimalInput(value) {
    var text = $.trim(value == null ? "" : String(value));
    if (text === "") {
        return true;
    }
    return /^(\d+\.?\d*|\.\d+)$/.test(text);
}

/** 绑定金额搜索框：输入时过滤非法字符 */
function imBindAmountInputs(container) {
    var $scope = container ? $(container) : $(document);
    $scope.find(".im-amount-input").each(function () {
        var $input = $(this);
        if ($input.data("imAmountBound")) {
            return;
        }
        $input.data("imAmountBound", true);
        $input.attr("inputmode", "decimal");
        $input.on("input", function () {
            var sanitized = imSanitizeDecimalInput(this.value);
            if (sanitized !== this.value) {
                this.value = sanitized;
            }
        });
    });
}

/** 提交前校验表单内金额搜索框 */
function imValidateAmountInputs(formId) {
    if (!formId || !$("#" + formId).length) {
        return true;
    }
    var invalid = [];
    $("#" + formId + " .im-amount-input").each(function () {
        if (!imIsValidDecimalInput($(this).val())) {
            invalid.push(this);
        }
    });
    if (invalid.length) {
        $.modal.alertWarning("金额查询条件只能输入数字");
        invalid[0].focus();
        return false;
    }
    return true;
}

/** 带金额校验的列表查询 */
function imTableSearch(formId, tableId, pageNumber, pageSize) {
    formId = $.common.isEmpty(formId) ? $("form").attr("id") : formId;
    if (!imValidateAmountInputs(formId)) {
        return;
    }
    $.table.search(formId, tableId, pageNumber, pageSize);
}

/** HTML 转义，用于 title 属性 */
function imEscapeHtml(text) {
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

/** 限制 td 宽度，防止长文本撑开表格 */
function imEllipsisCell(maxWidth) {
    var width = imParseColumnWidth(maxWidth);
    if (width == null || width < IM_TABLE_ELLIPSIS_DEFAULT_WIDTH) {
        width = IM_TABLE_ELLIPSIS_DEFAULT_WIDTH;
    }
    return function () {
        return {
            css: {
                "max-width": width + "px",
                "width": width + "px",
                "overflow": "hidden",
                "white-space": "nowrap",
                "text-overflow": "ellipsis"
            }
        };
    };
}

/** 合并省略号 cellStyle 与列原有 cellStyle */
function imMergeEllipsisCellStyle(column, maxWidth) {
    var base = imEllipsisCell(maxWidth);
    var original = column.cellStyle;
    if (typeof original !== "function") {
        return base;
    }
    return function (value, row, index) {
        var merged = base(value, row, index);
        var custom = original(value, row, index);
        if (custom && custom.css) {
            merged.css = $.extend({}, merged.css, custom.css);
        }
        if (custom && custom.classes) {
            merged.classes = custom.classes;
        }
        return merged;
    };
}

/** 列表文本列默认最大宽度（px） */
var IM_TABLE_ELLIPSIS_DEFAULT_WIDTH = 300;
/** 列表文本默认最多展示字符数（超出后加 ...；至少容纳 yyyy-MM-dd HH:mm:ss） */
var IM_TABLE_DATETIME_TEXT_LEN = 40;
var IM_TABLE_ELLIPSIS_DEFAULT_MAX_LEN = IM_TABLE_DATETIME_TEXT_LEN;
/** 时间列最小宽度（px，与 IM_TABLE_DATETIME_TEXT_LEN 匹配） */
var IM_TABLE_DATETIME_MIN_WIDTH = 165;

/** 普通列默认宽度（未设置 width 时，或 width 小于该值时抬升） */
var IM_TABLE_COLUMN_DEFAULT_WIDTH = 120;
/** 操作列默认宽度（px） */
var IM_TABLE_OPERATE_COLUMN_DEFAULT_WIDTH = 240;
/** 操作列最小宽度 */
var IM_TABLE_OPERATE_COLUMN_MIN_WIDTH = 200;
/** 复选框列宽度 */
var IM_TABLE_CHECKBOX_COLUMN_WIDTH = 50;

function imParseColumnWidth(width) {
    if (width == null || width === "") {
        return null;
    }
    var num = parseInt(width, 10);
    return isNaN(num) ? null : num;
}

function imIsLikelyDateTimeField(column) {
    if (!column || column.field == null) {
        return false;
    }
    var field = String(column.field);
    return /(?:time|date|Time|Date|At)$/i.test(field);
}

function imIsDateTimeDisplayText(text) {
    if (text == null) {
        return false;
    }
    return /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(String(text).trim());
}

function imResolveEllipsisMaxWidth(tableOptions, column) {
    var width;
    if (column && column.ellipsisMaxWidth != null) {
        width = column.ellipsisMaxWidth;
    } else if (tableOptions && tableOptions.ellipsisMaxWidth != null) {
        width = tableOptions.ellipsisMaxWidth;
    } else {
        width = IM_TABLE_ELLIPSIS_DEFAULT_WIDTH;
    }
    if (column && imIsLikelyDateTimeField(column)) {
        width = Math.max(width, IM_TABLE_DATETIME_MIN_WIDTH);
    }
    return width;
}

function imResolveEllipsisMaxLen(tableOptions, column) {
    var maxLen;
    if (column && column.ellipsisMaxLen != null) {
        maxLen = column.ellipsisMaxLen;
    } else if (tableOptions && tableOptions.ellipsisMaxLen != null) {
        maxLen = tableOptions.ellipsisMaxLen;
    } else {
        maxLen = IM_TABLE_ELLIPSIS_DEFAULT_MAX_LEN;
    }
    if (column && imIsLikelyDateTimeField(column)) {
        maxLen = Math.max(maxLen, IM_TABLE_DATETIME_TEXT_LEN);
    }
    return maxLen;
}

function imTruncateDisplayText(text, maxLen) {
    if (text == null) {
        return "";
    }
    var str = String(text);
    if (!maxLen || str.length <= maxLen) {
        return str;
    }
    return str.substring(0, maxLen) + "...";
}

function imShouldTruncateFormatterHtml(html) {
    if (!html || html.indexOf("<") === -1) {
        return true;
    }
    if (html.indexOf("im-cell-ellipsis") > -1) {
        return false;
    }
    if (/<(?:button|a|img|input|select|textarea)\b/i.test(html)) {
        return false;
    }
    if (/class="[^"]*\bbadge\b/i.test(html)) {
        return false;
    }
    if (/class="[^"]*\bbtn\b/i.test(html)) {
        return false;
    }
    return true;
}

/** 包装已有 formatter，按字符数截断展示（解决仅依赖 CSS 省略时整段长文仍可见） */
function imWrapColumnFormatterEllipsis(column, maxLen, emptyText) {
    if (!column || typeof column.formatter !== "function" || column.ellipsisWrap === false) {
        return;
    }
    var original = column.formatter;
    column.formatter = function (value, row, index) {
        var html = original.call(this, value, row, index);
        if (html == null || html === "") {
            return emptyText != null ? emptyText : "-";
        }
        var str = String(html);
        if (!imShouldTruncateFormatterHtml(str)) {
            return str;
        }
        if (str.indexOf("<") === -1) {
            return imFormatText(str, maxLen, emptyText);
        }
        var plain = $("<div>").html(str).text();
        if (!plain) {
            return str;
        }
        var effectiveMaxLen = maxLen;
        if (imIsDateTimeDisplayText(plain)) {
            effectiveMaxLen = Math.max(maxLen, IM_TABLE_DATETIME_TEXT_LEN);
        }
        var display = imTruncateDisplayText(plain, effectiveMaxLen);
        if (plain === display) {
            return str;
        }
        var safeFull = imEscapeHtml(plain);
        var safeDisplay = imEscapeHtml(display);
        var match = str.match(/^<span([^>]*)>([\s\S]*)<\/span>$/i);
        if (match) {
            return "<span" + match[1] + ' title="' + safeFull + '">' + safeDisplay + "</span>";
        }
        return imFormatText(plain, effectiveMaxLen, emptyText);
    };
}

function imShouldSkipGlobalColumnWidth(column) {
    return !column || column.columnWidth === false;
}

function imIsCheckboxColumn(column) {
    if (!column) {
        return false;
    }
    if (column.checkbox) {
        return true;
    }
    var field = column.field != null ? String(column.field) : "";
    return field === "state";
}

/** 是否跳过全局省略号处理 */
function imIsEllipsisSkippedColumn(column) {
    if (!column || column.ellipsis === false) {
        return true;
    }
    if (column.checkbox) {
        return true;
    }
    if (column.escape === false) {
        return true;
    }
    if (typeof column.formatter === "function") {
        return true;
    }
    if (column.events) {
        return true;
    }
    var title = column.title != null ? String(column.title) : "";
    if (title === "操作") {
        return true;
    }
    if (!column.field && title) {
        return true;
    }
    var field = column.field != null ? String(column.field) : "";
    if (field === "state") {
        return true;
    }
    return false;
}

/**
 * 为未自定义 formatter 的文本列统一应用省略号展示
 * 可通过 tableOptions.ellipsis = false 关闭；ellipsisMaxWidth / ellipsisMaxLen 可调
 */
function imApplyTableEllipsisColumns(columns, tableOptions) {
    if (!columns || !$.isArray(columns)) {
        return columns;
    }
    if (tableOptions && tableOptions.ellipsis === false) {
        return columns;
    }
    var emptyText = (tableOptions && tableOptions.undefinedText) || "-";
    $.each(columns, function (_, column) {
        if (imIsEllipsisSkippedColumn(column)) {
            return;
        }
        var colMaxWidth = imResolveEllipsisMaxWidth(tableOptions, column);
        var maxLen = imResolveEllipsisMaxLen(tableOptions, column);
        column.cellStyle = imMergeEllipsisCellStyle(column, colMaxWidth);
        column.class = $.trim((column.class || "") + " im-table-ellipsis-cell");
        var colWidth = imParseColumnWidth(column.width);
        if (colWidth == null || colWidth > colMaxWidth) {
            column.width = colMaxWidth;
        } else if (imIsLikelyDateTimeField(column) && colWidth < colMaxWidth) {
            column.width = colMaxWidth;
        }
        column.formatter = function (value) {
            if (value == null || value === "") {
                return emptyText;
            }
            if (typeof value === "object") {
                try {
                    value = JSON.stringify(value);
                } catch (e) {
                    value = String(value);
                }
            }
            return imFormatText(value, maxLen);
        };
    });
    return columns;
}

function imShouldApplyCellNowrap(column) {
    if (!column || column.cellWrap === true) {
        return false;
    }
    if (imIsCheckboxColumn(column)) {
        return false;
    }
    if (imIsOperateColumn(column)) {
        return false;
    }
    if (column.escape === false) {
        return false;
    }
    return true;
}

/**
 * 为含自定义 formatter 的文本列强制单行 + 字符截断（imApplyTableEllipsisColumns 会跳过 formatter 列）
 * tableOptions.cellNowrap = false 可关闭
 */
function imApplyTableCellNowrap(columns, tableOptions) {
    if (!columns || !$.isArray(columns)) {
        return columns;
    }
    if (tableOptions && tableOptions.cellNowrap === false) {
        return columns;
    }
    var emptyText = (tableOptions && tableOptions.undefinedText) || "-";
    $.each(columns, function (_, column) {
        if (!column) {
            return;
        }
        if (column.escape === false) {
            column.class = $.trim((column.class || "") + " im-table-media-cell im-table-no-ellipsis");
            return;
        }
        if (!imShouldApplyCellNowrap(column)) {
            return;
        }
        var colMaxWidth = imResolveEllipsisMaxWidth(tableOptions, column);
        var maxLen = imResolveEllipsisMaxLen(tableOptions, column);
        imWrapColumnFormatterEllipsis(column, maxLen, emptyText);
        var colWidth = imParseColumnWidth(column.width);
        if (column.ellipsisWidth !== false) {
            if (colWidth == null || colWidth > colMaxWidth) {
                column.width = colMaxWidth;
            } else if (imIsLikelyDateTimeField(column) && colWidth < colMaxWidth) {
                column.width = colMaxWidth;
            }
        }
        column.cellStyle = imMergeEllipsisCellStyle(column, colMaxWidth);
        column.class = $.trim((column.class || "") + " im-table-ellipsis-cell");
    });
    return columns;
}

/** 是否为表格操作列 */
function imIsOperateColumn(column) {
    if (!column || column.operateWidth === false) {
        return false;
    }
    var title = column.title != null ? String(column.title).trim() : "";
    return title === "操作";
}

/**
 * 统一加宽表格各列（含操作列、普通文本列等）
 * tableOptions.columnWidth = false 关闭；columnDefaultWidth / operateWidth 可调
 */
function imApplyTableColumnWidths(columns, tableOptions) {
    if (!columns || !$.isArray(columns)) {
        return columns;
    }
    if (tableOptions && tableOptions.columnWidth === false) {
        return columns;
    }
    var defaultWidth = (tableOptions && tableOptions.columnDefaultWidth) || IM_TABLE_COLUMN_DEFAULT_WIDTH;
    var operateDefault = (tableOptions && tableOptions.operateWidth) || IM_TABLE_OPERATE_COLUMN_DEFAULT_WIDTH;
    var operateMin = (tableOptions && tableOptions.operateMinWidth) || IM_TABLE_OPERATE_COLUMN_MIN_WIDTH;
    var checkboxWidth = (tableOptions && tableOptions.checkboxWidth) || IM_TABLE_CHECKBOX_COLUMN_WIDTH;

    $.each(columns, function (_, column) {
        if (imShouldSkipGlobalColumnWidth(column)) {
            return;
        }
        if (imIsCheckboxColumn(column)) {
            if (imParseColumnWidth(column.width) == null) {
                column.width = checkboxWidth;
            }
            return;
        }
        if (imIsOperateColumn(column)) {
            column.class = $.trim((column.class || "") + " im-table-operate-cell im-table-no-ellipsis");
            var operateWidth = imParseColumnWidth(column.width);
            if (operateWidth == null) {
                column.width = operateDefault;
            } else if (operateWidth < operateMin) {
                column.width = operateMin;
            }
            return;
        }
        var width = imParseColumnWidth(column.width);
        if (width == null) {
            column.width = defaultWidth;
        } else if (width < defaultWidth) {
            column.width = defaultWidth;
        }
    });
    return columns;
}

/** @deprecated 请使用 imApplyTableColumnWidths */
function imApplyTableOperateColumnWidth(columns, tableOptions) {
    return imApplyTableColumnWidths(columns, tableOptions);
}

/** 截断展示 + 悬浮 title 显示完整内容 */
function imFormatText(value, maxLen, emptyText) {
    if (value == null || value === "") {
        return emptyText != null ? emptyText : "-";
    }
    if (maxLen == null || maxLen === "") {
        maxLen = IM_TABLE_ELLIPSIS_DEFAULT_MAX_LEN;
    }
    var text = String(value);
    var display = imTruncateDisplayText(text, maxLen);
    var style = "display:block;max-width:100%;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;";
    return '<span class="im-cell-ellipsis" style="' + style + '" title="' + imEscapeHtml(text) + '">'
        + imEscapeHtml(display) + "</span>";
}

/** IM 分页查询参数（pageNo/pageSize，非若依 pageNum） */
function imBuildPageQuery(pageNo, pageSize, sort, order) {
    var query = {
        pageNo: pageNo,
        pageSize: pageSize
    };
    if (sort) {
        var asc = String(order) === 'asc' || order === true;
        query.orderBy = sort + ' ' + (asc ? 'asc' : 'desc');
    }
    return query;
}

/**
 * 解析 IM AjaxJson 中的 page（兼容顶层 page 与 data.page 两种结构）
 */
function imResolvePage(res) {
    if (!res) {
        return null;
    }
    if (res.page) {
        return res.page;
    }
    if (res.data && res.data.page) {
        return res.data.page;
    }
    return null;
}

/**
 * 适配 chat-ops AjaxJson 分页结构 -> 若依 bootstrap-table
 */
function imPageResponse(res) {
    if (typeof res === 'string') {
        try {
            res = JSON.parse(res);
        } catch (e) {
            if (typeof $.modal !== 'undefined') {
                $.modal.alertWarning('加载失败');
            }
            return { rows: [], total: 0 };
        }
    }
    var page = imResolvePage(res);
    if (res && (res.success === true || res.code === 200) && page) {
        var list = page.list || [];
        var total = page.count;
        var pageSize = page.pageSize > 0 ? page.pageSize : 10;
        var pageNo = page.pageNo > 0 ? page.pageNo : 1;
        if (total > 0) {
            // 使用后端真实总数
        } else if (list.length === 0) {
            total = 0;
        } else if (list.length < pageSize) {
            // 最后一页：可准确推算 total
            total = (pageNo - 1) * pageSize + list.length;
        } else {
            // count 未返回但满页：至少保证当前页可展示，并允许继续翻页
            total = pageNo * pageSize + list.length;
        }
        return { rows: list, total: total };
    }
    if (typeof $.modal !== 'undefined') {
        $.modal.alertWarning((res && res.msg) ? res.msg : '加载失败');
    }
    return { rows: [], total: 0 };
}

function imReadCookie(name) {
    if (!name) {
        return null;
    }
    var escaped = name.replace(/([.$?*|{}()[\]\\/+^])/g, '\\$1');
    var match = document.cookie.match(new RegExp('(?:^|; )' + escaped + '=([^;]*)'));
    return match ? decodeURIComponent(match[1]) : null;
}

function imTableBeforeSend(xhr) {
    try {
        var token = imReadCookie('token');
        if (!token && window.localStorage) {
            token = localStorage.getItem('token');
        }
        if (token) {
            xhr.setRequestHeader('token', token);
        }
    } catch (e) {
        // 读取 token 失败不阻断请求；同域下浏览器仍会自动携带 cookie
        console.warn('imTableBeforeSend skipped:', e);
    }
}

function imResolveTableId(ctx) {
    if (!ctx) {
        return 'bootstrap-table';
    }
    return ctx.id || (ctx.options && ctx.options.id) || 'bootstrap-table';
}

/**
 * 弹窗内日期时间：若依 laydate（与列表搜索 time-input 一致，在 layer 打开后调用）
 */
function imBindLaydate(container) {
    var $scope = container ? $(container) : $(document);
    if (!$scope.length || typeof layui === 'undefined') {
        return;
    }
    layui.use('laydate', function () {
        var laydate = layui.laydate;
        $scope.find('.time-input.im-modal-laydate').each(function () {
            var $item = $(this);
            if ($item.attr('data-laydate-bound') === '1') {
                return;
            }
            $item.attr('data-laydate-bound', '1');
            var type = $item.attr('data-type') || 'date';
            var format = $item.attr('data-format')
                || (type === 'datetime' ? 'yyyy-MM-dd HH:mm:ss' : 'yyyy-MM-dd');
            laydate.render({
                elem: this,
                theme: 'molv',
                trigger: 'click',
                type: type,
                format: format,
                btns: ['clear', 'now', 'confirm']
            });
        });
    });
}

/**
 * IM 列表页统一初始化（GET + AjaxJson 分页 + JWT 头）
 */
function imInitTable(options) {
    options = options || {};
    options.id = options.id || 'bootstrap-table';
    options.method = options.method || 'get';
    if (!options.responseHandler) {
        options.responseHandler = imPageResponse;
    }
    if (!options.formatLoadingMessage) {
        options.formatLoadingMessage = function () {
            return '正在加载数据，请稍候...';
        };
    }
    $.table.init(options);
    if (options.formId) {
        imBindAmountInputs("#" + options.formId);
    }
}

/** IM 接口导出为 GET 直出文件流，与若依 POST+common/download 不同 */
function imShouldUseImExport(exportUrl, tableOptions) {
    if (!exportUrl || exportUrl.indexOf("/export") === -1) {
        return false;
    }
    if (tableOptions && tableOptions.exportMethod === "post") {
        return false;
    }
    if (tableOptions && (tableOptions.exportMethod === "get" || tableOptions.exportIm === true)) {
        return true;
    }
    return !!(tableOptions && tableOptions.responseHandler === imPageResponse);
}

function imBuildExportQuery(formId, tableOptions) {
    var currentId = $.common.isEmpty(formId)
        ? (tableOptions.formId || $("form").attr("id"))
        : formId;
    var data = {};
    if (currentId) {
        $.each($("#" + currentId).serializeArray(), function (_, item) {
            if (item.value != null && item.value !== "") {
                data[item.name] = item.value;
            }
        });
    }
    var tableId = tableOptions.id || "bootstrap-table";
    var params = $("#" + tableId).bootstrapTable("getOptions");
    if (params && params.sortName) {
        data.orderByColumn = params.sortName;
        data.isAsc = params.sortOrder;
    }
    return data;
}

function imParseExportFileName(xhr, fallbackName) {
    var disposition = xhr.getResponseHeader("Content-Disposition") || "";
    var match = /filename\*=UTF-8''([^;]+)|filename="?([^";]+)"?/i.exec(disposition);
    if (match) {
        try {
            return decodeURIComponent(match[1] || match[2]);
        } catch (e) {
            return match[1] || match[2];
        }
    }
    return fallbackName || "export.xlsx";
}

function imDownloadBlob(blob, fileName) {
    var link = document.createElement("a");
    var url = window.URL.createObjectURL(blob);
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
}

function imExportExcel(formId, tableOptions) {
    if (typeof table !== "undefined" && table.set) {
        table.set();
    }
    tableOptions = tableOptions || (typeof table !== "undefined" ? table.options : {});
    var modalName = tableOptions.modalName || "数据";
    var exportUrl = tableOptions.exportUrl;
    if (!exportUrl) {
        $.modal.alertWarning("未配置导出地址");
        return;
    }
    $.modal.confirm("确定导出所有" + modalName + "吗？", function () {
        $.modal.loading("正在导出数据，请稍候...");
        $.ajax({
            url: exportUrl,
            type: "GET",
            data: imBuildExportQuery(formId, tableOptions),
            xhrFields: { responseType: "blob" },
            beforeSend: imTableBeforeSend,
            success: function (data, status, xhr) {
                $.modal.closeLoading();
                var contentType = (xhr.getResponseHeader("Content-Type") || "").toLowerCase();
                if (contentType.indexOf("json") > -1) {
                    var reader = new FileReader();
                    reader.onload = function () {
                        try {
                            var res = JSON.parse(reader.result);
                            $.modal.alertError((res && res.msg) ? res.msg : "导出失败");
                        } catch (e) {
                            $.modal.alertError("导出失败");
                        }
                    };
                    reader.readAsText(data);
                    return;
                }
                imDownloadBlob(data, imParseExportFileName(xhr, "export.xlsx"));
            },
            error: function (xhr) {
                $.modal.closeLoading();
                if (xhr.responseText) {
                    try {
                        var res = JSON.parse(xhr.responseText);
                        $.modal.alertError((res && res.msg) ? res.msg : "导出失败");
                        return;
                    } catch (e) {
                        /* ignore */
                    }
                }
                $.modal.alertError("导出失败");
            }
        });
    });
}

/** 列表图片预览初始化（需引入 im-piamom-common.js，与朋友圈列表一致） */
function imInitListMediaPreview() {
    if (typeof imPiamomInitMediaEvents === "function") {
        imPiamomInitMediaEvents();
    }
}

/** 表格渲染后绑定图片预览与加载失败回退 */
function imBindListMediaPreview($root) {
    if (typeof imPiamomBindMediaImagesIn === "function") {
        imPiamomBindMediaImagesIn($root || $("#bootstrap-table"));
    }
}

/** 列表图片列格式化（需引入 im-piamom-common.js） */
function imFormatListMedia(value, cacheKey, max) {
    if (typeof imPiamomFormatMedia === "function") {
        return imPiamomFormatMedia(value, cacheKey, max);
    }
    return value ? imEscapeHtml(String(value)) : "-";
}

/** 列表媒体列默认宽度 */
var IM_LIST_MEDIA_COLUMN_WIDTH = 200;

/** 列表媒体列紧凑格式化（默认最多 IM_LIST_MEDIA_COMPACT_MAX 张，固定行高） */
function imFormatListMediaCompact(value, cacheKey, max) {
    if (max == null) {
        max = (typeof IM_LIST_MEDIA_COMPACT_MAX !== "undefined") ? IM_LIST_MEDIA_COMPACT_MAX : 4;
    }
    return imFormatListMedia(value, cacheKey, max);
}

/** 列表媒体列 cellStyle */
function imListMediaCellStyle() {
    return { css: { "text-align": "left", "vertical-align": "middle" } };
}

/**
 * 构建列表媒体列配置
 * options: { title, width, max, cachePrefix, cacheKey(row,value), format(value,row,cacheKey) }
 */
function imBuildListMediaColumn(field, options) {
    options = options || {};
    var cachePrefix = options.cachePrefix || field;
    return {
        field: field,
        title: options.title || "图片",
        width: options.width != null ? options.width : IM_LIST_MEDIA_COLUMN_WIDTH,
        escape: false,
        cellStyle: imListMediaCellStyle,
        formatter: function (value, row) {
            var cacheKey = typeof options.cacheKey === "function"
                ? options.cacheKey(row, value)
                : (cachePrefix + "-" + row.id);
            var max = options.max != null
                ? options.max
                : ((typeof IM_LIST_MEDIA_COMPACT_MAX !== "undefined") ? IM_LIST_MEDIA_COMPACT_MAX : 4);
            if (typeof options.format === "function") {
                return options.format(value, row, cacheKey, max);
            }
            return imFormatListMediaCompact(value, cacheKey, max);
        }
    };
}

/** 为 imInitTable 选项注入媒体列预览绑定（escape:false + onPostBody） */
function imApplyListMediaTableOptions(options) {
    options = options || {};
    if (options.escape !== true) {
        options.escape = false;
    }
    imInitListMediaPreview();
    var tableId = options.id || "bootstrap-table";
    var userOnPostBody = options.onPostBody;
    options.onPostBody = function () {
        imBindListMediaPreview($("#" + tableId));
        if (typeof userOnPostBody === "function") {
            userOnPostBody.apply(this, arguments);
        }
    };
    return options;
}

(function imTableBootstrapPatch() {
    if (typeof jQuery === 'undefined' || !jQuery.table) {
        return;
    }
    var $ = jQuery;

    var ruoyiInit = $.table.init;
    var ruoyiResponseHandler = $.table.responseHandler;
    var ruoyiExportExcel = $.table.exportExcel;

    $.table.exportExcel = function (formId) {
        table.set();
        if (imShouldUseImExport(table.options.exportUrl, table.options)) {
            imExportExcel(formId, table.options);
            return;
        }
        return ruoyiExportExcel.call($.table, formId);
    };

    $.table.init = function (options) {
        options = options || {};
        if (options.columns) {
            if (options.columnWidth !== false) {
                options.columns = imApplyTableColumnWidths(options.columns, options);
            }
            if (options.ellipsis !== false) {
                options.columns = imApplyTableEllipsisColumns(options.columns, options);
            }
            if (options.cellNowrap !== false) {
                options.columns = imApplyTableCellNowrap(options.columns, options);
            }
        }
        if (options.responseHandler === imPageResponse) {
            options.method = options.method || 'get';
            options.cache = true;
        }
        return ruoyiInit.call($.table, options);
    };

    // 若依 wrapper 在 this.id 缺失时会抛错，导致 bootstrap-table 一直显示 loading
    $.table.responseHandler = function (res) {
        // IM 接口：优先按响应体识别，不依赖 this.id
        if (res && typeof res === 'object' && (res.page || (res.data && res.data.page))) {
            return imPageResponse(res);
        }
        if (typeof res === 'string' && res.indexOf('"page"') > -1) {
            return imPageResponse(res);
        }

        var tableId = imResolveTableId(this);
        var options = table.config[tableId];
        if (options && typeof options.responseHandler === 'function'
            && options.responseHandler !== ruoyiResponseHandler
            && options.responseHandler !== $.table.responseHandler) {
            return options.responseHandler(res);
        }
        try {
            return ruoyiResponseHandler.call({ id: tableId, options: options || {} }, res);
        } catch (e) {
            console.error('table responseHandler error', e);
            return { rows: [], total: 0 };
        }
    };

    $(document).ajaxSend(function (event, xhr, settings) {
        if (!settings || !settings.url) {
            return;
        }
        var path = settings.url;
        if (path.indexOf('/member/') > -1 || path.indexOf('/upgrade/') > -1
            || path.indexOf('/notif/') > -1 || path.indexOf('/agreement/') > -1
            || path.indexOf('/faq/') > -1 || path.indexOf('/feedback/') > -1
            || path.indexOf('/reason/') > -1 || path.indexOf('/customer/') > -1
            || path.indexOf('/buttonConfig/') > -1 || path.indexOf('/admin/asset/') > -1
            || path.indexOf('/group/') > -1 || path.indexOf('/friend/') > -1
            || path.indexOf('/chatlog/') > -1 || path.indexOf('/balancelog/') > -1
            || path.indexOf('/sys/dict/') > -1
            || path.indexOf('/rechagelog/') > -1
            || path.indexOf('/redPacketTransaction/') > -1
            || path.indexOf('/grouphongbaolog/') > -1
            || path.indexOf('/signset/') > -1
            || path.indexOf('/admin/piamom/') > -1
            || path.indexOf('/admin/live/') > -1)
        {
            imTableBeforeSend(xhr);
        }
    });
})();
