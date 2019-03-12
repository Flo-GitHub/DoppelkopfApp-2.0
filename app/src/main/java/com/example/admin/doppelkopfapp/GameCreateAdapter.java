package com.example.admin.doppelkopfapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameCreateAdapter extends RecyclerView.Adapter<GameCreateAdapter.MyViewHolder> {

    private Map<Long, Boolean> playerChecked;
    private List<Player[]> players;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox check1, check2;

        public MyViewHolder(View view) {
            super(view);
            check1 = view.findViewById(R.id.player_check_twin1);
            check2 = view.findViewById(R.id.player_check_twin2);
        }

        public void bindTwin(Player p1, Player p2) {
            check1.setText(p1.getName());
            check1.setOnClickListener(listener(check1, p1));

            if (p2 != null) {
                check2.setText(p2.getName());
                check2.setOnClickListener(listener(check2, p2));
            } else {
                check2.setVisibility(View.INVISIBLE);
                check2.setEnabled(false);
            }
        }

        private View.OnClickListener listener(final CheckBox check, final Player p) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playerChecked.put(p.getDataBaseId(), check.isChecked());
                }
            };
        }
    }

    public GameCreateAdapter(List<Player[]> players) {
        this.players = players;
        playerChecked = new HashMap<>();
        for(Player[] ps : players) {
            for(Player p : ps) {
                if( p != null) {
                    playerChecked.put(p.getDataBaseId(), false);
                }
            }
        }
    }

    @Override
    public GameCreateAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.player_check_twin, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindTwin(players.get(position)[0], players.get(position)[1]);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public Map<Long, Boolean> getPlayerChecked(){
        return playerChecked;
    }
}

