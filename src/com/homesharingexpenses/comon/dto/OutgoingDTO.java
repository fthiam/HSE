package com.homesharingexpenses.comon.dto;

// ======================================
//			Class						=
// =           Outgoing        			=
// ======================================
/**
 * @author THIAM
 *
 */
public class OutgoingDTO{

	float currentCost;
	float leftToPay;
	/**
	 * 
	 */
	public OutgoingDTO(){
		this.currentCost = 0;
		this.leftToPay = 0;
	}
	/**
	 * @param value
	 */
	public void addToCurrentCost (float value){
		this.currentCost = this.currentCost + value;
	}
	/**
	 * @param value
	 */
	public void addToleftToPay (float value){
		this.leftToPay = this.leftToPay + value;
	}
	/**
	 * @return
	 */
	public float getCurrentCost() {
		return currentCost;
	}
	/**
	 * @return
	 */
	public float getLeftToPay() {
		return leftToPay;
	}
}
