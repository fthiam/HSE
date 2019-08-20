package com.homesharingexpenses.comon.dto;

import com.homesharingexpenses.domain.group.member.GroupMember;

//======================================
//= Class						  	   =
//=           GroupMemberDTO	       =
//======================================
/**
 * @author THIAM
 *
 */
public class GroupMemberDTO {
	private String _id;
	private String _groupID;
	private String _memberID;
	private float _balance;
	 
	/**
	 * @param groupMember
	 */
	public GroupMemberDTO (GroupMember groupMember){
		 this._id = groupMember.getId();
		 this._groupID = groupMember.getGourpId();
		 this._balance = groupMember.getBalance();
		 this._memberID = groupMember.getMemberID();
	}
	
	/**
	 * @return
	 */
	public String getId() {
		return _id;
	}
	
	/**
	 * @param _id
	 */
	public void setId(String _id) {
		this._id = _id;
	}
	
	/**
	 * @return
	 */
	public String getGroupID() {
		return _groupID;
	}
	
	/**
	 * @param _groupID
	 */
	public void set_groupID(String _groupID) {
		this._groupID = _groupID;
	}
	
	/**
	 * @return
	 */
	public float getBalance() {
		return _balance;
	}
	
	/**
	 * @param _balance
	 */
	public void setBalance(float _balance) {
		this._balance = _balance;
	}
	
	/**
	 * @return
	 */
	public String getMemberID() {
		return _memberID;
	}
	
	/**
	 * @param _memberID
	 */
	public void setMemberID(String _memberID) {
		this._memberID = _memberID;
	}

	/**
	 * @param value
	 */
	public void updateMemberBalance(float value){
		this._balance  = this._balance + value;
	}

}
