angular.module('ChatModule')
	.service('UserService', ['$http', '$q', function($http, $q) 
	    {
			var userService = 
			{
				getSubscridedRooms : function() 
				{
					return deferResult(function(){return $http.get("/users/rooms")})
				},
				subscribeOnRooms : function(data)
				{
					return deferResult(function() {return $http.post("/users/rooms", data)})
				},
				getAllUsers : function()
				{
					return deferResult(function(){return $http.get("/users/")})
				},
				removeUser : function(userId)
				{
					return deferResult(function(){return $http['delete']("/users/" + userId)})
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
			
			return userService
	}])