package entity;
import java.awt.Rectangle;

public class Entity {
    public int worldX, worldY;
    public float Speed;
    int spriteCounter = 0;
    int spriteNum = 1;
    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public String direction = "down";
}