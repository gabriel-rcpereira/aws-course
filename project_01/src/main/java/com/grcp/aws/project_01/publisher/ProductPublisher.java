package com.grcp.aws.project_01.publisher;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grcp.aws.project_01.entity.Product;
import com.grcp.aws.project_01.enums.EventType;
import com.grcp.aws.project_01.model.EventWrapper;
import com.grcp.aws.project_01.model.ProductEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProductPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductPublisher.class);

    private static final String FAILED_TO_CREATE_EVENT_WRAPPER_MESSAGE = "Failed to create event wrapper message.";
    private static final String FAILED_TO_CREATE_PRODUCT_EVENT_MESSAGE = "Failed to create product event message.";

    private final AmazonSNS snsClient;
    private final Topic productEventTopic;
    private final ObjectMapper objectMapper;

    public ProductPublisher(AmazonSNS snsClient,
                            @Qualifier("productEventsTopic") Topic productEventTopic,
                            ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.productEventTopic = productEventTopic;
        this.objectMapper = objectMapper;
    }

    public void execute(Product product, EventType eventType, String username) {
        ProductEvent productEvent = new ProductEvent(product.getId(), product.getCode(), username);
        EventWrapper eventWrapper = new EventWrapper(eventType, convertToJson(productEvent));
        PublishResult published = snsClient.publish(this.productEventTopic.getTopicArn(), convertToJson(eventWrapper));
        LOGGER.info("Message sent. MessageId: {}", published.getMessageId());
    }

    private String convertToJson(EventWrapper eventWrapper) {
        try {
            return objectMapper.writeValueAsString(eventWrapper);
        } catch (JsonProcessingException e) {
            LOGGER.error(FAILED_TO_CREATE_EVENT_WRAPPER_MESSAGE,e);
            throw new RuntimeException(FAILED_TO_CREATE_EVENT_WRAPPER_MESSAGE);
        }
    }

    private String convertToJson(ProductEvent productEvent) {
        try {
            return this.objectMapper.writeValueAsString(productEvent);
        } catch (JsonProcessingException e) {
            LOGGER.error(FAILED_TO_CREATE_PRODUCT_EVENT_MESSAGE, e);
            throw new RuntimeException(FAILED_TO_CREATE_PRODUCT_EVENT_MESSAGE);
        }
    }
}
