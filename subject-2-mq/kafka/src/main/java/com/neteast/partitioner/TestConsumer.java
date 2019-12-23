package com.neteast.partitioner;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class TestConsumer {
    public static void main(String args[]){
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "192.168.90.131:9092,192.168.90.132:9092,192.168.90.133:9092");
        props.setProperty("group.id", "test111111111111111");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.offset.reset", "earliest");
        props.setProperty("auto.commit.interval.ms", "1000");

        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList("market_topic1"));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records){
                System.out.println(record.partition() + ":" + record.offset());
            }
        }
    }
}
