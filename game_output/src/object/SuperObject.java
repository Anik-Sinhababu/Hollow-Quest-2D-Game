package object;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;

public class SuperObject {
    
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int worldX, worldY;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    public void draw(Graphics2D g2, GamePanel gp) {
                int tileX = worldX - gp.camX;
                int tileY = worldY - gp.camY;
                if (tileX > -gp.tileSize && tileX < gp.screenWidth && tileY > -gp.tileSize && tileY < gp.screenHeight) {
                    if (name == "Treasure Box") {
                        g2.drawImage(image, tileX, tileY, gp.tileSize, gp.tileSize, null);

                    }
                    else
                    {
                        g2.drawImage(image, tileX, tileY, gp.tileSize, gp.tileSize, null);
                    }
                }
    }

}
