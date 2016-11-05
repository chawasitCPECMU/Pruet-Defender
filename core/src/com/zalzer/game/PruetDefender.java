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
import com.badlogic.gdx.math.Vector2;
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
    Texture touchPic;
    Pixmap pixmap;
    Vector2 vector1, vector2, vectorDraw;
    ArrayList<Integer> pattern;

    @Override
    public void create() {
        stateManager = new StateManager();
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        vector1 = new Vector2();
        vector2 = new Vector2();
        vectorDraw = new Vector2();
        pattern = new ArrayList<Integer>();
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

        int GRID_SIZE = 30;
        ArrayList<String> list;
        int stack;

        @Override
        public boolean keyDown(int keycode) {
            Gdx.app.log("Key", keycode + " fps: " + Gdx.graphics.getFramesPerSecond());

            return true;
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
            pixmap = new Pixmap(800, 480, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.LIGHT_GRAY);
            vector1.set(screenX, screenY);
            vectorDraw.set(vector1);
            list = new ArrayList<String>();
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            Gdx.app.log("Touch", "Finish");

            touchPic = new Texture(pixmap);
            if (pixmap != null)
                pixmap.dispose();
            Gdx.app.log("Gesture", "Data List: " + list);
            return true;
        }

        private String degreeToString(double degree) {
            if (degree <= 22.5 && degree >= -22.5)
                return "RIGHT";
            if (degree >= -180 && degree <= -157.5 || degree <= 180 && degree >= 157.5)
                return "LEFT";
            if (degree <= -67.5 && degree >= -112.5)
                return "UP";
            if (degree <= 112.5 && degree >= 67.5)
                return "DOWN";
            if (degree <= -22.5 && degree >= -67.5)
                return "UP-RIGHT";
            if (degree <= 67.5 && degree >= 22.5)
                return "DOWN-RIGHT";
            if (degree <= -112.5 && degree >= -157.5)
                return "UP-LEFT";
            if (degree <= 157.5 && degree >= 112.5)
                return "DOWN-LEFt";
            Gdx.app.log("Gesture", "Unknow degree " + degree);
            return "UNKNOW";
        }

        private void saveDirection(double degree) {
            stack++;
            String direction = degreeToString(degree);
            if (list.isEmpty() || (!list.isEmpty() && list.get(list.size() - 1).compareTo(direction) != 0)) {
                list.add(direction);
                stack = 1;
            }

            Gdx.app.log("Gesture", "Detect: " + direction + " stack: " + stack);
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
//            Gdx.app.log("Touch", "Drag: " + screenX + " " + screenY);
            Bresenham2 b = new Bresenham2();
            for (GridPoint2 point : b.line((int) vectorDraw.x, (int) vectorDraw.y, screenX, screenY))
                pixmap.fillCircle(point.x, point.y, 3);
            touchPic = new Texture(pixmap);

            vectorDraw.set(screenX, screenY);

            if (Math.abs(screenX - vector1.x) < GRID_SIZE && Math.abs(screenY - vector1.y) < GRID_SIZE)
                return true;

            double degree = Math.toDegrees(Math.atan2(screenY - vector1.y, screenX - vector1.x));
            saveDirection(degree);
//            Gdx.app.log("Gesture", "Angle " + degree);

            vector1.set(screenX, screenY);
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
