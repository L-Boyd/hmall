package com.itheima.publisher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
// 单元测试要和启动类在同一包下或启动类的子包下
class SpringAmqpTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSimpleQueue() {
        // 队列名
        String queueName = "simple.queue";
        // 消息
        String message = "hello, spring amqp";
        // 发送消息
        rabbitTemplate.convertAndSend(queueName, message);
    }

    @Test
    public void testWorkQueue() {
        String queueName = "work.queue";
        for (int i = 0; i < 50; i++) {
            String message = "hello, spring amqp" + i;
            rabbitTemplate.convertAndSend(queueName, message);
        }
    }

    @Test
    public void testFanoutExchange() {
        // 交换机名字
        String exchangeName = "hmall.fanout";
        // 消息
        String message = "hello, fanout";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, null, message);
    }

    @Test
    public void testDirectionExchange() {
        // 交换机名字
        String exchangeName = "hmall.direct";
        // 消息
        String message1 = "hello, direct1";
        String message2 = "hello, direct2";
        String message3 = "hello, direct3";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "blue", message1);
        rabbitTemplate.convertAndSend(exchangeName, "yellow", message2);
        rabbitTemplate.convertAndSend(exchangeName, "red", message3);
    }

    @Test
    public void testTopicxchange() {
        // 交换机名字
        String exchangeName = "hmall.topic";
        // 消息
        String message1 = "hello, china";
        String message2 = "hello, china.news";
        String message3 = "hello, japan.news";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "china", message1);
        rabbitTemplate.convertAndSend(exchangeName, "china.news", message2);
        rabbitTemplate.convertAndSend(exchangeName, "japan.news", message3);
    }

    @Test
    public void testSendObject() {
        // 消息
        Map<String, Object> message = new HashMap<>(2);
        message.put("name", "Jack");
        message.put("age", 18);
        // 发送消息
        rabbitTemplate.convertAndSend("object.queue", message);
    }

    @Test
    public void testConfirmCallback() throws InterruptedException {
        CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
        cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("spring amqp处理确认结果异常", ex);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                // 判断是否成功
                if (result.isAck()) {
                    log.info("收到ConfirmCallback ack，消息发送成功");
                }
                else {
                    log.error("收到ConfirmCallback nack，消息发送失败，reason:{}", result.getReason());
                    // 重发
                }
            }
        });

        // 交换机名字
        String exchangeName = "hmall.direct";
        // 消息
        String message = "hello, direct";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "blue", message, cd);

        Thread.sleep(1000);
    }
}