package object;

public class OBJ_door extends SuperObject{
    public OBJ_door() {
        name = "Door";
        try {
            image = utility.LoadImage.load("/objects/door.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        collision = true;
    }   
    
}
