package com.grcp.aws.project_02.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.grcp.aws.project_02.enums.EventType;
import java.util.Optional;
import org.springframework.data.annotation.Id;

@DynamoDBTable(tableName = "product-events")
public class ProductEventLog {

    public ProductEventLog() {
    }

    @Id
    private ProductEventKey productEventKey;

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "eventType")
    private EventType eventType;

    @DynamoDBAttribute(attributeName = "productId")
    private long productId;

    @DynamoDBAttribute(attributeName = "username")
    private String username;

    @DynamoDBAttribute(attributeName = "timestamp")
    private long timestamp;

    @DynamoDBAttribute(attributeName = "ttl")
    private long ttl;

    @DynamoDBAttribute(attributeName = "messageId")
    private String messageId;

    @DynamoDBHashKey(attributeName = "pk")
    public String getPk() {
        return Optional.ofNullable(this.productEventKey)
                .map(p -> p.getPk())
                .orElseThrow(() -> new IllegalStateException("There is no product event created."));
    }

    public void setPk(String pk) {
        if (Optional.ofNullable(this.productEventKey).isEmpty()) {
            ProductEventKey productEventKeyToSet = new ProductEventKey();
            this.productEventKey = productEventKeyToSet;
        }

        this.productEventKey.setPk(pk);
    }

    @DynamoDBRangeKey(attributeName = "sk")
    public String getSk() {
        return Optional.ofNullable(this.productEventKey)
                .map(s -> s.getSk())
                .orElseThrow(() -> new IllegalStateException("There is no product event created."));
    }

    public void setSk(String sk) {
        if (Optional.ofNullable(this.productEventKey).isEmpty()) {
            ProductEventKey productEventKeyToSet = new ProductEventKey();
            this.productEventKey = productEventKeyToSet;
        }

        this.productEventKey.setSk(sk);
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
