
var createAccountApp = angular.module('editAccountApp', []);

loginApp.controller('editAccountController',['$scope','$http','$window', function($scope, $http, $window) {
	
	    $scope.removeAccount = function (pass) {
	    	if (confirm("Are you sure you want delete your account?")) {
		    	if (confirm("Say again?...")) {
			    	var stringRemoveAccountreq = "removeAccount/?pass=" + pass;
			        $http.get("/HomeSharingExpenses/HSEServices/MemberService/" + stringRemoveAccountreq)
			        .then(function(response) {
			        	 var memberDeleted = "";
			        	  if(memberDeleted.localeCompare(response.data) === 0){
			        		  localStorage.removeItem("memberID");
			     			  $window.location.href = 'index.html';
			        	  }
			        	  else
			        		  $scope.errorStatus=response.data;
			        });	
		    	}
	    	}
	    } 
	}]);
