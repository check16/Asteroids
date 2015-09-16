package com.mygdx.asteroids;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import managers.GameStateManager;
import managers.Jukebox;

public class Game extends ApplicationAdapter {

    public static int WIDTH;
    public static int HEIGHT;

    public static OrthographicCamera cam;

    private GameStateManager gsm;

    @Override
    public void create() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        cam = new OrthographicCamera(WIDTH, HEIGHT);
        cam.translate(WIDTH / 2, HEIGHT / 2);
        cam.update();

        loadSounds();

        gsm = new GameStateManager();
    }

    private void loadSounds() {
        Jukebox.load("sounds/explode.ogg", "explode");
        Jukebox.load("sounds/extralife.ogg", "extralife");
        Jukebox.load("sounds/largesaucer.ogg", "largesaucer");
        Jukebox.load("sounds/pulsehigh.ogg", "pulsehigh");
        Jukebox.load("sounds/pulselow.ogg", "pulselow");
        Jukebox.load("sounds/saucershoot.ogg", "saucershoot");
        Jukebox.load("sounds/shoot.ogg", "shoot");
        Jukebox.load("sounds/smallsaucer.ogg", "smallsaucer");
        Jukebox.load("sounds/thruster.ogg", "thruster");
        Jukebox.load("sounds/main.mp3", "main");
        Jukebox.load("sounds/menu_move.mp3", "menumove");

    }

    @Override
    public void render() {
        // Borra la pantalla a negro
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.draw();
    }
}
