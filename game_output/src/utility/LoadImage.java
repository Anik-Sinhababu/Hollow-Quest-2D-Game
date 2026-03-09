// ...existing code...
package utility;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class LoadImage {
    public static BufferedImage load(String resourcePath) {
        // try classpath first
        InputStream is = LoadImage.class.getResourceAsStream(resourcePath.startsWith("/") ? resourcePath : ("/" + resourcePath));
        try {
            if (is != null) {
                try (BufferedInputStream bis = new BufferedInputStream(is)) {
                    return ImageIO.read(bis);
                }
            } else {
                // fallback to file system (development)
                File f = new File("src/main/resources/" + resourcePath);
                if (f.exists()) {
                    return ImageIO.read(f);
                } else {
                    throw new IOException("Resource not found: " + resourcePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
// ...existing code...