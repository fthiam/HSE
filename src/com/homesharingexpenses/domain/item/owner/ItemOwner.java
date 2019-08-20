package com.homesharingexpenses.domain.item.owner;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.homesharingexpenses.domain.DomainObject;
import com.homesharingexpenses.domain.item.Item;

/**
 * Entity that represents an association between an item and a member
 * In order to know the scope for an item
 * @author THIAM
 *
 */
@Entity
@Table(name = "T_ITEM_OWNER")
@NamedQueries( {
	 @NamedQuery(name="ItemOwner.findByMemberId",
            query="SELECT ito FROM ItemOwner ito WHERE ito._memberID = :memberID"),
})
public class ItemOwner extends DomainObject implements Serializable{
	@Id
    @Column(name = "id", length = 10)
    @TableGenerator(name="TABLE_GEN_ITEM_OWNER", table="T_COUNTER", pkColumnName="name",
        valueColumnName="value", pkColumnValue="ItemOwner")
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TABLE_GEN_ITEM_OWNER") 
    private String _id;
	
	@Column(name = "memberID")
	private String _memberID;
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	Item _item;
	
	// ======================================
    // =           ItemOwner	    		=
    // ======================================
	/**
	 * Default constructor
	 */
	public ItemOwner(){
		this._memberID = "";
	}
	// ======================================
    // =           ItemOwner    			=
    // ======================================
	/**
	 * @param item
	 * @param memberID
	 */
	public ItemOwner(Item item, String memberID){
		this._item = item;
		this._memberID = memberID;
		//relation
		this._item.addItemOwner(this);
	}
	// ======================================
    // =           getItemId    		    =
    // ======================================
	/**
	 * Get item id
	 * @return
	 */
	public String getItemId(){
		return this._item.getId();
	}
	// ======================================
    // =           getMemberId    		    =
    // ======================================
	/**
	 * Get member id
	 * @return
	 */
	public String getMemberId(){
		return this._memberID;
	}
	// ======================================
    // =           getId	    		    =
    // ======================================
	public String getId() {
		return this._id;
	}
	// ======================================
    // =           setId	    		    =
    // ======================================
	public void setId(String id) {
		this._id =  id;
	}

}
