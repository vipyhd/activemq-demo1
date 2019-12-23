package com.neteast.partitioner;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;

import java.util.Properties;

public class TestProducer {
    public static void main(String args[]){
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.90.131:9092,192.168.90.132:9092,192.168.90.133:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //         org.apache.kafka.clients.producer.internals.DefaultPartitioner
        props.put("partitioner.class","com.neteast.partitioner.MyPartitioner");

        Producer<String, String> producer = new KafkaProducer<>(props);

        for (int i=0; i< 10; i++){
            producer.send(new ProducerRecord<String, String>("market_topic1",
                    ""+i,
                    "message" + i));

            //Thread.sleep(1000L);
        }

        producer.close();
        System.out.println("done...");
    }
}
