package br.com.alexsdm.postech.oficina.ordem_servico.config.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class SqsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.sqs.endpoint-override:}") // Endpoint customizado para ambiente local (ex: LocalStack)
    private String sqsEndpointOverride;

    @Bean
    public SqsClient sqsClient() {

        var builder = SqsClient.builder()
                .region(Region.of(awsRegion));

        if (!sqsEndpointOverride.isEmpty()) {
            builder.endpointOverride(URI.create(sqsEndpointOverride));
        }

        return builder.build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
