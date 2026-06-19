package oj.judge.queue;

import java.util.LinkedList;
import java.util.Queue;

public class JudgeQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    public JudgeQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Queue capacity must be positive");
        }
        this.capacity = capacity;
    }

    public synchronized void put(T item) throws InterruptedException {
        if (item == null) {
            throw new IllegalArgumentException("Queue item cannot be null");
        }
        while (queue.size() >= capacity) {
            wait();
        }
        queue.offer(item);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.poll();
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }

    public int capacity() {
        return capacity;
    }
}
