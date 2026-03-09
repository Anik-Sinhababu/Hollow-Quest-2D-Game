package main;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {

        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Hollow Quest");
        window.setUndecorated(true); // Remove title bar & borders for true fullscreen

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();

        // Enter fullscreen exclusive mode
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(window);
        } else {
            // Fallback: maximised borderless window
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setVisible(true);
        }

        gamePanel.startGameThread();
    }
}