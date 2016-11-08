package com.zalzer.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.zalzer.game.api.StateManager;

import java.util.ArrayList;

public class PruetDefender extends ApplicationAdapter {
    public static int WIDTH = 800;
    public static int HEIGHT = 480;
    public static String TITLE = "PEFENDER";
    private StateManager stateManager;
    SpriteBatch batch;
    Texture img;
    Texture bg;
    Texture earth;
    Texture touchPic;
    Pixmap pixmap;
    Vector2 vector1, vector2, vectorDraw;
    ArrayList<Integer> pattern;
    int GESTURE_GRID_SIZE = 10;
    int[] histogram;
    ArrayList<String> gestureList;
    int gestureStack;
    BitmapFont font;
    ArrayList<GridPoint2> vectorList;
    int gesture_ans;
    int gesture_prev_ans;
    double last_error;

    String[] shape = {
            "Horizontal Line (right)",
            "Horizontal Line (left)",
            "Vertical Line (up)",
            "Vertical Line (down)",
            "Caret",
            "Caret",
            "Circle",
            "Rectangle"
    };
    int[][] prototype = {{15, 0, 0, 0, 0, 0, 0, 0, 15},
            {0, 15, 0, 0, 0, 0, 0, 0, 15},
            {0, 0, 15, 0, 0, 0, 0, 0, 15},
            {0, 0, 0, 15, 0, 0, 0, 0, 15},
            {0, 0, 0, 0, 15, 15, 0, 0, 30},
            {0, 0, 0, 0, 0, 0, 15, 15, 30},
            {5, 5, 5, 5, 5, 5, 5, 5, 40},
            {5, 5, 5, 5, 0, 0, 0, 0, 20},
            // R L U D UR DR UL DL
    };
    private double MAX_ERROR = 0.25;
    private ParticleEffect star;
    private TextureAtlas particleAtlas;

    @Override
    public void create() {
        stateManager = new StateManager();
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        bg = new Texture(Gdx.files.internal("Background3.png"), true);
        earth = new Texture(Gdx.files.internal("Earth2.png"), true);
        vector1 = new Vector2();
        vector2 = new Vector2();
        vectorDraw = new Vector2();
        pattern = new ArrayList<Integer>();
        font = new BitmapFont();
        Gdx.input.setInputProcessor(processor);
        star = new ParticleEffect();
        star.load(Gdx.files.internal("star.p"), Gdx.files.internal("."));
        star.setPosition(WIDTH/2, HEIGHT/2);
        star.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
//        batch.draw(bg, 0, 0, WIDTH, HEIGHT);
        star.draw(batch, Gdx.graphics.getDeltaTime());
        batch.draw(earth, WIDTH/2 - 100, HEIGHT/2 - 100, 200, 200);
        if (touchPic != null) {
            batch.draw(touchPic, 0, 0);
        }
        if (gestureList != null) {
            if(gestureList.size()>6)
                gestureList.remove(0);
            GlyphLayout layout = new GlyphLayout(font, "Direction: " + gestureList);
            GlyphLayout layout2 = new GlyphLayout(font, "Shape: " + detectShape());
            font.draw(batch, layout, 400 - layout.width / 2, 50);
            font.draw(batch, " error: "+ ((last_error>-1)?last_error:"GTX780TI :X"), 25, 460);
            font.draw(batch, layout2, 400 - layout2.width / 2, 80);
            int  fps = Gdx.graphics.getFramesPerSecond();
            font.draw(batch, "FPS: " + ((fps>24)?fps:"*CENSOR* :P"), 25, 25);
        }
        batch.end();
    }

    private String detectShape() {
        gesture_ans = -1;
        last_error = -1;
        if (histogram == null || histogram[8] == 0)
            return " - ";
        double min_error = 10000000000000.00;
        int ans = -1;
        // Calculate Error with Chi-Square
        double error, dx, dy;
        for (int i = 0; i < prototype.length; i++) {
            error = 0.0;
            for (int j = 0; j < 8; j++) {
                dx = prototype[i][j] / (double) prototype[i][8];
                dy = histogram[j] / (double) histogram[8];
                if (dx > 0) // Avoid NaN
                    error += Math.pow(dx - dy, 2) / (dx);
            }
            if (error < min_error) {
                ans = i;
                min_error = error;
            }
//            Gdx.app.log("shape ", shape[i] + " has error: " + error);
        }
        if (ans == -1)
            return " -- ";
        gesture_ans = min_error > MAX_ERROR ? -1 : ans;
        last_error = min_error;
//        Gdx.app.log("shape", "Most Matched: " + shape[ans] + " error: " + min_error);
        return (min_error > MAX_ERROR) ? "unknow" : shape[ans];
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }

    InputProcessor processor = new InputProcessor() {


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
            pixmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.LIGHT_GRAY);
            vector1.set(screenX, screenY);
            vectorDraw.set(vector1);
            gestureList = new ArrayList<String>();
            vectorList = new ArrayList<GridPoint2>();
            gestureStack = 0;
            gesture_ans = -1;
            gesture_prev_ans = -2;
            histogram = new int[9];
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            Gdx.app.log("Touch", "Finish");
            gestureColor();
//            touchPic = new Texture(pixmap);
            if (pixmap != null)
                pixmap.dispose();
//            histogram[8] = gestureStack;
            String his = "";
            for (int x :
                    histogram) {
                his += x + ", ";
            }
            detectShape();
            touchPic.dispose();
            touchPic = null;
            Gdx.app.log("Histogram", his);
            Gdx.app.log("Gesture", "Data List: " + gestureList);
            return true;
        }

        private void setLineColor(Color color) {
            pixmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
            pixmap.setColor(color);
            for (GridPoint2 point :
                    vectorList) {
                pixmap.fillCircle(point.x, point.y, 3);
            }
            touchPic = new Texture(pixmap);
        }

        private void gestureColor() {
            if (pixmap == null) return;
            if (gesture_ans == gesture_prev_ans)
                return;
            gesture_prev_ans = gesture_ans;
            switch (gesture_ans) {
                case 0:
                    setLineColor(Color.RED);
                    break;
                case 1:
                    setLineColor(Color.RED);
                    break;
                case 2:
                    setLineColor(Color.BLUE);
                    break;
                case 3:
                    setLineColor(Color.BLUE);
                    break;
                case 4:
                    setLineColor(Color.YELLOW);
                    break;
                case 5:
                    setLineColor(Color.YELLOW);
                    break;
                case 6:
                    setLineColor(Color.PINK);
                    break;
                case 7:
                    setLineColor(Color.MAGENTA);
                    break;
                default:
                    setLineColor(Color.LIGHT_GRAY);
                    break;
            }
        }

        private String degreeToString(double degree) {
            histogram[8]++;
            if (degree <= 22.5 && degree >= -22.5) {
                histogram[0]++;
                return "RIGHT";
            } else if (degree >= -180 && degree <= -157.5 || degree <= 180 && degree >= 157.5) {
                histogram[1]++;
                return "LEFT";
            } else if (degree <= -67.5 && degree >= -112.5) {
                histogram[2]++;
                return "UP";
            } else if (degree <= 112.5 && degree >= 67.5) {
                histogram[3]++;
                return "DOWN";
            } else if (degree <= -22.5 && degree >= -67.5) {
                histogram[4]++;
                return "UP-RIGHT";
            } else if (degree <= 67.5 && degree >= 22.5) {
                histogram[5]++;
                return "DOWN-RIGHT";
            } else if (degree <= -112.5 && degree >= -157.5) {
                histogram[6]++;
                return "UP-LEFT";
            } else if (degree <= 157.5 && degree >= 112.5) {
                histogram[7]++;
                return "DOWN-LEFT";
            }
            histogram[8]--;
            Gdx.app.log("Gesture", "Unknow degree " + degree);
            return "UNKNOW";
        }

        private void saveDirection(double degree) {
            String direction = degreeToString(degree);
            if (gestureList.isEmpty() || (!gestureList.isEmpty() && gestureList.get(gestureList.size() - 1).compareTo(direction) != 0)) {
                gestureList.add(direction);
            }
//            Gdx.app.log("Gesture", "Detect: " + direction + " Stack: " + gestureStack);
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
//            Gdx.app.log("Touch", "Drag: " + screenX + " " + screenY);
            Bresenham2 b = new Bresenham2();
            for (GridPoint2 point : b.line((int) vectorDraw.x, (int) vectorDraw.y, screenX, screenY)) {
                pixmap.fillCircle(point.x, point.y, 3);
                vectorList.add(point);
            }

            gestureColor();
            touchPic = new Texture(pixmap);

            vectorDraw.set(screenX, screenY);

            if (Math.abs(screenX - vector1.x) < GESTURE_GRID_SIZE && Math.abs(screenY - vector1.y) < GESTURE_GRID_SIZE)
                return true;

            double degree = Math.toDegrees(Math.atan2(screenY - vector1.y, screenX - vector1.x));
            saveDirection(degree);
//            Gdx.app.log("Gesture", "Angle " + degree);

            vector1.set(screenX, screenY);
            gestureColor();
//            touchPic = new Texture(pixmap);

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
