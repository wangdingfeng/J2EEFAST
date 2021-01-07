/*! Verify&admin MIT License by anji-plus*/
/**
 * J2eeFAST 二次修改
 * 1.事件后处理
 * 2.兼容IE模式
 */
;(function($, window, document,undefined) {

	//请求图片get事件
	function getPictrue(data,baseUrl,resolve,reject){
		$.ajax({
			type : "post",
			contentType: "application/json;charset=UTF-8",
			url : baseUrl + "/captcha/get",
			data :JSON.stringify(data),
			success:function(res){
				resolve(res)
			},
			fail: function(err) {
				reject(err)
			}
		})
	}
	//验证图片check事件
	function checkPictrue(data,baseUrl,resolve,reject){
		$.ajax({
			type : "post",
			contentType: "application/json;charset=UTF-8",
			url : baseUrl + "/captcha/check",
			data :JSON.stringify(data),
			success:function(res){
				resolve(res)
			},
			fail: function(err) {
				reject(err)
			}
		})
	}

	//定义Slide的构造函数
	var Slide = function(ele, opt) {
		this.$element = ele,
			this.backToken = null,
			this.moveLeftDistance = 0,
			this.secretKey = '',
			this.defaults = {
				baseUrl:"https://mirror.anji-plus.com/captcha-api",
				containerId:'',
				captchaType:"blockPuzzle",
				mode : 'fixed',	//弹出式pop，固定fixed
				vOffset: 5,
				vSpace : 5,
				explain : '向右拖动滑块填充拼图',
				imgSize : {
					width: '360px',
					height: '155px',
				},
				blockSize : {
					width: '50px',
					height: '50px',
				},
				circleRadius: '10px',
				barSize : {
					width : '360px',
					height : '40px',
				},
				beforeCheck: function(){ return true},
				ready : function(){},
				success : function(){},
				error : function(){}

			},
			this.options = $.extend({}, this.defaults, opt)
	};


	//定义Slide的方法
	Slide.prototype = {
		init: function() {
			var _this = this;
			//加载页面
			this.loadDom();
			_this.refresh();
			this.options.ready();

			this.$element[0].onselectstart = document.body.ondrag = function(){
				return false;
			};

			if(this.options.mode == 'pop')	{

				_this.$element.find('.verifybox-close').on('click', function() {
					_this.$element.find(".mask").css("display","none");
					_this.refresh();
				});
				if(this.options.containerId != ''){
					var clickBtn = document.getElementById(this.options.containerId);
					clickBtn && (clickBtn.onclick = function(){
						if (_this.options.beforeCheck()) {
							_this.$element.find(".mask").css("display","block");
						}
					})
				}
			}

			//按下
			this.htmlDoms.move_block.on('touchstart', function(e) {
				_this.start(e);
			});

			this.htmlDoms.move_block.on('mousedown', function(e) {
				_this.start(e);
			});

			//拖动
			window.addEventListener("touchmove", function(e) {
				_this.move(e);
			});

			window.addEventListener("mousemove", function(e) {
				_this.move(e);
			});

			//鼠标松开
			window.addEventListener("touchend", function() {
				_this.end();
			});
			window.addEventListener("mouseup", function() {
				_this.end();
			});

			//刷新
			_this.$element.find('.verify-refresh').on('click', function() {
				var ele = $(this).find('img');
				var css = ele.css('transform');
				var deg;
				var step=90;
				if(css === 'none'){
					deg = 90;
				} else {
					var values = css.split('(')[1].split(')')[0].split(',');
					var a = values[0];
					var b = values[1];
					var c = values[2];
					var d = values[3];
					deg = _this.getmatrix(a,b,c,d);
					deg = deg+step;
				}
				ele.css({'transform':'rotate('+deg+'deg)'});
				_this.refresh();
			});
		},

		//初始化加载
		loadDom : function() {
			this.status = false;	//鼠标状态
			this.isEnd = false;		//是够验证完成
			this.setSize = this.resetSize(this);	//重新设置宽度高度
			this.plusWidth = 0;
			this.plusHeight = 0;
			this.x = 0;
			this.y = 0;
			var panelHtml = '';
			var wrapStartHtml = '';
			this.lengthPercent = (parseInt(this.setSize.img_width)-parseInt(this.setSize.block_width)- parseInt(this.setSize.circle_radius) - parseInt(this.setSize.circle_radius) * 0.8)/(parseInt(this.setSize.img_width)-parseInt(this.setSize.bar_height));

			wrapStartHtml += '<div class="mask">';
			wrapStartHtml += '<div class="verifybox" style="width:'+(parseInt(this.setSize.img_width)+30)+'px">';
			wrapStartHtml += '<div class="verifybox-top">';
			wrapStartHtml += '请完成安全认证';
			wrapStartHtml += '<span class="verifybox-close">';
			wrapStartHtml += '<i class="iconfont icon-close"></i>';
			wrapStartHtml += '</span>';
			wrapStartHtml += '</div>';
			wrapStartHtml += '<div class="verifybox-bottom" style="padding:10px 15px 15px 15px">';
			wrapStartHtml += '<div style="position: relative;">';

			if (this.options.mode == 'pop') {
				panelHtml = wrapStartHtml
			}

			panelHtml += '<div class="verify-img-out">';
			panelHtml += '<div class="verify-img-panel">';
			panelHtml += '<div class="verify-refresh" style="z-index:3">';
			panelHtml += '<img src="data:image/png;base64,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">';
			panelHtml += '</div>';
			panelHtml += '<span class="verify-tips"  class="suc-bg"></span>';
			panelHtml += ' <img src="" class="backImg" style="width:100%;height:100%;display:block">';
			panelHtml += '</div>';
			panelHtml += '</div>';

			this.plusWidth = parseInt(this.setSize.block_width) + parseInt(this.setSize.circle_radius) * 2 - parseInt(this.setSize.circle_radius) * 0.2;
			this.plusHeight = parseInt(this.setSize.block_height) + parseInt(this.setSize.circle_radius) * 2 - parseInt(this.setSize.circle_radius) * 0.2;

			panelHtml +='<div class="verify-bar-area" ' +
				'style="width:'+this.setSize.img_width+',height:'+this.setSize.bar_height+
				',line-height:'+this.setSize.bar_height+'"><span class="verify-msg">' +
				this.options.explain+'</span><div class="verify-left-bar"><span class="verify-msg">' +
				'</span><div  class="verify-move-block"><i  class="verify-icon iconfont icon-right">' +
				'</i><div class="verify-sub-block"><img src="" class="bock-backImg"' +
				' alt=""  style="width:100%;height:100%;display:block"></div></div></div></div>';
			wrapEndHtml = '</div></div></div></div>';
			if (this.options.mode == 'pop') {
				panelHtml += wrapEndHtml
			}

			this.$element.append(panelHtml);
			this.htmlDoms = {
				tips: this.$element.find('.verify-tips'),
				sub_block : this.$element.find('.verify-sub-block'),
				out_panel : this.$element.find('.verify-img-out'),
				img_panel : this.$element.find('.verify-img-panel'),
				img_canvas : this.$element.find('.verify-img-canvas'),
				bar_area : this.$element.find('.verify-bar-area'),
				move_block : this.$element.find('.verify-move-block'),
				left_bar : this.$element.find('.verify-left-bar'),
				msg : this.$element.find('.verify-msg'),
				icon : this.$element.find('.verify-icon'),
				refresh :this.$element.find('.verify-refresh')
			};

			this.$element.css('position', 'relative');

			this.htmlDoms.sub_block.css({'height':this.setSize.img_height,'width':Math.floor(parseInt(this.setSize.img_width)*47/310)+ 'px',
				'top':-(parseInt(this.setSize.img_height) + this.options.vSpace) + 'px'});
			this.htmlDoms.out_panel.css('height', parseInt(this.setSize.img_height) + this.options.vSpace + 'px');
			this.htmlDoms.img_panel.css({'width': this.setSize.img_width, 'height': this.setSize.img_height});
			this.htmlDoms.bar_area.css({'width': this.setSize.img_width, 'height': this.setSize.bar_height, 'line-height':this.setSize.bar_height});
			this.htmlDoms.move_block.css({'width': this.setSize.bar_height, 'height': this.setSize.bar_height});
			this.htmlDoms.left_bar.css({'width': this.setSize.bar_height, 'height': this.setSize.bar_height});
		},


		//鼠标按下
		start: function(e) {
			if(!e.touches) {    //兼容移动端
				var x = e.clientX;
			}else {     //兼容PC端
				var x = e.touches[0].pageX;
			}
			this.startLeft = Math.floor(x - this.htmlDoms.bar_area[0].getBoundingClientRect().left);
			this.startMoveTime = new Date().getTime();
			if(this.isEnd == false) {
				this.htmlDoms.msg.text('');
				this.htmlDoms.move_block.css('background-color', '#337ab7');
				this.htmlDoms.left_bar.css('border-color', '#337AB7');
				this.htmlDoms.icon.css('color', '#fff');
				e.stopPropagation();
				this.status = true;
			}
		},

		//鼠标移动
		move: function(e) {
			if(this.status && this.isEnd == false) {
				if(!e.touches) {    //兼容移动端
					var x = e.clientX;
				}else {     //兼容PC端
					var x = e.touches[0].pageX;
				}
				var bar_area_left = this.htmlDoms.bar_area[0].getBoundingClientRect().left;
				var move_block_left = x - bar_area_left; //小方块相对于父元素的left值
				if(move_block_left >= (this.htmlDoms.bar_area[0].offsetWidth - parseInt(this.setSize.bar_height) + parseInt(parseInt(this.setSize.block_width)/2) - 2) ) {
					move_block_left = (this.htmlDoms.bar_area[0].offsetWidth - parseInt(this.setSize.bar_height) + parseInt(parseInt(this.setSize.block_width)/2)- 2);
				}
				if(move_block_left <= parseInt(parseInt(this.setSize.block_width)/2)) {
					move_block_left = parseInt(parseInt(this.setSize.block_width)/2);
				}
				//拖动后小方块的left值
				this.htmlDoms.move_block.css('left', move_block_left-this.startLeft + "px");
				this.htmlDoms.left_bar.css('width', move_block_left-this.startLeft + "px");
				this.htmlDoms.sub_block.css('left', "0px");
				this.moveLeftDistance = move_block_left - this.startLeft
			}
		},

		//鼠标松开
		end: function() {
			this.endMovetime = new Date().getTime();
			var _this = this;
			//判断是否重合
			if(this.status  && this.isEnd == false) {
				var vOffset = parseInt(this.options.vOffset);
				this.moveLeftDistance = this.moveLeftDistance * 310/ parseInt(this.setSize.img_width);
				//图片滑动
				var data = {
					captchaType:this.options.captchaType,
					"pointJson": this.secretKey ? aesEncrypt(JSON.stringify({x:this.moveLeftDistance,y:5.0}),this.secretKey):JSON.stringify({x:this.moveLeftDistance,y:5.0}),
					"token":this.backToken
				};
				var captchaVerification = this.secretKey ? aesEncrypt(this.backToken+'---'+JSON.stringify({x:this.moveLeftDistance,y:5.0}),this.secretKey):this.backToken+'---'+JSON.stringify({x:this.moveLeftDistance,y:5.0});
				checkPictrue(data,this.options.baseUrl, function resolve(res){
					// 请求反正成功的判断
					if (res.repCode=="0000") {
						_this.htmlDoms.move_block.css('background-color', '#5cb85c');
						_this.htmlDoms.left_bar.css({'border-color': '#5cb85c', 'background-color': '#fff'});
						_this.htmlDoms.icon.css('color', '#fff');
						_this.htmlDoms.icon.removeClass('icon-right');
						_this.htmlDoms.icon.addClass('icon-check');
						//提示框
						_this.htmlDoms.tips.addClass('suc-bg').removeClass('err-bg');
						_this.htmlDoms.tips.css({"display":"block",animation:"move 1s cubic-bezier(0, 0, 0.39, 1.01)"});
						_this.htmlDoms.tips.text(((_this.endMovetime-_this.startMoveTime)/1000).toFixed(2) + 's验证成功');
						_this.isEnd = true;
						setTimeout(function(){
							_this.$element.find(".mask").css("display","none");
							_this.htmlDoms.tips.css({"display":"none",animation:"none"});
							_this.refresh();
						},1000);
						_this.options.success({'__captchaVerification':captchaVerification});
					}else{
						_this.htmlDoms.move_block.css('background-color', '#d9534f');
						_this.htmlDoms.left_bar.css('border-color', '#d9534f');
						_this.htmlDoms.icon.css('color', '#fff');
						_this.htmlDoms.icon.removeClass('icon-right');
						_this.htmlDoms.icon.addClass('icon-close');

						_this.htmlDoms.tips.addClass('err-bg').removeClass('suc-bg');
						_this.htmlDoms.tips.css({"display":"block",animation:"move 1.3s cubic-bezier(0, 0, 0.39, 1.01)"});
						_this.htmlDoms.tips.text(res.repMsg);
						setTimeout(function () {
							_this.refresh();
						}, 400);

						setTimeout(function (){
							_this.htmlDoms.tips.css({"display":"none",animation:"none"});
						},1300);
						_this.options.error(this);
					}
				});
				this.status = false;
			}
		},

		resetSize : function(obj) {
			var img_width,img_height,bar_width,bar_height,block_width,block_height,circle_radius;	//图片的宽度、高度，移动条的宽度、高度
			var parentWidth = obj.$element.parent().width() || $(window).width();
			var parentHeight = obj.$element.parent().height() || $(window).height();

			if(obj.options.imgSize.width.indexOf('%')!= -1){
				img_width = parseInt(obj.options.imgSize.width)/100 * parentWidth + 'px';
			}else {
				img_width = obj.options.imgSize.width;
			}

			if(obj.options.imgSize.height.indexOf('%')!= -1){
				img_height = parseInt(obj.options.imgSize.height)/100 * parentHeight + 'px';
			}else {
				img_height = obj.options.imgSize.height;
			}

			if(obj.options.barSize.width.indexOf('%')!= -1){
				bar_width = parseInt(obj.options.barSize.width)/100 * parentWidth + 'px';
			}else {
				bar_width = obj.options.barSize.width;
			}

			if(obj.options.barSize.height.indexOf('%')!= -1){
				bar_height = parseInt(obj.options.barSize.height)/100 * parentHeight + 'px';
			}else {
				bar_height = obj.options.barSize.height;
			}

			if(obj.options.blockSize) {
				if(obj.options.blockSize.width.indexOf('%')!= -1){
					block_width = parseInt(obj.options.blockSize.width)/100 * parentWidth + 'px';
				}else {
					block_width = obj.options.blockSize.width;
				}


				if(obj.options.blockSize.height.indexOf('%')!= -1){
					block_height = parseInt(obj.options.blockSize.height)/100 * parentHeight + 'px';
				}else {
					block_height = obj.options.blockSize.height;
				}
			}

			if(obj.options.circleRadius) {
				if(obj.options.circleRadius.indexOf('%')!= -1){
					circle_radius = parseInt(obj.options.circleRadius)/100 * parentHeight + 'px';
				}else {
					circle_radius = obj.options.circleRadius;
				}
			};

			return {img_width : img_width, img_height : img_height, bar_width : bar_width, bar_height : bar_height, block_width : block_width, block_height : block_height, circle_radius : circle_radius};
		},

		//刷新
		refresh: function() {
			var _this = this;
			this.htmlDoms.refresh.show();
			this.$element.find('.verify-msg:eq(1)').text('');
			this.$element.find('.verify-msg:eq(1)').css('color', '#000');
			this.htmlDoms.move_block.animate({'left':'0px'}, 'fast');
			this.htmlDoms.left_bar.animate({'width': parseInt(this.setSize.bar_height)}, 'fast');
			this.htmlDoms.left_bar.css({'border-color': '#ddd'});
			this.htmlDoms.move_block.css('background-color', '#fff');
			this.htmlDoms.icon.css('color', '#000');
			this.htmlDoms.icon.removeClass('icon-close');
			this.htmlDoms.icon.addClass('icon-right');
			this.$element.find('.verify-msg:eq(0)').text(this.options.explain);
			this.isEnd = false;
			getPictrue({captchaType:"blockPuzzle"},this.options.baseUrl, function res(res){
				if (res.repCode=="0000") {
					_this.$element.find(".backImg")[0].src = 'data:image/png;base64,'+res.repData.originalImageBase64;
					_this.$element.find(".bock-backImg")[0].src = 'data:image/png;base64,'+res.repData.jigsawImageBase64;
					_this.secretKey = res.repData.secretKey;
					_this.backToken = res.repData.token;
				}
			});
			this.htmlDoms.sub_block.css('left', "0px");
		},

		/*
        * 解析matrix矩阵，0°-360°，返回旋转角度
        * 当a=b||-a=b,0<=deg<=180
        * 当-a+b=180,180<=deg<=270
        * 当a+b=180,270<=deg<=360
        *
        * 当0<=deg<=180,deg=d;
        * 当180<deg<=270,deg=180+c;
        * 当270<deg<=360,deg=360-(c||d);
        * */
		getmatrix: function(a,b,c,d){
			var aa=Math.round(180*Math.asin(a)/ Math.PI);
			var bb=Math.round(180*Math.acos(b)/ Math.PI);
			var cc=Math.round(180*Math.asin(c)/ Math.PI);
			var dd=Math.round(180*Math.acos(d)/ Math.PI);
			var deg=0;
			if(aa==bb||-aa==bb){
				deg=dd;
			}else if(-aa+bb==180){
				deg=180+cc;
			}else if(aa+bb==180){
				deg=360-cc||360-dd;
			}
			return deg>=360?0:deg;
			//return (aa+','+bb+','+cc+','+dd);
		}
	};


	//定义Points的构造函数
	var Points = function(ele, opt) {
		this.$element = ele,
			this.backToken = null,
			this.secretKey = '',
			this.defaults = {
				baseUrl:"https://mirror.anji-plus.com/captcha-api",
				captchaType:"clickWord",
				containerId:'',
				mode : 'fixed',	//弹出式pop，固定fixed
				checkNum : 3,	//校对的文字数量
				vSpace : 5,	//间隔
				imgSize : {
					width: '310px',
					height: '155px',
				},
				barSize : {
					width : '310px',
					height : '50px',
				},
				beforeCheck: function(){ return true},
				ready : function(){},
				success : function(){},
				error : function(){}
			},
			this.options = $.extend({}, this.defaults, opt)
	};

	//定义Points的方法
	Points.prototype = {
		init : function() {
			var _this = this;
			//加载页面
			_this.loadDom();

			_this.refresh();
			_this.options.ready();

			this.$element[0].onselectstart = document.body.ondrag = function(){
				return false;
			};

			if(this.options.mode == 'pop')	{

				_this.$element.find('.verifybox-close').on('click', function() {
					_this.$element.find(".mask").css("display","none");
				});

				var clickBtn = document.getElementById(this.options.containerId);
				clickBtn && (clickBtn.onclick = function(){
					if (_this.options.beforeCheck()) {
						_this.$element.find(".mask").css("display","block");
					}
				})

			}
			// 注册点击验证事件
			_this.$element.find('.back-img').on('click', function(e) {

				_this.checkPosArr.push(_this.getMousePos(this, e));

				if(_this.num == _this.options.checkNum) {
					_this.num = _this.createPoint(_this.getMousePos(this, e));
					//按比例转换坐标值
					_this.checkPosArr = _this.pointTransfrom(_this.checkPosArr,_this.setSize);
					setTimeout(function (){
						var data = {
							captchaType:_this.options.captchaType,
							"pointJson":_this.secretKey ? aesEncrypt(JSON.stringify(_this.checkPosArr),_this.secretKey):JSON.stringify(_this.checkPosArr),
							"token":_this.backToken
						}
						var captchaVerification = _this.secretKey ? aesEncrypt(_this.backToken+'---'+JSON.stringify(_this.checkPosArr),_this.secretKey):_this.backToken+'---'+JSON.stringify(_this.checkPosArr)
						checkPictrue(data, _this.options.baseUrl,function resolve(res){
							if (res.repCode=="0000") {
								_this.$element.find('.verify-bar-area').css({'color': '#4cae4c', 'border-color': '#5cb85c'});
								_this.$element.find('.verify-msg').text('验证成功');
								// _this.$element.find('.verify-refresh').hide();
								_this.$element.find('.verify-img-panel').unbind('click');
								setTimeout(function (){
									_this.$element.find(".mask").css("display","none");
									_this.refresh();
								},1000);
								_this.options.success({'__captchaVerification':captchaVerification});
							}else{
								_this.options.error(_this);
								_this.$element.find('.verify-bar-area').css({'color': '#d9534f', 'border-color': '#d9534f'});
								_this.$element.find('.verify-msg').text('验证失败');
								setTimeout(function () {
									_this.$element.find('.verify-bar-area').css({'color': '#000','border-color': '#ddd'});
									_this.refresh();
								}, 400);
							}
						})
					}, 400);

				}
				if(_this.num < _this.options.checkNum) {
					_this.num = _this.createPoint(_this.getMousePos(this, e));
				}
			});

			//刷新
			_this.$element.find('.verify-refresh').on('click', function() {
				var ele = $(this).find('img');
				var css = ele.css('transform');
				var deg;
				var step=90;
				if(css === 'none'){
					deg = 90;
				} else {
					var values = css.split('(')[1].split(')')[0].split(',');
					var a = values[0];
					var b = values[1];
					var c = values[2];
					var d = values[3];
					deg = _this.getmatrix(a,b,c,d);
					deg = deg+step;
				}
				ele.css({'transform':'rotate('+deg+'deg)'});
				_this.refresh();
			});

		},

		//加载页面
		loadDom : function() {

			this.fontPos = [];	//选中的坐标信息
			this.checkPosArr = [];	//用户点击的坐标
			this.num = 1;	//点击的记数

			var panelHtml = '';
			var wrapStartHtml = '';

			this.setSize = Slide.prototype.resetSize(this);	//重新设置宽度高度

			wrapStartHtml +='<div class="mask">';
			wrapStartHtml +='<div class="verifybox" style="width:'+(parseInt(this.setSize.img_width)+30)+'px">';
			wrapStartHtml +='<div class="verifybox-top">';
			wrapStartHtml +='请完成安全验证';
			wrapStartHtml +='<span class="verifybox-close">';
			wrapStartHtml +='<i class="iconfont icon-close"></i>';
			wrapStartHtml +='</span>';
			wrapStartHtml +='</div>';
			wrapStartHtml +='<div class="verifybox-bottom" style="padding:10px 15px 15px 15px">';
			wrapStartHtml +='<div style="position: relative;">';

			if (this.options.mode == 'pop') {
				panelHtml = wrapStartHtml
			}

			panelHtml += '<div class="verify-img-out">';
			panelHtml += '<div class="verify-img-panel">';
			panelHtml += '<div class="verify-refresh" style="z-index:3">';
			panelHtml += '<img src="data:image/png;base64,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">';
			panelHtml += '</div>';
			panelHtml += '<img src="" class="back-img" width="'+this.setSize.img_width+'" height="'+this.setSize.img_height+'">';
			panelHtml += '</div>';
			panelHtml += '</div>';
			panelHtml += '<div class="verify-bar-area" style="width:'+this.setSize.img_width+',height:'+this.setSize.bar_height+',line-height:'+this.setSize.bar_height+'">';
			panelHtml += '<span  class="verify-msg"></span>';
			panelHtml += '</div>';
			wrapEndHtml = '</div></div></div></div>';

			if (this.options.mode == 'pop') {
				panelHtml += wrapEndHtml
			}

			this.$element.append(panelHtml);

			this.htmlDoms = {
				back_img : this.$element.find('.back-img'),
				out_panel : this.$element.find('.verify-img-out'),
				img_panel : this.$element.find('.verify-img-panel'),
				bar_area : this.$element.find('.verify-bar-area'),
				msg : this.$element.find('.verify-msg'),
			};

			this.$element.css('position', 'relative');

			this.htmlDoms.out_panel.css('height', parseInt(this.setSize.img_height) + this.options.vSpace + 'px');
			this.htmlDoms.img_panel.css({'width': this.setSize.img_width, 'height': this.setSize.img_height, 'background-size' : this.setSize.img_width + ' '+ this.setSize.img_height, 'margin-bottom': this.options.vSpace + 'px'});
			this.htmlDoms.bar_area.css({'width': this.setSize.img_width, 'height': this.setSize.bar_height, 'line-height':this.setSize.bar_height});

		},

		//获取坐标
		getMousePos :function(obj, event) {
			var e = event || window.event;
			var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
			var scrollY = document.documentElement.scrollTop || document.body.scrollTop;
			var x = e.clientX - ($(obj).offset().left - $(window).scrollLeft());
			var y = e.clientY - ($(obj).offset().top - $(window).scrollTop());

			return {'x': x, 'y': y};
		},

		//创建坐标点
		createPoint : function (pos) {
			this.htmlDoms.img_panel.append('<div class="point-area" style="background-color:#1abd6c;color:#fff;z-index:9999;width:20px;height:20px;text-align:center;line-height:20px;border-radius: 50%;position:absolute;'+
				'top:'+parseInt(pos.y-10)+'px;left:'+parseInt(pos.x-10)+'px;">'+this.num+'</div>');
			return ++this.num;
		},

		//刷新
		refresh: function() {
			var _this = this;
			this.$element.find('.point-area').remove();
			this.fontPos = [];
			this.checkPosArr = [];
			this.num = 1;
			getPictrue({captchaType:"clickWord"},_this.options.baseUrl, function res(res){
				if (res.repCode=="0000") {
					_this.htmlDoms.back_img[0].src ='data:image/png;base64,'+ res.repData.originalImageBase64
					_this.backToken = res.repData.token
					_this.secretKey = res.repData.secretKey
					var text = '请依次点击【' + res.repData.wordList.join(",") + '】'
					_this.$element.find('.verify-msg').text(text);
				}
			})
		},

		getmatrix: function(a,b,c,d){
			var aa=Math.round(180*Math.asin(a)/ Math.PI);
			var bb=Math.round(180*Math.acos(b)/ Math.PI);
			var cc=Math.round(180*Math.asin(c)/ Math.PI);
			var dd=Math.round(180*Math.acos(d)/ Math.PI);
			var deg=0;
			if(aa==bb||-aa==bb){
				deg=dd;
			}else if(-aa+bb==180){
				deg=180+cc;
			}else if(aa+bb==180){
				deg=360-cc||360-dd;
			}
			return deg>=360?0:deg;
			//return (aa+','+bb+','+cc+','+dd);
		},
		pointTransfrom:function(pointArr,imgSize){
			var newPointArr = [];
			for(var i=0; i< pointArr.length; i++){
				var x = Math.round(310 * pointArr[i].x/parseInt(imgSize.img_width))
				var y =Math.round(155 * pointArr[i].y/parseInt(imgSize.img_height))
				var temp = {'x':x,'y':y};
				newPointArr.push(temp);
			}
			return newPointArr
		}
	};
	//在插件中使用slideVerify对象
	$.fn.slideVerify = function(options, callbacks) {
		var slide = new Slide(this, options);
		if (slide.options.mode=="pop" && slide.options.beforeCheck()) {
			slide.init();
		}else if (slide.options.mode=="fixed") {
			slide.init();
		}
	};

	//在插件中使用clickVerify对象
	$.fn.pointsVerify = function(options, callbacks) {
		var points = new Points(this, options);
		if (points.options.mode=="pop" && points.options.beforeCheck()) {
			points.init();
		}else if (points.options.mode=="fixed") {
			points.init();
		}
	};

})(jQuery, window, document);