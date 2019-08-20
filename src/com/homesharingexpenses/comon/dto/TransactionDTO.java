package com.homesharingexpenses.comon.dto;

//======================================
//Class								   =
//=           TransactionDTO           =
//======================================
/**
 * @author THIAM
 *
 */
public class TransactionDTO {
	private String memberOrigId;
	private String memberOrigName;
	private String memberDestId;
	private String memberDestName;
	private float value;
	
	/**
	 * @param memberOrigId
	 * @param memberDestId
	 * @param value
	 */
	public TransactionDTO (String memberOrigId, String memberDestId, float value){
		this.memberOrigId = memberOrigId;
		this.memberDestId = memberDestId;
		this.value = value;
	}
	
	/**
	 * @return
	 */
	public String getMemberOrigId() {
		return memberOrigId;
	}
	/**
	 * @param memberOrigId
	 */
	public void setMemberOrigId(String memberOrigId) {
		this.memberOrigId = memberOrigId;
	}
	/**
	 * @return
	 */
	public String getMemberDestId() {
		return memberDestId;
	}
	/**
	 * @param memberDestId
	 */
	public void setMemberDestId(String memberDestId) {
		this.memberDestId = memberDestId;
	}
	/**
	 * @return
	 */
	public float getValue() {
		return value;
	}
	/**
	 * @param value
	 */
	public void setValue(float value) {
		this.value = value;
	}

	/**
	 * @param memberOrigName
	 */
	public void setMemberOrigName(String memberOrigName){
		this.memberOrigName = memberOrigName;
	}
	
	/**
	 * @param memberDestName
	 */
	public void setMemberDestName(String memberDestName){
		this.memberDestName = memberDestName;
	}
}
