package serg.shamiryan.twitter.to.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import serg.shamiryan.twitter.to.kafka.service.init.KafkaStreamInitializer;
import serg.shamiryan.twitter.to.kafka.service.runner.MockKafkaStreamRunner;

@SpringBootApplication
@ComponentScan(basePackages = "serg.shamiryan")
public class TwitterToKafkaServiceApplication {

    private final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(MockKafkaStreamRunner mockRunner, KafkaStreamInitializer kafkaStreamInitializer){
        return args -> {
            kafkaStreamInitializer.init();
            mockRunner.start();
        };
    }
}
