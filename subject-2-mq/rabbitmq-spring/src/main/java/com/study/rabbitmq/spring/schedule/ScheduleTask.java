package com.study.rabbitmq.spring.schedule;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduleTask {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 3000)
    public void reportCurrentTime() {
        System.out.println("当前时间：" + dateFormat.format(new Date()));
        // 消息发送到 3秒必定过期的Queue
        rabbitTemplate.convertAndSend("ttl_exchange", "ttl_queue_3", "消息发出时间：" + dateFormat.format(new Date()));
    }
}
