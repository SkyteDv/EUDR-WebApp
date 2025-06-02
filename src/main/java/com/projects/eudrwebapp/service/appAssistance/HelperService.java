package com.projects.eudrwebapp.service.appAssistance;

import com.projects.eudrwebapp.service.OrderService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;

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

    public double roundToPercentage(double value, int decimalPlaces) {
        if (decimalPlaces < 0) throw new IllegalArgumentException("Decimal places must be non-negative.");

        double scale = Math.pow(10, decimalPlaces);
        return Math.round(value * 100 * scale) / scale; // multiply by 100 inside
    }


}
