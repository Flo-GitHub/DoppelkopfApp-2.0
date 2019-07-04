package com.example.admin.doppelkopfapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SeatingAdapter extends RecyclerView.Adapter<SeatingAdapter.MyViewHolder> {

    private Party party;
    private List<Long> dataBaseIds;
    private OnStartDragListener startDragListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView numberView, nameView;
        public ImageView handleView;

        public MyViewHolder(View view) {
            super(view);
            numberView = view.findViewById(R.id.player_seat_number);
            nameView = view.findViewById(R.id.player_seat_name);
            handleView = view.findViewById(R.id.player_seat_handles);
        }

        public void bindPlayer(Player p, int pos) {
            numberView.setText(String.valueOf(pos+1));
            nameView.setText(p.getName());
            handleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        startDragListener.onStartDrag(MyViewHolder.this);
                    }
                    return false;
                }
            });
        }
    }

    public SeatingAdapter(Party party, List<Long> dataBaseIds, OnStartDragListener startDragListener) {
        this.party = party;
        this.dataBaseIds = dataBaseIds;
        this.startDragListener = startDragListener;

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
        myViewHolder.bindPlayer(party.getPlayerByDBId(dataBaseIds.get(i)), i);
    }

    @Override
    public int getItemCount() {
        return dataBaseIds.size();
    }

}
