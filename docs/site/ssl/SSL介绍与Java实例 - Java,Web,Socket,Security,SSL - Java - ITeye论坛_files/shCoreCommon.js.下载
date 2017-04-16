/**
 * Modified by quake, add new clipboard swf to support none IE browser and generic language highlight
 * Code Syntax Highlighter.
 * Version 1.5.1
 * Copyright (C) 2004-2007 Alex Gorbatchev.
 * http://www.dreamprojections.com/syntaxhighlighter/
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General 
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) 
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to 
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */

//
// create namespaces
//
var dp = {
    sh :
    {
        Toolbar : {},
        Utils	: {},
        RegexLib: {},
        Brushes	: {},
        Version : '1.5.1'
    }
};

// make an alias
dp.SyntaxHighlighter = dp.sh;

//
// Toolbar functions
//

dp.sh.Toolbar.CopyToClipboard = function(sender)
{
    var n = sender;

    while(n != null && n.className.indexOf('dp-highlighter') == -1)
        n = n.parentNode;

    var highlighter = n.highlighter;

    var code = highlighter.originalCode.replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&amp;/g,'&');
    window.clipboardData.setData('text', code);
    alert('代码已被复制到剪贴板');
}

// creates a <div /> with all toolbar links
dp.sh.Toolbar.Create = function(highlighter)
{
    var div = document.createElement('DIV');
    div.className = 'tools';
    div.innerHTML = highlighter.language.capitalize()+'代码';
    if(window.clipboardData) {
        div.innerHTML += ' <a href="#" onclick="dp.sh.Toolbar.CopyToClipboard(this);return false;" title="复制代码"><img src="/images/icon_copy.gif" alt="复制代码"/></a>';
    }else{
        var code = highlighter.originalCode.replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&amp;/g,'&');
        div.innerHTML += ' <embed wmode="transparent" src="/javascripts/syntaxhighlighter/clipboard_new.swf" width="14" height="15" flashvars="clipboard='+encodeURIComponent(code)+'" quality="high" allowScriptAccess="always" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer"/>';
    }
    return div;
}

//
// Common reusable regular expressions
//
dp.sh.RegexLib = {
    MultiLineCComments : new RegExp('/\\*[\\s\\S]*?\\*/', 'gm'),
    SingleLineCComments : new RegExp('//.*$', 'gm'),
    SingleLinePerlComments : new RegExp('#.*$', 'gm'),
    DoubleQuotedString : new RegExp('"(?:\\.|(\\\\\\")|[^\\""\\n])*"','g'),
    SingleQuotedString : new RegExp("'(?:\\.|(\\\\\\')|[^\\''\\n])*'", 'g')
};

//
// Match object
//
dp.sh.Match = function(value, index, css)
{
    this.value = value;
    this.index = index;
    this.length = value.length;
    this.css = css;
}

//
// Highlighter object
//
dp.sh.Highlighter = function()
{
    this.noGutter = false;
    this.addControls = true;
    this.collapse = false;
    this.tabsToSpaces = true;
    this.wrapColumn = 80;
    this.showColumns = true;
}

// static callback for the match sorting
dp.sh.Highlighter.SortCallback = function(m1, m2)
{
    // sort matches by index first
    if(m1.index < m2.index)
        return -1;
    else if(m1.index > m2.index)
        return 1;
    else
    {
        // if index is the same, sort by length
        if(m1.length < m2.length)
            return -1;
        else if(m1.length > m2.length)
            return 1;
    }
    return 0;
}

dp.sh.Highlighter.prototype.CreateElement = function(name)
{
    var result = document.createElement(name);
    result.highlighter = this;
    return result;
}

// gets a list of all matches for a given regular expression
dp.sh.Highlighter.prototype.GetMatches = function(regex, css)
{
    var index = 0;
    var match = null;

    while((match = regex.exec(this.code)) != null)
        this.matches[this.matches.length] = new dp.sh.Match(match[0], match.index, css);
}

dp.sh.Highlighter.prototype.AddBit = function(str, css)
{
    if(str == null || str.length == 0)
        return;

    var span = this.CreateElement('SPAN');
	
    //	str = str.replace(/&/g, '&amp;');
    str = str.replace(/ /g, '&nbsp;');
    str = str.replace(/</g, '&lt;');
    //	str = str.replace(/&lt;/g, '<');
    //	str = str.replace(/>/g, '&gt;');
    str = str.replace(/\n/gm, '&nbsp;<br>');

    // when adding a piece of code, check to see if it has line breaks in it
    // and if it does, wrap individual line breaks with span tags
    if(css != null)
    {
        if((/br/gi).test(str))
        {
            var lines = str.split('&nbsp;<br>');
			
            for(var i = 0; i < lines.length; i++)
            {
                span = this.CreateElement('SPAN');
                span.className = css;
                span.innerHTML = lines[i];
				
                this.div.appendChild(span);
				
                // don't add a <BR> for the last line
                if(i + 1 < lines.length)
                    this.div.appendChild(this.CreateElement('BR'));
            }
        }
        else
        {
            span.className = css;
            span.innerHTML = str;
            this.div.appendChild(span);
        }
    }
    else
    {
        span.innerHTML = str;
        this.div.appendChild(span);
    }
}

// checks if one match is inside any other match
dp.sh.Highlighter.prototype.IsInside = function(match)
{
    if(match == null || match.length == 0)
        return false;
	
    for(var i = 0; i < this.matches.length; i++)
    {
        var c = this.matches[i];
		
        if(c == null)
            continue;

        if((match.index > c.index) && (match.index < c.index + c.length))
            return true;
    }
	
    return false;
}

dp.sh.Highlighter.prototype.ProcessRegexList = function()
{
    for(var i = 0; i < this.regexList.length; i++)
        this.GetMatches(this.regexList[i].regex, this.regexList[i].css);
}

dp.sh.Highlighter.prototype.ProcessSmartTabs = function(code)
{
    var lines	= code.split('\n');
    var result	= '';
    var tabSize	= 4;
    var tab		= '\t';

    // This function inserts specified amount of spaces in the string
    // where a tab is while removing that given tab.
    function InsertSpaces(line, pos, count)
    {
        var left	= line.substr(0, pos);
        var right	= line.substr(pos + 1, line.length);	// pos + 1 will get rid of the tab
        var spaces	= '';
		
        for(var i = 0; i < count; i++)
            spaces += ' ';
		
        return left + spaces + right;
    }

    // This function process one line for 'smart tabs'
    function ProcessLine(line, tabSize)
    {
        if(line.indexOf(tab) == -1)
            return line;

        var pos = 0;

        while((pos = line.indexOf(tab)) != -1)
        {
            // This is pretty much all there is to the 'smart tabs' logic.
            // Based on the position within the line and size of a tab,
            // calculate the amount of spaces we need to insert.
            var spaces = tabSize - pos % tabSize;
			
            line = InsertSpaces(line, pos, spaces);
        }
		
        return line;
    }

    // Go through all the lines and do the 'smart tabs' magic.
    for(var i = 0; i < lines.length; i++)
        result += ProcessLine(lines[i], tabSize) + '\n';
	
    return result;
}

dp.sh.Highlighter.prototype.SwitchToList = function()
{
    // thanks to Lachlan Donald from SitePoint.com for this <br/> tag fix.
    var html = this.div.innerHTML.replace(/<(br)\/?>/gi, '\n');
    var lines = html.split('\n');
	
    if(this.addControls == true)
        this.bar.appendChild(dp.sh.Toolbar.Create(this));

    // add columns ruler
    if(this.showColumns)
    {
        var div = this.CreateElement('div');
        var columns = this.CreateElement('div');
        var showEvery = 10;
        var i = 1;
		
        while(i <= 150)
        {
            if(i % showEvery == 0)
            {
                div.innerHTML += i;
                i += (i + '').length;
            }
            else
            {
                div.innerHTML += '&middot;';
                i++;
            }
        }
		
        columns.className = 'columns';
        columns.appendChild(div);
        this.bar.appendChild(columns);
    }

    for(var i = 0, lineIndex = this.firstLine; i < lines.length - 1; i++, lineIndex++)
    {
        var li = this.CreateElement('LI');
        var span = this.CreateElement('SPAN');
        span.innerHTML = lines[i] + '&nbsp;';
        li.appendChild(span);
        this.ol.appendChild(li);
    }
	
    this.div.innerHTML	= '';
}

dp.sh.Highlighter.prototype.Highlight = function(code)
{
    function Trim(str)
    {
        return str.replace(/^\s*(.*?)[\s\n]*$/g, '$1');
    }
	
    function Chop(str)
    {
        return str.replace(/\n*$/, '').replace(/^\n*/, '');
    }

    function Unindent(str)
    {
        var lines = str.split('\n');
        var indents = new Array();
        var regex = new RegExp('^\\s*', 'g');
        var min = 1000;

        // go through every line and check for common number of indents
        for(var i = 0; i < lines.length && min > 0; i++)
        {
            if(Trim(lines[i]).length == 0)
                continue;
				
            var matches = regex.exec(lines[i]);

            if(matches != null && matches.length > 0)
                min = Math.min(matches[0].length, min);
        }

        // trim minimum common number of white space from the begining of every line
        if(min > 0)
            for(var i = 0; i < lines.length; i++)
                lines[i] = lines[i].substr(min);

        return lines.join('\n');
    }
	
    // This function returns a portions of the string from pos1 to pos2 inclusive
    function Copy(string, pos1, pos2)
    {
        return string.substr(pos1, pos2 - pos1);
    }

    var pos	= 0;
	
    if(code == null)
        code = '';
	
    this.originalCode = code;
    this.code = Chop(Unindent(code));
    this.div = this.CreateElement('DIV');
    this.bar = this.CreateElement('DIV');
    this.ol = this.CreateElement('OL');
    this.matches = new Array();

    this.div.className = 'dp-highlighter';
    this.div.highlighter = this;
	
    this.bar.className = 'bar';
	
    // set the first line
    this.ol.start = this.firstLine;

    if(this.CssClass != null)
        this.ol.className = this.CssClass;

    if(this.collapse)
        this.div.className += ' collapsed';
	
    if(this.noGutter)
        this.div.className += ' nogutter';

    // replace tabs with spaces
    if(this.tabsToSpaces == true)
        this.code = this.ProcessSmartTabs(this.code);

    this.ProcessRegexList();

    // if no matches found, add entire code as plain text
    if(this.matches.length == 0)
    {
        this.AddBit(this.code, null);
        this.SwitchToList();
        this.div.appendChild(this.bar);
        this.div.appendChild(this.ol);
        return;
    }

    // sort the matches
    this.matches = this.matches.sort(dp.sh.Highlighter.SortCallback);

    // The following loop checks to see if any of the matches are inside
    // of other matches. This process would get rid of highligted strings
    // inside comments, keywords inside strings and so on.
    for(var i = 0; i < this.matches.length; i++)
        if(this.IsInside(this.matches[i]))
            this.matches[i] = null;

    // Finally, go through the final list of matches and pull the all
    // together adding everything in between that isn't a match.
    for(var i = 0; i < this.matches.length; i++)
    {
        var match = this.matches[i];

        if(match == null || match.length == 0)
            continue;

        this.AddBit(Copy(this.code, pos, match.index), null);
        this.AddBit(match.value, match.css);

        pos = match.index + match.length;
    }
	
    this.AddBit(this.code.substr(pos), null);

    this.SwitchToList();
    this.div.appendChild(this.bar);
    this.div.appendChild(this.ol);
}

dp.sh.Highlighter.prototype.GetKeywords = function(str) 
{
    return '\\b' + str.replace(/ /g, '\\b|\\b') + '\\b';
}

// highlightes all elements identified by name and gets source code from specified property
dp.sh.HighlightAll = function(name, showGutter /* optional */, showControls /* optional */, collapseAll /* optional */, firstLine /* optional */, showColumns /* optional */)
{
    function FindValue()
    {
        var a = arguments;
		
        for(var i = 0; i < a.length; i++)
        {
            if(a[i] == null)
                continue;
				
            if(typeof(a[i]) == 'string' && a[i] != '')
                return a[i] + '';
		
            if(typeof(a[i]) == 'object' && a[i].value != '')
                return a[i].value + '';
        }
		
        return null;
    }
	
    function IsOptionSet(value, list)
    {
        for(var i = 0; i < list.length; i++)
            if(list[i] == value)
                return true;
		
        return false;
    }
	
    function GetOptionValue(name, list, defaultValue)
    {
        var regex = new RegExp('^' + name + '\\[(\\w+)\\]$', 'gi');
        var matches = null;

        for(var i = 0; i < list.length; i++)
            if((matches = regex.exec(list[i])) != null)
                return matches[1];
		
        return defaultValue;
    }
	
    function FindTagsByName(list, name, tagName)
    {
        var tags = document.getElementsByTagName(tagName);

        for(var i = 0; i < tags.length; i++)
            if(tags[i].getAttribute('name') == name)
                list.push(tags[i]);
    }

    var elements = [];
    var highlighter = null;
    var registered = {};
    var propertyName = 'innerHTML';

    // for some reason IE doesn't find <pre/> by name, however it does see them just fine by tag name...
    FindTagsByName(elements, name, 'pre');
    FindTagsByName(elements, name, 'textarea');

    if(elements.length == 0)
        return;

    // register all brushes
    for(var brush in dp.sh.Brushes)
    {
        var aliases = dp.sh.Brushes[brush].Aliases;

        if(aliases == null)
            continue;
		
        for(var i = 0; i < aliases.length; i++)
            registered[aliases[i]] = brush;
    }

    for(var i = 0; i < elements.length; i++)
    {
        var element = elements[i];
        var options = FindValue(
            element.attributes['class'], element.className,
            element.attributes['language'], element.language
            );
        var language = '';
		
        if(options == null)
            continue;
		
        options = options.split(':');
		
        language = options[0].toLowerCase();

        if(registered[language] == null)
            registered[language] = registered['default'];
		
        // instantiate a brush
        highlighter = new dp.sh.Brushes[registered[language]]();
        highlighter.language = language;
		
        // hide the original element
        element.style.display = 'none';

        highlighter.noGutter = (showGutter == null) ? IsOptionSet('nogutter', options) : !showGutter;
        highlighter.addControls = (showControls == null) ? !IsOptionSet('nocontrols', options) : showControls;
        highlighter.collapse = (collapseAll == null) ? IsOptionSet('collapse', options) : collapseAll;
        highlighter.showColumns = (showColumns == null) ? IsOptionSet('showcolumns', options) : showColumns;

        // write out custom brush style
        var headNode = document.getElementsByTagName('head')[0];
        if(highlighter.Style && headNode)
        {
            var styleNode = document.createElement('style');
            styleNode.setAttribute('type', 'text/css');

            if(styleNode.styleSheet) // for IE
            {
                styleNode.styleSheet.cssText = highlighter.Style;
            }
            else // for everyone else
            {
                var textNode = document.createTextNode(highlighter.Style);
                styleNode.appendChild(textNode);
            }

            headNode.appendChild(styleNode);
        }
		
        // first line idea comes from Andrew Collington, thanks!
        highlighter.firstLine = (firstLine == null) ? parseInt(GetOptionValue('firstline', options, 1)) : firstLine;

        highlighter.Highlight(element[propertyName]);
		
        highlighter.source = element;

        element.parentNode.insertBefore(highlighter.div, element);
    }
}
dp.sh.Brushes.JScript = function()
{
	var keywords =	'abstract boolean break byte case catch char class const continue debugger ' +
					'default delete do double else enum export extends false final finally float ' +
					'for function goto if implements import in instanceof int interface long native ' +
					'new null package private protected public return short static super switch ' +
					'synchronized this throw throws transient true try typeof var void volatile while with';

	this.regexList = [
		{ regex: dp.sh.RegexLib.SingleLineCComments,				css: 'comment' },			// one line comments
		{ regex: dp.sh.RegexLib.MultiLineCComments,					css: 'comment' },			// multiline comments
		{ regex: dp.sh.RegexLib.DoubleQuotedString,					css: 'string' },			// double quoted strings
		{ regex: dp.sh.RegexLib.SingleQuotedString,					css: 'string' },			// single quoted strings
		{ regex: new RegExp('^\\s*#.*', 'gm'),						css: 'preprocessor' },		// preprocessor tags like #region and #endregion
		{ regex: new RegExp(this.GetKeywords(keywords), 'gm'),		css: 'keyword' }			// keywords
		];

	this.CssClass = 'dp-c';
}

dp.sh.Brushes.JScript.prototype	= new dp.sh.Highlighter();
dp.sh.Brushes.JScript.Aliases	= ['js', 'jscript', 'javascript'];
dp.sh.Brushes.Java = function()
{
	var keywords =	'abstract assert boolean break byte case catch char class const ' +
			'continue default do double else enum extends ' +
			'false final finally float for goto if implements import ' +
			'instanceof int interface long native new null ' +
			'package private protected public return ' +
			'short static strictfp super switch synchronized this throw throws true ' +
			'transient try void volatile while';

	this.regexList = [
		{ regex: dp.sh.RegexLib.SingleLineCComments,							css: 'comment' },		// one line comments
		{ regex: dp.sh.RegexLib.MultiLineCComments,								css: 'comment' },		// multiline comments
		{ regex: dp.sh.RegexLib.DoubleQuotedString,								css: 'string' },		// strings
		{ regex: dp.sh.RegexLib.SingleQuotedString,								css: 'string' },		// strings
		{ regex: new RegExp('\\b([\\d]+(\\.[\\d]+)?|0x[a-f0-9]+)\\b', 'gi'),	css: 'number' },		// numbers
		{ regex: new RegExp('(?!\\@interface\\b)\\@[\\$\\w]+\\b', 'g'),			css: 'annotation' },	// annotation @anno
		{ regex: new RegExp('\\@interface\\b', 'g'),							css: 'keyword' },		// @interface keyword
		{ regex: new RegExp(this.GetKeywords(keywords), 'gm'),					css: 'keyword' }		// java keyword
		];

	this.CssClass = 'dp-j';
	this.Style =	'.dp-j .annotation { color: #646464; }' +
					'.dp-j .number { color: #C00000; }';
}

dp.sh.Brushes.Java.prototype	= new dp.sh.Highlighter();
dp.sh.Brushes.Java.Aliases	= ['java'];
/* Ruby 1.8.4 syntax contributed by Erik Peterson */
dp.sh.Brushes.Ruby = function()
{
  var keywords =	'alias and BEGIN begin break case class def define_method defined do each else elsif ' +
					'END end ensure false for if in module new next nil not or raise redo rescue retry return ' +
					'self super then throw true undef unless until when while yield';

  var builtins =	'Array Bignum Binding Class Continuation Dir Exception FalseClass File::Stat File Fixnum Fload ' +
					'Hash Integer IO MatchData Method Module NilClass Numeric Object Proc Range Regexp String Struct::TMS Symbol ' +
					'ThreadGroup Thread Time TrueClass'

	this.regexList = [
		{ regex: dp.sh.RegexLib.SingleLinePerlComments,			css: 'comment' },	// one line comments
		{ regex: dp.sh.RegexLib.DoubleQuotedString,				css: 'string' },	// double quoted strings
		{ regex: dp.sh.RegexLib.SingleQuotedString,				css: 'string' },	// single quoted strings
		{ regex: new RegExp(':[a-z][A-Za-z0-9_]*', 'g'),		css: 'symbol' },	// symbols
		{ regex: new RegExp('(\\$|@@|@)\\w+', 'g'),				css: 'variable' },	// $global, @instance, and @@class variables
		{ regex: new RegExp(this.GetKeywords(keywords), 'gm'),	css: 'keyword' },	// keywords
		{ regex: new RegExp(this.GetKeywords(builtins), 'gm'),	css: 'builtin' }	// builtins
		];

	this.CssClass = 'dp-rb';
	this.Style =	'.dp-rb .symbol { color: #a70; }' +
					'.dp-rb .variable { color: #a70; font-weight: bold; }';
}

dp.sh.Brushes.Ruby.prototype = new dp.sh.Highlighter();
dp.sh.Brushes.Ruby.Aliases = ['ruby', 'rails', 'ror'];
dp.sh.Brushes.Xml = function()
{
	this.CssClass = 'dp-xml';
	this.Style =	'.dp-xml .cdata { color: #ff1493; }' +
					'.dp-xml .tag, .dp-xml .tag-name { color: #069; font-weight: bold; }' +
					'.dp-xml .attribute { color: red; }' +
					'.dp-xml .attribute-value { color: blue; }';
}

dp.sh.Brushes.Xml.prototype	= new dp.sh.Highlighter();
dp.sh.Brushes.Xml.Aliases	= ['xml', 'xhtml', 'xslt', 'html', 'xhtml'];

dp.sh.Brushes.Xml.prototype.ProcessRegexList = function()
{
	function push(array, value)
	{
		array[array.length] = value;
	}
	
	/* If only there was a way to get index of a group within a match, the whole XML
	   could be matched with the expression looking something like that:
	
	   (<!\[CDATA\[\s*.*\s*\]\]>)
	   | (<!--\s*.*\s*?-->)
	   | (<)*(\w+)*\s*(\w+)\s*=\s*(".*?"|'.*?'|\w+)(/*>)*
	   | (</?)(.*?)(/?>)
	*/
	var index	= 0;
	var match	= null;
	var regex	= null;

	// Match CDATA in the following format <![ ... [ ... ]]>
	// (\&lt;|<)\!\[[\w\s]*?\[(.|\s)*?\]\](\&gt;|>)
	this.GetMatches(new RegExp('(\&lt;|<)\\!\\[[\\w\\s]*?\\[(.|\\s)*?\\]\\](\&gt;|>)', 'gm'), 'cdata');
	
	// Match comments
	// (\&lt;|<)!--\s*.*?\s*--(\&gt;|>)
	this.GetMatches(new RegExp('(\&lt;|<)!--\\s*.*?\\s*--(\&gt;|>)', 'gm'), 'comments');

	// Match attributes and their values
	// (:|\w+)\s*=\s*(".*?"|\'.*?\'|\w+)*
	regex = new RegExp('([:\\w-\.]+)\\s*=\\s*(".*?"|\'.*?\'|\\w+)*|(\\w+)', 'gm'); // Thanks to Tomi Blinnikka of Yahoo! for fixing namespaces in attributes
	while((match = regex.exec(this.code)) != null)
	{
		if(match[1] == null)
		{
			continue;
		}
			
		push(this.matches, new dp.sh.Match(match[1], match.index, 'attribute'));
	
		// if xml is invalid and attribute has no property value, ignore it	
		if(match[2] != undefined)
		{
			push(this.matches, new dp.sh.Match(match[2], match.index + match[0].indexOf(match[2]), 'attribute-value'));
		}
	}

	// Match opening and closing tag brackets
	// (\&lt;|<)/*\?*(?!\!)|/*\?*(\&gt;|>)
	this.GetMatches(new RegExp('(\&lt;|<)/*\\?*(?!\\!)|/*\\?*(\&gt;|>)', 'gm'), 'tag');

	// Match tag names
	// (\&lt;|<)/*\?*\s*(\w+)
	regex = new RegExp('(?:\&lt;|<)/*\\?*\\s*([:\\w-\.]+)', 'gm');
	while((match = regex.exec(this.code)) != null)
	{
		push(this.matches, new dp.sh.Match(match[1], match.index + match[0].indexOf(match[1]), 'tag-name'));
	}
}
dp.sh.Brushes.CSharp = function()
{
	var keywords =	'abstract as base bool break byte case catch char checked class const ' +
					'continue decimal default delegate do double else enum event explicit ' +
					'extern false finally fixed float for foreach get goto if implicit in int ' +
					'interface internal is lock long namespace new null object operator out ' +
					'override params private protected public readonly ref return sbyte sealed set ' +
					'short sizeof stackalloc static string struct switch this throw true try ' +
					'typeof uint ulong unchecked unsafe ushort using virtual void while';

	this.regexList = [
		// There's a slight problem with matching single line comments and figuring out
		// a difference between // and ///. Using lookahead and lookbehind solves the
		// problem, unfortunately JavaScript doesn't support lookbehind. So I'm at a 
		// loss how to translate that regular expression to JavaScript compatible one.
//		{ regex: new RegExp('(?<!/)//(?!/).*$|(?<!/)////(?!/).*$|/\\*[^\\*]*(.)*?\\*/', 'gm'),	css: 'comment' },			// one line comments starting with anything BUT '///' and multiline comments
//		{ regex: new RegExp('(?<!/)///(?!/).*$', 'gm'),											css: 'comments' },		// XML comments starting with ///

		{ regex: dp.sh.RegexLib.SingleLineCComments,				css: 'comment' },			// one line comments
		{ regex: dp.sh.RegexLib.MultiLineCComments,					css: 'comment' },			// multiline comments
		{ regex: dp.sh.RegexLib.DoubleQuotedString,					css: 'string' },			// strings
		{ regex: dp.sh.RegexLib.SingleQuotedString,					css: 'string' },			// strings
		{ regex: new RegExp('^\\s*#.*', 'gm'),						css: 'preprocessor' },		// preprocessor tags like #region and #endregion
		{ regex: new RegExp(this.GetKeywords(keywords), 'gm'),		css: 'keyword' }			// c# keyword
		];

	this.CssClass = 'dp-c';
	this.Style = '.dp-c .vars { color: #d00; }';
}

dp.sh.Brushes.CSharp.prototype	= new dp.sh.Highlighter();
dp.sh.Brushes.CSharp.Aliases	= ['c#', 'c-sharp', 'csharp'];
/**
 * Code Syntax Highlighter for C++(Windows Platform).
 * Version 0.0.2
 * Copyright (C) 2006 Shin, YoungJin.
 * http://www.jiniya.net/lecture/techbox/test.html
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General 
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) 
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to 
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */

dp.sh.Brushes.Cpp = function()
{
	var datatypes = 
	'ATOM BOOL BOOLEAN BYTE CHAR COLORREF DWORD DWORDLONG DWORD_PTR ' +
	'DWORD32 DWORD64 FLOAT HACCEL HALF_PTR HANDLE HBITMAP HBRUSH ' + 
	'HCOLORSPACE HCONV HCONVLIST HCURSOR HDC HDDEDATA HDESK HDROP HDWP ' +
	'HENHMETAFILE HFILE HFONT HGDIOBJ HGLOBAL HHOOK HICON HINSTANCE HKEY ' +
	'HKL HLOCAL HMENU HMETAFILE HMODULE HMONITOR HPALETTE HPEN HRESULT ' +
	'HRGN HRSRC HSZ HWINSTA HWND INT INT_PTR INT32 INT64 LANGID LCID LCTYPE ' +
	'LGRPID LONG LONGLONG LONG_PTR LONG32 LONG64 LPARAM LPBOOL LPBYTE LPCOLORREF ' +
	'LPCSTR LPCTSTR LPCVOID LPCWSTR LPDWORD LPHANDLE LPINT LPLONG LPSTR LPTSTR ' +
	'LPVOID LPWORD LPWSTR LRESULT PBOOL PBOOLEAN PBYTE PCHAR PCSTR PCTSTR PCWSTR ' +
	'PDWORDLONG PDWORD_PTR PDWORD32 PDWORD64 PFLOAT PHALF_PTR PHANDLE PHKEY PINT ' +
	'PINT_PTR PINT32 PINT64 PLCID PLONG PLONGLONG PLONG_PTR PLONG32 PLONG64 POINTER_32 ' +
	'POINTER_64 PSHORT PSIZE_T PSSIZE_T PSTR PTBYTE PTCHAR PTSTR PUCHAR PUHALF_PTR ' +
	'PUINT PUINT_PTR PUINT32 PUINT64 PULONG PULONGLONG PULONG_PTR PULONG32 PULONG64 ' +
	'PUSHORT PVOID PWCHAR PWORD PWSTR SC_HANDLE SC_LOCK SERVICE_STATUS_HANDLE SHORT ' + 
	'SIZE_T SSIZE_T TBYTE TCHAR UCHAR UHALF_PTR UINT UINT_PTR UINT32 UINT64 ULONG ' +
	'ULONGLONG ULONG_PTR ULONG32 ULONG64 USHORT USN VOID WCHAR WORD WPARAM WPARAM WPARAM ' +
	'char bool short int __int32 __int64 __int8 __int16 long float double __wchar_t ' +
	'clock_t _complex _dev_t _diskfree_t div_t ldiv_t _exception _EXCEPTION_POINTERS ' +
	'FILE _finddata_t _finddatai64_t _wfinddata_t _wfinddatai64_t __finddata64_t ' +
	'__wfinddata64_t _FPIEEE_RECORD fpos_t _HEAPINFO _HFILE lconv intptr_t ' +
	'jmp_buf mbstate_t _off_t _onexit_t _PNH ptrdiff_t _purecall_handler ' +
	'sig_atomic_t size_t _stat __stat64 _stati64 terminate_function ' +
	'time_t __time64_t _timeb __timeb64 tm uintptr_t _utimbuf ' +
	'va_list wchar_t wctrans_t wctype_t wint_t signed';

	var keywords = 
	'break case catch class const __finally __exception __try ' +
	'const_cast continue private public protected __declspec ' + 
	'default delete deprecated dllexport dllimport do dynamic_cast ' + 
	'else enum explicit extern if for friend goto inline ' + 
	'mutable naked namespace new noinline noreturn nothrow ' + 
	'register reinterpret_cast return selectany ' + 
	'sizeof static static_cast struct switch template this ' + 
	'thread throw true false try typedef typeid typename union ' + 
	'using uuid virtual void volatile whcar_t while';

	this.regexList = [
		{ regex: dp.sh.RegexLib.SingleLineCComments,				css: 'comment' },			// one line comments
		{ regex: dp.sh.RegexLib.MultiLineCComments,					css: 'comment' },			// multiline comments
		{ regex: dp.sh.RegexLib.DoubleQuotedString,					css: 'string' },			// strings
		{ regex: dp.sh.RegexLib.SingleQuotedString,					css: 'string' },			// strings
		{ regex: new RegExp('^ *#.*', 'gm'),						css: 'preprocessor' },
		{ regex: new RegExp(this.GetKeywords(datatypes), 'gm'),		css: 'datatypes' },
		{ regex: new RegExp(this.GetKeywords(keywords), 'gm'),		css: 'keyword' }
		];

	this.CssClass = 'dp-cpp';
	this.Style =	'.dp-cpp .datatypes { color: #2E8B57; font-weight: bold; }';
}

dp.sh.Brushes.Cpp.prototype	= new dp.sh.Highlighter();
dp.sh.Brushes.Cpp.Aliases	= ['cpp', 'c', 'c++'];
/* Python 2.3 syntax contributed by Gheorghe Milas */
dp.sh.Brushes.Python = function()
{
    var keywords =  'and assert break class continue def del elif else ' +
                    'except exec finally for from global if import in is ' +
                    'lambda not or pass print raise return try yield while';

    var special =  'None True False self cls class_'

    this.regexList = [
        { regex: dp.sh.RegexLib.SingleLinePerlComments, css: 'comment' },
        { regex: new RegExp("^\\s*@\\w+", 'gm'), css: 'decorator' },
        { regex: new RegExp("(['\"]{3})([^\\1])*?\\1", 'gm'), css: 'comment' },
        { regex: new RegExp('"(?!")(?:\\.|\\\\\\"|[^\\""\\n\\r])*"', 'gm'), css: 'string' },
        { regex: new RegExp("'(?!')*(?:\\.|(\\\\\\')|[^\\''\\n\\r])*'", 'gm'), css: 'string' },
        { regex: new RegExp("\\b\\d+\\.?\\w*", 'g'), css: 'number' },
        { regex: new RegExp(this.GetKeywords(keywords), 'gm'), css: 'keyword' },
        { regex: new RegExp(this.GetKeywords(special), 'gm'), css: 'special' }
        ];

    this.CssClass = 'dp-py';
	this.Style =	'.dp-py .builtins { color: #ff1493; }' +
					'.dp-py .magicmethods { color: #808080; }' +
					'.dp-py .exceptions { color: brown; }' +
					'.dp-py .types { color: brown; font-style: italic; }' +
					'.dp-py .commonlibs { color: #8A2BE2; font-style: italic; }';
}

dp.sh.Brushes.Python.prototype  = new dp.sh.Highlighter();
dp.sh.Brushes.Python.Aliases    = ['py', 'python'];
dp.sh.Brushes.Sql = function()
{
	var funcs	=	'abs avg case cast coalesce convert count current_timestamp ' +
					'current_user day isnull left lower month nullif replace right ' +
					'session_user space substring sum system_user upper user year';

	var keywords =	'absolute action add after alter as asc at authorization begin bigint ' +
					'binary bit by cascade char character check checkpoint close collate ' +
					'column commit committed connect connection constraint contains continue ' +
					'create cube current current_date current_time cursor database date ' +
					'deallocate dec decimal declare default delete desc distinct double drop ' +
					'dynamic else end end-exec escape except exec execute false fetch first ' +
					'float for force foreign forward free from full function global goto grant ' +
					'group grouping having hour ignore index inner insensitive insert instead ' +
					'int integer intersect into is isolation key last level load local max min ' +
					'minute modify move name national nchar next no numeric of off on only ' +
					'open option order out output partial password precision prepare primary ' +
					'prior privileges procedure public read real references relative repeatable ' +
					'restrict return returns revoke rollback rollup rows rule schema scroll ' +
					'second section select sequence serializable set size smallint static ' +
					'statistics table temp temporary then time timestamp to top transaction ' +
					'translation trigger true truncate uncommitted union unique update values ' +
					'varchar varying view when where with work';

	var operators =	'all and any between cross in join like not null or outer some';

	this.regexList = [
		{ regex: new RegExp('--(.*)$', 'gm'),						css: 'comment' },			// one line and multiline comments
		{ regex: dp.sh.RegexLib.DoubleQuotedString,					css: 'string' },			// double quoted strings
		{ regex: dp.sh.RegexLib.SingleQuotedString,					css: 'string' },			// single quoted strings
		{ regex: new RegExp(this.GetKeywords(funcs), 'gmi'),		css: 'func' },				// functions
		{ regex: new RegExp(this.GetKeywords(operators), 'gmi'),	css: 'op' },				// operators and such
		{ regex: new RegExp(this.GetKeywords(keywords), 'gmi'),		css: 'keyword' }			// keyword
		];

	this.CssClass = 'dp-sql';
	this.Style =	'.dp-sql .func { color: #ff1493; }' +
					'.dp-sql .op { color: #808080; }';
}

dp.sh.Brushes.Sql.prototype	= new dp.sh.Highlighter();
dp.sh.Brushes.Sql.Aliases	= ['sql'];
dp.sh.Brushes.Php = function()
{
	var funcs	=	'abs acos acosh addcslashes addslashes ' +
					'array_change_key_case array_chunk array_combine array_count_values array_diff '+
					'array_diff_assoc array_diff_key array_diff_uassoc array_diff_ukey array_fill '+
					'array_filter array_flip array_intersect array_intersect_assoc array_intersect_key '+
					'array_intersect_uassoc array_intersect_ukey array_key_exists array_keys array_map '+
					'array_merge array_merge_recursive array_multisort array_pad array_pop array_product '+
					'array_push array_rand array_reduce array_reverse array_search array_shift '+
					'array_slice array_splice array_sum array_udiff array_udiff_assoc '+
					'array_udiff_uassoc array_uintersect array_uintersect_assoc '+
					'array_uintersect_uassoc array_unique array_unshift array_values array_walk '+
					'array_walk_recursive atan atan2 atanh base64_decode base64_encode base_convert '+
					'basename bcadd bccomp bcdiv bcmod bcmul bindec bindtextdomain bzclose bzcompress '+
					'bzdecompress bzerrno bzerror bzerrstr bzflush bzopen bzread bzwrite ceil chdir '+
					'checkdate checkdnsrr chgrp chmod chop chown chr chroot chunk_split class_exists '+
					'closedir closelog copy cos cosh count count_chars date decbin dechex decoct '+
					'deg2rad delete ebcdic2ascii echo empty end ereg ereg_replace eregi eregi_replace error_log '+
					'error_reporting escapeshellarg escapeshellcmd eval exec exit exp explode extension_loaded '+
					'feof fflush fgetc fgetcsv fgets fgetss file_exists file_get_contents file_put_contents '+
					'fileatime filectime filegroup fileinode filemtime fileowner fileperms filesize filetype '+
					'floatval flock floor flush fmod fnmatch fopen fpassthru fprintf fputcsv fputs fread fscanf '+
					'fseek fsockopen fstat ftell ftok getallheaders getcwd getdate getenv gethostbyaddr gethostbyname '+
					'gethostbynamel getimagesize getlastmod getmxrr getmygid getmyinode getmypid getmyuid getopt '+
					'getprotobyname getprotobynumber getrandmax getrusage getservbyname getservbyport gettext '+
					'gettimeofday gettype glob gmdate gmmktime ini_alter ini_get ini_get_all ini_restore ini_set '+
					'interface_exists intval ip2long is_a is_array is_bool is_callable is_dir is_double '+
					'is_executable is_file is_finite is_float is_infinite is_int is_integer is_link is_long '+
					'is_nan is_null is_numeric is_object is_readable is_real is_resource is_scalar is_soap_fault '+
					'is_string is_subclass_of is_uploaded_file is_writable is_writeable mkdir mktime nl2br '+
					'parse_ini_file parse_str parse_url passthru pathinfo readlink realpath rewind rewinddir rmdir '+
					'round str_ireplace str_pad str_repeat str_replace str_rot13 str_shuffle str_split '+
					'str_word_count strcasecmp strchr strcmp strcoll strcspn strftime strip_tags stripcslashes '+
					'stripos stripslashes stristr strlen strnatcasecmp strnatcmp strncasecmp strncmp strpbrk '+
					'strpos strptime strrchr strrev strripos strrpos strspn strstr strtok strtolower strtotime '+
					'strtoupper strtr strval substr substr_compare';

	var keywords =	'and or xor __FILE__ __LINE__ array as break case ' +
					'cfunction class const continue declare default die do else ' +
					'elseif empty enddeclare endfor endforeach endif endswitch endwhile ' +
					'extends for foreach function include include_once global if ' +
					'new old_function return static switch use require require_once ' +
					'var while __FUNCTION__ __CLASS__ ' +
					'__METHOD__ abstract interface public implements extends private protected throw';

	this.regexList = [
		{ regex: dp.sh.RegexLib.SingleLineCComments,				css: 'comment' },			// one line comments
		{ regex: dp.sh.RegexLib.MultiLineCComments,					css: 'comment' },			// multiline comments
		{ regex: dp.sh.RegexLib.DoubleQuotedString,					css: 'string' },			// double quoted strings
		{ regex: dp.sh.RegexLib.SingleQuotedString,					css: 'string' },			// single quoted strings
		{ regex: new RegExp('\\$\\w+', 'g'),						css: 'vars' },				// variables
		{ regex: new RegExp(this.GetKeywords(funcs), 'gmi'),		css: 'func' },				// functions
		{ regex: new RegExp(this.GetKeywords(keywords), 'gm'),		css: 'keyword' }			// keyword
		];

	this.CssClass = 'dp-c';
}

dp.sh.Brushes.Php.prototype	= new dp.sh.Highlighter();
dp.sh.Brushes.Php.Aliases	= ['php'];
dp.sh.Brushes.Default = function()
{
	this.regexList = [
		{ regex: dp.sh.RegexLib.DoubleQuotedString,								css: 'string' },		// strings
		{ regex: dp.sh.RegexLib.SingleQuotedString,								css: 'string' },		// strings
		{ regex: new RegExp('\\b([\\d]+(\\.[\\d]+)?|0x[a-f0-9]+)\\b', 'gi'),	css: 'number' }
		];

	this.CssClass = 'dp-default';
	this.Style = '.dp-default .number { color: #C00000; }';
}

dp.sh.Brushes.Default.prototype	= new dp.sh.Highlighter();
dp.sh.Brushes.Default.Aliases	= ['default'];
