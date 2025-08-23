package com.itheima.publisher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqConifg {

    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            log.error("监听到了消息return callback");
            log.debug("交换机：{}", returnedMessage.getExchange());
            log.debug("routingKey：{}", returnedMessage.getRoutingKey());
            log.debug("消息：{}", returnedMessage.getMessage());
            log.debug("replyCode：{}", returnedMessage.getReplyCode());
            log.debug("replyText：{}", returnedMessage.getReplyText());
        });
    }
}
