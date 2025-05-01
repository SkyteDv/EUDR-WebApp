package com.projects.eudrwebapp.controller.supplier;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("supplier/deliveries/details")
public class COrderDetailsController {


    @GetMapping
    public String details(Model model) {
        return "s-order-details";
    }

}
