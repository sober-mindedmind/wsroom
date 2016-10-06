/**
 * 
 */
angular.module('ChatModule')
	.factory('ChatService', [function() {
		var service = 
		{
			connection : new WSConnection('ws://localhost:8080/room'),				
		};
		return service; 
	}]);