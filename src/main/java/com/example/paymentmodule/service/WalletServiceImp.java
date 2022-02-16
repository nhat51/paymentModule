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

import static com.example.paymentmodule.queue.Config.*;

@Service
public class WalletServiceImp implements WalletService{
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    /*
    * Xử lí payment qua orderDto đc gửi lên từ OrderService và gửi PaymentDto lên queue
    * */
    public void handlePayment(OrderDto orderDto) {
        PaymentDto paymentDto = new PaymentDto(orderDto.getOrderId(),orderDto.getCustomerId());

        if(orderDto.getPaymentStatus().equals(Status.PaymentStatus.PAID.name())){
            paymentDto.setMessage("Order already paid");
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_PAY,paymentDto);
        }
        Wallet checkWallet = checkWallet(paymentDto);

        if(checkWallet == null){
            return;
        }

        double totalPrice = orderDto.getTotalPrice();
        double balance = checkWallet.getBalance();

        if (totalPrice > balance){
            paymentDto.setPaymentStatus(Status.PaymentStatus.UNPAID.name());
            paymentDto.setMessage("Balance is not enough");
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_PAY,paymentDto);
        }

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setSender_id(orderDto.getCustomerId());
        transactionHistory.setReceive_id(1); //mặc định của hệ thống
        transactionHistory.setAmount(totalPrice);
        transactionHistory.setMessage("Pay for order" + orderDto.getOrderId());
        transactionHistory.setOrder_id(orderDto.getOrderId());
        transactionHistory.setPayment_type(PaymentType.TRANSFER.name());

        try{
            checkWallet.setBalance(balance - totalPrice);
            walletRepository.save(checkWallet);
            transactionHistory.setStatus(Status.TransactionStatus.SUCCESS.name());
            transactionHistoryRepository.save(transactionHistory);
            paymentDto.setMessage("Payment success");
            paymentDto.setPaymentStatus(Status.PaymentStatus.PAID.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_PAY,paymentDto);
        }catch (Exception e){
            transactionHistory.setStatus(Status.TransactionStatus.FAIL.name());
            transactionHistoryRepository.save(transactionHistory);
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_ORDER,orderDto);
        }
    }

        /*
        * Được gọi đến khi totalPrice của order lớn hơn balance trong ví
        * Thông tin sẽ được lưu vào transaction history với payment status là Refunded
        * Order sẽ được lưu với order status là refund
        * */
        public void handleRefund(OrderDto orderDto,PaymentDto paymentDto,Wallet wallet){
            TransactionHistory transactionHistory = new TransactionHistory();
            transactionHistory.setAmount(orderDto.getTotalPrice());
            transactionHistory.setSender_id(paymentDto.getUserId());
            transactionHistory.setReceive_id(1);
            transactionHistory.setMessage("Refund for order" + orderDto.getOrderId());
            transactionHistory.setOrder_id(orderDto.getOrderId());
            transactionHistory.setPayment_type(PaymentType.REFUND.name());

            try{
                wallet.setBalance(wallet.getBalance() + orderDto.getTotalPrice());
                walletRepository.save(wallet);
                transactionHistoryRepository.save(transactionHistory);
                orderDto.setOrderStatus(Status.OrderStatus.REFUND.name());
                orderDto.setPaymentStatus(Status.PaymentStatus.REFUNDED.name());
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_ORDER,orderDto);
            }catch (Exception e){

            }
        }

        /*
        * Kiểm tra wallet theo user id có hợp lệ hay không
        * Nếu không hợp lệ thì set Status của payment thành UNPAID rồi gửi lên queue
        * */
         private Wallet checkWallet(PaymentDto paymentDto){
            if (paymentDto.getUserId() == 0){
                paymentDto.setMessage("User id must not be null");
                paymentDto.setPaymentStatus(Status.PaymentStatus.UNPAID.name());
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_PAY,paymentDto);
                return null;
            }
            Wallet wallet = walletRepository.findWalletByUserId(paymentDto.getUserId());

            if (wallet == null){
                paymentDto.setMessage("Wallet not found");
                paymentDto.setPaymentStatus(Status.PaymentStatus.UNPAID.name());
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_PAY,paymentDto);
                return null;
            }
            return wallet;
        }
}
