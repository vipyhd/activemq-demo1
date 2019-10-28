package com.study.rabbitmq.spring.schedule;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ScheduleConfig {

    /**
     * 延迟交换机
     *
     * @returndelay_queue
     */
    @Bean
    public DirectExchange perQueueTTLExchange() {
        DirectExchange directExchange = new DirectExchange("ttl_exchange", true, false);
        return directExchange;
    }

    @Bean
    public Queue delayTTLQueue3() {
        Map<String, Object> paramMap = new HashMap<>();
        // 如果这个queue的消息过期之后，数据就到死信队列 -- 实际上就是普通queue
        paramMap.put("x-dead-letter-exchange", "delay_exchange");
        paramMap.put("x-dead-letter-routing-key", "delay_queue_3");
        paramMap.put("x-message-ttl", 3000);
        return new Queue("ttl_queue_3", true, false, false, paramMap);
    }

    /**
     * 绑定延迟队列
     *
     * @return
     */
    @Bean
    public Binding queueTTLBinding(Queue delayTTLQueue3, DirectExchange perQueueTTLExchange) {
        return BindingBuilder.bind(delayTTLQueue3).to(perQueueTTLExchange).with("ttl_queue_3");
    }

    /**
     * 死信交换机
     *
     * @return
     */
    @Bean
    public DirectExchange delayExchange() {
        DirectExchange directExchange = new DirectExchange("delay_exchange", true, false);
        return directExchange;
    }

    /**
     * 实际消费队列
     *
     * @return
     */
    @Bean
    public Queue delayQueue3() {
        return new Queue("delay_queue_3", true, false, false);
    }

    /**
     * 绑定死信队列
     *
     * @return
     */
    @Bean
    public Binding dlxBinding(Queue delayQueue3, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayQueue3).to(delayExchange).with("delay_queue_3");
    }
}
