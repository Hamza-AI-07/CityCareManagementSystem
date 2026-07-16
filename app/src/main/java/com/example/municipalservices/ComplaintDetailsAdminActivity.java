package com.example.municipalservices;

import android.content.res.ColorStateList;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.municipalservices.models.ComplaintModel;
import com.example.municipalservices.models.UserSignUpModel;
import com.example.municipalservices.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ComplaintDetailsAdminActivity extends AppCompatActivity {
    private static final String TAG = "ComplaintDetailsAdmin";
    ImageView complaintImage;
    TextView complaintTitle, complaintDescription, complaintUser, complaintID, complaintStatus;
    TextView tvAssignedRole, tvRemarks;
    EditText etRemarks;
    Spinner spinnerForward;
    View layoutAdminActions;
    String pushID = "";
    public static ComplaintModel complaint;
    private final ArrayList<String> rolesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_details_admin_acticity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.complaint_details_title_str);
        }

        complaintImage = findViewById(R.id.iv_complaint_details_image);
        complaintTitle = findViewById(R.id.complaint_details_title);
        complaintDescription = findViewById(R.id.complaint_details_description);
        complaintUser = findViewById(R.id.complaint_details_user);
        complaintID = findViewById(R.id.complaint_details_id);
        complaintStatus = findViewById(R.id.complaint_details_status);
        tvAssignedRole = findViewById(R.id.tv_assigned_role);
        tvRemarks = findViewById(R.id.tv_remarks);
        etRemarks = findViewById(R.id.et_admin_remarks);
        spinnerForward = findViewById(R.id.spinnerForwardRole);
        layoutAdminActions = findViewById(R.id.layout_admin_actions);

        if (complaint != null) {
            complaintTitle.setText(complaint.getComplainTitle());
            complaintDescription.setText(complaint.getComplainDescription());
            complaintID.setText(getString(R.string.complaint_id_format, complaint.getComplaintID()));
            complaintStatus.setText(complaint.getComplainStatus());
            updateStatusBadgeColor(complaint.getComplainStatus());
            tvAssignedRole.setText(complaint.getAssignedRole());
            tvRemarks.setText(complaint.getRemarks());

            if (complaint.getComplainImageURL() != null && !complaint.getComplainImageURL().isEmpty()) {
                Picasso.get().load(complaint.getComplainImageURL()).placeholder(R.drawable.icon_upload).into(complaintImage);
            } else {
                complaintImage.setImageResource(R.drawable.icon_upload);
            }
            complaintUser.setText(getString(R.string.complainer_details_format,
                    complaint.getComplainerName(),
                    complainerEmailSafe(),
                    complaint.getComplainerPhoneNumber(),
                    complaint.getComplainerAddress()));
        }

        checkUserRoleAndSetupUI();
    }

    private void checkUserRoleAndSetupUI() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail != null && userEmail.equalsIgnoreCase(Constant.ADMIN_EMAIL)) {
            layoutAdminActions.setVisibility(View.VISIBLE);
            fetchRolesForForwarding();
        } else {
            layoutAdminActions.setVisibility(View.GONE);
        }
    }

    private void fetchRolesForForwarding() {
        FirebaseDatabase.getInstance().getReference(Constant.USER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rolesList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            UserSignUpModel user = child.getValue(UserSignUpModel.class);
                            if (user != null && user.getRole() != null) {
                                String role = user.getRole();
                                if (!role.equalsIgnoreCase("User") && !role.equalsIgnoreCase("Admin") && !rolesList.contains(role)) {
                                    rolesList.add(role);
                                }
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ComplaintDetailsAdminActivity.this, android.R.layout.simple_spinner_item, rolesList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerForward.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    public void forwardComplaint(View view) {
        if (spinnerForward.getSelectedItem() == null) {
            Toast.makeText(this, "No departments available to forward", Toast.LENGTH_SHORT).show();
            return;
        }
        String role = spinnerForward.getSelectedItem().toString();
        updateComplaintField("assignedRole", role);
        Toast.makeText(this, "Forwarded to " + role, Toast.LENGTH_SHORT).show();
        tvAssignedRole.setText(role);
    }

    public void updateRemarks(View view) {
        String remarks = etRemarks.getText().toString().trim();
        if (remarks.isEmpty()) {
            Toast.makeText(this, "Please enter remarks", Toast.LENGTH_SHORT).show();
            return;
        }
        updateComplaintField("remarks", remarks);
        Toast.makeText(this, "Remarks updated", Toast.LENGTH_SHORT).show();
        tvRemarks.setText(remarks);
        etRemarks.setText("");
    }

    public void setInProgress(View view) {
        changeComplaintStatus(Constant.STATUS_IN_PROGRESS);
    }

    private void updateComplaintField(String field, Object value) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(Constant.COMPLAINTS);

        reference.orderByChild("complaintID").equalTo(complaint.getComplaintID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            child.getRef().child(field).setValue(value);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    private String complainerEmailSafe() {
        return complaint.getComplainerEmail() != null ? complaint.getComplainerEmail() : "";
    }

    public void openMaps(View view) {
        if (complaint != null) {
            String latitude = complaint.getComplainerLatitude();
            String longitude = complaint.getComplainerLongitude();
            String uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude;
            Uri gmmIntentUri = Uri.parse(uri);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }

    public void approveComplaint(View view) {
        changeComplaintStatus(Constant.STATUS_OPEN);
    }

    public void deleteComplaint(View view) {
        deleteComplaintInternal();
    }

    public void closeComplaint(View view) {
        changeComplaintStatus(Constant.STATUS_CLOSED);
    }

    public void rejectComplaint(View view) {
        changeComplaintStatus(Constant.STATUS_REJECTED);
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
        complaintStatus.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void changeComplaintStatus(final String status) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(Constant.COMPLAINTS);

        reference.orderByChild("complaintID").equalTo(complaint.getComplaintID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            pushID = child.getKey();
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            if (pushID != null) {
                                rootRef.child(Constant.COMPLAINTS)
                                        .child(pushID)
                                        .child("complainStatus")
                                        .setValue(status)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ComplaintDetailsAdminActivity.this, getString(R.string.status_changed_to, status), Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(e -> {
                                            Log.e(TAG, "Status Change Error", e);
                                            Toast.makeText(ComplaintDetailsAdminActivity.this, R.string.failed_to_update_status, Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Database Error", databaseError.toException());
                    }
                });
    }

    private void deleteComplaintInternal() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(Constant.COMPLAINTS);

        reference.orderByChild("complaintID").equalTo(complaint.getComplaintID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            pushID = child.getKey();
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            if (pushID != null) {
                                rootRef.child(Constant.COMPLAINTS)
                                        .child(pushID)
                                        .removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ComplaintDetailsAdminActivity.this, R.string.complaint_removed, Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(e -> {
                                            Log.e(TAG, "Delete Complaint Error", e);
                                            Toast.makeText(ComplaintDetailsAdminActivity.this, R.string.failed_to_delete_complaint, Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Database Error", databaseError.toException());
                    }
                });
    }
}
