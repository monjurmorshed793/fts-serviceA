package org.ums.servicea;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

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
    public void testKafkaMessage() throws Exception{
	    //this.template.send("my_topic", "key2", "message_to_serviceB");
        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setServiceId("serviceA");
        serviceStatus.setParentServiceId(null);
        serviceStatus.setStatus(false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonToString=mapper.writeValueAsString(serviceStatus);
        this.template.send("my_topic", "serviceB", jsonToString);
        /*this.template.send("tracker", "serviceA", jsonToString);
        assertThat(getSuccessResponse()).isEqualTo(true);*/
    }

    private boolean getSuccessResponse() throws InterruptedException {
        Boolean serviceExecutionStatus=false;
        RestTemplate restTemplate = new RestTemplate();
        for(int i=0; i<15; i++){
            serviceExecutionStatus=restTemplate.getForObject("http://localhost:8099/service?service-name=serviceA&service-number=1", Boolean.class);
            if(serviceExecutionStatus)
                break;
            Thread.sleep(500);
        }
        return serviceExecutionStatus;
    }


}
