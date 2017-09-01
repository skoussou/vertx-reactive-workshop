package com.redhat.consulting.vertx.dto;

import java.util.HashSet;
import java.util.Set;

public class HomePlanIdsDTO {
	
	private Set<String> ids;

	public HomePlanIdsDTO() {
		super();
		ids = new HashSet<>();
	}
	
	public HomePlanIdsDTO(Set<String> ids) {
		super();
		this.ids = ids;
	}

	public Set<String> getIds() {
		return ids;
	}

	public void setIds(Set<String> ids) {
		this.ids = ids;
	}

}
