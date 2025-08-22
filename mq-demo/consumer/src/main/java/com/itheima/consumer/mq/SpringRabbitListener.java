package com.itheima.consumer.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Slf4j
@Component
public class SpringRabbitListener {

    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String message) {
        log.info("spring 消费者收到消息：{}", message);
    }

    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue1(String message) throws InterruptedException {
        System.out.println("消费者1接收到消息: " + message + " time:" + LocalTime.now());
        Thread.sleep(25);
    }

    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue2(String message) throws InterruptedException {
        System.err.println("消费者2接收到消息......: " + message + " time:" +LocalTime.now());
        Thread.sleep(200);
    }

    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutQueue1(String message) {
        log.info("消费者1收到消息：{}", message);
    }

    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutQueue2(String message) {
        log.info("消费者2收到消息：{}", message);
    }

    @RabbitListener(queues = "direct.queue1")
    public void listenDirectQueue1(String message) {
        log.info("消费者1收到direct.queue1的消息：{}", message);
    }

    @RabbitListener(queues = "direct.queue2")
    public void listenDirectQueue2(String message) {
        log.info("消费者2收到direct.queue2的消息：{}", message);
    }

    @RabbitListener(queues = "topic.queue1")
    public void listenTopicQueue1(String message) {
        log.info("消费者1收到topic.queue1的消息：{}", message);
    }

    @RabbitListener(queues = "topic.queue2")
    public void listenTopicQueue2(String message) {
        log.info("消费者2收到topic.queue2的消息：{}", message);
    }
}
