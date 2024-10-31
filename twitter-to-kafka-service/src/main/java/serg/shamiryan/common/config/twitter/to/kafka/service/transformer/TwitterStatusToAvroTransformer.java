package serg.shamiryan.common.config.twitter.to.kafka.service.transformer;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component
@RequiredArgsConstructor
public class TwitterStatusToAvroTransformer {

    public TwitterAvroModel twitterAvroModel(Status status) {
        return TwitterAvroModel
                .newBuilder()
                .setId(status.getId())
                .setUserId(status.getUser().getId())
                .setText(status.getText())
                .setCreatedAt(status.getCreatedAt().getTime())
                .build();
    }
}
