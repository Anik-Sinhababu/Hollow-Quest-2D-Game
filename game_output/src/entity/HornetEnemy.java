package entity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import main.GamePanel;
import utility.LoadImage;

/**
 * A tracking hornet with:
 *  - 4 directional sprites (left/right/up/down), 12 frames each
 *  - float speed for subtle acceleration
 *  - 3 states: PATROL (circles spawn) → ALERTED (! flash) → CHASE (hunt player)
 *  - Loses interest when player escapes beyond LOSE_RANGE tiles
 */
public class HornetEnemy extends Entity {

    GamePanel gp;

    // ── Sprite cache (static — shared across all hornet instances) ────────
    private static BufferedImage[] framesRight = null;
    private static BufferedImage[] framesLeft  = null;
    private static BufferedImage[] framesUp    = null;
    private static BufferedImage[] framesDown  = null;
    private static final int FRAME_COUNT = 12;

    // ── Float speed override (Entity.Speed is int, we shadow it) ─────────
    private float fSpeed;
    private static final float PATROL_SPEED = 0.8f;
    private static final float CHASE_SPEED  = 0.75f; // slightly slower than player (1.0f)  // noticeably faster than dragonfly

    // ── View ranges (in tiles) ────────────────────────────────────────────
    private static final int VIEW_TILES = 7;
    private static final int LOSE_TILES = 10;

    // ── States ────────────────────────────────────────────────────────────
    private enum State { PATROL, ALERTED, CHASE }
    private State state = State.PATROL;

    // Alert flash timer
    private int alertTimer = 0;
    private static final int ALERT_DURATION = 100; // frames at 200fps ≈ 0.5s

    // Accumulated sub-pixel movement
    private float accumX = 0, accumY = 0;

    // Spawn position for patrol circle
    private final int spawnX, spawnY;

    public HornetEnemy(GamePanel gp, int tileX, int tileY) {
        this.gp     = gp;
        this.worldX = gp.tileSize * tileX;
        this.worldY = gp.tileSize * tileY;
        this.spawnX = worldX;
        this.spawnY = worldY;
        this.fSpeed = PATROL_SPEED;
        this.Speed  = 1; // Entity.Speed used by collision checker

        solidArea = new java.awt.Rectangle(10, 10, 44, 44);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // Load frames once
        if (framesRight == null) {
            framesRight = new BufferedImage[FRAME_COUNT];
            framesLeft  = new BufferedImage[FRAME_COUNT];
            framesUp    = new BufferedImage[FRAME_COUNT];
            framesDown  = new BufferedImage[FRAME_COUNT];
            for (int i = 0; i < FRAME_COUNT; i++) {
                framesRight[i] = scale(LoadImage.load("/hornet/hornet_right_" + (i+1) + ".png"));
                framesLeft[i]  = scale(LoadImage.load("/hornet/hornet_left_"  + (i+1) + ".png"));
                framesUp[i]    = scale(LoadImage.load("/hornet/hornet_up_"    + (i+1) + ".png"));
                framesDown[i]  = scale(LoadImage.load("/hornet/hornet_down_"  + (i+1) + ".png"));
            }
        }
    }

    private BufferedImage scale(BufferedImage src) {
        if (src == null) return null;
        int s = gp.tileSize;
        BufferedImage out = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, s, s, null);
        g2.dispose();
        return out;
    }

    public void update() {
        int ts = gp.tileSize;

        // Player centre
        int px = gp.player.worldX + gp.player.solidArea.x + gp.player.solidArea.width  / 2;
        int py = gp.player.worldY + gp.player.solidArea.y + gp.player.solidArea.height / 2;
        int ex = worldX + solidArea.x + solidArea.width  / 2;
        int ey = worldY + solidArea.y + solidArea.height / 2;

        double dist = Math.sqrt((px-ex)*(px-ex) + (py-ey)*(py-ey));

        // ── State machine ────────────────────────────────────────────────
        switch (state) {
            case PATROL:
                if (dist < VIEW_TILES * ts) {
                    state      = State.ALERTED;
                    alertTimer = 0;
                    fSpeed     = 0.3f; // slow but never fully frozen
                }
                break;
            case ALERTED:
                alertTimer++;
                if (alertTimer >= ALERT_DURATION) {
                    state  = State.CHASE;
                    fSpeed = PATROL_SPEED; // start slow then accelerate
                }
                break;
            case CHASE:
                // Accelerate smoothly toward chase speed
                if (fSpeed < CHASE_SPEED) fSpeed = Math.min(fSpeed + 0.02f, CHASE_SPEED); // slower ramp-up too
                if (dist > LOSE_TILES * ts) {
                    state  = State.PATROL;
                    fSpeed = PATROL_SPEED;
                    accumX = 0; accumY = 0; // clear stale float accumulator
                }
                break;
        }

        // ── Movement ─────────────────────────────────────────────────────
        if (state == State.PATROL) {
            // Collision-aware left/right patrol — never drifts into water
            collisionOn = false;
            gp.collisionChecker.checkTile(this);
            if (collisionOn) {
                direction = direction.equals("left") ? "right" : "left";
            } else {
                worldX += direction.equals("left") ? -(int)fSpeed : (int)fSpeed;
            }

        } else if (state == State.CHASE) {
            // Full 4-directional chase using float accumulator
            float dx = px - ex;
            float dy = py - ey;
            float len = (float)Math.sqrt(dx*dx + dy*dy);
            if (len > 0) {
                dx /= len; dy /= len;
            }

            accumX += dx * fSpeed;
            accumY += dy * fSpeed;

            int moveX = (int)accumX;
            int moveY = (int)accumY;
            accumX -= moveX;
            accumY -= moveY;

            // Update direction for sprite
            if (Math.abs(dx) >= Math.abs(dy)) direction = dx < 0 ? "left" : "right";
            else                               direction = dy < 0 ? "up"   : "down";

            // Move X with collision
            if (moveX != 0) {
                direction = moveX < 0 ? "left" : "right";
                collisionOn = false;
                gp.collisionChecker.checkTile(this);
                if (!collisionOn) worldX += moveX;
            }
            // Move Y with collision
            if (moveY != 0) {
                direction = moveY < 0 ? "up" : "down";
                collisionOn = false;
                gp.collisionChecker.checkTile(this);
                if (!collisionOn) worldY += moveY;
            }

            // Restore dominant direction for sprite after collision checks
            if (Math.abs(dx) >= Math.abs(dy)) direction = dx < 0 ? "left" : "right";
            else                               direction = dy < 0 ? "up"   : "down";
        }

        // ── Animate ──────────────────────────────────────────────────────
        spriteCounter++;
        if (spriteCounter > 4) {
            spriteNum     = (spriteNum % FRAME_COUNT) + 1;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        int drawX = worldX - gp.camX;
        int drawY = worldY - gp.camY;

        if (drawX + gp.tileSize < 0 || drawX > gp.screenWidth  ||
            drawY + gp.tileSize < 0 || drawY > gp.screenHeight) return;

        // ── Sprite ───────────────────────────────────────────────────────
        BufferedImage[] frames = switch (direction) {
            case "left"  -> framesRight;
            case "up"    -> framesDown;
            case "down"  -> framesUp;
            default      -> framesLeft;
        };

        BufferedImage img = (frames != null && frames[spriteNum-1] != null)
                ? frames[spriteNum-1] : null;

        if (img != null) {
            g2.drawImage(img, drawX, drawY, null);
        } else {
            g2.setColor(Color.ORANGE);
            g2.fillRect(drawX+4, drawY+4, gp.tileSize-8, gp.tileSize-8);
        }

        // ── View ring (patrol only) ───────────────────────────────────────
        if (state == State.PATROL) {
            int cx = drawX + gp.tileSize/2;
            int cy = drawY + gp.tileSize/2;
            int r  = VIEW_TILES * gp.tileSize;
            g2.setColor(new Color(255, 140, 0, 30));
            g2.fillOval(cx-r, cy-r, r*2, r*2);
            g2.setColor(new Color(255, 140, 0, 100));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx-r, cy-r, r*2, r*2);
            g2.setStroke(new BasicStroke(1f));
        }

        // ── Alert flash ───────────────────────────────────────────────────
        if (state == State.ALERTED && (alertTimer / 10) % 2 == 0) {
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            g2.setColor(Color.ORANGE);
            int cx = drawX + gp.tileSize/2;
            g2.drawString("!", cx - 8, drawY - 6);
        }

        // ── Chase glow ────────────────────────────────────────────────────
        if (state == State.CHASE) {
            float alpha = (float)(0.2 + 0.15 * Math.sin(System.currentTimeMillis() / 120.0));
            int cx = drawX + gp.tileSize/2;
            int cy = drawY + gp.tileSize/2;
            int r  = gp.tileSize/2 + 8;
            g2.setColor(new Color(1f, 0.5f, 0f, alpha));
            g2.fillOval(cx-r, cy-r, r*2, r*2);
        }
    }
}