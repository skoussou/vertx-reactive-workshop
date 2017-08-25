package com.redhat.consulting.vertx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.redhat.consulting.vertx.data.DevicesMessage;
import com.redhat.consulting.vertx.data.HomePlan;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
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

	@Override
	public void start() {

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
	}

	// Methods used by router

	private void getAll(RoutingContext routingContext) {
		SharedData sd = vertx.sharedData();

		sd.<String, Set<String>>getClusterWideMap(HOMEPLAN_IDS_MAP, res -> {
			if (res.succeeded()) {
				res.result().get(SET_ID, rh -> {
					if (rh.succeeded()) {
						Set<String> indexes = rh.result();
						// create final list
						List<HomePlan> homePlans = new ArrayList<HomePlan>();
						System.out.println("Indexes size: " + indexes.size());
						if (indexes != null && !indexes.isEmpty()) {
							sd.<String, HomePlan>getClusterWideMap(HOMEPLANS_MAP, arEntries -> {
								System.out.println("getClusterWideMap " + HOMEPLANS_MAP);

								if (arEntries.succeeded()) {
									System.out.println("Iterate indexes");

									// get all Home plans
									for (String id : indexes) {
										System.out.println("Id " + id);

										arEntries.result().get(id, arHP -> {

											if (arHP.succeeded()) {
												HomePlan homePlan = arHP.result();
												System.out.println("HomePlan obtained " + homePlan);

												if (homePlan != null) {
													homePlans.add(homePlan);
													System.out.println("HomePlan added " + homePlan);
												}

											} else {
												// Something went wrong!
												routingContext.fail(500);
											}
										});
									}
								} else {
									// Something went wrong!
									routingContext.fail(500);
								}
								System.out.println("Returning homeplans: " + homePlans.size());

								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.end(Json.encodePrettily(homePlans));

							});
						}
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.end(Json.encodePrettily(homePlans));
					}
				});
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

//		sd.<String, HomePlan>getClusterWideMap(HOMEPLANS_MAP, res -> {
//			if (res.succeeded()) {
//				res.result().get(routingContext.pathParam(ID_PARAM), ar -> {
//					if (ar.succeeded()) {
//						HomePlan homePlan = ar.result();
//						if (homePlan != null) {
//							routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
//									.end(Json.encodePrettily(homePlan));
//						} else {
//							routingContext.fail(404);
//						}
//
//					} else {
//						// Something went wrong!
//						routingContext.fail(500);
//					}
//				});
//				;
//			} else {
//				// Something went wrong!
//				routingContext.fail(500);
//			}
//		});
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
		sd.<String, HomePlan>getClusterWideMap(HOMEPLANS_MAP, res -> {
			if (res.succeeded()) {
				res.result().get(id, ar -> {
					if (ar.succeeded()) {
						future.complete(ar.result());
					} else {
						// Something went wrong!
						future.fail(ar.cause());
					}
				});
			} else {
				// Something went wrong!
				future.fail(res.cause());
			}
		});
		return future;
	}

	private Future<String> addHomePlan(SharedData sd, String id, HomePlan homePlan) {
		Future<String> future = Future.future();
		sd.<String, HomePlan>getClusterWideMap(HOMEPLANS_MAP, res -> {
			if (res.succeeded()) {
				res.result().put(id, homePlan, ar -> {
					if (ar.succeeded()) {
						future.complete("HomePlan added successfully");
					} else {
						// Something went wrong!
						future.fail(ar.cause());
					}
				});
			} else {
				// Something went wrong!
				future.fail(res.cause());
			}
		});
		return future;
	}

	private Future<String> addIdToIndex(SharedData sd, String id) {
		Future<String> future = Future.future();
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
							} else {
								// Something went wrong!
								future.fail(rhids.cause());
							}
						});
					} else {
						// Something went wrong!
						future.fail(rh.cause());
					}
				});
				;
			} else {
				// Something went wrong!
				future.fail(res.cause());
			}
		});
		return future;
	}

	private Future<String> sendDevicesRegistration(HomePlan homePlan) {
		Future<String> future = Future.future();
		// create devices message, that is, homeplan without sensors
		DevicesMessage message = new DevicesMessage(homePlan.getId(), homePlan.getDevices());
		// FIXME I defined reply.. will see if we add it on the other service
		// side
		vertx.eventBus().send(DEVICE_REGISTRATION_EVENTS_ADDRESS, Json.encodePrettily(message), reply -> {
			if (reply.succeeded()) {
				future.complete("Received reply");
			} else {
				// FIXME when device management is ready, change lines
				// future.fail("No reply from receiver");
				future.complete("No reply.. but mocking OK");
			}
		});
		return future;
	}
}
