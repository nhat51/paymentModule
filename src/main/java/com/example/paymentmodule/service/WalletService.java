package com.example.paymentmodule.service;

import com.example.paymentmodule.dto.OrderDto;

public interface WalletService {
    void handlePayment(OrderDto orderDto);
}
