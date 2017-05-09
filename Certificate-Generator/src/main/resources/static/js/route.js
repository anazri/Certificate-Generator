var app = angular.module("certificateApp.route", [ "ngRoute" ]);

app.config(function($routeProvider) {
	$routeProvider.when("/", {
		templateUrl : "html/home.html"
	}).when("/home", {
		templateUrl : "html/home.html"
	});
});