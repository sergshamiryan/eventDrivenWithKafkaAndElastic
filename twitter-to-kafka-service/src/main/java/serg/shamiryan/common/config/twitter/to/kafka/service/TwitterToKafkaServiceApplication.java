package serg.shamiryan.common.config.twitter.to.kafka.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import serg.shamiryan.common.config.twitter.to.kafka.service.runner.MockKafkaStreamRunner;
import serg.shamiryan.common.config.twitter.to.kafka.service.config.TwitterToKafkaServiceConfigData;

@SpringBootApplication
@RequiredArgsConstructor
public class TwitterToKafkaServiceApplication {

    private final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
    private final TwitterToKafkaServiceConfigData configData;

    private final MockKafkaStreamRunner mockRunner;

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            mockRunner.start();
        };
    }
}
