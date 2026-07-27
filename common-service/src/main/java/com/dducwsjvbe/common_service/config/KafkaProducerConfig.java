package com.dducwsjvbe.common_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers:localhost:29092}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); //key là thứ quyết định xem có cx 1 partition ko
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    @Bean
    public NewTopic sendEmail() {
        return new NewTopic("email", 3, (short) 1);
    }
    @Bean
    public NewTopic asyncUpload() {
        return new NewTopic("async-upload-topic", 3, (short) 1);
    }
    @Bean
    public NewTopic asyncCreateArticle() {
        return new NewTopic("async-create-article-topic", 3, (short) 1);
    }
    @Bean
    public NewTopic asyncDeleteArticle() {
        return new NewTopic("article-delete-topic", 3, (short) 1);
    }
    @Bean
    public NewTopic asyncUpViewArticle() {
        return new NewTopic("article-up-view-topic", 3, (short) 1);
    }
    @Bean
    public NewTopic asyncDeleteCache() {
        return new NewTopic("delete-cache-topic", 3, (short) 1);
    }

}
