
var shoppingListApp = angular.module('shoppingListApp', []);

shoppingListApp.controller('shoppingListController',['$scope','$http','$window','$location','ModalService', function($scope, $http, $window,$location,ModalService) {
	
	// ======================================
    // =      DATA INITIALISATION  		    =
    // ======================================
	var notInGroup = "Not in group";
	$scope.urlArgs = $location.search();   
	$scope.shoppingListID = $scope.urlArgs.shoppingListId;
	$scope.shoppingListName = $scope.urlArgs.shoppingListName;
	$scope.groupId = $scope.urlArgs.groupId;
	//refresh scope Member ID
	$scope.memberId = localStorage.getItem("memberID");
	var number = 'number';
	
	$scope.isValid = function(value) {
	    return !value
	}
	//GET Shopping List Items, refresh document elements, refresh outgoings
	var stringShoppingListItemsReq = "getShoppingList/?listID=" + $scope.shoppingListID;
	$http.get("/HomeSharingExpenses/HSEServices/ShoppingListService/"+stringShoppingListItemsReq)
	.then(function(response) {
		if(notInGroup.localeCompare(response.data) === 0){
			$window.alert("You are not in this group!");
			//if member not in group, redirect to his home page
			$window.location.href = '#home';
		}
		else{
			$scope.shoppingListItems =  response.data;
			$scope.shoppingListItemsDone = [];
			$scope.shoppingListItemsTodo = [];
			angular.forEach($scope.shoppingListItems, function(item, key) {
				if(item.state){
					$scope.shoppingListItemsDone.push(item);
				}else{
					$scope.shoppingListItemsTodo.push(item);
				}
			});
			$scope.refreshDocumentElements();
			$scope.setListTypeDisplay();
			$scope.refreshShoppingListOutgoings();
		}
	}, function errorCallback(response) {
		$scope.shoppingListItems = "";
	});
	
	//GET groupMembers : For item scope
	var stringGroupMembersReq = "getMembersInGroup/?groupId=" + $scope.groupId;
	$http.get("/HomeSharingExpenses/HSEServices/GroupService/"+stringGroupMembersReq)
	.then(function(response) {
		$scope.membersInGroup = response.data;
	}, function errorCallback(response) {
		$scope.membersInGroup = "";
	});	
	// ======================================
    // = END OF INIT					    =
    // ======================================
	
	// ======================================
    // =      refreshItemList	  		    =
    // ======================================
	$scope.refreshItemList = function () {
		$http.get("/HomeSharingExpenses/HSEServices/ShoppingListService/"+stringShoppingListItemsReq)
		.then(function(response) {
		$scope.shoppingListItems =  response.data;
		//split in two different lists : todo 
		$scope.shoppingListItemsDone = [];
		$scope.shoppingListItemsTodo = [];
		angular.forEach($scope.shoppingListItems, function(item, key) {
			if(item.state){
				$scope.shoppingListItemsDone.push(item);
			}else{
				$scope.shoppingListItemsTodo.push(item);
			}
		});
		$scope.refreshDocumentElements();
		$scope.refreshShoppingListOutgoings();
		}, function errorCallback(response) {
			$scope.shoppingListItems = "";
		});
	}
	// ======================================
    // =    refreshShoppingListOutgoings    =
    // ======================================
	$scope.refreshShoppingListOutgoings = function () {
		var stringGetOutgoings = "getOutgoings/?shoppingListId=" + $scope.shoppingListID;
		$http.get("/HomeSharingExpenses/HSEServices/ShoppingListService/"+stringGetOutgoings)
		.then(function(response) {
			$scope.currentCost =  response.data.currentCost;
			$scope.leftToPay =  response.data.leftToPay;
		}, function errorCallback(response) {
			$scope.shoppingListItems = "";
		});
	}
	
	// ======================================
    // =      removeItemFunc	  		    =
    // ======================================
	$scope.removeItemFunc = function (itemId, shoppingList) {
		var itId = itemId;
    	var stringRemoveItemReq = "removeItem/?itemId=" + itId +
	    									 "&shoppingListId=" + $scope.shoppingListID;
        $http.get("/HomeSharingExpenses/HSEServices/ShoppingListService/" + stringRemoveItemReq)
        .then(function(response) {
        	if(notInGroup.localeCompare(response.data) === 0){
    			$window.alert("You are not in this group!");
    			//if member not in group, redirect to his home page
    			$window.location.href = '#home';
    		}
    		else{
	            $scope.itemOperationState = response.data;
	            //Update current item list :
	            $scope.refreshItemList();
    		}
        });
    }
	// ======================================
    // =      resetItem			  		    =
    // ======================================
	$scope.resetItem = function (itemId) {
		var itId = itemId;
    	var stringResetItemReq = "resetItem/?itemId=" + itId +
	    								   "&shoppingListId=" + $scope.shoppingListID;
        $http.get("/HomeSharingExpenses/HSEServices/ShoppingListService/" + stringResetItemReq)
        .then(function(response) {
        	if(notInGroup.localeCompare(response.data) === 0){
    			$window.alert("You are not in this group!");
    			//if member not in group, redirect to his home page
    			$window.location.href = '#home';
    		}
    		else{
	            $scope.itemOperationState = response.data;
	            //Update current item list :
	            $scope.refreshItemList();
    		}
        });
    }
	// ======================================
    // =      addItem			  		    =
    // ======================================
	$scope.addItem = function (membersIn) {
		$scope.memberId = localStorage.getItem("memberID");
		//if user didn't fill description, then NONE
		if (!($scope.newItemDescription))
			$scope.newItemDescription = "None";
		//If user didn't set a name
		if (!($scope.newItemName)){
			$window.alert("You need to set a name for this new item");
		//check if price has been set, if so check that it is a number
		}else {
			var stringAddItemReq = "addItem/?itemName=" 	+ $scope.newItemName +
											"&description=" + $scope.newItemDescription +
											"&shoppingListId=" + $scope.shoppingListID;
			$http.get("/HomeSharingExpenses/HSEServices/ShoppingListService/"+stringAddItemReq)
			.then(function(response) {
				if(notInGroup.localeCompare(response.data) === 0){
					$window.alert("You are not in this group!");
					//if member not in group, redirect to his home page
					$window.location.href = '#home';
				}
				else{
					//Update current item list :
					$scope.refreshItemList();
					//use update modal to set price and scope
					$scope.updateItemMod(0, response.data);
				}
			}, function errorCallback(response) {
			});	
			
		}
	}
	// ======================================
    // =      buyItem			  		    =
    // ======================================
	$scope.buyItem = function (itemId, itemPrice) {
		$scope.memberId = localStorage.getItem("memberID");
		var stringBuydItemReq = "buyItem/?itemId=" 		+ itemId +
										"&itemPrice=" 	+ itemPrice +
										"&shoppingListId=" + $scope.shoppingListID;
		$http.get("/HomeSharingExpenses/HSEServices/ShoppingListService/"+stringBuydItemReq)
		.then(function(response) {
			if(notInGroup.localeCompare(response.data) === 0){
				$window.alert("You are not in this group!");
				//if member not in group, redirect to his home page
				$window.location.href = '#home';
			}
			else{
				$scope.itemOperationState = response.data;
				//Update current item list :
				$scope.refreshItemList();
			}
		
		}, function errorCallback(response) {
			$scope.itemOperationState = stringAddItemReq;
		});	
	}
	
	// ======================================
    // =      showBuyItemPrompt			  	=
    // ======================================
    $scope.showBuyItemPrompt = function (itemId, itemName, itemPrice) {
        var newPrice = prompt('Is the price different than '+ itemPrice+' for ' + itemName + ' ?', itemPrice);
        if (newPrice != null && newPrice != "") {
        	//check if new price is a number
        	if(!isNaN(newPrice))
        		$scope.buyItem(itemId, newPrice);
        	//if new price not correct, use old price
        	else
        		$scope.buyItem(itemId, itemPrice);
        }
       // else 
        //	$scope.buyItem(itemId, itemPrice);
    }
    
    // ======================================
    // =      removeShoppingList 		    =
    // ======================================
	$scope.removeShoppingList = function () {
		if (confirm("Are you sure you want to delete "+ $scope.shoppingListName + "?")) {
			$scope.memberId = localStorage.getItem("memberID");
	    	var stringRemoveShoppingListReq = "removeShoppingList/?groupId=" + $scope.groupId
	    												 		+"&listId=" + $scope.shoppingListID;
	        $http.get("/HomeSharingExpenses/HSEServices/GroupService/" + stringRemoveShoppingListReq)
	        .then(function(response) {
	        	if(notInGroup.localeCompare(response.data) === 0){
					$window.alert("You are not in this group!");
					//if member not in group, redirect to his home page
					$window.location.href = '#home';
				}
				else{
		           $scope.removeListState = response.data;
		           //redirect then to home page
		           $window.location.href = 'index.html#shoppingListsSelection?groupId='+ $scope.groupId;
				}
	        });
		}
    }
	// ======================================
    // =      setListTypeDisplay		    =
    // ======================================
	$scope.setListTypeDisplay = function(){
		var stringShoppingListState = "getShoppingListArchiveState/?listID=" + $scope.shoppingListID;
		$http.get("/HomeSharingExpenses/HSEServices/ShoppingListService/" + stringShoppingListState)
		.then(function(response) {
			//will return true if the list has been archived
			if(response.data){
				document.getElementById('newItem').style.display = "none";
			}
			else
				document.getElementById('newItem').style.display = "";
		});	
	}
	// ======================================
    // =      refreshDocumentElements	    =
    // ======================================
	$scope.refreshDocumentElements = function(){
		if ($scope.shoppingListItemsTodo.length == 0)
			document.getElementById('todoItemList').style.display = "none";
		else
			document.getElementById('todoItemList').style.display = "";
		
		if ($scope.shoppingListItemsDone.length == 0)
			document.getElementById('doneItemList').style.display = "none";
		else
			document.getElementById('doneItemList').style.display  = "";
	}
	// ======================================
    // =      CheckBoxes updates  		    =
    // ======================================
	$scope.checkAllBoxes = function () {
		if(document.getElementById("everyoneBox").checked){
			angular.forEach($scope.membersInGroup, function(member, key) {
				document.getElementById(member.id).checked = true;
			});
		}	
	}
	$scope.refreshMainBox = function () {
		var allMemberChecked = true;
			angular.forEach($scope.membersInGroup, function(member, key) {
				//If one at least is not check 
				if(!(document.getElementById(member.id).checked)){
					allMemberChecked = false;
				}
			});
			document.getElementById("everyoneBox").checked = allMemberChecked;
	}
	// ======================================
    // = updateItemMod				   	    =
    // ======================================  
    $scope.updateItemMod = function(oldItemPrice, itemId) {
    	$scope.memberId = localStorage.getItem("memberID");
    	$scope.oldItemPrice = oldItemPrice;
    	$scope.itemToUpdateId = itemId;
        ModalService.showModal({
        	backdrop: 'static', 
            templateUrl: "pages/modalPages/updateItem.html",
            controller: "updateItemController", 
            scope : $scope,
          }).then(function(modal) {
            modal.element.modal();
          });
    };
	
}]);
//==================================================
//
//
//= 		MODAL updateItemController		       =
//
//
//==================================================
groupApp.controller('updateItemController',  ['$scope','$http','$window', 'close', function($scope,  $http, $window, close) {
	var notInGroup = "Not in group";
	// ======================================
    // =      updateItemInit			  	=
	// = 							        =
    // ======================================
	$scope.updateItemInit = function (itemPr) {
		var membersIn = "";
		var itemPrice = itemPr;
		angular.forEach($scope.membersInGroup, function(member, key) {
				if(document.getElementById(member.id).checked){
					if(membersIn === "")
						membersIn = member.id;
					else
						membersIn= membersIn+","+ member.id;
				}
			});
		membersIn = "[" + membersIn + "]";
		//if user didn't fill description, then NONE
		if (!($scope.newItemDescription))
			$scope.newItemDescription = "None";
		//check if price has been set, if so check that it is a number
		if(itemPrice){
			if( isNaN(itemPrice) || itemPrice<0)
				itemPrice = $scope.oldItemPrice;
			else
				$scope.updateItem(membersIn, itemPrice);
		}else{
			$scope.updateItem(membersIn, $scope.oldItemPrice);
		}
		
	}
	// ======================================
    // =      updateItem				  	=
	// = 							        =
    // ======================================
	$scope.updateItem = function( membersIn, price){
		var stringUpdateItemReq = "updateItem/?itemId=" + $scope.itemToUpdateId 
	    									+"&ConcernedMembersIds=" + membersIn 
	    									+"&price=" + price
	    									+"&shoppingListId=" + $scope.shoppingListID;
		$http.get("/HomeSharingExpenses/HSEServices/ShoppingListService/"+ stringUpdateItemReq)
		.then(function(response) {
			if(notInGroup.localeCompare(response.data) === 0){
				$window.alert("You are not in this group!");
				//if member not in group, redirect to his home page
				$window.location.href = '#home';
			}
			else{
				$scope.itemOperationState = response.data;
				//Update current item list :
				$scope.refreshItemList();
				$scope.close();
			}
		}, function errorCallback(response) {
			$scope.itemOperationState = response.data;
			$scope.close();
		});	
	}
	// ======================================
    // =      close						  	=
    // ======================================
	$scope.close = function(){
		//close modal
		close("", 500);
	}

}]);