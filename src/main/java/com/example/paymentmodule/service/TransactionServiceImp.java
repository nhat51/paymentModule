package com.example.paymentmodule.service;

import com.example.paymentmodule.entity.TransactionHistory;
import com.example.paymentmodule.repository.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImp implements TransactionService{
    @Autowired
    TransactionHistoryRepository historyRepository;

    @Override
    public List<TransactionHistory> getAll() {
        return historyRepository.findAll();
    }
}
