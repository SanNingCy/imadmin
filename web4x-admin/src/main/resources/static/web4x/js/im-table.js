/**
 * 适配 chat-ops AjaxJson 分页结构 -> 若依 bootstrap-table
 */
function imPageResponse(res) {
    if (res && (res.success === true || res.code === 200) && res.page) {
        return {
            rows: res.page.list || [],
            total: res.page.count || 0
        };
    }
    if (typeof $.modal !== 'undefined') {
        $.modal.alertWarning((res && res.msg) ? res.msg : '加载失败');
    }
    return { rows: [], total: 0 };
}

function imTableBeforeSend(xhr) {
    var token = $.common.getCookie('token') || localStorage.getItem('token');
    if (token) {
        xhr.setRequestHeader('token', token);
    }
}
