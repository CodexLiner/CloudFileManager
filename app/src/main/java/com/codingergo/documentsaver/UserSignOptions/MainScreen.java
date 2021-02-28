package com.codingergo.documentsaver.UserSignOptions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.codingergo.documentsaver.R;
import com.codingergo.documentsaver.HomeScreen.Home;
import com.google.firebase.auth.FirebaseAuth;

public class MainScreen extends AppCompatActivity {
    Button Login , SignUp;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Login = findViewById(R.id.ButtonLogin);
        SignUp = findViewById(R.id.ButtonSignUp);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), Home.class));
            finishAffinity();
        }
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginPanel.class));
            }
        });
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), com.codingergo.documentsaver.UserSignOptions.SignUp.class));
            }
        });
    }
}