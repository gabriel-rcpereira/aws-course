package com.grcp.aws.project_02.repository;

import com.grcp.aws.project_02.model.ProductEventKey;
import com.grcp.aws.project_02.model.ProductEventLog;
import java.util.List;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface ProductEventRepository extends CrudRepository<ProductEventLog, ProductEventKey> {

    List<ProductEventLog> findAllByPk(String code);

    List<ProductEventLog> findAllByPkAndSkStartsWith(String code, String eventType);
}
