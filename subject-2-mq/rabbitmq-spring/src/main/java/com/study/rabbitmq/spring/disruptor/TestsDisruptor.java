package com.study.rabbitmq.spring.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TestsDisruptor {
    public static void main(String[] args) throws IOException {
        // 执行器，用于构造消费者线程
        Executor executor = Executors.newCachedThreadPool();
        // 指定 ring buffer字节大小, must be power of 2.
        int bufferSize = 1024;
        //单线程模式，获取额外的性能
        AtomicInteger seq = new AtomicInteger();
        Disruptor<PrintEvent> disruptor = new Disruptor<PrintEvent>(new EventFactory<PrintEvent>() {
            @Override
            public PrintEvent newInstance() {
                return new PrintEvent();  // 指定事件实例构建工厂
            }
        },
                bufferSize, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "disruptor-thread-" + seq.incrementAndGet());
                System.out.println("生成线程 " + t);
                return t;
            }
        },
                ProducerType.SINGLE, // 单个生产者
                new BlockingWaitStrategy());
        //设置事件业务处理器---消费者 (消费者组- 3个)
        disruptor.handleEventsWithWorkerPool(new PrintEventHandler(), new PrintEventHandler(), new PrintEventHandler());
        //启动disruptor线程
        disruptor.start();

        // 并不是用java自带的容器 juc
        RingBuffer<PrintEvent> ringBuffer = disruptor.getRingBuffer();
        //为 ring buffer指定事件生产者
        EventTranslatorOneArg translator = new EventTranslatorOneArg<PrintEvent, String>() {
            @Override
            public void translateTo(PrintEvent event, long sequence, String arg0) {
                event.setId(arg0);
            }
        };
        long time = System.currentTimeMillis();
        System.out.println("开始 " + time);
        for (int i = 1; i <= 1000000; i++) {
            ringBuffer.publishEvent(translator, i + "");
        }

        System.in.read();
    }
    // 选择性 暂时不学
}

