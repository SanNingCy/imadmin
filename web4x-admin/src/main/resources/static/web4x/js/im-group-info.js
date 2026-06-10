/**
 * 群组信息列表
 * - GET    /group/group/list
 * - GET    /group/group/queryById
 * - POST   /group/group/save
 * - DELETE /group/group/delete
 * - GET    /groupitem/groupItem/list（群成员弹窗）
 */
var imGroupInfoApi = ctx + "group/group";
var imGroupInfoMemberApi = ctx + "groupitem/groupItem";

function imGroupInfoQueryParams(params) {
    return imGroupBuildQueryParams("group-info-form", params);
}

function imGroupInfoMemberQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("group-info-member-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imGroupInfoResolveGroup(res) {
    return imGroupResolveEntity(res, "group");
}

function imGroupInfoOpenModal(mode, id) {
    var readOnly = String(mode) === "view";
    $("#group-info-modal-form :input").prop("disabled", readOnly);
    $("#group-info-id").val(id || "");
    $("#group-info-name").val("");
    $("#group-info-idno").val("");
    $("#group-info-gonggao").val("");
    $("#group-info-jiange").val("0");
    $("#group-info-allJy").val("0");
    $("#group-info-openpic").val("0");

    if (id) {
        $.ajax({
            url: imGroupInfoApi + "/queryById",
            type: "GET",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    var info = imGroupInfoResolveGroup(res);
                    $("#group-info-name").val(info.name || "");
                    $("#group-info-idno").val(info.idno || "");
                    $("#group-info-gonggao").val(info.gonggao || "");
                    $("#group-info-jiange").val(info.jiange != null ? info.jiange : "0");
                    $("#group-info-allJy").val(info.allJy != null ? String(info.allJy) : "0");
                    $("#group-info-openpic").val(info.openpic != null ? String(info.openpic) : "0");
                } else {
                    $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                }
                imGroupInfoShowLayer(mode, readOnly);
            },
            error: function () {
                $.modal.alertWarning("获取详情失败");
                imGroupInfoShowLayer(mode, readOnly);
            }
        });
    } else {
        imGroupInfoShowLayer(mode, readOnly);
    }
}

function imGroupInfoShowLayer(mode, readOnly) {
    var titles = { view: "查看群信息", edit: "修改群信息" };
    layer.open({
        type: 1,
        title: titles[mode] || "群信息",
        area: ["520px", "520px"],
        shadeClose: true,
        content: $("#group-info-modal"),
        btn: readOnly ? ["关闭"] : ["保存", "取消"],
        yes: function (index) {
            if (readOnly) {
                layer.close(index);
                return;
            }
            imGroupInfoSave(index);
        },
        btn2: function (index) {
            layer.close(index);
        }
    });
}

function imGroupInfoSave(layerIndex) {
    var name = $.trim($("#group-info-name").val());
    if (!name) {
        $.modal.alertWarning("群组名称不能为空");
        return;
    }
    var payload = {
        id: $("#group-info-id").val() || undefined,
        name: name,
        idno: $("#group-info-idno").val(),
        gonggao: $("#group-info-gonggao").val(),
        jiange: $("#group-info-jiange").val(),
        allJy: $("#group-info-allJy").val(),
        openpic: $("#group-info-openpic").val()
    };
    $.ajax({
        url: imGroupInfoApi + "/save",
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify(payload),
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (res && (res.success === true || res.code === 200)) {
                $.modal.msgSuccess(res.msg || "保存成功");
                layer.close(layerIndex);
                $("#bootstrap-table").bootstrapTable("refresh");
            } else {
                $.modal.alertWarning((res && res.msg) ? res.msg : "保存失败");
            }
        },
        error: function () {
            $.modal.alertWarning("保存失败");
        }
    });
}

function imGroupInfoRemove(id) {
    $.modal.confirm("确定删除该群组吗？", function () {
        $.ajax({
            url: imGroupInfoApi + "/delete",
            type: "DELETE",
            dataType: "json",
            data: { ids: id },
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess(res.msg || "删除成功");
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

var imGroupInfoMemberLayerCtx = { index: null, layero: null };

function imGroupInfoMemberMeasureHeight(layero) {
    var $modal = $("#group-info-member-modal");
    var $tableWrap = $modal.find(".group-info-member-table-wrap");
    var $pagination = $tableWrap.find(".fixed-table-pagination");
    var height = 0;

    $modal.children().each(function () {
        height += $(this).outerHeight(true) || 0;
    });

    if ($pagination.length) {
        var modalTop = $modal.offset().top;
        var paginationBottom = $pagination.offset().top + $pagination.outerHeight(true);
        height = Math.max(height, paginationBottom - modalTop + 8);
    }

    if (layero && layero.length) {
        var $content = layero.find(".layui-layer-content");
        if ($content.length && $content[0].scrollHeight) {
            height = Math.max(height, $content[0].scrollHeight);
        }
    }

    return height;
}

function imGroupInfoMemberFitLayer() {
    if (imGroupInfoMemberLayerCtx.index == null || !imGroupInfoMemberLayerCtx.layero) {
        return;
    }
    setTimeout(function () {
        var layero = imGroupInfoMemberLayerCtx.layero;
        var index = imGroupInfoMemberLayerCtx.index;
        var $table = $("#group-info-member-table");
        if ($table.data("bootstrap.table")) {
            $table.bootstrapTable("resetView");
        }

        var $content = layero.find(".layui-layer-content");
        $content.css({ height: "auto", overflow: "visible" });

        var contentHeight = imGroupInfoMemberMeasureHeight(layero);
        if (!contentHeight) {
            return;
        }

        var titleHeight = layero.find(".layui-layer-title").outerHeight() || 42;
        var totalHeight = contentHeight + titleHeight + 2;
        var maxHeight = $(window).height() - 20;
        var finalHeight = Math.min(totalHeight, maxHeight);

        layer.style(index, {
            height: finalHeight + "px",
            top: Math.max(10, ($(window).height() - finalHeight) / 2) + "px"
        });

        if (finalHeight >= maxHeight) {
            $content.css({
                overflow: "auto",
                height: (maxHeight - titleHeight - 2) + "px"
            });
        }
    }, 120);
}

function imGroupInfoOpenMemberModal(groupId) {
    $("#group-info-member-group-id").val(groupId || "");
    layer.open({
        type: 1,
        title: "群成员列表",
        area: ["960px", "auto"],
        shadeClose: true,
        content: $("#group-info-member-modal"),
        success: function (layero, index) {
            imGroupInfoMemberLayerCtx = { index: index, layero: layero };
            if (!$("#group-info-member-table").data("bootstrap.table")) {
                $("#group-info-member-table").bootstrapTable({
                    url: imGroupInfoMemberApi + "/list",
                    method: "get",
                    sidePagination: "server",
                    pagination: true,
                    pageSize: 10,
                    pageList: [10, 20, 50],
                    sortName: "createDate",
                    sortOrder: "desc",
                    queryParams: imGroupInfoMemberQueryParams,
                    responseHandler: imPageResponse,
                    ajaxOptions: { beforeSend: imTableBeforeSend },
                    onLoadSuccess: imGroupInfoMemberFitLayer,
                    onPostBody: imGroupInfoMemberFitLayer,
                    onPageChange: imGroupInfoMemberFitLayer,
                    columns: [
                        {
                            field: "u.nickname",
                            title: "用户",
                            formatter: function (value, row) {
                                return imGroupFormatNestedText(row, "u", "nickname", 12);
                            }
                        },
                        {
                            field: "group.name",
                            title: "群组",
                            formatter: function (value, row) {
                                return imGroupFormatNestedText(row, "group", "name", 12);
                            }
                        },
                        {
                            field: "nickname",
                            title: "群昵称",
                            formatter: function (value) {
                                return imFormatText(value, 12);
                            }
                        },
                        {
                            field: "type",
                            title: "身份",
                            formatter: function (value) {
                                return imGroupFormatUserType(value);
                            }
                        },
                        {
                            field: "isjy",
                            title: "是否被禁言",
                            formatter: function (value) {
                                return imGroupFormatYesNo(value);
                            }
                        },
                        {
                            field: "updateDate",
                            title: "更新时间",
                            sortable: true,
                            formatter: function (value) {
                                return imFormatText(value, 19);
                            }
                        },
                        {
                            field: "createDate",
                            title: "添加时间",
                            sortable: true,
                            formatter: function (value) {
                                return imFormatText(value, 19);
                            }
                        }
                    ]
                });
            } else {
                $("#group-info-member-table").bootstrapTable("refresh");
            }
            imGroupInfoMemberFitLayer();
        },
        end: function () {
            imGroupInfoMemberLayerCtx = { index: null, layero: null };
        }
    });
}

function imGroupInfoMemberSearch() {
    $("#group-info-member-table").bootstrapTable("refresh");
}

function imGroupInfoMemberReset() {
    var groupId = $("#group-info-member-group-id").val();
    $("#group-info-member-form")[0].reset();
    $("#group-info-member-group-id").val(groupId);
    imGroupInfoMemberSearch();
}

function imGroupInfoInitTable(canView, canEdit, canDelete, canMember) {
    imInitTable(imApplyListMediaTableOptions({
        url: imGroupInfoApi + "/list",
        formId: "group-info-form",
        modalName: "群组信息",
        sortName: "createDate",
        sortOrder: "desc",
        queryParams: imGroupInfoQueryParams,
        columns: [
            {
                field: "u.id",
                title: "群主ID",
                cellStyle: imEllipsisCell(140),
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "u", "id", 20);
                }
            },
            {
                field: "u.nickname",
                title: "群主",
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "u", "nickname", 10);
                }
            },
            {
                field: "aikey",
                title: "群ai机器人key",
                formatter: function (value) {
                    return imFormatText(value, 16);
                }
            },
            imBuildListMediaColumn("icon", {
                title: "群头像",
                width: 80,
                max: 1,
                cachePrefix: "group-info-icon",
                format: function (v, row, cacheKey, max) {
                    return imFormatListMedia(row.icon != null ? row.icon : v, cacheKey, max);
                }
            }),
            {
                field: "name",
                title: "群组名称",
                sortable: true,
                formatter: function (value) {
                    return imFormatText(value, 16);
                }
            },
            {
                field: "idno",
                title: "群ID",
                sortable: true,
                formatter: function (value) {
                    return imFormatText(value, 12);
                }
            },
            imBuildListMediaColumn("qrcode", {
                title: "群名片二维码",
                width: 80,
                max: 1,
                cachePrefix: "group-info-qrcode",
                format: function (v, row, cacheKey, max) {
                    return imFormatListMedia(row.qrcode != null ? row.qrcode : v, cacheKey, max);
                }
            }),
            {
                field: "gonggao",
                title: "群公告",
                formatter: function (value) {
                    return imFormatText(value, 20);
                }
            },
            {
                field: "jiange",
                title: "群员发言间隔",
                formatter: function (value) {
                    return imGroupFormatJiange(value);
                }
            },
            {
                field: "allJy",
                title: "全员禁言",
                formatter: function (value) {
                    return imGroupFormatYesNo(value);
                }
            },
            {
                field: "openpic",
                title: "图片AI回复",
                formatter: function (value) {
                    return imGroupFormatYesNo(value);
                }
            },
            {
                field: "createDate",
                title: "创建时间",
                sortable: true,
                formatter: function (value) {
                    return imFormatText(value, 19);
                }
            },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    var actions = [];
                    if (canView) {
                        actions.push('<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imGroupInfoOpenModal(\'view\',\'' + row.id + '\')"><i class="fa fa-search"></i>查看</a> ');
                    }
                    if (canEdit) {
                        actions.push('<a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="imGroupInfoOpenModal(\'edit\',\'' + row.id + '\')"><i class="fa fa-edit"></i>修改</a> ');
                    }
                    if (canDelete) {
                        actions.push('<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imGroupInfoRemove(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a> ');
                    }
                    if (canMember) {
                        actions.push('<a class="btn btn-primary btn-xs" href="javascript:void(0)" onclick="imGroupInfoOpenMemberModal(\''
                            + row.id + '\')">群成员</a>');
                    }
                    return actions.join("");
                }
            }
        ]
    }));
}
