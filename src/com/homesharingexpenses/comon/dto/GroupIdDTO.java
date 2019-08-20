package com.homesharingexpenses.comon.dto;

//======================================
//= Class						  	   =
//=           GroupIdDTO		       =
//======================================
/**
 * @author THIAM
 *
 */
public	class GroupIdDTO{
	String id;
	String name;
	/**
	 * @param id
	 * @param name
	 */
	public GroupIdDTO(String id, String name){
		this.id = id;
		this.name = name;
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