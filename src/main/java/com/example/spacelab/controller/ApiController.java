package com.example.spacelab.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Hidden
@Controller
public class ApiController {

    @GetMapping
    public String swaggerRedirect() {
        return "redirect:/swagger-ui/index.html";
    }
}
