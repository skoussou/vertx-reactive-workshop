package com.redhat.consulting.vertx;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.redhat.consulting.vertx.MainVerticle.DEVICE_TYPE;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.SharedData;
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
 * Receives message from {@code MainVerticle.DEVICE_REGISTRATION_EVENTS_ADDRESS} 
 * Utilizes Header {@code MainVerticle.DEVICE_ACTION_HEADER} with possible values INCREASE_DEVICE_VALUE, 
 * 		DECREASE_DEVICE_VALUE, TURNOFF_DEVICE from {@code MainVerticle.DEVICE_MANAGEMENT_ACTION}
 * Message expected format
 * {
     "houseHoldId" : kousourisHousehold,
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
     "houseHoldId" : kousourisHousehold,
     "id" : "kitchen-1",
     "type" : "AIRCON",
     "action" : NONE,
     "state" : OFF,
     "fromNumber" : 0,
     "toNumber" : 0,
     "timeStart" : null
   }

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
//	public static final Integer DEVICE_MANAGEMENT_GET_DEVICE_ERROR = 100;
//	public static final Integer DEVICE_MANAGEMENT_GET_DEVICES_MAP_ERROR = 101;
//	public static final Integer DEVICE_MANAGEMENT_ACTIONS_BAD_ACTION = 102;

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
		DECREASE_DEVICE_VALUE,
		INCREASE_DEVICE_VALUE,
		TURNOFF_DEVICE;
	}
	
	public enum ERROR_CODES {
		DEVICE_MANAGEMENT_GET_DEVICE_ERROR(100),
		DEVICE_MANAGEMENT_GET_DEVICES_MAP_ERROR(101),
		DEVICE_MANAGEMENT_ACTIONS_BAD_ACTION (102);
		
		ERROR_CODES(int errorCode){
			this.errorCode = errorCode;
		}
		
		private int errorCode;
		
		public int getErrorCode(){
			return errorCode;
		}
		
	}

	
	@Override
	public void start() {

		System.out.println("\n\n STARTING MainVerticle");


		// **********************************************************************************
		// HANDLE DEVICE REGISTRATIONS 
		// Receive message from the address 'DEVICE_REGISTRATION_EVENTS_ADDRESS'
		// *********************************************************************************
		
//		vertx.eventBus().<String>consumer(DEVICE_REGISTRATION_EVENTS_ADDRESS, message -> {
//
//			System.out.println("\n\n CONSUMING message from #"+DEVICE_REGISTRATION_EVENTS_ADDRESS);
//			System.out.println("HANDLED BY Verticle.EventLoop" + this.toString());
//
//			// Check whether we have received a payload in the incoming message
//			if (message.body().isEmpty()) {
//				// SEND/REPLY example
//				// message.reply(json.put("message", "hello"));
//			} else {
//								
//				// Option 1 - We will receive it as JSON String and leave it as Json in the map
//				//JsonObject deviceJsonPayload = new JsonObject(message.body().toString());
//
//				// Option 2 - We will receive it as JSON string, transform it to its class equivalent
//				DeviceDTO devicesToRegister = Json.decodeValue(message.body(), DeviceDTO.class);
//
//				//getDevice(deviceToRegister.getHouseHoldId()+deviceToRegister.getId(), message);
//				
//				String householdId = devicesToRegister.getId();
//				
//				// TODO - NEED TO CHANGE THIS TO PROCESS THE MESSAGE AS A STREAM 
//					List<Device> devices = devicesToRegister.getDevices();
//					for (Device device : devices) {
//						registerDevice(devicesToRegister.getId()+"-"+device.getId(), device);
//					}
//					
//				
//			        // ONLY RE-ACTIVATE FOR TESTING - HACKING
//				//getDevice("kousourisHousehold"+"-"+"kitchen-1", message);
//					//getDevice("kousourisHousehold"+"-"+"bedroom-1", message);
//			}
//		});


         //ONLY RE-ACTIVATE FOR TESTING - HACKING
		// System.out.println("\n\n SENDING MESSAGE to #" + MainVerticle.DEVICE_REGISTRATION_EVENTS_ADDRESS);   
		//vertx.eventBus().send(MainVerticle.DEVICE_REGISTRATION_EVENTS_ADDRESS, createRegistrationPayload());


		// **********************************************************************************
		// HANDLE DEVICE ACTIONS 
		// Receive message from the address 'DEVICE_ACTION_EVENTS_ADDRESS'
		// ACTIONS increase/decrease/deactivate
		// separate ACTIONS based on the 'header' of the message
		// *********************************************************************************

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
				
				  switch (action) {
				    case DECREASE_DEVICE_VALUE:
				    	System.out.println("ACTION: "+ DEVICE_MANAGEMENT_ACTION.DECREASE_DEVICE_VALUE +"on DEVICE: "+deviceActionable.getHouseHoldId()+"-"+deviceActionable.getId());
				    	//decreaseDevice(deviceActionable);
				      break;
				    case INCREASE_DEVICE_VALUE:
				    	System.out.println("ACTION: "+ DEVICE_MANAGEMENT_ACTION.INCREASE_DEVICE_VALUE +"on DEVICE: "+deviceActionable.getHouseHoldId()+"-"+deviceActionable.getId());
				    	//increaseDevice(deviceActionable);
				      break;
				    case TURNOFF_DEVICE:
				    	System.out.println("ACTION: "+ DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE +"on DEVICE: "+deviceActionable.getHouseHoldId()+"-"+deviceActionable.getId());
				    	//turnoffDevice(deviceActionable);
				      break;
				    default:
				      message.fail(ERROR_CODES.DEVICE_MANAGEMENT_ACTIONS_BAD_ACTION.getErrorCode(), "Bad action: " + action);
				  }
				
			}
		});
		
        // ONLY RE-ACTIVATE FOR TESTING - HACKING
	    System.out.println("\n\n SENDING MESSAGE to #" + MainVerticle.DEVICE_ACTION_EVENTS_ADDRESS);   
		
	    /* *********  Test withaction header - for increase */
		DeliveryOptions options = new DeliveryOptions();
		options.addHeader(DEVICE_ACTION_HEADER, DEVICE_MANAGEMENT_ACTION.INCREASE_DEVICE_VALUE.toString());
	    vertx.eventBus().send(MainVerticle.DEVICE_ACTION_EVENTS_ADDRESS, 
	    		              createDeviceDummyActionPayload(DEVICE_MANAGEMENT_ACTION.INCREASE_DEVICE_VALUE, DEVICE_ACTION.INCREASING, 17, 22),
	    		              options);
	    
	    /* *********  Test withaction header - for decrease */
		options = new DeliveryOptions();
		options.addHeader(DEVICE_ACTION_HEADER, DEVICE_MANAGEMENT_ACTION.DECREASE_DEVICE_VALUE.toString());
	    vertx.eventBus().send(MainVerticle.DEVICE_ACTION_EVENTS_ADDRESS, 
	    		              createDeviceDummyActionPayload(DEVICE_MANAGEMENT_ACTION.DECREASE_DEVICE_VALUE, DEVICE_ACTION.DECREASING, 28, 20),
	    		              options);
	    
	    /* *********  Test withaction header - for TURN OFF device */
		options = new DeliveryOptions();
		options.addHeader(DEVICE_ACTION_HEADER, DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE.toString());
	    vertx.eventBus().send(MainVerticle.DEVICE_ACTION_EVENTS_ADDRESS, 
	    		              createDeviceDummyActionPayload(DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE, DEVICE_ACTION.NONE, 0, 0),
	    		              options);

	
	}
	
	  private String createRegistrationPayload() {
			Device regDev1 = new Device(null, "kitchen-1", DEVICE_TYPE.AIRCON, null, null,null, null, null);
			Device regDev2 = new Device(null, "bedroom-1", DEVICE_TYPE.AIRCON, null, null, null, null, null);
			
			HashMap<String, List<Device>> payload = new HashMap<String, List<Device>>();
			DeviceDTO dtoMsg = new DeviceDTO("kousourisHousehold", Arrays.asList(regDev1, regDev2));
			//payload.put("kousourisHousehold", Arrays.asList(regDev1, regDev2));
			
			
			System.out.println("\n-----------------PAYLOAD ---------------------------\n"+Json.encodePrettily(dtoMsg)+"\n------------------------------------------------------------------");
			
			return Json.encodePrettily(dtoMsg);	
			
	  }
	  
	  private String createDeviceDummyActionPayload(DEVICE_MANAGEMENT_ACTION devicemntaction,  DEVICE_ACTION action, int fromNo, int toNo) {
		  Device updateDevice;

		if (devicemntaction.equals(DEVICE_MANAGEMENT_ACTION.TURNOFF_DEVICE.toString())) {
			updateDevice = new Device("householdKousouris", "bedroom-1", DEVICE_TYPE.AIRCON, DEVICE_ACTION.INCREASING, DEVICE_STATE.OFF, 0, 0, null);
		} else {
			updateDevice = new Device("householdKousouris", "bedroom-1", DEVICE_TYPE.AIRCON, action, DEVICE_STATE.ON, fromNo, toNo, null);
		}




		System.out.println("\n-----------------PAYLOAD ---------------------------\n"+Json.encodePrettily(updateDevice)+"\n------------------------------------------------------------------");

		return Json.encodePrettily(updateDevice);	

	  }
	
	// If I want to do something with the final result/oucome I need to pass a handler eg. SEND/REPLY otherwise no handler needed
	private void registerDevice(String key, Device deviceRegister) {
		SharedData sd = vertx.sharedData();
		sd.<String, Device>getClusterWideMap(DEVICES_MAP, res -> {
			if (res.succeeded()) {
				
				// SUCCEEDED to find the MAP
				
				// ADDING THE DEVICE
				res.result().putIfAbsent(key, deviceRegister, ar -> {
					if (ar.succeeded()) {
						// HERE IT SHOULD HAVE BEEN ADDED
//						message.
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
//							routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
//									.end(Json.encodePrettily(ar.result()));
							
							
							System.out.println("\n\n FOUND DEVICE \n ------------------------------------------------------------------- \n "+device.toString()+" \n -------------------------------------------------------------------");
							
							// IF I AM USING A HANDLER
							message.reply(device);
							// WITHOUT a handler
							//return device;
						} else {
							// HERE I need to return c
							//routingContext.response().setStatusCode(404).end();
							message.reply(null);
							// WITHOUT a handler
							//return device;
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

}
