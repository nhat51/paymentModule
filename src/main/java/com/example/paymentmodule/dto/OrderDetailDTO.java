package com.example.paymentmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    private int orderId;
    private int productId;
    private String productName;
    private double unitPrice;
    private int quantity;
}
