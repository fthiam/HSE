
var shoppingListsPresApp = angular.module('shoppingListsPresApp', ['angularModalService']);

shoppingListsPresApp.controller('shoppingListsPresentation',['$scope','$http','$window','$location','ModalService', function($scope, $http, $window,$location, ModalService) {
	// ======================================
    // = DATA INITIALISATION			    =
    // ======================================
	var notConnected = "Not connected";
	var notInGroup = "Not in group";
	//get GroupId using url arguments
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
    // = REFRESH SHOPPING LISTS			    =
    // ======================================
	 $scope.refreshShoppingLists = function (){
		$scope.activeShoppingList = [];
		$scope.archiveShoppingList = [];
		var stringGetShoppingListNameReq = "getShoppingLists/?groupId=" + $scope.groupID;
		 $http.get("/HomeSharingExpenses/HSEServices/GroupService/"+stringGetShoppingListNameReq)
			.then(function(response) {
				if(notInGroup.localeCompare(response.data) === 0){
					//if member not in group, redirect to his home page
					$window.location.href = '#home';
				}
				else{
					$scope.shoppingList =  response.data;
					angular.forEach($scope.shoppingList, function(shopList, key) {
						if(shopList.archiveState)
							$scope.archiveShoppingList.push(shopList);
						else
							$scope.activeShoppingList.push(shopList);
						$scope.refreshDocumentElements();
					});
				}
				
			}, function errorCallback(response) {
			});
	 }
	// ======================================
    // =      refreshDocumentElements	    =
    // ======================================
	$scope.refreshDocumentElements = function(){
		if ($scope.activeShoppingList.length == 0)
			document.getElementById('activeShoppingLists').style.display = "none";
		else
			document.getElementById('activeShoppingLists').style.display = "";
		
		if ($scope.archiveShoppingList.length == 0)
			document.getElementById('archiveShoppingLists').style.display = "none";
		else
			document.getElementById('archiveShoppingLists').style.display = "";
	}
	// ======================================
    // = refresh values					    =
    // ======================================
	 $scope.refreshValues = function (){
		$scope.refreshShoppingLists();
	 }
	 //FIRST CALL AT INIT
	 $scope.refreshValues();
	// ======================================
    // = END OF INIT					    =
    // ======================================

	// ======================================
    // = CREATE SHOPPING LIST			    =
    // ======================================
	 $scope.createShoppingListFunc = function (shoppingListName) {
		 $scope.memberId = localStorage.getItem("memberID");
		var shoppingListN = shoppingListName;
	    if ($scope.isValid(shoppingListN)) {
	        alert("Shopping List name is empty!");
	    }else{
	    	var stringCreateShoppingListReq = "createList/?listName=" + shoppingListName 
	    									  			+"&groupId=" + $scope.groupID;
		    $http.get("/HomeSharingExpenses/HSEServices/GroupService/" + stringCreateShoppingListReq)
		    .then(function(response) {
		    	if(notInGroup.localeCompare(response.data) === 0){
					//if member not in group, redirect to his home page
					$window.location.href = '#home';
				}
				else{
			        $scope.responseFromListCreation = response.data;
			        $scope.refreshValues();
				}
		    });
	    }
	}
	 
	// ======================================
    // = ARCHIVE SHOPPING LIST			    =
    // ======================================
	 $scope.archiveShoppingListFunc = function (shoppingListId, shoppingListName) {
		if (confirm("Are you sure you want to archive "+ shoppingListName + "?")) {
	    	var stringArchiveShoppingListReq = "archiveShoppingList/?shoppingListId=" + shoppingListId;
		    $http.get("/HomeSharingExpenses/HSEServices/ShoppingListService/" + stringArchiveShoppingListReq)
		    .then(function(response) {
		    	if(notInGroup.localeCompare(response.data) === 0){
					//if member not in group, redirect to his home page
					$window.location.href = '#home';
				}
				else{
					$scope.refreshValues();
				}
		    });
		}
	}
	 
	// ======================================
    // = MAKE NEW LISTE FROM ARCHIVE	   =
    // ======================================  
    $scope.makeNewListFromArchive = function(archiveShoppingListId, archiveShoppingListName) {
    	$scope.archiveShoppingListIdOrig = archiveShoppingListId;
    	$scope.memberId = localStorage.getItem("memberID");
        ModalService.showModal({
            templateUrl: "pages/modalPages/NewShoppingListFromArchive.html",
            controller: "NewShoppingListFromArchiveController", 
            scope : $scope,
          }).then(function(modal) {
            modal.element.modal();
          });
    };
	
}]);

//==================================================
//
//
//=  MODAL NewShoppingListFromArchiveController	   =
//
//
//==================================================
groupApp.controller('NewShoppingListFromArchiveController', ['$scope','$http','$window', 'close', function($scope,  $http, $window, close) {
	//refresh scope Member ID
	var notInGroup = "Not in group";
	
	$scope.createList = function(listName) {
		  $scope.newShoppingListName = listName;
          //if the shoppingList name is empty
          if (!($scope.newShoppingListName)){
        	  $window.alert("New shopping list name was empty !");
          }
          else{
        	  var stringMakeNewListFromArchReq = "makeNewListFromArchive/?archiveShoppingListId=" + $scope.archiveShoppingListIdOrig
																		+ "&groupId=" + $scope.groupID
																		+ "&newShoppingListName=" + $scope.newShoppingListName;
			$http.get("/HomeSharingExpenses/HSEServices/GroupService/" + stringMakeNewListFromArchReq)
			.then(function(response) {
				if(notInGroup.localeCompare(response.data) === 0){
					//if member not in group, redirect to his home page
					$window.location.href = '#home';
				}
				else{
					$scope.refreshValues();
				}
			});
          }
	  };

}]);







