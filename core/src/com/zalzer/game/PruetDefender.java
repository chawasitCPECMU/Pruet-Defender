package com.zalzer.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.zalzer.game.api.StateManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class PruetDefender extends ApplicationAdapter {
    public static int WIDTH = 800;
    public static int HEIGHT = 480;
    public static String TITLE = "Pruet Defender";
    private StateManager stateManager;
    SpriteBatch batch;
    Texture img;
    int lastPoint[];
    boolean[][] gmap;
    Texture touchPic;
    Pixmap pixmap;

    @Override
    public void create() {
        stateManager = new StateManager();
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        gmap = new boolean[HEIGHT + 10][WIDTH + 10];
        lastPoint = new int[2];
        Gdx.input.setInputProcessor(processor);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (touchPic != null)
            batch.draw(touchPic, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }

    InputProcessor processor = new InputProcessor() {
        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Gdx.app.log("Touch", "Touch Down: " + screenX + " " + screenY);
            gmap[screenY][screenX] = true;
            pixmap = new Pixmap(800, 480, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            lastPoint[0] = screenX;
            lastPoint[1] = screenY;
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            Gdx.app.log("Touch", "Finish");
            if (pixmap != null)
                pixmap.dispose();
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            Gdx.app.log("Touch", "Drag: " + screenX + " " + screenY);

            Bresenham2 b = new Bresenham2();
            for (GridPoint2 point : b.line(lastPoint[0], lastPoint[1], screenX, screenY))
//                    pixmap.drawPixel(point.x, point.y);
            pixmap.fillCircle(point.x, point.y, 3);


            touchPic = new Texture(pixmap);
            lastPoint[1] = screenY;
            lastPoint[0] = screenX;
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    };

}
