package entity;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import main.GamePanel;
import utility.LoadImage;

public class Porcupine extends Entity {

    GamePanel gp;
    private static BufferedImage sprite = null;
    private static final int VIEW_TILES = 6;
    private static final int COOLDOWN_FRAMES = 400;
    private int cooldownTimer = 0;
    public List<Thorn> thorns = new ArrayList<>();

    public Porcupine(GamePanel gp, int tileX, int tileY) {
        this.gp = gp;
        this.worldX = gp.tileSize * tileX;
        this.worldY = gp.tileSize * tileY;
        this.Speed = 0;

        solidArea = new java.awt.Rectangle(8, 8, 48, 48);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        if (sprite == null) {
            BufferedImage raw = LoadImage.load("/porcupine/porcupie.png");
            if (raw != null) {
                int s = gp.tileSize;
                BufferedImage scaled = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2s = scaled.createGraphics();
                g2s.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                     RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2s.drawImage(raw, 0, 0, s, s, null);
                g2s.dispose();
                sprite = scaled;
            }
        }
    }

    public void update() {
        if (cooldownTimer > 0) cooldownTimer--;

        thorns.removeIf(t -> !t.active);
        for (Thorn t : thorns) t.update();

        float px = gp.player.worldX + gp.player.solidArea.x + gp.player.solidArea.width / 2f;
        float py = gp.player.worldY + gp.player.solidArea.y + gp.player.solidArea.height / 2f;
        float ex = worldX + solidArea.x + solidArea.width / 2f;
        float ey = worldY + solidArea.y + solidArea.height / 2f;

        float rawDx = px - ex;
        float rawDy = py - ey;
        double dist = Math.sqrt(rawDx * rawDx + rawDy * rawDy);

        if (dist < VIEW_TILES * gp.tileSize && cooldownTimer == 0) {
            float ndx = (float)(rawDx / dist);
            float ndy = (float)(rawDy / dist);
            thorns.add(new Thorn(gp, ex - gp.tileSize / 4f, ey - gp.tileSize / 4f, ndx, ndy, toDir8(ndx, ndy)));
            cooldownTimer = COOLDOWN_FRAMES;
        }
    }

    private String toDir8(float dx, float dy) {
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) angle += 360;
        if (angle < 22.5 || angle >= 337.5) return "right";
        if (angle < 67.5) return "se";
        if (angle < 112.5) return "down";
        if (angle < 157.5) return "sw";
        if (angle < 202.5) return "left";
        if (angle < 247.5) return "nw";
        if (angle < 292.5) return "up";
        return "ne";
    }

    public boolean thornHitsPlayer() {
        for (Thorn t : thorns) {
            if (t.hitsPlayer()) {
                t.active = false;
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics2D g2) {
        int drawX = worldX - gp.camX;
        int drawY = worldY - gp.camY;

        if (drawX + gp.tileSize < 0 || drawX > gp.screenWidth ||
            drawY + gp.tileSize < 0 || drawY > gp.screenHeight) {
            for (Thorn t : thorns) t.draw(g2);
            return;
        }

        if (sprite != null) g2.drawImage(sprite, drawX, drawY, null);
        else {
            g2.setColor(java.awt.Color.DARK_GRAY);
            g2.fillRect(drawX, drawY, gp.tileSize, gp.tileSize);
        }

        for (Thorn t : thorns) t.draw(g2);
    }
}