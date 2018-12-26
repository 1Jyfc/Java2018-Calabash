package creatures;

import javafx.scene.image.Image;
import other.Field;
import other.Location;

import java.io.File;

public class Calabash extends Creature {
    private int calabashType;

    public Calabash(int calabashType, Location location, Field field) {
        super(-calabashType, false, location, field, 1, 7, 350, 180, 1000);
        this.calabashType = calabashType;
        setImageSrc();
    }

    private void setImageSrc() {
        switch (calabashType) {
            case 1: setImage(new Image("/image/calabash_1st.png")); break;
            case 2: setImage(new Image("/image/calabash_2nd.png")); break;
            case 3: setImage(new Image("/image/calabash_3rd.png")); break;
            case 4: setImage(new Image("/image/calabash_4th.png")); break;
            case 5: setImage(new Image("/image/calabash_5th.png")); break;
            case 6: setImage(new Image("/image/calabash_6th.png")); break;
            case 7: setImage(new Image("/image/calabash_7th.png")); break;
            default: break;
        }
    }
}
