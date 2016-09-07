/**
 * 
 */
angular.module('ChatModule', [])
	.factory('ChatService', ['$http', '$q', function($http, $q) {
		var service = {
				connection : new WSConnection('ws://localhost:8080/room'),
				getSubscridedRooms : function() 
				{
					return deferResult(function(){return $http.get("/users/rooms")})
				},
				getUsersInRoom : function(room)
				{
					return deferResult(function() {return $http.get("/rooms/" + room + "/ausers/")})
				},
				getAllRooms : function()
				{
					return deferResult(function() {return $http.get("/rooms/")})
				},
				subscribeOnRooms : function(data)
				{
					return deferResult(function() {return $http.post("/users/rooms", data)})
				}
		};
		
		function deferResult(asyncCall) 
		{
			var deferred = $q.defer();
			asyncCall().then(function (response){deferred.resolve(response.data);},
				             function(errResponse)
				             {   
								console.error('Error while fetching Users');
				                deferred.reject(errResponse);
				             }		           
            );
			return deferred.promise;
		}		
		
		return service; 
	}]);