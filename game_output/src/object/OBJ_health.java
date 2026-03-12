package object;

import utility.LoadImage;

public class OBJ_health extends SuperObject {
    public OBJ_health() {
        name  = "Health";
        image = LoadImage.load("/objects/health.png");
    }
}
