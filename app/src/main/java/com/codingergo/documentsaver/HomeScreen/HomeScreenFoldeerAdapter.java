package com.codingergo.documentsaver.HomeScreen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingergo.documentsaver.FolderManager.FragmentClass;
import com.codingergo.documentsaver.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.Map;

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
                Intent intent = new Intent(holder.itemView.getContext() , FragmentClass.class);
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
//                Toast.makeText(holder.name.getContext(), "Hellow Long", Toast.LENGTH_SHORT).show();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.itemView.getContext(),R.style.BottomSheet);
                bottomSheetDialog.setContentView(R.layout.long_dialog );
                LinearLayout Share , Download , Rename ,Delete ;
                Share = bottomSheetDialog.findViewById(R.id.ShareLong);
                Download= bottomSheetDialog.findViewById(R.id.DownloadLong);
                Rename = bottomSheetDialog.findViewById(R.id.RenameLong);
                Delete = bottomSheetDialog.findViewById(R.id.DeleteLong);
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
