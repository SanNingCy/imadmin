/**
 * 群欢迎语配置
 * - GET /grouphuanying/groupHuanying/list
 */
var imGroupWelcomeApi = ctx + "grouphuanying/groupHuanying";

function imGroupWelcomeQueryParams(params) {
    return imGroupBuildQueryParams("group-welcome-form", params);
}

function imGroupWelcomeInitTable() {
    imPiamomInitMediaEvents();
    imInitTable({
        url: imGroupWelcomeApi + "/list",
        formId: "group-welcome-form",
        modalName: "群欢迎语",
        sortName: "createDate",
        sortOrder: "desc",
        queryParams: imGroupWelcomeQueryParams,
        escape: false,
        onPostBody: function () {
            imPiamomBindMediaImagesIn($("#bootstrap-table"));
        },
        columns: [
            {
                field: "group.idno",
                title: "群IDNO",
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
            {
                field: "imgs",
                title: "图片",
                width: 300,
                escape: false,
                cellStyle: function () {
                    return { css: { "text-align": "left", "vertical-align": "middle" } };
                },
                formatter: function (value, row) {
                    return imPiamomFormatMedia(value, "group-welcome-" + row.id);
                }
            },
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
    imGroupWelcomeInitTable();
});
