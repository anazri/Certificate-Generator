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
		var fd = new FormData();
	    fd.append('file', file);
		return $http.post("/keystores/load/"+pw+"/"+pw, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined} });
	}
	
	temp.saveKeyStore = function(file, pw){
		return $http.post("/keystores/save/"+file+"/"+pw);
	}
	
	return temp;
})



