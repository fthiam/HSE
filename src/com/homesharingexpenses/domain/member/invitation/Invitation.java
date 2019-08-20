package com.homesharingexpenses.domain.member.invitation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.homesharingexpenses.domain.DomainObject;

/**
 * Entity that represents a member's invitation for a group 
 * Also has id of the member who sent the invitation
 * @author THIAM
 *
 */
@Entity
@Table(name = "T_INVITATION")
@NamedQueries( {
	 @NamedQuery(name="Invitation.findByDestinateMemberId",
            query="SELECT iV FROM Invitation iV WHERE iV._memberDestID = :memberID")
})
public class Invitation extends DomainObject implements Serializable{
	
	@Id
    @Column(name = "id")
    private int _id;
	@Column(name = "groupID")
	private String _groupID;
	@Column(name = "memberDestID")
	private String _memberDestID;
	@Column(name = "memberOrigID")
	private String _memberOrigID;
	
	// ======================================
    // =           Invitation    		    =
    // ======================================
	/**
	 * Default constructor
	 */
	public Invitation(){
		this._groupID = "";
		this._memberDestID = "";
	}
	// ======================================
    // =           Invitation    		    =
    // ======================================
	/**
	 * @param groupID
	 * @param memberDestID
	 * @param memberOrgiId
	 */
	public Invitation(String groupID, String memberDestID, String memberOrgiId){
		this._groupID = groupID;
		this._memberDestID = memberDestID;
		this._memberOrigID = memberOrgiId;
	}
	// ======================================
    // =           getGourpId    	    	=
    // ======================================
	/**
	 * Get group id
	 * @return
	 */
	public String getGourpId(){
		return this._groupID;
	}
	// ======================================
    // =           getDestMemberId 		    =
    // ======================================
	/**
	 * Get member's id who is concerned by this invitation
	 * @return
	 */
	public String getDestMemberId(){
		return this._memberDestID;
	}
	// ======================================
    // =           getMemberOrigID 		    =
    // ======================================
	/**
	 * Get member's id who sent this invitation
	 * @return
	 */
	public String getMemberOrigID() {
		return _memberOrigID;
	}
	// ======================================
    // =           getId	    		    =
    // ======================================
	public String getId() {
		return String.valueOf(this._id);
	}
	// ======================================
    // =           setId	    		    =
    // ======================================
	public void setId(String id) {
		this._id =  Integer.parseInt(id);
	}
}
