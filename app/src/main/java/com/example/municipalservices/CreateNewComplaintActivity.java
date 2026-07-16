package com.example.municipalservices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.municipalservices.models.ComplaintModel;
import com.example.municipalservices.utils.Constant;
import com.example.municipalservices.utils.DuplicateDetectionUtils;
import com.example.municipalservices.utils.ValidationUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateNewComplaintActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewComplaint";

    EditText complainerNameTextView, complainerPhoneTextView, complainerEmailTextView, complainTitleTextView, complainDescriptionTextView, latitudeTextView, longitudeTextView, complainerAddressTextView;
    ImageView complaintImage;
    ImageButton btnClearImage;
    Button btnSubmitComplain;
    ProgressBar progressBar;
    CheckBox complainerNumberCheckBox;
    
    int complaintID = 1;

    FirebaseDatabase database;
    String image_url;
    StorageReference mStorage;
    String showNumberToEveryOne;
    String complainType;
    Uri cameraImageUri;
    private boolean isEditMode = false;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String[]> galleryPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_complaint);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String category = getIntent().getStringExtra("category");
            actionBar.setTitle(category != null ? category : "New Complaint");
        }

        complainType = getIntent().getStringExtra("category");

        complainerNameTextView = findViewById(R.id.tv_complainer_name);
        complainerPhoneTextView = findViewById(R.id.tv_complainer_contact_number);
        complainerEmailTextView = findViewById(R.id.tv_complainer_email);
        complainTitleTextView = findViewById(R.id.tv_complaint_title);
        complainDescriptionTextView = findViewById(R.id.tv_complaint_description);
        latitudeTextView = findViewById(R.id.tv_complainer_latitude);
        longitudeTextView = findViewById(R.id.tv_complainer_lonitude);
        complainerAddressTextView = findViewById(R.id.tv_complainer_address);
        complaintImage = findViewById(R.id.iv_complaint_image);
        btnClearImage = findViewById(R.id.btn_clear_image);
        progressBar = findViewById(R.id.pb_complain);
        btnSubmitComplain = findViewById(R.id.btn_submit_complain);
        complainerNumberCheckBox = findViewById(R.id.cb_complainer_contact_number_checkbox);

        checkEditMode();

        // Autofill current user's email
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            complainerEmailTextView.setText(currentUserEmail);
            
            View tilEmail = findViewById(R.id.til_email);
            if (tilEmail != null) {
                tilEmail.setVisibility(View.GONE); // Hide the whole container
            } else {
                complainerEmailTextView.setVisibility(View.GONE);
            }
        }

        // Initialize Firebase
        try {
            String bucketUrl = "gs://" + Constant.STORAGE_BUCKET;
            Log.d(TAG, "Initializing FirebaseStorage with URL: " + bucketUrl);
            mStorage = FirebaseStorage.getInstance(bucketUrl).getReference();
            Log.d(TAG, "FirebaseStorage initialized successfully. Current bucket: " + mStorage.getBucket());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing FirebaseStorage", e);
            mStorage = FirebaseStorage.getInstance().getReference();
        }
        database = FirebaseDatabase.getInstance();
        getUniqueComplaintID();

        initLaunchers();

        latitudeTextView.setOnClickListener(v -> {
            Toast.makeText(this, "Manual entry enabled for location.", Toast.LENGTH_SHORT).show();
            latitudeTextView.setFocusableInTouchMode(true);
            longitudeTextView.setFocusableInTouchMode(true);
            latitudeTextView.requestFocus();
        });

        complaintImage.setOnClickListener(v -> showImageSelectionDialog());
        btnClearImage.setOnClickListener(v -> clearSelectedImage());
        btnSubmitComplain.setOnClickListener(view -> validateAndSubmit());
    }

    private void checkEditMode() {
        isEditMode = getIntent().getBooleanExtra("isEdit", false);
        if (isEditMode && ComplaintDetailsUserActivity.list != null) {
            ComplaintModel c = ComplaintDetailsUserActivity.list;
            complainerNameTextView.setText(c.getComplainerName());
            complainerPhoneTextView.setText(c.getComplainerPhoneNumber());
            complainerEmailTextView.setText(c.getComplainerEmail());
            complainTitleTextView.setText(c.getComplainTitle());
            complainDescriptionTextView.setText(c.getComplainDescription());
            latitudeTextView.setText(c.getComplainerLatitude());
            longitudeTextView.setText(c.getComplainerLongitude());
            complainerAddressTextView.setText(c.getComplainerAddress());
            complainType = c.getComplainType();
            image_url = c.getComplainImageURL();
            complaintID = c.getComplaintID();
            
            if (image_url != null && !image_url.isEmpty()) {
                Picasso.get().load(image_url).into(complaintImage);
                btnClearImage.setVisibility(View.VISIBLE);
            }

            btnSubmitComplain.setText("Update Complaint");
        }
    }

    private void initLaunchers() {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) uploadImage(uri);
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (cameraImageUri != null) uploadImage(cameraImageUri);
            }
        });

        cameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        galleryPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean isGranted = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                isGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_MEDIA_IMAGES)) ||
                            Boolean.TRUE.equals(result.get(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                isGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_MEDIA_IMAGES));
            } else {
                isGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
            }

            if (isGranted) {
                openGallery();
            } else {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageSelectionDialog() {
        String[] options = {getString(R.string.take_photo), getString(R.string.choose_from_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_image);
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkPermissionAndOpenImageSource(true);
            } else {
                checkPermissionAndOpenImageSource(false);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void checkPermissionAndOpenImageSource(boolean isCamera) {
        if (isCamera) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                openCamera();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                String[] permissions = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED};
                galleryPermissionLauncher.launch(permissions);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                galleryPermissionLauncher.launch(new String[]{Manifest.permission.READ_MEDIA_IMAGES});
            } else {
                galleryPermissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file", ex);
            }
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                cameraLauncher.launch(intent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void clearSelectedImage() {
        image_url = null;
        complaintImage.setImageResource(R.drawable.icon_upload);
        btnClearImage.setVisibility(View.GONE);
        Toast.makeText(this, R.string.image_cleared, Toast.LENGTH_SHORT).show();
    }

    private void validateAndSubmit() {
        String name = complainerNameTextView.getText().toString().trim();
        String phone = complainerPhoneTextView.getText().toString().trim();
        String email = complainerEmailTextView.getText().toString().trim();
        String title = complainTitleTextView.getText().toString().trim();
        String description = complainDescriptionTextView.getText().toString().trim();
        String lat = latitudeTextView.getText().toString().trim();

        if (!ValidationUtils.isValidName(name)) {
            complainerNameTextView.setError("Name too short");
            return;
        }
        if (!ValidationUtils.isValidPhoneNumber(phone)) {
            complainerPhoneTextView.setError("Invalid phone");
            return;
        }
        
        // Email is now auto-fetched from current user and set to complainerEmailTextView in onCreate

        if (!ValidationUtils.isValidTitle(title)) {
            complainTitleTextView.setError("Title too short");
            return;
        }
        if (!ValidationUtils.isValidDescription(description)) {
            complainDescriptionTextView.setError("Description too short");
            return;
        }
        if (lat.isEmpty()) {
            Toast.makeText(this, "Please enter location coordinates.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Image is now optional
        if (image_url == null) {
            image_url = ""; // Set to empty string if no image
        }

        showNumberToEveryOne = complainerNumberCheckBox.isChecked() ? "yes" : "no";

        final DatabaseReference complaints = database.getReference(Constant.COMPLAINTS);
        complaints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ComplaintModel> existing = new ArrayList<>();
                Map<String, String> idMap = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ComplaintModel c = snapshot.getValue(ComplaintModel.class);
                    if (c != null) {
                        existing.add(c);
                        idMap.put(String.valueOf(c.getComplaintID()), snapshot.getKey());
                    }
                }

                DuplicateDetectionUtils.DuplicateResult result = DuplicateDetectionUtils.checkForDuplicates(
                        title, description, email, existing, idMap
                );

                if (result.isDuplicate && !isEditMode) {
                    Toast.makeText(CreateNewComplaintActivity.this, "Duplicate: " + result.message, Toast.LENGTH_LONG).show();
                } else {
                    submitComplaint(complaints);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void submitComplaint(DatabaseReference complaints) {
        ComplaintModel model = new ComplaintModel(
                complaintID,
                isEditMode ? ComplaintDetailsUserActivity.list.getLikes() : 0,
                isEditMode ? ComplaintDetailsUserActivity.list.getDislikes() : 0,
                complainerNameTextView.getText().toString(),
                complainerPhoneTextView.getText().toString(),
                complainerEmailTextView.getText().toString(),
                complainTitleTextView.getText().toString(),
                complainDescriptionTextView.getText().toString(),
                latitudeTextView.getText().toString(),
                longitudeTextView.getText().toString(),
                complainerAddressTextView.getText().toString(),
                complainType, image_url, Constant.STATUS_PENDING, showNumberToEveryOne
        );
        
        // Retain original metadata if editing
        if (isEditMode) {
            model.setAssignedRole(ComplaintDetailsUserActivity.list.getAssignedRole());
            model.setRemarks(ComplaintDetailsUserActivity.list.getRemarks());
            model.setFeedback(ComplaintDetailsUserActivity.list.getFeedback());
            model.setTimestamp(ComplaintDetailsUserActivity.list.getTimestamp());
        }

        if (isEditMode) {
            // Update existing
            complaints.orderByChild("complaintID").equalTo(complaintID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                child.getRef().setValue(model).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Complaint Updated", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        } else {
            // Create new
            complaints.push().setValue(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void uploadImage(Uri uri) {
        if (uri == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        btnSubmitComplain.setEnabled(false);

        // Create a unique filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "JPEG_" + timeStamp + ".jpg";
        
        Log.d(TAG, "Uploading image to bucket: " + mStorage.getBucket());
        StorageReference photosRef = mStorage.child("photos");
        StorageReference filepath = photosRef.child(fileName);

        Log.d(TAG, "Full Storage Path: " + filepath.getPath());

        filepath.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        if (task.getException() != null) {
                            throw task.getException();
                        } else {
                            throw new Exception("Unknown upload error");
                        }
                    }
                    return filepath.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmitComplain.setEnabled(true);
                    
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        image_url = downloadUri.toString();
                        
                        btnClearImage.setVisibility(View.VISIBLE);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            complaintImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Log.e(TAG, "Bitmap Error", e);
                        }
                        Toast.makeText(getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Exception e = task.getException();
                        String error = "Unknown error";
                        if (e != null) {
                            error = e.getMessage();
                            if (e instanceof StorageException) {
                                int errorCode = ((StorageException) e).getErrorCode();
                                Log.e(TAG, "Storage Error Code: " + errorCode);
                                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                                    error = "Object not found (check bucket name in google-services.json)";
                                }
                            }
                        }
                        Log.e(TAG, "Upload failed: " + error, e);
                        Toast.makeText(CreateNewComplaintActivity.this, "Upload failed: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getUniqueComplaintID() {
        if (database == null) return;
        database.getReference(Constant.COMPLAINTS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                ComplaintModel di = dataSnapshot.getValue(ComplaintModel.class);
                if (di != null) complaintID = Math.max(complaintID, di.getComplaintID() + 1);
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
}
