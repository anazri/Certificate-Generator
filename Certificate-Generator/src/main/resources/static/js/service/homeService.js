/**
 * 
 */

var homeService = angular.module('certificateApp.homeService', []);

homeService.factory('homeService', function($http) {
	var temp={};
	
	temp.newKeystore = function(){
		return $http.get("/keystores/create");
	}
	
	temp.openKeyStore = function(file, pw){
		return $http.post("/keystores/load/"+file+"/"+pw);
	}
	
	temp.saveKeyStore = function(file, pw){
		return $http.post("/keystores/save/"+file+"/"+pw);
	}
	
	return temp;
})



