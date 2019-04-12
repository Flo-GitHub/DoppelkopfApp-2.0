package com.example.admin.doppelkopfapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SeatingAdapter extends RecyclerView.Adapter<SeatingAdapter.MyViewHolder> {

    List<Player> players;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView numberView, nameView;

        public MyViewHolder(View view) {
            super(view);
            numberView = view.findViewById(R.id.player_seat_number);
            nameView = view.findViewById(R.id.player_seat_name);
        }

        public void bindPlayer(Player p, int pos) {
            numberView.setText(String.valueOf(pos+1));
            nameView.setText(p.getName());
        }
    }

    public SeatingAdapter(List<Player> players) {
        this.players = players;
    }

    @NonNull
    @Override
    public SeatingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.player_seat_row, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatingAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.bindPlayer(players.get(i), i);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
