package com.ballmerpeakindustries.tracer.drunkenpiratecompass;

import android.location.Location;

/**
 * Created by tracer on 7/6/2015.
 */
public class Place implements Comparable{

    protected Location loc;
    protected String name;
    protected float distanceTo;


    public Place(){
        loc.setLongitude(0);
        loc.setLongitude(0);
        distanceTo = 0;
        name = "Null";
    }

    public Place(Location l, String n, float d){
        loc = l;
        name = n;
        distanceTo = d;
    }

    public void setLoc (Location l){
        loc = l;
    }
    public void setName (String n){
        name = n;
    }
    public void setDistance(float d){
        distanceTo=d;
    }

    @Override
    public int compareTo(Object other)throws ClassCastException{
        float otherDist = ((Place)other).distanceTo;
        if(this.distanceTo<otherDist)return -1;
        else if(this.distanceTo>otherDist)return 1;
        return 0;
    }

    @Override
    public boolean equals(Object p){
        boolean isEqual = false;
        if(p!= null && p instanceof Place){
            isEqual = (((Place)p).name.equals(this.name));
        }
        return isEqual;
    }
}