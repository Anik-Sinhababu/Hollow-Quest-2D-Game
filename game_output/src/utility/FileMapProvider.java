package utility;

// ...existing code...
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.GamePanel;

public class FileMapProvider {
    public static final String BASE_PATH = "src/main/resources/maps/"; // Base path for resources

    public static int[][] getMap(String mapName)
    {
        int[][] map = new int[GamePanel.maxWorldRow][GamePanel.maxWorldCol]; // default zero-filled

        // Try loading from classpath first (works in IDE and packaged JAR)
        InputStream is = FileMapProvider.class.getResourceAsStream("/maps/" + mapName + ".txt");
        BufferedReader reader = null;

        try {
            if (is != null) {
                reader = new BufferedReader(new InputStreamReader(is));
            } else {
                // Fallback to file system (useful while developing)
                File f = new File(BASE_PATH + mapName + ".txt");
                if (!f.exists()) {
                    System.err.println("Map file not found on classpath or filesystem: " + mapName + ".txt");
                    return map; // return empty map (not null)
                }
                reader = new BufferedReader(new FileReader(f));
            }

            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < GamePanel.maxWorldRow) {
                String[] values = line.trim().split("\\s+");
                int limit = Math.min(values.length, GamePanel.maxWorldCol);
                for (int col = 0; col < limit; col++) {
                    try {
                        map[row][col] = Integer.parseInt(values[col]);
                    } catch (NumberFormatException ex) {
                        // leave as 0 if parse fails
                    }
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // return zero-filled map on error (avoid returning null)
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) {}
        }

        return map;
    }
}
// ...existing code...