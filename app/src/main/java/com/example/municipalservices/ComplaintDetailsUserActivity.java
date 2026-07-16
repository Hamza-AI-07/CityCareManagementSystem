package com.example.municipalservices;

import android.content.res.ColorStateList;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.municipalservices.models.ComplaintModel;
import com.example.municipalservices.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ComplaintDetailsUserActivity extends AppCompatActivity {

    ImageView complaintImage;
    TextView complaintTitle, complaintDescription, complaintID;
    TextView tvStatus, tvAssignedRole, tvRemarks;
    View layoutPendingActions;
    String pushID = "";
    public static ComplaintModel list;
    String mobileNumber;
    boolean isLiked=false, isDisliked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_details_user);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Complaint Details");
        }

        complaintImage = findViewById(R.id.iv_complaint_details_image);
        complaintTitle = findViewById(R.id.complaint_details_title);
        complaintDescription = findViewById(R.id.complaint_details_description);
        // complaintUser = findViewById(R.id.complaint_details_user); // Removed from XML
        complaintID = findViewById(R.id.complaint_details_id);
        tvStatus = findViewById(R.id.tv_user_status);
        tvAssignedRole = findViewById(R.id.tv_user_assigned_role);
        tvRemarks = findViewById(R.id.tv_user_remarks);
        layoutPendingActions = findViewById(R.id.layout_pending_actions);
        
        // Ensure static variable 'list' is not null
        if (list == null) {
            Toast.makeText(this, "Error: Complaint data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if(list.getShowComplainNumberToEveryOne() != null && list.getShowComplainNumberToEveryOne().equalsIgnoreCase("yes"))
            mobileNumber = String.valueOf(list.getComplainerPhoneNumber());
        else
            mobileNumber = "Hidden";

        complaintTitle.setText(""+list.getComplainTitle());
        complaintDescription.setText(""+list.getComplainDescription());
        complaintID.setText("ID: #"+list.getComplaintID());
        tvStatus.setText(list.getComplainStatus());
        updateStatusBadgeColor(list.getComplainStatus());
        tvAssignedRole.setText(list.getAssignedRole());
        tvRemarks.setText(list.getRemarks());

        if (list.getComplainStatus() != null && list.getComplainStatus().equalsIgnoreCase(Constant.STATUS_PENDING)) {
            layoutPendingActions.setVisibility(View.VISIBLE);
        } else {
            layoutPendingActions.setVisibility(View.GONE);
        }
        
        if (list.getComplainImageURL() != null && !list.getComplainImageURL().isEmpty()) {
            Picasso.get().load(list.getComplainImageURL()).placeholder(R.drawable.icon_upload).into(complaintImage);
        } else {
            complaintImage.setImageResource(R.drawable.icon_upload);
        }
        
        // complaintUser.setText(...) // Removed from XML
    }

    private void updateStatusBadgeColor(String status) {
        if (status == null) return;
        int color;

        if (status.equalsIgnoreCase(Constant.STATUS_PENDING)) {
            color = ContextCompat.getColor(this, R.color.colorDarkYellow);
        } else if (status.equalsIgnoreCase(Constant.STATUS_OPEN)) {
            color = ContextCompat.getColor(this, R.color.colorLightBlue);
        } else if (status.equalsIgnoreCase(Constant.STATUS_IN_PROGRESS)) {
            color = ContextCompat.getColor(this, R.color.colorPrimary);
        } else if (status.equalsIgnoreCase(Constant.STATUS_CLOSED)) {
            color = ContextCompat.getColor(this, R.color.colorGreen);
        } else if (status.equalsIgnoreCase(Constant.STATUS_REJECTED)) {
            color = ContextCompat.getColor(this, R.color.colorRed);
        } else {
            color = ContextCompat.getColor(this, R.color.colorPrimary);
        }
        tvStatus.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void editComplaintClick(View view) {
        Intent intent = new Intent(this, CreateNewComplaintActivity.class);
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }

    public void btnViewMap(View view) {
        String latitude = String.valueOf(list.getComplainerLatitude());
        String longitude = String.valueOf(list.getComplainerLongitude());

        String uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Point" + ")";
        Uri gmmIntentUri = Uri.parse(uri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    public void likeFunction(View view) {
        if(isLiked) {
            Toast.makeText(this, "You already gave feedback", Toast.LENGTH_SHORT).show();
        } else {
            isLiked=true;
            isDisliked=false;
            updateFeedback("positive");
            likeComplaint();
        }
    }

    public void dislikeFunction(View view) {
        if(isDisliked){
            Toast.makeText(this, "You already gave feedback", Toast.LENGTH_SHORT).show();
        } else {
            isDisliked=true;
            isLiked=false;
            updateFeedback("negative");
            disLikeComplaint();
        }
    }

    private void updateFeedback(String type) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(Constant.COMPLAINTS);

        reference.orderByChild("complaintID").equalTo(list.getComplaintID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            child.getRef().child("feedback").setValue(type);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    private void likeComplaint () {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(Constant.COMPLAINTS);

        reference.orderByChild("complaintID").equalTo(list.getComplaintID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            pushID = child.getKey();
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            rootRef.child(Constant.COMPLAINTS)
                                    .child(pushID)
                                    .child("likes")
                                    .setValue(list.getLikes()+1);
                            Toast.makeText(ComplaintDetailsUserActivity.this, "Like Counted", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    private void disLikeComplaint () {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(Constant.COMPLAINTS);

        reference.orderByChild("complaintID").equalTo(list.getComplaintID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            pushID = child.getKey();
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            rootRef.child(Constant.COMPLAINTS)
                                    .child(pushID)
                                    .child("dislikes")
                                    .setValue(list.getDislikes()+1);
                            Toast.makeText(ComplaintDetailsUserActivity.this, "Dislike  Counted", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    public void withdrawComplaintClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Withdraw Complaint")
                .setMessage("Are you sure you want to withdraw this complaint? This action cannot be undone.")
                .setPositiveButton("Withdraw", (dialog, which) -> deleteComplaintFromFirebase())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteComplaintFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(Constant.COMPLAINTS);

        reference.orderByChild("complaintID").equalTo(list.getComplaintID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            child.getRef().removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ComplaintDetailsUserActivity.this, "Complaint withdrawn successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ComplaintDetailsUserActivity.this, "Failed to withdraw complaint", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ComplaintDetailsUserActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
