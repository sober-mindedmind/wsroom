/**
 * 
 */

angular.module('ChatModule').controller('ChatController', 
		['$scope', 'ChatService', 'RoomService', 'UserService',
		 function($scope, ChatService, RoomService, UserService) 
		 {
			ChatService.connect(window.location.hostname)
			UserService.getPrincipal().then(function(principal){$scope.principal = principal})
			
			/* rooms that were created by the current user */
			$scope.allMyRooms = []
			
			/* rooms to which the current user is connected */
			$scope.rooms = {}
			
			/* currently active room */
			$scope.currentRoom = null
			
			$scope.messageContent = ''
			$scope.visibleChat = true;
			$scope.visibleRooms = false
			
			/* all rooms registered in the application */
			$scope.allRooms = {};
			
			$scope.notification = ''
			var self = this
			var typedChars = 0
			var editableMessage = null
			var edit = false
			
			/** Fetches all rooms created in the application */
			this.fetchAllRooms = function()
			{
				RoomService.getAllRooms().then
				(
					function(rooms)
					{
						$scope.allRooms = rooms
					}
				)
			}
			
			this.startTyping = function()
			{				
				if ($scope.currentRoom && typedChars++ % 3 == 0)
				{
					ChatService.connection.sendTyping($scope.currentRoom.obj.name, '')
				}
			}
			
			this.onRoomClick = function(room)
			{
				room.visible = !room.visible
				$scope.currentRoom = room
			}
			
			this.notification = function(text, time)
			{
				$scope.notification = text;
				clearAfterDelay(time ? time : 2000)
			}
			
			this.sendMessage = function() 
			{
				if ($scope.currentRoom)
				{
					if (edit)
					{
						editableMessage.text = $scope.messageContent
						editableMessage && this.updateMessage(editableMessage)
						edit = false
						editableMessage = null
					}
					else
					{
						ChatService.connection.send($scope.currentRoom.obj.name, $scope.messageContent)
					}				
					$scope.messageContent = ''
				}	
				else
				{
					this.notification("You did not specify a chat room. At first choose room")
				}	
				typedChars = 0
			}
			
			/** Subscribes the current user on the given room */
			this.subscribe = function(room)
			{
				var callbacks = {
						
					/** Will be called when a new user connects to some room to which the current user is already connected */
					onnewuser :	function(frame) 
					{
						console.log(frame.body)
						var userName = JSON.parse(frame.body).name;
						self.notification("User " + userName + " has been connected")
						$scope.rooms[room].users.push(userName)
						$scope.$apply()
					},
					
					/** Will be called when some user in a room starts to typing a message */
			 		ontyping :  function(frame)
			 		{
			 			var typing = JSON.parse(frame.body)
			 			if (typing.userName !== $scope.principal.name)
			 			{
			 				self.notification("User " + typing.userName + " is typing a message in " + typing.roomName, 1500)
			 				$scope.$apply()
			 			}
			 		}, 
			 		
			 		/** Will be called when a new message arrives */
			 		onmessage : function(frame)
			 		{		 			
			 			$scope.rooms[room].messages.push(JSON.parse(frame.body))			 		
			 		    $scope.$apply() 		   
			 		},
			 		
			 		/** Will be called when a user in a room disconnects from the room */
			 		onuserleave :  function(frame) 
			 		{
			 			var userName = JSON.parse(frame.body).name;
			 			self.notification("User " + userName + " is leaving us")
			 			var users = $scope.rooms[room].users
			 			Util.removeElement(users, userName)
			 			$scope.$apply()		 			
			 		},
			 		
			 		/** Will be called when some user in a room deletes his message from the room */
			 		ondeletemsg: function(frame)
			 		{
			 			var id = JSON.parse(frame.body).id
			 			findMessage(id, function(messages, msg){Util.removeElement(messages, msg)});			 			
			 			$scope.$apply()
			 		},
			 		
			 		/** Will be called when a user decides to update text data in a message */
			 		onupdatemsg : function(frame)
			 		{
			 			var receivedMsg = JSON.parse(frame.body)
			 			findMessage(receivedMsg.id, function(messages, msg){msg.text = receivedMsg.text});	
			 			$scope.$apply()
			 		}
				}
				ChatService.connection.subscribe(room, callbacks)
			}
			this.unsubscribe = function(room)
			{
				ChatService.connection.unsubscribe(room)
				delete $scope.rooms[room]
			}
			
			function findMessage(id, action)
			{
				var messages = $scope.currentRoom.messages
	 			for (var i = 0; i < messages.length; i++)
	 			{
	 				if (messages[i].id === id)
	 				{
	 					action(messages, messages[i]);	 					
	 				}
	 			}
			}			
			
			function clearAfterDelay(time)
			{
				setTimeout(function(){$scope.notification = ''; $scope.$apply()}, time);
			}
						
			this.fetchSubscribedRooms = function(connectToRooms)
			{
			 	UserService.getSubscridedRooms().then
			 	(
			            function(rooms)
			            {			            	
			            	if (!rooms)
			            	{
			            		this.fetchAllRooms();
			            		return;
			            	}
			            	
			            	rooms.forEach(function(room)
			            	{			            		
			            		var roomModel = {messages: [], users: [], obj: room}
			            		$scope.rooms[room.name] = roomModel
			            		if (room.activeUsers)
			            		{
			            			room.activeUsers.forEach(function(user)
			            			{
			            				roomModel.users.push(user)
				            		})
			            		}
			            		if (room.messages)
			            		{
			            			roomModel.messages = room.messages
			            		}			            		
			            	})
			            			            	
			            	if (connectToRooms)
		            		{
			            		rooms.forEach(function(room)
			            		{
			            			self.subscribe(room.name)
			            			setTimeout(function(){$('#' + room.name).collapse('hide')}, 500);
			            		})
		            		}
			            },
			            function(errResponse){
			                console.error('Error while fetching rooms');
			            }
			        );			 	
			 }
				
			this.subscribeOnRooms = function()
			{				
				if ($scope.allRooms)
				{
					var rooms = []
					$scope.allRooms.forEach(function(e)
					{
						if (e.checked)
						{
							delete e.checked
							rooms.push(e)
						}
					})
					if (rooms.length > 0)
					{
						var self = this
						UserService.subscribeOnRooms(rooms).then(function()
						{
							this.fetchSubscribedRooms(false);
							rooms.forEach(function(room){self.subscribe(room.name)})
						})
					}
				}
			}
			
			this.startEdit = function(message)
			{				 
				if (edit = !edit)
				{
					$scope.messageContent = message.text
					editableMessage = message
				}
				else
				{
					$scope.messageContent = ""
				}				
			}			
			
			this.fetchAllMyRooms = function()
			{
				RoomService.getMyRooms().then(function(rooms){$scope.allMyRooms = rooms;})
			}
			
			this.removeRoom = function(roomName)
			{
				RoomService.removeRoom(roomName).then(function(){$scope.allMyRooms.splice($scope.allMyRooms.indexOf(roomName), 1)}) 
			}
						
			/** 
			 * Removes the specified message from the current room, all clients of the current room will be notified if the message
			 * is successfully removed
			 */
			this.removeMessage = function(message)
			{
				/* TODO replace room name on room id*/
				UserService.removeMessage(message.owner, $scope.currentRoom.obj.name, message.id)
			}			

			this.updateMessage = function(message)
			{
				UserService.updateMessage(message.owner, $scope.currentRoom.obj.name, message)
			}
			
			this.fetchSubscribedRooms(true);
		 }]);