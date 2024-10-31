package serg.shamiryan.kafka.producer.service;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterKafkaProducer {
    private final KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public void send(String topicName, Long key, TwitterAvroModel message) {
        log.info("Sending message=  '{}' to topic {}", message, topicName);
        /* CompletableFuture callback methods for handling events when the response return
        * Callback methods used to specify actions that should be executed after a particular event or
        *condition is met.*/
        CompletableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture =
                kafkaTemplate.send(topicName, key, message);
        kafkaResultFuture.thenAccept(sendResult -> {
            /*RecordMetadata contains metadata about the record,
            such as the topic, partition, offset, and timestamp.*/
            RecordMetadata metadata = sendResult.getRecordMetadata();
            log.debug("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    metadata.timestamp(),
                    System.nanoTime());
        }).exceptionally(throwable -> {
            log.error("Error while sending message {} to topic {}", message.toString(), topicName, throwable);
            return null;
        });
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Closing kafka producer");
            kafkaTemplate.destroy();
        }
    }
}
