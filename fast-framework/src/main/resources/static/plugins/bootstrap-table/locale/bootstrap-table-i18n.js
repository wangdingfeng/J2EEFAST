(function ($) {
    'use strict';

    $.fn.bootstrapTable.locales['zh-CN'] = {
        formatLoadingMessage: function () {
            return  $.i18n.prop('数据正在努力加载中,请稍后');
        },
        formatRecordsPerPage: function (pageNumber) {
            return pageNumber + ' 条记录每页';
        },
        formatShowingRows: function (pageFrom, pageTo, totalRows) {
            return '第 ' + pageFrom + ' 到 ' + pageTo + ' 条，共  ' + totalRows + ' 条记录。';
        },
        formatSearch: function () {
            return  $.i18n.prop('查询');
        },
        formatPageGo: function () {
            return '跳转';
        },
        formatNoMatches: function () {
            return  '<div class="table-empty"><div class="table-empty__image" style="height: 50px;"><img src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjQiIGhlaWdodD0iNDEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CiAgPGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMCAxKSIgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj4KICAgIDxlbGxpcHNlIGZpbGw9IiNGNUY1RjUiIGN4PSIzMiIgY3k9IjMzIiByeD0iMzIiIHJ5PSI3Ii8+CiAgICA8ZyBmaWxsLXJ1bGU9Im5vbnplcm8iIHN0cm9rZT0iI0Q5RDlEOSI+CiAgICAgIDxwYXRoIGQ9Ik01NSAxMi43Nkw0NC44NTQgMS4yNThDNDQuMzY3LjQ3NCA0My42NTYgMCA0Mi45MDcgMEgyMS4wOTNjLS43NDkgMC0xLjQ2LjQ3NC0xLjk0NyAxLjI1N0w5IDEyLjc2MVYyMmg0NnYtOS4yNHoiLz4KICAgICAgPHBhdGggZD0iTTQxLjYxMyAxNS45MzFjMC0xLjYwNS45OTQtMi45MyAyLjIyNy0yLjkzMUg1NXYxOC4xMzdDNTUgMzMuMjYgNTMuNjggMzUgNTIuMDUgMzVoLTQwLjFDMTAuMzIgMzUgOSAzMy4yNTkgOSAzMS4xMzdWMTNoMTEuMTZjMS4yMzMgMCAyLjIyNyAxLjMyMyAyLjIyNyAyLjkyOHYuMDIyYzAgMS42MDUgMS4wMDUgMi45MDEgMi4yMzcgMi45MDFoMTQuNzUyYzEuMjMyIDAgMi4yMzctMS4zMDggMi4yMzctMi45MTN2LS4wMDd6IiBmaWxsPSIjRkFGQUZBIi8+CiAgICA8L2c+CiAgPC9nPgo8L3N2Zz4K" alt=""></div><p class="table-empty__desc">'+$.i18n.prop('暂无数据')+'</p></div>';
        },
        formatPaginationSwitch: function () {
            return  $.i18n.prop('隐藏/显示分页');
        },
        formatRefresh: function () {
            return $.i18n.prop('刷新');
        },
        formatToggle: function () {
            return $.i18n.prop('切换');
        },
        formatColumns: function () {
            return $.i18n.prop('列');
        },
        formatExport: function () {
            return $.i18n.prop('导出数据');
        },
        formatClearFilters: function () {
            return $.i18n.prop('清空过滤');
        }
    };

    $.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales['zh-CN']);

})(jQuery);
