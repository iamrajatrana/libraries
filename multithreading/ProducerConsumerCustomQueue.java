import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MyBlockingQueue<T> {
    private Queue<T> queue = new LinkedList();
    Lock lock = new ReentrantLock();
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();
    int maxSize;

    public MyBlockingQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    public void put(T elem) throws InterruptedException {
        lock.lock();
        try {
            while(queue.size() == maxSize) {
                notFull.await();
            }
            queue.add(elem);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T get() throws InterruptedException {
        lock.lock();
        try {
            while(queue.size() == 0) {
                notEmpty.await();
            }
            T item = queue.remove();
            notFull.signalAll();
            return item;
        } finally {
            lock.unlock();
        }
    }
}

public class ProducerConsumerCustomQueue {
    public static void main(String[] args) {

        MyBlockingQueue<Double> queue = new MyBlockingQueue(100);
        Runnable producer = () -> {
            try {
                while (true) {
                    queue.put(Math.random());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable consumer = () -> {
            try {
                while (true) {
                    System.out.println(queue.get());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread t1 = new Thread(producer);

        Thread t3 = new Thread(consumer);
        Thread t4 = new Thread(consumer);
        Thread t5 = new Thread(consumer);

        t1.start();
        t3.start();
        t4.start();
        t5.start();
    }
}
