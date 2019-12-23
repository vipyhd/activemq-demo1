package com.neteast.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class AutomaticCommit {
    public static void main(String args[]){
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "192.168.90.131:9092,192.168.90.132:9092,192.168.90.133:9092");
        props.setProperty("group.id", "test");

        //重点
        props.setProperty("enable.auto.commit", "true");    //自动提交
        //早期存在ZK，新版存在Kafka
        //自动提交，到底是什么时候提交，在下一次poll 提交


        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("market_topic"));

        while (true) {
            //下一次poll，会讲上次poll的offset提交上去
            ConsumerRecords<String, String> records = consumer.poll(8000);

            for (ConsumerRecord<String, String> record : records){
                long offset = record.offset();
                record.partition();
                record.value();
                record.key();
            }
        }

    }
}
