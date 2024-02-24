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

@OpenAPIDefinition(servers = {@Server(url = "/spacelab", description = "Default Server URL (w/ https??)")})
@SpringBootApplication
@ComponentScan( basePackages = {"com.example.spacelab", "com.example.spacelab.controller"})
@Slf4j
public class SpacelabApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(SpacelabApplication.class, args);
		CourseController cc = ctx.getBean(CourseController.class);
		AdminController ac = ctx.getBean(AdminController.class);
	}

}
