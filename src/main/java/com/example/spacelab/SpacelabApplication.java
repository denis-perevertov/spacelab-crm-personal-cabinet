package com.example.spacelab;

import com.example.spacelab.controller.AdminController;
import com.example.spacelab.controller.CourseController;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@OpenAPIDefinition(
		servers = {
				@Server(url = "https://slj.avada-media-dev2.od.ua/spacelab/cabinet", description = "Avada Deploy"),
				@Server(url = "www.denis-perevertov.com/spacelab/cabinet", description = "AWS Deploy"),
				@Server(url = "http://localhost:1488/spacelab/cabinet", description = "Local")
		}
)
@SpringBootApplication
public class SpacelabApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpacelabApplication.class, args);
	}

}
