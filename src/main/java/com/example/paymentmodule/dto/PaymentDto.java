package com.example.paymentmodule.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentDto {
    private int orderId;
    private int userId;
    private String paymentStatus;
    private String message;

    public PaymentDto(int orderId,int userId){
        this.orderId = orderId;
        this.userId = userId;
    }
}
