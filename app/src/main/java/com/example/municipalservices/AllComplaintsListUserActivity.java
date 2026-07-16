package com.example.municipalservices;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.municipalservices.adapters.NewComplaintAdapter;

public class AllComplaintsListUserActivity extends AppCompatActivity {

    ListView newComplaintListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_complaints_list_user);


        getSupportActionBar().setTitle("Categories");

        newComplaintListView = findViewById(R.id.lv_all_complaints);

        final String names[] = {
                "Cleanliness",
                "Street Lights",
                "Water Connection",
                "Water Supply Scheme",
                "Illegal Construction",
                "Encroachment",
                "Repair of Street",
                "Graveyards"
        };


        int images[] = {
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


        //Click listener to listView
        newComplaintListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AllComplaintsListUserActivity.this, SortedComplaintsListActivity.class);
                intent.putExtra("category",""+names[i]);
                startActivity(intent);
            }
        });



    }
}
