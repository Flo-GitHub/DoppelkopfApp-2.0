package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PartySelectAdapter extends RecyclerView.Adapter<PartySelectAdapter.MyViewHolder> {

    private Context context;
    private List<Party> partyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView group, players, date;
        public ImageView image;

        public MyViewHolder(View view){
            super(view);
            group = view.findViewById(R.id.party_card_group);
            players = view.findViewById(R.id.party_card_players);
            date = view.findViewById(R.id.party_card_date);
            image = view.findViewById(R.id.party_card_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("viewhold", ""+ (getItemId()));//todo
                }
            });
        }
    }

    public PartySelectAdapter(Context c, PartyManager partyManager) {
        this.context = c;
        this.partyList = partyManager.getParties();
    }

    @Override
    public PartySelectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.party_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Party party = partyList.get(position);
        holder.group.setText(party.getName());
        holder.players.setText(party.getPlayersAsString());
        holder.date.setText(AndroidUtils.getDate());
        //todo set image to the groups image
        //todo maybe add delete?
    }

    @Override
    public int getItemCount() {
        return partyList.size();
    }

}
