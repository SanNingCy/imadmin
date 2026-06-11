/**
 * 广场朋友圈 - 用户主页统计
 * - GET    /admin/piamom/profileStat/page
 * - GET    /admin/piamom/follow/page
 * - DELETE /admin/piamom/follow/delete?id=
 * - GET    /admin/piamom/blacklist/page
 * - DELETE /admin/piamom/blacklist/delete?id=
 * - GET    /admin/piamom/user/likeRecord/page
 * - DELETE /admin/piamom/user/likeRecord/delete?id=
 */

var imPiamomProfileStatDrawerUserId = null;
var imPiamomProfileStatDrawerInited = false;
var imPiamomProfileStatCanDelete = false;
var imPiamomProfileStatActiveTab = "following";

function imPiamomProfileStatQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("piamom-profile-stat-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imPiamomProfileStatBuildDrawerQueryParams(params, mode) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    if (!imPiamomProfileStatDrawerUserId) {
        return query;
    }
    if (mode === "follower") {
        query.followerId = imPiamomProfileStatDrawerUserId;
    } else {
        query.userId = imPiamomProfileStatDrawerUserId;
    }
    return query;
}

function imPiamomProfileStatDeleteFollow(id) {
    $.modal.confirm("确定删除该关注关系吗？", function () {
        $.ajax({
            url: imPiamomApi + "/follow/delete",
            type: "DELETE",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "删除成功");
                    $("#pps-following-table").bootstrapTable("refresh");
                    $("#pps-follower-table").bootstrapTable("refresh");
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

function imPiamomProfileStatDeleteBlacklist(id) {
    $.modal.confirm("确定删除该黑名单记录吗？", function () {
        $.ajax({
            url: imPiamomApi + "/blacklist/delete",
            type: "DELETE",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "删除成功");
                    $("#pps-blacklist-table").bootstrapTable("refresh");
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

function imPiamomProfileStatDeleteLikeRecord(id) {
    $.modal.confirm("确定删除该获赞记录吗？", function () {
        $.ajax({
            url: imPiamomApi + "/user/likeRecord/delete",
            type: "DELETE",
            data: { id: id },
            dataType: "json",
            beforeSend: imTableBeforeSend,
            success: function (res) {
                if (res && (res.success === true || res.code === 200)) {
                    $.modal.msgSuccess((res && res.msg) ? res.msg : "删除成功");
                    $("#pps-like-table").bootstrapTable("refresh");
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

function imPiamomProfileStatDeleteAction(type, id) {
    if (type === "follow") {
        imPiamomProfileStatDeleteFollow(id);
    } else if (type === "blacklist") {
        imPiamomProfileStatDeleteBlacklist(id);
    } else if (type === "like") {
        imPiamomProfileStatDeleteLikeRecord(id);
    }
}

function imPiamomProfileStatInitDrawerTables() {
    if (imPiamomProfileStatDrawerInited) {
        return;
    }
    imPiamomProfileStatDrawerInited = true;

    imPiamomInitTable({
        id: "pps-following-table",
        url: imPiamomApi + "/follow/page",
        showSearch: false,
        showRefresh: false,
        showToggle: false,
        showColumns: false,
        queryParams: function (params) {
            return imPiamomProfileStatBuildDrawerQueryParams(params, "following");
        },
        responseHandler: imPageResponse,
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "userIdno", title: "被关注者ID号", width: 120 },
            { field: "userNickname", title: "被关注者昵称", width: 120, formatter: function (v) { return imFormatText(v, 16); } },
            { field: "followerIdno", title: "粉丝ID号", width: 120 },
            { field: "createdAt", title: "时间", width: 150 },
            {
                title: "操作",
                width: 80,
                formatter: function (value, row) {
                    if (!imPiamomProfileStatCanDelete) {
                        return "-";
                    }
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomProfileStatDeleteAction(\'follow\',\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>';
                }
            }
        ]
    });

    imPiamomInitTable({
        id: "pps-follower-table",
        url: imPiamomApi + "/follow/page",
        showSearch: false,
        showRefresh: false,
        showToggle: false,
        showColumns: false,
        queryParams: function (params) {
            return imPiamomProfileStatBuildDrawerQueryParams(params, "follower");
        },
        responseHandler: imPageResponse,
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "userIdno", title: "被关注者ID号", width: 120 },
            { field: "followerIdno", title: "粉丝ID号", width: 120 },
            { field: "followerNickname", title: "粉丝昵称", width: 120, formatter: function (v) { return imFormatText(v, 16); } },
            { field: "createdAt", title: "时间", width: 150 },
            {
                title: "操作",
                width: 80,
                formatter: function (value, row) {
                    if (!imPiamomProfileStatCanDelete) {
                        return "-";
                    }
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomProfileStatDeleteAction(\'follow\',\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>';
                }
            }
        ]
    });

    imPiamomInitTable({
        id: "pps-blacklist-table",
        url: imPiamomApi + "/blacklist/page",
        showSearch: false,
        showRefresh: false,
        showToggle: false,
        showColumns: false,
        queryParams: function (params) {
            return imPiamomProfileStatBuildDrawerQueryParams(params, "blacklist");
        },
        responseHandler: imPageResponse,
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "userIdno", title: "拉黑方ID号", width: 120 },
            { field: "blackUserIdno", title: "被拉黑ID号", width: 120 },
            { field: "blackUserNickname", title: "被拉黑昵称", width: 120, formatter: function (v) { return imFormatText(v, 16); } },
            { field: "createdAt", title: "时间", width: 150 },
            {
                title: "操作",
                width: 80,
                formatter: function (value, row) {
                    if (!imPiamomProfileStatCanDelete) {
                        return "-";
                    }
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomProfileStatDeleteAction(\'blacklist\',\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>';
                }
            }
        ]
    });

    imPiamomInitTable({
        id: "pps-like-table",
        url: imPiamomApi + "/user/likeRecord/page",
        showSearch: false,
        showRefresh: false,
        showToggle: false,
        showColumns: false,
        queryParams: function (params) {
            return imPiamomProfileStatBuildDrawerQueryParams(params, "like");
        },
        responseHandler: imPageResponse,
        columns: [
            { field: "id", title: "ID", width: 70 },
            { field: "fromUserIdno", title: "来源ID号", width: 120 },
            { field: "fromUserNickname", title: "来源昵称", width: 120, formatter: function (v) { return imFormatText(v, 16); } },
            { field: "targetType", title: "对象类型", width: 100, formatter: imPiamomFormatTargetType },
            { field: "targetId", title: "对象ID", width: 90 },
            { field: "createdAt", title: "时间", width: 150 },
            {
                title: "操作",
                width: 80,
                formatter: function (value, row) {
                    if (!imPiamomProfileStatCanDelete) {
                        return "-";
                    }
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imPiamomProfileStatDeleteAction(\'like\',\'' + row.id + '\')"><i class="fa fa-remove"></i>删除</a>';
                }
            }
        ]
    });
}

function imPiamomProfileStatRefreshDrawerTab(tab) {
    if (tab === "following") {
        $("#pps-following-table").bootstrapTable("refresh");
    } else if (tab === "follower") {
        $("#pps-follower-table").bootstrapTable("refresh");
    } else if (tab === "blacklist") {
        $("#pps-blacklist-table").bootstrapTable("refresh");
    } else if (tab === "like") {
        $("#pps-like-table").bootstrapTable("refresh");
    }
}

function imPiamomProfileStatSwitchTab(tab) {
    imPiamomProfileStatActiveTab = tab;
    imPiamomProfileStatRefreshDrawerTab(tab);
}

function imPiamomProfileStatOpenDrawer(userId) {
    imPiamomProfileStatDrawerUserId = userId;
    imPiamomProfileStatActiveTab = "following";
    $("#piamom-profile-stat-drawer-title").text("用户 " + userId + " 社交数据");
    imPiamomProfileStatInitDrawerTables();
    layer.open({
        type: 1,
        title: false,
        area: ["920px", "580px"],
        shadeClose: true,
        content: $("#piamom-profile-stat-drawer"),
        success: function () {
            imPiamomProfileStatRefreshDrawerTab("following");
        }
    });
}

function imPiamomProfileStatInitTable(canView, canDelete) {
    imPiamomProfileStatCanDelete = !!canDelete;
    imPiamomInitTable({
        url: imPiamomApi + "/profileStat/page",
        formId: "piamom-profile-stat-form",
        queryParams: imPiamomProfileStatQueryParams,
        responseHandler: imPageResponse,
        modalName: "用户主页统计",
        columns: [
            { field: "id", title: "ID", sortable: true, width: 80 },
            { field: "userId", title: "用户ID", sortable: true, width: 200, formatter: function (v) { return imFormatText(v, 24); } },
            { field: "userIdno", title: "ID号", width: 110 },
            { field: "userNickname", title: "昵称", width: 120, formatter: function (v) { return imFormatText(v, 16); } },
            { field: "followerCount", title: "粉丝数", sortable: true, width: 90 },
            { field: "followingCount", title: "关注数", sortable: true, width: 90 },
            { field: "likeCount", title: "获赞数", sortable: true, width: 90 },
            { field: "updatedAt", title: "更新时间", sortable: true, width: 160 },
            {
                title: "操作",
                align: "center",
                width: 100,
                formatter: function (value, row) {
                    if (!canView || !row.userId) {
                        return "-";
                    }
                    return '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imPiamomProfileStatOpenDrawer(\'' + row.userId + '\')"><i class="fa fa-list"></i>子列表</a>';
                }
            }
        ]
    });
}
