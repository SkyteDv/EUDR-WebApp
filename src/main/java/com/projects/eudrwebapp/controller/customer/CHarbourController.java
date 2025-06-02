package com.projects.eudrwebapp.controller.customer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("customer/harbour/")
public class CHarbourController {

    @GetMapping("overview")
    public String harbour() {
        return "c-manage-harbours";
    }

}
