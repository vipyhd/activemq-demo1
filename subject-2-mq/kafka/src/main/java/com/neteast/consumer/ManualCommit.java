package com.neteast.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ManualCommit {
    public static void main(String args[]){
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "192.168.90.131:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test"));

        final int minBatchSize = 2000;
        List<ConsumerRecord<String, String>> buffer = new ArrayList<>();

        while (true) {
            System.out.println("getting...");
            ConsumerRecords<String, String> records = consumer.poll(4000);

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("received msg：" + record.value());
                buffer.add(record);
            }
            if (buffer.size() >= minBatchSize) {
                insertToDB(buffer);        //打印出内容

                System.out.println("======》》》offset commit。。。");
                consumer.commitSync();      //手动提交offset
                buffer.clear();
            }
        }
    }

    public static void insertToDB(List<ConsumerRecord<String, String>> buffer){
        System.out.println("模拟插入数据库。。。");
    }
}
