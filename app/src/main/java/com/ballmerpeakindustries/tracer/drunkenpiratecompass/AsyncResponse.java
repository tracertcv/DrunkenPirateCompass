package com.ballmerpeakindustries.tracer.drunkenpiratecompass;

import java.util.ArrayList;

/**
 * Created by tracer on 7/10/2015.
 */
public interface AsyncResponse {
    public void onProcessFinish(ArrayList<Place> placesOut, String lastJSON);
}
