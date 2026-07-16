package com.example.municipalservices;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        getSupportActionBar().setTitle("Admin Panel");


    }

    public void btnAllComplaints(View view) {
        startActivity(new Intent(AdminPanelActivity.this, AllComplaintsActivity.class));
    }

    public void adminHome(View view) {

        startActivity(new Intent(AdminPanelActivity.this, AdminHomeActivity.class));
    }

    public void openDashboard(View view) {
        startActivity(new Intent(AdminPanelActivity.this, AdminDashboardActivity.class));
    }

    public void openManageUsers(View view) {
        startActivity(new Intent(AdminPanelActivity.this, ManageUsersActivity.class));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(AdminPanelActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out from Admin Panel?")
                .setPositiveButton("Logout", (dialogInterface, i) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(AdminPanelActivity.this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
