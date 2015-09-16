package gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.asteroids.Game;

import java.util.ArrayList;

import entities.Asteroid;
import managers.GameStateManager;
import managers.Jukebox;
import managers.Save;

/**
 * Created by Antonio on 24/08/2015.
 */
public class MenuState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;
    private BitmapFont titleFont, itemFont;
    private GlyphLayout layout;

    private final String title = "Asteroids";

    private int currentItem;
    private String[] menuItems;

    private ArrayList<Asteroid> asteroids;

    public MenuState(GameStateManager gsm) {
        super(gsm);

    }

    public void init() {
        sb = new SpriteBatch();
        sr = new ShapeRenderer();
        layout = new GlyphLayout();
        setFonts();
        menuItems = new String[]{
                "Play",
                "Highscores",
                "Quit"
        };
        Save.load();
        asteroids = new ArrayList<Asteroid>();
        for (int i = 0; i < 6; i++) {
            asteroids.add(new Asteroid(
                    MathUtils.random(Game.WIDTH),
                    MathUtils.random(Game.HEIGHT),
                    Asteroid.LARGE));
        }
        Jukebox.play("main");
    }

    private void setFonts() {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 100;
        titleFont = gen.generateFont(param);
        param.size = 30;
        itemFont = gen.generateFont(param);
    }

    public void update(float dt) {

        handleInput();
        for(int i = 0; i<asteroids.size(); i++) {
            asteroids.get(i).update(dt);
        }
    }

    public void draw() {
        sb.setProjectionMatrix(Game.cam.combined);
        sr.setProjectionMatrix(Game.cam.combined);

        //Dibujamos los asteroides
        for(int i = 0; i<asteroids.size(); i++) {
            asteroids.get(i).draw(sr);
        }

        sb.begin();

        // Dibujamos el titulo en el layout
        layout.setText(titleFont, title);
        titleFont.draw(sb, title, (Game.WIDTH - layout.width) / 2, 600);
        //Dibujamos los elementos del menu
        for (int i = 0; i < menuItems.length; i++) {
            layout.setText(itemFont, menuItems[i]);
            if (currentItem == i) itemFont.setColor(Color.RED);
            else itemFont.setColor(Color.WHITE);
            itemFont.draw(sb,
                    menuItems[i],
                    (Game.WIDTH - layout.width) / 2,
                    400 - 55 * i);
        }
        sb.end();
    }

    public void handleInput() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (currentItem > 0) {
                currentItem--;
                Jukebox.play("menumove");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (currentItem < menuItems.length - 1) {
                currentItem++;
                Jukebox.play("menumove");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            select();
        }

    }

    private void select() {
        if (currentItem == 0) {
            gsm.setState(GameStateManager.PLAY);
            Jukebox.stop("main");
        }
        if (currentItem == 1) {
            gsm.setState(GameStateManager.HIGHSCORE);
        } else if (currentItem == 2) {
            Gdx.app.exit();
        }
    }

    public void dispose() {
        sb.dispose();
        sr.dispose();
        titleFont.dispose();
        itemFont.dispose();
    }
}
