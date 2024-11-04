package serg.shamiryan.kafka.to.elastic.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 */
@SpringBootApplication
@ComponentScan(basePackages = "serg.shamiryan")
public class KafkaToElasticSearchService {
    public static void main(String[] args) {
        SpringApplication.run(KafkaToElasticSearchService.class, args);
    }
}
