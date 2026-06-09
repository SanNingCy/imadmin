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
    return function () {
        return {
            css: {
                "max-width": maxWidth + "px",
                "width": maxWidth + "px",
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
var IM_TABLE_ELLIPSIS_DEFAULT_WIDTH = 280;

/** 普通列默认宽度（未设置 width 时） */
var IM_TABLE_COLUMN_DEFAULT_WIDTH = 200;
/** 普通列最小宽度：已有 width 小于该值时会被抬升 */
var IM_TABLE_COLUMN_MIN_WIDTH = 120;
/** 操作列默认宽度（px） */
var IM_TABLE_OPERATE_COLUMN_DEFAULT_WIDTH = 280;
/** 操作列最小宽度 */
var IM_TABLE_OPERATE_COLUMN_MIN_WIDTH = 240;
/** 复选框列宽度 */
var IM_TABLE_CHECKBOX_COLUMN_WIDTH = 50;

function imParseColumnWidth(width) {
    if (width == null || width === "") {
        return null;
    }
    var num = parseInt(width, 10);
    return isNaN(num) ? null : num;
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
    var maxWidth = (tableOptions && tableOptions.ellipsisMaxWidth) || IM_TABLE_ELLIPSIS_DEFAULT_WIDTH;
    var maxLen = tableOptions && tableOptions.ellipsisMaxLen;
    var emptyText = (tableOptions && tableOptions.undefinedText) || "-";
    $.each(columns, function (_, column) {
        if (imIsEllipsisSkippedColumn(column)) {
            return;
        }
        column.cellStyle = imMergeEllipsisCellStyle(column, maxWidth);
        column.class = $.trim((column.class || "") + " im-table-ellipsis-cell");
        var colWidth = imParseColumnWidth(column.width);
        if (colWidth == null || colWidth < maxWidth) {
            column.width = maxWidth;
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
 * tableOptions.columnWidth = false 关闭；columnDefaultWidth / columnMinWidth / operateWidth 可调
 */
function imApplyTableColumnWidths(columns, tableOptions) {
    if (!columns || !$.isArray(columns)) {
        return columns;
    }
    if (tableOptions && tableOptions.columnWidth === false) {
        return columns;
    }
    var defaultWidth = (tableOptions && tableOptions.columnDefaultWidth) || IM_TABLE_COLUMN_DEFAULT_WIDTH;
    var minWidth = (tableOptions && tableOptions.columnMinWidth) || IM_TABLE_COLUMN_MIN_WIDTH;
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
        } else if (width < minWidth) {
            column.width = minWidth;
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
    var text = String(value);
    var display = text;
    if (maxLen && text.length > maxLen) {
        display = text.substring(0, maxLen) + "...";
    }
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
 * IM 列表页统一初始化（GET + AjaxJson 分页 + JWT 头）
 */
function imInitTable(options) {
    options = options || {};
    options.id = options.id || 'bootstrap-table';
    options.method = options.method || 'get';
    if (!options.responseHandler) {
        options.responseHandler = imPageResponse;
    }
    $.table.init(options);
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

(function imTableBootstrapPatch() {
    if (typeof jQuery === 'undefined' || !jQuery.table) {
        return;
    }
    var $ = jQuery;

    var ruoyiInit = $.table.init;
    var ruoyiResponseHandler = $.table.responseHandler;

    $.table.init = function (options) {
        options = options || {};
        if (options.columns) {
            if (options.columnWidth !== false) {
                options.columns = imApplyTableColumnWidths(options.columns, options);
            }
            if (options.ellipsis !== false) {
                options.columns = imApplyTableEllipsisColumns(options.columns, options);
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
