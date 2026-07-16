package com.example.municipalservices;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.municipalservices.adapters.NewComplaintAdapter;

public class NewComplaintActivity extends AppCompatActivity {


    ListView newComplaintListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Complaint Types");
        }

        newComplaintListView = findViewById(R.id.lv_new_complaint);

        final String[] names = {
                "Cleanliness",
                "Street Lights",
                "Water Connection",
                "Water Supply Scheme",
                "Illegal Construction",
                "Encroachment",
                "Repair of Street",
                "Graveyards"
        };


        int[] images = {
                R.drawable.icon_cleanliness,
                R.drawable.icon_road,
                R.drawable.icon_water_supply,
                R.drawable.icon_water,
                R.drawable.icon_illegal_construction,
                R.drawable.icon_enchraochment,
                R.drawable.icon_road,
                R.drawable.icon_graveyard
        };


        //set custom adapter to listView
        NewComplaintAdapter newComplaintAdapter = new NewComplaintAdapter(this, images,names);
        newComplaintListView.setAdapter(newComplaintAdapter);


        newComplaintListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(NewComplaintActivity.this, CreateNewComplaintActivity.class);
            intent.putExtra("category", names[i]);
            startActivity(intent);
        });



    }
}
