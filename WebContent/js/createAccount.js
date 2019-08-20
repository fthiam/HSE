
var createAccountApp = angular.module('createAccountApp', []);

loginApp.controller('createAccountController',['$scope','$http','$window', function($scope, $http, $window) {
	
	    $scope.createAccount = function () {
	    	
	    	//check that adress is correctly write 
	    	if($scope.validateEmail($scope.email)){
	    		//Check both pass are equals
		    	if($scope.password.localeCompare($scope.passwordConf) === 0)
	    		{
			    	var memberObj = {"name" : $scope.first_name,
		    						 "forename" : $scope.last_name,
		    						 "emailAdress" : $scope.email,
		    						 "passWord" : $scope.password};
			    	
			    	var memberJson = JSON.stringify(memberObj); 
			    	
			    	var stringConnection = "createMember/?member=" + memberJson;
			        $http.get("/HomeSharingExpenses/HSEServices/MemberService/" + stringConnection)
			        .then(function(response) {			        		
			        		if(response.data.indexOf("Account creation failed : ") == -1){
					            //redirect then to home page
					            $window.location.href = '#login';
				            }
				            else 
				            	$scope.createAccountStatus = response.data;
			        });
		    	}
		    	else
		    		$scope.createAccountStatus = "Wrong confirmation password, try again";
	    	}
	    	else
	    		$scope.createAccountStatus = "Adress mail is incorrect";
	    }
	    
	    $scope.validateEmail = function(email) {
	    	  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    	  return re.test(email);
	    	}
	    
	}]);
