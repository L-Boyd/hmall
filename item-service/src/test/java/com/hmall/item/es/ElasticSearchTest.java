package com.hmall.item.es;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import com.hmall.item.service.impl.ItemServiceImpl;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest(properties = "spring.profiles.active=dev")
public class ElasticSearchTest {

    private RestHighLevelClient client;

    // 在kibana写的创建索引模板，直接复制过来的
    private static final String CREATE_INDEX_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"price\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"stock\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"image\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"category\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"brand\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"sold\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"commentCount\":{\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"isAD\":{\n" +
            "        \"type\": \"boolean\"\n" +
            "      },\n" +
            "      \"updateTime\":{\n" +
            "        \"type\": \"date\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Autowired
    private IItemService iItemService;
    @Autowired
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.1.7:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    @Test
    void testConnection() {
        System.out.println("client = " + client);
    }

    @Test
    void testCreateIndex() throws IOException {
        // Request对象
        CreateIndexRequest request = new CreateIndexRequest("items"); // "items" 索引名称
        // 请求参数
        request.source(CREATE_INDEX_TEMPLATE, XContentType.JSON);
        // 发送请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetIndex() throws IOException {
        // Request对象
        GetIndexRequest request = new GetIndexRequest("items");
        // 发送请求
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println("exists = " + exists);
        //GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
    }

    @Test
    void testDeleteIndex() throws IOException {
        // Request对象
        DeleteIndexRequest request = new DeleteIndexRequest("items");
        // 发送请求
        client.indices().delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void testCreateDocument() throws IOException {
        // 准备文档数据
        Item item = itemService.getById(317578L);
        ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);

        // Request对象
        IndexRequest request = new IndexRequest("items").id(itemDoc.getId());

        // 请求参数
        request.source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);

        // 发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetDocument() throws IOException {
        // Request对象
        GetRequest request = new GetRequest("items","317578");
        // 发送请求
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 解析响应结果
        String json = response.getSourceAsString();
        ItemDoc doc = JSONUtil.toBean(json, ItemDoc.class);
        System.out.println("doc = " + doc);
    }
}
