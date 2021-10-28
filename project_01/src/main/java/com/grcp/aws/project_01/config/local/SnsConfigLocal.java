package com.grcp.aws.project_01.config.local;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.Topic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration()
@Profile("local")
public class SnsConfigLocal {

    private final String productEventsTopic;
    private final AmazonSNS snsClient;

    public SnsConfigLocal() {
        this.snsClient = AmazonSNSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration("http://localhost:4566", Regions.US_EAST_1.getName()))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        CreateTopicResult productEventsSns = this.snsClient.createTopic("product-events-1");
        this.productEventsTopic = productEventsSns.getTopicArn();
    }

    @Bean
    public AmazonSNS snsClient() {
        return this.snsClient;
    }

    @Bean(name = "productEventsTopic")
    public Topic snsProductEventsTopic() {
        return new Topic().withTopicArn(this.productEventsTopic);
    }
}
