package com.homesharingexpenses.domain.group.operation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.homesharingexpenses.domain.DomainObject;
import com.homesharingexpenses.domain.group.Group;

/**
 * Entity that describes a group operation
 * in order to have an history 
 * @author THIAM
 *
 */
@Entity
@Table(name = "T_GROUP_OPERATION")
public class GroupOperation extends DomainObject implements Serializable{

	@Id
    @Column(name = "id", length = 10)
    @TableGenerator(name="TABLE_GEN_TRANSACTION", table="T_COUNTER", pkColumnName="name",
    valueColumnName="value", pkColumnValue="GroupTransaction")
	@GeneratedValue(strategy=GenerationType.TABLE, generator="TABLE_GEN_TRANSACTION") 
    private int _id;
	@ManyToOne
    private Group _group;
	@Column(name = "description")
	private String _description;
	@Column(name = "date")
	private java.sql.Date _date;
	
	// ======================================
    // =           GroupTransaction		    =
    // ======================================
	/**
	 * Default constructor
	 */
	public GroupOperation (){
		this._description = ""; 
		Date date = new Date();
		this._date = new java.sql.Date(date.getTime());
	}
	// ======================================
    // =           GroupTransaction		    =
    // ======================================
	/**
	 * @param group
	 * @param description
	 */
	public GroupOperation (Group group, String description){
		this._description = description; 
		this._group = group;
		Date date = new Date();
		this._date = new java.sql.Date(date.getTime());
		//Maintain relationship 
		this._group.addGroupHistory(this);
	}
	// ======================================
    // =           getId				    =
    // ======================================
	/** 
	 * Get GroupOperation ID
	 * @return
	 */
	public String getId() {
		return String.valueOf(this._id);
	}
	// ======================================
    // =           setId				    =
    // ======================================
	/** 
	 * Set GroupOperation ID
	 */
	public void setId(String id) {
		this._id =  Integer.parseInt(id);
	}
	// ======================================
    // =           getDescription		    =
    // ======================================
	/**
	 * @return
	 */
	public String getDescription() {
		return _description;
	}
	// ======================================
    // =           setDescription		    =
    // ======================================
	/**
	 * @param _description
	 */
	public void setDescription(String _description) {
		this._description = _description;
	}
	// ======================================
    // =           getDate				    =
    // ======================================
	/**
	 * Get date of operation
	 * @return
	 */
	public java.sql.Date getDate() {
		return _date;
	}
	// ======================================
    // =           setDate				    =
    // ======================================
	/**
	 * Set date of operation
	 * @param _date
	 */
	public void setDate(java.sql.Date _date) {
		this._date = _date;
	}
	// ======================================
    // =           getGroup				    =
    // ======================================
	/**
	 * Get concerned group
	 * @return
	 */
	public Group getGroup() {
		return _group;
	}
	// ======================================
    // =           setGroup		   		    =
    // ======================================
	/**
	 * Set concerned group
	 * @param group
	 */
	public void setGroup(final Group group){
		this._group = group;
	}

}
