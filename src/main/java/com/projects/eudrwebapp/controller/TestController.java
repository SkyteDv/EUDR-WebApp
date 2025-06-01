package com.projects.eudrwebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String test() {
        return "test";
    }

    @GetMapping("/1")
    public String test1() {
        return "test";
    }

    @GetMapping("/2")
    public String test2() {
        return "test2";
    }

}
