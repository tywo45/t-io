(function(window,undefined){
	//处理localStroage的兼容问题

	if (!window.localStorage) {
	    window.localStorage = {
	        getItem: function (sKey) {
	            if (!sKey || !this.hasOwnProperty(sKey)) { return null; }
	            return unescape(document.cookie.replace(new RegExp("(?:^|.*;\\s*)" + escape(sKey).replace(/[\-\.\+\*]/g, "\\$&") + "\\s*\\=\\s*((?:[^;](?!;))*[^;]?).*"), "$1"));
	        },
	        key: function (nKeyId) { return unescape(document.cookie.replace(/\s*\=(?:.(?!;))*$/, "").split(/\s*\=(?:[^;](?!;))*[^;]?;\s*/)[nKeyId]); },
	        setItem: function (sKey, sValue) {
	            if(!sKey) { return; }
	            document.cookie = escape(sKey) + "=" + escape(sValue) + "; path=/";
	            this.length = document.cookie.match(/\=/g).length;
	        },
	        length: 0,
	        removeItem: function (sKey) {
	            if (!sKey || !this.hasOwnProperty(sKey)) { return; }
	            var sExpDate = new Date();
	            sExpDate.setDate(sExpDate.getDate() - 1);
	            document.cookie = escape(sKey) + "=; expires=" + sExpDate.toGMTString() + "; path=/";
	            this.length--;
	        },
	        hasOwnProperty: function (sKey) { return (new RegExp("(?:^|;\\s*)" + escape(sKey).replace(/[\-\.\+\*]/g, "\\$&") + "\\s*\\=")).test(document.cookie); }
	    };
	    window.localStorage.length = (document.cookie.match(/\=/g) || window.localStorage).length;
	}
	var storeFlag={
	    getStore:function(name){
	        if(window.localStorage[name+"time"] && window.localStorage[name+"time"]<new Date().getTime()){
	            window.localStorage.removeItem(name)
	            window.localStorage.removeItem(name+"time")
	        }
	        return window.localStorage[name]
	    },
	    setStore:function(name,val,expoire){
	        window.localStorage[name]=val;
	        if(expoire === "none"){
	            return ;
	        }
	        if(expoire){
	            window.localStorage[name+"time"]=new Date().getTime()+expoire;
	        }

	    }
	}
    var Layer={};
    //左右或者底部的广告
    var styObj={
        width:"280px",
        height:"210px",
        position:"fixed",
        backgroundColor:"#efefef",
        bottom:0,
        right:0
    }

    Layer.PopuLayer=PopuLayer;
    function PopuLayer(selector,opts/*styleObj,storageName*/){
        if(!(this instanceof PopuLayer)){
            return new PopuLayer(selector,opts)
        }
        this.selector=selector;
        this.styleObj=opts.styleObj || styObj;
        this.storageName=opts.storageName;
        this.expoire=opts.expoire;
        this.total=opts.total;
        this.init()
    }
    PopuLayer.prototype={
        constructor:PopuLayer,
        init:function(){
            var _self=this;
            function _show(){
                $(_self.selector).css(_self.styleObj).show();
                $(_self.selector).find(".layer_close").on("click",function(){
                    $(this).parents(_self.selector).hide();
                })
            }
            var value=Number(storeFlag.getStore(this.storageName)?storeFlag.getStore(this.storageName):0) //取出存取的次数
            if(!value){ //第一次访问
                _show()
                return storeFlag.setStore(this.storageName,value+1,this.expoire)
            }
            if(this.total<0||value < this.total){
                _show()
                return storeFlag.setStore(this.storageName,value+1)
            }
        }
    }
    //首页顶部广告
    var fullSty={
        width:"1170px",
        overflow:"hidden",
        margin:"0 auto",
        display:"block"
    }
    Layer.FullAdv=FullAdv;
    function FullAdv(selector,opts){
        if(!(this instanceof FullAdv)){
            return new FullAdv(selector,opts)
        }
        this.selector=selector;
        this.opts=opts ||{};
        this.init();
    }
    FullAdv.prototype={
        constructor:FullAdv,
        init:function(){
            var _self=this,
                styObj=this.opts.styObj || fullSty,  //自定义样式
                intervalTime=this.opts.intervalTime*1000 || 3000,  //设置间隔多长时间去隐藏
                speedTime=this.opts.speedtime*1000 || 500,  //效果过渡时间
                storageName=this.opts.storageName ||true,  //存储storage key
                expoire=this.opts.expoire || "none", //过期时间
                total=this.opts.total || 1; //最多显示次数
            function _show(){
                $(_self.selector).css(styObj);
                setTimeout(function(){
                    $(_self.selector).animate({height:0},speedTime,"swing")
                },intervalTime)
            }
            var value=Number(storeFlag.getStore(storageName)?storeFlag.getStore(storageName):0) //取出存取的次数
            if(!value){ //第一次访问
                _show()
                return storeFlag.setStore(storageName,value+1,expoire)
            }
            if(total<0||value < total){
                _show()
                return storeFlag.setStore(storageName,value+1)
            }


        }
    }

    window.CSDN=window.CSDN?window.CSDN:{};
    window.CSDN.Layer=Layer;
}(window))