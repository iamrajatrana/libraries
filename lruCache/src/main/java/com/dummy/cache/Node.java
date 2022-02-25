package com.dummy.cache;

public class Node<K, V> {
        K key;
        V value;
        Node next;
        Node prev;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}