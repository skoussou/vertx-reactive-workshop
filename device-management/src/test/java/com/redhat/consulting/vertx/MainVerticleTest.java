package com.redhat.consulting.vertx;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.redhat.consulting.vertx.Constants.DeviceType;
import com.redhat.consulting.vertx.data.Device;
import com.redhat.consulting.vertx.dto.DeviceDTO;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private Vertx vertx;
  
  final AtomicBoolean loaded = new AtomicBoolean(false);

  @Before
  public void setUp(TestContext tc) {
//	  ClusterManager mgr = new HazelcastClusterManager();
//
//	 VertxOptions options = new VertxOptions().setClusterManager(mgr);
//	 options.setClustered(true).setClusterHost("127.0.0.1");
//	 Vertx.clusteredVertx(options, res -> {if (res.succeeded()) {
//		    vertx = res.result();
//		  } else {
//		    // failed!
//		  }});
////	 vertx.deployVerticle(MainVerticle.class.getName(), tc.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext tc) {
//    vertx.close(tc.asyncAssertSuccess());
  }

  @Test
  public void testRegistration(TestContext tc) {
////	  Awaitility.waitAtMost(Duration.ofMinutes(1L)).await().untilTrue(loaded);
//	  
//    Async async = tc.async();
//    vertx.deployVerticle(MainVerticle.class.getName(), tc.asyncAssertSuccess());
////    vertx.createHttpClient().getNow(8080, "localhost", "/", response -> {
////      tc.assertEquals(response.statusCode(), 200);
////      response.bodyHandler(body -> {
////        tc.assertTrue(body.length() > 0);
////        async.complete();
////      });
////    });
//    System.out.println("\n\n SENDING MESSAGE to #" + MainVerticle.DEVICE_REGISTRATION_EVENTS_ADDRESS);
//    
//    vertx.eventBus().send(MainVerticle.DEVICE_REGISTRATION_EVENTS_ADDRESS, createRegistrationPayload());
//    
//    async.complete(); 
  }

  /* ----------- CREATEPAYLOAD DEVICE FOR REGISTRATION-------------- */
  private String createRegistrationPayload() {
		Device regDev1 = new Device(null, "kitchen-1", DeviceType.AIRCON, null, null,0, 0L);
		Device regDev2 = new Device(null, "bedroom-1", DeviceType.AIRCON, null, null, 0, 0L);
		
		HashMap<String, List<Device>> payload = new HashMap<String, List<Device>>();
		DeviceDTO dtoMsg = new DeviceDTO("kousourisHousehold", Arrays.asList(regDev1, regDev2));
		//payload.put("kousourisHousehold", Arrays.asList(regDev1, regDev2));
		
		
		System.out.println("\n-----------------PAYLOAD ---------------------------\n"+Json.encodePrettily(dtoMsg)+"\n------------------------------------------------------------------");
		
		return Json.encodePrettily(dtoMsg);	
		
  }
  
}
