function connectionBtnClicked(){
	var serverele = document.getElementById('server');
	var portele = document.getElementById('port');
	var countele = document.getElementById('count');
	var wsUrl = "ws://" + serverele.value + ":" + portele.value;
	for (var i = 0; i < countele.value; i++){
		initWs(wsUrl);
	}
}

function sendBtnClicked(){
	var textele = document.getElementById('text');
	
	var command = Command.values.COMMAND_CHAT_REQ;
	var bodyData = {
		text : textele.value
		,group : 'g'
		,type : ChatType.values.CHAT_TYPE_PUBLIC
	};
	
	for(var i = 0; i < websockets.length; i++){
		var ws = websockets[i];
		sendPacket(ws, command, ChatReqBody, bodyData);
		break;
	}
}
