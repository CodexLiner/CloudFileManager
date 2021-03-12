package com.codingergo.documentsaver.FolderManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codingergo.documentsaver.MyDownloadManager.MyDownloadManager;
import com.codingergo.documentsaver.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

public class FileListAdapter extends FirestoreRecyclerAdapter<FilesListViewerModel, FileListAdapter.Holder> {

    public FileListAdapter(@NonNull FirestoreRecyclerOptions<FilesListViewerModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull FilesListViewerModel model) {
        final String x = MimeTypeMap.getFileExtensionFromUrl(model.getName());
        Log.d("TAG", "onBindViewHolder: "+x);
         holder.name.setText(model.getName());
         if (x.equals("mp3")){
            holder.imageView.setImageResource(R.drawable.mp3);
         }
        if (x.equals("pdf")){
            holder.imageView.setImageResource(R.drawable.pngpdf);
        }
        if (x.equals("mp4")){
            Glide.with(holder.imageView).load(model.getUrl()).into(holder.imageView);
        }
        if (x.equals("jpg")){
            Glide.with(holder.imageView).load(model.getUrl()).into(holder.imageView);
        }
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(holder.itemView.getContext());
                dialog.setContentView(R.layout.long_dialog);
//                Toast.makeText(holder.name.getContext(), "Hellow Long", Toast.LENGTH_SHORT).show();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.itemView.getContext(),R.style.BottomSheet);
                bottomSheetDialog.setContentView(R.layout.long_dialog );
                LinearLayout Share , Download , Rename ,Delete ;
                Share = bottomSheetDialog.findViewById(R.id.ShareLong);
                Download= bottomSheetDialog.findViewById(R.id.DownloadLong);
                Rename = bottomSheetDialog.findViewById(R.id.RenameLong);
                Delete = bottomSheetDialog.findViewById(R.id.DeleteLong);
                Share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT ,"Sharing Files");
                            intent.putExtra(Intent.EXTRA_TEXT, model.getUrl());
                            intent.setType("text/plain");
                            holder.imageView.getContext().startActivity(intent);
                        }catch (Exception e){
                            Toast.makeText(holder.imageView.getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                Download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.imageView.getContext() , MyDownloadManager.class);
                        intent.putExtra("URL", model.getUrl());
                        intent.putExtra("Name", model.name);
                        holder.imageView.getContext().startActivity(intent);
                        Toast.makeText(holder.imageView.getContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                    }
                });
                Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(holder.itemView.getContext()  )
                                .setMessage("Click ok to delete")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try{
                                            getSnapshots().getSnapshot(position).getReference().delete();
                                            bottomSheetDialog.dismiss();
                                        }catch (Exception e){
                                            bottomSheetDialog.dismiss();
                                        }

                                    }
                                }).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                    }
                });
                Rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog rDialog = new Dialog(holder.itemView.getContext());
                        rDialog.setContentView(R.layout.fileboxdialog);
                        EditText name = rDialog.findViewById(R.id.fileName);
                        Button Done = rDialog.findViewById(R.id.ButtonSelect);
                        Done.setText("Done");
                        name.requestFocus();
                        rDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        rDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        rDialog.show();
                        Done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String name2 = name.getText().toString().trim();
                                if (name2.isEmpty()){
                                    name.requestFocus();
                                    name.setError("Required");
                                    return;
                                }
                                Map<String , Object> Map = new HashMap<>();
                                Map.put("name" ,name2);
                                getSnapshots().getSnapshot(position).getReference().update(Map);
                                rDialog.dismiss();
                                bottomSheetDialog.dismiss();

                            }
                        });
                    }
                });
                bottomSheetDialog.show();

                return true;
            }
        });
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filerowdesign,parent,false);
        return new Holder(view);
    }

    class  Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            imageView = itemView.findViewById(R.id.image);

        }
    }
}
