package com.example.admin.doppelkopfapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GameSelectAdapter extends RecyclerView.Adapter<GameSelectAdapter.MyViewHolder> {

    private GameSelectFragment.OnGameSelectListener gameSelectListener;
    private Party party;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, players, gamesPlayed;
        public ImageView image;
        public ImageButton overflow;

        public MyViewHolder(View view){
            super(view);
            date = view.findViewById(R.id.game_card_date);
            players = view.findViewById(R.id.game_card_players);
            gamesPlayed = view.findViewById(R.id.game_card_games_played);
            image = view.findViewById(R.id.game_card_image);
            overflow = view.findViewById(R.id.game_card_overflow);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        gameSelectListener.onGameSelected(pos);
                    }
                }
            });
        }

        public void bindGame(GameManager game) {
            players.setText(game.getPlayersAsString());
            gamesPlayed.setText(game.getRounds().size() + MainActivity.getContext().getString(R.string.rounds_played));
            date.setText(MyUtils.getDisplayDate(game.getLastDate()));
            try {
                image.setImageBitmap(game.getImage());
            } catch (Exception ignore) {}
        }
    }

    public GameSelectAdapter(GameSelectFragment.OnGameSelectListener listener, Party party) {
        this.gameSelectListener = listener;
        this.party = party;
    }

    private void showPopupMenu(View view, final int position) {
        PopupMenu popup = new PopupMenu(MainActivity.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_card, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Context c = MainActivity.getContext();
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        new AlertDialog.Builder(c)
                                .setTitle(R.string.delete_game_title)
                                .setMessage(R.string.confirmation_delete_game)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        gameSelectListener.onGameDeleted(position, GameSelectAdapter.this);
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                        return true;
                    case R.id.action_edit:
                        gameSelectListener.onGameEdited(position);
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public GameSelectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.game_card, parent, false);
        return new GameSelectAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameSelectAdapter.MyViewHolder holder, final int position) {
        GameManager game = party.getGames().get(position);
        party.setCurrentGame(position);
        holder.bindGame(game);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return party.getGames().size();
    }

}
