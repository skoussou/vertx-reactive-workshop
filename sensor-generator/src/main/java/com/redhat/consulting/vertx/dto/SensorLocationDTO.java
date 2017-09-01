package com.redhat.consulting.vertx.dto;

import java.io.Serializable;

/**
 * SensorLocation data object
 * 
 * @author dsancho
 *
 */
public class SensorLocationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String type;
	
	private int temperature;

	public SensorLocationDTO(String id, String type, int temperature) {
		super();
		this.id = id;
		this.type = type;
		this.temperature = temperature;
	}

	public SensorLocationDTO() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
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
