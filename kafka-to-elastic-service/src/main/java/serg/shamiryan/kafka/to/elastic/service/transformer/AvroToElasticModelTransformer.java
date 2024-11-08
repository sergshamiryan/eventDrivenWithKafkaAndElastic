package serg.shamiryan.kafka.to.elastic.service.transformer;

import org.springframework.stereotype.Component;
import serg.shamiryan.elastic.model.index.impl.TwitterIndexModel;
import serg.shamiryan.kafka.avro.model.TwitterAvroModel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class AvroToElasticModelTransformer {

    public List<TwitterIndexModel> getElasticModels(List<TwitterAvroModel> avroModels) {
        return avroModels.stream()
                .map(avroModel -> new TwitterIndexModel(
                        String.valueOf(avroModel.getId()),
                        avroModel.getUserId(),
                        avroModel.getText(),
                        LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(avroModel.getCreatedAt()),
                                ZoneId.systemDefault()))).toList();
    }
}
