package com.example.paymentmodule.queue;

import com.example.paymentmodule.dto.OrderDto;
import com.example.paymentmodule.service.WalletService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static com.example.paymentmodule.queue.Config.QUEUE_PAYMENT;

@Component
public class ReceiveMessage {
    @Autowired
    ConsumerService consumerService;

    @RabbitListener(queues = {QUEUE_PAYMENT})
    public void getInfoOrder(OrderDto orderDto){
        System.out.println("Đang nhận message và chờ xử lý");
        consumerService.handlePayment(orderDto);
        System.out.println("đã nhận order và xử lý");
    }
}
