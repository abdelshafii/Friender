package com.example.mytask;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class Home extends AppCompatActivity {
@BindView(R.id.logo)
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.sign_in)
    public void signin(View view){
        startActivity(new Intent(Home.this, Login.class));
        Animatoo.animateSlideDown(this);
    }
    @OnClick(R.id.sign_up)
    public void signup(View view){
        startActivity(new Intent(this, SignUp.class));
        Animatoo.animateSlideUp(this);
    }
}
