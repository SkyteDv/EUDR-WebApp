package com.projects.eudrwebapp.service;

import org.springframework.stereotype.Service;

@Service
public class URLService {

    public String goHome() {
        System.out.println("Going to Home");
        return "redirect:/";
    }

}
