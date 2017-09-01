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

	private int temperature;

//	private int toNumber;
	
	//private Calendar timeStart;
	private long lastUpdate;
	
//	/** Maintains a sequence of actions tobe activated on a Device) */
//	private long actionSequence;


	
//	public Device(String housePlanId, String id, DEVICE_TYPE type, DEVICE_ACTION action, DEVICE_STATE state, int fromNumber,
//			int toNumber, long timeStart, long actionSequence) {
//		super();
//		this.housePlanId = housePlanId;
//		this.id = id;
//		this.type = type;
//		this.action = action;
//		this.state = state;
//		this.fromNumber = fromNumber;
//		this.toNumber = toNumber;
//		this.timeStart = timeStart;
//		this.actionSequence = actionSequence;
//	}

	public Device(String housePlanId, String id, DEVICE_TYPE type, DEVICE_ACTION action, DEVICE_STATE state, int temperature, long lastUpdate) {
		super();
		this.housePlanId = housePlanId;
		this.id = id;
		this.type = type;
		this.action = action;
		this.state = state;
		this.temperature = temperature;
		this.lastUpdate = lastUpdate;
	}
	
	public Device() {
		super();
	}
	
	public String getHousePlanId() {
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

	public int getTemperature() {
		return temperature;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public void setHousePlanId(String housePlanId) {
		this.housePlanId = housePlanId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(MainVerticle.DEVICE_TYPE type) {
		this.type = type;
	}

	public void setAction(MainVerticle.DEVICE_ACTION action) {
		this.action = action;
	}

	public void setState(MainVerticle.DEVICE_STATE state) {
		this.state = state;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "Device [housePlanId=" + housePlanId + ", id=" + id + ", type=" + type + ", action=" + action
				+ ", state=" + state + ", temperature=" + temperature + ", lastUpdate=" + lastUpdate + "]";
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
