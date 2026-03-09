package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;
import utility.LoadImage;

public class Enemy extends Entity {

    GamePanel gp;

    // ── Shared sprite cache — loaded ONCE for ALL enemy instances ────────
    // Static so they are shared across all 5 Enemy objects, not reloaded each time
    private static BufferedImage[] leftFrames  = null;
    private static BufferedImage[] rightFrames = null;

    // Pre-scale helper — same fix as Player, prevents runtime scaling flicker
    private static BufferedImage scaleImage(BufferedImage src, int size) {
        if (src == null) return null;
        BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                            java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, size, size, null);
        g2.dispose();
        return scaled;
    }

    public Enemy(GamePanel gp, int tileX, int tileY) {
        this.gp     = gp;
        this.worldX = gp.tileSize * tileX;
        this.worldY = gp.tileSize * tileY;
        this.Speed  = 1.0f;
        this.direction = "left";

        solidArea = new Rectangle(8, 8, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // Only load images the very first time — all subsequent enemies reuse them
        if (leftFrames == null) {
            leftFrames  = new BufferedImage[16];
            rightFrames = new BufferedImage[16];
            for (int i = 0; i < 16; i++) {
                leftFrames[i]  = scaleImage(LoadImage.load("/enemy/enemy_left_"  + (i + 1) + ".png"), gp.tileSize);
                rightFrames[i] = scaleImage(LoadImage.load("/enemy/enemy_right_" + (i + 1) + ".png"), gp.tileSize);
            }
        }
    }

    public void update() {
        collisionOn = false;
        gp.collisionChecker.checkTile(this);

        if (collisionOn) {
            direction = direction.equals("left") ? "right" : "left";
        } else {
            worldX += direction.equals("left") ? -Speed : Speed;
        }

        spriteCounter++;
        if (spriteCounter > 5) {
            spriteNum     = (spriteNum % 16) + 1;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        int drawX = worldX - gp.camX;
        int drawY = worldY - gp.camY;

        if (drawX + gp.tileSize < 0 || drawX > gp.screenWidth ||
            drawY + gp.tileSize < 0 || drawY > gp.screenHeight) return;

        BufferedImage image = direction.equals("left")
                ? leftFrames[spriteNum - 1]
                : rightFrames[spriteNum - 1];

        if (image != null) {
            g2.drawImage(image, drawX, drawY, null); // 1:1 — pre-scaled at load
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(drawX + 4, drawY + 4, gp.tileSize - 8, gp.tileSize - 8);
        }
    }

    // Package-level accessors so TrackerEnemy can reuse the same static frame cache
    static BufferedImage getLeftFrame(int idx)  { return leftFrames[idx]; }
    static BufferedImage getRightFrame(int idx) { return rightFrames[idx]; }
}