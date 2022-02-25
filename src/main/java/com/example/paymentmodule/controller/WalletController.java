package com.example.paymentmodule.controller;

import com.example.paymentmodule.dto.OrderDto;
import com.example.paymentmodule.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/payment")
@CrossOrigin("*")
public class WalletController {
    @Autowired
    WalletService service;



    @RequestMapping(method = RequestMethod.GET,path = "wallet")
    public ResponseEntity<?> getWallet(@RequestParam(name = "userId") int id){
        return ResponseEntity.ok().body(service.getWallet(id));
    }
}
