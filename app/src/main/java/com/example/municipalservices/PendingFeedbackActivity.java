package com.example.municipalservices;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.municipalservices.adapters.SortedComplaintsAdapter;
import com.example.municipalservices.models.ComplaintModel;
import com.example.municipalservices.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PendingFeedbackActivity extends AppCompatActivity implements SortedComplaintsAdapter.complaintlist {

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mStatuses = new ArrayList<>();
    private ArrayList<Integer> mIDs = new ArrayList<>();
    public static ArrayList<ComplaintModel> list = new ArrayList<>();
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_feedback);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.pending_feedback);
        }

        list = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        final DatabaseReference complaints = database.getReference(Constant.COMPLAINTS);
        
        String currentUserEmail = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        final String finalEmail = currentUserEmail;

        complaints.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                ComplaintModel di = dataSnapshot.getValue(ComplaintModel.class);
                if (di != null) {
                    // Filter by current user email
                    if (finalEmail != null && !finalEmail.isEmpty()) {
                        if (di.getComplainerEmail() == null || !di.getComplainerEmail().equalsIgnoreCase(finalEmail)) {
                            return;
                        }
                    }

                    if (di.getLikes() <= 0 && di.getDislikes() <= 0) {
                        list.add(di);
                        mNames.add(di.getComplainTitle());
                        mImageUrls.add(di.getComplainImageURL());
                        mStatuses.add(di.getComplainStatus());
                        mIDs.add(di.getComplaintID());
                        initRecyclerView();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.sd_recycler_view);
        SortedComplaintsAdapter adapter = new SortedComplaintsAdapter(mIDs, mNames, mImageUrls, mStatuses, this);
        adapter.getdata(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void getclicks(int positions, String s) {
        if (list != null && positions < list.size()) {
            FeedbackDetailsActivity.list = list.get(positions);
            startActivity(new Intent(getApplicationContext(), FeedbackDetailsActivity.class));
        }
    }
}
