package serg.shamiryan.elastic.index.client.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import serg.shamiryan.elastic.model.index.impl.TwitterIndexModel;

@Repository
public interface TwitterElasticsearchIndexRepository extends ElasticsearchRepository<TwitterIndexModel,String> {
}
