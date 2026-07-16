package com.example.municipalservices;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.municipalservices.R;
import com.example.municipalservices.models.ComplaintModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserHomeActivity extends AppCompatActivity {

    int total = 0;
    int open = 0;
    int closed = 0;
    int pendingFeedback = 0;
    int positiveFeedback = 0;
    int negativeFeedBack = 0;
    int droppedFeedBack = 0;

    RelativeLayout rlTotalComplaints, rlOpenComplaints, rlClosedComplaints;

    TextView totalComplaintsInNumber, openComplaintsInNumber, closedComplaintsInNumber, positiveFeedbackInNumber, negativeFeedBackInNumber, pendingFeedbackInNumber, droppedFeedbackInNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        rlTotalComplaints = findViewById(R.id.rl_total_complaints);
        rlOpenComplaints = findViewById(R.id.rl_open_complaints);
        rlClosedComplaints = findViewById(R.id.rl_closed_complaints);
        totalComplaintsInNumber = findViewById(R.id.tv_total_complaints_in_number);
        openComplaintsInNumber = findViewById(R.id.tv_open_complaints_in_number);
        closedComplaintsInNumber = findViewById(R.id.tv_closed_complaints_in_number);
        positiveFeedbackInNumber = findViewById(R.id.tv_positive_feedback_in_number);
        negativeFeedBackInNumber = findViewById(R.id.tv_negative_feedback_in_number);
        pendingFeedbackInNumber = findViewById(R.id.tv_pending_feedback_in_number);
        droppedFeedbackInNumber = findViewById(R.id.tv_dropped_feedback_in_number);


        setNumbersToViews();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomeActivity.this, NewComplaintActivity.class));
            }
        });


        rlTotalComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomeActivity.this, AllComplaintsListUserActivity.class));
            }
        });


        rlOpenComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomeActivity.this, OpenComplaintsActivity.class));
            }
        });

        rlClosedComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomeActivity.this, ClosedComplaintsActivity.class));
            }
        });


    }


    private void setNumbersToViews() {


        FirebaseDatabase database;
        database = FirebaseDatabase.getInstance();
        final DatabaseReference complaints = database.getReference("Complaints");
        
        String currentUserEmail = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        final String finalEmail = currentUserEmail;

        complaints.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ComplaintModel di = dataSnapshot.getValue(ComplaintModel.class);
                if (di == null) return;
                
                // Filter by current user email
                if (finalEmail != null && !finalEmail.isEmpty()) {
                    if (di.getComplainerEmail() == null || !di.getComplainerEmail().equalsIgnoreCase(finalEmail)) {
                        return;
                    }
                }

                String status = di.getComplainStatus();
                if (status != null) {
                    if (status.equalsIgnoreCase("open"))
                        open++;

                    if (status.equalsIgnoreCase("closed") || status.equalsIgnoreCase("close"))
                        closed++;
                }

                if (di.getLikes() > di.getDislikes() && di.getDislikes() < (di.getLikes() * 2))
                    positiveFeedback++;

                if (di.getDislikes() > di.getLikes())
                    negativeFeedBack++;

                if (di.getLikes() <= 0 && di.getDislikes() <= 0)
                    pendingFeedback++;

                if (di.getDislikes() >= (di.getLikes() * 2) && di.getLikes() != 0)
                    droppedFeedBack++;

                total++;

                if (totalComplaintsInNumber != null) totalComplaintsInNumber.setText(String.valueOf(total));
                if (openComplaintsInNumber != null) openComplaintsInNumber.setText(String.valueOf(open));
                if (closedComplaintsInNumber != null) closedComplaintsInNumber.setText(String.valueOf(closed));
                if (positiveFeedbackInNumber != null) positiveFeedbackInNumber.setText(String.valueOf(positiveFeedback));
                if (negativeFeedBackInNumber != null) negativeFeedBackInNumber.setText(String.valueOf(negativeFeedBack));
                if (pendingFeedbackInNumber != null) pendingFeedbackInNumber.setText(String.valueOf(pendingFeedback));
                if (droppedFeedbackInNumber != null) droppedFeedbackInNumber.setText(String.valueOf(droppedFeedBack));

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

    public void positiveFeedBack(View view) {
        startActivity(new Intent(UserHomeActivity.this, PositiveFeedbackActivity.class));
    }


    public void negativeFeedback(View view) {
        startActivity(new Intent(UserHomeActivity.this, NegativeFeedbackActivity.class));
    }

    public void pendingFeedBack(View view) {
        startActivity(new Intent(UserHomeActivity.this, PendingFeedbackActivity.class));
    }

    public void droppedFeedBack(View view) {
        startActivity(new Intent(UserHomeActivity.this, DroppedFeedbackActivity.class));
    }
}
