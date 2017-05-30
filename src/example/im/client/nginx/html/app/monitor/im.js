var ByteBuffer = window.dcodeIO.ByteBuffer;

var websockets = [];
var reconnInterval = 10;    //重连间隔
// var reconnLock = false;      //避免重复连接
// var needconn = true;      //是否需要重连

var lastInteractionTime = 0;//上一次交互时间
var heartbeatTimeout = 60 * 1000;
var heartbeatSendInterval = heartbeatTimeout / 2;

var stat = {
	received : 0  //收到次数
	,sent:0       //发送次数
	,connCount:0  //连成功次数
	,reconnCount:0 //重连次数
};

protobuf.load("chat.proto", function(err, root) {
	if (err) {
		throw err;
	}
	loadClass(root);
});

function loadClass(root) {
	AuthReqBody = root.lookup("org.tio.examples.im.common.packets.AuthReqBody");
	AuthRespBody = root.lookup("org.tio.examples.im.common.packets.AuthRespBody");
	
	JoinReqBody = root.lookup("org.tio.examples.im.common.packets.JoinReqBody");
	JoinGroupResult = root.lookup("org.tio.examples.im.common.packets.JoinGroupResult");
	JoinRespBody = root.lookup("org.tio.examples.im.common.packets.JoinRespBody");
	
	ChatReqBody = root.lookup("org.tio.examples.im.common.packets.ChatReqBody");
	ChatRespBody = root.lookup("org.tio.examples.im.common.packets.ChatRespBody");
	
	ChatType = root.lookup("org.tio.examples.im.common.packets.ChatType");
	DeviceType = root.lookup("org.tio.examples.im.common.packets.DeviceType");
	Command = root.lookup("org.tio.examples.im.common.packets.Command");
	
	console.log(Command.values.COMMAND_AUTH_REQ);  //3
	console.log(Command.valuesById[4]);   //COMMAND_AUTH_RESP
	console.dir(Command);
}

var connection_info = new Vue({
  el: '#connection_info',
  data: {
    count: 0
  }
});

function initWs(url) {
	try {
		var ws = new WebSocket(url);
		
		ws.binaryType = 'arraybuffer'; // 'blob';//
		initWsEvent(ws, url);
		return ws;
	} catch (e) {
		console.log(e);
		reconn(url, null, e);
	}
}

function initWsEvent(ws, url){
	ws.onopen = function(event) {
		var ws = event.srcElement;
		
		stat.connCount++;
		console.log(ws);
		websockets.push(ws);
		connection_info.count = websockets.length;
		
		console.log(event);
		
		lastInteractionTime = new Date().getTime();
		
		var command = Command.values.COMMAND_AUTH_REQ;
		var bodyData = {
			deviceId : "deviceId--888888888888"
			,seq : 1
			,deviceType : DeviceType.DEVICE_TYPE_PC
			,deviceInfo : "chrome"
			,token :"token"
		};
		sendPacket(ws, command, AuthReqBody, bodyData);
	};

	ws.onmessage = function(event) {
		var ws = event.srcElement;
		stat.received++;
		var arrayBuffer = event.data;
//		console.log("arrayBuffer.byteLength:"+arrayBuffer.byteLength);
		
		var byteBuffer = ByteBuffer.wrap(arrayBuffer);
		var command =  Command.valuesById[byteBuffer.readByte()];

//		console.log(byteBuffer);
		console.log("收到消息", command, byteBuffer);
		
		arrayBuffer = byteBuffer.toArrayBuffer();
		byteBuffer = ByteBuffer.wrap(arrayBuffer);
		
		var uint8Array = new Uint8Array(arrayBuffer);
		handler[command].call(handler[command], uint8Array, event, ws);
		
		lastInteractionTime = new Date().getTime();
	};

	ws.onclose = function(event) {
		var ws = event.srcElement;		
		websockets.remove(ws);
		connection_info.count = websockets.length;
		reconn(url, event, null);
	};

	ws.onerror = function(event){
		var ws = event.srcElement;
		console.log("error", event);
		//reconn(url, event, null)
	};
}
function reconn(url, event, e) {
// 	if (!needconn) {
// 		console.log("已经不需要重连了", event, e);
// 		return;
// 	}
	
// 	if (reconnLock) {
// 		console.log("没有拿到重连权限", event, e);
// 		return;
// 	}
	
// 	reconnLock = true;
	stat.reconnCount++;

	setTimeout(function() {
				console.log("开始第次" + stat.reconnCount + "重连:" + url, event, e);
				initWs(url);
// 				reconnLock = false;
			}, reconnInterval);
}


/**
 * 发送packet
 * 
 * @param {} ws
 * @param {} command
 * @param {} BodyClass
 * @param {} bodyData
 */
function sendPacket(ws, command, BodyClass, bodyData) {
    var bodyObj = null;
    if (bodyData) {
    	bodyObj = BodyClass.create(bodyData);
    }
    
    var bodyBuffer = null;
    if (bodyObj) {
    	bodyBuffer = BodyClass.encode(bodyObj).finish();
    }
	
	sendBuffer(ws, command, bodyBuffer);
}

/**
 * 发送buffer
 * @param {} ws
 * @param {} command
 * @param {} bodyBuffer
 */
function sendBuffer(ws, command, bodyBuffer){
	var bodyLength = 0;
	if (bodyBuffer) {
		bodyLength = bodyBuffer.length;
	}

	var allBuffer = ByteBuffer.allocate(1 + bodyLength);
	allBuffer.writeByte(command);

	if (bodyBuffer) {
		allBuffer.append(bodyBuffer);
	}
	ws.send(allBuffer.buffer);
	console.log("已经发送", Command.valuesById[command], allBuffer);
	lastInteractionTime = new Date().getTime();
}

/**
 * 发送心跳
 */
function ping()
{
	var nowTime = new Date().getTime();
	var iv = nowTime - lastInteractionTime; // 已经多久没发消息了
	if ((heartbeatSendInterval + iv) >= heartbeatTimeout) {
		var command = Command.values.COMMAND_HEARTBEAT_REQ;
		for(var i = 0; i < websockets.length; i++){
			var ws = websockets[i];
			sendBuffer(ws, command, null);
		}
	}
}
setInterval("ping()", heartbeatSendInterval);





/**
 * 消息处理者
 * @type 
 */
var handler = {};
handler.COMMAND_AUTH_RESP = function(uint8Array, event, ws){
	var respBody = AuthRespBody.decode(uint8Array);
	var groupele = document.getElementById("group");
	var command = Command.values.COMMAND_JOIN_GROUP_REQ;
	var bodyData = {
		group : groupele.value
	};
	sendPacket(ws, command, JoinReqBody, bodyData);
};

handler.COMMAND_JOIN_GROUP_RESP = function(uint8Array, event, ws){
	var respBody = JoinRespBody.decode(uint8Array);
//	console.log("已经进入到群组:" + respBody.group);
};


var chat = new Vue({
  el: '#chat',
  data: {
    chatRespBodys: [
      
    ]
  }
});
handler.COMMAND_CHAT_RESP = function(uint8Array, event, ws){
	var respBody = ChatRespBody.decode(uint8Array);
//	console.log("收到聊天消息:" + stat.received + "-" + respBody.text);
//	console.log(respBody);
//	console.log(respBody.time);
//	console.log(respBody.time.toNumber());
	
	if (chat.chatRespBodys.length > 200) {
		chat.chatRespBodys = [];
	}
	
	respBody.date = new Date(respBody.time.toNumber()).format('yyyy-MM-dd hh:mm:ss.S');
	chat.chatRespBodys.push(respBody);
};
