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
		return $http.post("/keystores/load/"+pw, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined} });
	}
	
	temp.saveKeyStore = function(file, pw){
		return $http.post("/keystores/save/"+file+"/"+pw);
	}
	
	temp.createRootCertificate = function(cert){
		return $http({
			method : 'POST',
			url: '../certificates/generateRoot',
			data: {
				"alias": cert.alias,
				"surname": cert.surname,
				"givenName": cert.givenName,
				"o" : cert.o,
				"ou": cert.ou,
				"c" : cert.c,
				"e": cert.e,
				"password": cert.password,
				"ca": cert.ca,
				"numberOfDays": cert.numberOfDays,
				"keySize": cert.keySize,
				"serialNumber": cert.serialNumber
			}
		});
	}
	
	temp.createCertificate = function(cert,parentAlias,parentPassword){
		return $http({
			method : 'POST',
			url: '../certificates/generateCertificate/'+parentAlias+'/'+parentPassword,
			data: {
				"alias": cert.alias,
				"surname": cert.surname,
				"givenName": cert.givenName,
				"o" : cert.o,
				"ou": cert.ou,
				"c" : cert.c,
				"e": cert.e,
				"password": cert.password,
				"ca": cert.ca,
				"numberOfDays": cert.numberOfDays,
				"keySize": cert.keySize,
				"serialNumber": cert.serialNumber
			}
		});
	}
	
	temp.getCertificates = function(){
		return $http({
			method : 'GET',
			url: '../certificates/getCertificates'
		});
	}
	
	temp.getExisting = function(certificateId){
		return $http({
			method : 'GET',
			url: '../certificates/getExisting/'+certificateId
		});
	}
	
	temp.revokeCertificate = function(certificateId){
		return $http({
			method : 'POST',
			url: '../certificates/revoke/'+certificateId
		});
	}
	
	return temp;
})



