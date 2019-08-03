package bean;

public class LockRequest {
	
	private int tx_id;
	private String lock_type;
	
	public LockRequest(int tx_id, String lock_type) {
		this.tx_id = tx_id;
		this.lock_type = lock_type;
	}
	
	public int getTx_id() {
		return tx_id;
	}
	public void setTx_id(int tx_id) {
		this.tx_id = tx_id;
	}
	public String getLock_type() {
		return lock_type;
	}
	public void setLock_type(String lock_type) {
		this.lock_type = lock_type;
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((this.getClass().equals(obj.getClass())) && this.getTx_id() == ((LockRequest)obj).getTx_id() && 
				(this.getLock_type().equals(((LockRequest)obj).getLock_type())));
	}
	
}
