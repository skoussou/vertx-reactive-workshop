package com.redhat.consulting.vertx.dto;

import java.io.Serializable;

/**
 * Device data object
 * 
 * @author dsancho
 *
 */
public class DeviceDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String type;

	public DeviceDTO(String id, String type) {
		super();
		this.id = id;
		this.type = type;
	}

	public DeviceDTO() {
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

	@Override
	public String toString() {
		return "Device [id=" + id + ", type=" + type + "]";
	}

}
