/*
UI地址：\\192.168.6.119\产品管理\2_数据支撑\03_UI\职位推荐
引用方法：<script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
调用方法：csdn.position.show({
sourceType: "", //博客blog，论坛discussion_topic, 下载 download，问答ask_topic, 个人空间space？？, 英雄会hero？？, 在线培训 course, csto
tplType: "", //模板类型，
               博客详情：blogDetail,
               博客专栏：blogSpec,
               论坛详情：bbsDetail，
               问答详情：askDetail，
               个人空间--我的空间：personalSpaceMy，
               个人空间--首页：personalSpaceHome，
               英雄会--首页：heroHome
               英雄会--答题专家组：heroExpert
               英雄会--挑战题目详情页：heroFightDetail
               英雄会--我的英雄会：heroMy
               在线培训--课程分类列表：无
               在线培训--课程详情页：eduDetail
               搜索：search
               下载--我的资源：downMy
               下载--下载页和下载详情页：downDetail

               CSTO案例库列表：cstoCaseList
               CSTO案例详情页：cstoCaseDetail
               CSTO我的T台：cstoMy
               CSTO项目列表页：cstoProjectList
               CSTO项目详情页：cstoProjectDetail

searchType: "", //页面类型，用于搜索函数，detail(详情页) / list(列表页)。
searchKey: "", //搜索关键字，例如博客详情：博文ID，如果是博客专栏：分类字符串。
username: "", //当前登录用户名
containerId: "" //容器DIV的id。
});
举例：
博客详情页
<div id="job_blog_reco">
 <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
 <script type="text/javascript">
   csdn.position.show({
     sourceType: "blog",
     tplType: "blogDetail",
     searchType: "detail",
     searchKey: "博文ID",
     username: "当前登录用户名",
     containerId: "job_blog_reco"  //容器DIV的id。
   });
 </script>
</div>

博客专栏页
<div id="job_blog_reco_spec">
 <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
 <script type="text/javascript">
   csdn.position.show({
     sourceType: "blog",
     tplType: "blogSpec",
     searchType: "list",
     searchKey: "专栏分类字符串",
     username: "当前登录用户名",
     containerId: "job_blog_reco_spec"  //容器DIV的id。
   });
 </script>
</div>

论坛详情页
<div id="job_bbs_reco">
 <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
 <script type="text/javascript">
   csdn.position.show({
     sourceType: "discussion_topic",
     tplType: "bbsDetail",
     searchType: "detail",
     searchKey: "贴子ID",
     username: "当前登录用户名",
     containerId: "job_bbs_reco"  //容器DIV的id。
   });
 </script>
</div>

问答详情页
<div id="job_ask_reco">
 <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
 <script type="text/javascript">
   csdn.position.show({
     sourceType: "ask_topic",
     tplType: "askDetail",
     searchType: "detail",
     searchKey: "问题ID",
     username: "当前登录用户名",
     containerId: "job_ask_reco"  //容器DIV的id。
   });
 </script>
</div>

个人空间--我的空间
<div id="job_myspace_reco">
 <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
 <script type="text/javascript">
   csdn.position.show({
     sourceType: "my",
     tplType: "personalSpaceMy",
     searchType: "list",
     searchKey: "NO",
     username: "当前登录用户名",
     containerId: "job_myspace_reco"  //容器DIV的id。
   });
 </script>
</div>
个人空间--首页
<div id="job_myhome_reco">
 <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
 <script type="text/javascript">
   csdn.position.show({
     sourceType: "my",
     tplType: "personalSpaceHome",
     searchType: "list",
     searchKey: "NO",
     username: "当前登录用户名",
     containerId: "job_myhome_reco"  //容器DIV的id。
  });
 </script>
</div>







 英雄会-首页，正在发生的下面。
 http://hero.csdn.net/
 <div id="job_yx_home_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "hero",
   tplType: "heroHome",
   searchType: "list",
   searchKey: "NO",
   username: "当前登录用户名",
   containerId: "job_yx_home_reco"  //容器DIV的id。
   });
   </script>
 </div>

 英雄会--答题专家组，审题专家组下面。
 http://hero.csdn.net/Examine/Apply
 <div id="job_yx_expert_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "hero",
   tplType: "heroExpert",
   searchType: "list",
   searchKey: "NO",
   username: "当前登录用户名",
   containerId: "job_yx_expert_reco"  //容器DIV的id。
   });
   </script>
 </div>

 英雄会--挑战题目详情页，发布公司下面。
 http://hero.csdn.net/OnlineCompiler/Index?ID=10646&ExamID=10649&from=4
 <div id="job_yx_fight_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "hero",
   tplType: "heroFightDetail",
   searchType: "list",
   searchKey: "NO",
   username: "当前登录用户名",
   containerId: "job_yx_fight_reco"  //容器DIV的id。
   });
   </script>
 </div>


 英雄会--我的英雄会，列表的下面。
 http://hero.csdn.net/Exam/List
 <div id="job_yx_my_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "hero",
   tplType: "heroMy",
   searchType: "list",
   searchKey: "NO",
   username: "当前登录用户名",
   containerId: "job_yx_my_reco"  //容器DIV的id。
   });
   </script>
 </div>


 在线培训--课程分类列表
 <div id="job_edu_detail_reco">
 <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
 <script type="text/javascript">
 csdn.position.show({
 sourceType: "course",
 tplType: "eduDetail",
 searchType: "list",
 searchKey: "页面上的搜索类型",
 username: "当前登录用户名",
 containerId: "job_edu_detail_reco"  //容器DIV的id。
 });
 </script>
 </div>




 在线培训--课程详情页，在右侧推荐课程下边。
 http://edu.csdn.net/course/detail/326
 <div id="job_edu_detail_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "course",
   tplType: "eduDetail",
   searchType: "detail",
   searchKey: "课程ID",
   username: "当前登录用户名",
   containerId: "job_edu_detail_reco"  //容器DIV的id。
   });
   </script>
 </div>



 搜索
 http://so.csdn.net/so/search/s.do?q=java
 <div id="job_search_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "all（该关键字暂时不被使用）",
   tplType: "search",
   searchType: "list",
   searchKey: "搜索关键字",
   username: "当前登录用户名",
   containerId: "job_search_reco"  //容器DIV的id。
   });
   </script>
 </div>

 下载--我的资源
 http://so.csdn.net/so/search/s.do?q=java
 <div id="job_down_my_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "download",
   tplType: "downMy",
   searchType: "list",
   searchKey: "NO",
   username: "当前登录用户名",
   containerId: "job_down_my_reco"  //容器DIV的id。
   });
   </script>
 </div>

 下载--下载页和下载详情页
 http://so.csdn.net/so/search/s.do?q=java
 <div id="job_down_detail_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "download",
   tplType: "downDetail",
   searchType: "detail",
   searchKey: "资源ID",
   username: "当前登录用户名",
   containerId: "job_down_detail_reco"  //容器DIV的id。
   });
   </script>
 </div>

 CSTO-案例库列表，放在“上周最受欢迎”下边。
 http://www.csto.com/case
 <div id="job_csto_caselist_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "csto",
   tplType: "cstoCaseList",
   searchType: "list",
   searchKey: "筛选条件里的热门分类和热门技术，逗号分隔",
   username: "当前登录用户名",
   containerId: "job_csto_caselist_reco"  //容器DIV的id。
   });
   </script>
 </div>

 CSTO-案例详情页，放在“最近浏览过的人”下边。
 http://www.csto.com/case/show/id:21740
 <div id="job_csto_casedetail_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "csto",
   tplType: "cstoCaseDetail",
   searchType: "detail",
   searchKey: "案例ID",
   username: "当前登录用户名",
   containerId: "job_csto_casedetail_reco"  //容器DIV的id。
   });
   </script>
 </div>

 CSTO-我的T台，放在“我的资料”下边。
 http://www.csto.com/my/info/edit
 <div id="job_csto_my_reco1">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "csto",
   tplType: "cstoMy",
   searchType: "list",
   searchKey: "NO",
   username: "当前登录用户名",
   containerId: "job_csto_my_reco1"  //容器DIV的id。
   });
   </script>
 </div>

 CSTO-项目列表页
 http://www.csto.com/project/list
 <div id="job_csto_projlist_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "csto",
   tplType: "cstoProjectList",
   searchType: "list",
   searchKey: "筛选条件里的热门分类和热门技术，逗号分隔",
   username: "当前登录用户名",
   containerId: "job_csto_projlist_reco"  //容器DIV的id。
   });
   </script>
 </div>



 CSTO-项目详情页
 http://www.csto.com/p/72969
 <div id="job_csto_projdetail_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
   csdn.position.show({
   sourceType: "csto",
   tplType: "cstoProjectDetail",
   searchType: "detail",
   searchKey: "项目ID",
   username: "当前登录用户名",
   containerId: "job_csto_projdetail_reco"  //容器DIV的id。
   });
   </script>
 </div>



//------------------------------------------------------------------------------------------------------------------
课程推荐
//------------------------------------------------------------------------------------------------------------------

 搜索页的培训推荐
 http://so.csdn.net/so/search/s.do?q=java
 <div id="edu_so_reco">
   <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
   <script type="text/javascript">
       csdn.position.showEdu({
           sourceType: "so",
           searchType: "detail",
           searchKey: "搜索关键字",
           username: "当前登录用户名",
           recordcount: "4",
           containerId: "edu_so_reco"  //容器DIV的id。
       });
   </script>
 </div>


搜索结果页


 博客详情页将原来的adCollege注释掉，其相同位置放置如下div。
博客详情页
 http://blog.csdn.net/hu1991die/article/details/45564465
 <div id="adCollege" style="width: 42%;float: left;">
 <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
 <script type="text/javascript">
     csdn.position.showEdu({
         sourceType: "blog",
         searchType: "detail",
         searchKey: "博文id",
         username: "当前登录用户名",
         recordcount: "5",
         containerId: "adCollege"  //容器DIV的id。
     });
 </script>
 </div>


 下载详情页将原来的related po_down_detail_big_div注释掉，其相同位置放置如下div。
下载详情页
 http://download.csdn.net/detail/dudud20/8662993
 <div id="edu_down_reco" class="related po_down_detail_big_div">
 <script src="http://csdnimg.cn/jobreco/job_reco.js" type="text/javascript"></script>
 <script type="text/javascript">
     csdn.position.showEdu({
         sourceType: "down",
         searchType: "detail",
         searchKey: "下载资源id",
         username: "当前登录用户名",
         recordcount: "5",
         containerId: "edu_down_reco"  //容器DIV的id。
     });
 </script>
 </div>






//------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------



*/
$(document).ready(function() {
  var i = 1;
});
(function (window) {
  var csdn = window.csdn || {};
  function Position() {
    this.prefix = window.location.protocol;


    $("<link>")
      .attr({ rel: "stylesheet",
        type: "text/css",
        href: window.location.protocol + "//csdnimg.cn/jobreco/job_reco.css" //"//c.csdnimg.cn/jobreco/job_reco.css"//
      })
      .appendTo("head");

    /*
     http://blog.csdn.net/lmj623565791/article/details/42407923#t7
     http://blog.csdn.net/column.html
     http://bbs.csdn.net/topics/390963719
     http://ask.csdn.net/
     http://my.csdn.net/
     http://my.csdn.net/my/mycsdn
     http://hero.csdn.net/
    */

    //博客详情：tplType = blogDetail
    this.blogTpl = '<dl class="blog-ass-articl tracking-ad" data-mod="{0}">' +
      '<dt><span>准备好了么？&nbsp;<label class="po_blg_detail_tiao">跳</label><label class="po_blg_detail_ba">吧</label><label class="po_blg_detail_th">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</label></span>' +
      '<a href="{1}" target="_blank" class="po_blg_more">更多职位尽在&nbsp;<label class="po_blg_detail_csdn">CSDN JOB</label></a></dt>' +
      '{2}' +
      '</dl>';//{0}，点击标记popu_36    //{1}，http, https ://job.csdn.net    //{2}，内容
    this.blogItem = '<dd class="po_blg_dd">' +
      '<div class="po_blg_po">' +
      '<a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a>' +
      '</div>' +
      '<div class="po_blg_company">' +
      '<a href="{3}" title="{4}" target="_blank">{5}</a>' +
      '</div>' +
      '<label class="po_blg_separator">|</label>' +
      '<div class="po_blg_salary"><a href="{6}" target="_blank">{7}</a></div>' +
      '<a class="po_blg_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</dd>' ;
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略


    //博客专栏：tplType = blogSpec
    this.blogSpecTpl = '<div class="box_1 tracking-ad" data-mod="{0}">' +
      '<div style="position: relative;">' +
      '<h2>准备好了么？&nbsp;<label class="po_blg_detail_tiao">跳</label><label class="po_blg_detail_ba">吧</label><label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</label></h2>' +
      '</div>' +
      '<ul class="list_comm">' +
      '{2}' +
      '</ul>' +
      '<div class="po_blg_spec_more_div"><a href="{1}" target="_blank" class="po_blg_spec_more">更多职位尽在&nbsp;<label class="po_blg_spec_csdn">CSDN JOB</label></a></div>' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.blogSpecItem = '<li>' +
      '<div class="po_blg_spec_po">' +
      '<a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a>' +
      '</div>' +
      '<div class="po_blg_spec_company">' +
      '<a href="{3}" title="{4}" target="_blank">{5}</a>' +
      '</div>' +
      '<div class="po_blg_spec_salary"><a href="{6}" target="_blank">{7}</a></div>' +
      '<div><a class="po_blg_spec_iwant" href="{9}" target="_blank">我要跳槽</a></div>' +
      '</li>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略


    //论坛详情：tplType = bbsDetail
    this.bbsTpl = '<div id="topic-suggest" class="po_bbs_div tracking-ad" data-mod="{0}">' +
      '<div class="related-tags po_bbs_tit_div">' +
      '<span>准备好了么？&nbsp;<label class="po_blg_detail_tiao">跳</label><label class="po_blg_detail_ba">吧</label><label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</label></span>' +
      '<a class="po_bbs_more" href="{1}" target="_blank">更多职位尽在&nbsp;<label class="po_bbs_csdn">CSDN JOB</label></a>' +
      '</div>' +
      '<div class="related-topics po_bbs_item_div">' +
      '<ul>' +
      '{2}' +
      '</ul>' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.bbsItem = '<li class="po_bbs_li"><div class="po_bbs_po">' +
      '<a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a>' +
      '</div>' +
      '<div class="po_bbs_company">' +
      '<a href="{3}" title="{4}" target="_blank">{5}</a>' +
      '</div>' +
      '<label class="po_bbs_separator">|</label>' +
      '<div class="po_bbs_salary"><a href="{6}" target="_blank">{7}</a></div>' +
      '<div><a class="po_bbs_iwant" href="{9}" target="_blank">我要跳槽</a></div>' +
      '</li>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略


    //问答首页：tplType = askDetail
    this.askTpl = '<div class="mod_other_ask hot_tags po_ask_div tracking-ad" data-mod="{0}">' +
      '<div class="other_ask">' +
      '<h3><span>准备好了么？&nbsp;<label class="po_ask_tiao">跳</label><label class="po_ask_ba">吧</label><label class="po_blg_detail_th">！</label></span></h3>' +
      '<div class="po_ask_div_list">' +
      '{2}' +
      '</div>' +
      '<div class="po_ask_more_div"><a href="{1}" target="_blank" class="po_ask_more">更多职位尽在&nbsp;<label class="po_my_home_csdn">CSDN JOB</label></a></div>' +
      '</div>' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.askItem = '<div class="po_ask_item_div">' +
      '<div class="po_ask_po">' +
      '<a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a>' +
      '</div>' +
      '<div class="po_ask_salary">' +
      '<a href="{6}" target="_blank">{7}</a>' +
      '</div>' +
      '<div class="po_ask_company">' +
      '<a href="{3}" title="{4}" target="_blank">{5}</a>' +
      '</div>' +
      '<a class="po_ask_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略


    //个人空间-首页：tplType = personalSpaceHome
    this.perSpaceHomeTpl = '<div class="phr_third clearfix tracking-ad" data-mod="{0}">' +
      '<div class="phr_third_tit po_my_home_tit">' +
      '<div class="phrt_t po_my_home_t">准备好了么？&nbsp;<label class="po_my_my_tiao">跳</label><label class="po_my_my_ba">吧</label><label class="po_blg_detail_th">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</label></div>' +
      '</div>' +
      '<div class="phr_third_con po_my_home_div">' +
      '{2}' +
      '</div>' +
      '<div class="po_my_home_more"><a href="{1}" target="_blank" class="po_my_home_more">更多职位尽在&nbsp;<label class="po_my_home_csdn">CSDN JOB</label></a></div>' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.perSpaceHomeItem = '<div class="po_my_home_item_div clearfix">' +
      '<div class="po_my_home_po"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></div>' +
      '<div class="po_my_home_salary"><a href="{6}" target="_blank">{7}</a></div>' +
      '<div class="po_my_home_company"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<a class="po_my_home_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略


    //个人空间-我的：tplType = personalSpaceMy
    this.perSpaceMyTpl = '<div class="interested_con tracking-ad" data-mod="{0}" style="display: block;">' +
      '<h3 class="po_my_my_h3">准备好了么？&nbsp;<label class="po_my_my_tiao">跳</label><label class="po_my_my_ba">吧</label><label class="po_blg_detail_th">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</label></h3>' +
      '{2}' +
      '<div class="po_my_my_more_div"><a href="{1}" target="_blank" class="po_my_my_more">更多职位尽在&nbsp;<label class="po_my_my_csdn">CSDN JOB</label></a></div>' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.perSpaceMyItem = '<div class="po_my_my_item_div">' +
      '<div class="po_my_my_po">' +
      '<a class="po_my_my_po_a" href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a>' +
      '</div>' +
      '<div class="po_my_my_salary">' +
      '<a href="{6}" target="_blank">{7}</a>' +
      '</div>' +
      '<div class="po_my_my_company">' +
      '<a href="{3}" title="{4}" target="_blank">{5}</a>' +
      '</div>' +
      '<a class="po_my_my_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略    //{9}，职位链接


    //英雄会--首页
    this.heroHomeTpl = '<div class="her_topic_right tracking-ad" data-mod="{0}">' +
      '<h3 class="haping_t">准备好了么？&nbsp;<span class="po_yx_home_tiao">跳</span><span class="po_yx_home_ba">吧</span><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span></h3>' +
      '{2}' +
      '<div class="po_yx_home_more_div"><a href="{1}" target="_blank" class="po_yx_home_more">更多职位尽在&nbsp;<label class="po_yx_home_csdn">CSDN JOB</label></a></div>' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.heroHomeItem = '<div class="her_platform po_yx_home_item_div">' +
      '<div class="po_yx_home_po"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></div>' +
      '<div class="po_yx_home_salary"><a href="{6}" target="_blank">{7}</a></div>' +
      '<div class="po_yx_home_company"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<a class="po_yx_home_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略


    //英雄会--答题专家组
    this.heroExpertTpl = '<div class="her-r-expli po_yx_ex_div tracking-ad" data-mod="{0}">' +
      '<h3 class="tit"><span class="po_yx_ex_tit">准备好了么？&nbsp;</span><label class="po_yx_home_tiao px_yx_ex_tiao">跳</label><label class="po_yx_home_ba px_yx_ex_ba">吧</label><span class="po_yx_ex_tit">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span></h3>' +
      '{2}' +
      '<div class="po_yx_ex_more_div"><a href="{1}" target="_blank" class="po_yx_ex_more">更多职位尽在&nbsp;<label class="po_yx_ex_csdn">CSDN JOB</label></a></div>' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.heroExpertItem = '<dl class="her-r-explicon po_yx_ex_po_item_div">' +
      '<dt class="po_yx_ex_po"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></dt>' +
      '<dd class="po_yx_ex_salary"><a href="{6}" target="_blank">{7}</a></dd>' +
      '<dd class="py_yx_ex_company"><a href="{3}" title="{4}" target="_blank">{5}</a></dd>' +
      '<a class="po_yx_ex_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</dl>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略


    //英雄会--挑战题目详情页
    this.heroFightDetailTpl = '<div class="her_format_right py_yx_fd_div tracking-ad" data-mod="{0}">' +
      '<h3 class="po_yx_fd_tit"><span>准备好了么？&nbsp;</span><label class="po_yx_home_tiao">跳</label><label class="po_yx_home_ba">吧</label><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span></h3>' +
      '{2}' +
      '<div class="po_yx_fd_more_div"><a href="{1}" target="_blank" class="po_yx_fd_more">更多职位尽在&nbsp;<label class="po_yx_fd_csdn">CSDN JOB</label></a></div>' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.heroFightDetailItem = '<div class="po_yx_fd_item_div">' +
      '<div class="po_yx_fd_po"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></div>' +
      '<div class="po_yx_fd_company"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<div class="po_yx_fd_salary"><a href="{6}" target="_blank">{7}</a></div>' +
      '<div><a class="po_yx_fd_iwant" href="{9}" target="_blank">我要跳槽</a></div>' +
      '</div>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略


    //英雄会--我的英雄会
    this.heroMyTpl = '<div class="her-resultli po_yx_my_div tracking-ad" data-mod="{0}">' +
      '<h3><span>准备好了么？&nbsp;</span><label class="po_yx_home_tiao">跳</label><label class="po_yx_home_ba">吧</label><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span>' +
      '<a href="{1}" target="_blank" class="po_yx_my_more">更多职位尽在&nbsp;<label class="po_yx_my_csdn">CSDN JOB</label></a></h3>' +
      '{2}' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.heroMyItem = '<div class="po_yx_my_item_div">' +
      '<div class="po_yx_my_item_dot">▪</div>' +
      '<div class="po_yx_my_item_cont">' +
      '<div class="po_yx_my_po"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></div>' +
      '<div class="po_yx_my_company"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<label class="po_yx_my_separator">|</label>' +
      '<div class="po_yx_my_salary"><a href="{6}" target="_blank">{7}</a></div>' +
      '</div>' +
      '<a class="po_yx_my_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略


    this.heroFightDetailTpl = this.heroMyTpl;
    this.heroFightDetailItem = this.heroMyItem;


    //在线培训--课程详情页
    this.eduDetailTpl = '<div class="boutique-curr-box tracking-ad" data-mod="{0}">' +
      '<div class="boutique-curr"><h3>准备好了么？&nbsp;<label class="po_my_my_tiao">跳</label><label class="po_my_my_ba">吧</label><label class="po_blg_detail_th">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</label></h3>' +
      '<div class="cutt-column">' +
      '{2}' +
      '<div class="po_edu_detail_more_div"><a href="{1}" target="_blank" class="po_edu_detail_more">更多职位尽在&nbsp;<label class="po_edu_detail_csdn">CSDN JOB</label></a></div>' +
      '</div></div></div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.eduDetailItem = '<div class="po_edu_detail_item_div clearfix">' +
      '<div class="po_edu_detail_po"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}<a></div>' +
      '<div class="po_edu_detail_salary"><a href="{6}" target="_blank">{7}</a></div>' +
      '<div class="po_edu_detail_company"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<a class="po_edu_detail_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略


    //在线培训--课程分类列表
    this.eduListTpl = this.eduDetailTpl;
    this.eduListItem = this.eduDetailItem;


//--------------------------------------------------------------------------------------------------------------------------------
    //搜索培训推广
    this.search_reco_edu = '' +
        '<div class="common-box tracking-ad" data-mod="popu_84" style="display: block;">' + //todo 增加统计号码
        '<h3 id="job-pos-title" class="po_search_tit">精品课程<a class="class-edu-more" href="http://edu.csdn.net">更多</a></h3>' +
        '<div class="po_search_div">' +
        '{0}' +
        '</div>' +
        '</div>';
    this.search_reco_edu_item = '<div class="po_search_item_div">' +
        '<div class="class-img-box"><a href="{0}" target="_blank" strategy="{7}"><img src="{1}"></a></div>' +
        '<div class="class-content-box">' +
        '<div class="class-content-tit"><a href="{2}" target="_blank" title="{3}" strategy="{8}">{4}</a></div>' +
        '<div class="class-content-hp">好评率：<a href="{9}" class="class-content-hp-hpl">{5}%</a>  <span class="class-content-hp-rzx"><i class="class-content-icon">&nbsp;</i><a href="{10}" class="class-content-icon-rdx">{6}</a>人在学</span></div>' +
        '</div>' +
        '</div>';

      //<div id="adCollege" style="width: 42%;float: left;">
      this.blog_reco_edu = '<div class="tracking-ad" data-mod="popu_84">{0}</div>';            //todo 增加统计号码
      this.blog_reco_edu_item = '<dd style="background:url(http://static.blog.csdn.net/skin/default/images/blog-dot-red3.gif) no-repeat 0 10px; white-space: nowrap;">' +
          '<a href="{0}" title="{1}" strategy="{3}" target="_blank">{2}</a>' +
          '</dd>';

      //<div class="related po_down_detail_big_div">
      this.down_reco_edu = '<div class="section-list panel panel-default tracking-ad" data-mod="popu_84">' + //todo 修改统计号码
          '<div class="panel-heading po_down_detail_tit_div">' +
          '<h3 class="panel-title po_down_detail_tit">相关课程</h3>' +
          '</div>' +
          '<div class="panel-body">' +
          '<ul class="down_edu_t">' +
          '{0}' +
          '</ul>' +
          '</div>' +
          '</div>' +
          '';
      this.down_reco_edu_item = '  <li style="line-height: 25px;display:block;margin-bottom: 9px;margin-top: 9px;padding-top: 0px;">' +
          '<div style="padding:0;margin:0;border:0;text-overflow: ellipsis;overflow: hidden;">' +
          '<a href="{0}" title="{1}" alt="" target="_blank" strategy="{3}">{2}</a>' +
          '</div>' +
          '</li>';

      this.edu_detail_url_base = 'http://edu.csdn.net/course/detail/';

//结束。
//----------------------------------------------------------------------------------------------------------------------------------------





    //搜索
    this.searchTpl = '<div class="common-box tracking-ad" data-mod="{0}" style="display: block;">' +
      '<h3 id="job-pos-title" class="po_search_tit">准备好了么？&nbsp;<span class="po_blg_detail_tiao">跳</span><span class="po_blg_detail_ba">吧</span><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span></h3>' +
      '<div class="po_search_div">' +
      '{2}' +
      '<div class="po_search_more_div"><a href="{1}" target="_blank" class="po_search_more">更多职位尽在&nbsp;<label class="po_search_csdn">CSDN JOB</label></a></div>' +
      '</div>' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.searchItem = '<div class="po_search_item_div">' +
      '<div class="po_search_po_div"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></div>' +
      '<div class="po_search_salary_div"><a href="{6}" target="_blank">{7}</a></div>' +
      '<div class="po_search_company_div"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<a class="po_search_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>' +
      '';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略    //{9}，我要跳槽



    //下载--我的资源
    this.downMyTpl = '<div id="my-tags-side" class="panel panel-default tracking-ad" data-mod="{0}">' +
      '<div class="panel-heading po_downmy_div">' +
      '<h3 class="panel-title">准备好了么？&nbsp;<span class="po_blg_detail_tiao">跳</span><span class="po_blg_detail_ba">吧</span><span class="po_blg_detail_th">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span></h3>' +
      '</div>' +
      '<div>' +
      '{2}' +
      '</div>' +
      '<div class="po_downmy_more_div"><a href="{1}" target="_blank" class="po_downmy_more">更多职位尽在&nbsp;<label class="po_downmy_csdn">CSDN JOB</label></a></div>' +
      '</div>';//{0}，点击标记popu_36    //{1}，更多的链接    //{2}，内容
    this.downMyItem = '<div class="po_downmy_item_div">' +
      '<div class="po_downmy_po_div"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></div>' +
      '<div class="po_downmy_company_div"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<div class="po_downmy_salary_div"><a href="{6}">{7}</a></div>' +
      '<a class="po_downmy_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';
    //{0}，职位链接    //{1}，职位名称    //{2}，职位名称    //{3}，公司链接    //{4}，公司名称    //{5}，公司名称    //{6}，职位链接    //{7}，职位薪水    //{8}，上报策略    //{9}，我要跳槽



    //下载--下载页和下载详情页
    this.downDetailTpl = '<div class="related po_down_detail_big_div tracking-ad" data-mod="{0}">' +
      '<div>' +
      '<div class="section-list panel panel-default">' +
      '<div class="panel-heading po_down_detail_tit_div">' +
      '<h3 class="panel-title po_down_detail_tit">准备好了么？&nbsp;<span class="po_blg_detail_tiao">跳</span><span class="po_blg_detail_ba">吧</span><span class="po_blg_detail_th">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span></h3>' +
      '<a class="po_downdetail_more" href="{1}" target="_blank">更多职位尽在&nbsp;<label class="po_dwondetail_csdn">CSDN JOB</label></a>' +
      '</div>' +
      '{2}' +
      '</div>' +
      '</div>' +
      '</div>';
    this.downDetailItem = '<div class="panel-body po_down_detail_item_div">' +
      '<ul>' +
      '<li>' +
      '<div class="po_down_detail_left"><a class="con" href="{0}" title="{1}" target="_blank">【{7}】{2}</a></div>' +
      '<div class="po_down_detail_right"><a class="po_downdetail_iwant" href="http://job.csdn.net/Job/Index?jobID=81328" target="_blank">我要跳槽</a></div>' +
      '</li>' +
      '</ul>' +
      '</div>';



    //CSTO案例库列表：cstoCaseList
    this.cstoCaseListTpl = '<div class="contbox tracking-ad" data-mod="{0}">' +
      '<h3 class="po_csto_caselist_tit"><span>准备好了么？&nbsp;</span><span class="po_my_my_tiao">跳</span><span class="po_my_my_ba">吧</span><span class="po_blg_detail_th">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span></h3>' +
      '<div class="po_caselist_div">' +
      '{2}' +
      '<div class="po_caselist_more_div"><a href="{1}" target="_blank" class="po_caselist_more">更多职位尽在&nbsp;<label class="po_caselist_csdn">CSDN JOB</label></a></div>' +
      '</div>' +
      '</div>';
    this.cstoCaseListItem = '<div class="po_caselist_item_div">' +
      '<div class="po_caselist_po_div"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></div>' +
      '<div class="po_caselist_salary_div"><a href="{6}" target="_blank">{7}</a></div>' +
      '<div class="po_caselist_company_div"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<a class="po_caselist_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';



    //CSTO案例详情页：cstoCaseDetail
    this.cstoCaseDetailTpl = '<ul class="case_list po_case_detail_div tracking-ad" data-mod="{0}">' +
      '<h3 class="po_csto_casedetail_tit"><span>准备好了么？&nbsp;</span><span class="po_my_my_tiao">跳</span><span class="po_my_my_ba">吧</span><span class="po_blg_detail_th">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span></h3>' +
      '<div class="po_casedetail_div">' +
      '{2}' +
      '<div class="po_casedetail_more_div"><a href="{1}" target="_blank" class="po_casedetail_more">更多职位尽在&nbsp;<label class="po_casedetail_csdn">CSDN JOB</label></a></div>' +
      '</div>' +
      '</ul>';
    this.cstoCaseDetailItem = '<div class="po_casedetail_item_div clearfix">' +
      '<div class="po_casedetail_po_div"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></div>' +
      '<div class="po_casedetail_company_div"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<div class="po_casedetail_salary_div"><a href="{6}" target="_blank">{7}</a></div>' +
      '<a class="po_casedetail_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';






    //CSTO我的T台：cstoMy
    this.cstoMyTpl = '<ul class="menu tracking-ad" data-mod="{0}">' +
      '<li class="icon selected po_csto_my_big_div">' +
      '<h3 class="po_csto_my_tit"><span>准备好了么？&nbsp;</span><span class="po_my_my_tiao">跳</span><span class="po_my_my_ba">吧</span><span class="po_blg_detail_th">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span></h3>' +
      '<div class="po_csto_my_div">' +
      '{2}' +
      '</div>' +
      '<div class="po_csto_my_more_div"><a href="{1}" target="_blank" class="po_csto_my_more">更多职位尽在&nbsp;<label class="po_csto_my_csdn">CSDN JOB</label></a></div>' +
      '</li>' +
      '</ul>';
    this.cstoMyItem = '<div class="po_csto_my_item_div">' +
      '<div class="po_csto_my_po_div"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></div>' +
      '<div class="po_csto_my_salary_div"><a href="{6}" target="_blank">{7}</a></div>' +
      '<div class="po_csto_my_company_div"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<a class="po_csto_my_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';


    //CSTO项目列表页：cstoProjectList
    this.cstoProjectListTpl = '' +
      '' +
      '' +
      '';
    this.cstoProjectListItem = '' +
      '' +
      '' +
      '';




    //CSTO项目详情页：cstoProjectDetail
    this.cstoProjectDetailTpl = '<div class="bid_scheme tracking-ad" data-mod="{0}">' +
      '<h3 class="po_csto_proj_detail_tit"><span>准备好了么？&nbsp;</span><span class="po_my_my_tiao">跳</span><span class="po_my_my_ba">吧</span><span class="po_blg_detail_th">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;！</span></h3>' +
      '<div class="po_proj_detail_div">' +
      '{2}' +
      '<div class="po_proj_detail_more_div"><a href="{1}" target="_blank" class="po_proj_detail_more">更多职位尽在&nbsp;<label class="po_proj_detail_csdn">CSDN JOB</label></a></div>' +
      '</div>' +
      '</div>';
    this.cstoProjectDetailItem = '<div class="po_proj_detail_item_div clearfix">' +
      '<div class="po_proj_detail_po_div"><a href="{0}" title="{1}" strategy="{8}" target="_blank">{2}</a></div>' +
      '<div class="po_proj_detail_salary_div"><a href="{6}" target="_blank">{7}</a></div>' +
      '<div class="po_proj_detail_company_div"><a href="{3}" title="{4}" target="_blank">{5}</a></div>' +
      '<a class="po_proj_detail_iwant" href="{9}" target="_blank">我要跳槽</a>' +
      '</div>';
      this.getCount = 300;
  };

  Position.prototype = {
    show: function(conf) {
      var _conf = conf;
      var _this = this;
      _this.show_inner(_conf);
      /*$(window).load(function() {
        _this.show_inner(_conf);
      });*/
    },
    showEdu: function(conf) {
        var _conf = conf;
        var _this = this;
        _this.show_edu_mlgb(_conf);
    },
    show_edu_mlgb: function(conf) {
        this.sourceType = conf.sourceType;//blog, bbs, downlowd, ask, space, hero, edu, csto .....
        this.searchType = conf.searchType;
        this.searchKey = conf.searchKey;
        this.username = conf.username ? conf.username : '';
        this.recordcount = conf.recordcount;
        this.containerId = conf.containerId;

        this.$container = $("#" + this.containerId);
        this.prefix = window.location.protocol;
        this.load_edu_reco();
    },
      load_edu_reco: function() {
          var tpl = '';
          var itemTpl = '';
          var _url = '';
          _data = {};
          //var urlBase = 'http://192.168.5.75:9400';
          //var urlBase = 'http://p.search.dm.csdn.net';

          var kk = this.searchKey.replace(/%/g,"%25").replace(/#/g,"%23").replace(/&/g,"%26").replace(/\+/g, "%2B");
          var urlBase = 'http://internalapi.csdn.net/psearch/psearch/query?x-acl-token=kUOm7x6dCaKGFa8RxxLQ5Hm75ioK&index_name=pro_course_v2';
          if (this.sourceType == 'so') {
              tpl = this.search_reco_edu;
              itemTpl = this.search_reco_edu_item;
              //url = 'http://p.search.dm.csdn.net/v2/pro_course/csdn/_search?_client_=rcommend_course&fields=id,title,stu_count,good_ratio,rc_flag&searchStr=初级java教程&user_id=blogchong';
              _url = urlBase + '&search_str=' + kk; // + '/v2/pro_course_v2/csdn/_search';
              _data = {'_client_': 'rcommend_course','fields': 'id,title,pic,stu_count,good_ratio,rc_flag,source_type', 'user_id': this.username, 'size': this.recordcount}; //'search_str': kk,
              if (this.username == '')
              _data = {'_client_': 'rcommend_course','fields': 'id,title,pic,stu_count,good_ratio,rc_flag,source_type', 'size': this.recordcount}; //'search_str': kk,
          } else if (this.sourceType == 'blog') {
              tpl = this.blog_reco_edu;
              itemTpl = this.blog_reco_edu_item;
              //url = 'http://p.search.dm.csdn.net/v2/pro_course/csdn/_search?_client_=rcommend_course&fields=id,title,stu_count,good_ratio,rc_flag&pro_id=45521251&pro_type=blog&user_id=blogchong';
              _url = urlBase + '&pro_id=' + kk;// + '/v2/pro_course_v2/csdn/_search';
              _data = {'_client_': 'rcommend_course','fields': 'id,title,pic,stu_count,good_ratio,rc_flag,source_type', 'pro_type': 'blog', 'user_id': this.username, 'size': this.recordcount}; //'pro_id': kk,

              if (this.username == '')
                  _data = {'_client_': 'rcommend_course','fields': 'id,title,pic,stu_count,good_ratio,rc_flag,source_type', 'pro_type': 'blog', 'size': this.recordcount}; //'pro_id': kk,
          } else if (this.sourceType == 'down') {
              tpl = this.down_reco_edu;
              itemTpl = this.down_reco_edu_item;
              //url = 'http://p.search.dm.csdn.net/v2/pro_course/csdn/_search?_client_=rcommend_course&fields=id,title,stu_count,good_ratio,rc_flag&pro_id=45521251&pro_type=blog&user_id=blogchong';
              _url = urlBase + '&pro_id=' + kk;// + '/v2/pro_course_v2/csdn/_search';
              _data = {'_client_': 'rcommend_course','fields': 'id,title,pic,stu_count,good_ratio,rc_flag,source_type', 'pro_type': 'download', 'user_id': this.username, 'size': this.recordcount}; //'pro_id': kk,
              if (this.username == '')
                  _data = {'_client_': 'rcommend_course','fields': 'id,title,pic,stu_count,good_ratio,rc_flag,source_type', 'pro_type': 'download', 'size': this.recordcount}; //'pro_id': kk,
          }
          if (tpl == '') return;
          var _this = this;
          $.ajax({
              type: 'get',
              url: _url,
              data: _data,
              dataType: "jsonp",
              jsonp: "callback",
              async: false,
              success: function(result) {
                  if (result.hits && result.hits.length > 0) {
                      var htmlItems = '';
                      for (var i = 0; i < _this.recordcount; i ++) {
                          var item = result.hits[i].object;
                          var htmlItem = '';
                          if (_this.sourceType == 'so') {
                              var rzx = item.stu_count ? item.stu_count : 0;
                              var kcurl = _this.edu_detail_url_base + item.id;
                              htmlItem = itemTpl.replace(/\{0\}/, kcurl) //课程url
                                  .replace(/\{1\}/, item.pic)            //图片url
                                  .replace(/\{2\}/, kcurl)               //课程url
                                  .replace(/\{3\}/, item.title)          //课程标题
                                  .replace(/\{4\}/, item.title)          //课程名称
                                  .replace(/\{5\}/, item.good_ratio == 0 ? 100 : item.good_ratio)     //好评率
                                  .replace(/\{6\}/, rzx)                 //正在学习的人数
                                  .replace(/\{7\}/, item.rc_flag)        //策略
                                  .replace(/\{8\}/, item.rc_flag)        //策略
                                  .replace(/\{9\}/, kcurl)               //课程url
                                  .replace(/\{10\}/, kcurl);             //课程url
                          } else if (_this.sourceType == 'blog' || _this.sourceType == 'down') {
                              htmlItem = itemTpl.replace(/\{0\}/, _this.edu_detail_url_base + item.id)
                                  .replace(/\{1\}/, item.title)
                                  .replace(/\{2\}/, item.title) //'【精品课程】' + item.title
                                  .replace(/\{3\}/, item.rc_flag);
                          }
                          htmlItems += htmlItem;
                      }
                      var jHtml = tpl.replace(/\{0\}/, htmlItems);
                      var tdd = $(jHtml).appendTo(_this.$container);
                      var tds = [];
                      tds.push(tdd[0]);
                      try {
                          window['trackingAd']($(tds));
                      } catch(ee){};
                  }
              },
              error: function(result) {
                  var i = 0;
              }
          }); //问题：记录数；类型只有blog、download、bbs没有搜索；

          //todo 暂时处理一下，因为看不懂原来的猜你在找代码。后面找时间把猜你在找调整为只显示5条数据。
          $(function() {
              var count = 0;
              var setFive = setInterval(function() {
                  count ++;
                  if (count > _this.getCount) {
                      clearInterval(setFive);
                  }
                  $('#res').hide();
                  var cc = $('#res').children();
                  if (cc.length > 5) {
                      for (var i = 0; i < cc.length; i++) {
                          clearInterval(setFive);
                          var item = cc[i];
                          if (i > 4) {
                              $(item).remove();
                          }
                      }
                      $('#res').show();
                  }
              }, 200);
          });
      },
    show_inner: function(conf) {
      this.sourceType = conf.sourceType;//blog，bbs, download，ask, space, hero, edu, csto .....
      this.tplType = conf.tplType;
      //模板类型，博客详情：blogDetail，博客专栏：blogSpec，论坛详情：bbsDetail，问答首页：askDetail，个人空间--我的空间：personalSpaceMy，个人空间--首页：personalSpaceHome，
      //英雄会--首页：heroHome，英雄会--答题专家组：heroExpert，英雄会--挑战题目详情页：heroFightDetail，英雄会--我的英雄会：heroMy，
      //在线培训--课程分类列表页：eduList，在线培训--课程详情页：eduDetail，.....
      this.searchType = conf.searchType;//页面类型，用于搜索函数，detail(详情页) / list(列表页)。
      this.searchKey = conf.searchKey;//搜索关键字，例如博客详情：博文ID，如果是博客专栏：分类字符串。
      this.username = conf.username;
      this.containerId = conf.containerId;

      this.$container = $("#" + this.containerId);
      this.prefix = window.location.protocol;

      this.load();
    },
    goInPage: function(containerTpl, itemTpl, container, _prefix) {
      var homeUrl = _prefix + "//job.csdn.net";
      var jHtml = containerTpl.replace(/\{0\}/, "popu_72")
       .replace(/\{1\}/, homeUrl)
       .replace(/\{2\}/, totalHtmlItems);
       //container.html(jHtml);
       container.html("");
       var tdd = $(jHtml).appendTo(container);
       var tds = [];
       tds.push(tdd[0]);
       try {
       window['trackingAd']($(tds));
       } catch(ee) {};
       return true;
    },
    totalHtmlItems: "",
    load: function() {
      var that = this;
      if (that.searchKey === "NO" && that.username === "") {
        return;
      }

      var _url = that.get_url(that.username, that.searchType, that.searchKey, that.sourceType, that.tplType);
      var _strategy = that.get_strategy(that.username, that.searchType);
      var containerTpl = that.get_containerTpl(that.tplType);
      var itemTpl = that.get_itemTpl(that.tplType);

      //that.username = that.getUserName();

      $.ajax({
        type: "get",
        url: _url,
        dataType: "jsonp",
        jsonp: "callback",
        async: false,
        success: function (obj) {
          totalHtmlItems = "";
          var htmlItems = "";
          var count = obj.hits.length;
          if (obj.hits && obj.hits.length > 0) {
            totalHtmlItems = that.getData(that.$container, obj.hits, containerTpl, itemTpl, that.prefix, _strategy, true);
          }
          if (count < 4 && that.username && that.searchType == "detail") {
            //以内容搜索职位，再次发送请求
            var _strategy_detail = that.get_strategy("", "detail");
            that.loadByDetail(containerTpl, itemTpl, _strategy_detail, count, that.$container, that.prefix);
          } else if (count < 4) {
            //以最新职位发送请求。
            var _strategy_latest = that.get_strategy("", "latest");
            htmlItems = that.loadByLatest(containerTpl, itemTpl, _strategy_latest, count, that.$container, that.prefix);
          }
          if (count >= 4) {
            that.goInPage(containerTpl, itemTpl, that.$container, that.prefix);
          }
        },
        error: function(err) {
          var i = 0;
          //alert('err');
        }
      });
    },
    loadByDetail: function(_containerTpl, _itemTpl, _strategy, _count, _containerObj, _prefix) {
      var that = this;
      var _url = that.get_url("", that.searchType, that.searchKey, that.sourceType, that.tplType , 4 - _count);
      $.ajax({
        type: "get",
        url: _url,
        dataType: "jsonp",
        jsonp: "callback",
        async: false,
        success: function (obj) {
          var count = obj.hits.length;
          var htmlItems = "";
          if (obj.hits && obj.hits.length > 0) {
            totalHtmlItems += that.getData(_containerObj, obj.hits, _containerTpl, _itemTpl, _prefix, _strategy, false);
          }
          if (count + _count < 4) {
            //以最新职位再次发送请求。
            var _strategy_latest = that.get_strategy("", "latest");
            that.loadByLatest(_containerTpl, _itemTpl, _strategy, count + _count, _containerObj, _prefix);
          }
          if (count + _count >= 4) {
            that.goInPage(_containerTpl, _itemTpl, _containerObj, _prefix);
          }
        },
        error: function(err) {
          var i = 0;
        }
      });
    },
    loadByLatest: function(_containerTpl, _itemTpl, _strategy, _count, _containerObj, _prefix) {
      var that = this;
      var _url = "http://job.csdn.net/api/lastJobList";//http://job.csdn.net/api/lastJobList //http://tmpjob.csdn.net/api/LastJobList

      var homeUrl = _prefix + "//job.csdn.net";
      var jobUrl = _prefix + "//job.csdn.net/Job/Index?jobID=";
      var companyUrl = _prefix + "//pr.csdn.net/enterprise/ent_home?orgid=";

      $.ajax({
        type: "get",
        url: _url,
        dataType: "jsonp",
        jsonp: "callback",
        async: false,
        success: function(obj) {
          var count = obj;
          if (obj.Data && obj.Data.paperList && obj.Data.paperList.length > 0 && obj.Data.paperList.length >= 4 - _count) {
            var htmlItems = "";
            for (var i = 0; i <  4 - _count; i ++) {
              var item = obj.Data.paperList[i];
              if (item.JobID && item.JobName && item.OrgID && item.OrgName && item.SalaryMax && item.SalaryMin) {
                var salaryText = "";
                if (item.SalaryMax == 0 && item.SalaryMin == 0) {
                  salaryText = "面议";
                } else {
                  var mins = item.SalaryMin / 1000;
                  var maxs = item.SalaryMax / 1000;
                  salaryText = mins + "-" + maxs + "K/月";
                }

                var htmlItem = _itemTpl.replace(/\{0\}/, jobUrl + item.JobID)
                  .replace(/\{1\}/, item.JobName)
                  .replace(/\{2\}/, item.JobName)
                  .replace(/\{3\}/, companyUrl + item.OrgID)
                  .replace(/\{4\}/, item.OrgName)
                  .replace(/\{5\}/, item.OrgName)
                  .replace(/\{6\}/, jobUrl + item.JobID)
                  .replace(/\{7\}/, salaryText)
                  .replace(/\{8\}/, _strategy)
                  .replace(/\{9\}/, jobUrl + item.JobID);

                htmlItems  += htmlItem;
              }
            }
            totalHtmlItems += htmlItems;
            /*if (htmlItems != "") {
              var jHtml = _containerTpl.replace(/\{0\}/, "popu_72")
                .replace(/\{1\}/, homeUrl)
                .replace(/\{2\}/, htmlItems);
              //_containerObj.html("");
              var tdd = $(jHtml).appendTo(_containerObj);
              var tds = [];
              tds.push(tdd[0]);
              try {
                window['trackingAd']($(tds));
              } catch (ee) {};
            }*/
          }
          that.goInPage(_containerTpl, _itemTpl, _containerObj, _prefix);
          return "";
          /*
           obj.Data.paperList[0].JobID
           obj.Data.paperList[0].JobName
           obj.Data.paperList[0].OrgID
           obj.Data.paperList[0].OrgName
           obj.Data.paperList[0].SalaryMax
           obj.Data.paperList[0].SalaryMin
          * */
        },
        error: function(err) {
          var i = 0;
        }
      });
    },
    getData: function(container, items, containerTpl, itemTpl, prefix, _strategy, isClear) {
      var homeUrl = prefix + "//job.csdn.net";
      var jobUrl = prefix + "//job.csdn.net/Job/Index?jobID=";//职位页面url，样例：http://job.csdn.net/Job/Index?jobID=80500，http://tmpjob.csdn.net/Job/Index?jobID=80500
      var companyUrl = prefix + "//pr.csdn.net/enterprise/ent_home?orgid=";//3，样例：http://pr.csdn.net/enterprise/ent_home?orgid=406854，http://lpr.csdn.net/enterprise/ent_home?orgid=406854

      var htmlItems = "";
      for (var i = 0; i < items.length; i++) {
        var item = items[i];
        var obj = item.object;
        if (obj.id && obj.title && obj.org_id && obj.org_name) {
          var salaryText = "";
          if (obj.salary_max == 0 && obj.salary_min == 0) {
            salaryText = "面议";
          } else {
            var mins = obj.salary_min / 1000;
            var maxs = obj.salary_max / 1000;
            salaryText = mins + "-" + maxs + "K/月";
          }

          var htmlItem = itemTpl.replace(/\{0\}/, jobUrl + obj.id)
            .replace(/\{1\}/, obj.title)
            .replace(/\{2\}/, obj.title)
            .replace(/\{3\}/, companyUrl + obj.org_id)
            .replace(/\{4\}/, obj.org_name)
            .replace(/\{5\}/, obj.org_name)
            .replace(/\{6\}/, jobUrl + obj.id)
            .replace(/\{7\}/, salaryText)
            .replace(/\{8\}/, _strategy)
            .replace(/\{9\}/, jobUrl + obj.id);
          //{0}，职位链接  //{1}，职位名称  //{2}，职位名称  //{3}，公司链接  //{4}，公司名称  //{5}，公司名称  //{6}，职位链接  //{7}，职位薪水  //{8}，上报策略
          htmlItems += htmlItem;
        }
      }
      return htmlItems;

      /*var jHtml = containerTpl.replace(/\{0\}/, "popu_72")
        .replace(/\{1\}/, homeUrl)
        .replace(/\{2\}/, htmlItems);
      //container.html(jHtml);
      if (isClear) {
        container.html("");
      }
      var tdd = $(jHtml).appendTo(container);
      var tds = [];
      tds.push(tdd[0]);
      try {
        window['trackingAd']($(tds));
      } catch(ee) {};
      return true; */
    },
    get_strategy: function(un, searcht) {
      var _st = "";
      if (un != "") {
        _st = "PersonalRecommend";
      } else if (searcht == "detail") {
        _st = "DetailRecommend";
      } else if (searcht == "list") {
        _st = "ListRecommend";
      } else if (searcht == "latest") {
        _st = "LatestRecommend";
      } else {
        _st = "unknown";
      }

      return _st;
    },
    get_itemTpl: function(tplType) {
      var c = "";
      switch (tplType) {
        case "blogDetail":
          c = this.blogItem;
          break;
        case "blogSpec":
          c = this.blogSpecItem;
          break;
        case "bbsDetail":
          c = this.bbsItem;
          break;
        case "askDetail":
          c = this.askItem;
          break;
        case "personalSpaceMy":
          c = this.perSpaceMyItem;
          break;
        case "personalSpaceHome":
          c = this.perSpaceHomeItem;
          break;
        case "heroHome":
          c = this.heroHomeItem;
          break;
        case "heroExpert":
          c = this.heroExpertItem;
          break;
        case "heroFightDetail":
          c = this.heroFightDetailItem;
          break;
        case "heroMy":
          c = this.heroMyItem;
          break;
        case "eduList":
          c = this.eduListItem;
          break;
        case "eduDetail":
          c = this.eduDetailItem;
          break;
        case "search":
          c = this.searchItem;
          break;
        case "downMy":
          c = this.downMyItem;
          break;
        case "downDetail":
          c = this.downDetailItem;
          break;
        case"cstoCaseList":
          c = this.cstoCaseListItem;
          break;
        case"cstoCaseDetail":
          c = this.cstoCaseDetailItem;
          break;
        case"cstoMy":
          c = this.cstoMyItem;
          break;
        case"cstoProjectList":
          c = this.cstoCaseListItem;//同案例列表
          break;
        case"cstoProjectDetail":
          c = this.cstoProjectDetailItem;
          break;
        default:
          break;
      }
      return c;
    },
    get_containerTpl: function(tplType) {
      var c = "";
      switch (tplType) {
        case "blogDetail":
          c = this.blogTpl;
          break;
        case "blogSpec":
          c = this.blogSpecTpl;
          break;
        case "bbsDetail":
          c = this.bbsTpl;
          break;
        case "askDetail":
          c = this.askTpl;
          break;
        case "personalSpaceMy":
          c = this.perSpaceMyTpl;
          break;
        case "personalSpaceHome":
          c = this.perSpaceHomeTpl;
          break;
        case "heroHome":
          c = this.heroHomeTpl
          break;
        case "heroExpert":
          c = this.heroExpertTpl;
          break;
        case "heroFightDetail":
          c = this.heroFightDetailTpl;
          break;
        case "heroMy":
          c = this.heroMyTpl;
          break;
        case "eduList":
          c = this.eduListTpl;
          break;
        case "eduDetail":
          c = this.eduDetailTpl;
          break;
        case "search":
          c = this.searchTpl;
          break;
        case "downMy":
          c = this.downMyTpl;
          break;
        case "downDetail":
          c = this.downDetailTpl;
          break;
        case "cstoCaseList":
          c = this.cstoCaseListTpl;
          break;
        case "cstoCaseDetail":
          c = this.cstoCaseDetailTpl;
          break;
        case "cstoMy":
          c = this.cstoMyTpl;
          break;
        case "cstoProjectList":
          c = this.cstoCaseListTpl;//同案例列表
          break;
        case "cstoProjectDetail":
          c = this.cstoProjectDetailTpl;
          break;
        default:
          break;
      }
      return c;
    },
    get_url: function(un, searcht, key, st, tt, count) {
      var _st = st;
      var kk = key.replace(/%/g,"%25").replace(/#/g,"%23").replace(/&/g,"%26").replace(/\+/g, "%2B");
      var u = "http://internalapi.csdn.net/psearch/psearch/query?x-acl-token=kUOm7x6dCaKGFa8RxxLQ5Hm75ioK&index_name=test_b2d_job_141211&_client_=";
      //var u = "http://p.search.dm.csdn.net/v2/test_b2d_job_141211/csdn/_search?_client_=";
      if (un != "" && un != undefined && un != null) {
        _st = "uc_proxy";
        u = u + "search_job_by_user";
        u = u + "&from=1&size=4";
        u = u + "&id=" + un;
      } else if (tt == "search") {
        u = u + "job_position_query";
        u = u + "&like=title:" + kk;
        u = u + "&shouldNum=0&from=1&size=" + (count ? count : 4);
      } else if (searcht == "detail") {
        u = u + "search_job_by_content";
        u = u + "&id=" + kk;
        u = u + "&from=1&size=" + (count ? count : 4);
      } else if (searcht == "list") {
        u = u + "search_job_by_content";
        u = u + "&content=" + kk;
        u = u + "&from=1&size=" + (count ? count : 4);
      }
      u = u + "&source_type=" + _st;
      u = u + "&fields=id,publish_time,title,org_name,org_id,salary_max,salary_min";
      return u;
    },
    getThisCss: function() {
      $("<link>")
        .attr({ rel: "stylesheet",
          type: "text/css",
          href: csdn.position.prefix + "//csdnimg.cn/jobreco/job_reco.css"
        })
        .appendTo("head");
    },
    getUserName: function() {
      return this.getCookie("UserName");
    },
    getCookie: function(objName) {
      var arrStr = document.cookie.split("; ");
      for(var i = 0;i < arrStr.length;i ++){
        var temp = arrStr[i].split("=");
        if(temp[0] == objName) return decodeURI(temp[1]);
      }
    },
    evil: function() {
    }
  };

  csdn.position = new Position();
  window["csdn"] = csdn;
})(window);