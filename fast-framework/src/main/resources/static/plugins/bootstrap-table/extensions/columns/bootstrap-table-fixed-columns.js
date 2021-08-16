/**
 * 表格冻结
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 * @author J2eeFAST
 * @date 2021-03-21
 */
(function ($) {
    'use strict';

    /**
     * 获取冻结项数据
     * @param options 表格对象 options
     * @param $this 表格对象
     * @param number 冻结列数
     * @param fixed 冻结方向 true 左 false 右
     */
    var getFixedColimns = function(options, $this, number,fixed){
        var html = [],
            tag = [],_u = 0,
            column= $this.columns.length,//一共多少列
            row = options.columns.length;// 一共多少行
        for (var i = 0; i < number; i++) {
            var hq = fixed ? (i + 1) : column - i;
            tag[i] = hq;
        }
        if(row == 1){
            for(var i = 0; i< options.columns[0].length; i++){
                if(typeof options.columns[0][i].visible !== 'undefined' && !options.columns[0][i].visible){
                    var _T = 0;
                    for (var k = 0; k < tag.length; k++) {
                        if(tag[k] >= i){
                            _T++;
                            break;
                        }
                    }
                    if(_T > 0){
                        _u++;
                    }
                }
            }
        }
        // console.log("_u" +_u);
        var store = new Array();
        // console.log("一共" +column +"列");
        // console.log("一共" +row +"行");
        for (var i = 0; i < number; i++) {
            var hq = fixed ? (i + 1) : column - i;
            //console.log(" 当前:" + hq);
            var s = '';
            for (var j = 0; j < row; j++) {
                store[j] = new Array();
                if (options.columns[j].length != 1) {
                    var o = 0;
                    for (var k = 0; k < options.columns[j].length; k++) {
                        if (options.columns[j][k].colspan) {
                            o += options.columns[j][k].colspan;
                        } else {
                            if (s.length > 0) {
                                var bb = s.substr(0, s.length - 1).split('#');
                                var m = o;
                                for (var q = 0; q < bb.length; q++) {
                                    if (j > parseInt(bb[q].split('=')[0]) && j <= parseInt(bb[q].split('=')[1])) {
                                        // console.log('++++' + parseInt(bb[q].split('=')[2]));
                                        var oo = parseInt(bb[q].split('=')[0]) + parseInt(bb[q].split('=')[1]) + parseInt(bb[q].split('=')[2]);
                                        //判断是否有下个且前面有合并占用的列就添加一列
                                        if ((m + 1) >= parseInt(bb[q].split('=')[2]) &&
                                            !store[j][oo] && (k + 1) < options.columns[j].length) {
                                            m++;
                                            store[j][oo] = parseInt(bb[q].split('=')[2]);
                                        }
                                        // console.log(k + '-->' + m);
                                        if (!html[i] &&
                                            m == hq && !options.columns[j][k].colspan) {
                                            html[i] = $this.$header.find('tr:eq(' + j + ')').find('th:eq(' + (k-_u) + ')').clone(true);
                                            if (row > 1) html[i].height($this.$header.height());
                                            if (row > 1) html[i].attr('rowspan', row);
                                            if (row > 1) html[i].css('vertical-align', 'inherit');
                                            // console.log('检查到' + i + '---HS:' + j + "K:" + k + " html:" + html[i].html() + " row:" + row);
                                            break;
                                        }
                                    }
                                }
                                o = m;
                            }
                            o++;
                            if (!html[i] && (o == hq &&
                                options.columns[j][k].rowspan &&
                                (options.columns[j][k].rowspan + j) == row)
                                || (o == hq && (j + 1) == row)) {
                                html[i] = $this.$header.find('tr:eq(' + j + ')').find('th:eq(' + (k-_u) + ')').clone(true);
                                if (row > 1) html[i].height($this.$header.height());
                                if (row > 1) html[i].attr('rowspan', row);
                                if (row > 1) html[i].css('vertical-align', 'inherit');
                                // console.log('检查到' + i + '---HS:' + j + "K:" + k + " html:" + html[i].html() + " hs:" + row);
                            }
                        }

                        if (options.columns[j][k].rowspan) {
                            s += j + '=' + (j + parseInt(options.columns[j][k].rowspan) - 1) + '=' + o + '#';
                        }
                        // console.log(s)
                        if (s.length > 0) {
                            var bb = s.substr(0, s.length - 1).split('#');
                            var m = o;
                            for (var q = 0; q < bb.length; q++) {
                                if (j > parseInt(bb[q].split('=')[0]) &&
                                    j <= parseInt(bb[q].split('=')[1])) {
                                    // console.log('++++' + parseInt(bb[q].split('=')[2]));
                                    var oo = parseInt(bb[q].split('=')[0]) + parseInt(bb[q].split('=')[1]) + parseInt(bb[q].split('=')[2]);
                                    //判断是否有下个且前面有合并占用的列就添加一列
                                    if ((m + 1) >= parseInt(bb[q].split('=')[2]) &&
                                        !store[j][oo] && (k + 1) < options.columns[j].length) {
                                        m++;
                                        store[j][oo] = parseInt(bb[q].split('=')[2]);
                                    }
                                    // console.log(k + '-->' + m);
                                    if (!html[i] && m == hq &&
                                        !options.columns[j][k].colspan) {
                                        html[i] = $this.$header.find('tr:eq(' + j + ')').find('th:eq(' + (k-_u) + ')').clone(true);
                                        if (row > 1) html[i].height($this.$header.height());
                                        if (row > 1) html[i].attr('rowspan', row);
                                        if (row > 1) html[i].css('vertical-align', 'inherit');
                                        // console.log('检查到' + i + '---HS:' + j + "K:" + k + " html:" + html[i].html() + " hs:" + row);
                                        break;
                                    }
                                }
                            }
                            o = m;
                        }
                    }
                }
            }
        }
        var $html = $this.$header.find('tr:eq(0)').clone(true);
        $html.html('');
        if(fixed){
            for(var i= 0; i < html.length; i++){
                $html.append(html[i]);
            }
        }else{
            for(var i= html.length -1; i >=0; i--){
                $html.append(html[i]);
            }
        }
        // console.log('------------ 结束----');
        return $html;
    };

    $.extend($.fn.bootstrapTable.defaults, {
        fixedColumns: false,
        fixedNumber: 1,
        rightFixedColumns: false,
        rightFixedNumber: 1
    });

    var BootstrapTable = $.fn.bootstrapTable.Constructor,
        _initHeader = BootstrapTable.prototype.initHeader,
        _initBody = BootstrapTable.prototype.initBody,
        _resetView = BootstrapTable.prototype.resetView;

    BootstrapTable.prototype.initFixedColumns = function () {
        this.timeoutHeaderColumns_ = 0;
        this.timeoutBodyColumns_ = 0;

        if(this.options.cardView){
            if(this.options.fixedColumns){
                if($("#"+this.options.id + "-left-fixed-table-columns")){
                    $("#"+this.options.id + "-left-fixed-table-columns").hide();
                }
            }
            if(this.options.rightFixedColumns){
                if($("#"+this.options.id + "-right-fixed-table-columns")){
                    $("#"+this.options.id + "-right-fixed-table-columns").hide();
                }
            }
            return;
        }
        if (this.options.fixedColumns) {
            var $leftFixedHeader = this.$tableContainer.find('.left-fixed-table-columns');
            if(!$leftFixedHeader.length){
                this.$fixedBody = $([
                    '<div id="'+this.options.id+'-left-fixed-table-columns" class="left-fixed-table-columns">',
                        '<div class="fixed-table-header">',
                            '<table>',
                                '<thead></thead>',
                            '</table>',
                        '</div>',
                        '<div class="fixed-table-body">',
                            '<table>',
                                // '<thead></thead>',
                                '<tbody style="background-color: #fff"></tbody>',
                            '</table>',
                        '</div>',
                    '</div>'].join(''));

                this.$fixedBody.find('table').attr('class', this.$el.attr('class'));
                this.$fixedHeaderColumns = this.$fixedBody.find('.fixed-table-header').find('thead');
                this.$fixedBodyColumns = this.$fixedBody.find('.fixed-table-body').find('tbody');
                this.$tableBody.before(this.$fixedBody);
            }else{
                $("#"+this.options.id + "-left-fixed-table-columns").show();
            }
        }
        if (this.options.rightFixedColumns) {
            var $rightFixedHeader = this.$tableContainer.find('.right-fixed-table-columns');
            if(!$rightFixedHeader.length){
                this.$rightfixedBody = $([
                    '<div id="'+this.options.id+'-right-fixed-table-columns" class="right-fixed-table-columns">',
                    '<div class="fixed-table-header">',
                    '<table>',
                    '<thead></thead>',
                    '</table>',
                    '</div>',
                    '<div class="fixed-table-body">',
                    '<table>',
                    // '<thead></thead>',
                    '<tbody style="background-color: #fff"></tbody>',
                    '</table>',
                    '</div>',
                    '</div>'].join(''));
                this.$rightfixedBody.find('table').attr('class', this.$el.attr('class'));
                this.$rightfixedHeaderColumns = this.$rightfixedBody.find('.fixed-table-header').find('thead');
                this.$rightfixedBodyColumns = this.$rightfixedBody.find('.fixed-table-body').find('tbody');
                this.$tableBody.before(this.$rightfixedBody);
                if (this.options.fixedColumns) {
                    $('.right-fixed-table-columns').attr('style','right:0px;');
                }
            }else{
                $("#"+this.options.id + "-right-fixed-table-columns").show();
            }
        }
    };

    BootstrapTable.prototype.initHeader = function () {

        _initHeader.apply(this, Array.prototype.slice.apply(arguments));

        if (!this.options.fixedColumns && !this.options.rightFixedColumns){
            return;
        }

        this.initFixedColumns();

        if(this.options.cardView){
            return;
        }

        var that = this;

        //右边列冻结
        if (that.options.rightFixedColumns) {
            that.$rightfixedHeaderColumns.html('').
            append(getFixedColimns(that.options,that,that.options.rightFixedNumber,false));
        }


        //左边列冻结
        if (that.options.fixedColumns) {
            var $ltr = getFixedColimns(that.options,that,that.options.fixedNumber,true);
            that.$fixedHeaderColumns.html('').append($ltr);
            that.$selectAll = $ltr.find('[name="btSelectAll"]');
            that.$selectAll.on('click', function () {
                var checked = $(this).prop('checked');
                that.$fixedBodyColumns.find("input[name=btSelectItem]").filter(':enabled').prop('checked', checked);
                that.$fixedBodyColumns.find("input[name=btSelectItem]").closest('tr')[checked ? 'addClass' : 'removeClass']('selected');
                if (that.options.rightFixedColumns) {
                    that.$rightfixedBody.find('tr').each(function () {
                        checked  ? $(this).addClass('selected') : $(this).removeClass('selected');
                    })
                }
            });
        }

    };

    BootstrapTable.prototype.initBody = function () {

        _initBody.apply(this, Array.prototype.slice.apply(arguments));

        if (!this.options.fixedColumns && !this.options.rightFixedColumns) {
            return;
        }

        if(this.options.cardView){
            return;
        }

        var that = this;
        if (this.options.fixedColumns) {
            //页面新增左侧表格内容
            this.$fixedBodyColumns.html('');
            this.$body.find('> tr[data-index]').each(function () {
                var $that = $(this).find('td');
                var $tr = $(this).clone(true),
                    $tds = $tr.clone(true).find('td');
                $tr.html('');
                var hs = 0;
                for (var i = 0; i < that.options.fixedNumber; i++) {
                    var $ftd = $tds.eq(i).clone(true);
                    if($tds.eq(i).attr('colspan')){
                        hs += parseInt($tds.eq(i).attr('colspan'));
                    }else{
                        hs ++;
                    }
                    if(hs == that.options.fixedNumber){
                        $tr.append($ftd);
                        $that.eq(i).css("visibility" ,"hidden");
                        break;
                    }
                    if(hs > that.options.fixedNumber){
                        $tr.append($ftd.html('').removeAttr('colspan'));
                        break;
                    }else{
                        $tr.append($ftd);
                        $that.eq(i).css("visibility" ,"hidden");
                    }
                }
                that.$fixedBodyColumns.append($tr);
            });
        }
        if (this.options.rightFixedColumns) {
            //页面新增右侧表格内容
            this.$rightfixedBodyColumns.html('');
            this.$body.find('> tr[data-index]').each(function () { //遍历行
                // var $thisTr = $(this);
                // var $tr = $(this).clone(true), //当前行
                //     $tds = $tr.clone(true).find('td'), //列
                //     $thisTds =  $thisTr.find('td');
                // $tr.html('');

                var $that = $(this).find('td');
                var $tr = $(this).clone(true),
                    $tds = $tr.clone(true).find('td');
                $tr.html('');


                // 判断body 行中的列是否有合并如果有合并就减少冻结数量
                var ll = 0, nn = 0;
                for (var i = 0; i < that.options.rightFixedNumber; i++) {
                    var indexTd = $that.length - i -1;
                    var c = $tds.eq(indexTd).attr('colspan');
                    if(typeof c !== 'undefined' && c && parseInt(c,10) > 0){
                        ll = ll + parseInt(c,10);
                    }
                    nn++;
                    if(ll >= that.options.rightFixedNumber){
                        break;
                    }
                    ll++;
                }
                for (var i = 0; i < nn; i++) {
                    var indexTd = $that.length - nn + i;
                    var fixTd = $tds.eq(indexTd).clone(true);
                    $tr.append(fixTd);
                    $that.eq(indexTd).css("visibility" ,"hidden");
                }
                that.$rightfixedBodyColumns.append($tr);
            });
        }
    };

    BootstrapTable.prototype.resetView = function () {

        _resetView.apply(this, Array.prototype.slice.apply(arguments));

        if (!this.options.fixedColumns && !this.options.rightFixedColumns) {
            return;
        }

        if(this.options.cardView){
            // $("#"  + that.options.id).removeAttr("style");
            // console.log("--->>"+$("#"  + that.options.id).attr("style"))
            return;
        }

        clearTimeout(this.timeoutHeaderColumns_);
        this.timeoutHeaderColumns_ = setTimeout($.proxy(this.fitHeaderColumns, this), this.$el.is(':hidden') ? 1000 : 100);

        clearTimeout(this.timeoutBodyColumns_);
        this.timeoutBodyColumns_ = setTimeout($.proxy(this.fitBodyColumns, this), this.$el.is(':hidden') ? 1000 : 100);
    };

    BootstrapTable.prototype.fitHeaderColumns = function () {
        var that = this,
            headerWidth = 0;
        if (that.options.fixedColumns) {
            if(this.$body.find('tr:eq(0)').find('td').length == 1){
                this.$fixedBody.hide();
            }else{

                this.$body.find('tr:eq(0)').find('td').each(function (i) {
                    for(var k=0; k<that.options.fixedNumber; k++){
                        if(i == k){
                            headerWidth += $(this).outerWidth();
                        }
                    }
                });

                if (this.options.height) {
                    var height = this.$tableBody.height();
                    this.$fixedBody.find('.fixed-table-body').css('height',height+'px');
                }

                //this.$fixedBody.width(headerWidth).show();
            }
        }
        if (that.options.rightFixedColumns) {
            headerWidth = 0;
            var totalLength = this.$body.find('tr:eq(0)').find('td').length;
            if(totalLength == 1){
                this.$rightfixedBody.hide();
            }else{
                this.$body.find('tr:eq(0)').find('td').each(function (i) {
                    for(var k=0; k<that.options.rightFixedNumber; k++){
                        if(i == (totalLength -k -1)){
                            headerWidth += parseInt($(this).outerWidth(),10);
                        }
                    }
                });

                this.$rightfixedBody.css('top','0px');

                if (this.options.height) {
                    var height = this.$tableBody.height();
                    this.$rightfixedBody.find('.fixed-table-body').css('height',height+'px');
                }

                this.$header.find('tr:eq(0)').find('th').each(function(i){
                    for(var k=0; k<that.options.rightFixedNumber; k++){
                        if(i == (totalLength -k -1)){
                            var width = $(this).innerWidth();
                            var field  = $(this).data('field');

                            var $th = that.$rightfixedHeaderColumns.find('th[data-field="'+field+'"]');
                            if( i == (totalLength -1)){
                                $th.find('.fht-cell').width(width + 5);
                            }else{
                                $th.find('.fht-cell').width(width);
                            }
                        }
                    }
                });

                this.$rightfixedBody.width(headerWidth + 5).show();
            }

        }
    };

    BootstrapTable.prototype.fitBodyColumns = function () {
        var that = this,
            height = this.$tableBody.height(),
            width = 0;


        //添加行事件
        this.$body.find('> tr[data-index]').off('hover').hover(function () {
            var index = $(this).data('index');
            if (that.options.fixedColumns) that.$fixedBody.find('tr[data-index="' + index + '"]').addClass('hover');
            if (that.options.rightFixedColumns) that.$rightfixedBody.find('tr[data-index="' + index + '"]').addClass('hover');
        }, function () {
            var index = $(this).data('index');
            if (that.options.fixedColumns) that.$fixedBody.find('tr[data-index="' + index + '"]').removeClass('hover');
            if (that.options.rightFixedColumns) that.$rightfixedBody.find('tr[data-index="' + index + '"]').removeClass('hover');
        });

        if (that.options.fixedColumns) {

            var visibleFields = that.getVisibleFields();

            if (!this.$body.find('> tr[data-index]').length) {
                this.$fixedBody.hide();
                return;
            }

            //
            this.$body.find('tr:eq(0)').find('td').each(function (i) {
                for (var k = 0; k < that.options.fixedNumber; k++) {
                    if(i == k){
                        width += $(this).outerWidth();
                        break;
                    }
                }
            });


            this.$fixedBody.css({
                //width: width,
                height: height,
                top: '-1px'
            }).show();

            // 设置冻结内容宽度
            // this.$header.find('tr:eq(0)').find('th').each(function(i){
            //     for (var k = 0; k < that.options.fixedNumber; k++) {
            //         if(i == k){
            //             var width = $(this).outerWidth();
            //             var field  = $(this).data('field');
            //             var $th = that.$fixedHeaderColumns.find('th[data-field="'+field+'"]');
            //             $th.find('.fht-cell').width(width);
            //             that.$fixedBodyColumns.find('tr:eq(0)').find('td:eq('+k+')').css({width:width});
            //             break;
            //         }
            //     }
            // });
            this.$body.find('>tr:first-child:not(.no-records-found) > *').each(function (i) {
                var $this = $(this),
                    index = i;
                for (var k = 0; k < that.options.fixedNumber; k++) {
                    if(i == k){
                        var width = $this.innerWidth();
                        var $th = that.$fixedHeaderColumns.find('th[data-field="'+visibleFields[index]+'"]');
                        $th.find('.fht-cell').width(width);
                        that.$fixedBodyColumns.find('tr:eq(0)').find('td:eq('+k+')').css({width:width});
                        break;
                    }
                }
            });

            this.$body.find('> tr').each(function (i) {
                that.$fixedBody.find('tr:eq(' + i + ')').height($(this).outerHeight());
            });

            this.$fixedBody.find('tr[data-index]').off('hover').hover(function () {
                var index = $(this).data('index');
                that.$body.find('tr[data-index="' + index + '"]').addClass('hover');
                if (that.options.rightFixedColumns) that.$rightfixedBody.find('tr[data-index="' + index + '"]').addClass('hover');
            }, function () {
                var index = $(this).data('index');
                that.$body.find('> tr[data-index="' + index + '"]').removeClass('hover');
                if (that.options.rightFixedColumns)  that.$rightfixedBody.find('tr[data-index="' + index + '"]').removeClass('hover');
            });


            if(that.options.rightFixedColumns){
                //获取点击
                this.$fixedBodyColumns.find('tr').each(function () {
                    $(this).find('input[name="btSelectItem"]').each(function(){
                        $(this).on('click', function () {
                            var index = $(this).data('index');
                            var checked = $(this).prop('checked');
                            that.$rightfixedBody.find('tr[data-index="'+index+'"]').each(function () {
                                checked  ? $(this).addClass('selected') : $(this).removeClass('selected');
                            })
                        });
                    })
                })
            }

        }



        if (that.options.rightFixedColumns) {

            if (!this.$body.find('> tr[data-index]').length) {
                this.$rightfixedBody.hide();
                return;
            }

            this.$body.find('> tr').each(function (i) {
                that.$rightfixedBody.find('tbody tr:eq(' + i + ')').height($(this).height());
            });

            this.$rightfixedBody.find('tr[data-index]').off('hover').hover(function () {
                var index = $(this).data('index');
                that.$body.find('tr[data-index="' + index + '"]').addClass('hover');
                if (that.options.fixedColumns) that.$fixedBody.find('tr[data-index="' + index + '"]').addClass('hover');
            }, function () {
                var index = $(this).data('index');
                that.$body.find('> tr[data-index="' + index + '"]').removeClass('hover');
                if (that.options.fixedColumns) that.$fixedBody.find('tr[data-index="' + index + '"]').removeClass('hover');
            });
        }


        // //是否点击选择行
        if(this.options.clickToSelect){

            this.$body.find('> tr[data-index] > td').off('click dblclick').on('click dblclick', function (e) {
                var $td = $(this),
                    $tr = $td.parent();
                var index = $tr.data('index');
                if (e.type === 'click') {
                    if(that.options.fixedColumns){
                        var tr = that.$fixedBody.find('tr[data-index="' + index + '"] >td')
                        var $selectItem = tr.find('[name="'+that.options.selectItemName+'"]');
                        if ($selectItem.length) {
                            $selectItem[0].click(); // #144: .trigger('click') bug
                        }
                    }else{
                        if (that.options.rightFixedColumns){
                            // that.$rightfixedBody.find('tr[data-index="' + index + '"]').toggleClass("selected");
                            var $selectItem = $tr.find('[name="'+that.options.selectItemName+'"]');
                            if ($selectItem.length) {
                                $selectItem[0].click(); // #144: .trigger('click') bug
                            }

                            if($tr.hasClass('selected')){
                                that.$rightfixedBody.find('tr[data-index="' + index + '"]').addClass("selected");
                            }else{
                                that.$rightfixedBody.find('tr[data-index="' + index + '"]').removeClass("selected");
                            }
                        }
                    }
                }
            })

            if(that.options.fixedColumns){
                this.$fixedBody.find('tr[data-index]').off('click dblclick').on('click dblclick', function (e) {
                    var index = $(this).data('index');
                    var th = $(this);
                    if (that.options.rightFixedColumns){
                        if(th.hasClass('selected')){
                            that.$rightfixedBody.find('tr[data-index="' + index + '"]').addClass("selected");
                        }else{
                            that.$rightfixedBody.find('tr[data-index="' + index + '"]').removeClass("selected");
                        }
                    }
                })
            }

            if(that.options.rightFixedColumns){
                this.$rightfixedBody.find('tr[data-index]').off('click dblclick').on('click dblclick', function (e) {
                    var index = $(this).data('index');
                    var tr = that.$body.find('tr[data-index="' + index + '"]')
                    var $selectItem = tr.find('[name="'+that.options.selectItemName+'"]');
                    if ($selectItem.length) {
                        $selectItem[0].click(); // #144: .trigger('click') bug
                    }
                })
            }
        }

        //固定高度 联动滚动
        if (this.options.height) {
            //// events
            this.$tableBody.on('scroll', function () {
                if (that.options.rightFixedColumns) that.$rightfixedBody.find('.fixed-table-body').scrollTop($(this).scrollTop());
                if (that.options.fixedColumns) that.$fixedBody.find('.fixed-table-body').scrollTop($(this).scrollTop());
            });

            if (that.options.rightFixedColumns){
                this.$rightfixedBody.find('.fixed-table-body').off('scroll').on('scroll', function () {
                    that.$tableBody.scrollTop($(this).scrollTop());
                })
            }
        }

    };


})(jQuery);