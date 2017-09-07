package com.redhat.consulting.vertx;

/**
 * Class to centralize some constatns and enum types
 * @author dsancho
 *
 */
public class Constants {

	// Addresses
	public static final String HOMEPLANS_EVENTS_ADDRESS = "homeplans";

	public static final String DEVICE_DATA_EVENTS_ADDRESS = "device-data";

	public static final String AMBIANCE_DATA_EVENTS_ADDRESS = "ambiance-data";
	
	public enum DeviceState {
		ON,
		OFF;
	}
	
	public enum DeviceAction {
		INCREASING,
		DECREASING,
		TURNOFF,
		NONE;
	}
	
	public static long MILIS_TO_REACT = 30000;
	
	public static int MIN_TEMP = 15;
	
	public static int MAX_TEMP = 35;



}
