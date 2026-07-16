package com.example.municipalservices;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.municipalservices.models.UserSignUpModel;
import com.example.municipalservices.utils.Constant;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManageUsersActivity extends AppCompatActivity {

    private static final String TAG = "ManageUsers";
    private EditText etName, etEmail, etPassword, etOtherRole;
    private Spinner spinnerRole;
    private View tilOtherRole;
    private Button btnCreate;
    private ProgressBar progressBar;

    private DatabaseReference usersRef;
    private List<String> rolesList = new ArrayList<>();
    private ArrayAdapter<String> roleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Officer Management");
        }

        etName = findViewById(R.id.etNewUserName);
        etEmail = findViewById(R.id.etNewUserEmail);
        etPassword = findViewById(R.id.etNewUserPassword);
        etOtherRole = findViewById(R.id.etOtherRole);
        tilOtherRole = findViewById(R.id.tilOtherRole);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnCreate = findViewById(R.id.btnCreateUser);
        progressBar = findViewById(R.id.pbManageUsers);

        usersRef = FirebaseDatabase.getInstance().getReference(Constant.USER);

        setupRoleSpinner();
        fetchExistingRoles();

        btnCreate.setOnClickListener(v -> createCredentials());
    }

    private void setupRoleSpinner() {
        rolesList.add("Admin");
        rolesList.add("Other");
        roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rolesList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        spinnerRole.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (rolesList.get(position).equals("Other")) {
                    tilOtherRole.setVisibility(View.VISIBLE);
                } else {
                    tilOtherRole.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void fetchExistingRoles() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> fetchedRoles = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    UserSignUpModel user = child.getValue(UserSignUpModel.class);
                    if (user != null && user.getRole() != null) {
                        String role = user.getRole();
                        if (!role.equalsIgnoreCase("User") && !fetchedRoles.contains(role)) {
                            fetchedRoles.add(role);
                        }
                    }
                }
                
                // Keep Admin at start and Other at end
                rolesList.clear();
                rolesList.add("Admin");
                for (String r : fetchedRoles) {
                    if (!r.equalsIgnoreCase("Admin")) {
                        rolesList.add(r);
                    }
                }
                rolesList.add("Other");
                roleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void createCredentials() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String selectedRole = spinnerRole.getSelectedItem().toString();
        final String role;

        if (selectedRole.equals("Other")) {
            role = etOtherRole.getText().toString().trim();
            if (role.isEmpty()) {
                Toast.makeText(this, "Please specify the role name", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            role = selectedRole;
        }

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnCreate.setEnabled(false);

        // Attempt to create user in FirebaseAuth without logging out the current admin
        // We use a secondary FirebaseApp for this
        FirebaseOptions options = FirebaseApp.getInstance().getOptions();
        FirebaseApp secondaryApp = null;
        try {
            secondaryApp = FirebaseApp.getInstance("secondary");
        } catch (IllegalStateException e) {
            secondaryApp = FirebaseApp.initializeApp(this, options, "secondary");
        }

        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);

        secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String encryptedPassword = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
                        UserSignUpModel newUser = new UserSignUpModel(name, email, encryptedPassword, null, role);
                        
                        usersRef.push().setValue(newUser).addOnCompleteListener(dbTask -> {
                            progressBar.setVisibility(View.GONE);
                            btnCreate.setEnabled(true);
                            if (dbTask.isSuccessful()) {
                                Toast.makeText(this, "Credentials created for " + role, Toast.LENGTH_LONG).show();
                                etName.setText("");
                                etEmail.setText("");
                                etPassword.setText("");
                                etOtherRole.setText("");
                                secondaryAuth.signOut(); // Sign out from the secondary app instance
                            } else {
                                Toast.makeText(this, "Database error: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnCreate.setEnabled(true);
                        Toast.makeText(this, "Auth error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
