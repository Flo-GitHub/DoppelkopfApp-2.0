package com.example.admin.doppelkopfapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PartySelectAdapter extends RecyclerView.Adapter<PartySelectAdapter.MyViewHolder> {

    private PartySelectFragment.OnPartySelectListener listener;
    private List<Party> partyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView group, players, date;
        public ImageView image;
        public ImageButton overflow;

        public MyViewHolder(View view){
            super(view);
            group = view.findViewById(R.id.game_card_date);
            players = view.findViewById(R.id.game_card_players);
            date = view.findViewById(R.id.party_card_date);
            image = view.findViewById(R.id.game_card_image);
            overflow = view.findViewById(R.id.party_card_overflow);

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
            date.setText(MyUtils.getDisplayDate(party.getLastDate()));
            if(party.getImageBytes() != null) {
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.bindParty(partyList.get(position));

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    private void showPopupMenu(View view, final int position) {
        // inflate menu
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
                                .setTitle(R.string.delete_group_title)
                                .setMessage(R.string.confirmation_delete_group)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        listener.onPartyDeleted(position, PartySelectAdapter.this);
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                        return true;
                    case R.id.action_edit:
                        listener.onPartyEdited(position);
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return partyList.size();
    }


}
