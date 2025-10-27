package com.sunghoonkim0.jwt_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/protected")
    public Map<String, String> protectedEndpoint() {
        return Map.of("message", "protected data access granted");
    }
}
