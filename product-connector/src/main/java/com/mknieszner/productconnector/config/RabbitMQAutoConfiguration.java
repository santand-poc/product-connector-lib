package com.mknieszner.productconnector.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class RabbitMQAutoConfiguration {
    @Value("${rabbitmq.exchange:product-events-fanout}")
    private String exchangeName;
    @Value("${rabbitmq.queue}")
    private String queueName;

    // PRODUCT QUEUE
    @Bean
    public FanoutExchange productEventsExchange() {
        return new FanoutExchange(exchangeName, true, false);
    }

    @Bean
    public Queue productQueue() {
        return QueueBuilder.durable(productQueueName())
                .withArgument("x-dead-letter-exchange", dlxExchangeName())
//                .withArgument("x-message-ttl", 10_000)
                .build();
    }

    @Bean
    public Binding binding(Queue productQueue, FanoutExchange productEventsExchange) {
        return BindingBuilder.bind(productQueue).to(productEventsExchange);
    }

    // PRODUCT DLQ QUEUE
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(dlxExchangeName());
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(dlqProductQueueName()).build();
    }

    @Bean
    public Binding dlqBinding(Queue dlqQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlqQueue).to(dlxExchange).with("");
    }

    @Bean
    public SimpleRabbitListenerContainerFactory productListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setPrefetchCount(1);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(5)
                        .backOffOptions(1_000, 2, 10_000)
                        .recoverer((message, cause) -> {
                            System.err.println("Failed to process message: " + new String(message.getBody()));
                            System.err.println("Cause: " + cause.getMessage());
                            System.err.println("Stack: " + Arrays.toString(cause.getStackTrace()));
                        })
                        .build()
        );
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public String dlxExchangeName() {
        return "dlx-exchange-" + productQueueName();
    }

    @Bean
    public String dlqProductQueueName() {
        return "dlq-queue-" + productQueueName();
    }

    @Bean
    public String productQueueName() {
        return queueName;
    }
}
