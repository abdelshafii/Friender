package com.example.mytask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Random rand = new Random();
    int random = rand.nextInt(10000) + 1;
    final String Ran = String.valueOf(random);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
            "(?=.*[0-9])" +
            "(?=.*[A-Z])" +
            "(?=.*[a-z])" +
            "(?=.*[@#$%^&+=!])" +
            //"(?=\\S+$)" +
            ".{6,}" +
            "$");
    @BindView(R.id.upload)
    ImageView image;
    @BindView(R.id.email_register)
    EditText email;
    @BindView(R.id.name)
    EditText Name;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.password_register)
    EditText password;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    double lat = 0, lan = 0;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private StorageReference mStorageRef;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("wait for verifying your email address,\n your process is loading....");
        progressDialog.setTitle("registering");
        progressDialog.setIcon(R.drawable.common_google_signin_btn_icon_dark);
        ButterKnife.bind(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Data_Users");
        currentUser = mAuth.getCurrentUser();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.sign_up2)
    public void storge(View view) {

        String Email = email.getText().toString().trim();
        String name = Name.getText().toString().trim();
        String Phone = phone.getText().toString().trim();
        String Password = password.getText().toString().trim();
        if (Email.equals("")) {
            email.setError("enter your email!");
            KToast.errorToast(SignUp.this, "enter your information please☺", Gravity.BOTTOM, KToast.LENGTH_LONG);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            email.setError("please enter a valid email!('@','.')");
        }
        if (name.equals("")) {
            Name.setError("enter your name!");
        } else if (Phone.equals("")) {
            phone.setError("enter your phone!");
        } else if (Password.equals("")) {
            password.setError("enter your pass!");
        } else if (!PASSWORD_PATTERN.matcher(password.getText().toString().trim()).matches()) {
            password.setError("0-9\n'A-Z'\n'a-z'\n'@#$%^&+=!'\n6 characters");
        } else {
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String finalRan = Name.getText().toString() + "_" + Ran;
                        Map<String, String> map = new HashMap<>();
                        map.put("email", email.getText().toString().trim());
                        map.put("name", Name.getText().toString().trim());
                        map.put("password", password.getText().toString().trim());
                        map.put("phone", phone.getText().toString().trim());
                        map.put("Secret Number", finalRan);
                        map.put("Latitude", String.valueOf(lat));
                        map.put("Longitude", String.valueOf(lan));
                        myRef.getRef().child(finalRan).setValue(map);
                        KToast.successToast(SignUp.this, "Account Created Success \n enjoy ♥☺☺", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), Login.class));

                    } else if (!task.isSuccessful()) {
                        progressDialog.dismiss();
                        email.setError("this email address is used before!");
                        KToast.errorToast(SignUp.this, "this email address is used before!", Gravity.BOTTOM, KToast.LENGTH_LONG);
                    }
                }
            });
        }
    }

    @OnClick(R.id.already)
    public void already(View view) {
        startActivity(new Intent(this, Login.class));
        this.finish();
    }

    @OnClick(R.id.click)
    public void clickk(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        String finalRan = Name.getText().toString() + "_" + Ran;
        this.lat = location.getLatitude();
        this.lan = location.getLongitude();
        final double lat2 = location.getLatitude();
        final double lan2 = location.getLongitude();

        myRef.child(finalRan).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myRef.child(finalRan).child("Latitude").setValue(String.valueOf(lat2));
                myRef.child(finalRan).child("Longitude").setValue(String.valueOf(lan2));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }
}
