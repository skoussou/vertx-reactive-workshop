package com.redhat.consulting.vertx.dto;

import java.io.Serializable;

public class HomePlanRegulationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4725214663151715087L;

	private String housePlanId;
	
	private String sensor;

	public HomePlanRegulationDTO() {
	}

	public HomePlanRegulationDTO(String housePlanId, String sensor) {
		super();
		this.housePlanId = housePlanId;
		this.sensor = sensor;
	}

	public String getHousePlanId() {
		return housePlanId;
	}

	public String getSensor() {
		return sensor;
	}
	
	
}
