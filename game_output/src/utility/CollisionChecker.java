package utility;

import entity.Enemy;
import entity.HornetEnemy;
import entity.Entity;
import main.GamePanel;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public boolean checkTile(Entity entity) {

        int leftX = entity.worldX + entity.solidArea.x;
        int rightX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int topY = entity.worldY + entity.solidArea.y;
        int bottomY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int leftCol = leftX / gp.tileSize;
        int rightCol = rightX / gp.tileSize;
        int topRow = topY / gp.tileSize;
        int bottomRow = bottomY / gp.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                topRow = (topY - (int)entity.Speed) / gp.tileSize;
                topRow = Math.max(0, topRow);
                tileNum1 = gp.tileM.map[topRow][leftCol];
                tileNum2 = gp.tileM.map[topRow][rightCol];
                if (gp.tileM.tiles[tileNum1].collision || gp.tileM.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                    return true;
                }
                break;

            case "down":
                bottomRow = (bottomY + (int)entity.Speed) / gp.tileSize;
                bottomRow = Math.min(gp.tileM.map.length - 1, bottomRow);
                tileNum1 = gp.tileM.map[bottomRow][leftCol];
                tileNum2 = gp.tileM.map[bottomRow][rightCol];
                if (gp.tileM.tiles[tileNum1].collision || gp.tileM.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                    return true;
                }
                break;

            case "left":
                leftCol = (leftX - (int)entity.Speed) / gp.tileSize;
                leftCol = Math.max(0, leftCol);
                tileNum1 = gp.tileM.map[topRow][leftCol];
                tileNum2 = gp.tileM.map[bottomRow][leftCol];
                if (gp.tileM.tiles[tileNum1].collision || gp.tileM.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                    return true;
                }
                break;

            case "right":
                rightCol = (rightX + (int)entity.Speed) / gp.tileSize;
                rightCol = Math.min(gp.tileM.map[0].length - 1, rightCol);
                tileNum1 = gp.tileM.map[topRow][rightCol];
                tileNum2 = gp.tileM.map[bottomRow][rightCol];
                if (gp.tileM.tiles[tileNum1].collision || gp.tileM.tiles[tileNum2].collision) {
                    entity.collisionOn = true;
                    return true;
                }
                break;
        }

        entity.collisionOn = false;
        return false;
    }

    public int checkObject(Entity entity, boolean player)
    {
        int index = 999;
        for(int i=0; i<gp.obj.length; i++)
        {
            if (gp.obj[i] != null) {
                
                // Get entity's solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;
                // Get the object's solid area position
                gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;

                switch (entity.direction)
                {
                    case "up":
                    entity.solidArea.y -= (int)entity.Speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if (gp.obj[i].collision) {
                                entity.collisionOn = true;   
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;

                    case "down":
                    entity.solidArea.y += (int)entity.Speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if (gp.obj[i].collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;

                    case "left":
                    entity.solidArea.x -= (int)entity.Speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if (gp.obj[i].collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;

                    case "right":
                    entity.solidArea.x += (int)entity.Speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if (gp.obj[i].collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;
                }

                // Reset the position of the solid areas
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }
        return index;
    } 

    /**
     * Checks whether the player's solid area overlaps any enemy's solid area.
     * Uses simple world-space AABB intersection.
     *
     * @param enemies  The array of active Enemy instances from GamePanel
     * @return true if the player is touching at least one enemy
     */
    public boolean checkEnemyContact(entity.Enemy[] enemies) {
        entity.Entity player = gp.player;

        java.awt.Rectangle playerRect = new java.awt.Rectangle(
            player.worldX + player.solidArea.x,
            player.worldY + player.solidArea.y,
            player.solidArea.width,
            player.solidArea.height
        );

        for (entity.Enemy enemy : enemies) {
            if (enemy == null) continue;

            java.awt.Rectangle enemyRect = new java.awt.Rectangle(
                enemy.worldX + enemy.solidArea.x,
                enemy.worldY + enemy.solidArea.y,
                enemy.solidArea.width,
                enemy.solidArea.height
            );

            if (playerRect.intersects(enemyRect)) {
                return true;
            }
        }
        return false;
    }


    public boolean checkHornetContact(entity.HornetEnemy[] hornets) {
        entity.Entity player = gp.player;
        java.awt.Rectangle pr = new java.awt.Rectangle(
            player.worldX + player.solidArea.x, player.worldY + player.solidArea.y,
            player.solidArea.width, player.solidArea.height);
        for (entity.HornetEnemy h : hornets) {
            if (h == null) continue;
            java.awt.Rectangle hr = new java.awt.Rectangle(
                h.worldX + h.solidArea.x, h.worldY + h.solidArea.y,
                h.solidArea.width, h.solidArea.height);
            if (pr.intersects(hr)) return true;
        }
        return false;
    }
}