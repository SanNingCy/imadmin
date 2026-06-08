/**
 * IM 用户单选弹窗（供好友添加等场景复用）
 * - GET /member/member/list
 */
var imMemberPickerApi = ctx + "member/member";
var imMemberPickerCallback = null;

function imMemberPickerQueryParams(params) {
    var pageNo = params.offset / params.limit + 1;
    var query = imBuildPageQuery(pageNo, params.limit);
    var search = $.common.formToJSON("im-member-picker-search-form");
    return $.extend(query, imOmitEmptyParams(search));
}

function imMemberPickerOpen(title, onConfirm) {
    imMemberPickerCallback = onConfirm;
    layer.open({
        type: 1,
        title: title || "选择用户",
        area: ["720px", "500px"],
        shadeClose: true,
        content: $("#im-member-picker-wrap"),
        btn: ["确定", "取消"],
        success: function () {
            if (!$("#im-member-picker-table").data("bootstrap.table")) {
                $("#im-member-picker-table").bootstrapTable({
                    url: imMemberPickerApi + "/list",
                    method: "get",
                    sidePagination: "server",
                    pagination: true,
                    pageSize: 10,
                    pageList: [10, 20, 50],
                    clickToSelect: true,
                    singleSelect: true,
                    queryParams: imMemberPickerQueryParams,
                    responseHandler: imPageResponse,
                    columns: [
                        { radio: true },
                        { field: "idno", title: "id号" },
                        { field: "nickname", title: "昵称" },
                        { field: "lianghao", title: "靓号" }
                    ],
                    ajaxOptions: { beforeSend: imTableBeforeSend }
                });
            } else {
                $("#im-member-picker-table").bootstrapTable("refresh");
            }
            $("#im-member-picker-table").bootstrapTable("uncheckAll");
        },
        yes: function (index) {
            var rows = $("#im-member-picker-table").bootstrapTable("getSelections") || [];
            if (!rows.length) {
                $.modal.alertWarning("请选择一条记录");
                return;
            }
            if (typeof imMemberPickerCallback === "function") {
                imMemberPickerCallback(rows[0]);
            }
            layer.close(index);
        }
    });
}

function imMemberPickerSearch() {
    $("#im-member-picker-table").bootstrapTable("refresh");
}
