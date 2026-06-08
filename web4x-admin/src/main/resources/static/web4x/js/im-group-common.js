/**
 * 群组管理公共工具（列表格式化、查询参数等）
 */

function imGroupResolveEntity(res, key) {
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

function imGroupNestedField(row, parentKey, field) {
    if (row && row[parentKey] && row[parentKey][field] != null && row[parentKey][field] !== "") {
        return row[parentKey][field];
    }
    return null;
}

function imGroupBuildQueryParams(formId, params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON(formId);
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imGroupFormatYesNo(val) {
    if (String(val) === "1") {
        return "是";
    }
    if (String(val) === "0") {
        return "否";
    }
    return "-";
}

function imGroupFormatYesNoLabel(val) {
    if (String(val) === "1") {
        return '<span class="label label-success">是</span>';
    }
    if (String(val) === "0") {
        return '<span class="label label-default">否</span>';
    }
    return "-";
}

function imGroupFormatUserType(val) {
    var map = {
        "1": '<span class="label label-success">群主</span>',
        "2": '<span class="label label-primary">管理员</span>',
        "3": '<span class="label label-default">普通成员</span>'
    };
    return map[String(val)] || "-";
}

function imGroupFormatXiaoxiType(val) {
    var map = {
        "1": "纯文本",
        "2": "文本+图片",
        "3": "文本+视频",
        "4": "文本+文件"
    };
    return map[String(val)] || (val || "-");
}

function imGroupFormatTixingType(val) {
    var map = {
        "1": "仅一次",
        "2": "重复"
    };
    return map[String(val)] || (val || "-");
}

function imGroupFormatNestedText(row, parentKey, field, maxLen) {
    var value = imGroupNestedField(row, parentKey, field);
    return imFormatText(value, maxLen);
}

function imGroupNormalizePreviewUrls(urls, startIndex) {
    var list = [];
    if (Array.isArray(urls)) {
        list = urls.filter(function (item) {
            return !!item;
        });
    } else if (urls) {
        list = [urls];
    }
    var index = startIndex == null ? 0 : parseInt(startIndex, 10);
    if (isNaN(index) || index < 0) {
        index = 0;
    }
    if (list.length && index >= list.length) {
        index = list.length - 1;
    }
    return { urls: list, index: index };
}

function imGroupParsePreviewImagesAttr(value) {
    if (!value) {
        return [];
    }
    if (Array.isArray(value)) {
        return value;
    }
    try {
        var parsed = JSON.parse(value);
        return Array.isArray(parsed) ? parsed : [];
    } catch (e) {
        return [];
    }
}

function imGroupEnsureImageViewerDom() {
    if ($("#im-group-image-viewer-mask").length) {
        return $("#im-group-image-viewer-mask");
    }
    var html = ''
        + '<div id="im-group-image-viewer-mask" class="im-group-image-viewer-mask">'
        + '  <div class="im-group-image-viewer-dialog">'
        + '    <div class="im-group-image-viewer-stage">'
        + '      <button type="button" class="im-group-image-viewer-nav prev is-hidden" data-im-viewer-action="prev" title="上一张"><i class="fa fa-chevron-left"></i></button>'
        + '      <div class="im-group-image-viewer-img-wrap">'
        + '        <img class="im-group-image-viewer-img" alt="图片预览"/>'
        + '      </div>'
        + '      <button type="button" class="im-group-image-viewer-nav next is-hidden" data-im-viewer-action="next" title="下一张"><i class="fa fa-chevron-right"></i></button>'
        + '      <div class="im-group-image-viewer-counter is-hidden">1 / 1</div>'
        + '    </div>'
        + '    <div class="im-group-image-viewer-toolbar">'
        + '      <button type="button" class="btn btn-default btn-sm" data-im-viewer-action="zoom-in" title="放大"><i class="fa fa-search-plus"></i> 放大</button>'
        + '      <button type="button" class="btn btn-default btn-sm" data-im-viewer-action="zoom-out" title="缩小"><i class="fa fa-search-minus"></i> 缩小</button>'
        + '      <button type="button" class="btn btn-default btn-sm" data-im-viewer-action="rotate-left" title="左旋转"><i class="fa fa-rotate-left"></i> 左转</button>'
        + '      <button type="button" class="btn btn-default btn-sm" data-im-viewer-action="rotate-right" title="右旋转"><i class="fa fa-rotate-right"></i> 右转</button>'
        + '      <button type="button" class="btn btn-default btn-sm" data-im-viewer-action="reset" title="重置"><i class="fa fa-refresh"></i> 重置</button>'
        + '      <button type="button" class="btn btn-warning btn-sm" data-im-viewer-action="close" title="关闭"><i class="fa fa-close"></i> 关闭</button>'
        + '    </div>'
        + '  </div>'
        + '</div>';
    return $(html).appendTo("body");
}

function imGroupCloseImageViewer() {
    var $mask = $("#im-group-image-viewer-mask");
    $(document).off(".imGroupImageViewerDrag");
    $mask.removeClass("active");
    $("body").removeClass("im-group-image-viewer-open");
    $mask.find(".im-group-image-viewer-stage").removeClass("is-dragging");
    $mask.find(".im-group-image-viewer-img").removeClass("is-dragging").attr("src", "").css("transform", "");
}

function imGroupBindImageViewer($dialog, urls, startIndex) {
    var gallery = imGroupNormalizePreviewUrls(urls, startIndex);
    var $stage = $dialog.find(".im-group-image-viewer-stage");
    var $img = $dialog.find(".im-group-image-viewer-img");
    var $prev = $dialog.find(".im-group-image-viewer-nav.prev");
    var $next = $dialog.find(".im-group-image-viewer-nav.next");
    var $counter = $dialog.find(".im-group-image-viewer-counter");
    var state = {
        urls: gallery.urls,
        index: gallery.index,
        scale: 1,
        rotate: 0,
        x: 0,
        y: 0,
        dragging: false,
        moved: false,
        startX: 0,
        startY: 0,
        originX: 0,
        originY: 0
    };

    function applyTransform() {
        $img.css("transform", "translate(" + state.x + "px," + state.y + "px) scale(" + state.scale
            + ") rotate(" + state.rotate + "deg)");
    }

    function resetView() {
        state.scale = 1;
        state.rotate = 0;
        state.x = 0;
        state.y = 0;
        applyTransform();
    }

    function updateGalleryUi() {
        var hasMany = state.urls.length > 1;
        $prev.toggleClass("is-hidden", !hasMany);
        $next.toggleClass("is-hidden", !hasMany);
        $counter.toggleClass("is-hidden", !hasMany);
        if (hasMany) {
            $counter.text((state.index + 1) + " / " + state.urls.length);
        }
    }

    function showImage(index) {
        if (!state.urls.length) {
            return;
        }
        if (index < 0) {
            index = state.urls.length - 1;
        } else if (index >= state.urls.length) {
            index = 0;
        }
        state.index = index;
        $img.attr("src", state.urls[index]);
        resetView();
        updateGalleryUi();
    }

    function showPrev() {
        if (state.urls.length <= 1) {
            return;
        }
        showImage(state.index - 1);
    }

    function showNext() {
        if (state.urls.length <= 1) {
            return;
        }
        showImage(state.index + 1);
    }

    function endDrag() {
        if (!state.dragging) {
            return;
        }
        state.dragging = false;
        $stage.removeClass("is-dragging");
        $img.removeClass("is-dragging");
        $(document).off(".imGroupImageViewerDrag");
    }

    $dialog.off("click.imViewer").on("click.imViewer", "[data-im-viewer-action]", function () {
        var action = $(this).data("im-viewer-action");
        if (action === "zoom-in") {
            state.scale = Math.min(5, +(state.scale + 0.2).toFixed(2));
        } else if (action === "zoom-out") {
            state.scale = Math.max(0.2, +(state.scale - 0.2).toFixed(2));
        } else if (action === "rotate-left") {
            state.rotate -= 90;
        } else if (action === "rotate-right") {
            state.rotate += 90;
        } else if (action === "reset") {
            resetView();
            return;
        } else if (action === "prev") {
            showPrev();
            return;
        } else if (action === "next") {
            showNext();
            return;
        } else if (action === "close") {
            imGroupCloseImageViewer();
            return;
        }
        applyTransform();
    });

    $stage.off("wheel.imViewer").on("wheel.imViewer", function (e) {
        e.preventDefault();
        if (e.originalEvent.deltaY < 0) {
            state.scale = Math.min(5, +(state.scale + 0.1).toFixed(2));
        } else {
            state.scale = Math.max(0.2, +(state.scale - 0.1).toFixed(2));
        }
        applyTransform();
    });

    $stage.off("mousedown.imViewer").on("mousedown.imViewer", function (e) {
        if (e.button !== 0 || $(e.target).closest("[data-im-viewer-action]").length) {
            return;
        }
        e.preventDefault();
        state.dragging = true;
        state.moved = false;
        state.startX = e.clientX;
        state.startY = e.clientY;
        state.originX = state.x;
        state.originY = state.y;
        $stage.addClass("is-dragging");
        $img.addClass("is-dragging");
        $(document).off(".imGroupImageViewerDrag").on("mousemove.imGroupImageViewerDrag", function (moveEvent) {
            if (!state.dragging) {
                return;
            }
            if (Math.abs(moveEvent.clientX - state.startX) > 3 || Math.abs(moveEvent.clientY - state.startY) > 3) {
                state.moved = true;
            }
            state.x = state.originX + (moveEvent.clientX - state.startX);
            state.y = state.originY + (moveEvent.clientY - state.startY);
            applyTransform();
        }).on("mouseup.imGroupImageViewerDrag", function (upEvent) {
            if (state.dragging && !state.moved && state.urls.length > 1) {
                var stageWidth = $stage.innerWidth();
                var offsetLeft = $stage.offset().left;
                var clickX = upEvent.clientX - offsetLeft;
                if (clickX > stageWidth * 0.55) {
                    showNext();
                } else if (clickX < stageWidth * 0.45) {
                    showPrev();
                }
            }
            endDrag();
        });
    });

    showImage(state.index);
}

function imGroupOpenImagePreview(urls, startIndex) {
    var gallery = imGroupNormalizePreviewUrls(urls, startIndex);
    if (!gallery.urls.length) {
        return;
    }
    var $mask = imGroupEnsureImageViewerDom();
    var $dialog = $mask.find(".im-group-image-viewer-dialog");
    $mask.off("click.imViewerMask").on("click.imViewerMask", function (e) {
        if (e.target === $mask[0]) {
            imGroupCloseImageViewer();
        }
    });
    $dialog.off("click.imViewerDialog").on("click.imViewerDialog", function (e) {
        e.stopPropagation();
    });
    imGroupBindImageViewer($dialog, gallery.urls, gallery.index);
    $mask.addClass("active");
    $("body").addClass("im-group-image-viewer-open");
}

(function imGroupImageViewerEscInit() {
    if (typeof jQuery === "undefined" || jQuery.imGroupImageViewerEscBound) {
        return;
    }
    jQuery.imGroupImageViewerEscBound = true;
    jQuery(document).on("keyup.imGroupImageViewer", function (e) {
        if (!jQuery("#im-group-image-viewer-mask").hasClass("active")) {
            return;
        }
        if (e.key === "Escape") {
            imGroupCloseImageViewer();
            return;
        }
        if (e.key === "ArrowRight") {
            jQuery("#im-group-image-viewer-mask").find("[data-im-viewer-action='next']").trigger("click");
        } else if (e.key === "ArrowLeft") {
            jQuery("#im-group-image-viewer-mask").find("[data-im-viewer-action='prev']").trigger("click");
        }
    });
})();

function imGroupFormatImage(url, size, extraClass, galleryUrls, galleryIndex) {
    if (!url) {
        return "-";
    }
    var wh = size || 40;
    var className = "im-group-preview-img";
    if (extraClass) {
        className += " " + extraClass;
    }
    var urls = galleryUrls && galleryUrls.length ? galleryUrls : [url];
    var index = galleryIndex == null ? 0 : galleryIndex;
    var safeUrl = String(url).replace(/'/g, "&#39;");
    var imagesAttr = imEscapeHtml(JSON.stringify(urls));
    return '<img class="' + className + '" data-im-group-images="' + imagesAttr + '" data-im-group-index="'
        + index + '" src="' + safeUrl + '" style="width:' + wh + 'px;height:' + wh
        + 'px;object-fit:cover;cursor:pointer;" alt=""/>';
}

function imGroupSplitMedia(val) {
    if (!val) {
        return [];
    }
    return String(val).split(/[|,]/).map(function (item) {
        return $.trim(item);
    }).filter(function (item) {
        return !!item;
    });
}

function imGroupFormatImages(val, size) {
    var urls = imGroupSplitMedia(val);
    if (!urls.length) {
        return "-";
    }
    var wh = size || 40;
    return urls.map(function (url, index) {
        return imGroupFormatImage(url, wh, null, urls, index);
    }).join(" ");
}

function imGroupFormatMediaLink(val) {
    if (!val) {
        return "-";
    }
    var text = String(val);
    var name = text.substring(text.lastIndexOf("/") + 1);
    try {
        name = decodeURIComponent(name);
    } catch (e) {
        // ignore
    }
    return '<a href="' + imEscapeHtml(text) + '" target="_blank" rel="noreferrer">'
        + imEscapeHtml(name) + "</a>";
}

function imGroupFormatJiange(val) {
    if (val == null || val === "") {
        return "-";
    }
    return val + "秒";
}

(function imGroupImagePreviewInit() {
    if (typeof jQuery === "undefined" || jQuery.imGroupImagePreviewBound) {
        return;
    }
    jQuery.imGroupImagePreviewBound = true;
    jQuery(document).on("click", "img.im-group-preview-img", function (e) {
        e.stopPropagation();
        var $img = jQuery(this);
        var urls = imGroupParsePreviewImagesAttr($img.attr("data-im-group-images"));
        if (!urls.length) {
            urls = [$img.attr("src")];
        }
        var index = parseInt($img.attr("data-im-group-index"), 10);
        imGroupOpenImagePreview(urls, isNaN(index) ? 0 : index);
    });
})();
