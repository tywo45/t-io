/*  Prototype JavaScript framework, version 1.6.1
 *  (c) 2005-2009 Sam Stephenson
 *
 *  Prototype is freely distributable under the terms of an MIT-style license.
 *  For details, see the Prototype web site: http://www.prototypejs.org/
 *
 *--------------------------------------------------------------------------*/

var Prototype = {
  Version: '1.6.1',

  Browser: (function(){
    var ua = navigator.userAgent;
    var isOpera = Object.prototype.toString.call(window.opera) == '[object Opera]';
    return {
      IE:             !!window.attachEvent && !isOpera,
      Opera:          isOpera,
      WebKit:         ua.indexOf('AppleWebKit/') > -1,
      Gecko:          ua.indexOf('Gecko') > -1 && ua.indexOf('KHTML') === -1,
      MobileSafari:   /Apple.*Mobile.*Safari/.test(ua)
    }
  })(),

  BrowserFeatures: {
    XPath: !!document.evaluate,
    SelectorsAPI: !!document.querySelector,
    ElementExtensions: (function() {
      var constructor = window.Element || window.HTMLElement;
      return !!(constructor && constructor.prototype);
    })(),
    SpecificElementExtensions: (function() {
      if (typeof window.HTMLDivElement !== 'undefined')
        return true;

      var div = document.createElement('div');
      var form = document.createElement('form');
      var isSupported = false;

      if (div['__proto__'] && (div['__proto__'] !== form['__proto__'])) {
        isSupported = true;
      }

      div = form = null;

      return isSupported;
    })()
  },

  ScriptFragment: '<script[^>]*>([\\S\\s]*?)<\/script>',
  JSONFilter: /^\/\*-secure-([\s\S]*)\*\/\s*$/,

  emptyFunction: function() { },
  K: function(x) {return x}
};

if (Prototype.Browser.MobileSafari)
  Prototype.BrowserFeatures.SpecificElementExtensions = false;


var Abstract = { };


var Try = {
  these: function() {
    var returnValue;

    for (var i = 0, length = arguments.length; i < length; i++) {
      var lambda = arguments[i];
      try {
        returnValue = lambda();
        break;
      } catch (e) { }
    }

    return returnValue;
  }
};

/* Based on Alex Arnell's inheritance implementation. */

var Class = (function() {
  function subclass() {};
  function create() {
    var parent = null, properties = $A(arguments);
    if (Object.isFunction(properties[0]))
      parent = properties.shift();

    function klass() {
      this.initialize.apply(this, arguments);
    }

    Object.extend(klass, Class.Methods);
    klass.superclass = parent;
    klass.subclasses = [];

    if (parent) {
      subclass.prototype = parent.prototype;
      klass.prototype = new subclass;
      parent.subclasses.push(klass);
    }

    for (var i = 0; i < properties.length; i++)
      klass.addMethods(properties[i]);

    if (!klass.prototype.initialize)
      klass.prototype.initialize = Prototype.emptyFunction;

    klass.prototype.constructor = klass;
    return klass;
  }

  function addMethods(source) {
    var ancestor   = this.superclass && this.superclass.prototype;
    var properties = Object.keys(source);

    if (!Object.keys({toString: true}).length) {
      if (source.toString != Object.prototype.toString)
        properties.push("toString");
      if (source.valueOf != Object.prototype.valueOf)
        properties.push("valueOf");
    }

    for (var i = 0, length = properties.length; i < length; i++) {
      var property = properties[i], value = source[property];
      if (ancestor && Object.isFunction(value) &&
          value.argumentNames().first() == "$super") {
        var method = value;
        value = (function(m) {
          return function() {return ancestor[m].apply(this, arguments);};
        })(property).wrap(method);

        value.valueOf = method.valueOf.bind(method);
        value.toString = method.toString.bind(method);
      }
      this.prototype[property] = value;
    }

    return this;
  }

  return {
    create: create,
    Methods: {
      addMethods: addMethods
    }
  };
})();
(function() {

  var _toString = Object.prototype.toString;

  function extend(destination, source) {
    for (var property in source)
      destination[property] = source[property];
    return destination;
  }

  function inspect(object) {
    try {
      if (isUndefined(object)) return 'undefined';
      if (object === null) return 'null';
      return object.inspect ? object.inspect() : String(object);
    } catch (e) {
      if (e instanceof RangeError) return '...';
      throw e;
    }
  }

  function toJSON(object) {
    var type = typeof object;
    switch (type) {
      case 'undefined':
      case 'function':
      case 'unknown':return;
      case 'boolean':return object.toString();
    }

    if (object === null) return 'null';
    if (object.toJSON) return object.toJSON();
    if (isElement(object)) return;

    var results = [];
    for (var property in object) {
      var value = toJSON(object[property]);
      if (!isUndefined(value))
        results.push(property.toJSON() + ': ' + value);
    }

    return '{' + results.join(', ') + '}';
  }

  function toQueryString(object) {
    return $H(object).toQueryString();
  }

  function toHTML(object) {
    return object && object.toHTML ? object.toHTML() : String.interpret(object);
  }

  function keys(object) {
    var results = [];
    for (var property in object)
      results.push(property);
    return results;
  }

  function values(object) {
    var results = [];
    for (var property in object)
      results.push(object[property]);
    return results;
  }

  function clone(object) {
    return extend({ }, object);
  }

  function isElement(object) {
    return !!(object && object.nodeType == 1);
  }

  function isArray(object) {
    return _toString.call(object) == "[object Array]";
  }


  function isHash(object) {
    return object instanceof Hash;
  }

  function isFunction(object) {
    return typeof object === "function";
  }

  function isString(object) {
    return _toString.call(object) == "[object String]";
  }

  function isNumber(object) {
    return _toString.call(object) == "[object Number]";
  }

  function isUndefined(object) {
    return typeof object === "undefined";
  }

  extend(Object, {
    extend:        extend,
    inspect:       inspect,
    toJSON:        toJSON,
    toQueryString: toQueryString,
    toHTML:        toHTML,
    keys:          keys,
    values:        values,
    clone:         clone,
    isElement:     isElement,
    isArray:       isArray,
    isHash:        isHash,
    isFunction:    isFunction,
    isString:      isString,
    isNumber:      isNumber,
    isUndefined:   isUndefined
  });
})();
Object.extend(Function.prototype, (function() {
  var slice = Array.prototype.slice;

  function update(array, args) {
    var arrayLength = array.length, length = args.length;
    while (length--) array[arrayLength + length] = args[length];
    return array;
  }

  function merge(array, args) {
    array = slice.call(array, 0);
    return update(array, args);
  }

  function argumentNames() {
    var names = this.toString().match(/^[\s\(]*function[^(]*\(([^)]*)\)/)[1]
      .replace(/\/\/.*?[\r\n]|\/\*(?:.|[\r\n])*?\*\//g, '')
      .replace(/\s+/g, '').split(',');
    return names.length == 1 && !names[0] ? [] : names;
  }

  function bind(context) {
    if (arguments.length < 2 && Object.isUndefined(arguments[0])) return this;
    var __method = this, args = slice.call(arguments, 1);
    return function() {
      var a = merge(args, arguments);
      return __method.apply(context, a);
    }
  }

  function bindAsEventListener(context) {
    var __method = this, args = slice.call(arguments, 1);
    return function(event) {
      var a = update([event || window.event], args);
      return __method.apply(context, a);
    }
  }

  function curry() {
    if (!arguments.length) return this;
    var __method = this, args = slice.call(arguments, 0);
    return function() {
      var a = merge(args, arguments);
      return __method.apply(this, a);
    }
  }

  function delay(timeout) {
    var __method = this, args = slice.call(arguments, 1);
    timeout = timeout * 1000
    return window.setTimeout(function() {
      return __method.apply(__method, args);
    }, timeout);
  }

  function defer() {
    var args = update([0.01], arguments);
    return this.delay.apply(this, args);
  }

  function wrap(wrapper) {
    var __method = this;
    return function() {
      var a = update([__method.bind(this)], arguments);
      return wrapper.apply(this, a);
    }
  }

  function methodize() {
    if (this._methodized) return this._methodized;
    var __method = this;
    return this._methodized = function() {
      var a = update([this], arguments);
      return __method.apply(null, a);
    };
  }

  return {
    argumentNames:       argumentNames,
    bind:                bind,
    bindAsEventListener: bindAsEventListener,
    curry:               curry,
    delay:               delay,
    defer:               defer,
    wrap:                wrap,
    methodize:           methodize
  }
})());


Date.prototype.toJSON = function() {
  return '"' + this.getUTCFullYear() + '-' +
    (this.getUTCMonth() + 1).toPaddedString(2) + '-' +
    this.getUTCDate().toPaddedString(2) + 'T' +
    this.getUTCHours().toPaddedString(2) + ':' +
    this.getUTCMinutes().toPaddedString(2) + ':' +
    this.getUTCSeconds().toPaddedString(2) + 'Z"';
};


RegExp.prototype.match = RegExp.prototype.test;

RegExp.escape = function(str) {
  return String(str).replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');
};
var PeriodicalExecuter = Class.create({
  initialize: function(callback, frequency) {
    this.callback = callback;
    this.frequency = frequency;
    this.currentlyExecuting = false;

    this.registerCallback();
  },

  registerCallback: function() {
    this.timer = setInterval(this.onTimerEvent.bind(this), this.frequency * 1000);
  },

  execute: function() {
    this.callback(this);
  },

  stop: function() {
    if (!this.timer) return;
    clearInterval(this.timer);
    this.timer = null;
  },

  onTimerEvent: function() {
    if (!this.currentlyExecuting) {
      try {
        this.currentlyExecuting = true;
        this.execute();
        this.currentlyExecuting = false;
      } catch(e) {
        this.currentlyExecuting = false;
        throw e;
      }
    }
  }
});
Object.extend(String, {
  interpret: function(value) {
    return value == null ? '' : String(value);
  },
  specialChar: {
    '\b': '\\b',
    '\t': '\\t',
    '\n': '\\n',
    '\f': '\\f',
    '\r': '\\r',
    '\\': '\\\\'
  }
});

Object.extend(String.prototype, (function() {

  function prepareReplacement(replacement) {
    if (Object.isFunction(replacement)) return replacement;
    var template = new Template(replacement);
    return function(match) {return template.evaluate(match)};
  }

  function gsub(pattern, replacement) {
    var result = '', source = this, match;
    replacement = prepareReplacement(replacement);

    if (Object.isString(pattern))
      pattern = RegExp.escape(pattern);

    if (!(pattern.length || pattern.source)) {
      replacement = replacement('');
      return replacement + source.split('').join(replacement) + replacement;
    }

    while (source.length > 0) {
      if (match = source.match(pattern)) {
        result += source.slice(0, match.index);
        result += String.interpret(replacement(match));
        source  = source.slice(match.index + match[0].length);
      } else {
        result += source, source = '';
      }
    }
    return result;
  }

  function sub(pattern, replacement, count) {
    replacement = prepareReplacement(replacement);
    count = Object.isUndefined(count) ? 1 : count;

    return this.gsub(pattern, function(match) {
      if (--count < 0) return match[0];
      return replacement(match);
    });
  }

  function scan(pattern, iterator) {
    this.gsub(pattern, iterator);
    return String(this);
  }

  function truncate(length, truncation) {
    length = length || 30;
    truncation = Object.isUndefined(truncation) ? '...' : truncation;
    return this.length > length ?
      this.slice(0, length - truncation.length) + truncation : String(this);
  }

  function strip() {
    return this.replace(/^\s+/, '').replace(/\s+$/, '');
  }

  function stripTags() {
    return this.replace(/<\w+(\s+("[^"]*"|'[^']*'|[^>])+)?>|<\/\w+>/gi, '');
  }

  function stripScripts() {
    return this.replace(new RegExp(Prototype.ScriptFragment, 'img'), '');
  }

  function extractScripts() {
    var matchAll = new RegExp(Prototype.ScriptFragment, 'img');
    var matchOne = new RegExp(Prototype.ScriptFragment, 'im');
    return (this.match(matchAll) || []).map(function(scriptTag) {
      return (scriptTag.match(matchOne) || ['', ''])[1];
    });
  }

  function evalScripts() {
    return this.extractScripts().map(function(script) {return eval(script)});
  }

  function escapeHTML() {
    return this.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
  }

  function unescapeHTML() {
    return this.stripTags().replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&amp;/g,'&');
  }


  function toQueryParams(separator) {
    var match = this.strip().match(/([^?#]*)(#.*)?$/);
    if (!match) return { };

    return match[1].split(separator || '&').inject({ }, function(hash, pair) {
      if ((pair = pair.split('='))[0]) {
        var key = decodeURIComponent(pair.shift());
        var value = pair.length > 1 ? pair.join('=') : pair[0];
        if (value != undefined) value = decodeURIComponent(value);

        if (key in hash) {
          if (!Object.isArray(hash[key])) hash[key] = [hash[key]];
          hash[key].push(value);
        }
        else hash[key] = value;
      }
      return hash;
    });
  }

  function toArray() {
    return this.split('');
  }

  function succ() {
    return this.slice(0, this.length - 1) +
      String.fromCharCode(this.charCodeAt(this.length - 1) + 1);
  }

  function times(count) {
    return count < 1 ? '' : new Array(count + 1).join(this);
  }

  function camelize() {
    var parts = this.split('-'), len = parts.length;
    if (len == 1) return parts[0];

    var camelized = this.charAt(0) == '-'
      ? parts[0].charAt(0).toUpperCase() + parts[0].substring(1)
      : parts[0];

    for (var i = 1; i < len; i++)
      camelized += parts[i].charAt(0).toUpperCase() + parts[i].substring(1);

    return camelized;
  }

  function capitalize() {
    return this.charAt(0).toUpperCase() + this.substring(1).toLowerCase();
  }

  function underscore() {
    return this.replace(/::/g, '/')
               .replace(/([A-Z]+)([A-Z][a-z])/g, '$1_$2')
               .replace(/([a-z\d])([A-Z])/g, '$1_$2')
               .replace(/-/g, '_')
               .toLowerCase();
  }

  function dasherize() {
    return this.replace(/_/g, '-');
  }

  function inspect(useDoubleQuotes) {
    var escapedString = this.replace(/[\x00-\x1f\\]/g, function(character) {
      if (character in String.specialChar) {
        return String.specialChar[character];
      }
      return '\\u00' + character.charCodeAt().toPaddedString(2, 16);
    });
    if (useDoubleQuotes) return '"' + escapedString.replace(/"/g, '\\"') + '"';
    return "'" + escapedString.replace(/'/g, '\\\'') + "'";
  }

  function toJSON() {
    return this.inspect(true);
  }

  function unfilterJSON(filter) {
    return this.replace(filter || Prototype.JSONFilter, '$1');
  }

  function isJSON() {
    var str = this;
    if (str.blank()) return false;
    str = this.replace(/\\./g, '@').replace(/"[^"\\\n\r]*"/g, '');
    return (/^[,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]*$/).test(str);
  }

  function evalJSON(sanitize) {
    var json = this.unfilterJSON();
    try {
      if (!sanitize || json.isJSON()) return eval('(' + json + ')');
    } catch (e) { }
    throw new SyntaxError('Badly formed JSON string: ' + this.inspect());
  }

  function include(pattern) {
    return this.indexOf(pattern) > -1;
  }

  function startsWith(pattern) {
    return this.indexOf(pattern) === 0;
  }

  function endsWith(pattern) {
    var d = this.length - pattern.length;
    return d >= 0 && this.lastIndexOf(pattern) === d;
  }

  function empty() {
    return this == '';
  }

  function blank() {
    return /^\s*$/.test(this);
  }

  function interpolate(object, pattern) {
    return new Template(this, pattern).evaluate(object);
  }

  return {
    gsub:           gsub,
    sub:            sub,
    scan:           scan,
    truncate:       truncate,
    strip:          String.prototype.trim ? String.prototype.trim : strip,
    stripTags:      stripTags,
    stripScripts:   stripScripts,
    extractScripts: extractScripts,
    evalScripts:    evalScripts,
    escapeHTML:     escapeHTML,
    unescapeHTML:   unescapeHTML,
    toQueryParams:  toQueryParams,
    parseQuery:     toQueryParams,
    toArray:        toArray,
    succ:           succ,
    times:          times,
    camelize:       camelize,
    capitalize:     capitalize,
    underscore:     underscore,
    dasherize:      dasherize,
    inspect:        inspect,
    toJSON:         toJSON,
    unfilterJSON:   unfilterJSON,
    isJSON:         isJSON,
    evalJSON:       evalJSON,
    include:        include,
    startsWith:     startsWith,
    endsWith:       endsWith,
    empty:          empty,
    blank:          blank,
    interpolate:    interpolate
  };
})());

var Template = Class.create({
  initialize: function(template, pattern) {
    this.template = template.toString();
    this.pattern = pattern || Template.Pattern;
  },

  evaluate: function(object) {
    if (object && Object.isFunction(object.toTemplateReplacements))
      object = object.toTemplateReplacements();

    return this.template.gsub(this.pattern, function(match) {
      if (object == null) return (match[1] + '');

      var before = match[1] || '';
      if (before == '\\') return match[2];

      var ctx = object, expr = match[3];
      var pattern = /^([^.[]+|\[((?:.*?[^\\])?)\])(\.|\[|$)/;
      match = pattern.exec(expr);
      if (match == null) return before;

      while (match != null) {
        var comp = match[1].startsWith('[') ? match[2].replace(/\\\\]/g, ']') : match[1];
        ctx = ctx[comp];
        if (null == ctx || '' == match[3]) break;
        expr = expr.substring('[' == match[3] ? match[1].length : match[0].length);
        match = pattern.exec(expr);
      }

      return before + String.interpret(ctx);
    });
  }
});
Template.Pattern = /(^|.|\r|\n)(#\{(.*?)\})/;

var $break = { };

var Enumerable = (function() {
  function each(iterator, context) {
    var index = 0;
    try {
      this._each(function(value) {
        iterator.call(context, value, index++);
      });
    } catch (e) {
      if (e != $break) throw e;
    }
    return this;
  }

  function eachSlice(number, iterator, context) {
    var index = -number, slices = [], array = this.toArray();
    if (number < 1) return array;
    while ((index += number) < array.length)
      slices.push(array.slice(index, index+number));
    return slices.collect(iterator, context);
  }

  function all(iterator, context) {
    iterator = iterator || Prototype.K;
    var result = true;
    this.each(function(value, index) {
      result = result && !!iterator.call(context, value, index);
      if (!result) throw $break;
    });
    return result;
  }

  function any(iterator, context) {
    iterator = iterator || Prototype.K;
    var result = false;
    this.each(function(value, index) {
      if (result = !!iterator.call(context, value, index))
        throw $break;
    });
    return result;
  }

  function collect(iterator, context) {
    iterator = iterator || Prototype.K;
    var results = [];
    this.each(function(value, index) {
      results.push(iterator.call(context, value, index));
    });
    return results;
  }

  function detect(iterator, context) {
    var result;
    this.each(function(value, index) {
      if (iterator.call(context, value, index)) {
        result = value;
        throw $break;
      }
    });
    return result;
  }

  function findAll(iterator, context) {
    var results = [];
    this.each(function(value, index) {
      if (iterator.call(context, value, index))
        results.push(value);
    });
    return results;
  }

  function grep(filter, iterator, context) {
    iterator = iterator || Prototype.K;
    var results = [];

    if (Object.isString(filter))
      filter = new RegExp(RegExp.escape(filter));

    this.each(function(value, index) {
      if (filter.match(value))
        results.push(iterator.call(context, value, index));
    });
    return results;
  }

  function include(object) {
    if (Object.isFunction(this.indexOf))
      if (this.indexOf(object) != -1) return true;

    var found = false;
    this.each(function(value) {
      if (value == object) {
        found = true;
        throw $break;
      }
    });
    return found;
  }

  function inGroupsOf(number, fillWith) {
    fillWith = Object.isUndefined(fillWith) ? null : fillWith;
    return this.eachSlice(number, function(slice) {
      while(slice.length < number) slice.push(fillWith);
      return slice;
    });
  }

  function inject(memo, iterator, context) {
    this.each(function(value, index) {
      memo = iterator.call(context, memo, value, index);
    });
    return memo;
  }

  function invoke(method) {
    var args = $A(arguments).slice(1);
    return this.map(function(value) {
      return value[method].apply(value, args);
    });
  }

  function max(iterator, context) {
    iterator = iterator || Prototype.K;
    var result;
    this.each(function(value, index) {
      value = iterator.call(context, value, index);
      if (result == null || value >= result)
        result = value;
    });
    return result;
  }

  function min(iterator, context) {
    iterator = iterator || Prototype.K;
    var result;
    this.each(function(value, index) {
      value = iterator.call(context, value, index);
      if (result == null || value < result)
        result = value;
    });
    return result;
  }

  function partition(iterator, context) {
    iterator = iterator || Prototype.K;
    var trues = [], falses = [];
    this.each(function(value, index) {
      (iterator.call(context, value, index) ?
        trues : falses).push(value);
    });
    return [trues, falses];
  }

  function pluck(property) {
    var results = [];
    this.each(function(value) {
      results.push(value[property]);
    });
    return results;
  }

  function reject(iterator, context) {
    var results = [];
    this.each(function(value, index) {
      if (!iterator.call(context, value, index))
        results.push(value);
    });
    return results;
  }

  function sortBy(iterator, context) {
    return this.map(function(value, index) {
      return {
        value: value,
        criteria: iterator.call(context, value, index)
      };
    }).sort(function(left, right) {
      var a = left.criteria, b = right.criteria;
      return a < b ? -1 : a > b ? 1 : 0;
    }).pluck('value');
  }

  function toArray() {
    return this.map();
  }

  function zip() {
    var iterator = Prototype.K, args = $A(arguments);
    if (Object.isFunction(args.last()))
      iterator = args.pop();

    var collections = [this].concat(args).map($A);
    return this.map(function(value, index) {
      return iterator(collections.pluck(index));
    });
  }

  function size() {
    return this.toArray().length;
  }

  function inspect() {
    return '#<Enumerable:' + this.toArray().inspect() + '>';
  }









  return {
    each:       each,
    eachSlice:  eachSlice,
    all:        all,
    every:      all,
    any:        any,
    some:       any,
    collect:    collect,
    map:        collect,
    detect:     detect,
    findAll:    findAll,
    select:     findAll,
    filter:     findAll,
    grep:       grep,
    include:    include,
    member:     include,
    inGroupsOf: inGroupsOf,
    inject:     inject,
    invoke:     invoke,
    max:        max,
    min:        min,
    partition:  partition,
    pluck:      pluck,
    reject:     reject,
    sortBy:     sortBy,
    toArray:    toArray,
    entries:    toArray,
    zip:        zip,
    size:       size,
    inspect:    inspect,
    find:       detect
  };
})();
function $A(iterable) {
  if (!iterable) return [];
  if ('toArray' in Object(iterable)) return iterable.toArray();
  var length = iterable.length || 0, results = new Array(length);
  while (length--) results[length] = iterable[length];
  return results;
}

function $w(string) {
  if (!Object.isString(string)) return [];
  string = string.strip();
  return string ? string.split(/\s+/) : [];
}

Array.from = $A;


(function() {
  var arrayProto = Array.prototype,
      slice = arrayProto.slice,
      _each = arrayProto.forEach; // use native browser JS 1.6 implementation if available

  function each(iterator) {
    for (var i = 0, length = this.length; i < length; i++)
      iterator(this[i]);
  }
  if (!_each) _each = each;

  function clear() {
    this.length = 0;
    return this;
  }

  function first() {
    return this[0];
  }

  function last() {
    return this[this.length - 1];
  }

  function compact() {
    return this.select(function(value) {
      return value != null;
    });
  }

  function flatten() {
    return this.inject([], function(array, value) {
      if (Object.isArray(value))
        return array.concat(value.flatten());
      array.push(value);
      return array;
    });
  }

  function without() {
    var values = slice.call(arguments, 0);
    return this.select(function(value) {
      return !values.include(value);
    });
  }

  function reverse(inline) {
    return (inline !== false ? this : this.toArray())._reverse();
  }

  function uniq(sorted) {
    return this.inject([], function(array, value, index) {
      if (0 == index || (sorted ? array.last() != value : !array.include(value)))
        array.push(value);
      return array;
    });
  }

  function intersect(array) {
    return this.uniq().findAll(function(item) {
      return array.detect(function(value) {return item === value});
    });
  }


  function clone() {
    return slice.call(this, 0);
  }

  function size() {
    return this.length;
  }

  function inspect() {
    return '[' + this.map(Object.inspect).join(', ') + ']';
  }

  function toJSON() {
    var results = [];
    this.each(function(object) {
      var value = Object.toJSON(object);
      if (!Object.isUndefined(value)) results.push(value);
    });
    return '[' + results.join(', ') + ']';
  }

  function indexOf(item, i) {
    i || (i = 0);
    var length = this.length;
    if (i < 0) i = length + i;
    for (; i < length; i++)
      if (this[i] === item) return i;
    return -1;
  }

  function lastIndexOf(item, i) {
    i = isNaN(i) ? this.length : (i < 0 ? this.length + i : i) + 1;
    var n = this.slice(0, i).reverse().indexOf(item);
    return (n < 0) ? n : i - n - 1;
  }

  function concat() {
    var array = slice.call(this, 0), item;
    for (var i = 0, length = arguments.length; i < length; i++) {
      item = arguments[i];
      if (Object.isArray(item) && !('callee' in item)) {
        for (var j = 0, arrayLength = item.length; j < arrayLength; j++)
          array.push(item[j]);
      } else {
        array.push(item);
      }
    }
    return array;
  }

  Object.extend(arrayProto, Enumerable);

  if (!arrayProto._reverse)
    arrayProto._reverse = arrayProto.reverse;

  Object.extend(arrayProto, {
    _each:     _each,
    clear:     clear,
    first:     first,
    last:      last,
    compact:   compact,
    flatten:   flatten,
    without:   without,
    reverse:   reverse,
    uniq:      uniq,
    intersect: intersect,
    clone:     clone,
    toArray:   clone,
    size:      size,
    inspect:   inspect,
    toJSON:    toJSON
  });

  var CONCAT_ARGUMENTS_BUGGY = (function() {
    return [].concat(arguments)[0][0] !== 1;
  })(1,2)

  if (CONCAT_ARGUMENTS_BUGGY) arrayProto.concat = concat;

  if (!arrayProto.indexOf) arrayProto.indexOf = indexOf;
  if (!arrayProto.lastIndexOf) arrayProto.lastIndexOf = lastIndexOf;
})();
function $H(object) {
  return new Hash(object);
};

var Hash = Class.create(Enumerable, (function() {
  function initialize(object) {
    this._object = Object.isHash(object) ? object.toObject() : Object.clone(object);
  }

  function _each(iterator) {
    for (var key in this._object) {
      var value = this._object[key], pair = [key, value];
      pair.key = key;
      pair.value = value;
      iterator(pair);
    }
  }

  function set(key, value) {
    return this._object[key] = value;
  }

  function get(key) {
    if (this._object[key] !== Object.prototype[key])
      return this._object[key];
  }

  function unset(key) {
    var value = this._object[key];
    delete this._object[key];
    return value;
  }

  function toObject() {
    return Object.clone(this._object);
  }

  function keys() {
    return this.pluck('key');
  }

  function values() {
    return this.pluck('value');
  }

  function index(value) {
    var match = this.detect(function(pair) {
      return pair.value === value;
    });
    return match && match.key;
  }

  function merge(object) {
    return this.clone().update(object);
  }

  function update(object) {
    return new Hash(object).inject(this, function(result, pair) {
      result.set(pair.key, pair.value);
      return result;
    });
  }

  function toQueryPair(key, value) {
    if (Object.isUndefined(value)) return key;
    return key + '=' + encodeURIComponent(String.interpret(value));
  }

  function toQueryString() {
    return this.inject([], function(results, pair) {
      var key = encodeURIComponent(pair.key), values = pair.value;

      if (values && typeof values == 'object') {
        if (Object.isArray(values))
          return results.concat(values.map(toQueryPair.curry(key)));
      } else results.push(toQueryPair(key, values));
      return results;
    }).join('&');
  }

  function inspect() {
    return '#<Hash:{' + this.map(function(pair) {
      return pair.map(Object.inspect).join(': ');
    }).join(', ') + '}>';
  }

  function toJSON() {
    return Object.toJSON(this.toObject());
  }

  function clone() {
    return new Hash(this);
  }

  return {
    initialize:             initialize,
    _each:                  _each,
    set:                    set,
    get:                    get,
    unset:                  unset,
    toObject:               toObject,
    toTemplateReplacements: toObject,
    keys:                   keys,
    values:                 values,
    index:                  index,
    merge:                  merge,
    update:                 update,
    toQueryString:          toQueryString,
    inspect:                inspect,
    toJSON:                 toJSON,
    clone:                  clone
  };
})());

Hash.from = $H;
Object.extend(Number.prototype, (function() {
  function toColorPart() {
    return this.toPaddedString(2, 16);
  }

  function succ() {
    return this + 1;
  }

  function times(iterator, context) {
    $R(0, this, true).each(iterator, context);
    return this;
  }

  function toPaddedString(length, radix) {
    var string = this.toString(radix || 10);
    return '0'.times(length - string.length) + string;
  }

  function toJSON() {
    return isFinite(this) ? this.toString() : 'null';
  }

  function abs() {
    return Math.abs(this);
  }

  function round() {
    return Math.round(this);
  }

  function ceil() {
    return Math.ceil(this);
  }

  function floor() {
    return Math.floor(this);
  }

  return {
    toColorPart:    toColorPart,
    succ:           succ,
    times:          times,
    toPaddedString: toPaddedString,
    toJSON:         toJSON,
    abs:            abs,
    round:          round,
    ceil:           ceil,
    floor:          floor
  };
})());

function $R(start, end, exclusive) {
  return new ObjectRange(start, end, exclusive);
}

var ObjectRange = Class.create(Enumerable, (function() {
  function initialize(start, end, exclusive) {
    this.start = start;
    this.end = end;
    this.exclusive = exclusive;
  }

  function _each(iterator) {
    var value = this.start;
    while (this.include(value)) {
      iterator(value);
      value = value.succ();
    }
  }

  function include(value) {
    if (value < this.start)
      return false;
    if (this.exclusive)
      return value < this.end;
    return value <= this.end;
  }

  return {
    initialize: initialize,
    _each:      _each,
    include:    include
  };
})());



var Ajax = {
  getTransport: function() {
    return Try.these(
      function() {return new XMLHttpRequest()},
      function() {return new ActiveXObject('Msxml2.XMLHTTP')},
      function() {return new ActiveXObject('Microsoft.XMLHTTP')}
    ) || false;
  },

  activeRequestCount: 0
};

Ajax.Responders = {
  responders: [],

  _each: function(iterator) {
    this.responders._each(iterator);
  },

  register: function(responder) {
    if (!this.include(responder))
      this.responders.push(responder);
  },

  unregister: function(responder) {
    this.responders = this.responders.without(responder);
  },

  dispatch: function(callback, request, transport, json) {
    this.each(function(responder) {
      if (Object.isFunction(responder[callback])) {
        try {
          responder[callback].apply(responder, [request, transport, json]);
        } catch (e) { }
      }
    });
  }
};

Object.extend(Ajax.Responders, Enumerable);

Ajax.Responders.register({
  onCreate:   function() {Ajax.activeRequestCount++},
  onComplete: function() {Ajax.activeRequestCount--}
});
Ajax.Base = Class.create({
  initialize: function(options) {
    this.options = {
      method:       'post',
      asynchronous: true,
      contentType:  'application/x-www-form-urlencoded',
      encoding:     'UTF-8',
      parameters:   '',
      evalJSON:     true,
      evalJS:       true
    };
    Object.extend(this.options, options || { });

    this.options.method = this.options.method.toLowerCase();

    if (Object.isString(this.options.parameters))
      this.options.parameters = this.options.parameters.toQueryParams();
    else if (Object.isHash(this.options.parameters))
      this.options.parameters = this.options.parameters.toObject();
  }
});
Ajax.Request = Class.create(Ajax.Base, {
  _complete: false,

  initialize: function($super, url, options) {
    $super(options);
    this.transport = Ajax.getTransport();
    this.request(url);
  },

  request: function(url) {
    this.url = url;
    this.method = this.options.method;
    var params = Object.clone(this.options.parameters);

    if (!['get', 'post'].include(this.method)) {
      params['_method'] = this.method;
      this.method = 'post';
    }

    this.parameters = params;

    if (params = Object.toQueryString(params)) {
      if (this.method == 'get')
        this.url += (this.url.include('?') ? '&' : '?') + params;
      else if (/Konqueror|Safari|KHTML/.test(navigator.userAgent))
        params += '&_=';
    }

    try {
      var response = new Ajax.Response(this);
      if (this.options.onCreate) this.options.onCreate(response);
      Ajax.Responders.dispatch('onCreate', this, response);

      this.transport.open(this.method.toUpperCase(), this.url,
        this.options.asynchronous);

      if (this.options.asynchronous) this.respondToReadyState.bind(this).defer(1);

      this.transport.onreadystatechange = this.onStateChange.bind(this);
      this.setRequestHeaders();

      this.body = this.method == 'post' ? (this.options.postBody || params) : null;
      this.transport.send(this.body);

      /* Force Firefox to handle ready state 4 for synchronous requests */
      if (!this.options.asynchronous && this.transport.overrideMimeType)
        this.onStateChange();

    }
    catch (e) {
      this.dispatchException(e);
    }
  },

  onStateChange: function() {
    var readyState = this.transport.readyState;
    if (readyState > 1 && !((readyState == 4) && this._complete))
      this.respondToReadyState(this.transport.readyState);
  },

  setRequestHeaders: function() {
    var headers = {
      'X-Requested-With': 'XMLHttpRequest',
      'X-Prototype-Version': Prototype.Version,
      'Accept': 'text/javascript, text/html, application/xml, text/xml, */*'
    };

    if (this.method == 'post') {
      headers['Content-type'] = this.options.contentType +
        (this.options.encoding ? '; charset=' + this.options.encoding : '');

      /* Force "Connection: close" for older Mozilla browsers to work
       * around a bug where XMLHttpRequest sends an incorrect
       * Content-length header. See Mozilla Bugzilla #246651.
       */
      if (this.transport.overrideMimeType &&
          (navigator.userAgent.match(/Gecko\/(\d{4})/) || [0,2005])[1] < 2005)
            headers['Connection'] = 'close';
    }

    if (typeof this.options.requestHeaders == 'object') {
      var extras = this.options.requestHeaders;

      if (Object.isFunction(extras.push))
        for (var i = 0, length = extras.length; i < length; i += 2)
          headers[extras[i]] = extras[i+1];
      else
        $H(extras).each(function(pair) {headers[pair.key] = pair.value});
    }

    for (var name in headers)
      this.transport.setRequestHeader(name, headers[name]);
  },

  success: function() {
    var status = this.getStatus();
    return !status || (status >= 200 && status < 300);
  },

  getStatus: function() {
    try {
      return this.transport.status || 0;
    } catch (e) {return 0}
  },

  respondToReadyState: function(readyState) {
    var state = Ajax.Request.Events[readyState], response = new Ajax.Response(this);

    if (state == 'Complete') {
      try {
        this._complete = true;
        (this.options['on' + response.status]
         || this.options['on' + (this.success() ? 'Success' : 'Failure')]
         || Prototype.emptyFunction)(response, response.headerJSON);
      } catch (e) {
        this.dispatchException(e);
      }

      var contentType = response.getHeader('Content-type');
      if (this.options.evalJS == 'force'
          || (this.options.evalJS && this.isSameOrigin() && contentType
          && contentType.match(/^\s*(text|application)\/(x-)?(java|ecma)script(;.*)?\s*$/i)))
        this.evalResponse();
    }

    try {
      (this.options['on' + state] || Prototype.emptyFunction)(response, response.headerJSON);
      Ajax.Responders.dispatch('on' + state, this, response, response.headerJSON);
    } catch (e) {
      this.dispatchException(e);
    }

    if (state == 'Complete') {
      this.transport.onreadystatechange = Prototype.emptyFunction;
    }
  },

  isSameOrigin: function() {
    var m = this.url.match(/^\s*https?:\/\/[^\/]*/);
    return !m || (m[0] == '#{protocol}//#{domain}#{port}'.interpolate({
      protocol: location.protocol,
      domain: document.domain,
      port: location.port ? ':' + location.port : ''
    }));
  },

  getHeader: function(name) {
    try {
      return this.transport.getResponseHeader(name) || null;
    } catch (e) {return null;}
  },

  evalResponse: function() {
    try {
      return eval((this.transport.responseText || '').unfilterJSON());
    } catch (e) {
      this.dispatchException(e);
    }
  },

  dispatchException: function(exception) {
    (this.options.onException || Prototype.emptyFunction)(this, exception);
    Ajax.Responders.dispatch('onException', this, exception);
  }
});

Ajax.Request.Events =
  ['Uninitialized', 'Loading', 'Loaded', 'Interactive', 'Complete'];








Ajax.Response = Class.create({
  initialize: function(request){
    this.request = request;
    var transport  = this.transport  = request.transport,
        readyState = this.readyState = transport.readyState;

    if((readyState > 2 && !Prototype.Browser.IE) || readyState == 4) {
      this.status       = this.getStatus();
      this.statusText   = this.getStatusText();
      this.responseText = String.interpret(transport.responseText);
      this.headerJSON   = this._getHeaderJSON();
    }

    if(readyState == 4) {
      var xml = transport.responseXML;
      this.responseXML  = Object.isUndefined(xml) ? null : xml;
      this.responseJSON = this._getResponseJSON();
    }
  },

  status:      0,

  statusText: '',

  getStatus: Ajax.Request.prototype.getStatus,

  getStatusText: function() {
    try {
      return this.transport.statusText || '';
    } catch (e) {return ''}
  },

  getHeader: Ajax.Request.prototype.getHeader,

  getAllHeaders: function() {
    try {
      return this.getAllResponseHeaders();
    } catch (e) {return null}
  },

  getResponseHeader: function(name) {
    return this.transport.getResponseHeader(name);
  },

  getAllResponseHeaders: function() {
    return this.transport.getAllResponseHeaders();
  },

  _getHeaderJSON: function() {
    var json = this.getHeader('X-JSON');
    if (!json) return null;
    json = decodeURIComponent(escape(json));
    try {
      return json.evalJSON(this.request.options.sanitizeJSON ||
        !this.request.isSameOrigin());
    } catch (e) {
      this.request.dispatchException(e);
    }
  },

  _getResponseJSON: function() {
    var options = this.request.options;
    if (!options.evalJSON || (options.evalJSON != 'force' &&
      !(this.getHeader('Content-type') || '').include('application/json')) ||
        this.responseText.blank())
          return null;
    try {
      return this.responseText.evalJSON(options.sanitizeJSON ||
        !this.request.isSameOrigin());
    } catch (e) {
      this.request.dispatchException(e);
    }
  }
});

Ajax.Updater = Class.create(Ajax.Request, {
  initialize: function($super, container, url, options) {
    this.container = {
      success: (container.success || container),
      failure: (container.failure || (container.success ? null : container))
    };

    options = Object.clone(options);
    var onComplete = options.onComplete;
    options.onComplete = (function(response, json) {
      this.updateContent(response.responseText);
      if (Object.isFunction(onComplete)) onComplete(response, json);
    }).bind(this);

    $super(url, options);
  },

  updateContent: function(responseText) {
    var receiver = this.container[this.success() ? 'success' : 'failure'],
        options = this.options;

    if (!options.evalScripts) responseText = responseText.stripScripts();

    if (receiver = $(receiver)) {
      if (options.insertion) {
        if (Object.isString(options.insertion)) {
          var insertion = { };nsertion[options.insertion] = responseText;
          receiver.insert(insertion);
        }
        else options.insertion(receiver, responseText);
      }
      else receiver.update(responseText);
    }
  }
});

Ajax.PeriodicalUpdater = Class.create(Ajax.Base, {
  initialize: function($super, container, url, options) {
    $super(options);
    this.onComplete = this.options.onComplete;

    this.frequency = (this.options.frequency || 2);
    this.decay = (this.options.decay || 1);

    this.updater = { };
    this.container = container;
    this.url = url;

    this.start();
  },

  start: function() {
    this.options.onComplete = this.updateComplete.bind(this);
    this.onTimerEvent();
  },

  stop: function() {
    this.updater.options.onComplete = undefined;
    clearTimeout(this.timer);
    (this.onComplete || Prototype.emptyFunction).apply(this, arguments);
  },

  updateComplete: function(response) {
    if (this.options.decay) {
      this.decay = (response.responseText == this.lastText ?
        this.decay * this.options.decay : 1);

      this.lastText = response.responseText;
    }
    this.timer = this.onTimerEvent.bind(this).delay(this.decay * this.frequency);
  },

  onTimerEvent: function() {
    this.updater = new Ajax.Updater(this.container, this.url, this.options);
  }
});



function $(element) {
  if (arguments.length > 1) {
    for (var i = 0, elements = [], length = arguments.length; i < length; i++)
      elements.push($(arguments[i]));
    return elements;
  }
  if (Object.isString(element))
    element = document.getElementById(element);
  return Element.extend(element);
}

if (Prototype.BrowserFeatures.XPath) {
  document._getElementsByXPath = function(expression, parentElement) {
    var results = [];
    var query = document.evaluate(expression, $(parentElement) || document,
      null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
    for (var i = 0, length = query.snapshotLength; i < length; i++)
      results.push(Element.extend(query.snapshotItem(i)));
    return results;
  };
}

/*--------------------------------------------------------------------------*/

if (!window.Node) var Node = { };

if (!Node.ELEMENT_NODE) {
  Object.extend(Node, {
    ELEMENT_NODE: 1,
    ATTRIBUTE_NODE: 2,
    TEXT_NODE: 3,
    CDATA_SECTION_NODE: 4,
    ENTITY_REFERENCE_NODE: 5,
    ENTITY_NODE: 6,
    PROCESSING_INSTRUCTION_NODE: 7,
    COMMENT_NODE: 8,
    DOCUMENT_NODE: 9,
    DOCUMENT_TYPE_NODE: 10,
    DOCUMENT_FRAGMENT_NODE: 11,
    NOTATION_NODE: 12
  });
}


(function(global) {

  var SETATTRIBUTE_IGNORES_NAME = (function(){
    var elForm = document.createElement("form");
    var elInput = document.createElement("input");
    var root = document.documentElement;
    elInput.setAttribute("name", "test");
    elForm.appendChild(elInput);
    root.appendChild(elForm);
    var isBuggy = elForm.elements
      ? (typeof elForm.elements.test == "undefined")
      : null;
    root.removeChild(elForm);
    elForm = elInput = null;
    return isBuggy;
  })();

  var element = global.Element;
  global.Element = function(tagName, attributes) {
    attributes = attributes || { };
    tagName = tagName.toLowerCase();
    var cache = Element.cache;
    if (SETATTRIBUTE_IGNORES_NAME && attributes.name) {
      tagName = '<' + tagName + ' name="' + attributes.name + '">';
      delete attributes.name;
      return Element.writeAttribute(document.createElement(tagName), attributes);
    }
    if (!cache[tagName]) cache[tagName] = Element.extend(document.createElement(tagName));
    return Element.writeAttribute(cache[tagName].cloneNode(false), attributes);
  };
  Object.extend(global.Element, element || { });
  if (element) global.Element.prototype = element.prototype;
})(this);

Element.cache = { };
Element.idCounter = 1;

Element.Methods = {
  visible: function(element) {
    return $(element).style.display != 'none';
  },

  toggle: function(element) {
    element = $(element);
    Element[Element.visible(element) ? 'hide' : 'show'](element);
    return element;
  },


  hide: function(element) {
    element = $(element);
    element.style.display = 'none';
    return element;
  },

  show: function(element) {
    element = $(element);
    element.style.display = '';
    return element;
  },

  remove: function(element) {
    element = $(element);
    element.parentNode.removeChild(element);
    return element;
  },

  update: (function(){

    var SELECT_ELEMENT_INNERHTML_BUGGY = (function(){
      var el = document.createElement("select"),
          isBuggy = true;
      el.innerHTML = "<option value=\"test\">test</option>";
      if (el.options && el.options[0]) {
        isBuggy = el.options[0].nodeName.toUpperCase() !== "OPTION";
      }
      el = null;
      return isBuggy;
    })();

    var TABLE_ELEMENT_INNERHTML_BUGGY = (function(){
      try {
        var el = document.createElement("table");
        if (el && el.tBodies) {
          el.innerHTML = "<tbody><tr><td>test</td></tr></tbody>";
          var isBuggy = typeof el.tBodies[0] == "undefined";
          el = null;
          return isBuggy;
        }
      } catch (e) {
        return true;
      }
    })();

    var SCRIPT_ELEMENT_REJECTS_TEXTNODE_APPENDING = (function () {
      var s = document.createElement("script"),
          isBuggy = false;
      try {
        s.appendChild(document.createTextNode(""));
        isBuggy = !s.firstChild ||
          s.firstChild && s.firstChild.nodeType !== 3;
      } catch (e) {
        isBuggy = true;
      }
      s = null;
      return isBuggy;
    })();

    function update(element, content) {
      element = $(element);

      if (content && content.toElement)
        content = content.toElement();

      if (Object.isElement(content))
        return element.update().insert(content);

      content = Object.toHTML(content);

      var tagName = element.tagName.toUpperCase();

      if (tagName === 'SCRIPT' && SCRIPT_ELEMENT_REJECTS_TEXTNODE_APPENDING) {
        element.text = content;
        return element;
      }

      if (SELECT_ELEMENT_INNERHTML_BUGGY || TABLE_ELEMENT_INNERHTML_BUGGY) {
        if (tagName in Element._insertionTranslations.tags) {
          while (element.firstChild) {
            element.removeChild(element.firstChild);
          }
          Element._getContentFromAnonymousElement(tagName, content.stripScripts())
            .each(function(node) {
              element.appendChild(node)
            });
        }
        else {
          element.innerHTML = content.stripScripts();
        }
      }
      else {
        element.innerHTML = content.stripScripts();
      }

      content.evalScripts.bind(content).defer();
      return element;
    }

    return update;
  })(),

  replace: function(element, content) {
    element = $(element);
    if (content && content.toElement) content = content.toElement();
    else if (!Object.isElement(content)) {
      content = Object.toHTML(content);
      var range = element.ownerDocument.createRange();
      range.selectNode(element);
      content.evalScripts.bind(content).defer();
      content = range.createContextualFragment(content.stripScripts());
    }
    element.parentNode.replaceChild(content, element);
    return element;
  },

  insert: function(element, insertions) {
    element = $(element);

    if (Object.isString(insertions) || Object.isNumber(insertions) ||
        Object.isElement(insertions) || (insertions && (insertions.toElement || insertions.toHTML)))
          insertions = {bottom:insertions};

    var content, insert, tagName, childNodes;

    for (var position in insertions) {
      content  = insertions[position];
      position = position.toLowerCase();
      insert = Element._insertionTranslations[position];

      if (content && content.toElement) content = content.toElement();
      if (Object.isElement(content)) {
        insert(element, content);
        continue;
      }

      content = Object.toHTML(content);

      tagName = ((position == 'before' || position == 'after')
        ? element.parentNode : element).tagName.toUpperCase();

      childNodes = Element._getContentFromAnonymousElement(tagName, content.stripScripts());

      if (position == 'top' || position == 'after') childNodes.reverse();
      childNodes.each(insert.curry(element));

      content.evalScripts.bind(content).defer();
    }

    return element;
  },

  wrap: function(element, wrapper, attributes) {
    element = $(element);
    if (Object.isElement(wrapper))
      $(wrapper).writeAttribute(attributes || { });
    else if (Object.isString(wrapper)) wrapper = new Element(wrapper, attributes);
    else wrapper = new Element('div', wrapper);
    if (element.parentNode)
      element.parentNode.replaceChild(wrapper, element);
    wrapper.appendChild(element);
    return wrapper;
  },

  inspect: function(element) {
    element = $(element);
    var result = '<' + element.tagName.toLowerCase();
    $H({'id': 'id', 'className': 'class'}).each(function(pair) {
      var property = pair.first(), attribute = pair.last();
      var value = (element[property] || '').toString();
      if (value) result += ' ' + attribute + '=' + value.inspect(true);
    });
    return result + '>';
  },

  recursivelyCollect: function(element, property) {
    element = $(element);
    var elements = [];
    while (element = element[property])
      if (element.nodeType == 1)
        elements.push(Element.extend(element));
    return elements;
  },

  ancestors: function(element) {
    return Element.recursivelyCollect(element, 'parentNode');
  },

  descendants: function(element) {
    return Element.select(element, "*");
  },

  firstDescendant: function(element) {
    element = $(element).firstChild;
    while (element && element.nodeType != 1) element = element.nextSibling;
    return $(element);
  },

  immediateDescendants: function(element) {
    if (!(element = $(element).firstChild)) return [];
    while (element && element.nodeType != 1) element = element.nextSibling;
    if (element) return [element].concat($(element).nextSiblings());
    return [];
  },

  previousSiblings: function(element) {
    return Element.recursivelyCollect(element, 'previousSibling');
  },

  nextSiblings: function(element) {
    return Element.recursivelyCollect(element, 'nextSibling');
  },

  siblings: function(element) {
    element = $(element);
    return Element.previousSiblings(element).reverse()
      .concat(Element.nextSiblings(element));
  },

  match: function(element, selector) {
    if (Object.isString(selector))
      selector = new Selector(selector);
    return selector.match($(element));
  },

  up: function(element, expression, index) {
    element = $(element);
    if (arguments.length == 1) return $(element.parentNode);
    var ancestors = Element.ancestors(element);
    return Object.isNumber(expression) ? ancestors[expression] :
      Selector.findElement(ancestors, expression, index);
  },

  down: function(element, expression, index) {
    element = $(element);
    if (arguments.length == 1) return Element.firstDescendant(element);
    return Object.isNumber(expression) ? Element.descendants(element)[expression] :
      Element.select(element, expression)[index || 0];
  },

  previous: function(element, expression, index) {
    element = $(element);
    if (arguments.length == 1) return $(Selector.handlers.previousElementSibling(element));
    var previousSiblings = Element.previousSiblings(element);
    return Object.isNumber(expression) ? previousSiblings[expression] :
      Selector.findElement(previousSiblings, expression, index);
  },

  next: function(element, expression, index) {
    element = $(element);
    if (arguments.length == 1) return $(Selector.handlers.nextElementSibling(element));
    var nextSiblings = Element.nextSiblings(element);
    return Object.isNumber(expression) ? nextSiblings[expression] :
      Selector.findElement(nextSiblings, expression, index);
  },


  select: function(element) {
    var args = Array.prototype.slice.call(arguments, 1);
    return Selector.findChildElements(element, args);
  },

  adjacent: function(element) {
    var args = Array.prototype.slice.call(arguments, 1);
    return Selector.findChildElements(element.parentNode, args).without(element);
  },

  identify: function(element) {
    element = $(element);
    var id = Element.readAttribute(element, 'id');
    if (id) return id;
    do {id = 'anonymous_element_' + Element.idCounter++} while ($(id));
    Element.writeAttribute(element, 'id', id);
    return id;
  },

  readAttribute: function(element, name) {
    element = $(element);
    if (Prototype.Browser.IE) {
      var t = Element._attributeTranslations.read;
      if (t.values[name]) return t.values[name](element, name);
      if (t.names[name]) name = t.names[name];
      if (name.include(':')) {
        return (!element.attributes || !element.attributes[name]) ? null :
         element.attributes[name].value;
      }
    }
    return element.getAttribute(name);
  },

  writeAttribute: function(element, name, value) {
    element = $(element);
    var attributes = { }, t = Element._attributeTranslations.write;

    if (typeof name == 'object') attributes = name;
    else attributes[name] = Object.isUndefined(value) ? true : value;

    for (var attr in attributes) {
      name = t.names[attr] || attr;
      value = attributes[attr];
      if (t.values[attr]) name = t.values[attr](element, value);
      if (value === false || value === null)
        element.removeAttribute(name);
      else if (value === true)
        element.setAttribute(name, name);
      else element.setAttribute(name, value);
    }
    return element;
  },

  getHeight: function(element) {
    return Element.getDimensions(element).height;
  },

  getWidth: function(element) {
    return Element.getDimensions(element).width;
  },

  classNames: function(element) {
    return new Element.ClassNames(element);
  },

  hasClassName: function(element, className) {
    if (!(element = $(element))) return;
    var elementClassName = element.className;
    return (elementClassName.length > 0 && (elementClassName == className ||
      new RegExp("(^|\\s)" + className + "(\\s|$)").test(elementClassName)));
  },

  addClassName: function(element, className) {
    if (!(element = $(element))) return;
    if (!Element.hasClassName(element, className))
      element.className += (element.className ? ' ' : '') + className;
    return element;
  },

  removeClassName: function(element, className) {
    if (!(element = $(element))) return;
    element.className = element.className.replace(
      new RegExp("(^|\\s+)" + className + "(\\s+|$)"), ' ').strip();
    return element;
  },

  toggleClassName: function(element, className) {
    if (!(element = $(element))) return;
    return Element[Element.hasClassName(element, className) ?
      'removeClassName' : 'addClassName'](element, className);
  },

  cleanWhitespace: function(element) {
    element = $(element);
    var node = element.firstChild;
    while (node) {
      var nextNode = node.nextSibling;
      if (node.nodeType == 3 && !/\S/.test(node.nodeValue))
        element.removeChild(node);
      node = nextNode;
    }
    return element;
  },

  empty: function(element) {
    return $(element).innerHTML.blank();
  },

  descendantOf: function(element, ancestor) {
    element = $(element), ancestor = $(ancestor);

    if (element.compareDocumentPosition)
      return (element.compareDocumentPosition(ancestor) & 8) === 8;

    if (ancestor.contains)
      return ancestor.contains(element) && ancestor !== element;

    while (element = element.parentNode)
      if (element == ancestor) return true;

    return false;
  },

  scrollTo: function(element) {
    element = $(element);
    var pos = Element.cumulativeOffset(element);
    window.scrollTo(pos[0], pos[1]);
    return element;
  },

  getStyle: function(element, style) {
    element = $(element);
    style = style == 'float' ? 'cssFloat' : style.camelize();
    var value = element.style[style];
    if (!value || value == 'auto') {
      var css = document.defaultView.getComputedStyle(element, null);
      value = css ? css[style] : null;
    }
    if (style == 'opacity') return value ? parseFloat(value) : 1.0;
    return value == 'auto' ? null : value;
  },

  getOpacity: function(element) {
    return $(element).getStyle('opacity');
  },

  setStyle: function(element, styles) {
    element = $(element);
    var elementStyle = element.style, match;
    if (Object.isString(styles)) {
      element.style.cssText += ';' + styles;
      return styles.include('opacity') ?
        element.setOpacity(styles.match(/opacity:\s*(\d?\.?\d*)/)[1]) : element;
    }
    for (var property in styles)
      if (property == 'opacity') element.setOpacity(styles[property]);
      else
        elementStyle[(property == 'float' || property == 'cssFloat') ?
          (Object.isUndefined(elementStyle.styleFloat) ? 'cssFloat' : 'styleFloat') :
            property] = styles[property];

    return element;
  },

  setOpacity: function(element, value) {
    element = $(element);
    element.style.opacity = (value == 1 || value === '') ? '' :
      (value < 0.00001) ? 0 : value;
    return element;
  },

  getDimensions: function(element) {
    element = $(element);
    var display = Element.getStyle(element, 'display');
    if (display != 'none' && display != null) // Safari bug
      return {width: element.offsetWidth, height: element.offsetHeight};

    var els = element.style;
    var originalVisibility = els.visibility;
    var originalPosition = els.position;
    var originalDisplay = els.display;
    els.visibility = 'hidden';
    if (originalPosition != 'fixed') // Switching fixed to absolute causes issues in Safari
      els.position = 'absolute';
    els.display = 'block';
    var originalWidth = element.clientWidth;
    var originalHeight = element.clientHeight;
    els.display = originalDisplay;
    els.position = originalPosition;
    els.visibility = originalVisibility;
    return {width: originalWidth, height: originalHeight};
  },

  makePositioned: function(element) {
    element = $(element);
    var pos = Element.getStyle(element, 'position');
    if (pos == 'static' || !pos) {
      element._madePositioned = true;
      element.style.position = 'relative';
      if (Prototype.Browser.Opera) {
        element.style.top = 0;
        element.style.left = 0;
      }
    }
    return element;
  },

  undoPositioned: function(element) {
    element = $(element);
    if (element._madePositioned) {
      element._madePositioned = undefined;
      element.style.position =
        element.style.top =
        element.style.left =
        element.style.bottom =
        element.style.right = '';
    }
    return element;
  },

  makeClipping: function(element) {
    element = $(element);
    if (element._overflow) return element;
    element._overflow = Element.getStyle(element, 'overflow') || 'auto';
    if (element._overflow !== 'hidden')
      element.style.overflow = 'hidden';
    return element;
  },

  undoClipping: function(element) {
    element = $(element);
    if (!element._overflow) return element;
    element.style.overflow = element._overflow == 'auto' ? '' : element._overflow;
    element._overflow = null;
    return element;
  },

  cumulativeOffset: function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
      element = element.offsetParent;
    } while (element);
    return Element._returnOffset(valueL, valueT);
  },

  positionedOffset: function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
      element = element.offsetParent;
      if (element) {
        if (element.tagName.toUpperCase() == 'BODY') break;
        var p = Element.getStyle(element, 'position');
        if (p !== 'static') break;
      }
    } while (element);
    return Element._returnOffset(valueL, valueT);
  },

  absolutize: function(element) {
    element = $(element);
    if (Element.getStyle(element, 'position') == 'absolute') return element;

    var offsets = Element.positionedOffset(element);
    var top     = offsets[1];
    var left    = offsets[0];
    var width   = element.clientWidth;
    var height  = element.clientHeight;

    element._originalLeft   = left - parseFloat(element.style.left  || 0);
    element._originalTop    = top  - parseFloat(element.style.top || 0);
    element._originalWidth  = element.style.width;
    element._originalHeight = element.style.height;

    element.style.position = 'absolute';
    element.style.top    = top + 'px';
    element.style.left   = left + 'px';
    element.style.width  = width + 'px';
    element.style.height = height + 'px';
    return element;
  },

  relativize: function(element) {
    element = $(element);
    if (Element.getStyle(element, 'position') == 'relative') return element;

    element.style.position = 'relative';
    var top  = parseFloat(element.style.top  || 0) - (element._originalTop || 0);
    var left = parseFloat(element.style.left || 0) - (element._originalLeft || 0);

    element.style.top    = top + 'px';
    element.style.left   = left + 'px';
    element.style.height = element._originalHeight;
    element.style.width  = element._originalWidth;
    return element;
  },

  cumulativeScrollOffset: function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.scrollTop  || 0;
      valueL += element.scrollLeft || 0;
      element = element.parentNode;
    } while (element);
    return Element._returnOffset(valueL, valueT);
  },

  getOffsetParent: function(element) {
    if (element.offsetParent) return $(element.offsetParent);
    if (element == document.body) return $(element);

    while ((element = element.parentNode) && element != document.body)
      if (Element.getStyle(element, 'position') != 'static')
        return $(element);

    return $(document.body);
  },

  viewportOffset: function(forElement) {
    var valueT = 0, valueL = 0;

    var element = forElement;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;

      if (element.offsetParent == document.body &&
        Element.getStyle(element, 'position') == 'absolute') break;

    } while (element = element.offsetParent);

    element = forElement;
    do {
      if (!Prototype.Browser.Opera || (element.tagName && (element.tagName.toUpperCase() == 'BODY'))) {
        valueT -= element.scrollTop  || 0;
        valueL -= element.scrollLeft || 0;
      }
    } while (element = element.parentNode);

    return Element._returnOffset(valueL, valueT);
  },

  clonePosition: function(element, source) {
    var options = Object.extend({
      setLeft:    true,
      setTop:     true,
      setWidth:   true,
      setHeight:  true,
      offsetTop:  0,
      offsetLeft: 0
    }, arguments[2] || { });

    source = $(source);
    var p = Element.viewportOffset(source);

    element = $(element);
    var delta = [0, 0];
    var parent = null;
    if (Element.getStyle(element, 'position') == 'absolute') {
      parent = Element.getOffsetParent(element);
      delta = Element.viewportOffset(parent);
    }

    if (parent == document.body) {
      delta[0] -= document.body.offsetLeft;
      delta[1] -= document.body.offsetTop;
    }

    if (options.setLeft)   element.style.left  = (p[0] - delta[0] + options.offsetLeft) + 'px';
    if (options.setTop)    element.style.top   = (p[1] - delta[1] + options.offsetTop) + 'px';
    if (options.setWidth)  element.style.width = source.offsetWidth + 'px';
    if (options.setHeight) element.style.height = source.offsetHeight + 'px';
    return element;
  }
};

Object.extend(Element.Methods, {
  getElementsBySelector: Element.Methods.select,

  childElements: Element.Methods.immediateDescendants
});

Element._attributeTranslations = {
  write: {
    names: {
      className: 'class',
      htmlFor:   'for'
    },
    values: { }
  }
};

if (Prototype.Browser.Opera) {
  Element.Methods.getStyle = Element.Methods.getStyle.wrap(
    function(proceed, element, style) {
      switch (style) {
        case 'left': case 'top': case 'right': case 'bottom':
          if (proceed(element, 'position') === 'static') return null;
        case 'height': case 'width':
          if (!Element.visible(element)) return null;

          var dim = parseInt(proceed(element, style), 10);

          if (dim !== element['offset' + style.capitalize()])
            return dim + 'px';

          var properties;
          if (style === 'height') {
            properties = ['border-top-width', 'padding-top',
             'padding-bottom', 'border-bottom-width'];
          }
          else {
            properties = ['border-left-width', 'padding-left',
             'padding-right', 'border-right-width'];
          }
          return properties.inject(dim, function(memo, property) {
            var val = proceed(element, property);
            return val === null ? memo : memo - parseInt(val, 10);
          }) + 'px';
        default:return proceed(element, style);
      }
    }
  );

  Element.Methods.readAttribute = Element.Methods.readAttribute.wrap(
    function(proceed, element, attribute) {
      if (attribute === 'title') return element.title;
      return proceed(element, attribute);
    }
  );
}

else if (Prototype.Browser.IE) {
  Element.Methods.getOffsetParent = Element.Methods.getOffsetParent.wrap(
    function(proceed, element) {
      element = $(element);
      try {element.offsetParent}
      catch(e) {return $(document.body)}
      var position = element.getStyle('position');
      if (position !== 'static') return proceed(element);
      element.setStyle({position: 'relative'});
      var value = proceed(element);
      element.setStyle({position: position});
      return value;
    }
  );

  $w('positionedOffset viewportOffset').each(function(method) {
    Element.Methods[method] = Element.Methods[method].wrap(
      function(proceed, element) {
        element = $(element);
        try {element.offsetParent}
        catch(e) {return Element._returnOffset(0,0)}
        var position = element.getStyle('position');
        if (position !== 'static') return proceed(element);
        var offsetParent = element.getOffsetParent();
        if (offsetParent && offsetParent.getStyle('position') === 'fixed')
          offsetParent.setStyle({zoom: 1});
        element.setStyle({position: 'relative'});
        var value = proceed(element);
        element.setStyle({position: position});
        return value;
      }
    );
  });

  Element.Methods.cumulativeOffset = Element.Methods.cumulativeOffset.wrap(
    function(proceed, element) {
      try {element.offsetParent}
      catch(e) {return Element._returnOffset(0,0)}
      return proceed(element);
    }
  );

  Element.Methods.getStyle = function(element, style) {
    element = $(element);
    style = (style == 'float' || style == 'cssFloat') ? 'styleFloat' : style.camelize();
    var value = element.style[style];
    if (!value && element.currentStyle) value = element.currentStyle[style];

    if (style == 'opacity') {
      if (value = (element.getStyle('filter') || '').match(/alpha\(opacity=(.*)\)/))
        if (value[1]) return parseFloat(value[1]) / 100;
      return 1.0;
    }

    if (value == 'auto') {
      if ((style == 'width' || style == 'height') && (element.getStyle('display') != 'none'))
        return element['offset' + style.capitalize()] + 'px';
      return null;
    }
    return value;
  };

  Element.Methods.setOpacity = function(element, value) {
    function stripAlpha(filter){
      return filter.replace(/alpha\([^\)]*\)/gi,'');
    }
    element = $(element);
    var currentStyle = element.currentStyle;
    if ((currentStyle && !currentStyle.hasLayout) ||
      (!currentStyle && element.style.zoom == 'normal'))
        element.style.zoom = 1;

    var filter = element.getStyle('filter'), style = element.style;
    if (value == 1 || value === '') {
      (filter = stripAlpha(filter)) ?
        style.filter = filter : style.removeAttribute('filter');
      return element;
    } else if (value < 0.00001) value = 0;
    style.filter = stripAlpha(filter) +
      'alpha(opacity=' + (value * 100) + ')';
    return element;
  };

  Element._attributeTranslations = (function(){

    var classProp = 'className';
    var forProp = 'for';

    var el = document.createElement('div');

    el.setAttribute(classProp, 'x');

    if (el.className !== 'x') {
      el.setAttribute('class', 'x');
      if (el.className === 'x') {
        classProp = 'class';
      }
    }
    el = null;

    el = document.createElement('label');
    el.setAttribute(forProp, 'x');
    if (el.htmlFor !== 'x') {
      el.setAttribute('htmlFor', 'x');
      if (el.htmlFor === 'x') {
        forProp = 'htmlFor';
      }
    }
    el = null;

    return {
      read: {
        names: {
          'class':      classProp,
          'className':  classProp,
          'for':        forProp,
          'htmlFor':    forProp
        },
        values: {
          _getAttr: function(element, attribute) {
            return element.getAttribute(attribute);
          },
          _getAttr2: function(element, attribute) {
            return element.getAttribute(attribute, 2);
          },
          _getAttrNode: function(element, attribute) {
            var node = element.getAttributeNode(attribute);
            return node ? node.value : "";
          },
          _getEv: (function(){

            var el = document.createElement('div');
            el.onclick = Prototype.emptyFunction;
            var value = el.getAttribute('onclick');
            var f;

            if (String(value).indexOf('{') > -1) {
              f = function(element, attribute) {
                attribute = element.getAttribute(attribute);
                if (!attribute) return null;
                attribute = attribute.toString();
                attribute = attribute.split('{')[1];
                attribute = attribute.split('}')[0];
                return attribute.strip();
              };
            }
            else if (value === '') {
              f = function(element, attribute) {
                attribute = element.getAttribute(attribute);
                if (!attribute) return null;
                return attribute.strip();
              };
            }
            el = null;
            return f;
          })(),
          _flag: function(element, attribute) {
            return $(element).hasAttribute(attribute) ? attribute : null;
          },
          style: function(element) {
            return element.style.cssText.toLowerCase();
          },
          title: function(element) {
            return element.title;
          }
        }
      }
    }
  })();

  Element._attributeTranslations.write = {
    names: Object.extend({
      cellpadding: 'cellPadding',
      cellspacing: 'cellSpacing'
    }, Element._attributeTranslations.read.names),
    values: {
      checked: function(element, value) {
        element.checked = !!value;
      },

      style: function(element, value) {
        element.style.cssText = value ? value : '';
      }
    }
  };

  Element._attributeTranslations.has = {};

  $w('colSpan rowSpan vAlign dateTime accessKey tabIndex ' +
      'encType maxLength readOnly longDesc frameBorder').each(function(attr) {
    Element._attributeTranslations.write.names[attr.toLowerCase()] = attr;
    Element._attributeTranslations.has[attr.toLowerCase()] = attr;
  });

  (function(v) {
    Object.extend(v, {
      href:        v._getAttr2,
      src:         v._getAttr2,
      type:        v._getAttr,
      action:      v._getAttrNode,
      disabled:    v._flag,
      checked:     v._flag,
      readonly:    v._flag,
      multiple:    v._flag,
      onload:      v._getEv,
      onunload:    v._getEv,
      onclick:     v._getEv,
      ondblclick:  v._getEv,
      onmousedown: v._getEv,
      onmouseup:   v._getEv,
      onmouseover: v._getEv,
      onmousemove: v._getEv,
      onmouseout:  v._getEv,
      onfocus:     v._getEv,
      onblur:      v._getEv,
      onkeypress:  v._getEv,
      onkeydown:   v._getEv,
      onkeyup:     v._getEv,
      onsubmit:    v._getEv,
      onreset:     v._getEv,
      onselect:    v._getEv,
      onchange:    v._getEv
    });
  })(Element._attributeTranslations.read.values);

  if (Prototype.BrowserFeatures.ElementExtensions) {
    (function() {
      function _descendants(element) {
        var nodes = element.getElementsByTagName('*'), results = [];
        for (var i = 0, node; node = nodes[i]; i++)
          if (node.tagName !== "!") // Filter out comment nodes.
            results.push(node);
        return results;
      }

      Element.Methods.down = function(element, expression, index) {
        element = $(element);
        if (arguments.length == 1) return element.firstDescendant();
        return Object.isNumber(expression) ? _descendants(element)[expression] :
          Element.select(element, expression)[index || 0];
      }
    })();
  }

}

else if (Prototype.Browser.Gecko && /rv:1\.8\.0/.test(navigator.userAgent)) {
  Element.Methods.setOpacity = function(element, value) {
    element = $(element);
    element.style.opacity = (value == 1) ? 0.999999 :
      (value === '') ? '' : (value < 0.00001) ? 0 : value;
    return element;
  };
}

else if (Prototype.Browser.WebKit) {
  Element.Methods.setOpacity = function(element, value) {
    element = $(element);
    element.style.opacity = (value == 1 || value === '') ? '' :
      (value < 0.00001) ? 0 : value;

    if (value == 1)
      if(element.tagName.toUpperCase() == 'IMG' && element.width) {
        element.width++;element.width--;
      } else try {
        var n = document.createTextNode(' ');
        element.appendChild(n);
        element.removeChild(n);
      } catch (e) { }

    return element;
  };

  Element.Methods.cumulativeOffset = function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
      if (element.offsetParent == document.body)
        if (Element.getStyle(element, 'position') == 'absolute') break;

      element = element.offsetParent;
    } while (element);

    return Element._returnOffset(valueL, valueT);
  };
}

if ('outerHTML' in document.documentElement) {
  Element.Methods.replace = function(element, content) {
    element = $(element);

    if (content && content.toElement) content = content.toElement();
    if (Object.isElement(content)) {
      element.parentNode.replaceChild(content, element);
      return element;
    }

    content = Object.toHTML(content);
    var parent = element.parentNode, tagName = parent.tagName.toUpperCase();

    if (Element._insertionTranslations.tags[tagName]) {
      var nextSibling = element.next();
      var fragments = Element._getContentFromAnonymousElement(tagName, content.stripScripts());
      parent.removeChild(element);
      if (nextSibling)
        fragments.each(function(node) {parent.insertBefore(node, nextSibling)});
      else
        fragments.each(function(node) {parent.appendChild(node)});
    }
    else element.outerHTML = content.stripScripts();

    content.evalScripts.bind(content).defer();
    return element;
  };
}

Element._returnOffset = function(l, t) {
  var result = [l, t];
  result.left = l;
  result.top = t;
  return result;
};

Element._getContentFromAnonymousElement = function(tagName, html) {
  var div = new Element('div'), t = Element._insertionTranslations.tags[tagName];
  if (t) {
    div.innerHTML = t[0] + html + t[1];
    t[2].times(function() {div = div.firstChild});
  } else div.innerHTML = html;
  return $A(div.childNodes);
};

Element._insertionTranslations = {
  before: function(element, node) {
    element.parentNode.insertBefore(node, element);
  },
  top: function(element, node) {
    element.insertBefore(node, element.firstChild);
  },
  bottom: function(element, node) {
    element.appendChild(node);
  },
  after: function(element, node) {
    element.parentNode.insertBefore(node, element.nextSibling);
  },
  tags: {
    TABLE:  ['<table>',                '</table>',                   1],
    TBODY:  ['<table><tbody>',         '</tbody></table>',           2],
    TR:     ['<table><tbody><tr>',     '</tr></tbody></table>',      3],
    TD:     ['<table><tbody><tr><td>', '</td></tr></tbody></table>', 4],
    SELECT: ['<select>',               '</select>',                  1]
  }
};

(function() {
  var tags = Element._insertionTranslations.tags;
  Object.extend(tags, {
    THEAD: tags.TBODY,
    TFOOT: tags.TBODY,
    TH:    tags.TD
  });
})();

Element.Methods.Simulated = {
  hasAttribute: function(element, attribute) {
    attribute = Element._attributeTranslations.has[attribute] || attribute;
    var node = $(element).getAttributeNode(attribute);
    return !!(node && node.specified);
  }
};

Element.Methods.ByTag = { };

Object.extend(Element, Element.Methods);

(function(div) {

  if (!Prototype.BrowserFeatures.ElementExtensions && div['__proto__']) {
    window.HTMLElement = { };
    window.HTMLElement.prototype = div['__proto__'];
    Prototype.BrowserFeatures.ElementExtensions = true;
  }

  div = null;

})(document.createElement('div'))

Element.extend = (function() {

  function checkDeficiency(tagName) {
    if (typeof window.Element != 'undefined') {
      var proto = window.Element.prototype;
      if (proto) {
        var id = '_' + (Math.random()+'').slice(2);
        var el = document.createElement(tagName);
        proto[id] = 'x';
        var isBuggy = (el[id] !== 'x');
        delete proto[id];
        el = null;
        return isBuggy;
      }
    }
    return false;
  }

  function extendElementWith(element, methods) {
    for (var property in methods) {
      var value = methods[property];
      if (Object.isFunction(value) && !(property in element))
        element[property] = value.methodize();
    }
  }

  var HTMLOBJECTELEMENT_PROTOTYPE_BUGGY = checkDeficiency('object');

  if (Prototype.BrowserFeatures.SpecificElementExtensions) {
    if (HTMLOBJECTELEMENT_PROTOTYPE_BUGGY) {
      return function(element) {
        if (element && typeof element._extendedByPrototype == 'undefined') {
          var t = element.tagName;
          if (t && (/^(?:object|applet|embed)$/i.test(t))) {
            extendElementWith(element, Element.Methods);
            extendElementWith(element, Element.Methods.Simulated);
            extendElementWith(element, Element.Methods.ByTag[t.toUpperCase()]);
          }
        }
        return element;
      }
    }
    return Prototype.K;
  }

  var Methods = { }, ByTag = Element.Methods.ByTag;

  var extend = Object.extend(function(element) {
    if (!element || typeof element._extendedByPrototype != 'undefined' ||
        element.nodeType != 1 || element == window) return element;

    var methods = Object.clone(Methods),
        tagName = element.tagName.toUpperCase();

    if (ByTag[tagName]) Object.extend(methods, ByTag[tagName]);

    extendElementWith(element, methods);

    element._extendedByPrototype = Prototype.emptyFunction;
    return element;

  }, {
    refresh: function() {
      if (!Prototype.BrowserFeatures.ElementExtensions) {
        Object.extend(Methods, Element.Methods);
        Object.extend(Methods, Element.Methods.Simulated);
      }
    }
  });

  extend.refresh();
  return extend;
})();

Element.hasAttribute = function(element, attribute) {
  if (element.hasAttribute) return element.hasAttribute(attribute);
  return Element.Methods.Simulated.hasAttribute(element, attribute);
};

Element.addMethods = function(methods) {
  var F = Prototype.BrowserFeatures, T = Element.Methods.ByTag;

  if (!methods) {
    Object.extend(Form, Form.Methods);
    Object.extend(Form.Element, Form.Element.Methods);
    Object.extend(Element.Methods.ByTag, {
      "FORM":     Object.clone(Form.Methods),
      "INPUT":    Object.clone(Form.Element.Methods),
      "SELECT":   Object.clone(Form.Element.Methods),
      "TEXTAREA": Object.clone(Form.Element.Methods)
    });
  }

  if (arguments.length == 2) {
    var tagName = methods;
    methods = arguments[1];
  }

  if (!tagName) Object.extend(Element.Methods, methods || { });
  else {
    if (Object.isArray(tagName)) tagName.each(extend);
    else extend(tagName);
  }

  function extend(tagName) {
    tagName = tagName.toUpperCase();
    if (!Element.Methods.ByTag[tagName])
      Element.Methods.ByTag[tagName] = { };
    Object.extend(Element.Methods.ByTag[tagName], methods);
  }

  function copy(methods, destination, onlyIfAbsent) {
    onlyIfAbsent = onlyIfAbsent || false;
    for (var property in methods) {
      var value = methods[property];
      if (!Object.isFunction(value)) continue;
      if (!onlyIfAbsent || !(property in destination))
        destination[property] = value.methodize();
    }
  }

  function findDOMClass(tagName) {
    var klass;
    var trans = {
      "OPTGROUP": "OptGroup", "TEXTAREA": "TextArea", "P": "Paragraph",
      "FIELDSET": "FieldSet", "UL": "UList", "OL": "OList", "DL": "DList",
      "DIR": "Directory", "H1": "Heading", "H2": "Heading", "H3": "Heading",
      "H4": "Heading", "H5": "Heading", "H6": "Heading", "Q": "Quote",
      "INS": "Mod", "DEL": "Mod", "A": "Anchor", "IMG": "Image", "CAPTION":
      "TableCaption", "COL": "TableCol", "COLGROUP": "TableCol", "THEAD":
      "TableSection", "TFOOT": "TableSection", "TBODY": "TableSection", "TR":
      "TableRow", "TH": "TableCell", "TD": "TableCell", "FRAMESET":
      "FrameSet", "IFRAME": "IFrame"
    };
    if (trans[tagName]) klass = 'HTML' + trans[tagName] + 'Element';
    if (window[klass]) return window[klass];
    klass = 'HTML' + tagName + 'Element';
    if (window[klass]) return window[klass];
    klass = 'HTML' + tagName.capitalize() + 'Element';
    if (window[klass]) return window[klass];

    var element = document.createElement(tagName);
    var proto = element['__proto__'] || element.constructor.prototype;
    element = null;
    return proto;
  }

  var elementPrototype = window.HTMLElement ? HTMLElement.prototype :
   Element.prototype;

  if (F.ElementExtensions) {
    copy(Element.Methods, elementPrototype);
    copy(Element.Methods.Simulated, elementPrototype, true);
  }

  if (F.SpecificElementExtensions) {
    for (var tag in Element.Methods.ByTag) {
      var klass = findDOMClass(tag);
      if (Object.isUndefined(klass)) continue;
      copy(T[tag], klass.prototype);
    }
  }

  Object.extend(Element, Element.Methods);
  delete Element.ByTag;

  if (Element.extend.refresh) Element.extend.refresh();
  Element.cache = { };
};


document.viewport = {

  getDimensions: function() {
    return {width: this.getWidth(), height: this.getHeight()};
  },

  getScrollOffsets: function() {
    return Element._returnOffset(
      window.pageXOffset || document.documentElement.scrollLeft || document.body.scrollLeft,
      window.pageYOffset || document.documentElement.scrollTop  || document.body.scrollTop);
  }
};

(function(viewport) {
  var B = Prototype.Browser, doc = document, element, property = {};

  function getRootElement() {
    if (B.WebKit && !doc.evaluate)
      return document;

    if (B.Opera && window.parseFloat(window.opera.version()) < 9.5)
      return document.body;

    return document.documentElement;
  }

  function define(D) {
    if (!element) element = getRootElement();

    property[D] = 'client' + D;

    viewport['get' + D] = function() {return element[property[D]]};
    return viewport['get' + D]();
  }

  viewport.getWidth  = define.curry('Width');

  viewport.getHeight = define.curry('Height');
})(document.viewport);


Element.Storage = {
  UID: 1
};

Element.addMethods({
  getStorage: function(element) {
    if (!(element = $(element))) return;

    var uid;
    if (element === window) {
      uid = 0;
    } else {
      if (typeof element._prototypeUID === "undefined")
        element._prototypeUID = [Element.Storage.UID++];
      uid = element._prototypeUID[0];
    }

    if (!Element.Storage[uid])
      Element.Storage[uid] = $H();

    return Element.Storage[uid];
  },

  store: function(element, key, value) {
    if (!(element = $(element))) return;

    if (arguments.length === 2) {
      Element.getStorage(element).update(key);
    } else {
      Element.getStorage(element).set(key, value);
    }

    return element;
  },

  retrieve: function(element, key, defaultValue) {
    if (!(element = $(element))) return;
    var hash = Element.getStorage(element), value = hash.get(key);

    if (Object.isUndefined(value)) {
      hash.set(key, defaultValue);
      value = defaultValue;
    }

    return value;
  },

  clone: function(element, deep) {
    if (!(element = $(element))) return;
    var clone = element.cloneNode(deep);
    clone._prototypeUID = void 0;
    if (deep) {
      var descendants = Element.select(clone, '*'),
          i = descendants.length;
      while (i--) {
        descendants[i]._prototypeUID = void 0;
      }
    }
    return Element.extend(clone);
  }
});
/* Portions of the Selector class are derived from Jack Slocum's DomQuery,
 * part of YUI-Ext version 0.40, distributed under the terms of an MIT-style
 * license.  Please see http://www.yui-ext.com/ for more information. */

var Selector = Class.create({
  initialize: function(expression) {
    this.expression = expression.strip();

    if (this.shouldUseSelectorsAPI()) {
      this.mode = 'selectorsAPI';
    } else if (this.shouldUseXPath()) {
      this.mode = 'xpath';
      this.compileXPathMatcher();
    } else {
      this.mode = "normal";
      this.compileMatcher();
    }

  },

  shouldUseXPath: (function() {

    var IS_DESCENDANT_SELECTOR_BUGGY = (function(){
      var isBuggy = false;
      if (document.evaluate && window.XPathResult) {
        var el = document.createElement('div');
        el.innerHTML = '<ul><li></li></ul><div><ul><li></li></ul></div>';

        var xpath = ".//*[local-name()='ul' or local-name()='UL']" +
          "//*[local-name()='li' or local-name()='LI']";

        var result = document.evaluate(xpath, el, null,
          XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);

        isBuggy = (result.snapshotLength !== 2);
        el = null;
      }
      return isBuggy;
    })();

    return function() {
      if (!Prototype.BrowserFeatures.XPath) return false;

      var e = this.expression;

      if (Prototype.Browser.WebKit &&
       (e.include("-of-type") || e.include(":empty")))
        return false;

      if ((/(\[[\w-]*?:|:checked)/).test(e))
        return false;

      if (IS_DESCENDANT_SELECTOR_BUGGY) return false;

      return true;
    }

  })(),

  shouldUseSelectorsAPI: function() {
    if (!Prototype.BrowserFeatures.SelectorsAPI) return false;

    if (Selector.CASE_INSENSITIVE_CLASS_NAMES) return false;

    if (!Selector._div) Selector._div = new Element('div');

    try {
      Selector._div.querySelector(this.expression);
    } catch(e) {
      return false;
    }

    return true;
  },

  compileMatcher: function() {
    var e = this.expression, ps = Selector.patterns, h = Selector.handlers,
        c = Selector.criteria, le, p, m, len = ps.length, name;

    if (Selector._cache[e]) {
      this.matcher = Selector._cache[e];
      return;
    }

    this.matcher = ["this.matcher = function(root) {",
                    "var r = root, h = Selector.handlers, c = false, n;"];

    while (e && le != e && (/\S/).test(e)) {
      le = e;
      for (var i = 0; i<len; i++) {
        p = ps[i].re;
        name = ps[i].name;
        if (m = e.match(p)) {
          this.matcher.push(Object.isFunction(c[name]) ? c[name](m) :
            new Template(c[name]).evaluate(m));
          e = e.replace(m[0], '');
          break;
        }
      }
    }

    this.matcher.push("return h.unique(n);\n}");
    eval(this.matcher.join('\n'));
    Selector._cache[this.expression] = this.matcher;
  },

  compileXPathMatcher: function() {
    var e = this.expression, ps = Selector.patterns,
        x = Selector.xpath, le, m, len = ps.length, name;

    if (Selector._cache[e]) {
      this.xpath = Selector._cache[e];return;
    }

    this.matcher = ['.//*'];
    while (e && le != e && (/\S/).test(e)) {
      le = e;
      for (var i = 0; i<len; i++) {
        name = ps[i].name;
        if (m = e.match(ps[i].re)) {
          this.matcher.push(Object.isFunction(x[name]) ? x[name](m) :
            new Template(x[name]).evaluate(m));
          e = e.replace(m[0], '');
          break;
        }
      }
    }

    this.xpath = this.matcher.join('');
    Selector._cache[this.expression] = this.xpath;
  },

  findElements: function(root) {
    root = root || document;
    var e = this.expression, results;

    switch (this.mode) {
      case 'selectorsAPI':
        if (root !== document) {
          var oldId = root.id, id = $(root).identify();
          id = id.replace(/([\.:])/g, "\\$1");
          e = "#" + id + " " + e;
        }

        results = $A(root.querySelectorAll(e)).map(Element.extend);
        root.id = oldId;

        return results;
      case 'xpath':
        return document._getElementsByXPath(this.xpath, root);
      default:
       return this.matcher(root);
    }
  },

  match: function(element) {
    this.tokens = [];

    var e = this.expression, ps = Selector.patterns, as = Selector.assertions;
    var le, p, m, len = ps.length, name;

    while (e && le !== e && (/\S/).test(e)) {
      le = e;
      for (var i = 0; i<len; i++) {
        p = ps[i].re;
        name = ps[i].name;
        if (m = e.match(p)) {
          if (as[name]) {
            this.tokens.push([name, Object.clone(m)]);
            e = e.replace(m[0], '');
          } else {
            return this.findElements(document).include(element);
          }
        }
      }
    }

    var match = true, name, matches;
    for (var i = 0, token; token = this.tokens[i]; i++) {
      name = token[0], matches = token[1];
      if (!Selector.assertions[name](element, matches)) {
        match = false;break;
      }
    }

    return match;
  },

  toString: function() {
    return this.expression;
  },

  inspect: function() {
    return "#<Selector:" + this.expression.inspect() + ">";
  }
});

if (Prototype.BrowserFeatures.SelectorsAPI &&
 document.compatMode === 'BackCompat') {
  Selector.CASE_INSENSITIVE_CLASS_NAMES = (function(){
    var div = document.createElement('div'),
     span = document.createElement('span');

    div.id = "prototype_test_id";
    span.className = 'Test';
    div.appendChild(span);
    var isIgnored = (div.querySelector('#prototype_test_id .test') !== null);
    div = span = null;
    return isIgnored;
  })();
}

Object.extend(Selector, {
  _cache: { },

  xpath: {
    descendant:   "//*",
    child:        "/*",
    adjacent:     "/following-sibling::*[1]",
    laterSibling: '/following-sibling::*',
    tagName:      function(m) {
      if (m[1] == '*') return '';
      return "[local-name()='" + m[1].toLowerCase() +
             "' or local-name()='" + m[1].toUpperCase() + "']";
    },
    className:    "[contains(concat(' ', @class, ' '), ' #{1} ')]",
    id:           "[@id='#{1}']",
    attrPresence: function(m) {
      m[1] = m[1].toLowerCase();
      return new Template("[@#{1}]").evaluate(m);
    },
    attr: function(m) {
      m[1] = m[1].toLowerCase();
      m[3] = m[5] || m[6];
      return new Template(Selector.xpath.operators[m[2]]).evaluate(m);
    },
    pseudo: function(m) {
      var h = Selector.xpath.pseudos[m[1]];
      if (!h) return '';
      if (Object.isFunction(h)) return h(m);
      return new Template(Selector.xpath.pseudos[m[1]]).evaluate(m);
    },
    operators: {
      '=':  "[@#{1}='#{3}']",
      '!=': "[@#{1}!='#{3}']",
      '^=': "[starts-with(@#{1}, '#{3}')]",
      '$=': "[substring(@#{1}, (string-length(@#{1}) - string-length('#{3}') + 1))='#{3}']",
      '*=': "[contains(@#{1}, '#{3}')]",
      '~=': "[contains(concat(' ', @#{1}, ' '), ' #{3} ')]",
      '|=': "[contains(concat('-', @#{1}, '-'), '-#{3}-')]"
    },
    pseudos: {
      'first-child': '[not(preceding-sibling::*)]',
      'last-child':  '[not(following-sibling::*)]',
      'only-child':  '[not(preceding-sibling::* or following-sibling::*)]',
      'empty':       "[count(*) = 0 and (count(text()) = 0)]",
      'checked':     "[@checked]",
      'disabled':    "[(@disabled) and (@type!='hidden')]",
      'enabled':     "[not(@disabled) and (@type!='hidden')]",
      'not': function(m) {
        var e = m[6], p = Selector.patterns,
            x = Selector.xpath, le, v, len = p.length, name;

        var exclusion = [];
        while (e && le != e && (/\S/).test(e)) {
          le = e;
          for (var i = 0; i<len; i++) {
            name = p[i].name
            if (m = e.match(p[i].re)) {
              v = Object.isFunction(x[name]) ? x[name](m) : new Template(x[name]).evaluate(m);
              exclusion.push("(" + v.substring(1, v.length - 1) + ")");
              e = e.replace(m[0], '');
              break;
            }
          }
        }
        return "[not(" + exclusion.join(" and ") + ")]";
      },
      'nth-child':      function(m) {
        return Selector.xpath.pseudos.nth("(count(./preceding-sibling::*) + 1) ", m);
      },
      'nth-last-child': function(m) {
        return Selector.xpath.pseudos.nth("(count(./following-sibling::*) + 1) ", m);
      },
      'nth-of-type':    function(m) {
        return Selector.xpath.pseudos.nth("position() ", m);
      },
      'nth-last-of-type': function(m) {
        return Selector.xpath.pseudos.nth("(last() + 1 - position()) ", m);
      },
      'first-of-type':  function(m) {
        m[6] = "1";return Selector.xpath.pseudos['nth-of-type'](m);
      },
      'last-of-type':   function(m) {
        m[6] = "1";return Selector.xpath.pseudos['nth-last-of-type'](m);
      },
      'only-of-type':   function(m) {
        var p = Selector.xpath.pseudos;return p['first-of-type'](m) + p['last-of-type'](m);
      },
      nth: function(fragment, m) {
        var mm, formula = m[6], predicate;
        if (formula == 'even') formula = '2n+0';
        if (formula == 'odd')  formula = '2n+1';
        if (mm = formula.match(/^(\d+)$/)) // digit only
          return '[' + fragment + "= " + mm[1] + ']';
        if (mm = formula.match(/^(-?\d*)?n(([+-])(\d+))?/)) { // an+b
          if (mm[1] == "-") mm[1] = -1;
          var a = mm[1] ? Number(mm[1]) : 1;
          var b = mm[2] ? Number(mm[2]) : 0;
          predicate = "[((#{fragment} - #{b}) mod #{a} = 0) and " +
          "((#{fragment} - #{b}) div #{a} >= 0)]";
          return new Template(predicate).evaluate({
            fragment: fragment, a: a, b: b});
        }
      }
    }
  },

  criteria: {
    tagName:      'n = h.tagName(n, r, "#{1}", c);      c = false;',
    className:    'n = h.className(n, r, "#{1}", c);    c = false;',
    id:           'n = h.id(n, r, "#{1}", c);           c = false;',
    attrPresence: 'n = h.attrPresence(n, r, "#{1}", c); c = false;',
    attr: function(m) {
      m[3] = (m[5] || m[6]);
      return new Template('n = h.attr(n, r, "#{1}", "#{3}", "#{2}", c); c = false;').evaluate(m);
    },
    pseudo: function(m) {
      if (m[6]) m[6] = m[6].replace(/"/g, '\\"');
      return new Template('n = h.pseudo(n, "#{1}", "#{6}", r, c); c = false;').evaluate(m);
    },
    descendant:   'c = "descendant";',
    child:        'c = "child";',
    adjacent:     'c = "adjacent";',
    laterSibling: 'c = "laterSibling";'
  },

  patterns: [
    {name: 'laterSibling', re: /^\s*~\s*/},
    {name: 'child',        re: /^\s*>\s*/},
    {name: 'adjacent',     re: /^\s*\+\s*/},
    {name: 'descendant',   re: /^\s/},

    {name: 'tagName',      re: /^\s*(\*|[\w\-]+)(\b|$)?/},
    {name: 'id',           re: /^#([\w\-\*]+)(\b|$)/},
    {name: 'className',    re: /^\.([\w\-\*]+)(\b|$)/},
    {name: 'pseudo',       re: /^:((first|last|nth|nth-last|only)(-child|-of-type)|empty|checked|(en|dis)abled|not)(\((.*?)\))?(\b|$|(?=\s|[:+~>]))/},
    {name: 'attrPresence', re: /^\[((?:[\w-]+:)?[\w-]+)\]/},
    {name: 'attr',         re: /\[((?:[\w-]*:)?[\w-]+)\s*(?:([!^$*~|]?=)\s*((['"])([^\4]*?)\4|([^'"][^\]]*?)))?\]/}
  ],

  assertions: {
    tagName: function(element, matches) {
      return matches[1].toUpperCase() == element.tagName.toUpperCase();
    },

    className: function(element, matches) {
      return Element.hasClassName(element, matches[1]);
    },

    id: function(element, matches) {
      return element.id === matches[1];
    },

    attrPresence: function(element, matches) {
      return Element.hasAttribute(element, matches[1]);
    },

    attr: function(element, matches) {
      var nodeValue = Element.readAttribute(element, matches[1]);
      return nodeValue && Selector.operators[matches[2]](nodeValue, matches[5] || matches[6]);
    }
  },

  handlers: {
    concat: function(a, b) {
      for (var i = 0, node; node = b[i]; i++)
        a.push(node);
      return a;
    },

    mark: function(nodes) {
      var _true = Prototype.emptyFunction;
      for (var i = 0, node; node = nodes[i]; i++)
        node._countedByPrototype = _true;
      return nodes;
    },

    unmark: (function(){

      var PROPERTIES_ATTRIBUTES_MAP = (function(){
        var el = document.createElement('div'),
            isBuggy = false,
            propName = '_countedByPrototype',
            value = 'x'
        el[propName] = value;
        isBuggy = (el.getAttribute(propName) === value);
        el = null;
        return isBuggy;
      })();

      return PROPERTIES_ATTRIBUTES_MAP ?
        function(nodes) {
          for (var i = 0, node; node = nodes[i]; i++)
            node.removeAttribute('_countedByPrototype');
          return nodes;
        } :
        function(nodes) {
          for (var i = 0, node; node = nodes[i]; i++)
            node._countedByPrototype = void 0;
          return nodes;
        }
    })(),

    index: function(parentNode, reverse, ofType) {
      parentNode._countedByPrototype = Prototype.emptyFunction;
      if (reverse) {
        for (var nodes = parentNode.childNodes, i = nodes.length - 1, j = 1; i >= 0; i--) {
          var node = nodes[i];
          if (node.nodeType == 1 && (!ofType || node._countedByPrototype)) node.nodeIndex = j++;
        }
      } else {
        for (var i = 0, j = 1, nodes = parentNode.childNodes; node = nodes[i]; i++)
          if (node.nodeType == 1 && (!ofType || node._countedByPrototype)) node.nodeIndex = j++;
      }
    },

    unique: function(nodes) {
      if (nodes.length == 0) return nodes;
      var results = [], n;
      for (var i = 0, l = nodes.length; i < l; i++)
        if (typeof (n = nodes[i])._countedByPrototype == 'undefined') {
          n._countedByPrototype = Prototype.emptyFunction;
          results.push(Element.extend(n));
        }
      return Selector.handlers.unmark(results);
    },

    descendant: function(nodes) {
      var h = Selector.handlers;
      for (var i = 0, results = [], node; node = nodes[i]; i++)
        h.concat(results, node.getElementsByTagName('*'));
      return results;
    },

    child: function(nodes) {
      var h = Selector.handlers;
      for (var i = 0, results = [], node; node = nodes[i]; i++) {
        for (var j = 0, child; child = node.childNodes[j]; j++)
          if (child.nodeType == 1 && child.tagName != '!') results.push(child);
      }
      return results;
    },

    adjacent: function(nodes) {
      for (var i = 0, results = [], node; node = nodes[i]; i++) {
        var next = this.nextElementSibling(node);
        if (next) results.push(next);
      }
      return results;
    },

    laterSibling: function(nodes) {
      var h = Selector.handlers;
      for (var i = 0, results = [], node; node = nodes[i]; i++)
        h.concat(results, Element.nextSiblings(node));
      return results;
    },

    nextElementSibling: function(node) {
      while (node = node.nextSibling)
        if (node.nodeType == 1) return node;
      return null;
    },

    previousElementSibling: function(node) {
      while (node = node.previousSibling)
        if (node.nodeType == 1) return node;
      return null;
    },

    tagName: function(nodes, root, tagName, combinator) {
      var uTagName = tagName.toUpperCase();
      var results = [], h = Selector.handlers;
      if (nodes) {
        if (combinator) {
          if (combinator == "descendant") {
            for (var i = 0, node; node = nodes[i]; i++)
              h.concat(results, node.getElementsByTagName(tagName));
            return results;
          } else nodes = this[combinator](nodes);
          if (tagName == "*") return nodes;
        }
        for (var i = 0, node; node = nodes[i]; i++)
          if (node.tagName.toUpperCase() === uTagName) results.push(node);
        return results;
      } else return root.getElementsByTagName(tagName);
    },

    id: function(nodes, root, id, combinator) {
      var targetNode = $(id), h = Selector.handlers;

      if (root == document) {
        if (!targetNode) return [];
        if (!nodes) return [targetNode];
      } else {
        if (!root.sourceIndex || root.sourceIndex < 1) {
          var nodes = root.getElementsByTagName('*');
          for (var j = 0, node; node = nodes[j]; j++) {
            if (node.id === id) return [node];
          }
        }
      }

      if (nodes) {
        if (combinator) {
          if (combinator == 'child') {
            for (var i = 0, node; node = nodes[i]; i++)
              if (targetNode.parentNode == node) return [targetNode];
          } else if (combinator == 'descendant') {
            for (var i = 0, node; node = nodes[i]; i++)
              if (Element.descendantOf(targetNode, node)) return [targetNode];
          } else if (combinator == 'adjacent') {
            for (var i = 0, node; node = nodes[i]; i++)
              if (Selector.handlers.previousElementSibling(targetNode) == node)
                return [targetNode];
          } else nodes = h[combinator](nodes);
        }
        for (var i = 0, node; node = nodes[i]; i++)
          if (node == targetNode) return [targetNode];
        return [];
      }
      return (targetNode && Element.descendantOf(targetNode, root)) ? [targetNode] : [];
    },

    className: function(nodes, root, className, combinator) {
      if (nodes && combinator) nodes = this[combinator](nodes);
      return Selector.handlers.byClassName(nodes, root, className);
    },

    byClassName: function(nodes, root, className) {
      if (!nodes) nodes = Selector.handlers.descendant([root]);
      var needle = ' ' + className + ' ';
      for (var i = 0, results = [], node, nodeClassName; node = nodes[i]; i++) {
        nodeClassName = node.className;
        if (nodeClassName.length == 0) continue;
        if (nodeClassName == className || (' ' + nodeClassName + ' ').include(needle))
          results.push(node);
      }
      return results;
    },

    attrPresence: function(nodes, root, attr, combinator) {
      if (!nodes) nodes = root.getElementsByTagName("*");
      if (nodes && combinator) nodes = this[combinator](nodes);
      var results = [];
      for (var i = 0, node; node = nodes[i]; i++)
        if (Element.hasAttribute(node, attr)) results.push(node);
      return results;
    },

    attr: function(nodes, root, attr, value, operator, combinator) {
      if (!nodes) nodes = root.getElementsByTagName("*");
      if (nodes && combinator) nodes = this[combinator](nodes);
      var handler = Selector.operators[operator], results = [];
      for (var i = 0, node; node = nodes[i]; i++) {
        var nodeValue = Element.readAttribute(node, attr);
        if (nodeValue === null) continue;
        if (handler(nodeValue, value)) results.push(node);
      }
      return results;
    },

    pseudo: function(nodes, name, value, root, combinator) {
      if (nodes && combinator) nodes = this[combinator](nodes);
      if (!nodes) nodes = root.getElementsByTagName("*");
      return Selector.pseudos[name](nodes, value, root);
    }
  },

  pseudos: {
    'first-child': function(nodes, value, root) {
      for (var i = 0, results = [], node; node = nodes[i]; i++) {
        if (Selector.handlers.previousElementSibling(node)) continue;
          results.push(node);
      }
      return results;
    },
    'last-child': function(nodes, value, root) {
      for (var i = 0, results = [], node; node = nodes[i]; i++) {
        if (Selector.handlers.nextElementSibling(node)) continue;
          results.push(node);
      }
      return results;
    },
    'only-child': function(nodes, value, root) {
      var h = Selector.handlers;
      for (var i = 0, results = [], node; node = nodes[i]; i++)
        if (!h.previousElementSibling(node) && !h.nextElementSibling(node))
          results.push(node);
      return results;
    },
    'nth-child':        function(nodes, formula, root) {
      return Selector.pseudos.nth(nodes, formula, root);
    },
    'nth-last-child':   function(nodes, formula, root) {
      return Selector.pseudos.nth(nodes, formula, root, true);
    },
    'nth-of-type':      function(nodes, formula, root) {
      return Selector.pseudos.nth(nodes, formula, root, false, true);
    },
    'nth-last-of-type': function(nodes, formula, root) {
      return Selector.pseudos.nth(nodes, formula, root, true, true);
    },
    'first-of-type':    function(nodes, formula, root) {
      return Selector.pseudos.nth(nodes, "1", root, false, true);
    },
    'last-of-type':     function(nodes, formula, root) {
      return Selector.pseudos.nth(nodes, "1", root, true, true);
    },
    'only-of-type':     function(nodes, formula, root) {
      var p = Selector.pseudos;
      return p['last-of-type'](p['first-of-type'](nodes, formula, root), formula, root);
    },

    getIndices: function(a, b, total) {
      if (a == 0) return b > 0 ? [b] : [];
      return $R(1, total).inject([], function(memo, i) {
        if (0 == (i - b) % a && (i - b) / a >= 0) memo.push(i);
        return memo;
      });
    },

    nth: function(nodes, formula, root, reverse, ofType) {
      if (nodes.length == 0) return [];
      if (formula == 'even') formula = '2n+0';
      if (formula == 'odd')  formula = '2n+1';
      var h = Selector.handlers, results = [], indexed = [], m;
      h.mark(nodes);
      for (var i = 0, node; node = nodes[i]; i++) {
        if (!node.parentNode._countedByPrototype) {
          h.index(node.parentNode, reverse, ofType);
          indexed.push(node.parentNode);
        }
      }
      if (formula.match(/^\d+$/)) { // just a number
        formula = Number(formula);
        for (var i = 0, node; node = nodes[i]; i++)
          if (node.nodeIndex == formula) results.push(node);
      } else if (m = formula.match(/^(-?\d*)?n(([+-])(\d+))?/)) { // an+b
        if (m[1] == "-") m[1] = -1;
        var a = m[1] ? Number(m[1]) : 1;
        var b = m[2] ? Number(m[2]) : 0;
        var indices = Selector.pseudos.getIndices(a, b, nodes.length);
        for (var i = 0, node, l = indices.length; node = nodes[i]; i++) {
          for (var j = 0; j < l; j++)
            if (node.nodeIndex == indices[j]) results.push(node);
        }
      }
      h.unmark(nodes);
      h.unmark(indexed);
      return results;
    },

    'empty': function(nodes, value, root) {
      for (var i = 0, results = [], node; node = nodes[i]; i++) {
        if (node.tagName == '!' || node.firstChild) continue;
        results.push(node);
      }
      return results;
    },

    'not': function(nodes, selector, root) {
      var h = Selector.handlers, selectorType, m;
      var exclusions = new Selector(selector).findElements(root);
      h.mark(exclusions);
      for (var i = 0, results = [], node; node = nodes[i]; i++)
        if (!node._countedByPrototype) results.push(node);
      h.unmark(exclusions);
      return results;
    },

    'enabled': function(nodes, value, root) {
      for (var i = 0, results = [], node; node = nodes[i]; i++)
        if (!node.disabled && (!node.type || node.type !== 'hidden'))
          results.push(node);
      return results;
    },

    'disabled': function(nodes, value, root) {
      for (var i = 0, results = [], node; node = nodes[i]; i++)
        if (node.disabled) results.push(node);
      return results;
    },

    'checked': function(nodes, value, root) {
      for (var i = 0, results = [], node; node = nodes[i]; i++)
        if (node.checked) results.push(node);
      return results;
    }
  },

  operators: {
    '=':  function(nv, v) {return nv == v;},
    '!=': function(nv, v) {return nv != v;},
    '^=': function(nv, v) {return nv == v || nv && nv.startsWith(v);},
    '$=': function(nv, v) {return nv == v || nv && nv.endsWith(v);},
    '*=': function(nv, v) {return nv == v || nv && nv.include(v);},
    '~=': function(nv, v) {return (' ' + nv + ' ').include(' ' + v + ' ');},
    '|=': function(nv, v) {return ('-' + (nv || "").toUpperCase() +
     '-').include('-' + (v || "").toUpperCase() + '-');}
  },

  split: function(expression) {
    var expressions = [];
    expression.scan(/(([\w#:.~>+()\s-]+|\*|\[.*?\])+)\s*(,|$)/, function(m) {
      expressions.push(m[1].strip());
    });
    return expressions;
  },

  matchElements: function(elements, expression) {
    var matches = $$(expression), h = Selector.handlers;
    h.mark(matches);
    for (var i = 0, results = [], element; element = elements[i]; i++)
      if (element._countedByPrototype) results.push(element);
    h.unmark(matches);
    return results;
  },

  findElement: function(elements, expression, index) {
    if (Object.isNumber(expression)) {
      index = expression;expression = false;
    }
    return Selector.matchElements(elements, expression || '*')[index || 0];
  },

  findChildElements: function(element, expressions) {
    expressions = Selector.split(expressions.join(','));
    var results = [], h = Selector.handlers;
    for (var i = 0, l = expressions.length, selector; i < l; i++) {
      selector = new Selector(expressions[i].strip());
      h.concat(results, selector.findElements(element));
    }
    return (l > 1) ? h.unique(results) : results;
  }
});

if (Prototype.Browser.IE) {
  Object.extend(Selector.handlers, {
    concat: function(a, b) {
      for (var i = 0, node; node = b[i]; i++)
        if (node.tagName !== "!") a.push(node);
      return a;
    }
  });
}

function $$() {
  return Selector.findChildElements(document, $A(arguments));
}

var Form = {
  reset: function(form) {
    form = $(form);
    form.reset();
    return form;
  },

  serializeElements: function(elements, options) {
    if (typeof options != 'object') options = {hash: !!options};
    else if (Object.isUndefined(options.hash)) options.hash = true;
    var key, value, submitted = false, submit = options.submit;

    var data = elements.inject({ }, function(result, element) {
      if (!element.disabled && element.name) {
        key = element.name;value = $(element).getValue();
        if (value != null && element.type != 'file' && (element.type != 'submit' || (!submitted &&
            submit !== false && (!submit || key == submit) && (submitted = true)))) {
          if (key in result) {
            if (!Object.isArray(result[key])) result[key] = [result[key]];
            result[key].push(value);
          }
          else result[key] = value;
        }
      }
      return result;
    });

    return options.hash ? data : Object.toQueryString(data);
  }
};

Form.Methods = {
  serialize: function(form, options) {
    return Form.serializeElements(Form.getElements(form), options);
  },

  getElements: function(form) {
    var elements = $(form).getElementsByTagName('*'),
        element,
        arr = [ ],
        serializers = Form.Element.Serializers;
    for (var i = 0; element = elements[i]; i++) {
      arr.push(element);
    }
    return arr.inject([], function(elements, child) {
      if (serializers[child.tagName.toLowerCase()])
        elements.push(Element.extend(child));
      return elements;
    })
  },

  getInputs: function(form, typeName, name) {
    form = $(form);
    var inputs = form.getElementsByTagName('input');

    if (!typeName && !name) return $A(inputs).map(Element.extend);

    for (var i = 0, matchingInputs = [], length = inputs.length; i < length; i++) {
      var input = inputs[i];
      if ((typeName && input.type != typeName) || (name && input.name != name))
        continue;
      matchingInputs.push(Element.extend(input));
    }

    return matchingInputs;
  },

  disable: function(form) {
    form = $(form);
    Form.getElements(form).invoke('disable');
    return form;
  },

  enable: function(form) {
    form = $(form);
    Form.getElements(form).invoke('enable');
    return form;
  },

  findFirstElement: function(form) {
    var elements = $(form).getElements().findAll(function(element) {
      return 'hidden' != element.type && !element.disabled;
    });
    var firstByIndex = elements.findAll(function(element) {
      return element.hasAttribute('tabIndex') && element.tabIndex >= 0;
    }).sortBy(function(element) {return element.tabIndex}).first();

    return firstByIndex ? firstByIndex : elements.find(function(element) {
      return /^(?:input|select|textarea)$/i.test(element.tagName);
    });
  },

  focusFirstElement: function(form) {
    form = $(form);
    form.findFirstElement().activate();
    return form;
  },

  request: function(form, options) {
    form = $(form), options = Object.clone(options || { });

    var params = options.parameters, action = form.readAttribute('action') || '';
    if (action.blank()) action = window.location.href;
    options.parameters = form.serialize(true);

    if (params) {
      if (Object.isString(params)) params = params.toQueryParams();
      Object.extend(options.parameters, params);
    }

    if (form.hasAttribute('method') && !options.method)
      options.method = form.method;

    return new Ajax.Request(action, options);
  }
};

/*--------------------------------------------------------------------------*/


Form.Element = {
  focus: function(element) {
    $(element).focus();
    return element;
  },

  select: function(element) {
    $(element).select();
    return element;
  }
};

Form.Element.Methods = {

  serialize: function(element) {
    element = $(element);
    if (!element.disabled && element.name) {
      var value = element.getValue();
      if (value != undefined) {
        var pair = { };
        pair[element.name] = value;
        return Object.toQueryString(pair);
      }
    }
    return '';
  },

  getValue: function(element) {
    element = $(element);
    var method = element.tagName.toLowerCase();
    return Form.Element.Serializers[method](element);
  },

  setValue: function(element, value) {
    element = $(element);
    var method = element.tagName.toLowerCase();
    Form.Element.Serializers[method](element, value);
    return element;
  },

  clear: function(element) {
    $(element).value = '';
    return element;
  },

  present: function(element) {
    return $(element).value != '';
  },

  activate: function(element) {
    element = $(element);
    try {
      element.focus();
      if (element.select && (element.tagName.toLowerCase() != 'input' ||
          !(/^(?:button|reset|submit)$/i.test(element.type))))
        element.select();
    } catch (e) { }
    return element;
  },

  disable: function(element) {
    element = $(element);
    element.disabled = true;
    return element;
  },

  enable: function(element) {
    element = $(element);
    element.disabled = false;
    return element;
  }
};

/*--------------------------------------------------------------------------*/

var Field = Form.Element;

var $F = Form.Element.Methods.getValue;

/*--------------------------------------------------------------------------*/

Form.Element.Serializers = {
  input: function(element, value) {
    switch (element.type.toLowerCase()) {
      case 'checkbox':
      case 'radio':
        return Form.Element.Serializers.inputSelector(element, value);
      default:
        return Form.Element.Serializers.textarea(element, value);
    }
  },

  inputSelector: function(element, value) {
    if (Object.isUndefined(value)) return element.checked ? element.value : null;
    else element.checked = !!value;
  },

  textarea: function(element, value) {
    if (Object.isUndefined(value)) return element.value;
    else element.value = value;
  },

  select: function(element, value) {
    if (Object.isUndefined(value))
      return this[element.type == 'select-one' ?
        'selectOne' : 'selectMany'](element);
    else {
      var opt, currentValue, single = !Object.isArray(value);
      for (var i = 0, length = element.length; i < length; i++) {
        opt = element.options[i];
        currentValue = this.optionValue(opt);
        if (single) {
          if (currentValue == value) {
            opt.selected = true;
            return;
          }
        }
        else opt.selected = value.include(currentValue);
      }
    }
  },

  selectOne: function(element) {
    var index = element.selectedIndex;
    return index >= 0 ? this.optionValue(element.options[index]) : null;
  },

  selectMany: function(element) {
    var values, length = element.length;
    if (!length) return null;

    for (var i = 0, values = []; i < length; i++) {
      var opt = element.options[i];
      if (opt.selected) values.push(this.optionValue(opt));
    }
    return values;
  },

  optionValue: function(opt) {
    return Element.extend(opt).hasAttribute('value') ? opt.value : opt.text;
  }
};

/*--------------------------------------------------------------------------*/


Abstract.TimedObserver = Class.create(PeriodicalExecuter, {
  initialize: function($super, element, frequency, callback) {
    $super(callback, frequency);
    this.element   = $(element);
    this.lastValue = this.getValue();
  },

  execute: function() {
    var value = this.getValue();
    if (Object.isString(this.lastValue) && Object.isString(value) ?
        this.lastValue != value : String(this.lastValue) != String(value)) {
      this.callback(this.element, value);
      this.lastValue = value;
    }
  }
});

Form.Element.Observer = Class.create(Abstract.TimedObserver, {
  getValue: function() {
    return Form.Element.getValue(this.element);
  }
});

Form.Observer = Class.create(Abstract.TimedObserver, {
  getValue: function() {
    return Form.serialize(this.element);
  }
});

/*--------------------------------------------------------------------------*/

Abstract.EventObserver = Class.create({
  initialize: function(element, callback) {
    this.element  = $(element);
    this.callback = callback;

    this.lastValue = this.getValue();
    if (this.element.tagName.toLowerCase() == 'form')
      this.registerFormCallbacks();
    else
      this.registerCallback(this.element);
  },

  onElementEvent: function() {
    var value = this.getValue();
    if (this.lastValue != value) {
      this.callback(this.element, value);
      this.lastValue = value;
    }
  },

  registerFormCallbacks: function() {
    Form.getElements(this.element).each(this.registerCallback, this);
  },

  registerCallback: function(element) {
    if (element.type) {
      switch (element.type.toLowerCase()) {
        case 'checkbox':
        case 'radio':
          Event.observe(element, 'click', this.onElementEvent.bind(this));
          break;
        default:
          Event.observe(element, 'change', this.onElementEvent.bind(this));
          break;
      }
    }
  }
});

Form.Element.EventObserver = Class.create(Abstract.EventObserver, {
  getValue: function() {
    return Form.Element.getValue(this.element);
  }
});

Form.EventObserver = Class.create(Abstract.EventObserver, {
  getValue: function() {
    return Form.serialize(this.element);
  }
});
(function() {

  var Event = {
    KEY_BACKSPACE: 8,
    KEY_TAB:       9,
    KEY_RETURN:   13,
    KEY_ESC:      27,
    KEY_LEFT:     37,
    KEY_UP:       38,
    KEY_RIGHT:    39,
    KEY_DOWN:     40,
    KEY_DELETE:   46,
    KEY_HOME:     36,
    KEY_END:      35,
    KEY_PAGEUP:   33,
    KEY_PAGEDOWN: 34,
    KEY_INSERT:   45,

    cache: {}
  };

  var docEl = document.documentElement;
  var MOUSEENTER_MOUSELEAVE_EVENTS_SUPPORTED = 'onmouseenter' in docEl
    && 'onmouseleave' in docEl;

  var _isButton;
  if (Prototype.Browser.IE) {
    var buttonMap = {0: 1, 1: 4, 2: 2};
    _isButton = function(event, code) {
      return event.button === buttonMap[code];
    };
  } else if (Prototype.Browser.WebKit) {
    _isButton = function(event, code) {
      switch (code) {
        case 0:return event.which == 1 && !event.metaKey;
        case 1:return event.which == 1 && event.metaKey;
        default:return false;
      }
    };
  } else {
    _isButton = function(event, code) {
      return event.which ? (event.which === code + 1) : (event.button === code);
    };
  }

  function isLeftClick(event)   {return _isButton(event, 0)}

  function isMiddleClick(event) {return _isButton(event, 1)}

  function isRightClick(event)  {return _isButton(event, 2)}

  function element(event) {
    event = Event.extend(event);

    var node = event.target, type = event.type,
     currentTarget = event.currentTarget;

    if (currentTarget && currentTarget.tagName) {
      if (type === 'load' || type === 'error' ||
        (type === 'click' && currentTarget.tagName.toLowerCase() === 'input'
          && currentTarget.type === 'radio'))
            node = currentTarget;
    }

    if (node.nodeType == Node.TEXT_NODE)
      node = node.parentNode;

    return Element.extend(node);
  }

  function findElement(event, expression) {
    var element = Event.element(event);
    if (!expression) return element;
    var elements = [element].concat(element.ancestors());
    return Selector.findElement(elements, expression, 0);
  }

  function pointer(event) {
    return {x: pointerX(event), y: pointerY(event)};
  }

  function pointerX(event) {
    var docElement = document.documentElement,
     body = document.body || {scrollLeft: 0};

    return event.pageX || (event.clientX +
      (docElement.scrollLeft || body.scrollLeft) -
      (docElement.clientLeft || 0));
  }

  function pointerY(event) {
    var docElement = document.documentElement,
     body = document.body || {scrollTop: 0};

    return  event.pageY || (event.clientY +
       (docElement.scrollTop || body.scrollTop) -
       (docElement.clientTop || 0));
  }


  function stop(event) {
    Event.extend(event);
    event.preventDefault();
    event.stopPropagation();

    event.stopped = true;
  }

  Event.Methods = {
    isLeftClick: isLeftClick,
    isMiddleClick: isMiddleClick,
    isRightClick: isRightClick,

    element: element,
    findElement: findElement,

    pointer: pointer,
    pointerX: pointerX,
    pointerY: pointerY,

    stop: stop
  };


  var methods = Object.keys(Event.Methods).inject({ }, function(m, name) {
    m[name] = Event.Methods[name].methodize();
    return m;
  });

  if (Prototype.Browser.IE) {
    function _relatedTarget(event) {
      var element;
      switch (event.type) {
        case 'mouseover':element = event.fromElement;break;
        case 'mouseout':element = event.toElement;break;
        default:return null;
      }
      return Element.extend(element);
    }

    Object.extend(methods, {
      stopPropagation: function() {this.cancelBubble = true},
      preventDefault:  function() {this.returnValue = false},
      inspect: function() {return '[object Event]'}
    });

    Event.extend = function(event, element) {
      if (!event) return false;
      if (event._extendedByPrototype) return event;

      event._extendedByPrototype = Prototype.emptyFunction;
      var pointer = Event.pointer(event);

      Object.extend(event, {
        target: event.srcElement || element,
        relatedTarget: _relatedTarget(event),
        pageX:  pointer.x,
        pageY:  pointer.y
      });

      return Object.extend(event, methods);
    };
  } else {
    Event.prototype = window.Event.prototype || document.createEvent('HTMLEvents').__proto__;
    Object.extend(Event.prototype, methods);
    Event.extend = Prototype.K;
  }

  function _createResponder(element, eventName, handler) {
    var registry = Element.retrieve(element, 'prototype_event_registry');

    if (Object.isUndefined(registry)) {
      CACHE.push(element);
      registry = Element.retrieve(element, 'prototype_event_registry', $H());
    }

    var respondersForEvent = registry.get(eventName);
    if (Object.isUndefined(respondersForEvent)) {
      respondersForEvent = [];
      registry.set(eventName, respondersForEvent);
    }

    if (respondersForEvent.pluck('handler').include(handler)) return false;

    var responder;
    if (eventName.include(":")) {
      responder = function(event) {
        if (Object.isUndefined(event.eventName))
          return false;

        if (event.eventName !== eventName)
          return false;

        Event.extend(event, element);
        handler.call(element, event);
      };
    } else {
      if (!MOUSEENTER_MOUSELEAVE_EVENTS_SUPPORTED &&
       (eventName === "mouseenter" || eventName === "mouseleave")) {
        if (eventName === "mouseenter" || eventName === "mouseleave") {
          responder = function(event) {
            Event.extend(event, element);

            var parent = event.relatedTarget;
            while (parent && parent !== element) {
              try {parent = parent.parentNode;}
              catch(e) {parent = element;}
            }

            if (parent === element) return;

            handler.call(element, event);
          };
        }
      } else {
        responder = function(event) {
          Event.extend(event, element);
          handler.call(element, event);
        };
      }
    }

    responder.handler = handler;
    respondersForEvent.push(responder);
    return responder;
  }

  function _destroyCache() {
    for (var i = 0, length = CACHE.length; i < length; i++) {
      Event.stopObserving(CACHE[i]);
      CACHE[i] = null;
    }
  }

  var CACHE = [];

  if (Prototype.Browser.IE)
    window.attachEvent('onunload', _destroyCache);

  if (Prototype.Browser.WebKit)
    window.addEventListener('unload', Prototype.emptyFunction, false);


  var _getDOMEventName = Prototype.K;

  if (!MOUSEENTER_MOUSELEAVE_EVENTS_SUPPORTED) {
    _getDOMEventName = function(eventName) {
      var translations = {mouseenter: "mouseover", mouseleave: "mouseout"};
      return eventName in translations ? translations[eventName] : eventName;
    };
  }

  function observe(element, eventName, handler) {
    element = $(element);

    var responder = _createResponder(element, eventName, handler);

    if (!responder) return element;

    if (eventName.include(':')) {
      if (element.addEventListener)
        element.addEventListener("dataavailable", responder, false);
      else {
        element.attachEvent("ondataavailable", responder);
        element.attachEvent("onfilterchange", responder);
      }
    } else {
      var actualEventName = _getDOMEventName(eventName);

      if (element.addEventListener)
        element.addEventListener(actualEventName, responder, false);
      else
        element.attachEvent("on" + actualEventName, responder);
    }

    return element;
  }

  function stopObserving(element, eventName, handler) {
    element = $(element);

    var registry = Element.retrieve(element, 'prototype_event_registry');

    if (Object.isUndefined(registry)) return element;

    if (eventName && !handler) {
      var responders = registry.get(eventName);

      if (Object.isUndefined(responders)) return element;

      responders.each( function(r) {
        Element.stopObserving(element, eventName, r.handler);
      });
      return element;
    } else if (!eventName) {
      registry.each( function(pair) {
        var eventName = pair.key, responders = pair.value;

        responders.each( function(r) {
          Element.stopObserving(element, eventName, r.handler);
        });
      });
      return element;
    }

    var responders = registry.get(eventName);

    if (!responders) return;

    var responder = responders.find( function(r) {return r.handler === handler;});
    if (!responder) return element;

    var actualEventName = _getDOMEventName(eventName);

    if (eventName.include(':')) {
      if (element.removeEventListener)
        element.removeEventListener("dataavailable", responder, false);
      else {
        element.detachEvent("ondataavailable", responder);
        element.detachEvent("onfilterchange",  responder);
      }
    } else {
      if (element.removeEventListener)
        element.removeEventListener(actualEventName, responder, false);
      else
        element.detachEvent('on' + actualEventName, responder);
    }

    registry.set(eventName, responders.without(responder));

    return element;
  }

  function fire(element, eventName, memo, bubble) {
    element = $(element);

    if (Object.isUndefined(bubble))
      bubble = true;

    if (element == document && document.createEvent && !element.dispatchEvent)
      element = document.documentElement;

    var event;
    if (document.createEvent) {
      event = document.createEvent('HTMLEvents');
      event.initEvent('dataavailable', true, true);
    } else {
      event = document.createEventObject();
      event.eventType = bubble ? 'ondataavailable' : 'onfilterchange';
    }

    event.eventName = eventName;
    event.memo = memo || { };

    if (document.createEvent)
      element.dispatchEvent(event);
    else
      element.fireEvent(event.eventType, event);

    return Event.extend(event);
  }


  Object.extend(Event, Event.Methods);

  Object.extend(Event, {
    fire:          fire,
    observe:       observe,
    stopObserving: stopObserving
  });

  Element.addMethods({
    fire:          fire,

    observe:       observe,

    stopObserving: stopObserving
  });

  Object.extend(document, {
    fire:          fire.methodize(),

    observe:       observe.methodize(),

    stopObserving: stopObserving.methodize(),

    loaded:        false
  });

  if (window.Event) Object.extend(window.Event, Event);
  else window.Event = Event;
})();

(function() {
  /* Support for the DOMContentLoaded event is based on work by Dan Webb,
     Matthias Miller, Dean Edwards, John Resig, and Diego Perini. */

  var timer;

  function fireContentLoadedEvent() {
    if (document.loaded) return;
    if (timer) window.clearTimeout(timer);
    document.loaded = true;
    document.fire('dom:loaded');
  }

  function checkReadyState() {
    if (document.readyState === 'complete') {
      document.stopObserving('readystatechange', checkReadyState);
      fireContentLoadedEvent();
    }
  }

  function pollDoScroll() {
    try {document.documentElement.doScroll('left');}
    catch(e) {
      timer = pollDoScroll.defer();
      return;
    }
    fireContentLoadedEvent();
  }

  if (document.addEventListener) {
    document.addEventListener('DOMContentLoaded', fireContentLoadedEvent, false);
  } else {
    document.observe('readystatechange', checkReadyState);
    if (window == top)
      timer = pollDoScroll.defer();
  }

  Event.observe(window, 'load', fireContentLoadedEvent);
})();

Element.addMethods();

/*------------------------------- DEPRECATED -------------------------------*/

Hash.toQueryString = Object.toQueryString;

var Toggle = {display: Element.toggle};

Element.Methods.childOf = Element.Methods.descendantOf;

var Insertion = {
  Before: function(element, content) {
    return Element.insert(element, {before:content});
  },

  Top: function(element, content) {
    return Element.insert(element, {top:content});
  },

  Bottom: function(element, content) {
    return Element.insert(element, {bottom:content});
  },

  After: function(element, content) {
    return Element.insert(element, {after:content});
  }
};

var $continue = new Error('"throw $continue" is deprecated, use "return" instead');

var Position = {
  includeScrollOffsets: false,

  prepare: function() {
    this.deltaX =  window.pageXOffset
                || document.documentElement.scrollLeft
                || document.body.scrollLeft
                || 0;
    this.deltaY =  window.pageYOffset
                || document.documentElement.scrollTop
                || document.body.scrollTop
                || 0;
  },

  within: function(element, x, y) {
    if (this.includeScrollOffsets)
      return this.withinIncludingScrolloffsets(element, x, y);
    this.xcomp = x;
    this.ycomp = y;
    this.offset = Element.cumulativeOffset(element);

    return (y >= this.offset[1] &&
            y <  this.offset[1] + element.offsetHeight &&
            x >= this.offset[0] &&
            x <  this.offset[0] + element.offsetWidth);
  },

  withinIncludingScrolloffsets: function(element, x, y) {
    var offsetcache = Element.cumulativeScrollOffset(element);

    this.xcomp = x + offsetcache[0] - this.deltaX;
    this.ycomp = y + offsetcache[1] - this.deltaY;
    this.offset = Element.cumulativeOffset(element);

    return (this.ycomp >= this.offset[1] &&
            this.ycomp <  this.offset[1] + element.offsetHeight &&
            this.xcomp >= this.offset[0] &&
            this.xcomp <  this.offset[0] + element.offsetWidth);
  },

  overlap: function(mode, element) {
    if (!mode) return 0;
    if (mode == 'vertical')
      return ((this.offset[1] + element.offsetHeight) - this.ycomp) /
        element.offsetHeight;
    if (mode == 'horizontal')
      return ((this.offset[0] + element.offsetWidth) - this.xcomp) /
        element.offsetWidth;
  },


  cumulativeOffset: Element.Methods.cumulativeOffset,

  positionedOffset: Element.Methods.positionedOffset,

  absolutize: function(element) {
    Position.prepare();
    return Element.absolutize(element);
  },

  relativize: function(element) {
    Position.prepare();
    return Element.relativize(element);
  },

  realOffset: Element.Methods.cumulativeScrollOffset,

  offsetParent: Element.Methods.getOffsetParent,

  page: Element.Methods.viewportOffset,

  clone: function(source, target, options) {
    options = options || { };
    return Element.clonePosition(target, source, options);
  }
};

/*--------------------------------------------------------------------------*/

if (!document.getElementsByClassName) document.getElementsByClassName = function(instanceMethods){
  function iter(name) {
    return name.blank() ? null : "[contains(concat(' ', @class, ' '), ' " + name + " ')]";
  }

  instanceMethods.getElementsByClassName = Prototype.BrowserFeatures.XPath ?
  function(element, className) {
    className = className.toString().strip();
    var cond = /\s/.test(className) ? $w(className).map(iter).join('') : iter(className);
    return cond ? document._getElementsByXPath('.//*' + cond, element) : [];
  } : function(element, className) {
    className = className.toString().strip();
    var elements = [], classNames = (/\s/.test(className) ? $w(className) : null);
    if (!classNames && !className) return elements;

    var nodes = $(element).getElementsByTagName('*');
    className = ' ' + className + ' ';

    for (var i = 0, child, cn; child = nodes[i]; i++) {
      if (child.className && (cn = ' ' + child.className + ' ') && (cn.include(className) ||
          (classNames && classNames.all(function(name) {
            return !name.toString().blank() && cn.include(' ' + name + ' ');
          }))))
        elements.push(Element.extend(child));
    }
    return elements;
  };

  return function(className, parentElement) {
    return $(parentElement || document.body).getElementsByClassName(className);
  };
}(Element.Methods);

/*--------------------------------------------------------------------------*/

Element.ClassNames = Class.create();
Element.ClassNames.prototype = {
  initialize: function(element) {
    this.element = $(element);
  },

  _each: function(iterator) {
    this.element.className.split(/\s+/).select(function(name) {
      return name.length > 0;
    })._each(iterator);
  },

  set: function(className) {
    this.element.className = className;
  },

  add: function(classNameToAdd) {
    if (this.include(classNameToAdd)) return;
    this.set($A(this).concat(classNameToAdd).join(' '));
  },

  remove: function(classNameToRemove) {
    if (!this.include(classNameToRemove)) return;
    this.set($A(this).without(classNameToRemove).join(' '));
  },

  toString: function() {
    return $A(this).join(' ');
  }
};

Object.extend(Element.ClassNames.prototype, Enumerable);

/*--------------------------------------------------------------------------*/
// Copyright (c) 2005-2008 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
// Contributors:
//  Justin Palmer (http://encytemedia.com/)
//  Mark Pilgrim (http://diveintomark.org/)
//  Martin Bialasinki
//
// script.aculo.us is freely distributable under the terms of an MIT-style license.
// For details, see the script.aculo.us web site: http://script.aculo.us/

// converts rgb() and #xxx to #xxxxxx format,
// returns self (or first argument) if not convertable
String.prototype.parseColor = function() {
  var color = '#';
  if (this.slice(0,4) == 'rgb(') {
    var cols = this.slice(4,this.length-1).split(',');
    var i=0;do {color += parseInt(cols[i]).toColorPart()} while (++i<3);
  } else {
    if (this.slice(0,1) == '#') {
      if (this.length==4) for(var i=1;i<4;i++) color += (this.charAt(i) + this.charAt(i)).toLowerCase();
      if (this.length==7) color = this.toLowerCase();
    }
  }
  return (color.length==7 ? color : (arguments[0] || this));
};

/*--------------------------------------------------------------------------*/

Element.collectTextNodes = function(element) {
  return $A($(element).childNodes).collect( function(node) {
    return (node.nodeType==3 ? node.nodeValue :
      (node.hasChildNodes() ? Element.collectTextNodes(node) : ''));
  }).flatten().join('');
};

Element.collectTextNodesIgnoreClass = function(element, className) {
  return $A($(element).childNodes).collect( function(node) {
    return (node.nodeType==3 ? node.nodeValue :
      ((node.hasChildNodes() && !Element.hasClassName(node,className)) ?
        Element.collectTextNodesIgnoreClass(node, className) : ''));
  }).flatten().join('');
};

Element.setContentZoom = function(element, percent) {
  element = $(element);
  element.setStyle({fontSize: (percent/100) + 'em'});
  if (Prototype.Browser.WebKit) window.scrollBy(0,0);
  return element;
};

Element.getInlineOpacity = function(element){
  return $(element).style.opacity || '';
};

Element.forceRerendering = function(element) {
  try {
    element = $(element);
    var n = document.createTextNode(' ');
    element.appendChild(n);
    element.removeChild(n);
  } catch(e) { }
};

/*--------------------------------------------------------------------------*/

var Effect = {
  _elementDoesNotExistError: {
    name: 'ElementDoesNotExistError',
    message: 'The specified DOM element does not exist, but is required for this effect to operate'
  },
  Transitions: {
    linear: Prototype.K,
    sinoidal: function(pos) {
      return (-Math.cos(pos*Math.PI)/2) + .5;
    },
    reverse: function(pos) {
      return 1-pos;
    },
    flicker: function(pos) {
      var pos = ((-Math.cos(pos*Math.PI)/4) + .75) + Math.random()/4;
      return pos > 1 ? 1 : pos;
    },
    wobble: function(pos) {
      return (-Math.cos(pos*Math.PI*(9*pos))/2) + .5;
    },
    pulse: function(pos, pulses) {
      return (-Math.cos((pos*((pulses||5)-.5)*2)*Math.PI)/2) + .5;
    },
    spring: function(pos) {
      return 1 - (Math.cos(pos * 4.5 * Math.PI) * Math.exp(-pos * 6));
    },
    none: function(pos) {
      return 0;
    },
    full: function(pos) {
      return 1;
    }
  },
  DefaultOptions: {
    duration:   1.0,   // seconds
    fps:        100,   // 100= assume 66fps max.
    sync:       false, // true for combining
    from:       0.0,
    to:         1.0,
    delay:      0.0,
    queue:      'parallel'
  },
  tagifyText: function(element) {
    var tagifyStyle = 'position:relative';
    if (Prototype.Browser.IE) tagifyStyle += ';zoom:1';

    element = $(element);
    $A(element.childNodes).each( function(child) {
      if (child.nodeType==3) {
        child.nodeValue.toArray().each( function(character) {
          element.insertBefore(
            new Element('span', {style: tagifyStyle}).update(
              character == ' ' ? String.fromCharCode(160) : character),
              child);
        });
        Element.remove(child);
      }
    });
  },
  multiple: function(element, effect) {
    var elements;
    if (((typeof element == 'object') ||
        Object.isFunction(element)) &&
       (element.length))
      elements = element;
    else
      elements = $(element).childNodes;

    var options = Object.extend({
      speed: 0.1,
      delay: 0.0
    }, arguments[2] || { });
    var masterDelay = options.delay;

    $A(elements).each( function(element, index) {
      new effect(element, Object.extend(options, {delay: index * options.speed + masterDelay}));
    });
  },
  PAIRS: {
    'slide':  ['SlideDown','SlideUp'],
    'blind':  ['BlindDown','BlindUp'],
    'appear': ['Appear','Fade']
  },
  toggle: function(element, effect) {
    element = $(element);
    effect = (effect || 'appear').toLowerCase();
    var options = Object.extend({
      queue: {position:'end', scope:(element.id || 'global'), limit: 1}
    }, arguments[2] || { });
    Effect[element.visible() ?
      Effect.PAIRS[effect][1] : Effect.PAIRS[effect][0]](element, options);
  }
};

Effect.DefaultOptions.transition = Effect.Transitions.sinoidal;

/* ------------- core effects ------------- */

Effect.ScopedQueue = Class.create(Enumerable, {
  initialize: function() {
    this.effects  = [];
    this.interval = null;
  },
  _each: function(iterator) {
    this.effects._each(iterator);
  },
  add: function(effect) {
    var timestamp = new Date().getTime();

    var position = Object.isString(effect.options.queue) ?
      effect.options.queue : effect.options.queue.position;

    switch(position) {
      case 'front':
        // move unstarted effects after this effect
        this.effects.findAll(function(e){return e.state=='idle'}).each( function(e) {
            e.startOn  += effect.finishOn;
            e.finishOn += effect.finishOn;
          });
        break;
      case 'with-last':
        timestamp = this.effects.pluck('startOn').max() || timestamp;
        break;
      case 'end':
        // start effect after last queued effect has finished
        timestamp = this.effects.pluck('finishOn').max() || timestamp;
        break;
    }

    effect.startOn  += timestamp;
    effect.finishOn += timestamp;

    if (!effect.options.queue.limit || (this.effects.length < effect.options.queue.limit))
      this.effects.push(effect);

    if (!this.interval)
      this.interval = setInterval(this.loop.bind(this), 15);
  },
  remove: function(effect) {
    this.effects = this.effects.reject(function(e) {return e==effect});
    if (this.effects.length == 0) {
      clearInterval(this.interval);
      this.interval = null;
    }
  },
  loop: function() {
    var timePos = new Date().getTime();
    for(var i=0, len=this.effects.length;i<len;i++)
      this.effects[i] && this.effects[i].loop(timePos);
  }
});

Effect.Queues = {
  instances: $H(),
  get: function(queueName) {
    if (!Object.isString(queueName)) return queueName;

    return this.instances.get(queueName) ||
      this.instances.set(queueName, new Effect.ScopedQueue());
  }
};
Effect.Queue = Effect.Queues.get('global');

Effect.Base = Class.create({
  position: null,
  start: function(options) {
    function codeForEvent(options,eventName){
      return (
        (options[eventName+'Internal'] ? 'this.options.'+eventName+'Internal(this);' : '') +
        (options[eventName] ? 'this.options.'+eventName+'(this);' : '')
      );
    }
    if (options && options.transition === false) options.transition = Effect.Transitions.linear;
    this.options      = Object.extend(Object.extend({ },Effect.DefaultOptions), options || { });
    this.currentFrame = 0;
    this.state        = 'idle';
    this.startOn      = this.options.delay*1000;
    this.finishOn     = this.startOn+(this.options.duration*1000);
    this.fromToDelta  = this.options.to-this.options.from;
    this.totalTime    = this.finishOn-this.startOn;
    this.totalFrames  = this.options.fps*this.options.duration;

    this.render = (function() {
      function dispatch(effect, eventName) {
        if (effect.options[eventName + 'Internal'])
          effect.options[eventName + 'Internal'](effect);
        if (effect.options[eventName])
          effect.options[eventName](effect);
      }

      return function(pos) {
        if (this.state === "idle") {
          this.state = "running";
          dispatch(this, 'beforeSetup');
          if (this.setup) this.setup();
          dispatch(this, 'afterSetup');
        }
        if (this.state === "running") {
          pos = (this.options.transition(pos) * this.fromToDelta) + this.options.from;
          this.position = pos;
          dispatch(this, 'beforeUpdate');
          if (this.update) this.update(pos);
          dispatch(this, 'afterUpdate');
        }
      };
    })();

    this.event('beforeStart');
    if (!this.options.sync)
      Effect.Queues.get(Object.isString(this.options.queue) ?
        'global' : this.options.queue.scope).add(this);
  },
  loop: function(timePos) {
    if (timePos >= this.startOn) {
      if (timePos >= this.finishOn) {
        this.render(1.0);
        this.cancel();
        this.event('beforeFinish');
        if (this.finish) this.finish();
        this.event('afterFinish');
        return;
      }
      var pos   = (timePos - this.startOn) / this.totalTime,
          frame = (pos * this.totalFrames).round();
      if (frame > this.currentFrame) {
        this.render(pos);
        this.currentFrame = frame;
      }
    }
  },
  cancel: function() {
    if (!this.options.sync)
      Effect.Queues.get(Object.isString(this.options.queue) ?
        'global' : this.options.queue.scope).remove(this);
    this.state = 'finished';
  },
  event: function(eventName) {
    if (this.options[eventName + 'Internal']) this.options[eventName + 'Internal'](this);
    if (this.options[eventName]) this.options[eventName](this);
  },
  inspect: function() {
    var data = $H();
    for(property in this)
      if (!Object.isFunction(this[property])) data.set(property, this[property]);
    return '#<Effect:' + data.inspect() + ',options:' + $H(this.options).inspect() + '>';
  }
});

Effect.Parallel = Class.create(Effect.Base, {
  initialize: function(effects) {
    this.effects = effects || [];
    this.start(arguments[1]);
  },
  update: function(position) {
    this.effects.invoke('render', position);
  },
  finish: function(position) {
    this.effects.each( function(effect) {
      effect.render(1.0);
      effect.cancel();
      effect.event('beforeFinish');
      if (effect.finish) effect.finish(position);
      effect.event('afterFinish');
    });
  }
});

Effect.Tween = Class.create(Effect.Base, {
  initialize: function(object, from, to) {
    object = Object.isString(object) ? $(object) : object;
    var args = $A(arguments), method = args.last(),
      options = args.length == 5 ? args[3] : null;
    this.method = Object.isFunction(method) ? method.bind(object) :
      Object.isFunction(object[method]) ? object[method].bind(object) :
      function(value) {object[method] = value};
    this.start(Object.extend({from: from, to: to}, options || { }));
  },
  update: function(position) {
    this.method(position);
  }
});

Effect.Event = Class.create(Effect.Base, {
  initialize: function() {
    this.start(Object.extend({duration: 0}, arguments[0] || { }));
  },
  update: Prototype.emptyFunction
});

Effect.Opacity = Class.create(Effect.Base, {
  initialize: function(element) {
    this.element = $(element);
    if (!this.element) throw(Effect._elementDoesNotExistError);
    // make this work on IE on elements without 'layout'
    if (Prototype.Browser.IE && (!this.element.currentStyle.hasLayout))
      this.element.setStyle({zoom: 1});
    var options = Object.extend({
      from: this.element.getOpacity() || 0.0,
      to:   1.0
    }, arguments[1] || { });
    this.start(options);
  },
  update: function(position) {
    this.element.setOpacity(position);
  }
});

Effect.Move = Class.create(Effect.Base, {
  initialize: function(element) {
    this.element = $(element);
    if (!this.element) throw(Effect._elementDoesNotExistError);
    var options = Object.extend({
      x:    0,
      y:    0,
      mode: 'relative'
    }, arguments[1] || { });
    this.start(options);
  },
  setup: function() {
    this.element.makePositioned();
    this.originalLeft = parseFloat(this.element.getStyle('left') || '0');
    this.originalTop  = parseFloat(this.element.getStyle('top')  || '0');
    if (this.options.mode == 'absolute') {
      this.options.x = this.options.x - this.originalLeft;
      this.options.y = this.options.y - this.originalTop;
    }
  },
  update: function(position) {
    this.element.setStyle({
      left: (this.options.x  * position + this.originalLeft).round() + 'px',
      top:  (this.options.y  * position + this.originalTop).round()  + 'px'
    });
  }
});

// for backwards compatibility
Effect.MoveBy = function(element, toTop, toLeft) {
  return new Effect.Move(element,
    Object.extend({x: toLeft, y: toTop}, arguments[3] || { }));
};

Effect.Scale = Class.create(Effect.Base, {
  initialize: function(element, percent) {
    this.element = $(element);
    if (!this.element) throw(Effect._elementDoesNotExistError);
    var options = Object.extend({
      scaleX: true,
      scaleY: true,
      scaleContent: true,
      scaleFromCenter: false,
      scaleMode: 'box',        // 'box' or 'contents' or { } with provided values
      scaleFrom: 100.0,
      scaleTo:   percent
    }, arguments[2] || { });
    this.start(options);
  },
  setup: function() {
    this.restoreAfterFinish = this.options.restoreAfterFinish || false;
    this.elementPositioning = this.element.getStyle('position');

    this.originalStyle = { };
    ['top','left','width','height','fontSize'].each( function(k) {
      this.originalStyle[k] = this.element.style[k];
    }.bind(this));

    this.originalTop  = this.element.offsetTop;
    this.originalLeft = this.element.offsetLeft;

    var fontSize = this.element.getStyle('font-size') || '100%';
    ['em','px','%','pt'].each( function(fontSizeType) {
      if (fontSize.indexOf(fontSizeType)>0) {
        this.fontSize     = parseFloat(fontSize);
        this.fontSizeType = fontSizeType;
      }
    }.bind(this));

    this.factor = (this.options.scaleTo - this.options.scaleFrom)/100;

    this.dims = null;
    if (this.options.scaleMode=='box')
      this.dims = [this.element.offsetHeight, this.element.offsetWidth];
    if (/^content/.test(this.options.scaleMode))
      this.dims = [this.element.scrollHeight, this.element.scrollWidth];
    if (!this.dims)
      this.dims = [this.options.scaleMode.originalHeight,
                   this.options.scaleMode.originalWidth];
  },
  update: function(position) {
    var currentScale = (this.options.scaleFrom/100.0) + (this.factor * position);
    if (this.options.scaleContent && this.fontSize)
      this.element.setStyle({fontSize: this.fontSize * currentScale + this.fontSizeType});
    this.setDimensions(this.dims[0] * currentScale, this.dims[1] * currentScale);
  },
  finish: function(position) {
    if (this.restoreAfterFinish) this.element.setStyle(this.originalStyle);
  },
  setDimensions: function(height, width) {
    var d = { };
    if (this.options.scaleX) d.width = width.round() + 'px';
    if (this.options.scaleY) d.height = height.round() + 'px';
    if (this.options.scaleFromCenter) {
      var topd  = (height - this.dims[0])/2;
      var leftd = (width  - this.dims[1])/2;
      if (this.elementPositioning == 'absolute') {
        if (this.options.scaleY) d.top = this.originalTop-topd + 'px';
        if (this.options.scaleX) d.left = this.originalLeft-leftd + 'px';
      } else {
        if (this.options.scaleY) d.top = -topd + 'px';
        if (this.options.scaleX) d.left = -leftd + 'px';
      }
    }
    this.element.setStyle(d);
  }
});

Effect.Highlight = Class.create(Effect.Base, {
  initialize: function(element) {
    this.element = $(element);
    if (!this.element) throw(Effect._elementDoesNotExistError);
    var options = Object.extend({startcolor: '#ffff99'}, arguments[1] || { });
    this.start(options);
  },
  setup: function() {
    // Prevent executing on elements not in the layout flow
    if (this.element.getStyle('display')=='none') {this.cancel();return;}
    // Disable background image during the effect
    this.oldStyle = { };
    if (!this.options.keepBackgroundImage) {
      this.oldStyle.backgroundImage = this.element.getStyle('background-image');
      this.element.setStyle({backgroundImage: 'none'});
    }
    if (!this.options.endcolor)
      this.options.endcolor = this.element.getStyle('background-color').parseColor('#ffffff');
    if (!this.options.restorecolor)
      this.options.restorecolor = this.element.getStyle('background-color');
    // init color calculations
    this._base  = $R(0,2).map(function(i){return parseInt(this.options.startcolor.slice(i*2+1,i*2+3),16)}.bind(this));
    this._delta = $R(0,2).map(function(i){return parseInt(this.options.endcolor.slice(i*2+1,i*2+3),16)-this._base[i]}.bind(this));
  },
  update: function(position) {
    this.element.setStyle({backgroundColor: $R(0,2).inject('#',function(m,v,i){
      return m+((this._base[i]+(this._delta[i]*position)).round().toColorPart());}.bind(this))});
  },
  finish: function() {
    this.element.setStyle(Object.extend(this.oldStyle, {
      backgroundColor: this.options.restorecolor
    }));
  }
});

Effect.ScrollTo = function(element) {
  var options = arguments[1] || { },
  scrollOffsets = document.viewport.getScrollOffsets(),
  elementOffsets = $(element).cumulativeOffset();

  if (options.offset) elementOffsets[1] += options.offset;

  return new Effect.Tween(null,
    scrollOffsets.top,
    elementOffsets[1],
    options,
    function(p){scrollTo(scrollOffsets.left, p.round());}
  );
};

/* ------------- combination effects ------------- */

Effect.Fade = function(element) {
  element = $(element);
  var oldOpacity = element.getInlineOpacity();
  var options = Object.extend({
    from: element.getOpacity() || 1.0,
    to:   0.0,
    afterFinishInternal: function(effect) {
      if (effect.options.to!=0) return;
      effect.element.hide().setStyle({opacity: oldOpacity});
    }
  }, arguments[1] || { });
  return new Effect.Opacity(element,options);
};

Effect.Appear = function(element) {
  element = $(element);
  var options = Object.extend({
  from: (element.getStyle('display') == 'none' ? 0.0 : element.getOpacity() || 0.0),
  to:   1.0,
  // force Safari to render floated elements properly
  afterFinishInternal: function(effect) {
    effect.element.forceRerendering();
  },
  beforeSetup: function(effect) {
    effect.element.setOpacity(effect.options.from).show();
  }}, arguments[1] || { });
  return new Effect.Opacity(element,options);
};

Effect.Puff = function(element) {
  element = $(element);
  var oldStyle = {
    opacity: element.getInlineOpacity(),
    position: element.getStyle('position'),
    top:  element.style.top,
    left: element.style.left,
    width: element.style.width,
    height: element.style.height
  };
  return new Effect.Parallel(
   [ new Effect.Scale(element, 200,
      {sync: true, scaleFromCenter: true, scaleContent: true, restoreAfterFinish: true}),
     new Effect.Opacity(element, {sync: true, to: 0.0} ) ],
     Object.extend({duration: 1.0,
      beforeSetupInternal: function(effect) {
        Position.absolutize(effect.effects[0].element);
      },
      afterFinishInternal: function(effect) {
         effect.effects[0].element.hide().setStyle(oldStyle);}
     }, arguments[1] || { })
   );
};

Effect.BlindUp = function(element) {
  element = $(element);
  element.makeClipping();
  return new Effect.Scale(element, 0,
    Object.extend({scaleContent: false,
      scaleX: false,
      restoreAfterFinish: true,
      afterFinishInternal: function(effect) {
        effect.element.hide().undoClipping();
      }
    }, arguments[1] || { })
  );
};

Effect.BlindDown = function(element) {
  element = $(element);
  var elementDimensions = element.getDimensions();
  return new Effect.Scale(element, 100, Object.extend({
    scaleContent: false,
    scaleX: false,
    scaleFrom: 0,
    scaleMode: {originalHeight: elementDimensions.height, originalWidth: elementDimensions.width},
    restoreAfterFinish: true,
    afterSetup: function(effect) {
      effect.element.makeClipping().setStyle({height: '0px'}).show();
    },
    afterFinishInternal: function(effect) {
      effect.element.undoClipping();
    }
  }, arguments[1] || { }));
};

Effect.SwitchOff = function(element) {
  element = $(element);
  var oldOpacity = element.getInlineOpacity();
  return new Effect.Appear(element, Object.extend({
    duration: 0.4,
    from: 0,
    transition: Effect.Transitions.flicker,
    afterFinishInternal: function(effect) {
      new Effect.Scale(effect.element, 1, {
        duration: 0.3, scaleFromCenter: true,
        scaleX: false, scaleContent: false, restoreAfterFinish: true,
        beforeSetup: function(effect) {
          effect.element.makePositioned().makeClipping();
        },
        afterFinishInternal: function(effect) {
          effect.element.hide().undoClipping().undoPositioned().setStyle({opacity: oldOpacity});
        }
      });
    }
  }, arguments[1] || { }));
};

Effect.DropOut = function(element) {
  element = $(element);
  var oldStyle = {
    top: element.getStyle('top'),
    left: element.getStyle('left'),
    opacity: element.getInlineOpacity()};
  return new Effect.Parallel(
    [ new Effect.Move(element, {x: 0, y: 100, sync: true}),
      new Effect.Opacity(element, {sync: true, to: 0.0}) ],
    Object.extend(
      {duration: 0.5,
        beforeSetup: function(effect) {
          effect.effects[0].element.makePositioned();
        },
        afterFinishInternal: function(effect) {
          effect.effects[0].element.hide().undoPositioned().setStyle(oldStyle);
        }
      }, arguments[1] || { }));
};

Effect.Shake = function(element) {
  element = $(element);
  var options = Object.extend({
    distance: 20,
    duration: 0.5
  }, arguments[1] || {});
  var distance = parseFloat(options.distance);
  var split = parseFloat(options.duration) / 10.0;
  var oldStyle = {
    top: element.getStyle('top'),
    left: element.getStyle('left')};
    return new Effect.Move(element,
      {x:  distance, y: 0, duration: split, afterFinishInternal: function(effect) {
    new Effect.Move(effect.element,
      {x: -distance*2, y: 0, duration: split*2,  afterFinishInternal: function(effect) {
    new Effect.Move(effect.element,
      {x:  distance*2, y: 0, duration: split*2,  afterFinishInternal: function(effect) {
    new Effect.Move(effect.element,
      {x: -distance*2, y: 0, duration: split*2,  afterFinishInternal: function(effect) {
    new Effect.Move(effect.element,
      {x:  distance*2, y: 0, duration: split*2,  afterFinishInternal: function(effect) {
    new Effect.Move(effect.element,
      {x: -distance, y: 0, duration: split, afterFinishInternal: function(effect) {
        effect.element.undoPositioned().setStyle(oldStyle);
  }});}});}});}});}});}});
};

Effect.SlideDown = function(element) {
  element = $(element).cleanWhitespace();
  // SlideDown need to have the content of the element wrapped in a container element with fixed height!
  var oldInnerBottom = element.down().getStyle('bottom');
  var elementDimensions = element.getDimensions();
  return new Effect.Scale(element, 100, Object.extend({
    scaleContent: false,
    scaleX: false,
    scaleFrom: window.opera ? 0 : 1,
    scaleMode: {originalHeight: elementDimensions.height, originalWidth: elementDimensions.width},
    restoreAfterFinish: true,
    afterSetup: function(effect) {
      effect.element.makePositioned();
      effect.element.down().makePositioned();
      if (window.opera) effect.element.setStyle({top: ''});
      effect.element.makeClipping().setStyle({height: '0px'}).show();
    },
    afterUpdateInternal: function(effect) {
      effect.element.down().setStyle({bottom:
        (effect.dims[0] - effect.element.clientHeight) + 'px'});
    },
    afterFinishInternal: function(effect) {
      effect.element.undoClipping().undoPositioned();
      effect.element.down().undoPositioned().setStyle({bottom: oldInnerBottom});}
    }, arguments[1] || { })
  );
};

Effect.SlideUp = function(element) {
  element = $(element).cleanWhitespace();
  var oldInnerBottom = element.down().getStyle('bottom');
  var elementDimensions = element.getDimensions();
  return new Effect.Scale(element, window.opera ? 0 : 1,
   Object.extend({scaleContent: false,
    scaleX: false,
    scaleMode: 'box',
    scaleFrom: 100,
    scaleMode: {originalHeight: elementDimensions.height, originalWidth: elementDimensions.width},
    restoreAfterFinish: true,
    afterSetup: function(effect) {
      effect.element.makePositioned();
      effect.element.down().makePositioned();
      if (window.opera) effect.element.setStyle({top: ''});
      effect.element.makeClipping().show();
    },
    afterUpdateInternal: function(effect) {
      effect.element.down().setStyle({bottom:
        (effect.dims[0] - effect.element.clientHeight) + 'px'});
    },
    afterFinishInternal: function(effect) {
      effect.element.hide().undoClipping().undoPositioned();
      effect.element.down().undoPositioned().setStyle({bottom: oldInnerBottom});
    }
   }, arguments[1] || { })
  );
};

// Bug in opera makes the TD containing this element expand for a instance after finish
Effect.Squish = function(element) {
  return new Effect.Scale(element, window.opera ? 1 : 0, {
    restoreAfterFinish: true,
    beforeSetup: function(effect) {
      effect.element.makeClipping();
    },
    afterFinishInternal: function(effect) {
      effect.element.hide().undoClipping();
    }
  });
};

Effect.Grow = function(element) {
  element = $(element);
  var options = Object.extend({
    direction: 'center',
    moveTransition: Effect.Transitions.sinoidal,
    scaleTransition: Effect.Transitions.sinoidal,
    opacityTransition: Effect.Transitions.full
  }, arguments[1] || { });
  var oldStyle = {
    top: element.style.top,
    left: element.style.left,
    height: element.style.height,
    width: element.style.width,
    opacity: element.getInlineOpacity()};

  var dims = element.getDimensions();
  var initialMoveX, initialMoveY;
  var moveX, moveY;

  switch (options.direction) {
    case 'top-left':
      initialMoveX = initialMoveY = moveX = moveY = 0;
      break;
    case 'top-right':
      initialMoveX = dims.width;
      initialMoveY = moveY = 0;
      moveX = -dims.width;
      break;
    case 'bottom-left':
      initialMoveX = moveX = 0;
      initialMoveY = dims.height;
      moveY = -dims.height;
      break;
    case 'bottom-right':
      initialMoveX = dims.width;
      initialMoveY = dims.height;
      moveX = -dims.width;
      moveY = -dims.height;
      break;
    case 'center':
      initialMoveX = dims.width / 2;
      initialMoveY = dims.height / 2;
      moveX = -dims.width / 2;
      moveY = -dims.height / 2;
      break;
  }

  return new Effect.Move(element, {
    x: initialMoveX,
    y: initialMoveY,
    duration: 0.01,
    beforeSetup: function(effect) {
      effect.element.hide().makeClipping().makePositioned();
    },
    afterFinishInternal: function(effect) {
      new Effect.Parallel(
        [ new Effect.Opacity(effect.element, {sync: true, to: 1.0, from: 0.0, transition: options.opacityTransition}),
          new Effect.Move(effect.element, {x: moveX, y: moveY, sync: true, transition: options.moveTransition}),
          new Effect.Scale(effect.element, 100, {
            scaleMode: {originalHeight: dims.height, originalWidth: dims.width},
            sync: true, scaleFrom: window.opera ? 1 : 0, transition: options.scaleTransition, restoreAfterFinish: true})
        ], Object.extend({
             beforeSetup: function(effect) {
               effect.effects[0].element.setStyle({height: '0px'}).show();
             },
             afterFinishInternal: function(effect) {
               effect.effects[0].element.undoClipping().undoPositioned().setStyle(oldStyle);
             }
           }, options)
      );
    }
  });
};

Effect.Shrink = function(element) {
  element = $(element);
  var options = Object.extend({
    direction: 'center',
    moveTransition: Effect.Transitions.sinoidal,
    scaleTransition: Effect.Transitions.sinoidal,
    opacityTransition: Effect.Transitions.none
  }, arguments[1] || { });
  var oldStyle = {
    top: element.style.top,
    left: element.style.left,
    height: element.style.height,
    width: element.style.width,
    opacity: element.getInlineOpacity()};

  var dims = element.getDimensions();
  var moveX, moveY;

  switch (options.direction) {
    case 'top-left':
      moveX = moveY = 0;
      break;
    case 'top-right':
      moveX = dims.width;
      moveY = 0;
      break;
    case 'bottom-left':
      moveX = 0;
      moveY = dims.height;
      break;
    case 'bottom-right':
      moveX = dims.width;
      moveY = dims.height;
      break;
    case 'center':
      moveX = dims.width / 2;
      moveY = dims.height / 2;
      break;
  }

  return new Effect.Parallel(
    [ new Effect.Opacity(element, {sync: true, to: 0.0, from: 1.0, transition: options.opacityTransition}),
      new Effect.Scale(element, window.opera ? 1 : 0, {sync: true, transition: options.scaleTransition, restoreAfterFinish: true}),
      new Effect.Move(element, {x: moveX, y: moveY, sync: true, transition: options.moveTransition})
    ], Object.extend({
         beforeStartInternal: function(effect) {
           effect.effects[0].element.makePositioned().makeClipping();
         },
         afterFinishInternal: function(effect) {
           effect.effects[0].element.hide().undoClipping().undoPositioned().setStyle(oldStyle);}
       }, options)
  );
};

Effect.Pulsate = function(element) {
  element = $(element);
  var options    = arguments[1] || { },
    oldOpacity = element.getInlineOpacity(),
    transition = options.transition || Effect.Transitions.linear,
    reverser   = function(pos){
      return 1 - transition((-Math.cos((pos*(options.pulses||5)*2)*Math.PI)/2) + .5);
    };

  return new Effect.Opacity(element,
    Object.extend(Object.extend({duration: 2.0, from: 0,
      afterFinishInternal: function(effect) {effect.element.setStyle({opacity: oldOpacity});}
    }, options), {transition: reverser}));
};

Effect.Fold = function(element) {
  element = $(element);
  var oldStyle = {
    top: element.style.top,
    left: element.style.left,
    width: element.style.width,
    height: element.style.height};
  element.makeClipping();
  return new Effect.Scale(element, 5, Object.extend({
    scaleContent: false,
    scaleX: false,
    afterFinishInternal: function(effect) {
    new Effect.Scale(element, 1, {
      scaleContent: false,
      scaleY: false,
      afterFinishInternal: function(effect) {
        effect.element.hide().undoClipping().setStyle(oldStyle);
      }});
  }}, arguments[1] || { }));
};

Effect.Morph = Class.create(Effect.Base, {
  initialize: function(element) {
    this.element = $(element);
    if (!this.element) throw(Effect._elementDoesNotExistError);
    var options = Object.extend({
      style: { }
    }, arguments[1] || { });

    if (!Object.isString(options.style)) this.style = $H(options.style);
    else {
      if (options.style.include(':'))
        this.style = options.style.parseStyle();
      else {
        this.element.addClassName(options.style);
        this.style = $H(this.element.getStyles());
        this.element.removeClassName(options.style);
        var css = this.element.getStyles();
        this.style = this.style.reject(function(style) {
          return style.value == css[style.key];
        });
        options.afterFinishInternal = function(effect) {
          effect.element.addClassName(effect.options.style);
          effect.transforms.each(function(transform) {
            effect.element.style[transform.style] = '';
          });
        };
      }
    }
    this.start(options);
  },

  setup: function(){
    function parseColor(color){
      if (!color || ['rgba(0, 0, 0, 0)','transparent'].include(color)) color = '#ffffff';
      color = color.parseColor();
      return $R(0,2).map(function(i){
        return parseInt( color.slice(i*2+1,i*2+3), 16 );
      });
    }
    this.transforms = this.style.map(function(pair){
      var property = pair[0], value = pair[1], unit = null;

      if (value.parseColor('#zzzzzz') != '#zzzzzz') {
        value = value.parseColor();
        unit  = 'color';
      } else if (property == 'opacity') {
        value = parseFloat(value);
        if (Prototype.Browser.IE && (!this.element.currentStyle.hasLayout))
          this.element.setStyle({zoom: 1});
      } else if (Element.CSS_LENGTH.test(value)) {
          var components = value.match(/^([\+\-]?[0-9\.]+)(.*)$/);
          value = parseFloat(components[1]);
          unit = (components.length == 3) ? components[2] : null;
      }

      var originalValue = this.element.getStyle(property);
      return {
        style: property.camelize(),
        originalValue: unit=='color' ? parseColor(originalValue) : parseFloat(originalValue || 0),
        targetValue: unit=='color' ? parseColor(value) : value,
        unit: unit
      };
    }.bind(this)).reject(function(transform){
      return (
        (transform.originalValue == transform.targetValue) ||
        (
          transform.unit != 'color' &&
          (isNaN(transform.originalValue) || isNaN(transform.targetValue))
        )
      );
    });
  },
  update: function(position) {
    var style = { }, transform, i = this.transforms.length;
    while(i--)
      style[(transform = this.transforms[i]).style] =
        transform.unit=='color' ? '#'+
          (Math.round(transform.originalValue[0]+
            (transform.targetValue[0]-transform.originalValue[0])*position)).toColorPart() +
          (Math.round(transform.originalValue[1]+
            (transform.targetValue[1]-transform.originalValue[1])*position)).toColorPart() +
          (Math.round(transform.originalValue[2]+
            (transform.targetValue[2]-transform.originalValue[2])*position)).toColorPart() :
        (transform.originalValue +
          (transform.targetValue - transform.originalValue) * position).toFixed(3) +
            (transform.unit === null ? '' : transform.unit);
    this.element.setStyle(style, true);
  }
});

Effect.Transform = Class.create({
  initialize: function(tracks){
    this.tracks  = [];
    this.options = arguments[1] || { };
    this.addTracks(tracks);
  },
  addTracks: function(tracks){
    tracks.each(function(track){
      track = $H(track);
      var data = track.values().first();
      this.tracks.push($H({
        ids:     track.keys().first(),
        effect:  Effect.Morph,
        options: {style: data}
      }));
    }.bind(this));
    return this;
  },
  play: function(){
    return new Effect.Parallel(
      this.tracks.map(function(track){
        var ids = track.get('ids'), effect = track.get('effect'), options = track.get('options');
        var elements = [$(ids) || $$(ids)].flatten();
        return elements.map(function(e){return new effect(e, Object.extend({sync:true}, options))});
      }).flatten(),
      this.options
    );
  }
});

Element.CSS_PROPERTIES = $w(
  'backgroundColor backgroundPosition borderBottomColor borderBottomStyle ' +
  'borderBottomWidth borderLeftColor borderLeftStyle borderLeftWidth ' +
  'borderRightColor borderRightStyle borderRightWidth borderSpacing ' +
  'borderTopColor borderTopStyle borderTopWidth bottom clip color ' +
  'fontSize fontWeight height left letterSpacing lineHeight ' +
  'marginBottom marginLeft marginRight marginTop markerOffset maxHeight '+
  'maxWidth minHeight minWidth opacity outlineColor outlineOffset ' +
  'outlineWidth paddingBottom paddingLeft paddingRight paddingTop ' +
  'right textIndent top width wordSpacing zIndex');

Element.CSS_LENGTH = /^(([\+\-]?[0-9\.]+)(em|ex|px|in|cm|mm|pt|pc|\%))|0$/;

String.__parseStyleElement = document.createElement('div');
String.prototype.parseStyle = function(){
  var style, styleRules = $H();
  if (Prototype.Browser.WebKit)
    style = new Element('div',{style:this}).style;
  else {
    String.__parseStyleElement.innerHTML = '<div style="' + this + '"></div>';
    style = String.__parseStyleElement.childNodes[0].style;
  }

  Element.CSS_PROPERTIES.each(function(property){
    if (style[property]) styleRules.set(property, style[property]);
  });

  if (Prototype.Browser.IE && this.include('opacity'))
    styleRules.set('opacity', this.match(/opacity:\s*((?:0|1)?(?:\.\d*)?)/)[1]);

  return styleRules;
};

if (document.defaultView && document.defaultView.getComputedStyle) {
  Element.getStyles = function(element) {
    var css = document.defaultView.getComputedStyle($(element), null);
    return Element.CSS_PROPERTIES.inject({ }, function(styles, property) {
      styles[property] = css[property];
      return styles;
    });
  };
} else {
  Element.getStyles = function(element) {
    element = $(element);
    var css = element.currentStyle, styles;
    styles = Element.CSS_PROPERTIES.inject({ }, function(results, property) {
      results[property] = css[property];
      return results;
    });
    if (!styles.opacity) styles.opacity = element.getOpacity();
    return styles;
  };
}

Effect.Methods = {
  morph: function(element, style) {
    element = $(element);
    new Effect.Morph(element, Object.extend({style: style}, arguments[2] || { }));
    return element;
  },
  visualEffect: function(element, effect, options) {
    element = $(element);
    var s = effect.dasherize().camelize(), klass = s.charAt(0).toUpperCase() + s.substring(1);
    new Effect[klass](element, options);
    return element;
  },
  highlight: function(element, options) {
    element = $(element);
    new Effect.Highlight(element, options);
    return element;
  }
};

$w('fade appear grow shrink fold blindUp blindDown slideUp slideDown '+
  'pulsate shake puff squish switchOff dropOut').each(
  function(effect) {
    Effect.Methods[effect] = function(element, options){
      element = $(element);
      Effect[effect.charAt(0).toUpperCase() + effect.substring(1)](element, options);
      return element;
    };
  }
);

$w('getInlineOpacity forceRerendering setContentZoom collectTextNodes collectTextNodesIgnoreClass getStyles').each(
  function(f) {Effect.Methods[f] = Element[f];}
);

Element.addMethods(Effect.Methods);// script.aculo.us builder.js v1.8.0, Tue Nov 06 15:01:40 +0300 2007

// Copyright (c) 2005-2007 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
//
// script.aculo.us is freely distributable under the terms of an MIT-style license.
// For details, see the script.aculo.us web site: http://script.aculo.us/

var Builder = {
  NODEMAP: {
    AREA: 'map',
    CAPTION: 'table',
    COL: 'table',
    COLGROUP: 'table',
    LEGEND: 'fieldset',
    OPTGROUP: 'select',
    OPTION: 'select',
    PARAM: 'object',
    TBODY: 'table',
    TD: 'table',
    TFOOT: 'table',
    TH: 'table',
    THEAD: 'table',
    TR: 'table'
  },
  // note: For Firefox < 1.5, OPTION and OPTGROUP tags are currently broken,
  //       due to a Firefox bug
  node: function(elementName) {
    elementName = elementName.toUpperCase();
    
    // try innerHTML approach
    var parentTag = this.NODEMAP[elementName] || 'div';
    var parentElement = document.createElement(parentTag);
    try { // prevent IE "feature": http://dev.rubyonrails.org/ticket/2707
      parentElement.innerHTML = "<" + elementName + "></" + elementName + ">";
    } catch(e) {}
    var element = parentElement.firstChild || null;
      
    // see if browser added wrapping tags
    if(element && (element.tagName.toUpperCase() != elementName))
      element = element.getElementsByTagName(elementName)[0];
    
    // fallback to createElement approach
    if(!element) element = document.createElement(elementName);
    
    // abort if nothing could be created
    if(!element) return;

    // attributes (or text)
    if(arguments[1])
      if(this._isStringOrNumber(arguments[1]) ||
        (arguments[1] instanceof Array) ||
        arguments[1].tagName) {
          this._children(element, arguments[1]);
        } else {
          var attrs = this._attributes(arguments[1]);
          if(attrs.length) {
            try { // prevent IE "feature": http://dev.rubyonrails.org/ticket/2707
              parentElement.innerHTML = "<" +elementName + " " +
                attrs + "></" + elementName + ">";
            } catch(e) {}
            element = parentElement.firstChild || null;
            // workaround firefox 1.0.X bug
            if(!element) {
              element = document.createElement(elementName);
              for(attr in arguments[1]) 
                element[attr == 'class' ? 'className' : attr] = arguments[1][attr];
            }
            if(element.tagName.toUpperCase() != elementName)
              element = parentElement.getElementsByTagName(elementName)[0];
          }
        } 

    // text, or array of children
    if(arguments[2])
      this._children(element, arguments[2]);

     return element;
  },
  _text: function(text) {
     return document.createTextNode(text);
  },

  ATTR_MAP: {
    'className': 'class',
    'htmlFor': 'for'
  },

  _attributes: function(attributes) {
    var attrs = [];
    for(attribute in attributes)
      attrs.push((attribute in this.ATTR_MAP ? this.ATTR_MAP[attribute] : attribute) +
          '="' + attributes[attribute].toString().escapeHTML().gsub(/"/,'&quot;') + '"');
    return attrs.join(" ");
  },
  _children: function(element, children) {
    if(children.tagName) {
      element.appendChild(children);
      return;
    }
    if(typeof children=='object') { // array can hold nodes and text
      children.flatten().each( function(e) {
        if(typeof e=='object')
          element.appendChild(e);
        else
          if(Builder._isStringOrNumber(e))
            element.appendChild(Builder._text(e));
      });
    } else
      if(Builder._isStringOrNumber(children))
        element.appendChild(Builder._text(children));
  },
  _isStringOrNumber: function(param) {
    return(typeof param=='string' || typeof param=='number');
  },
  build: function(html) {
    var element = this.node('div');
    $(element).update(html.strip());
    return element.down();
  },
  dump: function(scope) { 
    if(typeof scope != 'object' && typeof scope != 'function') scope = window; //global scope 
  
    var tags = ("A ABBR ACRONYM ADDRESS APPLET AREA B BASE BASEFONT BDO BIG BLOCKQUOTE BODY " +
      "BR BUTTON CAPTION CENTER CITE CODE COL COLGROUP DD DEL DFN DIR DIV DL DT EM FIELDSET " +
      "FONT FORM FRAME FRAMESET H1 H2 H3 H4 H5 H6 HEAD HR HTML I IFRAME IMG INPUT INS ISINDEX "+
      "KBD LABEL LEGEND LI LINK MAP MENU META NOFRAMES NOSCRIPT OBJECT OL OPTGROUP OPTION P "+
      "PARAM PRE Q S SAMP SCRIPT SELECT SMALL SPAN STRIKE STRONG STYLE SUB SUP TABLE TBODY TD "+
      "TEXTAREA TFOOT TH THEAD TITLE TR TT U UL VAR").split(/\s+/);
  
    tags.each( function(tag){ 
      scope[tag] = function() { 
        return Builder.node.apply(Builder, [tag].concat($A(arguments)));  
      } 
    });
  }
};// Tooltip Object
var Tooltip = Class.create();
Tooltip.prototype = {
  initialize: function(el, options) {
    this.el = $(el);
    this.initialized = false;
    this.setOptions(options);
    
    // Event handlers
    this.showEvent = this.show.bindAsEventListener(this);
    this.hideEvent = this.hide.bindAsEventListener(this);
    this.updateEvent = this.update.bindAsEventListener(this);
    Event.observe(this.el, "mouseover", this.showEvent );
    Event.observe(this.el, "mouseout", this.hideEvent );
    
    // Removing title from DOM element to avoid showing it
    this.content = this.el.title;
    this.el.title = "";
  },
  setOptions: function(options) {
    this.options = {
      backgroundColor: '#999', // Default background color
      borderColor: '#666', // Default border color
      textColor: '', // Default text color (use CSS value)
      textShadowColor: '', // Default text shadow color (use CSS value)
      maxWidth: 250,	// Default tooltip width
      align: "left", // Default align
      delay: 250, // Default delay before tooltip appears in ms
      mouseFollow: false, // Tooltips follows the mouse moving
      opacity: .75, // Default tooltips opacity
      appearDuration: .25, // Default appear duration in sec
      hideDuration: .25 // Default disappear duration in sec
    };
    Object.extend(this.options, options || {});
  },
  show: function(e) {
    this.xCord = Event.pointerX(e);
    this.yCord = Event.pointerY(e);
    if(!this.initialized)
      this.timeout = window.setTimeout(this.appear.bind(this), this.options.delay);
  },
  hide: function(e) {
    if(this.initialized) {
      this.appearingFX.cancel();
      if(this.options.mouseFollow)
        Event.stopObserving(this.el, "mousemove", this.updateEvent);
      new Effect.Fade(this.tooltip, {duration: this.options.hideDuration, afterFinish: function() {Element.remove(this.tooltip)}.bind(this)});
    }
    this._clearTimeout(this.timeout);
    
    this.initialized = false;
  },
  update: function(e){
    this.xCord = Event.pointerX(e);
    this.yCord = Event.pointerY(e);
    this.setup();
  },
  appear: function() {
    // Building tooltip container
    this.tooltip = Builder.node("div", {className: "tooltip", style: "display: none;"}, [
      Builder.node("div", {className:"xtop"}, [
        Builder.node("div", {className:"xb1", style:"background-color:" + this.options.borderColor + ";"}),
        Builder.node("div", {className:"xb2", style: "background-color:" + this.options.backgroundColor + "; border-color:" + this.options.borderColor + ";"}),
        Builder.node("div", {className:"xb3", style: "background-color:" + this.options.backgroundColor + "; border-color:" + this.options.borderColor + ";"}),
        Builder.node("div", {className:"xb4", style: "background-color:" + this.options.backgroundColor + "; border-color:" + this.options.borderColor + ";"})
      ]),
      Builder.node("div", {className: "xboxcontent", style: "background-color:" + this.options.backgroundColor + 
        "; border-color:" + this.options.borderColor + 
        ((this.options.textColor != '') ? "; color:" + this.options.textColor : "") + 
        ((this.options.textShadowColor != '') ? "; text-shadow:2px 2px 0" + this.options.textShadowColor + ";" : "")}, this.content), 
      Builder.node("div", {className:"xbottom"}, [
        Builder.node("div", {className:"xb4", style: "background-color:" + this.options.backgroundColor + "; border-color:" + this.options.borderColor + ";"}),
        Builder.node("div", {className:"xb3", style: "background-color:" + this.options.backgroundColor + "; border-color:" + this.options.borderColor + ";"}),
        Builder.node("div", {className:"xb2", style: "background-color:" + this.options.backgroundColor + "; border-color:" + this.options.borderColor + ";"}),
        Builder.node("div", {className:"xb1", style:"background-color:" + this.options.borderColor + ";"})
      ]),
    ]);
    document.body.insertBefore(this.tooltip, document.body.childNodes[0]);
    
    Element.extend(this.tooltip); // IE needs element to be manually extended
    this.options.width = this.tooltip.getWidth();
    this.tooltip.style.width = this.options.width + 'px'; // IE7 needs width to be defined
    
    this.setup();
    
    if(this.options.mouseFollow)
      Event.observe(this.el, "mousemove", this.updateEvent);
    
    this.initialized = true;
    this.appearingFX = new Effect.Appear(this.tooltip, {duration: this.options.appearDuration, to: this.options.opacity});
  },
  setup: function(){
    // If content width is more then allowed max width, set width to max
    if(this.options.width > this.options.maxWidth) {
      this.options.width = this.options.maxWidth;
      this.tooltip.style.width = this.options.width + 'px';
    }
    
    // Tooltip doesn't fit the current document dimensions
    if(this.xCord + this.options.width >= Element.getWidth(document.body)) {
      this.options.align = "right";
      this.xCord = this.xCord - this.options.width + 20;
    }
    
    this.tooltip.style.left = this.xCord - 7 + "px";
    this.tooltip.style.top = this.yCord + 12 + "px";
  },
  stop: function() {
    this.hide();
    Event.stopObserving(this.el, "mouseover", this.showEvent );
    Event.stopObserving(this.el, "mouseout", this.hideEvent );          
    Event.stopObserving(this.el, "mousemove", this.updateEvent);
  },        
  _clearTimeout: function(timer) {
    clearTimeout(timer);
    clearInterval(timer);
    return null;
  }
};//modified by Quake (added tooltip integration and remove some unused code)

/**
 * modified by badqiu (badqiu(a)gmail.com)
 * blog: http://badqiu.javaeye.com
 * Project Home: http://code.google.com/p/rapid-validation/
 */

/*
 * Really easy field validation with Prototype
 * http://tetlaw.id.au/view/blog/really-easy-field-validation-with-prototype
 * Andrew Tetlaw
 * Version 1.5.3 (2006-07-15)
 * 
 * Copyright (c) 2006 Andrew Tetlaw
 * http://www.opensource.org/licenses/mit-license.php
 */

var ValidationDefaultOptions = function(){};
ValidationDefaultOptions.prototype = {
  onSubmit : true, //是否监听form的submit事件
  stopOnFirst : false, //表单验证时停留在第一个验证的地方,不继续验证下去
  immediate : true, //是否实时检查数据的合法性
  focusOnError : true, //是否出错时将光标指针移到出错的输入框上
  useTitles : false, //是否使用input的title属性作为出错时的提示信息
  onFormValidate : function(result, form) {},//Form验证时的回调函数
  onElementValidate : function(result, elm) {} //某个input验证时的回调函数
};

var ValidatorDefaultOptions = function(){};
ValidatorDefaultOptions.prototype = {
  ignoreEmptyValue : true, //是否忽略空值
  depends : [] //相关依赖项
};
 
Validator = Class.create();

ValidationUtils = {
  getReferenceForm : function(elm) {
    while(elm && elm.tagName != 'BODY') {
      if(elm.tagName == 'FORM') return elm;
      elm = elm.parentNode;
    }
    return null;
  },
  getInputValue : function(elm) {
    var elm = $(elm);
    if(elm.type.toLowerCase() == 'file') {
      return elm.value;
    }else {
      return $F(elm);
    }
  },
  getElmID : function(elm) {
    return elm.id ? elm.id : elm.name;
  },
  format : function(str,args) {
    args = args || [];
    ValidationUtils.assert(args.constructor == Array,"ValidationUtils.format() arguement 'args' must is Array");
    var result = str;
    for (var i = 0; i < args.length; i++){
      result = result.replace(/%s/, args[i]);	
    }
    return result;
  },
  // 通过classname传递的参数必须通过'-'分隔各个参数
  // 返回值包含一个参数singleArgument,例:validate-pattern-/[a-c]/gi,singleArgument值为/[a-c]/gi
  getArgumentsByClassName : function(prefix,className) {
    if(!className || !prefix)
      return [];
    var pattern = new RegExp(prefix+'-(\\S+)');
    var matchs = className.match(pattern);
    if(!matchs)
      return [];
    var results = [];
    results.singleArgument = matchs[1];
    var args =  matchs[1].split('-');
    for(var i = 0; i < args.length; i++) {
      if(args[i] == '') {
        if(i+1 < args.length) args[i+1] = '-'+args[i+1];
      }else{
        results.push(args[i]);
      }
    }
    return results;
  },
  assert : function(condition,message) {
    var errorMessage = message || ("assert failed error,condition="+condition);
    if (!condition) {
      alert(errorMessage);
      throw new Error(errorMessage);
    }else {
      return condition;
    }
  },
  isDate : function(v,dateFormat) {
    var MONTH = "MM";
    var DAY = "dd";
    var YEAR = "yyyy";
    var regex = '^'+dateFormat.replace(YEAR,'\\d{4}').replace(MONTH,'\\d{2}').replace(DAY,'\\d{2}')+'$';
    if(!new RegExp(regex).test(v)) return false;

    var year = v.substr(dateFormat.indexOf(YEAR),4);
    var month = v.substr(dateFormat.indexOf(MONTH),2);
    var day = v.substr(dateFormat.indexOf(DAY),2);
		
    var d = new Date(ValidationUtils.format('%s/%s/%s',[year,month,day]));
    return ( parseInt(month, 10) == (1+d.getMonth()) ) && 
      (parseInt(day, 10) == d.getDate()) && 
      (parseInt(year, 10) == d.getFullYear() );		
  },
  getLanguage : function() {
    var lang = null;
    if (typeof navigator.userLanguage == 'undefined')
      lang = navigator.language.toLowerCase();
    else
      lang = navigator.userLanguage.toLowerCase();
    return lang;
  },
  getMessageSource : function() {
    var lang = ValidationUtils.getLanguage();
    var messageSource = Validator.messageSource['zh-cn'];
    if(Validator.messageSource[lang]) {
      messageSource = Validator.messageSource[lang];
    }
    return messageSource;
  }
};

Validator.messages = {
  'validation-failed' : '验证失败.',
  'required' : '请填写值.',
  'validate-number' : '请填写有效的数字.',
  'validate-digits' : '请填写数字.',
  'validate-alpha' : '请填写英文字母.',
  'validate-alphanum' : '请填写英文字母或是数字,其它字符是不允许的.',
  'validate-email' : '请填写有效的邮件地址,只能使用a-z A-Z 0-9 _ . -\',如 username@example.com。 ',
  'validate-url' : '请填写有效的URL地址.',
  'validate-currency-dollar' : 'Please enter a valid $ amount. For example $100.00 .',
  'validate-one-required' : '在上面选项至少选择一个.',
  'validate-integer' : '请填写正确的整数',
  'validate-pattern' : '填写的值不匹配',
  'validate-ip' : '请填写正确的IP地址',
  'min-value' : '最小值为%s',
  'max-value' : '最大值为%s',
  'min-length' : '最小长度为%s,当前长度为%s.',
  'max-length' : '最大长度为%s,当前长度为%s.',
  'int-range' : '填写值应该为 %s 至 %s 的整数',
  'float-range' : '填写值应该为 %s 至 %s 的数字',
  'length-range' : '填写值的长度应该在 %s 至 %s 之间,当前长度为%s',
  'equals' : '两次填写不一致,请重新填写',
  'less-than' : '请填写小于前面的值',
  'great-than' : '请填写大于前面的值',
  'validate-date' : '请填写有效的日期,格式为 %s. 例如:%s.',
  'validate-file' : function(v,elm,args,metadata) {
    return ValidationUtils.format("文件类型应该为[%s]其中之一",[args.join(',')]);
  },
  'validate-id-number' : '请填写合法的身份证号码',
  'validate-chinese' : '请填写中文',
  'validate-phone' : '请填写正确的电话号码,如:021-87654321,当前长度为%s.',
  'validate-mobile-phone' : '请填写正确的手机号码,当前长度为%s.',
  'validate-zip' : '请填写有效的邮政编码',
  'validate-qq' : '请填写有效的QQ号码.',
  'validate-richeditor' : '请填写值.',
  'validate-combobox' : '请填写值或选取前面的选项.',
  'max-tag' : '请填写1-%s个Tag.'
};

Validator.bad_words=[/fuck/i,/shit/i,/QQ群/i,/MSN群/i,/求(助|救)/,/(跪|裸)求/,/急急|我很急|紧急寻求/,/在线等/,/救(命|我|急|救)/,/急(!|！)/,/(高手|高人|大虾|大侠|达人|前辈|快来|没有人)(指点|解答|帮忙|帮帮|指教|赐教|提点|不吝赐教|到哪)/,/(我是|帮帮)(新手|小弟|菜鸟)/,/帮帮忙/, /万分火急/, /我顶|顶上去|好贴要顶|帮顶|看看先|顶一下/,/有*没有人(回答|帮忙|关注|知道啊|回复)/,/教主|装B|装13/,/关注(ing|中)/i,/收藏先/,/先收藏/,/收藏了/,/图书兼职作者/,/谢谢楼主/,/传智播客/,/FineReport/i];

Validator.prototype = {
  initialize : function(className, test, options) {
    this.options = Object.extend(new ValidatorDefaultOptions(), options || {});
    this._test = test ? test : function(v,elm){return true;};
    this._error = Validator.messages[className] ? Validator.messages[className] : Validator.messages['validation-failed'];
    this.className = className;
    this._dependsTest = this._dependsTest.bind(this);
    this._getDependError = this._getDependError.bind(this);
  },
  _dependsTest : function(v,elm) {
    if(this.options.depends && this.options.depends.length > 0) {
      var dependsResult = $A(this.options.depends).all(function(depend){
        return Validation.get(depend).test(v,elm);
      });
      return dependsResult;
    }
    return true;
  },
  test : function(v, elm) {
    if(!this._dependsTest(v,elm))
      return false;
    if(!elm) elm = {};
    return (this.options.ignoreEmptyValue && ((v == null) || (v.length == 0))) || this._test(v,elm,ValidationUtils.getArgumentsByClassName(this.className,elm.className),this);
  },
  _getDependError : function(v,elm,useTitle) {
    var dependError = null;
    $A(this.options.depends).any(function(depend){
      var validation = Validation.get(depend);
      if(!validation.test(v,elm))  {
        dependError = validation.error(v,elm,useTitle);
        return true;
      }
      return false;
    });
    return dependError;
  }, 
  error : function(v,elm,useTitle) {
    var dependError = this._getDependError(v,elm,useTitle);
    if(dependError != null) return dependError;

    var args  = ValidationUtils.getArgumentsByClassName(this.className,elm.className);
    var error = this._error;
    if(typeof error == 'string') {
      if(v) args.push(v.length);
      error = ValidationUtils.format(this._error,args);
    }else if(typeof error == 'function') {
      error = error(v,elm,args,this);
    }else {
      alert('property "_error" must type of string or function');
    }
    if(!useTitle) useTitle = elm.className.indexOf('useTitle') >= 0;
    return useTitle ? ((elm && elm.title) ? elm.title : error) : error;
  }
};

var Validation = Class.create();

Validation.prototype = {
  initialize : function(form, options){
    this.options = Object.extend(new ValidationDefaultOptions(), options || {});
    this.form = $(form);
    var formId =  ValidationUtils.getElmID($(form));
    Validation.validations[formId] = this;
    if(this.options.onSubmit) Event.observe(this.form,'submit',this.onSubmit.bind(this),false);
    if(this.options.immediate) {
      var useTitles = this.options.useTitles;
      var callback = this.options.onElementValidate;
      Form.getElements(this.form).each(function(input) { // Thanks Mike!
        Event.observe(input, 'blur', function(ev) {Validation.validateElement(Event.element(ev),{useTitle : useTitles, onElementValidate : callback});});
      });
    }
  },
  onSubmit :  function(ev){
    if(this.form.spinner && this.form.spinner.visible()) {
      Event.stop(ev);
    }else if(!this.validate()) {
      Event.stop(ev);
    }else {
      Event.observe(this.form,'submit',function(e){Event.stop(e);}.bind(this.form),false);
      if(this.form.spinner) {
        this.form.spinner.show();
      }else{
        this.form.spinner = $(document.createElement("img"));
        this.form.spinner.className = 'spinner_img';
        this.form.spinner.src = 'http://www.iteye.com/images/spinner.gif';
        Element.insert(this.form.select('input.submit')[0], {"after" : this.form.spinner});
      }
    }
  },
  validate : function() {
    var result = false;
    var useTitles = this.options.useTitles;
    var callback = this.options.onElementValidate;
    if(this.options.stopOnFirst) {
      result = Form.getElements(this.form).all(function(elm) {return Validation.validateElement(elm,{useTitle : useTitles, onElementValidate : callback});});
    } else {
      result = Form.getElements(this.form).collect(function(elm) {return Validation.validateElement(elm,{useTitle : useTitles, onElementValidate : callback});}).all();
    }
    if(!result && this.options.focusOnError) {
      var first = Form.getElements(this.form).findAll(function(elm){return $(elm).hasClassName('validation-failed');}).first();
      try{
        if(first.select) first.select();
        first.focus();
      }catch(e){}
    }
    this.options.onFormValidate(result, this.form);
    return result;
  }
};

Object.extend(Validation, {
  validateElement : function(elm, options){
    options = Object.extend({
      useTitle : false,
      onElementValidate : function(result, elm) {}
    }, options || {});
    elm = $(elm);
    var cn = elm.classNames();
    return cn.all(function(value) {
      var test = Validation.test(value,elm,options.useTitle);
      options.onElementValidate(test, elm);
      return test;
    });
  },
  showErrorMsg : function(name,elm,errorMsg) {           
    if(!elm.tooltip) elm.tooltip = new Tooltip(elm, {backgroundColor: "#FC9", borderColor: "#C96", textColor: "#000", textShadowColor: "#FFF"});
    elm.tooltip.content = errorMsg;
    elm.removeClassName('validation-passed');
    elm.addClassName('validation-failed');
  },
  showErrorMsgByValidator : function(name,elm,useTitle) {
    Validation.showErrorMsg(name,elm,Validation.get(name).error(ValidationUtils.getInputValue(elm),elm,useTitle));
  },
  hideErrorMsg : function(name,elm) {		
    if(elm.tooltip) {
      elm.tooltip.stop();
      elm.tooltip = false;            
    }
    elm.removeClassName('validation-failed');
    elm.addClassName('validation-passed');
  },
  test : function(name, elm, useTitle) {
    var v = Validation.get(name);
    if(!v.test(ValidationUtils.getInputValue(elm),elm)) {
      Validation.showErrorMsgByValidator(name,elm,useTitle);
      return false;
    } else {
      Validation.hideErrorMsg(name,elm);
      return true;
    }
  },
  getAdvice : function(name, elm) {
    return Try.these(
    function(){return $('advice-' + name + '-' + ValidationUtils.getElmID(elm));},
    function(){return $('advice-' + ValidationUtils.getElmID(elm));}
  );
  },
  add : function(className, test, options) {
    var nv = {};
    var testFun = test;
    if(test instanceof RegExp)
      testFun = function(v,elm,args,metadata){return test.test(v);};
    nv[className] = new Validator(className, testFun, options);
    Object.extend(Validation.methods, nv);
  },
  addAllThese : function(validators) {
    $A(validators).each(function(value) {
      Validation.add(value[0], value[1], (value.length > 2 ? value[2] : {}));
    });
  },
  get : function(name) {
    var resultMethodName;
    for(var methodName in Validation.methods) {
      if(name == methodName) {
        resultMethodName = methodName;
        break;
      }
      if(name.indexOf(methodName) >= 0) {
        resultMethodName = methodName;
      }
    }
    return Validation.methods[resultMethodName] ? Validation.methods[resultMethodName] : new Validator();
  },
  $ : function(formId) {
    return Validation.validations[formId];
  },
  methods : {},
  validations : {}
});

Validation.addAllThese([
  ['required', function(v) {
      return !((v == null) || (v.length == 0) || /^[\s|\u3000]+$/.test(v));
    },{ignoreEmptyValue:false}],
  ['validate-number', function(v) {
      return (!isNaN(v) && !/^\s+$/.test(v));
    }],
  ['validate-digits', function(v) {
      return !/[^\d]/.test(v);
    }],
  ['validate-alphanum', function(v) {
      return !/\W/.test(v);
    }],
  ['validate-one-required', function (v,elm) {
      var p = elm.parentNode;
      var options = p.getElementsByTagName('INPUT');
      return $A(options).any(function(elm) {
        return $F(elm);
      });
    },{ignoreEmptyValue : false}],
			
  ['validate-digits',/^[\d]+$/],		
  ['validate-alphanum',/^[a-zA-Z0-9]+$/],		
  ['validate-alpha',/^[a-zA-Z]+$/],

  ['validate-email',/^[\w.+-]+@(?:[-a-z0-9]+\.)+[a-z]{2,4}$/i],
  ['validate-url',/^(http|https|ftp):\/\/(([A-Z0-9][A-Z0-9_-]*)(\.[A-Z0-9][A-Z0-9_-]*)+)(:(\d+))?\/?/i],
  // [$]1[##][,###]+[.##]
  // [$]1###+[.##]
  // [$]0.##
  // [$].##
  ['validate-currency-dollar',/^\$?\-?([1-9]{1}[0-9]{0,2}(\,[0-9]{3})*(\.[0-9]{0,2})?|[1-9]{1}\d*(\.[0-9]{0,2})?|0(\.[0-9]{0,2})?|(\.[0-9]{1,2})?)$/]
]);

//custom validate start

Validation.addAllThese([
  /**
   * Usage : equals-$otherInputId
   * Example : equals-username or equals-email etc..
   */
  ['equals', function(v,elm,args,metadata) {
      return $F(args[0]) == v;
    },{ignoreEmptyValue:false}],
  /**
   * Usage : less-than-$otherInputId
   */
  ['less-than', function(v,elm,args,metadata) {
      if(Validation.get('validate-number').test(v) && Validation.get('validate-number').test($F(args[0])))
        return parseFloat(v) < parseFloat($F(args[0]));
      return v < $F(args[0]);
    }],
  /**
   * Usage : great-than-$otherInputId
   */
  ['great-than', function(v,elm,args,metadata) {
      if(Validation.get('validate-number').test(v) && Validation.get('validate-number').test($F(args[0])))
        return parseFloat(v) > parseFloat($F(args[0]));
      return v > $F(args[0]);
    }],
  /*
   * Usage: min-length-$number
   * Example: min-length-10
   */
  ['min-length',function(v,elm,args,metadata) {
      return v.length >= parseInt(args[0]);
    }],
  /*
   * Usage: max-length-$number
   * Example: max-length-10
   */
  ['max-length',function(v,elm,args,metadata) {
      return v.length <= parseInt(args[0]);
    }],
  /*
   * Usage: validate-file-$type1-$type2-$typeX
   * Example: validate-file-png-jpg-jpeg
   */
  ['validate-file',function(v,elm,args,metadata) {
      return $A(args).any(function(extentionName) {
        return new RegExp('\\.'+extentionName+'$','i').test(v);
      });
    }],
  /*
   * Usage: float-range-$minValue-$maxValue
   * Example: -2.1 to 3 = float-range--2.1-3
   */
  ['float-range',function(v,elm,args,metadata) {
      return (parseFloat(v) >= parseFloat(args[0]) && parseFloat(v) <= parseFloat(args[1]));
    },{depends : ['validate-number']}],
  /*
   * Usage: int-range-$minValue-$maxValue
   * Example: -10 to 20 = int-range--10-20
   */
  ['int-range',function(v,elm,args,metadata) {
      return (parseInt(v) >= parseInt(args[0]) && parseInt(v) <= parseInt(args[1]));
    },{depends : ['validate-integer']}],
  /*
   * Usage: length-range-$minLength-$maxLength
   * Example: 10 to 20 = length-range-10-20
   */
  ['length-range',function(v,elm,args,metadata) {
      return (v.length >= parseInt(args[0]) && v.length <= parseInt(args[1]));
    }],
  /*
   * Usage: max-value-$number
   * Example: max-value-10
   */
  ['max-value',function(v,elm,args,metadata) {
      return parseFloat(v) <= parseFloat(args[0]);
    },{depends : ['validate-number']}],
  /*
   * Usage: min-value-$number
   * Example: min-value-10
   */
  ['min-value',function(v,elm,args,metadata) {
      return parseFloat(v) >= parseFloat(args[0]);
    },{depends : ['validate-number']}],
  /*
   * Usage: validate-pattern-$RegExp
   * Example: <input id='sex' class='validate-pattern-/^[fm]$/i'>
   */
  ['validate-pattern',function(v,elm,args,metadata) {
      return eval('('+args.singleArgument+'.test(v))');
    }],
  /*
   * Usage: validate-ajax-$url
   * Example: <input id='email' class='validate-ajax-http://localhost:8080/validate-email.jsp'>
   */
  ['validate-ajax',function(v,elm,args,metadata) {
      var form = ValidationUtils.getReferenceForm(elm);
      var params = (form ? Form.serialize(form) : Form.Element.serialize(elm));
      params += ValidationUtils.format("&what=%s&value=%s",[elm.name,encodeURIComponent(v)]);
      var request = new Ajax.Request(args.singleArgument,{
        parameters : params,
        asynchronous : false,
        method : "post"
      });
		
      var responseText = request.transport.responseText;
      if("" == responseText.strip()) return true;
      metadata._error = responseText;
      return false;
    }],
  /*
   * Usage: validate-date-$dateFormat or validate-date($dateFormat default is yyyy-MM-dd)
   * Example: validate-date-yyyy/MM/dd
   */
  ['validate-date', function(v,elm,args,metadata) {
      var dateFormat = args.singleArgument || 'yyyy-MM-dd';
      metadata._error = ValidationUtils.format(Validator.messages[metadata.className],[dateFormat,dateFormat.replace('yyyy','2006').replace('MM','03').replace('dd','12')]);
      return ValidationUtils.isDate(v,dateFormat);
    }],	
  ['validate-integer',/^[-+]?[1-9]\d*$|^0$/],
  ['validate-ip',/^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/],
  //中国相关验证开始
  ['validate-id-number',function(v,elm,args,metadata) {
      if(!(/^\d{17}(\d|x)$/i.test(v) || /^\d{15}$/i.test(v))) return false;
      var provinceCode = parseInt(v.substr(0,2));
      if((provinceCode < 11) || (provinceCode > 91)) return false;
      var forTestDate = v.length == 18 ? v : v.substr(0,6)+"19"+v.substr(6,15);
      var birthday = forTestDate.substr(6,8);
      if(!ValidationUtils.isDate(birthday,'yyyyMMdd')) return false;
      if(v.length == 18) {
        v = v.replace(/x$/i,"a");
        var verifyCode = 0;
        for(var i = 17;i >= 0;i--)   
          verifyCode += (Math.pow(2,i) % 11) * parseInt(v.charAt(17 - i),11);
        if(verifyCode % 11 != 1) return false;
      }
      return true;
    }],
  ['validate-chinese',/^[\u4e00-\u9fa5]+$/],
  ['validate-phone',/^((0[1-9]{3})?(0[12][0-9])?[-])?\d{6,8}$/],
  ['validate-mobile-phone',/(^0?[1][35][0-9]{9}$)/],
  ['validate-zip',/^[1-9]\d{5}$/],
  ['validate-qq',/^[1-9]\d{4,8}$/],
	
  //javaeye added
  ['validate-richeditor', function(v, elm) {
      if(editor.mode == "rich") {
        v = tinyMCE.activeEditor.getContent().stripTags().replace(/&nbsp;/g,' ');	
      }
      return !((v == null) || (v.length == 0) || /^[\s|\u3000]+$/.test(v));			
    },{ignoreEmptyValue:false}],
	
  ['bad-words', function(v) {
      for (var i=0; i < Validator.bad_words.length; i++) {
        var m = v.match(Validator.bad_words[i]);
        if (m) {
          alert("ITeye发贴提示\n\nITeye网站不允许您使用 \"" + m[0] + "\" 这样的词汇发贴！\n\n您很可能违反了网站规则，请仔细阅读并遵守网站规则，否则将被扣分删贴。\n\n");
          return false;
        }}
      return true;		
    }],
  ['validate-combobox', function(v, elm, args) {
      var v = $F(args[0]);
      return Validation.get('required').test(elm.value) || Validation.get('required').test(v);
    },{ignoreEmptyValue:false}],

  /*
   * Usage: max-tag-$number
   * Example: max-tag-3
   */
  ['max-tag', function(v, elm, args){
    return v.replace(/^\s+|\s+$/g, '').split(/,\s*|，\s*/).length <= parseInt(args[0]);
  }]
]);
String.prototype.trim = function() {return this.replace(/^\s+|\s+$/g, '');};

function multiple_upload_attachment(ele, max_size) {
  Event.observe(ele, 'change', function() {
    addAttachmentDiv(ele, max_size);
  });
  if (multiple_upload_attachment_counter >= max_size) ele.disabled = true;
}

var attachment_template = new Template('<table><tr><th>文件:</th><td><span>#{filename}</span><span class="more"><a href="#" class="delete">删除</a></span><div class="clear"></div></td></tr><tr><th>描述:</th><td><textarea name="attachments[][remark]" cols="" rows=""></textarea></td></tr></table>');

function addAttachmentDiv(ele, max_size) {
  if (!$A(['jpg','jpeg','bmp','png','gif','rar','zip', 'tar', 'gz', 'jar', 'war', 'bz2', '7z']).any(function(extName){return new RegExp('\\.'+extName+'$','i').test(ele.value);})){
    alert("如果您上传图片，请上传JPG、BMP、PNG或者GIF格式的图片\n如果您上传附件，请先压缩再上传");
      return false;
  }

  multiple_upload_attachment_counter++;
  var div = attachment_template.evaluate({filename: ele.value}); 
  var new_input = new Element("input", {type: "file", name: ele.name, id: ele.id, disabled: multiple_upload_attachment_counter >= max_size});

  Event.observe(new_input, 'change', function() {
    addAttachmentDiv(new_input, max_size);
  });
  $('groups_upload_button').insert({before : div});
  ele.insert({after: new_input});
  ele.id = ele.id + multiple_upload_attachment_counter;
  ele.name = "attachments[][uploaded_data]";
  div = $('groups_upload_button').previous();
  div.down('.delete').observe('click', function(event) {
    removeAttachmentDiv(this);
  });
  div.appendChild(ele.hide());
}

function removeAttachmentDiv(link) {
  multiple_upload_attachment_counter--;
  link.up('table').remove();
  $('attachment_upload').disabled = false;
}

function multiple_upload_picture(ele, max_size, tags) {
  Event.observe(ele, 'change', function() {
    addPictureDiv(ele, max_size, tags);
  });
  if (multiple_upload_picture_counter >= max_size) ele.disabled = true;
}

function addPictureDiv(ele, max_size, tags) {
  if (!$A(['jpg','jpeg','bmp','png','gif']).any(function(extName){return new RegExp('\\.'+extName+'$','i').test(ele.value);})){
    alert("您上传的图片格式不支持，请您上传JPG、BMP、PNG或者GIF格式的图片");
    return false;
  }
  
  multiple_upload_picture_counter++;
  //fix for opera
  var file_name = ele.value;
  try {file_name = ele.value.match(/(.*)[\/\\]([^\/\\]+)\.\w+$/)[2];} catch(e) {} 
  var tag_select = "";
  if(tags.length > 0) {
    var tag_select = "<select onchange='Element.previous($(this)).value = this.value;'><option value=''>选择已有标签</option>";
    for (var i = 0; i < tags.length; i++)
      tag_select += "<option value='" + tags[i] + "'>" + tags[i] + "</option>";
    tag_select += "</select>";
  }
  var div = new Element("div").update("<ul><li>文件: " + ele.value + " <a href='#' onclick='removePictureDiv(this, \"" + ele.id + "\");return false;'>删除</a></li><li>名称: <input type='text' name='pictures[][name]' value='" + file_name + "' size='50' class='text'/></li><li>描述: <textarea name='pictures[][description]' style='width:400px;height:80px;'></textarea></li></ul>");
  var new_input = new Element("input", {type: "file", name: ele.name, id: ele.id, disabled: multiple_upload_picture_counter >= max_size});
  
  Event.observe(new_input, 'change', function() {
    addPictureDiv(new_input, max_size, tags);
  });
  ele.insert({after : div});
  div.insert({after : new_input});
  ele.id = ele.id + multiple_upload_picture_counter;
  ele.name = "pictures[][uploaded_data]";
  div.appendChild(ele.hide().remove());
}

function removePictureDiv(link, eleId) {
  multiple_upload_picture_counter--;
  link.parentNode.parentNode.parentNode.remove();
  $(eleId).disabled = false;
}

function move_messages(dest) {
  $('dest').value = dest;
  $('messages_form').submit();
}

function digg(type, id, bury) {
    var url = '/' + type + (bury ? '/bury' : '/digg');
    var small_button = !!$('d' + id);
    if(small_button) $('d'+id).innerHTML = "<h3><img src='/images/spinner.gif'/><br/><span>...</span></h3>";

    if($('digg_bottom')){
      $$('#digg_bottom div').each(function(div){div.innerHTML="<img src='/images/spinner.gif'/>";});
      new Ajax.Request(url,{asynchronous:true,evalScripts:true,parameters:{"id":id, "bottom":true, small_button:small_button}});
    }else{
      new Ajax.Request(url,{asynchronous:true,evalScripts:true,parameters:{"id":id, small_button:small_button}});
    }
}

function bury_blog(blog_id) {
    $('b' + blog_id).innerHTML = "<h3><img src='/images/spinner.gif'/><br/><span>...</span></h3>";
    if($('digg_bottom') != undefined) {
        $$('#digg_bottom div').each(function(div){div.innerHTML="<img src='/images/spinner.gif'/>";});
        new Ajax.Request('/blogs/bury',{asynchronous:true,evalScripts:true,parameters:{"id":blog_id, "bottom":true}});
    }else{
        new Ajax.Request('/blogs/bury',{asynchronous:true,evalScripts:true,parameters:{"id":blog_id}});
    }
}

function fix_image_size(images, maxW) {
  if(images.length > 0) {
    Event.observe(window,'load',function() {
      images.each(function(img) {
        var w = img.width;
        var h = img.height;
        if (w > maxW) {
          var f = 1-((w - maxW) / w);
          img.title = "点击查看原始大小图片";
          img.addClassName("magplus");
          img.onclick = function() {window.open(this.src);};
          img.width = w * f;
          img.height = h * f;
        }
      });
    });
  }
}

function highlight_suggested_tags(tags) {
    if ($('suggested_tags_panel') !== null) {
        $('suggested_tags_panel').select('a').each( function(el) {
            var exist_tag = tags.find(function(tag){
                return tag.match(new RegExp('^' + el.text + '$', 'i'));
            });
            if (exist_tag) {
                el.addClassName('added');
            } else {
                el.removeClassName('added');
            }
        });
    }
}

function extract_tag_list(value) {
    var tags = value.trim().split(/,\s*|，\s*/).without('');
    highlight_suggested_tags(tags);
    tag_list = tags.map(function(tag) { 
        return "<span>" + tag + "</span>";
    }).join('');
    if (!tag_list.empty()) {
        tag_list = '当前标签：' + tag_list;
    }
    if ($("current_tags_panel") !== null) {
        $("current_tags_panel").update(tag_list);
    }
}

function observe_tag_list(id) {
    extract_tag_list($(id).value);
    new Form.Element.Observer(
        id,
        0.2,
        function(el, value) {
            extract_tag_list(value);
        }
    )
}

function trigger_suggested_tag() {
  var tag_list_input = this.parentNode.previous("input");
  var current_value = tag_list_input.value.trim();
  if (this.hasClassName('added')) {
    var tag_regex = new RegExp('^' + this.text + '$', 'i')
    tag_list_input.value = current_value.split(/,\s*|，\s*/).findAll(function(tag){
      tag = tag.trim();
      if (tag !== '' && !tag_regex.match(tag)) {
        return true;
      }
    }).join(', ');
    this.removeClassName('added');
  } else {
    if(current_value !== "" && current_value[current_value.length-1] !== ","){
      tag_list_input.value += ", ";
    }
    tag_list_input.value += this.innerHTML;
    this.addClassName('added');
  }
  Validation.validateElement($(tag_list_input));
}

function check_category_list(checkboxes, category_list){
  checkboxes.each(function(element){
    element.checked = category_list.split(',').any(function(name){
      return name.trim().toLowerCase() == this;
    }, element.value.toLowerCase());
  });
}

document.observe("dom:loaded",function(){
    $$("div.quick_menu").each(function(ele) {
        Event.observe(ele.previous(), 'mouseover', function(e) {
            $$("div.quick_menu").invoke('hide');
            ele.style.left = ele.previous().positionedOffset()[0] + "px";
            ele.show();
        });
        Event.observe(ele.previous(), 'mouseout', function(e) {
            if(!Position.within(ele,Event.pointerX(e),Event.pointerY(e) + 5))
                ele.hide();
        });
        Event.observe(ele, 'mouseout', function(e) {
            if(!Position.within(ele,Event.pointerX(e),Event.pointerY(e)))
                ele.hide();
        });
    });
    $$("div.nav_side > ul > li:not(.select)").each(function(ele) {
      Event.observe(ele, 'mouseover', function(e) {
        Event.findElement(e, 'LI').addClassName('select');
      });
      Event.observe(ele, 'mouseout', function(e) {
        Event.findElement(e, 'LI').removeClassName('select');
      });
    });

    $$('#blog_category_checkbox input[type=checkbox]').each(function(element){
      element.observe('change', function(ev){
        var current_value = $('blog_category_list').value.trim();
        if(this.checked) {
          if(current_value == '' || current_value[current_value.length -1] == ',')
            $('blog_category_list').value += this.value;
          else
            $('blog_category_list').value += (','+this.value);
        } else {
          $('blog_category_list').value = current_value.split(',').reject(function(name){
            return name.trim() == this;
          }, this.value).join(',');
        }
      });
    });

});

//IE6 background image cache fix
try {document.execCommand('BackgroundImageCache', false, true);} catch(e) {}



//resume

function add_work_experience(button, content){
  var new_id = new Date().getTime();
  $(button).up().insert({before: content.replace(/new_work_experiences/g, new_id)});
}

function add_project_experience(link, content){
  var new_id = new Date().getTime();
  $(link).up().insert({before: content.replace(/new_project_experiences/g, new_id)});
}

function add_education(button, content){
  var new_id = new Date().getTime();
  $$('.editing .educations')[0].insert({bottom: content.replace(/new_educations/g, new_id)});
}

function add_training(button, content){
  var new_id = new Date().getTime();
  $$('.editing .trainings')[0].insert({bottom: content.replace(/new_trainings/g, new_id)});
}

function add_certificate(button, content){
  var new_id = new Date().getTime();
  $$('.editing .certificates')[0].insert({bottom: content.replace(/new_certificates/g, new_id)});
}

function add_skill(button, content){
  $$('.editing .skills')[0].insert({bottom: content});
}

function add_language(button, content){
  $$('.editing .languages')[0].insert({bottom: content});
}

document.observe("dom:loaded",function(){
  $$('#my_resume .control .expand').each(function(el){
    el.hide();
  });
  document.observe('click', function(event){

    if(el = event.findElement('#my_resume .control .expand')){
      el.up('h1').next('.view_con').show();
      el.previous('.contract').show();
      el.hide();
      event.stop();
    } else if(el = event.findElement('#my_resume .control .contract')){
      el.up('h1').next('.view_con').hide();
      el.next('.expand').show();
      el.hide();
      event.stop();
    } else if (el = event.findElement('#my_resume .control .edit')){
      if($('profile_invalid_flag') && !(/myresume\/edit$/.test(el['href']))){
        alert("个人资料不完整,请先完善个人资料");
      }else{
        new Ajax.Request(el['href'], {
          method: 'get',
          onSuccess: function(res){
            el.up('.view').replace(res.responseText);
          }
        });
      }
      event.stop();
    } else if (el = event.findElement('.work_exp > .del_work a.del')){
      if(confirm("确定要删除该工作经验吗？")){
        if(/\#$/.test(el['href'])){
          el.up('.work_exp').remove();
        } else {
          new Ajax.Request(el['href'], {
            method: 'delete',
            onSuccess: function(res){
              el.up('.work_exp').remove();
            }
          });
        }
      }
      event.stop();
    } else if(el = event.findElement('.project_exp > .del_project a.del')) {
      if(confirm("确定要删除该项目经验吗？")){
        if(/\#$/.test(el['href'])){
          el.up('.project_exp').remove();
        } else {
          new Ajax.Request(el['href'], {
            method: 'delete',
            onSuccess: function(res){
              el.up('.project_exp').remove();
            }
          });
        }
      }
      event.stop();
    } else if(el = event.findElement('.edu_exp a.del')){
      if(confirm("确定要删除吗？")){
        if(/\#$/.test(el['href'])){
          el.up('.edu_exp').remove();
        } else {
          new Ajax.Request(el['href'], {
            method: 'delete',
            onSuccess: function(res){
              el.up('.edu_exp').remove();
            }
          });
        }
      }
      event.stop();
    } else if (el = event.findElement('.skills a.del')){
      if(confirm("确定要删除该技能吗？")){
        if(/\#$/.test(el['href'])){
          el.up('li').remove();
        } else {
          var form = el.up('form');

          el.up('li').getElementsBySelector('.level input[type=radio]').each(function(radio){
            radio['name'] = 'destroied';
          });

          new Ajax.Request(el['href'], {
            method: 'delete',
            parameters: form.serialize(),
            onSuccess: function(){
              el.up('li').remove();
            }
          });
        }
      }
      event.stop();
    } else if (el = event.findElement('.languages a.del')) {
      if (confirm("确定要删除该语种吗?")) { 
        if(/\#$/.test(el['href'])){
          el.up('li').remove();
        }else{
          var form = el.up('form');

          el.up('li').getElementsBySelector('.level select').each(function(select_elem){
            select_elem['name'] = 'destroied';
          });

          new Ajax.Request(el['href'], {
            method: 'delete',
            parameters: form.serialize(),
            onSuccess: function(){
              el.up('li').remove();
            }
          });
        }
      }
      event.stop();
    }

  });

  var resume_submit_precess = function(event){
    if(form = event.findElement('#my_resume .editing form.remote')){
      form.request({
        onSuccess: function(res){
          var prev = form.up('.editing').previous();
          prev.insert({after: res.responseText});
          prev.next().down('.expand').hide();
          form.up('.editing').remove();
        }
      });
      event.stop();
    }
  };
  if(Prototype.Browser.IE){
    document.observe('focusin', function(event){
      var form = event.findElement("#my_resume form");
      if(form && !form.submit_bubbles_on_ie){
        form.submit_bubbles_on_ie = true;
        form.observe('submit', resume_submit_precess);
      }
    });
  }else{
    document.observe('submit', resume_submit_precess);
  }



  var resume_change_process = function(event){
    if(select = event.findElement('#my_resume .skills .title .skill_option_one')){
      var two_elem = select.next();
      var skill_title = '';
      if('手动添加' == select['value']){
        two_elem.replace('<input class="skill_option_two" name="skill_option_two" style="width:80px;" type="text">');
      }else{
        if(two_elem.nodeName.toUpperCase() == "SELECT"){
          two_elem.length = 0;
        }else{
          two_elem.replace(new Element('select', {'class':'skill_option_two', 'name': 'skill_option_two'}));
        }
        window.skill_options_two.get(select['value']).each(function(v){ select.next().appendChild(new Element('option', { 'value': v }).update(v)); });
        skill_title = window.skill_options_two.get(select['value'])[0];
      }
      select.up('.title').next('.level').getElementsBySelector('input').each(function(radio){
        radio.checked = false;
        radio['name'] = 'resume[skills][' + skill_title + ']';
      });
    }else if(select = event.findElement('#my_resume .skills .title .skill_option_two')){
      select.up('.title').next('.level').getElementsBySelector('input').each(function(radio){
        radio.checked = false;
        radio['name'] = 'resume[skills][' + select['value'] + ']';
      });
    }else if(select = event.findElement('#my_resume .languages .title select')){
      select.up('.title').next('.level').getElementsBySelector('select').each(function(select_elem){
        select_elem['name'] = 'resume[languages]['+ select['value'] +'][]';
      });
    }else if(select = (event.findElement('#my_resume .work_exp select.company_industry') || event.findElement('#company_industry_search'))){
      if(select['value'] == '其它'){
         var elem_str = "<input type='text' class='input_1 required' style='margin-left: 10px;' name='" + select['name'] + "' />"
         if(select['id'] == 'company_industry_search'){elem_str = "<input type='text' class='text' style='margin-left: 10px;' name='" + select['name'] + "' />"}
         select.insert({'after': elem_str});
         select['name'] = '';
      }else{
        var ci_text_elem = select.next('input');
        if(ci_text_elem){
          select['name'] = ci_text_elem['name'];
          select.next('input').remove();
        }
      }
    }else if(select = event.findElement('#skills_search')){
      if(select['value'] == '其它'){
        select.insert({'after': "<input type='text' class='text' style='margin-left:10px;' name='"+ select['name'] +"' />"})
        select['name'] = '';
      }else{
        var ci_text_elem = select.next('input');
        if(ci_text_elem){
          select['name'] = ci_text_elem['name'];
          select.next('input').remove();
        }
      }
    }
  };
  if(Prototype.Browser.IE){
    document.observe('focusin', function(event){
      var select = event.findElement("#my_resume select");
      if(select && !select.change_bubbles_on_ie){
        select.change_bubbles_on_ie = true;
        select.observe('change', resume_change_process);
      }
    });
  }else{
    document.observe('change', resume_change_process);
  }
});

function add_vote_option(btn, html_str){
  var new_id = new Date().getTime();
  $(btn).up().previous().insert({bottom: html_str.replace(/new_vote_option_id/g, new_id)});
}
function del_vote_option(vote_option){
  vote_option = $(vote_option);
  var tr1 = vote_option.up(1);
  var tr2 = tr1.next();
  var vote_option_url = vote_option.getAttribute('url');
  if(vote_option_url){
    if(confirm('慎用,如投票选项下有很多投票,会删除其所有投票,而增加服务器压力')){
    new Ajax.Request(vote_option_url, {
        method: 'delete',
        onSuccess: function(transport){ tr1.remove(); tr2.remove();}
        });
    }
  }else{
    tr1.remove();
    tr2.remove();
  }
}
/* JSON-P implementation for Prototype.js somewhat by Dan Dean (http://www.dandean.com)
 * 
 * *HEAVILY* based on Tobie Langel's version: http://gist.github.com/145466.
 * Might as well just call this an iteration.
 * 
 * This version introduces:
 * - Support for predefined callbacks (Necessary for OAuth signed requests, by @rboyce)
 * - Partial integration with Ajax.Responders (Thanks to @sr3d for the kick in this direction)
 * - Compatibility with Prototype 1.7 (Thanks to @soung3 for the bug report)
 * - Will not break if page lacks a <head> element
 *
 * See examples in README for usage
 *
 * VERSION 1.1.2
 *
 * new Ajax.JSONRequest(url, options);
 * - url (String): JSON-P endpoint url.
 * - options (Object): Configuration options for the request.
 */
Ajax.JSONRequest = Class.create(Ajax.Base, (function() {
  var id = 0, head = document.getElementsByTagName('head')[0] || document.body;
  return {
    initialize: function($super, url, options) {
      $super(options);
      this.options.url = url;
      this.options.callbackParamName = this.options.callbackParamName || 'callback';
      this.options.timeout = this.options.timeout || 10; // Default timeout: 10 seconds
      this.options.invokeImmediately = (!Object.isUndefined(this.options.invokeImmediately)) ? this.options.invokeImmediately : true ;
      
      if (!Object.isUndefined(this.options.parameters) && Object.isString(this.options.parameters)) {
        this.options.parameters = this.options.parameters.toQueryParams();
      }
      
      if (this.options.invokeImmediately) {
        this.request();
      }
    },
    
    /**
     *  Ajax.JSONRequest#_cleanup() -> undefined
     *  Cleans up after the request
     **/
    _cleanup: function() {
      if (this.timeout) {
        clearTimeout(this.timeout);
        this.timeout = null;
      }
      if (this.transport && Object.isElement(this.transport)) {
        this.transport.remove();
        this.transport = null;
      }
    },
  
    /**
     *  Ajax.JSONRequest#request() -> undefined
     *  Invokes the JSON-P request lifecycle
     **/
    request: function() {
      
      // Define local vars
      var response = new Ajax.JSONResponse(this);
      var key = this.options.callbackParamName,
        name = '_prototypeJSONPCallback_' + (id++),
        complete = function() {
          if (Object.isFunction(this.options.onComplete)) {
            this.options.onComplete.call(this, response);
          }
          Ajax.Responders.dispatch('onComplete', this, response);
        }.bind(this);
      
      // If the callback parameter is already defined, use that
      if (this.options.parameters[key] !== undefined) {
        name = this.options.parameters[key];
      }
      // Otherwise, add callback as a parameter
      else {
        this.options.parameters[key] = name;
      }
      
      // Build request URL
      this.options.parameters[key] = name;
      var url = this.options.url + ((this.options.url.include('?') ? '&' : '?') + Object.toQueryString(this.options.parameters));
      
      // Define callback function
      window[name] = function(json) {
        this._cleanup(); // Garbage collection
        window[name] = undefined;

        response.status = 200;
        response.statusText = "OK";
        response.setResponseContent(json);

        if (Object.isFunction(this.options.onSuccess)) {
          this.options.onSuccess.call(this, response);
        }
        Ajax.Responders.dispatch('onSuccess', this, response);

        complete();

      }.bind(this);
      
      this.transport = new Element('script', {type: 'text/javascript', src: url});
      
      if (Object.isFunction(this.options.onCreate)) {
        this.options.onCreate.call(this, response);
      }
      Ajax.Responders.dispatch('onCreate', this);
      
      head.appendChild(this.transport);

      this.timeout = setTimeout(function() {
        this._cleanup();
        window[name] = Prototype.emptyFunction;
        if (Object.isFunction(this.options.onFailure)) {
          response.status = 504;
          response.statusText = "Gateway Timeout";
          this.options.onFailure.call(this, response);
        }
        complete();
      }.bind(this), this.options.timeout * 1000);
    },
    toString: function() {return "[object Ajax.JSONRequest]";}
  };
})());

Ajax.JSONResponse = Class.create({
  initialize: function(request) {
    this.request = request;
  },
  request: undefined,
  status: 0,
  statusText: '',
  responseJSON: undefined,
  responseText: undefined,
  setResponseContent: function(json) {
    this.responseJSON = json;
    this.responseText = Object.toJSON(json);
  },
  getTransport: function() {
    if (this.request) return this.request.transport;
  },
  toString: function() {return "[object Ajax.JSONResponse]";}
});document.observe("dom:loaded", function() {
  (function(){
    function is_ie6(){
      return ((window.XMLHttpRequest == undefined) && (ActiveXObject != undefined));
    }

    var notifications;
    var unread_count = 0;
    var notifications_count = $('notifications_count');
    var notifications_menu = $('notifications_menu');
    var notifications_summary, notifications_detail;

    var Render = {
      summary_names : function(names) {
        if (names.length > 3) {
          return new Template('#{names}和另外#{other}人').evaluate({
            'names' : names.clone().splice(0,3).join('，'), 'other' : names.length - 3
          });
        } else {
          return names.join('，');
        }
      },

      template_blog_comment : function(notification) {
        this.render_comments_common_template(notification, 'blog', '回复了您的博客文章');
      },

      template_blog_comment_other : function(notification) {
        this.render_comments_common_template(notification, 'blog', '回复了您回复过的博客文章');
      },

      template_topic_post : function(notification) {
        this.render_comments_common_template(notification, 'topic', '回复了您的论坛主题', 'posts');
      },

      template_topic_post_other : function(notification) {
        this.render_comments_common_template(notification, 'topic', '回复了您回复过的论坛主题', 'posts');
      },

      template_group_topic_post : function(notification) {
        this.render_comments_common_template(notification, 'topic', '回复了您的群组主题', 'posts');
      },

      template_group_topic_post_other : function(notification) {
        this.render_comments_common_template(notification, 'topic', '回复了您回复过的群组主题', 'posts');
      },

      template_news_comment: function(notification) {
        this.render_comments_common_template(notification, 'news', '评论了您的资讯文章');
      },

      template_news_comment_other: function(notification) {
        this.render_comments_common_template(notification, 'news', '评论了您评论过的资讯文章');
      },

      template_essence_comment: function(notification) {
        this.render_comments_common_template(notification, 'essence', '评论了您的精华文章');
      },

      template_essence_comment_other: function(notification) {
        this.render_comments_common_template(notification, 'essence', '评论了您评论过的精华文章');
      },

      template_problem_solution: function(notification) {
        this.render_comments_common_template(notification, 'problem', '回答了您的问题', 'solutions');
      },

      template_problem_solution_other: function(notification) {
        this.render_comments_common_template(notification, 'problem', '回答了您回答过的问题', 'solutions');
      },

      template_problem_solution_follow: function(notification) {
        this.render_comments_common_template(notification, 'problem', '回答了您关注的问题', 'solutions');
      },

      template_problem_comment: function(notification) {
        this.render_comments_common_template(notification, 'problem', '评论了您的问题', 'comments');
      },

      template_problem_comment_follow: function(notification) {
        this.render_comments_common_template(notification, 'problem', '评论了您关注的问题', 'comments');
      },

      template_problem_follow: function(notification) {
        this.render_comments_common_template(notification, 'problem', '关注了您的问题', 'follows');
      },

      template_solution_comment: function(notification) {
        this.render_comments_common_template(notification, 'solution', '评论了您的回答', 'comments');
      },

      template_event_comment: function(notification) {
        this.render_comments_common_template(notification, 'event', '评论了您的活动', 'comments');
      },

      template_event_comment_other: function(notification) {
        this.render_comments_common_template(notification, 'event', '评论了您评论过的活动', 'comments');
      },

      template_event_comment_member: function(notification) {
        this.render_comments_common_template(notification, 'event', '评论了您参与的活动', 'comments');
      },

      template_page_comment: function(notification) {
        this.render_comments_common_template(notification, 'page', '评论了您的知识库', 'comments');
      },

      template_page_comment_other: function(notification) {
        this.render_comments_common_template(notification, 'page', '评论了您评论过的知识库', 'comments');
      },

      render_comments_common_template : function(notification, subject, title, comments) {
        comments = comments || 'comments';
        var names = notification[comments].map(function(comment) {return comment.user.name;}).uniq();
        var summary = this.summary_names(names) + title;

        var link = new Template(' <span class="subject_link"><a href="#{url}" target="_blank">#{title}</a></span>').evaluate({
          'summary' : summary, 'url' : notification[subject].url, 'title' : notification[subject].title
        });
        var detail_items = new Element('div', {'class' : 'detail_items'});
        notification[comments].each(function(comment, index) {
          if (index > 2) {
            var template = new Template('<div class="notification_detail_item clearfix" style="display:none;"><span class="left"><a href="#{user_url}" target="_blank">#{user_name}</a>：#{body} </span><span class="time">#{time}</span></div>')
          } else {
            var template = new Template('<div class="notification_detail_item clearfix"><span class="left"><a href="#{user_url}" target="_blank">#{user_name}</a>：#{body}</span> <span class="time">#{time}</span></div>')
          }
          detail_items.insert(template.evaluate({
            user_url : comment.user.url, user_name : comment.user.name, body : comment.body, time : comment.created_at
          }));
          if (comment.url) {
            var comment_link = ' <a target="_blank" href="' + comment.url + '">查看</a>';
            detail_items.select(":last-child .left")[0].insert(comment_link);
          }
          if (index === 2 && notification[comments].length > 3) {
            var actions = new Element('div', {'class' : 'notification_detail_item clearfix'});
            var show_all = new Element('a', {'class': 'show_all'}).update('查看另外'+ (notification[comments].length - 3) + '条' );
            show_all.observe('click', function(ev) {
              this.up().siblings().each(Element.show);
              this.up().hide();
            });
            actions.insert(show_all);
            detail_items.insert(actions);
          }
        });

        this.render_common_template(notification, {summary : summary, detail_title : summary + link, detail_items : detail_items});
      },

      template_subscription: function(notification) {
        var names = notification.subscribers.map(function(subscriber) {return subscriber.name;}).uniq();
        var summary = new Template('#{names}关注了您').evaluate({
          names : this.summary_names(names)
        });

        var detail_items = new Element('div', {'class' : 'detail_items'});
        notification.subscribers.each(function(subscriber){
          detail_items.insert(new Template('<div class="notification_detail_item clearfix"><div class="logo"><a href="#{user_url}" target="_blank"><img src="#{logo_path}" /></a></div><a href="#{user_url}" target="_blank">#{user_name}</a></div>').evaluate({
            user_url : subscriber.url, user_name : subscriber.name, logo_path : subscriber.logo_path
          }));
        });
        this.render_common_template(notification, {summary : summary, detail_title : summary, detail_items : detail_items});
      },

      template_common: function(notification) {
        this.render_common_template(notification, {summary : notification.title,
          detail_title : notification.title, detail_content : notification.detail,
          detail_notice : notification.notice});
      },

      render_common_template: function(notification, params){
        var summary = new Element('div', {id: 'notification_summary_' + notification.id, 'class' : "notification_summary clearfix"});
        var summary_title = new Element('div', {'class' : 'left'}).update(params.summary);
        var time = '<span class="time">' + notification.updated_at + '</span>';
        if (!notification.read) {
          summary.addClassName("unread");
        }
        summary.update(summary_title).insert(time);
        notifications_summary.insert(summary);

        var detail = new Element('div', {id : 'notification_' + notification.id, 'class' : 'notification_detail clearfix'});
        detail.update('<div class="notification_detail_title clearfix"></div><div class="notification_detail_content clearfix"></div>').hide();
        var detail_title = new Element('div', {'class' : 'left'}).update(params.detail_title);
        detail.down('.notification_detail_title').insert(detail_title).insert(time);
        var content = detail.down('.notification_detail_content');
        if (params.detail_content) {
          content.insert(new Element('div', {'class' : 'system_message'}).insert(params.detail_content));
        }
        if (params.detail_items) {content.insert(params.detail_items);}
        if (params.detail_notice) {
          var notice = new Element('div', {'class' : 'notification_detail_notice clearfix'}).update(params.detail_notice);
          detail.insert({bottom : notice});
        }
        notifications_detail.insert(detail);

        summary.observe('click', function(ev) {
          if (notifications_summary.down('.current')) {
            notifications_summary.down('.current').removeClassName('current');
          }
          this.addClassName('current');
          summary_to_detail();
        });
      }
    }

    function refresh_paginate_button() {
      var current = notifications_detail.down('.current')
      if (current.next('.notification_detail')) {
        $('next_button').removeClassName('disable');
      } else {
        $('next_button').addClassName('disable');
      }
      if (current.previous('.notification_detail')) {
        $('prev_button').removeClassName('disable');
      } else {
        $('prev_button').addClassName('disable');
      }
    }

    var change_page_effect_running = false;
    function change_page_effect(from, to, reverse) {
      var topOffset = from.positionedOffset()[1] + 'px';
      var from_status, to_status;
      change_page_effect_running = true;
      function clean_style(effect){
        if (effect.element.id === from.id) {
          from_status = true;
        } else {
          to_status = true;
        }
        if (from_status && to_status) {
          from.writeAttribute('style', 'display:none');
          to.writeAttribute('style', '');
          change_page_effect_running = false;
          if (!is_ie6() && notifications_detail.visible()) {
            var link = notifications_detail.down('.current').select('a').first();
            if (link) {link.focus();}
          }
        }
      }
      var width = notifications_menu.getWidth();
      var widthPx = width + 'px';
      if (reverse) {
        to.setStyle({position: 'absolute', left: '-' + widthPx, width: widthPx, top: topOffset}).show();
        Effect.multiple([from, to], Effect.Move, {speed: 0, x: width, duration: 0.15, afterFinish: clean_style});
      } else {
        to.setStyle({position: 'absolute', left: widthPx, width: widthPx, top: topOffset}).show();
        Effect.multiple([from, to], Effect.Move, {speed: 0, x: -width, duration: 0.15, afterFinish: clean_style});
      }
    }

    function read_detail(detail) {
      $('notification_summary_' + detail.id.split('_').last()).removeClassName('unread');
    }

    function change_detail_page(current, to, reverse) {
      current.removeClassName('current');
      to.addClassName('current');
      var id = to.id.split('_').last();
      notifications_summary.down('.current').removeClassName('current');
      notifications_summary.down('#notification_summary_' + id).addClassName('current');
      change_page_effect(current, to, reverse);
      read_detail(to);
      refresh_paginate_button();
    }

    function next_detail() {
      var current = notifications_detail.down('.current');
      var next_page = current.next('.notification_detail');
      if (next_page) {
        change_detail_page(current, next_page);
      }
    }

    function prev_detail() {
      var current = notifications_detail.down('.current');
      var prev_page = current.previous('.notification_detail');
      if (prev_page) {
        change_detail_page(current, prev_page, true);
      }
    }

    function init_detail_paginate() {
      var paginate = new Element('span', {'class' : 'paginate'});
      var prev = new Element('a', {id : 'prev_button'}).update('‹ 上一条');
      var next = new Element('a', {id : 'next_button'}).update('下一条 ›');
      next.observe('click', function(ev) {
        if (!this.hasClassName('disable')){
          next_detail();
        }
      });
      prev.observe('click', function(ev) {
        if (!this.hasClassName('disable')){
          prev_detail();
        }
      });
      paginate.insert(prev).insert('<span class="separated">|</span>').insert(next);
      return paginate;
    }

    function summary_to_detail() {
      var summary = notifications_summary.down('.current');
      var detail = notifications_detail.down('#notification_' + summary.id.split('_').last());
      var prev = notifications_detail.down('.current');
      if (prev) {
        prev.removeClassName('current').hide();
      }
      detail.addClassName('current').show();
      read_detail(detail);
      refresh_paginate_button();
      change_page_effect(notifications_summary, notifications_detail);
    }

    function hotkey_left() {
      if (notifications_detail.visible()) {
        if (notifications_detail.down('.current').previous('.notification_detail')) {
          prev_detail();
        } else {
          change_page_effect(notifications_detail, notifications_summary, true);
        }
      }
    }

    function hotkey_right() {
      if (notifications_detail.visible()) {
        next_detail();
      } else {
        if (!notifications_summary.down('.current')) {
          notifications_summary.down('.notification_summary').addClassName('current');
        }
        summary_to_detail();
      }
    }

    function hotkey_up() {
      if (notifications_summary.visible()) {
        var current_summary = notifications_summary.down('.current');
        if (current_summary) {
          if (current_summary.previous('.notification_summary')) {
            current_summary.removeClassName('current');
            current_summary.previous('.notification_summary').addClassName('current');
          }
        }
      }
    }

    function hotkey_down() {
      if (notifications_summary.visible()) {
        var current_summary = notifications_summary.down('.current');
        if (current_summary) {
          if (current_summary.next('.notification_summary')) {
            current_summary.removeClassName('current');
            current_summary.next('.notification_summary').addClassName('current');
          }
        } else {
          notifications_summary.down('.notification_summary').addClassName('current');
        }
      }
    }

    function hotkey_enter(event) {
      if (notifications_summary.visible() && notifications_summary.down('.current')) {
        summary_to_detail();
        event.stop();
      }
    }

    function hotkey_backspace() {
      if (notifications_detail.visible()) {
        change_page_effect(notifications_detail, notifications_summary, true);
      }
    }

    function init_notification_menu() {
      notifications_menu = new Element('div', {'id': 'notifications_menu'});
      notifications_summary = new Element('div', {'id': 'notifications_summary'});
      notifications_summary.update('<div class="menu_title clearfix"><span class="title">提醒</span><span class="all"><a href="http://my.iteye.com/notifications">查看所有 »</a></span></div>');
      notifications_menu.insert(notifications_summary);

      notifications_detail = new Element('div', {'id': 'notifications_detail'});
      var detail_menu_title = new Element('div', {'class' : 'menu_title clearfix'});
      var return_button = new Element('span', {'class' : 'return left'}).update('« 返回通知列表');
      detail_menu_title.insert(return_button).insert(init_detail_paginate());

      return_button.observe('click', function(ev) {
        change_page_effect(notifications_detail, notifications_summary, true);
      });
      notifications_detail.insert(detail_menu_title).hide();
      notifications_menu.insert(notifications_detail);
      $('user_nav').insert({after: notifications_menu});

      $(document).observe('keydown', function(event) {
        if (notifications_menu.visible() && notifications.length > 0 && !change_page_effect_running){
          var keyCode = event.keyCode;
          if (keyCode == Event.KEY_LEFT || keyCode == 72 ) {
            hotkey_left();
            event.stop();
          } else if (keyCode == Event.KEY_RIGHT || keyCode == 76) {
            hotkey_right();
            event.stop();
          } else if (keyCode == Event.KEY_UP || keyCode == 75) {
            hotkey_up();
            event.stop();
          } else if (keyCode == Event.KEY_DOWN || keyCode == 74) {
            hotkey_down();
            event.stop();
          } else if (keyCode == Event.KEY_RETURN) {
            hotkey_enter(event);
          } else if (keyCode == Event.KEY_BACKSPACE) {
            hotkey_backspace();
            event.stop();
          } else if (keyCode == Event.KEY_ESC) {
            hide_notification_menu();
          }
        }
      });

      $(document.body).observe('click', function(event) {
        if (notifications_menu.visible()) {
          var clicked = event.element();
          if (!(clicked === notifications_menu || clicked.up("#notifications_menu"))) {
            hide_notification_menu();
          }
        }
      });

    }

    function update_notifications_menu() {
      $$('#notifications_menu .notification_summary').each(Element.remove);
      $$('#notifications_menu .notification_detail').each(Element.remove);

      if (notifications.length > 0) {
        notifications.each(function(notification) {
          if (notification) {
            if (Render['template_' + notification.type]) {
              Render['template_' + notification.type](notification);
            } else {
              Render['template_common'](notification);
            }
          };
        });
      } else {
        notifications_summary.insert('<div class="notification_summary">当前没有提醒信息</div>');
      }
    }

    function refresh_unread_count(count) {
      unread_count = count;
      notifications_count.update(unread_count);
      if (count > 0) {
        notifications_count.addClassName('new_notice').writeAttribute('title', '有' + count + '条未读消息');
      } else {
        notifications_count.removeClassName('new_notice').writeAttribute('title', '没有未读消息');
      }
    }

    function update_unread_count(pe) {
      new Ajax.JSONRequest('http://n.iteye.com/notifications/unread_count.json', {
        method: "get",
        onComplete: function(response) {
          var json_respone = response.responseJSON;
          refresh_unread_count(json_respone.unread_count);
        }
      });
    }

    function update_notifications() {
      var loading = new Element('div', {id : 'notifications_loading'}).update('载入中...');
      notifications_menu.insert({top: loading});
      new Ajax.Request('/notifications.json', {
        method: "get",
        onComplete: function(response) {
          var json_respone = response.responseJSON;
          notifications = json_respone;
          update_notifications_menu();
          loading.remove();
          mark_as_read();
        }
      });
    }

    function mark_as_read() {
      var unread_ids = notifications.map(function(notification){
        if (!notification.read) {
          return notification.id;
        }
      }).without(undefined);
      if (unread_ids.length > 0) {
        new Ajax.Request('/notifications/read.json?ids=' + unread_ids.join(','), {
          method: "put",
          onComplete: function(response) {
            notifications.each(function(notification){notification.read = true});
            refresh_unread_count(response.responseJSON.unread_count);
          }
        });
      }
    }

    function show_notification_menu() {
      if (notifications_menu === null) {
        init_notification_menu();
      } else {
        notifications_summary.show();
      }
      if (unread_count > 0 || notifications === undefined) {
        update_notifications();
      }
      notifications_menu.show();
      notifications_count.addClassName('clicked');
    }

    function hide_notification_menu() {
      notifications_summary.hide();
      notifications_detail.hide();
      notifications_menu.hide();
      notifications_count.removeClassName('clicked');
    }

    function trigger_menu() {
      if (notifications_count.hasClassName('clicked')) {
        hide_notification_menu();
      } else {
        show_notification_menu();
      }
    }

    if (notifications_count) {
      notifications_count.observe('click', function(event) {
        trigger_menu();
        event.stop();
      });
      $(document).observe('keydown', function(event){
        // alt + n
        if (event.keyCode == 78 && event.altKey) {
          trigger_menu();
        }
      });
      update_unread_count();
      new PeriodicalExecuter(update_unread_count, 60);
    }
  })();
})
document.observe("dom:loaded", function() {(function() {
    var spinner_elem = new Element('img', {
      src: '/images/spinner.gif',
      'class': 'spinner'
    });

    var AskPageAction = Class.create({
      initialize: function() {
        this.data = new Hash();
      },
      toggle_action: function(event, event_element) {
        this._event_element = event_element;
        if (this._check()) {
          this._clean();
          if (this._is_show) this._factory().show();
        }
        event.stop();
      },
      get_event_element_parent_id: function() {
        return this._event_element.readAttribute('data_parent_id');
      },
      set_status: function(status_str) {
        if (status_str == 'showing') this.data.set('status', 'showing');
        else this.data.set('status', undefined);
      },
      get_status: function() {
        return this.data.get('status');
      },
      set_dropdown_status: function(status_str) {
        var parent_id = this.get_event_element_parent_id();
        this.data.set(parent_id + '_status', status_str);
        this.set_status(status_str)
      },
      set_dropdown_current: function(action_obj) {
        var parent_id = this.get_event_element_parent_id();
        this.data.set(parent_id + '_current', action_obj);
      },
      get_dropdown_status: function() {
        var parent_id = this.get_event_element_parent_id();
        return this.data.get(parent_id + '_status');
      },
      get_dropdown_current: function() {
        var parent_id = this.get_event_element_parent_id();
        return this.data.get(parent_id + '_current');
      },
      set_popup_status: function(status_str) {
        this.data.set('popup_status', status_str);
        this.set_status(status_str)
      },
      set_popup_current: function(action_obj) {
        this.data.set('popup_current', action_obj);
      },
      get_popup_status: function() {
        return this.data.get('popup_status');
      },
      get_popup_current: function() {
        return this.data.get('popup_current');
      },
      hide_current_popup: function(event_element) {
        this._event_element = event_element;
        var popup_current;
        if (popup_current = this.get_popup_current()) popup_current.hide();
      },
      hide_current_dropdown: function(event_element) {
        this._event_element = event_element;
        var dropdown_current;
        if (dropdown_current = this.get_dropdown_current()) dropdown_current.hide();
      },
      submit_comment: function(event_element) {
        this._event_element = event_element;
        if (this._event_element.previous('.spinner')) return;
        var parent_id = this.get_event_element_parent_id;
        var form_elem = event_element.up('form');
        var comment_validate;
        if (!this.data.get(parent_id + '_comment_validate')) {
          comment_validate = new Validation(form_elem);
          this.data.set(parent_id + '_comment_validate');
        }
        if (!comment_validate.validate()) return;

        var url = form_elem['action'];
        var right_spinner_elem = spinner_elem.clone();
        right_spinner_elem.style.float = 'right';
        this._event_element.insert({
          before: right_spinner_elem
        });
        new Ajax.Request(url, {
          method: 'post',
          parameters: form_elem.serialize(),
          onFailure:function(response){
            form_elem.insert({top:response.responseText})
            this._event_element.previous('.spinner').remove();
            Element.scrollTo(form_elem);
          }.bind(this),
          onSuccess: function(transport) {
            this._event_element.up(0).previous().value = '';
            this._event_element.up(1).insert({
              before: transport.responseText
            });
            this.get_dropdown_current().event_element().writeAttribute('data_comments_count', Number(this.get_dropdown_current().event_element().readAttribute('data_comments_count')) + 1);
            this._event_element.previous('.spinner').remove();
          }.bind(this)
        });
      },
      destroy_comment: function(event_element) {
        this._event_element = event_element;
        if (this._event_element.previous('.spinner')) return;

        var url = this._event_element.readAttribute('data_url');
        var right_spinner_elem = spinner_elem.clone();
        right_spinner_elem.style.float = 'right';
        this._event_element.insert({
          before: right_spinner_elem
        });
        new Ajax.Request(url, {
          method: 'delete',
          onSuccess: function(transport) {
            this._event_element.up(1).remove();
            this.get_dropdown_current().event_element().writeAttribute('data_comments_count', Number(this.get_dropdown_current().event_element().readAttribute('data_comments_count')) - 1);
            this._event_element.previous('.spinner').remove();
          }.bind(this)
        });
      },
      show_problem_popup_notice: function(params) {
        this._action_type = 'popup';
        this._clean();
        var notice_action = new NoticeAction(this);
        notice_action.show(params);
      },
      //检测当前是否有层在显示中,以确定是否运行clean和show任务
      //返回false表示有任务进行中,不清理和显示
      //this._is_show表示是否显示新的层
      _check: function() {
        this._is_show = false;
        var action_type = this._event_element.readAttribute('data_action_type');
        this._action_type = action_type;
        if (this.get_status() == 'showing') return false;
        switch (action_type) {
        case 'dropdown':
          var dropdown_status = this.get_dropdown_status();
          var dropdown_current = this.get_dropdown_current();
          if (dropdown_status == undefined) { //当前没有下拉层
            this._is_show = true;
          } else if (dropdown_status == 'showed' && dropdown_current.event_element() != this._event_element) { //当前有下拉层,但需要显示新的下拉层
            this._is_show = true;
          } else if (dropdown_status == 'showing') { //有任务进行中
            return false;
          }
          break;
        case 'popup':
          var popup_status = this.get_popup_status();
          var popup_current = this.get_popup_current();
          if (popup_status == undefined) {
            this._is_show = true;
          } else if (popup_status == 'showed' && popup_current.event_element() != this._event_element) {
            this._is_show = true;
          } else if (popup_status == 'showing') {
            return false;
          }
          break;
        default:
          return false;
        }
        return true;
      },
      _clean: function() {
        switch (this._action_type) {
        case 'dropdown':
          var dropdown_current = this.get_dropdown_current();
          if (dropdown_current) {
            dropdown_current.hide();
          }
          break;
        case 'popup':
          var popup_current = this.get_popup_current();
          if (popup_current) {
            popup_current.hide();
          }
          break;
        }
      },
      _factory: function() {
        var action_sub_type = this._event_element.readAttribute('data_action_sub_type');
        var action_obj;
        switch (action_sub_type) {
        case 'comment':
          action_obj = new CommentAction(this, this._event_element);
          break;
        case 'increment_score':
        case 'edit_tags':
        case 'replenish':
          action_obj = new CommonDropdownAction(this, this._event_element);
          break;
        case 'close':
        case 'destroy':
        case 'postpone':
        case 'solution_accept':
        case 'solution_destroy':
          action_obj = new ConfirmAction(this, this._event_element);
          break;
        }
        return action_obj;
      }

    });

    var PageAction = Class.create({
      initialize: function(ask_page_action, event_element) {
        this._ask_page_action = ask_page_action;
        this._event_element = event_element;
      },
      event_element: function() {
        return this._event_element;
      }
    });

    var CommentAction = Class.create(PageAction, {
      initialize: function($super, ask_page_action, event_element) {
        $super(ask_page_action, event_element);
      },
      show: function() {
        this._ask_page_action.set_dropdown_status('showing');
        this._ask_page_action.set_dropdown_current(this);
        this._event_element.appendChild(spinner_elem);
        var url = this._event_element.readAttribute('data_url');
        new Ajax.Request(url, {
          method: 'get',
          onSuccess: function(transport) {
            this._event_element.up().addClassName('action_selected');
            this._event_element.up(2).addClassName('action_bg');
            var img_elem = this._event_element.down();
            this._event_element.update(img_elem);
            this._event_element.appendChild(document.createTextNode('收起评论'));

            this._event_element.up(2).insert({
              after: transport.responseText
            });
            this._ask_page_action.set_dropdown_status('showed');
          }.bind(this)
        });
      },
      hide: function() {
        var img_elem = this._event_element.down();
        this._event_element.up().removeClassName('action_selected');
        this._event_element.up(2).removeClassName('action_bg');
        this._event_element.update(img_elem);
        var comments_count = this._event_element.readAttribute('data_comments_count');
        comments_count = Number(comments_count);
        var comments_count_text;
        if (comments_count > 0) comments_count_text = document.createTextNode(comments_count + '条评论');
        else comments_count_text = document.createTextNode('添加评论');
        this._event_element.appendChild(comments_count_text);
        this._event_element.up(2).next().remove();
        this._ask_page_action.set_dropdown_status(undefined);
        this._ask_page_action.set_dropdown_current(undefined);
      }

    });

    var CommonDropdownAction = Class.create(PageAction, {
      initialize: function($super, ask_page_action, event_element) {
        $super(ask_page_action, event_element);
      },
      show: function() {
        this._ask_page_action.set_dropdown_status('showing');
        this._ask_page_action.set_dropdown_current(this);
        this._event_element.up().addClassName('action_selected');
        this._event_element.up(2).addClassName('action_bg');
        var action_sub_type = this._event_element.readAttribute('data_action_sub_type');
        this._event_element.up(2).insert({
          after: window.html_codes.get(action_sub_type)
        });
        this._ask_page_action.set_dropdown_status('showed');
      },
      hide: function() {
        var img_elem = this._event_element.down();
        this._event_element.up().removeClassName('action_selected');
        this._event_element.up(2).removeClassName('action_bg');
        this._event_element.up(2).next().remove();
        this._ask_page_action.set_dropdown_status(undefined);
        this._ask_page_action.set_dropdown_current(undefined);
      }
    });

    var PopupAction = Class.create(PageAction, {
      initialize: function($super, ask_page_action, event_element) {
        $super(ask_page_action, event_element);
      },
      show: function() {
        this._ask_page_action.set_popup_status('showing');
        this._ask_page_action.set_popup_current(this);
        var action_sub_type = this._event_element.readAttribute('data_action_sub_type');
        $(document.body).insert({
          bottom: window.html_codes.get(action_sub_type)
        });
        this._set_problem_popupbox_style();
        this._ask_page_action.set_popup_status('showed');
      },
      hide: function() {
        $('problem_popupbox').remove();
        this._ask_page_action.set_popup_status(undefined);
        this._ask_page_action.set_popup_current(undefined);
      },
      _set_problem_popupbox_style: function() {
        if (!$('problem_popupbox')) return;
        var viewport = document.viewport.getDimensions();
        $('problem_popupbox').setStyle({
          'position': 'fixed',
          'left': (viewport.width - $('problem_popupbox').getDimensions().width) / 2 + 'px',
          'top': (viewport.height - $('problem_popupbox').getDimensions().height) / 2 + 'px'
        });
        if (navigator.userAgent.indexOf("MSIE 6") > 0) { // IE6
          $('problem_popupbox').setStyle({
            'position': 'absolute',
            'top': document.documentElement.scrollTop + (viewport.height - $('problem_popupbox').getDimensions().height) / 2 + "px"
          });
        }
      }
    });

    var ConfirmAction = Class.create(PopupAction, {
      initialize: function($super, ask_page_action, event_element) {
        $super(ask_page_action, event_element);
      },
      show: function() {
        this._ask_page_action.set_popup_status('showing');
        this._ask_page_action.set_popup_current(this);
        var action_sub_type = this._event_element.readAttribute('data_action_sub_type');
        var html_code = window.html_codes.get(action_sub_type);
        if (this._event_element.readAttribute('replace_url')) {
          html_code = html_code.replace('action_url', this._event_element.readAttribute('data_url'));
        }
        $(document.body).insert({
          bottom: html_code
        });
        this._set_problem_popupbox_style();
        this._ask_page_action.set_popup_status('showed');
      }
    });

    var NoticeAction = Class.create(PopupAction, {
      initialize: function($super, ask_page_action) {
        $super(ask_page_action, null);
      },
      show: function(params) {
        this._ask_page_action.set_popup_status('showing');
        this._ask_page_action.set_popup_current(this);
        var html_code = window.html_codes.get('problem_popup_notice');
        var notice_template = new Template(html_code);
        var delay_number = params['delay_number'] || 0
        var delay_func = params['delay_func']
        if (delay_number > 0) {
          if(delay_func){
            delay_func.delay(delay_number);
          }else{
            this.delay_hide.bind(this).delay(delay_number);
            params['class_name'] = params['class_name'] + ' delay';
          }
        }
        $(document.body).insert({
          bottom: notice_template.evaluate(params)
        });
        this._set_problem_popupbox_style();
        this._ask_page_action.set_popup_status('showed');
      },
      delay_hide: function() {
        var problem_popupbox = $('problem_popupbox');
        if (problem_popupbox && problem_popupbox.className.include('delay')) {
          this.hide();
        }
      }

    });

    var ask_page_action = new AskPageAction();
    window.ask_page_action = ask_page_action;

    if ($$('div.ask_action_handler').length > 0) {
      $(document.body).observe('click', function(event) {
        var event_elem;
        if (event_elem = event.findElement('a.ask_click_action')) {
          ask_page_action.toggle_action(event, event_elem);
        } else if (event_elem = event.findElement('input#close_popup_action')) {
          ask_page_action.hide_current_popup(event_elem);
        } else if (event_elem = event.findElement('input.close_dropdown_action')) {
          ask_page_action.hide_current_dropdown(event_elem);
        } else if (event_elem = event.findElement('input.submit_comment')) {
          ask_page_action.submit_comment(event_elem);
        } else if (event_elem = event.findElement('a.destroy_comment')) {
          ask_page_action.destroy_comment(event_elem);
        } else if (event_elem = event.findElement('input.after_show_spinner')) {
          event_elem.disabled = true;
          event_elem.insert({
            after: spinner_elem
          });
          event_elem.form.submit();
        } else if (event_elem = event.findElement('input.before_show_spinner')) {
          event_elem.disabled = true;
          var before_spinner_elem = spinner_elem.clone();
          before_spinner_elem.className = 'spinner_img';
          event_elem.insert({
            before: before_spinner_elem
          });
          event_elem.form.submit();
        }
      });

      $(document).observe('keyup', function(event){
        var keyCode = event ? (event.which ? event.which : event.keyCode) : window.event.keyCode;
        var event_elem;
        if( (keyCode == 27) && (event_elem = $('close_popup_action')) ){ // escape key only, no enter key 
          ask_page_action.hide_current_popup(event_elem);
        }
      });
    }
  })();
});

function sortSolutionsByTime(){
    title = $$("#solutions #num")[0];
    dls = $$("#solutions dl.for_sort");
    dls.sortBy(function(e){
        return parseInt(e.attributes["time"]["value"])
    })
    .each(function(elem){
        title.insert({after: elem})
    });
    $("sort_time_link").style.color = "black";
    $("sort_vote_link").style.color = "#069";
    return false;
}

function sortSolutionsByVote(){
    title = $$("#solutions #num")[0];
    dls = $$("#solutions dl.for_sort");
    dls.sortBy(function(e){
        return parseInt(e.attributes["votes"]["value"])
    })
    .each(function(elem){
        title.insert({
            after: elem
        })
    });
    $("sort_vote_link").style.color = "black";
    $("sort_time_link").style.color = "#069";
    return false;
}

function highlightNewAddContent(css_rules) {
  renameElements(css_rules, 'new-code');
  dp.SyntaxHighlighter.HighlightAll('new-code', true, true);
  renameElements(css_rules, 'code');
}

function renameElements(css_rules, new_name) {
  $$(css_rules).each(function(element) {
    element.writeAttribute('name', new_name);
    console.log(element);
  });
}
