var app = angular.module("certificateApp.route", [ "ngRoute" ]);

app.config(function($routeProvider) {
	$routeProvider.when("/", {
		templateUrl : "html/index.html"
	}).when("/login", {
		templateUrl : "html/login.html"
	}).when("/home", {
		templateUrl : "html/index.html"
	});
});