package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
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
    public boolean thornGameOver = false; // hit by thorn

    // ── Map selection ─────────────────────────────────────────────────────
    private int mapCursor = 0; // 0=map_02, 1=map_03
    private static final String[] MAP_NAMES   = {"map_02", "map_03"};
    private static final String[] MAP_LABELS  = {"World A — The Grasslands", "World B — The Dungeon"};
    private boolean cursorMoved = false;

    // ── Hit flash effect ──────────────────────────────────────────────────
    private boolean hitEffectActive = false;
    private long    hitEffectStart  = 0;
    private static final long HIT_EFFECT_MS = 500; // 0.5 seconds

    public void triggerHitEffect() {
        hitEffectActive = true;
        hitEffectStart  = System.currentTimeMillis();
    }

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
        } else if (thornGameOver) {
            drawThornGameOverScreen(g2);
        } else {
            drawHUD(g2);
            drawHealthBar(g2);
            drawHitEffect(g2);
            drawInvincibilityFlash(g2);
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

    private void drawHealthBar(Graphics2D g2) {
        int barW  = 120;
        int barH  = 12;
        int x     = 5;      // aligned with key icon on the left
        int y     = 58;     // just below the key icon (48px tall + 10px gap)
        int hp    = gp.playerHealth;

        // Background track
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fill(new RoundRectangle2D.Float(x - 2, y - 2, barW + 4, barH + 4, 8, 8));

        // Red danger flash when hp <= 40
        float filled = hp / 100f;
        Color barColor;
        if (hp > 60)       barColor = new Color(60, 200, 60);
        else if (hp > 40)  barColor = new Color(220, 180, 0);
        else {
            float pulse = (float)(0.6 + 0.4 * Math.sin(System.currentTimeMillis() / 150.0));
            barColor = new Color(1f, 0f, 0f, pulse);
        }

        g2.setColor(barColor);
        g2.fill(new RoundRectangle2D.Float(x, y, barW * filled, barH, 6, 6));

        // Border
        g2.setColor(new Color(255, 255, 255, 180));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(x - 2, y - 2, barW + 4, barH + 4, 8, 8));
        g2.setStroke(new BasicStroke(1f));

        // Label
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.setColor(Color.WHITE);
        g2.drawString("HP " + hp + "%", x, y + barH + 13);
    }

    private void drawInvincibilityFlash(Graphics2D g2) {
        if (!gp.player.isInvincible()) return;
        // Blink: visible every other 10-frame block
        long t = System.currentTimeMillis();
        if ((t / 80) % 2 == 0) return;
        int drawX = gp.player.worldX - gp.camX;
        int drawY = gp.player.worldY - gp.camY;
        int s     = gp.tileSize;
        g2.setColor(new Color(255, 255, 255, 160));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(drawX, drawY, s, s);
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawHitEffect(Graphics2D g2) {
        if (!hitEffectActive) return;
        long elapsed = System.currentTimeMillis() - hitEffectStart;
        if (elapsed >= HIT_EFFECT_MS) { hitEffectActive = false; return; }

        // Progress 0→1 over 500ms
        float t = elapsed / (float) HIT_EFFECT_MS;

        // Circle starts at screenEdge radius, shrinks to 0
        int maxR  = (int)(Math.sqrt(gp.screenWidth * gp.screenWidth + gp.screenHeight * gp.screenHeight) / 2) + 20;
        int r     = (int)(maxR * (1f - t));
        int cx    = gp.screenWidth  / 2;
        int cy    = gp.screenHeight / 2;
        float alpha = (1f - t) * 0.55f; // fades as it shrinks

        g2.setColor(new Color(1f, 0f, 0f, alpha));
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);
    }

    // ===== MAP SELECTION =====
    public void handleMapSelect(main.KeyHandler keyH) {
        if (keyH.upPressed || keyH.leftPressed) {
            if (!cursorMoved) {
                mapCursor = (mapCursor - 1 + MAP_NAMES.length) % MAP_NAMES.length;
                gp.selectedMap = MAP_NAMES[mapCursor];
                cursorMoved = true;
            }
        } else if (keyH.downPressed || keyH.rightPressed) {
            if (!cursorMoved) {
                mapCursor = (mapCursor + 1) % MAP_NAMES.length;
                gp.selectedMap = MAP_NAMES[mapCursor];
                cursorMoved = true;
            }
        } else {
            cursorMoved = false;
        }
    }

    public void drawMapSelectScreen(Graphics2D g2) {
        int cx = gp.screenWidth  / 2;
        int cy = gp.screenHeight / 2;

        // Dark overlay
        g2.setColor(new Color(10, 15, 30, 220));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Title
        g2.setFont(new Font("Arial", Font.BOLD, 52));
        g2.setColor(new Color(255, 220, 80));
        String title = "SELECT YOUR WORLD";
        g2.drawString(title, cx - g2.getFontMetrics().stringWidth(title)/2, cy - 120);

        // Map options
        for (int i = 0; i < MAP_LABELS.length; i++) {
            boolean selected = (i == mapCursor);
            int oy = cy - 30 + i * 90;

            // Card background
            int cardW = 460; int cardH = 70;
            int cardX = cx - cardW/2;
            g2.setColor(selected ? new Color(60, 120, 60, 200) : new Color(30, 40, 60, 180));
            g2.fill(new java.awt.geom.RoundRectangle2D.Float(cardX, oy - 46, cardW, cardH, 16, 16));

            // Border
            g2.setStroke(new BasicStroke(selected ? 3f : 1f));
            g2.setColor(selected ? new Color(255, 220, 80) : new Color(100, 120, 160));
            g2.draw(new java.awt.geom.RoundRectangle2D.Float(cardX, oy - 46, cardW, cardH, 16, 16));
            g2.setStroke(new BasicStroke(1f));

            // Arrow indicator
            if (selected) {
                g2.setColor(new Color(255, 220, 80));
                g2.setFont(new Font("Arial", Font.BOLD, 28));
                g2.drawString("▶", cardX + 14, oy);
            }

            // Map label
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            g2.setColor(selected ? Color.WHITE : new Color(160, 180, 200));
            g2.drawString(MAP_LABELS[i], cardX + 46, oy);
        }

        // Instructions
        long now = System.currentTimeMillis();
        float alpha = (float)(0.5 + 0.5 * Math.sin(now / 500.0));
        g2.setColor(new Color(1f, 1f, 1f, alpha));
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        String prompt = "↑ / ↓  to choose      ENTER to begin";
        g2.drawString(prompt, cx - g2.getFontMetrics().stringWidth(prompt)/2, cy + 160);
    }

    // ===== THORN GAME-OVER SCREEN =====
    private void drawThornGameOverScreen(Graphics2D g2) {
        int cx = gp.screenWidth  / 2;
        int cy = gp.screenHeight / 2;

        g2.setColor(new Color(40, 20, 0, 190));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 96));
        g2.setColor(new Color(139, 69, 19));
        String title = "GAME OVER";
        g2.drawString(title, cx - g2.getFontMetrics().stringWidth(title) / 2, cy - 40);

        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(Color.WHITE);
        String sub = "You were struck by a thorn!";
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