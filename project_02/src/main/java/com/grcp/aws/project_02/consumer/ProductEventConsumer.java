package com.grcp.aws.project_02.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grcp.aws.project_02.model.EventWrapper;
import com.grcp.aws.project_02.model.ProductEvent;
import com.grcp.aws.project_02.model.SnsMessage;
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

    public ProductEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiveProductEvent(TextMessage textMessage) throws JMSException, JsonProcessingException {
        SnsMessage message = this.objectMapper.readValue(textMessage.getText(), SnsMessage.class);
        EventWrapper eventWrapper = this.objectMapper.readValue(message.getMessage(), EventWrapper.class);
        ProductEvent productEvent = this.objectMapper.readValue(eventWrapper.getData(), ProductEvent.class);

        LOGGER.info("Product event received - Event: {} - ProductId: {} - MessageId: {}",
                message.getType(),
                productEvent.getProductId(),
                message.getMessageId());
    }
}
