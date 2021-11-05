package com.grcp.aws.project_02.model;

public record ProductEventLogDto (String code,
                                  String eventType,
                                  long productId,
                                  String username,
                                  long timestamp,
                                  String messageId) {

    public static ProductEventLogDto of(ProductEventLog productEventLog) {
        return new ProductEventLogDto(productEventLog.getPk(),
                productEventLog.getEventType().name(),
                productEventLog.getProductId(),
                productEventLog.getUsername(),
                productEventLog.getTimestamp(),
                productEventLog.getMessageId());
    }
}