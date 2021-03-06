package com.study.rabbitmq.spring.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.handler.annotation.Payload;

@Configuration
@EnableAutoConfiguration
@RabbitListener(queues = "spring.cluster")
@Import(AppConfiguration.class)
public class ConsumerApp {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerApp.class);

    @RabbitHandler
    public void receive(@Payload String msg) {
        logger.info("收到消息：" + msg);
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApp.class, args);
    }
}
