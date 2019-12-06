package com.example.locap;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritePlacesList extends Fragment {
    private MapsActivity mainActivity;
    private View fragV;
    private static String selectedPlace;
    private List<String> Favorlist;

    public FavoritePlacesList() {
        // Required empty public constructor
    }

    public void setContainerActivity(MapsActivity context) {
        this.mainActivity = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragV = inflater.inflate(R.layout.fragment_favorite_places_list, container, false);
        mainActivity.setContentView(R.layout.fragment_favorite_places_list);
        return fragV;
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

    }

    /**
     * Update the place that user select
     * @param tv
     */
    public void selectPlace(View tv){

    }
}
