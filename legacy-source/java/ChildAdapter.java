package com.example.myproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.VH> {

    public interface OnChildClick {
        void onClick(Child child);
    }

    Context context;
    List<Child> list;
    OnChildClick listener;

    public ChildAdapter(Context context, List<Child> list, OnChildClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {

        TextView name, age;

        public VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            age = itemView.findViewById(R.id.tvAge);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        Child child = list.get(position);

        holder.name.setText(child.name);
        holder.age.setText("Age: " + child.age);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(child);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}