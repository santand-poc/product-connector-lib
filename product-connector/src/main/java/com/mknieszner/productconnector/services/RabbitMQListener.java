package com.mknieszner.productconnector.services;

import com.mknieszner.productconnector.common.ProductDto;
import com.mknieszner.productconnector.common.ProductEvent;
import com.mknieszner.productconnector.config.AsyncProductMessageReaderLeaderTask;
import com.mknieszner.productconnector.config.LeaderElectionService;
import com.mknieszner.productconnector.services.cases.CaseStatusService;
import com.mknieszner.productconnector.services.products.ProductRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class RabbitMQListener {
    private final ProductRepository productRepository;
    private final LeaderElectionService leaderElectionService;
    private final CaseStatusService caseStatusService;
    private final ApplicationEventPublisher eventPublisher;
    private static final int MAX_DLQ_RETRIES = 5;


    @RabbitListener(queues = "#{productQueueName}", containerFactory = "productListenerContainerFactory")
    @SneakyThrows
    @Transactional
    public void onMessage(ProductDto message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (!leaderElectionService.isCurrentInstanceLeader(AsyncProductMessageReaderLeaderTask.TASK_ID)) {
            System.out.println("Instance is NOT leader. Skipping message: " + message);
            channel.basicReject(deliveryTag, true);
            return;
        }

        if (caseStatusService.isCasePaused(message.getCaseId())) {
            System.out.println("Case " + message.getCaseId() + " is paused. Skipping message.");
            channel.basicReject(deliveryTag, true);
            return;
        }

        try {
            processMessage(message);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            handleException(message, e, channel, deliveryTag, "productQueue");
        }
    }

    @RabbitListener(queues = "#{dlqProductQueueName}")
    @Transactional
    @SneakyThrows
    public void onDlqMessage(ProductDto message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, @Header(name = "x-death", required = false) List<Map<String, Object>> xDeath) {
        if (hasExceededMaxRetries(xDeath)) {
            handleExceededRetries(message, channel, deliveryTag);
            return;
        }

        try {
            System.out.println("Processing DLQ message for case: " + message.getCaseId());
            processMessage(message);
            caseStatusService.resumeCase(message.getCaseId());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            handleException(message, e, channel, deliveryTag, "DLQ productQueue");
        }
    }

    private void processMessage(ProductDto message) {
        System.out.println("Processing message: " + message);
        productRepository.persist(message);
        eventPublisher.publishEvent(ProductEvent.builder()
                .productId(message.getId())
                .caseId(message.getCaseId())
                .message(message)
                .status("PROCESSED")
                .build()
        );
    }

    private void handleException(ProductDto message,
                                 Exception exception,
                                 Channel channel,
                                 @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                 String queue) {
        try {
            System.err.println("Failed to process message from: " + queue + " with message: " + exception.getMessage());
            eventPublisher.publishEvent(ProductEvent.builder()
                    .productId(message.getId())
                    .caseId(message.getCaseId())
                    .message(message)
                    .status("ERROR")
                    .build()
            );
            caseStatusService.pauseCase(message.getCaseId());
            channel.basicReject(deliveryTag, false);
        } catch (IOException ioException) {
            System.err.println("Failed to reject message: from: " + queue + " with message: " + ioException.getMessage());
        }
    }

    private boolean hasExceededMaxRetries(List<Map<String, Object>> xDeath) {
        if (xDeath == null || xDeath.isEmpty()) {
            return false;
        }

        return xDeath.stream().anyMatch(deathInfo -> {
                    Long count = (Long) deathInfo.get("count");
                    return count != null && count >= MAX_DLQ_RETRIES;
                });
    }

    private void handleExceededRetries(ProductDto message, Channel channel, long deliveryTag) throws IOException {
        System.err.println("Message exceeded max retries in DLQ. " + message);
        channel.basicAck(deliveryTag, false);
    }
}
