package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.asteroids.Game;

import java.util.ArrayList;

import managers.Jukebox;

/**
 * Created by Antonio on 27/08/2015.
 */
public class FlyingSaucer extends SpaceObject {

    private ArrayList<Bullet> bullets;
    private int type;

    public static final int LARGE = 0;
    public static final int SMALL = 1;

    private int score;

    private float fireTimer;
    private float fireTime;

    private Player player;

    private float pathTimer;
    private float pathTime1;
    private float pathTime2;

    private int direction;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    private boolean remove;

    public FlyingSaucer(int type, int direction, Player player, ArrayList<Bullet> bullets) {
        this.type = type;
        this.direction = direction;
        this.player = player;
        this.bullets = bullets;

        speed = 70;
        if (direction == LEFT) {
            dx = -speed;
            x = Game.WIDTH;
        } else if (direction == RIGHT) {
            dx = speed;
            x = 0;
        }

        y = MathUtils.random(Game.HEIGHT);

        shapex = new float[6];
        shapey = new float[6];
        setShape();

        if (type == LARGE) {
            score = 200;
            Jukebox.loop("largesaucer");
        } else if (type == SMALL) {
            score = 1000;
            Jukebox.loop("smallsaucer");
        }

        fireTimer = 0;
        fireTime = 1;

        pathTimer = 0;
        pathTime1 = 2;
        pathTime2 = pathTime1 + 2;
    }

    private void setShape() {
        if (type == LARGE) {
            shapex[0] = x - 10;
            shapey[0] = y;

            shapex[1] = x - 3;
            shapey[1] = y - 5;

            shapex[2] = x + 3;
            shapey[2] = y - 5;

            shapex[3] = x + 10;
            shapey[3] = y;

            shapex[4] = x + 3;
            shapey[4] = y + 5;

            shapex[5] = x - 3;
            shapey[5] = y + 5;
        } else if (type == SMALL) {

            shapex[0] = x - 6;
            shapey[0] = y;

            shapex[1] = x - 2;
            shapey[1] = y - 3;

            shapex[2] = x + 2;
            shapey[2] = y - 3;

            shapex[3] = x + 6;
            shapey[3] = y;

            shapex[4] = x + 2;
            shapey[4] = y + 3;

            shapex[5] = x - 2;
            shapey[5] = y + 3;

        }
    }

    public int getScore() {
        return score;
    }

    public boolean shouldRemove() {
        return remove;
    }

    public void update(float dt) {
        //disparo
        if (!player.isHit()) {
            fireTimer += dt;
            if (fireTimer > fireTime) {
                fireTimer = 0;
                if (type == LARGE) {
                    radians = MathUtils.random(2 * (float) Math.PI);
                } else if (type == SMALL) {
                    radians = MathUtils.atan2(player.getY() - y, player.getX() - x);
                }
                bullets.add(new Bullet(x, y, radians));
                Jukebox.play("saucershoot");
            }
        }

        // movimiento sobre la ruta

        pathTimer += dt;

        //movimiento hacia delante
        if (pathTimer < pathTime1) {
            dy = 0;
        }

        // movimiento hacia abajo

        if (pathTimer > pathTime1 && pathTimer < pathTime2) {
            dy = -speed;
        }

        // Movimiento al final de la pantalla

        if (pathTimer > pathTime1 + pathTime2) {
            dy = 0;
        }

        x += dx * dt;
        y += dy * dt;

        // Transporte por los finales de pantalla
        if (y < 0) y = Game.HEIGHT;

        // set shape

        setShape();

        //Comprobamos si se elimina
        if ((direction == RIGHT && x > Game.WIDTH) ||
                (direction == LEFT && x < 0)) {
            remove = true;
        }
    }

    public void draw(ShapeRenderer sr) {
        sr.setColor(Color.BLUE);
        sr.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0, j = shapex.length - 1;
             i < shapex.length;
             j = i++) {
            sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);
        }

        sr.line(shapex[0], shapey[0], shapex[3], shapey[3]);
        sr.end();
    }
}
