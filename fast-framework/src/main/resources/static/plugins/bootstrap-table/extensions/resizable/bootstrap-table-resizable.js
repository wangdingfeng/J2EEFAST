/**
 * @author: Dennis Hernández
 * @webSite: http://djhvscf.github.io/Blog
 * @version: v1.0.0
 *
 * J2eeFAST 二次修改
 */

(function ($) {
    'use strict';

    var initResizable = function (that) {
        //Deletes the plugin to re-create it
        that.$el.colResizable({disable: true});

        //Creates the plugin
        that.$el.colResizable({
            hoverCursor: that.options.hoverCursor,
            onResize: that.onResizableResize,
            onDrag: that.options.onResizableDrag,
            table: true
        });
    };

    $.extend($.fn.bootstrapTable.defaults, {
        timeoutHeaderColumns_: 0,
        resizable: false,
        liveDrag: false,
        fixed: true,
        headerOnly: false,
        minWidth: 15,
        hoverCursor: 'col-resize',
        dragCursor: 'e-resize',
        onResizableResize: function (e) {
            return false;
        },
        onResizableDrag: function (e) {
            return false;
        }
    });

    var BootstrapTable = $.fn.bootstrapTable.Constructor,
        _toggleView = BootstrapTable.prototype.toggleView,
        _resetView = BootstrapTable.prototype.resetView;

    BootstrapTable.prototype.toggleView = function () {
        _toggleView.apply(this, Array.prototype.slice.apply(arguments));

        if (this.options.resizable && this.options.cardView) {
            //Deletes the plugin
            $(this.$el).colResizable({disable: true});
        }
    };

    BootstrapTable.prototype.resetView = function () {
        var that = this;

        _resetView.apply(this, Array.prototype.slice.apply(arguments));

        if(this.options.cardView){
            return;
        }
        if (this.options.resizable) {
            // because in fitHeader function, we use setTimeout(func, 100);
            clearTimeout(that.timeoutHeaderColumns_);
            this.timeoutHeaderColumns_ = setTimeout(function () {
                initResizable(that);
            }, 100);
        }
    };

    // BootstrapTable.prototype.onResize = function (e) {
    //     // var that = $(e.currentTarget);
    //     // that.bootstrapTable('resetView');
    //     // that.data('bootstrap.table').options.onResizableResize.apply(e);
    //     console.log("---<<<<<<<<<<<<<<")
    // }
})(jQuery);
