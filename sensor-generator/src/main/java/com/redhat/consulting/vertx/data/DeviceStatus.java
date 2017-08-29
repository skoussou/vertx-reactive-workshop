package com.redhat.consulting.vertx.data;

/**
 * Device status data object
 * 
 * @author dsancho
 *
 */
public class DeviceStatus extends Device {

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

	private int fromNumber;

	private int toNumber;

	// FIXME align type with Stelios
	private long timeStart;

	// FIXME align type with Stelios
	private long actionSequence;

	public DeviceStatus() {
		super();
	}

	public DeviceStatus(String id, String type, String housePlanId, String type2, String action, String state,
			int fromNumber, int toNumber, long timeStart, long actionSequence) {
		super(id, type);
		this.housePlanId = housePlanId;
		type = type2;
		this.action = action;
		this.state = state;
		this.fromNumber = fromNumber;
		this.toNumber = toNumber;
		this.timeStart = timeStart;
		this.actionSequence = actionSequence;
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

	public int getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(int fromNumber) {
		this.fromNumber = fromNumber;
	}

	public int getToNumber() {
		return toNumber;
	}

	public void setToNumber(int toNumber) {
		this.toNumber = toNumber;
	}

	public long getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(long timeStart) {
		this.timeStart = timeStart;
	}

	public long getActionSequence() {
		return actionSequence;
	}

	public void setActionSequence(long actionSequence) {
		this.actionSequence = actionSequence;
	}
	
}
