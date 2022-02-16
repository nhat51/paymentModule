package com.example.paymentmodule.queue;

import com.example.paymentmodule.dto.OrderDto;
import com.example.paymentmodule.service.WalletService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static com.example.paymentmodule.queue.Config.QUEUE_ORDER;

@Component
public class ReceiveMessage {
    @Autowired
    WalletService walletService;

    @RabbitListener(queues = {QUEUE_ORDER})
    public void getInfoOrder(OrderDto orderDto){
        walletService.handlePayment(orderDto);
        System.out.println(orderDto.toString());
    }
}
