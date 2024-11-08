package serg.shamiryan.elastic.index.client.util;

import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;
import serg.shamiryan.elastic.model.index.IndexModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
@Component
public class ElasticIndexUtil<T extends IndexModel> {
    /*Mapping list of index models into index queries for insert in elastic search*/
    public List<IndexQuery> getIndexQueries(List<T> documents) {
        return documents.stream()
                .map(document -> new IndexQueryBuilder()
                        .withId(document.getId())
                        .withObject(document)
                        .build()
                ).collect(Collectors.toList());
    }
}
