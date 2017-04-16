var ua = navigator.userAgent.toLowerCase();
if (!(ua.indexOf('ipad') > -1 || ua.indexOf('iphone') > -1 || ua.indexOf('android') > -1 || ua.indexOf('wap') > -1) && (
screen.height > 768 || screen.width > 1024)) {
  var _msg_pop_height = 250;

  function _message_node(id) {
    return document.getElementById(id)
  }
  function _message_child(p, c) {
    var pdom = _message_node(p);
    var nodes = pdom.childNodes;
    var cdom = false;
    for (var i = 0; i < nodes.length; i++) {
      if (nodes[i].tagName == c.toUpperCase()) {
        cdom = nodes[i];
        break
      }
    }
    return cdom
  }
  function _message_child_attr_add(p, id, type) {
    var pdom = _message_node(p);
    var nodes = pdom.getElementsByTagName("a");
    var fn = function() {
      _msg_item_click_count(id, type)
    };
    for (var i = 0; i < nodes.length; i++) {
      url = nodes[i].href;
      if (url.toLowerCase().indexOf("http://") == 0 || url.toLowerCase().indexOf("https://") == 0) {
        if (nodes[i].addEventListener) {
          nodes[i].addEventListener("click", fn, false)
        } else {
          if (nodes[i].attachEvent) {
            nodes[i].attachEvent("onclick", fn)
          }
        }
      }
    }
  }
  function _message_callScript(url, loaded, error, charset) {
    var script = document.createElement("script");
    if (typeof charset == "string") {
      script.charset = charset
    }
    script.onreadystatechange = function() {
      switch (this.readyState) {
        case "complete":
        case "loaded":
          if (typeof loaded == "function") {
            loaded()
          }
          if (script.parentNode) {
            script.parentNode.removeChild(script)
          }
          break
      }
    };
    script.onload = function() {
      if (typeof loaded == "function") {
        loaded()
      }
      if (script.parentNode) {
        script.parentNode.removeChild(script)
      }
    };
    script.onerror = function() {
      if (typeof error == "function") {
        error()
      }
      if (script.parentNode) {
        script.parentNode.removeChild(script)
      }
    };
    script.type = "text/javascript";
    script.defer = "true";
    script.src = url;
    var parent = document.getElementsByTagName("HEAD")[0] || document.documentElement;
    if (parent && parent.insertBefore) {
      parent.insertBefore(script, parent.firstChild)
    }
  }
  function _message_tips_pop(act) {
    var MsgPop = _message_child("_popup_msg_container", "div");
    var MsgPop = null == MsgPop ? document.getElementById("_popup_msg_container") : MsgPop;
    var popH = parseInt(MsgPop.clientHeight);
    if (act == "up") {
      MsgPop.style.display = "block";
      show = setInterval("_message_changeH('up')", 5)
    }
    if (act == "down") {
      hide = setInterval("_message_changeH('down')", 5)
    }
  }
  function _message_changeH(str) {
    var MsgPop = _message_child("_popup_msg_container", "div");
    var MsgPop = null == MsgPop ? document.getElementById("_popup_msg_container") : MsgPop;
    var popH = parseInt(MsgPop.clientHeight);
    if (str == "up") {
      if (popH < _msg_pop_height) {
        MsgPop.style.height = (popH + 2).toString() + "px"
      } else {
        clearInterval(show)
      }
    }
    if (str == "down") {
      if (popH >= 2) {
        var h = popH - 4;
        if(h<0) h = 0;
        MsgPop.style.height = h + "px";
      } else {
        MsgPop.style.display = "none";
        clearInterval(hide)
      }
    }
  }
  function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }function _msg_item_click_count(id, type) {
    var pv = document.createElement("img");
    var rand = Math.ceil(Math.random() * 10000);
    pv.src = "http://" + _msg_server + "/shm/count_click.php?id=" + id + "&t=" + type + "#" + rand;
    pv.style.display = "none";
    document.body.appendChild(pv)
  }
  function _message_callback(data) {
    if (data.id > 0) {
      var css = document.createElement("link");
      css.href = data.css;
      css.rel = "stylesheet";
      css.type = "text/css";
      document.body.appendChild(css);
      var container = document.createElement("div");
      container.style.display = "block";
      container.id = "_popup_msg_container";
      container.innerHTML = data.content;
      document.body.appendChild(container);
      if (!data.no_count_click) {
        _message_child_attr_add("_popup_msg_container", data.id, data.types)
      }
      var MsgPop = _message_child("_popup_msg_container", "div");
      if (MsgPop) {
        spheight = MsgPop.getAttribute("height");
        if (spheight) {
          spheight = spheight.replace("px", "");
          if (isNaN(spheight) == false) {
            _msg_pop_height = spheight
          }
        }
        MsgPop.style.display = "none";
        MsgPop.style.height = "0px"
      }
      setTimeout("_message_tips_pop('up')", 100);
      if (!data.no_count_view) {
        var pv = document.createElement("img");
        pv.src = "http://" + _msg_server + "/shm/count_new.php?id=" + data.id + "&t=" + data.types;
        pv.style.display = "none";
        document.body.appendChild(pv)
      }
    }
  }
  function _message_get_search_args(qkey) {
    var args = new Object();
    var query = document.location.search.substring(1);
    var ret = "";
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf("=");
      if (pos == -1) {
        continue
      }
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
      ret = unescape(value);
      break
    }
    return ret
  }
  var _msg_server = "message.csdn.net";
  if (_message_get_search_args("_msg_local") == 1) {
    _msg_server = "local.message.csdn.net"
  }
  var p = "1=1";
  if (window.location.search.indexOf("?") > -1) {
    var p = window.location.search.substring(1)
  }
  
  window.onload = function() {
    _message_callScript("http://" + _msg_server + "/shm/msg_new.php?" + p + "&jsoncallback=_message_callback")
  };
}



var _m_p__un = document.cookie.match(new RegExp("(^| )UserName=([^;]*)(;|$)"));
if (_m_p__un)
{
  _gaq.push(['_setCustomVar',1,'User Type','Member',2]);
}else
{
  _gaq.push(['_setCustomVar',1,'User Type','Visitor',2]);
}
