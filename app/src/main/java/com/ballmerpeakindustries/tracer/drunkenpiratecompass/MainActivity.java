package com.ballmerpeakindustries.tracer.drunkenpiratecompass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements LocationListener, SensorEventListener, AsyncPlaceResponse {

    //Update distance
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    //Update time
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    //Location manager
    protected LocationManager locationManager;
    //Status flag
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double bearing;
    double latitude;
    double longitude;
    ArrayList<Place> placeList = new ArrayList<Place>();
    String locationString = "0,0";
    String radiusString = "1000";
    String typeString = "bar|liquor_store";
    String apiKey = "AIzaSyBQrk0CbUkTETN4KPzxezRfOjOwoGWQW28";
    String lastJSONResponse = "";
    SensorManager mSensorManager;
    Sensor accel;
    Sensor magnet;
    float mGravity[];
    float mGeomag[];
    DrawContentSurfaceView drawView;

    private Location updateLocation() {

        Location mLoc;
        if (this.canGetLocation) {
            mLoc = this.getLocation();
        } else mLoc = null;
        this.stopUsingGPS();
        return mLoc;
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                System.out.println("No trackers enabled.");
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                    );
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                    );
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void UpdatePlaces() {
        try {
            String placeUrlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "location=" + locationString +
                    "&radius=" + radiusString +
                    "&sensor=true" +
                    "&types=" + URLEncoder.encode(typeString, "UTF-8") +
                    "&key=" + apiKey;

            AsyncPlaceGetter mAsyncPlaceGetter = new AsyncPlaceGetter(placeList, location, lastJSONResponse);
            mAsyncPlaceGetter.delegate = this;
            mAsyncPlaceGetter.execute(placeUrlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProcessFinish(ArrayList<Place> p, String lastJSON) {
        placeList = p;
        lastJSONResponse = lastJSON;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        TextView lat = (TextView) findViewById(R.id.latitude);
        TextView lon = (TextView) findViewById(R.id.longitude);


        Location myLoc = this.getLocation();


        //lat.setText(Double.toString(myLoc.getLatitude()));
        //lon.setText(Double.toString(myLoc.getLongitude()));
        drawView = new DrawContentSurfaceView(this);
        setContentView(drawView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override

    protected void onDestroy() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onLocationChanged(Location location) {
        TextView lat = (TextView) findViewById(R.id.latitude);
        TextView lon = (TextView) findViewById(R.id.longitude);
        TextView tbearing = (TextView) findViewById(R.id.bearing);
        TextView places = (TextView) findViewById(R.id.places);
        location = this.getLocation();

        //lat.setText(Double.toString(location.getLatitude()));
        //lon.setText(Double.toString(location.getLongitude()));
        //tbearing.setText(Double.toString(bearing));
        locationString = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
        UpdatePlaces();

        //if(placeList.size()>0)places.setText(placeList.get(0).name);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomag = event.values;
            if (mGravity != null && mGeomag != null) {
                float R[] = new float[9];
                float I[] = new float[9];

                if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomag)) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    bearing = Math.toDegrees(orientation[0]);
                    updateCompass();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCompass() {
        Location target = placeList.get(0).loc;
        double difference = (bearing + location.bearingTo(target)) % 360.0;

        drawView.setRotation(difference);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnet, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUsingGPS();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
