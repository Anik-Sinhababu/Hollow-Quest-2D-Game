package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import object.OBJ_key;

public class GameUI {

    GamePanel gp;
    Font gameFont;
    OBJ_key key;
    BufferedImage key_image;
    String message = "";
    boolean messageOn = false;
    int messageCounter = 0;
    Color messageColor;

    public boolean GameOver      = false; // win
    public boolean enemyGameOver = false; // lose

    public double playTime = 0;

    public GameUI(GamePanel gp) {
        this.gp   = gp;
        gameFont  = new Font("Arial", Font.PLAIN, 20);
        key       = new OBJ_key();
        key_image = key.image;
    }

    public void showMessage(String text, Color color) {
        message        = text;
        messageOn      = true;
        messageCounter = 0;
        messageColor   = color;
    }

    // ===== LOADING SCREEN =====
    public void drawLoadingScreen(Graphics2D g2) {
        int cx = gp.screenWidth  / 2;
        int cy = gp.screenHeight / 2;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(Color.WHITE);
        String text = "Loading...";
        g2.drawString(text, cx - g2.getFontMetrics().stringWidth(text) / 2, cy);

        // Simple animated dots using time
        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        g2.setColor(Color.LIGHT_GRAY);
        String sub = "Preparing the world, please wait";
        g2.drawString(sub, cx - g2.getFontMetrics().stringWidth(sub) / 2, cy + 50);
    }

    // ===== TITLE / START SCREEN =====
    public void drawTitleScreen(Graphics2D g2) {
        int cx = gp.screenWidth  / 2;
        int cy = gp.screenHeight / 2;

        // Semi-transparent dark overlay over the map
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Game title
        g2.setFont(new Font("Arial", Font.BOLD, 72));
        g2.setColor(Color.YELLOW);
        String title = "Hollow Quest";
        g2.drawString(title, cx - g2.getFontMetrics().stringWidth(title) / 2, cy - 60);

        // Subtitle
        g2.setFont(new Font("Arial", Font.ITALIC, 24));
        g2.setColor(Color.LIGHT_GRAY);
        String sub = "Collect keys, open doors, find the treasure";
        g2.drawString(sub, cx - g2.getFontMetrics().stringWidth(sub) / 2, cy - 15);

        // Pulsing start prompt
        long  now   = System.currentTimeMillis();
        float alpha = (float)(0.5 + 0.5 * Math.sin(now / 500.0));
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.setColor(new Color(1f, 1f, 1f, alpha));
        String prompt = "Press  ENTER  to start";
        g2.drawString(prompt, cx - g2.getFontMetrics().stringWidth(prompt) / 2, cy + 60);

        // Controls hint
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(new Color(180, 180, 180, 200));
        String ctrl = "Arrow Keys / WASD to move    |    Avoid the dragonflies!";
        g2.drawString(ctrl, cx - g2.getFontMetrics().stringWidth(ctrl) / 2, cy + 110);
    }

    // ===== MAIN DRAW (gameplay only) =====
    public void draw(Graphics2D g2) {
        if (GameOver) {
            drawWinScreen(g2);
        } else if (enemyGameOver) {
            drawEnemyGameOverScreen(g2);
        } else {
            drawHUD(g2);
        }
    }

    // ===== WIN SCREEN =====
    private void drawWinScreen(Graphics2D g2) {
        int cx = gp.screenWidth / 2;
        int cy = gp.screenHeight / 2;

        g2.setFont(new Font("Arial", Font.BOLD, 76));
        g2.setColor(Color.YELLOW);
        String text = "Congratulations! You Win!";
        g2.drawString(text, cx - g2.getFontMetrics().stringWidth(text) / 2, cy);

        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(Color.WHITE);
        String sub = "You found the treasure!";
        g2.drawString(sub, cx - g2.getFontMetrics().stringWidth(sub) / 2, cy + 100);

        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.drawString("Time: " + (int) playTime + "s", gp.screenWidth - 150, 30);

        long  now   = System.currentTimeMillis();
        float alpha = (float)(0.5 + 0.5 * Math.sin(now / 500.0));
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(new Color(1f, 1f, 0f, alpha));
        String prompt = "Press  ENTER  to play again";
        g2.drawString(prompt, cx - g2.getFontMetrics().stringWidth(prompt) / 2, cy + 170);
    }

    // ===== ENEMY GAME-OVER SCREEN =====
    private void drawEnemyGameOverScreen(Graphics2D g2) {
        int cx = gp.screenWidth  / 2;
        int cy = gp.screenHeight / 2;

        g2.setColor(new Color(120, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 96));
        g2.setColor(Color.RED);
        String title = "GAME OVER";
        g2.drawString(title, cx - g2.getFontMetrics().stringWidth(title) / 2, cy - 40);

        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(Color.WHITE);
        String sub = "You were caught by a dragonfly!";
        g2.drawString(sub, cx - g2.getFontMetrics().stringWidth(sub) / 2, cy + 40);

        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.setColor(Color.LIGHT_GRAY);
        String time = "Time survived: " + (int) playTime + "s";
        g2.drawString(time, cx - g2.getFontMetrics().stringWidth(time) / 2, cy + 95);

        long  now   = System.currentTimeMillis();
        float alpha = (float)(0.5 + 0.5 * Math.sin(now / 400.0));
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(new Color(1f, 1f, 0f, alpha));
        String prompt = "Press  ENTER  to play again";
        g2.drawString(prompt, cx - g2.getFontMetrics().stringWidth(prompt) / 2, cy + 155);
    }

    // ===== HUD =====
    private void drawHUD(Graphics2D g2) {
        g2.setFont(gameFont);
        g2.setColor(Color.WHITE);

        g2.drawImage(key_image, 5, 5, 48, 48, gp);
        g2.drawString(" X " + gp.player.hasKey, 48, 30);

        playTime += (double) 1 / gp.FPS;
        g2.drawString("Time: " + (int) playTime + "s", gp.screenWidth - 150, 30);

        if (messageOn) {
            int x = 300, y = 500, pad = 10;
            int tw = g2.getFontMetrics().stringWidth(message);
            int th = g2.getFontMetrics().getHeight();
            g2.setColor(Color.BLACK);
            g2.fillRect(x - pad, y - th, tw + 2 * pad, th + pad);
            g2.setColor(messageColor);
            g2.drawString(message, x, y);
            if (++messageCounter > 240) { messageOn = false; messageCounter = 0; }
        }

        if (gp.player.isSpeedBoostActive()) {
            int bx = 300, by = 550, bw = 200, bh = 20, pad = 2;
            float prog = (float) gp.player.getBootCounter() / gp.player.getSpeedBoostDuration();
            g2.setColor(Color.DARK_GRAY);  g2.fillRect(bx, by, bw, bh);
            g2.setColor(Color.CYAN);       g2.fillRect(bx, by, (int)(bw * (1 - prog)), bh);
            g2.setColor(Color.WHITE);      g2.drawRect(bx - pad, by - pad, bw + 2*pad, bh + 2*pad);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(Color.WHITE);
            g2.drawString("Speed Boost", bx + 5, by - 5);
        }
    }
}