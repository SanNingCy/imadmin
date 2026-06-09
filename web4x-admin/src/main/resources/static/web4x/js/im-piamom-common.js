/**
 * 广场朋友圈(piamom) 公共工具
 * API 前缀：/admin/piamom
 */

var imPiamomApi = ctx + "admin/piamom";

function imPiamomResolveData(res, key) {
    if (!res) {
        return null;
    }
    if (res[key] != null) {
        return res[key];
    }
    if (res.data && res.data[key] != null) {
        return res.data[key];
    }
    return null;
}

function imPiamomParseImageUrls(value) {
    if (value == null || value === "") {
        return [];
    }
    if ($.isArray(value)) {
        return value.map(imPiamomNormalizeMediaUrl).filter(Boolean);
    }
    var trimmed = String(value).trim();
    if (!trimmed || trimmed === "[]") {
        return [];
    }
    // 多图用 | 拼接（朋友圈 imgs 等）；须在单 URL 判断之前拆分，否则 http://a|http://b 会被当成一个地址
    if (trimmed.indexOf("|") !== -1) {
        return trimmed.split("|").map(function (item) {
            return imPiamomNormalizeMediaUrl($.trim(item));
        }).filter(Boolean);
    }
    // 已是单个 http(s) 或内嵌图片（登录二维码等后端生成的 data URL）
    if (/^https?:\/\//i.test(trimmed) || /^data:/i.test(trimmed) || /^blob:/i.test(trimmed)) {
        return [imPiamomNormalizeMediaUrl(trimmed)];
    }
    try {
        var parsed = JSON.parse(trimmed);
        if ($.isArray(parsed)) {
            return parsed.map(imPiamomNormalizeMediaUrl).filter(Boolean);
        }
        if (typeof parsed === "string" && parsed) {
            return [imPiamomNormalizeMediaUrl(parsed)];
        }
    } catch (e) {
        // fall through
    }
    return trimmed.split(/[|,]/).map(function (item) {
        return imPiamomNormalizeMediaUrl($.trim(item));
    }).filter(Boolean);
}

/** 规范化媒体 URL（去引号/反斜杠，相对路径补 filePath 前缀） */
function imPiamomNormalizeMediaUrl(src) {
    if (src == null || src === "") {
        return "";
    }
    src = String(src).trim();
    if (!src || src === "[]") {
        return "";
    }
    // 去掉 JSON 残留引号、方括号
    src = src.replace(/^[\["']+|[\]"']+$/g, "");
    // MySQL/JSON 转义残留：\/ -> /
    src = src.replace(/\\\//g, "/").replace(/\\/g, "");
    if (/^https?:\/\//i.test(src) || /^data:/i.test(src) || /^blob:/i.test(src)) {
        return src;
    }
    var base = window.imFilePath || "";
    if (base) {
        if (base.charAt(base.length - 1) !== "/" && src.charAt(0) !== "/") {
            base += "/";
        }
        return base + src.replace(/^\//, "");
    }
    return src.charAt(0) === "/" ? src : src;
}

/** 行内图片画廊缓存（避免 onclick 拼接复杂 URL） */
var imPiamomMediaGalleryCache = {};
var imPiamomMediaEventsBound = false;

/** 绑定图片点击预览（事件委托，避免 inline onclick 被 CSP 拦截） */
function imPiamomInitMediaEvents() {
    if (imPiamomMediaEventsBound) {
        return;
    }
    imPiamomMediaEventsBound = true;
    $(document).on("click", ".piamom-media-img", function (e) {
        e.preventDefault();
        e.stopPropagation();
        var galleryId = $(this).attr("data-gallery");
        var index = parseInt($(this).attr("data-index"), 10);
        if (isNaN(index)) {
            index = 0;
        }
        imPiamomPreviewGallery(galleryId, index);
    });
    $(document).on("click", ".piamom-media-more", function (e) {
        e.preventDefault();
        e.stopPropagation();
        var galleryId = $(this).attr("data-gallery");
        var index = parseInt($(this).attr("data-index"), 10);
        if (isNaN(index)) {
            index = 0;
        }
        imPiamomPreviewGallery(galleryId, index);
    });
}

/** 表格/弹窗渲染后绑定图片 load error 回退 */
function imPiamomBindMediaImagesIn($root) {
    ($root || $(document)).find(".piamom-media-img").each(function () {
        var img = this;
        if (img.getAttribute("data-bound") === "1") {
            return;
        }
        img.setAttribute("data-bound", "1");
        img.onerror = function () {
            imPiamomMediaImgError(img, img.getAttribute("data-src") || img.src);
        };
    });
}

function imPiamomFormatTop(val) {
    return String(val) === "1"
        ? '<span class="label label-warning">置顶</span>'
        : '<span class="label label-default">否</span>';
}

function imPiamomFormatStatus(val) {
    return String(val) === "1"
        ? '<span class="label label-success">正常</span>'
        : '<span class="label label-default">下架/隐藏</span>';
}

var IM_PIAMOM_COMMENT_GATE_FLAG_OPTIONS = [
    { label: "普通", value: "1" },
    { label: "会员", value: "2" },
    { label: "信用分", value: "3" }
];

function imPiamomFormatCommentGateFlags(value) {
    if (value == null || value === "") {
        return "-";
    }
    var parts = String(value).split(/[,，]/).map(function (s) { return $.trim(s); }).filter(Boolean);
    if (!parts.length) {
        return "-";
    }
    return parts.map(function (p) {
        return imPiamomLabelFromOptions(p, IM_PIAMOM_COMMENT_GATE_FLAG_OPTIONS);
    }).join("、");
}

function imPiamomFormatOdicStakeLine(odicStake, odicStakeStatus) {
    var amount = odicStake != null && odicStake !== "" ? String(odicStake) : null;
    var statusHtml = imPiamomFormatOdicStakeStatus(odicStakeStatus);
    if (!amount) {
        return statusHtml;
    }
    return "质押 <strong>" + amount + "</strong> ODIC，状态：" + statusHtml;
}

function imPiamomSetFormReadOnly($form, readOnly) {
    $form.find("input, select, textarea").prop("disabled", !!readOnly);
}

/**
 * 图片列展示（对齐 im-admin-web MediaCell：flex 换行、展示全部缩略图、点击相册预览）
 * @param value imageUrls 字段值
 * @param cacheKey 缓存键（建议传 row.id）
 * @param max 最多展示张数，不传则展示全部
 */
function imPiamomFormatMedia(value, cacheKey, max) {
    var urls = imPiamomParseImageUrls(value);
    if (!urls.length) {
        return "-";
    }
    var compact = max != null && max > 0;
    var display = urls;
    var overflow = 0;
    var moreIndex = 0;
    if (compact && urls.length > max) {
        display = urls.slice(0, Math.max(max - 1, 1));
        overflow = urls.length - display.length;
        moreIndex = display.length;
    } else if (compact) {
        display = urls.slice(0, max);
    }
    var thumbSize = compact ? null : (display.length > 4 ? 40 : 48);
    var galleryId = cacheKey != null ? String(cacheKey) : ("g" + Date.now() + Math.random());
    imPiamomMediaGalleryCache[galleryId] = urls;
    var safeGalleryId = imEscapeHtml(galleryId);
    var cellClass = "piamom-media-cell" + (compact ? " piamom-media-cell--compact" : "");

    var html = '<div class="' + cellClass + '">';
    display.forEach(function (src, index) {
        var safe = src.replace(/"/g, "&quot;");
        var sizeStyle = thumbSize ? (' style="width:' + thumbSize + "px;height:" + thumbSize + 'px"') : "";
        html += '<img class="piamom-media-img" ' + sizeStyle
            + ' src="' + safe + '" data-src="' + safe + '" data-gallery="' + safeGalleryId + '" data-index="' + index + '" '
            + 'referrerpolicy="no-referrer" loading="lazy" alt=""/>';
    });
    if (overflow > 0) {
        html += '<span class="piamom-media-more" data-gallery="' + safeGalleryId + '" data-index="' + moreIndex
            + '" title="共' + urls.length + '张，点击查看">+' + overflow + "</span>";
    }
    html += "</div>";
    return html;
}

/** 缩略图加载失败时显示「查看」链接，新窗口打开原图 */
function imPiamomMediaImgError(img, src) {
    if (!img || !src) {
        return;
    }
    img.onerror = null;
    img.style.display = "none";
    var link = document.createElement("a");
    link.href = src;
    link.target = "_blank";
    link.rel = "noopener noreferrer";
    link.className = "piamom-media-fallback";
    link.textContent = "查看";
    link.title = src;
    if (img.parentNode) {
        img.parentNode.appendChild(link);
    }
}

/** 相册预览 */
function imPiamomPreviewGallery(galleryId, startIndex) {
    var urls = imPiamomMediaGalleryCache[galleryId] || [];
    if (!urls.length) {
        return;
    }
    startIndex = startIndex || 0;
    if (startIndex < 0 || startIndex >= urls.length) {
        startIndex = 0;
    }

    if (typeof layer !== "undefined" && layer.photos) {
        layer.photos({
            photos: {
                title: "图片预览",
                id: galleryId,
                start: startIndex,
                data: urls.map(function (src, i) {
                    return {
                        alt: "图片" + (i + 1),
                        pid: i + 1,
                        src: src,
                        thumb: src
                    };
                })
            },
            anim: 5,
            shadeClose: true
        });
        return;
    }
    imPiamomPreviewImageLayer(urls[startIndex]);
}

function imPiamomPreviewImageLayer(src) {
    if (!src) {
        return;
    }
    if (typeof layer === "undefined") {
        window.open(src, "_blank", "noopener,noreferrer");
        return;
    }
    var safe = String(src).replace(/"/g, "&quot;");
    layer.open({
        type: 1,
        title: "图片预览",
        shadeClose: true,
        area: ["auto", "auto"],
        content: '<div style="padding:12px;text-align:center;">'
            + '<img src="' + safe + '" referrerpolicy="no-referrer" '
            + 'style="max-width:85vw;max-height:80vh;display:block;margin:0 auto;"/>'
            + '<p style="margin:10px 0 0;"><a href="' + safe + '" target="_blank" rel="noopener noreferrer">新窗口打开</a></p>'
            + "</div>"
    });
}

function imPiamomPreviewImage(src) {
    if (!src) {
        return;
    }
    imPiamomPreviewImageLayer(src);
}

function imPiamomBuildDrawerQueryParams(params, momentId) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    if (momentId) {
        query.momentId = momentId;
    }
    return query;
}

function imPiamomBuildSquareDrawerQueryParams(params, squareId) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    if (squareId) {
        query.squareId = squareId;
    }
    return query;
}

/** 非分页 list 接口 -> bootstrap-table */
function imPiamomListResponse(res) {
    if (typeof res === "string") {
        try {
            res = JSON.parse(res);
        } catch (e) {
            return { rows: [], total: 0 };
        }
    }
    if (res && (res.success === true || res.code === 200)) {
        var list = res.list || (res.data && res.data.list) || [];
        return { rows: list, total: list.length };
    }
    if (typeof $.modal !== "undefined") {
        $.modal.alertWarning((res && res.msg) ? res.msg : "加载失败");
    }
    return { rows: [], total: 0 };
}

function imPiamomLabelFromOptions(val, options) {
    if (val == null || val === "") {
        return "-";
    }
    for (var i = 0; i < options.length; i++) {
        if (String(options[i].value) === String(val)) {
            return options[i].label;
        }
    }
    return String(val);
}

var IM_PIAMOM_SQUARE_TYPE_OPTIONS = [
    { label: "图片", value: "1" },
    { label: "视频", value: "2" }
];

var IM_PIAMOM_ODIC_STAKE_STATUS_OPTIONS = [
    { label: "质押中", value: 0 },
    { label: "已退还", value: 1 },
    { label: "举报没收", value: 2 }
];

var IM_PIAMOM_QUOTA_STATUS_OPTIONS = [
    { label: "停用", value: 0 },
    { label: "启用", value: 1 }
];

function imPiamomFormatSquareType(val) {
    return imPiamomLabelFromOptions(val, IM_PIAMOM_SQUARE_TYPE_OPTIONS);
}

function imPiamomFormatOdicStakeStatus(val) {
    var label = imPiamomLabelFromOptions(val, IM_PIAMOM_ODIC_STAKE_STATUS_OPTIONS);
    if (label === "-") {
        return label;
    }
    var cls = String(val) === "0" ? "label-primary" : (String(val) === "1" ? "label-success" : "label-danger");
    return '<span class="label ' + cls + '">' + label + "</span>";
}

function imPiamomFormatQuotaStatus(val) {
    return String(val) === "1"
        ? '<span class="label label-success">启用</span>'
        : '<span class="label label-default">停用</span>';
}

function imPiamomFormatCreditMax(val) {
    return val == null || val === "" ? "无上限" : val;
}

var IM_PIAMOM_AUDIT_STATUS_OPTIONS = [
    { label: "待审核", value: 0 },
    { label: "通过", value: 1 },
    { label: "驳回", value: 2 }
];

var IM_PIAMOM_TARGET_TYPE_OPTIONS = [
    { label: "朋友圈", value: "moment" },
    { label: "广场", value: "square" }
];

function imPiamomFormatAuditStatus(val) {
    var label = imPiamomLabelFromOptions(val, IM_PIAMOM_AUDIT_STATUS_OPTIONS);
    if (label === "-") {
        return label;
    }
    var cls = String(val) === "0" ? "label-warning" : (String(val) === "1" ? "label-success" : "label-default");
    return '<span class="label ' + cls + '">' + label + "</span>";
}

function imPiamomFormatTargetType(val) {
    return imPiamomLabelFromOptions(val, IM_PIAMOM_TARGET_TYPE_OPTIONS);
}

function imPiamomFormatVisibleStatus(val) {
    if (val == null || val === "") {
        return "-";
    }
    return String(val) === "1"
        ? '<span class="label label-success">正常</span>'
        : '<span class="label label-default">下架/隐藏</span>';
}

var IM_PIAMOM_NOTIFY_MSG_TYPE_OPTIONS = [
    { label: "朋友圈点赞", value: "moment_like" },
    { label: "朋友圈评论", value: "moment_comment" },
    { label: "广场点赞", value: "square_like" },
    { label: "广场评论", value: "square_comment" },
    { label: "举报下架(举报人)", value: "report_takedown_reporter" },
    { label: "举报下架(发帖人)", value: "report_takedown_publisher" }
];

var IM_PIAMOM_NOTIFY_TARGET_TYPE_OPTIONS = [
    { label: "朋友圈", value: "moment" },
    { label: "广场", value: "square" },
    { label: "举报", value: "report" }
];

var IM_PIAMOM_READ_STATUS_OPTIONS = [
    { label: "未读", value: 0 },
    { label: "已读", value: 1 }
];

function imPiamomFormatNotifyMsgType(val) {
    return imPiamomLabelFromOptions(val, IM_PIAMOM_NOTIFY_MSG_TYPE_OPTIONS);
}

function imPiamomFormatNotifyTargetType(val) {
    return imPiamomLabelFromOptions(val, IM_PIAMOM_NOTIFY_TARGET_TYPE_OPTIONS);
}

function imPiamomFormatReadStatus(val) {
    if (val == null || val === "") {
        return "-";
    }
    return String(val) === "1"
        ? '<span class="label label-success">已读</span>'
        : '<span class="label label-warning">未读</span>';
}

/** 列表媒体列默认最多展示位数（超出显示 +N，保持单行） */
var IM_LIST_MEDIA_COMPACT_MAX = 4;

/** 广场媒体：图片 + 视频链接 */
function imPiamomFormatSquareMedia(imageUrls, video, cacheKey, max) {
    var imgHtml = imPiamomFormatMedia(imageUrls, cacheKey, max);
    var videos = imPiamomParseImageUrls(video);
    if (imgHtml === "-" && !videos.length) {
        return "-";
    }
    if (videos.length && imgHtml !== "-") {
        var links = videos.map(function (src, i) {
            var safe = src.replace(/"/g, "&quot;");
            return '<a class="piamom-video-link" href="' + safe + '" target="_blank" rel="noopener noreferrer">视频' + (i + 1) + "</a>";
        }).join(" ");
        return imgHtml.replace("</div>", links + "</div>");
    }
    if (videos.length) {
        var onlyLinks = videos.map(function (src, i) {
            var safe = src.replace(/"/g, "&quot;");
            return '<a class="piamom-video-link" href="' + safe + '" target="_blank" rel="noopener noreferrer">视频' + (i + 1) + "</a>";
        }).join(" ");
        return '<div class="piamom-media-cell">' + onlyLinks + "</div>";
    }
    return imgHtml;
}
