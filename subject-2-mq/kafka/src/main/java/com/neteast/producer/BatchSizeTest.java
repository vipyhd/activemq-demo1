package com.neteast.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class BatchSizeTest {
    public static void main(String args[]){

        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.90.131:9092");
        props.put("acks", "all");
        props.put("batch.size", "16384");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        StringBuilder sb = new StringBuilder();
        for (int i =0; i<5000; i++){
            sb.append("org.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializerorg.apache.kafka.common.serialization.StringSerializer");
        }

        //System.out.println(sb.toString());

        producer.send(new ProducerRecord<String, String>("market_topic",
                null,
                sb.toString()));

        producer.close();



    }
}
