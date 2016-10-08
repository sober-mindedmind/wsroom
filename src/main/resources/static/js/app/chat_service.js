/**
 * 
 */
angular.module('ChatModule')
	.factory('ChatService', [function() {
		var service = 
		{
			connect : function(host)
			{
				this.connection = new WSConnection('ws://' + host + ':8080/room')
			},
			connection : null				
		};
		return service; 
	}]);