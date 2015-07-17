package com.ballmerpeakindustries.tracer.drunkenpiratecompass;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by tracer on 7/10/2015.
 */
public class AsyncDraw extends Thread {
    SurfaceHolder holder;
    DrawContentSurfaceView drawView;
    Canvas canvas;

    private boolean running;

    public AsyncDraw(SurfaceHolder holder, DrawContentSurfaceView drawView) {
        super();
        this.holder = holder;
        this.drawView = drawView;
    }

    public void SetRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {
            canvas = holder.lockCanvas();
            try {
                synchronized (holder) {
                    drawView.onDraw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas);
            }

        }
    }
}
