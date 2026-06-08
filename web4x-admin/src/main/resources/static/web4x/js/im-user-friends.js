/**
 * IM 好友关系管理
 * - GET    /friend/friend/list
 * - POST   /friend/friend/save
 * - DELETE /friend/friend/delete
 */

var imUserFriendsApi = ctx + "friend/friend";

function imUserFriendsQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("user-friends-form");
    var filtered = imOmitEmptyParams(formValues);
    if (filtered.idno) {
        query["u.idno"] = filtered.idno;
        delete filtered.idno;
    }
    if (filtered.friendId) {
        query["uid2.idno"] = filtered.friendId;
        delete filtered.friendId;
    }
    return $.extend(query, filtered);
}

function imUserFriendsMemberField(row, memberKey, field) {
    if (row && row[memberKey] && row[memberKey][field]) {
        return row[memberKey][field];
    }
    return "";
}

function imUserFriendsFormatYesNo(val) {
    if (String(val) === "1") return "是";
    if (String(val) === "0") return "否";
    return "-";
}

function imUserFriendsCopyText(text) {
    if (!text) return;
    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(String(text)).then(function () {
            $.modal.msgSuccess("复制成功");
        }).catch(function () {
            $.modal.msgWarning("复制失败");
        });
        return;
    }
    var input = document.createElement("textarea");
    input.value = String(text);
    document.body.appendChild(input);
    input.select();
    try {
        document.execCommand("copy");
        $.modal.msgSuccess("复制成功");
    } catch (e) {
        $.modal.msgWarning("复制失败");
    }
    document.body.removeChild(input);
}

function imUserFriendsFormatCopyIdno(val) {
    if (!val) return "-";
    return '<span class="user-list-copy" onclick="imUserFriendsCopyText(\'' + String(val).replace(/'/g, "\\'") + '\')">' + val + "</span>";
}

function imUserFriendsDelete(id) {
    $.modal.confirm("确定删除该好友关系吗？", function () {
        $.ajax({
            url: imUserFriendsApi + "/delete",
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

function imUserFriendsResetAddForm() {
    $("#uf-user-id").val("");
    $("#uf-friend-id").val("");
    $("#uf-user-label").val("");
    $("#uf-friend-label").val("");
    $("#uf-mdr").val("");
    $("#uf-isTop").val("");
    $("#uf-bei").val("");
}

function imUserFriendsOpenAdd() {
    imUserFriendsResetAddForm();
    layer.open({
        type: 1,
        title: "添加好友",
        area: ["560px", "420px"],
        shadeClose: true,
        content: $("#user-friends-add-modal"),
        btn: ["保存", "取消"],
        yes: function (index) {
            var userId = $("#uf-user-id").val();
            var friendId = $("#uf-friend-id").val();
            if (!userId || !friendId) {
                $.modal.alertWarning("请完整选择用户与好友");
                return;
            }
            var payload = {
                "u.id": userId,
                "uid2.id": friendId
            };
            var mdr = $("#uf-mdr").val();
            var isTop = $("#uf-isTop").val();
            var bei = $("#uf-bei").val();
            if (mdr !== "") payload.mdr = mdr;
            if (isTop !== "") payload.isTop = isTop;
            if (bei) payload.bei = bei;

            $.ajax({
                url: imUserFriendsApi + "/save",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(payload),
                dataType: "json",
                beforeSend: imTableBeforeSend,
                success: function (res) {
                    if (res && (res.success === true || res.code === 200)) {
                        $.modal.msgSuccess((res && res.msg) ? res.msg : "保存成功");
                        layer.close(index);
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
    });
}

function imUserFriendsPickUser() {
    imMemberPickerOpen("选择用户", function (member) {
        $("#uf-user-id").val(member.id);
        $("#uf-user-label").val(member.nickname || member.idno || member.id);
    });
}

function imUserFriendsPickFriend() {
    imMemberPickerOpen("选择好友", function (member) {
        $("#uf-friend-id").val(member.id);
        $("#uf-friend-label").val(member.nickname || member.idno || member.id);
    });
}

function imUserFriendsInitTable(canDelete) {
    imInitTable({
        url: imUserFriendsApi + "/list",
        formId: "user-friends-form",
        queryParams: imUserFriendsQueryParams,
        responseHandler: imPageResponse,
        modalName: "好友关系",
        columns: [
            {
                field: "u.idno",
                title: "idno",
                sortable: true,
                formatter: function (val, row) {
                    return imUserFriendsFormatCopyIdno(imUserFriendsMemberField(row, "u", "idno") || val);
                }
            },
            {
                field: "u.nickname",
                title: "用户",
                sortable: true,
                formatter: function (val, row) {
                    return imUserFriendsMemberField(row, "u", "nickname") || val || "-";
                }
            },
            {
                field: "u.lianghao",
                title: "靓号",
                sortable: true,
                formatter: function (val, row) {
                    return imUserFriendsMemberField(row, "u", "lianghao") || val || "-";
                }
            },
            {
                field: "uid2.nickname",
                title: "好友",
                sortable: true,
                formatter: function (val, row) {
                    return imUserFriendsMemberField(row, "uid2", "nickname") || val || "-";
                }
            },
            {
                field: "uid2.idno",
                title: "好友ID",
                sortable: true,
                formatter: function (val, row) {
                    return imUserFriendsFormatCopyIdno(imUserFriendsMemberField(row, "uid2", "idno") || val);
                }
            },
            {
                field: "uid2.lianghao",
                title: "好友靓号",
                sortable: true,
                formatter: function (val, row) {
                    return imUserFriendsMemberField(row, "uid2", "lianghao") || val || "-";
                }
            },
            { field: "zimu", title: "首字母", sortable: true },
            { field: "bei", title: "备注", sortable: true },
            { field: "mdr", title: "是否免打扰", sortable: true, formatter: imUserFriendsFormatYesNo },
            { field: "isTop", title: "是否置顶", sortable: true, formatter: imUserFriendsFormatYesNo },
            { field: "createDate", title: "添加时间", sortable: true },
            {
                title: "操作",
                align: "center",
                formatter: function (value, row) {
                    if (!canDelete) return "-";
                    return '<a class="btn btn-danger btn-xs" href="javascript:void(0)" onclick="imUserFriendsDelete(\'' + row.id + '\')"><i class="fa fa-remove"></i>删除好友</a>';
                }
            }
        ]
    });
}
