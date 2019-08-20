package com.homesharingexpenses.comon.process.transaction;

import java.util.LinkedList;
import java.util.List;

import com.homesharingexpenses.comon.dto.GroupMemberDTO;
import com.homesharingexpenses.comon.dto.TransactionDTO;
import com.homesharingexpenses.domain.group.member.GroupMember;

/**
 * Will determine the more efficient transactions to do  
 * @author THIAM
 *
 */
public class TransactionOptimizer {
	private List<TransactionDTO> _transactions = new LinkedList<TransactionDTO>();
	private List<GroupMemberDTO> _groupMembers = new LinkedList<GroupMemberDTO>();
	
	// ======================================
	// =      TransactionOptimizer
	// ======================================
	/**
	 * @param groupMembers
	 */
	public TransactionOptimizer(List<GroupMember> groupMembers) {
		for(GroupMember gp : groupMembers) {
			_groupMembers.add(new GroupMemberDTO (gp));
		}
		
		//Start optimizer algorithm here...
		if(_groupMembers.size() >= 2)
			this.findMinMaxBalances();
	}
	// ======================================
	// =      getTransactions
	// ======================================
	/**
	 * Will return transactions 
	 * @return
	 */
	public  List<TransactionDTO> getTransactions() {
		return this._transactions;
	}
	// ======================================
	// =     findMinMaxBalances
	// ======================================
	private void findMinMaxBalances() {
		GroupMemberDTO minBalanceForGroupFinded = _groupMembers.get(0);
		GroupMemberDTO maxBalanceForGroupFinded = _groupMembers.get(0);
		
		for(GroupMemberDTO gp : _groupMembers){
			if (gp.getBalance() < minBalanceForGroupFinded.getBalance())
				minBalanceForGroupFinded = gp;
			if (gp.getBalance() > maxBalanceForGroupFinded.getBalance())
				maxBalanceForGroupFinded = gp;
		}
		this.findTransaction(minBalanceForGroupFinded, maxBalanceForGroupFinded);
	}
	// ======================================
    // =           round		   		    
    // ======================================
	private static float round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (float) tmp / factor;
	}
	// ======================================
	// =      findTransaction
	// ======================================	
	private void findTransaction (GroupMemberDTO min, GroupMemberDTO max){
		LinkedList<GroupMemberDTO> localGroupMembers = new LinkedList<GroupMemberDTO>();
		localGroupMembers.addAll(_groupMembers);
		float minAccountValue;
		float maxAccountValue;
		//First check that min balance is negative..
		if(min.getBalance() < 0){
			minAccountValue = - min.getBalance();
			maxAccountValue = max.getBalance();
		}
		//we can't do anything else..
		else
			return;
		//The min balance is inferior to the biggest
		if(minAccountValue < maxAccountValue){
			float TransactionValue = round (minAccountValue, 2);
			if (TransactionValue != 0)
				_transactions.add(new TransactionDTO(min.getMemberID(), max.getMemberID(), TransactionValue));
			//Update group members left...
			for (GroupMemberDTO gp : localGroupMembers){
				if (gp.getMemberID().equals(min.getMemberID()))
					_groupMembers.remove(gp);
				if (gp.getMemberID().equals(max.getMemberID())){
					_groupMembers.remove(gp);
					gp.updateMemberBalance(-minAccountValue);
					_groupMembers.add(gp);
				}
			}
		}
		else if(minAccountValue > maxAccountValue){
			float TransactionValue = round (maxAccountValue, 2);
			if (TransactionValue != 0)
				_transactions.add(new TransactionDTO(min.getMemberID(), max.getMemberID(), TransactionValue));
			//Update group members left...
			for (GroupMemberDTO gp : localGroupMembers){
				if (gp.getMemberID().equals(max.getMemberID()))
					_groupMembers.remove(gp);
				if (gp.getMemberID().equals(min.getMemberID())){
					_groupMembers.remove(gp);
					gp.updateMemberBalance(maxAccountValue);
					_groupMembers.add(gp);
				}
			}
		}
		else if(minAccountValue == maxAccountValue){
			float TransactionValue = round (minAccountValue, 2);
			if (TransactionValue != 0)
				_transactions.add(new TransactionDTO(min.getMemberID(), max.getMemberID(), TransactionValue));
			//Update group members left...
			for (GroupMemberDTO gp : localGroupMembers){
				if (gp.getMemberID().equals(min.getMemberID()) || gp.getMemberID().equals(max.getMemberID()))
					_groupMembers.remove(gp);
			}
		}
		if (_groupMembers.size() >= 2)
			findMinMaxBalances();
	}

}
