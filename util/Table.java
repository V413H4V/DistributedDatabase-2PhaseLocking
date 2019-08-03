package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import bean.Lock;
import bean.Transaction;

/*
 *  @author Vaibhav Murkute
 * 	07/20/2019
 * 
 */

public class Table {

	private static HashMap<Integer, Transaction> transaction_table = new HashMap<>();
	private static HashMap<String, Lock> lock_table = new HashMap<>();
	private static HashMap<Integer, Queue<String>> waiting_operations = new HashMap<Integer, Queue<String>>();
	
	public static Transaction getTransactionInfo(int tx_id) {
		return transaction_table.get(tx_id);
	}
	
	public static void updateTransactionInfo(int tx_id, Transaction tx) {
		transaction_table.put(tx_id, tx);
	}
	
	public static Lock getLockInfo(String item_name) {
		return lock_table.get(item_name);
	}
	
	public static void updateLockInfo(String item_name, Lock lock) {
		lock_table.put(item_name, lock);
	}
	
	public static HashSet<Lock> getAllLocks(){
		HashSet<Lock> allLocks = new HashSet<>();
		for(Lock lock : lock_table.values()) {
			allLocks.add(lock);
		}
		
		return allLocks;
	}
	
	public static Queue<String> getWaitingOperations(int tx_id){
		return waiting_operations.get(tx_id);
	}
	
	public static void updateWaitingOperations(int tx_id, Queue<String> pending_ops) {
		waiting_operations.put(tx_id, pending_ops);
	}
}
