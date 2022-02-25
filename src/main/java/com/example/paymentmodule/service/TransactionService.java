package com.example.paymentmodule.service;

import com.example.paymentmodule.entity.TransactionHistory;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionService {
    List<TransactionHistory> getAll();
}
