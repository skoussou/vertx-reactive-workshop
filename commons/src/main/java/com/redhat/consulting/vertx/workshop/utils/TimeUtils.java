package com.redhat.consulting.vertx.workshop.utils;

import java.time.Instant;

public class TimeUtils {

	/* UTILITY METHODS */
	public static long timeInMillisNow(){
		return Instant.now().toEpochMilli();
	}
	
	public static long timeInMillisSinceDeviceOn(long startTimeInMillis) {
		if (startTimeInMillis > 0) {
			return timeInMillisNow() - startTimeInMillis;
		}
		return 0;
	}
}
