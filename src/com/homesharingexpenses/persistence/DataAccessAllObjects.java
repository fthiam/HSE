package com.homesharingexpenses.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.homesharingexpenses.comon.exception.DuplicateMemberException;
import com.homesharingexpenses.comon.exception.ObjectNotFoundException;
import com.homesharingexpenses.domain.group.Group;
import com.homesharingexpenses.domain.group.member.GroupMember;
import com.homesharingexpenses.domain.group.operation.GroupOperation;
import com.homesharingexpenses.domain.item.Item;
import com.homesharingexpenses.domain.item.owner.ItemOwner;
import com.homesharingexpenses.domain.member.Member;
import com.homesharingexpenses.domain.member.invitation.Invitation;
import com.homesharingexpenses.domain.shoppinglist.ShoppingList;

public class DataAccessAllObjects {

	 protected EntityManager _em;
	 protected EntityTransaction _tx;
	 
	 public DataAccessAllObjects (String puName){
		 EntityManagerFactory emf = Persistence.createEntityManagerFactory(puName);

		 this._em = emf.createEntityManager();
		 initEntityManager();
	 }
	 
	// ======================================
	// =       Generic CRUD methods         
	// =		GROUP
	// ======================================
	public void persistGroup(Group group) {
	    beginTransaction();
	    _em.persist(group);
	    endTransaction();
	}
	
	public void removeGroup(Group group) {
	    beginTransaction();
	    _em.remove(group);
	    endTransaction();
	}
	
	public Group findGroupById(String id) throws ObjectNotFoundException{
		Group result;
	    if (id == null) {
	       throw new ObjectNotFoundException("Id is null, cannot find group");
	    }
	    result = _em.find(Group.class, id);
	    if (result == null) {
	    	throw new ObjectNotFoundException("Group has not been found");
	    }
	    return result;
	}
	
	public void mergeGroup(Group group) {
	    beginTransaction();
	    _em.merge(group);
	    endTransaction();
	}
	 
	// ======================================
	// =       Generic CRUD methods         
	// =		MEMBER
	// ======================================
	public void persistMember(Member member) throws DuplicateMemberException{
		try{
			findMemberByEmailAdress(member.getEmailAdress());
			//two members with same email not allowed...
			//Didn't throw exception then member exists !
			throw new DuplicateMemberException("Email already in use ...");
		}catch (ObjectNotFoundException oNf){
			beginTransaction();
			_em.persist(member);
			endTransaction();
		}
	}
	
	public void removeMember(Member member) throws ObjectNotFoundException{
		findMemberById(member.getId());
	    beginTransaction();
	    _em.remove(member);
	    endTransaction();
	}
	
	public Member findMemberById(String id) throws ObjectNotFoundException{
		Member result;
	    if (id == null) {
	    	 throw new ObjectNotFoundException("Id is null, cannot find member");
	    }
	    result = _em.find(Member.class, id);
	    if (result == null) {
	    	throw new ObjectNotFoundException("Member has not been found");
	    }
	    return result;
	}
	
	public void mergeMember(Member member) throws ObjectNotFoundException{
		findMemberById(member.getId());
		
	    beginTransaction();
	    _em.merge(member);
	    endTransaction();
	}
	// ======================================
	// =      Specific Member method          
	// =		findMemberByEmailAdress
	// ======================================
	public Member findMemberByEmailAdress(String emailAdress)throws ObjectNotFoundException{
		Query query = this._em.createNamedQuery("Member.findByEmail");
		query.setParameter("emailAdress", emailAdress);
		List<Member> Members = (List<Member>) query.getResultList();
		
		if(Members.isEmpty())
			throw new ObjectNotFoundException("Member with mail " + emailAdress +" not found...");
		
		//TODO for now, take only the first one, not supposed to have several members with same mail adress...
		return Members.get(0);
	}
	
	// ======================================
	// =       Generic CRUD methods         
	// =		Invitation
	// ======================================
	public void persistInvitation(Invitation invitation) throws ObjectNotFoundException, DuplicateMemberException{
		try{
			//look if member has already been invitated 
			Member member = findMemberById(invitation.getDestMemberId());
			
			if (member.checkInvitationForGroupId(invitation.getGourpId()))
				throw new DuplicateMemberException("Invitation already exists ...");
			else {
				beginTransaction();
			    _em.persist(invitation);
			    endTransaction();
			}
		}catch (ObjectNotFoundException oNf){
			throw new ObjectNotFoundException ("Try to send invitation to unexisting member...");
		}
	}
	
	public void removeInvitation(Invitation invitation) throws ObjectNotFoundException{
		findInvitationById(invitation.getId());
		
	    beginTransaction();
	    _em.remove(invitation);
	    endTransaction();
	}
	
	public Invitation findInvitationById(String id) throws ObjectNotFoundException{
		Invitation result;
	    if (id == null) {
	    	throw new ObjectNotFoundException("Id is null, cannot find member");
	    }
	    int idInt = Integer.parseInt(id);
	    result = _em.find(Invitation.class, idInt );
	    if (result == null) {
	    	throw new ObjectNotFoundException("Invitation not found");
	    }
	    return result;
	}
	// ======================================
	// =      Specific methods         
	// =		Invitation
	// ======================================
	public List<Invitation> findAllInvitationForMemberId(String memberID){
		Query query = this._em.createNamedQuery("Invitation.findByDestinateMemberId");
		query.setParameter("memberID", memberID);
		List<Invitation> invitations = (List<Invitation>) query.getResultList();
		
		return invitations;
	}
	
	// ======================================
	// =       Generic CRUD methods         
	// =		ShoppingList
	// ======================================
	public void persistShoppingList(ShoppingList shoppingList) {
	    beginTransaction();
	    _em.persist(shoppingList);
	    endTransaction();
	}
	
	public void removeShoppingList(ShoppingList shoppingList) {
		for (Item it: shoppingList.getItems()){
			this.removeItem(it);
		}
	    beginTransaction();
	    _em.remove(shoppingList);
	    endTransaction();
	}
	
	public ShoppingList findShoppingListById(String id) throws ObjectNotFoundException{
		ShoppingList result;
	    if (id == null) {
	    	throw new ObjectNotFoundException("Trying to find shopping list : Id is null");
	    }
	    result = _em.find(ShoppingList.class, id);
	    if (result == null) {
	    	throw new ObjectNotFoundException("Can't find shopping list");
	    }
	    return result;
	}
	
	public void mergeShoppingList(ShoppingList shoppingList) throws ObjectNotFoundException{
		findShoppingListById(shoppingList.getId());
	    beginTransaction();
	    _em.merge(shoppingList);
	    endTransaction();
	}
	
	// ======================================
	// =       Generic CRUD methods         
	// =		Item
	// ======================================
	public void persistItem(Item item) {
	    beginTransaction();
	    _em.persist(item);
	    endTransaction();
	}
	
	public void removeItem(Item item) {
		//We need to delete ItemOwners too
		//we do it here so we don't need to do it in service (automatic treatment..)
		List<ItemOwner> itemOwners = item.getItemOwnerList();
		for(ItemOwner itO : itemOwners){
			this.removeItemOwner(itO);
		}
	    beginTransaction();
	    _em.remove(item);
	    endTransaction();
	}
	
	public Item findItemById(String id) throws ObjectNotFoundException{
		Item result;
	    if (id == null) {
	    	throw new ObjectNotFoundException("Trying to find item : Id is null");
	    }
	    result = _em.find(Item.class, id);
	    if (result == null) {
	    	throw new ObjectNotFoundException("Can't find shopping list");
	    }
	    return result;
	}
	
	public void mergeItem(Item item) throws ObjectNotFoundException{
		findItemById(item.getId());
	    beginTransaction();
	    _em.merge(item);
	    endTransaction();
	}
	// ======================================      
	// =		GroupMember
	// ======================================
	public void mergeGroupMember(GroupMember groupMember) throws ObjectNotFoundException{
		beginTransaction();
	    _em.merge(groupMember);
	    endTransaction();
	}

	// ======================================
	// =      Specific methods         
	// =		GroupMember
	// ======================================
	public List<GroupMember> findAllGroupsForMemberId(String memberId){
		Query query = this._em.createNamedQuery("GroupMember.findByMemberID");
		query.setParameter("memberID", memberId);
		List<GroupMember> GroupMembers = (List<GroupMember>) query.getResultList();
		
		return GroupMembers;
	}
	public List<GroupMember> findAllMembersForGroupId(String groupId){
		Query query = this._em.createNamedQuery("GroupMember.findByGroupID");
		query.setParameter("groupID", groupId);
		List<GroupMember> GroupMembers = (List<GroupMember>) query.getResultList();
		
		return GroupMembers;
	}
	public GroupMember findGroupMemberForMemberIdAndGroupId(String memberID, String groupID) throws ObjectNotFoundException{
		Query query = this._em.createNamedQuery("GroupMember.findByGroupIdMemberId");
		query.setParameter("memberID", memberID);
		query.setParameter("groupID", groupID);
		List<GroupMember> groupMember = (List<GroupMember>) query.getResultList();
		if (groupMember.isEmpty())
			throw new ObjectNotFoundException("Member has not been found in this group");
		
		return groupMember.get(0);
	}
	// ======================================
	// =       Generic CRUD methods         
	// =		ItemOwner
	// ======================================
	public void removeItemOwner(ItemOwner itemOwner) {
	    beginTransaction();
	    _em.remove(itemOwner);
	    endTransaction();
	}
	
	public ItemOwner findItemOwnerById(String id) {
		ItemOwner result;
	    result = _em.find(ItemOwner.class, id);
	    if (result == null) {
	    }
	    return result;
	}
	// ======================================
	// =       Generic CRUD methods         
	// =		GroupTransaction
	// ======================================
	public void persistTransaction(GroupOperation groupOperation) {
	    beginTransaction();
	    _em.persist(groupOperation);
	    endTransaction();
	}
	
	public void removeTransaction(GroupOperation groupOperation) {
	    beginTransaction();
	    _em.remove(groupOperation);
	    endTransaction();
	}
	// ======================================
	// =       Entity Manager gesture       =
	// ======================================
	 public void initEntityManager(){
        try {
            _tx = _em.getTransaction();
        } catch (Exception e) {
            _tx = null;
        }
	 }
	// ======================================
	// =       Transaction gesture       	=
	// ======================================
	 private void beginTransaction() {
	        if (_tx != null && !_tx.isActive()) {
	            _tx.begin();
	        }
	    }
    private void endTransaction() {
        if (_tx != null) {
            _tx.commit();
        }
    }
}
