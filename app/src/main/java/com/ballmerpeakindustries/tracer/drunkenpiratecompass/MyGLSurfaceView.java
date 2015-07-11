package com.ballmerpeakindustries.tracer.drunkenpiratecompass;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by tracer on 7/6/2015.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context){
        super(context);

        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();

        setRenderer(mRenderer);
    }
}
