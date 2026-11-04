package com.example.myproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhonicsAdapter extends RecyclerView.Adapter<PhonicsAdapter.VH> {

    List<PhonicsModel> list;

    public PhonicsAdapter(List<PhonicsModel> list) {
        this.list = list;
    }

    public static class VH extends RecyclerView.ViewHolder {

        TextView letter, word;

        public VH(View v) {
            super(v);
            letter = v.findViewById(R.id.tvLetter);
            word = v.findViewById(R.id.tvWord);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_phonics, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        PhonicsModel m = list.get(i);

        h.letter.setText(m.letter);
        h.word.setText(m.word);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
