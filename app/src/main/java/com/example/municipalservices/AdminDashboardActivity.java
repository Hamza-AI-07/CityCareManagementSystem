package com.example.municipalservices;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.municipalservices.models.ComplaintModel;
import com.example.municipalservices.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity {

    private static final String TAG = "AdminDashboard";
    private Button btnStartDate, btnEndDate, btnApplyFilter, btnClearFilter;
    private TextView tvTotalCount, tvCleanlinessCount, tvStreetLightsCount, tvWaterCount, tvIllegalCount, tvEncroachmentCount, tvRepairCount, tvGraveyardsCount;
    private TextView tvPendingStatusCount, tvOpenStatusCount, tvClosedStatusCount;
    private TextView tvPositiveFeedbackCount, tvNegativeFeedbackCount;
    private CardView cvCleanliness, cvStreetLights, cvWater, cvIllegal, cvEncroachment, cvRepair, cvGraveyards;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private long startTime = 0;
    private long endTime = Long.MAX_VALUE;

    private List<ComplaintModel> allComplaints = new ArrayList<>();
    private DatabaseReference complaintsRef;
    private boolean isFilterApplied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }

        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        btnClearFilter = findViewById(R.id.btnClearFilter);

        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvCleanlinessCount = findViewById(R.id.tvCleanlinessCount);
        tvStreetLightsCount = findViewById(R.id.tvStreetLightsCount);
        tvWaterCount = findViewById(R.id.tvWaterCount);
        tvIllegalCount = findViewById(R.id.tvIllegalCount);
        tvEncroachmentCount = findViewById(R.id.tvEncroachmentCount);
        tvRepairCount = findViewById(R.id.tvRepairCount);
        tvGraveyardsCount = findViewById(R.id.tvGraveyardsCount);

        tvPendingStatusCount = findViewById(R.id.tvPendingStatusCount);
        tvOpenStatusCount = findViewById(R.id.tvOpenStatusCount);
        tvClosedStatusCount = findViewById(R.id.tvClosedStatusCount);

        tvPositiveFeedbackCount = findViewById(R.id.tvPositiveFeedbackCount);
        tvNegativeFeedbackCount = findViewById(R.id.tvNegativeFeedbackCount);

        cvCleanliness = findViewById(R.id.cvAdminCleanliness);
        cvStreetLights = findViewById(R.id.cvAdminStreetLights);
        cvWater = findViewById(R.id.cvAdminWater);
        cvIllegal = findViewById(R.id.cvAdminIllegal);
        cvEncroachment = findViewById(R.id.cvAdminEncroachment);
        cvRepair = findViewById(R.id.cvAdminRepair);
        cvGraveyards = findViewById(R.id.cvAdminGraveyards);

        complaintsRef = FirebaseDatabase.getInstance().getReference(Constant.COMPLAINTS);

        setupDatePickers();
        fetchComplaints();
        setupCategoryClicks();

        btnApplyFilter.setOnClickListener(v -> applyFilters());
        btnClearFilter.setOnClickListener(v -> clearFilters());
    }

    private void setupCategoryClicks() {
        cvCleanliness.setOnClickListener(v -> openCategoryList("Cleanliness"));
        cvStreetLights.setOnClickListener(v -> openCategoryList("Street Lights"));
        cvWater.setOnClickListener(v -> openCategoryList("Water Connection")); // Default to one of the water types
        cvIllegal.setOnClickListener(v -> openCategoryList("Illegal Construction"));
        cvEncroachment.setOnClickListener(v -> openCategoryList("Encroachment"));
        cvRepair.setOnClickListener(v -> openCategoryList("Repair of Street"));
        cvGraveyards.setOnClickListener(v -> openCategoryList("Graveyards"));
    }

    private void openCategoryList(String category) {
        Intent intent = new Intent(AdminDashboardActivity.this, SortedComplaintsListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void setupDatePickers() {
        DatePickerDialog.OnDateSetListener startDateSetListener = (view, year, month, dayOfMonth) -> {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            startTime = startCalendar.getTimeInMillis();
            btnStartDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(startCalendar.getTime()));
        };

        DatePickerDialog.OnDateSetListener endDateSetListener = (view, year, month, dayOfMonth) -> {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, month);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            endCalendar.set(Calendar.HOUR_OF_DAY, 23);
            endCalendar.set(Calendar.MINUTE, 59);
            endCalendar.set(Calendar.SECOND, 59);
            endTime = endCalendar.getTimeInMillis();
            btnEndDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(endCalendar.getTime()));
        };

        btnStartDate.setOnClickListener(v -> new DatePickerDialog(this, startDateSetListener, 
                startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show());

        btnEndDate.setOnClickListener(v -> new DatePickerDialog(this, endDateSetListener, 
                endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void fetchComplaints() {
        complaintsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allComplaints.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ComplaintModel complaint = postSnapshot.getValue(ComplaintModel.class);
                    if (complaint != null) {
                        allComplaints.add(complaint);
                    }
                }
                if (isFilterApplied) {
                    applyFilters();
                } else {
                    updateStats(allComplaints);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database Error: " + error.getMessage());
            }
        });
    }

    private void applyFilters() {
        if (startTime > endTime) {
            Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show();
            return;
        }

        isFilterApplied = true;
        List<ComplaintModel> filtered = new ArrayList<>();
        int missingTimestampCount = 0;
        
        for (ComplaintModel c : allComplaints) {
            long ts = c.getTimestamp();
            if (ts == 0) {
                missingTimestampCount++;
                // If timestamp is missing, it won't match any specific date range.
                // We show it only if NO filter is applied (default state).
                continue; 
            }
            if (ts >= startTime && ts <= endTime) {
                filtered.add(c);
            }
        }
        
        if (missingTimestampCount > 0) {
            Log.d(TAG, "Excluded " + missingTimestampCount + " complaints with missing timestamps from filtered results.");
        }
        
        updateStats(filtered);
    }

    private void clearFilters() {
        isFilterApplied = false;
        startTime = 0;
        endTime = Long.MAX_VALUE;
        btnStartDate.setText("From");
        btnEndDate.setText("To");
        updateStats(allComplaints);
    }

    private void updateStats(List<ComplaintModel> list) {
        int total = list.size();
        int cleanliness = 0, streetLights = 0, water = 0, illegal = 0, encroachment = 0;
        int repair = 0, graveyards = 0;
        int pending = 0, open = 0, closed = 0, inProgress = 0;
        int positiveFeedback = 0, negativeFeedback = 0;

        for (ComplaintModel c : list) {
            String type = c.getComplainType();
            if (type != null) {
                if (type.equalsIgnoreCase("Cleanliness")) cleanliness++;
                else if (type.equalsIgnoreCase("Street Lights")) streetLights++;
                else if (type.equalsIgnoreCase("Water Connection") || type.equalsIgnoreCase("Water Supply Scheme")) water++;
                else if (type.equalsIgnoreCase("Illegal Construction")) illegal++;
                else if (type.equalsIgnoreCase("Encroachment")) encroachment++;
                else if (type.equalsIgnoreCase("Repair of Street")) repair++;
                else if (type.equalsIgnoreCase("Graveyards")) graveyards++;
            }

            String status = c.getComplainStatus();
            if (status != null) {
                String s = status.toLowerCase();
                if (s.contains("pending")) pending++;
                else if (s.contains("open")) open++;
                else if (s.contains("close")) closed++;
                else if (s.contains("in progress")) inProgress++;
            }

            String feedback = c.getFeedback();
            if (feedback != null) {
                if (feedback.equalsIgnoreCase("positive")) positiveFeedback++;
                else if (feedback.equalsIgnoreCase("negative")) negativeFeedback++;
            }
        }

        tvTotalCount.setText(String.valueOf(total));
        tvCleanlinessCount.setText(String.valueOf(cleanliness));
        tvStreetLightsCount.setText(String.valueOf(streetLights));
        tvWaterCount.setText(String.valueOf(water));
        tvIllegalCount.setText(String.valueOf(illegal));
        tvEncroachmentCount.setText(String.valueOf(encroachment));
        tvRepairCount.setText(String.valueOf(repair));
        tvGraveyardsCount.setText(String.valueOf(graveyards));

        tvPendingStatusCount.setText(String.valueOf(pending));
        tvOpenStatusCount.setText(String.valueOf(open + inProgress)); // Combine Open and In Progress for basic view or update UI
        tvClosedStatusCount.setText(String.valueOf(closed));

        tvPositiveFeedbackCount.setText(String.valueOf(positiveFeedback));
        tvNegativeFeedbackCount.setText(String.valueOf(negativeFeedback));
        
        Log.d(TAG, "Stats updated. Total: " + total + ", Filtered: " + isFilterApplied);
    }
}
