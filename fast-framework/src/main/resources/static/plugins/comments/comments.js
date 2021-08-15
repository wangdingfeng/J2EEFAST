/*!
 * 评论回复插件
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 * @author ZhouHuan
 * @date 2021年02月27日16:43:48
 * @version v1.0
 */
(function($) {
    "use strict";

    // it only does '%s', and return '' when arguments are undefined
    var sprintf = function (str) {
        var args = arguments,
            flag = true,
            i = 1;

        str = str.replace(/%s/g, function () {
            var arg = args[i++];

            if (typeof arg === 'undefined') {
                flag = false;
                return '';
            }
            return arg;
        });
        return flag ? str : '';
    };

    var calculateObjectValue = function (self, name, args, defaultValue) {
        var func = name;
        if (typeof name === 'string') {
            // support obj.func1.func2
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

    var CommentsData = function (el, options) {
        this.options = options;
        this.$el = $(el);
        this.$el_ = this.$el.clone();
        this.timeoutId_ = 0;
        this.timeoutFooter_ = 0;
        //评论组件初始化
        this.init();
    };

    // 初始化入口
    CommentsData.prototype.init = function () {
        this.initLocale();
        this.initContainer();
        this.initFoorter();
        this.initAvatar();
        this.initCommentData();
        this.hideLoading();
    };

    //初始化容器
    CommentsData.prototype.initContainer = function () {
        this.$container = $([
            '<div class="comment-wrap-box">',
                '<div class="comment-wrap-box__title">',
                    '<i class="'+this.options.titleIcon+'"></i>' + this.options.title,
                '</div>',
                '<div class="comment-wrap-box__inner">',
                    '<div class="comment-wrap-box-loading">',
                        '<span class="loading-wrap">',
                            '<span class="loading-text" style="font-size: 13px;">',
                                this.options.formatLoadingMessage(),
                            '</span>',
                            '<span class="animation-wrap">',
                                '<span class="animation-dot"></span>',
                            '</span>',
                        '</span>',
                    '</div>',
                    '<div class="comments-box">',
                        '<div class="comment-form-container">',
                            '<div class="comment-avatar-wrap">',
                            '</div>',
                            '<div class="comment-form-wrap">',
                                '<form class="comment-form">',
                                    '<div class="textarea-wrap">',
                                    '</div>',
                                    '<div class="footer-wrap">',
                                    '</div>',
                                '</from>',
                            '</div>',
                        '</div>',
                        '<div class="comments-box__list">',
                        '</div>',
                    '</div>',
                '</div>',
            '</div>'
        ].join(''));
        this.$container.insertAfter(this.$el);
        // this.$commentsContainer = this.$container.find('.comment-wrap-box__inner');
        this.$comment_wrap_box_loading = this.$container.find('.comment-wrap-box-loading');
        this.$comments_box = this.$container.find('.comments-box');
        this.$comment_avatar_wrap = this.$comments_box.find('.comment-avatar-wrap');
        this.$textarea_wrap = this.$comments_box.find('.textarea-wrap');
        this.$comments_box__list = this.$comments_box.find('.comments-box__list');
        this.$footer_wrap = this.$comments_box.find('.footer-wrap');
        this.$textarea_wrap.append(this.$el);
        this.$comments_box.css('visibility','hidden');
    }

    //初始化评论留言头像
    CommentsData.prototype.initAvatar = function(){
        var that = this,
            columns = [],
            html = [];
        //请求获取评论用户头像
        $.get(baseURL + 'sys/user/getAvatar/'+this.options.userId, function(result){
            if(result && result.code == opt.variable.web_status.SUCCESS) {
                html.push(
                    '<div title="'+result.name+'" data-user-id="'+that.options.userId+'" class="comment-avatar _35x35">',
                        '<img src="'+baseURL.substr(0,baseURL.length-1)+result.url+'" alt="'+result.name+'" title="'+result.name+'">',
                    '</div>'
                );
                that.$comment_avatar_wrap.append(html.join(''));
            }
        });
    }

    //初始化评论提交取消按钮
    CommentsData.prototype.initFoorter = function(){
        var that = this,
            html = [];
        html.push('<div class="toolbar"></div>',
                    '<div class="actions">',
                        '<div class="osc-button fluid basic btn-action btn-cancel">'+this.options.formatCancel()+'</div>',
                        '<div class="osc-button fluid primary btn-action btn-publish disabled">'+this.options.formatRelease()+'</div>',
                    '</div>');
        this.$toolbar = $(html.join(''));
        this.$footer_wrap.append(this.$toolbar);
        this.$actions = this.$footer_wrap.find('div.actions');
        //监听输入框聚焦事件
        this.$textarea_wrap.find('textarea').off('focus').on('focus',function(){
            if(!that.$footer_wrap.hasClass('active')){
                that.$footer_wrap.addClass('active');
            }
        });
        //取消事件
        this.$actions.find('.btn-cancel').off('click').on('click', $.proxy(this.onCancel, this));
        //发布事件
        this.$actions.find('.btn-publish').off('click').on('click', $.proxy(this.onPublish, this));
        //监听输入框输入事件
        this.$textarea_wrap.find('textarea').bind( 'input propertychange' , function (){
            var  value = $(this).val();
            if(value && value.length > 0){
                if(that.$actions.find('div.btn-publish').hasClass('disabled')){
                    that.$actions.find('div.btn-publish').removeClass('disabled');
                }
            }else{
                if(!that.$actions.find('div.btn-publish').hasClass('disabled')){
                    that.$actions.find('div.btn-publish').addClass(('disabled'));
                }
            }
        });
    }

    CommentsData.prototype.initCommentData = function(){
        var that = this, parms = {},params = {};

        if(this.options.sortName !== ""){
             parms = {
                __sidx:           this.options.sortName,
                __order:          this.options.sortOrder
            };
        }

        params.offset = this.options.pageSize * (this.options.pageNumber - 1);
        params.limit = this.options.pageSize;
        var curParams = {
            __limit:       params.limit,
            __page:        params.offset / params.limit + 1,
        };

        parms['msgId'] = this.options.msgId;

        parms = $.extend(curParams, parms);

        var config = {
            type: "POST",
            url: baseURL + this.options.listURL,
            data: parms,
            dataType: "JSON",
            success: function(res, textStatus, jqXHR) {
                res = calculateObjectValue(that.options, that.options.responseHandler, [res], res);
                that.load(res);
            },
            error: function(xhr, textStatus) {

            },
        };
        //J2eeFAST 新增CSRF 防护
        if(opt.common.isNotEmpty($('meta[name="csrf-token"]').attr("content"))){
            config = $.extend(config,{headers: {
                    "X-CSRF-Token": $('meta[name="csrf-token"]').attr("content") || ''
                }});
        }
        $.ajax(config);


    }

    //加载解析数据
    CommentsData.prototype.load = function (res) {
        var commentTitle = [],
            that = this, level = {}, html = '';
        if(res && res.code == '00000' && res.msg == 'SUCCESS' && res.data.totalCount > 0){
            that.options.totalRows = res.data.totalCount;
            commentTitle.push('<div class="comment-title">');
            commentTitle.push(this.options.formatCommentTitle(that.options.totalRows));
            commentTitle.push('</div>');
            commentTitle.push('<div class="comment-list">');
            commentTitle.push('</div>');
            this.$comments_box__list.empty().append(commentTitle.join(''));
            this.$comment_list = this.$comments_box__list.find('div.comment-list');
            this.options.data = res.data.list;
            this.dataAnalyze(this.options.data,level);
            this.commentList(this.options.data,false, level);
            if(this.options.isMore && this.options.data.length > 2){
                html = [
                    '<div class="collapse-bar">',
                    '<span class="collapse-btn">'+this.options.formatCommentMore()+' <i class="fa fa-angle-down"></i></span>',
                    '</div>'
                ].join('');
                this.$comments_box__list.after(html);
                this.options.isMore = false;
                this.$comments_box.find('.collapse-bar').off('click').on('click',function () {
                    that.localLoad();
                    that.$comments_box.find('.collapse-bar').remove();
                });
            }else{
                this.options.isMore = false;
                this.initPagination();
            }
        }else{
            this.$comments_box__list.empty();
        }
    }

    //本地加载
    CommentsData.prototype.localLoad = function () {
        var commentTitle = [],
        that = this, level = {};
        commentTitle.push('<div class="comment-title">');
        commentTitle.push(this.options.formatCommentTitle(that.options.totalRows));
        commentTitle.push('</div>');
        commentTitle.push('<div class="comment-list">');
        commentTitle.push('</div>');
        this.$comments_box__list.empty().append(commentTitle.join(''));
        this.$comment_list = this.$comments_box__list.find('div.comment-list');
        this.dataAnalyze(this.options.data,level);
        this.commentList(this.options.data,false, level);
        this.initPagination();
    }

    /**
     * 翻页
     * @param totalPage 一共几页
     * @param currPage 当前页
     */
    CommentsData.prototype.initPagination = function(){
        var data = this.options.data,
            html = [],that = this, from, to;
        //一共多少页
        this.totalPages = 0;

        if (this.options.totalRows) {
            this.totalPages = ~~((this.options.totalRows - 1) / this.options.pageSize) + 1;
            this.options.totalPages = this.totalPages;
        }

        //起始条数下标
        this.pageFrom = (this.options.pageNumber - 1) * this.options.pageSize + 1;
        //末尾条数下标
        this.pageTo = this.options.pageNumber * this.options.pageSize;
        if (this.pageTo > this.options.totalRows) {
            this.pageTo = this.options.totalRows;
        }

        $.each(this.options.pageList, function (i, page) {
            if(that.options.totalRows > page){
                that.options.onlyInfoPagination = true;
            }
        })
        this.$comments_box__list.find('div.comment-pagination').remove();
        if(this.options.onlyInfoPagination && this.totalPages > 1){
                html.push('<div class="comment-pagination" data-page="'+this.options.pageNumber+'">');
                html.push('<ul class="pagination pagination-outline">');
                html.push('<li class="page-pre"><a href="javascript:void(0)">'+this.options.paginationPreText +'</a></li>');
                if (this.totalPages < 5) {
                    from = 1;
                    to = this.totalPages;
                } else {
                    from = this.totalPages - 2;
                    to = from + 4;
                    if (from < 1) {
                        from = 1;
                        to = 5;
                    }
                    if (to > this.totalPages) {
                        to = this.totalPages;
                        from = to - 4;
                    }
                }

                if (this.totalPages >= 6) {
                    if (this.options.pageNumber >= 3) {
                        html.push('<li class="page-first' + (1 == this.options.pageNumber ? ' active' : '') + '">',
                            '<a href="javascript:void(0)">', 1, '</a>',
                            '</li>');

                        from++;
                    }

                    if (this.options.pageNumber >= 4) {
                        if (this.options.pageNumber == 4 || this.totalPages == 6 || this.totalPages == 7) {
                            from--;
                        } else {
                            html.push('<li class="page-first-separator disabled">',
                                '<a href="javascript:void(0)">...</a>',
                                '</li>');
                        }

                        to--;
                    }
                }

                if (this.totalPages >= 7) {
                    if (this.options.pageNumber >= (this.totalPages - 2)) {
                        from--;
                    }
                }

                if (this.totalPages == 6) {
                    if (this.options.pageNumber >= (this.totalPages - 2)) {
                        to++;
                    }
                } else if (this.totalPages >= 7) {
                    if (this.totalPages == 7 || this.options.pageNumber >= (this.totalPages - 3)) {
                        to++;
                    }
                }

                for (var i = from; i <= to; i++) {
                    html.push('<li class="page-number' + (i == this.options.pageNumber ? ' active' : '') + '">',
                        '<a href="javascript:void(0)">', i, '</a>',
                        '</li>');
                }

                if (this.totalPages >= 8) {
                    if (this.options.pageNumber <= (this.totalPages - 4)) {
                        html.push('<li class="page-last-separator disabled">',
                            '<a href="javascript:void(0)">...</a>',
                            '</li>');
                    }
                }

                if (this.totalPages >= 6) {
                    if (this.options.pageNumber <= (this.totalPages - 3)) {
                        html.push('<li class="page-last' + (this.totalPages === this.options.pageNumber ? ' active' : '') + '">',
                            '<a href="javascript:void(0)">', this.totalPages, '</a>',
                            '</li>');
                    }
                }

                html.push('<li class="page-next"><a href="javascript:void(0)">'+this.options.paginationNextText +'</a></li>');
                html.push('</ul></div>')
                this.$comment_list.after(html.join(''));

                var $this = this.$comments_box__list.find('div.comment-pagination');

                //添加事件
                //上一页
                var $pre = $this.find('.page-pre');
                //下一页
                var $next = $this.find('.page-next');
                //页码
                var $number = $this.find('.page-number');
                //首页
                var $first = $this.find('.page-first');
                //尾页
                var $last = $this.find('.page-last');

                $first.off('click').on('click', $.proxy(this.onPageFirst, this));
                $pre.off('click').on('click', $.proxy(this.onPagePre, this));
                $number.off('click').on('click', $.proxy(this.onPageNumber, this));
                $last.off('click').on('click', $.proxy(this.onPageLast, this));
                $next.off('click').on('click', $.proxy(this.onPageNext, that));
        }
    }

    /**
     * 上一页
     * @param event
     */
    CommentsData.prototype.onPagePre = function(event){
        if ((this.options.pageNumber - 1) === 0) {
            this.options.pageNumber = this.options.totalPages;
        } else {
            this.options.pageNumber--;
        }
        this.initCommentData();
    }

    /**
     * 下一页
     * @param event
     */
    CommentsData.prototype.onPageNext = function(event){
        if ((this.options.pageNumber + 1) > this.options.totalPages) {
            this.options.pageNumber = 1;
        } else {
            this.options.pageNumber++;
        }
        this.initCommentData();
    }

    /**
     * 点击首页第一页
     * @param event
     */
    CommentsData.prototype.onPageFirst = function(event){
        this.options.pageNumber = 1;
        this.initCommentData();
    }

    /**
     * 点击尾页页码
     * @param event
     */
    CommentsData.prototype.onPageLast = function(event){
        this.options.pageNumber = this.options.totalPages;
        this.initCommentData();
    }

    /**
     * 点击页码
     * @param event
     */
    CommentsData.prototype.onPageNumber = function(event){
        //点击为当前页
        if (this.options.pageNumber == $(event.currentTarget).text()) {
            return;
        }
        this.options.pageNumber = $(event.currentTarget).text();
        this.initCommentData();
    }


    //数据分析
    CommentsData.prototype.dataAnalyze = function (data,level){

        for(var i=0; i< data.length; i++){
            if(data[i].reply){
                if(data[i].replyId == '0'){
                    level[data[i].id] = {level: 0, name: data[i].userName};
                }else{
                    var l = level[data[i].replyId].level + 1;
                    level[data[i].id] = {level: l, name: data[i].userName};
                    if(l == 5){
                        level[data[i].id + "_f"] = data[i].replyId;
                    }
                    if(l > 5){
                        level[data[i].id + "_f"] = level[data[i].replyId + "_f"];
                    }
                }
                this.dataAnalyze(data[i].children,level);
            }else{
                if(data[i].replyId == '0'){
                    level[data[i].id] = {level: 0, name: data[i].userName};
                }else{
                    var l = level[data[i].replyId].level + 1;
                    level[data[i].id] = {level: l, name: data[i].userName};
                    if(l == 5){
                        level[data[i].id + "_f"] = data[i].replyId;
                    }
                    if(l > 5){
                        level[data[i].id + "_f"] = level[data[i].replyId + "_f"];
                    }
                }
            }
        }
    }

    CommentsData.prototype.recursive = function(data,id){
        for(var i=0; i< data.length; i++){
            if(data[i].id == id){
                return data[i];
            }
            if(data[i].reply){
                var isdata = this.recursive(data[i].children,id);
                if(isdata){
                    return  isdata;
                }
            }
        }
    }

    //解析加载数据
    CommentsData.prototype.commentList = function (data,reply,level) {
        var html = [],
            isDel = false,
            that = this;
        /**
         *
         */
        $.each(data, function (i, row) {

            if(that.options.isMore){
                if(i > 2 && row.replyId == "0"){
                    return;
                }
            }

            html = [], isDel = row.userId == that.options.userId;
            var div = reply ? '<div class="comment-item__children">' : '',avatar='';
            div =  div + ['<div class="comment-item"' + ( reply ? ' comment-item--child': ''),
                sprintf(' id="%s"',row.id),
                sprintf(' name="c_%s"',row.id),
                sprintf(' data-comment-id="%s"',row.id),
                sprintf(' data-comment-parent-id="%s"',row.replyId),
                sprintf(' data-comment-is-last-level="%s"',row.reply),
                sprintf(' data-comment-user-id="%s"',row.userId),
                sprintf(' data-comment-user-id="%s"',row.userId),
                sprintf(' data-comment-user-name="%s"',row.userName),
                (level[row.id].level > 5) ? sprintf(' data-comment-at="%s"',true): sprintf(' data-comment-at="%s"',false),
                '>'].join('');
            html.push(div+'<div class="comment-item__inner" data-id="_inner_'+row.id+'">');
            avatar = ['<div class="comment-item__avatar">',
                '<a href="#">',
                '<div class="comment-avatar _35x35" title="'+row.userName+'" data-user-id="'+row.userId+'">',
                '<img src="'+row.avatar+'" alt="'+row.userName+'" title="'+row.userName+'">',
                '</div></a></div>'
            ].join('');
            html.push(avatar);
            html.push('<div class="comment-item__main">' +
                '<div class="comment-item__header">' +
                '<div class="comment-item__author">' +
                '<span class="author">'+row.userName+'</span>' +
                '</div>' +
                '</div>' +
                '<div class="comment-item__content" data-emoji-render="">' +
                ((level[row.id].level > 5)? ( '回复 <span style="color: #00a0e9; font-weight: bold"> @'+ level[row.replyId].name + '</span> : ' + row.content) : row.content)+
                '</div>')
            html.push('<div class="comment-item__actions" data-id="_actions_'+row.id+'">' +
                '<div class="item-list">' +
                '<div class="item">'+row.commentsDate+'</div>'+
                '</div>');
            html.push('<div class="item-list">' +
                '<div class="item">' +
                '<span class="clickable" data-id="'+row.id+'"><i class="fa fa-comment"></i> '+ that.options.formatCommentReply()+'</span>' +
                '</div>');
            if(isDel){
                html.push('<div class="item">'+
                    '<span class="clickable" data-toggle-remove="'+row.id+'"><i class="fa fa-trash"></i> '+that.options.formatDelCommentReply() +'</span>'+
                    '</div>')
            }
            html.push('</div></div>');
            if(!reply){
                html.push('</div></div>');
                that.$comment_list.append(html.join(''));
            }else{
                html.push('</div></div></div>');
                if(level[row.id].level > 5){
                    $("#"+level[row.id + "_f"]).find('.comment-item__inner[data-id="_inner_'+level[row.id + "_f"]+'"]').after(html.join(''));
                }else{
                    $("#"+row.replyId).find('.comment-item__inner[data-id="_inner_'+row.replyId+'"]').after(html.join(''));
                }
            }
            //回复按钮添加事件
            $('#'+row.id).find('span[data-id="'+row.id+'"]').off('click').on('click', function () {
                var $this = $(this),
                    center = (level[row.id].level >= 5)? (' 回复 @ '+ level[row.id].name): '',
                    $html = that.initReply($this.data('id'),center);
                $('#'+$this.data('id')).find('.comment-item__actions[data-id="_actions_'+$this.data('id')+'"]').after($html);

                var $reply = $('#'+$this.data('id')).find('div[data-comment-id="reply_'+$this.data('id')+'"]');
                //取消事件
                $reply.find('.btn-close').off('click').on('click', function(){
                    $('#__replyCommentForm').remove();
                })

                //监听输入框输入事件
                $reply.find('.field >textarea ').bind( 'input propertychange' , function (){
                    var  value = $(this).val();
                    if(value && value.length > 0){
                        if($reply.find('.field > .btn-submit ').hasClass('disabled')){
                            $reply.find('.field > .btn-submit ').removeClass('disabled');
                        }
                    }else{
                        if(!$reply.find('.field > .btn-submit ').hasClass('disabled')){
                            $reply.find('.field > .btn-submit ').addClass('disabled');
                        }
                    }
                });

                //回复事件
                $reply.find('.field > .btn-submit ').off('click').on('click', $.proxy(that.onReplyPublish, that));

            });

            //删除评论事件
            if(isDel){
                $('#'+row.id).find('span[data-toggle-remove="'+row.id+'"]').off('click').on('click', function () {
                    opt.modal.confirm("请确认是否删除评论,数据将无法恢复!",function(){
                        that.showLoading();
                        var config = {
                            type: "POST",
                            url: baseURL + that.options.delUrl,
                            data: {'id':row.id},
                            success: function (result) {
                                if (result.code == '00000') {
                                    that.hideLoading();
                                    that.initCommentData();
                                } else if (result.code == '50001') {
                                    that.hideLoading();
                                    opt.modal.alertWarning(result.msg)
                                } else {
                                    that.hideLoading();
                                    opt.modal.alertError(result.msg);
                                }
                            }
                        }
                        if(opt.common.isNotEmpty($('meta[name="csrf-token"]').attr("content"))){
                            config = opt.common.extend(config,{headers: {
                                    "X-CSRF-Token": $('meta[name="csrf-token"]').attr("content")
                                }});
                        }
                        $.ajax(config);
                    })
                })
            }

            if(row.reply){ //有子节点
                that.commentList(row.children,true, level);
            }
        });
    }

    CommentsData.prototype.showLoading = function () {
        this.$comment_wrap_box_loading.show();
        this.$comments_box.css('visibility','hidden');
    };

    CommentsData.prototype.hideLoading = function () {
        this.$comment_wrap_box_loading.hide();
        this.$comments_box.css('visibility','visible');
    };

    CommentsData.prototype.onCancel = function(event){
        if(this.$footer_wrap.hasClass('active')){
            this.$footer_wrap.removeClass('active');
        }
    }

    //提交发布消息
    CommentsData.prototype.onPublish = function(event){
        var that = this;
        this.showLoading();
        var content = this.$textarea_wrap.find('textarea').val();
        var config = {
            type: "POST",
            url: baseURL + this.options.pushURL,
            data: {'userId':this.options.userId,'content':content,'msgId':this.options.msgId,'replyId':0},
            success: function (result) {
                if (result.code == '00000') {
                    that.$textarea_wrap.find('textarea').val('');
                    if(!that.$actions.find('div.btn-publish').hasClass('disabled')){
                        that.$actions.find('div.btn-publish').addClass(('disabled'));
                    }
                    that.hideLoading();
                    that.initCommentData();
                } else if (result.code == '50001') {
                    that.hideLoading();
                    opt.modal.alertWarning(result.msg)
                } else {
                    that.hideLoading();
                    opt.modal.alertError(result.msg);
                }
            }
        }

        if(opt.common.isNotEmpty($('meta[name="csrf-token"]').attr("content"))){
            config = opt.common.extend(config,{headers: {
                    "X-CSRF-Token": $('meta[name="csrf-token"]').attr("content")
                }});
        }
        $.ajax(config);
    }

    //回复事件方法
    CommentsData.prototype.onReplyPublish = function(event){
        var that = this,
            id = $(event.currentTarget).data('id');
        var content = $('#'+id).find('div[data-comment-id="reply_'+id+'"]').find('.field >textarea ').val();
        var config = {
            type: "POST",
            url: baseURL + that.options.pushURL,
            data: {'userId':that.options.userId,'content':content,'msgId':this.options.msgId,'replyId':id},
            success: function (result) {
                if (result.code == '00000') {
                    $('#__replyCommentForm').remove();
                    that.hideLoading();
                    that.initCommentData();
                } else if (result.code == '50001') {
                    that.hideLoading();
                    opt.modal.alertWarning(result.msg)
                } else {
                    that.hideLoading();
                    opt.modal.alertError(result.msg);
                }
            }
        }

        if(opt.common.isNotEmpty($('meta[name="csrf-token"]').attr("content"))){
            config = opt.common.extend(config,{headers: {
                    "X-CSRF-Token": $('meta[name="csrf-token"]').attr("content")
                }});
        }
        $.ajax(config);
    }

    CommentsData.prototype.initReply = function(id,center){
        $('#__replyCommentForm').remove();
        var html = ['<div class="comment-item__reply-form" id="__replyCommentForm">',
                '<div  data-reply-comment-form="" data-comment-id="reply_'+id+'">',
                '<form class="ui reply form comment-form">',
                '<div class="field">',
                '<textarea rows="4" placeholder="'+center+'" maxlength="1000" class="disabled-resize"></textarea>',
                '</div>',
                '<div class="field foot-bar">',
                '<div class="btn-close">'+this.options.formatCancel()+'</div>',
                '<div class="btn-submit ui primary button right floated disabled" data-id="'+id+'">'+this.options.formatCommentReply()+'</div>',
                '</div>',
                '</form>',
                '</div>',
                '</div>'].join('');
        return html;
    }

    //回复消息
    CommentsData.prototype.onReply = function(event){
        console.log(this);
        var id = $(event.currentTarget).data('id');

        this.initReply(id);
    }




    //初始化本地语言
    CommentsData.prototype.initLocale = function () {
        if (this.options.locale) {
            var parts = this.options.locale.split(/-|_/);
            parts[0].toLowerCase();
            if (parts[1]) parts[1].toUpperCase();
            if ($.fn.comments.locales[this.options.locale]) {
                // locale as requested
                $.extend(this.options, $.fn.comments.locales[this.options.locale]);
            } else if ($.fn.comments.locales[parts.join('-')]) {
                // locale with sep set to - (in case original was specified with _)
                $.extend(this.options, $.fn.comments.locales[parts.join('-')]);
            } else if ($.fn.comments.locales[parts[0]]) {
                // short locale language code (i.e. 'en')
                $.extend(this.options, $.fn.comments.locales[parts[0]]);
            }
        }
    };

    CommentsData.LOCALES = {};

    CommentsData.LOCALES['zh-CN'] = CommentsData.LOCALES.zh = {
        formatLoadingMessage: function () {
            return '努力加载中,请稍后';
        },
        formatCancel: function () {
            return '取消';
        },
        formatRelease: function () {
            return '发布';
        },
        formatCommentTitle: function (totalRows) {
           return sprintf('最新评论(%s)',  totalRows);
        },
        formatCommentReply: function () {
            return '回复';
        },
        formatDelCommentReply: function () {
            return '删除';
        },
        formatCommentMore: function () {
            return '更多评论';
        }
    };

    $.extend(CommentsData.DEFAULTS, CommentsData.LOCALES['zh-CN']);


    CommentsData.DEFAULTS = {
        id: '',                    // 加载容器id
        userId: _userId,           // 当前评论用户id
        msgId: '',                 //消息ID
        locale: 'zh-CN',           //
        sortName: "createTime",    // 排序字段
        sortOrder: "desc",         // 默认降序
        totalRows: 0,              // 总共条数
        pageNumber: 1,             // 当前页
        pageSize: 6,               // 一页条数
        onlyInfoPagination: false,  //
        pageList: [6, 12, 18],     // 分页数据库
        code: 'code',              // 选取记录返回的值,用于设置父子关系
        isMore : true,             // 是否开启更多隐藏
        rootIdValue: '0',          // 设置根节点id值----可指定根节点，默认为null,"",0,"0"
        data: [],                  // 数据缓存
        type: "POST",               // 请求数据的ajax类型
        pushURL: 'sys/msg/addCommets', // 请求数据的ajax的url
        listURL: 'sys/msg/commets/list', // 请求数据的ajax的url
        delUrl: 'sys/msg/commets/del', //删除评论
        title: '评论留言',           // 评论回复标题 评论留言
        titleIcon: 'fa fa-fw fa-comments-o',// 标题图标
        paginationPreText: '&lsaquo;',
        paginationNextText: '&rsaquo;',
        responseHandler: function(res) {
            return res;
        }
    }

    var allowedMethods = [
        'onPublish'
    ];

    $.fn.comments = function (option) {
        var value,
            args = Array.prototype.slice.call(arguments, 1);

        this.each(function () {
            var $this = $(this),
                data = $this.data('comments.data'),
                options = $.extend({}, CommentsData.DEFAULTS, $this.data(),
                    typeof option === 'object' && option);
            if (typeof option === 'string') {
                if ($.inArray(option, allowedMethods) < 0) {
                    throw new Error("Unknown method: " + option);
                }

                if (!data) {
                    return;
                }

                value = data[option].apply(data, args);

                if (option === 'destroy') {
                    $this.removeData('comments.data');
                }
            }

            if (!data) {
                $this.data('comments.data', (data = new CommentsData(this, options)));
            }
        });
        return typeof value === 'undefined' ? this : value;
    };

    $.fn.comments.Constructor = CommentsData;
    $.fn.comments.defaults = CommentsData.DEFAULTS;
    $.fn.comments.locales = CommentsData.LOCALES;
    $.fn.comments.utils = {
        sprintf: sprintf
    };

    $(function () {
        $('[data-toggle="comment"]').comments();
    });

})(jQuery);