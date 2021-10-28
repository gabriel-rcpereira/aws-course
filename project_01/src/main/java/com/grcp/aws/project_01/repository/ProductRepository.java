package com.grcp.aws.project_01.repository;

import com.grcp.aws.project_01.entity.Product;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findByCode(String code);
}
