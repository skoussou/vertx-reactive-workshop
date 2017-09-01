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
		vertx.eventBus().<String>consumer(Constants.AMBIANBCE_DATA_ADDRESS, message -> {
			logger.info("\n----------------------------------------------------------------------------\n HOMEPLAN REGULATOR EVENT BUS ready (Vert.X EventLoop "+this.toString()+" \n----------------------------------------------------------------------------");	
			applyHomePlanRegulation(message);
		});
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
				logger.info("HOMEPLAN WITH ID ["+homeplanId+"] RETURNED");
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
					logger.info("MATCH-FOUND: Homeplan Regulator in action on Device: "+ambianceData.getHousePlanId()+"-"+sl.getId());
					logger.info("AMBIANCE"+Json.encodePrettily(ambianceData.getSensorLocation()));
					logger.info("PLAN"+Json.encodePrettily(sl));

					DeviceAction headerAction = applyTemperatureHomePlan(sl.getTemperature(), ambianceData.getSensorLocation().getTemperature());
					String msgPayload = Json.encodePrettily((new DeviceActionDTO(ambianceData.getHousePlanId(), sl.getId())));

					sendDeviceAction(headerAction, msgPayload);

					futureHPReguMsg.complete("HomePlan Regulation applied");
				}
			}
		}
		return futureHPReguMsg;
	}
	
	  private Future<String> sendDeviceAction(DeviceAction headerAction, String msgPayload) {
		  Future<String> future = Future.future();
		  
		  logger.info("Sending Regulating action <"+headerAction+"> on Device: "+msgPayload);
					
		  DeliveryOptions options = new DeliveryOptions();
		  options.addHeader(Constants.DEVICE_ACTION_HEADER, headerAction.toString());
			
		  vertx.eventBus().send(Constants.DEVICE_ACTION_EVENTS_ADDRESS, msgPayload, options);
		  future.complete("HomePlan Regulation Sent");
		  return future;
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
