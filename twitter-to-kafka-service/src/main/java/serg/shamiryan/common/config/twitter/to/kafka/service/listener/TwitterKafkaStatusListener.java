package serg.shamiryan.common.config.twitter.to.kafka.service.listener;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import serg.shamiryan.common.config.twitter.to.kafka.service.transformer.TwitterStatusToAvroTransformer;
import serg.shamiryan.config.KafkaConfigData;
import serg.shamiryan.kafka.producer.service.TwitterKafkaProducer;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwitterKafkaStatusListener extends StatusAdapter {

    private final KafkaConfigData kafkaConfigData;
    private final TwitterKafkaProducer kafkaProducer;
    private final TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;

    @Override
    public void onStatus(Status status) {
        log.info("Twitter status with text {} sending to Kafka topic {}",
                status.getText(), kafkaConfigData.getTopicName());
        TwitterAvroModel twitterAvroModel = twitterStatusToAvroTransformer.twitterAvroModel(status);
        /*twitterAvroModel.getUserId() was used to partition the data using user id of twitter avro model
        * In this way the messages that belong to the same user will be in the same partition*/
        kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
    }
}
