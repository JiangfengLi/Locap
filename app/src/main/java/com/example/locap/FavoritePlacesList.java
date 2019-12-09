package com.example.locap;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritePlacesList extends Fragment {
    private MapsActivity mainActivity;
    private ListView favorPlacesListView;
    private View fragV;
    private static String selectedPlace = "";
    private List<String> Favorlist;
    private ListView favorPlacesListViewListView;
    private ArrayAdapter<String> favorPlacesAdapter = null;

    public FavoritePlacesList() {
        // Required empty public constructor
    }

    public void setContainerActivity(MapsActivity context) {
        this.mainActivity = context;
    }

    public void setFavorlist(List<String> inlist){
        this.Favorlist = inlist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragV = inflater.inflate(R.layout.fragment_favorite_places_list, container, false);
        mainActivity.setContentView(R.layout.fragment_favorite_places_list);

        return fragV;
    }

    /**
     * This onPause function, it sets up the ContactsAdapter to list view. It accepts and return
     * nothing.
     */
    @Override
    public void onResume() {
        super.onResume();
        setupFavorPlaces();
    }

    private void setupFavorPlaces() {
        favorPlacesListView =
                (ListView)mainActivity.findViewById(R.id.list_favorite_places);
        favorPlacesAdapter = new
                ArrayAdapter<String>(mainActivity, R.layout.location_row,
                R.id.location_name_row, Favorlist);
        favorPlacesListView.setAdapter(favorPlacesAdapter);
        favorPlacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                view.setSelected(true);
                selectedPlace = Favorlist.get(position);
                System.out.println("selectedPlace: " + selectedPlace);
            }
        });
    }

    public void backToMenu(View button) {
        Intent mainIntent = new Intent(getActivity(), MapsActivity.class);
        startActivity(mainIntent);
    }

    /**
     *  Delete places from favorite lists
     * @param bv
     */
    public void DeletePlace(View bv) {
        Favorlist.remove(selectedPlace);
        setupFavorPlaces();
    }

    /**
     * Update the place that user select
     * @param tv
     */
    public void selectPlace(TextView tv){
        selectedPlace = tv.getText().toString();
        System.out.println("selectedPlace: " + selectedPlace);
    }
}
