package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;
import main.KeyHandler;
import utility.LoadImage;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    // Original 2-frame sprites per direction
    BufferedImage up1, up2, up_idle;
    BufferedImage down_idle, down1, down2;
    BufferedImage left1, left2, left_idle;
    BufferedImage right1, right2, right_idle;

    public final int screenX;
    public final int screenY;

    boolean isMoving = false;
    public int hasKey = 0;
    public int treasuresFound = 0;
    public boolean hasGrandKey = false;

    // ===== Speed Boost =====
    private int bootCounter = 0;
    private final int speedBoostDuration = 900;
    private boolean speedBoostActive = false;

    // ── Dragonfly damage invincibility (5s = 1000 frames at 200fps) ────────
    private int  invincibleTimer   = 0;
    private static final int INVINCIBLE_DURATION = 1000;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp   = gp;
        this.keyH = keyH;
        setDefaultValues();
        loadPlayerImages();  // Images loaded ONCE here — never reloaded on restart

        screenX = gp.screenWidth  / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle(20, 28, 20, 28);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    // Pre-scale a sprite to exact tileSize once at load — eliminates runtime
    // scaling artifacts (black edge flicker) when drawing at 1:1 pixels.
    private BufferedImage scaleImage(BufferedImage src) {
        if (src == null) return null;
        BufferedImage scaled = new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                            java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, gp.tileSize, gp.tileSize, null);
        g2.dispose();
        return scaled;
    }

    public void loadPlayerImages() {
        up1      = scaleImage(LoadImage.load("/player/up_1.png"));
        up2      = scaleImage(LoadImage.load("/player/up_2.png"));
        up_idle  = scaleImage(LoadImage.load("/player/up_idle.png"));

        down1     = scaleImage(LoadImage.load("/player/down_1.png"));
        down2     = scaleImage(LoadImage.load("/player/down_2.png"));
        down_idle = scaleImage(LoadImage.load("/player/down_idle.png"));

        left1     = scaleImage(LoadImage.load("/player/left_1.png"));
        left2     = scaleImage(LoadImage.load("/player/left_2.png"));
        left_idle = left2;

        right1     = scaleImage(LoadImage.load("/player/right_1.png"));
        right2     = scaleImage(LoadImage.load("/player/right_2.png"));
        right_idle = right2;
    }

    public void setDefaultValues() {
        hasGrandKey = false;
        treasuresFound = 0;
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        Speed  = 1.0f;
    }

    public void update() {
        isMoving = false;

        if (keyH.upPressed)    { direction = "up";    isMoving = (worldY - Speed >= 0); }
        if (keyH.downPressed)  { direction = "down";  isMoving = (worldY + Speed <= gp.worldHeight - gp.tileSize); }
        if (keyH.leftPressed)  { direction = "left";  isMoving = (worldX - Speed >= 0); }
        if (keyH.rightPressed) { direction = "right"; isMoving = (worldX + Speed <= gp.worldWidth  - gp.tileSize); }

        collisionOn = false;
        gp.collisionChecker.checkTile(this);
        pickUpObject();

        if (!collisionOn && isMoving) {
            switch (direction) {
                case "up":    worldY -= Speed; break;
                case "down":  worldY += Speed; break;
                case "left":  worldX -= Speed; break;
                case "right": worldX += Speed; break;
            }
        }

        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 30) {
                spriteNum     = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0;
        }

        // Invincibility countdown
        if (invincibleTimer > 0) invincibleTimer--;

        if (speedBoostActive) {
            bootCounter++;
            if (bootCounter > speedBoostDuration) {
                speedBoostActive = false;
                Speed      -= 1;
                bootCounter = 0;
                gp.gameUI.showMessage("Speed boost ended.", Color.GRAY);
            }
        }
    }

    public void pickUpObject() {
        int objIndex = gp.collisionChecker.checkObject(this, true);
        if (objIndex != 999) {
            switch (gp.obj[objIndex].name) {
                case "Key":
                    gp.playSE(1);
                    gp.obj[objIndex] = null;
                    hasKey++;
                    gp.gameUI.showMessage("You picked up a key!", Color.YELLOW);
                    break;
                case "Grand Door":
                    if (hasGrandKey) {
                        gp.playSE(0);
                        gp.obj[objIndex] = null;
                        gp.gameUI.showMessage("The Grand Gate opens!", java.awt.Color.YELLOW);
                    } else {
                        gp.gameUI.showMessage("You need the Grand Key!", java.awt.Color.RED);
                    }
                    break;
                case "Door":
                    if (hasKey > 0) {
                        gp.playSE(0);
                        hasKey--;
                        gp.obj[objIndex] = null;
                        gp.gameUI.showMessage("You opened a door!", Color.GREEN);
                    } else {
                        gp.gameUI.showMessage("You need a key to open this door.", Color.RED);
                    }
                    break;
                case "Health":
                    gp.playerHealth = Math.min(100, gp.playerHealth + 50);
                    gp.obj[objIndex] = null;
                    gp.playSE(3); // same sound as boots
                    gp.gameUI.showMessage("Health +50%!", java.awt.Color.GREEN);
                    break;
                case "Grand Key":
                    hasGrandKey = true;
                    gp.obj[objIndex] = null;
                    gp.playSE(2);
                    gp.gameUI.showMessage("You found the GRAND KEY!", java.awt.Color.YELLOW);
                    break;
                case "Boots":
                    gp.playSE(3);
                    gp.obj[objIndex] = null;
                    Speed           += 1;
                    speedBoostActive = true;
                    bootCounter      = 0;
                    gp.gameUI.showMessage("Speed Boost +++++", Color.BLUE);
                    break;
                case "Treasure Box":
                    gp.playSE(2);
                    gp.obj[objIndex] = null;
                    gp.stopSound(5);
                    gp.gameUI.showMessage("You found the treasure! You win!", Color.MAGENTA);
                    gp.gameUI.GameOver = true;
                    break;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (!isMoving) {
            switch (direction) {
                case "up":    image = up_idle;    break;
                case "down":  image = down_idle;  break;
                case "left":  image = left_idle;  break;
                case "right": image = right_idle; break;
            }
        } else {
            switch (direction) {
                case "up":    image = (spriteNum == 1) ? up1    : up2;    break;
                case "down":  image = (spriteNum == 1) ? down1  : down2;  break;
                case "left":  image = (spriteNum == 1) ? left1  : left2;  break;
                case "right": image = (spriteNum == 1) ? right1 : right2; break;
            }
        }

        int drawX = worldX - gp.camX;
        int drawY = worldY - gp.camY;
        g2.drawImage(image, drawX, drawY, null); // 1:1 — already pre-scaled at load
    }

    public boolean isSpeedBoostActive()  { return speedBoostActive; }
    public boolean isInvincible()         { return invincibleTimer > 0; }
    public void    startInvincibility()   { invincibleTimer = INVINCIBLE_DURATION; }
    public int getBootCounter()          { return bootCounter; }
    public int getSpeedBoostDuration()   { return speedBoostDuration; }
}