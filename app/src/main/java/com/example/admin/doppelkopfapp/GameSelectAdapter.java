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

    private GameSelectActivity gameSelectActivity;
    private List<GameManager> gameList;
    private Party party;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, players, gamesPlayed;
        public ImageView image;

        public MyViewHolder(View view){
            super(view);
            date = view.findViewById(R.id.game_card_date);
            players = view.findViewById(R.id.game_card_players);
            gamesPlayed = view.findViewById(R.id.game_card_games_played);
            image = view.findViewById(R.id.game_card_date);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("viewhold", ""+ (getItemId()));//todo
                }
            });
        }
    }

    public GameSelectAdapter(GameSelectActivity act, Party party) {
        this.gameSelectActivity = act;
        this.gameList = party.getGames();
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
        GameManager game = gameList.get(position);
        holder.date.setText(game.getLastDate());//todo change dates
        holder.players.setText(game.getPlayersAsString());
        holder.date.setText(MyUtils.getDate());
        //todo set image to the groups image??
        //todo maybe add delete?
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

}
