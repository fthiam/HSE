package com.homesharingexpenses.comon.dto;

import com.homesharingexpenses.domain.shoppinglist.ShoppingList;

// ======================================
//			Class						=
// =           ShoppingListDTO          =
// ======================================
/**
 * @author THIAM
 *
 */
public class ShoppingListDTO{
	String id;
	String name;
	boolean archiveState;
	float totalPurshased;
	float leftToBuy;
	String date;
	
	/**
	 * @param shoppingList
	 */
	public ShoppingListDTO(ShoppingList shoppingList){
		this.id = shoppingList.getId();
		this.name = shoppingList.getName();
		this.archiveState = shoppingList.getArchiveSate();
		this.date = shoppingList.getArchiveDate();
		this.totalPurshased = shoppingList.getTotalPurshased();
		this.leftToBuy = shoppingList.getLeftToBuy();
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
}