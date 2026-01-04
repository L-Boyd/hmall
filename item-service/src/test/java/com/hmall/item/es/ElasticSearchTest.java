package com.hmall.item.es;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import com.hmall.item.service.impl.ItemServiceImpl;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

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
                HttpHost.create("http://192.168.1.12:9200")
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
        GetRequest request = new GetRequest("items", "317578");
        // 发送请求
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 解析响应结果
        String json = response.getSourceAsString();
        ItemDoc doc = JSONUtil.toBean(json, ItemDoc.class);
        System.out.println("doc = " + doc);
    }

    @Test
    void testDeleteDocument() throws IOException {
        // Request对象
        DeleteRequest request = new DeleteRequest("items", "317578");
        // 发送请求
        client.delete(request, RequestOptions.DEFAULT);
    }

    /**
     * 测试全量更新
     *
     * @throws IOException
     */
    @Test
    void testFullUpdateDocument() throws IOException {
        // 准备文档数据
        Item item = itemService.getById(317578L);
        ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);
        itemDoc.setPrice(29900);

        // Request对象
        IndexRequest request = new IndexRequest("items").id(itemDoc.getId());

        // 请求参数
        request.source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);

        // 发送请求
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("response = " + response);
    }

    /**
     * 测试局部更新
     *
     * @throws IOException
     */
    @Test
    void testPartialUpdateDocument() throws IOException {
        // Request对象
        UpdateRequest request = new UpdateRequest("items", "896020");

        // 请求参数
        request.doc(
                "sold", 3
        );

        // 发送请求
        client.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 测试文档批处理
     *
     * @throws IOException
     */
    @Test
    void testDocumentBulk() throws IOException {
        int pageNo = 1;
        int pageSize = 500;
        // 准备文档数据
        while (true) {
            Page<Item> page = itemService.lambdaQuery()
                    .eq(Item::getStatus, 1)
                    .page(Page.of(pageNo, pageSize));
            List<Item> records = page.getRecords();
            if (records == null || records.isEmpty()) {
                return;
            }

            // Request对象
            BulkRequest request = new BulkRequest();

            // 请求参数
            for (Item item : records) {
                ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);
                IndexRequest indexRequest = new IndexRequest("items")
                        .id(itemDoc.getId())
                        .source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);
                request.add(indexRequest);
            }

            // 发送请求
            client.bulk(request, RequestOptions.DEFAULT);

            // 翻页
            pageNo++;
        }
    }

    /**
     * 测试查询所有文档
     *
     * @throws IOException
     */
    @Test
    void testMatchAll() throws IOException {
        SearchRequest request = new SearchRequest("items");
        // request参数
        request.source()
                .query(QueryBuilders.matchAllQuery());
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //System.out.println("response = " + response);

        printResponseResult(response);
    }

    private static void printResponseResult(SearchResponse response) {
        // 解析响应结果
        SearchHits searchHits = response.getHits();
        // 总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("total = " + total);
        // 命中的数据
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            ItemDoc doc = JSONUtil.toBean(sourceAsString, ItemDoc.class);
            System.out.println("doc = " + doc);
        }
    }

    /**
     * 测试查询符合条件的文档，
     * 搜索关键字：脱脂牛奶，
     * 品牌：德亚，
     * 价格；低于300
     *
     * @throws IOException
     */
    @Test
    void testSearchWithRequirement() throws IOException {
        // 模拟前端传递的分页参数
        int pageNo = 1;
        int pageSize = 20;

        SearchRequest request = new SearchRequest("items");
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("name", "脱脂牛奶"))
                .filter(QueryBuilders.termQuery("brand", "德亚"));
                //.filter(QueryBuilders.rangeQuery("price").lt(30000));
        request.source()
                .query(queryBuilder)
                // 分页和排序
                .from((pageNo - 1) * pageSize)    // 从第(pageNo - 1) * pageSize条开始
                .size(pageSize)    // 每页显示pageSize条
                .sort("sold", SortOrder.DESC)   // 先按销量降序
                .sort("price", SortOrder.ASC);    // 销量相同，则按价格升序排序



        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 解析响应结果
        printResponseResult(response);
    }
}
