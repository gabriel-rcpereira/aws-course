package com.grcp.aws.project_02.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grcp.aws.project_02.model.EventWrapper;
import com.grcp.aws.project_02.model.ProductEvent;
import com.grcp.aws.project_02.model.ProductEventKey;
import com.grcp.aws.project_02.model.ProductEventLog;
import com.grcp.aws.project_02.model.SnsMessage;
import com.grcp.aws.project_02.repository.ProductEventRepository;
import java.time.Duration;
import java.time.Instant;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final ProductEventRepository productEventRepository;

    public ProductEventConsumer(ObjectMapper objectMapper,
                                ProductEventRepository productEventRepository) {
        this.objectMapper = objectMapper;
        this.productEventRepository = productEventRepository;
    }

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiveProductEvent(TextMessage textMessage) throws JMSException, JsonProcessingException {
        SnsMessage message = this.objectMapper.readValue(textMessage.getText(), SnsMessage.class);
        EventWrapper eventWrapper = this.objectMapper.readValue(message.getMessage(), EventWrapper.class);
        ProductEvent productEvent = this.objectMapper.readValue(eventWrapper.getData(), ProductEvent.class);

        LOGGER.info("Product event received - Event: {} - ProductId: {} - MessageId: {}",
                eventWrapper.getEventType(),
                productEvent.getProductId(),
                message.getMessageId());

        createProductEventLog(new ProductEventAndMessage(message, eventWrapper, productEvent));
    }

    private void createProductEventLog(ProductEventAndMessage productEventAndMessage) {
        ProductEventLog productEventLog = buildProductEventLog(productEventAndMessage);
        this.productEventRepository.save(productEventLog);

        LOGGER.info("Product event log created - Event: {} - ProductId: {} - MessageId: {}",
                productEventAndMessage.eventWrapper.getEventType(),
                productEventAndMessage.productEvent.getProductId(),
                productEventAndMessage.message.getMessageId());
    }

    private ProductEventLog buildProductEventLog(ProductEventAndMessage productEventAndMessage) {
        long nowTimestamp = Instant.now().toEpochMilli();

        ProductEventLog productEventLog = new ProductEventLog();
        productEventLog.setPk(productEventAndMessage.productEvent.getCode());
        productEventLog.setSk(ProductEventKey.generateSk(productEventAndMessage.eventWrapper.getEventType().name(), nowTimestamp));
        productEventLog.setEventType(productEventAndMessage.eventWrapper.getEventType());
        productEventLog.setProductId(productEventAndMessage.productEvent.getProductId());
        productEventLog.setUsername(productEventAndMessage.productEvent.getUsername());
        productEventLog.setTimestamp(nowTimestamp);
        productEventLog.setTtl(Instant.ofEpochMilli(nowTimestamp)
                .plus(Duration.ofMinutes(10))
                .toEpochMilli());
        productEventLog.setMessageId(productEventAndMessage.message.getMessageId());

        return productEventLog;
    }

    record ProductEventAndMessage(
            SnsMessage message,
            EventWrapper eventWrapper,
            ProductEvent productEvent
    ) { }
}
