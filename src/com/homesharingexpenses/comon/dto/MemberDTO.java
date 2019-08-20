package com.homesharingexpenses.comon.dto;

import javax.persistence.Column;

//======================================
//= Class						  	   =
//=           MemberDTO			       =
//======================================
/**
 * @author THIAM
 *
 */
public class MemberDTO {
	private String name;
	private String forename;
	private String emailAdress;
	private String passWord;	
	private String information;
	
	/**
	 * @param name
	 * @param forename
	 * @param emailAdress
	 * @param passWord
	 * @param information
	 */
	public MemberDTO( String name, String forename,String emailAdress,String passWord,String information){
		this.name = name;
		this.forename = forename;
		this.emailAdress = emailAdress;
		this.passWord = passWord;
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return
	 */
	public String getForename() {
		return forename;
	}
	/**
	 * @param forename
	 */
	public void setForename(String forename) {
		this.forename = forename;
	}
	/**
	 * @return
	 */
	public String getEmailAdress() {
		return emailAdress;
	}
	/**
	 * @param emailAdress
	 */
	public void setEmailAdress(String emailAdress) {
		this.emailAdress = emailAdress;
	}
	/**
	 * @return
	 */
	public String getPassWord() {
		return passWord;
	}
	/**
	 * @param passWord
	 */
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	/**
	 * @return
	 */
	public String getInformation() {
		return information;
	}
	/**
	 * @param information
	 */
	public void setInformation(String information) {
		this.information = information;
	}
}
