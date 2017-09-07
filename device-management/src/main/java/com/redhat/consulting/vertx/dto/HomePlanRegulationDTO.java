package com.redhat.consulting.vertx.dto;

import java.io.Serializable;

public class HomePlanRegulationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4725214663151715087L;

	private String housePlanId;
	
	private String id;

	public HomePlanRegulationDTO() {
	}

	public HomePlanRegulationDTO(String housePlanId, String sensor) {
		super();
		this.housePlanId = housePlanId;
		this.id = sensor;
	}

	public String getHousePlanId() {
		return housePlanId;
	}

	public String getId() {
		return id;
	}
	
	
}
