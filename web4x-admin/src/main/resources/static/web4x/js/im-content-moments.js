/**
 * IM 朋友圈动态管理
 * - GET    /dy/dy/list
 * - DELETE /dy/dy/delete?ids=
 */

var imContentMomentsApi = ctx + "dy/dy";

function imContentMomentsQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("moments-form");
    if (formValues.idno) query["u.idno"] = formValues.idno;
    if (formValues.lianghao) query["u.lianghao"] = formValues.lianghao;
    if (formValues.info) query.info = formValues.info;
    return imOmitEmptyParams(query);
}

function imContentMomentsSplitUrls(value) {
    if (!value) return [];
    return String(value).split("|").map(function (item) {
        return $.trim(item);
    }).filter(function (item) {
        return !!item;
    });
}

function imContentMomentsEscapeHtml(text) {
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

function imContentMomentsFormatEllipsis(val) {
    if (val == null || val === "") return "-";
    var text = String(val);
    var safe = imContentMomentsEscapeHtml(text);
    return '<span class="moment-ellipsis-inner" title="' + safe + '">' + safe + "</span>";
}

function imContentMomentsFormatImages(val) {
    var urls = imContentMomentsSplitUrls(val);
    if (!urls.length) return "-";
    return urls.map(function (src) {
        var safe = src.replace(/"/g, "&quot;");
        return '<img class="moment-media-img" src="' + safe + '" onclick="imContentMomentsPreviewImage(' + JSON.stringify(src) + ')" alt="img"/>';
    }).join("");
}

function imContentMomentsPreviewImage(src) {
    if (!src || typeof layer === "undefined") return;
    layer.open({
        type: 1,
        title: false,
        closeBtn: 1,
        shadeClose: true,
        area: ["auto", "auto"],
        content: '<img src="' + src.replace(/"/g, "&quot;") + '" style="max-width:90vw;max-height:80vh;"/>'
    });
}

function imContentMomentsOpenVideo(url) {
    if (!url) return;
    $("#moment-video-player").attr("src", url);
    layer.open({
        type: 1,
        title: "视频预览",
        area: ["720px", "480px"],
        shadeClose: true,
        content: $("#moment-video-modal"),
        end: function () {
            var player = document.getElementById("moment-video-player");
            if (player) {
                player.pause();
                player.removeAttribute("src");
            }
        }
    });
}

function imContentMomentsGoComments(dyId) {
    window.location.href = ctx + "moment-comments?dyId=" + encodeURIComponent(dyId);
}

function imContentMomentsRemove(id) {
    $.modal.confirm("确定删除该动态吗？", function () {
        $.ajax({
            url: imContentMomentsApi + "/delete",
            type: "DELETE",
            data: { ids: id },
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

function imContentMomentsInitTable(canView, canDelete) {
    imInitTable({
        url: imContentMomentsApi + "/list",
        formId: "moments-form",
        queryParams: imContentMomentsQueryParams,
        responseHandler: imPageResponse,
        modalName: "朋友圈动态",
        columns: [
            {
                field: "u.idno",
                title: "用户IDNO",
                sortable: true,
                class: "moment-ellipsis",
                formatter: function (value, row) {
                    return imContentMomentsFormatEllipsis(row.u ? row.u.idno : "-");
                }
            },
            {
                field: "u.lianghao",
                title: "靓号",
                sortable: true,
                formatter: function (value, row) {
                    var val = row.u ? row.u.lianghao : "";
                    return val || "-";
                }
            },
            {
                field: "u.nickname",
                title: "用户",
                sortable: true,
                formatter: function (value, row) {
                    return row.u && row.u.nickname ? row.u.nickname : "-";
                }
            },
            {
                field: "info",
                title: "内容",
                sortable: true,
                class: "moment-ellipsis",
                formatter: function (v) { return imContentMomentsFormatEllipsis(v); }
            },
            {
                field: "imgs",
                title: "图片",
                formatter: function (v) { return imContentMomentsFormatImages(v); }
            },
            {
                field: "vimg",
                title: "视频封面",
                formatter: function (v) { return imContentMomentsFormatImages(v); }
            },
            {
                field: "video",
                title: "视频",
                formatter: function (v) {
                    if (!v) return "-";
                    return '<a class="btn btn-default btn-xs" href="javascript:void(0)" onclick="imContentMomentsOpenVideo(' + JSON.stringify(String(v)) + ')"><i class="fa fa-play"></i> 查看视频</a>';
                }
            },
            { field: "createDate", title: "添加时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imContentMomentsGoComments(\'' + row.id + '\')"><i class="fa fa-comments"></i>评论列表</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imContentMomentsRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>');
                    }
                    return actions.length ? actions.join("") : "-";
                }
            }
        ]
    });
}
