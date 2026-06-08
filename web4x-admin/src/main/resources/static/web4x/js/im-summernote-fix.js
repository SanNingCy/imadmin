/**
 * Summernote overlay z-index fix: stay above visible layui-layer and centered.
 */
(function ($) {
    "use strict";
    if (!$) {
        return;
    }

    var LAYER_BASE_Z = 19891014;

    function imSummernoteScanLayerZIndex(win) {
        var max = 0;
        try {
            if (!win || !win.$) {
                return max;
            }
            win.$(".layui-layer:visible").each(function () {
                var z = parseInt(win.$(this).css("z-index"), 10);
                if (!isNaN(z) && z > max) {
                    max = z;
                }
            });
        } catch (e) {
            /* cross-origin iframe */
        }
        return max;
    }

    function imSummernoteMaxLayerZIndex() {
        var max = Math.max(LAYER_BASE_Z, imSummernoteScanLayerZIndex(window));
        try {
            if (window.top && window.top !== window) {
                max = Math.max(max, imSummernoteScanLayerZIndex(window.top));
            }
        } catch (e) {
            /* cross-origin iframe */
        }
        return max;
    }

    function imSummernoteCenterModal($modal) {
        if (!$modal || !$modal.length) {
            return;
        }
        $modal.css({
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: "16px"
        });
        $modal.find(".modal-dialog").css({
            margin: "0 auto"
        });
    }

    function imSummernoteRaiseOverlayZIndex() {
        var z = imSummernoteMaxLayerZIndex() + 20;
        var $modal = $("body > .note-modal.in");
        $modal.css("z-index", z);
        imSummernoteCenterModal($modal);
        $("body > .modal-backdrop.in:visible").last().css("z-index", z - 1);
        $(".note-popover:visible, .note-air-popover:visible").css("z-index", z);
        $(".note-dropdown-menu:visible, .note-btn-group.open .note-dropdown-menu").css("z-index", z);
    }

    $(document).on("show.bs.modal shown.bs.modal", "body > .note-modal", function () {
        imSummernoteRaiseOverlayZIndex();
    });

    $(document).on("mousedown", ".note-editable", function () {
        setTimeout(imSummernoteRaiseOverlayZIndex, 0);
        setTimeout(imSummernoteRaiseOverlayZIndex, 50);
    });

    $(document).on("click", ".note-toolbar .note-btn, .note-toolbar .note-dropdown-toggle", function () {
        setTimeout(imSummernoteRaiseOverlayZIndex, 0);
    });
})(jQuery);
