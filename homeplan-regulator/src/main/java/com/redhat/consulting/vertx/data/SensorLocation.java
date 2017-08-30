package com.redhat.consulting.vertx.data;

import java.io.Serializable;

import com.redhat.consulting.vertx.workshop.types.DEVICE_TYPE;

/**
 * SensorLocation data object
 * 
 * @author dsancho
 *
 */
public class SensorLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private DEVICE_TYPE type;
	
	private int temperature;

	public SensorLocation(String id, DEVICE_TYPE type, int temperature) {
		super();
		this.id = id;
		this.type = type;
		this.temperature = temperature;
	}

	public SensorLocation() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DEVICE_TYPE getType() {
		return type;
	}

	public void setType(DEVICE_TYPE type) {
		this.type = type;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	@Override
	public String toString() {
		return "SensorLocation [id=" + id + ", type=" + type + ", temperature=" + temperature + "]";
	}

}
