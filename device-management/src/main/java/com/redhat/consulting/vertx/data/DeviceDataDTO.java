package com.redhat.consulting.vertx.data;

import java.io.Serializable;

public class DeviceDataDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4279758586284541545L;

	private String housePlanId;
	
	private String sensor;

	public DeviceDataDTO(){
		
	}
	
	public DeviceDataDTO(String houdeHoldId, String sensor)   {
		super();
		this.housePlanId = houdeHoldId;
		this.sensor = sensor;
	}

	public String getHousePlanId() {
		return housePlanId;
	}

	public String getSensor() {
		return sensor;
	}

	@Override
	public String toString() {
		return "DeviceDataDTO [housePlanId=" + housePlanId + ", sensor=" + sensor + "]";
	}
	
	
}
