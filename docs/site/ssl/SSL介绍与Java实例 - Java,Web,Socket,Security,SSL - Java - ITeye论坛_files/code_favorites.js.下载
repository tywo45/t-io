

function code_favorites_init(css_rules){
    css_rules = css_rules || "pre[name=code]";

    $$(css_rules).each(function(pre){
        var sh_bar_tools = pre.previous().down(".tools");
        var favorite_link = '&nbsp;<a href="javascript:void()" title="收藏这段代码" onclick="code_favorites_do_favorite(this);return false;">'
        favorite_link += '<img class="star" src="/images/icon_star.png" alt="收藏代码" />';
        favorite_link += '<img class="spinner" src="/images/spinner.gif" style="display:none" />';
        favorite_link += '</a>';
        sh_bar_tools.innerHTML += favorite_link;
    });
}

function code_favorites_do_favorite(link_element){
    $(link_element);// for ie6
    var pre_element = link_element.up(".dp-highlighter").next();
    var parameters = undefined;
    if(pre_element.hasAttribute("code_favorite_id")){
      parameters = {
        "code_favorite[id]": pre_element.readAttribute("code_favorite_id")
      }
    }else{
      var language = pre_element.readAttribute("class");
      language = (!language || language == "data_info")?pre_element.readAttribute("language"):language;
      var pre_index =  pre_element.readAttribute("pre_index") || link_element.up(".dp-highlighter").readAttribute("pre_index");
      parameters = {
        'code_favorite[source_url]': pre_element.readAttribute("source_url"),
        'code_favorite[page_title]': pre_element.readAttribute("title"),
        'code_favorite[language]': language,
        'code_favorite[codeable_id]': pre_element.readAttribute("codeable_id"),
        'code_favorite[codeable_type]': pre_element.readAttribute("codeable_type"),
        'code_favorite[code_index]': pre_index
      };
    }

    link_element.down('.star').hide();
    link_element.down('.spinner').show();
    new Ajax.Request('/admin/code/new_xhr', {
        method: 'post',
        parameters: parameters,
        onSuccess: function(response){
            $(document.getElementsByTagName('body')[0]).insert({
                bottom:response.responseText
            });
            link_element.down('.spinner').hide();
            link_element.down('.star').show();
        }
    });
}

