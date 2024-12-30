package com.mknieszner.productmanager.service;


import com.mknieszner.productmanager.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductEventPublisher {
    @Value("${rabbitmq.exchange}")
    private String exchange;
    private final RabbitTemplate rabbitTemplate;

    public void publishProductEvent(Product product) {
        rabbitTemplate.convertAndSend(exchange, "", product);
    }
}
