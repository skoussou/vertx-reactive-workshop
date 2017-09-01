package com.redhat.consulting.vertx.dto;

import java.io.Serializable;

public class AmbianceDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4700553332146773530L;

	private String housePlanId;
	
	private SensorLocationDTO sensorLocation;
	
	public AmbianceDTO(){
		
	}
	
	public AmbianceDTO(String housePlanId, SensorLocationDTO sensorLocation) {
		super();
		this.housePlanId = housePlanId;
		this.sensorLocation = sensorLocation;
	}

	
	public String getHousePlanId() {
		return housePlanId;
	}

	public SensorLocationDTO getSensorLocation() {
		return sensorLocation;
	}

}
