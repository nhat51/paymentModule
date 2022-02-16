package com.example.paymentmodule.repository;

import com.example.paymentmodule.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findWalletByUserId(int id);
}
