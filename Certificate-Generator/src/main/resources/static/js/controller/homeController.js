/**
 * 
 */
var homeController = angular.module('certificateApp.homeController', []);

homeController.controller('homeController', function($scope, $location,
		homeService) {

	$scope.NewKeystore = function() {
		homeService.newKeystore().success(function(data) {
			alert("newkeyst");
		});
	}

	$scope.OpenKeyStore = function() {
		$('#openModal').modal('show');
	}

	$scope.open = {};
	$scope.confirmOpenKeystore = function(open) {
		homeService.openKeyStore(fils, open.password).success(function(data) {
			alert("openedkeyst");
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
					alert("savedkeyst");
				});
	}

	$scope.generateCertificate = function() {
		$("#generateCertificateModal").modal("show");
	}

})