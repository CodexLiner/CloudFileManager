package com.codingergo.documentsaver.HomeScreen;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingergo.documentsaver.FolderManager.FolderFileViewer;
import com.codingergo.documentsaver.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class HomeScreenFoldeerAdapter extends FirestoreRecyclerAdapter<ModelFolder , HomeScreenFoldeerAdapter.Holder> {

    public HomeScreenFoldeerAdapter(@NonNull FirestoreRecyclerOptions<ModelFolder> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull ModelFolder model) {
        holder.name.setText(model.getName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext() , FolderFileViewer.class);
                intent.putExtra("name", model.getName());
                intent.putExtra("id" , model.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(holder.itemView.getContext());
                dialog.setContentView(R.layout.long_dialog);
                Toast.makeText(holder.name.getContext(), "Hellow Long", Toast.LENGTH_SHORT).show();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.itemView.getContext(),R.style.BottomSheet);
                bottomSheetDialog.setContentView(R.layout.long_dialog );
                bottomSheetDialog.show();

                return true;
            }
        });

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_line , parent , false);

        return new Holder(v);
    }

    class  Holder extends RecyclerView.ViewHolder {
        TextView name ;
        LinearLayout relativeLayout ;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            relativeLayout  = itemView.findViewById(R.id.relativelayout);

        }
    }
}
