package object;
import utility.LoadImage;
public class OBJ_grand_door extends SuperObject {
    public OBJ_grand_door() {
        name      = "Grand Door";
        image     = LoadImage.load("/objects/grand_door.png");
        collision = true;
    }
}