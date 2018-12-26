package other;

import javafx.scene.image.Image;
import creatures.Creature;

import java.io.File;

public class Bullet {
    private Creature shooter;
    private Field field;
    private boolean type;
    private boolean isHit;
    private boolean moveable;
    private int attack;
    private Location location;
    private Image bulletImage;

    public Bullet() {
        this.attack = 0;
    }

    public Bullet(Creature shooter) {
        this.shooter = shooter;
        this.field = shooter.getField();
        this.type = shooter.isEnemy();
        this.isHit = false;
        this.moveable = true;
        this.attack = shooter.getShootAttack();
        this.location = new Location(shooter.getLocation());
        if (this.type)
            this.bulletImage = new Image("/image/lightning.png");
        else
            this.bulletImage = new Image("/image/fireball.png");
    }

    public Image getBulletImage() {
        return bulletImage;
    }

    public Location getLocation() {
        return location;
    }

    public int getAttack() {
        return attack;
    }

    public boolean isType() {
        return type;
    }

    public void setMoveable(boolean moveable) {
        this.moveable = moveable;
    }

    public void hit() {
        this.isHit = true;
        field.bulletHit(location.getX(), location.getY());
    }

    public void move() {
        if (this.attack == 0 || !this.moveable)
            return;
        this.moveable = false;
        int x = location.getX();
        int y = location.getY();
        field.bulletHit(x, y);
        if ((x == 13 && !type) || (x == 0 && type))
            return;
        if (type)
            location.setX(x - 1);
        else
            location.setX(x + 1);
        x = location.getX();

        Creature creature = field.getUnit(x, y);
        if (creature.getType() == 0) {
            //System.out.println("Empty");
            Bullet bullet = field.getBullet(x, y);
            if (bullet.getAttack() == 0) {
                field.addBullet(x, y, this);
            } else if (bullet.type != this.type) {
                this.hit();
                bullet.hit();
            }
        } else if (creature.isEnemy() == this.type) {
            //System.out.println("Empty");
            field.addBullet(x, y, this);
        } else if (creature.isAlive()) {
            //hit
            //System.out.println("Hit");
            creature.hurt(this.attack);
            this.isHit = true;
        }
    }
}
