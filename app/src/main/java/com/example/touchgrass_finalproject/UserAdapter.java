package com.example.touchgrass_finalproject;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class UserAdapter extends RealmRecyclerViewAdapter<User, UserAdapter.ViewHolder> {


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, password;
        ImageView delete,edit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameView);
            password = itemView.findViewById(R.id.passwordView);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }


    UserManagementPage activity;
    public UserAdapter(UserManagementPage activity, @Nullable OrderedRealmCollection<User> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = activity.getLayoutInflater().inflate(R.layout.user_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User u = getItem(position);
        holder.name.setText(u.getName());
        holder.password.setText(u.getPassword());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, RegisterPage.class);
                intent.putExtra("user_id", u.getUuid());
                activity.startActivity(intent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(activity)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                activity.delete(u);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .show();
            }
        });
    }

}
