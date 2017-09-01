package com.redhat.consulting.vertx.dto;

public class DeviceActionDTO {

	private static final long serialVersionUID = 1L;

	private String housePlanId;
	
	private String id;

	public DeviceActionDTO() {
		super();
	}

	public DeviceActionDTO(String housePlanId, String id) {
		super();
		this.housePlanId = housePlanId;
		this.id = id;
	}

	public String gethousePlanId() {
		return housePlanId;
	}

	public String getId() {
		return id;
	}
	
}
