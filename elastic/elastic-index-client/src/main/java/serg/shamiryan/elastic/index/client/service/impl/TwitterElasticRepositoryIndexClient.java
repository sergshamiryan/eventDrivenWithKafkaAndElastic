package serg.shamiryan.elastic.index.client.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import serg.shamiryan.elastic.index.client.repository.TwitterElasticsearchIndexRepository;
import serg.shamiryan.elastic.index.client.service.ElasticIndexClient;
import serg.shamiryan.elastic.model.index.impl.TwitterIndexModel;

import java.util.List;

@Slf4j
//@Primary /*We can use primary because we have now two implementations one TwitterElasticIndexClient and this one*/
@Service
@RequiredArgsConstructor
//@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "true", matchIfMissing = true/*If property not found us as this*/)
public class TwitterElasticRepositoryIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private final TwitterElasticsearchIndexRepository twitterElasticsearchIndexRepository;

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<TwitterIndexModel> response = (List<TwitterIndexModel>) twitterElasticsearchIndexRepository.saveAll(documents);
        List<String> ids = response.stream()
                .map(TwitterIndexModel::getId)
                .toList();
        log.info("Documents indexed successfully with type: {} and ids {}", TwitterIndexModel.class.getName(), ids);
        return ids;
    }
}
