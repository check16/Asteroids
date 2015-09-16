package gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.mygdx.asteroids.Game;

import managers.GameStateManager;
import managers.Jukebox;
import managers.Save;

/**
 * Created by Antonio on 25/08/2015.
 */
public class HighScoreState extends GameState {

    private SpriteBatch sb;
    private BitmapFont font;
    private GlyphLayout layout;
    private long[] highScores;
    private String[] names;


    public HighScoreState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {
        sb = new SpriteBatch();
        layout = new GlyphLayout();
        setFonts();
        Save.load();
        highScores = Save.gd.getHighScores();
        names = Save.gd.getNames();
    }

    private void setFonts() {

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 30;
        font = gen.generateFont(param);
    }

    public void update(float dt) {
        handleInput();
    }

    public void draw() {

        sb.setProjectionMatrix(Game.cam.combined);
        sb.begin();

        String s;
        s = "High Scores";
        layout.setText(font, s);
        font.draw(sb, s, (Game.WIDTH - layout.width) / 2, 600);

        for (int i = 0; i < highScores.length; i++) {
            s = String.format("%2d. %7s %s", i + 1, highScores[i], names[i]);
            font.draw(sb, s, (Game.WIDTH - layout.width - 100) / 2, 550 - 40 * i);
        }
        sb.end();

    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Jukebox.stop("main");
            gsm.setState(GameStateManager.MENU);

        }
    }

    public void dispose() {
        sb.dispose();
        font.dispose();
    }
}
