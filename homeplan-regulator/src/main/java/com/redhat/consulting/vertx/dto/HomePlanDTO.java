package com.redhat.consulting.vertx.dto;

import java.io.Serializable;
import java.util.List;

/**
 * HomePlan data object
 *  
 * @author dsancho
 *
 */
public class HomePlanDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id; 

	
	private List<SensorLocationDTO> sensorLocations;
	
	
	public HomePlanDTO(String id, List<SensorLocationDTO> sensorLocations) {
		this.id = id;
		this.sensorLocations = sensorLocations;
	}
	
	public HomePlanDTO() {
	}
	
	public String getId() {
		return id;
	}

	public List<SensorLocationDTO> getSensorLocations() {
		return sensorLocations;
	}

	public void setSensorLocations(List<SensorLocationDTO> sensorLocations) {
		this.sensorLocations = sensorLocations;
	}

	public void setId(String id) {
		this.id = id;
	}

	
}
