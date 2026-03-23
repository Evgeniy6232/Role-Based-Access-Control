package org.example;

import java.util.ArrayList;
import java.util.List;

public class ConsoleManager {
    private final List<String> lines = new ArrayList<>();

    public ConsoleManager(int threads) {
        for (int i = 1; i <= threads; i++) {
            lines.add("Thread " + i + " [                    ]   0% | 0ms");
            System.out.println(lines.get(i - 1));
        }
    }

    public synchronized void update(Worker w) {
        int idx = w.getId() - 1;
        String newLine = w.getBar().render();
        lines.set(idx, newLine);

        System.out.print("\033[" + lines.size() + "A");
        for (String line : lines) {
            System.out.print("\r\033[K" + line + "\n");
        }
    }

    public synchronized void finish(Worker w) {
        update(w);
    }
}