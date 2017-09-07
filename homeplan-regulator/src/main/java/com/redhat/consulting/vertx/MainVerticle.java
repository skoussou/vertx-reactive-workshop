package com.redhat.consulting.vertx;

import com.redhat.consulting.vertx.Constants.AppErrorCode;
import com.redhat.consulting.vertx.Constants.DeviceAction;
import com.redhat.consulting.vertx.data.Ambiance;
import com.redhat.consulting.vertx.data.HomePlan;
import com.redhat.consulting.vertx.data.SensorLocation;
import com.redhat.consulting.vertx.dto.AmbianceDTO;
import com.redhat.consulting.vertx.dto.DeviceActionDTO;
import com.redhat.consulting.vertx.dto.HomePlanDTO;
import com.redhat.consulting.vertx.utils.Mapper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Vert.X verticle to deliver HomePlan Regulator MicroService
 * 
 * @author stkousso
 *
 */
public class MainVerticle extends AbstractVerticle {
	
	private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
	
	public int actionCounter = 0;
	
	  @Override
	  public void start() {
			logger.info("\n----------------------------------------------------------------------------\n HOMEPLAN REGULATOR - MainVerticle \n----------------------------------------------------------------------------");
		  
		  startHomeplanRegulatorEventBusProvider();					
	  }
	
	private void startHomeplanRegulatorEventBusProvider() {

                // FIXME - Consume messages publised on #ambiance-data address (Constants.AMBIANBCE_DATA_ADDRESS)
                //         and add handler code as follows
                // logger.info("\n----------------------------------------------------------------------------\n HOMEPLAN REGULATOR EVENT BUS ready (Vert.X EventLoop "+this.toString()+") \n----------------------------------------------------------------------------");	
		// applyHomePlanRegulation(message);
                
	}

	private void applyHomePlanRegulation(Message<String> message) {
		// We will receive it as JSON string, transform it to its class equivalent
		Ambiance ambianceData = Mapper.toAmbiance(Json.decodeValue(message.body(), AmbianceDTO.class));
		
		logger.info("Begin Regulating Location "+ambianceData.getHousePlanId()+"-"+ambianceData.getSensorLocation());
		logger.debug("AMBIANCE DATA RECEIVED "+Json.encodePrettily(ambianceData));	

		Future<HomePlan> futureHomePlan = getHomePlan(ambianceData.getHousePlanId());
		futureHomePlan.compose(s1 -> {
			HomePlan homePlan = futureHomePlan.result();
			
			Future<String> futureRegMsg = sendRegulatoryMsg(homePlan, ambianceData);

			futureRegMsg.compose(s2 -> {

				if (s2 != null) {
					logger.info("Applied Successfully HomePlan temperature regulation for location "+ambianceData.getHousePlanId()+"-"+ambianceData.getSensorLocation().getId());
				} else {
					logger.info("Failed to apply HomePlan temperature regulation for location "+ambianceData.getHousePlanId()+"-"+ambianceData.getSensorLocation().getId());
				}

			}, Future.future().setHandler(handler -> {
				logger.error(appErrorPrefix(AppErrorCode.HOMEPLAN_REGULATOR_FAIL_APPLY_HOMEPLAN)+"Homeplan Regulation Error", handler.cause());
			}));
			
		}, Future.future().setHandler(handler -> {
			logger.error(appErrorPrefix(AppErrorCode.HOMEPLAN_REGULATOR_FAIL_RETRIEVE_HOMEPLAN)+"Homeplan Retrieval Error", handler.cause());
		}));
	}	
	
	private Future<HomePlan> getHomePlan(String homeplanId) {
		logger.debug("\n\n ASKING HOMEPLAN service for homeplan id --> "+homeplanId+"\n");
		
		Future<HomePlan> future = Future.future();
		vertx.eventBus().send(Constants.HOMEPLANS_EVENTS_ADDRESS, homeplanId, reply -> {
			if (reply.succeeded()) {
				logger.debug("HOMEPLAN WITH ID ["+homeplanId+"] RETURNED");
				final HomePlanDTO homePlanDTO = Json.decodeValue(reply.result().body().toString(), HomePlanDTO.class);
				final HomePlan homePlan = Mapper.toHomePlan(homePlanDTO);
				future.complete(homePlan);
			} else {
				logger.error("HOMEPLAN WITH ID ["+homeplanId+"] NOT FOUND");
				reply.cause().printStackTrace();
				future.fail("No reply from Homeplan service");
			}
		});
		return future;
	}
		
	
	
	private Future<String> sendRegulatoryMsg(HomePlan plan, Ambiance ambianceData) {
		Future<String> futureHPReguMsg = Future.future();
		
		if (plan.getSensorLocations()!= null && !plan.getSensorLocations().isEmpty()) {
			for (SensorLocation sl : plan.getSensorLocations()) {

				logger.info("Finding match between ambiance data ["+ambianceData.getHousePlanId()+"-"+ambianceData.getSensorLocation().getId()
						+"] and sensor location ["+ambianceData.getHousePlanId()+"-"+sl.getId()+"]");

				if (ambianceData.getSensorLocation().getId()!= null && sl.getId() != null && ambianceData.getSensorLocation().getId().equals(sl.getId())){
					logger.debug("MATCH-FOUND: Homeplan Regulator in action on Device: "+ambianceData.getHousePlanId()+"-"+sl.getId());
					logger.info("AMBIANCE"+Json.encodePrettily(ambianceData.getSensorLocation())+"\n"
					            +"PLAN"+Json.encodePrettily(sl));


                                        // FIXME - Generate a Message to be sent to #device-action EventBus adddress in such a way that the same address is 
                                        //         used for homeplan regulator to define different device management actions (DeviceAction.INCREASING, 
                                        //         DeviceAction.DECREASING, DeviceAction.TURNOFF). See device-management Verticle method deviceAction for
                                        //         inspiration and use applyTemperatureHomePlan(..) for the action decision                                        
					

					futureHPReguMsg.fail("FIXME - Missing solution to send the HomePlan Regulator decision on device "+ambianceData.getHousePlanId()+"-"+sl.getId());
					// Uncomment with changes removing the fail
					//futureHPReguMsg.complete("HomePlan Regulation applied");
				}
			}
		}
		return futureHPReguMsg;
	}
	

	
	private DeviceAction applyTemperatureHomePlan(int planSensorLocationTemperature, int sensorTemperature) {
		
		logger.info("Applying Temperature HomePlan for PLAN TEMP ["+planSensorLocationTemperature+" Location TEMP ["+sensorTemperature+"] ");
		
		
		if (planSensorLocationTemperature > sensorTemperature)
			return DeviceAction.INCREASING;
		else if (planSensorLocationTemperature < sensorTemperature)
			return DeviceAction.DECREASING;
		return DeviceAction.TURNOFF;
	}
	
	private String appErrorPrefix(AppErrorCode error){
		return error.getErrorCode()+": "+error;
	}

}
