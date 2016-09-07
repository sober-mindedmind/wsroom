/**
 * 
 */

angular.module('ChatModule').controller('ChatController', 
		['$scope', 'ChatService',
		 function($scope, ChatService) 
		 {
			$scope.rooms = {}
			$scope.currentRoom = null
			$scope.messageContent = ''
			$scope.visibleChat = true;
			$scope.visibleRooms = false
			$scope.allRooms = {};
			$scope.notification = ''
												
			this.fetchAllRooms = function()
			{
				ChatService.getAllRooms().then
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
					this.fetchSubscribedRooms();
				}
			}	
			
			this.startTyping = function()
			{
				ChatService.connection.sendTyping($scope.currentRoom.obj.name, '')
			}
			
			this.onRoomClick = function(room)
			{
				room.visible = !room.visible
				$scope.currentRoom = room
			}
				
			this.sendMessage = function() 
			{
				ChatService.connection.send($scope.currentRoom.obj.name, $scope.messageContent)
				$scope.messageContent = ''
			}
			
			this.subscribe = function(room)
			{
				var callbacks = {
					onnewuser :
						function(frame) 
						{
							var userName = JSON.parse(frame.body).name;
							$scope.notification = "User " + userName + " has been connected"
							$scope.rooms[room].users.push(userName)
							$scope.$apply()
							clearAfterDelay(2000)
						},						
			 		ontyping :  function(frame) 
			 		{
			 			$scope.notification = "User " + JSON.parse(frame.body).name + " is typing a message"
			 			$scope.$apply() 
			 			clearAfterDelay(1000)
			 		}, 
			 		onmessage : function(frame)
			 		{			 			
			 			
			 			$scope.rooms[room].messages.push(JSON.parse(frame.body))			 		
			 		    $scope.$apply()			 		   
			 		},
			 		onuserleave :  function(frame) 
			 		{
			 			var userName = JSON.parse(frame.body).name;
			 			$scope.notification = "User " + userName + " is leaving us" 
			 			var users = $scope.rooms[room].users			 			
			 			users.splice(users.indexOf(userName), 1)
			 			$scope.$apply()
			 			clearAfterDelay(2000)
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
						
			this.fetchSubscribedRooms = function()
			{
			 	ChatService.getSubscridedRooms().then
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
						ChatService.subscribeOnRooms(rooms).then(function(){self.showChat(true)})
					}
					else
					{
						self.showChat(false)
					}
				}
			}			
			
			this.fetchSubscribedRooms();
		 }]);