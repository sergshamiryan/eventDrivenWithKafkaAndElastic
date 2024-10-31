package serg.shamiryan.kafka.admin.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import serg.shamiryan.config.KafkaConfigData;

import java.util.Map;

@EnableRetry
@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfig {

    private final KafkaConfigData kafkaConfigData;

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(
                Map.of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
                        kafkaConfigData.getBootstrapServers()));
    }

}
