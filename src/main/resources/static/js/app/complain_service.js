/**
 * 
 */
angular.module('ChatModule')
	.service('ComplainService', ['$http', '$q', function($http, $q) 
	    {
			var complainService = {
					complainOnMessage : function(messageId, reason)
					{
						return Util.deferResult($q, function() {return $http.post("/messages/" + messageId + "/complaints", reason)})
					},
					getAllComplaints : function()
					{
						return Util.deferResult($q, function() {return $http.get("/complaints")})
					},
					removeComplaint : function(id)
					{
						return Util.deferResult($q, function() {return $http['delete']("/complaints/" + id)})
					}
			}
			return complainService
	    }])