
var loginApp = angular.module('loginApp', []);

loginApp.controller('loginController',['$scope','$http','$window', function($scope, $http, $window) {
		// create a message to display in our view
	//publish test
	    $scope.connectFunc = function () {
	    	var stringConnection = "login/?email=" + $scope.email +
	    								 "&pass=" + $scope.pass;
	        $http.get("/HomeSharingExpenses/HSEServices/MemberService/" + stringConnection)
	        .then(function(response) {
	           
	            if(response.data.indexOf("Authentification failed") == -1){
	            	$scope.id = response.data;
	            	 //Success then put ID (for now) in localStorage
		            localStorage.setItem("memberID", $scope.id);
		            //redirect then to home page
		            $window.location.href = 'index.html';
	            }
	            else 
	            	$scope.connectionStatus = response.data;
	        });
	    }
	}]);