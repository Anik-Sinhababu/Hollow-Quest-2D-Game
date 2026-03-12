package tiles;

import java.awt.Graphics2D;
import main.GamePanel;
import utility.FileMapProvider;
import utility.LoadImage;

public class TileManager {
    GamePanel gp;
    public Tile[] tiles;
    public int[][] map, coin_map, treemap;

    private java.awt.image.BufferedImage scaleImage(java.awt.image.BufferedImage src) {
        if (src == null) return null;
        java.awt.image.BufferedImage scaled = new java.awt.image.BufferedImage(
                gp.tileSize, gp.tileSize, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                            java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                            java.awt.RenderingHints.VALUE_RENDER_SPEED);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                            java.awt.RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.drawImage(src, 0, 0, gp.tileSize, gp.tileSize, null);
        g2.dispose();
        return scaled;
    }

    public TileManager(GamePanel gp) {
        this.gp = gp;
        map = FileMapProvider.getMap("map_02"); // default, overridden by loadMap()

        treemap = FileMapProvider.getMap("treemap");
        coin_map = FileMapProvider.getMap("coin_map01");

        tiles = new Tile[9];
        loadTiles();
    }

    public void loadTiles() {
        tiles = new Tile[42];

tiles[0]  = new Tile(); tiles[0].image  = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[0].collision = false;
tiles[1]  = new Tile(); tiles[1].image  = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[1].collision = false;
tiles[2]  = new Tile(); tiles[2].image  = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[2].collision = false;
tiles[3]  = new Tile(); tiles[3].image  = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[3].collision = false;
tiles[4]  = new Tile(); tiles[4].image  = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[4].collision = false;
tiles[5]  = new Tile(); tiles[5].image  = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[5].collision = false;
tiles[6]  = new Tile(); tiles[6].image  = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[6].collision = false;
tiles[7]  = new Tile(); tiles[7].image  = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[7].collision = false;
tiles[8]  = new Tile(); tiles[8].image  = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[8].collision = false;
tiles[9]  = new Tile(); tiles[9].image  = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[9].collision = false;
tiles[10] = new Tile(); tiles[10].image = scaleImage(LoadImage.load("/tiles/grass00.png")); tiles[10].collision = false;
tiles[11] = new Tile(); tiles[11].image = scaleImage(LoadImage.load("/tiles/grass01.png")); tiles[11].collision = false;

tiles[12] = new Tile(); tiles[12].image = scaleImage(LoadImage.load("/tiles/water00.png")); tiles[12].collision = true;
tiles[13] = new Tile(); tiles[13].image = scaleImage(LoadImage.load("/tiles/water01.png")); tiles[13].collision = true;
tiles[14] = new Tile(); tiles[14].image = scaleImage(LoadImage.load("/tiles/water02.png")); tiles[14].collision = true;
tiles[15] = new Tile(); tiles[15].image = scaleImage(LoadImage.load("/tiles/water03.png")); tiles[15].collision = true;
tiles[16] = new Tile(); tiles[16].image = scaleImage(LoadImage.load("/tiles/water04.png")); tiles[16].collision = true;
tiles[17] = new Tile(); tiles[17].image = scaleImage(LoadImage.load("/tiles/water05.png")); tiles[17].collision = true;
tiles[18] = new Tile(); tiles[18].image = scaleImage(LoadImage.load("/tiles/water06.png")); tiles[18].collision = true;
tiles[19] = new Tile(); tiles[19].image = scaleImage(LoadImage.load("/tiles/water07.png")); tiles[19].collision = true;
tiles[20] = new Tile(); tiles[20].image = scaleImage(LoadImage.load("/tiles/water08.png")); tiles[20].collision = true;
tiles[21] = new Tile(); tiles[21].image = scaleImage(LoadImage.load("/tiles/water09.png")); tiles[21].collision = true;
tiles[22] = new Tile(); tiles[22].image = scaleImage(LoadImage.load("/tiles/water10.png")); tiles[22].collision = true;
tiles[23] = new Tile(); tiles[23].image = scaleImage(LoadImage.load("/tiles/water11.png")); tiles[23].collision = true;
tiles[24] = new Tile(); tiles[24].image = scaleImage(LoadImage.load("/tiles/water12.png")); tiles[24].collision = true;
tiles[25] = new Tile(); tiles[25].image = scaleImage(LoadImage.load("/tiles/water13.png")); tiles[25].collision = true;

tiles[26] = new Tile(); tiles[26].image = scaleImage(LoadImage.load("/tiles/road00.png")); tiles[26].collision = false;
tiles[27] = new Tile(); tiles[27].image = scaleImage(LoadImage.load("/tiles/road01.png")); tiles[27].collision = false;
tiles[28] = new Tile(); tiles[28].image = scaleImage(LoadImage.load("/tiles/road02.png")); tiles[28].collision = false;
tiles[29] = new Tile(); tiles[29].image = scaleImage(LoadImage.load("/tiles/road03.png")); tiles[29].collision = false;
tiles[30] = new Tile(); tiles[30].image = scaleImage(LoadImage.load("/tiles/road04.png")); tiles[30].collision = false;
tiles[31] = new Tile(); tiles[31].image = scaleImage(LoadImage.load("/tiles/road05.png")); tiles[31].collision = false;
tiles[32] = new Tile(); tiles[32].image = scaleImage(LoadImage.load("/tiles/road06.png")); tiles[32].collision = false;
tiles[33] = new Tile(); tiles[33].image = scaleImage(LoadImage.load("/tiles/road07.png")); tiles[33].collision = false;
tiles[34] = new Tile(); tiles[34].image = scaleImage(LoadImage.load("/tiles/road08.png")); tiles[34].collision = false;
tiles[35] = new Tile(); tiles[35].image = scaleImage(LoadImage.load("/tiles/road09.png")); tiles[35].collision = false;
tiles[36] = new Tile(); tiles[36].image = scaleImage(LoadImage.load("/tiles/road10.png")); tiles[36].collision = false;
tiles[37] = new Tile(); tiles[37].image = scaleImage(LoadImage.load("/tiles/road11.png")); tiles[37].collision = false;
tiles[38] = new Tile(); tiles[38].image = scaleImage(LoadImage.load("/tiles/road12.png")); tiles[38].collision = false;

tiles[39] = new Tile(); tiles[39].image = scaleImage(LoadImage.load("/tiles/earth.png")); tiles[39].collision = false;
tiles[40] = new Tile(); tiles[40].image = scaleImage(LoadImage.load("/tiles/wall.png")); tiles[40].collision = true;
tiles[41] = new Tile(); tiles[41].image = scaleImage(LoadImage.load("/tiles/tree.png")); tiles[41].collision = true;

    }

    public void draw(Graphics2D g2) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                int tileCode = map[row][col];

                int tileX = col * gp.tileSize - gp.camX;
                int tileY = row * gp.tileSize - gp.camY;

                if (tileX > -gp.tileSize*2 && tileX < gp.screenWidth &&
                    tileY > -gp.tileSize*2 && tileY < gp.screenHeight) {
                        
                        g2.drawImage(tiles[tileCode].image, tileX, tileY, gp.tileSize + 1, gp.tileSize + 1, null); // +1 seals rounding seams
                }
            }
        }
    }

    public void loadMap(String mapName) {
        map = FileMapProvider.getMap(mapName);
    }
}