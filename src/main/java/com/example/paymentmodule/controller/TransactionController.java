package com.example.paymentmodule.controller;

import com.example.paymentmodule.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/transactionHistory")
@CrossOrigin("*")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllTransaction(){
        return ResponseEntity.ok().body(
                transactionService.getAll()
        );
    }
}
