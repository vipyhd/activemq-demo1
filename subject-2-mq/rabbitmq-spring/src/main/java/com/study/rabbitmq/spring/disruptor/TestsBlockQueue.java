package com.study.rabbitmq.spring.disruptor;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class TestsBlockQueue {
    static LinkedBlockingQueue<String> buffer = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch ctl = new CountDownLatch(2);
        long time = System.currentTimeMillis();
        new Thread(() -> {
            for (int i = 1; i <= 1000000; i++) {
                // 生产者 添加数据
                buffer.add("" + i);
            }
            ctl.countDown();
        }).start();

        // 消费者
        new Thread(() -> {
            while (true) {
                if (buffer.size() == 0) {
                    // block --- jdk queue 提供线程阻塞
                    Thread.yield();
                    continue;
                }
                // buffer消费者处理数据
                try {
                    String value = buffer.poll();
                    if ("1000000".equals(value)) {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ctl.countDown();
            }
        }).start();

        ctl.await();
        System.out.println("执行结束" + (System.currentTimeMillis() - time));

    }
}
