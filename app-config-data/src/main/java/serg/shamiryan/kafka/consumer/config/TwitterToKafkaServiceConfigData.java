package serg.shamiryan.kafka.consumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
public class TwitterToKafkaServiceConfigData {
    private List<String> twitterKeywords;
    private String welcomeMessage;
    private Boolean enableMockTweets;
    private Long mockSleepMax;
    private Integer mockMinTweetLength;
    private Integer mockMaxTweetLength;

    public List<String> getTwitterKeywords() {
        return twitterKeywords;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public Boolean getEnableMockTweets() {
        return enableMockTweets;
    }

    public Long getMockSleepMax() {
        return mockSleepMax;
    }

    public Integer getMockMinTweetLength() {
        return mockMinTweetLength;
    }

    public Integer getMockMaxTweetLength() {
        return mockMaxTweetLength;
    }
}
