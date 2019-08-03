package bean;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Lock {
	private String item_name;
	private String lock_state = null;
	private HashSet<Integer> read_txids = new HashSet<Integer>();
	private int write_txid;
	private Queue<LockRequest> waiting_list = new LinkedList<LockRequest>();
	
	public Lock() {}

	public Lock(String item_name, String lock_state, HashSet<Integer> read_txids, int write_txid,
			Queue<LockRequest> waiting_list) {
		this.item_name = item_name;
		this.lock_state = lock_state;
		this.read_txids = read_txids;
		this.write_txid = write_txid;
		this.waiting_list = waiting_list;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getLock_state() {
		return lock_state;
	}

	public void setLock_state(String lock_state) {
		this.lock_state = lock_state;
	}

	public HashSet<Integer> getRead_txids() {
		return read_txids;
	}

	public void setRead_txids(HashSet<Integer> read_txids) {
		this.read_txids = read_txids;
	}

	public int getWrite_txid() {
		return write_txid;
	}

	public void setWrite_txid(int write_txid) {
		this.write_txid = write_txid;
	}

	public Queue<LockRequest> getWaiting_list() {
		return waiting_list;
	}

	public void setWaiting_list(Queue<LockRequest> waiting_list) {
		this.waiting_list = waiting_list;
	}

	@Override
	public boolean equals(Object obj) {
		return ((this.getClass().equals(obj.getClass())) && this.item_name == ((Lock)obj).getItem_name());
	}
	
	
	
}
