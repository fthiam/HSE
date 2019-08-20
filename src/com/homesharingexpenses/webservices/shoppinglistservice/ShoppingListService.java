package com.homesharingexpenses.webservices.shoppinglistservice;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.homesharingexpenses.comon.dto.ItemDTO;
import com.homesharingexpenses.comon.dto.OutgoingDTO;
import com.homesharingexpenses.comon.exception.ObjectNotFoundException;
import com.homesharingexpenses.domain.group.Group;
import com.homesharingexpenses.domain.group.member.GroupMember;
import com.homesharingexpenses.domain.item.Item;
import com.homesharingexpenses.domain.item.owner.ItemOwner;
import com.homesharingexpenses.domain.member.Member;
import com.homesharingexpenses.domain.shoppinglist.ShoppingList;
import com.homesharingexpenses.persistence.DataAccessAllObjects;
import com.homesharingexpenses.webservices.groupservice.GroupService;

/**
 * The service responsible for list's management
 */
@Path("/ShoppingListService")
public class ShoppingListService {
	@Context HttpServletRequest _request;

	public ShoppingListService(){
	}	
	// ======================================
    // =           getShoppingList		    =
    // ======================================
	/**
     * Get shopping list using it's id.
     * @param listID : id of the list to get
     * @return Json : list of ItemDTO
     */
	@GET
	@Path("/getShoppingList")
	@Produces("application/json")
	public String getShoppingList(@QueryParam("listID") String listID) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, listID))
			return new Gson().toJson("Not in group");
		else{ 
			Type stringType = new TypeToken<String>(){}.getType();
			dao_A = new DataAccessAllObjects("homeSharePU"); 
			LinkedList<ItemDTO> itemDTOList = new LinkedList<ItemDTO>();
			try{
		        ShoppingList shoppingList = dao_A.findShoppingListById(listID);
		        Collection<Item> items = shoppingList.getItems();
		        for (Item it : items){
		        	//get owners name (display purpose)
		        	List<ItemOwner> itemOwners = it.getItemOwnerList();
		        	String ownersNames = "";
		        	if(itemOwners.size() > 0){
			        	for(ItemOwner ito : itemOwners){
			        		String itoMemberId = ito.getMemberId();
			        		Member itoMember = dao_A.findMemberById(itoMemberId);
			        		
			        		if(ownersNames.equals(""))
			        			ownersNames = new Gson().fromJson(itoMember.getName(), stringType);
			        		else
			        			ownersNames = ownersNames + ", " +  new Gson().fromJson(itoMember.getName(), stringType);	
			        	}
		        	}else
		        		ownersNames = "Nobody";
			        	
		        	ItemDTO itemDTO = new ItemDTO(it, ownersNames);
		        	itemDTOList.add(itemDTO);
		        }
		        return new Gson().toJson(itemDTOList);
				
			}catch(ObjectNotFoundException ONFE){
				return null;
			}
		}
	}
	
	// ======================================
    // =     getShoppingListArchiveState	=
    // ======================================
	/**
     * Get state of a shopping list.
     * @param listID : id of the list to get
     * @return Json : archive boolean state
     */
	@GET
	@Path("/getShoppingListArchiveState")
	@Produces("application/json")
	public String getShoppingListArchiveState(@QueryParam("listID") String listID) throws JSONException {
	
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 
		try{
	        ShoppingList shoppingList = dao_A.findShoppingListById(listID);
	        return new Gson().toJson(shoppingList.getArchiveSate());
			
		}catch(ObjectNotFoundException ONFE){
			return new Gson().toJson(ONFE.getMessage());
		}
	}
	
	// ======================================
    // =           addItem			    	=
    // ======================================
	/**
     * Add an item in a list.
     * @param itemName : name of the new item
     * @param description : item description
     * @param listID : id of the list 
     * @return Json : new item id
     */
	@GET
	@Path("/addItem")
	@Produces("application/json")
	public String addItem(@QueryParam("itemName")String itemName, 
						  @QueryParam("description")String itemDescription, 
						  @QueryParam("shoppingListId")String shoppingListId) 
								  throws JSONException {

		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
	
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, shoppingListId))
			return new Gson().toJson("Not in group");
		else{ 
			List<String> membersConcerned = new LinkedList<String>();
			
	
			try{
				//find corresponding shopping list
				ShoppingList itemShoppingList = dao_A.findShoppingListById(shoppingListId);
				//make new item
				Item newItemToAdd = new Item(itemName, 0, itemShoppingList, membersConcerned);
				newItemToAdd.setDescription(itemDescription);
				//Insert item 
				dao_A.persistItem(newItemToAdd);
				//Merge Shopping List
				dao_A.mergeShoppingList(itemShoppingList);
				//No exception thrown...
				return new Gson().toJson(newItemToAdd.getId());  
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson("Unable to add item : " + ONFE.getMessage());
			}
		}
	}
	// ======================================
    // =           updateItem			    =
    // ======================================
	/**
     * Update an item in a list.
     * @param itemId : Id of the item to update
     * @param ConcernedMembersIds : string containing the concerned members
     * @param price : new price for the item
     * @param shoppingListId : list of the item 
     * @return Json : request state
     */
	@GET
	@Path("/updateItem")
	@Produces("application/json")
	public String updateItem(@QueryParam("itemId")String itemId,
							 @QueryParam("ConcernedMembersIds")String ConcernedMembersIds,
							 @QueryParam("price")float itemPrice,
							 @QueryParam("shoppingListId")String shoppingListId)throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, shoppingListId))
			return new Gson().toJson("Not in group");
		else{ 
			Type listType = new TypeToken<LinkedList<String>>(){}.getType();
			//Get members scope from JSON string
			List<String> membersConcernedKeys =  new Gson().fromJson(ConcernedMembersIds, listType);
			List<String> membersConcerned = new LinkedList<String>();
			for (String mId : membersConcernedKeys){
				membersConcerned.add(mId);
			}
			dao_A = new DataAccessAllObjects("homeSharePU"); 
			Gson gsonMaker = new Gson();
			try{
				//find item to update 
				Item itemToUpdate = dao_A.findItemById(itemId);
				//Remove previous item owners
				List<String> itemOwnerIdsToDelete = itemToUpdate.removeAllOwners();
				
				for (String itemOwnerId : itemOwnerIdsToDelete){
					ItemOwner itOwner = dao_A.findItemOwnerById(itemOwnerId);
					dao_A.removeItemOwner(itOwner);
				}			
				//Create new itemOwners
				for(String mC : membersConcerned){
					//will automatically add to the itemToUpdate
					new ItemOwner(itemToUpdate, mC);
				}
				//set new price
				itemToUpdate.setPrice(itemPrice);
				//merge updated member
				dao_A.mergeItem(itemToUpdate);
				return gsonMaker.toJson("Item updated");  
			}catch(ObjectNotFoundException ONFE){
				return gsonMaker.toJson(ONFE.getMessage());
			}
		}
	}
	// ======================================
    // =           removeItem			    =
    // ======================================
	/**
     * Remove an item from a list.
     * @param itemId : Id of the item to remove
     * @param shoppingListId : list of the item 
     * @return Json : request state
     */
	@GET
	@Path("/removeItem")
	@Produces("application/json")
	public String removeItem(@QueryParam("itemId")String itemId,
							 @QueryParam("shoppingListId")String shoppingListId) throws JSONException {

		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
	
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, shoppingListId))
			return new Gson().toJson("Not in group");
		else{ 
			try{
				//find item to delete 
				Item itemToDelete = dao_A.findItemById(itemId);
				//find corresponding shopping list (have to maintain the relation)
				ShoppingList shoppingList = dao_A.findShoppingListById(itemToDelete.getShoppingListId());
				if (!shoppingList.getArchiveSate()){
					shoppingList.deleteItemFromList(itemId);
					//remove item...
					dao_A.removeItem(itemToDelete); 
					//merge list
					dao_A.mergeShoppingList(shoppingList);
					return new Gson().toJson("Item " +itemToDelete.getName()+" has been removed");
				}
				else
					return new Gson().toJson("You can't delete an item from an archived list");  
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson(ONFE.getMessage());
			}
		}
	}
	
	// ======================================
    // =           buyItem			   		=
    // ======================================
	/**
     * Buy an item in a list.
     * @param itemId : Id of the item to remove
     * @param itemPrice : price for this item
     * @param shoppingListId : list of the item 
     * @return Json : request state
     */
	@GET
	@Path("/buyItem")
	@Produces("application/json")
	public String buyItem(  @QueryParam("itemId") String itemId,
							@QueryParam("itemPrice") float itemPrice,
							@QueryParam("shoppingListId")String shoppingListId) throws JSONException {

		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, shoppingListId))
			return new Gson().toJson("Not in group");
		else{ 
			ShoppingList shoppingList = dao_A.findShoppingListById(shoppingListId);
			String groupId = shoppingList.getGroup().getId();
			//First update item owners for item
			//Make sure that if someone left the group, he would'nt be asked for this item
			this.checkItemScope(itemId, groupId);
			boolean buyerIsConcerned = false;		
			float buyerBalance = 0;
			float otherMembersBalance = 0;
			
			try{
				//get item owners
				Item itemToBuy = dao_A.findItemById(itemId);
				Member buyerMember = dao_A.findMemberById(memberId);
				List<String> ownersIds = itemToBuy.getItemOwnersIds();
				int numberOfMembers = ownersIds.size();
				float memberPart = (itemPrice / numberOfMembers);
				// Is the buyer concerned?
				for (String ito : ownersIds){
					if (ito.equals(memberId))
						buyerIsConcerned = true;
				}
				otherMembersBalance = - memberPart;
				if(ownersIds.size()>0){
					//Case where the buyer is one of item owners
					if(buyerIsConcerned){
						//if buyer is the only one concerned, do not impact any balance
						if(ownersIds.size() > 1){
							buyerBalance = itemPrice - memberPart;
							//Update balances
							for (String ito : ownersIds){
								if (ito.equals(memberId))
									updateMemberBalance(ito,groupId, buyerBalance);
								else
									updateMemberBalance(ito,groupId, otherMembersBalance);
							}
						}
					}
					//Buyer is not concerned buy the product
					else{
						updateMemberBalance(memberId, groupId, itemPrice);
						for (String ito : ownersIds){
							updateMemberBalance(ito,groupId, otherMembersBalance);
						}
					}
				}
				//item is now purchased
				itemToBuy.buyItem(buyerMember.getName(), itemPrice);
				dao_A.persistItem(itemToBuy);
				//ask group service to record this transaction 
				GroupService grs = new GroupService();
				grs.saveInGroupHistory(groupId, "Item " + itemToBuy.getName() +" has been buyed by " + buyerMember.getName() + " for " + itemPrice);
				
				return new Gson().toJson("Item " + itemToBuy.getName() +" has been buyed");  
				
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson("Unable to buy item : " + ONFE.getMessage());
			}
		}
	}
	// ======================================
    // =           resetItem			    =
    // ======================================
	/**
     * Reset an item in a list.
     * @param itemId : Id of the item to reset
     * @param shoppingListId : list of the item 
     * @return Json : request state
     */
	@GET
	@Path("/resetItem")
	@Produces("application/json")
	public String resetItem(@QueryParam("itemId")String itemId,
			 				@QueryParam("shoppingListId")String shoppingListId) throws JSONException {

		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, shoppingListId))
			return new Gson().toJson("Not in group");
		else{ 
			try{
				Item itemToReset = dao_A.findItemById(itemId);
				ShoppingList shoppingList = dao_A.findShoppingListById(itemToReset.getShoppingListId());
				if (!shoppingList.getArchiveSate()){
					itemToReset.restorItem();
					
					dao_A.mergeItem(itemToReset);
					return new Gson().toJson("Item " + itemToReset.getName() + " has been reseted");  
				}
				else
					return new Gson().toJson("You can't restore an item from an archived list");  
				
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson(ONFE.getMessage());
			}
		}
	}
	// ======================================
    // =           archiveShoppingList	    =
    // ======================================
	/**
     * Archive a shopping list.
     * @param shoppingListId : list to archive
     * @return Json : request state
     */
	@GET
	@Path("/archiveShoppingList")
	@Produces("application/json")
	public String archiveShoppingList(@QueryParam("shoppingListId")String shoppingListId) throws JSONException {

		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, shoppingListId))
			return new Gson().toJson("Not in group");
		else{ 
			try{
				ShoppingList shoppingList = dao_A.findShoppingListById(shoppingListId);
	
				for(Item it : shoppingList.getItems()){
					//We don't keep todos item in an archive list
					if (!it.getPurshasedState())
						removeItem(it.getId(), shoppingListId);
				}
				//liste has been merged.. refresh it
				shoppingList = dao_A.findShoppingListById(shoppingListId);
	
				//Change current list state
				shoppingList.archiveShoppingList();
				dao_A.mergeShoppingList(shoppingList);
				
				return new Gson().toJson("Shopping list " + shoppingList.getId() + " has been archived.");
				
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson(ONFE.getMessage());
			}
		}
	}
	// ======================================
    // =           getOutgoings	   			=
    // ======================================
	/**
     * Get outgoings for a specified list.
     * @param shoppingListId : list to archive
     * @param HttpServletRequest : current request
     * @return Json : outgoings (class Outgoing)
     */
	@GET
	@Path("/getOutgoings")
	@Produces("application/json")
	public String getOutgoings(@QueryParam("shoppingListId")String shoppingListId, @Context HttpServletRequest request) throws JSONException {
		this._request = request;
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, shoppingListId))
			return new Gson().toJson("Not in group");
		else{ 
			OutgoingDTO outgoingDTOs = new OutgoingDTO();
			try{	
				ShoppingList shoppingList = dao_A.findShoppingListById(shoppingListId);
				for (Item it : shoppingList.getItems()){
					if (it.getPurshasedState())
						outgoingDTOs.addToCurrentCost(it.getPrice());
					else
						outgoingDTOs.addToleftToPay(it.getPrice());	
				}
				return new Gson().toJson(outgoingDTOs);
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson(ONFE.getMessage());
			}
		}
	}
	// ======================================
    // =           updateMemberBalance	    =
    // ======================================
	private boolean updateMemberBalance(String memberId, String groupId, float value){
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 
		try{
	        GroupMember groupMember = dao_A.findGroupMemberForMemberIdAndGroupId(memberId, groupId);
	        groupMember.updateMemberBalance(value);
	        //save new balance
	        dao_A.mergeGroupMember(groupMember);
			return true;
		}catch(ObjectNotFoundException ONFE){
			return false;
		}
	}
	// ======================================
    // =           checkItemScope		    =
    // ======================================
	private void checkItemScope(String itemId, String groupId){
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 
		try{			
			//get item owners
			Item itemToBuy = dao_A.findItemById(itemId);
			//need to check that every member in scope is still in the group !
			List<String> ownersIds = itemToBuy.getItemOwnersIds();
			
			for(String memberId : ownersIds){
				try{
					
					dao_A.findGroupMemberForMemberIdAndGroupId(memberId, groupId);
				}catch(ObjectNotFoundException ONFE){
					//memberId Not find !  need to delete it from owners !
					String itemOwnerIdToDelete = itemToBuy.removeOwner(memberId);
					ItemOwner itemOwnerToDelete = dao_A.findItemOwnerById(itemOwnerIdToDelete);
					dao_A.removeItemOwner(itemOwnerToDelete);
					dao_A.mergeItem(itemToBuy);
				}
			}
		}catch(ObjectNotFoundException ONFE){}	
	}
	// ==========================================
    // =           isMemberInGroup	   	    	=
	// Check if user is in shoppingList's group =
    // ==========================================
	private boolean isMemberInGroup(String memberId, String shoppingListId){
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 
		
		try{
			Member member = dao_A.findMemberById(memberId);
			ShoppingList shoppingList = dao_A.findShoppingListById(shoppingListId);
			Group group = shoppingList.getGroup();
			List<Member> groupMembers = group.getMembers();
			for (Member grpMember : groupMembers){
				if (grpMember.getId().equals(member.getId()))
					return true;
			}
		}catch(ObjectNotFoundException ONFE){
			return false;
		}
		return false;
	}
}
