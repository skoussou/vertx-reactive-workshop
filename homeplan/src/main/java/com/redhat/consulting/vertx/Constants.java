package com.redhat.consulting.vertx;

/**
 * Class to centralize some constatns and enum types
 * @author dsancho
 *
 */
public class Constants {

	// Rest
	public static final String ROOT_PATH = "/homeplan";

	public static final String ID_PARAM = "id";

	// Share data
	public static final String HOMEPLANS_MAP = "homeplans";

	public static final String HOMEPLAN_IDS_MAP = "homeplan-ids";

	public static final String SET_ID = "index-set-id";

	// Addresses
	public static final String DEVICE_REGISTRATION_EVENTS_ADDRESS = "device-reg";
	
	public static final String HOMEPLANS_EVENTS_ADDRESS = "homeplans";
	
	public static final String DEVICE_DATA_EVENTS_ADDRESS = "device-data";
}
