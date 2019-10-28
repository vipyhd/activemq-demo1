package com.study.rabbitmq.spring.schedule;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@EnableScheduling
public class ScheduleDemoApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ScheduleDemoApp.class).run(args);
    }

    @RabbitListener(queues = "delay_queue_3") // 定时任务 -- 只有MQ -- 监听指定的Queue --- （其他事情交给任务调度系统）
    public void doSth(String msg) {
        // 你自己的情况 你自己管理 -- ack
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " --- 收到消息" + msg);
        // 执行异常 --- MQ队列
    }
}
