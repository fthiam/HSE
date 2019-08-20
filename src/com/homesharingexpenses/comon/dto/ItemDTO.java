package com.homesharingexpenses.comon.dto;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;

import com.google.gson.Gson;
import com.homesharingexpenses.domain.item.Item;
import com.homesharingexpenses.domain.shoppinglist.ShoppingList;
//======================================
//= Class						  	   =
//=           ItemDTO			       =
//======================================
/**
 * @author THIAM
 *
 */
public class ItemDTO {

	private String id;
	private String name;
	private float price;
	private String description;
	private boolean state;
	private String itemOwners;
	private String date;
	private String buyer;

	/**
	 * @param item
	 * @param itemOwners
	 */
	public ItemDTO (Item item, String itemOwners){
		this.id = item.getId();
		this.name = item.getName();
		this.price = item.getPrice();
		this.description = item.getDescription();
		this.state = item.getPurshasedState();	
		this.itemOwners = itemOwners;
		this.date = item.getDate();
		this.buyer = item.getBuyer();
	}
	
	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param price
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param purshased
	 */
	public void setState(boolean purshased) {
		this.state = purshased;
	}
	
    /**
     * @return
     */
    public String getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return
	 */
	public boolean getState() {
		return state;
	}
	
}
