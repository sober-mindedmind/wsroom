/**
 * 
 */
angular.module('ChatModule').controller('AdminController', 
		['$scope', 'RoomService', 'UserService', 'ComplainService',
		 function($scope, RoomService, UserService, ComplainService)
		 {			
			UserService.getAllUsers().then
			(
				function(users)
				{
					$scope.users = users;
				}, 
				function()
				{
					console.error("Can't load the list of users")
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
			ComplainService.getAllComplaints().then(
					function(complaints)
					{
						$scope.complaints = complaints
					},
					function()
					{
						console.error("Can't fetch all complaints")
					}					
			)
			
			this.removeComplaint = function(complaint)
			{
				ComplainService.removeComplaint(complaint.id).then
				(
						function()
						{
							Util.removeElement($scope.complaints, complaint)
						},
						function()
						{
							console.error("Can't remove the given " + complaint.id + " compliant")
						}
				)
			}
			
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