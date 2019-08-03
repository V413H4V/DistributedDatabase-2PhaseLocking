package bean;

import java.util.HashSet;

public class Transaction {
	private int tx_id;
	private int tx_time;
	private String tx_status;
	private HashSet<String> readlocked_items;
	private HashSet<String> writelocked_items;
	
	public Transaction() {}

	public Transaction(int tx_id, int tx_time, String tx_status, HashSet<String> readlocked_items,
			HashSet<String> writelocked_items) {
		this.tx_id = tx_id;
		this.tx_time = tx_time;
		this.tx_status = tx_status;
		this.readlocked_items = readlocked_items;
		this.writelocked_items = writelocked_items;
	}

	public int getTx_id() {
		return tx_id;
	}

	public void setTx_id(int tx_id) {
		this.tx_id = tx_id;
	}

	public int getTx_time() {
		return tx_time;
	}

	public void setTx_time(int tx_time) {
		this.tx_time = tx_time;
	}

	public String getTx_status() {
		return tx_status;
	}

	public void setTx_status(String tx_status) {
		this.tx_status = tx_status;
	}

	public HashSet<String> getReadlocked_items() {
		return readlocked_items;
	}

	public void setReadlocked_items(HashSet<String> readlocked_items) {
		this.readlocked_items = readlocked_items;
	}

	public HashSet<String> getWritelocked_items() {
		return writelocked_items;
	}

	public void setWritelocked_items(HashSet<String> writelocked_items) {
		this.writelocked_items = writelocked_items;
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((this.getClass().equals(obj.getClass())) && this.getTx_id() == ((Transaction)obj).getTx_id());
	}
	
}
