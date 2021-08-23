package com.distributedsystems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final List<Thread> threadList;


    public ThreadPool(int poolSize) {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.threadList = new ArrayList<>();

        for (int i = 0; i < poolSize; i++) {
            var thread = new TaskThread();
            thread.start();
            threadList.add(thread);
        }
    }


    public void execute(Runnable task) {
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notify();
        }
    }


    public class TaskThread extends Thread {
        /*
        TaskThread class instances threads for the pool. The run() method loops until work is allocated via the queue
        */

        @Override
        public void run() {
            while (true) {
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty()) {
                        try {
                            taskQueue.wait();         // .wait() exits loop once work is added via the .notify() under add
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Runnable task = taskQueue.poll();
                    task.run();
                }
            }
        }
    }
}
