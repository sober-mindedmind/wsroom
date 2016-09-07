/** 
 * class WSConnection.  Responsible for establishing web-socket connections. The web-socket connection establishes 
 * over STOMP protocol.  
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
		TYPING_TOPIC_DEST 	    : "/topic/typing/"
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
	this.stompClient.subscribe("/user/queue/" + room, callbacks.onmessage)
	this.stompClient.subscribe(Path.USER_LEAVE_TOPIC_DEST + room, callbacks.onuserleave);	
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
}

WSConnection.prototype.sendTyping = function (room, text)
{
	this.stompClient.send("/app/typing/" + room, 
			 {},
			 JSON.stringify({'text' : text}));
}

WSConnection.prototype.send = function (room, text)
{
	this.stompClient.send("/app/chat/" + room, 
					 {}, 
					 JSON.stringify({'text' : text}));
	console.log("Send message")
}
	
WSConnection.prototype.sendToUser = function (room, user, text)
{
	this.stompClient.send("/app/private/chat/" + room + "/" + user, 
					 {}, 
					 JSON.stringify({'text' : text}));
	console.log("Send message")
}