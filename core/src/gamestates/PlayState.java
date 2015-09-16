package gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.asteroids.Game;

import java.util.ArrayList;

import entities.Asteroid;
import entities.Bullet;
import entities.FlyingSaucer;
import entities.Particle;
import entities.Player;
import managers.GameStateManager;
import managers.Jukebox;
import managers.Save;

/**
 * Created by Antonio on 17/08/2015.
 */
public class PlayState extends GameState {

    private ShapeRenderer sr;
    private SpriteBatch sb;
    private BitmapFont font;
    private Player hudPlayer;

    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Bullet> enemyBullets;

    private FlyingSaucer flyingSaucer;
    private float fsTimer;
    private float fsTime;

    private ArrayList<Particle> particles;

    private int level;
    private int totalAsteroids;
    private int numAsteroidsLeft;

    private float maxDelay, minDelay, currentDelay, bgTimer;
    private boolean playLowPulse;

    public PlayState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {
        sr = new ShapeRenderer();
        sb = new SpriteBatch();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        font = gen.generateFont(20);
        bullets = new ArrayList<Bullet>();
        player = new Player(bullets);
        asteroids = new ArrayList<Asteroid>();
        particles = new ArrayList<Particle>();

        level = 1;
        spawnAsteroids();

        hudPlayer = new Player(null);

        fsTimer = 0;
        fsTime = 15;
        enemyBullets = new ArrayList<Bullet>();

        // set up Musica
        maxDelay = 1;
        minDelay = 0.25f;
        currentDelay = maxDelay;
        bgTimer = maxDelay;
        playLowPulse = true;
    }

    private void createParticles(float x, float y) {
        for (int i = 0; i < 6; i++) {
            particles.add(new Particle(x, y));
        }
    }

    private void splitAsteroids(Asteroid a) {
        createParticles(a.getX(), a.getY());
        numAsteroidsLeft--;
        currentDelay = ((maxDelay - minDelay) * numAsteroidsLeft / totalAsteroids) + minDelay;
        if (a.getType() == Asteroid.LARGE) {
            asteroids.add(new Asteroid(a.getX(), a.getY(), Asteroid.MEDIUM));
            asteroids.add(new Asteroid(a.getX(), a.getY(), Asteroid.MEDIUM));
        }
        if (a.getType() == Asteroid.MEDIUM) {
            asteroids.add(new Asteroid(a.getX(), a.getY(), Asteroid.SMALL));
            asteroids.add(new Asteroid(a.getX(), a.getY(), Asteroid.SMALL));
        }
    }

    private void spawnAsteroids() {
        asteroids.clear();
        int numToSpawn = 4 + level - 1;
        totalAsteroids = numToSpawn * 7;
        numAsteroidsLeft = totalAsteroids;
        currentDelay = maxDelay;

        for (int i = 0; i < numToSpawn; i++) {
            float x = MathUtils.random(Game.WIDTH);
            float y = MathUtils.random(Game.HEIGHT);

            float dx = x - player.getX();
            float dy = y - player.getY();
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            while (dist < 100) {
                x = MathUtils.random(Game.WIDTH);
                y = MathUtils.random(Game.HEIGHT);

                dx = x - player.getX();
                dy = y - player.getY();
                dist = (float) Math.sqrt(dx * dx + dy * dy);
            }

            asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
        }
    }

    public void update(float dt) {
        // Obtenemos el user input
        handleInput();

        //Siguiente nivel
        if (asteroids.size() == 0) {
            level++;
            spawnAsteroids();
        }

        // Actualizamos al jugador
        player.update(dt);
        if (player.isDead()) {
            if (player.getExtraLives() == 0) {
                Jukebox.stopAll();
                Save.gd.setTentativeScore(player.getScore());
                gsm.setState(GameStateManager.GAMEOVER);
                return;
            }
            player.reset();
            player.loseLife();
            flyingSaucer = null;
            Jukebox.stop("smallsaucer");
            Jukebox.stop("largesaucer");
            return;
        }

        // Actualizamos las balas del jugador
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update(dt);
            if (bullets.get(i).shouldRemove()) {
                bullets.remove(i);
                i--;
            }
        }


        // Actualizamos el platillo volante
        if (flyingSaucer == null) {
            fsTimer += dt;
            if (fsTimer >= fsTime) {
                fsTimer = 0;
                int type = MathUtils.random() < 0.5 ? FlyingSaucer.SMALL : FlyingSaucer.LARGE;
                int direction = MathUtils.random() < 0.5 ? FlyingSaucer.RIGHT : FlyingSaucer.LEFT;
                flyingSaucer = new FlyingSaucer(type, direction, player, enemyBullets);
            }
        }

        // Si existe platillo volante ya
        else {
            flyingSaucer.update(dt);
            if (flyingSaucer.shouldRemove()) {
                flyingSaucer = null;
                Jukebox.stop("smallsaucer");
                Jukebox.stop("largesaucer");
            }
        }

        // Actualizamos las balas del platillo volante
        for (int i = 0; i < enemyBullets.size(); i++) {
            enemyBullets.get(i).update(dt);
            if (enemyBullets.get(i).shouldRemove()) {
                enemyBullets.remove(i);
                i--;
            }
        }

        // Actualizamos los asteroides
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).update(dt);
            if (asteroids.get(i).shouldRemove()) {
                asteroids.remove(i);
                i--;
            }
        }

        //Actualizamos las particulas
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).update(dt);
            if (particles.get(i).shouldRemove()) {
                particles.remove(i);
            }
        }

        // Comprobamos las colisiones
        checkCollisions();

        // Play musica de fondo
        bgTimer += dt;
        if (!player.isHit() && bgTimer >= currentDelay) {
            if (playLowPulse) {
                Jukebox.play("pulselow");
            } else {
                Jukebox.play("pulsehigh");
            }
            playLowPulse = !playLowPulse;
            bgTimer = 0;

        }

    }

    private void checkCollisions() {
        //Colision de jugador con asteroide
        if (!player.isHit()) {
            for (int i = 0; i < asteroids.size(); i++) {
                Asteroid a = asteroids.get(i);
                if (a.intersects(player)) {
                    player.hit();
                    asteroids.remove(i);
                    i--;
                    splitAsteroids(a);
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        // Colision de bala y asteroide
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid a = asteroids.get(j);
                //Comprobamos si el asteroide contiene el punto b
                if (a.contains(b.getX(), b.getY())) {
                    bullets.remove(i);
                    i--;
                    asteroids.remove(j);
                    j--;
                    splitAsteroids(a);
                    //Incrementamos la puntuacion
                    player.incrementScore(a.getScore());
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        // Jugador con nave espacial
        if (flyingSaucer != null) {
            if (player.intersects(flyingSaucer)) {
                player.hit();
                createParticles(player.getX(), player.getY());
                createParticles(flyingSaucer.getX(), flyingSaucer.getY());
                flyingSaucer = null;
                Jukebox.stop("smallsaucer");
                Jukebox.stop("largesaucer");
                Jukebox.play("explode");
            }
        }

        // Bala con nave espacial
        if (flyingSaucer != null) {
            for (int i = 0; i < bullets.size(); i++) {
                Bullet b = bullets.get(i);
                if (flyingSaucer.contains(b.getX(), b.getY())) {
                    bullets.remove(i);
                    i--;
                    createParticles(flyingSaucer.getX(), flyingSaucer.getY());
                    player.incrementScore(flyingSaucer.getScore());
                    flyingSaucer = null;
                    Jukebox.stop("smallsaucer");
                    Jukebox.stop("largesaucer");
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        // Jugador con balas enemigas
        if (!player.isHit()) {
            for (int i = 0; i < enemyBullets.size(); i++) {
                Bullet b = enemyBullets.get(i);
                if (player.contains(b.getX(), b.getY())) {
                    player.hit();
                    enemyBullets.remove(i);
                    i--;
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        // Nave espacial con asteroide colision
        if (flyingSaucer != null) {
            for (int i = 0; i < asteroids.size(); i++) {
                Asteroid a = asteroids.get(i);
                if (a.intersects(flyingSaucer)) {
                    asteroids.remove(i);
                    i--;
                    splitAsteroids(a);
                    createParticles(a.getX(), a.getY());
                    createParticles(flyingSaucer.getX(), flyingSaucer.getY());
                    flyingSaucer = null;
                    Jukebox.stop("smallsaucer");
                    Jukebox.stop("largesaucer");
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        // Asteroides con balas enemigas colision
        for(int i = 0; i < enemyBullets.size(); i++) {
            Bullet b = enemyBullets.get(i);
            for(int j = 0; j< asteroids.size(); j++) {
                Asteroid a = asteroids.get(j);
                if(a.contains(b.getX(),b.getY())) {
                    asteroids.remove(j);
                    j--;
                    splitAsteroids(a);
                    enemyBullets.remove(i);
                    i--;
                    createParticles(a.getX(),a.getY());
                    Jukebox.play("explode");
                    break;
                }
            }
        }
    }

    public void draw() {
        sb.setProjectionMatrix(Game.cam.combined);
        sr.setProjectionMatrix(Game.cam.combined);

        // Dibujamos al jugador
        player.draw(sr);

        // Dibujamos las balas
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(sr);
        }

        //Dibujamos el platillo volante
        if (flyingSaucer != null) {
            flyingSaucer.draw(sr);
        }

        // Dibujamos las balas del platillo volante
        for (int i = 0; i < enemyBullets.size(); i++) {
            enemyBullets.get(i).draw(sr);
        }

        //Dibujamos los asteroides
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).draw(sr);
        }

        //Dibujamos las particulas
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).draw(sr);
        }

        //Dibujamos el marcador
        sb.setColor(1, 1, 1, 1);
        sb.begin();
        font.draw(sb, "Puntuación: " + Long.toString(player.getScore()), 40, Game.HEIGHT - 10);
        sb.end();

        // Dibujamos las vidas
        for (int i = 0; i < player.getExtraLives(); i++) {
            hudPlayer.setPosition(40 + i * 10, Game.HEIGHT - 50);
            hudPlayer.draw(sr);
        }


    }

    public void handleInput() {
        if (!player.isHit()) {
            player.setLeft(Gdx.input.isKeyPressed(Input.Keys.A));
            player.setRight(Gdx.input.isKeyPressed(Input.Keys.D));
            player.setUp(Gdx.input.isKeyPressed(Input.Keys.W));
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.shoot();
            }
        }
    }

    public void dispose() {
        sb.dispose();
        sr.dispose();
        font.dispose();
    }
}
