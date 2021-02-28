package com.codingergo.documentsaver.UserSignOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codingergo.documentsaver.HomeScreen.Home;
import com.codingergo.documentsaver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
Button SignUp;
EditText name , Email , password;
FirebaseAuth auth;
FirebaseFirestore firestore;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        SignUp = findViewById(R.id.ButtonSignUp);
        name = findViewById(R.id.NameAc);
        firestore = FirebaseFirestore.getInstance();
        password = findViewById(R.id.Password);
        Email = findViewById(R.id.NameEmail);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating Account....");

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Username = Email.getText().toString().trim();
                final String Password = password.getText().toString().trim();
                final  String Name = name.getText().toString().trim();
                if (TextUtils.isEmpty(Username)){
                    Email.setError("Email Required");
                    Email.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(Password)) {
                    password.requestFocus();
                    password.setError("Reqirede");
                    return;
                }
                if (TextUtils.isEmpty(Name)){
                    name.requestFocus();
                    name.setError("Reqirede");
                    return;
                }
                progressDialog.show();
                auth.createUserWithEmailAndPassword(Username , Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = auth.getCurrentUser();
                        DocumentReference df = firestore.collection("User").document(user.getUid());
                        Map<String , Object> map = new HashMap<>();
                        map.put("name" ,Name );
                        map.put("email", Username);
                        map.put("url"," ");
                        map.put("uid", auth.getCurrentUser().getUid());
                        df.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                DocumentReference df = firestore.collection("User").document(auth.getCurrentUser().getUid())
                                        .collection("Folders").document();
                               Map<String , Object > d = new HashMap<>();
                               d.put("name" ,"My Folder");
                               d.put("id", df.getId());
                               df.set(d);
                            }
                        });
                        Log.d("TAG", "onSuccess: "+auth.getCurrentUser().getUid());
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, "Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        finishAffinity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, "Faild", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}