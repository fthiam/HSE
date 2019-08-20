package com.homesharingexpenses.webservices.groupservice;

import com.homesharingexpenses.comon.dto.GroupOperationDTO;
import com.homesharingexpenses.comon.dto.OutgoingDTO;
import com.homesharingexpenses.comon.dto.ShoppingListDTO;
import com.homesharingexpenses.comon.dto.TransactionDTO;
import com.homesharingexpenses.comon.exception.DuplicateMemberException;
import com.homesharingexpenses.comon.exception.ObjectNotFoundException;
import com.homesharingexpenses.comon.process.transaction.TransactionOptimizer;
import com.homesharingexpenses.domain.group.Group;
import com.homesharingexpenses.domain.group.member.GroupMember;
import com.homesharingexpenses.domain.group.operation.GroupOperation;
import com.homesharingexpenses.domain.item.Item;
import com.homesharingexpenses.domain.item.owner.ItemOwner;
import com.homesharingexpenses.domain.member.Member;
import com.homesharingexpenses.domain.member.invitation.Invitation;
import com.homesharingexpenses.domain.shoppinglist.ShoppingList;
import com.homesharingexpenses.persistence.DataAccessAllObjects;
import com.homesharingexpenses.webservices.shoppinglistservice.ShoppingListService;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * The service responsible for group's management
 */
@Path("/GroupService")
public class GroupService {

	@Context HttpServletRequest _request;
	
	public GroupService(){
		
	}
	// ======================================
    // =           createGroup    		    =
    // ======================================
	/**
     * Create a new group.
     * @param groupName new group name
     * @return String in Json format with request status
     */
	@GET
	@Path("/createGroup")
	@Produces("application/json")
	public String createGroup(@QueryParam("groupName") String groupName) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		try{
			Member member = dao_A.findMemberById(memberId);
			Group group = new Group(member, groupName);
			
			member.addGroup(group);
			dao_A.persistGroup(group);
			saveInGroupHistory(group.getId(), "This group has been created by " + member.getName());
			return new Gson().toJson("Group has been created");  
		}catch(ObjectNotFoundException ONFE){
			return new Gson().toJson("Not connected");
		}
	}
	// ======================================
    // =       getGroupMembersDesc		    =
    // ======================================
	/**
     * Get a list of member description according to a groupId.
     * @param groupName new group name
     * @return Json : list of members 
     */
	@GET
	@Path("/getMembersInGroup")
	@Produces("application/json")
	public String getMembersInGroup(@QueryParam("groupId") String groupId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{
		
			try{
				Group group = dao_A.findGroupById(groupId);
				List<Member> memberList = group.getMembers();
				List<MemberInGroupDesc> memberDescList = new LinkedList<MemberInGroupDesc>();
				
				for(Member mb : memberList){
					MemberInGroupDesc memberDesc = new MemberInGroupDesc (mb.getId(),
																		  mb.getName(),
																		  mb.getEmailAdress(),
																		  round(Float.valueOf(this.getMemberBalance(groupId, mb.getId())), 2));
					memberDescList.add(memberDesc);
				}

		        return new Gson().toJson(memberDescList);
		
			}catch(ObjectNotFoundException ONFE){
				 return new Gson().toJson("");
			}
		}
	}
	// ======================================
    // =           InvitMember      	    =
    // ======================================
	/**
     * Send invitation to Member for a specific group.
     * @param emailAdress member to invite mail adress
     * @param groupId group in which member is invited
     * @return Json : request state
     */
	@GET
	@Path("/invitMember")
	public String invitMember(@QueryParam("emailAdress") String emailAdress, @QueryParam("groupId") String groupId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{
	        try{
	        	 Member memberDest = dao_A.findMemberByEmailAdress(emailAdress);
	             Invitation invitation = new Invitation(groupId, memberDest.getId(), memberId);
	
	        	//Check if member is already in group
	        	Group group = dao_A.findGroupById(groupId);
	        	List<Member> members = group.getMembers();
	        	for(Member mb : members){
	        		if (mb.getId().equals(memberDest.getId()))
	        			return new Gson().toJson("This member is already in this group.");
	        	}
	        	//check that invitation dosen't already exists..
	        	List<Invitation> destMemberinvitations = dao_A.findAllInvitationForMemberId(memberDest.getId());
	        	for (Invitation inv : destMemberinvitations){
	        		if (inv.getGourpId().equals(groupId))
	        			return new Gson().toJson("This member has already been invitated to this group!");
	        	}
	        	dao_A.persistInvitation(invitation);
	        	return new Gson().toJson("Member for mail adress :" + emailAdress + " has been invited.");
	        }catch(DuplicateMemberException dmE){
	        	return new Gson().toJson(dmE.getMessage());
	        }catch(ObjectNotFoundException dmE){
	        	return new Gson().toJson(dmE.getMessage());
	        }
		}
	}
	// ======================================
    // =           createList        	    =
    // ======================================
	/**
     * Create a new List in a specific group.
     * @param listName name for the new List
     * @param groupId group in which the new list has to be created
     * @return Json : request state
     */
	@GET
	@Path("/createList")
	@Produces("application/json")
	public String createList(@QueryParam("listName") String listName, @QueryParam("groupId") String groupId) throws JSONException {
		//Get Member id from session
		
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{
			try{
		        Group group = dao_A.findGroupById(groupId);
		       
		        ShoppingList shoppingList = new ShoppingList (listName, group);
		        
		        dao_A.persistShoppingList(shoppingList);
		        dao_A.mergeGroup(group);
		        
		        Gson gsonMaker = new Gson();
		        saveInGroupHistory(groupId, "List " + listName + " has been created");
		        return gsonMaker.toJson("Shopping list "+ listName +": has been created");
			 }catch(ObjectNotFoundException dmE){
			        return new Gson().toJson(dmE.getMessage());
	         }
		}
	}
	// ======================================
    // =     makeNewListFromArchive       	=
    // ======================================
	/**
     * Make new list from an archive.
     * @param archiveShoppingListId the old list ID
     * @param groupId group in which the new list has to be created
     * @param newShoppingListName the name for the new list
     * @return Json : request state
     */
	@GET
	@Path("/makeNewListFromArchive")
	@Produces("application/json")
	public String makeNewListFromArchive(@QueryParam("archiveShoppingListId") String archiveShoppingListId, 
										 @QueryParam("groupId") String groupId,
										 @QueryParam("newShoppingListName") String newShoppingListName) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	

		//check if member is in the group
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{
			try{
				
			    Group group = dao_A.findGroupById(groupId);
			    ShoppingList newShoppingList = new ShoppingList (newShoppingListName, group);
			    ShoppingList archiveShoppingList = dao_A.findShoppingListById(archiveShoppingListId);
			    //save new shoppingList
			    dao_A.persistShoppingList(newShoppingList);
			    
			    for (Item it : archiveShoppingList.getItems()){		 		
			    	Item newItemToAdd = new Item(it.getName(), (float)it.getPrice(), newShoppingList, it.getItemOwnersIds());
					newItemToAdd.setDescription(it.getDescription());
					//Insert item 
					dao_A.persistItem(newItemToAdd);
					//in case someone left group in the mean time
					checkItemScope(newItemToAdd.getId(), groupId);
			    }
			    //now merge the new shopping list
			    dao_A.mergeShoppingList(newShoppingList);
			    dao_A.mergeGroup(group);
			    
			    Gson gsonMaker = new Gson();
			    //save in group History
			    saveInGroupHistory(groupId, "New list "+newShoppingListName+" created, made out of archive " + archiveShoppingList.getName());
			    return gsonMaker.toJson("Shopping list "+ newShoppingListName +": has been created");
			    
			 }catch(ObjectNotFoundException dmE){
			    return new Gson().toJson(dmE.getMessage());
		     }
		}
	}
	// ======================================
    // =           getGroupNameById		    =
    // ======================================
	/**
     * Get Group Name by ID.
     * @param groupId 
     * @return Json : name of the group
     */
	@GET
	@Path("/getGroupNameById")
	public String getGroupNameById(@QueryParam("groupId") String groupId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	

		//check if member is in the group.
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{
			try{
		        Group group = dao_A.findGroupById(groupId);
		        return new Gson().toJson(group.getName());
			}catch(ObjectNotFoundException ONFE){
				return null;
			}
		}
	}
	// ======================================
    // =           getShoppingLists		    =
    // ======================================
	/**
     * Get shopping lists for current group.
     * @param groupId 
     * @return Json : List of ShoppingListDTO
     */
	@GET
	@Path("/getShoppingLists")
	@Produces("application/json")
	public String getShoppingLists(@QueryParam("groupId") String groupId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
	
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 
		//check if member is in the group
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{
			List<ShoppingListDTO> shoppingListDTOs = new LinkedList<ShoppingListDTO>();
			
			try{
		        Group group = dao_A.findGroupById(groupId);
		        Collection<ShoppingList> shoppingLists = group.getShoppingLists();
		        for (ShoppingList shopList : shoppingLists){
		        	ShoppingListDTO shoppingListDTO = new ShoppingListDTO(shopList);
		        	shoppingListDTOs.add(shoppingListDTO);
		        }
		        
		        return new Gson().toJson(shoppingListDTOs);
				
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson("");
			}
		}
	}
	// ======================================
    // =           getGroupOutgoings	    =
    // ======================================
	/**
     * Get Group Outgoings.
     * @param groupId 
     * @return Json : Lists of Outgoing
     */
	@GET
	@Path("/getGroupOutgoings")
	@Produces("application/json")
	public String getGroupOutgoings(@QueryParam("groupId") String groupId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{
			Type outGoingType = new TypeToken<OutgoingDTO>(){}.getType();
			try{
		        Group group = dao_A.findGroupById(groupId);
		        Collection<ShoppingList> shoppingLists = group.getShoppingLists();
		        ShoppingListService shoppingListService = new ShoppingListService();
		        OutgoingDTO groupOutgoings = new OutgoingDTO();
		        //for each active shopping list in group, ask to shoppingList service for the costs...
		        for (ShoppingList shopList : shoppingLists){
		        	if(!shopList.getArchiveSate()){
			        	OutgoingDTO localOutGoing= new Gson().fromJson(shoppingListService.getOutgoings(shopList.getId(), this._request), outGoingType);
			        	groupOutgoings.addToCurrentCost(localOutGoing.getCurrentCost());
			        	groupOutgoings.addToleftToPay(localOutGoing.getLeftToPay());
		        	}
		        }
		        
		        return new Gson().toJson(groupOutgoings);
				
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson(ONFE.getMessage());
			}
		}
	}
	// ======================================
    // =           transferMoney      	    =
    // ======================================
	/**
     * Transfer Money between account within a group.
     * @param memberDestId where the money goes
     * @param groupId group where transaction is done
     * @param value amount of transfer
     * @return Json : request state
     */
	@GET
	@Path("/transferMoney")
	public String transferMoney(@QueryParam("memberDestId") String memberDestId,
								@QueryParam("groupId") String groupId,  
								@QueryParam("value") float value) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
	
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{ 
			try{
		        GroupMember groupBalanceMemberOrig = dao_A.findGroupMemberForMemberIdAndGroupId(memberId, groupId);
		        groupBalanceMemberOrig.updateMemberBalance(value);
		        GroupMember groupBalanceMemberDest = dao_A.findGroupMemberForMemberIdAndGroupId(memberDestId, groupId);
		        groupBalanceMemberDest.updateMemberBalance(-value);
		        //save new balances
		        dao_A.mergeGroupMember(groupBalanceMemberOrig);
		        dao_A.mergeGroupMember(groupBalanceMemberDest);
		        
		        Member origMember = dao_A.findMemberById(memberId);
		        Member destMember = dao_A.findMemberById(memberDestId);
		        
		        String description = "Member " + origMember.getName() + " did pay member " + destMember.getName() + " " + value + " euros"; 
		        saveInGroupHistory(groupId, description);
	        	return new Gson().toJson("Money transfer ok!");
	        }catch(DuplicateMemberException dmE){
	        	return new Gson().toJson(dmE.getMessage());
	        }catch(ObjectNotFoundException dmE){
	        	return new Gson().toJson(dmE.getMessage());
	        }
		}
	}
	// ======================================
    // =      removeShoppingList		    =
    // ======================================
	/**
     * Remove Shopping List.
     * @param groupId group where shopping list is
     * @param listId list to remove
     * @return Json : request state
     */
	@GET
	@Path("/removeShoppingList")
	@Produces("application/json")
	public String removeShoppingList(@QueryParam("groupId") String groupId, @QueryParam("listId")String listId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	

		//check if member is in the group
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{ 
			try{
				//find ShoppingList to delete 
				ShoppingList shoppingListToDelete = dao_A.findShoppingListById(listId);
				Group groupToUpdate = dao_A.findGroupById(groupId);	
				//to maintain relationship
				groupToUpdate.deleteShoppingList(listId);
				dao_A.mergeGroup(groupToUpdate);
				
				dao_A.removeShoppingList(shoppingListToDelete);
				saveInGroupHistory(groupId, "List " + shoppingListToDelete.getName() + " has been removed...");
				return new Gson().toJson("shopping list removed");
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson(ONFE.getMessage());
			}
		}
	}
	// ======================================
    // =           getMemberBalance		    =
    // ======================================
	/**
     * Get Member Balance.
     * @param groupId : group where member is
     * @param memberId : member Id
     * @return Json : Value of balance for this member in this group
     */
	@GET
	@Path("/getMemberBalance")
	@Produces("application/json")
	public String getMemberBalance(@QueryParam("groupId") String groupId,  @QueryParam("memberId") String memberId) throws JSONException {
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	

		try{
	        Gson gsonMaker = new Gson();
			List<GroupMember> groupMembers = dao_A.findAllGroupsForMemberId(memberId);
			for (GroupMember gp : groupMembers){
				if (gp.getGourpId().equals(groupId)){
					return gsonMaker.toJson(gp.getBalance());
				}
			}	
			return null;
		}catch(ObjectNotFoundException ONFE){
			return null;
		}
		
	}
	// ======================================
    // =           getGroupHistory		    =
    // ======================================
	/**
     * Get Group History.
     * @param groupId 
     * @return Json : List<GroupOperationDTO>
     */
	@GET
	@Path("/getGroupHistory")
	@Produces("application/json")
	public String getGroupHistory(@QueryParam("groupId") String groupId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{ 
			try{
		        Group group = dao_A.findGroupById(groupId);
				List<GroupOperation> groupOperations = (List<GroupOperation>) group.getGroupHistory();
				List<GroupOperationDTO> groupHistorysDTO = new LinkedList<GroupOperationDTO>();
				for (GroupOperation gtr : groupOperations){
					GroupOperationDTO groupOperationDTO = new GroupOperationDTO(gtr);
					groupHistorysDTO.add(groupOperationDTO);
				}
				
				return new Gson().toJson(groupHistorysDTO);
		
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson(ONFE);
			}
		}
	}
	// ======================================
    // =           clearGroupHistory	    =
    // ======================================
	/**
     * Clear Group History.
     * @param groupId 
     * @return Json : request state
     */
	@GET
	@Path("/clearGroupHistory")
	@Produces("application/json")
	public String clearGroupHistory(@QueryParam("groupId") String groupId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member is in the group
		if (!isMemberInGroup(memberId, groupId))
			return new Gson().toJson("Not in group");
		else{ 
			try{
				Member member = dao_A.findMemberById(memberId);
		        Group group = dao_A.findGroupById(groupId);
				List<GroupOperation> groupHistory = (List<GroupOperation>) group.getGroupHistory();
				for (GroupOperation gtr : groupHistory){
					dao_A.removeTransaction(gtr);
				}
				group.clearHistory();
				dao_A.persistGroup(group);
				saveInGroupHistory(groupId, "Member " + member.getName() + " deleted hystory!");
				return new Gson().toJson("History cleared");
		
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson(ONFE);
			}
		}
	}
	// ======================================
    // =           processBalances		    =
    // ======================================
	/**
     * Process Balances, will calculate optimized transactions to do.
     * in order to balance all accounts 
     * @param groupId 
     * @return Json : List of TransactionDTO
     */
	@GET
	@Path("/processBalances")
	@Produces("application/json")
	public String processBalances(@QueryParam("groupId") String groupId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");

		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		List<GroupMember> groupMembers = dao_A.findAllMembersForGroupId(groupId);
		TransactionOptimizer transactionOptimizer = new TransactionOptimizer(groupMembers);
		List<TransactionDTO> transactionDTOs = transactionOptimizer.getTransactions();
		//we need to add informations (member name from Id) to those transactions
		for (TransactionDTO tr : transactionDTOs){
			Member locMemberOrig = dao_A.findMemberById(tr.getMemberOrigId());
			Member locMemberDest = dao_A.findMemberById(tr.getMemberDestId());
			tr.setMemberOrigName(locMemberOrig.getName());
			tr.setMemberDestName(locMemberDest.getName());
		}
		
		return new Gson().toJson(transactionDTOs);
		
	}
	// ======================================
    // =           isMemberInGroup	   	    =
    // ======================================
	private boolean isMemberInGroup(String memberId, String groupId){
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 
		
		try{
			Member member = dao_A.findMemberById(memberId);
			Group group = dao_A.findGroupById(groupId);
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

	// ======================================
    // =           equilibreAccounts   	    =
	// Method called from MemberService :	=
	// When a member Leave the group		=
    // ======================================
	public void equilibreAccounts(String groupId) {
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 
		
		try{
			List<GroupMember> groupMembers = dao_A.findAllMembersForGroupId(groupId);
			//first determine average value
			float average = 0;
			int i = 0;
			for (GroupMember gp : groupMembers){
				average = average + gp.getBalance();
				i++;
			}
			if(i>0)
				average = average/i;
			round(average, 2);
			//update balance
			for (GroupMember gp : groupMembers){
				gp.updateMemberBalance(-average);
				dao_A.mergeGroupMember(gp);
			}
		}catch(ObjectNotFoundException ONFE){
			
		}
	}
	// ======================================
    // =           addMember        	    =
	// Method called from MemberService :	=
	// When a member accept an invitation	=
    // ======================================
	public String addMember(String memberId, String groupID) throws JSONException {
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU");     
		try{
	        Member member = dao_A.findMemberById(memberId);
	        Group group = dao_A.findGroupById(groupID);
	        
	        //check that the member is not a part of the group
	        List<Group> groupsOfMember = member.getGroups();
	        for(Group gr : groupsOfMember){
	        	if (gr.getId().equals(groupID))
	        		 return new Gson().toJson("Member is already in group!");  
	        }
	        	
	        member.addGroup(group);
	        group.addMember(member);
		
	        dao_A.mergeGroup(group);

	        saveInGroupHistory(groupID, "Member " + member.getName() + " joined this group");
	        
	        return new Gson().toJson("Member has been added");  
		 }catch(ObjectNotFoundException dmE){
	        return new Gson().toJson(dmE.getMessage());
         }
	}
	// ======================================
    // =           saveInGroupHistory	    =
    // ======================================
	public void saveInGroupHistory(String groupId, String description){
		
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU");
		Group group = dao_A.findGroupById(groupId);
		GroupOperation newGroupHistory = new GroupOperation(group, description);
		
		dao_A.persistTransaction(newGroupHistory);	
		dao_A.mergeGroup(group);
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
	// ======================================
    // =           round		   		    =
    // ======================================
	public static float round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (float) tmp / factor;
	}
	//Simple classes used to serialize object into Json
	// ======================================
	//			Class						=
    // =           MemberInGroupDesc        =
    // ======================================
	class MemberInGroupDesc{
		String id;
		String name;
		String email;
		float balance;
		MemberInGroupDesc(String id, String name, String email, float balance){
			this.id = id;
			this.name = name;
			this.email = email;
			this.balance = balance;
		}
	}	
}

