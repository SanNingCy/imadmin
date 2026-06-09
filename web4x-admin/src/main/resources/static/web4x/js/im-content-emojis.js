/**
 * IM 表情包管理
 * - GET /emoji/emoji/list
 */

var imContentEmojisApi = ctx + "emoji/emoji";

function imContentEmojisQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("emojis-form");
    if (formValues.uid) query["u.id"] = formValues.uid;
    return imOmitEmptyParams(query);
}

function imContentEmojisInitTable() {
    imInitListMediaPreview();
    imInitTable({
        url: imContentEmojisApi + "/list",
        formId: "emojis-form",
        queryParams: imContentEmojisQueryParams,
        responseHandler: imPageResponse,
        modalName: "表情包",
        escape: false,
        onPostBody: function () {
            imBindListMediaPreview($("#bootstrap-table"));
        },
        columns: [
            {
                field: "u.id",
                title: "用户id",
                sortable: true,
                formatter: function (value, row) {
                    return row.u && row.u.id ? row.u.id : "-";
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
                field: "img",
                title: "表情",
                sortable: true,
                width: 80,
                escape: false,
                ellipsis: false,
                cellStyle: function () {
                    return { css: { "text-align": "left", "vertical-align": "middle" } };
                },
                formatter: function (value, row) {
                    return imFormatListMedia(value, "content-emojis-img-" + row.id);
                }
            },
            { field: "createDate", title: "添加时间", sortable: true }
        ]
    });
}
