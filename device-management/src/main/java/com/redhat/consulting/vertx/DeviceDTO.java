package com.redhat.consulting.vertx;

import java.util.List;

public class DeviceDTO {

	private String id;
	private List<Device> devices;
	public DeviceDTO(String id, List<Device> devices) {
		super();
		this.id = id;
		this.devices = devices;
	}
	
	public DeviceDTO() {
		super();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Device> getDevices() {
		return devices;
	}
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	
}
