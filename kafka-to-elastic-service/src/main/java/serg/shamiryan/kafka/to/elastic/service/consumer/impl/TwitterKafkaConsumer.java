package serg.shamiryan.kafka.to.elastic.service.consumer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import serg.shamiryan.config.KafkaConfigData;
import serg.shamiryan.config.KafkaConsumerConfigData;
import serg.shamiryan.elastic.index.client.service.ElasticIndexClient;
import serg.shamiryan.elastic.model.index.impl.TwitterIndexModel;
import serg.shamiryan.kafka.admin.client.KafkaAdminClient;
import serg.shamiryan.kafka.avro.model.TwitterAvroModel;
import serg.shamiryan.kafka.to.elastic.service.consumer.KafkaConsumer;
import serg.shamiryan.kafka.to.elastic.service.transformer.AvroToElasticModelTransformer;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final KafkaConsumerConfigData kafkaConsumerConfigData;

    private final KafkaAdminClient kafkaAdminClient;

    private final KafkaConfigData kafkaConfigData;

    private final AvroToElasticModelTransformer avroToElasticModelTransformer;

    private final ElasticIndexClient<TwitterIndexModel> elasticIndexClient;

    @EventListener(ApplicationReadyEvent.class)
    public void onAppStarted() {
        kafkaAdminClient.checkTopicsCreated();
        log.info("Topics with name {} is ready for operations", kafkaConfigData.getTopicNamesToCreate().toArray());
        kafkaListenerEndpointRegistry.getListenerContainer(kafkaConsumerConfigData.getConsumerGroupId()).start();
    }

    @Override
    //Create Kafka Consumer
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}"/*Listener id, not group id*/, topics = "${kafka-config.topic-name}")
    public void receive(@Payload List<TwitterAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<Integer> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.error("{} number of messages received with keys {}, partitions {} and offsets {}, " +
                        "sending it to elastic: Thread id  {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());
        List<TwitterIndexModel> indexModelsFromAvro = avroToElasticModelTransformer.getElasticModels(messages);
        List<String> ids = elasticIndexClient.save(indexModelsFromAvro);
        log.info("Documents saved to elastic search with ids: {}", ids.toArray());
    }
}
