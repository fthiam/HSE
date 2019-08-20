package com.homesharingexpenses.domain.group;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.homesharingexpenses.domain.DomainObject;
import com.homesharingexpenses.domain.group.operation.GroupOperation;
import com.homesharingexpenses.domain.item.Item;
import com.homesharingexpenses.domain.member.Member;
import com.homesharingexpenses.domain.shoppinglist.ShoppingList;


/**
 * Entity that describes a group
 * @author THIAM
 *
 */
@Entity
@Table(name = "T_GROUP")
public class Group extends DomainObject implements Serializable{
	
	@Id
    @Column(name = "id", length = 10)
    @TableGenerator(name="TABLE_GEN_GROUP", table="T_COUNTER", pkColumnName="name",
        valueColumnName="value", pkColumnValue="Group")
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TABLE_GEN_GROUP") 
    private String _id;
	@Column(name = "name", nullable = false, length = 50)
	private String _name;
	@Column(name = "description", nullable = false, length = 50)
	private String _description;

	@ManyToMany(fetch =FetchType.EAGER, cascade =CascadeType.PERSIST)
	@JoinTable(
		      name="T_GROUP_MEMBER",
		      joinColumns=@JoinColumn(name="groupID", referencedColumnName="id"),
		      inverseJoinColumns=@JoinColumn(name="memberID", referencedColumnName="id"))
	private List<Member> _members = new LinkedList<Member>();
	
	@OneToMany (mappedBy ="_group", fetch =FetchType.EAGER, cascade =CascadeType.ALL)
	private Collection<ShoppingList> _shoppingLists = new LinkedList<ShoppingList>();
	
	@OneToMany (mappedBy ="_group", fetch =FetchType.EAGER, cascade =CascadeType.ALL)
	private Collection<GroupOperation> _goupHistory = new LinkedList<GroupOperation>();
	
	// ======================================
    // =           Group        		    =
    // ======================================
	/**
	 * Default constructor
	 */
	public Group(){
		this._name = "NoName";
	}
	
	// ======================================
    // =           Group        		    =
    // ======================================
	/**
	 * Group constructor
	 * @param member
	 * @param name
	 */
	public Group(Member member, String name){
		this._members.add(member);
		this._name = name;
	}
	
	// ======================================
    // =           getName        		    =
    // ======================================
	/**
	 * Get group name
	 * @return
	 */
	public String getName(){
		return this._name;
	}
	// ======================================
    // =        getShoppingLists	  		=
    // ======================================	
	/**
	 * Get group shopping lists
	 * @return
	 */
	public Collection getShoppingLists() {
	    return _shoppingLists;
    }

	// ======================================
    // =        addShoppingList	  		=
    // ======================================
    /**
     * Add a new shopping list for this group
     * @param shoppinglist
     */
    public void addShoppingList(final ShoppingList shoppinglist) {
    	_shoppingLists.add( shoppinglist);
    }
    // ======================================
    // =        addGroupHistory	  		=
    // ======================================
	/**
	 * Add a new operation in group history
	 * @param groupOperation
	 */
	public void addGroupHistory(GroupOperation groupOperation) {
		_goupHistory.add(groupOperation);		
	}	
	// ======================================
    // =           addMember     		    =
    // ======================================
	/**
	 * Add a member in group
	 * @param member
	 */
	public void addMember(Member member){
		this._members.add(member);
	}
	// ======================================
    // =           removeMember         	=
    // ======================================
	/**
	 * Remove member from group
	 * @param memberId
	 * @return
	 */
	public boolean removeMember(String memberId){
		for(int i = 0; i <= _members.size(); i++){
			if (_members.get(i).getId().equals(memberId)){
				_members.remove(i);
				return true;
			}
		}
		return false;
	}
	// ======================================
    // =      	deleteShoppingList    	    =
    // ======================================
	/**
	 * Delete shopping list
	 * @param listId
	 */
	public void deleteShoppingList(String listId){
		
		for (Iterator<ShoppingList> iT = _shoppingLists.iterator(); iT.hasNext();) {
			ShoppingList list = iT.next();
			if (list.getId().equals(listId)){
		        // Remove the current element from the iterator and the list.
		        iT.remove();
		    }
		}
	}
	
	// ======================================
    // =      	getId			    	    =
    // ======================================
	/**
	 * Get group id
	 * @return
	 */
	public String getId() {
		return this._id;
	}

	// ======================================
    // =      	setId			    	    =
    // ======================================
	/**
	 * Set group id
	 * @return
	 */
	public void setId(String id) {
		this._id = id;
	}
	// ======================================
    // =      	getDesciption	    	    =
    // ======================================
	/**
	 * Get group description
	 * @return
	 */
	public String getDescription() {
		return _description;
	}
	// ======================================
    // =      	setDesciption			    =
    // ======================================
	/**
	 * Set  group description
	 * @param _desciption
	 */
	public void setDescription(String _description) {
		this._description = _description;
	}
	// ======================================
    // =      	getMembers			    	=
    // ======================================
	/**
	 * Get members in group
	 * @return
	 */
	public List<Member> getMembers() {
		return _members;
	}
	// ======================================
    // =      	getGroupHistory		   		=
    // ======================================
	/**
	 * Get group history
	 * @return
	 */
	public Collection<GroupOperation> getGroupHistory() {
		return _goupHistory;
	}
	// ======================================
    // =      	clearHistory		   		=
    // ======================================
	/**
	 * Clear group history
	 */
	public void clearHistory() {
		this._goupHistory.clear();
	}
}
