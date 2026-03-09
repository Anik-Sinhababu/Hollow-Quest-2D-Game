package object;

public class OBJ_treasure_box extends SuperObject{
    public OBJ_treasure_box() {
        name = "Treasure Box";
        try {
            image = utility.LoadImage.load("/objects/chest.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
