package com.grcp.aws.project_01.controller;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.grcp.aws.project_01.entity.Invoice;
import com.grcp.aws.project_01.model.UrlResponse;
import com.grcp.aws.project_01.repository.InvoiceRepository;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final String bucketName;
    private final AmazonS3 amazonS3;
    private final InvoiceRepository invoiceRepository;

    public InvoiceController(@Value("${aws.s3.bucket.invoice.name}") String bucketName,
                             AmazonS3 amazonS3,
                             InvoiceRepository invoiceRepository) {
        this.bucketName = bucketName;
        this.amazonS3 = amazonS3;
        this.invoiceRepository = invoiceRepository;
    }

    @PostMapping
    public ResponseEntity<UrlResponse> postCreatingUrl() {
        UrlResponse urlResponse = createUrl();
        return ResponseEntity.ok(urlResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<Invoice>> getAll() {
        Iterable<Invoice> invoices = this.invoiceRepository.findAll();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{invoiceNumber}")
    public ResponseEntity<?> getByInvoiceNumber(
            @PathVariable("invoiceNumber") String invoiceNumber) {
        return this.invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .map(invoice -> new ResponseEntity(invoice, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private UrlResponse createUrl() {
        Instant expirationTime = Instant.now().plus(Duration.ofMinutes(5));
        String generatedPresignedUrlRequest = generatePresignedUrlRequest(expirationTime);
        return new UrlResponse(generatedPresignedUrlRequest, expirationTime.getEpochSecond());
    }

    private String generatePresignedUrlRequest(Instant expirationTime) {
        String processId = UUID.randomUUID().toString();
        GeneratePresignedUrlRequest generatedPresignedUrlRequest = new GeneratePresignedUrlRequest(this.bucketName, processId)
                .withMethod(HttpMethod.PUT)
                .withExpiration(Date.from(expirationTime));

        return this.amazonS3.generatePresignedUrl(generatedPresignedUrlRequest).toString();
    }
}
