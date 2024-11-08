package serg.shamiryan.elastic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import serg.shamiryan.config.ElasticConfigData;

/**
 * Hello world!
 */
@Configuration
@RequiredArgsConstructor
@EnableElasticsearchRepositories(basePackages = "serg.shamiryan.elastic")
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    private final ElasticConfigData elasticConfigData;


    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticConfigData.getConnectionUrl())
                .withConnectTimeout(elasticConfigData.getConnectTimoutMs())
                .withSocketTimeout(elasticConfigData.getSocketTimeOutMs()).build();
    }
}
