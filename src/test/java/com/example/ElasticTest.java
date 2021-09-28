package com.example;

import com.example.config.ElasticSearchConfig;
import com.example.pojo.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @description: TODO
 * @author: Falcone
 * @date: 2021/9/28 22:30
 */

@SpringBootTest
public class ElasticTest {
    @Autowired
    private RestHighLevelClient client;

    ObjectMapper mapper = new ObjectMapper();

    // 测试 RestHighLevelClient 是否注入成功
    @Test
    public void contextLoads() {
        System.out.println(client);
    }

    @Test
    public void indexData() throws IOException {
        // 指定索引 和 id
        IndexRequest request = new IndexRequest("users");
        request.id("1");

        // 数据转换为 json 格式
        User user = new User("Zhangsan", "man", 19);
        String userString = mapper.writeValueAsString(user);
        System.out.println(userString);
        request.source(userString, XContentType.JSON);

        // 执行保存操作
        IndexResponse indexResponse = client.index(request, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(indexResponse);
    }

    @Test
    public void searchData() throws IOException {
        // 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("how2java");
        // 指定 DSL，检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 构造检索条件
        // address 中包含 mill
        searchSourceBuilder.query(QueryBuilders.matchQuery("name", "地砖"));
        // 按照前 10 种年龄分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("price").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        // 求所有人年龄的平均值
        AvgAggregationBuilder ageAvg = AggregationBuilders.avg("ageAvg").field("price");
        searchSourceBuilder.aggregation(ageAvg);

        System.out.println(searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);

        // 执行检索
        SearchResponse searchResponse = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

        // 分析结果
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for(SearchHit hit : searchHits) {
            System.out.println(hit);
        }

        // 查看聚合结果
        Aggregations aggs = searchResponse.getAggregations();

        Aggregation ageAgg2 = aggs.get("ageAgg");
        System.out.println(ageAgg2);
        Avg ageAvg2 = aggs.get("ageAvg");
        System.out.println("Avg age: " + ageAvg2.getValue());
    }
}
