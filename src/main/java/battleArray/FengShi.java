package battleArray;

import other.Location;

import java.util.ArrayList;

public class FengShi extends baseArray {
    public FengShi(boolean type) {
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
        calabashLocation.add(new Location(3, 4));
        calabashLocation.add(new Location(2, 5));
        calabashLocation.add(new Location(3, 3));
        calabashLocation.add(new Location(2, 3));
        grandpaLocation = new Location(1, 3);
    }

    private void initEnemy() {
        minionLocation = new ArrayList<>(7);
        minionLocation.add(new Location(10, 1));
        minionLocation.add(new Location(9, 2));
        minionLocation.add(new Location(8, 3));
        minionLocation.add(new Location(9, 4));
        minionLocation.add(new Location(10, 5));
        minionLocation.add(new Location(9, 3));
        minionLocation.add(new Location(10, 3));
        scorpionLocation = new Location(11, 3);
        snakeLocation = new Location(12, 3);
    }
}
