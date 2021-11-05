package com.grcp.aws.project_02.controller;

import com.grcp.aws.project_02.model.ProductEventLogDto;
import com.grcp.aws.project_02.repository.ProductEventRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductEventLogController {

    private final ProductEventRepository productEventRepository;

    public ProductEventLogController(ProductEventRepository productEventRepository) {
        this.productEventRepository = productEventRepository;
    }

    @GetMapping("/events")
    public ResponseEntity<List<ProductEventLogDto>> getAll() {
        List<ProductEventLogDto> productsEventLog = StreamSupport
                .stream(this.productEventRepository.findAll().spliterator(), false)
                .map(ProductEventLogDto::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productsEventLog);
    }

    @GetMapping("/events/{code}")
    public ResponseEntity<List<ProductEventLogDto>> getByCode(@PathVariable("code") String code) {
        List<ProductEventLogDto> productsEventLog = this.productEventRepository.findAllByPk(code)
                .stream()
                .map(ProductEventLogDto::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productsEventLog);
    }

    @GetMapping("/events/{code}/{event}")
    public ResponseEntity<List<ProductEventLogDto>> getByCodeAndEvent(@PathVariable("code") String code,
                                                                      @PathVariable("event") String event) {
        List<ProductEventLogDto> productsEventLog = this.productEventRepository.findAllByPkAndSkStartsWith(code, event)
                .stream()
                .map(ProductEventLogDto::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productsEventLog);
    }
}
