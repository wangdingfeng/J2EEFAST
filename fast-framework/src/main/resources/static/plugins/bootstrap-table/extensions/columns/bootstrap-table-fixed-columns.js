/**
 * @author zhixin wen <wenzhixin2010@gmail.com>
 * @version: v1.0.1
 * Modificated 16.08.16 by Aleksej Tokarev (Loecha)
 *  - Sorting Problem solved
 *  - Recalculated Size of fixed Columns
 */
/**
 * J2eeFAST 二次修改
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
            column= $this.columns.length,//一共多少列
            row = options.columns.length;// 一共多少行
        var store = new Array();

        for (var i = 0; i < number; i++) {
            var hq = fixed ? (i + 1) : column - i;
            // console.log(" 当前:" + hq);
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
                                            html[i] = $this.$header.find('tr:eq(' + j + ')').find('th:eq(' + (k) + ')').clone(true);
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
                                html[i] = $this.$header.find('tr:eq(' + j + ')').find('th:eq(' + k + ')').clone(true);
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
                                        html[i] = $this.$header.find('tr:eq(' + j + ')').find('th:eq(' + (k) + ')').clone(true);
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
                    '<table>',
                    '<thead></thead>',
                    '<tbody style="background-color: #fff"></tbody>',
                    '</table>',
                    '</div>'].join(''));

                this.$fixedBody.find('table').attr('class', this.$el.attr('class'));
                this.$fixedHeaderColumns = this.$fixedBody.find('thead');
                this.$fixedBodyColumns = this.$fixedBody.find('tbody');
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
                    '<table>',
                    '<thead></thead>',
                    '<tbody style="background-color: #fff"></tbody>',
                    '</table>',
                    '</div>'].join(''));
                this.$rightfixedBody.find('table').attr('class', this.$el.attr('class'));
                this.$rightfixedHeaderColumns = this.$rightfixedBody.find('thead');
                this.$rightfixedBodyColumns = this.$rightfixedBody.find('tbody');
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
            var $ltr = getFixedColimns(that.options,that,that.options.fixedNumber,true)
            that.$fixedHeaderColumns.html('').append($ltr);
            that.$selectAll = $ltr.find('[name="btSelectAll"]');
            that.$selectAll.on('click', function () {
                var checked = $(this).prop('checked');
                that.$fixedBodyColumns.find("input[name=btSelectItem]").filter(':enabled').prop('checked', checked);
                that.$fixedBodyColumns.find("input[name=btSelectItem]").closest('tr')[checked ? 'addClass' : 'removeClass']('selected');
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
                var $thisTr = $(this);
                var $tr = $(this).clone(true), //当前行
                    $tds = $tr.clone(true).find('td'), //列
                    $thisTds =  $thisTr.find('td');
                $tr.html('');
                for (var i = 0; i < that.options.rightFixedNumber; i++) {
                    var indexTd = $tds.length - that.options.rightFixedNumber + i;
                    var fixTd = $tds.eq(indexTd).clone(true);
                    $tr.append(fixTd);
                    $thisTds.eq(indexTd).css("visibility" ,"hidden");
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
        this.timeoutHeaderColumns_ = setTimeout($.proxy(this.fitHeaderColumns, this), this.$el.is(':hidden') ? 100 : 0);

        clearTimeout(this.timeoutBodyColumns_);
        this.timeoutBodyColumns_ = setTimeout($.proxy(this.fitBodyColumns, this), this.$el.is(':hidden') ? 100 : 0);
    };

    BootstrapTable.prototype.fitHeaderColumns = function () {
        var that = this,
            headerWidth = 0;
        if (that.options.fixedColumns) {
            this.$body.find('tr:eq(0)').find('td').each(function (i) {
                for(var k=0; k<that.options.fixedNumber; k++){
                    if(i == k){
                        headerWidth += $(this).outerWidth();
                    }
                }
            });
            this.$fixedBody.width(headerWidth).show();
        }
        if (that.options.rightFixedColumns) {
            headerWidth = 0;
            var totalLength = this.$body.find('tr:eq(0)').find('td').length;
            this.$body.find('tr:eq(0)').find('td').each(function (i) {
                for(var k=0; k<that.options.rightFixedNumber; k++){
                    if(i == (totalLength -k -1)){
                        headerWidth += $(this).outerWidth();
                    }
                }
            });
            this.$rightfixedBody.css('top','0px');
            this.$rightfixedBody.width(headerWidth).show();
        }
    };

    BootstrapTable.prototype.fitBodyColumns = function () {
        var that = this,
            height = this.$tableBody.height(),
            width = 0;
        if (that.options.fixedColumns) {
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
                width: width,
                height: height,
                top: '0px'
            }).show();

            this.$body.find('> tr').each(function (i) {
                that.$fixedBody.find('tr:eq(' + i + ')').height($(this).outerHeight());
            });
        }

        if (that.options.rightFixedColumns) {

            if (!this.$body.find('> tr[data-index]').length) {
                this.$rightfixedBody.hide();
                return;
            }

            this.$body.find('> tr').each(function (i) {
                that.$rightfixedBody.find('tbody tr:eq(' + i + ')').height($(this).height());
            });

            //// events
            this.$tableBody.on('scroll', function () {
                that.$rightfixedBody.find('table').css('top', -$(this).scrollTop());
            });

            this.$body.find('> tr[data-index]').off('hover').hover(function () {
                var index = $(this).data('index');
                that.$rightfixedBody.find('tr[data-index="' + index + '"]').addClass('hover');
            }, function () {
                var index = $(this).data('index');
                that.$rightfixedBody.find('tr[data-index="' + index + '"]').removeClass('hover');
            });
            this.$rightfixedBody.find('tr[data-index]').off('hover').hover(function () {
                var index = $(this).data('index');
                that.$body.find('tr[data-index="' + index + '"]').addClass('hover');
            }, function () {
                var index = $(this).data('index');
                that.$body.find('> tr[data-index="' + index + '"]').removeClass('hover');
            });
        }

        /* 页面大小变动适配*/
        var w = 0;
        if(that.options.columns.length == 1){
            for(var i=0; i<that.options.columns[0].length;i++ ){
                if(that.options.columns[0][i].width == undefined || that.options.columns[0][i].width == null || that.options.columns[0][i].width.length == 0){
                    w += 120;
                }else{
                    w += that.options.columns[0][i].width;
                }
            }
        }else{
            $.each(that.options.columns, function (i, columns) {
                $.each(columns, function (j, column) {
                    if(column.width &&  Object.prototype.toString.apply(column.width) == '[object Number]'){
                        w += column.width;
                    }
                })
            })
        }
        if($("#"  + that.options.id).outerWidth() < w){
            $("#"  + that.options.id).width(w+"px");
        }
        if($("#"  + that.options.id).parent(".fixed-table-body").outerWidth() > w){
            $("#"  + that.options.id).width($("#"  + that.options.id).parent(".fixed-table-body").outerWidth()+"px");
        }
    };


})(jQuery);