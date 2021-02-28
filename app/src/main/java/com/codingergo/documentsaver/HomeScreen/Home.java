package com.codingergo.documentsaver.HomeScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codingergo.documentsaver.PreCreatedFolder.docs;
import com.codingergo.documentsaver.PreCreatedFolder.music;
import com.codingergo.documentsaver.PreCreatedFolder.photo;
import com.codingergo.documentsaver.R;
import com.codingergo.documentsaver.UserSignOptions.MainScreen;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
TextView textView ;
FirebaseAuth auth;
FirebaseUser user;
RecyclerView recyclerView;
FirebaseFirestore firestore;
HomeScreenFoldeerAdapter folderAdapter;
ImageView new_Folder , music , photo , docs ;
    long backbutton;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerview);
        new_Folder = findViewById(R.id.new_Folder);
        docs = findViewById(R.id.Document);
        music = findViewById(R.id.Music);
        photo = findViewById(R.id.Photos);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
       Recyclerviews();
       CreateFolder();
       PreCreatedFolder();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), MainScreen.class));
                finish();
            }
        });
    }

    private void PreCreatedFolder() {
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), com.codingergo.documentsaver.PreCreatedFolder.photo.class));
            }
        });
        docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), com.codingergo.documentsaver.PreCreatedFolder.docs.class));
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), com.codingergo.documentsaver.PreCreatedFolder.music.class));
            }
        });
    }

    private void CreateFolder() {
        new_Folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(Home.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialogbox);
                dialog.setCancelable(false);
                dialog.show();
                Button create = dialog.findViewById(R.id.CreateButtonBox);
                Button cancel = dialog.findViewById(R.id.CancelButton);
                EditText FolderName= dialog.findViewById(R.id.FolderNameBox);
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final  String name = FolderName.getText().toString().trim();
                        if (TextUtils.isEmpty(name)){
                            FolderName.requestFocus();
                            FolderName.setError("Required");
                            return;
                        }

                        // Creating A Folder In Cloud
                        DocumentReference df = firestore.collection("User")
                                .document(auth.getCurrentUser().getUid())
                                  .collection("Folders").document();
                        // Genrating Random Id For Future Uses

                        Map<String , Object> folderMap = new HashMap<>();
                        folderMap.put("name", name);
                        folderMap.put("id", df.getId());
                        df.set(folderMap);
                        dialog.dismiss();
                        Toast.makeText(Home.this, "Folder Created", Toast.LENGTH_SHORT).show();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });



            }
        });
    }

    private void Recyclerviews() {
        recyclerView.setLayoutManager(new GridLayoutManager(this , 4));
        Query query = firestore.collection("User").document(user.getUid()).collection("Folders");
        FirestoreRecyclerOptions<ModelFolder> model = new FirestoreRecyclerOptions.Builder<ModelFolder>()
                .setQuery(query , ModelFolder.class).build();
        folderAdapter = new HomeScreenFoldeerAdapter(model);
        recyclerView.setAdapter(folderAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
       folderAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        folderAdapter.startListening();
    }

    @Override
    public void onBackPressed() {

        if (backbutton + 2000 > System.currentTimeMillis()) {
            Log.d("CDA", "onBackPressed Called");
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
            toast.cancel();
            super.onBackPressed();
        } else {
            toast = Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            toast.show();
        }

        backbutton = System.currentTimeMillis();

    }
}
