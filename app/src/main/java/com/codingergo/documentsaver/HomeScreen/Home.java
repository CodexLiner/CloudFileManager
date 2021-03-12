package com.codingergo.documentsaver.HomeScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.codingergo.documentsaver.BuildConfig;
import com.codingergo.documentsaver.PreCreatedFolder.docs;
import com.codingergo.documentsaver.PreCreatedFolder.music;
import com.codingergo.documentsaver.PreCreatedFolder.photo;
import com.codingergo.documentsaver.R;
import com.codingergo.documentsaver.UserSignOptions.MainScreen;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.BreakIterator;
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
ImageView profile;
DrawerLayout drawerLayout;
NavigationView navigationView;
ActionBarDrawerToggle toggle;
Toolbar toolbar ;
SharedPreferences sharedPreferences;
SharedPreferences.Editor editor ;
String url ;
    long backbutton;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerview);
        profile = findViewById(R.id.profilleUser);
        new_Folder = findViewById(R.id.new_Folder);
        docs = findViewById(R.id.Document);
        music = findViewById(R.id.Music);
        photo = findViewById(R.id.Photos);
        auth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.Toolbardrawer);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("HomeShare" ,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        navigationView = findViewById(R.id.menudrawer);
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
           Recyclerviews();
           CreateFolder();
           PreCreatedFolder();
           UserDetails();
           NavigationViewSetUp();
        Glide.with(profile).load(sharedPreferences.getString("url","")).placeholder(R.drawable.blank).into(profile);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), MainScreen.class));
                finish();
            }
        });
    }

    private void NavigationViewSetUp() {
        toggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar , R.string.open , R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home3 :{
                        Toast.makeText(Home.this, "home3", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    }
                    case R.id.home2: {
                        Toast.makeText(Home.this, "home2", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    }
                    case  R.id.home :{
                        Toast.makeText(Home.this, "Home1", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    }
                    case R.id.power :{
                        auth.signOut();
                        startActivity(new Intent(getApplicationContext(),MainScreen.class));
                        finish();
                    }
                    case R.id.ShareApp :{
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, "Download This App To Save Your Documents To Cloud Storage "+"http://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID);
                        startActivity(intent);
                    }
                }
                return true;
            }
        });
    }

    private void UserDetails() {
        DocumentReference df = firestore.collection("User").document(auth.getCurrentUser().getUid());
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               editor.putString("url", documentSnapshot.getString("url"));
               editor.commit();
                Glide.with(profile).load(documentSnapshot.getString("url")).placeholder(R.drawable.blank).into(profile);
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
