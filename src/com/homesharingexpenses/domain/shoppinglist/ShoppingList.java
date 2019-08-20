package com.homesharingexpenses.domain.shoppinglist;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.homesharingexpenses.domain.DomainObject;
import com.homesharingexpenses.domain.group.Group;
import com.homesharingexpenses.domain.item.Item;

/**
 * Entity that represents a shopping list 
 * @author THIAM
 *
 */
@Entity
@Table(name = "T_SHOPPING_LIST")
public class ShoppingList extends DomainObject implements Serializable{

	@Id
    @Column(name = "id", length = 10)
    @TableGenerator(name="TABLE_GEN_SHOPPIN_LIST", table="T_COUNTER", pkColumnName="name",
        valueColumnName="value", pkColumnValue="ShoppingList")
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TABLE_GEN_SHOPPIN_LIST") 
    private String _id;
	@Column(name = "name", nullable = false, length = 50)
	private String _name;
	@ManyToOne
    private Group _group;
	@OneToMany (mappedBy ="_shoppingList", fetch =FetchType.EAGER, cascade =CascadeType.ALL)
	private Collection<Item> _items = new LinkedList<Item>();
	@Column(name = "archive_state")
	boolean _archiveState;
	@Column(name = "archive_date")
	private java.sql.Date _dateArchiveDate;
	
	// ======================================
    // =           ShoppingList    		    =
    // ======================================
	/**
	 * Default constructor
	 */
	public ShoppingList (){
		this._archiveState = false;
		this._name = "noName";
	}
	// ======================================
    // =           ShoppingList    		    =
    // ======================================
	/**
	 * @param name
	 * @param group
	 */
	public ShoppingList (String name, Group group){
		this._archiveState = false;
		this._name = name;
		this._group = group;
		//Maintain relationship 
		this._group.addShoppingList(this);
		Date date = new Date();
		//init
		this._dateArchiveDate = new java.sql.Date(date.getTime());
	}
	
	// ======================================
    // =           getGroup		   		    =
    // ======================================
	/**
	 * Get corresponding group
	 * @return
	 */
	public Group getGroup(){
		return this._group;
	}
	// ======================================
    // =           setGroup		   		    =
    // ======================================
	/**
	 * Set group
	 * @param group
	 */
	public void setGroup(final Group group){
		this._group = group;
	}
	// ======================================
    // =           getName	    		    =
    // ======================================
	/**
	 * Get shopping list's name
	 * @return
	 */
	public String getName(){
		return this._name;
	}
	// ======================================
    // =           addItem	    		    =
    // ======================================
	/**
	 * Add an item in list
	 * @param itemToAdd
	 */
	public void addItem(Item itemToAdd){
		this._items.add(itemToAdd);
	}
	// ======================================
    // =           getItems	    		    =
    // ======================================
	/**
	 * Get list of items
	 * @return
	 */
	public Collection<Item> getItems(){
		return this._items;
	}
	// ======================================
    // =           deleteItemFromList		=
    // ======================================
	/**
	 * Delete an item from list
	 * @param itemID
	 */
	public void deleteItemFromList(String itemID){
		for (Iterator<Item> iT = _items.iterator(); iT.hasNext();) {
			Item item = iT.next();
			if (item.getId().equals(itemID)){
		        // Remove the current element from the iterator and the list.
		        iT.remove();
		    }
		}
	}
	// ======================================
    // =       archiveShoppingList		    =
    // ======================================
	public void archiveShoppingList() {
		this._archiveState = true;
		Date date = new Date();
		this._dateArchiveDate = new java.sql.Date(date.getTime());
	}
	// ======================================
    // =           getArchiveSate		    =
    // ======================================
	/**
	 * Get archive state 
	 * If true then the list has been archived
	 * @return
	 */
	public boolean getArchiveSate() {
		return _archiveState;
	}
	// ======================================
    // =       getArchiveDate			    =
    // ======================================
	/**
	 * Get archive date
	 * @return
	 */
	public String getArchiveDate() {
		return this._dateArchiveDate.toString();
	}
	// ======================================
    // =       getTotalPurshased		    =
    // ======================================
	/**
	 * Get total of purchased items in list
	 * @return
	 */
	public float getTotalPurshased() {
		float totalValue = 0;
		for(Item it : _items){
			if (it.getPurshasedState())
				totalValue = totalValue + it.getPrice();
		}
		return totalValue;
	}
	// ======================================
    // =       getLeftToBuy				    =
    // ======================================
	/**
	 * Get total of left to buy in list
	 * @return
	 */
	public float getLeftToBuy() {
		float totalValue = 0;
		for(Item it : _items){
			if (!it.getPurshasedState())
				totalValue = totalValue + it.getPrice();
		}
		return totalValue;
	}
	// ======================================
    // =           setId	    		    =
    // ======================================
	@Override
	public void setId(String id) {
		this._id = id;
	}
	// ======================================
    // =           getId	    		    =
    // ======================================
	public String getId(){
		return this._id;
	}
}
