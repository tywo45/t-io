var editor_change_ask_o_domain = "";
function editor_change_ask_o(str){
    var editor = $( document ).data('editor_all');
    //editor.render(document.getElementById('editor_all'));
    window.cmDrawImageUp(editor, str);

    if (editor_change_ask_o_domain != "") {
        document.domain = editor_change_ask_o_domain;
        editor_change_ask_o_domain = "";
    }
}
(function(){
    $(function(){
        var prot = window.location.protocol;
        var afont = prot + "//ask.csdn.net/assets/ask_float_fonts_css-6b30a53970eb5c3a2a045e3df585b475.css";//该样式文件名称要根据每次发布后的动态名称来修改。//font-awesome.min-58872ef19fd8eb4c71bc6667c200b0c0.css";
        $('body').append('<link rel="stylesheet" type="text/css" media="screen" href="' + afont + '">');

        var first_pop_str="<div class='pop_edit ask_first comm_ask_first' style='display: none;'><h3>提问</h3><a href='#' nodetype='close' class='close'>×</a>"+
            " <div class='context'>"+
            " <form id='form' nodetype='form-popup' class='form'>"+
            "<div class='askFirst_desc'><span class='afd_p'>CSDN 问答是一个真诚、认真、有活力的技术社区。</span><br><span class='afd_p'>我们很愿意帮助您，希望您提出的每一个问题都能得到解答，同时也希望您知道，一个好的问题</span><br><span"+
            " class='afd_p'>更容易得到好的答案<span>&nbsp;&nbsp;</span><a href='//ask.csdn.net/help?#rule12' target='_blank'>怎样提出一个好的问题？</a></span><br>"+
            "<span class='afd_p'>提问时应遵循认真、简洁、明确、规范的原则</span><br><span"+
            " class='afd_p'>• 首先确定您是否需要使用悬赏提问；</span><br><span class='afd_p'>• 问题中没必要出现“大神帮帮忙”、“教授救救我”之类与技术问题无关的信息，及时采纳最佳答案就是对回答人的最好认可；</span><br><span"+
            " class='afd_p'>• 问题标题要有明确而有价值的信息，“有人遇到过这种情况吗？帮忙进来看看”这种标题只会降低您的问题被回答的概率；</span><br><span"+
            " class='afd_p'>• “求代码”类的问题请您悬赏提问，我们乐于将其看成一种交易。</span><br><br><span"+
            " class='afd_p'>问答频道并不欢迎这种问题</span><br><span"+
            " class='afd_p'>• 和已有问题完全重复的问题，为避免重复，提问前请先进行搜索；</span><br><span class='afd_p'>• 招聘 / 求职、交易 / 合作、寻人 / 征友；</span><br><span"+
            " class='afd_p'>• 无明确答案的调查类问题。如苹果和谷歌，大家更欣赏谁？</span><br><span class='afd_p'>想学会更好的提问，请查看<span>&nbsp;</span><a"+
            " href='//ask.csdn.net/help' target='_blank'>问答频道提问规范</a></span></div>"+
            "<div class='askFirst_se'><span>提问前请搜索</span></div>"+
            "<div class='search_div'><input id='askInput' type='text' placeholder='输入问题，查看是否存在相似的问题'></div></form> </div>"+
            " <div class='success'></div></div>";
        var second_pop_str="<div class='pop_edit ask_second comm_ask_second'><h3>提问</h3><span class='ask_float_span'>您的问题将会被发布在“<a class='ask_float_channel' href='//ask.csdn.net' target='_blank' style='cursor:pointer'>技术问答</a>”频道</span><a href='#' nodeType='close' class='close'>×</a><div class='context'><div class='err_div'><span class='err_ico'></span>"+
            "<span class='err_txt'>该问题已存在，请勿重复提问</span></div>"+
            "<div class='input_div'><input id='askInputSecond' type='text' style='font-size:14px;' placeholder='问题标题'/></div>"+
            "<div class='cm_box'><div class='cm_dialog'></div> <div class='pop_cm cm_add_link'><input type='text' placeholder='链接内容' id='af_cm_link_txt'><input type='text' placeholder='链接地址' id='af_cm_link_url'><input type='text' placeholder='链接提示' id='af_cm_link_tit'><div class='text-right'><span class='btn btn-default btn-sm' id='add_link_btn'>插入链接</span> </div> </div>"+
            "<div class='pop_cm cm_add_img'><div class='nav-tabs'><a class='img_tab active' href='#tab_upload'>本地上传</a><a class='img_tab' href='#tab_weburl'>网络图片</a></div><div class='tab_panel active' id='tab_upload'><div class='set_img'><iframe src='http://ask.csdn.net/upload.html'></iframe></div></div><div class='tab_panel' id='tab_weburl'><input type='text' placeholder='图片地址' id='af_cm_img_url'><input type='text' placeholder='图片说明' id='af_cm_img_alt'><div class='text-right'><span class='btn btn-default btn-sm' id='add_img_btn'>插入图片</span> </div></div> </div></div> "+
            "<textarea id='editor_all' rows='8'></textarea>"+
            "<div class='div_tags clearfix'><div id='divSearchTags' class='tags_con'>"+
            "<input type='text'/></div><input type='hidden' name='txtSearchTags'/></div>"+
            "<div id='ask2_tagRecomm_div' class='drt_tagRecomm tracking-ad' data-mod='popu_73'><span class='drt_tit'>推荐标签：</span></div></div>"+
            "<div class='success'><div class='left_area'><input id='chk_cb' type='checkbox'/><span class='wyxs'>我要悬赏</span><input id='cb_num'  class='cb_num' readonly='true'/>"+
            "<span class='phib_rii'><span> 币</span></span></div><a href='#' nodeType='cancel' class='cancel'>取消</a><a href='#' nodeType='ok' class='ok'>发布</a></div></div>";

        //该值是返回顶部到上面的距离，大约在%55处
        var back_top_value =Math.ceil(document.documentElement.clientHeight *0.55);
        //现在我们开始加入二维码功能，然后把提问改为app下载
        if(window.quickReplyflag) {
            //注释的部分是以前提问的，这个时候其实后续的很多与这个按钮相关的代码没用了，就是那些弹出的提问框，先不删除吧。
            /* var float_ask_img="<div data-mod='popu_64' class='tracking-ad' chg-blk='0' ><a id='com-ask-float-block'  title='提问按钮' style='top:"+(back_top_value-82)+"px'></a>"+
             "<a id='com-quick-reply'  title='快速回复' style='top:"+(back_top_value-41)+"px'></a><a id='com-d-top-a'  style='top:"+back_top_value+"px' title='返回顶部' onclick='' ></a> </div>";*/
            /*var float_ask_img="<div data-mod='popu_64' class='tracking-ad' chg-blk='0' ><a id='com-appcode-float-block' href='http://www.csdn.net/app/' target='_blank' title='APP二维码' style='top:"+(back_top_value-160)+"px'><span id='appcode-close'></span></a><a id='com-app-float-block' href='http://www.csdn.net/app/' title='APP下载' style='top:"+(back_top_value-76)+"px'></a>"+
                "<a id='com-quick-reply'  title='快速回复' style='top:"+(back_top_value-38)+"px'></a><a id='com-d-top-a'  style='top:"+back_top_value+"px' title='返回顶部' onclick='' ></a> </div>";*/
            var float_ask_img="<div data-mod='popu_64' class='csdn-tracking-statistics' chg-blk='0' ><a id='com-quick-reply'  title='快速回复' style='top:"+(back_top_value-76)+"px'></a><a id='com-quick-collect'  title='我要收藏' style='top:"+(back_top_value-38)+"px'></a><a id='com-d-top-a'  style='top:"+back_top_value+"px' title='返回顶部' onclick='' ></a> </div>";
        } else if (window.eduCallCenter) {
            var float_ask_img="<div data-mod='popu_64' class='csdn-tracking-statistics' chg-blk='0' >" +
                /*"<a id='com-appcode-float-block' href='http://edu.csdn.net/app/' target='_blank' title='APP二维码' style='display:none;top:"+(back_top_value-135)+"px; background: url(http://edu.csdn.net/images/weix.png) no-repeat top left;background-size:100% auto; '><span id='appcode-close' style=''></span></a> <a id='com-app-float-block'  title='APP下载' style='top:"+(back_top_value-76)+"px'></a>"+
*/                "<a id='com-edu-call-center'  title='学院客服' style='top:"+(back_top_value-38)+"px' target='_blank' href='http://wpa.qq.com/msgrd?v=3&amp;uin=3390397209&amp;site=qq&amp;menu=yes'></a>"+
                "<a id='com-d-top-a'  style='top:"+back_top_value+"px' title='返回顶部' onclick='' ></a></div>";
        } else if (window.bbsInfoflag) { 
            var float_ask_img="<div data-mod='popu_64' class='csdn-tracking-statistics' chg-blk='0' >"+
                "<a id='com-quick-reply-bbs' title='快速回复' style='top:"+(back_top_value-152)+"px'></a>" +
                "<a id='com-quick-collect-bbs' title='我要收藏' style='top:"+(back_top_value-114)+"px'></a>" +
                "<a id='com-back-channel' title='回到频道' style='top:"+(back_top_value-76)+"px'></a>" +
                "<a id='com-back-home' title='回到首页' style='top:"+(back_top_value-38)+"px'></a>" +
                "<a id='com-d-top-a' style='top:"+back_top_value+"px' title='返回顶部' onclick='' ></a> </div>";
        }
        //再次重新加入ask供商务项目那边使用20150610 
        else if (window.askForBussenessFlag) {
            var float_ask_img="<div data-mod='popu_64' class='csdn-tracking-statistics' chg-blk='0' >"+
                 "<a id='com-ask-float-block'  style='top:"+(back_top_value-38)+"px' title='我要提问' onclick='' ></a>"+
                "<a id='com-d-top-a'  style='top:"+back_top_value+"px' title='返回顶部' onclick='' ></a></div>";
        }
        else {
            var float_ask_img="<div data-mod='popu_64' class='csdn-tracking-statistics' chg-blk='0' >"+
                "<a id='com-d-top-a'  style='top:"+back_top_value+"px' title='返回顶部' onclick='' ></a></div>";
        }

        //搜索建议的HTML
        var common_ask_div_sc="<div id=\"common_ask_div_sc\" class=\"searchContainer\"><div class=\"sTitle\">可能存在类似的问题：</div><div class=\"sFooter\"><a class=\"sFirstNewAsk\">我想提一个新问题</a></div></div>"
        var total_ask_str=float_ask_img+second_pop_str+common_ask_div_sc;//float_ask_img+first_pop_str+second_pop_str+common_ask_div_sc
        $("body").append(total_ask_str);

        //点击关闭二维码

        $("#com-appcode-float-block #appcode-close").click(function(){
            $("#com-appcode-float-block").hide('slow');
			return false;
            //$("#com-appcode-float-block").remove();

        })

        //学院APP下载

        $("#com-app-float-block").click(function(){
            if (window.eduCallCenter){
                $("#com-appcode-float-block").show();
            }
        });

        $( 'body').bind( 'click', function ( evt ) {
            $target = $( evt.target );
            if ( $target.attr( 'id') == 'add_img_btn' )
            {
                cmDrawImage(editor, "af_");
                return false;
            }else if( $target.attr( 'class') == 'img_tab' ){
                $target.addClass('active').siblings('.img_tab').removeClass('active');
                var _target = $target.attr('href');
                $(_target).addClass('active').siblings('.tab_panel').removeClass('active');
            }else if( $target.attr( 'id') == 'add_link_btn' ) {
                cmDrawLink(editor, "af_");
                return false;
            }else if( $target.attr( 'class') == 'cm_dialog'){
                $('.pop_cm').hide(300);
                $('.cm_dialog').hide();
                if (editor_change_ask_o_domain != "") {
                    document.domain = editor_change_ask_o_domain;
                    editor_change_ask_o_domain = "";
                }
            }else if ($target.hasClass('icon-link')){     //icon-link
                var $ft = $target.parents()[1];
                //var $ft = $tb.parents()[0];
                $(".cm_dialog", $ft).fadeIn(300);
                $(".cm_add_link", $ft).slideDown(300);
                $(".cm_add_img", $ft).slideUp(300);

            }else if ($target.hasClass('icon-picture')){     //icon-link
                var $ft = $target.parents()[1];
                //var $ft = $tb.parents()[0];
                $(".cm_dialog", $ft).fadeIn(300);
                $(".cm_add_link", $ft).slideUp(300);
                $(".cm_add_img", $ft).slideDown(300);
                editor_change_ask_o_domain = document.domain;
                document.domain = "csdn.net";
            }
        } )

        //判断是不是有快速回复，返回顶部start
        $("#com-quick-reply").click(function(){
            if($("#comment_content").length>0){
                setEditorFocus();
            }
            else{
                window.location.href = "https://passport.csdn.net/";
            }
        });
        //判断是不是有我要收藏，返回顶部start
        $("#com-quick-collect").click(function(){
          if($("#comment_content").length>0){
            if($('body').find('.pop_CA').length>0){
              $('#fa').attr('src', fasrc);
              $('.pop_CA').show();
              $('.pop_CA_cover').show();
            }else{
              console.log('No popups!');
            }
          }
          else{
            window.location.href = "https://passport.csdn.net/";
          }
        });


        var d_top = $('#com-d-top-a');

        $(document).scroll(function(){
            var scrTop = (document.body.scrollTop || document.documentElement.scrollTop);
            if (scrTop > 500)
            {
                d_top.show();
            } else
            {
                d_top.hide();
            }

        })
        $('#com-d-top-a').click(function ()
        {
            scrollTo(0, 0);
            this.blur();
            return false;
        });
        //判断是不是有快速回复，返回顶部end

        var coin_flag=false;
        //var prot = window.location.protocol;
        //判断是否首次点击提问按钮
        var check_accept_rules_url = prot + "//ask.csdn.net/users/check_accept_rules.js";
        //提问时，标题录入框的自动完成
        var ask_topic_autocomplete_url = prot + "//ask.csdn.net/questions/autocomplete.js";
        //提问时，光标离开标题后的推荐标签
        // var ask_topic_tag_suggest_url = " http://ask.csdn.net/questions/tag_autocomplete.js";
        var ask_topic_tag_suggest_proxy = prot + "//internalapi.csdn.net/proxy.html";
        var ask_topic_tag_suggest_url = prot + "//internalapi.csdn.net/myapi/ask_topic/ask_topic_tag_suggest";//?x-acl-token=Yz3GAP4stGOIrLaIrnDed-uThp8K";
        //提问时，发布问题的地址
        var create_question_proxy = prot + "//ask.csdn.net/proxy.html";
        var create_question_url = prot + "//ask.csdn.net/questions/create_question.json";
        //验证C币
        var valid_coin=   prot + "//ask.csdn.net/users/get_scores.js";
        //问题链接跳转
        var ask_detail_url =  prot + "//ask.csdn.net/questions/";
        var ajaxProxyCaches = {};

        var isSubmitting = false;


        var editor = new Editor();
        $( document ).data( 'editor_all', editor );
        editor.render(document.getElementById('editor_all'));

        function ajaxProxy(proxyUrl, opts) {
            var c = ajaxProxyCaches[proxyUrl];
            if (c === undefined) {
                c = ajaxProxyCaches[proxyUrl] = [];
                var func = arguments.callee;
                $('<iframe class="poxy_uc" src="' + proxyUrl + '" style="display:none">').load(function () {
                    c.contentWindow = this.contentWindow;
                    func(proxyUrl, opts);
                }).prependTo('body');
            } else if (c.contentWindow === undefined) {
                c.push(opts);
            } else {
                do {
                    c.contentWindow.jQuery.ajax(opts);
                } while (opts = c.shift());
            }
        }

        //json = {status:false, error:1, msg: '请先登录'}
        //json = {status:false, error:2, msg: '您已被CSDN问答禁言'}
        //json = {status:false, error:3, msg: '您已被封号'}
        //json = {status:true, error:0, msg: '您已经登录'}

        //是否首次点击提问
        $("#com-ask-float-block").bind('click', function(){
            //$('.editor-preview.editor-preview-active').removeClass('editor-preview-active');
            //$('.icon-preview.active').removeClass('active');
            //document.domain="csdn.net";
            //pop($("body > .ask_second"));
            //editor.codemirror.getValue = "";
            $(".ask_second").remove();
            $("body").append(second_pop_str);
            editor = new Editor();
            $( document ).data( 'editor_all', editor );
            editor.render(document.getElementById('editor_all'));


            $("#askInputSecond").focus();//
            $(".CodeMirror").removeClass("CodeMirror-focused");
            $(".comm_ask_second .CodeMirror textarea").blur();
            //$(".CodeMirror-code, .CodeMirror-measure").html("");
            $.ajax({
                type: "get",
                url: check_accept_rules_url, //"http://ask.csdn.net/users/check_accept_rules",//判断是否登录，
                dataType: "jsonp",
                jsonp: "callback",
                success: function(obj) {

                    if (obj.error == 0) {
                        pop($("body > .ask_second"));

                        $(".CodeMirror").removeClass("CodeMirror-focused");
                    } else if (obj.error == 1) {
                        window.location.href = "https://passport.csdn.net/";
                    } else {
                        alert(obj.msg);
                    }
                },
                error: function(err) {
                    var i = 0;
                }
            });

        });

        function pop($popup){
            //遮罩
            var w = $( document ).width();
            var h = $( document ).height();
            var $marker = $( '<div class="marker"></div>' );
            if ( $.browser && $.browser.msie) {
                w = w - 18;
            }

            $marker.css( {
                opacity: 0.5,
                width: w + 'px',
                height: h + 'px'
            } );

            $( 'body' ).append( $marker );
            //位置及弹出

            //var top = $( window ).height() / 2 + $( window ).scrollTop();
            //var top =document.documentElement.clientHeight / 2 + $( window ).scrollTop();

            /*$popup.css( {
             top: top + 'px',
             left:"50%"

             } );*/

            setTimeout( function () {
                $popup.show();
                $popup.css( {
                    opacity: 1
                } );
                $(".CodeMirror").removeClass("CodeMirror-focused");
            }, 200 );
            //初始化基本的事件绑定等
            //取消事件绑定

            var $cancel=$popup.find( '[nodetype="cancel"]' );
            var $close = $popup.find( '[nodetype="close"]' );
            $cancel.add($close).unbind( 'click' );
            $cancel.add($close).bind( 'click', function () {
                $("#common_ask_div_sc").css("display","none");
                $popup.css( {
                    opacity: 0
                } );
                var $btn_sub = $popup.find( '[nodetype="ok"]' );
                $btn_sub.css("background-color", "#be0000");
                isSubmitting = false;
                setTimeout( function () {
                    $popup.hide();
                }, 350 );
                $( '.marker' ).remove();
                return false;
            } );
            //提交事件绑定，需要判断是不是第二个弹窗

            if($popup.hasClass("ask_first")){

                //绑定自动搜索
                AskSearch({ $inputBox: $(".comm_ask_first #askInput"),
                    $container: $("body > #common_ask_div_sc"),
                    searchUrl: ask_topic_autocomplete_url, //"http://192.168.6.227:9403/ask_topic/autocomplete", //搜索的Url
                    isFirst: true

                })
            }

            else{
                //还远重设值
                $(".err_div").css("display", "none");
                $(" .comm_ask_second #askInputSecond").val("");
                $(" .comm_ask_second #wmd-input").val("");
                $(" .comm_ask_second span.tag").remove();
                //$(" .comm_ask_second  #ask2_tagRecomm_div label").remove();
                $(" .comm_ask_second  #ask2_tagRecomm_div a").remove();
                $(".comm_ask_second #chk_cb").attr("checked",false);
                $(".comm_ask_second #cb_num").attr("readonly",true);
                $(" .comm_ask_second #cb_num").val("");
                $(" .comm_ask_second #wmd-preview").empty();
                $(" .comm_ask_second [name='txtSearchTags']").val("");
                /*$(" .comm_ask_second #wmd-preview").css("display","none");
                 $(" .comm_ask_second #wmd-input").css("display","block");*/


                //给标签加上foucs效果
                $(" .comm_ask_second #divSearchTags input").focus(function(){
                    $(" .comm_ask_second #divSearchTags").addClass('focus');
                }).blur(function(){
                    $(" .comm_ask_second #divSearchTags").removeClass('focus');
                })
                //悬赏的开启
                $(".comm_ask_second #chk_cb").click(function(){
                    if ($(".comm_ask_second #chk_cb")[0].checked) {
                        $(".comm_ask_second #cb_num").attr("readonly",false)

                    }
                    else{
                        $(".comm_ask_second #cb_num").attr("readonly",true);
                        $(".comm_ask_second #cb_num").val("");
                        coin_flag = false;
                    }
                })
                var $submit = $popup.find( '[nodetype="ok"]' );
                $submit.unbind('click');
                $submit.bind( 'click', function () {
                    var _this = this;
                    if (isSubmitting) return;
                    isSubmitting = true;
                    $(this).css("background-color", "#999");
                    // data = _this.getBackData();需要自行序列化值
                    var _title = $(".comm_ask_second #askInputSecond").val();
                    var _content = editor.codemirror.getValue();
                    /* if (_content) {
                     _content = _content.replace(/\<br\>/g, "\n");
                     _content = _content.replace(/\<p\>/g, "");
                     _content = _content.replace(/\<\/p\>/g, "");
                     }*/
                    var _tagList = $(".comm_ask_second [name='txtSearchTags']").val();
                    var _fromType = window.location.hostname;//"read_index";

                    var _isreward = $(" .comm_ask_second #chk_cb")[0].checked;

                    var _coin = $(" .comm_ask_second #cb_num").val();

                    //验证
                    $(".err_div").css("display", "none");
                    if (!valid($(" .comm_ask_second #askInputSecond"),_title,"问题标题",10,50)){

                        return false;
                    }


                    if( !valid($(" .comm_ask_second #wmd-input"),_content,"问题描述",30,10000)){
                        return false;
                    };
                    if( !valid( $(" .comm_ask_second #divSearchTags"),_tagList.split(','),"问题标签",1,5)){
                        $(" .comm_ask_second #divSearchTags").addClass('focus');
                        return false;
                    };

                    //那个C币那个如果勾选了才验证是不是数字,还需验证C币的值够不够
                    var _cbNum = $(".comm_ask_second #cb_num").val();
                    if ($(".comm_ask_second #chk_cb")[0].checked) {
                        if (!/^[1-9]\d*$/.test(_cbNum)) {
                            $(".err_div").css("display", "block");
                            $(".err_txt").text("我要悬赏只能录入数字！");
                            $(".comm_ask_second #cb_num").focus();

                            $(_this).css("background-color", "#be0000");
                            isSubmitting = false;
                            return false;
                        }
                    }

                    function valid(fild,fild_value,name,minL,maxL){

                        if((fild_value && fild_value[0] == "") || fild_value.length==0){
                            mess = '请输入' + name;
                            $(".err_div").css("display", "block");
                            $(".err_txt").text(mess);
                            fild.focus();

                            $(_this).css("background-color", "#be0000");
                            isSubmitting = false;

                            return false;
                        }
                        if(fild_value.length<minL||fild_value.length>maxL){
                            mess = '请输入长度为' +minL+"-"+maxL+"的"+name;

                            $(".err_txt").text(mess);
                            $(".err_div").css("display", "block");
                            fild.focus();

                            $(_this).css("background-color", "#be0000");
                            isSubmitting = false;

                            return false;
                        }
                        else{
                            return true;
                        }
                    }
                    //验证C币不够不许提交
                    if(coin_flag){
                        $(".err_div").css("display", "block");
                        $(_this).css("background-color", "#be0000");
                        isSubmitting = false;
                        return false;
                    }
                    var tempDomain = document.domain;
                    document.domain = 'csdn.net';
                    //_fromType = "";
                    //留意该接口请求类型,异步提交数据，在该次加入了验证的能力，
                    //$.ajax({
                    //	type: "get",
                    ajaxProxy(create_question_proxy, {
                        type: "post",
                        data: {
                            "question[title]": _title,
                            "question[body]": _content,
                            "question[tag_list]": _tagList,
                            "question[from_type]": _fromType,
                            "question[is_reward]": _isreward,
                            "question[coin]": _coin
                        },
                        url: create_question_url,
                        dataType: "json",
                        //dataType: "jsonp",
                        //jsonp: "callback",
                        //async: false,
                        success: function(obj) {
                            document.domain = tempDomain;
                            if (obj.status) {
                                //跳转到新页面,关闭,重设这个值当前页面
                                $(".comm_ask_second").css("display","none");
                                $(_this).css("background-color", "#be0000");
                                isSubmitting = false;
                                $( '.marker' ).remove();
                                var href="//ask.csdn.net/questions/"+obj.id;
                                window.open(href, "target=_blank,location=1");
                                alert("你的问题已发布到技术问答频道。");
                            } else {
                                $(".err_div").css("display", "block");
                                $(".err_txt").text(obj.msg);
                                $(_this).css("background-color", "#be0000");
                                isSubmitting = false;
                            }
                        },
                        error: function(err) {
                            document.domain = tempDomain;
                            var i = 0;
                        }
                    });

                    //需要调用关闭这个窗口
                    return false;
                } );
                //绑定自动搜索
                AskSearch({    $inputBox: $(".comm_ask_second #askInputSecond"),
                    $container: $("body > #common_ask_div_sc"),
                    searchUrl: ask_topic_autocomplete_url, //"http://192.168.6.227:9403/ask_topic/autocomplete", //搜索的Url
                    isFirst: false
                });

                $(".comm_ask_second .CodeMirror textarea").bind("focus", function() {
                    $(".CodeMirror").addClass("CodeMirror-focused");
                });

                //动态去后台获取推荐标签start
                $(".comm_ask_second #askInputSecond").bind("blur", function(){
                    var _title = $(".comm_ask_second #askInputSecond").val();
                    var _content = editor.codemirror.getValue();//$(".comm_ask_second #wmd-input").val();
                    /* if (_content) {
                     _content = _content.replace(/\<br\>/g, "\n");
                     } else {
                     _content = "";
                     }*/
                    setTimeout(function(){$("#div_sc").css("display", "none");},400);
                    loadRecommTags( _title, _content);
                });
                $(".comm_ask_second .CodeMirror textarea").bind("blur", function(){
                    var _title = $(".comm_ask_second #askInputSecond").val();
                    var _content = editor.codemirror.getValue();//$(".comm_ask_second #wmd-input").val();
                    /*if (_content) {
                     _content = _content.replace(/\<br\>/g, "\n");
                     } else {
                     _content = "";
                     }*/
                    loadRecommTags( _title, _content);
                });
                //动态去后台获取推荐标签end
                //异步后台验证C币足够？
                $(".comm_ask_second #cb_num").bind("blur",function(){

                    if($(".comm_ask_second #chk_cb")[0].checked){
                        $(".err_div").css("display", "none");
                        if (!/^[0-9]\d*$/.test( $(".comm_ask_second #cb_num").val())) {
                            $(".err_div").css("display", "block");
                            $(".err_txt").text("我要悬赏只能录入数字！");
                            $(".comm_ask_second #cb_num").focus();
                            return false;
                        }
                        $.ajax({
                            type: "get",
                            url: valid_coin,
                            dataType: "jsonp",
                            jsonp: "callback",
                            async: false,
                            success: function(obj) {
                                if (obj.error==0) {
                                    //这个状态下表示拿到了C币
                                    if( $(".comm_ask_second #cb_num").val()>obj.score){
                                        $(".err_div").css("display", "block");
                                        $(".err_txt").text("您的C币余额不足");
                                        //验证不通过不许提交
                                        coin_flag=true;
                                    }
                                    else{
                                        coin_flag=false;
                                    }
                                } else {
                                    $(".err_div").css("display", "block");
                                    $(".err_txt").text(obj.msg);
                                    coin_flag=true;
                                }
                            },
                            error: function(err) {
                                // alert("后台出错了");
                            }
                        });}

                })
                //提问的自动TAG,需要你自行粘贴那个JS文件
                Creat_Dynamic_Tag(".comm_ask_second #divSearchTags", ask_topic_tag_suggest_url, ".comm_ask_second input[name='txtSearchTags']");

            }

        }//close pop function

        function ShowSecondPop() {
            // askFirst.popup.closeFun();隐藏起来

            $("body > .ask_first").find( '[nodetype="close"]' ).click();//隐藏起来第一个

            pop($("body > .ask_second"));
        }

        //自动搜索提示
        function AskSearch(conf) {

            $("body").click(function(e){

                var target=  e.target;

                if($(target).closest("#common_ask_div_sc").length<1){
                    conf.$container.css("display","none");
                }

            })

            var $inputBox = conf.$inputBox;
            var $container = conf.$container;
            var searchUrl = conf.searchUrl;
            var isFirst = conf.isFirst;  //调用搜索的是首次弹出窗还是提问弹出窗
            var nowItem = -1;
            var userInput = "";
            $container.css("display", "none");
            $inputBox.css("autocomplete", "off");
            $container.css("z-index", "1100");

            //$inputBox.unbind("keyup");
            $inputBox[0].onkeyup=function(ev) {//propertychange
                var oEvent = ev||event;
                if(oEvent.keyCode == 40 || oEvent.keyCode == 38 || oEvent.keyCode == 27 || (oEvent.keyCode >= 113 && oEvent.keyCode <= 123) ||
                    oEvent.keyCode == 16 || oEvent.keyCode == 17 || oEvent.keyCode == 18 || oEvent.keyCode == 9 || oEvent.keyCode == 20){
                    return;
                }
                $container.css("opacity", 0);
                var rect = this.getBoundingClientRect();
                var scrTop = (document.body.scrollTop || document.documentElement.scrollTop);
                $container.css("left", rect.left);
                $container.css("top", rect.bottom + scrTop);//rect.top + rect.height
                //$container.css("top", rect.bottom);
                var inVal = this.value;
                if ( this.value.length > 0 ) {
                    showHint(inVal,$container,$inputBox,isFirst);
                } else {
                    $container.css("display", "none");
                }
                userInput = inVal;

            };
            //$inputBox.unbind("keydown");
            $inputBox[0].onkeydown=function(ev){
                var evt = ev||event;
                if (evt.keyCode == 27) {
                    $container.css("display", "none");
                    $inputBox.val(userInput);
                }
                if (evt.keyCode == 13) {
                    $container.css("display", "none");
                }
                if (evt.keyCode == 40) {
                    nowItem ++;
                    var suggItems = $(".sItem", $container);
                    var itemCnt = suggItems.length;
                    if ( nowItem === itemCnt) {
                        nowItem = -1;
                    }
                    suggItems.removeClass("sItemHover");
                    if (nowItem != -1) {
                        var nowSugg = suggItems.eq(nowItem);
                        nowSugg.addClass("sItemHover");
                        $inputBox.val($(".sfirst", nowSugg).text());
                    } else {
                        $inputBox.val(userInput);
                    }
                }
                if (evt.keyCode == 38) {
                    nowItem --;
                    var suggItems = $(".sItem", $container);
                    var itemCnt = suggItems.length;
                    if (nowItem == -2) {
                        nowItem = itemCnt - 1;
                    }
                    suggItems.removeClass("sItemHover");
                    if (nowItem != -1) {
                        var nowSugg = suggItems.eq(nowItem);
                        nowSugg.addClass("sItemHover");
                        $inputBox.val($(".sfirst", nowSugg).text());
                    } else {
                        $inputBox.val(userInput);
                    }
                }
            };
        }
        //以下方法是AskSerch的原型方法拆解
        function showHint(inVal,$container,$inputBox,isFirst) {
            $container.css("display", "block");
            $.ajax( {
                type: "get",
                url: ask_topic_autocomplete_url, //"http://192.168.6.210//mcs/problem_solving/10710/1",
                data: {query: inVal, fields:"title,id,comments"},
                dataType: 'jsonp',
                jsonp: "callback",
                async: false,
                success: function (obj) {
                    if (obj != null && obj.contents) {
                        fillSuggestData($container, obj.contents,$inputBox,isFirst);
                    }
                },
                error: function(err){
                    var i = 0;
                    //alert('err');
                }
            } );
        }

        function fillSuggestData ($container, items,$inputBox,isFirst) {
            if (isFirst) {
                $container.html("<div class=\"sTitle\">可能存在类似的问题：</div><div class=\"sFooter\"><a class=\"sFirstNewAsk\">我想提一个新问题</a></div>");
            } else {
                $container.html("<div class=\"sTitle\">可能存在类似的问题：</div>");
            }
            var tpl = '<div class="sItem clearfix"><a href="{2}" target="_blank"><span class="sfirst">{0}</span><span class="ssecond">{1}</span></a></div>';

            if (items.length > 0) {
                $container.css("opacity", 1);
                //$container.css("display", "none");
            }
            for (var i = 0; i < items.length; i++){
                var item = items[i];
                var itemHtml = tpl.replace(/\{0\}/, item.title)
                    .replace(/\{1\}/, item.comments + "个回答")
                    .replace(/\{2\}/, ask_detail_url + item.id);

                if (isFirst) {
                    $(".sFooter", $container).before(itemHtml);
                } else {
                    $container.append(itemHtml);
                }
            }
            if (isFirst) {
                $("#common_ask_div_sc .sFirstNewAsk").unbind("click");
                $("#common_ask_div_sc .sFirstNewAsk").bind("click", function(){
                    ShowSecondPop();
                });
            }
            $(".sItem", $container).each(function(i, item) {
                    var _i = i;
                    var _item = $(item);
                    _item.bind("mouseover", function() {
                        $(".sItem", $container).removeClass("sItemHover");
                        $(this).addClass("sItemHover");
                        nowItem = _i;
                    });
                    _item.bind("click", function() {
                        //$inputBox.val($(".sfirst", $(this)).text());
                        $container.css("display","none");
                    });
                }
            );
        }
        //获取后台推荐标签
        function  loadRecommTags( _title, _content){
            if (_title == "" && _content=="") return;
            var dataStr = JSON.stringify({title: _title, body: _content, size: 4});
            //$.ajax({

            var tempDomain = document.domain;
            document.domain = 'csdn.net';
            ajaxProxy(ask_topic_tag_suggest_proxy, {
                headers: {
                    'X-acl-token': 'Yz3GAP4stGOIrLaIrnDed-uThp8K'
                },
                type: "POST",
                url: ask_topic_tag_suggest_url + "?dataType=json",
                data: dataStr,//{title: _title, body: _content, size: 4},
                contentType: "application/json",
                dataType: "json",
                //dataType: "jsonp",
                //jsonp: "callback",
                //contentType:'application/x-www-form-urlencoded; charset=UTF-8',
                success: function(obj) {
                    document.domain = tempDomain;
                    if (obj!=null && obj.length > 0) {
                        var $dataContainer = $( ".comm_ask_second #ask2_tagRecomm_div");
                        $dataContainer.html("<span class=\"drt_tit\">推荐标签：</span>");
                        //var tpl = "<label class=\"se_key\">{0}</label>";
                        var tpl = "<a href=\"#\" class=\"se_key\">{0}</a>";

                        var items = obj;
                        for (var i = 0; i < items.length; i++){
                            var item = items[i];
                            if (item.name == "") continue;
                            var itemHtml = tpl.replace(/\{0\}/, item.name + "&nbsp;&nbsp;＋");
                            $dataContainer.append(itemHtml);
                            if (i == 4) {
                                break;
                            }
                        }
                        try {
                            window['trackingAd']($dataContainer);
                        } catch (ex) {}
                        $(".comm_ask_second .se_key").each(function(i,item){
                            var _item = $(item);
                            _item.bind("click", function(){
                                var cTagsObj = $(".comm_ask_second input[name='txtSearchTags']");
                                var cTags = cTagsObj.val();
                                var tagArr = cTags.split(',');

                                var tagText = _item.text();
                                var tagName = tagText.substring(0, tagText.length - 3);

                                for (var i = 0; i < tagArr.length; i ++) {
                                    if (tagArr[i] === tagName) {
                                        return;
                                    }
                                }
                                var oTagStr = cTags;
                                if (tagArr.length >= 5) {
                                    //$(".err_div").css("display", "block");
                                    //$(".err_txt").text("标签最多只能录入5个！");
                                    //setTimeout(function(){$(".err_div").css("display", "none");}, 5000);
                                    return;
                                    //oTagStr = "";
                                    //for (var i = 0; i < 4; i ++) {
                                    //  oTagStr += tagArr[i] + ",";
                                    //}
                                    //oTagStr = oTagStr.substring(0, oTagStr.length-1);
                                }
                                var tagCon = $(".comm_ask_second #divSearchTags");
                                var tagInput = tagCon.find("input");

                                var preNode = tagInput[0].previousSibling;
                                if (tagArr.length >= 5 && preNode) {
                                    tagInput[0].parentNode.removeChild(preNode);
                                }
                                tagInput.before('<span class="tag">'+'<span>'+tagName+'</span>'+'<a title="删除标签" href="javascript:;">'+'×'+'</a>'+'</span>');

                                var newTags = oTagStr == "" ? tagName : oTagStr + "," + tagName;
                                cTagsObj.val(newTags);
                                return false;
                            });
                        });
                    }
                },
                error: function(err){
                    document.domain = tempDomain;
                    var i = 0;
                    //alert('初始化推荐标签错误！');
                }
            });
        }

        //自动Tag
        function Creat_Dynamic_Tag(conid,dataurl,subinput)
        {
            var oTagscon=$(conid)[0];
            var tagInput=$(conid).find('input')[0];

            var iNow=-1;
            var oldValue='';
            var tags_array = [];

            var oUl = document.createElement('ul');
            oUl.className = 'complete_list';
            var oBody=document.getElementsByTagName('body')[0];
            oBody.appendChild(oUl);

            tagInput.onkeyup=function(ev)
            {
                var oEvent=ev || event;

                if(oEvent.keyCode==40 || oEvent.keyCode==38|| oEvent.keyCode==13){
                    return false;
                }

                $(this).removeAttr("style");

                if(oEvent.keyCode==8&&tagInput.value==''){

                    oUl.style.display = 'none';
                    return false;
                }

                $.ajax({
                    type: "get",
                    //url:dataurl,
                    url: "https://recommend-api.csdn.net/tag/complete?",
                    //data:{'title': tagInput.value, body: tagInput.value, size: 4},
                    data: {'query': tagInput.value},
                    dataType:"jsonp",
                    //jsonp: "callback",
                    success: function(jsonstr){
                        //获取input输入框的绝对位置
                        var tagInput_pos = getPos(tagInput);
                        var scrTop = (document.body.scrollTop || document.documentElement.scrollTop);

                        //弹出下拉框相对于input输入框向下偏移值
                        var mp =242;
                        iNow=-1;
                        oldValue=tagInput.value;

                        var data = jsonstr.suggestions;

                        if(data.length!=0)
                        {

                            var bb = $( window ).height() - tagInput_pos.top;
                            $(oUl).empty();
                            //$(oUl).css({left:tagInput_pos.left,top:tagInput_pos.top+mp+scrTop})
                            $(oUl).css({left:tagInput_pos.left,bottom:bb});
                            for(var i=0;i<data.length;i++)
                            {
                                $(oUl).append('<li>'+data[i]+'</li>');
                            }
                            $(oUl).css('display','block');
                        }

                        var aLi = oUl.children;

                        for(var i=0; i<aLi.length; i++)
                        {
                            aLi[i].index=i;

                            aLi[i].onmouseover=function(){

                                tagInput.value = this.innerHTML;

                                for(var i=0; i<aLi.length; i++){
                                    aLi[i].className='';
                                }
                                this.className = 'outborder';
                                iNow=this.index;
                            }
                        }
                    }

                });
            }

            tagInput.onblur = function()
            {
                if(tagInput.value=='')
                {
                    return false;
                }
                else
                {
                    if ($(subinput).val() != "") {
                        tags_array = $(subinput).val().split(",");
                    }
                    var re=/[^0-9a-zA-Z\u4e00-\u9fa5\-+#]+/;
                    var re2 = /\-$/;
                    var re3 = /^\-/;
                    var str_arr = tagInput.value.split(re);
                    var str = str_arr.join("-");

                    if(str=='-')
                    {
                        $(tagInput).css('background-color','#fbd8db');
                        return false;
                    }

                    if(re2.test(str))
                    {
                        str = str.substring(0,str.length-1);
                    }

                    if(re3.test(str))
                    {
                        str = str.substr(1);
                    }

                    if(find_in_arr(tags_array,str))
                    {
                        $(tagInput).css('background-color','#fbd8db');
                        return false;
                    }
                    var tags_len = tags_array.length;
                    if (tags_len >= 5) {
                        //$(".err_div").css("display", "block");
                        //$(".err_txt").text("标签最多只能录入5个！");
                        //setTimeout(function(){$(".err_div").css("display", "none");}, 5000);
                        return;
                        //tags_array.splice(tags_len - 1, 1);
                        //_remove_last_tag(conid);
                    }

                    _add_tag(conid,str);
                    tagInput.value = '';
                    tags_array.push(str);
                    $(subinput).val(tags_array.join(','));
                    oUl.style.display = 'none';
                    return false;
                }
            }

            tagInput.onkeydown=function(ev){

                var aLi = oUl.children;

                var oEvent=ev || event;

                if(oEvent.keyCode==13||oEvent.keyCode==188){

                    if(tagInput.value=='')
                    {
                        return false;
                    }
                    else
                    {
                        if ($(subinput).val() != "") {
                            tags_array = $(subinput).val().split(",");
                        }
                        var re=/[^0-9a-zA-Z\u4e00-\u9fa5\-+#]+/;
                        var re2 = /\-$/;
                        var re3 = /^\-/;
                        var str_arr = tagInput.value.split(re);
                        var str = str_arr.join("-");
                        if(str=='-')
                        {
                            $(tagInput).css('background-color','#fbd8db');
                            return false;
                        }

                        if(re2.test(str))
                        {
                            str = str.substring(0,str.length-1);
                        }

                        if(re3.test(str))
                        {
                            str = str.substr(1);
                        }

                        if(find_in_arr(tags_array,str))
                        {
                            $(tagInput).css('background-color','#fbd8db');
                            return false;
                        }
                        var tags_len = tags_array.length;
                        if (tags_len >= 5) {
                            //$(".err_div").css("display", "block");
                            //$(".err_txt").text("标签最多只能录入5个！");
                            //setTimeout(function(){$(".err_div").css("display", "none");}, 5000);
                            return;
                            //tags_array.splice(tags_len - 1, 1);
                            //_remove_last_tag(conid);
                        }

                        _add_tag(conid,str);
                        tagInput.value = '';
                        oUl.style.display = 'none';
                        tags_array.push(str);
                        $(subinput).val(tags_array.join(','));
                        return false;
                    }
                }

                if(oEvent.keyCode==8&&tagInput.value==''){

                    _remove_last_tag(conid);
                    oUl.style.display = 'none';
                    tags_array.pop();
                    $(subinput).val(tags_array.join(','));
                }

                if(oEvent.keyCode==40){
                    iNow++;

                    if(iNow==aLi.length){
                        iNow=-1;
                    }

                    for(var i=0; i<aLi.length; i++){
                        aLi[i].className='';
                    }
                    if(iNow!=-1){
                        aLi[iNow].className='outborder';
                        tagInput.value=aLi[iNow].innerHTML;
                    }else{
                        tagInput.value=oldValue;
                    }
                }

                if(oEvent.keyCode==38){
                    iNow--;
                    if(iNow==-2){
                        iNow=aLi.length-1;
                    }

                    for(var i=0; i<aLi.length; i++){
                        aLi[i].className='';
                    }
                    if(iNow!=-1){
                        aLi[iNow].className='outborder';
                        tagInput.value=aLi[iNow].innerHTML;
                    }else{
                        tagInput.value=oldValue;
                    }
                    return false;
                }
            }

            //点击每个标签后的'x'删除当前标签

            oTagscon.onclick = function(e)
            {
                var e = e || window.event;
                var target = e.srcElement || e.target;

                if (target.title =="删除标签")
                {
                    if ($(subinput).val() != "") {
                        tags_array = $(subinput).val().split(",");
                    }
                    var cur_str = target.parentNode.children[0].innerHTML;
                    tags_array = remove_in_arr(tags_array,cur_str);
                    $(subinput).val(tags_array.join(','));
                    _remove_cur_tag($(target));
                    tagInput.focus();
                    $(tagInput).css('background-color','#fbd8db');
                    $(oUl).css('display','none');
                }
            }

            tagInput.onclick=function(ev){
                var oEvent=ev || event;
                oEvent.cancelBubble=true;
            }

            document.onclick = function()
            {
                oUl.style.display = 'none';
            }

            //查找数组中指定的元素,并且删除这个元素
            function remove_in_arr(arr,str)
            {
                for(var i=0;i<arr.length;i++)
                {
                    if(arr[i]==str)
                    {
                        arr.splice(i,1);
                    }
                }
                return arr;
            }
        }


        //查找数组中指定的元素,并且删除这个元素
        function remove_in_arr(arr,str)
        {
            for(var i=0;i<arr.length;i++)
            {
                if(arr[i]==str)
                {
                    arr.splice(i,1);
                }
            }
            return arr;
        }


        //查找数组中有没有相同的元素
        function find_in_arr(arr,str)
        {
            var find = false;

            for(var i=0;i<arr.length;i++)
            {
                if(arr[i]==str)
                {
                    find = true;
                }
            }
            return find;
        }

        //增加标签 obj为增加标签的容器，str为标签中的文字内容。
        function _add_tag(obj,str)
        {
            var tagCon = $(obj);
            var tagInput = tagCon.find('input');
            tagInput.before('<span class="tag">'+'<span>'+str+'</span>'+'<a title="删除标签" href="javascript:;">'+'×'+'</a>'+'</span>');
        }

        //删除最后一个标签
        function _remove_last_tag(obj)
        {
            var tagCon = $(obj);
            tagCon.find('.tag').last().remove();
        }

        //删除当前标签
        function _remove_cur_tag(obj)
        {
            obj.parent().remove();
        }

        function getPos(obj)
        {
            var l=0;
            var t=0;
            while(obj)
            {
                l+=obj.offsetLeft;
                t+=obj.offsetTop;
                obj=obj.offsetParent;
            };
            return {left:l,top:t}
        };

    });
})();
