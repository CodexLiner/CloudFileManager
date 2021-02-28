package com.codingergo.documentsaver.FolderManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codingergo.documentsaver.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FileListViewer extends FirestoreRecyclerAdapter<FilesListViewerModel, FileListViewer.Holder> {

    public FileListViewer(@NonNull FirestoreRecyclerOptions<FilesListViewerModel> options) {
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
