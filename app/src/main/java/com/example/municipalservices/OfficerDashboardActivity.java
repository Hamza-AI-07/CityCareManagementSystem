package com.example.municipalservices;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.municipalservices.models.ComplaintModel;
import com.example.municipalservices.models.UserSignUpModel;
import com.example.municipalservices.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OfficerDashboardActivity extends AppCompatActivity {

    private static final String TAG = "OfficerDashboard";
    private TextView tvWelcome, tvRole, tvTotal, tvPending, tvInProgress, tvClosed, tvRejected, tvPositive, tvNegative;
    private Button btnViewComplaints, btnLogout;

    private String currentUserRole = "";
    private DatabaseReference complaintsRef;
    private List<ComplaintModel> assignedComplaints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Officer Dashboard");
        }

        tvWelcome = findViewById(R.id.tvOfficerWelcome);
        tvRole = findViewById(R.id.tvOfficerRole);
        tvTotal = findViewById(R.id.tvOfficerTotalCount);
        tvPending = findViewById(R.id.tvOfficerPendingCount);
        tvInProgress = findViewById(R.id.tvOfficerInProgressCount);
        tvClosed = findViewById(R.id.tvOfficerClosedCount);
        tvRejected = findViewById(R.id.tvOfficerRejectedCount);
        tvPositive = findViewById(R.id.tvOfficerPositiveCount);
        tvNegative = findViewById(R.id.tvOfficerNegativeCount);

        btnViewComplaints = findViewById(R.id.btnViewAssignedComplaints);
        btnLogout = findViewById(R.id.btnOfficerLogout);

        complaintsRef = FirebaseDatabase.getInstance().getReference(Constant.COMPLAINTS);

        fetchUserRoleAndStats();

        btnViewComplaints.setOnClickListener(v -> {
            startActivity(new Intent(OfficerDashboardActivity.this, AllComplaintsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(OfficerDashboardActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(OfficerDashboardActivity.this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void fetchUserRoleAndStats() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (email == null) return;

        FirebaseDatabase.getInstance().getReference(Constant.USER).orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            UserSignUpModel user = child.getValue(UserSignUpModel.class);
                            if (user != null) {
                                currentUserRole = user.getRole();
                                tvRole.setText("Department: " + currentUserRole);
                                tvWelcome.setText("Welcome, " + user.getName());
                                fetchComplaints();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "User Role Fetch Error: " + error.getMessage());
                    }
                });
    }

    private void fetchComplaints() {
        complaintsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assignedComplaints.clear();
                int pending = 0, inProgress = 0, closed = 0, rejected = 0;
                int positive = 0, negative = 0;

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ComplaintModel complaint = postSnapshot.getValue(ComplaintModel.class);
                    if (complaint != null && complaint.getAssignedRole() != null && 
                        complaint.getAssignedRole().equalsIgnoreCase(currentUserRole)) {
                        
                        assignedComplaints.add(complaint);
                        
                        String status = complaint.getComplainStatus();
                        if (status != null) {
                            if (status.equalsIgnoreCase(Constant.STATUS_PENDING)) pending++;
                            else if (status.equalsIgnoreCase(Constant.STATUS_IN_PROGRESS)) inProgress++;
                            else if (status.equalsIgnoreCase(Constant.STATUS_CLOSED)) closed++;
                            else if (status.equalsIgnoreCase(Constant.STATUS_REJECTED)) rejected++;
                        }

                        String feedback = complaint.getFeedback();
                        if (feedback != null) {
                            if (feedback.equalsIgnoreCase("positive")) positive++;
                            else if (feedback.equalsIgnoreCase("negative")) negative++;
                        }
                    }
                }

                updateUI(assignedComplaints.size(), pending, inProgress, closed, rejected, positive, negative);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Complaints Fetch Error: " + error.getMessage());
            }
        });
    }

    private void updateUI(int total, int pending, int inProgress, int closed, int rejected, int positive, int negative) {
        tvTotal.setText(String.valueOf(total));
        tvPending.setText(String.valueOf(pending));
        tvInProgress.setText(String.valueOf(inProgress));
        tvClosed.setText(String.valueOf(closed));
        tvRejected.setText(String.valueOf(rejected));
        tvPositive.setText(String.valueOf(positive));
        tvNegative.setText(String.valueOf(negative));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(OfficerDashboardActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out from Officer Dashboard?")
                .setPositiveButton("Logout", (dialogInterface, i) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(OfficerDashboardActivity.this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
