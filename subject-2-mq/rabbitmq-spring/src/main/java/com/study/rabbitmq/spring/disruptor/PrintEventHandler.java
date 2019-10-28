package com.study.rabbitmq.spring.disruptor;

import com.lmax.disruptor.WorkHandler;

public class PrintEventHandler implements WorkHandler<PrintEvent> {

    public PrintEventHandler() {
    }

    @Override
    public void onEvent(PrintEvent event) throws Exception {
        if ("100000".equals(event.getId())) {
            System.out.println(Thread.currentThread() + "   " + event.getId() + "处理完毕" + System.currentTimeMillis());
            System.exit(0);
        }
    }
}