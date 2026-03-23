package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class ProgressBar {
    private final int id;
    private final int total;
    private final AtomicInteger current;
    private final long startTime;
    private boolean finished;
    private long endTime;

    public ProgressBar(int id, int total) {
        this.id = id;
        this.total = total;
        this.current = new AtomicInteger(0);
        this.startTime = System.currentTimeMillis();
        this.finished = false;
    }

    public void increment() {
        int val = current.incrementAndGet();
        if (val >= total && !finished) {
            finished = true;
            endTime = System.currentTimeMillis();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public String render() {
        int percent = (current.get() * 100) / total;
        int width = 20;
        int filled = (percent * width) / 100;

        String bar = "[" + "#".repeat(filled) + " ".repeat(width - filled) + "]";

        long elapsed = finished ? endTime - startTime : System.currentTimeMillis() - startTime;

        return String.format("Thread %d [%d] %s %3d%% | %dms",
                id, Thread.currentThread().threadId(), bar, percent, elapsed);
    }
}