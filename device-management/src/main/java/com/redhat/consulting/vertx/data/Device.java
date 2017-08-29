package com.redhat.consulting.vertx.data;

import java.io.Serializable;
import java.util.Calendar;

import com.redhat.consulting.vertx.MainVerticle;
import com.redhat.consulting.vertx.MainVerticle.DEVICE_ACTION;
import com.redhat.consulting.vertx.MainVerticle.DEVICE_STATE;
import com.redhat.consulting.vertx.MainVerticle.DEVICE_TYPE;

public class Device implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String housePlanId;
	
	private String id;

	private MainVerticle.DEVICE_TYPE type;
	
	private MainVerticle.DEVICE_ACTION action = DEVICE_ACTION.NONE;
	
	private MainVerticle.DEVICE_STATE state = DEVICE_STATE.OFF;

	private Integer fromNumber;

	private Integer toNumber;
	
	//private Calendar timeStart;
	private long timeStart;
	
	/** Maintains a sequence of actions tobe activated on a Device) */
	private long actionSequence;


	
	public Device(String housePlanId, String id, DEVICE_TYPE type, DEVICE_ACTION action, DEVICE_STATE state, Integer fromNumber,
			Integer toNumber, long timeStart, long actionSequence) {
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

	public Device() {
		super();
	}

	
	
	public String gethousePlanId() {
		return housePlanId;
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

	public long getTimeStart() {
		return timeStart;
	}
	
	

	public long getActionSequence() {
		return actionSequence;
	}

	@Override
	public String toString() {
		return "Device [housePlanId=" + housePlanId + ", id=" + id + ", type=" + type + ", action=" + action
				+ ", state=" + state + ", fromNumber=" + fromNumber + ", toNumber=" + toNumber + ", timeStart="
				+ timeStart + ", actionSequence=" + actionSequence + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((housePlanId == null) ? 0 : housePlanId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Device other = (Device) obj;
		if (housePlanId == null) {
			if (other.housePlanId != null)
				return false;
		} else if (!housePlanId.equals(other.housePlanId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}




}
