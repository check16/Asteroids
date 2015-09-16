package gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.asteroids.Game;

import managers.GameStateManager;
import managers.Save;

/**
 * Created by Antonio on 26/08/2015.
 */
public class GameOverState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;
    private GlyphLayout layout;

    private boolean newHighScore;
    private char[] newName;
    private int currentChar;

    private BitmapFont gameOverFont;
    private BitmapFont font;

    public GameOverState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {
        sb = new SpriteBatch();
        layout = new GlyphLayout();
        sr = new ShapeRenderer();

        newHighScore = Save.gd.isHighScore(Save.gd.getTentativeScore());
        if (newHighScore) {
            newName = new char[]{'A', 'A', 'A'};
            currentChar = 0;
        }

        setFonts();
    }

    private void setFonts() {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 46;
        gameOverFont = gen.generateFont(param);
        param.size = 24;
        font = gen.generateFont(param);
    }

    public void update(float dt) {
        handleInput();

    }

    public void draw() {
        sb.setProjectionMatrix(Game.cam.combined);
        sb.begin();
        String s;
        s = "Game Over";
        layout.setText(gameOverFont, s);
        gameOverFont.draw(sb, s, (Game.WIDTH - layout.width) / 2, 600);
        if (!newHighScore) {
            sb.end();
            return;
        }
        s = "New High Score: " + Save.gd.getTentativeScore();
        layout.setText(font, s);
        font.draw(sb, s, (Game.WIDTH - layout.width) / 2, 500);

        for (int i = 0; i < newName.length; i++) {
            font.draw(sb, Character.toString(newName[i]), (Game.WIDTH - layout.width) / 2 + 14 * i, 420);
        }

        sb.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.line((Game.WIDTH - layout.width) / 2 + 14 * currentChar, 400, ((Game.WIDTH - layout.width) / 2 + 14) + 14 * currentChar, 400);
        sr.end();
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (newHighScore) {
                Save.gd.addHighScore(Save.gd.getTentativeScore(), new String(newName));
                Save.save();
            }
            gsm.setState(GameStateManager.MENU);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (newName[currentChar] == ' ') {
                newName[currentChar] = 'Z';
            } else {
                newName[currentChar]--;
                if (newName[currentChar] < 'A') {
                    newName[currentChar] = ' ';
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (newName[currentChar] == ' ') {
                newName[currentChar] = 'A';
            } else {
                newName[currentChar]++;
                if (newName[currentChar] > 'Z') {
                    newName[currentChar] = ' ';
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (currentChar < newName.length - 1) {
                currentChar++;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (currentChar > 0) {
                currentChar--;
            }
        }
    }

    public void dispose() {
        sb.dispose();
        sr.dispose();
        gameOverFont.dispose();
        font.dispose();

    }
}
