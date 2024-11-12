package serg.shamiryan.elastic.index.client.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import serg.shamiryan.config.ElasticConfigData;
import serg.shamiryan.elastic.index.client.service.ElasticIndexClient;
import serg.shamiryan.elastic.index.client.util.ElasticIndexUtil;
import serg.shamiryan.elastic.model.index.impl.TwitterIndexModel;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "false")
public class TwitterElasticIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private final ElasticConfigData elasticConfigData;

    /*Index and Query against elastic Search*/
    private final ElasticsearchOperations elasticsearchOperations;

    private final ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil;

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        /*Mapping list of index models into index queries for insert in elastic search*/
        List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);
        /*Doing bulk insert and retrieving information about document*/
        List<IndexedObjectInformation> documentIds = elasticsearchOperations.bulkIndex(
                indexQueries,
                /*Specifying the index where documents has to be inserted*/
                IndexCoordinates.of(elasticConfigData.getIndexName())
        );
        /*returning ids of documents*/
        List<String> ids = documentIds.stream()
                .map(IndexedObjectInformation::id)
                .toList();
        log.info("Documents indexed successfully with type: {} and ids {}", TwitterIndexModel.class.getName(), ids);
        return ids;
    }
}
