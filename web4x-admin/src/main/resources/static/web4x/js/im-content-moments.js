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

var imContentMomentsVideoEventsBound = false;

function imContentMomentsInitVideoEvents() {
    if (imContentMomentsVideoEventsBound) {
        return;
    }
    imContentMomentsVideoEventsBound = true;
    $(document).on("click", ".moment-video-play", function (e) {
        e.preventDefault();
        e.stopPropagation();
        imContentMomentsOpenVideo($(this).attr("data-video-url"));
    });
}

function imContentMomentsOpenVideo(url) {
    if (!url || typeof layer === "undefined") {
        return;
    }
    if (typeof imPiamomNormalizeMediaUrl === "function") {
        url = imPiamomNormalizeMediaUrl(url);
    }
    if (!url) {
        return;
    }
    var safeSrc = String(url).replace(/"/g, "&quot;");
    layer.open({
        type: 1,
        title: "视频预览",
        area: ["720px", "480px"],
        shadeClose: true,
        content: '<div style="padding:10px;"><video controls autoplay playsinline style="width:100%;max-height:480px;" src="'
            + safeSrc + '"></video></div>'
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
    imContentMomentsInitVideoEvents();
    imInitTable(imApplyListMediaTableOptions({
        url: imContentMomentsApi + "/list",
        formId: "moments-form",
        queryParams: imContentMomentsQueryParams,
        responseHandler: imPageResponse,
        modalName: "朋友圈动态",
        columns: [
            {
                field: "u.idno",
                title: "用户ID号",
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
            imBuildListMediaColumn("imgs", { title: "图片", cachePrefix: "moments-imgs" }),
            imBuildListMediaColumn("vimg", {
                title: "视频封面",
                max: 1,
                cachePrefix: "moments-vimg"
            }),
            {
                field: "video",
                title: "视频",
                escape: false,
                formatter: function (v) {
                    if (!v) return "-";
                    return '<a class="btn btn-default btn-xs moment-video-play" href="javascript:void(0)" data-video-url="'
                        + imEscapeHtml(String(v)) + '"><i class="fa fa-play"></i> 查看视频</a>';
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
    }));
}
