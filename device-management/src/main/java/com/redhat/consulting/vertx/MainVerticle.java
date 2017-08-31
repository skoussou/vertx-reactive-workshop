package com.redhat.consulting.vertx;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;



import com.redhat.consulting.vertx.MainVerticle.DEVICE_ACTION;
import com.redhat.consulting.vertx.MainVerticle.DEVICE_STATE;
import com.redhat.consulting.vertx.MainVerticle.DEVICE_TYPE;
import com.redhat.consulting.vertx.data.Device;
import com.redhat.consulting.vertx.data.DeviceDTO;
import com.redhat.consulting.vertx.data.DeviceDataDTO;
import com.redhat.consulting.vertx.utils.TimeUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.SharedData;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
//import io.vertx.rxjava.core.shareddata.Counter;
//import io.vertx.rxjava.ext.web.RoutingContext;


/**
 * 
 * @author stkousso
 * 
 * Handles
 * 
 * Device Registration
 * Receives message from {@code MainVerticle.DEVICE_REGISTRATION_EVENTS_ADDRESS} 
 * Message expected format
 * {
    id : KoussourisHouseHold, 
    "devices" : [
         { "type" : air-con, id : "kitchen-1},
         { "type" : shutters, id : "kitchen-1"},
         { "type" : air-con, id : "bedroom-1"},
         { "type" : shutters, id : "bedroom-1"},
         { "type" : air-con, id : "bedroom-2",}
         { "type" : shutters, id : "bedroom-2"},
         { "type" : air-con, id : "living-room-1"},
         { "type" : shutters, id : "living-room-1"}
     ]
}


 * Device ACTION
 * Receives message from {@code MainVerticle.DEVICE_ACTION_EVENTS_ADDRESS} 
 * Utilizes Header {@code MainVerticle.DEVICE_ACTION_HEADER} with possible values {@code MainVerticle.DEVICE_MANAGEMENT_ACTION} 
 * 		
 * Message expected format
 * {
     "housePlanId" : kousourisHousehold,
     "id" : "kitchen-1",
     "type" : "AIRCON",
     "action" : INCREASING,
     "state" : ON,
     "fromNumber" : 17,
     "toNumber" : 22,
     "timeStart" : null
   }
   
   and for deactivate the header "TURNOFF_DEVICE"
   {
     "housePlanId" : kousourisHousehold,
     "id" : "kitchen-1",
     "type" : "AIRCON",
     "action" : NONE,
     "state" : OFF,
     "fromNumber" : 0,
     "toNumber" : 0,
     "timeStart" : null
   }
   
   
    * Device DATA
 * Receives message from {@code MainVerticle.DEVICE_DATA_EVENTS_ADDRESS} 
* 		
 * Message expected format
 * { id : "koussourisHousehold", sensor : "bedroom-1"} 
  

 *
 */
public class MainVerticle extends AbstractVerticle {


	public static final String DEVICE_ACTION_EVENTS_ADDRESS = "device-action";
	public static final String DEVICE_REGISTRATION_EVENTS_ADDRESS = "device-reg";
	public static final String DEVICE_DATA_EVENTS_ADDRESS = "device-data";

	public static final String DEVICE_ACTION_INCREASE_TEMPERATURE = "increase-temp";
	public static final String DEVICE_ACTION_DECREASE_TEMPERATURE = "decrease-temp";
	public static final String DEVICE_ACTION_DEACTIVATE = "increase-temp";
	
	public static final String DEVICE_ACTION_HEADER = "action";

	
	public static final String DEVICES_MAP = "DEVICES_MAP";
    public static final String DEVICES_ID_SEPARATOR = "-";
	

	public enum DEVICE_TYPE {
		AIRCON;
	}
	
	public enum DEVICE_STATE {
		ON,
		OFF;
	}
	
	public enum DEVICE_ACTION {
		INCREASING,
		DECREASING,
		NONE;
	}
	
	public enum DEVICE_MANAGEMENT_ACTION {
		ACTIVATE_DEVICE,
//		DECREASE_DEVICE_VALUE,
//		INCREASE_DEVICE_VALUE,
		TURNOFF_DEVICE;
	}
	
	/* Errors Generated by unpredictable Vert.X behavior */
	public enum ERROR_CODES {
		DEVICE_MANAGEMENT_GET_DEVICE_ERROR(500),
		DEVICE_MANAGEMENT_GET_DEVICES_MAP_ERROR(501),
		DEVICE_MANAGEMENT_ACTIONS_BAD_ACTION (502);
		
		ERROR_CODES(int errorCode){
			this.errorCode = errorCode;
		}
		
		private int errorCode;
		
		public int getErrorCode(){
			return errorCode;
		}
	}
	
	/* Errors Generated by application erroneous behavior */
	public enum APP_ERROR_CODES {
		NON_REGISTERED_DEVICE(400, "[ERROR]"),
		MESSAGE_OUT_OF_SYNC(401, "[WARN]");
		
		APP_ERROR_CODES(int errorCode, String type){
			this.errorCode = errorCode;
			this.type = type;
		}
		
		private int errorCode;
		private String type;
		
		public int getErrorCode(){
			return errorCode;
		}
		
		public String getType(){
			return type;
		}
	}
	
	
	
	
	@Override
	public void start() {
		System.out.println("\n\n STARTING DEVICE Management - MainVerticle \n");
	
		registerDevices(DEVICE_REGISTRATION_EVENTS_ADDRESS, vertx.eventBus());
		
		deviceAction(DEVICE_ACTION_EVENTS_ADDRESS, vertx.eventBus());
		
		readDevice(DEVICE_DATA_EVENTS_ADDRESS, vertx.eventBus());
		
	}
	

	private void readDevice(String deviceDataEventsAddress, EventBus eventBus) {
		vertx.eventBus().<String>consumer(DEVICE_DATA_EVENTS_ADDRESS, message -> {

			System.out.println("\n\n CONSUMING message from #"+DEVICE_DATA_EVENTS_ADDRESS);
			System.out.println("HANDLED BY Verticle.EventLoop" + this.toString());

			// Check whether we have received a payload in the incoming message
			if (message.body().isEmpty()) {
				// SEND/REPLY example
				// message.reply(json.put("message", "hello"));
			} else {
								
				// We will receive it as JSON string, transform it to its class equivalent
				DeviceDataDTO deviceDataRequested = Json.decodeValue(message.body(), DeviceDataDTO.class);
				
				System.out.println(deviceDataRequested);
							
				getDevice(generatedDeviceKey(deviceDataRequested.getHousePlanId(), deviceDataRequested.getSensor()), message);			}
		});		
		
        //ONLY RE-ACTIVATE FOR TESTING - HACKING
//		System.out.println("\n\n SENDING MESSAGE to #" + MainVerticle.DEVICE_DATA_EVENTS_ADDRESS);
//		
//		vertx.eventBus().send(MainVerticle.DEVICE_REGISTRATION_EVENTS_ADDRESS, createRegistrationPayload());
//		
//		
//		DeliveryOptions options = new DeliveryOptions();
//
//	    
//	    /* *********  Test withaction header - for increase */
//		options.addHeader(DEVICE_ACTION_HEADER, DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE.toString());
//	    vertx.eventBus().send(MainVerticle.DEVICE_ACTION_EVENTS_ADDRESS, 
//	    		              createDeviceDummyActionPayload(DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE, DEVICE_ACTION.INCREASING, 17, 23),
//	    		              options);
//		
//		DeviceDataDTO deviceDetails = new DeviceDataDTO("kousourisHousehold", "bedroom-1");
//		
//		vertx.eventBus().send(MainVerticle.DEVICE_DATA_EVENTS_ADDRESS, Json.encodePrettily(deviceDetails));
	}


	// **********************************************************************************
	// HANDLE DEVICE REGISTRATIONS 
	// Receive message from the address 'DEVICE_REGISTRATION_EVENTS_ADDRESS'
	// *********************************************************************************
	private void registerDevices(String deviceDataEventsAddress, EventBus eventBus) {
		
		vertx.eventBus().<String>consumer(DEVICE_REGISTRATION_EVENTS_ADDRESS, message -> {

			System.out.println("\n\n CONSUMING message from #"+DEVICE_REGISTRATION_EVENTS_ADDRESS);
			System.out.println("HANDLED BY Verticle.EventLoop" + this.toString());

			// Check whether we have received a payload in the incoming message
			if (message.body().isEmpty()) {
				// SEND/REPLY example
				// message.reply(json.put("message", "hello"));
			} else {
								
				// We will receive it as JSON string, transform it to its class equivalent
				DeviceDTO devicesToRegister = Json.decodeValue(message.body(), DeviceDTO.class);
						
				// TODO - NEED TO CHANGE THIS TO PROCESS THE MESSAGE AS A STREAM
				String housePlanId = devicesToRegister.getId();
					List<Device> devices = devicesToRegister.getDevices();
					for (Device device : devices) {
						registerDevice(generatedDeviceKey(devicesToRegister.getId(), device.getId()), 
								new Device(housePlanId, device.getId(), device.getType(), device.getAction(), 
										device.getState(), device.getFromNumber(), device.getToNumber(), 
										device.getTimeStart(), device.getActionSequence()));
					}
					
					// ONLY RE-ACTIVATE FOR TESTING - HACKING
//					getDevice(generatedDeviceKey("kousourisHousehold","kitchen-1"), message);
//					getDevice(generatedDeviceKey("kousourisHousehold", "bedroom-1"), message);
			}
		});		
		
        //ONLY RE-ACTIVATE FOR TESTING - HACKING
//		System.out.println("\n\n SENDING MESSAGE to #" + MainVerticle.DEVICE_REGISTRATION_EVENTS_ADDRESS);   
//		vertx.eventBus().send(MainVerticle.DEVICE_REGISTRATION_EVENTS_ADDRESS, createRegistrationPayload());
	}


	// **********************************************************************************
	// HANDLE DEVICE ACTIONS 
	// Receive message from the address 'DEVICE_ACTION_EVENTS_ADDRESS'
	// ACTIONS increase/decrease/deactivate
	// separate ACTIONS based on the 'header' of the message
	// *********************************************************************************
	private void deviceAction(String deviceActionEventsAddress, EventBus eventBus) {

		vertx.eventBus().<String>consumer(DEVICE_ACTION_EVENTS_ADDRESS, message -> {
			System.out.println("\n\n CONSUMING message from #"+DEVICE_ACTION_EVENTS_ADDRESS);
			System.out.println("HANDLED BY Verticle.EventLoop" + this.toString());

			// Check whether we have received a payload in the incoming message
			if (message.body().isEmpty()) {
				// SEND/REPLY example
				// message.reply(json.put("message", "hello"));
			} else {
				DEVICE_MANAGEMENT_ACTION action = DEVICE_MANAGEMENT_ACTION.valueOf(message.headers().get(DEVICE_ACTION_HEADER));
				Device deviceActionable = Json.decodeValue(message.body(), Device.class);
				
				
				System.out.println("<---------------------------------------------------->");
				System.out.println(Json.encodePrettily(deviceActionable));
				System.out.println("<---------------------------------------------------->");
				  switch (action) {
				    case ACTIVATE_DEVICE:
				    	System.out.println("ACTION: "+ DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE +" & START "+deviceActionable.getAction()+" on DEVICE: "+ generatedDeviceKey(deviceActionable));
				    	updateDevice(deviceActionable);
				    	break;
				    case TURNOFF_DEVICE:
				    	System.out.println("ACTION: "+ DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE +" on DEVICE: "+ generatedDeviceKey(deviceActionable));
				    	turnoffDevice(deviceActionable);
				      break;
				    default:
				      message.fail(ERROR_CODES.DEVICE_MANAGEMENT_ACTIONS_BAD_ACTION.getErrorCode(), "Bad action: " + action);
				  }
				
			}
		});
		
        // ONLY RE-ACTIVATE FOR TESTING - HACKING
//	    System.out.println("\n\n SENDING MESSAGE to #" + MainVerticle.DEVICE_ACTION_EVENTS_ADDRESS);   
//
//		DeliveryOptions options = new DeliveryOptions();
//
//	    
//	    /* *********  Test withaction header - for increase */
//		options.addHeader(DEVICE_ACTION_HEADER, DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE.toString());
//	    vertx.eventBus().send(MainVerticle.DEVICE_ACTION_EVENTS_ADDRESS, 
//	    		              createDeviceDummyActionPayload(DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE, DEVICE_ACTION.INCREASING, 17, 23),
//	    		              options);
//	    
//	    /* *********  Test withaction header - for TURN OFF device */
//		options = new DeliveryOptions();
//		options.addHeader(DEVICE_ACTION_HEADER, DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE.toString());
//	    vertx.eventBus().send(MainVerticle.DEVICE_ACTION_EVENTS_ADDRESS, 
//	    		              createDeviceDummyActionPayload(DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE, DEVICE_ACTION.NONE, 0, 0),
//	    		              options);		
//		
//	    
//	    /* *********  Test withaction header - for decrease */
//		options = new DeliveryOptions();
//		options.addHeader(DEVICE_ACTION_HEADER, DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE.toString());
//	    vertx.eventBus().send(MainVerticle.DEVICE_ACTION_EVENTS_ADDRESS, 
//	    		              createDeviceDummyActionPayload(DEVICE_MANAGEMENT_ACTION.ACTIVATE_DEVICE, DEVICE_ACTION.DECREASING, 28, 20),
//	    		              options);
//
	    
//		getDevice(generatedDeviceKey("kousourisHousehold","kitchen-1"), message);
//		getDevice2(generatedDeviceKey("kousourisHousehold","bedroom-1"));
		
	}


	// If I want to do something with the final result/oucome I need to pass a handler eg. SEND/REPLY otherwise no handler needed
	private void registerDevice(String key, Device deviceRegister) {
		SharedData sd = vertx.sharedData();
		sd.<String, Device>getClusterWideMap(DEVICES_MAP, res -> {
			if (res.succeeded()) {
				// SUCCEEDED to find the DEVICES SHARED MAP
				
				// ADDING THE DEVICE
				res.result().putIfAbsent(key, deviceRegister, ar -> {
					if (ar.succeeded()) {
						// HERE IT SHOULD HAVE BEEN ADDED
						System.out.println("ADDED DEVICE : "+deviceRegister.getId() +" with KEY : "+key);
					} else {
						
						// HERE IT SHOULD HAVE failed to BE ADDED on the MAP
					}
				});
			} else {
				// FAILED to find the MAP
			}
		});
	}
	
	private void updateDevice(Device deviceActionable) {
		SharedData sd = vertx.sharedData();
		sd.<String, Device>getClusterWideMap(DEVICES_MAP, res -> {
			if (res.succeeded()) {
				// SUCCEEDED to find the DEVICES SHARED MAP
			
				// GETTING THE DEVICE
				res.result().get(generatedDeviceKey(deviceActionable), ar -> {
					if (ar.succeeded()) {
						
						System.out.println("DEVICE RETRIEVED FROM SHARED MAP --> "+ar.result());
						
						// HERE IT SHOULD HAVE BEEN RETRIEVED based on the key
						System.out.println("RETRIEVED DEVICE FROM SHARED MAP: "+ (res.result() != null ? generatedDeviceKey(ar.result()) : "NONE FOUND") +" AS : "+Json.encodePrettily(ar.result()));
						
						// Check result matches one updating
						if (ar.result() != null && ar.result().equals(deviceActionable)){
							
							// updating
							res.result().put(generatedDeviceKey(deviceActionable), deviceActionable, ar2 -> {
								
								if (ar2.succeeded()) {
									// HERE IT SHOULD HAVE BEEN UPDADED on the MAP
									System.out.println("UPDATED DEVICE : "+deviceActionable.getId() +" to : "+Json.encodePrettily(deviceActionable));
								} else {

									// HERE IT SHOULD HAVE failed to BE Updated on the MAP
									// TODO - Consider Vert.X error
								}
							});
						} else if (ar.result().getActionSequence() > deviceActionable.getActionSequence()) {
							// WARNING: Messages out of Sync
							System.out.println(appErrorPrefix(APP_ERROR_CODES.MESSAGE_OUT_OF_SYNC)+" UPDATE on DEVICE : "+generatedDeviceKey(deviceActionable) +" with Action Sequence "+deviceActionable.getActionSequence()+ "will not be applied as Shared Map already contains action sequence" + ar.result().getActionSequence());
						} else {
							// Trying to update a device for which there is no key registered in the Shared Map
							System.out.println(appErrorPrefix(APP_ERROR_CODES.NON_REGISTERED_DEVICE)+" CANNOT MATCH DEVICE with ID "+generatedDeviceKey(deviceActionable)+" with a registered Device on Shared Map data");
						}
					} else {
						// HERE IT SHOULD HAVE failed to BE RETREIVED from the MAP
						// TODO - Consider Vert.X error
					}
				});
			} else {
				// FAILED to find the MAP
				// TODO - Consider Vert.X error
			}		
		});
	}
	
	private void turnoffDevice(Device deviceActionable) {		
		SharedData sd = vertx.sharedData();

		sd.<String, Device>getClusterWideMap(DEVICES_MAP, res -> {
			if (res.succeeded()) {
				// SUCCEEDED to find the DEVICES SHARED MAP

				// GETTING THE DEVICE
				res.result().get(generatedDeviceKey(deviceActionable), ar -> {
					if (ar.succeeded()) {
						// HERE IT SHOULD HAVE BEEN RETRIEVED based on the key
						System.out.println("RETRIEVED DEVICE FROM SHARED MAP: "+ (res.result() != null ? generatedDeviceKey(ar.result()) : "NONE FOUND") +" AS : "+Json.encodePrettily(ar.result()));

						// Check result matches one updating
						if (ar.result() != null && ar.result().equals(deviceActionable)){

							// updating
							res.result().put(generatedDeviceKey(deviceActionable), deviceActionable, ar2 -> {

								if (ar2.succeeded()) {
									// HERE IT SHOULD HAVE BEEN UPDADED on the MAP
									System.out.println("TURN-OFF DEVICE : "+deviceActionable.getId() +" to : "+Json.encodePrettily(deviceActionable));
								} else {

									// HERE IT SHOULD HAVE failed to BE Updated on the MAP
									// TODO - Consider Vert.X error
								}
							});
						} else if (ar.result().getActionSequence() > deviceActionable.getActionSequence()) {
							// WARNING: Messages out of Sync
							System.out.println(appErrorPrefix(APP_ERROR_CODES.MESSAGE_OUT_OF_SYNC)+" UPDATE on DEVICE : "+generatedDeviceKey(deviceActionable) +" with Action Sequence "+deviceActionable.getActionSequence()+ "will not be applied as Shared Map already contains action sequence" + ar.result().getActionSequence());
						} else {
							// Trying to update a device for which there is no key registered in the Shared Map
							System.out.println(appErrorPrefix(APP_ERROR_CODES.NON_REGISTERED_DEVICE)+" CANNOT MATCH DEVICE with ID "+generatedDeviceKey(deviceActionable)+" with a registered Device on Shared Map data");
						}
					} else {
						// HERE IT SHOULD HAVE failed to BE RETREIVED from the MAP
						// TODO - Consider Vert.X error
					}
				});
			} else {
				// FAILED to find the MAP
				// TODO - Consider Vert.X error
			}		
		});
	}

	private String appErrorPrefix(APP_ERROR_CODES error){
		return error.getType()+error.getErrorCode()+": "+error;
	}
	
	private String generatedDeviceKey(Device device) {
		return generatedDeviceKey(device.gethousePlanId(), device.getId());
	}
	
	private String generatedDeviceKey(String housePlanId, String deviceId) {
		return housePlanId+DEVICES_ID_SEPARATOR+deviceId;
	}

	
	// If I want to do something with the final result/oucome I need to pass a handler eg. SEND/REPLY otherwise no handler needed
	private void getDevice(String key, Message<String> message) {
//	private void getDevice(String key) {

		SharedData sd = vertx.sharedData();
		sd.<String, Device>getClusterWideMap(DEVICES_MAP, res -> {
			if (res.succeeded()) {
				
				// ADDING THE DEVICE
				//res.result().putIfAbsent(arg0, arg1, arg2);
				
				res.result().get(key, ar -> {
					if (ar.succeeded()) {
						Device device = ar.result();
						if (device != null) {
							// HERE I need to return the existing device						
							
							System.out.println("\n\n REPLYING to message FOUND DEVICE \n ------------------------------------------------------------------- \n "+Json.encodePrettily(device)+" \n -------------------------------------------------------------------");
							message.reply(Json.encodePrettily(device));

						} else {
							// HERE I need to return c
							// TODO - Handle ERROR MESSAGE
							message.reply(null);

						}
					} else {
						// Something went wrong! NOT BASED ON THE KEY
						message.fail(ERROR_CODES.DEVICE_MANAGEMENT_GET_DEVICE_ERROR.getErrorCode(), "[device-management.get.device.error] Unexpected Failure Occured during during retrieval of key "+key);
					}
				});
				;
			} else {
				// Something went wrong!
				message.fail(ERROR_CODES.DEVICE_MANAGEMENT_GET_DEVICES_MAP_ERROR.getErrorCode(), "[device-management.get.devices.map.error] Unexpected Failure Occured during retrieval of dvices map ["+DEVICES_MAP+"] from shared data");
			}
		});
	}
	
	// If I want to do something with the final result/oucome I need to pass a handler eg. SEND/REPLY otherwise no handler needed
	private void getDevice2(String key) {

		SharedData sd = vertx.sharedData();
		sd.<String, Device>getClusterWideMap(DEVICES_MAP, res -> {
			if (res.succeeded()) {
				
				// ADDING THE DEVICE
				//res.result().putIfAbsent(arg0, arg1, arg2);
				
				res.result().get(key, ar -> {
					if (ar.succeeded()) {
						Device device = ar.result();
						if (device != null) {
							// HERE I need to return the existing device						
							
							System.out.println("\n\n FOUND DEVICE \n ------------------------------------------------------------------- \n "+device.toString()+" \n -------------------------------------------------------------------");
							
						} else {
							System.out.println("\n FAILED TO FIND In shared map Device " + key);
						}
					} else {
						// Something went wrong! NOT BASED ON THE KEY
						// TODO - VertX error message
					}
				});
				;
			} else {
				// Something went wrong!
				// TODO - VertX error message
			}
		});
	}


	
	
//    private void incrementAndGetDeviceData(RoutingContext rc) {
//    	Future<Object> future = Future.future();
//		vertx.sharedData().getClusterWideMap("device-shared-data", future);
//    	
//    	/* RX JAVA API equivalent 
//        vertx.sharedData().rxGetCounter("device-shared-data")
//            .flatMap(Counter::rxIncrementAndGet)
//            .map(count -> new JsonObject().put("value", count).put("appId", getNodeId()))
//            .subscribe(
//                json -> rc.response().end(json.encode()),
//                rc::fail
//            );
//            */
//    }

//    private void getDeviceData(RoutingContext rc) {
//        vertx.sharedData().rxGetCounter("my-counter")
//            .flatMap(Counter::rxGet)
//            .map(count -> new JsonObject().put("value", count).put("appId", getNodeId()))
//            .subscribe(
//                json -> rc.response().end(json.encode()),
//                rc::fail
//            );
//    }
	
	
	// LATER FOR DEVICE ACTIONS FROM: http://vertx.io/docs/guide-for-java-devs/#_the_database_verticle
	/*
	 *
	public void onMessage(Message<JsonObject> message) {

  if (!message.headers().contains("action")) {
    LOGGER.error("No action header specified for message with headers {} and body {}",
      message.headers(), message.body().encodePrettily());
    message.fail(ErrorCodes.NO_ACTION_SPECIFIED.ordinal(), "No action header specified");
    return;
  }
  String action = message.headers().get("action");

  switch (action) {
    case "all-pages":
      fetchAllPages(message);
      break;
    case "get-page":
      fetchPage(message);
      break;
    case "create-page":
      createPage(message);
      break;
    case "save-page":
      savePage(message);
      break;
    case "delete-page":
      deletePage(message);
      break;
    default:
      message.fail(ErrorCodes.BAD_ACTION.ordinal(), "Bad action: " + action);
  }
}
	 */
	


	
	  /* TODO - Remove as only tester helper methods to generate messages on the bus */
	  private String createRegistrationPayload() {
			Device regDev1 = new Device(null, "kitchen-1", DEVICE_TYPE.AIRCON, DEVICE_ACTION.NONE, DEVICE_STATE.OFF, 0, 0, 0L, 0L);
			Device regDev2 = new Device(null, "bedroom-1", DEVICE_TYPE.AIRCON, DEVICE_ACTION.NONE, DEVICE_STATE.OFF, 0, 0, 0L, 0L);
			
			HashMap<String, List<Device>> payload = new HashMap<String, List<Device>>();
			DeviceDTO dtoMsg = new DeviceDTO("kousourisHousehold", Arrays.asList(regDev1, regDev2));
			//payload.put("kousourisHousehold", Arrays.asList(regDev1, regDev2));
			
			System.out.println("\n-----------------PAYLOAD ---------------------------\n"+Json.encodePrettily(dtoMsg)+"\n------------------------------------------------------------------");
			
			return Json.encodePrettily(dtoMsg);	
	  }
	  
	  private String createDeviceDummyActionPayload(DEVICE_MANAGEMENT_ACTION devicemntaction,  DEVICE_ACTION action, int fromNo, int toNo) {
		  Device updateDevice;

		if (devicemntaction.equals(DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE.toString())) {
			updateDevice = new Device("kousourisHousehold", "bedroom-1", DEVICE_TYPE.AIRCON, DEVICE_ACTION.INCREASING, DEVICE_STATE.OFF, 0, 0, 0L, 1L);
		} else {
			updateDevice = new Device("kousourisHousehold", "bedroom-1", DEVICE_TYPE.AIRCON, action, DEVICE_STATE.ON, fromNo, toNo, TimeUtils.timeInMillisNow(), 1L);
		}
		System.out.println("\n-----------------PAYLOAD ---------------------------\n"+Json.encodePrettily(updateDevice)+"\n------------------------------------------------------------------");

		return Json.encodePrettily(updateDevice);	

	  }
}
