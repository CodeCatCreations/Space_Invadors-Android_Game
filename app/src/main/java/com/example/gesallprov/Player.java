package com.example.gesallprov;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player {
    private int points;
    private int highscore;
    private Paint paint;
    private int x, y;
    private boolean gameOver = false;
    private Paint gameOverPaint;

    public Player(){
        points = 0;
        highscore = 0;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(70);
        paint.setAlpha(180);
        x = Resources.getSystem().getDisplayMetrics().widthPixels / 5;
        y = Resources.getSystem().getDisplayMetrics().heightPixels - 50;
    }

    public void update() {
        if (points > highscore){
            highscore = points;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawText("Highscore: " + highscore, 50, y, paint);
        canvas.drawText("Score: " + points, x + paint.measureText("Highscore: " + highscore) + 10, y, paint);
        if (gameOver) {
            gameOverPaint = new Paint();
            gameOverPaint.setColor(Color.RED);
            gameOverPaint.setTextSize(150);
            gameOverPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Game Over", canvas.getWidth()/2, canvas.getHeight()/2, gameOverPaint);
        }
    }

    public void addPoints(int x){
        points += x;
    }

    public void removePoints(int x){
        if (points - x <= 0){
            points = 0;
            gameOver = true;
        } else if (points > 150 && points <= 300) {
            points -= (x * 2);
        } else if (points > 300 && points <= 500){
            points -= (x*3);
        } else if (points > 500) {
            points -= (x*4);
        }
        else {
            points -= x;
        }
    }

    public int getHighscore(){
        return highscore;
    }

    public boolean gameOver(){
        return gameOver;
    }

    public void setHighscore(int x){
        highscore = x;
    }

}
