package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import main.GamePanel;
import utility.LoadImage;

public class Thorn {

    GamePanel gp;

    // World position (float for smooth movement)
    public float worldX, worldY;

    // Normalised direction vector
    private float dx, dy;

    // Speed — starts high, decays per tile crossed
    private float speed;
    private static final float START_SPEED  = 3.0f;
    private static final float SPEED_DECAY  = 0.4f;  // lost per tile crossed

    // Track how many tiles have been crossed
    private float distTravelled = 0;
    private float lastTileCheck = 0;

    // Direction label for sprite
    private final String dirLabel;

    // Active flag — set false to destroy this thorn
    public boolean active = true;

    // Collision box (small — just the tip)
    public java.awt.Rectangle solidArea;

    // Shared sprite cache
    private static final Map<String, BufferedImage> sprites = new HashMap<>();

    public Thorn(GamePanel gp, float startX, float startY, float dx, float dy, String dirLabel) {
        this.gp       = gp;
        this.worldX   = startX;
        this.worldY   = startY;
        this.dx       = dx;
        this.dy       = dy;
        this.dirLabel = dirLabel;
        this.speed    = START_SPEED;

        int sz = gp.tileSize / 4;
        solidArea = new java.awt.Rectangle(sz, sz, sz, sz);

        // Play launch sound
        gp.playSE(6);

        // Load sprites once
        if (sprites.isEmpty()) {
            for (String d : new String[]{"right","left","up","down","ne","nw","se","sw"}) {
                BufferedImage raw = LoadImage.load("/thorn/thorn_" + d + ".png");
                sprites.put(d, raw);
            }
        }
    }

    public void update() {
        if (!active) return;

        // Move along direction vector
        worldX += dx * speed;
        worldY += dy * speed;
        distTravelled += speed;

        // Decay speed every tile crossed
        float tilesCrossed = distTravelled / gp.tileSize;
        if (tilesCrossed - lastTileCheck >= 1.0f) {
            speed = Math.max(0, speed - SPEED_DECAY);
            lastTileCheck = tilesCrossed;
        }

        // Destroy if stopped
        if (speed <= 0) { active = false; return; }

        // Destroy if off visible screen
        int drawX = (int)worldX - gp.camX;
        int drawY = (int)worldY - gp.camY;
        if (drawX < -gp.tileSize || drawX > gp.screenWidth + gp.tileSize ||
            drawY < -gp.tileSize || drawY > gp.screenHeight + gp.tileSize) {
            active = false; return;
        }

        // Tile collision — destroy on solid tiles
        checkTileCollision();
    }

    private void checkTileCollision() {
        int tileCol = (int)(worldX + solidArea.x + solidArea.width  / 2) / gp.tileSize;
        int tileRow = (int)(worldY + solidArea.y + solidArea.height / 2) / gp.tileSize;

        int maxRow = gp.tileM.map.length - 1;
        int maxCol = gp.tileM.map[0].length - 1;
        tileRow = Math.max(0, Math.min(tileRow, maxRow));
        tileCol = Math.max(0, Math.min(tileCol, maxCol));

        int tileCode = gp.tileM.map[tileRow][tileCol];
        if (gp.tileM.tiles[tileCode].collision) {
            active = false;
        }
    }

    public boolean hitsPlayer() {
        if (!active) return false;
        java.awt.Rectangle pr = new java.awt.Rectangle(
            gp.player.worldX + gp.player.solidArea.x,
            gp.player.worldY + gp.player.solidArea.y,
            gp.player.solidArea.width,
            gp.player.solidArea.height
        );
        java.awt.Rectangle tr = new java.awt.Rectangle(
            (int)worldX + solidArea.x,
            (int)worldY + solidArea.y,
            solidArea.width,
            solidArea.height
        );
        boolean hit = pr.intersects(tr);
        if (hit) gp.playSE(7);
        return hit;
    }

    public void draw(Graphics2D g2) {
        if (!active) return;
        BufferedImage img = sprites.get(dirLabel);
        if (img == null) return;

        int drawX = (int)worldX - gp.camX;
        int drawY = (int)worldY - gp.camY;
        int size  = gp.tileSize;
        g2.drawImage(img, drawX, drawY, size, size, null);
    }
}