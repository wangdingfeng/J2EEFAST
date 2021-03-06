/*!
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 * @author ZhouHuan 二次封装 新增若干方法优化部分BUG
 * @date 2020-12-21
 * @version v1.1.0
 * 、、、、、注意此为源文件、测试环境中使用、若部署生产请 压缩去掉注释
 */
if (typeof jQuery === "undefined") {
    throw new Error("fastJS JavaScript requires jQuery")
}
(function ($, window, undefined) {

    var opt = {
        //变量
        variable:{
            navtab:null,
            _tabIndex:-999,
            tindex:0,
            pushMenu:null,
            version:'1.1.0',
            debug:true,
            mode: 'storage',
            // 默认加载提示名人名言 如果不用 false
            loadTip:true,
            // 是否开启水印
            isWatermark:false,
            // 表格类型
            table_type : {
                bootstrapTable: 0,
                bootstrapTreeTable: 1
            },
            // 弹窗状态码
            modal_status : {
                SUCCESS: "success",
                FAIL: "error",
                WARNING: "warning"
            },
            // 消息状态码
            web_status : {
                SUCCESS: "00000",
                FAIL: 500,
                WARNING: "50001"
            },
            // 皮肤
            skins:[
                'skin-blue',
                'skin-black',
                'skin-red',
                'skin-yellow',
                'skin-purple',
                'skin-green',
                'skin-blue-light',
                'skin-black-light',
                'skin-red-light',
                'skin-yellow-light',
                'skin-purple-light',
                'skin-green-light'
            ]
        },

        /**
         * 在dom树ready之后执行给定的回调函数
         * @method domReady
         * @remind 如果在执行该方法的时候， dom树已经ready， 那么回调函数将立刻执行
         * @param { Function } fn dom树ready之后的回调函数
         * @example
         * ```javascript
         *
         * opt.domReady( function () {
         *
         *     console.log('123');
         *
         * } );
         *
         * ```
         */
        domReady:function () {

            var fnArr = [];

            function doReady(doc) {
                //确保onready只执行一次
                doc.isReady = true;
                for (var ci; ci = fnArr.pop(); ci()) {
                }
            }

            return function (onready, win) {
                win = win || window;
                var doc = win.document;
                onready && fnArr.push(onready);
                if (doc.readyState === "complete") {
                    doReady(doc);
                } else {
                    doc.isReady && doReady(doc);
                    var agent = navigator.userAgent.toLowerCase();
                    var ie = /(msie\s|trident.*rv:)([\w.]+)/.test(agent);
                    var version;
                    var v1 =  agent.match(/(?:msie\s([\w.]+))/);
                    var v2 = agent.match(/(?:trident.*rv:([\w.]+))/);
                    if(v1 && v2 && v1[1] && v2[1]){
                        version = Math.max(v1[1]*1,v2[1]*1);
                    }else if(v1 && v1[1]){
                        version = v1[1]*1;
                    }else if(v2 && v2[1]){
                        version = v2[1]*1;
                    }else{
                        version = 0;
                    }
                    if (ie && version != 11) {
                        (function () {
                            if (doc.isReady) return;
                            try {
                                doc.documentElement.doScroll("left");
                            } catch (error) {
                                setTimeout(arguments.callee, 0);
                                return;
                            }
                            doReady(doc);
                        })();
                        win.attachEvent('onload', function () {
                            doReady(doc)
                        });
                    } else {
                        doc.addEventListener("DOMContentLoaded", function () {
                            doc.removeEventListener("DOMContentLoaded", arguments.callee, false);
                            doReady(doc);
                        }, false);
                        win.addEventListener('load', function () {
                            doReady(doc)
                        }, false);
                    }
                }

            }
        }(),

        // 设置菜单栏收缩
        sidebarCollapse: function(){
            opt.variable.pushMenu.expandOnHover();
            if (!$('body').hasClass('sidebar-collapse')){
                $('[data-layout="sidebar-collapse"]').click();
            }
            $('[data-toggle="push-menu"]').click();
        },
        pushMenuInit: function(){
            $('.main-sidebar').unbind('mouseenter').unbind('mouseleave');
        },
        // --
        wclearInterval: function(){
            window.clearInterval(opt.variable.tindex);
        },
        getMessage:function(){
            $.getJSON("sys/user/info?_" + $.now(), function (r) {
            });
        },
        /*
        debug: function (message) {
            if (window.console && opt.variable.debug) {
                console.log(message)
            }
        },
		*/

        /**
         * 系统提示通知
         */
        toast: function(){
            if ($.toast) {
                return $.toast
            }
            try {
                if (parent.$.toast) {
                    return parent.$.toast
                }
                if (parent.parent.$.toast) {
                    return parent.parent.$.toast
                }
                if (top.$.toast) {
                    return top.$.toast
                }
            } catch (e) {}
            return null
        }(),

        error: function (msg,callback) {
            opt.modal.enable(); //显示提交按钮
            if(opt.toast){
                opt.toast({
                    heading: $.i18n.prop('警告'),
                    text: msg,
                    hideAfter:4000,
                    position: {
                        right: 7,
                        bottom: 32
                    },
                    showHideTransition: 'slide',
                    afterHidden: function () {
                        if(typeof(callback) === "function"){
                            callback("ok");
                        }
                    },
                    //stack: false,
                    icon: 'error'
                })
            }else{
                opt.modal.alertError(msg);
            }

        },

        info: function(msg,callback){
            if(opt.toast){
                opt.toast({
                    heading: $.i18n.prop('提示'),
                    text: msg,
                    hideAfter:4000,
                    position: {
                        right: 7,
                        bottom: 32
                    },
                    showHideTransition: 'slide',
                    afterHidden: function () {
                        if(typeof(callback) === "function"){
                            callback("ok");
                        }
                    },
                    icon: 'info'
                })
            }else{
                opt.modal.alertInfo(msg);
                if(typeof(callback) === "function"){
                    callback("ok");
                }
            }
        },

        success: function (msg,callback) {
            if(opt.toast){
                opt.toast({
                    heading: $.i18n.prop('成功'),
                    text: msg,
                    hideAfter:4000,
                    position: {
                        right: 7,
                        bottom: 32
                    },
                    showHideTransition: 'slide',
                    afterHidden: function () {
                        if(typeof(callback) === "function"){
                            callback("ok");
                        }
                    },
                    //stack: false,
                    icon: 'success'
                })
            }else{
                // opt.modal.success(msg,callback);
                layui.use('layer', function(){
                    var layer = layui.layer;
                    layer.msg(msg);
                });

                if(typeof(callback) === "function"){
                    callback("ok");
                }
            }

        },

        warning:function(text,callback){
            if(opt.toast){
                opt.toast({
                    heading: $.i18n.prop('警告'),
                    text: text,
                    hideAfter:4000,
                    position: {
                        right: 7,
                        bottom: 32
                    },
                    showHideTransition: 'slide',
                    afterHidden: function () {
                        if(typeof(callback) === "function"){
                            callback("ok");
                        }
                    },
                    //stack: false,
                    icon: 'warning'
                })
            }else{
                opt.modal.alertWarning(text);
            }
        },
        // 设置皮肤
        changeSkin: function(cls) {
            $.each(opt.variable.skins, function (i) {
                $('body').removeClass(opt.variable.skins[i])
            })
            $('body').addClass(cls);
            opt.storage.set('skin', cls)
            return false
        },
        // 设置页面模式
        setPageMode: function(src){
            if(top.location == self.location){
                var mode = opt.storage.get('mode',0);
                if(typeof mode !== 'undefined' && mode
                    && mode.length >0){
                    $('html').removeClass(mode);
                }
                if(typeof src !== 'undefined' && src
                    && src.length >0){
                    if (!$('html').hasClass(src)){
                        $('html').addClass(src);
                    }
                }
            }
        },
        //get menu config data for layer open  opt.getLeftMeunDataConfig('core','sys/comp')
        getLeftMeunDataConfig:function(module,href){
            var dataId = $("#leftMenu li a[data-url='"+href+"'][data-module='"+module+"']").attr("data-id") || -1;
            var dataIcon = $("#leftMenu li a[data-url='"+href+"'][data-module='"+module+"'] i").attr("data-icon") || "";
            var title = $("#leftMenu li a[data-url='"+href+"'][data-module='"+module+"'] span").html() || ""
            var data = {
                href: href,
                icon:  dataIcon,  //'fa fa-address-card',
                title: $.i18n.prop(title),
                id:   dataId, // $("#leftMenu .treeview-menu [data-url='sys/comp'],[data-module='core']").attr("data-id"), //'Y29yZTQx',
                module:module
            };
            return data
        },
        //get the frist menu config for layer open
        getLeftFirstMenuConig:function(){
            var href =  $("#leftMenu li a[data-url]:first-child").attr("data-url") || "main";
            var dataId = $("#leftMenu li a[data-url]:first-child").attr("data-id") || -1;
            var module = $("#leftMenu li a[data-url]:first-child").attr("data-module") ||"core";
            var dataIcon = $("#leftMenu li a[data-url]:first-child i").attr("data-icon") || "";
            var title = $("#leftMenu li a[data-url]:first-child span").html() ;
            var data = {
                href: href,
                icon:  dataIcon,
                title: $.i18n.prop(title),
                id:   dataId,
                module:module
            };
            return data
        },

        createMenuItem: function(dataUrl, menuName) {
            if(top.location !== self.location) {
                var dataIndex = opt.common.randomString(16);
                var panelId,module;
                if (dataUrl == undefined || $.trim(dataUrl).length == 0) return false;
                if (opt.common.isEmpty(parent.opt.variable.navtab)) {
                    parent.opt.createMenuItem(dataUrl, menuName);
                    return;
                }
                var topWindow = $(window.parent.document);
                if ($(".layui-tab-title li", topWindow).length > 0) {
                    $(".layui-tab-title li", topWindow).each(function () {
                        if ($(this).hasClass("layui-this")) {
                            panelId = $(this).attr("lay-id");
                            module = $(this).children('em').data('module');
                        }
                    })
                }
                var data = {
                    href: dataUrl,
                    icon: 'fa fa-plus-square',
                    panel: panelId,
                    title: menuName,
                    module:module,
                    id: dataIndex
                };
                parent.opt.navTabAdd(data);
                return;
            }else {
                opt.selfLayer.open({
                    type: 2,
                    maxmin: true,
                    shadeClose: true,
                    title: menuName,
                    area: [($(window).width() - 100)+'px',
                        ($(window).height() - 100) + 'px'],
                    content:dataUrl
                });
            }
        },

        closeItem: function(dataId){
            if(top.location!=self.location){
                var topWindow = $(window.parent.document);
                if($(".layui-tab-title li",topWindow).length > 0){
                    if(opt.common.isNotEmpty(dataId)){
                        $('.layui-tab-title li[lay-id="'+dataId+'"]',topWindow).children('i.layui-tab-close[data-id="' + data.id + '"]').trigger("click");
                    }else{
                        $(".layui-tab-title li",topWindow).each(function(){
                            if($(this).hasClass("layui-this")){
                                $(this).children('i.layui-tab-close[data-id="' + $(this).attr("lay-id") + '"]').trigger("click");
                            }
                        })
                    }
                }else{
                    try{
                        if(parent.opt.table.options.type == opt.variable.table_type.bootstrapTable){
                            parent.$.table.refresh();
                        }else if (parent.opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                            parent.$.treeTable.refresh();
                        }
                    }catch (e) {
                    }
                    parent.opt.selfLayer.closeAll();
                }
            }else {
                window.opener=null;
                window.open('','_self');
                window.close();
            }
        },

        //页面遮罩
        block: function(value,element){
            if(opt.common.isEmpty(value)){
                if(opt.common.isNotEmpty(element)){
                    $(element).block();
                }else{
                    $.blockUI();
                }
            }else{
                if(opt.common.isNotEmpty(element)){
                    $(element).block({ message: '<div class="loaderbox"><div class="loading-activity"></div> ' + $.i18n.prop(value) + '</div>' });
                }else{
                    $.blockUI({ message: '<div class="loaderbox"><div class="loading-activity"></div> ' + $.i18n.prop(value) + '</div>' });
                }
            }

        },

        unblock: function(element){
            if(opt.common.isNotEmpty(element)){
                $(element).unblock();
            }else{
                $.unblockUI();
            }
        },
        /*****************************************************/
        //借鉴 jeesite
        //模板引擎调用
        template: function (id, data, callback) {
            var tpl = String($("#" + id).html()).replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g, ""),
                data = data || [];
            if (typeof callback == "function") {
                laytpl(tpl).render(data || [], function (render) {
                    callback(render)
                });
                return null
            }
            return laytpl(tpl).render(data || [])
        },

        layer: function () {
            try {
                if (top.layer) {
                    return top.layer
                }
                if (parent.parent.layer) {
                    return parent.parent.layer
                }
                if (parent.layer) {
                    return parent.layer
                }
            } catch (e) {}
            if (window.layer) {
                return layer
            }
            return null
        }(),
        /******************************************************/
        //获取系统cookie
        getCookie: function(name){
            if (document.cookie.length>0){
                var c_start=document.cookie.indexOf(name + "=")
                if (c_start!=-1){
                    c_start=c_start + name.length+1
                    var c_end=document.cookie.indexOf(";",c_start)
                    if (c_end==-1) c_end=document.cookie.length
                    return unescape(document.cookie.substring(c_start,c_end))
                }
            }
            return ""
        },

        selfLayer:function(){
            if (window.layer) {
                return layer
            }
            try {
                if (top.layer && top.layer.window) {
                    return top.layer
                }
                if (parent.parent.layer && parent.parent.layer.window) {
                    return parent.parent.layer
                }
                if (parent.layer && parent.layer.window) {
                    return parent.layer
                }
            } catch (e) {}
            return null
        }(),
        //========================TAB================================//
        navDelTab: function (dataId) {
            layui.element.tabDelete("main-tab",dataId).init();
        },

        navTabAdd: function(data){
            opt.variable.navtab.tabAdd(data);
        },

        navToTab: function(dataId){
            layui.element.tabChange("main-tab",dataId);
        },

        navDelTabNow: function(){
            if($(".layui-tab-title li").length > 1){
                $(".layui-tab-title li").each(function(){
                    if($(this).attr("lay-id") != 0){
                        opt.navDelTab($(this).attr("lay-id"));
                    }
                })
            }
        },
        //========================TAB================================//

        //本地缓存处理
        storage:{
            set: function(key, value,tag) {
                if(typeof tag !== 'undefined'&& tag == 0){
                    window.localStorage.setItem(key, value);
                }else{
                    window.localStorage.setItem(_username+key, value);
                }
            },
            get: function(key,tag) {
                if(typeof tag !== 'undefined'&& tag == 0){
                    return window.localStorage.getItem(key);
                }else {
                    return window.localStorage.getItem(_username+key);
                }
            },
            remove: function(key,tag) {
                if(typeof tag !== 'undefined'&& tag == 0){
                    window.localStorage.removeItem(key);
                }else{
                    window.localStorage.removeItem(_username+key);
                }
            },
            clear: function() {
                window.localStorage.clear();
            }
        },
        // 当前table相关信息
        table : {
            config: {},
            // 当前实例配置
            options: {},
            // 设置实例配置
            set: function(id) {
                if(opt.common.getLength(opt.table.config) > 1) {
                    var tableId = opt.common.isEmpty(id) ? $(event.currentTarget).parents(".bootstrap-table").find(".table").attr("id") : id;
                    if (opt.common.isNotEmpty(tableId)) {
                        opt.table.options = opt.table.get(tableId);
                    }
                }
            },
            // 获取实例配置
            get: function(id) {
                return opt.table.config[id];
            },
            // 记住选择实例组
            rememberSelecteds: {},
            // 记住选择ID组
            rememberSelectedIds: {},

        },
        // 通用方法封装处理
        common: {
            /**
             * 判断对象是否为空
             * @param obj 需要判断的对象
             * @remind 如果判断的对象是NULL， 将直接返回true， 如果是数组且为空， 返回true， 如果是字符串， 且字符串为空，
             *          返回true， 如果是普通对象， 且该对象没有任何实例属性， 返回true
             * @returns {boolean} 对象是否为空
             * @example
             * ```javascript
             *
             * //output: true
             * console.log( opt.common.isEmpty( {} ) );
             *
             * //output: true
             * console.log( opt.common.isEmpty( [] ) );
             *
             * //output: true
             * console.log( opt.common.isEmpty( "" ) );
             *
             * //output: false
             * console.log( opt.common.isEmpty( { key: 1 } ) );
             *
             * //output: false
             * console.log( opt.common.isEmpty( [1] ) );
             *
             * //output: false
             * console.log( opt.common.isEmpty( "1" ) );
             *
             * //output: false
             * console.log( opt.common.isEmpty( 1 ) );
             *
             * ```
             */
            isEmpty: function (obj) {
                try{
                    if (obj == null || obj == undefined) return true;
                    if (this.isArray(obj) || this.isString(obj)) return obj.length === 0;
                    if (this.isNumber(obj)) return false;
                    if (this.isFunction(obj)) return false;
                    if (typeof obj === 'boolean') return false;
                    for (var key in obj) if (obj.hasOwnProperty(key)) return false;
                    return true;
                }catch (e) {
                    if(window.console)console.error(e);
                    return true;
                }
            },
            /**
             * 为网页添加文字水印方法
             * @param [Array] str 要添加的字符串数组,分别为姓名，手机号，日期
             * [name,mobileNumber,datetime]
             */
            addWaterMarker: function(str){
                var cpyName = str;
                if (str.length > 26) {
                    cpyName = str.substring(0, 26);
                }
                var can = document.createElement('canvas');
                var report = $('.wrapper')[0];
                report.appendChild(can);
                can.width = 280;
                can.height = 110;
                can.style.display = 'none';
                can.style.zIndex = '999999'

                var cans = can.getContext('2d');
                cans.rotate(-25 * Math.PI / 180);
                cans.font = "800 30px Microsoft JhengHei";
                cans.fillStyle = "#000";
                cans.textAlign = 'center';
                cans.textBaseline = 'Middle';
                if(cans.measureText(cpyName).width > 180) {
                    var size = 180 / cpyName.length
                    cans.font = '800 ' + size +'px '+ ' Microsoft JhengHei'
                }
                cans.fillText(cpyName, 60, 100);
                report.style.backgroundImage = "url(" + can.toDataURL("image/png") + ")";
            },

            watermark: function (settings) {
                //默认设置
                var defaultSettings={
                    watermark_txt:"text",
                    watermark_x:20,//水印起始位置x轴坐标
                    watermark_y:20,//水印起始位置Y轴坐标
                    watermark_rows:100,//水印行数
                    watermark_cols:20,//水印列数
                    watermark_x_space:10,//水印x轴间隔
                    watermark_y_space:10,//水印y轴间隔
                    watermark_color:'#aaa',//水印字体颜色
                    watermark_alpha:0.3,//水印透明度
                    watermark_fontsize:'15px',//水印字体大小
                    watermark_font:'微软雅黑',//水印字体
                    watermark_width:150,//水印宽度
                    watermark_height:80,//水印长度
                    watermark_angle:15//水印倾斜度数
                };
                //采用配置项替换默认值，作用类似jquery.extend
                if(arguments.length===1&&typeof arguments[0] ==="object" )
                {
                    var src=arguments[0]||{};
                    for(key in src)
                    {
                        if(src[key]&&defaultSettings[key]&&src[key]===defaultSettings[key])
                            continue;
                        else if(src[key])
                            defaultSettings[key]=src[key];
                    }
                }

                let oTemp = document.createDocumentFragment();

                //获取页面最大宽度
                let page_width = Math.max(document.body.scrollWidth,document.body.clientWidth);
                let cutWidth = page_width*0.0150;
                page_width=page_width-cutWidth;
                //获取页面最大高度
                let page_height = Math.max(document.body.scrollHeight-80,document.body.clientHeight-40);
                // var page_height = document.body.scrollHeight+document.body.scrollTop;
                //如果将水印列数设置为0，或水印列数设置过大，超过页面最大宽度，则重新计算水印列数和水印x轴间隔
                if (defaultSettings.watermark_cols == 0 || (parseInt(defaultSettings.watermark_x + defaultSettings.watermark_width *defaultSettings.watermark_cols + defaultSettings.watermark_x_space * (defaultSettings.watermark_cols - 1)) > page_width)) {
                    defaultSettings.watermark_cols = parseInt((page_width-defaultSettings.watermark_x+defaultSettings.watermark_x_space) / (defaultSettings.watermark_width + defaultSettings.watermark_x_space));
                    defaultSettings.watermark_x_space = parseInt((page_width - defaultSettings.watermark_x - defaultSettings.watermark_width * defaultSettings.watermark_cols) / (defaultSettings.watermark_cols - 1));
                }
                //如果将水印行数设置为0，或水印行数设置过大，超过页面最大长度，则重新计算水印行数和水印y轴间隔
                if (defaultSettings.watermark_rows == 0 || (parseInt(defaultSettings.watermark_y + defaultSettings.watermark_height * defaultSettings.watermark_rows + defaultSettings.watermark_y_space * (defaultSettings.watermark_rows - 1)) > page_height)) {
                    defaultSettings.watermark_rows = parseInt((defaultSettings.watermark_y_space + page_height - defaultSettings.watermark_y) / (defaultSettings.watermark_height + defaultSettings.watermark_y_space));
                    defaultSettings.watermark_y_space = parseInt(((page_height - defaultSettings.watermark_y) - defaultSettings.watermark_height * defaultSettings.watermark_rows) / (defaultSettings.watermark_rows - 1));
                }
                let x;
                let y;
                for (let i = 0; i < defaultSettings.watermark_rows; i++) {
                    y = defaultSettings.watermark_y + (defaultSettings.watermark_y_space + defaultSettings.watermark_height) * i;
                    for (let j = 0; j < defaultSettings.watermark_cols; j++) {
                        x = defaultSettings.watermark_x + (defaultSettings.watermark_width + defaultSettings.watermark_x_space) * j ;
                        var mask_div = document.createElement('div');
                        mask_div.id = 'mask_div' + i + j;
                        mask_div.className = 'mask_div';
                        mask_div.appendChild(document.createTextNode(defaultSettings.watermark_txt));
                        //设置水印div倾斜显示
                        mask_div.style.webkitTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                        mask_div.style.MozTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                        mask_div.style.msTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                        mask_div.style.OTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                        mask_div.style.transform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                        mask_div.style.visibility = "";
                        mask_div.style.position = "fixed";
                        mask_div.style.left = x + 'px';
                        mask_div.style.top = y + 'px';
                        mask_div.style.overflow = "hidden";
                        mask_div.style.zIndex = "19920219";
                        mask_div.style.pointerEvents='none';//pointer-events:none  让水印不遮挡页面的点击事件
                        //mask_div.style.border="solid #eee 1px";
                        mask_div.style.opacity = defaultSettings.watermark_alpha;
                        mask_div.style.fontSize = defaultSettings.watermark_fontsize;
                        mask_div.style.fontFamily = defaultSettings.watermark_font;
                        mask_div.style.color = defaultSettings.watermark_color;
                        mask_div.style.textAlign = "center";
                        mask_div.style.width = defaultSettings.watermark_width + 'px';
                        mask_div.style.height = defaultSettings.watermark_height + 'px';
                        mask_div.style.display = "block";
                        //交叉网格显示
                        if((i%2==0)&&(j%2==0)){
                            oTemp.appendChild(mask_div);
                        }
                        if((i%2==1)&&(j%2==1)){
                            oTemp.appendChild(mask_div);
                        }
                    };
                };
                document.body.appendChild(oTemp);
            },
            /**
             * 检测值是否为 基本类型
             */
            isPrimitive: function (value) {
                return (
                    typeof value === 'string' ||
                    typeof value === 'number' ||
                    // $flow-disable-line
                    typeof value === 'symbol' ||
                    typeof value === 'boolean'
                )
            },
            // 判断一个字符串是否为非空串
            isNotEmpty: function (value) {
                return !opt.common.isEmpty(value);
            },
            // 空对象转字符串
            nullToStr: function(value,_default) {
                if (opt.common.isEmpty(value)) {
                    return (opt.common.isEmpty(_default))?"":_default;
                }
                return value;
            },
            // 是否显示数据 为空默认为显示
            visible: function (value) {
                if (opt.common.isEmpty(value) || value == true) {
                    return true;
                }
                return false;
            },
            /**
             * 删除字符串str的首尾空格
             * @param value 必须是字符串
             * @returns {string} 删除首尾空格字符
             * @example
             * var str = ' j2eefast ';
             *
             * //output: 10
             * console.log( str.length );
             *
             * //output: 8
             * console.log( opt.common.trim( ' j2eefast ').length );
             *
             * //output: 10
             * console.log( str.length );
             */
            trim: function (value) {
                if (value == null) {
                    return "";
                }
                if(typeof value === 'string'){
                    return value.toString().replace(/(^[ \t\n\r]+)|([ \t\n\r]+$)/g, '');
                }else{
                    return value;
                }
            },
            hideStr:function(value,len, tag){
                if (opt.common.isEmpty(value)) {
                    return "-";
                }else{
                    if(tag == 0){
                        if(value.length > len){
                            return value.substr(0,len) + "...";
                        }else{
                            return value;
                        }
                    }else{
                        return "..."+value.substr(value.length-len,len);
                    }
                }
            },
            // 比较两个字符串（大小写敏感）
            equals: function (str, that) {
                return str == that;
            },
            // 比较两个字符串（大小写不敏感）
            equalsIgnoreCase: function (str, that) {
                return String(str).toUpperCase() === String(that).toUpperCase();
            },
            // 将字符串按指定字符分割
            split: function (str, sep, maxLen) {
                if (opt.common.isEmpty(str)) {
                    return null;
                }
                var value = String(str).split(sep);
                return maxLen ? value.slice(0, maxLen - 1) : value;
            },
            // 字符串格式化(%s )
            sprintf: function (str) {
                var args = arguments, flag = true, i = 1;
                str = str.replace(/%s/g, function () {
                    var arg = args[i++];
                    if (typeof arg === 'undefined') {
                        flag = false;
                        return '';
                    }
                    return arg;
                });

                return flag ? str : '';
            },
            // 指定随机数返回
            random: function (min, max) {
                return Math.floor((Math.random() * max) + min);
            },
            // 指定随机生成字符串
            randomString:function (len) {
                len = len || 32;
                var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';    /****默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/
                var maxPos = $chars.length;
                var pwd = '';
                for (i = 0; i < len; i++) {
                    pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
                }
                return pwd;
            },
            // 判断字符串是否是以start开头
            startWith: function(value, start) {
                var reg = new RegExp("^" + start);
                return reg.test(value)
            },
            // 判断字符串是否是以end结尾
            endWith: function(value, end) {
                var reg = new RegExp(end + "$");
                return reg.test(value)
            },
            // 数组去重
            uniqueFn: function(array) {
                var result = [];
                var hashObj = {};
                for (var i = 0; i < array.length; i++) {
                    if (!hashObj[array[i]]) {
                        hashObj[array[i]] = true;
                        result.push(array[i]);
                    }
                }
                return result;
            },
            // 数组中的所有元素放入一个字符串
            join: function(array, separator) {
                if (opt.common.isEmpty(array)) {
                    return null;
                }
                return array.join(separator);
            },
            // 获取form下所有的字段并转换为json对象
            formToJSON: function(formId) {
                var json = {};
                $.each($("#" + formId).serializeArray(), function(i, field) {
                    if(json[field.name]) {
                        json[field.name] += ("," + field.value);
                    } else {
                        json[field.name] = field.value;
                    }
                });
                return json;
            },
            getJsonValue:function(obj,k){
                // var _r;
                // for(var key  in obj){
                //     if(key == k){
                //         _r = obj[key];
                //         break;
                //     }
                // }
                // return _r;

                var rn = obj;
                var props = k.split('.');
                for (var p in props) {
                    if(rn[props[p]] && typeof rn[props[p]] != 'undefined'){
                        rn = rn[props[p]];
                    }else {
                        rn = '';
                    }
                }
                return rn;
            },
            objToEmpty:function(obj){
                // for(var i=0; i<obj.length; i++){
                //     if(Array.prototype==obj[i].__proto__){
                //         obj[key] = [];
                //     }else if(typeof obj[key] == "string"){
                //         obj[key] = '';
                //     }else if(typeof obj[key] == "number"){
                //         obj[key] = 0;
                //     }
                // }

                for(var key  in obj){
                    if(Array.prototype==obj[key].__proto__){
                        obj[key] = [];
                    }else if(typeof obj[key] == "string"){
                        obj[key] = '';
                    }else if(typeof obj[key] == "number"){
                        obj[key] = 0;
                    }
                }
                return obj;
            },
            /**
             * 获取对象长度
             * @param obj
             * @returns {number}
             */
            getLength: function(obj) {
                var count = 0;
                for (var i in obj) {
                    if (obj.hasOwnProperty(i)) {
                        count++;
                    }
                }
                return count;
            },
            /**
             * 页面复制粘贴板
             * @param text
             */
            copy:function(text){
                var oInput = document.createElement('input');
                oInput.value = text;
                $(oInput).css({opacity:'0'})
                $(oInput).attr({name:"__copy_secukey"})
                document.body.appendChild(oInput);
                oInput.select(); // 选择对象
                document.execCommand("Copy"); // 执行浏览器复制命令
                oInput.className = 'oInput';
                $("input[name='__copy_secukey']").remove()
            },
            /**
             * 获取 Checkbox 值 已,隔开
             */
            getCheckboxValue:function (name) {
                var txt = "";
                $('input[name="'+name+'"]').each(function () {
                    if($(this).is(':checked')){
                        txt+=$(this).val()+","
                    }
                });
                if(txt.length > 0){
                    txt = txt.substring(0,txt.length-1);
                }
                return txt;
            },
            /**
             * 将字符串字符 转数字,如果转换失败，则返回原始字符串。
             */
            toNumber: function (val) {
                var n = parseFloat(val);
                return isNaN(n) ? val : n
            },
            /**
             *  从数组中删除
             *  例如 var a = [1,2], var b= 2
             *      opt.common.remove(a,b);
             *      中a 值为 [1]
             */
            remove: function (arr, item) {
                if (arr.length) {
                    var index = arr.indexOf(item);
                    if (index > -1) {
                        return arr.splice(index, 1)
                    }
                }
            },
            /**
             * 截取数组
             * 例如 var a = [1,2,3,4,5,6], var b = 3;
             *     返回 [4, 5, 6]
             */
            toArray: function (list, start) {
                start = start || 0;
                var i = list.length - start;
                var ret = new Array(i);
                while (i--) {
                    ret[i] = list[i + start];
                }
                return ret
            },
            /**
             * 将source对象中的属性扩展到target对象上
             * @method extend
             * @remind 该方法将强制把source对象上的属性复制到target对象上
             * @see opt.common.extend(Object,Object,Boolean)
             * @param { Object } target 目标对象， 新的属性将附加到该对象上
             * @param { Object } source 源对象， 该对象的属性会被附加到target对象上
             * @return { Object } 返回target对象
             * @example
             * ```javascript
             *
             * var target = { name: 'target', sex: 1 },
             *      source = { name: 'source', age: 17 };
             *
             * opt.common.extend( target, source );
             *
             * //output: { name: 'source', sex: 1, age: 17 }
             * console.log( target );
             *
             * ```
             */

            /**
             * 将source对象中的属性扩展到target对象上， 根据指定的isKeepTarget值决定是否保留目标对象中与
             * 源对象属性名相同的属性值。
             * @method extend
             * @param { Object } target 目标对象， 新的属性将附加到该对象上
             * @param { Object } source 源对象， 该对象的属性会被附加到target对象上
             * @param { Boolean } isKeepTarget 是否保留目标对象中与源对象中属性名相同的属性
             * @return { Object } 返回target对象
             * @example
             * ```javascript
             *
             * var target = { name: 'target', sex: 1 },
             *      source = { name: 'source', age: 17 };
             *
             * opt.common.extend( target, source, true );
             *
             * //output: { name: 'target', sex: 1, age: 17 }
             * console.log( target );
             *
             * ```
             */
            extend:function (target, source, isKeepTarget) {
                if (source) {
                    for (var k in source) {
                        if (!isKeepTarget || !target.hasOwnProperty(k)) {
                            target[k] = source[k];
                        }
                    }
                }
                return target;
            },

            /**
             * 用给定的迭代器遍历数组或类数组对象
             * @method each
             * @param { Array } array 需要遍历的数组或者类数组
             * @param { Function } iterator 迭代器， 该方法接受两个参数， 第一个参数是当前所处理的value， 第二个参数是当前遍历对象的key
             * @example
             * ```javascript
             * var divs = document.getElmentByTagNames( "div" );
             *
             * //output: 0: DIV, 1: DIV ...
             * opt.common.each( divs, funciton ( value, key ) {
             *
             *     console.log( key + ":" + value.tagName );
             *
             * } );
             * ```
             */
            each : function(obj, iterator, context) {
                if (obj == null) return;
                if (obj.length === +obj.length) {
                    for (var i = 0, l = obj.length; i < l; i++) {
                        if(iterator.call(context, obj[i], i, obj) === false)
                            return false;
                    }
                } else {
                    for (var key in obj) {
                        if (obj.hasOwnProperty(key)) {
                            if(iterator.call(context, obj[key], key, obj) === false)
                                return false;
                        }
                    }
                }
            },

            /**
             * 将一个对象数组合并到一个对象中。
             */
            toObject: function (arr) {
                var that = this;
                var res = {};
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i]) {
                        that.extend(res, arr[i]);
                    }
                }
                return res
            },

            /**
             * 金额转换中文汉字
             * @param money
             * @returns {string}
             * @example
             * ```javascript
             *
             * var money = opt.common.moneyToChinese(12390.97);
             *
             * //output: 壹万贰仟叁佰玖拾元玖角柒分
             * console.log( money );
             *
             * ```
             */
            moneyToChinese : function (money) {
                var cnNums = new Array("零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"); //汉字的数字
                var cnIntRadice = new Array("", "拾", "佰", "仟"); //基本单位
                var cnIntUnits = new Array("", "万", "亿", "兆"); //对应整数部分扩展单位
                var cnDecUnits = new Array("角", "分", "毫", "厘"); //对应小数部分单位
                var cnInteger = "整"; //整数金额时后面跟的字符
                var cnIntLast = "元"; //整型完以后的单位
                var maxNum = 999999999999999.9999; //最大处理的数字
                var IntegerNum; //金额整数部分
                var DecimalNum; //金额小数部分
                var ChineseStr = ""; //输出的中文金额字符串
                var parts; //分离金额后用的数组，预定义
                var Symbol = "";//正负值标记
                if (opt.common.isEmpty(money)) {
                    return "";
                }
                money = parseFloat(money);
                if (money >= maxNum) {
                    throw new Error('超出最大金额!');
                }
                if (money == 0) {
                    ChineseStr = cnNums[0] + cnIntLast + cnInteger;
                    return ChineseStr;
                }
                if (money < 0) {
                    money = -money;
                    Symbol = "负 ";
                }
                money = money.toString(); //转换为字符串
                if (money.indexOf(".") == -1) {
                    IntegerNum = money;
                    DecimalNum = '';
                } else {
                    parts = money.split(".");
                    IntegerNum = parts[0];
                    DecimalNum = parts[1].substr(0, 4);
                }
                if (parseInt(IntegerNum, 10) > 0) { //获取整型部分转换
                    var zeroCount = 0;
                    var IntLen = IntegerNum.length;
                    for (var i = 0; i < IntLen; i++) {
                        var n = IntegerNum.substr(i, 1);
                        var p = IntLen - i - 1;
                        var q = p / 4;
                        var m = p % 4;
                        if (n == "0") {
                            zeroCount++;
                        }
                        else {
                            if (zeroCount > 0) {
                                ChineseStr += cnNums[0];
                            }
                            zeroCount = 0; //归零
                            ChineseStr += cnNums[parseInt(n)] + cnIntRadice[m];
                        }
                        if (m == 0 && zeroCount < 4) {
                            ChineseStr += cnIntUnits[q];
                        }
                    }
                    ChineseStr += cnIntLast;
                    //整型部分处理完毕
                }
                if (DecimalNum != '') { //小数部分
                    var decLen = DecimalNum.length;
                    for (var i = 0; i < decLen; i++) {
                        var n = DecimalNum.substr(i, 1);
                        if (n != '0') {
                            ChineseStr += cnNums[Number(n)] + cnDecUnits[i];
                        }
                    }
                }
                if (ChineseStr == '') {
                    ChineseStr += cnNums[0] + cnIntLast + cnInteger;
                } else if (DecimalNum == '') {
                    ChineseStr += cnInteger;
                }
                ChineseStr = Symbol + ChineseStr;
                return ChineseStr;
            },
            /**
             * 版本比较函数
             * @param version1
             * @param version2
             * @returns {number}
             */
            compareVersion: function(version1, version2) {
                var arr1 = version1.split('.');
                var arr2 = version2.split('.');
                var length1 = arr1.length;
                var length2 = arr2.length;
                var minlength = Math.min(length1, length2);
                var i = 0;
                for (i ; i < minlength; i++) {
                    var a = parseInt(arr1[i]);
                    var b = parseInt(arr2[i]);
                    if (a > b) {
                        return 1;
                    } else if (a < b) {
                        return -1;
                    }
                }
                if (length1 > length2) {
                    for(var j = i; j < length1; j++) {
                        if (parseInt(arr1[j]) != 0) {
                            return 1;
                        }
                    }
                    return 0;
                } else if (length1 < length2) {
                    for(var j = i; j < length2; j++) {
                        if (parseInt(arr2[j]) != 0) {
                            return -1;
                        }
                    }
                    return 0;
                }
                return 0;
            },
            /**
             * 数字 转金额格式
             * 12345678,2,'$',',','.'
             * --> $12,345,678.00
             * @param number 数字
             * @param places 保留多少位
             * @param symbol 货币符号
             * @param thousand 整数部分千位分隔符
             * @param decimal 小数部分分隔符
             * @returns {string}
             */
            formatMoney: function(number, places, symbol, thousand, decimal) {
                number = number || 0;
                places = !isNaN(places = Math.abs(places)) ? places : 2;
                symbol = symbol !== undefined ? symbol : "$";
                thousand = thousand || ",";
                decimal = decimal || ".";
                var negative = number < 0 ? "-" : "",
                    i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
                    j = (j = i.length) > 3 ? j % 3 : 0;
                return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
            },

            /**
             * 播放音频
             * @param src 可以传网络url 也可传项目audio文件夹下面音频文件名称
             */
            playSound: function (src) {
                if (!(src.indexOf('http') == 0)) {
                    src = baseURL + 'static/audio/' + src + '.wav';
                }
                if (!!window.ActiveXObject || "ActiveXObject" in window) {  // IE
                    var embed = document.noticePlay;
                    if (embed) {
                        embed.remove();
                    }
                    embed = document.createElement('embed');
                    embed.setAttribute('name', 'noticePlay');
                    embed.setAttribute('src', src);
                    embed.setAttribute('autostart', true);
                    embed.setAttribute('loop', false);
                    embed.setAttribute('hidden', true);
                    document.body.appendChild(embed);
                    embed = document.noticePlay;
                    embed.volume = 100;
                } else {   // 非IE
                    var audio = document.createElement('audio');
                    audio.setAttribute('hidden', true);
                    audio.setAttribute('src', src);
                    document.body.appendChild(audio);
                    audio.addEventListener('ended', function () {
                        audio.parentNode.removeChild(audio);
                    }, false);
                    audio.play();
                }
            },

            /**
             * 发送AJax数据 Jq Ajax 原生方法添加防CSRF攻击
             * @param request
             */
            sendAjax :function(config){
                //防CSRF攻击
                if(opt.common.equalsIgnoreCase(config.type,'POST') && opt.common.isNotEmpty($('meta[name="csrf-token"]').attr("content"))){
                    config = opt.common.extend(config,{headers: {
                            "X-CSRF-Token": $('meta[name="csrf-token"]').attr("content")
                        }});
                }
                if(opt.common.isEmpty(config.error) || typeof config.error != "function"){
                    config = opt.common.extend(config,{error:
                            function(xhr, textStatus) {
                                try{
                                    opt.error(JSON.parse(xhr.responseText).msg || xhr.responseText );
                                }catch (e) {
                                    console.error(xhr.responseText);
                                }
                                opt.modal.closeLoading();
                                return;
                            }
                    });
                }
                $.ajax(config);
            },

            /**
             * 通过下载地址 静默下载
             * @param url
             */
            downLoadFile :function(url){
                var judgeDiv = document.getElementById("dwDiv");
                if(judgeDiv!=null){
                    document.body.removeChild(judgeDiv);
                }
                var divObj = document.createElement("div");
                divObj.id="dwDiv";
                var aObj = document.createElement("a");
                aObj.href=encodeURI(url);
                aObj.id = "hrefFile";
                divObj.appendChild(aObj);
                document.body.appendChild(divObj);
                document.getElementById("hrefFile").click();
                judgeDiv = document.getElementById("dwDiv");
                if(judgeDiv!=null){
                    document.body.removeChild(judgeDiv);
                }
            },
            digit: function(e, t) {
                var i = "";
                e = String(e),
                    t = t || 2;
                for (var n = e.length; n < t; n++)
                    i += "0";
                return e < Math.pow(10, t) ? i + (0 | e) : e
            },
            //日期格式转换
            toDateString: function(data, t) {
                var that = this
                    , n = new Date(data || new Date)
                    , a = [that.digit(n.getFullYear(), 4), that.digit(n.getMonth() + 1), that.digit(n.getDate())]
                    , o = [that.digit(n.getHours()), that.digit(n.getMinutes()), that.digit(n.getSeconds())];
                return t = t || "yyyy-MM-dd HH:mm:ss",
                    t.replace(/yyyy/g, a[0]).replace(/MM/g, a[1]).replace(/dd/g, a[2]).replace(/HH/g, o[0]).replace(/mm/g, o[1]).replace(/ss/g, o[2])
            },
            formatDat: function(date, format) {
                var that = this;
                if(that.isEmpty(date)) return "";
                if (!date) return;
                if (!format) format = "yyyy-MM-dd";
                switch(typeof date) {
                    case "string":
                        date = new Date(date.replace(/-/, "/"));
                        break;
                    case "number":
                        date = new Date(date);
                        break;
                }
                if (!date instanceof Date) return;
                var dict = {
                    "yyyy": date.getFullYear(),
                    "M": date.getMonth() + 1,
                    "d": date.getDate(),
                    "H": date.getHours(),
                    "m": date.getMinutes(),
                    "s": date.getSeconds(),
                    "MM": ("" + (date.getMonth() + 101)).substr(1),
                    "dd": ("" + (date.getDate() + 100)).substr(1),
                    "HH": ("" + (date.getHours() + 100)).substr(1),
                    "mm": ("" + (date.getMinutes() + 100)).substr(1),
                    "ss": ("" + (date.getSeconds() + 100)).substr(1)
                };
                return format.replace(/(yyyy|MM?|dd?|HH?|ss?|mm?)/g, function() {
                    return dict[arguments[0]];
                });
            },
            //HTML转义
            escape: function(html){
                return String(html||'').replace(/&(?!#?[a-zA-Z0-9]+;)/g, '&amp;')
                    .replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/'/g, '&#39;').replace(/"/g, '&quot;');
            },
            //url编码
            encodeUrl: function(a) {
                return encodeURIComponent(a)
            },
            //url解码
            decodeUrl: function(a) {
                return decodeURIComponent(a)
            },
            /**
             * 截取字符串考虑中文字符
             * @param str 源字符
             * @param len 长度
             * @param hasDot 超过长度是否用...代替
             * @returns 截取之后字符串
             */
            subString: function(str, len, hasDot) {
                var newLength = 0;
                var newStr = "";
                var chineseRegex = /[^\x00-\xff]/g;
                var singleChar = "";
                var strLength = str.replace(chineseRegex,"**").length;
                for(var i = 0;i < strLength;i++){
                    singleChar = str.charAt(i).toString();
                    if(singleChar.match(chineseRegex) != null)
                    {
                        newLength += 2;
                    }
                    else
                    {
                        newLength++;
                    }
                    if(newLength > len)
                    {
                        break;
                    }
                    newStr += singleChar;
                }
                if(hasDot && strLength > len){
                    newStr += "...";
                }
                return newStr;
            },
            /**
             * 字节转换
             */
            bytesToSize : function (bytes) {
                if (bytes === 0) return '0 B';
                var k = 1024;
                sizes = ['B','KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
                i = Math.floor(Math.log(bytes) / Math.log(k));
                var num = bytes / Math.pow(k, i);
                return num.toPrecision(3) + ' ' + sizes[i];
                //return (bytes / Math.pow(k, i)) + ' ' + sizes[i];
                //toPrecision(3) 后面保留一位小数，如1.0GB //return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
            },
            /**
             * Get file name from path(获取文件路径获取文件名)
             * @param {String} file path to file
             * @return filename
             */
            fileFromPath: function(file){
                return file.replace(/.*(\/|\\)/, "");
            },

            /**
             * Get file extension lowercase(获取文件后缀名称)
             * @param {String} file name
             * @return file extenstion
             */
            getExt: function(file){
                return (-1 !== file.indexOf('.')) ? file.replace(/.*[.]/, '') : '';
            },

            /**
             * Repair the path(http修复路径)
             * @param path
             * @returns {string}
             */
            optimizationPath: function(path) {
                var protocol = /^[a-z]+:\/\//.exec(path)[0],
                    tmp = null,
                    res = [];
                path = path.replace(protocol, "").split("?")[0].split("#")[0];
                path = path.replace(/\\/g, '/').split(/\//);
                path[path.length - 1] = "";
                while (path.length) {
                    if (( tmp = path.shift() ) === "..") {
                        res.pop();
                    } else if (tmp !== ".") {
                        res.push(tmp);
                    }
                }
                return protocol + res.join("/");
            }
        },
        // 弹出层封装处理
        modal: {
            // 显示图标
            icon: function(type) {
                var icon = "";
                if (type == opt.variable.modal_status.WARNING) {
                    icon = 0;
                } else if (type == opt.variable.modal_status.SUCCESS) {
                    icon = 1;
                } else if (type == opt.variable.modal_status.FAIL) {
                    icon = 2;
                } else {
                    icon = 3;
                }
                return icon;
            },
            // 消息提示
            msg: function(content, type) {
                if (type != undefined) {
                    layui.use('layer', function(){
                        var layer = layui.layer;
                        layer.msg(content, { icon: opt.modal.icon(type), time: 1000, shift: 5 });
                    });
                } else {
                    layui.use('layer', function(){
                        var layer = layui.layer;
                        layer.msg(content);
                    });
                }
            },
            // 错误消息
            msgError: function(content) {
                opt.modal.msg(content,  opt.variable.modal_status.FAIL);
            },
            // 成功消息
            msgSuccess: function(content) {
                opt.modal.msg(content,  opt.variable.modal_status.SUCCESS);
            },
            // 警告消息
            msgWarning: function(content) {
                opt.modal.msg(content,  opt.variable.modal_status.WARNING);
            },
            // 弹出提示
            alert: function(content, type,shadeClose) {
                opt.selfLayer.alert(content, {
                    icon: opt.modal.icon(type),
                    title: $.i18n.prop("系统提示"),
                    shadeClose: opt.common.isEmpty(shadeClose) ? true : shadeClose,
                    btn: ['<i class="fa fa-check"></i> '+$.i18n.prop("确定")],
                    btnclass: ['btn btn-primary'],
                });
            },
            // 消息提示并刷新父窗体
            msgReload: function(msg, type) {
                opt.selfLayer.msg(msg, {
                        icon: opt.modal.icon(type),
                        time: 500
                        //,shade: [0.4, '#8F8F8F']
                    },
                    function() {
                        opt.modal.reload();
                    });
            },
            // 错误提示
            alertError: function(content) {
                opt.modal.alert(content,  opt.variable.modal_status.FAIL);
            },
            // 成功提示
            alertSuccess: function(content) {
                opt.modal.alert(content,  opt.variable.modal_status.SUCCESS);
            },
            // 成功提示
            alertInfo: function(content) {
                opt.modal.alert(content, "");
            },
            // 成功提示
            success: function(msg, callback) {
                opt.selfLayer.alert(msg,{
                    icon: 1,
                    closeBtn: 0,
                    title: $.i18n.prop("系统提示"),
                    anim: 1,
                    // skin: 'layui-layer-molv',
                    yes:function(index){
                        if(typeof(callback) === "function"){
                            callback("ok");
                        }
                        opt.selfLayer.close(index);
                    }
                });
            },
            error : function(msg,callback){
                opt.error(msg,callback);
            },
            warning :function(msg,callback){
                opt.warning(msg,callback);
            },
            // 警告提示
            alertWarning: function(content) {
                opt.modal.alert(content, opt.variable.modal_status.WARNING,true);
            },
            // 关闭窗体
            close: function () {
                var index = opt.selfLayer.getFrameIndex(window.name);
                if(opt.common.isNotEmpty(index)){
                    opt.selfLayer.close(index);
                }else{
                    opt.layer.closeAll();
                }

            },
            // 关闭全部窗体
            closeAll: function () {
                opt.selfLayer.closeAll();
                opt.layer.closeAll();
            },
            // 确认窗体
            confirm: function (content, callback, shadeClose) {
                opt.selfLayer.confirm(content, {
                    icon: 3,
                    shadeClose: opt.common.isEmpty(shadeClose) ? true : shadeClose,
                    title: '<i class="fa fa-exclamation-circle"></i> '+$.i18n.prop("系统提示"),
                    btn: ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-close"></i> '+$.i18n.prop("取消")]
                }, function (index) {
                    if(typeof(callback) === "function"){
                        callback("ok");
                    }
                    opt.selfLayer.close(index);
                });
            },
            /**
             * 右侧弹出窗口 一般用于信息查看用
             * @param title 标题
             * @param url 展示地址
             * @param isEdit 是否显示修改按钮
             * @param editFun 修改方法
             */
            popupRight: function(title, url, isEdit,editFun,id){
                var width = 150;
                var topp = 50;
                if(top.location !== self.location) {
                    if($(top.window).outerWidth() < 400){
                        width = 50;
                    }
                }else{
                    topp = 0;
                    if($(window).outerWidth() < 400){
                        width = 50;
                    }
                }
                opt.layer.open({
                    type: 2,
                    //shade: false,
                    scrollbar:false,
                    anim:-1,
                    closeBtn: 0,
                    //shade: 0.3,
                    move: false,
                    title: title,
                    shadeClose: true,
                    outLeft: true,
                    skin:'layui-anim layui-anim-rl',
                    offset: [topp +'px', ($(top.window?top.window:window).outerWidth() -($(window).outerWidth()-width))  + 'px'],
                    area: [($(window).outerWidth()-width) + 'px', '100%'],
                    success: function(layero, index){
                        if(opt.common.isNotEmpty(isEdit) && isEdit){
                            var update = $('<div id="'+index+'" class="layui-right-update"><i class="fa fa-mail-reply-all"></i> 修改</div>').on('click',function(){
                                opt.layer.close($(this).attr('id'));
                                if( typeof  editFun == "function"){
                                    window.setTimeout(function(){
                                        editFun(id);
                                    },200);
                                }
                            });
                            if(!$(layero).find('.layui-right-update').html()){
                                $(layero).find('.layui-layer-title').attr("style","font-size:13px;").before(update);
                            }
                        }else{
                            var update = $('<div id="'+index+'" class="layui-right-update"><i class="fa fa fa-times-circle-o"></i> 关闭</div>').on('click',function(){
                                opt.layer.close($(this).attr('id'));
                            });
                            if(!$(layero).find('.layui-right-update').html()){
                                $(layero).find('.layui-layer-title').attr("style","font-size:13px;").before(update);
                            }
                        }
                    },
                    content: url
                });
            },
            /**
             * 弹出层指定宽度 此方法弹出窗口会在宽度高度做自动适配
             * 如果 width height 你设置了数值,当你设置的数值大于当前窗口的最大值则会全屏展示
             *
             * @param title 弹出窗口Title *必输
             * @param url 弹出窗口URL  *必输
             * @param width 指定弹出窗口宽度 [非必输]
             * @param height 指定弹出窗口高度 [非必输]
             * @param callback 弹出窗口点击确定按钮回调 弹出本页函数 [非必输] 如果不输入 则回调弹出的页面submitHandler 方法
             * @param yes [非必输] 只有在传 true 则先回调弹出层submitHandler 方法如果此submitHandler方法返回true,则再回调 callback 方法
             */
            open: function (title, url,width, height,callback,type) {
                var full = false;
                //如果是移动端，就使用自适应大小弹窗
                if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                    width = 'auto';
                    height = 'auto';
                    full = true;
                }
                if (opt.common.isEmpty(title)) {
                    title = false;
                }
                if (opt.common.isEmpty(url)) {
                    url = "/404.html";
                }
                if (opt.common.isEmpty(width)) {
                    width = 800;
                }
                if (opt.common.isEmpty(height)) {
                    height = ($(window).height() - 50);
                }
                if (opt.common.isEmpty(type)) {
                    type = 2;
                }
                //自动适配窗口大小 如果传的大小比所在窗口大 则最大化
                if(width !== 'auto' || height !== 'auto'){
                    if(width > $(window).width() || height > $(window).height() ){
                        full = true;
                    }
                }
                var submit;

                // 查看是否传回调函数
                if(!opt.common.isEmpty(callback) && typeof callback == 'function'){
                    submit = function(index, layero) {
                        var iframeWin = layero.find('iframe')[0];
                        if(typeof iframeWin.contentWindow.submitHandler == 'function'){
                            if(iframeWin.contentWindow.submitHandler(index, layero)){
                                callback(index,layero,opt.selfLayer);
                                return;
                            }
                        }else{
                            callback(index,layero,opt.selfLayer);
                            return;
                        }
                    }
                }else{
                    submit = function(index, layero) {
                        var iframeWin = layero.find('iframe')[0];
                        if(typeof iframeWin.contentWindow.submitHandler == 'function'){
                            iframeWin.contentWindow.submitHandler(index, layero)
                            return;
                        }else{
                            opt.selfLayer.close(index);
                            return;
                        }
                    }
                }

                opt.modal.loading($.i18n.prop("数据加载中，请稍后..."));

                var index = opt.selfLayer.open({
                    type: type,
                    area: [width + 'px', height + 'px'],
                    fix: false,
                    //不固定
                    maxmin: true,
                    //shade: 0.3,
                    title: $.i18n.prop(title),
                    content: url,
                    btn: ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-close"></i> '+$.i18n.prop("取消")],
                    // 弹层外区域关闭
                    shadeClose: true,
                    yes:submit,
                    success: function(layero, index){
                        // var ifWin = window[layero.find('iframe')[0]['name']];
                        // $("#_addTab",ifWin.document).addClass("hide");
                        // if (!opt.common.isEmpty(obj)) {
                        var iframeWin = layero.find('iframe')[0];
                        if(!opt.common.isEmpty(iframeWin.contentWindow.onLoadSuccess)){
                            iframeWin.contentWindow.onLoadSuccess(index,layero,opt.selfLayer);
                        }
                        // }
                        opt.modal.closeLoading();
                    },
                    cancel: function(index) {
                        return true;
                    },
                    end: function(){
                        opt.modal.closeLoading();
                    }
                });

                if(full){
                    opt.selfLayer.full(index);
                }

            },
            /**
             * btn : 按钮
             * url : 访问连接
             * title : 窗口标题
             * width : 窗口宽
             * height : 窗口高
             * fromData : POST 提交的参数
             * type : 基本层类型
             * @param options
             */
            // 弹出层指定参数选项
            openOptions: function (options) {
                var _btn = opt.common.isEmpty(options.btn) ? [] : options.btn;
                var _url = opt.common.isEmpty(options.url) ? "/404.html" : options.url;
                var _title = opt.common.isEmpty(options.title) ? $.i18n.prop("系统窗口") : $.i18n.prop(options.title);
                var _width = opt.common.isEmpty(options.width) ? "800" : options.width;
                var _height = opt.common.isEmpty(options.height) ? ($(window).height() - 50) : options.height;
                var _framData = opt.common.isEmpty(options.fromData) ? {} : options.fromData;
                var _type = opt.common.isEmpty(options.type) ? 2 : options.type;

                var full = false;
                //如果是移动端，就使用自适应大小弹窗
                if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                    _width = 'auto';
                    _height = 'auto';
                    full = true;
                }
                var index;
                //自动适配窗口大小 如果传的大小比所在窗口大 则最大化
                if(_width !== 'auto' || _height !== 'auto'){
                    if(_width > $(window).width() || _height > $(window).height() ){
                        full = true;
                    }
                }
                if(options.clear){
                    if(_btn.length == 0){
                        _btn = ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-trash-o"></i> '+$.i18n.prop("清除"),'<i class="fa fa-close"></i> '+$.i18n.prop("取消")];
                    }
                }else{
                    if(_btn.length == 0){
                        _btn = ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-close"></i> '+$.i18n.prop("取消")];
                    }
                }
                var _sf;
                if(opt.common.isNotEmpty(options.main) && options.main){
                    _sf = opt.layer;
                    _height = opt.common.isEmpty(options.height) ? ($(top.window).height() - 220) : options.height;
                }else{
                    _sf = opt.selfLayer;
                }

                if (opt.common.isEmpty(options.yes)) {
                    options.yes = function(index, layero) {
                        var iframeWin = layero.find('iframe')[0];
                        if(typeof iframeWin.contentWindow.submitHandler == 'function'){
                            if(iframeWin.contentWindow.submitHandler(index, layero,_sf)){
                                if(typeof  options.callBack == 'function'){
                                    options.callBack(index, layero,_sf);
                                }
                            }
                            return;
                        }else{
                            if(typeof  options.callBack == 'function'){
                                options.callBack(index, layero,_sf);
                                return;
                            }
                        }
                    }
                }

                index = _sf.open({
                    type: _type,
                    maxmin: true,
                    //shade: 0.3,
                    title: _title,
                    fix: false,
                    area: [_width + 'px', _height + 'px'],
                    content: _url,
                    fromData: _framData,
                    shadeClose: opt.common.isEmpty(options.shadeClose) ? true : options.shadeClose,
                    skin: options.skin,
                    btn: _btn,
                    yes: options.yes,
                    //取消或者清除
                    btn2: function(index, layero){
                        if(options.clear){
                            options.clear(index, layero,_sf);
                        }else{
                            if (!opt.common.isEmpty(options.cancel)) {
                                options.cancel(index,layero);
                            }
                            _sf.close(index);
                        }
                    },
                    btn3: function(index, layero){
                        if (!opt.common.isEmpty(options.cancel)) {
                            options.cancel(index,layero);
                        }
                        _sf.close(index);
                    },
                    success: function(layero, index){
                        // if (!opt.common.isEmpty(options.obj)) {
                        var iframeWin = layero.find('iframe')[0];
                        //判断页面是否有初始方法有就执行
                        if(typeof(iframeWin.contentWindow.onLoadSuccess) === "function"){
                            iframeWin.contentWindow.onLoadSuccess(options.obj,layero, index,_sf);
                        }
                        // }
                    },
                    cancel: function(index, layero){
                        if (!opt.common.isEmpty(options.cancel)) {
                            options.cancel(index,layero);
                        }
                        return true;
                    }
                });

                if(full){
                    _sf.full(index);
                }
            },
            /**
             * 窗口有确定 取消按钮
             * 在主窗口弹出窗口  url *必输 title *必输 callBack 非必输  but  非必输
             * callBack 如果没传 在弹出主窗口 的确定 事件没有 则会直接关掉窗口
             * but 弹出窗口是否自带确定取消按钮
             * 当弹出窗口会检测弹出窗口页面里面是否有onLoadSuccess 方法 如果有会执行此方法
             * options = { url: "",title:"", callBack:"" , but: false}
             * @param options
             */
            openMainWin :function(options){
                var _url = opt.common.isEmpty(options.url) ? "/404.html" : options.url;
                var _title = opt.common.isEmpty(options.title) ? $.i18n.prop("系统窗口") : $.i18n.prop(options.title);
                var _width = opt.common.isEmpty(options.width) ? $(top.window).width() - 100 : options.width;
                var _height = opt.common.isEmpty(options.height) ? $(top.window).height() - 100 : options.height;
                var _framData = opt.common.isEmpty(options.fromData) ? {} : options.fromData;
                var full = false;
                //如果是移动端，就使用自适应大小弹窗
                if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                    _width = 'auto';
                    _height = 'auto';
                    full = true;
                }
                var index;
                //自动适配窗口大小 如果传的大小比所在窗口大 则最大化
                if(_width !== 'auto' || _height !== 'auto'){
                    if(_width > $(top.window).width() || _height > $(top.window).height() ){
                        full = true;
                    }
                }

                if(opt.common.isEmpty(options.but)){
                    options.but = true;
                }

                if(options.but){
                    if(options.clear){
                        _btn = ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-trash-o"></i> '+$.i18n.prop("清除"),'<i class="fa fa-close"></i> '+$.i18n.prop("取消")];
                    }else{
                        _btn = ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-close"></i> '+$.i18n.prop("取消")];
                    }

					if (!opt.common.isEmpty(options.callBack)) {
                        options.yes = function(index, layero) {
                            var iframeWin = layero.find('iframe')[0];
                            if(typeof iframeWin.contentWindow.submitHandler == 'function'){
                                if(iframeWin.contentWindow.submitHandler(index, layero)){
                                    window.setTimeout(function(){
                                        options.callBack(index,layero,opt.layer);
                                    },100);
                                }
                            }else{
                                /**
                                 * 注意返回入参
                                 */
                                options.callBack(index,layero,opt.layer);
                            }
                        }
                    }else{
                        options.yes = function(index, layero) {
                            var iframeWin = layero.find('iframe')[0];
                            if(typeof iframeWin.contentWindow.submitHandler == 'function'){
                                if(iframeWin.contentWindow.submitHandler(index, layero)){
                                    window.setTimeout(function(){
                                        opt.layer.close(index);
                                    },100);
                                }
                            }else{
                                opt.layer.close(index);
                            }
                        }
                    }

                    index =  opt.layer.open({
                        type: 2,
                        maxmin: true,
                        shadeClose: true,
                        title: _title,
                        area: [_width+'px',
                            _height + 'px'],
                        scrollbar: false,
                        content:_url,
                        fromData: _framData,
                        success: function(layero, index){
                            var iframeWin = layero.find('iframe')[0];
                            //判断页面是否有
                            if(typeof(iframeWin.contentWindow.onLoadSuccess) === "function"){
                                iframeWin.contentWindow.onLoadSuccess(index,layero,opt.layer);
                            }
                        },
                        btn: opt.common.isEmpty(options.btn) ? _btn : options.btn,
                        yes: options.yes,
                        //取消或者清除
                        btn2: function(index, layero){
                            if(options.clear){
                                options.clear(index, layero,opt.layer);
                            }else{
                                opt.layer.close(index);
                            }
                        },
                        btn3: function(index, layero){
                            opt.layer.close(index);
                        },
                        cancel: function(index, layero){
                            if (!opt.common.isEmpty(options.cancel)) {
                                options.cancel(index,layero);
                            }
                            //opt.layer.close(index);
                            return true;
                        }
                    });
                }else{
                    index =  opt.layer.open({
                        type: 2,
                        maxmin: true,
                        shadeClose: true,
                        title: _title,
                        area: [_width+'px',
                            _height + 'px'],
                        content:_url,
                        success: function(layero, index){
                            opt.selfLayer = opt.layer;
                            var iframeWin = layero.find('iframe')[0];
                            //判断页面是否有
                            if(typeof(iframeWin.contentWindow.onLoadSuccess) === "function"){
                                iframeWin.contentWindow.onLoadSuccess(index,layero,opt.layer);
                            }
                        }
                    });
                }

                if(full){
                    opt.layer.full(index);
                }
            },
            // 弹出层全屏 本窗口
            openFull: function (title, url, width, height) {
                //如果是移动端，就使用自适应大小弹窗
                if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                    width = 'auto';
                    height = 'auto';
                }
                if (opt.common.isEmpty(title)) {
                    title = false;
                }
                if (opt.common.isEmpty(url)) {
                    url = "/404.html";
                }
                if (opt.common.isEmpty(width)) {
                    width = 800;
                }
                if (opt.common.isEmpty(height)) {
                    height = ($(window).height() - 50);
                }
                var index = opt.selfLayer.open({
                    type: 2,
                    area: [width + 'px', height + 'px'],
                    fix: false,
                    //不固定
                    maxmin: true,
                    //shade: 0.3,
                    title: title,
                    content: url,
                    btn: ['<i class="fa fa-check"></i> '+$.i18n.prop("确定"), '<i class="fa fa-close"></i> '+$.i18n.prop("取消")],
                    // 弹层外区域关闭
                    shadeClose: true,
                    yes: function(index, layero) {
                        var iframeWin = layero.find('iframe')[0];
                        iframeWin.contentWindow.submitHandler(index, layero);
                    },
                    cancel: function(index) {
                        return true;
                    }
                });
                opt.selfLayer.full(index);
            },
            //预览文件
            openView:function(name,url,width,height){
                var url = baseURL + "sys/component/fileViwe?fileName="+ opt.common.encodeUrl(name) +
                    "&fileUrl=" + opt.common.encodeUrl(url);
                width = opt.common.isEmpty(width)? $(top.window).width() - 200:width;
                height = opt.common.isEmpty(height)? $(top.window).height() - 50:height;
                opt.layer? opt.layer.open({
                    type: 2,
                    maxmin: false,
                    shadeClose: true,
                    title: false,
                    area: [width + "px", height + "px"],
                    content: url
                }):opt.modal.windowOpen(url);
            },
            windowOpen: function(url, title, width, height) {
                width && height || (width = window.screen.width - 200,
                    height = window.screen.height - 150);
                var top = parseInt((window.screen.height - height) / 2 - 20, 10)
                    , left = parseInt((window.screen.width - width) / 2, 10);
                window.open(url, title, "location=no,menubar=no,toolbar=no," +
                    "dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes,resizable=yes," +
                    "scrollbars=yes,width=" + width + ",height=" + height + ",top=" + top + ",left=" + left)
            },
            // 选卡页方式打开
            openTab: function (title, url) {
                opt.createMenuItem(url, title);
            },
            // 选卡页同一页签打开
            parentTab: function (title, url) {
                var dataId = window.frameElement.getAttribute('data-id');
                opt.createMenuItem(url, title);
                opt.closeItem(dataId);
            },
            // 关闭选项卡
            closeTab: function (dataId) {
                opt.closeItem(dataId);
            },
            // 禁用按钮
            disable: function() {
                var doc = window.top == window.parent ? window.document : window.parent.document;
                try {
                    $("a[class*=layui-layer-btn]", doc).addClass("layer-disabled");
                    $('button[class="btn btn-sm btn-primary"]',doc).attr("disabled",true);
                    // $('button[class="btn btn-sm btn-primary"]',doc).addClass("layer-disabled");
                }catch (e) {}
            },
            // 启用按钮
            enable: function() {
                var doc = window.top == window.parent ? window.document : window.parent.document;
                try{
                    $("a[class*=layui-layer-btn]", doc).removeClass("layer-disabled");
                    $('button[class="btn btn-sm btn-primary"]',doc).attr("disabled",false);
                }catch (e) {}
            },
            // 打开遮罩层
            loading: function (message) {
                if(opt.common.isEmpty(message)){
                    $.blockUI();
                }else{
                    $.blockUI({ message: '<div class="loaderbox"><div class="loading-activity"></div> ' + message + '</div>' });
                }
            },
            // 关闭遮罩层
            closeLoading: function () {
                // if(top.location != self.location){
                //     setTimeout(function(){
                //         $('.content-wrapper', window.parent.document).unblock();
                //     }, 50);
                // }else{
                setTimeout(function(){
                    $.unblockUI();
                }, 50);
                // }
            },
            // 立即关闭遮罩层
            closeNowLoading: function () {
                if(top.location != self.location){
                    $('.content-wrapper', window.parent.document).unblock();
                }else{
                    $.unblockUI();
                }
            },
            // 重新加载
            reload: function () {
                parent.location.reload();
            }
        },
        // 操作封装处理
        operate: {
            // 提交数据
            submit: function(url, type, dataType, data, callback,del) {
                var config = {
                    url: url,
                    type: type,
                    dataType: dataType,
                    data: data,
                    beforeSend: function () {
                        opt.modal.loading("正在处理中，请稍后...");
                    },
                    success: function(result) {
                        //判断如果时删除提交且表格有记住我 需要删除记住我里面删除的数据
                        if (result.code == opt.variable.web_status.SUCCESS && opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                            if(!opt.common.isEmpty(del) && del){
                                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                                    var s = opt.common.getJsonValue(data,"ids").split(',');
                                    var column = opt.common.isEmpty(opt.table.options.uniqueId) ? opt.table.options.columns[1].field : opt.table.options.uniqueId;
                                    var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                                    var p = [];
                                    if(opt.common.isNotEmpty(selectedRows)) {
                                        for (var j = 0; j < selectedRows.length; j++) {
                                            for(var i=0; i< s.length; i++){
                                                if(opt.common.getJsonValue(selectedRows[j],column) === s[i]){
                                                    p[p.length] = selectedRows[j];
                                                    selectedRows.splice(j, 1);
                                                    j = j - 1;
                                                }
                                            }
                                        }
                                    }
                                    var selectedIds = opt.table.rememberSelectedIds[opt.table.options.id];
                                    if(opt.common.isNotEmpty(selectedIds)) {
                                        for (var j = 0; j < selectedIds.length; j++) {
                                            for(var i=0; i< p.length; i++){
                                                if(opt.common.getJsonValue(p[i],column) === selectedIds[j]){
                                                    selectedIds.splice(j, 1);
                                                    j = j - 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (typeof callback == "function") {
                            callback(result);
                        }
                        opt.operate.ajaxSuccess(result);
                    }
                };
                opt.common.sendAjax(config);
            },
            //删除单独调用post删除
            delPost:function(url, data, callback) {
                //重置清空页面记住我数据
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    opt.table.rememberSelecteds = {};
                    opt.table.rememberSelectedIds = {};
                }
                opt.operate.submit(url, "post", "json", data, callback);
            },
            // post请求传输
            post: function(url, data, callback) {
                opt.operate.submit(url, "post", "json", data, callback);
            },
            // get请求传输
            get: function(url, callback) {
                opt.operate.submit(url, "get", "json", "", callback);
            },
            /**
             *  弹出详细信息
             * @param id url带参数
             * @param title 弹出title 默认为表格参数 table.options.modalName + "详细"
             * @param width 弹出宽度
             * @param height 弹出高度
             */
            detail: function(id,title, width, height) {
                opt.table.set();
                var _url = opt.operate.detailUrl(id);
                var _width = opt.common.isEmpty(width) ? "800" : width;
                var _height = opt.common.isEmpty(height) ? ($(window).height() - 50) : height;
                //如果是移动端，就使用自适应大小弹窗
                if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                    _width = 'auto';
                    _height = 'auto';
                }
                var options = {
                    title: opt.common.isEmpty(title)?opt.table.options.modalName + "详细":title,
                    width: _width,
                    height: _height,
                    url: _url,
                    //skin: 'layui-layer-gray',
                    btn: ['关闭'],
                    yes: function (index,layero) {
                        opt.layer.close(index) || opt.selfLayer.close(index);
                    }
                };
                opt.modal.openOptions(options);
            },
            // 详细访问地址
            detailUrl: function(id) {
                var url = "/404.html";
                if (opt.common.isNotEmpty(id)) {
                    url = opt.table.options.detailUrl.replace("{id}", id);
                } else {
                    var id = opt.common.isEmpty(opt.table.options.uniqueId) ? $.table.selectFirstColumns() : $.table.selectColumns(opt.table.options.uniqueId);
                    if (id.length == 0) {
                        opt.modal.alertWarning("请至少选择一条记录");
                        return;
                    }
                    url = opt.table.options.detailUrl.replace("{id}", id);
                }
                return url;
            },
            // 删除信息
            del: function(id,tableId) {

                if(opt.common.isNotEmpty(tableId)){
                    opt.table.set(tableId);
                }else{
                    opt.table.set();
                }
                if (opt.common.isEmpty( opt.table.options.delUrl)){
                    opt.modal.msgError("delUrl 未传!");
                    return;
                }
                //表格编辑回调
                if(typeof bootTableCallback === 'function'){
                    //获取当前编辑数据
                    var rows = $.table.selectAllColumnRows(tableId);
                    rows = rows.length ==0?[$.table.getRowByUniqueId(id)]:rows;
                    //当回调函数返回false 代码终止
                    if(!bootTableCallback(opt.table.options.id,'del',rows)){
                        return;
                    }
                }
                opt.modal.confirm("确定删除该条" + opt.table.options.modalName + "信息吗？", function() {
                    if(opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                        var row = $("#" + opt.table.options.id).bootstrapTreeTable('getSelections')[0];
                        var url;
                        if (opt.common.isEmpty(row)) {
                            url = opt.table.options.delUrl;
                        }else{
                            url = opt.table.options.delUrl.replace("{id}",  row[opt.table.options.uniqueId]);
                        }
                        opt.operate.get(url);
                    } else {
                        var url = opt.common.isEmpty(id) ? opt.table.options.delUrl : opt.table.options.delUrl.replace("{id}", id);
                        var data = { "ids": id };
                        opt.operate.submit(url, "POST", "json", data,"",true);
                    }
                });
            },
            /**
             * 处理信息
             * @param id
             */
            exe:function(id){
                opt.table.set();
                opt.modal.confirm("确定处理该条" + opt.table.options.modalName + "信息吗？", function() {
                    var url = opt.common.isEmpty(id) ? opt.table.options.exeUrl : opt.table.options.exeUrl.replace("{id}", id);
                    if(opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                        opt.operate.get(url);
                    } else {
                        var data = { "ids": id };
                        opt.operate.submit(url, "POST", "json", data);
                    }
                });
            },
            // 批量删除信息
            delAll: function(tableId) {
				if(opt.common.isEmpty(tableId)){
                    opt.table.set();
                }else{
                    opt.table.set(tableId);
                }
                if (opt.common.isEmpty( opt.table.options.delUrl)){
                    opt.modal.msgError("delUrl 未传!");
                    return;
                }

                //表格编辑回调
                if(typeof bootTableCallback === 'function'){
                    //获取当前编辑数据
                    var rows = $.table.selectAllColumnRows(tableId);
                    //当回调函数返回false 代码终止
                    if(!bootTableCallback(opt.table.options.id,'del',rows)){
                        return;
                    }
                }
                var rows  = opt.common.isEmpty(opt.table.options.uniqueId) ? $.table.selectFirstColumns() : $.table.selectColumns(opt.table.options.uniqueId);
                if (opt.common.isEmpty(rows)) {
                    opt.modal.error("请至少选择一条记录");
                    return;
                }
                opt.modal.confirm("确认要删除选中的" + rows.length + "条数据吗?", function() {
                    var url = opt.table.options.delUrl;
                    var data = { "ids": rows.join() };
                    opt.operate.submit(url, "POST", "json", data,"",true);
                });
            },
            // 清空信息
            clean: function() {
                opt.table.set();
                opt.modal.confirm("确定清空所有" + opt.table.options.modalName + "吗？", function() {
                    var url = opt.table.options.cleanUrl;
                    opt.operate.submit(url, "post", "json", "");
                });
            },
            // 添加信息
            add: function(id) {
                opt.table.set();
                opt.modal.open("添加" + opt.table.options.modalName, opt.operate.addUrl(id));
            },
            //处理信息
            exeDis: function(id,title) {
                opt.table.set();
                opt.modal.open("处理[" + title+"]", opt.operate.exeUrl(id));
            },
            // 添加信息，以tab页展现
            addTab: function (id) {
                opt.table.set();
                opt.modal.openTab("添加" + opt.table.options.modalName, opt.operate.addUrl(id));
            },
            // 添加信息 全屏
            addFull: function(id) {
                opt.table.set();
                var url = opt.common.isEmpty(id) ? opt.table.options.addUrl : opt.table.options.addUrl.replace("{id}", id);
                opt.modal.openFull("添加" + opt.table.options.modalName, url);
            },
            // 添加访问地址
            addUrl: function(id) {
                opt.table.set();
                var url = opt.common.isEmpty(id) ?  opt.table.options.addUrl.replace("{id}", "") :  opt.table.options.addUrl.replace("{id}", id);
                return url;
            },
            exeUrl: function(id) {
                opt.table.set();
                var url = opt.common.isEmpty(id) ?  opt.table.options.exeUrl.replace("{id}", "") :  opt.table.options.exeUrl.replace("{id}", id);
                return url;
            },
            /**
             * 修改信息
             * @param id 修改数据ID
             * @param tableId 表格对象ID
             */
            edit: function(id,tableId) {
                opt.table.set(tableId);
                if (opt.common.isEmpty( opt.table.options.editUrl)){
                    opt.modal.msgError("editUrl 未传!");
                    return;
                }
                //表格编辑回调
                if(typeof bootTableCallback === 'function'){
                    //获取当前编辑数据
                    var rows;
                    if(opt.common.isEmpty(id)){
                        //获取当前编辑数据
                        rows = $.table.selectAllColumnRows(tableId);
                        rows = rows.length ==0?[$.table.getRowByUniqueId(id)]:rows;
                    }else{
                        rows = [$.table.getRowByUniqueId(id)];
                    }
                    //当回调函数返回false 代码终止
                    /**
                     * 回调参数
                     * ID 表格对象ID
                     * event 方法名称
                     * data 当前数据
                     */
                    if(!bootTableCallback(opt.table.options.id,'edit',rows)){
                        return;
                    }
                }
                if(opt.common.isEmpty(id) &&  opt.table.options.type ==  opt.variable.table_type.bootstrapTreeTable) {
                    var row = $("#" + opt.table.options.id).bootstrapTreeTable('getSelections')[0];
                    if (opt.common.isEmpty(row)) {
                        opt.modal.error("请至少选择一条记录");
                        return;
                    }
                    var url = opt.table.options.editUrl.replace("{id}", row[opt.table.options.uniqueId]);
                    opt.modal.open("修改" + opt.table.options.modalName, url);
                } else {
                    opt.modal.open("修改" + opt.table.options.modalName, opt.operate.editUrl(id));
                }
            },
            // 查看表详情
            view: function(id,edit,fun){
                var _self = this;
                opt.table.set();
                if (opt.common.isEmpty( opt.table.options.viewUrl)){
                    opt.modal.msgError("viewUrl 未传!");
                    return;
                }
                if(opt.common.isEmpty(id)){
                    opt.modal.error("id必须传!");
                    return;
                }
                if(opt.common.isEmpty(edit)){
                    edit = false;
                }else{
                    edit = true;
                }
                var url = opt.table.options.viewUrl.replace("{id}", id);
                opt.modal.popupRight(opt.table.options.modalName + "信息详情",url,
                    edit,fun,id);
            },
            // 修改信息，以tab页展现
            editTab: function(id,prefix,tableId) {
                opt.table.set();
                if (opt.common.isEmpty( opt.table.options.editUrl)){
                    opt.modal.msgError("editUrl 未传!");
                    return;
                }
                //表格编辑回调
                if(typeof bootTableCallback === 'function'){
                    //获取当前编辑数据
                    var rows;
                    if(opt.common.isEmpty(id)){
                        //获取当前编辑数据
                        rows = $.table.selectAllColumnRows(tableId);
                        rows = rows.length ==0?[$.table.getRowByUniqueId(id)]:rows;
                    }else{
                        rows = [$.table.getRowByUniqueId(id)];
                    }
                    //当回调函数返回false 代码终止
                    /**
                     * 回调参数
                     * ID 表格对象ID
                     * event 方法名称
                     * data 当前数据
                     */
                    if(!bootTableCallback(opt.table.options.id,'edit',rows)){
                        return;
                    }
                }
                if(opt.common.isEmpty(id) &&  opt.table.options.type ==  opt.variable.table_type.bootstrapTreeTable) {
                    var row = $("#" + opt.table.options.id).bootstrapTreeTable('getSelections')[0];
                    if (opt.common.isEmpty(row)) {
                        opt.modal.error("请至少选择一条记录");
                        return;
                    }
                    var url = opt.table.options.editUrl.replace("{id}", row[opt.table.options.uniqueId]);
                    opt.modal.openTab(opt.common.isEmpty(prefix)?"修改":prefix + opt.table.options.modalName, opt.operate.editUrl(id));
                } else {
                    opt.modal.openTab(opt.common.isEmpty(prefix)?"修改":prefix + opt.table.options.modalName, opt.operate.editUrl(id));
                }
            },
            // 修改信息 全屏
            editFull: function(id) {
                opt.table.set();
                var url = "/404.html";
                if (opt.common.isNotEmpty(id)) {
                    url = opt.table.options.editUrl.replace("{id}", id);
                } else {
                    var row = opt.common.isEmpty(opt.table.options.uniqueId) ? $.table.selectFirstColumns() : $.table.selectColumns(opt.table.options.uniqueId);
                    url = opt.table.options.editUrl.replace("{id}", row);
                }
                opt.modal.openFull("修改" + opt.table.options.modalName, url);
            },
            // 修改访问地址
            editUrl: function(id) {
                opt.table.set();
                var url = "/404.html";
                if (opt.common.isNotEmpty(id)) {
                    url = opt.table.options.editUrl.replace("{id}", id);
                } else {
                    var id = opt.common.isEmpty(opt.table.options.uniqueId) ? $.table.selectFirstColumns() : $.table.selectColumns(opt.table.options.uniqueId);
                    if (id.length == 0) {
                        opt.modal.error("请至少选择一条记录");
                        return;
                    }
                    url = opt.table.options.editUrl.replace("{id}", id);
                }
                return url;
            },
            /****
             *  保存信息并且刷新表格 若有传callback 回调函数 则自行处理 保存信息服务端返回的信息 后续工作
             * @param url 保存信息URL
             * @param data 保存数据
             * @param callback 返回信息 回调函数
             */
            save: function(url, data, callback) {
                var config = {
                    url: url,
                    type: "post",
                    dataType: "json",
                    data: data,
                    beforeSend: function () {
                        opt.modal.disable();
                        opt.modal.loading($.i18n.prop("数据加载中，请稍后..."));
                    },
                    success: function(result) {
                        if (typeof callback == "function") {
                            opt.modal.closeLoading();
                            callback(result);
                        }else{
                            opt.operate.successCallback(result);
                        }
                    }
                };
                opt.common.sendAjax(config);
            },
            // 保存信息 弹出提示框
            saveModal: function(url, data, callback) {
                var config = {
                    url: url,
                    type: "post",
                    dataType: "json",
                    data: data,
                    beforeSend: function () {
                        opt.modal.loading($.i18n.prop("数据加载中，请稍后..."));
                    },
                    success: function(result) {
                        opt.modal.closeLoading();
                        if (typeof callback == "function") {
                            callback(result);
                            return;
                        }
                        if (result.code == opt.variable.web_status.SUCCESS) {
                            opt.modal.success($.i18n.prop("操作成功!"))
                        } else if (result.code == opt.variable.web_status.WARNING) {
                            opt.modal.warning(result.msg)
                        } else {
                            opt.modal.error(result.msg);
                        }
                    }
                };
                opt.common.sendAjax(config);
            },
            // 保存选项卡信息
            saveTab: function(url, data, callback) {
                var config = {
                    url: url,
                    type: "POST",
                    dataType: "JSON",
                    data: data,
                    beforeSend: function () {
                        opt.modal.loading($.i18n.prop("数据加载中，请稍后..."));
                    },
                    success: function(result) {
                        if (typeof callback == "function") {
                            opt.modal.closeLoading();
                            callback(result);
                        }else{
                            // opt.modal.closeLoading();
                            //opt.modal.success($.i18n.prop("操作成功!"),function () {
                            opt.operate.successTabCallback(result);
                            //});
                        }
                    }
                };
                opt.common.sendAjax(config);
            },
            // 保存结果弹出msg刷新table表格
            ajaxSuccess: function (result) {
                if (result.code == opt.variable.web_status.SUCCESS && opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                    opt.success($.i18n.prop("操作成功!"))
                    $.table.refresh();
                } else if (result.code == opt.variable.web_status.SUCCESS && opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                    opt.success($.i18n.prop("操作成功!"))
                    $.treeTable.refresh();
                }else if (result.code == opt.variable.web_status.SUCCESS){
                    opt.success($.i18n.prop("操作成功!"))
                }else if (result.code == opt.variable.web_status.WARNING) {
                    opt.modal.warning(result.msg)
                }  else {
                    opt.modal.error(result.msg);
                }
                opt.modal.closeLoading();
            },
            // 成功结果提示msg（父窗体全局更新）
            saveSuccess: function (result) {
                if (result.code == opt.variable.web_status.SUCCESS) {
                    opt.modal.msgReload("保存成功,正在刷新数据请稍后……", opt.variable.modal_status.SUCCESS);
                } else if (result.code == opt.variable.web_status.WARNING) {
                    opt.modal.alertWarning(result.msg)
                }  else {
                    opt.modal.alertError(result.msg);
                }
                opt.modal.closeLoading();
            },
            // 成功回调执行事件（父窗体静默更新）
            successCallback: function(result) {
                opt.modal.closeLoading();
                if (result.code == opt.variable.web_status.SUCCESS) {
                    var parent = window.parent;
                    if (parent.opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                        parent.$.table.refresh();
                        parent.opt.success("操作成功", parent.opt.modal.closeAll());
                        // parent.opt.modal.closeAll();

                    } else if (parent.opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                        parent.$.treeTable.refresh();
                        parent.opt.success("操作成功",parent.opt.modal.closeAll());
                    } else {
                        if (window.opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                            window.$.table.refresh();
                            window.opt.success("操作成功", parent.opt.modal.closeAll());
                        }else{
                            opt.modal.msgReload("保存成功,正在刷新数据请稍后……", opt.variable.modal_status.SUCCESS);
                        }
                    }
                } else if (result.code == opt.variable.web_status.WARNING) {
                    opt.modal.warning(result.msg);
                }  else {
                    opt.modal.error(result.msg);
                }
                opt.modal.enable();
            },
            // 选项卡成功回调执行事件（父窗体静默更新）
            successTabCallback: function(result) {
                opt.modal.closeLoading();
                if(top.location!=self.location){
                    if (result.code == opt.variable.web_status.SUCCESS) {
                        var topWindow = $(window.parent.document);
                        var currentId = $('.layui-tab-title', topWindow).find('.layui-this').children('em').attr('panel-id');
                        if(opt.common.isNotEmpty(currentId)){
                            var $contentWindow = $('iframe[data-id="' + currentId + '"]', topWindow)[0].contentWindow;
                            $contentWindow.opt.success($.i18n.prop("操作成功!"));
                            // if ($contentWindow.opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                            //     $contentWindow.$.table.refresh();
                            // } else if ($contentWindow.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                            //     $contentWindow.$.treeTable.refresh();
                            // }
                        }else{
                            parent.opt.success($.i18n.prop("操作成功!"));
                            // if (parent.opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                            //     parent.$.table.refresh();
                            // } else if (parent.opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                            //     parent.$.treeTable.refresh();
                            // }
                        }
                        opt.modal.closeTab();
                    } else if (result.code == opt.variable.web_status.WARNING) {
                        opt.modal.warning(result.msg);
                    } else {
                        opt.modal.error(result.msg);
                    }
                }else {
                    opt.modal.closeTab();
                }
            },
            successTab:function () {
                if(top.location!=self.location){
                    var topWindow = $(window.parent.document);
                    var currentId = $('.layui-tab-title', topWindow).find('.layui-this').children('em').attr('panel-id');
                    if(opt.common.isNotEmpty(currentId)){
                        var $contentWindow = $('iframe[data-id="' + currentId + '"]', topWindow)[0].contentWindow;
                        $contentWindow.opt.modal.msg($.i18n.prop("操作成功!"));
                        if ($contentWindow.opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                            $contentWindow.$.table.refresh();
                        } else if ($contentWindow.opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                            $contentWindow.$.treeTable.refresh();
                        }
                    }
                    opt.modal.closeTab();
                }else{
                    opt.modal.closeTab();
                }

            }
        },
        // 校验封装处理
        validate: {
            // 判断返回标识是否唯一 true 存在 其他返回则显示错误提示
            unique: function (o) {
                var obj = $.parseJSON(o);
                if (obj.code === opt.variable.web_status.SUCCESS) {
                    return true;
                }
                return "<i class='fa fa-times-circle'></i>  " + obj.msg;
            },
            // 表单验证
            /**
             * 表单验证
             * @param formId 参数不传则获取页面第一个from表单值
             * @returns {jQuery}
             */
            form: function (formId) {
                // var currentId = opt.common.isEmpty(formId) ? $('form').attr('id') : formId;
                var currentId;
                if(opt.common.isEmpty(formId)){
                    if(opt.common.isEmpty(opt.table.options.formId)){
                        currentId = $('form').attr('id');
                    }else{
                        currentId = opt.table.options.formId;
                    }
                }else{
                    currentId = formId;
                }
                return $("#" + currentId).validate().form();
            },
            // 重置表单验证（清除提示信息）
            reset: function (formId) {
                // var currentId = opt.common.isEmpty(formId) ? $('form').attr('id') : formId;
                var currentId;
                if(opt.common.isEmpty(formId)){
                    if(opt.common.isEmpty(opt.table.options.formId)){
                        currentId = $('form').attr('id');
                    }else{
                        currentId = opt.table.options.formId;
                    }
                }else{
                    currentId = formId;
                }
                return $("#" + currentId).validate().resetForm();
            },
            fromData: function (formId) {
                // var currentId = opt.common.isEmpty(formId) ? $('form').attr('id') : formId;
                var currentId;
                if(opt.common.isEmpty(formId)){
                    if(opt.common.isEmpty(opt.table.options.formId)){
                        currentId = $('form').attr('id');
                    }else{
                        currentId = opt.table.options.formId;
                    }
                }else{
                    currentId = formId;
                }
                return $("#" + currentId).serialize();
            }
        },
        // 表单封装处理
        form: {
            // 表单重置
            reset: function(tableId,formId,notName) {

                var notNames = opt.common.isEmpty(notName)?[]:notName.split(",");
                opt.table.set(tableId);
                // var currentId = opt.common.isEmpty(formId) ? $('form').attr('id') : formId;
                var currentId;
                if(opt.common.isEmpty(formId)){
                    if(opt.common.isEmpty(opt.table.options.formId)){
                        currentId = $('form').attr('id');
                    }else{
                        currentId = opt.table.options.formId;
                    }
                }else{
                    currentId = formId;
                }
                //$("#" + currentId)[0].reset();
                var inpt = $("#" + currentId).find('input, select');
                $.each(inpt, function() {
                    if(this.tagName == "SELECT"){
                        if(opt.common.isEmpty(notName)){
                            $(this).val(null).trigger("change");
                        }else{
                            var temp = false;
                            for(var i=0; i<notNames.length;i++){
                                if($(this).attr("name") == notNames[i]){
                                    temp = true;
                                }
                            }
                            if(!temp){
                                $(this).val(null).trigger("change");
                            }
                        }
                    }
                    else if(this.tagName == "INPUT"){
                        //表单域输入框
                        if(!(this.type == "hidden" && opt.common.isNotEmpty($(this).data("refresh"))
                            && !$(this).data("refresh"))){
                            this.value = "";
                        }
                    }
                });
                //重置表单select2
                $("#" + currentId +" select").each(function(i) {
                    if(!opt.common.isEmpty($(this).attr("data-select2-id"))){
                        if(opt.common.isEmpty(notName)){
                            $(this).val(null).trigger("change");
                        }else{
                            var temp = false;
                            for(var i=0; i<notNames.length;i++){
                                if($(this).attr("name") == notNames[i]){
                                    temp = true;
                                }
                            }
                            if(!temp){
                                $(this).val(null).trigger("change");
                            }
                        }
                    }
                });
                if (opt.table.options.type == opt.variable.table_type.bootstrapTable) {
                    //重置清空页面记住我数据
                    if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                        opt.table.rememberSelecteds = {};
                        opt.table.rememberSelectedIds = {};
                    }
                    if(opt.common.isEmpty(tableId)){
                        $("#" + opt.table.options.id).bootstrapTable('refresh');
                    } else{
                        $("#" + tableId).bootstrapTable('refresh');
                    }
                } else if (opt.table.options.type == opt.variable.table_type.bootstrapTreeTable) {
                    if(opt.common.isEmpty(tableId)){
                        $("#" + opt.table.options.id).bootstrapTreeTable('refresh', {'__refre':true});
                    } else{
                        $("#" + tableId).bootstrapTreeTable('refresh', {'__refre':true});
                    }
                }
            },
            submit: function(){
                if(typeof submitHandler == "function"){
                    opt.modal.disable();
                    submitHandler();
                    opt.modal.enable();
                }else{
                    opt.modal.error($.i18n.prop('页面必须存在[submitHandler]方法'));
                }
            },
            // 获取选中复选框项
            selectCheckeds: function(name) {
                var checkeds = "";
                $('input:checkbox[name="' + name + '"]:checked').each(function(i) {
                    if (0 == i) {
                        checkeds = $(this).val();
                    } else {
                        checkeds += ("," + $(this).val());
                    }
                });
                return checkeds;
            },
            // 获取选中下拉框项
            selectSelects: function(name) {
                var selects = "";
                $('#' + name + ' option:selected').each(function (i) {
                    if (0 == i) {
                        selects = $(this).val();
                    } else {
                        selects += ("," + $(this).val());
                    }
                });
                return selects.split(",");
            },
            getInputValue:function(name){
                return $('input[name="'+name+'"]').val();
            },
            dataUp:function (options) {
                var defaults = {
                    id :"form-user-saveUpdate",
                    del:[],
                    conversion:[]
                }
                var options = $.extend(defaults, options);
                var data =$("#" + options.id).serializeArray();
                for(var i=0; i<data.length; i++){
                    if(opt.common.isEmpty(options.del)){
                        break;
                    }
                    for (var k=0; k<options.del.length; k++) {
                        if(data[i].name == options.del[k]){
                            data.splice(i,1);
                            if(i==0){
                                i=0;
                            }else{
                                i--;
                            }
                        }
                    }
                }
                for(var i=0; i<data.length; i++){
                    if(opt.common.isEmpty(options.conversion)){
                        break;
                    }
                    for(var k=0; k<options.conversion.length; k++){
                        if(data[i].name == options.conversion[k].name){
                            if(options.conversion[k].value == 'arr'){
                                data[i].value = data[i].value.split(",");
                            }
                            if(options.conversion[k].value == 'num'){
                                data[i].value = Number(data[i].value);
                            }
                        }
                    }
                }
                return data;
            }
        },
    };

    /**
     * 判断给定的对象是否是字符串
     * @method opt.common.isString
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是字符串
     */

    /**
     * 判断给定的对象是否是数组
     * @method opt.common.isArray
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是数组
     */

    /**
     * 判断给定的对象是否是一个Function
     * @method opt.common.isFunction
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是Function
     */

    /**
     * 判断给定的对象是否是Number
     * @method opt.common.isNumber
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是Number
     */

    /**
     * 判断给定的对象是否是一个正则表达式
     * @method opt.common.isRegExp
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是正则表达式
     */

    /**
     * 判断给定的对象是否是一个普通对象
     * @method opt.common.isObject
     * @param { * } object 需要判断的对象
     * @return { Boolean } 给定的对象是否是普通对象
     */
    opt.common.each(['String', 'Function', 'Array', 'Number', 'RegExp', 'Object', 'Date'], function (v) {
        opt.common['is' + v] = function (obj) {
            return Object.prototype.toString.apply(obj) == '[object ' + v + ']';
        }
    });

    $(function () {

        // 全局添加水印
        if(!(self.frameElement && self.frameElement.tagName == "IFRAME")
            && opt.variable.isWatermark) {
            opt.common.watermark({"watermark_txt":"J2eeFAST 开源版本 v2.5.0"});
        }

        //全局设置tooltip
        $('[data-toggle="tooltip"]').each(function () {
            $(this).tooltip();
        });

        //分析所有新标签 添加跳动样式使其跳动
        setTimeout(function(){
            $('[data-index="_new"]').each(function () {
                $(this).addClass('jump5');
            });
        },2000);

        // select2复选框事件绑定
        if ($.fn.select2 !== undefined) {
            $.fn.select2.defaults.set( "theme", "bootstrap" );
            $("select.form-control:not(.noselect2)").each(function () {
                //     .on("change", function () {
                //     $(this).valid();
                // })
                if(typeof($(this).attr("multiple"))=="undefined"){
                    $(this).select2({allowClear: false, placeholder: "请点击选择"});
                }else{
                    $(this).select2({allowClear: true, placeholder: ""});
                }
            })
        }

        // iCheck单选框及复选框事件绑定
        if ($.fn.iCheck !== undefined) {
            $(".check-box:not(.noicheck),.radio-box:not(.noicheck)").each(function() {
                $(this).iCheck({
                    checkboxClass: (typeof($(this).data("style")) == "undefined")?'icheckbox-blue':("icheckbox_" +($(this).data("style") || "square-blue")),
                    radioClass:(typeof($(this).data("style")) == "undefined")? 'iradio-blue':("iradio_" +($(this).data("style") || "square-blue"))
                })
            })
        }

        $('[data-toggle="popover"]').each(function () {
            $(this).popover();
        });


        // laydate 时间控件绑定
        if ($(".select-time").length > 0) {
            layui.use('laydate', function() {
                var laydate = layui.laydate;
                var startDate = laydate.render({
                    elem: '#startTime',
                    max: $('#endTime').val(),
                    theme: 'default',
                    type: $('#startTime').attr("data-type") || 'date',
                    trigger: 'click',
                    done: function(value, date) {
                        // 结束时间大于开始时间
                        if (value !== '') {
                            endDate.config.min.year = date.year;
                            endDate.config.min.month = date.month - 1;
                            endDate.config.min.date = date.date;
                        } else {
                            endDate.config.min.year = '';
                            endDate.config.min.month = '';
                            endDate.config.min.date = '';
                        }
                        $('#endTime').trigger('click');
                    }
                });
                var endDate = laydate.render({
                    elem: '#endTime',
                    min: $('#startTime').val(),
                    theme: 'default',
                    type: $('#endTime').attr("data-type") || 'date',
                    trigger: 'click',
                    done: function(value, date) {
                        // 开始时间小于结束时间
                        if (value !== '') {
                            startDate.config.max.year = date.year;
                            startDate.config.max.month = date.month - 1;
                            startDate.config.max.date = date.date;
                        } else {
                            startDate.config.max.year = '2099';
                            startDate.config.max.month = '12';
                            startDate.config.max.date = '31';
                        }
                    }
                });
            });
        }
        // laydate time-input 时间控件绑定
        if ($(".time-input").length > 0) {
            layui.use('laydate', function () {
                var com = layui.laydate;
                $(".time-input").each(function (index, item) {
                    var time = $(item);
                    // 控制控件外观
                    var type = time.attr("data-type") || 'date';
                    // 控制回显格式
                    var format = time.attr("data-format") || 'yyyy-MM-dd';

                    // 控制日期控件按钮
                    var buttons = time.attr("data-btn") || 'clear|now|confirm', newBtnArr = [];
                    // 日期控件选择完成后回调处理
                    var callback = time.attr("data-callback") || {};

                    if (buttons) {
                        if (buttons.indexOf("|") > 0) {
                            var btnArr = buttons.split("|"), btnLen = btnArr.length;
                            for (var j = 0; j < btnLen; j++) {
                                if ("clear" === btnArr[j] || "now" === btnArr[j] || "confirm" === btnArr[j]) {
                                    newBtnArr.push(btnArr[j]);
                                }
                            }
                        } else {
                            if ("clear" === buttons || "now" === buttons || "confirm" === buttons) {
                                newBtnArr.push(buttons);
                            }
                        }
                    } else {
                        newBtnArr = ['clear', 'now', 'confirm'];
                    }
                    com.render({
                        elem: item,
                        theme: 'molv',
                        trigger: 'click',
                        type: type,
                        format: format,
                        btns: newBtnArr,
                        done: function (value, data) {
                            if (typeof window[callback] != 'undefined'
                                && window[callback] instanceof Function) {
                                window[callback](value, data);
                            }else{
                                try{
                                    if(opt.common.isString(callback)){
                                        console.log(callback);
                                        eval(callback) || new Function(callback);
                                    }
                                }catch (e) {
                                    console.error(e);
                                }
                            }
                        }
                    });
                });
            });
        }

        // tree表格树 展开/折叠
        var expandFlag;
        $("#expandAllBtn").click(function() {
            var dataExpand = opt.common.isEmpty(opt.table.options.expandAll) ? true : opt.table.options.expandAll;
            expandFlag = opt.common.isEmpty(expandFlag) ? dataExpand : expandFlag;
            if (!expandFlag) {
                $.bttTable.bootstrapTreeTable('expandAll');
            } else {
                $.bttTable.bootstrapTreeTable('collapseAll');
            }
            expandFlag = expandFlag ? false: true;
        });

        // tree 关键字搜索绑定
        if ($("#keyword").length > 0) {
            $("#keyword").bind("focus", function focusKey(e) {
                if ($("#keyword").hasClass("empty")) {
                    $("#keyword").removeClass("empty");
                }
            }).bind("blur", function blurKey(e) {
                if ($("#keyword").val() === "") {
                    $("#keyword").addClass("empty");
                }
                $.tree.searchNode(e);
            }).bind("input propertychange", $.tree.searchNode);
        };

        // 按下ESC按钮关闭弹层
        $('body', document).on('keyup',this, function(e) {
            if (e.which === 27) {
                opt.modal.closeAll();
            }
        });

        /* 回到顶部绑定 */
        if ($.fn.toTop !== undefined) {
            //判断是否
            if($.fn.layout !== undefined && $('.ui-layout-center')){
                var _opt = {
                    win:$('.ui-layout-center'),
                    doc:$('.ui-layout-center')
                };
                $('#scroll-up').toTop(_opt);
            }else{
                $('#scroll-up').toTop();
            }
        }
        /* 页面菜单更多事件*/
        $('a[data-toggle="formMore"]').each(function(){
            $(this).off("click").on("click", function () {
                var html = [];
                var formMore = $(this).parents('form').find('.select-list-more');
                var show = formMore.css('display');
                if(show == 'block'){
                    formMore.toggle('normal');
                    html.push('<i class="fa fa-angle-double-down"></i>&nbsp;',$.i18n.prop("更多"));
                }else{
                    formMore.fadeIn(500).css('display','block');
                    html.push('<i class="fa fa-angle-double-up"></i>&nbsp;',$.i18n.prop("隐藏"));
                }
                $(this).empty().append(html.join(''));
            })
        });
    });

    window.opt = opt;

})(window.jQuery, window);
// 回到顶部
(function( $ ){
    'use strict';
    //TOP方法
    $.fn.toTop = function(opt){
        //variables
        var elem = this;
        //修复插件冲突导致失效问题
        var win = (opt && opt.hasOwnProperty('win')) ? opt.win : $(window);
        var doc = (opt && opt.hasOwnProperty('doc')) ? opt.doc : $('html, body');
        //Extended Options
        var options = $.extend({
            autohide: true,
            offset: 50,
            speed: 500,
            position: true,
            right: 15,
            bottom: 5
        }, opt);

        elem.css({
            'cursor': 'pointer'
        });

        if(options.autohide){
            elem.css('display', 'none');
        }

        if(options.position){
            elem.css({
                'position': 'fixed',
                'right': options.right,
                'bottom': options.bottom,
            });
        }
        elem.click(function(){
            doc.animate({scrollTop: 0}, options.speed);
        });

        win.scroll(function(){
            var scrolling = win.scrollTop();
            if(options.autohide){
                if(scrolling > options.offset){
                    elem.fadeIn(options.speed);
                }
                else elem.fadeOut(options.speed);
            }
        });

    };

}( jQuery ));

// 表格封装处理
(function ($) {
    $.extend({
        table: {
            // 初始化表格参数
            init: function(options) {
                var defaults = {
                    formId:"",
                    id: "bootstrap-table",
                    type: 0, // 0 代表bootstrapTable 1代表bootstrapTreeTable
                    height: undefined,
                    sidePagination: "server",
                    sortName: "",
                    sortOrder: "desc",
                    pagination: true,
                    pageSize: 50,
                    pageList: [50, 100, 150],
                    toolbar: "toolbar",
                    striped: false,
                    escape: false,
                    outcheckbox:true, //是否开启检测toolbar有删除按钮 就默认使表格 checkbox:true 支持选中
                    firstLoad: true,
                    showFooter: false,
                    undefinedText:'-',
                    emptyText:'-',
                    search: false,
                    showSearch: true,
                    showPageGo: true,
                    showRefresh: true,
                    showColumns: true,
                    resizable: false,
                    showToggle: true,
                    showExport: false,
                    clickToSelect: true,
                    singleSelect: false,
                    paginationLoop: true,
                    /* 表格选中回调事件*/
                    onSelectData:undefined,
                    showPage: true,
                    mobileResponsive: true,
                    rememberSelected: true, /**默认记住我*/
                    _total:false, //是否需要合计表格
                    showPaginationSwitch: false,
                    fixedColumns: false,
                    fixedNumber: 0,
                    rightFixedColumns: false,
                    rightFixedNumber: 0,
                    queryParams: $.table.queryParams,
                    totalData:undefined, //服务返回合计对象
                    _totalColumns:[],
                    rowStyle: {},
                    showBorder: false,
                    isFixedColumn: false
                };

                if(options.resizable){
                    try{
                        if(0 !== _ColResizable){
                            opt.error("模板引擎缺少引入表格拖动Libs['resizable']")
                            return;
                        }
                    }catch (e) {
                        opt.error("模板引擎缺少引入表格拖动Libs['resizable']")
                        return;
                    }
                }
                var options = $.extend(defaults, options);
                //兼容自动识别有删除按钮表格有checkbox 选项
                if(options.outcheckbox && (!opt.common.isEmpty($('#' + options.toolbar+'-'+options.id + ' .multiple').html())
                    || !opt.common.isEmpty($('#' + options.toolbar +'-'+options.id+ ' .single').html()))){
                    var _flag = false;
                    if(!opt.common.isEmpty(options.columns.length)){
                        for(var i=0; i<options.columns.length; i++ ){
                            if(options.columns[i] instanceof Array){
                                for(var j=0; j<options.columns[i].length; j++) {
                                    if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i][j],"checkbox"))){
                                        _flag = true;
                                        break;
                                    }
                                }
                                if(_flag){
                                    break;
                                }
                            }else{
                                if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"checkbox"))){
                                    _flag = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!_flag){
                        //无表头信息
                        if(options.columns[0] instanceof Array){
                            options.columns[options.columns.length -1].splice(0,0,{checkbox: true, field: '_state'});
                        }else{
                            options.columns.splice(0,0,{checkbox: true, field: '_state'});
                        }
                    }
                }

                //
                if(!opt.common.isEmpty(options.columns.length)){
                    for(var i=0; i<options.columns.length; i++ ){
                        if(options.columns[0] instanceof Array){
                            for(var j=0; j<options.columns[i].length; j++) {
                                if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i][j],"align"))){
                                    options.columns[i][j].align = 'center';
                                }
                                if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i][j],"halign"))){
                                    options.columns[i][j].halign = 'center';
                                }
                                // 表格首列有checkbox 勾选字段名称必须state - 记住我必须是字段state
                                if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i][j],"checkbox"))){
                                    options.columns[i][j].field = '_state';
                                }
                                // 表格首列有radio 勾选字段名称必须state - 记住我必须是字段state
                                if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"radio"))){
                                    options.columns[i].field = '_state';
                                }
                            }
                        }else {
                            if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"align"))){
                                options.columns[i].align = 'center';
                            }
                            if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"halign"))){
                                options.columns[i].halign = 'center';
                            }
                            // 表格首列有checkbox 勾选字段名称必须state - 记住我必须是字段state
                            if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"checkbox"))){
                                options.columns[i].field = '_state';
                            }
                            // 表格首列有radio 勾选字段名称必须state - 记住我必须是字段state
                            if(!opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"radio"))){
                                options.columns[i].field = '_state';
                            }
                        }
                    }
                }
                opt.table.options = options;
                opt.table.config[options.id] = options;
                $.table.initEvent();
                $('#' + options.id).bootstrapTable({
                    id: options.id,                                     // 对象ID
                    url: options.url,                                   // 请求后台的URL（*）
                    contentType: "application/x-www-form-urlencoded",   // 发送到服务器的数据编码类型
                    method: 'post',                                     // 请求方式（*）
                    cache: false,                                       // 是否使用缓存
                    height: options.height,                             // 表格的高度
                    striped: options.striped,                           // 是否显示行间隔色
                    sortable: true,                                     // 是否启用排序
                    sortStable: true,                                   // 设置为 true 将获得稳定的排序
                    sortName: options.sortName,                         // 排序列名称
                    sortOrder: options.sortOrder,                       // 排序方式  asc 或者 desc
                    pagination: options.pagination,                     // 设置为 true 会在表格底部显示分页条
                    paginationLoop: options.paginationLoop,             // 设置为 true 启用分页条无限循环的功能。
                    undefinedText:options.undefinedText,                // 当数据为 undefined 时显示的字符
                    emptyText:options.emptyText,                        // 当数据为 "" 空 时显示的字符  [修改源码 扩展字段]
                    showPaginationSwitch:options.showPaginationSwitch,  // 是否显示 数据条数选择框
                    pageNumber: 1,                                      // 初始化加载第一页，默认第一页
                    pageSize: options.pageSize,                         // 每页的记录行数（*）
                    pageList: options.pageList,                         // 可供选择的每页的行数（*）
                    firstLoad: options.firstLoad,                       // 是否首次请求加载数据，对于数据较大可以配置false
                    isFixedColumn: options.isFixedColumn,               // 是否固定列宽，当列比较多时，开启水平滚动，可设置为 true
                    escape: options.escape,                             // 转义HTML字符串
                    showFooter: options.showFooter,                     // 是否统计显示表底部尾行 配合 footerFormatter 方法使用
                    footerStyle: options.footerStyle,                   // 表底部尾行统计样式
                    iconSize: 'outline',                                // 图标大小：undefined默认的按钮尺寸 xs超小按钮sm小按钮lg大按钮
                    toolbar: '#' + options.toolbar + '-'+options.id,    // 指定工作栏
                    sidePagination: options.sidePagination,             // server启用服务端分页client客户端分页
                    search: options.search,                             // 是否显示搜索框功能
                    showBorder: options.showBorder,                     // 是否显示表格边框
                    searchText: options.searchText,                     // 搜索框初始显示的内容，默认为空
                    showSearch: options.showSearch,                     // 是否显示检索信息
                    showPageGo: options.showPageGo,               		// 是否显示跳转页
                    showPage: options.showPage,               		    // 是否显示左下角页码
                    showRefresh: options.showRefresh,                   // 是否显示刷新按钮
                    showColumns: options.showColumns,                   // 是否显示隐藏某列下拉框
                    showToggle: options.showToggle,                     // 是否显示详细视图和列表视图的切换按钮
                    showExport: options.showExport,                     // 是否支持导出文件
                    uniqueId: options.uniqueId,                         // 唯 一的标识符
                    clickToSelect: options.clickToSelect,				// 是否启用点击选中行
                    singleSelect: options.singleSelect,                 // 是否单选checkbox
                    mobileResponsive: options.mobileResponsive,         // 是否支持移动端适配
                    detailView: options.detailView,                     // 是否启用显示细节视图
                    onCheck: options.onCheck,                           // 点击选择（check）此行时触发
                    onUncheck: options.onUncheck,                       // 当取消选择（uncheck）此行时触发
                    onCheckAll: options.onCheckAll,                     // 当全选行时触发
                    onUncheckAll: options.onUncheckAll,                 // 当取消全选行时触发
                    onClickRow: options.onClickRow,                     // 点击某行触发的事件
                    onDblClickRow: options.onDblClickRow,               // 双击某行触发的事件
                    onClickCell: options.onClickCell,                   // 单击某格触发的事件
                    onDblClickCell: options.onDblClickCell,             // 双击某格触发的事件
                    onEditableSave: options.onEditableSave,             // 行内编辑保存的事件
                    onExpandRow: options.onExpandRow,                   // 点击详细视图的事件
                    rememberSelected: options.rememberSelected,         // 启用翻页记住前面的选择
                    fixedColumns: options.fixedColumns,                 // 是否启用冻结列（左侧）
                    fixedNumber: options.fixedNumber,                   // 列冻结的个数（左侧）
                    resizable: options.resizable,                       // 是否允许拉伸列宽及增加表格线
                    rightFixedColumns: options.rightFixedColumns,       // 是否启用冻结列（右侧）
                    rightFixedNumber: options.rightFixedNumber,         // 列冻结的个数（右侧）
                    onReorderRow: options.onReorderRow,                 // 当拖拽结束后处理函数
                    queryParams: options.queryParams,                   // 传递参数（*）
                    rowStyle: options.rowStyle,                         // 通过自定义函数设置行样式
                    columns: options.columns,                           // 显示列信息（*）
                    responseHandler: $.table.responseHandler,           // 在加载服务器发送来的数据之前处理函数
                    onLoadSuccess: $.table.onLoadSuccess,               // 当所有数据被加载时触发处理函数
                    exportOptions: options.exportOptions,               // 前端导出忽略列索引
                    detailFormatter: options.detailFormatter,           // 在行下面展示其他数据列表
                });
            },
            // 获取实例ID，如存在多个返回#id1,#id2 delimeter分隔符
            getOptionsIds: function(separator) {
                var _separator = opt.common.isEmpty(separator) ? "," : separator;
                var optionsIds = "";
                $.each(opt.table.config, function(key, value){
                    optionsIds += "#" + key + _separator;
                });
                return optionsIds.substring(0, optionsIds.length - 1);
            },
            // 查询条件
            queryParams: function(params) {
                /*
                pageSize: 10
                pageNum: 1
                orderByColumn: createTime
                isAsc: desc
                dictName:
                dictType:
                status:
                params[beginTime]:
                params[endTime]:

                _search: false
                nd: 1576836627600
                limit: 50
                page: 1
                sidx: roleId
                order: asc
                * */
                var curParams = {
                    // 传递参数查询参数
                    __limit:       params.limit,
                    __page:        params.offset / params.limit + 1,
                    searchValue:    params.search,
                    __sidx:           params.sort,
                    __order:          params.order
                };
                opt.table.set(this.id);
                var currentId = opt.common.isEmpty(opt.table.options.formId) ? $('form').attr('id') : opt.table.options.formId;
                return $.extend(curParams, opt.common.formToJSON(currentId));
            },
            //表格合计汇总
            tableTotalHtml:function(id){
                opt.table.set();
                var _v = "";
                if(!opt.common.isEmpty(opt.table.options.totalData)){
                    return "<span>"+opt.common.getJsonValue(opt.table.options.totalData,id)+"</span>";
                }else{
                    return "";
                }
            },
            // 请求获取数据后处理回调函数
            responseHandler: function(res) {
                if (typeof opt.table.get(this.id).responseHandler == "function") {
                    opt.table.get(this.id).responseHandler(res);
                }
                if (res.code == 0) {
                    if (opt.common.isNotEmpty(opt.table.options.sidePagination) && opt.table.options.sidePagination == 'client') {
                        return res.rows;
                    } else {
                        if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                            var column = opt.common.isEmpty(opt.table.options.uniqueId) ? opt.table.options.columns[1].field : opt.table.options.uniqueId;
                            $.each(res.data.list, function(i, row) {
                                if(opt.table.rememberSelectedIds[opt.table.options.id]){
                                    var _flag = false;
                                    for(var i=0; i<opt.table.rememberSelectedIds[opt.table.options.id].length; i++){
                                        if(opt.common.getJsonValue(row,column) == opt.table.rememberSelectedIds[opt.table.options.id][i]){
                                            _flag = true;
                                        }
                                    }
                                    if(_flag){
                                        //同步记住我的数据
                                        if(opt.common.isEmpty(opt.table.rememberSelecteds[opt.table.options.id])){
                                            opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], row,column);
                                        }else{
                                            var _f = true;
                                            for(var k=0; k< opt.table.rememberSelecteds[opt.table.options.id].length; k++){
                                                if(opt.common.getJsonValue(row,column) == opt.common.getJsonValue(opt.table.rememberSelecteds[opt.table.options.id][k],column)){
                                                    _f = false;
                                                    break;
                                                }
                                            }
                                            if(_f){
                                                var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                                                opt.table.rememberSelecteds[opt.table.options.id] = _['union'](selectedRows, row,column);
                                            }
                                        }
                                    }
                                    row._state = _flag;
                                }else{
                                    row._state = false;
                                }
                            })
                        }
                        if(opt.common.isNotEmpty(opt.table.options._total) && opt.table.options._total){
                            opt.table.options.totalData = res.pageTotal; //服务返回合计对象
                        }
                        return { rows: res.data.list, total: res.data.totalCount };
                    }
                } else {
                    opt.modal.error(res.msg,function () {
                        try {
                            opt.closeItem();
                        }catch (e) {
                        }
                    });
                    return { rows: [], total: 0 };
                }
            },
            // 初始化事件
            initEvent: function() {
                // 实例ID信息
                var optionsIds = $.table.getOptionsIds();
                // 监听事件处理
                $(optionsIds).on(TABLE_EVENTS, function () {
                    opt.table.set(this.id);
                });

                // 选中、取消、全部选中、全部取消（事件）
                $(optionsIds).on("check.bs.table check-all.bs.table uncheck.bs.table uncheck-all.bs.table", function (e, rows) {
                    // 复选框分页保留保存选中数组
                    var rowIds = $.table.affectedRowIds(rows);
                    if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                        //获取到当前页面数据
                        var _trows = $.map($("#" + opt.table.options.id).bootstrapTable('getAllSelections'), function (row) {
                            return row;
                        });
                        var selectedIds = opt.table.rememberSelectedIds[opt.table.options.id];
                        var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                        if(opt.common.isEmpty(selectedIds) && opt.common.isEmpty(selectedRows) && _trows.length >= 1){
                            opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], _trows,opt.table.options.uniqueId);
                            opt.table.rememberSelectedIds[opt.table.options.id] = _['union']([],  $.table.affectedRowIds(_trows),opt.table.options.uniqueId);
                        }
                        if(!opt.table.options.singleSelect){
                            var column = opt.common.isEmpty(opt.table.options.uniqueId) ? opt.table.options.columns[1].field : opt.table.options.uniqueId,
                                //判断勾选使用合并函数-反选使用剔除元素
                                func = $.inArray(e.type, ['check', 'check-all']) > -1 ? 'union' : 'difference';
                            selectedIds = opt.table.rememberSelectedIds[opt.table.options.id];
                            if(opt.common.isNotEmpty(selectedIds)) {
                                opt.table.rememberSelectedIds[opt.table.options.id] = _[func](selectedIds, rowIds,column);
                            } else {
                                opt.table.rememberSelectedIds[opt.table.options.id] = _[func]([], rowIds,column);
                            }
                            selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                            if(opt.common.isNotEmpty(selectedRows)) {
                                opt.table.rememberSelecteds[opt.table.options.id] = _[func](selectedRows, rows,column);
                            } else {
                                opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], _trows,column);
                            }
                        }else{
                            opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], _trows,opt.table.options.uniqueId);
                            opt.table.rememberSelectedIds[opt.table.options.id] = _['union']([],  $.table.affectedRowIds(_trows),opt.table.options.uniqueId);
                        }
                    }

                    if (typeof opt.table.get(opt.table.options.id).onSelectData == "function") {
                        if(opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected){
                            opt.table.get(opt.table.options.id).onSelectData(opt.table.rememberSelecteds[opt.table.options.id]);
                        }else{
                            opt.table.get(opt.table.options.id).onSelectData(rows);
                        }
                    }
                });
                // 加载成功、选中、取消、全部选中、全部取消（事件）
                $(optionsIds).on("check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table load-success.bs.table", function () {
                    var toolbar = opt.table.options.toolbar + '-'+ opt.table.options.id ;
                    var uniqueId = opt.table.options.uniqueId;
                    // 工具栏按钮控制
                    var rows = opt.common.isEmpty(uniqueId) ? $.table.selectFirstColumns() : $.table.selectColumns(uniqueId);
                    // 非多个禁用
                    $('#' + toolbar + ' .multiple').toggleClass('disabled', !rows.length);
                    // 非单个禁用
                    $('#' + toolbar + ' .single').toggleClass('disabled', rows.length!=1);
                });
                // 图片预览事件
                $(optionsIds).off("click").on("click", '.img-circle', function() {
                    var src = $(this).attr('src');
                    var target = $(this).data('target');
                    var height = $(this).data('height') == 'auto'?'':$(this).data('height');
                    var name = $(this).data('name');
                    var width = $(this).data('width') == 'auto'?'':$(this).data('width');
                    if(opt.common.equals("self", target)) {
                        opt.modal.openView(name,src,width,height);
                    } else if (opt.common.equals("blank", target)) {
                        window.open(src);
                    }
                });
                // 单击tooltip事件
                $(optionsIds).on("click", '.tooltip-show', function() {
                    var target = $(this).data('target');
                    var input = $(this).prev();
                    if (opt.common.equals("open", target)) {
                        opt.selfLayer.alert(input.val(), {
                            title: "信息内容",
                            shadeClose: true,
                            btn: ['确认'],
                            btnclass: ['btn btn-primary'],
                        });
                    }
                });
            },
            // 当所有数据被加载时触发
            onLoadSuccess: function(data) {

                if (typeof opt.table.get(this.id).onLoadSuccess == "function") {
                    opt.table.get(this.id).onLoadSuccess(data);
                }

                // 浮动提示框特效
                $(".table [data-toggle='tooltip']").tooltip();

                //加载合计统计
                if(opt.common.isNotEmpty(opt.table.options._total) && opt.table.options._total){
                    var _p = "汇总:&nbsp;&nbsp;"
                    for(var i=0; i<opt.table.options._totalColumns.length;i++){
                        _p += opt.table.options._totalColumns[i].title;
                        _p += ":"
                        _p += opt.common.isEmpty(opt.common.getJsonValue(opt.table.options.totalData,opt.table.options._totalColumns[i].field))?"0.00":
                            opt.common.getJsonValue(opt.table.options.totalData,opt.table.options._totalColumns[i].field) + "&nbsp;&nbsp;"
                    }
                    $("#_pageTotal").html(_p);
                }

                $('.table [data-toggle="popover"]').popover();

                if ($.fn.iCheck !== undefined) {
                    $(".check-box:not(.noicheck),.radio-box:not(.noicheck)").each(function() {
                        $(this).iCheck({
                            checkboxClass: (typeof($(this).data("style")) == "undefined")?'icheckbox-blue':("icheckbox_" +($(this).data("style") || "square-blue")),
                            radioClass:(typeof($(this).data("style")) == "undefined")? 'iradio-blue':("iradio_" +($(this).data("style") || "square-blue"))
                        })
                    })
                }

                // laydate time-input 时间控件绑定
                if ($(".table .time-input").length > 0) {
                    layui.use('laydate', function () {
                        var com = layui.laydate;
                        $(".time-input").each(function (index, item) {
                            var time = $(item);
                            // 控制控件外观
                            var type = time.attr("data-type") || 'date';
                            // 控制回显格式
                            var format = time.attr("data-format") || 'yyyy-MM-dd';
                            // 控制日期控件按钮
                            var buttons = time.attr("data-btn") || 'clear|now|confirm', newBtnArr = [];
                            // 日期控件选择完成后回调处理
                            var callback = time.attr("data-callback") || {};
                            if (buttons) {
                                if (buttons.indexOf("|") > 0) {
                                    var btnArr = buttons.split("|"), btnLen = btnArr.length;
                                    for (var j = 0; j < btnLen; j++) {
                                        if ("clear" === btnArr[j] || "now" === btnArr[j] || "confirm" === btnArr[j]) {
                                            newBtnArr.push(btnArr[j]);
                                        }
                                    }
                                } else {
                                    if ("clear" === buttons || "now" === buttons || "confirm" === buttons) {
                                        newBtnArr.push(buttons);
                                    }
                                }
                            } else {
                                newBtnArr = ['clear', 'now', 'confirm'];
                            }
                            com.render({
                                elem: item,
                                theme: 'molv',
                                trigger: 'click',
                                type: type,
                                format: format,
                                btns: newBtnArr,
                                done: function (value, data) {
                                    if (typeof window[callback] != 'undefined'
                                        && window[callback] instanceof Function) {
                                        window[callback](value, data);
                                    }
                                }
                            });
                        });
                    });
                }

                //select2复选框事件绑定
                if ($.fn.select2 !== undefined) {
                    $.fn.select2.defaults.set( "theme", "bootstrap" );
                    $("select.form-control:not(.noselect2)").each(function () {
                        if(typeof($(this).attr("multiple"))=="undefined"){
                            $(this).select2();
                        }else{
                            $(this).select2({allowClear: true, placeholder: ""});
                        }
                    })
                }

                //设置溢出文本复制
                $.contextMenu({
                    selector: ".table a[data-target='copy']",
                    trigger: 'right',
                    autoHide: true,
                    items: {
                        "copy": {
                            name: $.i18n.prop("复制文本"),
                            icon: "fa-copy",
                            callback: function(key, opt) {
                                var input = $(this).prev();
                                input.select();
                                if(document.execCommand("copy")){
                                    window.opt.modal.msg($.i18n.prop('复制成功')+'!');
                                }
                            }
                        }
                    }
                });

                // $(window).on('resize', function () {
                //     // 浮动提示框特效
                //     //$(".table [data-toggle='tooltip']").tooltip();
                //
                //     $('[data-toggle="popover"]').popover();
                //
                //     if ($.fn.iCheck !== undefined) {
                //         $(".check-box:not(.noicheck),.radio-box:not(.noicheck)").each(function() {
                //             $(this).iCheck({
                //                 checkboxClass: (typeof($(this).data("style")) == "undefined")?'icheckbox-blue':("icheckbox_" +($(this).data("style") || "square-blue")),
                //                 radioClass:(typeof($(this).data("style")) == "undefined")? 'iradio-blue':("iradio_" +($(this).data("style") || "square-blue"))
                //             })
                //         })
                //     }
                //
                //     //select2复选框事件绑定
                //     if ($.fn.select2 !== undefined) {
                //         $.fn.select2.defaults.set( "theme", "bootstrap" );
                //         $("select.form-control:not(.noselect2)").each(function () {
                //             if(typeof($(this).attr("multiple"))=="undefined"){
                //                 $(this).select2();
                //             }else{
                //                 $(this).select2({allowClear: true, placeholder: ""});
                //             }
                //         })
                //     }
                //
                // }).resize();

            },
            // 表格销毁
            destroy: function (tableId) {
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                $("#" + currentId).bootstrapTable('destroy');
                delete opt.table.rememberSelectedIds[tableId];
                delete opt.table.rememberSelecteds[tableId];
            },
            // 序列号生成
            serialNumber: function (index, tableId) {
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                var tableParams = $("#" + currentId).bootstrapTable('getOptions');
                var pageSize = tableParams.pageSize;
                var pageNumber = tableParams.pageNumber;
                return pageSize * (pageNumber - 1) + index + 1;
            },
            /**
             *  列超出指定长度浮动提示
             * @param value 需要控制的文本
             * @param length 超出多长显示
             * @param align 截取value文本显示缩略
             * @param target copy 双击弹出复制文本 open弹窗打开文本
             * @returns 返回处理过后DIV
             */
            tooltip: function (value, length,align, target) {
                var _length = opt.common.isEmpty(length) ? 20 : length;
                var _align = opt.common.isEmpty(align) ? false : align;
                var _text = "";
                var _value = opt.common.nullToStr(value);
                var _target = opt.common.isEmpty(target) ? 'copy' : target;
                if (_value.length > _length) {
                    if(_align){
                        _text = "..." + _value.substr(_value.length-_length, _length);
                    }else{
                        _text = _value.substr(0, _length) + "...";
                    }
                    _value = _value.replace(/\'/g,"&apos;");
                    _value = _value.replace(/\"/g,"&quot;");
                    var actions = [];
                    actions.push(opt.common.sprintf('<input style="opacity: 0;position: absolute;z-index:-1" type="text" value="%s"/>', _value));
                    actions.push(opt.common.sprintf('<a href="###" class="tooltip-show" data-toggle="tooltip" data-target="%s" title="%s">%s</a>', _target, _value, _text));
                    return actions.join('');
                } else {
                    _text = _value;
                    return _text;
                }
            },
            // 下拉按钮切换
            dropdownToggle: function (value) {
                var actions = [];
                actions.push('<div class="btn-group">');
                actions.push('<button type="button" class="btn btn-xs dropdown-toggle" data-toggle="dropdown" aria-expanded="false">');
                actions.push('<i class="fa fa-cog"></i>&nbsp;<span class="fa fa-chevron-down"></span></button>');
                actions.push('<ul class="dropdown-menu">');
                actions.push(value.replace(/<a/g,"<li><a").replace(/<\/a>/g,"</a></li>"));
                actions.push('</ul>');
                actions.push('</div>');
                return actions.join('');
            },
            // 图片预览
            imageView: function (value, height, width, target) {
                if (opt.common.isEmpty(width)) {
                    width = 'auto';
                }
                if (opt.common.isEmpty(height)) {
                    height = 'auto';
                }
                // blank or self
                var _target = opt.common.isEmpty(target) ? 'self' : target;
                if (opt.common.isNotEmpty(value)) {
                    //value = opt.common.optimizationPath(value);
                    var name = opt.common.fileFromPath(value);
                    ///fast/sys/comm/fileAvatarView?filePath=/avatar/2020/12/25/7325bc151faf46538733237d97bf9270.png
                    return opt.common.sprintf("<img class='img-circle img-xs' data-height='%s' data-width='%s' data-target='%s' src='%s' data-name='%s'/>", height, width, _target, value,name);
                } else {
                    return opt.common.nullToStr(value);
                }
            },
            // 搜索-默认第一个form
            /**
             * 优先搜索传入表单from 如果没有传入则会查找当前表格是否指定表单from 如果有指定则搜索指定from表单,否则搜索第一个表单
             * @param formId 搜索表单ID
             * @param tableId 表格ID
             * @param data 附加数据
             */
            search: function(tableId, formId,data) {
                opt.table.set(tableId);
                // var currentId = opt.common.isEmpty(formId) ? $('form').attr('id') : formId;
                var currentId;
                if(opt.common.isEmpty(formId)){
                    if(opt.common.isEmpty(opt.table.options.formId)){
                        currentId = $('form').attr('id');
                    }else{
                        currentId = opt.table.options.formId;
                    }
                }else{
                    currentId = formId;
                }

                //查询是否有更多按钮
                var butform = $('#'+currentId).find('a[data-toggle="formMore"]');
                var formMore = $('#'+currentId).find('div.select-list-more');
                if(opt.common.isNotEmpty(butform) &&
                    opt.common.isNotEmpty(formMore) &&
                    formMore.css('display')){
                    var show = formMore.css('display');
                    var html = [];
                    if(show == 'block'){
                        formMore.toggle('normal');
                        html.push('<i class="fa fa-angle-double-down"></i>&nbsp;',$.i18n.prop("更多"));
                        butform.empty().append(html.join(''));
                    }
                }

                var params = opt.common.isEmpty(tableId) ? $("#" + opt.table.options.id).bootstrapTable('getOptions') : $("#" + tableId).bootstrapTable('getOptions');
                params.queryParams = function(params) {
                    var search = opt.common.formToJSON(currentId);
                    if(opt.common.isNotEmpty(data)){
                        $.each(data, function(key) {
                            search[key] = data[key];
                        });
                    }
                    search.__limit = params.limit;
                    search.__page = params.offset / params.limit + 1;
                    search.searchValue = params.search;
                    search.__sidx = params.sort;
                    search.__order = params.order;
                    return search;
                }

                if(opt.common.isNotEmpty(tableId)){
                    $("#" + tableId).bootstrapTable('refresh', params);
                } else{
                    $("#" + opt.table.options.id).bootstrapTable('refresh', params);
                }
            },
            /**
             * 导出Excel 表格
             * @param formId 导出表单ID 如果不传则获取默认页面第一个
             */
            exportExcel: function(formId) {
                opt.table.set();
                opt.modal.confirm("确定导出所有" + opt.table.options.modalName + "吗？", function() {
                    var currentId = opt.common.isEmpty(formId) ? $('form').attr('id') : formId;
                    opt.modal.loading("正在导出数据，请稍后...");
                    var config = {
                        type: "POST",
                        url: opt.table.options.exportUrl,
                        data: $("#" + currentId).serializeArray(),
                        success: function(result) {
                            if (result.code ==opt.variable.web_status.SUCCESS) {
                                window.location.href = baseURL + "excel/download?fileName=" + encodeURI(result.msg);
                            } else if (result.code == opt.variable.web_status.WARNING) {
                                opt.modal.alertWarning(result.msg)
                            } else {
                                opt.modal.alertError(result.msg);
                            }
                            opt.modal.closeLoading();
                        }
                    }
                    opt.common.sendAjax(config);
                });
            },
            // 下载模板
            importTemplate: function(fileName) {
                fileName = window.encodeURI(fileName);
                opt.common.downLoadFile(baseURL + "excel/download?fileName=" +fileName);
            },
            // 导入数据
            importExcel: function(formId) {
                opt.table.set();
                var currentId = opt.common.isEmpty(formId) ? 'importTpl' : formId;
                opt.layer.open({
                    type: 1,
                    area: ['580px', '200px'],
                    shadeClose: true,
                    resize: false,
                    scrollbar: true,
                    //shade: 0.3,
                    title: '<i class="fa fa-upload"></i> 导入' + opt.table.options.modalName + '数据',
                    content: opt.template(currentId),
                    btn: ['<i class="fa fa-check"></i> 导入', '<i class="fa fa-remove"></i> 取消'],
                    // 弹层外区域关闭
                    shadeClose: true,
                    btn1: function(index, layero){
                        var file = layero.find('#file').val();
                        if (file == '' || (!opt.common.endWith(file, '.xls') && !opt.common.endWith(file, '.xlsx'))){
                            opt.warning("请选择后缀为 “xls”或“xlsx”的文件。");
                            return false;
                        }
                        parent.opt.modal.loading();
                        var formData = new FormData(layero.find('form')[0]);
                        var config = {
                            url: opt.table.options.importUrl,
                            data: formData,
                            cache: false,
                            contentType: false,
                            processData: false,
                            type: 'POST',
                            success: function (result) {
                                if (result.code == opt.variable.web_status.SUCCESS) {
                                    parent.opt.modal.closeLoading();
                                    opt.error(result.msg);
                                    $.table.refresh();
                                } else if (result.code == opt.variable.web_status.WARNING) {
                                    opt.error(result.msg);
                                    parent.opt.modal.closeLoading();
                                    opt.layer.close(index);
                                } else {
                                    opt.error(result.msg);
                                    parent.opt.modal.closeLoading();
                                    opt.layer.close(index);
                                }
                            },
                            error: function(xhr, textStatus) {
                                opt.error(JSON.parse(xhr.responseText).msg || xhr.responseText );
                                parent.opt.modal.closeLoading();
                                return;
                            }
                        };
                        opt.common.sendAjax(config);
                    }
                });
            },
            // 刷新表格
            refresh: function(tableId) {
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                $("#" + currentId).bootstrapTable('refresh', {
                    silent: true
                });
            },
            //根据行uniqueId值 查询整行数据
            getRowByUniqueId: function(uniqueId,tableId){
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                return $("#" + currentId).bootstrapTable('getRowByUniqueId',uniqueId);
            },
            // 查询表格指定列值
            selectColumns: function(column) {
                var rows = $.map($("#" + opt.table.options.id).bootstrapTable('getSelections'), function (row) {
                    var value = row;
                    var props = column.split('.');
                    for (var p in props) {
                        value = value && value[props[p]];
                    }
                    return value;
                });
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                    if(opt.common.isNotEmpty(selectedRows)) {
                        rows = $.map(opt.table.rememberSelecteds[opt.table.options.id], function (row) {
                            var value = row;
                            var props = column.split('.');
                            for (var p in props) {
                                value = value && value[props[p]];
                            }
                            return value;
                        });
                    }
                }
                return opt.common.uniqueFn(rows);
            },
            // 获取当前页选中或者取消的行ID
            affectedRowIds: function(rows) {
                var field = opt.common.isEmpty(opt.table.options.uniqueId) ? opt.table.options.columns[1].field : opt.table.options.uniqueId;
                var rowIds;
                if (rows instanceof Array) {
                    rowIds = $.map(rows, function(row) {
                        var value = row;
                        var props = field.split('.');
                        for (var p in props) {
                            value = value && value[props[p]];
                        }
                        return value;
                    });
                } else {
                    rowIds = rows;
                    var props = field.split('.');
                    for (var p in props) {
                        rowIds = rowIds && rowIds[props[p]];
                    }
                    rowIds = [rowIds];
                }
                return rowIds;
            },
            // 查询表格首列值 -- 实际就是第二列 去掉了前面的 勾选 与序列
            selectFirstColumns: function(tableId) {
                if(opt.common.isNotEmpty(tableId)){
                    opt.table.set(tableId);
                }
                var rows = $.map($("#" + opt.table.options.id).bootstrapTable('getSelections'), function (row) {
                    return row[opt.table.options.columns[1].field];
                });
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                    if(opt.common.isNotEmpty(selectedRows)) {
                        rows = $.map(selectedRows, function (row) {
                            return row[opt.table.options.columns[1].field];
                        });
                    }
                }
                return opt.common.uniqueFn(rows);
            },
            //获取选中行 id 集合
            selectAllColumns :function(tableId){
                if(opt.common.isNotEmpty(tableId)){
                    opt.table.set(tableId);
                }
                var rows;
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                    if(opt.common.isNotEmpty(selectedRows)) {
                        rows = $.map(selectedRows, function (row) {
                            return opt.common.getJsonValue(row,opt.table.options.uniqueId);
                        });
                    }else{
                        rows = [];
                    }
                }
                //若没有记住我或者记住我内部无值 则获取页面，如果有值则获取记住我,因为记住我里面的值为所有,不单单是当前页还有其他页记住的数据
                if(opt.common.isEmpty(rows) || rows.length == 0){
                    rows = $.map($("#" + opt.table.options.id).bootstrapTable('getAllSelections'), function (row) {
                        return row;
                    });
                    // if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    //     opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], rows,opt.table.options.uniqueId);
                    //     opt.table.rememberSelectedIds[opt.table.options.id] = _['union']([], $.table.affectedRowIds(rows),opt.table.options.uniqueId);
                    // }
                    rows = $.table.affectedRowIds(rows);
                }
                return opt.common.uniqueFn(rows);
            },
            //获取选中行对象集合
            selectAllColumnRows: function(tableId){
                if(opt.common.isNotEmpty(tableId)){
                    opt.table.set(tableId);
                }
                var rows;
                if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    var selectedRows = opt.table.rememberSelecteds[opt.table.options.id];
                    if(opt.common.isNotEmpty(selectedRows)) {
                        return selectedRows;
                    }else{
                        rows = [];
                    }
                }
                //若没有记住我或者记住我内部无值 则获取页面，如果有值则获取记住我,因为记住我里面的值为所有,不单单是当前页还有其他页记住的数据
                if(opt.common.isEmpty(rows) || rows.length == 0){
                    rows = $.map($("#" + opt.table.options.id).bootstrapTable('getAllSelections'), function (row) {
                        return row;
                    });
                    // if (opt.common.isNotEmpty(opt.table.options.rememberSelected) && opt.table.options.rememberSelected) {
                    //     opt.table.rememberSelecteds[opt.table.options.id] = _['union']([], rows,opt.table.options.uniqueId);
                    //     opt.table.rememberSelectedIds[opt.table.options.id] = _['union']([], $.table.affectedRowIds(rows),opt.table.options.uniqueId);
                    // }
                    return rows;
                }
            },
            // 回显数据字典
            selectDictLabel: function(datas, value) {
                var actions = [];
                $.each(datas, function(index, dict) {
                    if (dict.dictValue == ('' + value)) {
                        var listClass = opt.common.equals("default", dict.listClass) || opt.common.isEmpty(dict.listClass) ? "" : "badge badge-" + dict.listClass;
                        if(!opt.common.isEmpty(dict.cssClass)){
                            listClass = opt.common.isEmpty(dict.cssClass) ? "" : dict.cssClass;
                        }
                        if(!opt.common.isEmpty(dict.cssStyle)){
                            listClass = dict.cssStyle;
                        }
                        if(!opt.common.isEmpty(dict.cssStyle)){
                            actions.push(opt.common.sprintf("<span style='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                        }else{
                            actions.push(opt.common.sprintf("<span class='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                        }
                        return false;
                    }
                    //兼容客户端数据为空 -- 则匹配字典默认值
                    if (opt.common.isEmpty(value) && dict.isDefault === "Y") {
                        var listClass = opt.common.equals("default", dict.listClass) || opt.common.isEmpty(dict.listClass) ? "" : "badge badge-" + dict.listClass;
                        if(!opt.common.isEmpty(dict.cssClass)){
                            listClass = opt.common.isEmpty(dict.cssClass) ? "" : dict.cssClass;
                        }
                        actions.push(opt.common.sprintf("<span class='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                        return false;
                    }
                });
                return actions.join('');
            },
            // 回显数据字典 多个, 约定以逗号分割---用于复选框翻译回显 , values 值 字符串 如： 1,2,3,4
            selectDictLabels: function(datas, valueStr) {
                var actions = [];
                valueStr = valueStr + "";
                valueStr = valueStr || "";
                var values = valueStr.split(",");
                $.each(values ,function(index, value){
                    $.each(datas, function(index, dict) {
                        if (dict.dictValue == ('' + value)) {
                            // var listClass = opt.common.equals("default", dict.listClass) || opt.common.isEmpty(dict.listClass) ? "" : "badge badge-" + dict.listClass;
                            // if(!opt.common.isEmpty(dict.cssClass)){
                            //     listClass = opt.common.isEmpty(dict.cssClass) ? "" : dict.cssClass;
                            // }
                            // actions.push(opt.common.sprintf("<span class='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                            // return false;
                            var listClass = opt.common.equals("default", dict.listClass) || opt.common.isEmpty(dict.listClass) ? "" : "badge badge-" + dict.listClass;
                            if(!opt.common.isEmpty(dict.cssClass)){
                                listClass = opt.common.isEmpty(dict.cssClass) ? "" : dict.cssClass;
                            }
                            if(!opt.common.isEmpty(dict.cssStyle)){
                                listClass = dict.cssStyle;
                            }
                            if(!opt.common.isEmpty(dict.cssStyle)){
                                actions.push(opt.common.sprintf("<span style='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                            }else{
                                actions.push(opt.common.sprintf("<span class='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                            }
                            return false;
                        }
                        //兼容客户端数据为空 -- 则匹配字典默认值
                        if (opt.common.isEmpty(value) && dict.isDefault === "Y") {
                            var listClass = opt.common.equals("default", dict.listClass) || opt.common.isEmpty(dict.listClass) ? "" : "badge badge-" + dict.listClass;
                            if(!opt.common.isEmpty(dict.cssClass)){
                                listClass = opt.common.isEmpty(dict.cssClass) ? "" : dict.cssClass;
                            }
                            actions.push(opt.common.sprintf("<span class='%s'>%s</span>", listClass, $.i18n.prop(dict.dictLabel)));
                            return false;
                        }
                    });
                });

                return actions.join(',');
            },
            // 显示表格指定列
            showColumn: function(column, tableId) {
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                $("#" + currentId).bootstrapTable('showColumn', column);
            },
            // 隐藏表格指定列
            hideColumn: function(column, tableId) {
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                $("#" + currentId).bootstrapTable('hideColumn', column);
            },
            //取消选择某一行，索引（index）从0开始
            uncheck: function(index,tableId){
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                $("#" + currentId).bootstrapTable('uncheck', index);
            },
            // 更新表格数据
            updataData: function(count,tableId){
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                // 创建一个数组,用来存放表格中所有的数据
                var params = [];
                for(var dataIndex=0;dataIndex<=count;dataIndex++) {
                    // 使用JQuery选择器找到td标签(也就是包含input标签的标签)对象,这种选法选中了很多td标签,因此需要遍历
                    var columns = $('#'+currentId+' tr[data-index="'+dataIndex+'"] td');
                    // 创建一个对象.用来存放表格中一行的数据
                    var obj = {};
                    for (var i=0; i < columns.length; i++) {
                        var inputValue = $(columns[i]).find('input');
                        if(inputValue.length == 1){
                            var selectValue = $(columns[i]).find('select');
                            var key = opt.table.options.columns[i].field;
                            if (opt.common.isNotEmpty(inputValue.val())) {
                                obj[key] = inputValue.val();
                            } else if (opt.common.isNotEmpty(selectValue.val())) {
                                obj[key] = selectValue.val();
                            } else {
                                obj[key] = "";
                            }
                        }else{
                            var selectValue = $(columns[i]).find('select');
                            var key = opt.table.options.columns[i].field;//use.id
                            if (opt.common.isNotEmpty($(inputValue[0]).val())) {
                                var names = $(inputValue[1]).attr('name').split('.');
                                if (names.length > 1) {
                                    obj[key] = $(inputValue[0]).val();
                                    var str = names;
                                    var obj0 = {},idx = str.length -1;
                                    while(idx>-1){
                                        var temp = {};
                                        temp[str[idx]] = obj0;
                                        obj0 = temp;
                                        if(idx == (str.length -1)){
                                            obj0[str[idx]] = $(inputValue[1]).val();
                                        }
                                        idx--;
                                    }
                                    obj = opt.common.extend(obj0,obj);
                                }else{
                                    obj[key] = $(inputValue[0]).val();
                                    obj[$(inputValue[1]).attr('name')] = $(inputValue[1]).val();
                                }
                            } else if (opt.common.isNotEmpty(selectValue.val())) {
                                obj[key] = selectValue.val();
                            } else {
                                obj[key] = "";
                            }
                        }

                    }
                    /*
                    * {index:索引值, row:每行索引对应的数据(是一个对象)}
                    * 表格中每一行的数据
                    * {index:0, row:{id: "1", name: "XXX", age: "18", address: "XX"}}
                    * {index:1, row:{id: "1", name: "XXX", age: "20", address: "XX"}}
                    * */
                    params.push({index:dataIndex, row:obj});
                }
                // 对表格中的数据进行更新
                $('#' + currentId).bootstrapTable("updateRow",params);
            },
            //删除选中行
            removeData: function(index,tableId) {
                var that = this;
                opt.table.set();
                index = opt.common.isEmpty(index) ? 'index' : index;
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                var rows = $('#' + currentId).bootstrapTable('getSelections');//获取选中行
                if (rows.length == 0) {
                    opt.modal.error($.i18n.prop("请选择要删除的数据"));
                    return;
                }
                var count = $("#" + currentId).bootstrapTable('getData').length;
                that.updataData(count);
                var indexs = [];
                for (var i = 0; i < rows.length; i++) {
                    indexs[i] = rows[i][index];
                }
                $('#' + opt.table.options.id).bootstrapTable('remove', {
                    field: index,
                    values: indexs
                });
            },
            //动态插入数据
            addColumn: function(row,callback,tableId){
                var that = this;
                opt.table.set(tableId);
                var currentId = opt.common.isEmpty(tableId) ? opt.table.options.id : tableId;
                var count = $("#" + currentId).bootstrapTable('getData').length;
                that.updataData(count);
                $("#" + currentId).bootstrapTable('insertRow', {
                    index: count + 1,
                    row: row
                });

                // laydate time-input 时间控件绑定
                if ($(".time-input").length > 0) {
                    layui.use('laydate', function () {
                        var com = layui.laydate;
                        $(".time-input").each(function (index, item) {
                            var time = $(item);
                            // 控制控件外观
                            var type = time.attr("data-type") || 'date';
                            // 控制回显格式
                            var format = time.attr("data-format") || 'yyyy-MM-dd';
                            // 控制日期控件按钮
                            var buttons = time.attr("data-btn") || 'clear|now|confirm', newBtnArr = [];
                            // 日期控件选择完成后回调处理
                            var callback = time.attr("data-callback") || {};
                            if (buttons) {
                                if (buttons.indexOf("|") > 0) {
                                    var btnArr = buttons.split("|"), btnLen = btnArr.length;
                                    for (var j = 0; j < btnLen; j++) {
                                        if ("clear" === btnArr[j] || "now" === btnArr[j] || "confirm" === btnArr[j]) {
                                            newBtnArr.push(btnArr[j]);
                                        }
                                    }
                                } else {
                                    if ("clear" === buttons || "now" === buttons || "confirm" === buttons) {
                                        newBtnArr.push(buttons);
                                    }
                                }
                            } else {
                                newBtnArr = ['clear', 'now', 'confirm'];
                            }
                            com.render({
                                elem: item,
                                theme: 'molv',
                                trigger: 'click',
                                type: type,
                                format: format,
                                btns: newBtnArr,
                                done: function (value, data) {
                                    if (typeof window[callback] != 'undefined'
                                        && window[callback] instanceof Function) {
                                        window[callback](value, data);
                                    }
                                }
                            });
                        });
                    });
                }

                if(typeof callback == "function"){
                    callback(row);
                }
            }
        }
    })
})(jQuery);

// 表格树封装处理
(function ($) {
    $.extend({
        bttTable: {},
        // 表格树封装处理
        treeTable: {
            // 初始化表格
            init: function(options) {
                var defaults = {
                    id: "bootstrap-tree-table",
                    type: 1, // 0 代表bootstrapTable 1代表bootstrapTreeTable
                    height: 0,
                    rootIdValue: '0',       //树根节点 id
                    ajaxParams: {},
                    sortName: "",           //排序字段
                    sortOrder: "asc",       //默认升序
                    async: false,
                    toolbar: "toolbar",
                    striped: false,
                    pageSize: 12,              // 一页条数
                    pageList: [12, 24, 32],    // 分页数据库
                    expandColumn: 1,
                    showSearch: true,
                    showRefresh: true,
                    showColumns: true,
                    expandAll: true,
                    expandFirst: true,
                    bordered: false,
                    asynUrl: null,
                };
                var options = $.extend(defaults, options);

                //
                var flag = false;
                if(!opt.common.isEmpty(options.columns.length)){
                    for(var i=0; i<options.columns.length; i++ ){
                        if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"align"))){
                            options.columns[i].align = 'center';
                        }
                        if(opt.common.isEmpty(opt.common.getJsonValue(options.columns[i],"halign"))){
                            options.columns[i].halign = 'center';
                        }
                        if(opt.common.getJsonValue(options.columns[i],"field") === 'selectItem'){
                            flag = true;
                        }
                    }
                }

                if(!flag){
                    options.expandColumn = 0;
                }

                opt.table.options = options;
                opt.table.config[options.id] = options;
                $.bttTable = $('#' + options.id).bootstrapTreeTable({
                    id:  options.id,
                    code: options.code,                                 // 用于设置父子关系
                    parentCode: options.parentCode,                     // 用于设置父子关系
                    type: 'post',                                       // 请求方式（*）
                    url: options.url,                                   // 请求后台的URL（*）
                    async: options.async,                               // 是否异步加载数据
                    asynUrl: options.asynUrl,                           // 异步加载子数据请求URL
                    data: options.data,                                 // 无url时用于渲染的数据
                    ajaxParams: options.ajaxParams,                     // 请求数据的ajax的data属性
                    sortName: options.sortName,                         // 排序字段
                    sortOrder: options.sortOrder,                       // 默认升序
                    rootIdValue: options.rootIdValue,                   // 设置指定根节点id值
                    height: options.height,                             // 表格树的高度
                    pageSize: options.pageSize,                         // 一页条数
                    pageList: options.pageList,                         // 页面列表
                    expandColumn: options.expandColumn,                 // 在哪一列上面显示展开按钮
                    striped: options.striped,                           // 是否显示行间隔色
                    bordered: options.bordered,                         // 是否显示边框
                    toolbar: '#' + options.toolbar + '-' + options.id,  // 指定工作栏
                    showSearch: options.showSearch,                     // 是否显示检索信息
                    showRefresh: options.showRefresh,                   // 是否显示刷新按钮
                    showColumns: options.showColumns,                   // 是否显示隐藏某列下拉框
                    expandAll: options.expandAll,                       // 是否全部展开
                    expandFirst: options.expandFirst,                   // 是否默认第一级展开--expandAll为false时生效
                    columns: options.columns,                           // 显示列信息（*）
                    onClickRow: $.treeTable.onClickRow,                                   // 单击某行事件
                    responseHandler: $.treeTable.responseHandler        // 当所有数据被加载时触发处理函数
                });
            },
            // 条件查询
            search: function(formId) {
                var currentId = opt.common.isEmpty(formId) ? $('form').attr('id') : formId;
                var params = opt.common.formToJSON(currentId);
                // 添加异步请求参数
                params['__refre'] = true;
                $.bttTable.bootstrapTreeTable('refresh', params);
            },
            // 刷新
            refresh: function() {
                $.bttTable.bootstrapTreeTable('refresh', {'__refre':true});
            },
            // 查询表格树指定列值
            selectColumns: function(column) {
                var rows = $.map($.bttTable.bootstrapTreeTable('getSelections'), function (row) {
                    return row[column];
                });
                return opt.common.uniqueFn(rows);
            },
            //单击某行回调事件
            onClickRow: function(data){
                var toolbar = 'toolbar-'+ opt.table.get(this.id).id ;
                $('#' + toolbar + ' .multiple').toggleClass('disabled', false);
                $('#' + toolbar + ' .single').toggleClass('disabled', false);
                if (typeof opt.table.get(this.id).onClickRow == "function") {
                    opt.table.get(this.id).onClickRow(data);
                }
            },
            // 请求获取数据后处理回调函数，校验异常状态提醒
            responseHandler: function(data) {

                if (data.code != undefined && data.code != 0) {
                    opt.modal.error(data.msg);
                    return [];
                } else {
                    if (typeof opt.table.get(this.id).responseHandler == "function") {
                        opt.table.get(this.id).responseHandler(data);
                    }
                    return data;
                }
            },
        }
    })
})(jQuery);

//树封装
(function ($) {
    $.extend({
        _tree: {},
        tree: {
            _option: {},
            _lastValue: {},
            // 初始化树结构
            init: function(options) {
                var defaults = {
                    ajxType: "GET",
                    ajaxParams:{},
                    id: "tree",                    // 属性ID
                    expandLevel: 0,                // 展开等级节点 0-默认 1-展开根节点(不包含子节点) 2-展开所有节点
                    view: {
                        selectedMulti: false,      // 设置是否允许同时选中多个节点
                        nameIsHTML: true           // 设置 name 属性是否支持 HTML 脚本
                    },
                    check: {
                        enable: true,             // 置 zTree 的节点上是否显示 checkbox / radio
                        radioType: "level",
                        chkStyle: "radio",
                        nocheckInherit: true      // 设置子节点是否自动继承
                    },
                    data: {
                        key: {
                            title: "name",         // 节点数据保存节点提示信息的属性名称
                            name: "title"
                        },
                        simpleData: {
                            enable: true           // true / false 分别表示 使用 / 不使用 简单数据模式
                        }
                    },
                    displayLen: 0
                };
                var options = $.extend(defaults, options);
                $.tree._option = options;
                // 树结构初始化加载
                var setting = {
                    callback: {
                        onClick: options.onClick,                      // 用于捕获节点被点击的事件回调函数
                        onCheck: options.onCheck,                      // 用于捕获 checkbox / radio 被勾选 或 取消勾选的事件回调函数
                        onDblClick: options.onDblClick,                // 用于捕获鼠标双击之后的事件回调函数
                        onCollapse: options.OnCollapse,                 // 用于捕获节点被折叠的事件回调函数
                        onExpand: options.OnExpand,                      // 用于捕获节点被展开的事件回调函数
                        beforeClick: options.beforeClick
                    },
                    check: options.check,
                    view: options.view,
                    data: options.data,

                };


                var config = {
                    url: options.url,
                    type: options.ajxType,
                    dataType: "JSON",
                    data: options.ajaxParams,
                    success: function(data) {

                        //兼容返回数据
                        var list;

                        if(opt.common.isArray(data)){
                            list = data;
                        }else{
                            if(data.code == opt.variable.web_status.SUCCESS){
                                for(var key  in data){
                                    if(opt.common.isArray(data[key])){
                                        list = data[key];
                                    }
                                }
                                for(var i=0; i<list.length; i++){
                                    for(var key  in list[i]){
                                        if(key == "url"){
                                            delete list[i][key];
                                        }
                                    }
                                }
                            }else{
                                opt.modal.error(data.msg);
                                return;
                            }
                        }

                        if(options.displayLen != 0){
                            for(var i=0; i<list.length; i++){
                                for(var key  in list[i]){
                                    if(key == "title"){
                                        list[i][key] = opt.common.subString(list[i][key],options.displayLen,true);
                                    }
                                }
                            }
                        }

                        var treeId = $("#treeId").val();
                        tree = $.fn.zTree.init($("#" + options.id), setting, list);
                        $._tree = tree;
                        // var nodes = tree.getNodesByParam("level", options.expandLevel - 1);
                        //
                        // for (var i = 0; i < nodes.length; i++) {
                        //     tree.expandNode(nodes[i], true, false, false);
                        // }
                        //展开根节点 不包含子节点
                        if(options.expandLevel == 1){
                            var node = tree.getNodesByFilter(function (node) { return node.level == 0 }, true);
                            tree.expandNode(node, true, null, null);
                        }
                        //展开所有节点
                        if(options.expandLevel == 2){
                            tree.expandAll(true);
                        }
                        //
                        // if(options.check.enable){
                            if(!opt.common.isEmpty(options._list)){
                                var _l = options._list.split(",");
                                for(var i=0; i<_l.length; i++){
                                    var node = tree.getNodeByParam(opt.common.isEmpty(options.data.simpleData.idKey)?
                                        "id":options.data.simpleData.idKey, _l[i]);
                                    if(node !=null){
                                        tree.checkNode(node,true);
                                        tree.selectNode(node);
                                    }
                                }
                            }
                            if(!opt.common.isEmpty(treeId)){
                                var _l = treeId.split(",");
                                for(var i=0; i<_l.length; i++){
                                    var node = tree.getNodeByParam(opt.common.isEmpty(options.data.simpleData.idKey)?
                                        "id":options.data.simpleData.idKey, _l[i]);
                                    if(node !=null){
                                        tree.checkNode(node,true);
                                        tree.selectNode(node);
                                    }
                                }
                                // var node = tree.getNodeByParam(opt.common.isEmpty(options.data.simpleData.idKey)?
                                //     "id":options.data.simpleData.idKey, treeId);
                                // if(!opt.common.isEmpty(node)){
                                //     tree.checkNode(node,true);
                                //     if($.tree._option.check.chkStyle == 'radio') tree.selectNode(node);
                                // }
                            }
                        // }

                        // if(!opt.common.isEmpty(treeId) && !options.check.enable){
                        //     var node = tree.getNodeByParam(opt.common.isEmpty(options.data.simpleData.idKey)?
                        //         "id":options.data.simpleData.idKey, treeId);
                        //     if(!opt.common.isEmpty(node)){
                        //         tree.selectNode(node);
                        //     }
                        // }
                        //回调方法
                        if(typeof(options.callBack) === "function"){
                            options.callBack($._tree);
                        }
                    }
                };
                opt.common.sendAjax(config);
            },
            expandAll: function(){
                $._tree.expandAll(true);
            },
            // 搜索节点
            searchNode: function() {
                // 取得输入的关键字的值
                var value = opt.common.trim($("#keyword").val());
                if ($.tree._lastValue == value) {
                    return;
                }
                // 保存最后一次搜索名称
                $.tree._lastValue = value;
                var nodes = $._tree.getNodes();
                // 如果要查空字串，就退出不查了。
                if (value == "") {
                    $.tree.showAllNode(nodes);
                    return;
                }
                $.tree.hideAllNode(nodes);

                //console.log($._tree.getNodesByParamFuzzy("name", value))

                // 根据搜索值模糊匹配
                $.tree.updateNodes($._tree.getNodesByParamFuzzy("name", value));
            },
            // 根据Id和Name选中指定节点
            selectByIdName: function(treeId, node) {
                if (opt.common.isNotEmpty(treeId) && treeId == opt.common.getJsonValue(node,$.tree._option.data.simpleData.idKey)) {
                    $._tree.selectNode(node, true);
                }
            },
            // 显示所有节点
            showAllNode: function(nodes) {
                nodes = $._tree.transformToArray(nodes);
                for (var i = nodes.length - 1; i >= 0; i--) {
                    if (nodes[i].getParentNode() != null) {
                        $._tree.expandNode(nodes[i], true, false, false, false);
                    } else {
                        $._tree.expandNode(nodes[i], true, true, false, false);
                    }
                    $._tree.showNode(nodes[i]);
                    $.tree.showAllNode(nodes[i].children);
                }
            },
            // 隐藏所有节点
            hideAllNode: function(nodes) {
                var tree = $.fn.zTree.getZTreeObj("tree");
                var nodes = $._tree.transformToArray(nodes);
                for (var i = nodes.length - 1; i >= 0; i--) {
                    $._tree.hideNode(nodes[i]);
                }
            },
            // 显示所有父节点
            showParent: function(treeNode) {
                var parentNode;
                while ((parentNode = treeNode.getParentNode()) != null) {
                    $._tree.showNode(parentNode);
                    $._tree.expandNode(parentNode, true, false, false);
                    treeNode = parentNode;
                }
            },
            // 获取所有 节点所有的父节点
            getParentIds: function(treeNode){
                if(opt.common.isEmpty(treeNode)) return "";
                var ids = treeNode.id;
                var node = treeNode.getParentNode();
                if(node != null){
                    ids = ids + "," + $.tree.getParentIds(node);
                }
                return ids;
            },
            // 显示所有孩子节点
            showChildren: function(treeNode) {
                if (treeNode.isParent) {
                    for (var idx in treeNode.children) {
                        var node = treeNode.children[idx];
                        $._tree.showNode(node);
                        $.tree.showChildren(node);
                    }
                }
            },
            // 更新节点状态
            updateNodes: function(nodeList) {
                $._tree.showNodes(nodeList);
                for (var i = 0, l = nodeList.length; i < l; i++) {
                    var treeNode = nodeList[i];
                    $.tree.showChildren(treeNode);
                    $.tree.showParent(treeNode)
                }
            },
            //重新加载
            refreshNode:function(){
                $._tree.reAsyncChildNodes(null, "refresh");
            },
            // 获取当前被勾选集合
            getCheckedNodes: function(column) {
                var _column = opt.common.isEmpty(column) ? (opt.common.isEmpty($.tree._option.data.simpleData.idKey)?
                    "id":$.tree._option.data.simpleData.idKey) : column;
                var nodes = $._tree.getCheckedNodes(true);
                var _t = $.map(nodes, function (row) {
                    return row[_column];
                }).join()
                return _t.split(",");
            },
            // 不允许根父节点选择
            notAllowParents: function(_tree) {
                // var nodes = _tree.getSelectedNodes();
                // if(nodes.length == 0){
                //     $.modal.msgError("请选择节点后提交");
                //     return false;
                // }
                // for (var i = 0; i < nodes.length; i++) {
                //     if (nodes[i].level == 0) {
                //         $.modal.msgError("不能选择根节点（" + nodes[i].name + "）");
                //         return false;
                //     }
                //     if (nodes[i].isParent) {
                //         $.modal.msgError("不能选择父节点（" + nodes[i].name + "）");
                //         return false;
                //     }
                // }
                return true;
            },
            // 不允许最后层级节点选择
            notAllowLastLevel: function(_tree) {
                var nodes = _tree.getSelectedNodes();
                for (var i = 0; i < nodes.length; i++) {
                    if (!nodes[i].isParent) {
                        $.modal.msgError("不能选择最后层级节点（" + nodes[i].name + "）");
                        return false;
                    }
                }
                return true;
            },
            // 隐藏/显示搜索栏
            toggleSearch: function() {
                $('#search').slideToggle(200);
                $('#btnShow').toggle();
                $('#btnHide').toggle();
                $('#keyword').focus();
            },
            //勾选 或 取消勾选 全部节点
            checkAllNodes: function(checked){
                $._tree.checkAllNodes(checked);
            },
            // 折叠
            collapse: function() {
                $._tree.expandAll(false);
            },
            // 展开
            expand: function() {
                $._tree.expandAll(true);
            },
            //处理回调之后获取id 与name 选取的所有父节点
            callBackTree:function (_p,_m) {
                var tree = _p.find("iframe")[0].contentWindow.$._tree;
                if ($.tree.notAllowParents(tree)) {
                    var body = layer.getChildFrame('body', _m);
                    var treeId = body.find('#treeId').val()
                    var treeName = body.find('#treeName').val();
                    var parentIds = body.find('#parentIds').val();
                    var treeTitle = body.find('#treeTitle').val();
                    var type = body.find('#type').val();
                    // if(opt.common.isEmpty(treeName)){
                    //     if(tree.setting.check.enable){
                    //         var _l = treeId.split(",");
                    //         for(var i=0; i<_l.length; i++){
                    //             var node = tree.getNodeByParam(tree.setting.data.simpleData.idKey,_l[i]);
                    //             if(node!=null){
                    //                 treeName += node.name + ",";
                    //             }
                    //         }
                    //         if(opt.common.isEmpty(treeName)) treeName = treeName.substr(0,treeName.length-1);
                    //     }else{
                    //         treeName = tree.getNodesByParam(tree.setting.data.simpleData.idKey, treeId, null)[0].name;
                    //     }
                    // }
                    var _n = "{\"id\":\""+treeId+"\",\"name\":\""+treeName+"\",\"title\":\""+treeTitle+"\",\"parentIds\":\""+parentIds+"\",\"type\":\""+type+"\"}";
                    return $.parseJSON(_n);
                }else{
                    return false;
                }
            }
        }
    })
})(jQuery);
//设置主页皮肤
+function () {

    var _lang = opt.getCookie("_lang");
    //初始化i18n插件
    $.i18n.properties({
        path: baseURL + 'i18n/',//这里表示访问路径
        name: 'i18n',//文件名开头
        language: _lang,//文件名语言 例如en_US
        cache: true,
        mode: 'map'//默认值
    });


    var tmp = opt.storage.get('skin');
    if (tmp && ($.inArray(tmp, opt.variable.skins)>=0)){
        opt.changeSkin(tmp);
    }

    //设置所有弹出框样式
    if(window.layer !== undefined){
        layer.config({
            extend: 'moon/style.css',
            skin: 'layer-ext-moon'
        });
    };

    /*$(document).ajaxError(function(e,xhr,opt){
        if(xhr.statusText == "parsererror"){//被踢下线,或者被挤下线
            window.opt.wclearInterval();
            $.getJSON("sys/user/info/login/msg/"+_username+"?V=" + $.now(), function (r) {
                if(r.code == "00000"){
                    var m = r.msg.split("#");
                    window.opt.outLogin(m[0]+"</br>" + m[1],$.i18n.prop('sys.login.out.info'));
                    return;
                }else{
                    window.opt.outLogin("",r.msg);
                    return;
                }
            });
        }
        if(xhr.statusText == "error"){//与服务器断开连接
            window.opt.wclearInterval();
            window.opt.outLogin("",$.i18n.prop('sys.login.out.error'));
            return;
        }
    });*/
    // $.ajaxSetup({
    //     complete: function(XMLHttpRequest, textStatus) {
    //         if (textStatus == 'timeout') {
    //             //window.opt.wclearInterval();
    //             opt.outLogin("",$.i18n.prop('sys.login.out.error'));
    //             return;
    //         } else if (textStatus == "parsererror" || textStatus == "error") {
    //             opt.outLogin("",$.i18n.prop('sys.login.out.error'));
    //             return;
    //         }
    //     }
    // });
}();
/**
 * 页面模板引擎
 * 声明: 引用layui.laytpl 作者:贤心
 * j2eefast.com zhouzhou 二次封装
 */
!function () {
    var config = {
        open: '{{',
        close: '}}'
    };

    var tool = {
        exp: function(str){
            return new RegExp(str, 'g');
        },
        //匹配满足规则内容
        query: function(type, _, __){
            var types = [
                '#([\\s\\S])+?',   //js语句
                '([^{#}])*?' //普通字段
            ][type || 0];
            return exp((_||'') + config.open + types + config.close + (__||''));
        },
        escape: function(html){
            return String(html||'').replace(/&(?!#?[a-zA-Z0-9]+;)/g, '&amp;')
                .replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/'/g, '&#39;').replace(/"/g, '&quot;');
        },
        error: function(e, tplog){
            var error = 'Laytpl Error：';
            typeof console === 'object' && console.error(error + e + '\n'+ (tplog || ''));
            return error + e;
        }
    };

    var exp = tool.exp, Tpl = function(tpl){
        this.tpl = tpl;
    };

    Tpl.pt = Tpl.prototype;

    window.errors = 0;

    //编译模版
    Tpl.pt.parse = function(tpl, data){
        var that = this, tplog = tpl;
        var jss = exp('^'+config.open+'#', ''), jsse = exp(config.close+'$', '');

        tpl = tpl.replace(/\s+|\r|\t|\n/g, ' ')
            .replace(exp(config.open+'#'), config.open+'# ')
            .replace(exp(config.close+'}'), '} '+config.close).replace(/\\/g, '\\\\')

            //不匹配指定区域的内容
            .replace(exp(config.open + '!(.+?)!' + config.close), function(str){
                str = str.replace(exp('^'+ config.open + '!'), '')
                    .replace(exp('!'+ config.close), '')
                    .replace(exp(config.open + '|' + config.close), function(tag){
                        return tag.replace(/(.)/g, '\\$1')
                    });
                return str
            })

            //匹配JS规则内容
            .replace(/(?="|')/g, '\\').replace(tool.query(), function(str){
                str = str.replace(jss, '').replace(jsse, '');
                return '";' + str.replace(/\\/g, '') + ';view+="';
            })

            //匹配普通字段
            .replace(tool.query(1), function(str){
                var start = '"+(';
                if(str.replace(/\s/g, '') === config.open+config.close){
                    return '';
                }
                str = str.replace(exp(config.open+'|'+config.close), '');
                if(/^=/.test(str)){
                    str = str.replace(/^=/, '');
                    start = '"+_escape_(';
                }
                return start + str.replace(/\\/g, '') + ')+"';
            });

        tpl = '"use strict";var view = "' + tpl + '";return view;';

        try{
            that.cache = tpl = new Function('d, _escape_', tpl);
            return tpl(data, tool.escape);
        } catch(e){
            delete that.cache;
            return tool.error(e, tplog);
        }
    };

    Tpl.pt.render = function(data, callback){
        var that = this, tpl;
        if(!data) return tool.error('no data');
        tpl = that.cache ? that.cache(data, tool.escape) : that.parse(that.tpl, data);
        if(!callback) return tpl;
        callback(tpl);
    };

    var laytpl = function(tpl){
        if(typeof tpl !== 'string') return tool.error('Template not found');
        return new Tpl(tpl);
    };

    laytpl.config = function(options){
        options = options || {};
        for(var i in options){
            config[i] = options[i];
        }
    };

    laytpl.v = '1.2.0';

    "function" == typeof define ? define(function () {
        return laytpl
    }) : "undefined" != typeof exports ? module.exports = laytpl : window.laytpl = laytpl
}();

/* BoxWidget()
 * ======
 * Adds box widget functions to boxes.
 *
 * @Usage: $('.my-box').boxWidget(options)
 *         This plugin auto activates on any element using the `.box` class
 *         Pass any option as data-option="value"
 */
+function ($) {
    'use strict';

    var DataKey = 'lte.boxwidget';

    var Default = {
        animationSpeed : 500,
        collapseTrigger: '[data-widget="collapse"]',
        removeTrigger  : '[data-widget="remove"]',
        collapseIcon   : 'fa-minus',
        expandIcon     : 'fa-plus',
        removeIcon     : 'fa-times'
    };

    var Selector = {
        data     : '.box',
        collapsed: '.collapsed-box',
        header   : '.box-header',
        body     : '.box-body',
        footer   : '.box-footer',
        tools    : '.box-tools'
    };

    var ClassName = {
        collapsed: 'collapsed-box'
    };

    var Event = {
        collapsing: 'collapsing.boxwidget',
        collapsed: 'collapsed.boxwidget',
        expanding: 'expanding.boxwidget',
        expanded: 'expanded.boxwidget',
        removing: 'removing.boxwidget',
        removed: 'removed.boxwidget'
    };

    // BoxWidget Class Definition
    // =====================
    var BoxWidget = function (element, options) {
        this.element = element;
        this.options = options;

        this._setUpListeners();
    };

    BoxWidget.prototype.toggle = function () {
        var isOpen = !$(this.element).is(Selector.collapsed);

        if (isOpen) {
            this.collapse();
        } else {
            this.expand();
        }
    };

    BoxWidget.prototype.expand = function () {
        var expandedEvent = $.Event(Event.expanded);
        var expandingEvent = $.Event(Event.expanding);
        var collapseIcon  = this.options.collapseIcon;
        var expandIcon    = this.options.expandIcon;

        $(this.element).removeClass(ClassName.collapsed);

        $(this.element)
            .children(Selector.header + ', ' + Selector.body + ', ' + Selector.footer)
            .children(Selector.tools)
            .find('.' + expandIcon)
            .removeClass(expandIcon)
            .addClass(collapseIcon);

        $(this.element).children(Selector.body + ', ' + Selector.footer)
            .slideDown(this.options.animationSpeed, function () {
                $(this.element).trigger(expandedEvent);
            }.bind(this))
            .trigger(expandingEvent);
    };

    BoxWidget.prototype.collapse = function () {
        var collapsedEvent = $.Event(Event.collapsed);
        var collapsingEvent = $.Event(Event.collapsing);
        var collapseIcon   = this.options.collapseIcon;
        var expandIcon     = this.options.expandIcon;

        $(this.element)
            .children(Selector.header + ', ' + Selector.body + ', ' + Selector.footer)
            .children(Selector.tools)
            .find('.' + collapseIcon)
            .removeClass(collapseIcon)
            .addClass(expandIcon);

        $(this.element).children(Selector.body + ', ' + Selector.footer)
            .slideUp(this.options.animationSpeed, function () {
                $(this.element).addClass(ClassName.collapsed);
                $(this.element).trigger(collapsedEvent);
            }.bind(this))
            .trigger(expandingEvent);
    };

    BoxWidget.prototype.remove = function () {
        var removedEvent = $.Event(Event.removed);
        var removingEvent = $.Event(Event.removing);

        $(this.element).slideUp(this.options.animationSpeed, function () {
            $(this.element).trigger(removedEvent);
            $(this.element).remove();
        }.bind(this))
            .trigger(removingEvent);
    };

    // Private

    BoxWidget.prototype._setUpListeners = function () {
        var that = this;

        $(this.element).on('click', this.options.collapseTrigger, function (event) {
            if (event) event.preventDefault();
            that.toggle($(this));
            return false;
        });

        $(this.element).on('click', this.options.removeTrigger, function (event) {
            if (event) event.preventDefault();
            that.remove($(this));
            return false;
        });
    };

    // Plugin Definition
    // =================
    function Plugin(option) {
        return this.each(function () {
            var $this = $(this);
            var data  = $this.data(DataKey);

            if (!data) {
                var options = $.extend({}, Default, $this.data(), typeof option == 'object' && option);
                $this.data(DataKey, (data = new BoxWidget($this, options)));
            }

            if (typeof option == 'string') {
                if (typeof data[option] == 'undefined') {
                    throw new Error('No method named ' + option);
                }
                data[option]();
            }
        });
    }

    var old = $.fn.boxWidget;

    $.fn.boxWidget             = Plugin;
    $.fn.boxWidget.Constructor = BoxWidget;

    // No Conflict Mode
    // ================
    $.fn.boxWidget.noConflict = function () {
        $.fn.boxWidget = old;
        return this;
    };

    // BoxWidget Data API
    // ==================
    $(window).on('load', function () {
        $(Selector.data).each(function () {
            Plugin.call($(this));
        });
    });
}(jQuery);

/**
 * 表单序列号成对象 使用--> $('').serializeJson()
 * 表单序列号成 字符串 使用-->$('').serialize()
 */
(function(window, $) {
    $.fn.serializeJson = function() {
        var serializeObj = {};
        var array = this.serializeArray();
        //var str = this.serialize();
        $(array).each(
            function() {
                if (serializeObj[this.name]) {
                    if ($.isArray(serializeObj[this.name])) {
                        serializeObj[this.name].push(this.value);
                    } else {
                        serializeObj[this.name] = [
                            serializeObj[this.name], this.value ];
                    }
                } else {
                    serializeObj[this.name] = this.value;
                }
            });
        return serializeObj;
    };
})(window, jQuery);