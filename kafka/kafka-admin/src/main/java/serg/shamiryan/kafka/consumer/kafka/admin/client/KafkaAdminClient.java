package serg.shamiryan.kafka.consumer.kafka.admin.client;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import serg.shamiryan.kafka.consumer.config.KafkaConfigData;
import serg.shamiryan.kafka.consumer.config.RetryConfigData;
import serg.shamiryan.kafka.consumer.kafka.admin.exception.KafkaClientException;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KafkaAdminClient {

    private static final Logger log = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final KafkaConfigData kafkaConfigData;

    private final RetryConfigData retryConfigData;

    private final AdminClient adminClient;

    private final RetryTemplate retryTemplate;

    private final WebClient webClient;

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        try {
            createTopicsResult = retryTemplate.execute(this::doCreateTopics);
            log.info("Create topic result {}", createTopicsResult.values().values());
        } catch (Throwable e) {
            throw new KafkaClientException("Reach max number of retries for creating kafka topics", e);
        }
        checkTopicsCreated();
    }

    public void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    public void checkSchemeRegistry() {
        int retryCount = 1;
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        while (!getSchemeRegistryStatus().is2xxSuccessful()) {
            checkMaxRetrySchemeRegistry(retryCount++, retryConfigData.getMaxAttempts());
            sleep(sleepTimeMs);
            sleepTimeMs *= retryConfigData.getMultiplier().intValue();
        }
    }

    private HttpStatus getSchemeRegistryStatus() {
        try {
            return webClient
                    .get()
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchangeToMono(Mono::just)
                    .map(clientResponse -> HttpStatus.valueOf(clientResponse.statusCode().value()))
                    .block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    private void checkMaxRetry(int retry, Integer maxRetry) {
        if (retry > maxRetry) {
            throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)");
        }
    }

    private void checkMaxRetrySchemeRegistry(int retry, Integer maxRetry) {
        if (retry > maxRetry) {
            throw new KafkaClientException("Reached max number of retry for schema registry");
        }
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Error while sleeping for waiting new created topics", e);
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topic) {
        if (topics == null) {
            return false;
        }
        return topics.stream().anyMatch(t -> t.name().equals(topic));
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        log.info("Creating {} topics, attempt {}", topicNames.size(), retryContext.getRetryCount());
        List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic(
                topic.trim(),
                kafkaConfigData.getNumOfPartitions(),
                kafkaConfigData.getReplicationFactor()
        )).collect(Collectors.toList());
        return adminClient.createTopics(kafkaTopics);
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topics;
        try {
            topics = retryTemplate.execute(this::doGetTopics);
        } catch (Throwable e) {
            throw new KafkaClientException("Reach max number of retries for reading kafka topics", e);
        }
        return topics;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext)
            throws ExecutionException, InterruptedException {
        log.info("Reading {} topics, attempt {}",
                kafkaConfigData.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null) {
            topics.forEach(topic -> log.debug("Topic with name {}", topic.name()));
        }
        return topics;
    }
}
