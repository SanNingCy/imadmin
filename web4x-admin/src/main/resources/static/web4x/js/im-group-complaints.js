/**
 * 群投诉记录
 * - GET    /tousu/tousu/list
 * - GET    /tousu/tousu/queryById
 * - DELETE /tousu/tousu/delete
 */
var imGroupComplaintsApi = ctx + "tousu/tousu";

function imGroupComplaintsQueryParams(params) {
    return imGroupBuildQueryParams("group-complaints-form", params);
}

function imGroupComplaintsOpenView(id) {
    $.ajax({
        url: imGroupComplaintsApi + "/queryById",
        type: "GET",
        data: { id: id },
        dataType: "json",
        beforeSend: imTableBeforeSend,
        success: function (res) {
            if (!(res && (res.success === true || res.code === 200))) {
                $.modal.alertWarning((res && res.msg) ? res.msg : "获取详情失败");
                return;
            }
            var info = imGroupResolveEntity(res, "tousu");
            var userText = (info.u && info.u.id ? info.u.id : "-")
                + (info.u && info.u.nickname ? (" / " + info.u.nickname) : "");
            var groupText = (info.group && info.group.id ? info.group.id : "-")
                + (info.group && info.group.name ? (" / " + info.group.name) : "");
            $("#complaints-view-user").text(userText);
            $("#complaints-view-group").text(groupText);
            $("#complaints-view-reason").text(info.reason || "-");
            $("#complaints-view-log").text(info.log || "-");
            $("#complaints-view-imgs").html(imGroupFormatImages(info.imgs, 72));
            $("#complaints-view-info").text(info.info || "-");
            $("#complaints-view-createDate").text(info.createDate || "-");

            layer.open({
                type: 1,
                title: "投诉详情",
                area: ["560px", "560px"],
                shadeClose: true,
                content: $("#group-complaints-modal"),
                btn: ["关闭"]
            });
        },
        error: function () {
            $.modal.alertWarning("获取详情失败");
        }
    });
}

function imGroupComplaintsRemove(id) {
    $.modal.confirm("确定删除该投诉记录吗？", function () {
        $.ajax({
            url: imGroupComplaintsApi + "/delete",
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

function imGroupComplaintsInitTable() {
    imInitTable({
        url: imGroupComplaintsApi + "/list",
        formId: "group-complaints-form",
        modalName: "群投诉记录",
        sortName: "createDate",
        sortOrder: "desc",
        queryParams: imGroupComplaintsQueryParams,
        columns: [
            {
                field: "u.id",
                title: "用户ID",
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "u", "id", 20);
                }
            },
            {
                field: "u.nickname",
                title: "用户",
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "u", "nickname", 12);
                }
            },
            {
                field: "group.id",
                title: "群组ID",
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "group", "id", 20);
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
                field: "reason",
                title: "投诉原因",
                formatter: function (value) {
                    return imFormatText(value, 16);
                }
            },
            {
                field: "log",
                title: "聊天记录",
                formatter: function (value) {
                    return imFormatText(value, 20);
                }
            },
            {
                field: "imgs",
                title: "图片",
                formatter: function (value) {
                    return imGroupFormatImages(value, 40);
                }
            },
            {
                field: "info",
                title: "描述",
                formatter: function (value) {
                    return imFormatText(value, 16);
                }
            },
            {
                field: "createDate",
                title: "添加时间",
                sortable: true,
                formatter: function (value) {
                    return imFormatText(value, 19);
                }
            },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    return '<a class="btn btn-info btn-xs" href="javascript:void(0)" onclick="imGroupComplaintsOpenView(\''
                        + row.id + '\')"><i class="fa fa-search"></i>查看</a> '
                        + '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imGroupComplaintsRemove(\''
                        + row.id + '\')"><i class="fa fa-remove"></i>删除</a>';
                }
            }
        ]
    });
}

$(function () {
    imGroupComplaintsInitTable();
});
