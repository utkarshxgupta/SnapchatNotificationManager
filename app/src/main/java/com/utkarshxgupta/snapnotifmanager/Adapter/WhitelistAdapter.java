package com.utkarshxgupta.snapnotifmanager.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.utkarshxgupta.snapnotifmanager.AddNewPerson;
import com.utkarshxgupta.snapnotifmanager.MainActivity;
import com.utkarshxgupta.snapnotifmanager.Model.WhitelistModel;
import com.utkarshxgupta.snapnotifmanager.R;
import com.utkarshxgupta.snapnotifmanager.Utils.DatabaseHandler;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class WhitelistAdapter extends RecyclerView.Adapter<WhitelistAdapter.ViewHolder> {

    private List<WhitelistModel> whiteList;
    private MainActivity activity;
    private DatabaseHandler db;

    public WhitelistAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.whitelist_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        WhitelistModel item = whiteList.get(position);
        holder.task.setText(item.getTask());
    }

    public int getItemCount() {
        return whiteList.size();
    }

    public void setTasks(List<WhitelistModel> whiteList) {
        this.whiteList = whiteList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return activity;
    }

    public void deleteItem(int position) {
        WhitelistModel item = whiteList.get(position);
        db.deletePerson(item.getId());
        whiteList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        WhitelistModel item = whiteList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewPerson fragment = new AddNewPerson();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewPerson.TAG);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView task;
        MaterialCardView card;
        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            card = view.findViewById(R.id.list_cardView);
            card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(view.getContext(), "Swipe Left to Delete and Right to Edit item", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }
}
