package com.redhat.consulting.vertx;

import java.util.List;
import java.util.Random;

import com.redhat.consulting.vertx.Constants.DeviceAction;
import com.redhat.consulting.vertx.Constants.DeviceState;
import com.redhat.consulting.vertx.dto.AmbianceDTO;
import com.redhat.consulting.vertx.dto.DeviceStatusDTO;
import com.redhat.consulting.vertx.dto.HomePlanDTO;
import com.redhat.consulting.vertx.dto.SensorDTO;
import com.redhat.consulting.vertx.dto.SensorLocationDTO;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.kubernetes.KubernetesServiceImporter;

public class MainVerticle extends AbstractVerticle {

	// logger

	private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

	// Web client
	// not needed when using service discovery
	// private WebClient client;

	@Override
	public void start() {
		// initWebClient();
		startAmbianceDataTimer();
	}

	// private void initWebClient() {
	// client = WebClient.create(vertx);
	// }

	private void startAmbianceDataTimer() {
		vertx.setPeriodic(10000, id -> {
			Future<List<String>> futureGetIds = getHomePlanIds();
			futureGetIds.compose(s1 -> {
				if (s1 != null && !s1.isEmpty()) {
					logger.info("Getting homeplan details for {0} homeplans", s1.size());
					for (String homeplan : s1) {
						Future<HomePlanDTO> futureHomeplan = getHomePlan(homeplan);
						futureHomeplan.compose(s2 -> {
							logger.info("Getting homeplan devices status");
							if (s2.getSensorLocations() != null && !s2.getSensorLocations().isEmpty()) {
								for (SensorLocationDTO sl : s2.getSensorLocations()) {
									Future<DeviceStatusDTO> futureDeviceStatus = getDeviceStatus(s2.getId(),
											sl.getId());
									futureDeviceStatus.compose(s3 -> {
										sendAmbianceData(s3, s2);
									}, Future.future().setHandler(handler -> {
										// Something went wrong!
										logger.error("Error getting devices status", handler.cause());
									}));
								}
							}
						}, Future.future().setHandler(handler -> {
							// Something went wrong!
							logger.error("Error getting Homeplan details", handler.cause());
						}));
					}
				} else {
					logger.info("No homeplans available");
				}

			}, Future.future().setHandler(handler -> {
				// Something went wrong!
				logger.error("Error getting Homeplans", handler.cause());
			}));
		});

	}

	private void sendAmbianceData(DeviceStatusDTO deviceStatus, HomePlanDTO homePlan) {
		// identify sensor location id in homeplan matching device id
		SensorLocationDTO sl = null;
		if (homePlan.getSensorLocations() != null && !homePlan.getSensorLocations().isEmpty()) {
			for (SensorLocationDTO slinHomeplan : homePlan.getSensorLocations()) {
				if (slinHomeplan.getId().equals(deviceStatus.getId())) {
					int temperature = simulateTemperatureBehavior(deviceStatus, slinHomeplan.getTemperature());
					sl = new SensorLocationDTO(slinHomeplan.getId(), slinHomeplan.getType(), temperature);
					break;
				}
			}

		}
		if (sl != null) {
			// publish ambiance data
			AmbianceDTO ae = new AmbianceDTO(homePlan.getId(), sl);
			logger.info("Publishing in address {0} event {1}", Constants.AMBIANCE_DATA_EVENTS_ADDRESS,
					Json.encodePrettily(ae));
			vertx.eventBus().publish(Constants.AMBIANCE_DATA_EVENTS_ADDRESS, Json.encode(ae));
		} else {
			logger.warn("No matching sensor with id {0} in homeplan {1}", deviceStatus.getId(), homePlan.getId());
		}

	}

	private int simulateTemperatureBehavior(DeviceStatusDTO deviceStatus, int homeplanTemperature) {
		int newTemperature = homeplanTemperature;
		long now = System.currentTimeMillis();
		if (DeviceState.OFF.toString().equalsIgnoreCase(deviceStatus.getState())) {
			if (now - deviceStatus.getLastUpdate() > Constants.MILIS_TO_REACT) {
				newTemperature = new Random().nextInt(Constants.MAX_TEMP - Constants.MIN_TEMP + 1) + Constants.MIN_TEMP;
				logger.info("New ambiance temperature of {0}", newTemperature);
			} else {
				newTemperature = deviceStatus.getTemperature();
				logger.info("Keeping device temperature of {0}", deviceStatus.getTemperature());
			}

		} else if (DeviceState.ON.toString().equalsIgnoreCase(deviceStatus.getState())) {
			if (now - deviceStatus.getLastUpdate() > Constants.MILIS_TO_REACT) {
				if (DeviceAction.INCREASING.toString().equalsIgnoreCase(deviceStatus.getAction())) {
					newTemperature = deviceStatus.getTemperature() + 1;
				} else {
					newTemperature = deviceStatus.getTemperature() - 1;
				}
				logger.info("New ambiance temperature of {0}", newTemperature);
			} else {
				newTemperature = deviceStatus.getTemperature();
				logger.info("Keeping device temperature of {0}", deviceStatus.getTemperature());
			}
		} else {
			logger.error("Unknown device state {0}, returning homeplan temperature of {1}", deviceStatus.getState(),
					homeplanTemperature);
		}
		return newTemperature;
	}

	private Future<DeviceStatusDTO> getDeviceStatus(String homeplanId, String sensorId) {
		Future<DeviceStatusDTO> future = Future.future();
		logger.info("Sending event to address {0} to get device status for id {1}-{2}",
				Constants.DEVICE_DATA_EVENTS_ADDRESS, homeplanId, sensorId);
		vertx.eventBus().send(Constants.DEVICE_DATA_EVENTS_ADDRESS, Json.encode(new SensorDTO(homeplanId, sensorId)),
				reply -> {
					if (reply.succeeded()) {
						final DeviceStatusDTO deviceStatus = Json.decodeValue(reply.result().body().toString(),
								DeviceStatusDTO.class);
						future.complete(deviceStatus);
						logger.info("Got device status");
					} else {
						reply.cause().printStackTrace();
						future.fail("No reply from device management service");
					}
				});
		return future;
	}

	private Future<HomePlanDTO> getHomePlan(String homeplanId) {
		Future<HomePlanDTO> future = Future.future();
		logger.info("Sending event to address {0} to get homeplan details for id {1}",
				Constants.HOMEPLANS_EVENTS_ADDRESS, homeplanId);
		vertx.eventBus().send(Constants.HOMEPLANS_EVENTS_ADDRESS, homeplanId, reply -> {
			if (reply.succeeded()) {
				final HomePlanDTO homePlan = Json.decodeValue(reply.result().body().toString(), HomePlanDTO.class);
				future.complete(homePlan);
				logger.info("Homeplan returned: {0}", Json.encodePrettily(homePlan));
			} else {
				logger.error("No reply from Homeplan service", reply.cause());
				future.fail("No reply from Homeplan service");
			}
		});
		return future;
	}

	private Future<List<String>> getHomePlanIds() {
		Future<List<String>> future = Future.future();
		logger.info("Getting all homeplans ids");
		WebClient client = WebClient.create(vertx);
		HttpRequest<JsonObject> request = client.get(8080, "localhost", "/homeplan").as(BodyCodec.jsonObject());
		request.send(response -> {
			if (response.succeeded()) {
				future.complete(response.result().body().getJsonArray("ids").getList());
				logger.info("Homeplan ids returned");
			} else {
				logger.error("Could not get Homeplan ids", response.cause());
				future.fail("Could not get Homeplan ids");
			}
			// Dont' forget to release the service
		});

		// FIXME - Change above service discovery in OCP


		return future;
	}

}
