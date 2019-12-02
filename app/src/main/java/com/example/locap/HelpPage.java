package com.example.locap;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpPage extends Fragment {
    private MapsActivity mainActivity;
    private View fragV;
    private static int index;

    public HelpPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragV = inflater.inflate(R.layout.fragment_help_page, container, false);
        System.out.println("Inside transition");
        mainActivity.setContentView(R.layout.fragment_help_page);
        index = 1;
        return fragV;
    }

    public void setContainerActivity(MapsActivity context) {
        this.mainActivity = context;
    }

    public void backToMenu(View button) {
        Intent mainIntent = new Intent(getActivity(), MapsActivity.class);
        startActivity(mainIntent);
    }

    public void updateBlock(View bv){
        ImageView img= (ImageView) fragV.findViewById(R.id.help_image);

        //ImageView imageView = new ImageView(this);
        index++;
        System.out.println("Index: " + index);
        Bitmap bImage = null;
        if(index == 1) {
            bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.instruction_1);
           // img.setImageResource(R.drawable.instruction_1);
        }else if(index == 2) {
            bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.instruction_2);
            img.setImageResource(R.drawable.instruction_2);
        }else if(index == 3) {
           // img.setImageResource(R.drawable.instruction_3);
            bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.instruction_3);
            index = 1;
        }
        img.setImageBitmap(bImage);

    }
}
