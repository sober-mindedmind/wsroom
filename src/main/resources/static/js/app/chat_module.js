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
