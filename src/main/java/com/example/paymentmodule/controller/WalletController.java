package com.example.paymentmodule.controller;

import com.example.paymentmodule.dto.OrderDto;
import com.example.paymentmodule.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/payment")
public class WalletController {
    @Autowired
    WalletService service;

    public void handlePayment(OrderDto orderDto){
        service.handlePayment(orderDto);
    }
}
