package com.example.paymentmodule.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {
    private int orderId;
    private int customerId;
    private double totalPrice;
    private String paymentStatus;
    private String orderStatus;
}
