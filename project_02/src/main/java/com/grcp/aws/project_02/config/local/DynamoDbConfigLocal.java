package com.grcp.aws.project_02.config.local;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.BillingMode;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.grcp.aws.project_02.repository.ProductEventRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableDynamoDBRepositories(basePackageClasses = ProductEventRepository.class)
@Profile("local")
public class DynamoDbConfigLocal {

    public static final String TABLE_PRODUCT_EVENTS = "product-events";
    final private AmazonDynamoDB amazonDynamoDB;

    public DynamoDbConfigLocal() throws InterruptedException {
        this.amazonDynamoDB = AmazonDynamoDBClient.builder()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration("http://localhost:4566",
                                Regions.US_EAST_1.getName()))
                .build();

        this.createTable();
    }

    @Bean
    @Primary
    public DynamoDBMapperConfig dynamoDBMapperConfig() {
        return DynamoDBMapperConfig.DEFAULT;
    }

    @Bean
    @Primary
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB,
                                         DynamoDBMapperConfig config) {
        return new DynamoDBMapper(amazonDynamoDB, config);
    }

    @Bean
    @Primary
    public AmazonDynamoDB amazonDynamoDB() {
        return this.amazonDynamoDB;
    }

    private void createTable() throws InterruptedException {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("pk").withAttributeType(ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("sk").withAttributeType(ScalarAttributeType.S));

        List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement().withAttributeName("pk").withKeyType(KeyType.HASH));
        keySchema.add(new KeySchemaElement().withAttributeName("sk").withKeyType(KeyType.RANGE));

        CreateTableRequest request = new CreateTableRequest()
                .withTableName(TABLE_PRODUCT_EVENTS)
                .withKeySchema(keySchema)
                .withAttributeDefinitions(attributeDefinitions)
                .withBillingMode(BillingMode.PAY_PER_REQUEST);

        TableUtils.createTableIfNotExists(amazonDynamoDB, request);
    }
}
