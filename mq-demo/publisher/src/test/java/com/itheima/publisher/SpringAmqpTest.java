package com.itheima.publisher;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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
}