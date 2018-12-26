package creatures;

import javafx.scene.image.Image;
import other.Field;
import other.Location;

import java.io.File;

public class Minion extends Creature{
    public Minion(Location location, Field field) {
        super(5, true, location, field, 1, 8, 300, 150, 1000);
        setImage(new Image("/image/minion.png"));
    }
}
