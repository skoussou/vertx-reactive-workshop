package com.redhat.consulting.vertx.data;

import java.util.HashSet;
import java.util.Set;

public class HomePlanIds {
	
	private Set<String> ids;

	public HomePlanIds() {
		super();
		ids = new HashSet<>();
	}
	
	public HomePlanIds(Set<String> ids) {
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
