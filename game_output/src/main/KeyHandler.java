package main;

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP)    upPressed    = true;
        if (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN)  downPressed  = true;
        if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT)  leftPressed  = true;
        if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT) rightPressed = true;
        if (k == KeyEvent.VK_ENTER)                        enterPressed = true;

        // ESC exits fullscreen
        if (k == KeyEvent.VK_ESCAPE) {
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .setFullScreenWindow(null);
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP)    upPressed    = false;
        if (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN)  downPressed  = false;
        if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT)  leftPressed  = false;
        if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT) rightPressed = false;
        if (k == KeyEvent.VK_ENTER)                        enterPressed = false;
    }
}