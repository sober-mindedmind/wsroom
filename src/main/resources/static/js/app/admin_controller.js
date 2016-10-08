/**
 * 
 */
angular.module('ChatModule').controller('AdminController', 
		['$scope', 'RoomService', 'UserService',
		 function($scope, RoomService, UserService)
		 {			
			UserService.getAllUsers().then
			(
				function(users)
				{
					$scope.users = users;
				}, 
				function()
				{
					console.error("Can't load the list of userrs")
				}
			)
			RoomService.getAllExistingRooms().then
			(
					function(rooms)
					{
						$scope.rooms = rooms
					},
					function()
					{
						console.error("Can't load the list of rooms")
					}
			)
			this.removeUser = function(user)
			{
				UserService.removeUser(user.name).then
				(
						function() 
						{
							Util.removeElement($scope.users, user)
						}, 
						function()
						{
							console.error("Can't remove chosen user")	
						}
				)
			}
			this.removeRoom = function(room)
			{
				RoomService.removeRoom(room.name).then
				(
						function() 
						{
							Util.removeElement($scope.rooms, room)
						}, 
						function()
						{
							console.error("Can't remove chosen user")
						}
				)
			}
		}])