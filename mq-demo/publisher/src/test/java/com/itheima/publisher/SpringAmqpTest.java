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
}