package com.codingergo.documentsaver.FolderManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class FragmentClass extends AppCompatActivity {
String name , id;
TextView textView  ,lastName;
FirebaseFirestore firestore ;
FirebaseAuth auth;
Uri path;
FirebaseStorage firebaseStorage;
StorageReference reference;
FolderListAdapter folderInFolderAdapter;
FileListAdapter filesRecAdapter ;
EditText NewName;
String mimeType;
RecyclerView ForFolderRec , ForFileRec;
FloatingActionButton floatingActionButton ,Forfile , Forfolder , hide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_data);
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
        lastName = findViewById(R.id.lastName);
        floatingActionButton = findViewById(R.id.fab);
        firebaseStorage = FirebaseStorage.getInstance();
        ForFolderRec = findViewById(R.id.folderRec);
        ForFileRec = findViewById(R.id.FilesRec);
        reference = firebaseStorage.getReference();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        Forfile = findViewById(R.id.ForFile);
        hide = findViewById(R.id.HideFab);
        Forfolder= findViewById(R.id.Forfolder);
        Forfolder.setVisibility(View.GONE);
        Forfile.setVisibility(View.GONE);
        hide.setVisibility(View.GONE);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
       RecViews();
       lastNameAction();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide.setVisibility(View.VISIBLE);
                Forfolder.setVisibility(View.VISIBLE);
                Forfile.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.GONE);
                Forfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = new Dialog(FragmentClass.this);
                        dialog.setContentView(R.layout.fileboxdialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        Button select = dialog.findViewById(R.id.ButtonSelect);
                        EditText name = dialog.findViewById(R.id.fileName);
                        name.requestFocus();
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        NewName = name;
                        dialog.show();
                        select.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UploadFile();
                            }
                        });
//                        UploadFile();

                    }
                });
                Forfolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // CreateFolder();
                        Dialog dialog = new Dialog(FragmentClass.this);
                        dialog.setContentView(R.layout.dialogbox);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        dialog.show();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        Button create = dialog.findViewById(R.id.CreateButtonBox);
                        Button CancelButton = dialog.findViewById(R.id.CancelButton);
                        EditText Foldername = dialog.findViewById(R.id.FolderNameBox);
                        Foldername.requestFocus();

                        CancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        create.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final  String name = Foldername.getText().toString().trim();
                                if (TextUtils.isEmpty(name)){
                                    Foldername.setError("Required");
                                    return;
                                }
                                // For Folder
                                DocumentReference df = firestore.collection("User")
                                        .document(auth.getCurrentUser()
                                                .getUid()).collection("Folders")
                                        .document(id).collection("folder").document();

                                Map<String ,Object> FolderMap = new HashMap<>();
                                FolderMap.put("name",name);
                                FolderMap.put("id",df.getId());
                                df.set(FolderMap);
                                Toast.makeText(FragmentClass.this, "Created", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                    }
                });
            }
        });
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.setVisibility(View.VISIBLE);
                Forfolder.setVisibility(View.GONE);
                Forfile.setVisibility(View.GONE);
                hide.setVisibility(View.GONE);
            }
        });
       // textView = findViewById(R.id.textView2);

    }

    private void lastNameAction() {
        lastName.setText(name);
        lastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void RecViews() {
        //folderRec
        ForFolderRec.setLayoutManager(new GridLayoutManager(this,4));
        Query query = firestore.collection("User")
                .document(auth.getCurrentUser().getUid())

                .collection("Folders").document(id).collection("folder");
        FirestoreRecyclerOptions<FolderListModel> options = new FirestoreRecyclerOptions.Builder<FolderListModel>()
                .setQuery(query , FolderListModel.class)
                .build();
        folderInFolderAdapter = new FolderListAdapter(options);
        ForFolderRec.setAdapter(folderInFolderAdapter);
        //FilesREc
        ForFileRec.setLayoutManager(new GridLayoutManager(this ,4));

        Query query2 = firestore.collection("User")
                .document(auth.getCurrentUser().getUid())
                .collection("Folders").document(id).collection("Files");
        FirestoreRecyclerOptions<FilesListViewerModel> options2 = new FirestoreRecyclerOptions.Builder<FilesListViewerModel>()
                .setQuery(query2 , FilesListViewerModel.class)
                .build();
        filesRecAdapter = new FileListAdapter(options2);
        ForFileRec.setAdapter(filesRecAdapter);
        Log.d("TAG", "RecViews: "+id);
    }

    private void UploadFile() {

        String [] mime = {"image/*" , "application/pdf","audio/*","application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                // .doc & .docx
                "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation",
                // .ppt & .pptx
                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                // .xls & .xlsx
                "text/plain",
                "application/pdf",
                "application/zip","video/*"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            intent.setType(mime.length==1 ? mime[0]: "*/*");
           if (mime.length > 0) {
               intent.putExtra(Intent.EXTRA_MIME_TYPES , mime);
           }

        }else {
            String newTYPE = "";
            for (String mimty : mime){
                newTYPE += mimty + "|";
            }
            intent.setType(newTYPE.substring(0 , newTYPE.length()-1));
        }

//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("|image/*|application/pdf|audio/*");
        startActivityForResult(Intent.createChooser(intent , "Select Files"),0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 0  && data.getData()!=null && data != null  && resultCode == RESULT_OK){
//            if (requestCode == 1 && resultCode == RESULT_OK && data !=null && data.getData() !=null)
                path = data.getData();
                ContentResolver contentResolver = getContentResolver() ;
                MimeTypeMap mm = MimeTypeMap.getSingleton();
                mimeType =  mm.getExtensionFromMimeType(contentResolver.getType(path));
                UploadFileOnServer();
            }
            else{
                Toast.makeText(this, "Not Selected", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}

    }

    private void UploadFileOnServer() {
        if (path!=null){
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Uploading..");
            progressDialog.show();
            final String name = NewName.getText().toString();
            if (TextUtils.isEmpty(name)){
                Toast.makeText(this, "Name Required", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            StorageReference storageReference = reference.child("files/"+System.currentTimeMillis());
            storageReference.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri url = uriTask.getResult();
                    DocumentReference df = firestore.collection("User")
                            .document(auth.getCurrentUser()
                                    .getUid()).collection("Folders")
                            .document(id).collection("Files").document();
                    Map<String ,Object> file= new HashMap<>();
                    file.put("name",name+"."+mimeType);
                    file.put("url", url.toString());
                    file.put("id", df.getId().toString());
                    Log.d("TAG", "onSuccesskarege: "+"name "+name +" url "+url+" uid "+df.getId().toString());
                    df.set(file).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(FragmentClass.this, "Done Uploading", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FragmentClass.this, "Failed"+e, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

                }
            });
        }

    }

    private void CreateFolder() {
        Dialog dialog = new Dialog(FragmentClass.this);
        dialog.setContentView(R.layout.dialogbox);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button create = dialog.findViewById(R.id.CreateButtonBox);
        Button CancelButton = dialog.findViewById(R.id.CancelButton);
        EditText Foldername = dialog.findViewById(R.id.FolderNameBox);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  String name = Foldername.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    Foldername.setError("Required");
                    return;
                }
                // For Folder
                DocumentReference df = firestore.collection("User")
                        .document(auth.getCurrentUser()
                                .getUid()).collection("Folders")
                        //For Uid
                        .document(id).collection("folder").document();
                String uid = firestore.collection("User")
                        .document(auth.getCurrentUser()
                                .getUid()).collection("Folders")
                        .document(id).collection("folder").document().getId();
                Map<String ,Object> FolderMap = new HashMap<>();
                FolderMap.put("name",name);
                FolderMap.put("id",df.getId());
                df.set(FolderMap);
                Toast.makeText(FragmentClass.this, "Created", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        folderInFolderAdapter.startListening();
        filesRecAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        folderInFolderAdapter.stopListening();
        filesRecAdapter.stopListening();
    }
}