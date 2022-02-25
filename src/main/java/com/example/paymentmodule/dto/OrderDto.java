package com.example.paymentmodule.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {
    private int orderId;
    private int customerId;
    private double totalPrice;
    private String paymentStatus;
    private String inventoryStatus;
    private String orderStatus;
    private String message;
    private Set<OrderDetailDTO> orderDetails = new HashSet<>();

}
