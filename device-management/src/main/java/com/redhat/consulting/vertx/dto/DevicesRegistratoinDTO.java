package com.redhat.consulting.vertx.dto;

import java.util.List;

public class DevicesRegistratoinDTO {

	private String id;
	private List<DeviceDTO> devices;
	public DevicesRegistratoinDTO(String id, List<DeviceDTO> devices) {
		super();
		this.id = id;
		this.devices = devices;
	}
	
	public DevicesRegistratoinDTO() {
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
