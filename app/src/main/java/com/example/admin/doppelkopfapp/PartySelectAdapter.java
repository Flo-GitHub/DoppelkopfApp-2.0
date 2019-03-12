package com.example.admin.doppelkopfapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PartySelectAdapter extends RecyclerView.Adapter<PartySelectAdapter.MyViewHolder> {

    private PartySelectFragment.OnPartySelectListener listener;
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
                        listener.onPartySelected(pos);
                    }
                }
            });
        }
        public void bindParty(Party party) {
            group.setText(party.getName());
            players.setText(party.getPlayersAsString());
            date.setText(party.getLastDate());
            if(party.getImage() != null) {
                image.setImageBitmap(party.getImage());
            }

        }
    }

    public PartySelectAdapter(PartySelectFragment.OnPartySelectListener listener, PartyManager partyManager) {
        this.listener = listener;
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
        holder.bindParty(partyList.get(position));
    }

    @Override
    public int getItemCount() {
        return partyList.size();
    }


}
