package com.homesharingexpenses.domain.group.member;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.homesharingexpenses.domain.DomainObject;

/**
 * Entity that describes an association between a group and a member
 * @author THIAM
 *
 */
@Entity
@Table(name = "T_GROUP_MEMBER")
@NamedQueries( {
	 @NamedQuery(name="GroupMember.findByGroupID",
             query="SELECT gB FROM GroupMember gB WHERE gB._groupID = :groupID"),
	 @NamedQuery(name="GroupMember.findByMemberID",
     		 query="SELECT gB FROM GroupMember gB WHERE gB._memberID = :memberID"),
	 @NamedQuery(name="GroupMember.findByGroupIdMemberId",
		 query="SELECT gB FROM GroupMember gB WHERE gB._memberID = :memberID AND gB._groupID = :groupID"),
} )
public class GroupMember extends DomainObject implements Serializable{
	
	@Id
    @Column(name = "id")
    private int _id;
	@Column(name = "groupID")
	private String _groupID;
	@Column(name = "memberID")
	private String _memberID;
	@Column(name = "balance")
	private float _balance;
	
	// ======================================
    // =           GroupMember    		    =
    // ======================================
	/**
	 * Default constructor
	 */
	public GroupMember(){
		this._groupID = "";
		this._memberID = "";
		this._balance = 0;
	}
	// ======================================
    // =           GroupMember    		    =
    // ======================================
	/**
	 * @param groupID
	 * @param memberID
	 */
	public GroupMember(String groupID, String memberID){
		this._groupID = groupID;
		this._memberID = memberID;
		this._balance = 0;
	}
	// ======================================
    // =           updateMemberBalance	    =
    // ======================================
	/**
	 * Update a member balance
	 * @param value
	 */
	public void updateMemberBalance(float value){
		this._balance  = this._balance + value;
	}
	// ======================================
    // =           getGourpId    		    =
    // ======================================
	/**
	 * Get group id
	 * @return
	 */
	public String getGourpId(){
		return this._groupID;
	}
	// ======================================
    // =           getId	    		    =
    // ======================================
	/** 
	 * get groupMember id
	 * @return
	 */
	public String getId() {
		return String.valueOf(this._id);
	}
	// ======================================
    // =           getBalance   		    =
    // ======================================
	/**
	 * Get member balance
	 * @return
	 */
	public float getBalance() {
		return this._balance;
	}
	// ======================================
    // =           setId	    		    =
    // ======================================
	public void setId(String id) {
		this._id =  Integer.parseInt(id);
	}
	// ======================================
    // =           getMemberID    		    =
    // ======================================
	public String getMemberID() {
		return _memberID;
	}
}
