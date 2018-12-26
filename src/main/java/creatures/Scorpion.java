package creatures;

import javafx.scene.image.Image;
import other.Field;
import other.Location;

import java.io.File;

public class Scorpion extends Creature {
    public Scorpion(Location location, Field field) {
        super(3, true, location, field, 4, 10, 500, 50, 3000);
        setImage(new Image("/image/scorpion.png"));
    }
}
