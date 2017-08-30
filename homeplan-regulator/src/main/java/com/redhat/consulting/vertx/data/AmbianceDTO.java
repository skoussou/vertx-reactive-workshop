package com.redhat.consulting.vertx.data;

import java.io.Serializable;

public class AmbianceDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4700553332146773530L;

	private String housePlanId;
	
	private SensorLocation sensorLocation;
	
	public AmbianceDTO(){
		
	}
	
	public AmbianceDTO(String housePlanId, SensorLocation sensorLocation) {
		super();
		this.housePlanId = housePlanId;
		this.sensorLocation = sensorLocation;
	}

	
	public String getHousePlanId() {
		return housePlanId;
	}

	public SensorLocation getSensorLocation() {
		return sensorLocation;
	}




//	class SensorLocation implements Serializable {
//		DEVICE_TYPE type;
//		String id;
//		int temperature;
//		
//		public SensorLocation(DEVICE_TYPE type, String id, int temperature) {
//			super();
//			this.type = type;
//			this.id = id;
//			this.temperature = temperature;
//		}
//
//		public DEVICE_TYPE getType() {
//			return type;
//		}
//
//		public String getId() {
//			return id;
//		}
//
//		public int getTemperature() {
//			return temperature;
//		}
//		
//		
//		
//	}
}
