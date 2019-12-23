package com.neteast.intercepter;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class IntercepterTest {
    public static void main(String args[]) throws InterruptedException {

        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.90.131:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        List<String> interceptors = new ArrayList<>();
        interceptors.add("com.neteast.intercepter.TimeStampPrependerInterceptor"); // interceptor 1
        interceptors.add("com.neteast.intercepter.CounterInterceptor"); // interceptor 2
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        Producer<String, String> producer = new KafkaProducer<>(props);

        //System.out.println(sb.toString());

        for (int i=0; i< 10; i++){
            producer.send(new ProducerRecord<String, String>("test1",
                    "key",
                    "message" + i));

            Thread.sleep(1000L);
        }



        producer.close();


    }
}
