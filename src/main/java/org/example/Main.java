package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int THREADS = 5;
        int STEPS = 20;
        int DELAY_MS = 200;

        ConsoleManager console = new ConsoleManager(THREADS);

        Thread.sleep(100);

        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            Worker worker = new Worker(i + 1, STEPS, console, DELAY_MS);
            threads[i] = new Thread(worker);
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join(500);
        }

    }
}