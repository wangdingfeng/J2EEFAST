/**
 * 基于bootstrapTreeTable/bootstrap-table-treegrid修改
 * Copyright (c) 2019 ruoyi
 * J2eeFast 二次修改
 * 1. 增强功能、
 * 2. 支持异步加载数据、增加排序
 * 3. 新增异步加载分页
 * 4. 新增表格边框显示
 */
(function($) {
    "use strict";

    $.fn.bootstrapTreeTable = function(options, param) {
        var target = $(this).data('bootstrap.tree.table');
        target = target ? target : $(this);
        // 如果是调用方法
        if (typeof options == 'string') {
            return $.fn.bootstrapTreeTable.methods[options](target, param);
        }
        // 如果是初始化组件
        options = $.extend({}, $.fn.bootstrapTreeTable.defaults, options || {});
        target.hasSelectItem = false;// 是否有radio或checkbox
        target.data_list = null; //用于缓存格式化后的数据-按父分组
        target.totalPages = 0; //总页数
        target.pageSize =  0; // 一页多少
        target.totalRows = 0; // 一共数据
        target.pageNumber = 0; // 当前实际数据

        target.data_obj = null; //用于缓存格式化后的数据-按id存对象
        target.hiddenColumns = []; //用于存放被隐藏列的field
        target.lastAjaxParams; //用户最后一次请求的参数
        target.isFixWidth=false; //是否有固定宽度

        // 初始化
        var init = function() {

            // 初始化容器
            initContainer();
            // 初始化工具栏
            initToolbar();
            // 初始化表头
            initHeader();
            // 初始化表体
            initBody();
            // 初始化数据服务
            if(options.async){
                var parms = {};
                parms[options.parentCode] = options.rootIdValue;
                initServer(parms);
            }else{
                initServer();
            }

            // 动态设置表头宽度
            autoTheadWidth(true);
            // 缓存target对象
            target.data('bootstrap.tree.table', target);

        }
        // 初始化容器
        var initContainer = function() {
            if(options.bordered){
                $('head').append("<style type='text/css'>.bootstrap-tree-table .treetable-thead th {border-left: 1px solid #e7eaec!important;}" +
                    " .bootstrap-tree-table .treetable-tbody td {border-right: 1px solid #e7eaec!important;}</style>");
            }
            // 在外层包装一下div，样式用的bootstrap-table的
            var $main_div = $("<div class='bootstrap-tree-table'></div>");
            var $treetable = $("<div class='treetable-table'></div>");
            // var $pagination = $("<div class='fixed-table-pagination'></div>");
            target.before($main_div);
            $main_div.append($treetable);
            // $main_div.append($pagination);
            $treetable.append(target);
            target.addClass("table");
            if (options.striped) { //是否各行渐变色
                target.addClass('table-striped');
            }
            // if (options.bordered) { //是否显示边框
            //     target.addClass('table-bordered');
            // }
            if (options.hover) {//是否鼠标悬停
                target.addClass('table-hover');
            }
            if (options.condensed) {//是否紧缩表格
                target.addClass('table-condensed');
            }
            target.html("");
        }
        // 初始化工具栏
        var initToolbar = function() {
            var $toolbar = $("<div class='treetable-bars'></div>");
            if (options.toolbar) {
                $(options.toolbar).addClass('tool-left');
                $toolbar.append($(options.toolbar));
            }
            var $rightToolbar = $('<div class="btn-group tool-right">');
            $toolbar.append($rightToolbar);
            target.parent().before($toolbar);
            // ruoyi 是否显示检索信息
            if (options.showSearch) {
                var $searchBtn = $('<button class="btn btn-default btn-outline" type="button" aria-label="search" title="搜索"><i class="glyphicon glyphicon-search"></i></button>');
                $rightToolbar.append($searchBtn);
                registerSearchBtnClickEvent($searchBtn);
            }
            // 是否显示刷新按钮
            if (options.showRefresh) {
                var $refreshBtn = $('<button class="btn btn-default btn-outline" type="button" aria-label="refresh" title="刷新"><i class="glyphicon glyphicon-repeat"></i></button>');
                $rightToolbar.append($refreshBtn);
                registerRefreshBtnClickEvent($refreshBtn);
            }
            // 是否显示列选项
            if (options.showColumns) {
                var $columns_div = $('<div class="btn-group pull-right" title="列"><button type="button" aria-label="columns" class="btn btn-default btn-outline dropdown-toggle" data-toggle="dropdown" aria-expanded="false"><i class="glyphicon glyphicon-list"></i> <span class="caret"></span></button></div>');
                var $columns_ul = $('<ul class="dropdown-menu columns" role="menu"></ul>');
                $.each(options.columns, function(i, column) {
                    if (column.field != 'selectItem') {
                        var _li = null;
                        if(typeof column.visible == "undefined"||column.visible==true){
                            _li = $('<li role="menuitem"><label><input type="checkbox" checked="checked" data-field="'+column.field+'" value="'+column.field+'" > '+column.title+'</label></li>');
                        }else{
                            _li = $('<li role="menuitem"><label><input type="checkbox" data-field="'+column.field+'" value="'+column.field+'" > '+column.title+'</label></li>');
                            target.hiddenColumns.push(column.field);
                        }
                        $columns_ul.append(_li);
                    }
                });
                $columns_div.append($columns_ul);
                $rightToolbar.append($columns_div);
                // 注册列选项事件
                registerColumnClickEvent();
            }else{
                $.each(options.columns, function(i, column) {
                    if (column.field != 'selectItem') {
                        if(!(typeof column.visible == "undefined"||column.visible==true)){
                            target.hiddenColumns.push(column.field);
                        }
                    }
                });
            }
        }
        // 初始化隐藏列
        var initHiddenColumns = function(){
            $.each(target.hiddenColumns, function(i, field) {
                target.find("."+field+"_cls").hide();
            });
        }
        // 初始化表头
        var initHeader = function() {
            var $thr = $('<tr></tr>');
            $.each(options.columns, function(i, column) {
                var $th = null;
                // 判断有没有选择列
                if (i == 0 && column.field == 'selectItem') {
                    target.hasSelectItem = true;
                    $th = $('<th style="width:36px"></th>');
                } else {
                    var _halign = '';
                    if(column.halign){
                        _halign = '; text-align:'+column.halign+';';
                    }
                    $th = $('<th style="' + ((column.width) ? ('width:' + column.width) : '') + ''+_halign+'" class="' + column.field + '_cls"></th>');
                }
                if((!target.isFixWidth)&& column.width){
                    target.isFixWidth = column.width.indexOf("px")>-1?true:false;
                }
                $th.text(column.title);
                $thr.append($th);
            });
            var $thead = $('<thead class="treetable-thead"></thead>');
            $thead.append($thr);
            target.append($thead);
        }



        // 初始化表体
        var initBody = function() {
            var $tbody = $('<tbody class="treetable-tbody"></tbody>');
            target.append($tbody);
            // 默认高度
            if (options.height) {
                $tbody.css("height", options.height);
            }

            if(options.async){
                var $pagination =  $('<div class="fixed-table-pagination"></div>');
                target.append($pagination);
            }
        }
        // 初始化数据服务
        var initServer = function(parms) {
            // 加载数据前先清空
            target.data_list = {};
            target.data_obj = {};
            // 新增排序
            if(options.sortName !== ""){
                var curParams = {
                    __sidx:           options.sortName,
                    __order:          options.sortOrder
                };
                if(parms){
                    !options.async && delete parms.__refre;
                    parms = $.extend(curParams, parms);
                }else{
                    parms = curParams;
                }
            }

            //设置请求分页参数
            if(options.async){
                var params = {};
                params.offset = options.pageSize * (options.pageNumber - 1);
                params.limit = options.pageSize;
                var curParams = {
                    __limit:       params.limit,
                    __page:        params.offset / params.limit + 1,
                };
                if(parms){
                    if(parms.__refre){
                        //delete parms.__refre;
                        parms[options.parentCode] = options.rootIdValue;
                    }
                    parms = $.extend(curParams, parms);
                }else{
                    parms = curParams;
                }
            }

            var $tbody = target.find("tbody");
            // 添加加载loading
            var $loading = '<tr><td colspan="' + options.columns.length + '"><div style="display: block;text-align: center;">正在努力地加载数据中，请稍候……</div></td></tr>'
            $tbody.html($loading);
            if (options.url) {
                var config = {
                    type: options.type,
                    url: options.url,
                    data: parms ? parms : options.ajaxParams,
                    dataType: "JSON",
                    success: function(data, textStatus, jqXHR) {
                        data = calculateObjectValue(options, options.responseHandler, [data], data);
                        renderTable(data);
                    },
                    error: function(xhr, textStatus) {
                        var _errorMsg = '<tr><td colspan="' + options.columns.length + '"><div style="display: block;text-align: center;font-weight: bold;color: red;">' + JSON.parse(xhr.responseText).msg || xhr.responseText + '</div></td></tr>'
                        $tbody.html(_errorMsg);
                    },
                };
                //J2eeFAST 新增CSRF 防护
                if(String(options.type).toUpperCase() === String('POST').toUpperCase() && $('meta[name="csrf-token"]').attr("content")){
                    config = $.extend(config,{headers: {
                            "X-CSRF-Token": $('meta[name="csrf-token"]').attr("content") || ''
                        }});
                }
                $.ajax(config);
            } else {
                renderTable(options.data);
            }
        }


        // 加载完数据后渲染表格
        var renderTable = function(data) {
            //兼容返回数据
            //pageSize: "10"
            // totalCount: "34"
            // totalPage: "4"
            var list,totalPage = 0,currPage = 0;

            if(options.async){
                list = data.data.list; //数据
                totalPage = data.data.totalPage; //一共多少页
                target.totalPages = totalPage;
                currPage = data.data.currPage; // 当前页
                target.pageSize =  data.data.pageSize; // 一页多少
                target.totalRows = data.data.totalCount; // 一共数据
                target.pageNumber = list.length; // 当前实际数据
            }else{
                if(data.constructor==Array){
                    list = data;
                }else{
                    if(data.code == '00000'){
                        if(Array==data.list.constructor){
                            list = data.list;
                        }
                    }
                }
            }

            data = list;

            //options.pageNumber = data.length;

            var $tbody = target.find("tbody");
            // 先清空
            $tbody.html("");
            if (!data || data.length <= 0) {
                var _empty = '<tr><td colspan="' + options.columns.length + '"><div style="display: block;text-align: center;">没有找到匹配的记录</div></td></tr>'
                $tbody.html(_empty);
                return;
            }

            // 缓存并格式化数据
            formatData(data);

            // 获取所有根节点
            var rootNode = target.data_list["_root_"];
            // 开始绘制
            if (rootNode) {
                $.each(rootNode, function(i, item) {
                    var _child_row_id = "row_id_" + i //
                    recursionNode(item, options.expandColumn, _child_row_id, "row_root",item[options.code]);
                });
            }
            // 下边的操作主要是为了查询时让一些没有根节点的节点显示
            $.each(data, function(i, item) {
                if (!item.isShow) {
                    var tr = renderRow(item, false, 1, "", "",options.async,item[options.code]);
                    $tbody.append(tr);
                }
            });
            // target.append($tbody);
            registerExpanderEvent();
            registerRowClickEvent();
            initHiddenColumns();
            // 动态设置表头宽度
            autoTheadWidth();

            if(options.async){
                initPagination(totalPage,currPage);
            }
        }


        var initPagination = function (totalPage,currPage) {
            var $pagination = target.find(".fixed-table-pagination");
            $pagination.empty();
            //
            var html = [];
            var pageFrom = (options.pageNumber - 1) * options.pageSize + 1;
            var pageTo = options.pageNumber * options.pageSize;
            if (pageTo > target.totalRows) {
                pageTo = target.totalRows;
            }
            html.push('<div class="pull-left pagination-detail">');
            html.push('<span class="pagination-info">第  '+pageFrom+' 条,到 '+pageTo+' 条，共  '+target.totalRows+' 条记录。</span>');
            var pageList = false;
            $.each(options.pageList, function (i, page) {
                if(target.totalRows > page){
                    pageList = true;
                }
            })
            if(pageList){
                html.push('<span class="page-list">');
                html.push('<span class="btn-group dropup">');
                html.push('<button type="button" class="btn btn-default btn-outline dropdown-toggle" data-toggle="dropdown">');
                html.push('<span class="page-size">'+target.pageSize+'</span>');
                html.push('<span class="caret"></span>');
                html.push('</button>');
                html.push('<ul class="dropdown-menu" role="menu">');
                $.each(options.pageList, function (i, page) {
                    if(page == target.pageSize){
                        html.push('<li class="active"><a href="javascript:void(0)">'+page+'</a></li>');
                    }else if(page <= target.totalRows){
                        html.push('<li><a href="javascript:void(0)">'+page+'</a></li>');
                    }
                })
                html.push('</ul>');
                html.push('</span> 条记录每页</span>');
            }
            html.push('</div>');

            //右边跳转
            if(totalPage > 1){
                html.push('<div class="pull-right pagination">');
                html.push('<ul class="pagination pagination-outline">');
                html.push('<li class="page-pre"><a href="javascript:void(0)">'+options.paginationPreText +'</a></li>');
                var from, to;
                if (totalPage < 5) {
                    from = 1;
                    to = totalPage;
                } else {
                    from = currPage - 2;
                    to = from + 4;
                    if (from < 1) {
                        from = 1;
                        to = 5;
                    }
                    if (to > totalPage) {
                        to = totalPage;
                        from = to - 4;
                    }
                }

                if (totalPage >= 6) {
                    if (currPage >= 3) {
                        html.push('<li class="page-first' + (1 == currPage ? ' active' : '') + '">',
                            '<a href="javascript:void(0)">', 1, '</a>',
                            '</li>');

                        from++;
                    }

                    if (currPage >= 4) {
                        if (currPage == 4 || totalPage == 6 || totalPage == 7) {
                            from--;
                        } else {
                            html.push('<li class="page-first-separator disabled">',
                                '<a href="javascript:void(0)">...</a>',
                                '</li>');
                        }

                        to--;
                    }
                }

                if (totalPage >= 7) {
                    if (currPage >= (totalPage - 2)) {
                        from--;
                    }
                }

                if (totalPage == 6) {
                    if (currPage >= (totalPage - 2)) {
                        to++;
                    }
                } else if (totalPage >= 7) {
                    if (totalPage == 7 || currPage >= (totalPage - 3)) {
                        to++;
                    }
                }

                for (var i = from; i <= to; i++) {
                    html.push('<li class="page-number' + (i == currPage ? ' active' : '') + '">',
                        '<a href="javascript:void(0)">', i, '</a>',
                        '</li>');
                }

                if (totalPage >= 8) {
                    if (currPage <= (totalPage - 4)) {
                        html.push('<li class="page-last-separator disabled">',
                            '<a href="javascript:void(0)">...</a>',
                            '</li>');
                    }
                }

                if (totalPage >= 6) {
                    if (currPage <= (totalPage - 3)) {
                        html.push('<li class="page-last' + (totalPage === currPage ? ' active' : '') + '">',
                            '<a href="javascript:void(0)">', totalPage, '</a>',
                            '</li>');
                    }
                }

                html.push('<li class="page-next"><a href="javascript:void(0)">'+options.paginationNextText +'</a></li>');
                html.push('</ul></div>');
            }
            //
            //<ul class="pagination pagination-outline">
            //
            // <li class="page-number active"><a href="javascript:void(0)">1</a></li>
            // <li class="page-number"><a href="javascript:void(0)">2</a></li>
            // <li class="page-next"><a href="javascript:void(0)">›</a></li>
            // <li class="pageGo"><input type="text" class="form-control" value="1"><button class="btn btn-default btn-outline" title="跳转" type="button">跳转</button></li>
            // </ul>
            // </div>
            $pagination.append(html.join(''));

            //添加事件
            var $pageList = $pagination.find('.page-list a');
            var $pre = $pagination.find('.page-pre'); //上一页
            var $next = $pagination.find('.page-next'); //下一页
            var $number = $pagination.find('.page-number'); // 页码
            var $first = $pagination.find('.page-first'); //首页
            var $last = $pagination.find('.page-last'); //尾页
            $pre.off('click').on('click', $.proxy(onPagePre, this));
            $pageList.off('click').on('click', $.proxy(onPageListChange, this));
            $number.off('click').on('click', $.proxy(onPageNumber, this));
            $first.off('click').on('click', $.proxy(onPageFirst, this));
            $last.off('click').on('click', $.proxy(onPageLast, this));
            $next.off('click').on('click', $.proxy(onPageNext, this));
        }

        //选择一页最大数量
        var onPageListChange = function(event){
            var $this = $(event.currentTarget);
            $this.parent().addClass('active').siblings().removeClass('active');
            var $pagination = target.find(".fixed-table-pagination");
            options.pageSize = $this.text().toUpperCase() === target.totalRows.toUpperCase() ?
                target.totalRows : +$this.text();
            if(target.totalRows < options.pageSize * options.pageNumber){
                options.pageNumber = 1;
            }
            $pagination.find('.page-size').text(options.pageSize);
            // 初始化数据服务
            var parms = {};
            parms[options.parentCode] = options.rootIdValue;
            initServer(parms);
        }
        //点击页面上一页
        var onPagePre = function(event){
            if ((options.pageNumber - 1) === 0) {
                options.pageNumber = target.totalPages;
            } else {
                options.pageNumber--;
            }
            // 初始化数据服务
            var parms = {};
            parms[options.parentCode] = options.rootIdValue;
            initServer(parms);
        }
        // 页面事件
        var onPageNumber = function(event){
            if (options.pageNumber == $(event.currentTarget).text()) {
                return;
            }
            options.pageNumber = $(event.currentTarget).text();
            var parms = {};
            parms[options.parentCode] = options.rootIdValue;
            initServer(parms);
        }
        // 首页
        var onPageFirst = function(event){
            options.pageNumber = 1;
            // 初始化数据服务
            var parms = {};
            parms[options.parentCode] = options.rootIdValue;
            initServer(parms);
        }
        // 尾页
        var onPageLast = function (event) {
            options.pageNumber = target.totalPages;
            // 初始化数据服务
            var parms = {};
            parms[options.parentCode] = options.rootIdValue;
            initServer(parms);
        }
        var onPageNext = function(event){
            if ((options.pageNumber + 1) > target.totalPages) {
                options.pageNumber = 1;
            } else {
                options.pageNumber++;
            }
            // 初始化数据服务
            var parms = {};
            parms[options.parentCode] = options.rootIdValue;
            initServer(parms);
        }
        // 动态设置表头宽度
        var autoTheadWidth = function(initFlag) {
            if(options.height>0){
                var $thead = target.find("thead");
                var $tbody = target.find("tbody");
                var borderWidth = parseInt(target.css("border-left-width")) + parseInt(target.css("border-right-width"))
                
                $thead.css("width", $tbody.children(":first").width());
                if(initFlag){
                    var resizeWaiter = false;
                    $(window).resize(function() {
                        if(!resizeWaiter){
                            resizeWaiter = true;
                            setTimeout(function(){
                                if(!target.isFixWidth){
                                    $tbody.css("width", target.parent().width()-borderWidth);
                                }
                                $thead.css("width", $tbody.children(":first").width());
                                resizeWaiter = false;
                            }, 300);
                        }
                    });
                }
            }
        
        }
        // 缓存并格式化数据
        var formatData = function(data) {
            var _root = options.rootIdValue ? options.rootIdValue : null;
            var firstCode;
            for(var i=0; i< data.length; i++){
                var f = data[i][options.parentCode];
                var flag = false;
                for(var k=0; k<data.length; k++){
                    if(data[k][options.code] == f){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    firstCode = f;
                    break;
                }
            }
            $.each(data, function(index, item) {
                // 添加一个默认属性，用来判断当前节点有没有被显示
                item.isShow = false;
                //是否有是异步
                if(options.async){
                    // item.__nodes = (item["nodes"] == 1? true: false) || ((item["nodes"] == 'true' || item["nodes"] == true)? true: false);
                    if(item.isTreeLeaf == undefined || item.isTreeLeaf == null){
                        item.isTreeLeaf = false;
                    }else{
                        item.isTreeLeaf = (item["isTreeLeaf"] == 1? true: false) || ((item["isTreeLeaf"] == 'true' || item["isTreeLeaf"] == true)? true: false);
                    }
                }

                // 这里兼容几种常见Root节点写法
                // 默认的几种判断
                var _defaultRootFlag = item[options.parentCode] == '0' ||
                    item[options.parentCode] == 0 ||
                    item[options.parentCode] == null ||
                    item[options.parentCode] == firstCode ||
                    item[options.parentCode] == '';
                if (!item[options.parentCode] || ((_root && _root == firstCode) ? (item[options.parentCode] == options.rootIdValue) : _defaultRootFlag)) {
                    if (!target.data_list["_root_"]) {
                        target.data_list["_root_"] = [];
                    }
                    if (!target.data_obj["id_" + item[options.code]]) {
                        target.data_list["_root_"].push(item);
                    }
                    if (!target.data_list["_n_" + item[options.parentCode]]) {
                        target.data_list["_n_" + item[options.parentCode]] = [];
                    }
                    if (!target.data_obj["id_" + item[options.code]]) {
                        target.data_list["_n_" + item[options.parentCode]].push(item);
                    }
                } else {
                    if (!target.data_list["_n_" + item[options.parentCode]]) {
                        target.data_list["_n_" + item[options.parentCode]] = [];
                    }
                    if (!target.data_obj["id_" + item[options.code]]) {
                        target.data_list["_n_" + item[options.parentCode]].push(item);
                    }
                }
                target.data_obj["id_" + item[options.code]] = item;
            });
        }
        /**
         * 递归获取子节点并且设置子节点
         * @param parentNode 节点对象
         * @param lv 在哪一列上面显示展开按钮
         * @param row_id 行 tr Id 例如 <tr id="row_id_0">
         * @param p_id 父ID <tr id="row_id_0" pid="row_root">
         * @param k  行唯一ID <tr id="row_id_0" pid="row_root"  data-id="1">
         */
        var recursionNode = function(parentNode, lv, row_id, p_id,k) {
            var $tbody = target.find("tbody");
            var _ls = target.data_list["_n_" + parentNode[options.code]]; //子节点

            var $tr = renderRow(parentNode, _ls ? true : false, lv, row_id, p_id, options.async,k);
            $tbody.append($tr);
            if (_ls) {
                $.each(_ls, function(i, item) {
                    var _child_row_id = row_id + "_" + i
                    recursionNode(item, (lv + 1), _child_row_id, row_id,item[options.code])
                });
            }
        };

        /**
         * 绘制行
         * @param item 行对象数据
         * @param isP 是否有子节点(同步)
         * @param lv 在哪一列上面显示展开按钮
         * @param row_id 行 tr Id 例如 <tr id="row_id_0">
         * @param p_id 父ID <tr id="row_id_0" pid="row_root">
         * @param _async 是否异步请求
         * @param k 行唯一ID <tr id="row_id_0" pid="row_root"  data-id="1">
         * @returns {*|jQuery|HTMLElement}
         */
        var renderRow = function(item, isP, lv, row_id, p_id,_async,k) {
            // 标记已显示
            item.isShow = true;
            // tr ID
            item.row_id = row_id;
            // tr pid
            item.p_id = p_id;
            // 在哪一列上面显示展开按钮
            item.lv = lv;
            var $tr = $('<tr id="' + row_id + '" data-id="' + k + '"pid="' + p_id + '"></tr>');

            //--- 设置行是否显示 是否有下级展示图标
            var _icon = options.expanderCollapsedClass;
            if (options.expandAll) {
                $tr.css("display", "table");
                _icon = options.expanderExpandedClass;
            } else if (lv == 1) {
                $tr.css("display", "table");
                _icon = (options.expandFirst) ? options.expanderExpandedClass : options.expanderCollapsedClass;
            } else if (lv == 2) {
                if (options.expandFirst) {
                    $tr.css("display", "table");
                } else {
                    $tr.css("display", "none");
                }
                _icon = options.expanderCollapsedClass;
            } else if(_async){
                 if(item.isTreeLeaf){
                     _icon = options.expanderCollapsedClass;
                 }
            }else{
                $tr.css("display", "none");
                _icon = options.expanderCollapsedClass;
            }
            // --- end ---

            // 绘制此行列信息 --- columns//
            $.each(options.columns, function(index, column) {
                // 判断有没有选择列 -- 绘制第一列 radio 或者 checkbox
                if (column.field == 'selectItem') {
                    target.hasSelectItem = true;
                    var $td = $('<td style="text-align:center;width:36px"></td>');
                    if (column.radio) {
                        var _ipt = $('<input name="select_item" type="radio" value="' + item[options.code] + '"></input>');
                        $td.append(_ipt);
                    }
                    if (column.checkbox) {
                        var _ipt = $('<input name="select_item" type="checkbox" value="' + item[options.code] + '"></input>');
                        $td.append(_ipt);
                    }
                    $tr.append($td);
                } else {
                    // var _n = '';
                    // if(column.class){
                    //     _n =  ' '+column.class;
                    // }
                    // var $td = $('<td '+(column.field?('name="' + column.field + '" class="' + column.field + '_cls'+_n+'"'):'')+'></td>');
                    var $td = $('<td '+(column.class?(' class="'+column.field + '_cls ' + column.class + '"'): ' class="'+column.field + '_cls"')+'></td>');
                    if(column.width){
                        $td.css("width",column.width);
                    }
                    if(options.expandColumn == index){
                        $td.css("text-align","left");
                    }

                    if(column.align){
                        $td.css("text-align",column.align);
                    }
                    // if(options.expandColumn == index){
                    //     $td.css("text-align","left");
                    // }
                    if(column.valign){
                        $td.css("vertical-align",column.valign);
                    }
                    // if(options.showTitle){
                    //     //$td.addClass("ellipsis");
                    // }
                    // 增加formatter渲染
                    if (column.formatter) {
                        var value = getItemField(item, column.field);

                        value = calculateObjectValue(column,
                            column.formatter, [value, item,index], value);
                        // $td.html(column.formatter.call(this, getItemField(item, column.field), item, index));
                        value = typeof value === 'undefined' || value === null || value === ""  ?
                            options.undefinedText : value;
                        $td.html(value);
                        // if(options.showTitle){
                        //     // 只在字段没有formatter时才添加title属性
                        //     $td.attr("title",item[column.field]);
                        // }
                        if(options.showTitle){
                            if(column.field){
                               var  reg = /<[^>]+>/g;
                               if(reg.test(value)){
                                   if($(value).is('span')){
                                     $td.attr("title",$(value).html());
                                   }
                               }else{
                                   $td.attr("title",value);
                               }
                            }
                        }
                    } else {
                        var value = getItemField(item, column.field);
                        if(options.showTitle){
                            // 只在字段没有formatter时才添加title属性
                            $td.attr("title",item[column.field]);
                        }
                        value = typeof value === 'undefined' || value === null || value === ""  ?
                            options.undefinedText : value;
                        $td.text(value);
                    }
                    if (options.expandColumn == index) {
                        if(_async){
                            if(item["isTreeLeaf"]){
                                $td.prepend('<span class="treetable-expander ' + _icon + '"></span>');
                            }else {
                                $td.prepend('<span class="treetable-expander"></span>')
                            }
                        }else {
                            if (!isP)  {
                                $td.prepend('<span class="treetable-expander"></span>')
                            } else {
                                $td.prepend('<span class="treetable-expander ' + _icon + '"></span>');
                            }
                        }
                        for (var int = 0; int < (lv - options.expandColumn); int++) {
                            $td.prepend('<span class="treetable-indent"></span>')
                        }
                    }
                    $tr.append($td);
                }
            });
            return $tr;
        }
        // 检索信息按钮点击事件
        var registerSearchBtnClickEvent = function(btn) {
            $(btn).off('click').on('click', function () {
                $(".search-collapse").slideToggle();
            });
        }
        // 注册刷新按钮点击事件
        var registerRefreshBtnClickEvent = function(btn) {
            $(btn).off('click').on('click', function () {
                target.refresh({'__refre':true});
            });
        }
        // 注册列选项事件
        var registerColumnClickEvent = function() {
            $(".bootstrap-tree-table .treetable-bars .columns label input").off('click').on('click', function () {
                var $this = $(this);
                if($this.prop('checked')){
                    target.showColumn($(this).val());
                }else{
                    target.hideColumn($(this).val());
                }
            });
        }
        // 注册行点击选中事件
        var registerRowClickEvent = function() {
            target.find("tbody").find("tr").unbind();
            target.find("tbody").find("tr").click(function() {
                if (target.hasSelectItem) {
                    var _ipt = $(this).find("input[name='select_item']");
                    if (_ipt.attr("type") == "radio") {
                        _ipt.prop('checked', true);
                        target.find("tbody").find("tr").removeClass("treetable-selected");
                        $(this).addClass("treetable-selected");
                    } else if (_ipt.attr("type") == "checkbox") {
                    	if (_ipt.prop('checked')) {
                    		_ipt.prop('checked', true);
                    		target.find("tbody").find("tr").removeClass("treetable-selected");
                    		$(this).addClass("treetable-selected");
                    	} else {
                    		_ipt.prop('checked', false);
                    		target.find("tbody").find("tr").removeClass("treetable-selected");
                    	}
                    } else {
                        if (_ipt.prop('checked')) {
                            _ipt.prop('checked', false);
                            $(this).removeClass("treetable-selected");
                        } else {
                            _ipt.prop('checked', true);
                            $(this).addClass("treetable-selected");
                        }
                    }
                }
            });
        }
        // 注册小图标点击事件--展开缩起
        var registerExpanderEvent = function() {
            target.find("tbody").find("tr").find(".treetable-expander").unbind();
            target.find("tbody").find("tr").find(".treetable-expander").click(function() {
                var _isExpanded = $(this).hasClass(options.expanderExpandedClass); //展开
                var _isCollapsed = $(this).hasClass(options.expanderCollapsedClass); //收起
                if (_isExpanded || _isCollapsed) {
                    var tr = $(this).parent().parent(); // tr 本行
                    var row_id = tr.attr("id");
                    var  _id = tr.attr("data-id");
                    if(!options.async){ // 普通情况
                        var _ls = target.find("tbody").find("tr[id^='" + row_id + "_']"); //下所有
                        if (_isExpanded) {
                            $(this).removeClass(options.expanderExpandedClass);
                            $(this).addClass(options.expanderCollapsedClass);
                            if (_ls && _ls.length > 0) {
                                $.each(_ls, function(index, item) {
                                    $(item).css("display", "none");
                                });
                            }
                        } else {
                            $(this).removeClass(options.expanderCollapsedClass);
                            $(this).addClass(options.expanderExpandedClass);
                            if (_ls && _ls.length > 0) {
                                $.each(_ls, function(index, item) {
                                    // 父icon
                                    var _p_icon = $("#" + $(item).attr("pid")).children().eq(options.expandColumn).find(".treetable-expander");
                                    if (_p_icon.hasClass(options.expanderExpandedClass)) {
                                        $(item).css("display", "table");
                                    }
                                });
                            }
                        }
                    }else{  //异步
                        var _ls = target.find("tbody").find("tr[id^='" + row_id + "_']"); //下所有
                        if (_ls && _ls.length > 0) {
                            if (_isExpanded) {
                                $.each(_ls, function(index, item) {
                                    $(item).css("display", "none");
                                });
                            }else {
                                $.each(_ls, function(index, item) {
                                    // 父icon
                                    // var _p_icon = $("#" + $(item).attr("pid")).children().eq(options.expandColumn).find(".treetable-expander");
                                    // console.log( "_p_icon:" +_p_icon.html());
                                    // if (_p_icon.hasClass(options.expanderCollapsedClass)) {
                                    //     $(item).css("display", "table");
                                    // }
                                    var _icon = $(item).eq(options.expandColumn).find(".treetable-expander");
                                    if(_icon && _icon.hasClass(options.expanderExpandedClass)){
                                        //递归显示

                                        $(item).css("display", "table");
                                    }else{
                                        $(item).css("display", "table");
                                    }
                                });
                            }
                        }else{
                            if(options.async){
                                var parms = {};
                                parms[options.parentCode] = _id;
                                if (options.asynUrl) {
                                    var config = {
                                        type: options.type,
                                        url: options.asynUrl,
                                        data: parms,
                                        dataType: "JSON",
                                        beforeSend: function () {
                                            var _errorMsg = '<tr id="'+row_id+'_load"><td colspan="' + options.columns.length + '"><div style="display: block;text-align: center;">'+$.i18n.prop("数据正在加载...")+'</div></td></tr>'
                                            $("#" + row_id).after(_errorMsg);
                                        },
                                        success: function(data, textStatus, jqXHR) {
                                            $("#" + row_id + "_load").remove();
                                            //data = calculateObjectValue(options, options.responseHandler, [data], data);
                                            //兼容返回数据
                                            var list;
                                            if(data.constructor==Array){
                                                list = data;
                                            }else{
                                                if(data.code == '00000'){
                                                    if(Array==data.list.constructor){
                                                        list = data.list;
                                                    }
                                                }
                                            }
                                            data = list;
                                            target.appendData(data)
                                        },
                                        error: function(xhr, textStatus) {
                                            var _errorMsg = '<tr><td colspan="' + options.columns.length + '"><div style="display: block;text-align: center;font-weight: bold;color: red;">' + JSON.parse(xhr.responseText).msg || xhr.responseText + '</div></td></tr>'
                                            $("#" + row_id).after(_errorMsg);
                                        }
                                    };
                                    //J2eeFAST 新增CSRF 防护
                                    if(String(options.type).toUpperCase() === String('POST').toUpperCase() && $('meta[name="csrf-token"]').attr("content")){
                                        config = $.extend(config,{headers: {
                                                "X-CSRF-Token": $('meta[name="csrf-token"]').attr("content") || ''
                                            }});
                                    }
                                    $.ajax(config);
                                }
                            }
                        }
                        if (_isExpanded) {
                            $(this).removeClass(options.expanderExpandedClass);
                            $(this).addClass(options.expanderCollapsedClass);
                        } else {
                            $(this).removeClass(options.expanderCollapsedClass);
                            $(this).addClass(options.expanderExpandedClass);
                        }
                    }
                }
            });
        }
        // 刷新数据
        target.refresh = function(parms) {
            if(parms){
                target.lastAjaxParams=parms;
            }
            //$.extend(curParams, opt.common.formToJSON(currentId));
            initServer(target.lastAjaxParams);
        }
        // 添加数据刷新表格
        target.appendData = function(data) {
            // 下边的操作主要是为了查询时让一些没有根节点的节点显示
            $.each(data, function(i, item) {
                if(options.async){
                    item.__nodes = (item["nodes"] == 1? true: false) || ((item["nodes"] == 'true' || item["nodes"] == true)? true: false);
                }
                var _data = target.data_obj["id_" + item[options.code]];
                var _p_data = target.data_obj["id_" + item[options.parentCode]];
                var _c_list = target.data_list["_n_" + item[options.parentCode]];
                var row_id = ""; //行id
                var p_id = ""; //父行id
                var _lv = 1; //如果没有父就是1默认显示
                var tr; //要添加行的对象
                if (_data && _data.row_id && _data.row_id != "") {
                    row_id = _data.row_id; // 如果已经存在了，就直接引用原来的
                }
                if (_p_data) {
                    p_id = _p_data.row_id;
                    if (row_id == "") {
                        var _tmp = 0
                        if (_c_list && _c_list.length > 0) {
                            _tmp = _c_list.length;
                        }
                        row_id = _p_data.row_id + "_" + _tmp;
                    }
                    _lv = _p_data.lv + 1; //如果有父
                    // 绘制行
                    tr = renderRow(item, true, _lv, row_id, p_id,options.async,item[options.code]);

                    var _p_icon = $("#" + _p_data.row_id).children().eq(options.expandColumn).find(".treetable-expander");
                    var _isExpanded = _p_icon.hasClass(options.expanderExpandedClass);
                    var _isCollapsed = _p_icon.hasClass(options.expanderCollapsedClass);
                    // 父节点有没有展开收缩按钮
                    if (_isExpanded || _isCollapsed) {
                        // 父节点展开状态显示新加行
                        if (_isExpanded) {
                            tr.css("display", "table");
                        }
                    } else {
                        // 父节点没有展开收缩按钮则添加
                        _p_icon.addClass(options.expanderCollapsedClass);
                    }

                    if (_data) {
                        $("#" + _data.row_id).before(tr);
                        $("#" + _data.row_id).remove();
                    } else {
                        // 计算父的同级下一行
                        var _tmp_ls = _p_data.row_id.split("_");
                        var _p_next = _p_data.row_id.substring(0, _p_data.row_id.length - (_tmp_ls[_tmp_ls.length - 1]+"").length) + (parseInt(_tmp_ls[_tmp_ls.length - 1]) + 1);
                        // 画上
                        //$("#" + _p_next).before(tr);
                        $("#" + _p_data.row_id).after(tr);
                    }
                } else {
                    tr = renderRow(item, false, _lv, row_id, p_id,options.async,item[options.code]);
                    if (_data) {
                        $("#" + _data.row_id).before(tr);
                        $("#" + _data.row_id).remove();
                    } else {
                        // 画上
                        var tbody = target.find("tbody");
                        tbody.append(tr);
                    }
                }
                item.isShow = true;
                // 缓存并格式化数据
                formatData([item]);
            });
            registerExpanderEvent();
            registerRowClickEvent();
            initHiddenColumns();
        }

        // 展开/折叠指定的行
        target.toggleRow=function(id) {
            var _rowData = target.data_obj["id_" + id];
            var $row_expander = $("#"+_rowData.row_id).find(".treetable-expander");
            $row_expander.trigger("click");
        }
        // 展开指定的行
        target.expandRow=function(id) {
            var _rowData = target.data_obj["id_" + id];
            var $row_expander = $("#"+_rowData.row_id).find(".treetable-expander");
            var _isCollapsed = $row_expander.hasClass(target.options.expanderCollapsedClass);
            if (_isCollapsed) {
                $row_expander.trigger("click");
            }
        }
        // 折叠 指定的行
        target.collapseRow=function(id) {
            var _rowData = target.data_obj["id_" + id];
            var $row_expander = $("#"+_rowData.row_id).find(".treetable-expander");
            var _isExpanded = $row_expander.hasClass(target.options.expanderExpandedClass);
            if (_isExpanded) {
                $row_expander.trigger("click");
            }
        }
        // 展开所有的行
        target.expandAll=function() {
            target.find("tbody").find("tr").find(".treetable-expander").each(function(i,n){
                var _isCollapsed = $(n).hasClass(options.expanderCollapsedClass);
                if (_isCollapsed) {
                    $(n).trigger("click");
                }
            })
        }
        // 折叠所有的行
        target.collapseAll=function() {
            target.find("tbody").find("tr").find(".treetable-expander").each(function(i,n){
                var _isExpanded = $(n).hasClass(options.expanderExpandedClass);
                if (_isExpanded) {
                    $(n).trigger("click");
                }
            })
        }
        // 显示指定列
        target.showColumn=function(field,flag) {
            var _index = $.inArray(field, target.hiddenColumns);
            if (_index > -1) {
                target.hiddenColumns.splice(_index, 1);
            }
            target.find("."+field+"_cls").show();
            //是否更新列选项状态
            if(flag&&options.showColumns){
                var $input = $(".bootstrap-tree-table .treetable-bars .columns label").find("input[value='"+field+"']")
                $input.prop("checked", 'checked');
            }
        }
        // 隐藏指定列
        target.hideColumn=function(field,flag) {
            target.hiddenColumns.push(field);
            target.find("."+field+"_cls").hide();
            //是否更新列选项状态
            if(flag&&options.showColumns){
                var $input = $(".bootstrap-tree-table .treetable-bars .columns label").find("input[value='"+field+"']")
                $input.prop("checked", '');
            }
        }
        // ruoyi 解析数据，支持多层级访问
        var getItemField = function (item, field) {
            var value = item;

            if (typeof field !== 'string' || item.hasOwnProperty(field)) {
                return item[field];
            }
            var props = field.split('.');
            for (var p in props) {
                value = value && value[props[p]];
            }
            return value;
        };
        // ruoyi 发起对目标(target)函数的调用
        var calculateObjectValue = function (self, name, args, defaultValue) {
            var func = name;

            if (typeof name === 'string') {
                var names = name.split('.');

                if (names.length > 1) {
                    func = window;
                    $.each(names, function (i, f) {
                        func = func[f];
                    });
                } else {
                    func = window[name];
                }
            }
            if (typeof func === 'object') {
                return func;
            }
            if (typeof func === 'function') {
                return func.apply(self, args);
            }
            if (!func && typeof name === 'string' && sprintf.apply(this, [name].concat(args))) {
                return sprintf.apply(this, [name].concat(args));
            }
            return defaultValue;
        };
        // 初始化
        init();

        return target;
    };

    // 组件方法封装........
    $.fn.bootstrapTreeTable.methods = {
        // 为了兼容bootstrap-table的写法，统一返回数组，这里返回了表格显示列的数据
        getSelections: function(target, data) {
            // 所有被选中的记录input
            var _ipt = target.find("tbody").find("tr").find("input[name='select_item']:checked");
            var chk_value = [];
            // 如果是radio
            if (_ipt.attr("type") == "radio") {
                var _data = target.data_obj["id_" + _ipt.val()];
                chk_value.push(_data);
            } else {
                _ipt.each(function(_i, _item) {
                    var _data = target.data_obj["id_" + $(_item).val()];
                    chk_value.push(_data);
                });
            }
            return chk_value;
        },
        // 刷新记录
        refresh: function(target, parms) {
            if (parms) {
                target.refresh(parms);
            } else {
                target.refresh();
            }
        },
        // 添加数据到表格
        appendData: function(target, data) {
            if (data) {
                target.appendData(data);
            }
        },
        // 展开/折叠指定的行
        toggleRow: function(target, id) {
            target.toggleRow(id);
        },
        // 展开指定的行
        expandRow: function(target, id) {
            target.expandRow(id);
        },
        // 折叠 指定的行
        collapseRow: function(target, id) {
            target.collapseRow(id);
        },
        // 展开所有的行
        expandAll: function(target) {
            target.expandAll();
        },
        // 折叠所有的行
        collapseAll: function(target) {
            target.collapseAll();
        },
        // 显示指定列
        showColumn: function(target,field) {
            target.showColumn(field,true);
        },
        // 隐藏指定列
        hideColumn: function(target,field) {
            target.hideColumn(field,true);
        }
        // 组件的其他方法也可以进行类似封装........
    };

    $.fn.bootstrapTreeTable.defaults = {
        id: '',
        code: 'code',              // 选取记录返回的值,用于设置父子关系
        parentCode: 'parentCode',  // 用于设置父子关系
        rootIdValue: '0',         // 设置根节点id值----可指定根节点，默认为null,"",0,"0"
        data: null,                // 构造table的数据集合
        type: "GET",               // 请求数据的ajax类型
        url: null,                 // 请求数据的ajax的url
        ajaxParams: {},            // 请求数据的ajax的data属性
        expandColumn: 0,           // 在哪一列上面显示展开按钮
        expandAll: false,          // 是否全部展开
        expandFirst: true,         // 是否默认第一级展开--expandAll为false时生效
        striped: false,            // 是否各行渐变色
        bordered: false,            // 是否显示边框
        hover: true,               // 是否鼠标悬停
        condensed: false,          // 是否紧缩表格
        columns: [],               // 列
        toolbar: null,             // 顶部工具条
        height: 0,                 // 表格高度
        undefinedText: '-',        // 列无值默认显示
        sortName: "",              // 排序字段
        sortOrder: "asc",          // 默认升序
        totalRows: 0,              // 总共条数
        pageNumber: 1,             // 当前页条数
        pageSize: 6,               // 一页条数
        pageList: [6, 12, 18],     // 分页数据库
        showTitle: true,           // 是否采用title属性显示字段内容（被formatter格式化的字段不会显示）
        showSearch: true,          // 是否显示检索信息
        showColumns: true,         // 是否显示内容列下拉框
        showRefresh: true,         // 是否显示刷新按钮
        paginationPreText: '&lsaquo;',
        paginationNextText: '&rsaquo;',
        expanderExpandedClass: 'fa fa-angle-down', // 展开的按钮的图标
        expanderCollapsedClass: 'fa fa-angle-right', // 缩起的按钮的图标
        async: false,              //异步加载数据
        asynUrl: null,             //异步加载数据URL
        responseHandler: function(res) {
            return false;
        }
    };
})(jQuery);