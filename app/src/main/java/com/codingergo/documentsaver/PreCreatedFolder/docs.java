package com.codingergo.documentsaver.PreCreatedFolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codingergo.documentsaver.FolderManager.FileListViewer;
import com.codingergo.documentsaver.FolderManager.FilesListViewerModel;
import com.codingergo.documentsaver.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class docs extends AppCompatActivity {
    FloatingActionButton f;
    FirebaseStorage firebaseStorage;
    StorageReference reference;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    Uri path;
    EditText name;
    Dialog dialog;
    FileListViewer fileListViewer ;
    String mimeType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs);
        firebaseStorage = FirebaseStorage.getInstance();
        reference = firebaseStorage.getReference().child("docs");
        auth = FirebaseAuth.getInstance();
        firestore  = FirebaseFirestore.getInstance();
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        RecyclerViews();
        f = findViewById(R.id.fabmusic);
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(docs.this);
                dialog.setContentView(R.layout.fileboxdialog);
                dialog.show();
                name = dialog.findViewById(R.id.fileName);
                Button submit = dialog.findViewById(R.id.ButtonSelect);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        startActivityForResult(Intent.createChooser(intent, "Choose"), 0);
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode== RESULT_OK && data!=null &&data.getData() !=null){
            path = data.getData();
            ContentResolver contentResolver = getContentResolver() ;
            MimeTypeMap mm = MimeTypeMap.getSingleton();
            mimeType =  mm.getExtensionFromMimeType(contentResolver.getType(path));
            Log.d("TAG", "onActivityResult: "+mimeType);
            StartUpload();
        }
        else {
            Toast.makeText(this, "File Not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void StartUpload() {
        final  String names = name.getText().toString();
        if (TextUtils.isEmpty(names)){
            name.requestFocus();
            name.setError("required");
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        StorageReference rf = reference.child(names+System.currentTimeMillis());
        rf.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri uri = uriTask.getResult();
                DocumentReference df = firestore.collection("User").document(auth.getCurrentUser().getUid()).collection("Docs").document();
                Map<String , Object> map = new HashMap<>();
                map.put("name", names +"."+mimeType);
                map.put("id",df.getId());
                map.put("url", uri.toString());
                df.set(map);
                dialog.dismiss();
                progressDialog.dismiss();
                Toast.makeText(docs.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(docs.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void RecyclerViews() {
        RecyclerView recyclerView = findViewById(R.id.folderRec);
        recyclerView.setLayoutManager(new GridLayoutManager(this , 4));

        Query query = firestore.collection("User").document(auth.getCurrentUser().getUid()).collection("Docs");
        FirestoreRecyclerOptions<FilesListViewerModel> options = new FirestoreRecyclerOptions.Builder<FilesListViewerModel>()
                .setQuery(query , FilesListViewerModel.class).build();
        fileListViewer = new FileListViewer(options);
        recyclerView.setAdapter(fileListViewer);

    }

    @Override
    protected void onStop() {
        super.onStop();
        fileListViewer.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fileListViewer.startListening();
    }

}