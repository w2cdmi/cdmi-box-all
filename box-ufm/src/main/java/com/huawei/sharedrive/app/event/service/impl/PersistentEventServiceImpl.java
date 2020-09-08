package com.huawei.sharedrive.app.event.service.impl;

import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.service.PersistentEventConsumer;
import com.huawei.sharedrive.app.event.service.PersistentEventService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class PersistentEventServiceImpl implements PersistentEventService {

    /**
     * 调度线程
     *
     * @author t90006461
     * @version CloudStor CSE Service Platform Subproject, 2015-8-11
     * @see
     * @since
     */
    private class EventDispatcher implements Runnable {

        @Override
        public void run() {
            while (isRunning) {
                doDispatch();
            }
        }

        private void doDispatch() {
            try {
                PersistentEvent event = eventQueue.take();
                if (event != null) {
                    pool.execute(new Porter(event));
                }
            } catch (Exception e) {
                LOGGER.error("Dispatch event failed.", e);
            }
        }

    }

    /**
     * 将临时队列中的事件存入ActiveMQ持久化队列
     *
     * @author t90006461
     * @version CloudStor CSE Service Platform Subproject, 2015-8-11
     * @see
     * @since
     */
    private final class Porter implements Runnable {
        private PersistentEvent event;

        Porter(PersistentEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            producer.sendEvent(event);
        }

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentEventServiceImpl.class);

    private static final int DEFAULT_QUEUE_SIZE = 10000;

    // 最大队列长度
    private static final int MAX_QUEUE_SIZE = getMaxQueueSize();

    private static final int DEFAULT_QUEUE_WORKERS = 10;

    // 事件临时队列执行线程数
    private static final int MAX_QUEUE_WORKERS = getMaxQueueWorkers();

    private boolean isRunning;

    // 事件队列
    private LinkedBlockingQueue<PersistentEvent> eventQueue = new LinkedBlockingQueue<PersistentEvent>(
            MAX_QUEUE_SIZE);

    // 任务队列
    private LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>(MAX_QUEUE_SIZE);

    private ThreadPoolExecutor pool;

    @Autowired
    private PersistentEventDispatcher dispatcher;

    @Autowired
    private PersistentEventProducer producer;

    @PreDestroy
    public void destroy() {
        isRunning = false;
        pool.shutdown();
    }

    /**
     * 将事件添加到临时队列，再由异步线程从队列中取出添加到ActiveMQ队列。 当ActiveMQ业务异常时，不会影响主业务流程。
     *
     * @param event
     */
    @Override
    public void fireEvent(PersistentEvent event) {
        if (event == null) {
            return;
        }
        if (!eventQueue.offer(event)) {
            LOGGER.warn("Can not offer event: {}", event.getEventType());
        }
    }

    @PostConstruct
    public void init() {
        isRunning = true;
        int totalThreadSize = MAX_QUEUE_WORKERS + 1;
        pool = new ThreadPoolExecutor(totalThreadSize, totalThreadSize, 10, TimeUnit.SECONDS, taskQueue);
        pool.execute(new EventDispatcher());
    }

    @Override
    public void registerConsumer(PersistentEventConsumer consumer) {
        dispatcher.addConsumer(consumer);
    }

    private static int getMaxQueueSize() {
        try {
            int maxQueueSize = Integer.parseInt(PropertiesUtils.getProperty("persistent.event.temp.queue.size", String.valueOf(DEFAULT_QUEUE_SIZE)));
            return maxQueueSize;
        } catch (NumberFormatException e) {
            return DEFAULT_QUEUE_SIZE;
        }
    }

    private static int getMaxQueueWorkers() {
        try {
            int maxQueueWorkers = Integer.parseInt(PropertiesUtils.getProperty("persistent.event.temp.queue.workers", String.valueOf(DEFAULT_QUEUE_WORKERS)));
            return maxQueueWorkers;
        } catch (NumberFormatException e) {
            return DEFAULT_QUEUE_WORKERS;
        }
    }
}
