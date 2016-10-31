angular.module('ChatModule')
	.service('RoomService', ['$http', '$q', function($http, $q) 
	    {
			var roomService = 
			{
				getUsersInRoom : function(room)
				{
					return deferResult(function() {return $http.get("/rooms/" + room + "/ausers/")})
				},
				/* all rooms visible to this user */
				getAllRooms : function()
				{
					return deferResult(function() {return $http.get("/rooms/")})
				},
				getAllExistingRooms : function()
				{
					return deferResult(function() {return $http.get("/rooms?all")})
				},
				getMyRooms : function(data)
				{
					return deferResult(function() {return $http.get("/rooms/myrooms")})
				}, 
				removeRoom : function(roomName) 
				{
					return deferResult(function() {return $http['delete']("/rooms/" + roomName)})
				}
				,
				banUser : function(roomName, user, ban)
				{
					console.log(user)
					return deferResult(function() {return $http.post("/rooms/" + roomName + "/bannedUsers?ban=" + ban, user)})
				}
			}
			
			function deferResult(asyncCall) 
			{
				var deferred = $q.defer();
				asyncCall().then(function (response){deferred.resolve(response.data);},
					             function(errResponse)
					             {   
									console.error('Error while making request');
					                deferred.reject(errResponse);
					             }		           
			    );
				return deferred.promise;
			}		
			
			return roomService
	}
	
	
	
	])