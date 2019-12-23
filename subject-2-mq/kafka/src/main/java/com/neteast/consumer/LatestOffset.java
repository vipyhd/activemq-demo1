package com.neteast.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class LatestOffset {
    public static void main(String args[]){

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "192.168.90.131:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");


        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);


        List<TopicPartition> list = new ArrayList<>();

        for (int i=0; i<3; i++){
            list.add(new TopicPartition("market_topic",i));
        }

        Map<TopicPartition, Long> map = consumer.endOffsets(list);
        for (TopicPartition par : map.keySet()){
            Long offset = map.get(par);
            System.out.println(par.partition() + ":" + offset);
        }
    }
}
