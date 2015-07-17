package com.ballmerpeakindustries.tracer.drunkenpiratecompass;

import android.location.Location;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class AsyncPlaceGetter extends AsyncTask<String, Void, String> {

    ArrayList<Place> placeList = null;
    Location location = new Location("dummyprovider");
    String lastJSONResponse = "";
    AsyncPlaceResponse delegate = null;

    public AsyncPlaceGetter(ArrayList<Place> p, Location l, String s) {
        placeList = p;
        location = l;
        lastJSONResponse = s;
    }

    @Override
    protected String doInBackground(String... PlacesURL) {
        if (placeList.size() == 0) {
            StringBuilder placesBuilder = new StringBuilder();
            for (String placesSearchUrl : PlacesURL) {
                HttpClient placesClient = new DefaultHttpClient();
                try {
                    HttpGet placesGet = new HttpGet(placesSearchUrl);
                    HttpResponse placesResponse = placesClient.execute(placesGet);
                    if (placesResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity placesEntity = placesResponse.getEntity();
                        InputStream placesContent = placesEntity.getContent();
                        InputStreamReader placesInput = new InputStreamReader(placesContent);
                        BufferedReader placesReader = new BufferedReader(placesInput);
                        String lineIn;
                        while ((lineIn = placesReader.readLine()) != null) {
                            placesBuilder.append(lineIn);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                lastJSONResponse = placesBuilder.toString();

            }
        }
        return lastJSONResponse;
    }

    @Override
    protected void onPostExecute(String placesResponse) {
        try {
            JSONObject placeResponseObject = new JSONObject(placesResponse);
            JSONArray placeArray = placeResponseObject.getJSONArray("results");
            String NamesByDistance = "";
            for (int i = 0; i < placeArray.length(); i++) {

                JSONObject place = placeArray.getJSONObject(i);
                JSONObject geo = place.getJSONObject("geometry").getJSONObject("location");
                Location tempLoc = new Location("dummyprovider");


                Double lati = Double.parseDouble(geo.getString("lat"));
                Double longi = Double.parseDouble(geo.getString("lng"));

                String tempName = place.getString("name");

                tempLoc.setLatitude(lati);
                tempLoc.setLongitude(longi);
                float distance = location.distanceTo(tempLoc);
                Place p = new Place(tempLoc, tempName, distance);

                if (placeList.contains(p)) {
                    placeList.get(placeList.indexOf(p)).setDistance(distance);
                } else {
                    placeList.add(p);
                }
            }
            Collections.sort(placeList);
            for (Place p : placeList) {
                String s = p.name + ": " + p.distanceTo + "m " + location.bearingTo(p.loc) + "\n";
                NamesByDistance += s;
            }
            System.out.println(placeList.get(0).name);
            delegate.onProcessFinish(placeList, lastJSONResponse);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}