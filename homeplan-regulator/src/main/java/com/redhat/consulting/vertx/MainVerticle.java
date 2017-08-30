package com.redhat.consulting.vertx;

import java.util.concurrent.ThreadLocalRandom;


import com.redhat.consulting.vertx.data.AmbianceDTO;
import com.redhat.consulting.vertx.data.Device;
import com.redhat.consulting.vertx.data.DeviceActionDTO;
import com.redhat.consulting.vertx.data.HomePlan;
import com.redhat.consulting.vertx.data.SensorLocation;
import com.redhat.consulting.vertx.workshop.types.DEVICE_ACTION;
import com.redhat.consulting.vertx.workshop.types.DEVICE_MANAGEMENT_ACTION;
import com.redhat.consulting.vertx.workshop.types.DEVICE_STATE;
import com.redhat.consulting.vertx.workshop.types.DEVICE_TYPE;
import com.redhat.consulting.vertx.workshop.utils.TimeUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;

public class MainVerticle extends AbstractVerticle {

	private static final String HOMEPLANS_EVENTS_ADDRESS = "homeplans";
	
	public static final String AMBIANBCE_DATA_ADDRESS = "ambiance-action";
	
	public static final String DEVICE_ACTION_EVENTS_ADDRESS = "device-action";
	
	public static final String DEVICE_ACTION_HEADER = "action";


	
	public int actionCounter = 0;
	
	
  @Override
  public void start() {
	  
		System.out.println("\n\n STARTING HOMEPLAN REGULATOR - MainVerticle \n");

	  
		vertx.eventBus().<String>consumer(AMBIANBCE_DATA_ADDRESS, message -> {

			System.out.println("\n\n CONSUMING message from #"+AMBIANBCE_DATA_ADDRESS);
			System.out.println("HANDLED BY Verticle.EventLoop" + this.toString());

			// Check whether we have received a payload in the incoming message
			if (message.body().isEmpty()) {
				// SEND/REPLY example
				// message.reply(json.put("message", "hello"));
			} else {
								
				// We will receive it as JSON string, transform it to its class equivalent
				AmbianceDTO ambianceData = Json.decodeValue(message.body(), AmbianceDTO.class);

				
				System.out.println("\n CONSUMED AMBIANCE-DATA \n"+Json.encodePrettily(ambianceData)+"\n\n");
							
				Future<HomePlan> futureHomeplan = getHomePlan(ambianceData.getHousePlanId());
				futureHomeplan.compose(s2 -> {
					System.out.println("Get sensor location preferences status for home plan: " + ambianceData.getHousePlanId());
					
					if (s2.getSensorLocations()!= null && !s2.getSensorLocations().isEmpty()) {
						for (SensorLocation sl : s2.getSensorLocations()) {
							
							System.out.println("Finding match between ambiance data ["+ambianceData.getHousePlanId()+"-"+ambianceData.getSensorLocation().getId()
									+"] and sensor location ["+ambianceData.getHousePlanId()+"-"+sl.getId()+"]");
							
							if (ambianceData.getSensorLocation().getId()!= null && sl.getId() != null && ambianceData.getSensorLocation().equals(sl.getId())){
								
								String msgPayload = null;
								DEVICE_MANAGEMENT_ACTION headerAction = DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE;
								
								System.out.println("MATCH-FOUND: Homeplan Regulator in action on Device: "+ambianceData.getHousePlanId()+"-"+sl.getId());
								
								System.out.println("Action Before: "+actionCounter);
								if (actionCounter == 3){
									actionCounter = 1;
								} else {
									actionCounter =+ 1;
								}
								System.out.println("Action NOW: "+actionCounter);
								

								
								switch(actionCounter) {
								case 1:
									System.out.println("Action NOW: "+DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE);
									System.out.println("Action NOW: "+DEVICE_ACTION.INCREASING);
									headerAction = DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE;
									msgPayload = createDeviceManagementActionPayload(DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE, DEVICE_ACTION.INCREASING, DEVICE_TYPE.AIRCON, 16, 23, TimeUtils.timeInMillisNow(), 1L);
									break;
								case 2:
									System.out.println("Action NOW: "+DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE);
									headerAction = DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE;
									msgPayload = createDeviceManagementActionPayload(DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE, DEVICE_ACTION.NONE, DEVICE_TYPE.AIRCON, 0, 0, TimeUtils.timeInMillisNow(), 1L);

									break;
								case 3:
									System.out.println("Action NOW: "+DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE);
									System.out.println("Action NOW: "+DEVICE_ACTION.DECREASING);
									headerAction = DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE;
									msgPayload = createDeviceManagementActionPayload(DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE, DEVICE_ACTION.DECREASING, DEVICE_TYPE.AIRCON, 25, 21, TimeUtils.timeInMillisNow(), 1L);

									break;

								}
										
								sendDeviceAction(headerAction, msgPayload);

							}

						}
					}
				}, Future.future().setHandler(handler -> {
					// Something went wrong!
					handler.cause().printStackTrace();
					System.out.println("Error getting Homeplans");
				}));
			}

		});		
					
		
  }

  private void sendDeviceAction(DEVICE_MANAGEMENT_ACTION headerAction, String msgPayload) {
	
		DeliveryOptions options = new DeliveryOptions();
		options.addHeader(DEVICE_ACTION_HEADER, headerAction.toString());
		
		vertx.eventBus().send(DEVICE_ACTION_EVENTS_ADDRESS, msgPayload, options);

  }

  
  private String createDeviceManagementActionPayload(DEVICE_MANAGEMENT_ACTION deviceManagement, DEVICE_ACTION action, DEVICE_TYPE type,
		  int from, int to, long timeInMillisNow, long c) {
	  if (action.equals(DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE)) {
		 return  Json.encodePrettily((new DeviceActionDTO("kousourisHousehold", "bedroom-1", type, action, DEVICE_STATE.ON, from, to, timeInMillisNow, timeInMillisNow)));
	  } else {
		  return  Json.encodePrettily((new DeviceActionDTO("kousourisHousehold", "bedroom-1", type, action, DEVICE_STATE.OFF, from, to, timeInMillisNow, timeInMillisNow)));
	  }

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


}
