package util;

import java.util.HashSet;
import bean.Lock;
import bean.Transaction;

/*
 *  @author Vaibhav Murkute
 * 	07/20/2019
 * 
 */

public class Operation {
	
	public static void begin(int tx_id) {
		System.out.println("Inserting record in Transaction-Table for Transaction: "+tx_id);
		int tx_time = Utility.getNextTimeStamp();
		Transaction tx = new Transaction(tx_id, tx_time, "active", (new HashSet<String>()), (new HashSet<String>()));
		Table.updateTransactionInfo(tx_id, tx);
		System.out.println("Transaction: "+tx_id+" started at time: "+tx_time);
	}
	
	public static void read(int tx_id, String item_name) {
		Utility.read_lock(tx_id, item_name);
	}
	
	public static void write(int tx_id, String item_name) {
		Utility.write_lock(tx_id, item_name);
	}
	
	public static void end(int tx_id) {
		Transaction tx = Table.getTransactionInfo(tx_id);
		tx.setTx_status("committed");
		System.out.println("Transaction: "+tx_id+" commited.");
		System.out.println("Releasing all locks for Transaction: "+tx_id);
		HashSet<Lock> releasedLocks = Utility.releaseAllLocks(tx_id);
		
		for(Lock lock : releasedLocks) {
			Utility.serveWaiting(lock, lock.getItem_name());
		}
	}
	
}
