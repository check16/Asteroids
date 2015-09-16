package managers;

import java.io.Serializable;

/**
 * Created by Antonio on 25/08/2015.
 */
public class GameData implements Serializable {

    private static final long serialVersionUID = 1;

    private final int MAX_SCORES = 10;

    private long[] highScores;
    private String[] names;

    private long tentativeScore;

    public GameData() {
        highScores = new long[MAX_SCORES];
        names = new String[MAX_SCORES];
    }

    public void init() {
        for (int i = 0; i < MAX_SCORES; i++) {
            highScores[i] = 0;
            names[i] = "---";
        }
    }

    public long[] getHighScores() {
        return highScores;
    }

    public String[] getNames() {
        return names;
    }

    public long getTentativeScore() {
        return tentativeScore;
    }

    public void setTentativeScore(long tentativeScore) {
        this.tentativeScore = tentativeScore;
    }

    public boolean isHighScore(long score) {
        return score > highScores[MAX_SCORES - 1];
    }

    public void addHighScore(long newScore, String name) {
        if (isHighScore(newScore)) {
            highScores[MAX_SCORES - 1] = newScore;
            names[MAX_SCORES - 1] = name;
            sortHighScores();
        }
    }

    private void sortHighScores() {
        for (int i = 0; i < MAX_SCORES; i++) {
            long score = highScores[i];
            String name = names[i];
            int j;
            for (j = i - 1;
                 j >= 0 && highScores[j] < score;
                 j--) {
                highScores[j + 1] = highScores[j];
                names[j + 1] = names[j];
            }
            highScores[j + 1] = score;
            names[j + 1] = name;
        }
    }
}
