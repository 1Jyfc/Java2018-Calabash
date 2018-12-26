package battleArray;

import other.Location;

import java.util.ArrayList;

public class YanYue extends baseArray {
    public YanYue(boolean type) {
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
        calabashLocation.add(new Location(3, 2));
        calabashLocation.add(new Location(4, 2));
        calabashLocation.add(new Location(3, 3));
        calabashLocation.add(new Location(4, 3));
        calabashLocation.add(new Location(2, 1));
        calabashLocation.add(new Location(1, 0));
        calabashLocation.add(new Location(2, 4));
        grandpaLocation = new Location(1, 5);
    }

    private void initEnemy() {
        minionLocation = new ArrayList<>(7);
        minionLocation.add(new Location(10, 2));
        minionLocation.add(new Location(9, 2));
        minionLocation.add(new Location(10, 3));
        minionLocation.add(new Location(9, 3));
        minionLocation.add(new Location(11, 1));
        minionLocation.add(new Location(12, 0));
        minionLocation.add(new Location(11, 4));
        scorpionLocation = new Location(12, 5);
        snakeLocation = new Location(13, 6);
    }
}
