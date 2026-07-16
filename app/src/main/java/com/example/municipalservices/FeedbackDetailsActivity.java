package com.example.municipalservices;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.municipalservices.models.ComplaintModel;
import com.squareup.picasso.Picasso;

public class FeedbackDetailsActivity extends AppCompatActivity {
    ImageView complaintImage;
    TextView complaintTitle, complaintDescription, complaintUser, complaintID;
    public static ComplaintModel list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Feedback Details");
        }

        if (list == null) {
            Toast.makeText(this, "Error: Data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        complaintImage = findViewById(R.id.iv_complaint_details_image);
        complaintTitle = findViewById(R.id.complaint_details_title);
        complaintDescription = findViewById(R.id.complaint_details_description);
        complaintUser = findViewById(R.id.complaint_details_user);
        complaintID = findViewById(R.id.complaint_details_id);

        String mobileNumber = "Hidden";
        if ("yes".equalsIgnoreCase(list.getShowComplainNumberToEveryOne())) {
            mobileNumber = list.getComplainerPhoneNumber() != null ? list.getComplainerPhoneNumber() : "N/A";
        }

        complaintTitle.setText(list.getComplainTitle() != null ? list.getComplainTitle() : "No Title");
        complaintID.setText(String.valueOf(list.getComplaintID()));
        complaintDescription.setText(list.getComplainDescription() != null ? list.getComplainDescription() : "No Description");
        
        if (list.getComplainImageURL() != null && !list.getComplainImageURL().isEmpty()) {
            Picasso.get().load(list.getComplainImageURL()).placeholder(R.drawable.icon_upload).into(complaintImage);
        }

        String userDetails = "Name: " + (list.getComplainerName() != null ? list.getComplainerName() : "Unknown") +
                "\n\nEmail:  " + (list.getComplainerEmail() != null ? list.getComplainerEmail() : "N/A") +
                "\n\nMobile:  " + mobileNumber +
                "\n\nAddress:  " + (list.getComplainerAddress() != null ? list.getComplainerAddress() : "N/A");
        
        complaintUser.setText(userDetails);
    }

    public void btnViewMap(View view) {
        if (list == null) return;

        String latitude = list.getComplainerLatitude();
        String longitude = list.getComplainerLongitude();

        if (latitude == null || longitude == null) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude;
        Uri gmmIntentUri = Uri.parse(uri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps not found", Toast.LENGTH_SHORT).show();
        }
    }
}
