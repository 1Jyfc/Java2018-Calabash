package battleArray;

import other.Location;

import java.util.ArrayList;

public class ChongE extends baseArray {
    public ChongE(boolean type) {
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
        calabashLocation.add(new Location(2, 0));
        calabashLocation.add(new Location(3, 1));
        calabashLocation.add(new Location(2, 2));
        calabashLocation.add(new Location(3, 3));
        calabashLocation.add(new Location(2, 4));
        calabashLocation.add(new Location(3, 5));
        calabashLocation.add(new Location(2, 6));
        grandpaLocation = new Location(1, 3);
    }

    private void initEnemy() {
        minionLocation = new ArrayList<>(7);
        minionLocation.add(new Location(11, 0));
        minionLocation.add(new Location(10, 1));
        minionLocation.add(new Location(11, 2));
        minionLocation.add(new Location(10, 3));
        minionLocation.add(new Location(11, 4));
        minionLocation.add(new Location(10, 5));
        minionLocation.add(new Location(11, 6));
        scorpionLocation = new Location(12, 2);
        snakeLocation = new Location(12, 4);
    }
}
