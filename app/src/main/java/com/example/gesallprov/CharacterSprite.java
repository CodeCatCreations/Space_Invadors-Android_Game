package com.example.gesallprov;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class CharacterSprite {
    private Bitmap image;
    private int x, y;
    private boolean destroyed = false;
    private boolean isKillable;
    private boolean reachedBottom = false;
    private long destroyedTime;
    private boolean finished;

    public CharacterSprite(Bitmap bmp) {
        image = bmp;
        x = new Random().nextInt(Resources.getSystem().getDisplayMetrics().widthPixels);
        if (x >= Resources.getSystem().getDisplayMetrics().widthPixels - image.getWidth()){
            x -= image.getWidth();
        } else if (x < image.getWidth()){
            x += image.getWidth();
        }
        y = 0;
        isKillable = true;
    }

    public void draw(Canvas canvas) {
        if(!destroyed) {
            canvas.drawBitmap(image, x, y, null);
        } else {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(42);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setShadowLayer(2, 0, 0, Color.BLACK);
            canvas.drawText("30 points!", x+getWidth()/2, y+getHeight()/2, textPaint);
        }
    }

    public void update() {
        if (!destroyed){
            y += 40;
            if (y >= Resources.getSystem().getDisplayMetrics().heightPixels){
                reachedBottom = true;
                destroyed = true;
                destroyedTime = System.currentTimeMillis();
            }
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - destroyedTime > 2000) {
                finished = true;
            }
        }
    }

    public void destroy() {
        destroyed = true;
    }
    public boolean isDestroyed() {
        return destroyed;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getWidth(){
        return image.getWidth();
    }

    public int getHeight(){
        return image.getHeight();
    }

    public boolean getReachedBottom(){
        return reachedBottom;
    }

    public boolean isKillable(){
        return isKillable;
    }

    public boolean getFinished(){
        return finished;
    }

}
