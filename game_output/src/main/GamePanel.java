package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import entity.Enemy;
import entity.HornetEnemy;
import entity.Player;
import object.SuperObject;
import tiles.TileManager;
import utility.CollisionChecker;
import utility.SoundLoader;

public class GamePanel extends JPanel implements Runnable {

    // ===== INTERNAL GAME RESOLUTION — 4:3 (1024x768 logical) =====
    final static int originalTileSize    = 16;
    final static int scale               = 4;
    public final int tileSize            = scale * originalTileSize; // 64px
    public static final int maxScreenCol = 16;
    public static final int maxScreenRow = 12;
    public final int screenWidth         = tileSize * maxScreenCol;  // 1024
    public final int screenHeight        = tileSize * maxScreenRow;  // 768

    // ===== WORLD =====
    public static final int maxWorldCol = 50;
    public static final int maxWorldRow = 50;
    public final int worldWidth         = tileSize * maxWorldCol;
    public final int worldHeight        = tileSize * maxWorldRow;

    // ===== CAMERA =====
    public int camX, camY;

    // ===== FULLSCREEN SCALING =====
    private int displayWidth, displayHeight;
    private int drawOffsetX, drawOffsetY;
    private float drawScale;
    private final BufferedImage gameBuffer; // Offscreen canvas — created once

    // ===== GAME STATES =====
    public static final int STATE_LOADING = 0;
    public static final int STATE_TITLE   = 1;
    public static final int STATE_PLAYING = 2;
    public int gameState = STATE_LOADING;

    // ===== SYSTEMS =====
    KeyHandler keyH                          = new KeyHandler();
    SoundLoader soundLoader                  = new SoundLoader();
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public Player player                     = new Player(this, keyH);
    public TileManager tileM                 = new TileManager(this);
    public SuperObject obj[]                 = new SuperObject[10];

    // ===== ENEMIES =====
    public Enemy[]        enemies  = new Enemy[5];
    public HornetEnemy[]   hornets  = new HornetEnemy[2];   // Tracking hornets

    private boolean restartHandled = false;
    public Thread gameThread;
    public GameUI gameUI = new GameUI(this);

    // Target 60 FPS — matches typical monitor refresh, far less CPU load than 200
    int FPS = 200;

    // ===== CONSTRUCTOR =====
    public GamePanel() {
        java.awt.Rectangle sb = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getBounds();
        displayWidth  = sb.width;
        displayHeight = sb.height;

        float sx = (float) displayWidth  / screenWidth;
        float sy = (float) displayHeight / screenHeight;
        drawScale   = Math.min(sx, sy);
        drawOffsetX = (displayWidth  - (int)(screenWidth  * drawScale)) / 2;
        drawOffsetY = (displayHeight - (int)(screenHeight * drawScale)) / 2;

        // Single offscreen buffer — never recreated
        gameBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

        this.setPreferredSize(new Dimension(displayWidth, displayHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame() {
        utility.ObjectSetter oSetter = new utility.ObjectSetter(this);
        oSetter.setObject();

        enemies[0] = new Enemy(this, 35, 21);
        enemies[1] = new Enemy(this, 37,  7);
        enemies[2] = new Enemy(this, 33,  8);
        enemies[3] = new Enemy(this, 20,  7);
        enemies[4] = new Enemy(this, 22, 37);


        // Hornets — placed on open land far from player spawn (23,21)
        hornets[0] = new HornetEnemy(this, 39,  9); // top-right road corridor
        hornets[1] = new HornetEnemy(this, 11, 32); // bottom-left grass area
    }

    private void resetGame() {
        for (int i = 0; i < obj.length;     i++) obj[i]     = null;
        for (int i = 0; i < enemies.length;  i++) enemies[i]  = null;
        for (int i = 0; i < hornets.length;  i++) hornets[i]  = null;
        player.setDefaultValues();
        player.hasKey        = 0;
        gameUI.playTime      = 0;
        gameUI.GameOver      = false;
        gameUI.enemyGameOver = false;
        setupGame();
        playSound(5);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void updateCamera() {
        camX = player.worldX - screenWidth  / 2;
        camY = player.worldY - screenHeight / 2;
        if (camX < 0) camX = 0;
        if (camY < 0) camY = 0;
        if (camX > worldWidth  - screenWidth)  camX = worldWidth  - screenWidth;
        if (camY > worldHeight - screenHeight) camY = worldHeight - screenHeight;
    }

    // ===== GAME LOOP — hybrid sleep + spin for precise frame timing =====
    // Thread.sleep() alone has ~15ms granularity on Windows (giving ~66 FPS max).
    // Fix: sleep until 2ms before the deadline, then spin-wait the remainder.
    // This gives true 200 FPS frame pacing with minimal CPU waste.
    @Override
    public void run() {
        gameState = STATE_LOADING;
        repaint();

        setupGame();
        try { Thread.sleep(600); } catch (InterruptedException e) {}

        gameState = STATE_TITLE;
        repaint();

        final long frameTimeNs = 1_000_000_000L / FPS; // nanoseconds per frame
        final long spinThresholdNs = 2_000_000L;        // spin for last 2ms

        long nextFrameTime = System.nanoTime();

        while (gameThread != null) {
            update();
            repaint();

            nextFrameTime += frameTimeNs;

            // Sleep in chunks until 2ms before the deadline
            long now = System.nanoTime();
            long sleepMs = (nextFrameTime - now - spinThresholdNs) / 1_000_000L;
            if (sleepMs > 0) {
                try { Thread.sleep(sleepMs); } catch (InterruptedException e) {}
            }

            // Spin-wait the final 2ms for precise wakeup
            while (System.nanoTime() < nextFrameTime) {
                Thread.yield(); // yield instead of empty spin — reduces CPU heat
            }
        }
    }

    // ===== UPDATE =====
    public void update() {
        if (gameState == STATE_TITLE) {
            if (keyH.enterPressed && !restartHandled) {
                restartHandled = true;
                gameState = STATE_PLAYING;
                playSound(5);
            }
            if (!keyH.enterPressed) restartHandled = false;
            return;
        }

        if (gameUI.enemyGameOver) {
            if (keyH.enterPressed && !restartHandled) {
                restartHandled = true;
                stopSound(5);
                resetGame();
                gameState = STATE_PLAYING;
            }
            if (!keyH.enterPressed) restartHandled = false;
            return;
        }

        if (gameUI.GameOver) {
            if (keyH.enterPressed && !restartHandled) {
                restartHandled = true;
                stopSound(5);
                resetGame();
                gameState = STATE_PLAYING;
            }
            if (!keyH.enterPressed) restartHandled = false;
            return;
        }

        restartHandled = false;
        player.update();
        for (Enemy e          : enemies)  { if (e != null) e.update(); }
        for (HornetEnemy h    : hornets)  { if (h != null) h.update(); }

        if (collisionChecker.checkEnemyContact(enemies) ||
            collisionChecker.checkHornetContact(hornets)) {
            stopSound(5);
            gameUI.enemyGameOver = true;
        }

        updateCamera();
    }

    // ===== RENDER — draw to buffer, then scale once to screen =====
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Step 1: draw everything into the fixed-size offscreen buffer
        Graphics2D g2 = gameBuffer.createGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        switch (gameState) {
            case STATE_LOADING:
                gameUI.drawLoadingScreen(g2);
                break;
            case STATE_TITLE:
                tileM.draw(g2);
                gameUI.drawTitleScreen(g2);
                break;
            default:
                tileM.draw(g2);
                for (SuperObject o : obj)     { if (o != null) o.draw(g2, this); }
                for (Enemy e       : enemies)  { if (e != null) e.draw(g2); }
                for (HornetEnemy h  : hornets)  { if (h != null) h.draw(g2); }
                player.draw(g2);
                gameUI.draw(g2);
                break;
        }
        g2.dispose();

        // Step 2: scale buffer to fullscreen with styled black surround
        Graphics2D gs = (Graphics2D) g;

        // Fill entire display with deep black
        gs.setColor(new Color(8, 8, 8));
        gs.fillRect(0, 0, displayWidth, displayHeight);

        int scaledW = (int)(screenWidth  * drawScale);
        int scaledH = (int)(screenHeight * drawScale);

        // Thin dark border around game canvas for a "screen frame" feel
        gs.setColor(new Color(30, 30, 30));
        gs.fillRect(drawOffsetX - 3, drawOffsetY - 3, scaledW + 6, scaledH + 6);

        // Draw the game canvas — NEAREST_NEIGHBOR keeps pixel-art crisp
        gs.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        gs.drawImage(gameBuffer, drawOffsetX, drawOffsetY, scaledW, scaledH, null);

        // Subtle vignette on the black bars to blend nicely
        gs.setColor(new Color(0, 0, 0, 180));
        // Left bar
        if (drawOffsetX > 0)
            gs.fillRect(0, 0, drawOffsetX, displayHeight);
        // Right bar
        if (drawOffsetX > 0)
            gs.fillRect(drawOffsetX + scaledW, 0, drawOffsetX + 1, displayHeight);
        // Top bar
        if (drawOffsetY > 0)
            gs.fillRect(0, 0, displayWidth, drawOffsetY);
        // Bottom bar
        if (drawOffsetY > 0)
            gs.fillRect(0, drawOffsetY + scaledH, displayWidth, drawOffsetY + 1);

        gs.dispose();
    }

    public void playSound(int i) { soundLoader.play(i); soundLoader.loop(i); }
    public void stopSound(int i) { soundLoader.stop(i); }
    public void playSE(int i)    { soundLoader.play(i); }
}