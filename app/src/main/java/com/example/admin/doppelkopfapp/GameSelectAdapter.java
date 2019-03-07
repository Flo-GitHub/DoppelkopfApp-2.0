package com.example.admin.doppelkopfapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class GameSelectAdapter extends RecyclerView.Adapter<GameSelectAdapter.MyViewHolder> {


    private GameSelectFragment.OnGameSelectListener gameSelectListener;
    private Party party;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, players, gamesPlayed;
        public ImageView image;

        public MyViewHolder(View view){
            super(view);
            date = view.findViewById(R.id.game_card_date);
            players = view.findViewById(R.id.game_card_players);
            gamesPlayed = view.findViewById(R.id.game_card_games_played);
            image = view.findViewById(R.id.game_card_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION) {
                        Log.e("viewholder", ""+ (pos));//todo
                        gameSelectListener.onGameSelect(pos);
                    }
                }
            });
        }
        public void bindGame(GameManager game) {
            date.setText(game.getLastDate());
            players.setText(game.getPlayersAsString());
            date.setText(MyUtils.getDate());
        }
    }

    public GameSelectAdapter(GameSelectFragment.OnGameSelectListener listener, Party party) {
        this.gameSelectListener = listener;
        this.party = party;
    }

    @Override
    public GameSelectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.game_card, parent, false);
        return new GameSelectAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameSelectAdapter.MyViewHolder holder, int position) {
        GameManager game = party.getGames().get(position);
        party.setCurrentGame(position);
        holder.bindGame(game);
        //todo set image to the groups image??
        //todo maybe add delete?
    }

    @Override
    public int getItemCount() {
        return party.getGames().size();
    }

}
