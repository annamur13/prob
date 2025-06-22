package com.haulmont.prob.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")

public class TestController {
    @PostMapping
    public String test(@RequestBody String body) {
        return "Received: " + body;
    }
}
