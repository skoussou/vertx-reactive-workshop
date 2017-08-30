package com.redhat.consulting.vertx.data;

import com.redhat.consulting.vertx.MainVerticle;
import com.redhat.consulting.vertx.workshop.types.DEVICE_ACTION;
import com.redhat.consulting.vertx.workshop.types.DEVICE_STATE;
import com.redhat.consulting.vertx.workshop.types.DEVICE_TYPE;

public class DeviceActionDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String housePlanId;
	
	private String id;

	private DEVICE_TYPE type;
	
	private DEVICE_ACTION action = DEVICE_ACTION.NONE;
	
	private DEVICE_STATE state = DEVICE_STATE.OFF;

	private int fromNumber;

	private int toNumber;
	
	//private Calendar timeStart;
	private long timeStart;
	
	/** Maintains a sequence of actions tobe activated on a Device) */
	private long actionSequence;


	
	public DeviceActionDTO(String housePlanId, String id, DEVICE_TYPE type, DEVICE_ACTION action, DEVICE_STATE state, int fromNumber,
			int toNumber, long timeStart, long actionSequence) {
		super();
		this.housePlanId = housePlanId;
		this.id = id;
		this.type = type;
		this.action = action;
		this.state = state;
		this.fromNumber = fromNumber;
		this.toNumber = toNumber;
		this.timeStart = timeStart;
		this.actionSequence = actionSequence;
	}

	public DeviceActionDTO() {
		super();
	}

	
	
	public String gethousePlanId() {
		return housePlanId;
	}

	public String getId() {
		return id;
	}

	public DEVICE_TYPE getType() {
		return type;
	}

	public DEVICE_ACTION getAction() {
		return action;
	}

	public DEVICE_STATE getState() {
		return state;
	}

	public int getFromNumber() {
		return fromNumber;
	}

	public int getToNumber() {
		return toNumber;
	}

	public long getTimeStart() {
		return timeStart;
	}
	

	public long getActionSequence() {
		return actionSequence;
	}
	
}
