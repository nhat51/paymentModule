package com.example.paymentmodule.service;

import com.example.paymentmodule.dto.OrderDto;
import com.example.paymentmodule.dto.PaymentDto;
import com.example.paymentmodule.entity.TransactionHistory;
import com.example.paymentmodule.entity.Wallet;
import com.example.paymentmodule.enums.PaymentType;
import com.example.paymentmodule.enums.Status;
import com.example.paymentmodule.repository.TransactionHistoryRepository;
import com.example.paymentmodule.repository.WalletRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDate;

import static com.example.paymentmodule.queue.Config.*;

@Service
public class WalletServiceImp implements WalletService{
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Wallet getWallet(int id){
        return walletRepository.findWalletByUserId(id);
    }


}
