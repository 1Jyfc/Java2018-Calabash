package battleArray;

import other.Location;

import java.util.ArrayList;

public class YuLin extends baseArray {
    public YuLin(boolean type) {
        this.arrayType = type;
        if(arrayType) {
            initCalabash();
        }
        else {
            initEnemy();
        }
    }

    private void initCalabash() {
        calabashLocation = new ArrayList<>(7);
        calabashLocation.add(new Location(2, 1));
        calabashLocation.add(new Location(3, 2));
        calabashLocation.add(new Location(4, 3));
        calabashLocation.add(new Location(3, 3));
        calabashLocation.add(new Location(2, 3));
        calabashLocation.add(new Location(1, 3));
        calabashLocation.add(new Location(3, 4));
        grandpaLocation = new Location(2, 4);
    }

    private void initEnemy() {
        minionLocation = new ArrayList<>(7);
        minionLocation.add(new Location(11, 1));
        minionLocation.add(new Location(10, 2));
        minionLocation.add(new Location(9, 3));
        minionLocation.add(new Location(10, 3));
        minionLocation.add(new Location(11, 3));
        minionLocation.add(new Location(12, 3));
        minionLocation.add(new Location(10, 4));
        scorpionLocation = new Location(11, 4);
        snakeLocation = new Location(12, 4);
    }
}
