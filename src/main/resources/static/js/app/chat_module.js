/**
 * 
 */
 
var ChatModule = angular.module('ChatModule',[]);

/* Utility class */
function Util() 
{}

Util.removeElement = function removeElement(arr, el)
{
	var index = arr.indexOf(el)
	if (index > -1)
	{
		arr.splice(index, 1)
	}	
}

Util.deferResult = function ($q, asyncCall) 
{
	var deferred = $q.defer();
	asyncCall().then(function (response){deferred.resolve(response.data);},
		             function(errResponse)
		             {   
						console.error('Error while making a request');
		                deferred.reject(errResponse);
		             }		           
    );
	return deferred.promise;
}
