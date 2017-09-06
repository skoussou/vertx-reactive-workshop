package com.redhat.consulting.vertx.dto;

/**
 * Device status data object
 * 
 * @author dsancho
 *
 */
public class DeviceStatusDTO extends DeviceDTO {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String housePlanId;

	// FIXME align as enum
	private String type;

	// FIXME align as enum
	private String action;

	// FIXME align as enum
	private String state;

	private int temperature;

	private long lastUpdate;

	public DeviceStatusDTO() {
		super();
	}

	public DeviceStatusDTO(String id, String type, String housePlanId, String type2, String action, String state, int temperature, long lastUpdate) {
		super(id, type);
		this.housePlanId = housePlanId;
		type = type2;
		this.action = action;
		this.state = state;
		this.temperature = temperature;
		this.lastUpdate = lastUpdate;
	}

	public String getHousePlanId() {
		return housePlanId;
	}

	public void setHousePlanId(String housePlanId) {
		this.housePlanId = housePlanId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
