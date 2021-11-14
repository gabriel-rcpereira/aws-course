package com.grcp.aws.project_01.consumer;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grcp.aws.project_01.entity.Invoice;
import com.grcp.aws.project_01.model.SnsMessage;
import com.grcp.aws.project_01.repository.InvoiceRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class InvoiceConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceConsumer.class);

    private final ObjectMapper objectMapper;
    private final InvoiceRepository invoiceRepository;
    private final AmazonS3 amazonS3;

    public InvoiceConsumer(ObjectMapper objectMapper,
                           InvoiceRepository invoiceRepository,
                           AmazonS3 amazonS3) {
        this.objectMapper = objectMapper;
        this.invoiceRepository = invoiceRepository;
        this.amazonS3 = amazonS3;
    }

    @JmsListener(destination = "${aws.sqs.queue.invoice.events.name}")
    public void receiveS3Event(TextMessage textMessage) throws JMSException, IOException {
        SnsMessage snsMessage = this.objectMapper.readValue(textMessage.getText(), SnsMessage.class);
        S3EventNotification s3EventNotification = this.objectMapper.readValue(snsMessage.getMessage(), S3EventNotification.class);
        processInvoiceNotification(s3EventNotification);
    }

    private void processInvoiceNotification(S3EventNotification s3EventNotification) throws IOException {
        for (S3EventNotification.S3EventNotificationRecord s3EventNotificationRecord : s3EventNotification.getRecords()) {
            S3EventNotification.S3Entity s3Entity = s3EventNotificationRecord.getS3();

            String name = s3Entity.getBucket().getName();
            String key = s3Entity.getObject().getKey();

            String invoiceFile = downloadInvoice(name, key);

            Invoice invoice = this.objectMapper.readValue(invoiceFile, Invoice.class);
            LOGGER.info("Invoice received: {}", invoice.getInvoiceNumber());

            this.invoiceRepository.save(invoice);

            this.amazonS3.deleteObject(name, key);
        }
    }

    private String downloadInvoice(String name, String key) throws IOException {
        S3Object s3Object = this.amazonS3.getObject(name, key);

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));

        String content;
        while ((content = bufferedReader.readLine()) != null) {
            stringBuilder.append(content);
        }

        return stringBuilder.toString();
    }
}
