Object.Event={
extend:function(object){
object._objectEventSetup=function(event_name){
this._observers=this._observers||{};
this._observers[event_name]=this._observers[event_name]||[];
};
object.observe=function(event_name,observer){
if(typeof(event_name)=='string'&&typeof(observer)!='undefined'){
this._objectEventSetup(event_name);
if(!this._observers[event_name].include(observer))
this._observers[event_name].push(observer);
}else
for(var e in event_name)
this.observe(e,event_name[e]);
};
object.stopObserving=function(event_name,observer){
this._objectEventSetup(event_name);
if(event_name&&observer)
this._observers[event_name]=this._observers[event_name].without(observer);
else if(event_name)
this._observers[event_name]=[];
else
this._observers={};
};
object.observeOnce=function(event_name,outer_observer){
var inner_observer=function(){
outer_observer.apply(this,arguments);
this.stopObserving(event_name,inner_observer);
}.bind(this);
this._objectEventSetup(event_name);
this._observers[event_name].push(inner_observer);
};
object.notify=function(event_name){
this._objectEventSetup(event_name);
var collected_return_values=[];
var args=$A(arguments).slice(1);
try{
for(var i=0;i<this._observers[event_name].length;++i)
collected_return_values.push(this._observers[event_name][i].apply(this._observers[event_name][i],args)||null);
}catch(e){
if(e==$break)
return false;
else
throw e;
}
return collected_return_values;
};
if(object.prototype){
object.prototype._objectEventSetup=object._objectEventSetup;
object.prototype.observe=object.observe;
object.prototype.stopObserving=object.stopObserving;
object.prototype.observeOnce=object.observeOnce;
object.prototype.notify=function(event_name){
if(object.notify){
var args=$A(arguments).slice(1);
args.unshift(this);
args.unshift(event_name);
object.notify.apply(object,args);
}
this._objectEventSetup(event_name);
var args=$A(arguments).slice(1);
var collected_return_values=[];
try{
if(this.options&&this.options[event_name]&&typeof(this.options[event_name])=='function')
collected_return_values.push(this.options[event_name].apply(this,args)||null);
for(var i=0;i<this._observers[event_name].length;++i)
collected_return_values.push(this._observers[event_name][i].apply(this._observers[event_name][i],args)||null);
}catch(e){
if(e==$break)
return false;
else
throw e;
}
return collected_return_values;
};
}
}
};
var HotKey=Class.create({
initialize:function(letter,callback,options){
if(!(letter instanceof Number))letter=letter.toUpperCase().charCodeAt(0);
HotKey.hotkeys.push(this);
this.options=Object.extend({
element:false,
shiftKey:false,
altKey:false,
ctrlKey:true
},options||{});
this.letter=letter;
this.callback=callback;
this.element=$(this.options.element||document);
this.handler=function(event){
if(!event||(this.letter==event.keyCode&&((!this.options.shiftKey||(this.options.shiftKey&&event.shiftKey))&&(!this.options.altKey||(this.options.altKey&&event.altKey))&&(!this.options.ctrlKey||(this.options.ctrlKey&&event.ctrlKey))))){
this.callback(event);
event.cancelBubble=true;
event.returnValue=false;
if(event.stopPropagation){
event.stopPropagation();
event.preventDefault();
}
return false;
}
}.bind(this);
this.enable();
},
trigger:function(){
this.handler();
},
enable:function(){
this.element.observe('keydown',this.handler);
},
disable:function(){
this.element.stopObserving('keydown',this.handler);
},
destroy:function(){
this.disable();
HotKey.hotkeys=Control.HotKey.hotkeys.without(this);
}
});
Object.extend(HotKey,{
hotkeys:[]
});
Object.Event.extend(HotKey);