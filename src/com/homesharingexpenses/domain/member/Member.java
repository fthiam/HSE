package com.homesharingexpenses.domain.member;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.homesharingexpenses.domain.DomainObject;
import com.homesharingexpenses.domain.group.Group;

/**
 * Entity that represents a member
 * @author THIAM
 *
 */
@Entity
@Table(name = "T_MEMBER")
@NamedQueries( {
	 @NamedQuery(name="Member.findByEmail",
            query="SELECT mB FROM Member mB WHERE mB._emailAdress = :emailAdress"),
} )
public class Member extends DomainObject implements Serializable {
	
	@Id
	@Column(name = "id", length = 10)
	@TableGenerator(name="TABLE_GEN_MEMBER", table="T_COUNTER", pkColumnName="name",
	valueColumnName="value", pkColumnValue="Member")
	@GeneratedValue(strategy=GenerationType.TABLE, generator="TABLE_GEN_MEMBER") 
	private String _id;
	@Column(name = "name", nullable = false, length = 50)
	private String _name;
	@Column(name = "forename", nullable = false, length = 50)
	private String _forename;
	@Column(name = "emailAdress", nullable = false, length = 50)
	private String _emailAdress;
	@Column(name = "passWord", nullable = false, length = 50)
	private String _passWord;	
	@Column(name = "information", nullable = false, length = 50)
	private String _information;
	@ManyToMany(mappedBy="_members", fetch =FetchType.EAGER)
	private List<Group> _groups = new LinkedList<Group>();
	@OneToMany
	@JoinTable(
		      name="T_INVITATION",
		      joinColumns=@JoinColumn(name="memberDestID", referencedColumnName="id"),
		      inverseJoinColumns=@JoinColumn(name="groupID", referencedColumnName="id"))
	private List<Group> _groupsWhoSentInvitation = new LinkedList<Group>();
	
		
	// ======================================
    // =           Member        		    =
    // ======================================
	/**
	 * Default constructor
	 */
	public Member (){
		this._emailAdress = "";
		this._passWord = "";
		this._name = "noName";
	}
	// ======================================
    // =           Member        		    =
    // ======================================
	/**
	 * @param name
	 * @param forename
	 * @param email
	 * @param passWord
	 */
	public Member (String name, String forename, String email, String passWord){
		this._emailAdress = email;
		this._passWord = passWord;
		this._name = name;
		this._forename = forename;
	}
	// ======================================
    // =           setInformation        	=
    // ======================================
	/**
	 * Set member's information
	 * @param information
	 */
	public void setInformation (String information){
		this._information = information;
	}
	// ======================================
    // =           getEmailAdress     		=
    // ======================================
	/**
	 * Get member's email
	 * @return
	 */
	public String getEmailAdress(){
		return this._emailAdress;
	}
	// ======================================
    // =           getName	        		=
    // ======================================
	/**
	 * Get member's name
	 * @return
	 */
	public String getName(){
		return this._name;
	}
	// ======================================
    // =           getForename	        	=
    // ======================================
	/**
	 * Get member's forename
	 * @return
	 */
	public String getForename(){
		return this._forename;
	}
	// ======================================
    // =           getGroups	        	=
    // ======================================
	/**
	 * Get member's groups
	 * @return
	 */
	public List<Group> getGroups(){
		return this._groups;
	}
	// ======================================
    // =           removeGroup	        	=
    // ======================================
	/**
	 * Remove group 
	 * @param groupId
	 * @return
	 */
	public boolean removeGroup(String groupId){
		for(int i = 0; i <= _groups.size(); i++){
			if (_groups.get(i).getId().equals(groupId)){
				_groups.remove(i);
				return true;
			}
		}
		return false;
	}
	// ======================================
    // =           addGroup	        	=
    // ======================================
	/**
	 * Add group
	 * @param group
	 */
	public void addGroup(Group group){
		this._groups.add(group);
	}
	// ======================================
    // =           getInvitations        	=
    // ======================================
	/**
	 * Get member's invitations
	 * @return
	 */
	public List<Group> getInvitations(){
		return this._groupsWhoSentInvitation;
	}
	// ======================================
    // =           checkPassword        	=
    // ======================================
	/**
	 * Check member's password 
	 * @param password
	 * @return
	 */
	public boolean checkPassword(String password){
		return this._passWord.equals(password);
	}
	// ======================================
    // =           checkInvitation        	=
    // ======================================
	/**
	 * Check if an invitation already exists
	 * @param groupID
	 * @return
	 */
	public boolean checkInvitationForGroupId(String groupID){

		for (Group Gp : this._groupsWhoSentInvitation){
			if (Gp.getId().equals(groupID))
				return true;
		}
		return false;
	}
	// ======================================
    // =           getId	        		=
    // ======================================
	public String getId() {
		return this._id;
	}
	// ======================================
    // =           setId	        		=
    // ======================================
	public void setId(String id) {
		this._id = id;
	}
}
