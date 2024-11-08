package serg.shamiryan.elastic.index.client.service;

import serg.shamiryan.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticIndexClient<T extends IndexModel> {
    List<String> save(List<T> documents);
}
