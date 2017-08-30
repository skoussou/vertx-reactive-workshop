package com.redhat.consulting.vertx;

import java.util.HashSet;
import java.util.Set;

import com.redhat.consulting.vertx.data.Devices;
import com.redhat.consulting.vertx.data.HomePlan;
import com.redhat.consulting.vertx.data.HomePlanIds;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Main verticle that will expose basic read-create-update operations vía REST
 * API
 * 
 * @author dsancho
 *
 */
public class MainVerticle extends AbstractVerticle {

	// Rest
	private static final String ROOT_PATH = "/homeplan";

	private static final String ID_PARAM = "id";

	// Share data
	private static final String HOMEPLANS_MAP = "homeplans";

	private static final String HOMEPLAN_IDS_MAP = "homeplan-ids";

	private static final String SET_ID = "index-set-id";

	// Addresses
	private static final String DEVICE_REGISTRATION_EVENTS_ADDRESS = "device-reg";
	
	private static final String HOMEPLANS_EVENTS_ADDRESS = "homeplans";
	
	// logger
	
	private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);


	@Override
	public void start() {
		startHttpServer();		
		startHomeplanEventBusProvider();
	}
	
	private void startHttpServer() {
		
		// * 1 (simple http server)

		// vertx.createHttpServer().requestHandler(req ->
		// req.response().end("hello")).listen(8080);

		// * 2 (adding router)
		
		Router router = Router.router(vertx);

		router.route(ROOT_PATH + "*").handler(BodyHandler.create());

		router.get(ROOT_PATH).handler(this::getAll);
		router.get(ROOT_PATH + "/:" + ID_PARAM).handler(this::getOne);
		router.post(ROOT_PATH + "/:" + ID_PARAM).handler(this::addOne);
		router.put(ROOT_PATH + "/:" + ID_PARAM).handler(this::addOne);

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		
		logger.info("Http router has started");
	}
	
	private void startHomeplanEventBusProvider() {
		vertx.eventBus().<String>consumer(HOMEPLANS_EVENTS_ADDRESS, message -> {
			replyWithHomeplan(message);
		});
		logger.info("Homeplans event bus ready");

	}

	// Methods used by event bus consumer
	
	private void replyWithHomeplan(Message<String> message) {
		SharedData sd = vertx.sharedData();
		Future<HomePlan> futureHomePlan = getHomePlan(sd, message.body());
		futureHomePlan.compose(s -> {
			HomePlan homePlan = futureHomePlan.result();
			if (homePlan != null) {
				message.reply(Json.encode(homePlan));
				logger.debug("Replied to message successfully");
			} else {
				logger.debug("Homeplan not found, replying failure");
				message.fail(404, "Not found");
			}
		}, Future.future().setHandler(handler -> {
			logger.error("Homeplan consumer error, replying failure", handler.cause());
			message.fail(500, "Homeplan consumer error");
		}));
	}

	// Methods used by router

	private void getAll(RoutingContext routingContext) {
		SharedData sd = vertx.sharedData();
		logger.debug("Getting all homeplan ids available");
		sd.<String, Set<String>>getClusterWideMap(HOMEPLAN_IDS_MAP, res -> {
			if (res.succeeded()) {
				res.result().get(SET_ID, rh -> {
					if (rh.succeeded() && rh.result()!=null) {
						logger.debug("Returning {} ", Json.encodePrettily(new HomePlanIds(rh.result())));
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.end(Json.encodePrettily(new HomePlanIds(rh.result())));
					} else {
						logger.debug("Returning empty response");
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.end(Json.encodePrettily(new HomePlanIds()));
					}
				});
			} else {
				logger.error("Error getting homeplan ids", res.cause());
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(new HomePlanIds()));
			}
		});
	}

	private void getOne(RoutingContext routingContext) {
		SharedData sd = vertx.sharedData();

		// using futures so we can reuse getHomePlan

		Future<HomePlan> futureHomePlan = getHomePlan(sd, routingContext.pathParam(ID_PARAM));
		futureHomePlan.compose(s -> {
			HomePlan homePlan = futureHomePlan.result();
			if (homePlan != null) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(homePlan));
			} else {
				routingContext.fail(404);
			}
		}, Future.future().setHandler(handler -> {
			// Something went wrong!
			routingContext.fail(500);
		}));

		// keep it for see differences (may be useful in workshop)

		// sd.<String, HomePlan>getClusterWideMap(HOMEPLANS_MAP, res -> {
		// if (res.succeeded()) {
		// res.result().get(routingContext.pathParam(ID_PARAM), ar -> {
		// if (ar.succeeded()) {
		// HomePlan homePlan = ar.result();
		// if (homePlan != null) {
		// routingContext.response().putHeader("content-type",
		// "application/json; charset=utf-8")
		// .end(Json.encodePrettily(homePlan));
		// } else {
		// routingContext.fail(404);
		// }
		//
		// } else {
		// // Something went wrong!
		// routingContext.fail(500);
		// }
		// });
		// ;
		// } else {
		// // Something went wrong!
		// routingContext.fail(500);
		// }
		// });
	}

	private void addOne(RoutingContext routingContext) {
		final HomePlan homePlan = Json.decodeValue(routingContext.getBodyAsString(), HomePlan.class);

		SharedData sd = vertx.sharedData();

		// add homeplan
		Future<String> futureHomePlan = addHomePlan(sd, routingContext.pathParam(ID_PARAM), homePlan);

		futureHomePlan.compose(s1 -> {
			// add id to indexes set
			Future<String> futureId = addIdToIndex(sd, routingContext.pathParam(ID_PARAM));

			futureId.compose(s2 -> {
				// sending device registration
				Future<String> futureDevReg = sendDevicesRegistration(homePlan);

				futureDevReg.compose(s3 -> {

					routingContext.response().setStatusCode(201)
							.putHeader("content-type", "application/json; charset=utf-8")
							.end(Json.encodePrettily(homePlan));

				}, Future.future().setHandler(handler -> {
					// Something went wrong!
					routingContext.fail(500);
				}));
			}, Future.future().setHandler(handler -> {
				// Something went wrong!
				routingContext.fail(500);
			}));
		}, Future.future().setHandler(handler -> {
			// Something went wrong!
			routingContext.fail(500);
		}));

	}

	// Methods returning futures and used by above operations

	private Future<HomePlan> getHomePlan(SharedData sd, String id) {
		Future<HomePlan> future = Future.future();
		logger.debug("Getting homeplan details for id {}", id);
		sd.<String, HomePlan>getClusterWideMap(HOMEPLANS_MAP, res -> {
			if (res.succeeded()) {
				res.result().get(id, ar -> {
					if (ar.succeeded()) {
						future.complete(ar.result());
						logger.debug("Homeplan returned");
					} else {
						// Something went wrong!
						logger.error("Error getting Homeplan", res.cause());
						future.fail(ar.cause());
					}
				});
			} else {
				// Something went wrong!
				logger.error("Error getting Homeplan", res.cause());
				future.fail(res.cause());
			}
		});
		return future;
	}

	// FIXME Concurrency... what if were adding several homeplans at the same time? Lock should be done
	private Future<String> addHomePlan(SharedData sd, String id, HomePlan homePlan) {
		Future<String> future = Future.future();
		logger.debug("Adding homeplan {}", Json.encodePrettily(homePlan));
		sd.<String, HomePlan>getClusterWideMap(HOMEPLANS_MAP, res -> {
			if (res.succeeded()) {
				res.result().put(id, homePlan, ar -> {
					if (ar.succeeded()) {
						future.complete("HomePlan added successfully");
						logger.debug("HomePlan added successfully");
					} else {
						// Something went wrong!
						logger.error("Error adding homeplan", ar.cause());
						future.fail(ar.cause());
					}
				});
			} else {
				// Something went wrong!
				logger.error("Error adding homeplan", res.cause());
				future.fail(res.cause());
			}
		});
		return future;
	}

	// FIXME Concurrency... what if were adding several ids at the same time? Lock should be done
	private Future<String> addIdToIndex(SharedData sd, String id) {
		Future<String> future = Future.future();
		logger.debug("Adding homeplan id {} to index table", id);
		sd.<String, Set<String>>getClusterWideMap(HOMEPLAN_IDS_MAP, res -> {
			if (res.succeeded()) {
				res.result().get(SET_ID, rh -> {
					if (rh.succeeded()) {
						Set<String> indexes = rh.result();
						if (indexes == null) {
							// init set
							indexes = new HashSet<>();
						}
						indexes.add(id);
						res.result().put(SET_ID, indexes, rhids -> {
							if (rhids.succeeded()) {
								future.complete("Id added successfully");
								logger.debug("Id added successfully");
							} else {
								// Something went wrong!
								future.fail(rhids.cause());
								logger.error("Error adding homeplan id", rhids.cause());
							}
						});
					} else {
						// Something went wrong!
						logger.error("Error adding homeplan id", rh.cause());
						future.fail(rh.cause());
					}
				});
				;
			} else {
				// Something went wrong!
				logger.error("Error adding homeplan id", res.cause());
				future.fail(res.cause());
			}
		});
		return future;
	}

	private Future<String> sendDevicesRegistration(HomePlan homePlan) {
		Future<String> future = Future.future();
		// create devices message, that is, homeplan without sensors
		Devices message = new Devices(homePlan.getId(), homePlan.getDevices());
		logger.debug("Sending event to address {} to register devices", DEVICE_REGISTRATION_EVENTS_ADDRESS);
		vertx.eventBus().send(DEVICE_REGISTRATION_EVENTS_ADDRESS, Json.encodePrettily(message), reply -> {
			if (reply.succeeded()) {
				future.complete("Received reply");
				logger.debug("Devices registered");
			} else {
				future.fail("No reply from Device management service");
				//future.complete("No reply.. but mocking OK");
				logger.error("No reply from Device management service", reply.cause());
			}
		});
		return future;
	}
}
