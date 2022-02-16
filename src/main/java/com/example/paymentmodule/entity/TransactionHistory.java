package com.example.paymentmodule.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction_histories")
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer sender_id;
    private Integer receive_id;
    private Integer order_id;
    private String payment_type;
    private double amount;
    private String status;
    private String message;
    private LocalDate created_at;
    private LocalDate updated_at;
}
