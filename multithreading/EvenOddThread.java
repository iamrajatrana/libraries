import java.util.concurrent.Semaphore;

interface Printer {
    public void printEven(int number);
    public void printOdd(int number);
}
class SynchronizedPrinter implements Printer{

    private volatile boolean isNumberEven;

    public SynchronizedPrinter() {}

    public synchronized void printEven(int number) {
        while (isNumberEven) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(Thread.currentThread().getName() + ":" + number);
        isNumberEven = true;
        notify();
    }

    public synchronized void printOdd(int number) {
        while (!isNumberEven) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(Thread.currentThread().getName() + ":" + number);
        isNumberEven = false;
        notify();
    }
}

class SemaphorePrinter implements  Printer{

    private Semaphore semEven = new Semaphore(0);
    private Semaphore semOdd = new Semaphore(1);

    public void printEven(int number) {
        try {
            semEven.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(Thread.currentThread().getName() + ":" + number);
        semOdd.release();
    }

    public void printOdd(int number) {
        try {
            semOdd.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(Thread.currentThread().getName() + ":" + number);
        semEven.release();

    }
}
class PrintTask implements Runnable {

    private Printer printer;
    private int max;
    private boolean isEvenNumber;


    public PrintTask(Printer printer, int max, boolean isEvenNumber) {
        this.printer = printer;
        this.max = max;
        this.isEvenNumber = isEvenNumber;
    }

    @Override
    public void run() {
        int number = isEvenNumber ? 2 : 1;
        while (number <= max) {
            if (isEvenNumber)
                printer.printEven(number);
            else
                printer.printOdd(number);
            number += 2;
        }
    }
}

public class EvenOddThread {

    public static void main(String[] args) {
//        Printer printer = new Printer();
//        new Thread(new PrintTask(printer, 100, false), "Odd").start();
//        new Thread(new PrintTask(printer, 100, true), "Even").start();
//
        // Using Semaphores
        SemaphorePrinter sharedPrinter = new SemaphorePrinter();
        new Thread(new PrintTask(sharedPrinter, 100, false), "Odd").start();
        new Thread(new PrintTask(sharedPrinter, 100, true), "Even").start();
    }

}