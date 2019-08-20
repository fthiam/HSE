
var homeApp = angular.module('homeApp', ['angularModalService']);

homeApp.controller('homeController',['$scope','$http','$window','ModalService', function($scope, $http, $window, ModalService) {

		$scope.testCallFunc ="false";
		$scope.memberId = localStorage.getItem("memberID");
		$scope.groupsInvitations=[];
		$scope.groups=[];
		var notConnected = "Not connected";
		
		//redirect if user not connected
		if(!($scope.memberId))
			$window.location.href = '#';
			
		// ======================================
	    // =       GET MEMBER NAME			    =
	    // ======================================
		var StringGetMemberNameReq = "getMemberName/";
		$http.get("/HomeSharingExpenses/HSEServices/MemberService/" + StringGetMemberNameReq)
	    .then(function(response) {
	        $scope.memberName = response.data;
	    });
		// ======================================
	    // =      refreshDocumentElements	    =
	    // ======================================
		$scope.refreshDocumentElements = function(){
			if ($scope.groupsInvitations.length == 0)
				document.getElementById('invitations').style.display = "none";
			else
				document.getElementById('invitations').style.display = "";
			
			if ($scope.groups.length == 0)
				document.getElementById('groups').style.display = "none";
			else
				document.getElementById('groups').style.display = "";
		}
		// ======================================
	    // =  refreshInvitations			 	=
	    // ======================================
		var StringGetMemberInvitations = "getGroupInvitations/";
		$scope.refreshInvitations = function(){
			$http.get("/HomeSharingExpenses/HSEServices/MemberService/" + StringGetMemberInvitations)
	        .then(function(response) {
	            $scope.groupsInvitations = response.data;
	            $scope.refreshDocumentElements();
	        });
		}
		// ======================================
	    // =  refreshGroups					 	=
	    // ======================================
		var StringGetMemberGroups = "getGroups/";
		$scope.refreshGroups = function(){
			$http.get("/HomeSharingExpenses/HSEServices/MemberService/" + StringGetMemberGroups)
	        .then(function(response) {
	        	//Check connection status
	    		if(notConnected.localeCompare(response.data) === 0){
	    			$window.alert("You are not correctcly connected! Try again!");
	    			//clean local storage
	    			localStorage.removeItem("memberID");
	    			$window.location.href = 'index.html';
	    		}
	    		else{
		            $scope.groups = response.data;
		            $scope.refreshDocumentElements();
	    		}
	        });
		}
		// ======================================
	    // =        GROUPS FOR CURRENT MEMBER   =
	    // ======================================
		$scope.refreshGroups();
		if(!$scope.groups)
			 document.getElementById('groups').style.display='block';
		// ======================================
	    // =  INVITATIONS FOR CURRENT MEMBER    =
	    // ======================================
		$scope.refreshInvitations();
		// ======================================
	    // =  acceptInvitation			 	    =
	    // ======================================
		$scope.acceptInvitationFunc = function (id) {
			var invId = id;
	    	var stringAcceptInvitationReq = "acceptInvitation/?invitationId=" + invId;
	        $http.get("/HomeSharingExpenses/HSEServices/MemberService/" + stringAcceptInvitationReq)
	        .then(function(response) {
	        	$scope.refreshInvitations();
	        	$scope.refreshGroups();
	        });
	    }
		// ======================================
	    // =  declineInvitationFunc			 	=
	    // ======================================
		$scope.declineInvitationFunc = function (id) {
			var invId = id;
	    	var stringAcceptInvitationReq = "declineInvitation/?invitationId=" + invId;
	        $http.get("/HomeSharingExpenses/HSEServices/MemberService/" + stringAcceptInvitationReq)
	        .then(function(response) {
	        	$scope.refreshInvitations();
	        });
	    }
		
		// ======================================
	    // = CREATE GROUP					    =
	    // ======================================  
	    $scope.showNewGroupModal = function() {
	        ModalService.showModal({
	            templateUrl: "pages/modalPages/createGroup.html",
	            controller: "createGroupController", 
	            scope : $scope,
	          }).then(function(modal) {
	            modal.element.modal();
	          });
	    };
			
	}]);
//==================================================
//
//
//= 		MODAL createGroupController		       =
//
//
//==================================================
homeApp.controller('createGroupController', ['$scope','$http','$window', 'close', function($scope, $http, $window, close) {
	  $scope.createGroup = function(groupName) {
	  $scope.newGroupName = groupName;
      //if the group name is empty, alert
	  if (!($scope.newGroupName)){
    	  $window.alert("Group name was empty !");
      }
      else{
    	//create group using group service
    	var stringCreateGroupReq = "createGroup/?groupName="+ $scope.newGroupName;
    	  
  		$http.get("/HomeSharingExpenses/HSEServices/GroupService/" + stringCreateGroupReq)
          .then(function(response) {
        	  var groupCreated = "Group has been created";
        	  if(groupCreated.localeCompare(response.data) === 0)
        		  $scope.refreshGroups();
        	  else
        		  $scope.errorStatus=response.data;
          });
      }
	  close(groupName, 500); // close, but give 500ms for bootstrap to animate
	  };

	}]);

