package app;


/**
 * Created by rares on 27.03.2016.
 */

import java.awt.EventQueue;

import gui.GameManager;

public class Main {
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                GameManager.runApp();
            }
        });
    }

}

