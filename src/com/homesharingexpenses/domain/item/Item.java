package com.homesharingexpenses.domain.item;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.Iterator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.homesharingexpenses.domain.DomainObject;
import com.homesharingexpenses.domain.item.owner.ItemOwner;
import com.homesharingexpenses.domain.shoppinglist.ShoppingList;

/**
 * Entity that describes an item 
 * @author THIAM
 *
 */
@Entity
@Table(name = "T_ITEM")
public class Item  extends DomainObject implements Serializable{
	@Id
    @Column(name = "id", length = 10)
    @TableGenerator(name="TABLE_GEN_ITEM", table="T_COUNTER", pkColumnName="name",
        valueColumnName="value", pkColumnValue="Item")
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TABLE_GEN_ITEM") 
    private String _id;
	@Column(name = "name", nullable = false, length = 50)
	private String _name;
	@Column(name = "price", nullable = false, length = 50)
	private float _price;
	@Column(name = "description", nullable = true, length = 50)
	private String _description;
	@Column(name = "state", nullable = false, length = 50)
	private boolean _purshased;
	@Column(name = "date")
	private java.sql.Date _date;
	@Column(name = "buyer")
	private String _buyer;
	@OneToMany(mappedBy ="_item", fetch =FetchType.EAGER, cascade =CascadeType.ALL)
	private List<ItemOwner> _itemOwner = new LinkedList<ItemOwner>();
	@ManyToOne
    private ShoppingList _shoppingList;	
	
	// ======================================
    // =           Item	        		    =
    // ======================================
	/**
	 * Default constructor
	 */
	public Item (){
		this._shoppingList = null; 
		this._name = "noName";		
		this._price = 0;
		this._purshased = false;
		this._description = "";
		this._buyer = "";
	}
	
	// ======================================
    // =           Item	        		    =
    // ======================================
	/**
	 * @param name
	 * @param price
	 * @param shoppingList
	 * @param membersID
	 */
	public Item (String name, float price, ShoppingList shoppingList, List<String> membersID){
		this._shoppingList = shoppingList; 
		this._name = name;		
		this._price = price;
		this._purshased = false;
		this._description = "";
		//Maintain relationship 
		this._shoppingList.addItem(this);
		//make owner relation
		//If item is removed, itemOwner will be delete in DataAccessAllObjects
		for(String memberId : membersID){
			new ItemOwner(this, memberId);
		}
		Date date = new Date();
		this._date = new java.sql.Date(date.getTime());
		this._buyer = "";
	}
	// ======================================
    // =          getName		  		    =
    // ======================================
	/**
	 * Get item's name
	 * @return
	 */
	public String getName(){
		return this._name;
	}
	// ======================================
    // =           getDescription  		    =
    // ======================================
	/**
	 * Get item's description
	 * @return
	 */
	public String getDescription(){
		return this._description;
	}
	// ======================================
    // =           setState	 	  		    =
    // ======================================
	/**
	 * Set item's state
	 * true : Item has been purchased
	 * false : Item is to be purchased
	 * @param state
	 */
	public void setState(boolean state){
		this._purshased = state;
	}
	// ======================================
    // =           getPurshasedState	    =
    // ======================================
	/**
	 * Get item's state
	 * @return
	 */
	public boolean getPurshasedState(){
		return this._purshased;
	}
	// ======================================
    // =           setName	 	  		    =
    // ======================================
	/**
	 * Set item's name
	 * @param name
	 */
	public void setName(String name){
		this._name = name;
	}
	// ======================================
    // =           setDescription  		    =
    // ======================================
	/**
	 * Set item's description
	 * @param description
	 */
	public void setDescription(String description){
		this._description = description;
	}
	// ======================================
    // =           getPrice 	  		    =
    // ======================================
	/**
	 * Get item's price
	 * @return
	 */
	public float getPrice(){
		return this._price;
	}
	// ======================================
    // =           setPrice 	  		    =
    // ======================================
	/**
	 * Set item's price
	 * @param price
	 */
	public void setPrice(float price){
		this._price = price;
	}
	// ======================================
    // =           addItemOwner	  		    =
    // ======================================
	/**
	 * Add an item owner
	 * @param itemOwner
	 */
	public void addItemOwner(ItemOwner itemOwner){
		this._itemOwner.add(itemOwner);
	}
	// ======================================
    // =           getItemOwnerList		    =
    // ======================================	
	/**
	 * Get list of item's owners
	 * @return
	 */
	public List<String> getItemOwnersIds(){
		List<String> itemOwnersIds = new LinkedList<String>();
		for(ItemOwner ito : this._itemOwner){
			itemOwnersIds.add(ito.getMemberId());
		}
		return itemOwnersIds;
	}
	// ======================================
    // =           removeOwner 	  		    =
    // ======================================
	/**
	 * Remove an owner
	 * @param memberId
	 * @return
	 */
	public String removeOwner(String memberId) {
		String itemOwnerId = "";
		for (Iterator<ItemOwner> iTo = _itemOwner.iterator(); iTo.hasNext();) {
			ItemOwner itemOwner = iTo.next();
			if (itemOwner.getMemberId().equals(memberId)){
		        // Remove the current element from the iterator and the list.
		        iTo.remove();
		        itemOwnerId = itemOwner.getId();
		    }
		}
		return itemOwnerId;
	}
	// ======================================
    // =           removeAllOwners 		    =
    // ======================================
	/**
	 * @return
	 */
	public List<String> removeAllOwners() {
		List<String> itemOwnersIds = new LinkedList<String>();
		for (Iterator<ItemOwner> iTo = _itemOwner.iterator(); iTo.hasNext();) {
			ItemOwner itemOwner = iTo.next();
			itemOwnersIds.add(itemOwner.getId());
	        iTo.remove();
		}
		return itemOwnersIds;
	}
	// ======================================
    // =           buyItem	 	  		    =
    // ======================================
	/**
	 * Buy an item
	 * @param buyer
	 * @param price
	 */
	public void buyItem(String buyer, float price){
		this._purshased = true;
		Date date = new Date();
		this._date = new java.sql.Date(date.getTime());
		this._price = price;
		this._buyer = buyer;
	}
	// ======================================
    // =           restorItem 	  		    =
    // ======================================
	/**
	 * Restore an item
	 * Change it's state to false
	 */
	public void restorItem(){
		this._purshased = false;
		Date date = new Date();
		this._date = new java.sql.Date(date.getTime());
		this._buyer = "";
	}
	// ======================================
    // =           getShoppingListId	    =
    // ======================================
	/**
	 * Get shopping list id for this item
	 * @return
	 */
	public String getShoppingListId() {
		return this._shoppingList.getId();
	}
	// ======================================
    // =           getBuyer	 	  		    =
    // ======================================
	/**
	 * Get buyer's id
	 * @return
	 */
	public String getBuyer() {
		return _buyer;
	}
	// ======================================
    // =           getItemOwnerList		    =
    // ======================================
	/**
	 * Get item owner List
	 * @return
	 */
	public List<ItemOwner> getItemOwnerList() {
		return this._itemOwner;
	}
	// ======================================
    // =           getDate				    =
    // ======================================
	/**
	 * Get date of creation
	 * @return
	 */
	public String getDate() {
		return this._date.toString();
	}

	// ======================================
    // =           getId	 	  		    =
    // ======================================
	public String getId() {
		return this._id;
	}
	// ======================================
    // =           setId	 	  		    =
    // ======================================
	public void setId(String id) {
		this._id = id;
	}

	
}
