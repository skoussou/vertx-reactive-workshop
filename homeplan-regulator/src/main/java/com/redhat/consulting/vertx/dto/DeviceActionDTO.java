package com.redhat.consulting.vertx.dto;

public class DeviceActionDTO {

	private static final long serialVersionUID = 1L;

	private String housePlanId;
	
	private String id;

//	private DEVICE_TYPE type;
//	
//	private DEVICE_ACTION action = DEVICE_ACTION.NONE;
//	
//	private DEVICE_STATE state = DEVICE_STATE.OFF;
//
//	private int fromNumber;
//
//	private int toNumber;
//	
//	//private Calendar timeStart;
//	private long timeStart;
//	
//	/** Maintains a sequence of actions tobe activated on a Device) */
//	private long actionSequence;


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
