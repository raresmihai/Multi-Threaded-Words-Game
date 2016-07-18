package gui;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by rares on 22.04.2016.
 */
public class Timer extends JLabel implements Runnable {
    private volatile boolean shouldRun = true;

    public Timer() {
        this.setForeground(Color.green);
    }
    @Override
    public void run() {
        long startTime = System.nanoTime();
        while (shouldRun) {
            long estimatedTime = System.nanoTime() - startTime;
            this.setText(String.valueOf(TimeUnit.NANOSECONDS.toSeconds(estimatedTime)) + " seconds elapsed");
        }
    }

    public void stop() {
        shouldRun = false;
    }


}
