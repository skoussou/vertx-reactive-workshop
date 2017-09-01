package com.redhat.consulting.vertx.data;

import java.io.Serializable;

/**
 * Device data object
 * 
 * @author stkousso
 *
 */
public class Device implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String type;

	public Device(String id, String type) {
		super();
		this.id = id;
		this.type = type;
	}

	public Device() {
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
