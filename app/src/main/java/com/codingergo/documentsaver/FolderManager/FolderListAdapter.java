package com.codingergo.documentsaver.FolderManager;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingergo.documentsaver.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FolderListAdapter extends FirestoreRecyclerAdapter<FolderListModel, FolderListAdapter.Holder> {

    public FolderListAdapter(@NonNull FirestoreRecyclerOptions<FolderListModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull FolderListModel model) {
        holder.name.setText(model.getName());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(holder.linearLayout.getContext(), "wow", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(holder.linearLayout.getContext() , FolderFileViewer.class);
                intent.putExtra("name" , model.getName());
                intent.putExtra("id", model.getId());
                intent.putExtra("url" , model.getUrl());
                holder.linearLayout.getContext().startActivity(intent);

            }
        });

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_line,parent ,false);
        return new Holder(view);
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView name;
        LinearLayout linearLayout;

        public Holder(@NonNull View itemView) {
            super(itemView);
           name = itemView.findViewById(R.id.textName);
           linearLayout = itemView.findViewById(R.id.relativelayout);
        }
    }
}
