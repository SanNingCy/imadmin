/**
 * IM 群红包领取明细（接口：/grouphongbaolog/groupHongbaoLog/*）
 */
var imGroupHongbaoLogApi = ctx + "grouphongbaolog/groupHongbaoLog";

function imGroupHongbaoLogQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("group-hongbao-log-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imGroupHongbaoLogNestedField(row, parentKey, field) {
    if (row && row[parentKey] && row[parentKey][field] != null && row[parentKey][field] !== "") {
        return row[parentKey][field];
    }
    return null;
}

function imGroupHongbaoLogEscapeHtml(text) {
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

function imGroupHongbaoLogEllipsisCell(maxWidth) {
    return function () {
        return {
            css: {
                "max-width": maxWidth + "px",
                "overflow": "hidden",
                "white-space": "nowrap",
                "text-overflow": "ellipsis"
            }
        };
    };
}

/** 限制展示长度，鼠标悬浮 title 显示完整内容 */
function imGroupHongbaoLogFormatText(value, maxLen) {
    if (value == null || value === "") {
        return "-";
    }
    var text = String(value);
    var display = text;
    if (maxLen && text.length > maxLen) {
        display = text.substring(0, maxLen) + "...";
    }
    var style = "display:block;max-width:100%;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;";
    return '<span style="' + style + '" title="' + imGroupHongbaoLogEscapeHtml(text) + '">'
        + imGroupHongbaoLogEscapeHtml(display) + "</span>";
}

function imGroupHongbaoLogFormatNestedText(row, parentKey, field, maxLen) {
    var value = imGroupHongbaoLogNestedField(row, parentKey, field);
    return imGroupHongbaoLogFormatText(value, maxLen);
}

function imGroupHongbaoLogFormatMoney(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    return isNaN(num) ? value : num.toFixed(2);
}

function imGroupHongbaoLogInitTable() {
    imInitTable({
        url: imGroupHongbaoLogApi + "/list",
        formId: "group-hongbao-log-form",
        modalName: "群红包领取明细",
        sortName: "createDate",
        sortOrder: "desc",
        queryParams: imGroupHongbaoLogQueryParams,
        columns: [
            {
                field: "u.idno",
                title: "ID号",
                cellStyle: imGroupHongbaoLogEllipsisCell(100),
                formatter: function (value, row) {
                    return imGroupHongbaoLogFormatNestedText(row, "u", "idno", 12);
                }
            },
            {
                field: "u.id",
                title: "用户ID",
                cellStyle: imGroupHongbaoLogEllipsisCell(120),
                formatter: function (value, row) {
                    return imGroupHongbaoLogFormatNestedText(row, "u", "id", 16);
                }
            },
            {
                field: "u.nickname",
                title: "用户昵称",
                cellStyle: imGroupHongbaoLogEllipsisCell(100),
                formatter: function (value, row) {
                    return imGroupHongbaoLogFormatNestedText(row, "u", "nickname", 10);
                }
            },
            {
                field: "baoId",
                title: "红包",
                sortable: true,
                cellStyle: imGroupHongbaoLogEllipsisCell(120),
                formatter: function (value) {
                    return imGroupHongbaoLogFormatText(value, 16);
                }
            },
            {
                field: "group.idno",
                title: "群组ID号",
                cellStyle: imGroupHongbaoLogEllipsisCell(100),
                formatter: function (value, row) {
                    return imGroupHongbaoLogFormatNestedText(row, "group", "idno", 12);
                }
            },
            {
                field: "group.name",
                title: "群组名称",
                cellStyle: imGroupHongbaoLogEllipsisCell(120),
                formatter: function (value, row) {
                    return imGroupHongbaoLogFormatNestedText(row, "group", "name", 12);
                }
            },
            {
                field: "money",
                title: "领取金额",
                sortable: true,
                formatter: function (value) {
                    return imGroupHongbaoLogFormatMoney(value);
                }
            },
            {
                field: "createDate",
                title: "领取时间",
                sortable: true,
                cellStyle: imGroupHongbaoLogEllipsisCell(150),
                formatter: function (value) {
                    return imGroupHongbaoLogFormatText(value, 19);
                }
            }
        ]
    });
}

$(function () {
    imGroupHongbaoLogInitTable();
});