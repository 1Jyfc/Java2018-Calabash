package creatures;

import javafx.scene.image.Image;
import other.Field;
import other.Location;

import java.io.File;

public class Grandpa extends Creature {
    public Grandpa(Location location, Field field) {
        super(2, false, location, field, 4, 5, 200, 150, 2000);
        setImage(new Image("/image/grandpa.png"));
    }
}
