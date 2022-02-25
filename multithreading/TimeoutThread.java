import java.util.concurrent.atomic.AtomicBoolean;
class Task implements Runnable {

    public AtomicBoolean keepRunning = new AtomicBoolean(true);

    @Override
    public void run() {
        while (keepRunning.get()) {
            System.out.println("Hello World");
        }
    }

    public void kill(){
        keepRunning.set(false);
    }
}

public class TimeoutThread {

    public static void main(String []args) {
        Task task = new Task();
        Thread t1 = new Thread(task);
        t1.start();

        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        task.kill();

    }

}
