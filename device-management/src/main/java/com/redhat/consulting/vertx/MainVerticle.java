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
		//deviceAction(Constants.DEVICE_ACTION_EVENTS_ADDRESS);
		//readDevice(Constants.DEVICE_DATA_EVENTS_ADDRESS);
		//updateDevice(Constants.AMBIANCE_DATA_EVENTS_ADDRESS);
		
	}
	
	/* **********************************************************************************
	 * HANDLE DEVICE REGISTRATIONS 
	 * Receive message from the address 'DEVICE_REGISTRATION_EVENTS_ADDRESS'
	 * *********************************************************************************/
	private void registerDevices(String deviceDataEventsAddress) {
		logger.info("FIXME - REGISTRATION HAS NOT TAKEN PLACE");

		// FIXME - Get the Vert.x EventBus and provide a hanlder which will read messages sent to #device-reg
                //         Once in place add the following code snippet as the handler content
                /* 
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
                 */

        }

	// If we want to do something with the final result/oucome I need to pass a handler eg. SEND/REPLY otherwise no handler needed
	private void registerDevice(String key, Device deviceRegister) {
           logger.info("Handler for adding Device ["+deviceRegister+"] is missing. You need to complete it");
           // FIXME: Get access to Vert.x shared Data and retrieve Map Constants.DEVICES_MAP 
           //        then provide a handler with the following code snippet to register the device to it
 
           //	if (res.succeeded()) {
           //		// SUCCEEDED to find the DEVICES SHARED MAP
           //		
           //		// ADDING THE DEVICE
           //		res.result().putIfAbsent(key, deviceRegister, ar -> {
           //			if (ar.succeeded()) {
           //				// HERE IT SHOULD HAVE BEEN ADDED
           //				logger.info("ADDED DEVICE : "+deviceRegister.getId() +" with KEY : "+key);
           //			} else {
           //				// HERE IT SHOULD HAVE failed to BE ADDED on the MAP
           //				logger.error("FAILED DEVICE : "+deviceRegister.getId() +" with KEY : "+key);						
           //			}
           //		});
           //		 else {
           //		logger.error("FAILED TO Retrieve DEVICES MAP");
           //	}
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
	
}
