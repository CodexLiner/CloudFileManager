package com.codingergo.documentsaver.UserSignOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codingergo.documentsaver.HomeScreen.Home;
import com.codingergo.documentsaver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPanel extends AppCompatActivity {
TextView CreateButton ;
EditText Email , Pass;
Button login;
FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_panel);
        CreateButton = findViewById(R.id.CreateButton);
        auth = FirebaseAuth.getInstance();
        Pass = findViewById(R.id.Pass);
        Email = findViewById(R.id.Email);
        login = findViewById(R.id.ButtonLogin);
        if (auth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), Home.class));
            finishAffinity();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = Email.getText().toString().trim();
                final String password = Pass.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    Email.requestFocus();
                    Email.setError("Required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Pass.requestFocus();
                    Pass.setError("Required");
                    return;
                }
                auth.signInWithEmailAndPassword(username , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        finishAffinity();
                    }
                });
            }
        });
        CreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });
    }
}