package utility;

import main.GamePanel;
import object.OBJ_boots;
import object.OBJ_door;
import object.OBJ_key;
import object.OBJ_health;
import object.OBJ_treasure_box;

public class ObjectSetter {
    
    GamePanel gp;

    public ObjectSetter(GamePanel gp) {
        this.gp = gp;
    }
    public void setObject(){

        gp.obj[0] = new OBJ_key();
        gp.obj[0].worldX = gp.tileSize * 23;
        gp.obj[0].worldY = gp.tileSize * 7;

        gp.obj[1] = new OBJ_key();
        gp.obj[1].worldX = gp.tileSize * 23;
        gp.obj[1].worldY = gp.tileSize * 40;

        gp.obj[2] = new OBJ_boots();
        gp.obj[2].worldX = gp.tileSize * 37;
        gp.obj[2].worldY = gp.tileSize * 7;

        gp.obj[3] = new OBJ_treasure_box();
        gp.obj[3].worldX = gp.tileSize * 10;
        gp.obj[3].worldY = gp.tileSize * 8;

        gp.obj[4] = new OBJ_door();
        gp.obj[4].worldX = gp.tileSize * 10;
        gp.obj[4].worldY = gp.tileSize * 12;

        gp.obj[5] = new OBJ_door();
        gp.obj[5].worldX = gp.tileSize * 8;
        gp.obj[5].worldY = gp.tileSize * 28;

        gp.obj[6] = new OBJ_door();
        gp.obj[6].worldX = gp.tileSize * 12;
        gp.obj[6].worldY = gp.tileSize * 22;

        gp.obj[7] = new OBJ_key();
        gp.obj[7].worldX = gp.tileSize * 30;
        gp.obj[7].worldY = gp.tileSize * 8;

        gp.obj[8] = new OBJ_health();
        gp.obj[8].worldX = gp.tileSize * 10;
        gp.obj[8].worldY = gp.tileSize * 10;

        gp.obj[9] = new OBJ_health();
        gp.obj[9].worldX = gp.tileSize * 37;
        gp.obj[9].worldY = gp.tileSize * 28;
    }
}