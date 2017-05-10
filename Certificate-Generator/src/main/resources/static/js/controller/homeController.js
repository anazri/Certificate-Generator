/**
 * 
 */
var homeController = angular.module('certificateApp.homeController', []);

homeController.controller('homeController', function($scope, $location, ngNotify,
		homeService) {

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
	$scope.confirmOpenKeystore = function(open) {
		
		homeService.openKeyStore(fils, open.password).success(function(data) {
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
					ngNotify.set('Successfully saved key store.' , {
						type : 'success',
						duration: 3000,
						theme: 'pitchy'
					})
				});
	}

	$scope.generateCertificate = function() {
		$("#generateCertificateModal").modal("show");
	}
	
	$scope.resetOpenKeyStore = function(){
		$scope.open={};
		$("#file").val("");
	}
	
	$scope.cert={};
	$scope.parent={};
	$scope.createCertificate = function(cert){
		if($scope.selfSigned)
			homeService.createRootCertificate(cert).success(
					function(data) {
						ngNotify.set('Successfully created root certificate.' , {
							type : 'success',
							duration: 3000,
							theme: 'pitchy'
						})
			});
		else
			homeService.createCertificate(cert,$scope.parent.alias,$scope.parent.password).success(
					function(data) {
						ngNotify.set('Successfully created certificate.' , {
							type : 'success',
							duration: 3000,
							theme: 'pitchy'
						})
			});
	}

})