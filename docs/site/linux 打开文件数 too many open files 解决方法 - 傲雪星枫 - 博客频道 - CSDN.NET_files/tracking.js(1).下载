/*
	@function adv tracking
	@editer fjj
	@create_time 20151201
**/
(function(widnow,$csdn_iteye_jq,undefined){
	function TrackAdv(){
		this.advs=[];  // 被载入广告位集合
		this.advFlag={};  //  用于载入广告位去重
		this.dataArr=[]; //缓存需要提交数据的广告对象
		this.dataTimer=null;
		this.swapImgSet=false;  //第一个轮播图广告位载入时 触发轮播图方法  之后不再不再触发
		this.init();
	}
	TrackAdv.prototype={
		constructor:"Track",
		init:function(){
			var self=this;
			//滚动处理函数  采用了事件节流 每隔100毫米计算一次
			var scrollHandle=(function(){
				var timer=null;
				return function(){
					if(timer){
						return false;
					}
					timer=setTimeout(function(){
						timer=null;
						/*if(self.advs.length === 0){
							return false;
						}
						for(var i=0;i<self.advs.length;i++){

							if(self.advs[i].view && self.posTest(self.advs[i])){
								if(self.advs[i].ele.data("swapimg") && !self.advs[i].ele.hasClass("curSwap")){
									continue;
								}
								self.advs[i].viewed=true;
								//self.sendData(self.advs[i]);
								if(!self.dataTimer){
									self.discreteSend();
								}
								self.dataArr.push(self.advs[i]);

							}

						}*/
						self.insertData();
					},100)
				}
			})();
			//关闭浏览器 关闭当前页 跳转到其他页面时 将没有提交的数据一次行提交
			var unloadHandle=function(){
				if(self.dataArr.length ===0){
					return false;
				}
				for(var i=0,len=self.dataArr.length;i<len;i++){
					self.sendData(self.dataArr[i]);
				}
				self.dataArr=[];
			}
			//$csdn_iteye_jq(window).on("scroll",scrollHandle);
			//$csdn_iteye_jq(window).on("unload",unloadHandle);
		},
		//载入广告对象
		addAdvs:function(eleStr,opt){
			var self=this,
				eleStr=eleStr || ".J_adv",
				opt=typeof opt === "object"?opt:{},
				oAdvs=$csdn_iteye_jq(eleStr),
				nAdvLen=oAdvs.length,
				preAdr=document.referrer || "-";   // 前一个文档地址
			if(nAdvLen === 0){
				return false;
			}
			for(var i=0;i<nAdvLen;i++){
				var adv={};
				var mod=$csdn_iteye_jq(oAdvs[i]).data("mod");
				if(this.advFlag[mod]){
					return false;
				}
				adv.ele=$csdn_iteye_jq(oAdvs[i]);
				adv.top=$csdn_iteye_jq(oAdvs[i]).offset().top;
				adv.height=$csdn_iteye_jq(oAdvs[i]).height()?$csdn_iteye_jq(oAdvs[i]).height():$csdn_iteye_jq(oAdvs[i]).find("img").height();
				adv.view=typeof $csdn_iteye_jq(oAdvs[i]).data("view") === "undefined" ?true:$csdn_iteye_jq(oAdvs[i]).data("view");  //  对应广告位是否需要曝光
				adv.viewed=false;   //被曝光时为true
				adv.data={
					uid:"-",
					ref:preAdr,
					mod:adv.ele.data("mod") || "-",
					mtp:opt.mtp || adv.ele.data("mtp") || 1,
					con:self.exportData(adv),
					ck:"-"
				};
				if(adv.ele.data("swapimg") && !this.swapImgSet){
					//只触发一次轮播图方法
					//adv.ele.addClass("curSwap").parent().css("z-index",100);
					csdn.SwapImage.swap({
		        swapRoot: $csdn_iteye_jq( '.hot' )
		    	});
					this.swapImgSet=true;
				}
				if(this.posTest(adv) && adv.view){
					if($csdn_iteye_jq(oAdvs[i]).data("swapimg")){  //是轮播图广告位的情况
						if($csdn_iteye_jq(oAdvs[i]).hasClass("curSwap")){
							adv.viewed=true;
							this.sendData(adv);
							/*this.dataArr.push(adv);
							if(!this.dataTimer){
								this.discreteSend();
							}*/

						}
					}else{ //如果不是轮播图的只要进入屏幕区域就算曝光
						adv.viewed=true;

						/*//如果定时提交数据的定时器没有开启的话，开启定时器
						this.dataArr.push(adv);
						if(!this.dataTimer){
							this.discreteSend();
						}*/

						this.sendData(adv);
					}

				}
				this.advs.push(adv);
				this.linkNodes(adv);
				//this.expAdvs.push(adv);
				this.advFlag[mod]=true;
			}
		},

		//判断广告位是否进入屏幕 进入即将广告对象缓存到(dataArr)中
		insertData:function(){
			var self=this;
			if(self.advs.length === 0){
				return false;
			}
			for(var i=0;i<self.advs.length;i++){
				if(self.advs[i].view && self.posTest(self.advs[i])){
					if(self.advs[i].ele.data("swapimg") && !self.advs[i].ele.hasClass("curSwap")){
						continue;
					}
					self.advs[i].viewed=true;
					self.sendData(self.advs[i]);
					/*//将广告对象添加到数据队列(dataArr)即可并等待提交
					self.dataArr.push(self.advs[i]);

					//如果定时提交数据的定时器没有开启的话，开启定时器
					if(!self.dataTimer){
						self.discreteSend();
					}*/

				}

			}
		},
		//每隔1秒提交一次曝光数据(1条)
		discreteSend:function(){
			var self=this;
			var discreteHandle=function(){
				if(self.dataArr.length ===0){
					clearInterval(self.dataTimer);
					self.dataTimer=null;
					return false;
				}
				var i=0;
				while(i<1){
					if(self.dataArr[0]){
						self.sendData(self.dataArr[0]);
						self.dataArr.shift();
						i++;
					}else{
						clearInterval(self.dataTimer);
						self.dataTimer=null;
						break;
					}
				}
			}
			this.dataTimer=setInterval(discreteHandle,20);
		},
		// 获取曝光内容即广告位中所有连接的内容
		exportData:function(adv){
			var con=adv.ele.data("con") || "-";
			return !!adv.ele.data("order")?con+",ad_order_"+adv.ele.data("order"):con+"-"
		},
		// 测试广告位是否在曝光区域
		posTest:function(adv){
			//修改曝光规则
			if(adv.viewed){
				return false;
			}else{
				return true;
			}
		},
		// 获取广告位中所有的连接且添加click事件
		linkNodes:function(adv){
			var self=this;
			var aLinks=adv.ele.find("a");
			var iframeLinks=adv.ele.find("iframe")
			if(aLinks.length === 0 && iframeLinks.length === 0 ){
				return false;
			}
			aLinks.each(function(){
				if($csdn_iteye_jq(this).attr("target") == undefined){
					$csdn_iteye_jq(this).attr("target","_blank");
				}
				$csdn_iteye_jq(this).on("click",function(){
					con=self.linkData(this);
					self.sendData(adv,con);
				})
			})
		},
		//获取点击元素的内容
		linkData:function(that){
			var ck=""+that.href;
			var conStr;
			if($csdn_iteye_jq(that).find("img").length){
				conStr=$csdn_iteye_jq(that).find("img").eq(0).attr("title") || $csdn_iteye_jq(that).find("img").eq(0).attr("alt")

			}else{
				conStr=$csdn_iteye_jq(that).html();
			}
			ck+=conStr?";"+conStr:"";
			return ck;
		},
		//获取用户ID
		getUserId:function(){
			var result=/(; )?(UserName|_javaeye_cookie_id_)=([^;]+)/.exec(widnow.document.cookie);
			var uid= (result != null ? result[3] : void 0) || '-';
			return uid;
		},
		//提交数据
		sendData:function(adv,con){
			adv.data.uid=this.getUserId();
			protocol="http:";
			if(typeof con === "string"){
				adv.data.ck=con;
			}
			var dataStr=this.paramData(adv.data);
			var img =new Image();
			img.onload=img.onerror=function(){
				img.onload=img.onerror=null;
				img=null;
			}
			if(typeof con === "string"){
				img.src=protocol+"//dc.csdn.net/re?"+dataStr;
			}
		},
		//数据转换为字符串形式
		paramData:function(data){
			var dataArr=[];
			for(var key in data){
				var text=key+"="+data[key];
				text.replace(/^\s+|\s+$/g,"");
				dataArr.push(text);
			}
			return dataArr.join("&")
		}
		//处理url
		/*handleUrl:function(url){
			if(typeof url === "string" && url.length >0 ){
				var hostStr=url.split("://")[1];
				hostName=hostStr.split(".")[0];
				strArr=hostStr.split("?")[0].split("/");
				fileName=strArr[strArr.length-1];
				return hostName+"_"+fileName;
			}else{
				return false;
			}
		}*/
	}
	//轮播图   淡入淡出
/*	function SwapImg(opts,obj){
		if(!(this instanceof SwapImg)){
			return new SwapImg(opts,obj);

		}
		this.obj=obj;
		this.parentSelector=opts.parentSelector || ".hot";
		this.swapSelector=opts.swapSelector || ".hot .J_adv";
		this.swapCur="curSwap";
		this.iconSelector=opts.iconSelector || ".js-tagRoot";
		this.iconCur="current";
		this.duration=opts.duration || 500;
		this.interval=opts.interval || 3000;
		this.lastIndex=-1;
		this.index=0;
		this.swapFlag=false; //是否正在轮播中
		this.timer=null;
		this.init();
	}
	SwapImg.prototype={
		constructor:SwapImg,
		init:function(){
			var self=this,
				swapNode=$csdn_iteye_jq(this.swapSelector),
				swapLen=swapNode.length,
				iconNode=$csdn_iteye_jq(this.iconSelector),
				iconStr="";
			for(var i=0;i<swapLen;i++){
				iconStr+=i==0?"<li class='current'></li>":"<li></li>";
			}
			iconNode.html(iconStr);
			iconNode.find("li").each(function(i){
				$csdn_iteye_jq(this).on("click",function(){
					//clearInterval(self.timer);
					if($csdn_iteye_jq(this).hasClass("current") || self.swapFlag ){
						return false;
					}
					self.swapHandle(i);
				})
			});
			self.timer=setInterval(function(){
				self.swapHandle();
			},self.interval)

			$csdn_iteye_jq(self.parentSelector).hover(function(){
				clearInterval(self.timer)
			},function(){
				self.timer=setInterval(function(){
					self.swapHandle();
				},self.interval)
			});
		},
		swapHandle:function(i){
			var self=this;
			self.swapFlag=true;
			self.lastIndex=self.index;
			self.index=typeof i === 'number'?i:++self.index%$(self.swapSelector).length;  //i || ....
			//self.index=i || ++self.index%$(self.swapSelector).length;
			$csdn_iteye_jq(self.iconSelector).find("li").removeClass("current").eq(self.index).addClass("current");
			$csdn_iteye_jq(self.swapSelector).removeClass("curSwap").eq(self.index).addClass("curSwap");
			$csdn_iteye_jq(self.swapSelector).eq(self.index).animate({opacity:1},self.duration,function(){
				//$csdn_iteye_jq(this).parent().css("z-index",100)
				$csdn_iteye_jq(this).parent().css({"position":"absolute","z-index":"100","height":"200","top":"0","left":"0"})
			});
			$csdn_iteye_jq(self.swapSelector).eq(self.lastIndex).animate({opacity:0},self.duration,function(){
				$csdn_iteye_jq(this).parent().css("z-index",0)
				if(self.obj && typeof self.obj.insertData === "function"){
					self.obj.insertData();
				}
				self.swapFlag=false;
			})
		}
	}*/
	widnow.CSDN=widnow.CSDN?widnow.CSDN:{};
	return window.CSDN.track=new TrackAdv();
})(window,jQuery)