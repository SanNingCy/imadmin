/**
 * 群升级记录
 * - GET /groupuplog/groupUplog/list
 */
var imGroupUpgradeLogsApi = ctx + "groupuplog/groupUplog";

function imGroupUpgradeLogsQueryParams(params) {
    return imGroupBuildQueryParams("group-upgrade-logs-form", params);
}

function imGroupUpgradeLogsInitTable() {
    imInitTable({
        url: imGroupUpgradeLogsApi + "/list",
        formId: "group-upgrade-logs-form",
        modalName: "群升级记录",
        sortName: "createDate",
        sortOrder: "desc",
        queryParams: imGroupUpgradeLogsQueryParams,
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
                title: "用户昵称",
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "u", "nickname", 12);
                }
            },
            {
                field: "group.name",
                title: "群组昵称",
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "group", "name", 14);
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
                field: "createDate",
                title: "创建时间",
                sortable: true,
                formatter: function (value) {
                    return imFormatText(value, 19);
                }
            }
        ]
    });
}

$(function () {
    imGroupUpgradeLogsInitTable();
});
