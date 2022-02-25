package com.dummy.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache<K, V> implements Cache<K, V> {

    static final int MAXIMUM_CAPACITY = 1 << 10;
    private int capcity;
    private DoubleLinkedList<K, V> doubleLinkedList = new DoubleLinkedList<>();
    private Map<K, Node> map = new HashMap<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private LRUCache(int capcity) {
        this.capcity = capcity;
    }

    public static LRUCache create(int capcity) {
        return new LRUCache(capcity);
    }

    public int size() {
        this.lock.readLock().lock();
        try {
            return doubleLinkedList.size();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        this.lock.writeLock().lock();
        try {
            map.clear();
            doubleLinkedList.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public boolean put(K key, V value) {
        this.lock.writeLock().lock();
        try {
            if (map.containsKey(key)) {
                doubleLinkedList.remove(map.get(key));
                doubleLinkedList.addLast(map.get(key));
                return true;
            }

            if (map.size() == capcity) {
                Node node = doubleLinkedList.removeFirst();
                map.remove(node.getKey());
            }

            Node node = new Node(key, value);
            doubleLinkedList.addLast(node);
            map.put(key, node);
            return false;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<V> get(K key) {
        this.lock.writeLock().lock();
        try {
            if (!map.containsKey(key)) {
                return Optional.empty();
            }
            Node node = map.get(key);
            doubleLinkedList.remove(node);
            doubleLinkedList.addLast(node);
            return Optional.of((V) map.get(key).getValue());
        } finally {
            this.lock.writeLock().unlock();
        }
    }

}
