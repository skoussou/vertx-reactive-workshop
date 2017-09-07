package com.redhat.consulting.vertx.dto;

/**
 * Ambiance data event
 * 
 * @author dsancho
 *
 */
public class AmbianceDTO {

	private String housePlanId;

	private SensorLocationDTO sensorLocation;

	public AmbianceDTO() {
		super();
	}

	public AmbianceDTO(String housePlanId, SensorLocationDTO sensorLocation) {
		super();
		this.housePlanId = housePlanId;
		this.sensorLocation = sensorLocation;
	}

	public String getHousePlanId() {
		return housePlanId;
	}

	public void setHousePlanId(String housePlanId) {
		this.housePlanId = housePlanId;
	}

	public SensorLocationDTO getSensorLocation() {
		return sensorLocation;
	}

	public void setSensorLocation(SensorLocationDTO sensorLocation) {
		this.sensorLocation = sensorLocation;
	}

}
