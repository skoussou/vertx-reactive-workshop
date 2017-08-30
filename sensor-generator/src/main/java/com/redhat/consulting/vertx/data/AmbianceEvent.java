package com.redhat.consulting.vertx.data;

/**
 * Ambiance data event
 * 
 * @author dsancho
 *
 */
public class AmbianceEvent {

	private String housePlanId;

	private SensorLocation sensorLocation;

	public AmbianceEvent() {
		super();
	}

	public AmbianceEvent(String housePlanId, SensorLocation sensorLocation) {
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

	public SensorLocation getSensorLocation() {
		return sensorLocation;
	}

	public void setSensorLocation(SensorLocation sensorLocation) {
		this.sensorLocation = sensorLocation;
	}

}
