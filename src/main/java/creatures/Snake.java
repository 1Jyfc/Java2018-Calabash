package creatures;

import javafx.scene.image.Image;
import other.Field;
import other.Location;

import java.io.File;

public class Snake extends Creature {
    public Snake(Location location, Field field) {
        super(4, true, location, field, 2, 5, 100, 100, 1000);
        setImage(new Image("/image/snake.png"));
    }
}