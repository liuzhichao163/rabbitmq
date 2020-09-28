package org.liuzhichao.entity;


//@Data
public class Mall {
	
	private String eamil;
	
	private String massage;
	
	private Integer timestamp;

	public Mall() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Mall(String eamil, String massage, Integer timestamp) {
		super();
		this.eamil = eamil;
		this.massage = massage;
		this.timestamp = timestamp;
	}



	public String getEamil() {
		return eamil;
	}

	public void setEamil(String eamil) {
		this.eamil = eamil;
	}

	public String getMassage() {
		return massage;
	}

	public void setMassage(String massage) {
		this.massage = massage;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Mall [eamil=" + eamil + ", massage=" + massage + ", timestamp=" + timestamp + "]";
	}
	
	
}
