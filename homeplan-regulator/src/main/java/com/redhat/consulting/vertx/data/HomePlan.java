package com.redhat.consulting.vertx.data;

import java.io.Serializable;
import java.util.List;

/**
 * HomePlan data object
 *  
 * @author dsancho
 *
 */
public class HomePlan extends Devices implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private List<SensorLocation> sensorLocations;
	
	
	public HomePlan(String id, List<SensorLocation> sensorLocations, List<Device> devices) {
		super(id, devices);
		this.sensorLocations = sensorLocations;
	}
	
	public HomePlan() {
		super();
	}

	public List<SensorLocation> getSensorLocations() {
		return sensorLocations;
	}

	public void setSensorLocations(List<SensorLocation> sensorLocations) {
		this.sensorLocations = sensorLocations;
	}

}
