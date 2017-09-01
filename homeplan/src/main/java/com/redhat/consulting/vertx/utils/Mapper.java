package com.redhat.consulting.vertx.utils;

import java.util.ArrayList;
import java.util.List;

import com.redhat.consulting.vertx.data.HomePlan;
import com.redhat.consulting.vertx.data.SensorLocation;
import com.redhat.consulting.vertx.dto.HomePlanDTO;
import com.redhat.consulting.vertx.dto.SensorLocationDTO;

public class Mapper {

	public static HomePlan toHomePlan(HomePlanDTO homeplanDTO) {
		HomePlan homeplan = null;
		if (homeplanDTO != null) {
			homeplan = new HomePlan();
			homeplan.setId(homeplanDTO.getId());
			homeplan.setSensorLocations(toSensorLocations(homeplanDTO.getSensorLocations()));
		}
		return homeplan;
	}
	
	private static SensorLocation toSensorLocation(SensorLocationDTO sensorLocationDTO) {
		SensorLocation sensorLocation = null;
		if (sensorLocationDTO != null) {
			sensorLocation = new SensorLocation();
			sensorLocation.setId(sensorLocationDTO.getId());
			sensorLocation.setType(sensorLocationDTO.getType());
			sensorLocation.setTemperature(sensorLocationDTO.getTemperature());
		}
		return sensorLocation;
	}

	private static List<SensorLocation> toSensorLocations(List<SensorLocationDTO> sensorLocationsDTO) {
		List<SensorLocation> sensorLocations = null;
		if (sensorLocationsDTO != null) {
			sensorLocations = new ArrayList<>();
			if (!sensorLocationsDTO.isEmpty()) {
				for (SensorLocationDTO slDTO : sensorLocationsDTO) {
					sensorLocations.add(toSensorLocation(slDTO));
				}
			}
		}
		return sensorLocations;
	}
	
	public static HomePlanDTO toHomePlanDTO(HomePlan homeplan) {
		HomePlanDTO homeplanDTO = null;
		if (homeplan != null) {
			homeplanDTO = new HomePlanDTO();
			homeplanDTO.setId(homeplan.getId());
			homeplanDTO.setSensorLocations(toSensorLocationsDTO(homeplan.getSensorLocations()));
		}
		return homeplanDTO;
	}

	private static SensorLocationDTO toSensorLocationDTO(SensorLocation sensorLocation) {
		SensorLocationDTO sensorLocationDTO = null;
		if (sensorLocation != null) {
			sensorLocationDTO = new SensorLocationDTO();
			sensorLocationDTO.setId(sensorLocation.getId());
			sensorLocationDTO.setType(sensorLocation.getType());
			sensorLocationDTO.setTemperature(sensorLocation.getTemperature());
		}
		return sensorLocationDTO;
	}

	private static List<SensorLocationDTO> toSensorLocationsDTO(List<SensorLocation> sensorLocations) {
		List<SensorLocationDTO> sensorLocationsDTO = null;
		if (sensorLocations != null) {
			sensorLocationsDTO = new ArrayList<>();
			if (!sensorLocations.isEmpty()) {
				for (SensorLocation sl : sensorLocations) {
					sensorLocationsDTO.add(toSensorLocationDTO(sl));
				}
			}
		}
		return sensorLocationsDTO;
	}

}
