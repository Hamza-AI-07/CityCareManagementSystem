package com.example.municipalservices;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.municipalservices.models.UserSignUpModel;
import com.example.municipalservices.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import android.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    FirebaseDatabase database;

    EditText name,email,password,confirmPassword;
    Button signup;
    FirebaseAuth auth;

   public static String pasrse_email;
    ArrayList<UserSignUpModel> list=new ArrayList<>();
    DatabaseReference pt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up");

        name=findViewById(R.id.tv_complaint_title);
        email=findViewById(R.id.tv_complainer_name);
        password=findViewById(R.id.su_password);
        confirmPassword = findViewById(R.id.su_confirm_password);
        signup=findViewById(R.id.su_signup);

        database = FirebaseDatabase.getInstance();
        pt = database.getReference(Constant.USER);

        auth = FirebaseAuth.getInstance();
        email.setText(pasrse_email);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pattern p1=Pattern.compile("^[a-z][a-z0-9_/.]*@(gmail|yahoo|hotmail).com$");


                Matcher m1=p1.matcher(email.getText().toString());

                if(name.getText().length()<5){Toast.makeText(getApplicationContext(),"Enter Name minimum 5 characters",Toast.LENGTH_SHORT).show();return;}

                if(!m1.matches()){
                    Toast.makeText(getApplicationContext(),"Email is not valid",Toast.LENGTH_SHORT).show(); return;}
                if(password.getText().length()<8){Toast.makeText(getApplicationContext(),"Password is not valid min 8 character",Toast.LENGTH_SHORT).show(); return;}

//                for (int i=0;i<list.size();i++)
//                {
//                    if(list.get(i).getEmail().toString().equals(email.getText().toString()))
//                    {
//                        Toast.makeText(SignUpActivity.this, "Email is already exist", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }

                if(password.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString())) {

                    auth.createUserWithEmailAndPassword(email.getText().toString().trim().toLowerCase(), password.getText().toString())
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "sign up complete", Toast.LENGTH_SHORT).show();
                                        String encryptedPassword = Base64.encodeToString(password.getText().toString().getBytes(), Base64.DEFAULT);
                                        pt.push().setValue(new UserSignUpModel(name.getText().toString(), email.getText().toString(), encryptedPassword));
                                        Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        finish();
                                    } else {
                                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                        Toast.makeText(SignUpActivity.this, "Authentication failed: " + error, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }else Toast.makeText(SignUpActivity.this, "Password mismatch", Toast.LENGTH_SHORT).show();




            }
        });

        pt.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                UserSignUpModel sd=dataSnapshot.getValue(UserSignUpModel.class);
                list.add(sd);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void goto_login(View v)
    {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}