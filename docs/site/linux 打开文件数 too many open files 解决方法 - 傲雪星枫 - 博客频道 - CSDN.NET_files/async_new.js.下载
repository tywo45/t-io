(function (d, c) {
    var host_info = document.domain;
    var prefix_domain = '';
    if (host_info.match(/^beta*/)) {
        prefix_domain = 'beta';
    } else if (host_info.match(/^l[a-z]*/)) {
        prefix_domain = 'l';
    }
    var host_http = 'http://' + prefix_domain + 'ads.csdn.net/';

    var a = "8c38e720de1c90a6f6ff52f3f89c4d57";
    c.reviveAsync = c.reviveAsync || {};
    if (!c.reviveAsync.hasOwnProperty(a)) {
        f = c.reviveAsync[a] = {
            id: Object.keys(c.reviveAsync).length,
            name: "revive",
            start: function () {
                var e = function () {
                    try {
                        if (!f.done) {
                            d.removeEventListener("DOMContentLoaded", e, false);
                            c.removeEventListener("load", e, false);
                            f.done = true;
                            f.apply(f.detect())
                        }
                    } catch (g) {
                        console.log(g)
                    }
                };
                if (d.readyState === "complete") {
                    setTimeout(e)
                } else {
                    d.addEventListener("DOMContentLoaded", e, false);
                    c.addEventListener("load", e, false)
                }
            },
            ajax: function (e, g) {
                /*
                 var h = new XMLHttpRequest();
                 h.onreadystatechange = function() {
                 if (this.readyState == 4) {
                 if (this.status == 200) {
                 //f.spc(JSON.parse(this.responseText))
                 }
                 }
                 };
                 h.open("GET", e + "?" + f.encode(g).join("&"), true);
                 h.withCredentials = true;
                 h.send()
                 */
                /*
                 var oHead = document.getElementsByTagName('HEAD').item(0);
                 var oScript= document.createElement("script");
                 oScript.type = "text/javascript";
                 oScript.src=e + "?" + f.encode(g).join("&");
                 oHead.appendChild(oScript);
                 */
                //alert(1);

            },
            encode: function (m, n) {
                var e = [], h, i;
                for (h in m) {
                    if (m.hasOwnProperty(h)) {
                        var l = n ? n + "[" + h + "]" : h;
                        if ((/string|number|boolean/).test(typeof m[h])) {
                            e.push(encodeURIComponent(l) + "="
                            + encodeURIComponent(m[h]))
                        } else {
                            var g = f.encode(m[h], l);
                            for (i in g) {
                                e.push(g[i])
                            }
                        }
                    }
                }
                return e
            },
            apply: function (g_m) {
                var g = g_m[0];
                if (g.zones.length) {
                    var e = host_http + "get_ads.php";
                    g.zones = g.zones.join("|");
                    g.loc = d.location.href;
                    g.ip = d.ip;
                    g.iframe = g.iframe.join("|");
                    if (d.referrer) {
                        g.referer = d.referrer
                    }
                    //var aaa= document.getElementById("test1");
                    //alert(aaa.innerHTML);
                    //g_m[1].innerHTML = "aaa<script type='text/javascript' src='"+e + "?" + f.encode(g).join("&")+"'><\/script>";

                    //f.ajax(e, g)
                    var span = document.createElement("span");
                    g_m[1] = g_m[1].appendChild(span);
                    if (g_m[1]) g_m[1].parentNode.removeChild(g_m[1]);
                    var scriptObj = document.createElement("script");
                    scriptObj.src = e + "?ip=" + f.GetQueryString("ip") + "&" + f.encode(g).join("&");
                    scriptObj.type = "text/javascript";
                    document.getElementsByTagName("head")[0].appendChild(scriptObj);

                    //var sinfo = '{"revive-0-0":{"html":"<table width=\'150\' height=\'300\' border=\'0\' cellspacing=\'0\' cellpadding=\'0\' align=\'center\' bgcolor=\'#fff\' style=\'background-color:#fff;border-left:1px solid #e8e8e8;border-right:1px solid #e8e8e8;\'>    <tbody>     <tr style=\'vertical-align:top;\'>      <td width=\'150\'>&nbsp;<\/td>      <td style=\'text-align:center;\'><img src=\'http:\/\/img.bss.csdn.net\/201511241004259379.png\' \/>       <p style=\'height:14px\'><\/p><p style=\'font-size:16px;font-family:\'\u5fae\u8f6f\u96c5\u9ed1\',\u5b8b\u4f53,Arial,sans-serif;line-height:22px;color:#ff9900;text-align:center;\'>\u56fe\u6587\u6df7\u6392\u5e7f\u544a\u6d4b\u8bd5<p> <p style=\'font-size:14px;font-family:\'\u5fae\u8f6f\u96c5\u9ed1\',\u5b8b\u4f53,Arial,sans-serif;line-height:22px;color:#666;text-align:left;\'>\u8fd9\u662f\u7b80\u5355\u7684\u56fe\u6587\u6df7\u6392\u6d4b\u8bd5<p><\/td>  <td width=\'90\'>&nbsp;<\/td>     <td width=\'150\' style=\'border-right:1px solid #e8e8e8;\'>&nbsp;<\/td>     <\/tr>    <\/tbody>  <\/table> ","width":"150","height":"300"}}';
                    //var kinfo = '{"revive-0-0":{"html":"aaabbb","width":"150","height":"300"}}';
                    //alert(sinfo);
                    //f.spc(JSON.parse(sinfo));
                }
            },
            detect: function () {
                var o = d.querySelectorAll("ins[data-" + f.name + "-id='" + a
                + "']");
                var n = {
                    zones: [],
                    iframe: [],
                    prefix: f.name + "-" + f.id + "-"
                };
                for (var g = 0; g < o.length; g++) {
                    var k = o[g];
                    var ak = k;
                    if (k.hasAttribute("data-" + f.name + "-zoneid")) {
                        var l = new RegExp("^data-" + f.name + "-(.*)$"), e;
                        for (var h = 0; h < k.attributes.length; h++) {
                            if (e = k.attributes[h].name.match(l)) {
                                if (e[1] == "zoneid") {
                                    n.zones[g] = k.attributes[h].value;
                                    k.id = n.prefix + g
                                } else {
                                    if (e[1] != "id") {
                                        n[e[1]] = k.attributes[h].value
                                    }
                                }
                            }
                        }
                    }
                    if (ak.hasAttribute("iframe")) {
                        var al = new RegExp("iframe"), e;
                        for (var h = 0; h < ak.attributes.length; h++) {
                            if (e = ak.attributes[h].name.match(al)) {
                                if (e == "iframe") {
                                    n.iframe[g] = ak.attributes[h].value;
                                }
                            }
                        }
                    } else {
                        n.iframe[g] = 'false';
                    }
                }
                var n_m = new Array();
                n_m[0] = n;
                n_m[1] = k;
                return n_m;
            },
            createFrame: function (h) {
                var e = d.createElement("IFRAME"), g = e.style;
                e.scrolling = "no";
                e.frameBorder = 0;
                e.width = h.width > 0 ? h.width : 0;
                e.height = h.height > 0 ? h.height : 0;
                g.border = 0;
                g.overflow = "hidden";
                return e
            },
            loadFrame: function (g, e) {
                var h = g.contentDocument || g.contentWindow.document;
                h.open();
                h.writeln("<!DOCTYPE html>");
                h.writeln("<html>");
                h.writeln('<head><base target="_top"></head>');
                h.writeln('<body border="0" margin="0" style="margin: 0; padding: 0">');
                h.writeln(e);
                h.writeln("</body>");
                h.writeln("</html>");
                h.close()
            },
            spc: function (k) {
                for (var e in k) {
                    if (k.hasOwnProperty(e)) {
                        var o = k[e];
                        var n = d.getElementById(e);
                        if (o.html) {
                            if (n) {
                                var m = d.createElement("div");
                                var w=o.width;
                                var h=o.height;
								m.style.width=w+"px";
								m.style.height=h+"px";
                                //m.style = "padding: 0px; margin: 0px; border: 0px none;width:"+o.width+"px;height:"+o.height+"px";
                                //m.class = "J_adv";
                                m.setAttribute('class','J_adv'); 
                                m.setAttribute("data-view", o.data_view);
                                m.setAttribute("data-mod", 'ad_popu_' + o.data_aid);//广告位id
                                m.setAttribute("data-mtp", o.data_mod);//广告位类型
                                m.setAttribute("data-order", o.data_order);//广告位类型
                                if (o.hasOwnProperty('data_swapimg')) {
                                    m.setAttribute('data-swapimg', o.data_swapimg);
                                }
                                //var img =new Image();
                    			//img.src="http://ads.csdn.net/add_num.php?mod=ad_popu_"+o.data_aid+"&ck=-";
                    			
                    			var adv={};
                    			adv.data={};
                    			var preAdr=d.referrer || "-";   // 前一个文档地址
                    			adv.data.ref = preAdr;
                    			adv.data.mtp = o.data_mod;
                    			adv.data.mod = 'ad_popu_'+o.data_aid;
                    			adv.data.con = 'ad_content_'+o.data_ideaid+',ad_order_'+o.data_order;
                    			//f.sendData(adv,"//ads.csdn.net/add_num.php?");
                    			f.sendData(adv,"//dc.csdn.net/re?");
                    			
                                m.setAttribute("data-con", 'ad_content_' + o.data_ideaid);
                                m.setAttribute("class", "J_adv");
                                if (o.iframeFriendly) {
                                    var l = f.createFrame(o);
                                    m.appendChild(l);
                                    n.parentNode.replaceChild(m, n);
                                    f.loadFrame(l, o.html);
                                } else {
                                    m.innerHTML = o.html;
                                    var g = m.getElementsByTagName("SCRIPT");
                                    for (var l = 0; l < g.length; l++) {
                                        var q = document.createElement("SCRIPT");
                                        var p = g[l].attributes;
                                        for (var h = 0; h < p.length; h++) {
                                            q[p[h].nodeName] = p[h].value
                                        }
                                        if (g[l].innerHTML) {
                                            q.text = g[l].innerHTML;
                                        }
                                        m.replaceChild(q, g[l])
                                    }
                                    n.parentNode.replaceChild(m, n)
                                }
                            }
                        }
                    }
                }

                //CSDN.track.addAdvs();
                
                 if(f.isExitsFunction('CSDN.track.addAdvs')){
                	 CSDN.track.addAdvs();
                 }
                 
            },
            //是否存在指定函数
            isExitsFunction: function (funcName) {
                try {
                    if (typeof(eval(funcName)) == "function") {
                        return true;
                    }
                } catch (e) {
                }
                return false;
            },
            GetQueryString: function (name) {
                var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
                var r = window.location.search.substr(1).match(reg);
                if (r != null) return unescape(r[2]);
                else return null;
            },
            //获取用户ID
    		getUserId:function(){
    			var result=/(; )?(UserName|_javaeye_cookie_id_)=([^;]+)/.exec(d.cookie);
    			var uid= (result != null ? result[3] : void 0) || '-';
    			return uid;
    		},
    		//提交数据
    		sendData:function(adv,url){
    			adv.data.uid=f.getUserId();
    			protocol="http:";
    			adv.data.ck='-';
    			var dataStr=this.paramData(adv.data);
    			var img =new Image();
    			img.onload=img.onerror=function(){
    				img.onload=img.onerror=null;
    				img=null;
    			}
				img.src=protocol+url+dataStr;
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
        };
        try {
            f.start()
        } catch (b) {
            console.log(b)
        }
    }
})(document, window);
