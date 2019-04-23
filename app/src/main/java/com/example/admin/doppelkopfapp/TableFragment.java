package com.example.admin.doppelkopfapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TableFragment extends Fragment {

    private static final boolean reversed = true;

    private OnNextRoundListener onNextRoundListener;
    private Party party;

    private TextView dealerView;

    public TableFragment() {
        // Required empty public constructor
    }

    public static TableFragment newInstance(Party party) {
        TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.ARG_PARTY, party);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            party = (Party) getArguments().getSerializable(MainActivity.ARG_PARTY);
        } else {
            throw new RuntimeException(getString(R.string.error_party_not_set));
        }

        getActivity().setTitle(getString(R.string.table_fragment_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    private String getString(){
       return String.format(getString(R.string.table_info),
                party.getPlayerByDBId(party.getCurrentGame().getDealer()).getName(),
                party.getCurrentGame().getBockSafe(0),
                party.getCurrentGame().getBockSafe(1));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dealerView = view.findViewById(R.id.game_dealer_text);

        dealerView.setText(getString());

        final TableLayout table = view.findViewById(R.id.game_table);

        Button buttonNext = view.findViewById(R.id.table_next_button);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextRoundListener.onNextRound();
            }
        });

        Button buttonDelete = view.findViewById(R.id.table_delete_last_button);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(party.getCurrentGame().getRounds().size() > 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.delete_round_title)
                            .setMessage(R.string.confirmation_delete_round)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    onNextRoundListener.onDeleteLastRound(table, reversed);
                                    dealerView.setText(getString());
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });


        fillTable(table, party.getCurrentGame(), reversed);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNextRoundListener) {
            onNextRoundListener = (OnNextRoundListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onNextRoundListener = null;
    }

    private void fillTable(TableLayout tableLayout, GameManager game, boolean reversed) {
        for(TableRowValue[] rowValue : rowValues(game, reversed)) {
            tableLayout.addView(fillRow(rowValue));
        }
    }

    private TableRow fillRow(TableRowValue[] values) {
        TableRow row = new TableRow(getContext());
        row.setId(ViewCompat.generateViewId());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        for(int i = 0; i < values.length; i++) {
            if(i == 0 || i == values.length-1) {
                row.addView(createExtraTextView(values[i]));
            } else {
                row.addView(createTextView(values[i]));
            }
        }
        return row;
    }

    private List<TableRowValue[]> rowValues(GameManager gameManager, boolean reversed){
        List<Player> players = gameManager.getPlayers();

        List<TableRowValue[]> values = new ArrayList<>();
        Map<Long, Integer> playerPoints = new HashMap<>();

        TableRowValue[] headerRow = headerRow(players);

        for(int num = 0; num < gameManager.getRounds().size(); num++) {
            GameRound round = gameManager.getRounds().get(num);

            TableRowValue[] row = new TableRowValue[headerRow.length];
            row[0] = new TableRowValue(String.valueOf(num+1));

            for(int i = 0; i < players.size(); i++) {
                long id = players.get(i).getDataBaseId();

                int rPoints = round.getPlayerPoints().get(id) == null ? 0 : round.getPlayerPoints().get(id);
                int pPoints = playerPoints.get(id) == null ? 0 : playerPoints.get(id);

                playerPoints.put(id, rPoints + pPoints);

                TableRowValue.Appearance appearance;
                if(!round.getPlayerPoints().keySet().contains(gameManager.getPlayersDataBaseIds()[i])) {
                    appearance = TableRowValue.Appearance.INACTIVE;
                } else if(rPoints > 0 ) {
                    appearance = TableRowValue.Appearance.WINNER;
                } else if(rPoints < 0) {
                    appearance = TableRowValue.Appearance.LOSER;
                } else{
                    appearance = TableRowValue.Appearance.REGULAR;
                }

                boolean solo = isSolo(round.getPlayerPoints(), gameManager.getPlayersDataBaseIds()[i]);

                row[i+1] = new TableRowValue(String.valueOf(playerPoints.get(id)), appearance, solo);
            }
            row[row.length-1] = new TableRowValue(getBockString(round.getCurrentBocks()));
            values.add(row);
        }

        if(reversed){
            Collections.reverse(values);
        }

        values.add(0, headerRow);

        return values;
    }

    private String getBockString(int bocks) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < bocks;i++) {
            builder.append("X");
        }
        return builder.toString();
    }

    private TableRowValue[] headerRow(List<Player> players) {
        TableRowValue[] header = new TableRowValue[players.size()+2];
        header[0] = new TableRowValue("#");
        for(int i = 0; i < players.size(); i++) {
            header[i+1] = new TableRowValue(players.get(i).getName());
        }
        header[header.length-1] = new TableRowValue("B");
        return header;
    }


    private TextView createTextView(TableRowValue value) {
        TextView tv = new TextView(getContext());
        tv.setId(ViewCompat.generateViewId());
        tv.setText(value.getText());
        tv.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Subhead);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new TableRow.LayoutParams(
                0, TableRow.LayoutParams.MATCH_PARENT, 1));
        tv.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        tv.setPadding(8, 8, 8 ,8);

        if(value.isSolo()) {
            tv.setTypeface(null, Typeface.BOLD);
        }

        if(value.getAppearance() == TableRowValue.Appearance.INACTIVE){
            tv.setTextColor(getResources().getColor(R.color.inActive));
        } else if(value.getAppearance() == TableRowValue.Appearance.WINNER) {
            tv.setTextColor(getResources().getColor(R.color.winner));
        } else if(value.getAppearance() == TableRowValue.Appearance.LOSER) {
            tv.setTextColor(getResources().getColor(R.color.loser));
        }

        return tv;
    }


    private TextView createExtraTextView(TableRowValue val) {
        TextView tv = createTextView(val);
        tv.setLayoutParams(new TableRow.LayoutParams(
                0, TableRow.LayoutParams.MATCH_PARENT, 0.7f));
        return tv;
    }


    private boolean isSolo(Map<Long, Integer> playerPoints, long player) {
        Set<Long> winners = new HashSet<>();
        Set<Long> losers = new HashSet<>();

        for(long id : playerPoints.keySet()) {
            if(playerPoints.get(id) > 0) {
                winners.add(id);
            } else if (playerPoints.get(id) < 0) {
                losers.add(id);
            }
        }

        if(winners.size() == 3) {
            return losers.contains(player);
        } else if(losers.size() == 3) {
            return winners.contains(player);
        }
        return false;
    }


    public interface OnNextRoundListener {
        void onNextRound();
        void onDeleteLastRound(TableLayout tableLayout, boolean reversed);
    }
}
