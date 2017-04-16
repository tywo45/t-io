/*
搜索服务 https://code.csdn.net/CSDN_Dev/csdn_searchservice_fe

依赖jQuery，使用示例(除非明确要求必填，都是可选参数；除非明确说明默认值，默认值就是示例中的值):

  <script type="text/javascript" src="http://csdnimg.cn/rabbit/search-service/main.js" charset="utf-8"></script>
  <script type="text/javascript">
    searchService({
      index: 'blog',
        // 必填，使用的索引名称，默认值是''
      appendTo: '#output',
        // 搜索结果显示到指定元素里面，如果想手工处理可以忽略，默认值是''
      query: {text: {'title,body':'java'}},
        // 必填，查询条件，详细请参阅搜索系统API
      tmpl: '<li><a href="#{ object.url }" title="#{ object.title }">#{ object.title }</a></li>',
        // 显示到appendTo的元素模板，可使用的模板标签主要由返回的数据 hits 数据元素的结构决定；
        // 除此之外还可以使用 item 和 data，分别代表每一条数据的原始结构和返回的整个数据
        // 也可以访问window下的变量
      from: 0,
        // 要从搜索结果中第多少条开始获取数据
      size: 6,
        // 要从搜索结果中获取多少条数据
      filter: {"range":{"created_at":{"from":2010072320,"to":2010072330}}},
        // 默认不传递这个参数，过滤条件，详细请参阅搜索系统API
      sort: { 'updated_at': 'desc' },
        // 默认不传递这个参数，排序字段, 详细请参阅搜索系统API
      fields: ['title', 'url'],
        // 最终显示的字段，详细请参阅搜索系统API
      token: 'search_js_dkuyqthzbajmncbsb_token'
        // 访问控制标识
    }, function(err, data, options){
      // 处理完成时的回调
      // err是错误信息，如果无错将为null
      // data是搜索到的原始数据
      // options是上面调用最终使用的参数
    });
  </script>

 */
;(function(definition, undefined){
  var global = this, exports = {}, $ = global.jQuery;
  definition(global, exports, $);

  if(global.csdn === undefined){
    global.csdn = exports;
  } else {
    $.extend(global.csdn, exports);
  }
  $.extend(global, exports);

}(function(global, exports, $, undefined){
  document.domain = 'csdn.net';

  var searchServiceReady = false
    , searchArgs = []
    , searchService = exports.searchService = function(){
      var args = [].slice.call(arguments, 0);
      if(searchServiceReady){
        searchServiceFunc.apply(null, args);
      }else{
        searchArgs.push(args);
      }
    }
    , render = searchService.render = function(tmpl, item, data){
      return tmpl.replace(/#\{ +([^}]+) +\}/gm, function($0, $1){
        var fieldN = $1;
        if(eval('item.' + $1) !== undefined) $1 = 'item.' + $1;
        //return eval($1);
        var val = eval($1);
        if (fieldN.toLowerCase() == "title") {
          var reg=/[^`~!\$%\^\*\(\)=\|\{\}':;',\\\[\\\]\.<>\/\?~！￥%……*\（\）——\|\{\}【】‘；：”“’。，、？]/g;
          var titleFilter = val.match(reg).join( '' );
          val = titleFilter;
        }
        return val;
      });
    }
    , ajaxProxyCaches = {}
    , ajaxProxy = searchService.ajaxProxy = function(proxyUrl, opts){
      var c = ajaxProxyCaches[proxyUrl];
      if(c === undefined){
        c = ajaxProxyCaches[proxyUrl] = [];
        var func = arguments.callee;
        $('<iframe src="' + proxyUrl + '" style="display:none">').load(function(){
          c.contentWindow = this.contentWindow;
          func(proxyUrl, opts);
        }).prependTo('body');
      }else if(c.contentWindow === undefined){
        c.push(opts);
      }else{
        do{
          c.contentWindow.jQuery.ajax(opts);
        }while(opts = c.shift());
      }
    }
    , trackingAd = function(ele){
      if(typeof window.csdn !== 'undefined' && typeof window.csdn.trackingAd === 'function'){
        window.csdn.trackingAd(ele);
      }else{
        $(document).bind('trackingAd-ready', function(){
          trackingAd(ele);
        });
      }
    }
    , searchServiceFunc = function(opts, callback){
      var tmpl = ( opts.url == 'search' ) ? '<li><a href="#{ object.url }" title="#{ object.title }">#{ object.title }</a></li>' : '<li><a href="#{ url }" title="#{ title }" strategy="#{ strategy }">#{ title }</a></li>';
      opts = $.extend({
        index: '',
        appendTo: '',
        query: '',
        tmpl: tmpl,
        from: 0,
        size: 6,
        fields: ['title', 'url'],
        token: 'search_js_dkuyqthzbajmncbsb_token',
        render: render
      }, opts);

      if(!opts.index || !opts.query) return;

      var postData = {};
      if ( opts.url == 'recommend' )
      {
        var userId = $.cookie( 'UserName' ) || 'fake_userId';
        var uuid = $.cookie( 'uuid_tt_dd' ) || 'fake_uuid';
        var reg = /(\d+)$/;
        var href = window.location.href;
        var downId = reg.test( href ) ? RegExp.$1 : '';
        // var query = $( '.info' ).find( 'h1' ).attr( 'title' ) || $( '.info h1' ).text();

        postData = {
          userId: userId,
          size: 10,
          his: opts.his + ':' + downId,
          client: opts.client || 'download_cf_enhance',
          query: opts.query,
          cid: uuid
        };

        $.ajax( {
          type: 'GET',
          url: 'http://recdm.csdn.net/getRecommendList.html',
          data: postData,
          dataType: 'jsonp',
          jsonp: 'jsonp',
          success: function ( data ) {
            var appendTo = opts.appendTo, tmpl = opts.tmpl;
            if(data === null || data.ok === false && data.message){
              var msg = data !== null ? data.message : '请求失败';
              if(appendTo){
                $(appendTo).append(render('<div class="search-error">#{ item }</div>', msg));
              }
              if(typeof callback === 'function') {
                callback(msg, data, opts);
              }
            }else if( data.length > 0 ){
              if( appendTo ){
                $(appendTo).append($.map(data, function(i){
                  return opts.render(tmpl, i, data);
                }).join(''));
                trackingAd(appendTo);
              }
              if(typeof callback === 'function') {
                callback(null, data, opts);
              }
            }
          }
        } );
      }
      else if ( opts.url == 'search' )
      {
        $.map(['query', 'from', 'size', 'filter', 'sort', 'fields'], function(k){
          if(opts.hasOwnProperty(k)){
            postData[k] = opts[k];
          }
        });
        ajaxProxy('http://search.api.csdn.net/proxy.html', {
          type: 'POST',
          url: 'http://search.api.csdn.net/' + opts.index + '/csdn/_search',
          data: JSON.stringify(postData),
          dataType: 'json',
          headers: {
            'X-ACL-TOKEN': opts.token
          },
          success: function(data){
            var appendTo = opts.appendTo, tmpl = opts.tmpl;
            if(data === null || data.ok === false && data.message){
              var msg = data !== null ? data.message : '请求失败';
              if(appendTo){
                $(appendTo).append(render('<div class="search-error">#{ item }</div>', msg));
              }
              if(typeof callback === 'function') {
                callback(msg, data, opts);
              }
            }else if(data.hits){
              if(appendTo){
                $(appendTo).append($.map(data.hits, function(i){
                  return opts.render(tmpl, i, data);
                }).join(''));
                trackingAd(appendTo);
              }
              if(typeof callback === 'function') {
                callback(null, data, opts);
              }
            }
          }
        });
      }
    }
    ;

  if(global.JSON === undefined){
    $.getScript('http://csdnimg.cn/rabbit/search-service/json2.js', function(){
      searchServiceReady = true;
      var args;
      while(args = searchArgs.shift()){
        searchService.apply(null, args);
      }
    });
  }else{
    searchServiceReady = true;
  }

}));