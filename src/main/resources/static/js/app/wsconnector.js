/** 
 * class WSConnection.  Responsible for establishing web-socket connections. The web-socket connection is established 
 * over the STOMP protocol.  
 */
var Path =
{
		CHAT_APP_DEST           : "/app/chat/",
		CHAT_TOPIC_DEST	        : "/topic/chat/",
		
		USER_JOIN_APP_DEST      : "/app/join/",
		USER_JOIN_TOPIC_DEST    : "/topic/join/",
		
		USER_LEAVE_APP_DEST     : "/app/leave/",
		USER_LEAVE_TOPIC_DEST   : "/topic/leave/",
		
		TYPING_APP_DEST   	    : "/app/typing/",
		TYPING_TOPIC_DEST 	    : "/topic/typing/", 
		
		DELETE_MESSAGE_TOPIC_DEST : "/topic/delete_msg/"
}

function WSConnection(url)
{	
	var socket = new WebSocket(url);
	socket.onconnect = function()
	{
		console.log('Connected: ');
	}
	this.stompClient = Stomp.over(socket);
	this.stompClient.connect({}, function(frame)
	{
		console.log('Connected: ' + frame);
	});
}

WSConnection.prototype.subscribe = function (room, callbacks) 
{
	this.stompClient.subscribe(Path.USER_JOIN_TOPIC_DEST + room, callbacks.onnewuser);
	this.stompClient.subscribe(Path.TYPING_TOPIC_DEST + room, callbacks.ontyping);
	this.stompClient.subscribe(Path.CHAT_TOPIC_DEST + room, callbacks.onmessage);
	this.stompClient.subscribe(Path.USER_LEAVE_TOPIC_DEST + room, callbacks.onuserleave);
	this.stompClient.subscribe(Path.DELETE_MESSAGE_TOPIC_DEST + room, callbacks.ondeletemsg);
}

WSConnection.prototype.disconnect = function()
{	
	if (this.stompClient != null) {
		this.stompClient.disconnect();
	}
	console.log("Disconnected");
}

WSConnection.prototype.unsubscribe = function(room)
{	
	this.stompClient.unsubscribe(Path.CHAT_TOPIC_DEST + room);	
	this.stompClient.unsubscribe(Path.USER_JOIN_TOPIC_DEST + room);
	this.stompClient.unsubscribe(Path.TYPING_TOPIC_DEST + room);
	this.stompClient.unsubscribe(Path.USER_LEAVE_TOPIC_DEST + room);
	this.stompClient.unsubscribe(Path.DELETE_MESSAGE_TOPIC_DEST + room);
}

WSConnection.prototype.sendTyping = function (room, text)
{
	this.stompClient.send(Path.TYPING_APP_DEST + room, 
			 {},
			 JSON.stringify({'text' : text}));
}

WSConnection.prototype.send = function (room, text)
{
	this.stompClient.send(Path.CHAT_APP_DEST + room, 
					 {}, 
					 JSON.stringify({'text' : text}));
	console.log("Send message")
}