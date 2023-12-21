package se.onemanstudio.randy.old;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import timber.log.Timber;

public class BreakoutGameActivity extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    BreakoutView breakoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);

    }

    // Here is our implementation of BreakoutView
    // It is an inner class.
    // Note how the final closing curly brace }
    // is inside the BreakoutGameActivity class

    // Notice we implement runnable so we have
    // A thread and can override the run method.
    class BreakoutView extends SurfaceView implements Runnable {
        private int x = 0;

        // This is our thread
        Thread gameThread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        final SurfaceHolder ourHolder;

        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;

        // Game is paused at the start
        boolean paused = true;

        // A Canvas and a Paint object
        Canvas canvas;
        final Paint paint;

        // This variable tracks the game frame rate
        long fps;

        // The size of the screen in pixels
        final int screenX;
        final int screenY;

        // The players paddle
        final Paddle paddle;

        // A ball
        final Ball ball;

        // Up to 200 bricks
        final Brick[] bricks = new Brick[200];
        int numBricks = 0;

        // For sound FX
        final SoundPool soundPool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loseLifeID = -1;
        int explodeID = -1;

        // The score
        int score = 0;

        // Lives
        int lives = 3;

        /** @noinspection deprecation*/ // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public BreakoutView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            paddle = new Paddle(screenX, screenY);

            // Create a ball
            ball = new Ball(screenX, screenY);

            // Load the sounds

            // This SoundPool is deprecated but don't worry
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

            try {
                // Create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                // Load our fx in memory ready for use
                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);

            } catch (IOException e) {
                // Print an error message to the console
                Timber.tag("error").e("failed to load sound files");
            }

            createBricksAndRestart();

        }

        public void createBricksAndRestart() {
            // Put the ball back to the start
            ball.reset(screenX, screenY);

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;

            // Build a wall of bricks
            numBricks = 0;
            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 3; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }
            // if game over reset scores and lives
            if (lives == 0) {
                score = 0;
                lives = 3;
            }
        }

        @Override
        public void run() {
            while (playing) {
                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();
                // Update the frame
                if (!paused) {
                    update();
                }
                // Draw the frame
                draw();
                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                // This is used to help calculate the fps
                long timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        // Everything that needs to be updated goes in here
        // Movement, collision detection etc.
        public void update() {
            x += 1;
            Timber.d("X: %d", x);

            // Move the paddle if required
            paddle.update(fps);

            ball.update(fps);

            // Check for ball colliding with a brick
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                    }
                }
            }
            // Check for ball colliding with paddle
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - 2);
                soundPool.play(beep1ID, 1, 1, 0, 0, 1);
            }
            // Bounce the ball back when it hits the bottom of screen
            if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 2);

                // Lose a life
                lives--;
                soundPool.play(loseLifeID, 1, 1, 0, 0, 1);

                if (lives == 0) {
                    paused = true;
                    createBricksAndRestart();
                }
            }

            // Bounce the ball back when it hits the top of screen
            if (ball.getRect().top < 0) {
                ball.reverseYVelocity();
                ball.clearObstacleY(12);

                soundPool.play(beep2ID, 1, 1, 0, 0, 1);
            }

            // If the ball hits left wall bounce
            if (ball.getRect().left < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // If the ball hits right wall bounce
            if (ball.getRect().right > screenX - 10) {

                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22);

                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // Pause if cleared screen
            if (score == numBricks * 10) {
                paused = true;
                createBricksAndRestart();
            }

        }

        // Draw the newly updated scene
        public void draw() {
            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255, 26, 128, 182));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));


                //canvas.drawColor(Color.WHITE);
                canvas.drawRect(new RectF(x, 800, x + 50, 800 + 50), paint);

                canvas.drawRect(new RectF(2 * x, 900, 2 * x + 50, 900 + 50), paint);


                // Draw the paddle
                canvas.drawRect(paddle.getRect(), paint);

                // Draw the ball
                canvas.drawRect(ball.getRect(), paint);

                // Change the brush color for drawing
                paint.setColor(Color.argb(255, 249, 129, 0));

                // Draw the bricks if visible
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                // Draw the score
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + "   Lives: " + lives, 10, 50, paint);

                // Has the player cleared the screen?
                if (score == numBricks * 10) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10, (float) screenY / 2, paint);
                }

                // Has the player lost?
                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!", 10, (float) screenY / 2, paint);
                }

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        /** @noinspection BlockingMethodInNonBlockingContext*/ // If SimpleGameEngine Activity is paused/stopped shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Timber.e("joining thread");
            }
        }

        // If SimpleGameEngine Activity is started then start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                // Player has touched the screen
                case MotionEvent.ACTION_DOWN -> {
                    paused = false;
                    if (motionEvent.getX() > (float) screenX / 2) {

                        paddle.setMovementState(paddle.RIGHT);
                    } else {
                        paddle.setMovementState(paddle.LEFT);
                    }
                }

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP -> paddle.setMovementState(paddle.STOPPED);
            }

            return true;
        }
    }
    // This is the end of our BreakoutView inner class

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        breakoutView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        breakoutView.pause();
    }
}
// This is the end of the BreakoutGameActivity class