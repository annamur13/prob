package com.haulmont.prob.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  // или @Controller, если возвращаете HTML
public class SimpleController {

    @GetMapping("/")
    public String home() {
        return "Главная страница работает!";
    }
}