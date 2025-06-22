package com.haulmont.prob;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    @GetMapping("/number")
    public int getNumber() {
        return 42;
    }
}