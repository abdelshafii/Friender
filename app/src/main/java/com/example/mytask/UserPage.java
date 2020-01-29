package com.example.mytask;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.time.Instant;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserPage extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    SignUp signUp=new SignUp();
    @BindView(R.id.your_ID_TextView)
    TextView yourid;
    @BindView(R.id.textview)
    TextView textView;
    @BindView(R.id.friend_id)
    EditText FriendId;
    @BindView(R.id.friend_name)
    EditText FriendName;
    @BindView(R.id.my_name)
    EditText MyName;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    UserDataBaseManager userDataBaseManager;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    double lat = 0, lan = 0;
    String ran=signUp.Ran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);
        ButterKnife.bind(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("Data_Users");
        userDataBaseManager = new UserDataBaseManager(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @OnClick(R.id.GetId_btn)
    public void get_id(View view) {
        try {
            if (MyName.getText().toString().trim().length() == 0) {
                MyName.setError("enter your name to get your id!");
            } else {
                myRef.child(MyName.getText().toString().trim()+"_"+ran).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Random rand = new Random();
                        int random = rand.nextInt(10000) + 1;
                        final String Ran = String.valueOf(random);
                        String id = MyName.getText().toString().trim() + "_" + Ran;
                        myRef.child(MyName.getText().toString().trim()).child("Secret Number").setValue(id);
                        myRef.child(MyName.getText().toString().trim()).child("Latitude").setValue(String.valueOf(lat));
                        myRef.getRef().child(MyName.getText().toString().trim()).child("Longitude").setValue(String.valueOf(lan));
                        textView.setVisibility(View.VISIBLE);
                        yourid.setVisibility(View.VISIBLE);
                        yourid.setText(id);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.getMessage());
        }
    }

    @OnClick(R.id.go_map)
    public void Go_Map(View view) {
        try {
            if ((FriendId.getText().toString().length()) == 0) {
                FriendId.setError("Enter your friend id!");
            } else {
                myRef.child(FriendName.getText().toString()+"_"+ran).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("Secret Number").getValue().equals(FriendId.getText().toString())) {
                            String Name = dataSnapshot.child("name").getValue().toString();
                            String Latitude = dataSnapshot.child("Latitude").getValue().toString();
                            String Longitude = dataSnapshot.child("Longitude").getValue().toString();
                            Intent intent = new Intent(UserPage.this, MapsActivity.class);
                            intent.putExtra("Name", Name);
                            intent.putExtra("Latitude", Latitude);
                            intent.putExtra("Longitude", Longitude);
                            startActivity(intent);
                            Toast.makeText(UserPage.this, userDataBaseManager.insertToMyData(Name, FriendId.getText().toString()), Toast.LENGTH_SHORT).show();
                        } else  {
                            KToast.errorToast(UserPage.this, "Data Not Found!", Gravity.TOP, KToast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }
                });
            }
        } catch (Exception e) {
            KToast.errorToast(UserPage.this, "Not Found!", Gravity.BOTTOM, KToast.LENGTH_SHORT);
        }
    }

    @OnClick(R.id.allfriend)
    public void All_Friend(View view) {
        startActivity(new Intent(this, UserDataBase.class));
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
        this.lat = location.getLatitude();
        this.lan = location.getLongitude();
        final double lat2 = location.getLatitude();
        final double lan2 = location.getLongitude();

        myRef.child(FriendName.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myRef.getRef().child(MyName.getText().toString().trim()+"_"+ran).child("Latitude").setValue(String.valueOf(lat));
                myRef.getRef().child(MyName.getText().toString().trim()+"_"+ran).child("Longitude").setValue(String.valueOf(lan));

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
