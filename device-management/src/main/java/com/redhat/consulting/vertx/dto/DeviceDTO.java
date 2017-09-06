package com.redhat.consulting.vertx.dto;

import java.io.Serializable;

import com.redhat.consulting.vertx.Constants.DeviceAction;
import com.redhat.consulting.vertx.Constants.DeviceState;
import com.redhat.consulting.vertx.Constants.DeviceType;

public class DeviceDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String housePlanId;
	
	private String id;

	private DeviceType type;
	
	private DeviceAction action = DeviceAction.NONE;
	
	private DeviceState state = DeviceState.OFF;

	private int temperature;

	private long lastUpdate;

	public DeviceDTO(String housePlanId, String id, DeviceType type, DeviceAction action, DeviceState state, int temperature, long lastUpdate) {
		super();
		this.housePlanId = housePlanId;
		this.id = id;
		this.type = type;
		this.action = action;
		this.state = state;
		this.temperature = temperature;
		this.lastUpdate = lastUpdate;
	}
	
	public DeviceDTO() {
		super();
	}
	
	public String getHousePlanId() {
		return housePlanId;
	}

	public String getId() {
		return id;
	}

	public DeviceType getType() {
		return type;
	}

	public DeviceAction getAction() {
		return action;
	}

	public DeviceState getState() {
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

	public void setType(DeviceType type) {
		this.type = type;
	}

	public void setAction(DeviceAction action) {
		this.action = action;
	}

	public void setState(DeviceState state) {
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
		DeviceDTO other = (DeviceDTO) obj;
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
