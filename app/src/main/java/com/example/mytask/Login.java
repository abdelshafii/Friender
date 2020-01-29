package com.example.mytask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends Activity {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    double lat = 0, lan = 0;
    @BindView(R.id.email_login)
    EditText email;
    @BindView(R.id.password_login)
    EditText password;
    @BindView(R.id.root)
    LinearLayout root;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Your login is loading....");
        progressDialog.setTitle("login");
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
    }
    @OnClick(R.id.roundedButton)
    public void sss(View view){
        if(email.getText().toString().trim().equals("")){
            email.setError("enter your email!");
            KToast.errorToast(Login.this,"enter your email",Gravity.BOTTOM,KToast.LENGTH_LONG);
        }
        else if(password.getText().toString().trim().equals("")){
            password.setError("enter your password!");
            KToast.errorToast(Login.this,"enter your password",Gravity.BOTTOM,KToast.LENGTH_LONG);
        }
        else{
            progressDialog.show();
            String Email=email.getText().toString().trim();
            String Password=password.getText().toString().trim();
            mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(getApplicationContext(),UserPage.class));
                        KToast.successToast(Login.this,"loged in successfully",Gravity.BOTTOM,KToast.LENGTH_LONG);
                        progressDialog.dismiss();
                        Login.this.finish();
                    }else{
                        progressDialog.dismiss();
                        KToast.errorToast(Login.this,"make sure that is your email and password",Gravity.BOTTOM,KToast.LENGTH_LONG);
                        KToast.errorToast(Login.this,"chick your connection to the internet",Gravity.BOTTOM,KToast.LENGTH_LONG);
                    }
                }
            });
        }
    }
    @OnClick(R.id.newAccount)
    public void newAccount(View view){
        startActivity(new Intent(this, SignUp.class));
        this.finish();
    }
}
