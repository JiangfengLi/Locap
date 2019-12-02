package com.example.locap;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartMenu extends Fragment {
    private MapsActivity mainAct;
    private View fragV;

    // The name of the tasks file
    private String FavorFileName = "Favors_file.txt";
    private String LocsFileName = "Locs_file.txt";


    // The task data, ListView and the adapter that will be associated with it
    private ListView tasksListView = null;
    ArrayAdapter<String> taskArrayAdapter = null;
    private ArrayList<String> tasks = new ArrayList<String>();
    private Map<String, LocaObj> placeAndPopulations = new HashMap<String, LocaObj>();
    public StartMenu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragV = inflater.inflate(R.layout.fragment_start_menu, container, false);
        return fragV;
    }

    public void setContainerActivity(MapsActivity mainActivity) {
        this.mainAct = mainActivity;
    }


    /**
     * Create the data <-> adapter <-> ListView association

    private void setupTasksList() {
        // Get a reference to the ListView
        tasksListView =
                (ListView)fragV.findViewById(R.id.list_locations);

        // Create a new Array Adapter
        // Specify which layout and view to use for a row
        // and the data (array) to use
        taskArrayAdapter = new
                ArrayAdapter<String>(mainAct, R.layout.location_row,
                R.id.location_name, tasks);

        // Link the ListView and the Adapter
        tasksListView.setAdapter(taskArrayAdapter);
    }
     */

    /**
     * Save task list to non-volatile memory

    private void saveFavoriteFile() {
        File file = mainAct.getFileStreamPath(FavorFileName);
        FileOutputStream outputStream;
        try {
            outputStream = mainAct.openFileOutput(FavorFileName, mainAct.MODE_PRIVATE);
            // for (String taskString : tasks) {
            //     outputStream.write((taskString + "\n").getBytes());
            // }
            for (String locationName : placeAndPopulations.keySet()) {
                outputStream.write((locationName + "\n" + placeAndPopulations.get(locationName) +
                        "\n").getBytes());
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */

     /**
     * Save task list to non-volatile memory
     */
    private void saveLocationsFile() {
        File file = mainAct.getFileStreamPath(LocsFileName);
        FileOutputStream outputStream;
        try {
            outputStream = mainAct.openFileOutput(LocsFileName, mainAct.MODE_PRIVATE);
            for (String locationName : placeAndPopulations.keySet()) {
                outputStream.write((locationName + "\n" + placeAndPopulations.get(locationName) +
                        "\n").getBytes());
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the locations file, only if it doesn't already exist.
     */
    private void createLocsFileIfNotExists() {
        File file = mainAct.getFileStreamPath(LocsFileName);
        if (!file.exists()) {
            // String fileContents = "Wake up\nAttend 317\nAdd tasks\nDo the tasks!\n";
            String fileContents = "Reilly Craft Pizza and Drink\nrestaurant\n";
            FileOutputStream outputStream;
            try {
                outputStream = mainAct.openFileOutput(FavorFileName, mainAct.MODE_PRIVATE);
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
        Context context = mainAct.getApplicationContext();
        try {
            FileInputStream fileInputStream = context.openFileInput(FavorFileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //String taskLine = bufferedReader.readLine();
            String locaName = bufferedReader.readLine();
            while (locaName != null) {
                //tasks.add(taskLine);
                //taskLine = bufferedReader.readLine();
                String population = bufferedReader.readLine();
                //placeAndPopulations.put(locaName, );
                tasks.add(locaName + ": " + population);
                locaName = bufferedReader.readLine();
            }
        } catch (Exception e) {
            System.out.println("Oh Sheisse-Schnitzel, das error ja.");
        }
    }

    /*
    private void populateListView(){
        // set up the initial values for array of names and authors.
        articles = json.getJSONArray("articles");

        newsNames = new String[articles.length()];
        newsAuthors = new String[articles.length()];

        for(int i = 0; i < articles.length(); i++){
            newsNames[i] = articles.getJSONObject(i).getJSONObject("source").
                    getString("name");
            newsAuthors[i] = articles.getJSONObject(i).getString("author");
        }

        // Help to set up a list of name - author pairs and set it to ListView
        List<HashMap<String, String>> aList =
                new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < newsNames.length; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("new_row_name", newsNames[i]);
            hm.put("new_row_author", newsAuthors[i]);
            aList.add(hm);
        }

        String[] from = {"new_row_name", "new_row_author"};
        int[] to = {R.id.new_row_name, R.id.new_row_author};

        SimpleAdapter simpleAdapter =
                new SimpleAdapter(containerActivity, aList,
                        R.layout.new_row, from, to);
    } */

}
