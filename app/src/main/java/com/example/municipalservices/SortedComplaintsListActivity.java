package com.example.municipalservices;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.municipalservices.adapters.SortedComplaintsAdapter;
import com.example.municipalservices.models.ComplaintModel;
import com.example.municipalservices.models.UserSignUpModel;
import com.example.municipalservices.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SortedComplaintsListActivity extends AppCompatActivity implements SortedComplaintsAdapter.complaintlist {

    String complainType;

    private final ArrayList<String> mNames = new ArrayList<>();
    private final ArrayList<String> mImageUrls = new ArrayList<>();
    private final ArrayList<Integer> mIDs = new ArrayList<>();
    private final ArrayList<String> mStatuses = new ArrayList<>();
    public static ArrayList<ComplaintModel> list;
    FirebaseDatabase database;
    private String currentUserRole = "User";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorted_complaints_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("category"));
        }

        complainType = getIntent().getStringExtra("category");
        list = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        fetchUserRoleAndComplaints();
    }

    private void fetchUserRoleAndComplaints() {
        String email = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                       FirebaseAuth.getInstance().getCurrentUser().getEmail() : "";

        if (email == null || email.isEmpty()) {
            loadComplaints();
            return;
        }

        if (email.equalsIgnoreCase(Constant.ADMIN_EMAIL)) {
            currentUserRole = "Admin";
            loadComplaints();
        } else {
            database.getReference(Constant.USER).orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                UserSignUpModel user = child.getValue(UserSignUpModel.class);
                                if (user != null) {
                                    currentUserRole = user.getRole();
                                }
                            }
                            loadComplaints();
                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                            loadComplaints();
                        }
                    });
        }
    }

    private void loadComplaints() {
        final DatabaseReference complaints = database.getReference("Complaints");
        
        String currentUserEmail = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        
        final boolean isAdmin = currentUserRole.equalsIgnoreCase("Admin");
        final boolean isUser = currentUserRole.equalsIgnoreCase("User");
        final String finalEmail = currentUserEmail;

        complaints.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@androidx.annotation.NonNull DataSnapshot dataSnapshot, String s) {

                ComplaintModel di = dataSnapshot.getValue(ComplaintModel.class);
                if (di == null) return;

                if (complainType.equalsIgnoreCase(di.getComplainType())) {
                    // Filtering logic
                    if (isAdmin) {
                        // Admin sees everything in this category
                    } else if (isUser) {
                        // Users see only their own complaints
                        if (di.getComplainerEmail() == null || !di.getComplainerEmail().equalsIgnoreCase(finalEmail)) {
                            return;
                        }
                    } else {
                        // Officers see only complaints assigned to their role
                        if (di.getAssignedRole() == null || !di.getAssignedRole().equalsIgnoreCase(currentUserRole)) {
                            return;
                        }
                    }

                    list.add(di);
                    mNames.add(di.getComplainTitle());
                    mImageUrls.add(di.getComplainImageURL());
                    mStatuses.add(di.getComplainStatus());
                    mIDs.add(di.getComplaintID());
                    initRecylerView();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void initRecylerView(){
        RecyclerView recyclerView = findViewById(R.id.sd_recycler_view);
        SortedComplaintsAdapter adapter = new SortedComplaintsAdapter(mIDs, mNames ,mImageUrls ,mStatuses, this);
        adapter.getdata(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void getclicks(int positions, String s) {
        if (currentUserRole != null && !currentUserRole.equalsIgnoreCase("User")) {
            ComplaintDetailsAdminActivity.complaint = list.get(positions);
            startActivity(new Intent(getApplicationContext(), ComplaintDetailsAdminActivity.class));
        } else {
            ComplaintDetailsUserActivity.list = list.get(positions);
            startActivity(new Intent(getApplicationContext(), ComplaintDetailsUserActivity.class));
        }
    }
}
