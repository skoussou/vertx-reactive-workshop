package com.redhat.consulting.vertx.data;

import java.io.Serializable;

public class DeviceDataDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4279758586284541545L;

	String id;
	
	String sensorId;

	public DeviceDataDTO(){
		
	}
	
	public DeviceDataDTO(String houdeHoldId, String sensor)   {
		super();
		this.id = houdeHoldId;
		this.sensorId = sensor;
	}

	public String getId() {
		return id;
	}

	public String getSensorId() {
		return sensorId;
	}

	@Override
	public String toString() {
		return "DeviceDataDTO [id=" + id + ", sensorId=" + sensorId + "]";
	}
	
	
}
