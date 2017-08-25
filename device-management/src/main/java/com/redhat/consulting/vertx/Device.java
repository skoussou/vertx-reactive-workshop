package com.redhat.consulting.vertx;

import java.io.Serializable;
import java.util.Calendar;

import com.redhat.consulting.vertx.MainVerticle.DEVICE_ACTION;
import com.redhat.consulting.vertx.MainVerticle.DEVICE_STATE;
import com.redhat.consulting.vertx.MainVerticle.DEVICE_TYPE;

public class Device implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String houseHoldId;
	
	private String id;

	private MainVerticle.DEVICE_TYPE type;
	
	private MainVerticle.DEVICE_ACTION action = DEVICE_ACTION.NONE;
	
	private MainVerticle.DEVICE_STATE state;

	private Integer fromNumber;

	private Integer toNumber;
	
	private Calendar timeStart;


	
	public Device(String houseHoldId, String id, DEVICE_TYPE type, DEVICE_ACTION action, DEVICE_STATE state, Integer fromNumber,
			Integer toNumber, Calendar timeStart) {
		super();
		this.houseHoldId = houseHoldId;
		this.id = id;
		this.type = type;
		this.action = action;
		this.state = state;
		this.fromNumber = fromNumber;
		this.toNumber = toNumber;
		this.timeStart = timeStart;
	}

	public Device() {
		super();
	}

	
	
	public String getHouseHoldId() {
		return houseHoldId;
	}

	public String getId() {
		return id;
	}

	public MainVerticle.DEVICE_TYPE getType() {
		return type;
	}

	public MainVerticle.DEVICE_ACTION getAction() {
		return action;
	}

	public MainVerticle.DEVICE_STATE getState() {
		return state;
	}

	public Integer getFromNumber() {
		return fromNumber;
	}

	public Integer getToNumber() {
		return toNumber;
	}

	public Calendar getTimeStart() {
		return timeStart;
	}

	@Override
	public String toString() {
		return "Device [id=" + id + ", type=" + type + ", action=" + action + ", state=" + state + ", fromNumber="
				+ fromNumber + ", toNumber=" + toNumber + ", timeStart=" + timeStart + "]";
	}


}
