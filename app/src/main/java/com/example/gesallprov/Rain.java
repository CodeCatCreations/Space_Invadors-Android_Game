package com.example.gesallprov;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.PaintDrawable;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Rain {
    private int x, y;
    private float radius;
    private Paint paint;
    private boolean destroyed = false;
    private boolean reachedBottom = false;
    private long destroyedTime;
    private boolean finished;

    public Rain() {
        radius = 70;
        x = new Random().nextInt(Resources.getSystem().getDisplayMetrics().widthPixels);
        if (x >= Resources.getSystem().getDisplayMetrics().widthPixels - radius){
            x -= radius;
        } else if (x < radius) {
            x += radius;
        }
        y = 0;
        paint = new Paint();
        paint.setColor(Color.rgb(192,192,192));
    }

    public void draw(Canvas canvas) {
        if (!isDestroyed()){
            canvas.drawCircle(x, y, radius, paint);
        } else {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(28);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setShadowLayer(2, 0, 0, Color.BLACK);
            canvas.drawText("10 points!", x, y, textPaint);
        }
    }

    public void update() {
        if (!isDestroyed()){
            y += 20;
            if (y >= Resources.getSystem().getDisplayMetrics().heightPixels){
                reachedBottom = true;
                destroyed = true;
                destroyedTime = System.currentTimeMillis();
            }
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - destroyedTime > 1000) {
                finished = true;
            }
        }
    }

    public boolean getFinished(){
        return finished;
    }
    public boolean getReachedBottom(){
        return reachedBottom;
    }

    public void destroy() {
        destroyed = true;
        destroyedTime = System.currentTimeMillis();
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getWidth(){
        return radius;
    }

    public float getHeight(){
        return radius;
    }

}