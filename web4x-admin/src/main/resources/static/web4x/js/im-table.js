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

(function imTableBootstrapPatch() {
    if (typeof jQuery === 'undefined' || !jQuery.table) {
        return;
    }
    var $ = jQuery;

    var ruoyiInit = $.table.init;
    var ruoyiResponseHandler = $.table.responseHandler;

    $.table.init = function (options) {
        options = options || {};
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
            || path.indexOf('/notif/') > -1 || path.indexOf('/group/') > -1
            || path.indexOf('/friend/') > -1 || path.indexOf('/chatlog/') > -1) {
            imTableBeforeSend(xhr);
        }
    });
})();
