package com.redhat.consulting.vertx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import com.redhat.consulting.vertx.Constants.AppErrorCode;
import com.redhat.consulting.vertx.Constants.DeviceAction;
import com.redhat.consulting.vertx.Constants.DeviceState;
import com.redhat.consulting.vertx.Constants.DeviceType;
import com.redhat.consulting.vertx.Constants.DevicesPurpose;
import com.redhat.consulting.vertx.Constants.ErrorCode;
import com.redhat.consulting.vertx.data.Device;
import com.redhat.consulting.vertx.dto.AmbianceDTO;
import com.redhat.consulting.vertx.dto.DeviceDTO;
import com.redhat.consulting.vertx.dto.DevicesRegistratoinDTO;
import com.redhat.consulting.vertx.dto.DeviceDataDTO;
import com.redhat.consulting.vertx.dto.HomePlanRegulationDTO;
import com.redhat.consulting.vertx.utils.TimeUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
//import io.vertx.rxjava.core.shareddata.Counter;
//import io.vertx.rxjava.ext.web.RoutingContext;


/**
 * Vert.X verticle to deliver Device Management MicroService

 * @author stkousso
 * 
 * Handles
 * 
 * Device Registration
 * Receives message from Vert.X Event Bus address '#device-reg'
 * 
 * Device ACTION
 * Receives message from Vert.X Event Bus address '#device-action'
 * 
 * Utilizes Header {@code MainVerticle.DEVICE_ACTION_HEADER} with possible values {@code MainVerticle.DEVICE_MANAGEMENT_ACTION} 
 * 	
 * Device DATA
 * Receives message from Vert.X Event Bus address '#device-action' and replies to the same address
 *
 * Device UPDATE
 * Receives message from Vert.X Event Bus address '#ambiance-data'
 *		
 */
public class MainVerticle extends AbstractVerticle {

	private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
	
	@Override
	public void start() {
		logger.info("\n----------------------------------------------------------------------------\n STARTING DEVICE Management - MainVerticle \n----------------------------------------------------------------------------");
	
		registerDevices(Constants.DEVICE_REGISTRATION_EVENTS_ADDRESS);
		readDevice(Constants.DEVICE_DATA_EVENTS_ADDRESS);
		
	}
	
	/* **********************************************************************************
	 * HANDLE DEVICE REGISTRATIONS 
	 * Receive message from the address 'DEVICE_REGISTRATION_EVENTS_ADDRESS'
	 * *********************************************************************************/
	private void registerDevices(String deviceDataEventsAddress) {
		
		vertx.eventBus().<String>consumer(deviceDataEventsAddress, message -> {

			logger.info("\n CONSUMING message from #"+deviceDataEventsAddress+ "(HANDLED BY VertX.EventLoop" + this.toString()+")\n ");

			// Check whether we have received a payload in the incoming message
			if (message.body().isEmpty()) {
				logger.error(appErrorPrefix(AppErrorCode.MESSAGE_IS_EMPTY));
			} else {
				// We will receive it as JSON string, transform it to its class equivalent
				DevicesRegistratoinDTO devicesToRegister = Json.decodeValue(message.body(), DevicesRegistratoinDTO.class);

				// TODO - NEED TO CHANGE THIS TO PROCESS THE MESSAGE AS A STREAM
				String housePlanId = devicesToRegister.getId();
				List<DeviceDTO> devices = devicesToRegister.getDevices();
				for (DeviceDTO device : devices) {
					registerDevice(generatedDeviceKey(devicesToRegister.getId(), device.getId()), 
							new Device(housePlanId, device.getId(), device.getType(), device.getAction(), 
									device.getState(), generateRandomLocationSensorTemperature(), device.getLastUpdate()));
				}
			}
		});		
	}

	// If we want to do something with the final result/oucome I need to pass a handler eg. SEND/REPLY otherwise no handler needed
	private Future<Device> registerDevice(String key, Device deviceRegister) {
	// NEW VERSION with Futures (Can we do it with RXJava API?
		
		Future<Device> futureRegDevice = Future.future();
		Future<AsyncMap<String, Device>> futureDevicesSharedMap = retrieveSharedMap(Constants.DEVICES_MAP);
		
		futureDevicesSharedMap.compose(sharedMap -> {

			futureDevicesSharedMap.result().putIfAbsent(key, deviceRegister, ar -> {
				if (ar.succeeded()) {
					Device device = ar.result();
					logger.info("\n REGISTERED DEVICE : "+key+" {"+deviceRegister+"}\n");
					futureRegDevice.complete(device);
				} else {
					futureRegDevice.fail(appErrorPrefix(AppErrorCode.DEVICE_ALREADY_REGISTERED)+" for key "+generatedDeviceKey(deviceRegister));
				}
			});
		}, Future.future().setHandler(handler -> {
			logger.error(vertxErrorPrefix(ErrorCode.DEVICE_MANAGEMENT_GET_DEVICE_ERROR)+" Unexpected Failure Occured during Device registration", handler.cause());
		}));
		
		return futureRegDevice;
		
		// OLD VERSION with handlers
//		SharedData sd = vertx.sharedData();
//		sd.<String, Device>getClusterWideMap(DEVICES_MAP, res -> {
//			if (res.succeeded()) {
//				// SUCCEEDED to find the DEVICES SHARED MAP
//				
//				// ADDING THE DEVICE
//				res.result().putIfAbsent(key, deviceRegister, ar -> {
//					if (ar.succeeded()) {
//						// HERE IT SHOULD HAVE BEEN ADDED
//						logger.info("ADDED DEVICE : "+deviceRegister.getId() +" with KEY : "+key);
//					} else {
//						// HERE IT SHOULD HAVE failed to BE ADDED on the MAP
//						logger.error("FAILED DEVICE : "+deviceRegister.getId() +" with KEY : "+key);						
//					}
//				});
//			} else {
//				logger.error("FAILED TO Retrieve DEVICES MAP");
//			}
//		});
	}



	private void readDevice(String deviceDataEventsAddress) {
		vertx.eventBus().<String>consumer(deviceDataEventsAddress, message -> {

			logger.info("CONSUMING message from #"+deviceDataEventsAddress+" (HANDLED BY VerX.EventLoop" + this.toString()+"\n");

			// Check whether we have received a payload in the incoming message
			if (message.body().isEmpty()) {
				logger.error(appErrorPrefix(AppErrorCode.MESSAGE_IS_EMPTY)+" Received Message on address #"+deviceDataEventsAddress+" is empty");
				// SEND/REPLY example for compensation
				// message.reply(json.put("message", "ERROR"));
                                message.fail("The message provided was empty", AppErrorCode.NON_REGISTERED_DEVICE.getErrorCode());
			} else {

				// We will receive it as JSON string, transform it to its class equivalent
				DeviceDataDTO deviceDataRequested = Json.decodeValue(message.body(), DeviceDataDTO.class);
				logger.trace("Returning Requested Device Details"+deviceDataRequested+"\n");

				replyDevice(generatedDeviceKey(deviceDataRequested.getHousePlanId(), deviceDataRequested.getSensor()), message);			
			}
		});		

	}
	




	private Future<AsyncMap<String, Device>> retrieveSharedMap(String mapName){
		SharedData sd = vertx.sharedData();

		Future<AsyncMap<String, Device>> futureDevicesMap = Future.future();

		sd.<String, Device>getClusterWideMap(Constants.DEVICES_MAP, res -> {
			if (res.succeeded()) {
				futureDevicesMap.complete(res.result());
			} else {
				res.cause().printStackTrace();
				futureDevicesMap.fail(vertxErrorPrefix(ErrorCode.DEVICE_MANAGEMENT_GET_DEVICES_MAP_ERROR)+" Failed to retrieved the shared MAP with name ["+mapName+"]");
			}
		});
		return futureDevicesMap;
	}
	
	private Future<Device> getDevice(String key) {

		Future<Device> futureRegDevice = Future.future();
		Future<AsyncMap<String, Device>> futureDevicesSharedMap = retrieveSharedMap(Constants.DEVICES_MAP);
		
		futureDevicesSharedMap.compose(sharedMap -> {

			futureDevicesSharedMap.result().get(key, ar -> {
				if (ar.succeeded()) {
					Device device = ar.result();
					
					logger.info("DEVICE UPDATED FROM : {"+device+"}\n" );
					
					if (device != null) {
						futureRegDevice.complete(device);
					} else {
						futureRegDevice.fail(appErrorPrefix(AppErrorCode.NON_REGISTERED_DEVICE)+" for key "+generatedDeviceKey(device));
					}
				} else {
					// Something went wrong! NOT BASED ON THE KEY
					futureRegDevice.fail(vertxErrorPrefix(ErrorCode.DEVICE_MANAGEMENT_GET_DEVICE_ERROR)+" Unexpected Failure Occured during during retrieval of a Device ");
				}
			});
		}, Future.future().setHandler(handler -> {
			logger.error(vertxErrorPrefix(ErrorCode.DEVICE_MANAGEMENT_GET_DEVICES_MAP_ERROR), handler.cause());
		}));
		
		return futureRegDevice;
	}
	
	// If I want to do something with the final result/oucome I need to pass a handler eg. SEND/REPLY otherwise no handler needed
	private void replyDevice(String key, Message<String> message) {

		SharedData sd = vertx.sharedData();
		sd.<String, Device>getClusterWideMap(Constants.DEVICES_MAP, res -> {
			if (res.succeeded()) {

				res.result().get(key, ar -> {
					if (ar.succeeded()) {
						Device device = ar.result();
						if (device != null) {
							
							DeviceDTO dto = new DeviceDTO(device.getHousePlanId(), device.getId(), device.getType(), device.getAction(), device.getState(), device.getTemperature(), device.getLastUpdate());
							
							logger.info("\n\n REPLYING to (#"+Constants.DEVICE_DATA_EVENTS_ADDRESS+") message FOUND DEVICE ("+key+") \n ------------------------------------------------------------------- \n "+Json.encodePrettily(device)+" \n -------------------------------------------------------------------");

                                                                        // FIXME -  Having Consumed from EventBus address #device-data a request for the device information
                                                                        //          reply with the identified device info. Content must be DeviceDTO and Json formatted.

						} else {
							logger.error("\n\n DEVICE NOT FOUND ");
							message.reply(null);
						}
					} else {
						// Something went wrong! NOT BASED ON THE KEY
						message.fail(ErrorCode.DEVICE_MANAGEMENT_GET_DEVICE_ERROR.getErrorCode(), "[device-management.get.device.error] Unexpected Failure Occured during during retrieval of key "+key);
					}
				});

			} else {
				// Something went wrong!
				message.fail(ErrorCode.DEVICE_MANAGEMENT_GET_DEVICES_MAP_ERROR.getErrorCode(), "[device-management.get.devices.map.error] Unexpected Failure Occured during retrieval of dvices map ["+Constants.DEVICES_MAP+"] from shared data");
			}
		});
	}
	

	private String appErrorPrefix(AppErrorCode error){
		return error.getErrorCode()+": "+error;
	}
	
	private String vertxErrorPrefix(ErrorCode error){
		return error.getErrorCode()+": "+error;
	}
	
	private String generatedDeviceKey(Device device) {
		return generatedDeviceKey(device.getHousePlanId(), device.getId());
	}
	
	private String generatedDeviceKey(HomePlanRegulationDTO device) {
		return generatedDeviceKey(device.getHousePlanId(), device.getId());
	}
	
	private String generatedDeviceKey(String housePlanId, String deviceId) {
		return housePlanId+Constants.DEVICES_ID_SEPARATOR+deviceId;
	}

	/* Used in registration and in turn off activities on a Device object */
	private int generateRandomLocationSensorTemperature(){
		Random rn = new Random();
		return (rn.nextInt(45 - 13 + 1) + 13);
	}
	
}
