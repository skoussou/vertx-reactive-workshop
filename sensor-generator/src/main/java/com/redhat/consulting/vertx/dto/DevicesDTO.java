package com.redhat.consulting.vertx.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Devices message used for registration process data object
 *  
 * @author dsancho
 *
 */
public class DevicesDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id; 
	
	private List<DeviceDTO> devices;
	
	public DevicesDTO(String id, List<DeviceDTO> devices) {
		super();
		this.id = id;
		this.devices = devices;
	}
	
	public DevicesDTO() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<DeviceDTO> getDevices() {
		return devices;
	}

	public void setDevices(List<DeviceDTO> devices) {
		this.devices = devices;
	}
}
