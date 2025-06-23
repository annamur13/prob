package com.haulmont.prob.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")  // Изменён путь
public class TestController {
    @PostMapping("/echo")
    public String test(@RequestBody String body) {
        return "Echo: " + body;
    }

    @GetMapping("/number")
    public int getNumber() {
        return 42;  // Просто возвращаем число
    }
}
