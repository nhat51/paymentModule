package com.example.paymentmodule.queue;

import com.example.paymentmodule.dto.OrderDto;
import com.example.paymentmodule.entity.TransactionHistory;
import com.example.paymentmodule.entity.Wallet;
import com.example.paymentmodule.enums.PaymentType;
import com.example.paymentmodule.enums.Status;
import com.example.paymentmodule.repository.TransactionHistoryRepository;
import com.example.paymentmodule.repository.WalletRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static com.example.paymentmodule.queue.Config.DIRECT_EXCHANGE;
import static com.example.paymentmodule.queue.Config.DIRECT_ROUTING_KEY_ORDER;

@Component
public class ConsumerService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    /*
     * Xử lí payment qua orderDto đc gửi lên từ OrderService và gửi PaymentDto lên queue
     * */
    public void handlePayment(OrderDto orderDto) {
        System.out.println(orderDto.getPaymentStatus());
        if (orderDto.getPaymentStatus().equals(Status.PaymentStatus.PENDING.name())){
            handlePending(orderDto);
            return;
        }
        if (orderDto.getPaymentStatus().equals(Status.PaymentStatus.REFUND.name())){
            handleRefund(orderDto);
            return;
        }
    }
    /*
     * Được gọi đến khi order có status là pending
     * */
    public void handlePending(OrderDto orderDto){
        Wallet wallet = checkWallet(orderDto);
        if (wallet == null){
            return;
        }
        double totalPrice = orderDto.getTotalPrice();
        double balance = wallet.getBalance();

        TransactionHistory history = new TransactionHistory();
        history.setPayment_type(PaymentType.TRANSFER.name());
        history.setSender_id(orderDto.getCustomerId());
        history.setReceive_id(1);
        history.setAmount(orderDto.getTotalPrice());
        history.setOrder_id(orderDto.getOrderId());
        history.setCreated_at(LocalDate.now());

        if(totalPrice > balance){
            orderDto.setPaymentStatus(Status.PaymentStatus.UNPAID.name());
            orderDto.setMessage(Status.OrderMessage.NOT_ENOUGH_BALANCE);
            orderDto.setOrderStatus(Status.OrderStatus.REJECT.name());

            history.setMessage("Balance not enough");
            history.setStatus(Status.TransactionStatus.FAIL.name());
            transactionHistoryRepository.save(history);
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_ORDER,orderDto);
            System.out.println("dto: " + orderDto);
            return;
        }

        try{
            wallet.setBalance(balance - totalPrice);
            walletRepository.save(wallet);

            history.setStatus(Status.TransactionStatus.SUCCESS.name());
            history.setMessage("Pay for order: " + orderDto.getOrderId());
            transactionHistoryRepository.save(history);

            orderDto.setPaymentStatus(Status.PaymentStatus.PAID.name());
            orderDto.setOrderStatus(Status.OrderStatus.CONFIRM.name());

            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_ORDER,orderDto);
        }catch (Exception e){
            history.setStatus(Status.TransactionStatus.FAIL.name());
            transactionHistoryRepository.save(history);
//                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_PAY,orderDto);
        }

    }

    /*
     * Được gọi đến khi số lượng product trong inventory không đủ và phải hoàn tiền đã trừ trước đó
     * Thông tin sẽ được lưu vào transaction history với payment status là Refunded
     * Order sẽ được lưu với order status là refund
     * */
    public void handleRefund(OrderDto orderDto){
        TransactionHistory transactionHistory = new TransactionHistory();
        Wallet wallet = checkWallet(orderDto);
        transactionHistory.setAmount(orderDto.getTotalPrice());
        transactionHistory.setSender_id(orderDto.getCustomerId());
        transactionHistory.setReceive_id(1);
        transactionHistory.setMessage("Refund for order" + orderDto.getOrderId());
        transactionHistory.setOrder_id(orderDto.getOrderId());
        transactionHistory.setPayment_type(PaymentType.REFUND.name());

        try{
            wallet.setBalance(wallet.getBalance() + orderDto.getTotalPrice());
            walletRepository.save(wallet);
            transactionHistoryRepository.save(transactionHistory);
            orderDto.setOrderStatus(Status.OrderStatus.DONE.name());
            orderDto.setPaymentStatus(Status.PaymentStatus.REFUNDED.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_ORDER,orderDto);
        }catch (Exception e){
            transactionHistory.setStatus(Status.TransactionStatus.FAIL.name());
            transactionHistoryRepository.save(transactionHistory);
//                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_PAY, orderDto);
        }
    }
    /*
     * Kiểm tra wallet theo user id có hợp lệ hay không
     * Nếu không hợp lệ thì set Status của payment thành UNPAID rồi gửi lên queue
     * */
    private Wallet checkWallet(OrderDto orderDto){
        if (orderDto.getCustomerId() == 0){
            orderDto.setOrderStatus(Status.OrderStatus.REJECT.name());
            orderDto.setMessage(Status.OrderMessage.NOT_FOUND_USER);
            orderDto.setPaymentStatus(Status.PaymentStatus.UNPAID.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_ORDER,orderDto);
            return null;
        }
        Wallet wallet = walletRepository.findWalletByUserId(orderDto.getCustomerId());

        if (wallet == null){
            orderDto.setOrderStatus(Status.OrderStatus.REJECT.name());
            orderDto.setMessage(Status.OrderMessage.NOT_FOUND_WALLET);
            orderDto.setPaymentStatus(Status.PaymentStatus.UNPAID.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,DIRECT_ROUTING_KEY_ORDER,orderDto);
            return null;
        }
        return wallet;
    }
}
