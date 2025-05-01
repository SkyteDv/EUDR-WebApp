package com.projects.eudrwebapp.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class HelperService {

    OrderService orderService;

    public HelperService(OrderService orderService) {
        this.orderService = orderService;
    }

    public InputStream getInputStream(String location) throws IOException {
        ClassPathResource resource = new ClassPathResource(location);
        InputStream inputStream = resource.getInputStream();
        return inputStream;
    }

    public void updateDeliveries(String fileLocation, String osapiensId, String userType) throws Exception {
        InputStream inputStream = getInputStream(fileLocation);
        orderService.importOrders(inputStream, osapiensId, userType);
    }

}
