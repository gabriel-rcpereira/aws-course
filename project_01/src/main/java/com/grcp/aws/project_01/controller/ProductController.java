package com.grcp.aws.project_01.controller;

import com.grcp.aws.project_01.entity.Product;
import com.grcp.aws.project_01.enums.EventType;
import com.grcp.aws.project_01.publisher.ProductPublisher;
import com.grcp.aws.project_01.repository.ProductRepository;
import java.net.URI;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductPublisher productPublisher;

    public ProductController(ProductRepository productRepository,
                             ProductPublisher productPublisher) {
        this.productRepository = productRepository;
        this.productPublisher = productPublisher;
    }

    @GetMapping("/api/products")
    public ResponseEntity<Iterable<Product>> getAll() {
        return ResponseEntity.ok(this.productRepository.findAll());
    }

    @GetMapping("/api/products/{code}")
    public ResponseEntity<Product> getById(@PathVariable("id") String code) {
        return this.productRepository.findByCode(code)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound()
                        .build());
    }

    @PostMapping("/api/products")
    public ResponseEntity<Void> create(@RequestBody Product product) {
        Product createdProduct = this.productRepository.save(product);
        this.productPublisher.execute(createdProduct, EventType.CREATED, "user01");

        URI newProductUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProduct.getId())
                .toUri();

        return ResponseEntity.created(newProductUri)
                .build();
    }

    @PutMapping("/api/products/{code}")
    public ResponseEntity<Void> update(@RequestBody Product updatedProduct,
                                       @PathVariable("code") String code) {
        Optional<Product> foundProductOpt = this.productRepository.findByCode(code);

        if (foundProductOpt.isEmpty()) {
            return ResponseEntity.notFound()
                    .build();
        }

        updatedProduct.setId(foundProductOpt.get().getId());
        this.productRepository.save(updatedProduct);
        this.productPublisher.execute(updatedProduct, EventType.UPDATED, "user02");

        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/api/products/{code}")
    public ResponseEntity<Void> delete(@PathVariable("code") String code) {
        Optional<Product> foundProductOpt = this.productRepository.findByCode(code);

        if (foundProductOpt.isEmpty()) {
            return ResponseEntity.notFound()
                    .build();
        }

        this.productRepository.delete(foundProductOpt.get());
        this.productPublisher.execute(foundProductOpt.get(), EventType.DELETED, "user02");

        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/api/products/code/{code}")
    public ResponseEntity<Product> getByCode(@PathVariable("code") String code) {
        return this.productRepository.findByCode(code)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound()
                        .build());
    }
}
