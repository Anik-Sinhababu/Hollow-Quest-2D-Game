package object;

public class OBJ_boots extends SuperObject{
    public OBJ_boots() {
        name = "Boots";
        try {
            image = utility.LoadImage.load("/objects/boot.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
