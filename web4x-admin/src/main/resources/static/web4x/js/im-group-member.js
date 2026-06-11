/**
 * 群成员管理
 * - GET /groupitem/groupItem/list
 */
var imGroupMemberApi = ctx + "groupitem/groupItem";

function imGroupMemberQueryParams(params) {
    return imGroupBuildQueryParams("group-member-form", params);
}

function imGroupMemberInitTable() {
    imInitTable({
        url: imGroupMemberApi + "/list",
        formId: "group-member-form",
        modalName: "群成员",
        sortName: "createDate",
        sortOrder: "desc",
        queryParams: imGroupMemberQueryParams,
        columns: [
            {
                field: "u.idno",
                title: "用户ID号",
                sortable: true,
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "u", "idno", 12);
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
                field: "group.idno",
                title: "群组ID号",
                sortable: true,
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "group", "idno", 12);
                }
            },
            {
                field: "group.name",
                title: "群组",
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "group", "name", 14);
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
                field: "jyTime",
                title: "禁言到期时间",
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
}

$(function () {
    imGroupMemberInitTable();
});
