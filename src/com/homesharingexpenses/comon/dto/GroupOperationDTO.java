package com.homesharingexpenses.comon.dto;

import java.sql.Date;

import com.homesharingexpenses.domain.group.operation.GroupOperation;

// ======================================
//			Class						=
// =           GroupOperationDTO	    =
// ======================================
/**
 * @author THIAM
 *
 */
public class GroupOperationDTO{
	String id;
	String description;
	Date date;
	/**
	 * @param groupOperation
	 */
	public GroupOperationDTO(GroupOperation groupOperation){
		this.id = groupOperation.getId();
		this.description = groupOperation.getDescription();
		this.date = groupOperation.getDate();
	}
}