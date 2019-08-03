package deadlock;

import java.util.HashSet;
import bean.Lock;
import bean.Transaction;
import util.Table;
import util.Utility;

/*
 *  @author Vaibhav Murkute
 * 	07/20/2019
 * 
 */

public class WoundWaitProtocol {
	
	public static final String WOUNDWAIT_ABORT_STATUS = "abort";
	public static final String WOUNDWAIT_WAIT_STATUS = "wait";
	
	public static String woundWait(String lock_type, String item_name, int tx_id1, HashSet<Integer> conflict, Lock lock) {
		
		int time_tx1 = getTransactionTime(tx_id1);
		boolean blocked = false;
		
		for(int tx_id2 : conflict) {
			if(tx_id1 == tx_id2)
				continue;
			int time_tx2 = getTransactionTime(tx_id2);
			if(time_tx1 < time_tx2) {
				Utility.abort(tx_id2);		
			}else {
				blocked = true;
			}
		}
		
		if(!blocked) {
			switch(lock_type) {
			case "read":
				System.out.println("Updating Lock-Table to acquire Read-Lock on: "+lock.getItem_name()+", for Transaction: "+tx_id1);
				lock.setLock_state("readlocked");
				lock.setWrite_txid(0);
				if(!lock.getRead_txids().contains(tx_id1))
					lock.getRead_txids().add(tx_id1);
				break;
			case "write":
				System.out.println("Updating Lock-Table to acquire Write-Lock on: "+lock.getItem_name()+", for Transaction: "+tx_id1);
				lock.setLock_state("writeLocked");
				lock.setWrite_txid(tx_id1);
				if(lock.getRead_txids().contains(tx_id1))
					lock.getRead_txids().remove(tx_id1);
			}
			
			return WOUNDWAIT_ABORT_STATUS;
			
		}else {
			Utility.wait(tx_id1, item_name, lock_type);
			
			return WOUNDWAIT_WAIT_STATUS;
		}
	}
	
	private static int getTransactionTime(int tx_id) {
		Transaction tx = Table.getTransactionInfo(tx_id);
		if(tx != null)
			return tx.getTx_time();
		else
			return -1;
	}
}
