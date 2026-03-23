package org.example;

import java.util.Random;

public class Worker implements Runnable {
    private final int id;
    private final ProgressBar bar;
    private final ConsoleManager console;
    private final int baseDelay;
    private final Random random = new Random();

    public Worker(int id, int totalSteps, ConsoleManager console, int baseDelay) {
        this.id = id;
        this.bar = new ProgressBar(id, totalSteps);
        this.console = console;
        this.baseDelay = baseDelay;
    }

    @Override
    public void run() {
        while (!bar.isFinished()) {
            try {
                int delay = baseDelay + random.nextInt(1000);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            bar.increment();
            console.update(this);
        }
        console.finish(this);
    }

    public int getId() { return id; }
    public ProgressBar getBar() { return bar; }
}