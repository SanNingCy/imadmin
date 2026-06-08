/**
 * IM 红包交易记录（接口：/redPacketTransaction/redPacketTransaction/*）
 */
var imRedPacketApi = ctx + "redPacketTransaction/redPacketTransaction";

function imRedPacketQueryParams(params) {
    var pageSize = params.limit;
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, pageSize, params.sort, params.order);
    var formValues = $.common.formToJSON("red-packet-form");
    return $.extend(query, imOmitEmptyParams(formValues));
}

function imRedPacketNestedField(row, parentKey, field) {
    if (row && row[parentKey] && row[parentKey][field] != null && row[parentKey][field] !== "") {
        return row[parentKey][field];
    }
    return null;
}

function imRedPacketEscapeHtml(text) {
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

function imRedPacketEllipsisCell(maxWidth) {
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

function imRedPacketFormatText(value, maxLen) {
    if (value == null || value === "") {
        return "-";
    }
    var text = String(value);
    var display = text;
    if (maxLen && text.length > maxLen) {
        display = text.substring(0, maxLen) + "...";
    }
    var style = "display:block;max-width:100%;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;";
    return '<span style="' + style + '" title="' + imRedPacketEscapeHtml(text) + '">'
        + imRedPacketEscapeHtml(display) + "</span>";
}

function imRedPacketFormatMemberText(row, memberKey, field, maxLen) {
    return imRedPacketFormatText(imRedPacketNestedField(row, memberKey, field), maxLen);
}

function imRedPacketFormatGroupText(row, field, maxLen) {
    return imRedPacketFormatText(imRedPacketNestedField(row, "group", field), maxLen);
}

function imRedPacketFormatMoney(value) {
    if (value == null || value === "") {
        return "-";
    }
    var num = Number(value);
    return isNaN(num) ? imRedPacketFormatText(value, 12) : num.toFixed(2);
}

function imRedPacketInitTable() {
    imInitTable({
        url: imRedPacketApi + "/list",
        formId: "red-packet-form",
        modalName: "红包交易记录",
        sortName: "createDate",
        sortOrder: "desc",
        queryParams: imRedPacketQueryParams,
        columns: [
            {
                field: "packetTypeName",
                title: "红包类型",
                sortable: true,
                cellStyle: imRedPacketEllipsisCell(90),
                formatter: function (value) {
                    return imRedPacketFormatText(value, 8);
                }
            },
            {
                field: "u.idno",
                title: "发送用户ID",
                sortable: true,
                cellStyle: imRedPacketEllipsisCell(100),
                formatter: function (value, row) {
                    return imRedPacketFormatMemberText(row, "u", "idno", 12);
                }
            },
            {
                field: "group.idno",
                title: "群ID",
                sortable: true,
                cellStyle: imRedPacketEllipsisCell(90),
                formatter: function (value, row) {
                    return imRedPacketFormatGroupText(row, "idno", 10);
                }
            },
            {
                field: "group.name",
                title: "群昵称",
                cellStyle: imRedPacketEllipsisCell(120),
                formatter: function (value, row) {
                    return imRedPacketFormatGroupText(row, "name", 12);
                }
            },
            {
                field: "u.nickname",
                title: "发红包人",
                sortable: true,
                cellStyle: imRedPacketEllipsisCell(120),
                formatter: function (value, row) {
                    return imRedPacketFormatMemberText(row, "u", "nickname", 10);
                }
            },
            {
                field: "uid2.idno",
                title: "发送对象ID",
                sortable: true,
                cellStyle: imRedPacketEllipsisCell(100),
                formatter: function (value, row) {
                    return imRedPacketFormatMemberText(row, "uid2", "idno", 12);
                }
            },
            {
                field: "uid2.nickname",
                title: "发送对象",
                cellStyle: imRedPacketEllipsisCell(120),
                formatter: function (value, row) {
                    return imRedPacketFormatMemberText(row, "uid2", "nickname", 10);
                }
            },
            {
                field: "money",
                title: "金额",
                sortable: true,
                formatter: function (value) {
                    return imRedPacketFormatMoney(value);
                }
            },
            {
                field: "info",
                title: "描述",
                cellStyle: imRedPacketEllipsisCell(160),
                formatter: function (value) {
                    return imRedPacketFormatText(value, 16);
                }
            },
            {
                field: "shouTime",
                title: "领取时间",
                sortable: true,
                cellStyle: imRedPacketEllipsisCell(150),
                formatter: function (value) {
                    return imRedPacketFormatText(value, 19);
                }
            },
            {
                field: "tuiTime",
                title: "退款时间",
                sortable: true,
                cellStyle: imRedPacketEllipsisCell(150),
                formatter: function (value) {
                    return imRedPacketFormatText(value, 19);
                }
            },
            {
                field: "createDate",
                title: "发放时间",
                sortable: true,
                cellStyle: imRedPacketEllipsisCell(150),
                formatter: function (value) {
                    return imRedPacketFormatText(value, 19);
                }
            }
        ]
    });
}

$(function () {
    imRedPacketInitTable();
});