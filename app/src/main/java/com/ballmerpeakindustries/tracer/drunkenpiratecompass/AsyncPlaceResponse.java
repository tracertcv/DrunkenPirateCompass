package com.ballmerpeakindustries.tracer.drunkenpiratecompass;

import java.util.ArrayList;

/**
 * Created by tracer on 7/10/2015.
 */
public interface AsyncPlaceResponse {
    public void onProcessFinish(ArrayList<Place> placesOut, String lastJSON);
}
