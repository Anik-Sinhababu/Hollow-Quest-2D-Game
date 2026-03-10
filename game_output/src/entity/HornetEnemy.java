package entity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import main.GamePanel;
import utility.LoadImage;

public class HornetEnemy extends Entity {

    GamePanel gp;

    // ── 8-direction sprite cache (static — loaded once) ───────────────────
    private static BufferedImage[] frRight = null;
    private static BufferedImage[] frLeft  = null;
    private static final int FC = 12;

    // ── Float speed ───────────────────────────────────────────────────────
    private float fSpeed;
    private static final float PATROL_SPEED = 0.8f;
    private static final float CHASE_SPEED  = 0.75f;

    // ── View ranges ───────────────────────────────────────────────────────
    private static final int VIEW_TILES = 7;
    private static final int LOSE_TILES = 10;

    // ── States ────────────────────────────────────────────────────────────
    private enum State { PATROL, ALERTED, CHASE }
    private State state = State.PATROL;
    private int alertTimer = 0;
    private static final int ALERT_DURATION = 100;

    // ── Sub-pixel accumulator for smooth float movement ───────────────────
    private float accumX = 0, accumY = 0;

    // ── Current 8-dir label (used for sprite selection) ───────────────────
    private String dir8 = "right";

    public HornetEnemy(GamePanel gp, int tileX, int tileY) {
        this.gp     = gp;
        this.worldX = gp.tileSize * tileX;
        this.worldY = gp.tileSize * tileY;
        this.fSpeed = PATROL_SPEED;
        this.Speed  = 1;
        this.direction = "left"; // used by collision checker

        solidArea = new java.awt.Rectangle(10, 10, 44, 44);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        if (frRight == null) {
            frRight = load("left");   // sprites are mirrored — swap to correct
            frLeft  = load("right");
        }
    }

    private BufferedImage[] load(String d) {
        BufferedImage[] arr = new BufferedImage[FC];
        int s = gp.tileSize;
        for (int i = 0; i < FC; i++) {
            BufferedImage raw = LoadImage.load("/hornet/hornet_" + d + "_" + (i+1) + ".png");
            if (raw == null) continue;
            BufferedImage scaled = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2 = scaled.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(raw, 0, 0, s, s, null);
            g2.dispose();
            arr[i] = scaled;
        }
        return arr;
    }

    // ── Direction: only left or right based on horizontal component ────────
    private String toDir8(float dx, float dy) {
        return dx >= 0 ? "right" : "left";
    }

    public void update() {
        int ts = gp.tileSize;

        // Player centre
        float px = gp.player.worldX + gp.player.solidArea.x + gp.player.solidArea.width  / 2f;
        float py = gp.player.worldY + gp.player.solidArea.y + gp.player.solidArea.height / 2f;
        float ex = worldX + solidArea.x + solidArea.width  / 2f;
        float ey = worldY + solidArea.y + solidArea.height / 2f;

        float rawDx = px - ex;
        float rawDy = py - ey;
        double dist = Math.sqrt(rawDx * rawDx + rawDy * rawDy);

        // ── State machine ─────────────────────────────────────────────────
        switch (state) {
            case PATROL:
                if (dist < VIEW_TILES * ts) {
                    state = State.ALERTED;
                    alertTimer = 0;
                    fSpeed = 0.3f;
                }
                break;
            case ALERTED:
                if (++alertTimer >= ALERT_DURATION) {
                    state  = State.CHASE;
                    fSpeed = 0;
                }
                break;
            case CHASE:
                if (fSpeed < CHASE_SPEED) fSpeed = Math.min(fSpeed + 0.02f, CHASE_SPEED);
                if (dist > LOSE_TILES * ts) {
                    state  = State.PATROL;
                    fSpeed = PATROL_SPEED;
                    direction = "left";
                }
                break;
        }

        // ── Movement ──────────────────────────────────────────────────────
        if (state == State.PATROL) {
            // Simple left/right patrol with collision awareness
            direction   = direction.equals("left") ? "left" : "right";
            collisionOn = false;
            gp.collisionChecker.checkTile(this);
            if (collisionOn) {
                direction = direction.equals("left") ? "right" : "left";
            } else {
                worldX += direction.equals("left") ? -(int) fSpeed : (int) fSpeed;
            }
            dir8 = direction;

        } else if (state == State.CHASE && fSpeed > 0) {
            // True Euclidean chase — normalise vector and accumulate sub-pixels
            float len = (float) dist;
            if (len > 0) {
                float nx = rawDx / len;
                float ny = rawDy / len;

                // Pick sprite direction from the actual movement vector
                dir8 = toDir8(nx, ny);

                accumX += nx * fSpeed;
                accumY += ny * fSpeed;

                int moveX = (int) accumX;
                int moveY = (int) accumY;
                accumX -= moveX;
                accumY -= moveY;

                // Move X — set cardinal direction for collision checker
                if (moveX != 0) {
                    direction   = moveX < 0 ? "left" : "right";
                    collisionOn = false;
                    gp.collisionChecker.checkTile(this);
                    if (!collisionOn) worldX += moveX;
                }
                // Move Y — separate axis so diagonals slide along walls
                if (moveY != 0) {
                    direction   = moveY < 0 ? "up" : "down";
                    collisionOn = false;
                    gp.collisionChecker.checkTile(this);
                    if (!collisionOn) worldY += moveY;
                }
            }
        }

        // ── Animate ───────────────────────────────────────────────────────
        spriteCounter++;
        if (spriteCounter > 4) {
            spriteNum     = (spriteNum % FC) + 1;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        int drawX = worldX - gp.camX;
        int drawY = worldY - gp.camY;

        if (drawX + gp.tileSize < 0 || drawX > gp.screenWidth ||
            drawY + gp.tileSize < 0 || drawY > gp.screenHeight) return;

        // Pick sprite array from 8-dir label
        BufferedImage[] frames = dir8.equals("left") ? frLeft : frRight;

        BufferedImage img = (frames != null && frames[spriteNum - 1] != null)
                ? frames[spriteNum - 1] : null;

        if (img != null) g2.drawImage(img, drawX, drawY, null);
        else {
            g2.setColor(Color.ORANGE);
            g2.fillRect(drawX + 4, drawY + 4, gp.tileSize - 8, gp.tileSize - 8);
        }

        int cx = drawX + gp.tileSize / 2;
        int cy = drawY + gp.tileSize / 2;

        // ── Patrol: black view ring ───────────────────────────────────────
        if (state == State.PATROL) {
            int r = VIEW_TILES * gp.tileSize;
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(new Color(0, 0, 0, 140));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            g2.setStroke(new BasicStroke(1f));
        }

        // ── Alerted: exclamation mark ─────────────────────────────────────
        if (state == State.ALERTED && (alertTimer / 10) % 2 == 0) {
            g2.setFont(new Font("Arial", Font.BOLD, 34));
            g2.setColor(Color.WHITE);
            g2.drawString("!", cx - 6, drawY - 8);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 34));
            // thin outline for readability
            g2.drawString("!", cx - 7, drawY - 7);
        }

        // ── Chase: pulsing semi-transparent black shrink/expand circle ────
        if (state == State.CHASE) {
            double pulse = Math.sin(System.currentTimeMillis() / 200.0); // -1..1
            int baseR = gp.tileSize;
            int r     = baseR + (int)(pulse * (gp.tileSize / 3));       // shrinks and expands
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(new Color(0, 0, 0, 120));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            g2.setStroke(new BasicStroke(1f));
        }
    }
}