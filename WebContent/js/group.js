
var groupApp = angular.module('groupApp', ['angularModalService']);

groupApp.controller('groupController',['$scope','$http','$window','$location','ModalService', function($scope, $http, $window,$location, ModalService) {
	// ======================================
    // = DATA INITIALISATION			    =
    // ======================================
	var notConnected = "Not connected";
	var notInGroup = "Not in group";
	//get GroupId using url arguments
	$scope.currentMemberBalance = 0;
	$scope.urlArgs = $location.search();   
	$scope.groupID = $scope.urlArgs.groupId;
	$scope.otherMembersInGroup = [];
	$scope.leftToPay = 0;
	$scope.currentCost = 0;
	//refresh scope Member ID
	$scope.memberId = localStorage.getItem("memberID");
	//redirect if user not connected
	if(!($scope.memberId))
		$window.location.href = '#';
	
	$scope.isValid = function(value) {
	    return !value
	}
	// ======================================
    // = Member name					    =
    // ======================================
	var stringMemberNameReq = "getMemberName/"
	$http.get("/HomeSharingExpenses/HSEServices/MemberService/"+stringMemberNameReq)
	.then(function(response) {
		//Check connection status
		if(notConnected.localeCompare(response.data) === 0){
			$window.alert("You are not correctcly connected! Try again!");
			//clean local storage
			localStorage.removeItem("memberID");
			$window.location.href = '#';
		}
		else
			$scope.memberName = response.data;
		
	}, function errorCallback(response) {
		$window.location.href = '#errorPage';
	});
	// ======================================
    // = Group name						    =
    // ======================================
	var stringGroupNameReq = "getGroupNameById/?groupId=" + $scope.groupID;
	$http.get("/HomeSharingExpenses/HSEServices/GroupService/"+stringGroupNameReq)
	.then(function(response) {
		if(notInGroup.localeCompare(response.data) === 0){
			$window.alert("You are not in this group!");
			//if member not in group, redirect to his home page
			$window.location.href = '#home';
		}
		else
			$scope.groupName =  response.data;
	}, function errorCallback(response) {
		$scope.groupName = "";
	  });
	// ======================================
    // = Group Members					    =
	// will also refresh members balances   =
    // ======================================
	$scope.refreshGroupMembers = function (){
		$scope.otherMembersInGroup = [];
		var stringGroupMembersReq = "getMembersInGroup/?groupId=" + $scope.groupID;
		$http.get("/HomeSharingExpenses/HSEServices/GroupService/"+stringGroupMembersReq)
		.then(function(response) {
			if(notInGroup.localeCompare(response.data) === 0){
				//if member not in group, redirect to his home page
				$window.location.href = '#home';
			}
			else{
				$scope.membersInGroup = response.data;
				// We need for money transfer to know what are the other member
				angular.forEach($scope.membersInGroup, function(members, key) {
					if(members.id != $scope.memberId)
						$scope.otherMembersInGroup.push(members);
				});
				//refresh optimized transactions 
				$scope.findOptimizedTransactions();
			}
			
		}, function errorCallback(response) {
			$scope.membersInGroup = "";
		});
	};
    // ======================================
    // = Refresh Group Costs		   	    =
    // ======================================  
    $scope.refreshGroupCosts = function(){
    	var stringGetGroupCostsReq = "getGroupOutgoings/?groupId=" + $scope.groupID;
	    $http.get("/HomeSharingExpenses/HSEServices/GroupService/" + stringGetGroupCostsReq)
	    .then(function(response) {
	    	if(notInGroup.localeCompare(response.data) === 0){
				//if member not in group, redirect to his home page
				$window.location.href = '#home';
			}
			else{
		    	$scope.currentCost = response.data.currentCost;
		    	$scope.leftToPay = response.data.leftToPay;
			}
	    });
    };
	// ======================================
    // = refresh values					    =
    // ======================================
	 $scope.refreshValues = function (){
		//then refreshGroupCosts, refreshDocumentElements
		$scope.refreshGroupMembers();
		$scope.refreshGroupCosts();
	 }
	 //FIRST CALL AT INIT
	 $scope.refreshValues();
	// ======================================
    // = END OF INIT					    =
    // ======================================
	// ======================================
    // = LEAVE GROUP					    =
    // ======================================
	 $scope.leaveGroup = function () {
		 $scope.refreshGroupMembers();
		//refresh local member balance
		var stringMemberBalanceReq = "getMemberBalance/?groupId=" + $scope.groupID
													  +"&memberId=" + $scope.memberId;
		$http.get("/HomeSharingExpenses/HSEServices/GroupService/" + stringMemberBalanceReq)
		.then(function(response) {
			if(notInGroup.localeCompare(response.data) === 0){
				//if member not in group, redirect to his home page
				$window.location.href = '#home';
			}
			else{
				$scope.currentMemberBalance = response.data;
				if (confirm("Are you sure you want to leave "+ $scope.groupName + "?")) {
					 if ($scope.currentMemberBalance == 0 ){
						 $scope.memberId = localStorage.getItem("memberID");
						 var stringLeaveGroupReq = "leaveGroup/?groupId="+ $scope.groupID;
						 $http.get("/HomeSharingExpenses/HSEServices/MemberService/"+stringLeaveGroupReq)
							.then(function(response) {
								if(notConnected.localeCompare(response.data) === 0){
									//if member is not connected redirect index
									$window.location.href = '#';
								}
								else{
									var groupLeftResp = "Group left";
										$window.alert(groupLeftResp);
										//redirect then to home page
										if(groupLeftResp.localeCompare(response.data) === 0)
											$window.location.href = '#home';
								}
								
							}, function errorCallback(response) {
								$scope.errorComm = response;
						});
					 }else{
						 $window.alert("You can't leave a group with a balance different than 0!");
					 }
					 
				 }
			}
		});
	 }
	// ======================================
    // = INVITE MEMBER					    =
    // ======================================
	$scope.inviteMember = function (emailToSendInvitationTo) {
		$scope.memberId = localStorage.getItem("memberID");
		var emailToSendInvitationToN = emailToSendInvitationTo;
	    if ($scope.isValid(emailToSendInvitationToN)) {
	        alert("Email is empty!");
	    }else{
	    	var stringSendInvitationReq= "invitMember/?emailAdress=" + emailToSendInvitationTo
	    										   + "&groupId="+ $scope.groupID;
		    $http.get("/HomeSharingExpenses/HSEServices/GroupService/" + stringSendInvitationReq)
		    .then(function(response) {
		    	if(notInGroup.localeCompare(response.data) === 0){
					//if member not in group, redirect to his home page
					$window.location.href = '#home';
				}
				else{
					$scope.responseInvitation = response.data;
				}
		    });
	    }
	}    
	// ======================================
    // = PAY ANOTHER MEMBER			   	    =
    // ======================================  
    $scope.payAnotherMember = function() {
    	//first refresh members balances
    	$scope.memberId = localStorage.getItem("memberID");
    	$scope.refreshGroupMembers();
        ModalService.showModal({
            templateUrl: "pages/modalPages/memberMoneyTransfert.html",
            controller: "payAnotherMemberController", 
            scope : $scope,
          }).then(function(modal) {
            modal.element.modal();
          });
    };
    // ======================================
    // = Find Optimized transactions   	    =
    // ======================================  
    $scope.findOptimizedTransactions = function (){
    	var stringProcessBalancesReq = "processBalances/?groupId=" + $scope.groupID;
		$http.get("/HomeSharingExpenses/HSEServices/GroupService/" + stringProcessBalancesReq)
		.then(function(response) {
			$scope.optimizedTransactions = response.data;
			
		});
    }
    
    // ======================================
    // =  OPTIMIZED TRANSACTIONS	   	    =
    // ======================================  
    $scope.optimizedTransactionsFunc = function() {
    	//first refresh members balances
    	$scope.memberId = localStorage.getItem("memberID");
    	$scope.refreshGroupMembers();
    	$scope.findOptimizedTransactions();
		ModalService.showModal({
	            templateUrl: "pages/modalPages/optimizedTransfers.html",
	            controller: "payAnotherMemberController", 
	            scope : $scope,
	          }).then(function(modal) {
	            modal.element.modal();
	          });
    };
	
}]);
//==================================================
//= MODAL payAnotherMemberController	   	       =
//==================================================
groupApp.controller('payAnotherMemberController',  ['$scope','$http','$window', 'close', function($scope,  $http, $window, close) {
	var notInGroup = "Not in group";
	$scope.transferMoney = function(value, memberDestId) {
		  //if the shoppingList name is empty, alert
          if (!(value) || !(memberDestId) || (value<0)){
        	  $window.alert("Not a correct value !");
          }
          else{
        	  var stringMakeNewListFromArchReq = "transferMoney/?&memberDestId=" + memberDestId
															 + "&groupId=" + $scope.groupID
															 + "&value=" + value;
			$http.get("/HomeSharingExpenses/HSEServices/GroupService/" + stringMakeNewListFromArchReq)
			.then(function(response) {
				if(notInGroup.localeCompare(response.data) === 0){
					//if member not in group, redirect to his home page
					$window.location.href = '#home';
				}
				else{
					$scope.refreshGroupMembers();
				}
			});
          }
	  };

}]);

//==================================================
//= Controller groupHistoryController	   	       =
//==================================================
groupApp.controller('groupHistoryController', ['$scope','$http','$location','$window', function($scope, $http, $location, $window) {
	$scope.urlArgs = $location.search();   
	$scope.groupID = $scope.urlArgs.groupId;
	//refresh scope Member ID
	$scope.memberId = localStorage.getItem("memberID");
	var notInGroup = "Not in group";
	
	// ======================================
    // = Group name						    =
    // ======================================
	$scope.getGroupName = function (){
		var stringGroupNameReq = "getGroupNameById/?groupId=" + $scope.groupID;
		$http.get("/HomeSharingExpenses/HSEServices/GroupService/"+stringGroupNameReq)
		.then(function(response) {
			if(notInGroup.localeCompare(response.data) === 0){
				$window.alert("you are not in this group!");
				//if member not in group, redirect to his home page
				$window.location.href = '#home';
			}
			else{
				$scope.groupName =  response.data;
			}
		}, function errorCallback(response) {
			$scope.groupName = "";
		 });
	}
	// ======================================
    // = Group History					    =
    // ======================================
	$scope.refreshGroupHistory = function (){
		var stringGroupNameReq = "getGroupHistory/?&groupId=" + $scope.groupID;
		$http.get("/HomeSharingExpenses/HSEServices/GroupService/"+stringGroupNameReq)
		.then(function(response) {
			if(notInGroup.localeCompare(response.data) === 0){
				//if member not in group, redirect to his home page
				$window.location.href = '#home';
			}
			else{
				$scope.groupHistory =  response.data;
			}
		}, function errorCallback(response) {
			$scope.groupName = "";
		 });
	}
	// ======================================
    // = INIT							    =
    // ======================================
	$scope.refreshGroupHistory();
	$scope.getGroupName();
	// ======================================
    // = Group History					    =
    // ======================================
	$scope.clearHistory = function () {
		if (confirm("Are you sure you want to clear History ?")) {
			if (confirm("Does everyone knows about this ?")) {
				var stringGroupNameReq = "clearGroupHistory/?groupId=" + $scope.groupID;
				$http.get("/HomeSharingExpenses/HSEServices/GroupService/"+stringGroupNameReq)
				.then(function(response) {
					if(notInGroup.localeCompare(response.data) === 0){
						$window.alert("you are not in this group!");
						//if member not in group, redirect to his home page
						$window.location.href = '#home';
					}
					else{
						//refresh Group History
						$scope.refreshGroupHistory();
					}
				});
			}
		}
	}

}]);







