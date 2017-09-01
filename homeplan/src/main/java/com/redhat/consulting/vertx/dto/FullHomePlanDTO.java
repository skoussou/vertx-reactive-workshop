package com.redhat.consulting.vertx.dto;

import java.io.Serializable;
import java.util.List;

/**
 * HomePlan data object
 *  
 * @author dsancho
 *
 */
public class FullHomePlanDTO extends HomePlanDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<DeviceDTO> devices;
	
	
	public FullHomePlanDTO(String id, List<SensorLocationDTO> sensorLocations, List<DeviceDTO> devices) {
		super(id, sensorLocations);
		this.devices = devices;
	}
	
	public FullHomePlanDTO() {
		super();
	}
	
	public List<DeviceDTO> getDevices() {
		return devices;
	}

	public void setDevices(List<DeviceDTO> devices) {
		this.devices = devices;
	}

}
