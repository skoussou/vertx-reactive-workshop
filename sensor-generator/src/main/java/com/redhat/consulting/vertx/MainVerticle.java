package com.redhat.consulting.vertx;

import java.util.List;

import com.redhat.consulting.vertx.data.DeviceStatus;
import com.redhat.consulting.vertx.data.HomePlan;
import com.redhat.consulting.vertx.data.Sensor;
import com.redhat.consulting.vertx.data.SensorLocation;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class MainVerticle extends AbstractVerticle {

	// Addresses
	private static final String HOMEPLANS_EVENTS_ADDRESS = "homeplans";

	public static final String DEVICE_DATA_EVENTS_ADDRESS = "device-data";

	private WebClient client;

	@Override
	public void start() {
		client = WebClient.create(vertx);
		vertx.setPeriodic(10000, id -> {
			Future<List<String>> futureGetIds = getHomePlanIds();
			futureGetIds.compose(s1 -> {
				if (s1 != null && !s1.isEmpty()) {
					System.out.println("Get homeplans details");
					for (String homeplan : s1) {
						Future<HomePlan> futureHomeplan = getHomePlan(homeplan);
						futureHomeplan.compose(s2 -> {
							System.out.println("Get home plan devices status: " + s2.getId());
							if (s2.getSensorLocations()!= null && !s2.getSensorLocations().isEmpty()) {
								for (SensorLocation sl : s2.getSensorLocations()) {
									Future<DeviceStatus> futureDeviceStatus= getDeviceStatus(s2.getId(), sl.getId());
									futureDeviceStatus.compose(s3 -> {
										System.out.println("TODO ... here we will generate ambience-data");
									}, Future.future().setHandler(handler -> {
										// Something went wrong!
										handler.cause().printStackTrace();
										System.out.println("Error getting devices status");
									}));
								}
							}
						}, Future.future().setHandler(handler -> {
							// Something went wrong!
							handler.cause().printStackTrace();
							System.out.println("Error getting Homeplans");
						}));
					}
				} else {
					System.out.println("No homeplans");
				}

			}, Future.future().setHandler(handler -> {
				// Something went wrong!
				handler.cause().printStackTrace();
				System.out.println("Error getting Homeplans");
			}));
		});
	}

	private Future<DeviceStatus> getDeviceStatus(String homeplanId, String sensorId) {
		Future<DeviceStatus> future = Future.future();
		vertx.eventBus().send(DEVICE_DATA_EVENTS_ADDRESS, Json.encode(new Sensor(homeplanId, sensorId)), reply -> {
			if (reply.succeeded()) {
				final DeviceStatus deviceStatus = Json.decodeValue(reply.result().body().toString(),
						DeviceStatus.class);
				future.complete(deviceStatus);
			} else {
				reply.cause().printStackTrace();
				future.fail("No reply from device management service");
			}
		});
		return future;
	}

	private Future<HomePlan> getHomePlan(String homeplanId) {
		Future<HomePlan> future = Future.future();
		vertx.eventBus().send(HOMEPLANS_EVENTS_ADDRESS, homeplanId, reply -> {
			if (reply.succeeded()) {
				final HomePlan homePlan = Json.decodeValue(reply.result().body().toString(), HomePlan.class);
				future.complete(homePlan);
			} else {
				reply.cause().printStackTrace();
				future.fail("No reply from Homeplan service");
			}
		});
		return future;
	}

	private Future<List<String>> getHomePlanIds() {
		Future<List<String>> future = Future.future();
		HttpRequest<JsonObject> request = client.get(8080, "localhost", "/homeplan").as(BodyCodec.jsonObject());
		request.send(ar -> {
			if (ar.succeeded()) {
				future.complete(ar.result().body().getJsonArray("ids").getList());
			} else {
				ar.cause().printStackTrace();
				future.fail("Could not get Homeplan ids");
			}
		});
		return future;
	}

	// private Future<HomePlan> getHomePlan(String homeplanId) {
	// Future<HomePlan> future = Future.future();
	// HttpRequest<JsonObject> request = client.get(8080, "localhost",
	// "/homeplan/" + homeplanId).as(BodyCodec.jsonObject());
	// request.send(ar -> {
	// if (ar.succeeded()) {
	// future.complete(ar.result().bodyAsJson(HomePlan.class));
	// } else {
	// ar.cause().printStackTrace();
	// future.fail("Could not get Homeplan " + homeplanId);
	// }
	// });
	// return future;
	// }

}
