	// create the module and name it mainApp
	var mainApp = angular.module('mainApp', ['ngRoute', 'loginApp', 'createAccountApp','editAccountApp', 'homeApp', 'groupApp', 'shoppingListApp', 'shoppingListsPresApp']);

	// configure our routes
	mainApp.config(function($routeProvider) {
		$routeProvider
			.when('/login', {
				templateUrl : 'pages/login.html',
				controller  : 'loginController'
			})
			.when('/createAccount', {
				templateUrl : 'pages/createAccount.html',
				controller  : 'createAccountController'
			})
			.when('/disconnect', {
				templateUrl : 'pages/disconnect.html',
				controller  : 'disconnectController'
			})
			.when('/home', {
				templateUrl : 'pages/home.html',
				controller : 'homeController',
				css : 'css/home.css'
			})
			.when('/editAccount', {
				templateUrl : 'pages/editAccount.html',
				controller : 'editAccountController',
				css : 'css/home.css'
			})
			.when('/group', {
				templateUrl : 'pages/group.html',
				controller : 'groupController'
			})
			.when('/shoppingListsSelection', {
				templateUrl : 'pages/shoppingListsSelection.html',
				controller : 'shoppingListsPresentation'
			})
			.when('/shoppingList', {
				templateUrl : 'pages/shoppingList.html',
				controller : 'shoppingListController'
			})
			.when('/groupHistoric', {
				templateUrl : 'pages/groupHistoric.html',
				controller : 'groupHistoryController'
			})
			 .otherwise({
			      templateUrl: 'pages/presentation.html'
			    });
	});
	
	mainApp.controller('showButtonsController',['$scope','$http','$window', function($scope, $http, $window) {
		// create a message to display in our view
		$scope.disconnectFunc = function () {
			 localStorage.removeItem("memberID");
			 $window.location.href = 'index.html';
			 $http.get("/HomeSharingExpenses/HSEServices/MemberService/logOff")
			        .then(function(response) {
			        	//log off completed...
			        });
		}		
		
		//Not connected
		if (localStorage.getItem("memberID") === null) {
	     	$scope.showLogin = true;
	     	$scope.showCreateAccount = true;
	     	$scope.showDisconnect = false;
	     	$scope.showUserHome = false;
	     	$scope.showEditAccount = false;
    	}else{//Connected
    		//set visible user action 
    		$scope.showUserHome = true;
    		$scope.showEditAccount = true;
    		$scope.showDisconnect = true;
    		$scope.showLogin = false;
    		$scope.showCreateAccount = false;
    	}
	}]);