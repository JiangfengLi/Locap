package com.example.locap;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static MapsActivity itself = null;
    private HelpPage HelpPageFragment;
    private FavoritePlacesList FPListFragment;
    private LatLng userCurrentLoc;
    private GoogleMap mMap;

    private static final int LAT_INDICATOR = 0123;
    private static final int LNG_INDICATOR = 0456;
    private static String currentFrag = "";
    private List<String> Favorlist;
    private static int index;
    private static boolean isLike = false;
    private String locationName = "";
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private String FavorLocsFileName = "FavorLocs_file.txt";
    private String shareAdress = "";
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        HelpPageFragment = new HelpPage();
        mapFragment.getMapAsync(this);
        itself = this;
        Favorlist = new ArrayList<String>();
        createLocsFileIfNotExists();
        populateFavors();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        populateFavors();
        System.out.println("onRestart Favorlist: \n" + Favorlist);
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

    @Override
    protected void onPause() {
        super.onPause();
        saveFavorLocationsFile();
        System.out.println("onPause Favorlist: \n" + Favorlist);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveFavorLocationsFile();
        System.out.println("onDestroy Favorlist: \n" + Favorlist);
    }

    /**
     * This setupHelpFragments function, it sets up the Fragment which include a title, EditText,
     * search button, and a ListView to use to display the search results and allow users to search.
     * It accepts and return nothing.
     */
    public void setupHelpFragments(View buttonView) {

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        HelpPageFragment.setContainerActivity(this);
        //System.out.println("Before transition");
        transaction.replace(R.id.fragment_container, HelpPageFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        currentFrag = "HelpFragments";
        //System.out.println("After transition");

    }

    /**
     * Set up FavoritePlacesList fragment
     * @param iv
     */
    public void listFavor(View iv){
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        FPListFragment = new FavoritePlacesList();
        FPListFragment.setContainerActivity(this);
        FPListFragment.setFavorlist(Favorlist);
        //System.out.println("Before transition");
        transaction.replace(R.id.fragment_container, FPListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        currentFrag = "FavoritePlacesList";
    }

    public void updateLocation(View v) {
        mMap.clear();
        CameraPosition currentCameraPosition = mMap.getCameraPosition();
        new DownloadTask().execute(currentCameraPosition.target.latitude + "," + currentCameraPosition.target.longitude);
    }

    /**
     * take the user back to main menu if the user click 'Back To Menu' button
     * @param button
     */
    public void backToMenu(View button){
        if(currentFrag.equals("HelpFragments"))
            HelpPageFragment.backToMenu(button);
        else if(currentFrag.equals("FavoritePlacesList"))
            FPListFragment.backToMenu(button);
    }

    public void selectPlace(View tv){
        FPListFragment.selectPlace((TextView) tv);
    }

    public void DeletePlace(View bv) {
        FPListFragment.DeletePlace(bv);
    }

    /**
     * Update the block of instructions to next or precious one according to the button that user
     * click
     * @param bv
     */
    public void updateBlock(View bv){
        float setPos = 0;
        if(bv.getId()==R.id.next_help_button) {
            setPos = 300f;
            if(index == 7)
                index = 7;
            else
                index++;
        } else if (bv.getId()==R.id.prev_help_button){
            setPos = -300f;
            if(index == 1)
                index = 1;
            else
              index--;
        }

        //HelpPageFragment.setNextBlock(bv);
        ImageView img= (ImageView)findViewById(R.id.help_image);

        //ImageView imageView = new ImageView(this);
        System.out.println("Index: " + index);
        Bitmap bImage = null;
        if(index == 1) {
            bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.instruction_1);
        }else if(index == 2) {
            bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.instruction_2);
        }else if(index == 3) {
            // img.setImageResource(R.drawable.instruction_3);
            bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.instruction_3);
        } else if(index == 4) {
            // img.setImageResource(R.drawable.instruction_3);
            bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.instruction_4);
        }else if(index == 5) {
            // img.setImageResource(R.drawable.instruction_3);
            bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.instruction_5);
        }else if(index == 6) {
            // img.setImageResource(R.drawable.instruction_3);
            bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.instruction_6);
        }else if(index == 7) {
            // img.setImageResource(R.drawable.instruction_3);
            bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.instruction_7);
        }

        //set up animation
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(img, "x", 0f);
        animatorX.setDuration(1000);

        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(img, View.ALPHA,1.0f,0.0f);
        ObjectAnimator alphaAnimation2 = ObjectAnimator.ofFloat(img, View.ALPHA,0.0f,1.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation2.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alphaAnimation, animatorX);
        img.setImageBitmap(bImage);
        img.setX(setPos);
        animatorSet.playTogether(alphaAnimation2);
        animatorSet.start();

        //img.setAlpha(1.0f);
    }

    public void updateFavor(View button){
        Button myFavor= (Button)findViewById(R.id.myFavor_button);

        if(locationName != null){
            if(!isLike) {
                if(!Favorlist.contains(locationName))
                    Favorlist.add(locationName);
                myFavor.setBackgroundResource(R.drawable.favorite_true);
            } else {
                Favorlist.remove(locationName);
                myFavor.setBackgroundResource(R.drawable.favorite_false);
            }
            isLike = !isLike;
            System.out.println("Favor List: \n" + Favorlist);
        }

    }

    public void shareContacts(View bv){
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.setContainerActivity(this);
        contactsFragment.setShareLocation(shareAdress);
        //System.out.println("Before transition");
        transaction.replace(R.id.fragment_container, contactsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Create the locations file, only if it doesn't already exist.
     */
    private void createLocsFileIfNotExists() {
        File file = getFileStreamPath(FavorLocsFileName);
        if (!file.exists()) {
            // String fileContents = "Wake up\nAttend 317\nAdd tasks\nDo the tasks!\n";
            String fileContents = "Reilly Craft Pizza and Drink\nSky Bar Tucson";
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(FavorLocsFileName,MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Read the tasks from the task file.
     */
    private void populateFavors() {
        Context context = getApplicationContext();
        try {
            FileInputStream fileInputStream = context.openFileInput(FavorLocsFileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String locaName = bufferedReader.readLine();
            while (locaName != null) {
                if(!Favorlist.contains(locaName))
                    Favorlist.add(locaName);
                locaName = bufferedReader.readLine();
            }
            System.out.println("populateFavors: \n" + Favorlist);
        } catch (Exception e) {
            System.out.println("Oh Sheisse-Schnitzel, das error ja.");
        }
    }

    /**
     * Save task list to non-volatile memory
     */
    private void saveFavorLocationsFile() {
        File file = getFileStreamPath(FavorLocsFileName);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(FavorLocsFileName, MODE_PRIVATE);
            for (String FavorlocationName : Favorlist) {
                outputStream.write((FavorlocationName + "\n").getBytes());
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    if(i == 0){
                        locationName = results.getJSONObject(i).getString("name");
                        shareAdress = results.getJSONObject(i).getString("vicinity");
                        isLike = Favorlist.contains(locationName);
                        Button myFavor= (Button)findViewById(R.id.myFavor_button);
                        if(isLike)
                            myFavor.setBackgroundResource(R.drawable.favorite_true);
                        else
                            myFavor.setBackgroundResource(R.drawable.favorite_false);
                    }
                    JSONObject loc = results.getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location");
                    double lat = loc.getDouble("lat");
                    double lng = loc.getDouble("lng");
                    String label = results.getJSONObject(i).getString("name");
                    LatLng location = new LatLng(lat,lng);
                    Marker m = mMap.addMarker(new MarkerOptions().position(location).title(label));
                    m.setVisible(true);
                }

                int dynamicPopulation = new Random().nextInt(100) + 10;
                TextView popTV = (TextView)findViewById(R.id.populationView);
                popTV.setText("Current Population: " + dynamicPopulation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
     }

}
