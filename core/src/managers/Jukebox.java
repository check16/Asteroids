package managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

/**
 * Created by Antonio on 24/08/2015.
 */
public class Jukebox {

    private static HashMap<String, Sound> sounds;

    static {
        sounds = new HashMap<String, Sound>();
    }

    public static void load(String path, String name) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sounds.put(name, sound);
    }

    public static void play(String name) {
        sounds.get(name).play();
    }

    public static void loop(String name) {
        sounds.get(name).loop(0.5f);
    }

    public static void stop(String name) {
        sounds.get(name).stop();
    }

    public static void stopAll() {
        for(Sound sound : sounds.values()) {
            sound.stop();
        }
    }
}
