package battleArray;

import other.Location;

import java.util.ArrayList;

public class YanXing extends baseArray{
    public YanXing(boolean type) {
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
        calabashLocation.add(new Location(4, 0));
        calabashLocation.add(new Location(3, 1));
        calabashLocation.add(new Location(2, 2));
        calabashLocation.add(new Location(1, 3));
        calabashLocation.add(new Location(4, 3));
        calabashLocation.add(new Location(3, 4));
        calabashLocation.add(new Location(2, 5));
        grandpaLocation = new Location(1, 6);
    }

    private void initEnemy() {
        minionLocation = new ArrayList<>(7);
        minionLocation.add(new Location(9, 0));
        minionLocation.add(new Location(10, 1));
        minionLocation.add(new Location(11, 2));
        minionLocation.add(new Location(12, 3));
        minionLocation.add(new Location(9, 3));
        minionLocation.add(new Location(10, 4));
        minionLocation.add(new Location(11, 5));
        scorpionLocation = new Location(12, 6);
        snakeLocation = new Location(12, 0);
    }
}
