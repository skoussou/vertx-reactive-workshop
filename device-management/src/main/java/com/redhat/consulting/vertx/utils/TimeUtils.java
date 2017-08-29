package com.redhat.consulting.vertx.utils;

import java.time.Instant;

import com.redhat.consulting.vertx.data.Device;

public class TimeUtils {

	/* UTILITY METHODS */
	public static long timeInMillisNow(){
		return Instant.now().toEpochMilli();
	}
	
	public static long timeInMillisSinceDeviceOn(Device device) {
		if (device.getTimeStart() > 0) {
			return timeInMillisNow() - device.getTimeStart();
		}
		return 0;
	}
}
