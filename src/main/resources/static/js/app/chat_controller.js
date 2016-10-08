/**
 * 
 */

angular.module('ChatModule').controller('ChatController', 
		['$scope', 'ChatService', 'RoomService', 'UserService',
		 function($scope, ChatService, RoomService, UserService) 
		 {
			ChatService.connect(window.location.hostname)
			UserService.getPrincipal().then(function(principal){$scope.principal = principal})
			$scope.allMyRooms = []
			$scope.rooms = {}
			$scope.currentRoom = null
			$scope.messageContent = ''
			$scope.visibleChat = true;
			$scope.visibleRooms = false
			$scope.allRooms = {};
			$scope.notification = ''
			var self = this
			var typedChars = 0
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
						
			this.showChat = function(refresh)	
			{
				if (refresh)
				{
					this.fetchSubscribedRooms(false);
				}
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
					ChatService.connection.send($scope.currentRoom.obj.name, $scope.messageContent)
					$scope.messageContent = ''
				}	
				else
				{
					this.notification("You did not specify a chat room. At first choose room")
				}	
				typedChars = 0
			}
			
			this.subscribe = function(room)
			{				
				var callbacks = {
					onnewuser :
						function(frame) 
						{
							console.log(frame.body)
							var userName = JSON.parse(frame.body).name;
							self.notification("User " + userName + " has been connected")
							$scope.rooms[room].users.push(userName)
							$scope.$apply()							
						},						
			 		ontyping :  function(frame) 
			 		{
			 			var typing = JSON.parse(frame.body)
			 			if (typing.userName !== $scope.principal.name)
			 			{
			 				self.notification("User " + typing.userName + " is typing a message in " + typing.roomName, 1500)
			 				$scope.$apply()
			 			}		 			
			 		}, 
			 		onmessage : function(frame)
			 		{		 			
			 			$scope.rooms[room].messages.push(JSON.parse(frame.body))			 		
			 		    $scope.$apply()			 		   
			 		},
			 		onuserleave :  function(frame) 
			 		{
			 			var userName = JSON.parse(frame.body).name;
			 			self.notification("User " + userName + " is leaving us") 
			 			var users = $scope.rooms[room].users
			 			Util.removeElement(users, userName)
			 			$scope.$apply()		 			
			 		}, 
				}				
				ChatService.connection.subscribe(room, callbacks)
			}
			this.unsubscribe = function(room)
			{
				ChatService.connection.unsubscribe(room)
				delete $scope.rooms[room]
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
							self.showChat(true)
							rooms.forEach(function(room){self.subscribe(room.name)})
						})
					}
					else
					{
						self.showChat(false)
					}
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
			
			this.fetchSubscribedRooms(true);
		 }]);