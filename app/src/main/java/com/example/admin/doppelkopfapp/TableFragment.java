package com.example.admin.doppelkopfapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TableFragment extends Fragment {

    //private OnSettingsChangeListener mListener;
    private Party party;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView dealerView = view.findViewById(R.id.game_dealer_text);

        String t = String.format(getString(R.string.table_info),
                party.getPlayerByDBId(party.getCurrentGame().getGiver()).getName(),
                party.getCurrentGame().getBockSafe(0),
                party.getCurrentGame().getBockSafe(1));


        dealerView.setText(String.format(t));
        fillTable(getView(), party.getCurrentGame());
    }

    /*    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsChangeListener) {
            mListener = (OnSettingsChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsChangeListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    private void fillTable(View view, GameManager game) {
        TableLayout table = view.findViewById(R.id.game_table);
        for(TableRowValue[] rowValue : rowValues(game)) {
            table.addView(fillRow(rowValue));
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

    private List<TableRowValue[]> rowValues(GameManager gameManager){
        List<Player> players = gameManager.getPlayers();

        List<TableRowValue[]> values = new ArrayList<>();
        values.add(headerRow(players));

        Map<Long, Integer> playerPoints = new HashMap<>();

        for(int num = 0; num < gameManager.getRounds().size(); num++) {
            GameRound round = gameManager.getRounds().get(num);

            TableRowValue[] row = new TableRowValue[values.get(0).length];
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnSettingsChangeListener {
        void onSettingsSaved(Uri uri);
    }*/
}
