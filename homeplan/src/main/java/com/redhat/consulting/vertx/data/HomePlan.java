package com.redhat.consulting.vertx.data;

import java.io.Serializable;
import java.util.List;

/**
 * HomePlan data object
 *  
 * @author dsancho
 *
 */
public class HomePlan implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private List<SensorLocation> sensorLocations;
	
	private List<Device> devices;
	
	public HomePlan(String id, List<SensorLocation> sensorLocations, List<Device> devices) {
		super();
		this.id = id;
		this.sensorLocations = sensorLocations;
		this.devices = devices;
	}
	
	public HomePlan() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<SensorLocation> getSensorLocations() {
		return sensorLocations;
	}

	public void setSensorLocations(List<SensorLocation> sensorLocations) {
		this.sensorLocations = sensorLocations;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
}
