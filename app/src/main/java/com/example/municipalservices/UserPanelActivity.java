package com.example.municipalservices;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class UserPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);
        getSupportActionBar().setTitle("User Panel");
    }

    public void userHome(View view) {

        startActivity(new Intent(UserPanelActivity.this, UserHomeActivity.class));
    }


    public void btnAllComplaints(View view) {

        startActivity(new Intent(UserPanelActivity.this, AllComplaintsListUserActivity.class));
    }
}
