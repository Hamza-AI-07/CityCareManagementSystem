package com.example.municipalservices;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.municipalservices.models.UserSignUpModel;
import com.example.municipalservices.utils.Constant;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminHomeActivity extends AppCompatActivity {



    private int IMAGE_REQUEST_CODE = 1;
    FirebaseDatabase database;
    Button btnResetPassword;
    String pushID = "";
    ImageView adminImage;
    TextView adminName, adminEmail;
    EditText adminPassword;


    String image_url;
    StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        getSupportActionBar().setTitle("Admin Profile");

        btnResetPassword = findViewById(R.id.btnChangePassword);
        adminImage = findViewById(R.id.civ_admin_image);
        adminName = findViewById(R.id.tv_admin_name);
        adminPassword = findViewById(R.id.tv_admin_password);
        adminEmail = findViewById(R.id.tv_admin_email);


        setAdminData();


        mStorage= FirebaseStorage.getInstance().getReference();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(adminPassword.isEnabled())
                {
                    if(adminPassword.getText().toString().length()<5){
                        Toast.makeText(AdminHomeActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        resetPasswordInFirebase(adminPassword.getText().toString());
                        adminPassword.setEnabled(false);

                    }
                }
                else
                    adminPassword.setEnabled(true);

            }
        });

        adminImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent();
                i.setType("image/");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select File"), IMAGE_REQUEST_CODE);
            }
        });


    }





    private void setAdminData()
    {


        database = FirebaseDatabase.getInstance();
      /*  final DatabaseReference pt = database.getReference(Constant.ADMIN);

        pt.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                UserSignUpModel sd = dataSnapshot.getValue(UserSignUpModel.class);

                adminName.setText(""+sd.getName());
                adminEmail.setText(""+sd.getEmail());
                adminPassword.setText(""+sd.getPassword());

                Picasso.get().load(sd.getImageURL()).into(adminImage);

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
*/
    }



   private void resetPasswordInFirebase(final String password)
    {

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(Constant.ADMIN);

        reference.orderByChild("email").equalTo("admin@gmail.com")
                .addListenerForSingleValueEvent(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            pushID = child.getKey();


                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();


                            rootRef.child(Constant.ADMIN)
                                    .child(pushID)
                                    .child("password")
                                    .setValue(password);
                            Toast.makeText(AdminHomeActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==IMAGE_REQUEST_CODE && resultCode==RESULT_OK)
        {


            Uri uri=data.getData();
            StorageReference filepath=mStorage.child("photos").child("admin@gmail.com");

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // image_url=taskSnapshot.getDownloadUrl().toString();
                    Toast.makeText(AdminHomeActivity.this, "Picture changed", Toast.LENGTH_SHORT).show();

                    //updateImageURL();

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminHomeActivity.this, "Picture not changed", Toast.LENGTH_SHORT).show();

                }
            });

            Bitmap bitmap= null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

            } catch (IOException e) {
                e.printStackTrace();
            }
            adminImage.setImageBitmap(bitmap);



        }




    }



    private void updateImageURL(){

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(Constant.ADMIN);

        reference.orderByChild("email").equalTo("admin@gmail.com")
                .addListenerForSingleValueEvent(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            pushID = child.getKey();


                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();


                            rootRef.child(Constant.ADMIN)
                                    .child(pushID)
                                    .child("imageURL")
                                    .setValue(image_url);
                            Toast.makeText(AdminHomeActivity.this, "Picture changed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
    }



}
