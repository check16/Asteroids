package managers;

import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Antonio on 25/08/2015.
 */
public class Save {

    public static GameData gd;

    public static void save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("highscores.sav")
            );
            oos.writeObject(gd);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.exit();
        }
    }

    public static void load() {
        try {
            if (!saveFileExists()) {
                init();
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream("highscores.sav")
            );
            gd = (GameData) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.exit();
        }
    }

    public static boolean saveFileExists() {

        File f = new File("highscores.sav");
        return f.exists();
    }

    public static void init() {
        gd = new GameData();
        gd.init();
        save();
    }
}
