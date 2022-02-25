package com.example.paymentmodule.service;

import com.example.paymentmodule.dto.OrderDto;
import com.example.paymentmodule.entity.Wallet;

public interface WalletService {
    public Wallet getWallet(int id);
}
