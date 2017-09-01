package com.redhat.consulting.vertx.data;

import java.io.Serializable;

import com.redhat.consulting.vertx.data.SensorLocation;

/**
 * Handles messages on Vert.X event bus address #ambiance-data
 * @author stkousso
 *
 */
public class Ambiance implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4700553332146773530L;

	private String housePlanId;
	
	private SensorLocation sensorLocation;
	
	public Ambiance(){
		
	}
	
	public Ambiance(String housePlanId, SensorLocation sensorLocation) {
		super();
		this.housePlanId = housePlanId;
		this.sensorLocation = sensorLocation;
	}

	
	public String getHousePlanId() {
		return housePlanId;
	}

	public SensorLocation getSensorLocation() {
		return sensorLocation;
	}

	public void setHousePlanId(String housePlanId) {
		this.housePlanId = housePlanId;
	}

	public void setSensorLocation(SensorLocation sensorLocation) {
		this.sensorLocation = sensorLocation;
	}

}
