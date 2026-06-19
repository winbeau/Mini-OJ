package oj.queue;

import java.util.*;

public class JudgeQueue {

    private final List<JudgeTask> list = new ArrayList<>();
    private final int MAX_CAPACITY = 10;

    private final Object lock = new Object();

    private static final JudgeQueue INSTANCE = new JudgeQueue();

    private JudgeQueue() {}

    public static JudgeQueue getInstance() {
        return INSTANCE;
    }

    public void add(JudgeTask task) {
        synchronized (lock) {

            while (list.size() >= MAX_CAPACITY) {
                try {
                    System.out.println("[Queue Log] Judge Queue full");
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            list.add(task);

            lock.notifyAll();
        }
    }

    public JudgeTask take() {
        synchronized (lock) {
            while (list.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            JudgeTask task = list.remove(0);

            lock.notifyAll();
            
            return task;
        }
    }

    public int size() {
        synchronized (lock) {
            return list.size();
        }
    }
}
