package creatures;

import javafx.scene.image.Image;
import other.Field;
import other.Bullet;
import other.Location;

import java.util.Random;

public class Creature implements Runnable {
    private int type;
    private boolean isEnemy;
    private boolean isAlive;
    private Location location;
    private int moveTurn;
    private int moveTime;
    private int shootTurn;
    private int shootTime;
    private int attack;
    private int shootAttack;
    private int maxHealth;
    private int health;
    private Image image;
    private Field field;

    public Creature(int type, boolean isEnemy, Location location, Field field, int moveTurn, int shootTurn, int attack, int shootAttack, int health) {
        this.type = type;
        this.isEnemy = isEnemy;
        this.location = location;
        this.field = field;

        Random random = new Random();
        this.moveTurn = moveTurn;
        this.moveTime = random.nextInt(moveTurn);
        this.shootTurn = shootTurn;
        this.shootTime = random.nextInt(shootTurn);

        this.attack = attack;
        this.shootAttack = shootAttack;
        this.maxHealth = health;
        this.health = health;
        this.isAlive = true;
    }

    public Creature() {
        this.type = 0;
        this.isAlive = false;
    }

    public boolean isEnemy() {
        return isEnemy;
    }

    public void setEnemy(boolean enemy) {
        isEnemy = enemy;
    }

    public int getType() {
        return type;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getAttack() {
        return attack;
    }

    public int getShootAttack() {
        return shootAttack;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getMoveTurn() {
        return moveTurn;
    }

    public int getShootTurn() {
        return shootTurn;
    }

    public Location getLocation() {
        return location;
    }

    public Image getImage() {
        return image;
    }

    public Field getField() {
        return field;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setShootAttack(int shootAttack) {
        this.shootAttack = shootAttack;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setMoveTurn(int moveTurn) {
        this.moveTurn = moveTurn;
    }

    public void setShootTurn(int shootTurn) {
        this.shootTurn = shootTurn;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setField(Field field) {
        this.field = field;
    }

    private boolean isOpposite(Creature c1, Creature c2) {
        if (c1.type == 0 || c2.type == 0)
            return false;
        else {
            return (c1.type < 3 && c2.type > 2) || (c1.type > 2 && c2.type < 3);
        }
    }

    private int surrounding(int x, int y) {
        Creature it = field.getUnit(x, y);
        if (x < 13 && isOpposite(this, field.getUnit(x + 1, y)))
            return 1;
        else if (y < 6 && isOpposite(this, field.getUnit(x, y + 1)))
            return 2;
        else if (x > 0 && isOpposite(this, field.getUnit(x - 1, y)))
            return 3;
        else if (y > 0 && isOpposite(this, field.getUnit(x, y - 1)))
            return 4;
        else
            return 0;
    }

    private void move() {
        int x = location.getX();
        int y = location.getY();
        switch (surrounding(x, y)) {
            case 1:
                attacking(field.getUnit(x + 1, y));
                field.battle(x * 100 + 50, y * 100);
                break;
            case 2:
                attacking(field.getUnit(x, y + 1));
                field.battle(x * 100, y * 100 + 50);
                break;
            case 3:
                attacking(field.getUnit(x - 1, y));
                field.battle(x * 100 - 50, y * 100);
                break;
            case 4:
                attacking(field.getUnit(x, y - 1));
                field.battle(x * 100, y * 100 - 50);
                break;
            case 0:
                boolean ifMoved = false;
                while (!ifMoved) {
                    Random random = new Random();
                    int moveWhere = random.nextInt(4);
                    switch (moveWhere) {
                        case 0:
                            ifMoved = moveTo(x + 1, y);
                            break;
                        case 1:
                            ifMoved = moveTo(x, y + 1);
                            break;
                        case 2:
                            ifMoved = moveTo(x - 1, y);
                            break;
                        case 3:
                            ifMoved = moveTo(x, y - 1);
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean moveTo(int x, int y) {
        if (x < 0 || x > 13 || y < 0 || y > 6)
            return false;
        if (field.getUnit(x, y).getType() == 0) {
            field.clearUnit(location);
            this.location.setLocation(x, y);
            field.setUnit(location, this);

            Bullet bullet = field.getBullet(x, y);
            if (bullet.getAttack() != 0 && bullet.isType() != isEnemy) {
                //crash
                this.hurt(bullet.getAttack());
            }
        }
        return true;
    }

    private boolean attacking(Creature attacked) {
        //System.out.println("Attack type " + Integer.toString(attacked.type) + " , health is " + Integer.toString(attacked.health));
        while (this.isAlive) {
            boolean isKilled = attacked.hurt(this.attack);
            if (isKilled) {
                //System.out.println("Kill it! Rest health is " + Integer.toString(health));
                return true;
            }
            this.hurt(attacked.getAttack());
            //System.out.println("Hurt by" + Integer.toString(attacked.getAttack()));
        }
        return false;
    }

    public boolean hurt(int attack) {
        this.health -= attack;
        if (this.health <= 0) {
            this.health = 0;
            this.dead();
        }
        return !this.isAlive;
    }

    private void shoot() {
        Bullet bullet = new Bullet(this);
        field.addBullet(location.getX(), location.getY(), bullet);
    }

    public void dead() {
        field.clearUnit(location);
        field.buildTomb(location.getX(), location.getY(), this.isEnemy);
        setAlive(false);
    }

    public void run() {
        while (!Thread.interrupted()) {
            //dead???
            if (!this.isAlive)
                return;

            //move
            if (this.moveTime == 0) {
                this.move();
                this.moveTime = this.moveTurn;
            } else {
                this.moveTime--;
            }

            //shoot
            if (this.shootTime == 0) {
                this.shoot();
                this.shootTime = this.shootTurn;
            } else {
                this.shootTime--;
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
