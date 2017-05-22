/**
 * 
 */
var homeController = angular.module('certificateApp.homeController', []);

homeController.controller('homeController', function($scope, $location, ngNotify,
		homeService) {
	

	
	$scope.listAllCertificates = function(){
		homeService.getCertificates().then(function(response){
			if(response.data != null)
				$scope.certificates=response.data;
		});
	}
	
	$scope.listAllCertificates();

	$scope.keySizes = [1024, 2048];
	
	$scope.NewKeystore = function() {
		homeService.newKeystore().success(function(data) {
			ngNotify.set('Successfully created key store.' , {
				type : 'success',
				duration: 3000,
				theme: 'pitchy'
			});
		});
	}

	$scope.OpenKeyStore = function() {
		$scope.resetOpenKeyStore();
		$('#openModal').modal('show');
	}

	$scope.open = {};
	$scope.certificates={};
	$scope.confirmOpenKeystore = function(open) {
		
		homeService.openKeyStore(fils, open.password).success(function(data) {
			homeService.getCertificates().then(function(response){
				if(response.data != null)
					$scope.certificates=response.data;
			});
			ngNotify.set('Successfully opened key store.' , {
				type : 'success',
				duration: 3000,
				theme: 'pitchy'
			});
			$('#openModal').modal('hide');
		});
	}

	var flnm = {};
	var fils = {};
	$scope.uploadedFile = function(file) {
		fils=file.files[0];
		flnm = file.value;
	}

	$scope.SaveKeyStore = function() {
		$('#saveModal').modal('show');
	}

	$scope.saved = {};
	$scope.confirmSaveKeystore = function(saved) {
		homeService.saveKeyStore(saved.filename, saved.password).success(
				function(data) {
					$scope.cleanFieldsSave();
					ngNotify.set('Successfully saved key store.' , {
						type : 'success',
						duration: 3000,
						theme: 'pitchy'
					})
				});
	}
	
	$scope.addExtension = function(save){
		var n = save.filename.endsWith(".jks");
		if(!n)
			save.filename = save.filename + ".jks";
	}
	
	$scope.generateCertificate = function() {
		$("#generateCertificateModal").modal("show");
	}
	
	$scope.resetOpenKeyStore = function(){
		$scope.open={};
		$("#file").val("");
	}
	
	$scope.cert={};
	$scope.cert.keySize = 1024;
	$scope.parent={};
	$scope.certificates=[];
	$scope.createCertificate = function(cert){
		if($scope.selfSigned)
			homeService.createRootCertificate(cert).success(
					function(data) {
						$scope.certificates.push(data);
						$scope.cleanFields();
						ngNotify.set('Successfully created root certificate.' , {
							type : 'success',
							duration: 3000,
							theme: 'pitchy'
						})
			});
		else
			homeService.createCertificate(cert,$scope.parent.alias,$scope.parent.password).success(
					function(data) {
						$scope.certificates.push(data);
						$scope.cleanFields();
						ngNotify.set('Successfully created certificate.' , {
							type : 'success',
							duration: 3000,
							theme: 'pitchy'
						})
						homeService.getCertificates().then(function(response){
							$scope.certificates=response.data;
						});
			});
	}
	
	$scope.certificateId="";
	$scope.getExisting = function(){
		homeService.getExisting($scope.certificateId).then(function(response){
			$scope.certificates={};
			$scope.certificates[0] = response.data;
		});
	}
	


	
	$scope.revokeCertificate = function(certificateId){
		homeService.revokeCertificate(certificateId).then(function(response){
			for(var i=0;i<response.data.length;i++){
				for(var j=0;j<$scope.certificates.length;j++){
					if(response.data[i].serialNumber==$scope.certificates[j].serialNumber){
						$scope.certificates.splice(j,1);
						break;
					}
				}
			}
		});
	}
	
	$scope.cleanFields = function(){
		$scope.cert = {};
	}
	
	$scope.cleanFieldsSave = function(){
		$scope.save = {};
	}
	
})