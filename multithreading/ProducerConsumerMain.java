import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerConsumerMain {

    static BlockingQueue queue = new LinkedBlockingQueue();
    public static void main(String[] args) {

        Runnable producer = () -> {
            while(true){
                queue.add(1);
            }
        };

        Runnable consumer = () -> {
            while(true){
                try {
                    System.out.println(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread t1 = new Thread(producer);
        Thread t2 = new Thread(producer);

        Thread t3 = new Thread(consumer);
        Thread t4 = new Thread(consumer);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

    }
}
