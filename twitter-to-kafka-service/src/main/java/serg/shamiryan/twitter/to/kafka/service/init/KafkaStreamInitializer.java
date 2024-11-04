package serg.shamiryan.twitter.to.kafka.service.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import serg.shamiryan.config.KafkaConfigData;
import serg.shamiryan.kafka.admin.client.KafkaAdminClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStreamInitializer {
    private final KafkaConfigData kafkaConfigData;
    private final KafkaAdminClient kafkaAdminClient;

    public void init() {
        kafkaAdminClient.createTopics();
        kafkaAdminClient.checkSchemeRegistry();
        log.info(
                "Topics with name {} is ready for operations!",
                kafkaConfigData.getTopicNamesToCreate().toArray());
    }
}
