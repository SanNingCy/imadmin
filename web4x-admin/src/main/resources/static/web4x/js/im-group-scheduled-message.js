/**
 * 群定时消息管理
 * - GET /groupdingshi/groupDingshi/list
 */
var imGroupScheduledApi = ctx + "groupdingshi/groupDingshi";

function imGroupScheduledQueryParams(params) {
    return imGroupBuildQueryParams("group-scheduled-form", params);
}

function imGroupScheduledInitTable() {
    imInitTable(imApplyListMediaTableOptions({
        url: imGroupScheduledApi + "/list",
        formId: "group-scheduled-form",
        modalName: "群定时消息",
        sortName: "createDate",
        sortOrder: "desc",
        queryParams: imGroupScheduledQueryParams,
        columns: [
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
                title: "群",
                formatter: function (value, row) {
                    return imGroupFormatNestedText(row, "group", "name", 14);
                }
            },
            {
                field: "title",
                title: "文本",
                formatter: function (value) {
                    return imFormatText(value, 20);
                }
            },
            imBuildListMediaColumn("imgs", { title: "图片", cachePrefix: "group-scheduled" }),
            {
                field: "video",
                title: "视频",
                formatter: function (value) {
                    return imGroupFormatMediaLink(value);
                }
            },
            {
                field: "pdf",
                title: "文件",
                formatter: function (value) {
                    return imGroupFormatMediaLink(value);
                }
            },
            {
                field: "name",
                title: "文件、视频名称",
                formatter: function (value) {
                    return imFormatText(value, 16);
                }
            },
            {
                field: "type",
                title: "类型",
                sortable: true,
                formatter: function (value) {
                    return imGroupFormatXiaoxiType(value);
                }
            },
            {
                field: "time",
                title: "提醒时间",
                sortable: true,
                formatter: function (value) {
                    return imFormatText(value, 19);
                }
            },
            {
                field: "txtype",
                title: "提醒类型",
                sortable: true,
                formatter: function (value) {
                    return imGroupFormatTixingType(value);
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
    }));
}

$(function () {
    imGroupScheduledInitTable();
});
