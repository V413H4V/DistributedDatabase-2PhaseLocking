package util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import bean.Lock;
import bean.LockRequest;
import bean.Transaction;
import deadlock.WoundWaitProtocol;

/*
 *  @author Vaibhav Murkute
 * 	07/20/2019
 * 
 */

public class Utility {
	
	private static int time = 0;
	
	public static int getNextTimeStamp() {
		time += 1;
		return time;
	}
	
	public static void abort(int tx_id) {
		System.out.println("Aborting Transaction: "+tx_id);
		Transaction tx = Table.getTransactionInfo(tx_id);
		tx.setTx_status("aborted");
		System.out.println("Updated Transaction-table to set Status of Transaction: "+tx_id+" to \"aborted\".");
		System.out.println("Releasing all locks for Transaction: "+tx_id);
		HashSet<Lock> releasedLocks = Utility.releaseAllLocks(tx_id);
		
		for(Lock lock : releasedLocks) {
			Utility.serveWaiting(lock, lock.getItem_name());
		}
	}
	
	public static void wait(int tx_id, String item_name, String lock_type) {
		System.out.println("Blocking Transaction: "+tx_id);
		Transaction tx = Table.getTransactionInfo(tx_id);
		tx.setTx_status("blocked");
		System.out.println("Updated Transaction-table to set Status of Transaction: "+tx_id+" to \"blocked\".");
		Lock lock = Table.getLockInfo(item_name);
		lock.getWaiting_list().offer(new LockRequest(tx_id, lock_type));
		System.out.println("Added Transaction:"+tx_id+" to the waiting list for: "+lock_type+"("+item_name+").");
		
		String opcode = (lock_type.equals("read") ? "r" : "w") + tx_id + "(" +item_name+");";
		Queue<String> pending_ops = Table.getWaitingOperations(tx_id);
		if(pending_ops == null) {
			pending_ops = new LinkedList<String>();
			Table.updateWaitingOperations(tx_id, pending_ops);
			pending_ops = Table.getWaitingOperations(tx_id);
		}
		pending_ops.add(opcode);
	}
	
	public static void read_lock(int tx_id, String item_name) {
		System.out.println("Requested Read-lock on: "+item_name+", by Transaction: "+tx_id);
		Lock lock = Table.getLockInfo(item_name);
		if(lock == null) {
			// this means this is the first lock on this item.
			Table.updateLockInfo(item_name, new Lock(item_name, null, (new HashSet<Integer>()), 0, (new LinkedList<LockRequest>())));
			lock = Table.getLockInfo(item_name);
		}
		String lock_state = lock.getLock_state();
		if(lock_state == null || lock_state.equalsIgnoreCase("readlocked")) {
			System.out.println("Updating Lock-Table to acquire Read-Lock on: "+lock.getItem_name()+", for Transaction: "+tx_id);
			if(!lock.getRead_txids().contains(tx_id)) {
				lock.getRead_txids().add(tx_id);
			}
			lock.setLock_state("readlocked");
			
		}else if(lock_state.equalsIgnoreCase("writelocked")) {
			int writeLocked_txid = lock.getWrite_txid();
			System.out.println(item_name+" is already write-locked by Transaction: "+writeLocked_txid);
			System.out.println("Enforcing Wound-Wait Protocol to resolve the conflict.");
			HashSet<Integer> conflict = new HashSet<>();
			conflict.add(writeLocked_txid);
			WoundWaitProtocol.woundWait("read", item_name, tx_id, conflict, lock);
		}
	}
	
	public static void write_lock(int tx_id, String item_name) {
		System.out.println("Requested Write-lock on: "+item_name+", by Transaction: "+tx_id);
		Lock lock = Table.getLockInfo(item_name);
		if(lock == null) {
			// this means this is the first lock on this item.
			Table.updateLockInfo(item_name, new Lock(item_name, null, (new HashSet<Integer>()), 0, (new LinkedList<LockRequest>())));
			lock = Table.getLockInfo(item_name);
		}
		String lock_state = lock.getLock_state();
		boolean onlyReadingTx = false;
		if(lock_state != null)
			onlyReadingTx = (lock_state.equalsIgnoreCase("readlocked") && lock.getRead_txids().size() == 1 && lock.getRead_txids().contains(tx_id));
		
		if(lock_state == null || onlyReadingTx) {
			System.out.println("Updating Lock-Table to acquire Write-Lock on: "+lock.getItem_name()+", for Transaction: "+tx_id);
			lock.setWrite_txid(tx_id);
			lock.setLock_state("writelocked");
			if(lock.getRead_txids().contains(tx_id)) {
				lock.getRead_txids().remove(tx_id);
			}
			System.out.println("Read-Lock upgraded to Write-Lock on: "+lock.getItem_name()+", for Transaction: "+tx_id);
			
		}else {
			
			HashSet<Integer> conflict = new HashSet<Integer>();
			switch(lock_state) {
			case "readlocked":
				System.out.println(item_name+" is already read-locked by other Transactions.");
				conflict =  lock.getRead_txids();
				break;
			case "writelocked":
				System.out.println(item_name+" is already write-locked by Transaction: "+lock.getWrite_txid());
				conflict.add(lock.getWrite_txid());
				break;
			}
			
			System.out.println("Enforcing Wound-Wait Protocol to resolve the conflict.");
			WoundWaitProtocol.woundWait("write", item_name, tx_id, conflict, lock);
			
		}
	}
	
	public static void unlockItem(String item_name, int tx_id, String lock_type) {
		Lock lock = Table.getLockInfo(item_name);
		switch(lock_type) {
		case "read":
			if(lock.getRead_txids().contains(tx_id)) {
				lock.getRead_txids().remove(tx_id);
				System.out.println("Updated Lock-Table: removed Read-lock on: "+item_name+", by Transaction: "+tx_id);
			}
			break;
			
		case "write":
			lock.setWrite_txid(0);
			System.out.println("Updated Lock-Table: removed Write-lock on: "+item_name+", by Transaction: "+tx_id);
			break;
		}
		
		lock.setLock_state(null);
	}
	
	public static void serveWaiting(Lock lock, String item_name) {
		System.out.println("Fetching transaction-list waiting on item: "+item_name);
		LockRequest next_waiting = lock.getWaiting_list().poll();
		while(next_waiting != null && Table.getTransactionInfo(next_waiting.getTx_id()).getTx_status().equalsIgnoreCase("aborted"))
			next_waiting = lock.getWaiting_list().poll();
		
		if(next_waiting == null) {
			System.out.println("No transaction waiting on item: "+item_name);
			
			if(lock.getLock_state() != null && !lock.getLock_state().equalsIgnoreCase("writelocked")) {
				if(lock.getRead_txids().size() == 0)
					lock.setLock_state(null);
				else
					lock.setLock_state("readlocked");
			}
					
		}else {
			String next_lock = next_waiting.getLock_type();
			System.out.println("Transaction: "+next_waiting.getTx_id()+" is waiting on item: "+item_name + ", for "+next_lock+" lock.");
			Table.getTransactionInfo(next_waiting.getTx_id()).setTx_status("active");
			System.out.println("Status for Transaction:"+next_waiting.getTx_id()+" set to \"active\".");
			System.out.println("Running all pending operations of Transaction: "+next_waiting.getTx_id());
			
			Queue<String> pending_ops = Table.getWaitingOperations(next_waiting.getTx_id());
			String operation = "";
			while(!pending_ops.isEmpty()) {
				operation = pending_ops.poll();
				runOperation(operation);
			}
		}
	}
	
	public static HashSet<Lock> releaseAllLocks(int tx_id) {
		HashSet<Lock> releasedLocks = new HashSet<>();
		for(Lock lock : Table.getAllLocks()) {
			if(lock.getWrite_txid() == tx_id) {
				unlockItem(lock.getItem_name(), tx_id, "write");
				releasedLocks.add(lock);
			}
			if(lock.getRead_txids().contains(tx_id)) {
				unlockItem(lock.getItem_name(), tx_id, "read");
				releasedLocks.add(lock);
			}
		}
		
		return releasedLocks;
	}
	
	public static void runOperation(String opcode) {
		if(opcode == null)
			return;
		
		System.out.println("<Operation>: "+opcode);
		
		int tx_id = Integer.valueOf(opcode.charAt(1) - '0');
		Transaction tx = Table.getTransactionInfo(tx_id);
		
		if(tx == null && opcode.charAt(0) != 'b')
			return;
		
		if(opcode.charAt(0) != 'b') {
			String tx_status = tx.getTx_status();
			if(tx_status.equalsIgnoreCase("aborted")) {
				System.out.println("Transaction: "+tx_id+" has already been \"aborted\".");
				return;
			}
			if(tx_status.equalsIgnoreCase("blocked")) {
				Queue<String> pending_ops = Table.getWaitingOperations(tx_id);
				if(pending_ops == null) {
					pending_ops = new LinkedList<String>();
					Table.updateWaitingOperations(tx_id, pending_ops);
					pending_ops = Table.getWaitingOperations(tx_id);
				}
				
				pending_ops.add(opcode);
				System.out.println("Transaction: "+tx_id+" is in \"blocked\" state.");
				System.out.println("Operation: "+opcode+" added to the waiting list.");
				return;
			}
		}
		
		switch(opcode.charAt(0)) {
		case 'b':
			Operation.begin(Integer.valueOf(opcode.charAt(1) - '0'));
			break;
		case 'r':
			Operation.read(Integer.valueOf(opcode.charAt(1) - '0'), String.valueOf(opcode.charAt(3)));
			break;
		case 'w':
			Operation.write(Integer.valueOf(opcode.charAt(1)- '0'), String.valueOf(opcode.charAt(3)));
			break;
		case 'e':
			Operation.end(Integer.valueOf(opcode.charAt(1) - '0'));
			break;
		}
	}
	
}
