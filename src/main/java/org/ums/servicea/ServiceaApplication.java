package org.ums.servicea;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.RequestEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.stream.IntStream;

@SpringBootApplication
@RestController("/")
public class ServiceaApplication {

  @Autowired
  private KafkaTemplate<String, String> template;

    @GetMapping("/")
    public String getHome(RequestEntity requestEntity){
        return "Server A";
    }

    //@PostConstruct
  public void testSynchronizeRequestCall(){
    for(int i=1;i<=100; i++){
      RestTemplate restTemplate = new RestTemplate();
      long startTime = System.nanoTime();
      Boolean status = restTemplate.getForObject("http://localhost:8082/service-b?number="+i, Boolean.class);
      if(status){
        System.out.print("Call--->"+i+" & time taken: "+((System.nanoTime()-startTime)/1000000)/1000 +"\n");
      }
    }
  }

  //@PostConstruct
  public void parallelCall(){
  System.out.println("In the parallel call");
    IntStream.range(0,100).parallel().forEach(i->{
      RestTemplate restTemplate = new RestTemplate();
      long startTime = System.nanoTime();
      Boolean status = restTemplate.getForObject("http://localhost:8082/service-b/parallel?number="+i, Boolean.class);
      if(status){
        System.out.print("Call--->"+i+" & time taken: "+((System.nanoTime()-startTime)/1000000)/1000 +"\n");
      }
    });
  }

  public void parallelMessageQCall() {
    IntStream.range(0,100).sequential().forEach(i->{
      ServiceStatus serviceStatus = new ServiceStatus();
      serviceStatus.setServiceId("serviceA"+i);
      serviceStatus.setParentServiceId(null);
      serviceStatus.setStatus(false);
      ObjectMapper mapper = new ObjectMapper();
      String jsonToString="";
      long startTime = System.nanoTime();
      try{
        jsonToString = mapper.writeValueAsString(serviceStatus);
        this.template.send("service-b", i+"", jsonToString);
       /* this.template.send("my_topic", "serviceB", jsonToString);
    this.template.send("tracker", "serviceA", jsonToString);*/
        this.template.send("tracker", "serviceA", jsonToString);
        if(getSuccessResponse()==false)
          throw new NullPointerException();
      }catch (Exception e){
        e.printStackTrace();
      }

      System.out.print("Call--->"+i+" & time taken: "+((System.nanoTime()-startTime)/1000000)/1000 +"\n");


    });
  }


  private boolean getSuccessResponse() throws InterruptedException {
    Boolean serviceExecutionStatus = false;
    RestTemplate restTemplate = new RestTemplate();
    for (int i = 0; i < 15; i++) {
      serviceExecutionStatus = restTemplate.getForObject("http://localhost:8099/service?service-name=serviceA&service-number=1", Boolean.class);
      if (serviceExecutionStatus)
        break;
      Thread.sleep(500);
    }
    return serviceExecutionStatus;
  }


	public static void main(String[] args) {
		SpringApplication.run(ServiceaApplication.class, args);
	}
}
