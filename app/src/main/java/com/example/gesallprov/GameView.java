package com.example.gesallprov;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private MainThread mainThread;
    private Bitmap mUniverseBitmap;
    private ArrayList<Rain> rain;
    private ArrayList<CharacterSprite> sprites;
    private Timer rainTimer, characterSpriteTimer;
    private final int POINTS_FOR_BALL = 10;
    private final int POINTS_FOR_SPRITE = 30;
    private Player player;
    private int spriteCounter = 1;
    private boolean gameStarted, showRestartButton;
    private Paint startPaint, restartPaint;
    private RectF startRect;
    private Rect restartRect;
    // private Rect startRect, restartRect;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public GameView(Context context, Bitmap universeBitmap) {
        super(context);
        mUniverseBitmap = universeBitmap;
        getHolder().addCallback(this);
        setOnTouchListener(this);

        sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mainThread = new MainThread(getHolder(), this);
        setFocusable(true);
        player = new Player();
        player.setHighscore(getHighScore());
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        mainThread.setRunning(true);
        mainThread.start();
        scheduleRainGeneration();
        scheduleCharacterSpriteGeneration();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                mainThread.setRunning(false);
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {
        if (!player.gameOver()) {
            rainIterator();
            spriteIterator();
            player.update();
            editor.putInt("highScore", player.getHighscore());
            editor.apply();
        } else {
            saveHighScore();
            showRestartButton = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            // Ordningen spelar roll, först hamnar underst
            // canvas.drawColor(Color.WHITE); // Bakgrundsfärg
            Rect rect = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(mUniverseBitmap, null, rect, new Paint());


            if (!gameStarted){
                drawStartButton(canvas);
                return;
            }

            if (showRestartButton) {
                drawRestartButton(canvas);
            } else {

                for (CharacterSprite sprite : sprites){
                    sprite.draw(canvas);
                }

                for (Rain ball : rain){
                    ball.draw(canvas);
                }
            }
            player.draw(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (startRect != null){
                if (x > startRect.left && x < startRect.right + startRect.width()
                        && y > startRect.top && y < startRect.top + startRect.height()) {
                    restartGame();
                    return true;
                }
            }

            else if (restartRect != null) {
                if (x > restartRect.left && x < restartRect.right + restartRect.width()
                        && y > restartRect.top && y < restartRect.top + restartRect.height()) {
                    restartGame();
                    return true;
                }
            } else {
                rainOnTouchIterator(view, event);
                spriteOnTouchIterator(view, event);
            }
        }
        return true;
    }

    private void rainOnTouchIterator(View view, MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();
        Iterator<Rain> itr = rain.iterator();

        while (itr.hasNext()){
            Rain ball = itr.next();
            int buffer = 4;
            if (ball.getFinished()){
                itr.remove();
            }
            if (x > ball.getX() - buffer && x < ball.getX() + ball.getWidth() + buffer
                    && y > ball.getY() - buffer && y < ball.getY() + ball.getHeight() + buffer) {
                if (!ball.isDestroyed()){
                    ball.destroy();
                    player.addPoints(POINTS_FOR_BALL);
                }
            }
        }
    }

    private void spriteOnTouchIterator(View view, MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();
        Iterator<CharacterSprite> itr = sprites.iterator();
        while (itr.hasNext()){
            CharacterSprite sprite = itr.next();
            if (sprite.isKillable()) {
                if (sprite.getFinished()){
                    itr.remove();
                }
                if (x > sprite.getX() && x < sprite.getX() + sprite.getWidth()
                        && y > sprite.getY() && y < sprite.getY() + sprite.getHeight()) {
                    if (!sprite.isDestroyed()){
                        sprite.destroy();
                        player.addPoints(POINTS_FOR_SPRITE);
                    }
                    // itr.remove();
                }
            }
        }
    }

    private void drawStartButton(Canvas canvas) {
        int x = getWidth()/2;
        int y = (getHeight()/2); // 30 pixels under the "Game Over" text
        int buttonWidth = 600;
        int buttonHeight = 200;
        int cornerRadius = 20;
        int shadowRadius = 10;
        int shadowColor = Color.parseColor("#33000000");
        int backgroundColor = Color.parseColor("#4CAF50");
        // int textColor = Color.WHITE;
        String buttonText = "Start";

        Paint textPaint = new Paint();
        textPaint.setTextSize(150);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(4);
        textPaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);


        startRect = new RectF(x - buttonWidth/2, y - buttonHeight/2, x + buttonWidth/2, y + buttonHeight/2);

        startPaint = new Paint();
        startPaint.setColor(backgroundColor);
        startPaint.setStyle(Paint.Style.FILL);
        startPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);

        canvas.drawRoundRect(startRect, cornerRadius, cornerRadius, startPaint);
        canvas.drawText(buttonText, startRect.centerX(), startRect.centerY() - 20 + textPaint.getTextSize()/2, textPaint);
    }

    private void drawRestartButton(Canvas canvas){
        rainTimer.purge();
        characterSpriteTimer.purge();

        restartPaint = new Paint();
        restartPaint.setColor(Color.GREEN);
        restartPaint.setStyle(Paint.Style.FILL);
        restartPaint.setTextAlign(Paint.Align.CENTER);

        Paint textPaint = new Paint();
        textPaint.setTextSize(45);
        textPaint.setColor(Color.WHITE);

        int x = getWidth()/2;
        int y = (getHeight()/2) + 100; // 30 pixels under the "Game Over" text
        restartRect = new Rect(x - 150, y - 60, x + 150, y); // 150 pixels wide and 75 pixels tall

        canvas.drawRect(restartRect,restartPaint);
        canvas.drawText("Restart", restartRect.centerX() - 70, restartRect.centerY() + 5 + restartPaint.getTextSize()/2, textPaint);
    }

    private void restartGame(){
        //reset necessary variables and objects
        gameStarted = true;
        startRect = null;
        startPaint = null;
        showRestartButton = false;
        restartRect = null;
        restartPaint = null;
        player = new Player();
        player.setHighscore(getHighScore());
        spriteCounter = 1;
        rain.clear();
        sprites.clear();
        scheduleRainGeneration();
        scheduleCharacterSpriteGeneration();
    }

    private void rainIterator(){
        Iterator<Rain> itr = rain.iterator();
        while (itr.hasNext()){
            Rain ball = itr.next();
            ball.update();
            if (ball.getReachedBottom()) {
                player.removePoints(POINTS_FOR_BALL);
                itr.remove();
            }
        }
    }
    private void spriteIterator(){
        Iterator<CharacterSprite> itr = sprites.iterator();
        while (itr.hasNext()){
            CharacterSprite sprite = itr.next();
            sprite.update();
            if (sprite.getReachedBottom()) {
                player.removePoints(POINTS_FOR_SPRITE);
                itr.remove();
            }
        }
    }

    private void scheduleRainGeneration() {
        rainTimer = new Timer();
        int delay = 2000; // delay of 3 seconds
        int period = 3000; // repeat every 3 seconds
        rain = new ArrayList<>();
        rainTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(rain.size()<10){
                    rain.add(new Rain());
                }
            }
        }, delay, period);
    }
    private void scheduleCharacterSpriteGeneration() {
        characterSpriteTimer = new Timer();
        int delay = 2000; // delay of 3 seconds
        int period = 8000; // repeat every 8 seconds
        sprites = new ArrayList<>();
        characterSpriteTimer.scheduleAtFixedRate(new TimerTask () {
            @Override
            public void run() {
                if(sprites.size()<3){
                    if (spriteCounter % 2 == 0){
                        sprites.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(),R.drawable.batman)));
                    } else {
                        sprites.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(),R.drawable.hitman)));
                    }
                    spriteCounter++;
                }

            }
        }, delay, period);
    }

    private void saveHighScore(){
        editor.putInt("highScore", player.getHighscore());
        editor.commit();
    }
    private int getHighScore(){
        return sharedPreferences.getInt("highScore", 0);
    }

}