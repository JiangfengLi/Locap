package com.example.locap;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static MapsActivity itself = null;
    private StartMenu StartMenuFragment;
    private HelpPage HelpPageFragment;
    private LatLng userCurrentLoc;
    private GoogleMap mMap;
    private static final int LAT_INDICATOR = 0123;
    private static final int LNG_INDICATOR = 0456;

    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        itself = this;

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in University of Arizona and move the camera
        userCurrentLoc = new LatLng(32.230,-110.951);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLoc, 15.5F));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userCurrentLoc));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

       // setupFragments();
    }

    /**
     * This showOriginalPost function, it sets up the Fragment which include a title, EditText,
     * search button, and a ListView to use to display the search results and allow users to search.
     * It accepts and return nothing.
     */
    private void setupFragments() {
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        StartMenuFragment = new StartMenu();
        HelpPageFragment = new HelpPage();

        StartMenuFragment.setContainerActivity(this);
        HelpPageFragment.setContainerActivity(this);
        transaction.replace(R.id.fragment_container,
                StartMenuFragment);

        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void updateLocation(View v) {
        mMap.clear();
        CameraPosition currentCameraPosition = mMap.getCameraPosition();
        new DownloadTask().execute(currentCameraPosition.target.latitude + "," + currentCameraPosition.target.longitude);
    }


    private class DownloadTask extends AsyncTask<String, Void, JSONObject> {

        private String baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?&radius=1000&key=AIzaSyAnYJcGL7MiFEQl2RS6DhFXOSN0c4orRAA";
        private String locComponent = "&location=";
        private String keyComponent = "&keyword=";
        String inputKeyword;

        @Override
        protected void onPreExecute() {
            inputKeyword = ((EditText)findViewById(R.id.search_key)).getText().toString();
        }

        @Override
        protected JSONObject doInBackground(String[] s) {
            try {
                String textJson = "";
                String line;
                String textUrl = baseUrl + locComponent + s[0] + keyComponent + inputKeyword;
                System.out.println("textUrl: " + textUrl);
                URL url = new URL(textUrl);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                while ((line = in.readLine()) != null) {
                    textJson += line;
                }
                in.close();
                JSONObject json = new JSONObject(textJson);
                return json;
            } catch (Exception e) { e.printStackTrace(); }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                // System.out.println("onPostExecute work");
                JSONArray results = json.getJSONArray("results");
                System.out.println("results: \n" + results.toString());
                for (int i = 0 ; i < results.length(); i++) {
                    /** TODO
                     * Assume that the json variable will be a JSONObject populates with results from a places search.
                     * You should put a Marker on the map for each resulting location, with a corresponding label.
                     */
                    JSONObject loc = results.getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location");
                    double lat = loc.getDouble("lat");
                    double lng = loc.getDouble("lng");
                    String label = results.getJSONObject(i).getString("name");
                    LatLng location = new LatLng(lat,lng);
                    Marker m = mMap.addMarker(new MarkerOptions().position(location).title(label));
                    m.setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
