package object;

import utility.LoadImage;

public class OBJ_key extends SuperObject{
    public OBJ_key() {
        name = "Key";
        try {
            image = LoadImage.load("/objects/key.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
