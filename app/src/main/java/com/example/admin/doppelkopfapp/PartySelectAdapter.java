package com.example.admin.doppelkopfapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class PartySelectAdapter extends RecyclerView.Adapter<PartySelectAdapter.ViewHolder> {

    private String[] dataSet;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(TextView v){
            super(v);
            this.textView = v;
        }
    }

    public PartySelectAdapter(String[] dataSet) {
        this.dataSet = dataSet;
    }

    public PartySelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.);
    }

}
