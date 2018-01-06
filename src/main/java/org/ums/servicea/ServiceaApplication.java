package org.ums.servicea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController("/")
public class ServiceaApplication {

    @GetMapping("/")
    public String getHome(RequestEntity requestEntity){
        return "Server A";
    }

	public static void main(String[] args) {
		SpringApplication.run(ServiceaApplication.class, args);
	}
}
