package com.example.admin.doppelkopfapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

public class PartySelectAdapter extends RecyclerView.Adapter<PartySelectAdapter.MyViewHolder> {

    private PartySelectActivity partySelectActivity;
    private List<Party> partyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView group, players, date;
        public ImageView image;

        public MyViewHolder(View view){
            super(view);
            group = view.findViewById(R.id.game_card_date);
            players = view.findViewById(R.id.game_card_players);
            date = view.findViewById(R.id.party_card_date);
            image = view.findViewById(R.id.game_card_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION) {
                        Log.e("viewholder", ""+ (pos));//todo

                        Intent intent = new Intent(partySelectActivity, GameSelectActivity.class);
                        intent.putExtra(INTENT_EXTRAS.PARTY_INDEX, pos);
                        partySelectActivity.startActivity(intent);
                    }

                }
            });
        }
    }

    public PartySelectAdapter(PartySelectActivity act, PartyManager partyManager) {
        this.partySelectActivity = act;
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
        holder.date.setText(MyUtils.getDate());
        //todo set image to the groups image
        //todo maybe add delete?
    }

    @Override
    public int getItemCount() {
        return partyList.size();
    }


}
