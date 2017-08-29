package com.redhat.consulting.vertx.data;

/**
 * Sensor data object
 * 
 * @author dsancho
 *
 */
public class Sensor {
	
	private String housePlanId;
	
	private String sensor;

	public Sensor() {
		super();
	}
	
	public Sensor(String housePlanId, String sensor) {
		super();
		this.housePlanId = housePlanId;
		this.sensor = sensor;
	}

	public String getHousePlanId() {
		return housePlanId;
	}

	public void setHousePlanId(String housePlanId) {
		this.housePlanId = housePlanId;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

}
