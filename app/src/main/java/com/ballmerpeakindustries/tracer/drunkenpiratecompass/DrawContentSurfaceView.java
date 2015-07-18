package com.ballmerpeakindustries.tracer.drunkenpiratecompass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by tracer on 7/10/2015.
 */
public class DrawContentSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    Bitmap mBase;
    Bitmap mPointer;
    private AsyncDraw drawTask;
    private float rotation = 0;
    private float lastRotation = 0;


    public DrawContentSurfaceView(Context ctx) {
        super(ctx);

        mBase = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.compass_base);
        mPointer = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.compass_pointer);

        getHolder().addCallback(this);
        drawTask = new AsyncDraw(this.getHolder(), this);
        drawTask.setRunning(true);
        drawTask.start();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        Matrix mPointerMatrix = new Matrix();

        lastRotation = rotation;

        mPointerMatrix.setRotate(rotation, mPointer.getWidth() / 2, mPointer.getHeight() / 2);
        Bitmap mPointerN = Bitmap.createBitmap(mPointer, 0, 0, mPointer.getWidth(), mPointer.getHeight(), mPointerMatrix, true);

        canvas.drawBitmap(mBase, canvas.getWidth() / 2 - mBase.getWidth() / 2, canvas.getHeight() / 2 - mBase.getHeight() / 2, null);
        canvas.drawBitmap(mPointerN, canvas.getWidth() / 2 - mPointerN.getWidth() / 2, canvas.getHeight() / 2 - mPointerN.getHeight() / 2, null);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawTask.setRunning(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawTask.setRunning(false);
        try {
            drawTask.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRotation(double rotation) {
        this.rotation = (float) rotation;
        System.out.println(rotation);
    }

    public void pauseThread(){
        if(drawTask.isRunning())drawTask.setRunning(false);
    }

    public void resumeThread(){
        if(!drawTask.isRunning()){
            drawTask = new AsyncDraw(this.getHolder(), this);
            drawTask.setRunning(true);
            drawTask.start();
        }
    }
}
