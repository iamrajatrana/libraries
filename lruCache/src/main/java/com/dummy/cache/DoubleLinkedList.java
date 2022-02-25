package com.dummy.cache;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DoubleLinkedList<K, V> {

    private AtomicInteger currentSize = new AtomicInteger(0);
    private Node head;
    private Node tail;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public DoubleLinkedList() {
    }

    public void remove(Node node) {

        this.lock.writeLock().lock();
        try {
            Node next = node.next;
            Node prev = node.prev;
            if (prev != null)
                prev.next = next;
            if(next != null)
                next.prev = prev;
        } finally {
            currentSize.decrementAndGet();
            this.lock.writeLock().unlock();
        }

    }

    public Node removeFirst() {
        this.lock.writeLock().lock();
        try {
            Node node = head;
            if (head != null) {
                head = head.next;
                head.prev = node;
            }
            return node;
        } finally {
            currentSize.decrementAndGet();
            this.lock.writeLock().unlock();
        }
    }

    public void addLast(Node node) {
        this.lock.writeLock().lock();
        try {
            if(tail == null) {
                head = tail = node;
            } else {
                tail.next = node;
                node.prev = tail;
                tail = tail.next;
            }
        } finally {
            currentSize.incrementAndGet();
            this.lock.writeLock().unlock();
        }
    }

    public void clear() {
        this.lock.writeLock().lock();
        try {
            head = null;
            tail = null;
        } finally {
            currentSize = new AtomicInteger(0);
            this.lock.writeLock().unlock();
        }
    }

    public int size() {
        this.lock.readLock().lock();
        try {
            return currentSize.get();
        } finally {
            this.lock.readLock().unlock();
        }
    }

//    public void print() {
//        Node current = head;
//        while(current != null) {
//            System.out.print(current.getKey());
//            current = current.next;
//        }
//        System.out.println();
//    }
}
