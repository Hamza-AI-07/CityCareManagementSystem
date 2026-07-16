package com.example.municipalservices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.municipalservices.models.UserSignUpModel;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.example.municipalservices.utils.Constant;
import com.example.municipalservices.utils.ValidationUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    EditText email, password, etOtherRole;
    TextView tvLoginTitle;
    Spinner spinnerRole;
    View layoutAdminFields;
    ViewGroup layoutRoot;
    MaterialButtonToggleGroup toggleGroup;
    Button signIn;
    FirebaseAuth auth;
    private List<String> rolesList = new ArrayList<>();
    private ArrayAdapter<String> roleAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            String emailStr = auth.getCurrentUser().getEmail();
            if (emailStr != null && emailStr.equals(Constant.ADMIN_EMAIL)) {
                startActivity(new Intent(LoginActivity.this, AdminPanelActivity.class));
                finish();
            } else if (emailStr != null) {
                // Fetch role from database to decide which dashboard
                FirebaseDatabase.getInstance().getReference(Constant.USER)
                        .orderByChild("email").equalTo(emailStr)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String role = "User";
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    UserSignUpModel user = child.getValue(UserSignUpModel.class);
                                    if (user != null) role = user.getRole();
                                }
                                
                                if (role.equalsIgnoreCase("User")) {
                                    startActivity(new Intent(LoginActivity.this, UserHomeTab.class));
                                } else if (role.equalsIgnoreCase("Admin")) {
                                    startActivity(new Intent(LoginActivity.this, AdminPanelActivity.class));
                                } else {
                                    startActivity(new Intent(LoginActivity.this, OfficerDashboardActivity.class));
                                }
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Default to user if failed
                                startActivity(new Intent(LoginActivity.this, UserHomeTab.class));
                                finish();
                            }
                        });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login");
        }

        email = findViewById(R.id.lg_email);
        password = findViewById(R.id.lg_password);
        etOtherRole = findViewById(R.id.etLoginOtherRole);
        spinnerRole = findViewById(R.id.spinnerLoginRole);
        layoutAdminFields = findViewById(R.id.layoutAdminFields);
        layoutRoot = findViewById(R.id.loginLayoutRoot);
        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        toggleGroup = findViewById(R.id.toggleGroupPanel);
        signIn = findViewById(R.id.lg_signin);

        setupRoleSpinner();
        setupPanelToggle();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Are you sure you want to exit?")
                        .setPositiveButton("Exit", (dialogInterface, i) -> finish())
                        .setNegativeButton("No", null)
                        .setCancelable(false)
                        .show();
            }
        });

        signIn.setOnClickListener(v -> handleLogin());
    }

    private void setupRoleSpinner() {
        // Initialize with default roles immediately
        rolesList.clear();
        rolesList.add("Admin");
        rolesList.add("Other");
        
        roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rolesList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        spinnerRole.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (rolesList.get(position).equals("Other")) {
                    etOtherRole.setVisibility(View.VISIBLE);
                } else {
                    etOtherRole.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        fetchAvailableRoles();
    }

    private void fetchAvailableRoles() {
        FirebaseDatabase.getInstance().getReference(Constant.USER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> fetchedRoles = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            UserSignUpModel user = child.getValue(UserSignUpModel.class);
                            if (user != null && user.getRole() != null) {
                                String role = user.getRole();
                                if (!role.equalsIgnoreCase("User") && !role.equalsIgnoreCase("Admin") && !fetchedRoles.contains(role)) {
                                    fetchedRoles.add(role);
                                }
                            }
                        }

                        // Re-build list with Admin first, then fetched roles, then Other
                        rolesList.clear();
                        rolesList.add("Admin");
                        rolesList.addAll(fetchedRoles);
                        rolesList.add("Other");
                        roleAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void setupPanelToggle() {
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                TransitionManager.beginDelayedTransition(layoutRoot);
                if (checkedId == R.id.btnAdminPanel) {
                    layoutAdminFields.setVisibility(View.VISIBLE);
                    tvLoginTitle.setText("Admin/Officer Login");
                } else {
                    layoutAdminFields.setVisibility(View.GONE);
                    tvLoginTitle.setText("User Login");
                }
            }
        });
    }

    private void handleLogin() {
        if (!isNetworkAvailable()) {
            Toast.makeText(LoginActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        final String emailStr = email.getText().toString().trim();
        final String passwordStr = password.getText().toString().trim();
        
        final String roleToVerify;
        if (toggleGroup.getCheckedButtonId() == R.id.btnUserPanel) {
            roleToVerify = "User";
        } else {
            if (spinnerRole.getSelectedItem() == null) {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedRole = spinnerRole.getSelectedItem().toString();
            if (selectedRole.equals("Other")) {
                roleToVerify = etOtherRole.getText().toString().trim();
                if (roleToVerify.isEmpty()) {
                    etOtherRole.setError("Specify your role");
                    return;
                }
            } else {
                roleToVerify = selectedRole;
            }
        }

        if (emailStr.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("Enter a valid email");
            return;
        }
        if (passwordStr.isEmpty()) {
            password.setError("Password is required");
            return;
        }

        final android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setMessage("Authenticating...");
        pd.setCancelable(false);
        pd.show();

        signIn.setVisibility(View.INVISIBLE); // Button invisible but dialog shows
        
        auth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Success: Keep button invisible, verify role
                        verifyRoleAndRedirect(emailStr, roleToVerify, pd);
                    } else {
                        // Failure: Dismiss dialog and show button again
                        pd.dismiss();
                        signIn.setVisibility(View.VISIBLE);
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verifyRoleAndRedirect(String emailStr, String roleToVerify, final android.app.ProgressDialog pd) {
        // Special case for main admin (admin@gmail.com)
        if (emailStr.equalsIgnoreCase(Constant.ADMIN_EMAIL)) {
            pd.dismiss();
            if (roleToVerify.equalsIgnoreCase("Admin")) {
                Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, AdminPanelActivity.class));
                finish();
            } else {
                auth.signOut();
                signIn.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Access denied: Please select 'Admin' role", Toast.LENGTH_LONG).show();
            }
            return;
        }

        FirebaseDatabase.getInstance().getReference(Constant.USER)
                .orderByChild("email").equalTo(emailStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pd.dismiss();
                        signIn.setVisibility(View.VISIBLE);
                        String matchedRole = null;
                        
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                UserSignUpModel user = child.getValue(UserSignUpModel.class);
                                if (user != null && user.getRole() != null && user.getRole().equalsIgnoreCase(roleToVerify)) {
                                    matchedRole = user.getRole();
                                    break;
                                }
                            }
                        }

                        if (matchedRole != null) {
                            redirectUser(matchedRole);
                        } else {
                            auth.signOut();
                            Toast.makeText(LoginActivity.this, "Access denied: Role mismatch in database", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pd.dismiss();
                        signIn.setVisibility(View.VISIBLE);
                        auth.signOut();
                        Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectUser(String role) {
        if (role.equalsIgnoreCase("User")) {
            startActivity(new Intent(LoginActivity.this, UserHomeTab.class));
        } else if (role.equalsIgnoreCase("Admin")) {
            startActivity(new Intent(LoginActivity.this, AdminPanelActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this, OfficerDashboardActivity.class));
        }
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            android.net.Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
        return false;
    }

    public void goto_signup(View v) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        finish();
    }

    public void forgetPassword(View view) {
        String emailStr = email.getText().toString().trim();
        if (emailStr.isEmpty()) {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Alert")
                    .setMessage("Please enter a valid email address to reset your password")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Password Reset")
                    .setMessage("You will receive a password reset email at: " + emailStr)
                    .setPositiveButton("Send", (dialogInterface, i) -> 
                        auth.sendPasswordResetEmail(emailStr)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Password Reset Error", task.getException());
                                    }
                                })
                    )
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}
