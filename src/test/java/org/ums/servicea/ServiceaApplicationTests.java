package org.ums.servicea;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceaApplicationTests {

  @Autowired
  private KafkaTemplate<String, String> template;

  @Test
  public void contextLoads() {
  }

  @Test
  public void testSynchronizeRequestCall(){
    for(int i=1;i<=100; i++){
      RestTemplate restTemplate = new RestTemplate();
      long startTime = System.nanoTime();
      Boolean status = restTemplate.getForObject("http://localhost:8082/service-b?number="+i, Boolean.class);
      if(status){
        System.out.print("Call--->"+i+" & time taken: "+(System.nanoTime()-startTime));
      }
    }
  }

  @Test
  public void testKafkaMessage() throws Exception {
    //this.template.send("my_topic", "key2", "message_to_serviceB");
    for(int i=1;i<=3;i++){
      ServiceStatus serviceStatus = new ServiceStatus();
      serviceStatus.setServiceId("serviceA"+i);
      serviceStatus.setParentServiceId(null);
      serviceStatus.setStatus(false);
      ObjectMapper mapper = new ObjectMapper();
      String jsonToString = mapper.writeValueAsString(serviceStatus);
      this.template.send("service-b", ""+i, jsonToString);
       /* this.template.send("my_topic", "serviceB", jsonToString);
    this.template.send("tracker", "serviceA", jsonToString);*/
      this.template.send("tracker", "serviceA", jsonToString);
      assertThat(getSuccessResponse(serviceStatus.getServiceId())).isEqualTo(true);
    }

  }

  private boolean getSuccessResponse(String serviceName) throws InterruptedException {
    Boolean serviceExecutionStatus = false;
    RestTemplate restTemplate = new RestTemplate();
    for (int i = 0; i < 50; i++) {
      serviceExecutionStatus = restTemplate.getForObject("http://localhost:8099/service?service-name="+serviceName+"&service-number=1", Boolean.class);
      if (serviceExecutionStatus)
        break;
      Thread.sleep(500);
    }
    return serviceExecutionStatus;
  }


}
