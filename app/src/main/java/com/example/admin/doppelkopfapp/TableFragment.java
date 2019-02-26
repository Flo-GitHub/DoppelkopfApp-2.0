package com.example.admin.doppelkopfapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.time.chrono.MinguoChronology;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TableFragment extends Fragment {

    //private OnFragmentInteractionListener mListener;
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
            throw new RuntimeException("NO PARTY SET - NEW GAME NOT AVAILABLE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillTable(getView(), party.getGames().get(0));
    }

    /*    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    private void fillTable(View view, GameManager game) {
        TableLayout table = view.findViewById(R.id.game_table);
        for(String[] rowValue : rowValues(game)) {
            table.addView(fillRow(rowValue));
        }
    }

    private TableRow fillRow(String[] values) {
        TableRow row = new TableRow(getContext());
        row.setId(ViewCompat.generateViewId());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        for(String value : values) {
            row.addView(createTextView(value));
        }
        return row;
    }

    private List<String[]> rowValues(GameManager gameManager){
        List<Player> players = gameManager.getPlayers();

        List<String[]> values = new ArrayList<>();
        values.add(headerRow(players));

        Map<Long, Integer> playerPoints = new HashMap<>();

        for(int num = 0; num < gameManager.getRounds().size(); num++) {
            GameRound round = gameManager.getRounds().get(num);

            String[] row = new String[values.get(0).length];
            row[0] = String.valueOf(num+1);

            for(int i = 0; i < players.size(); i++) {
                long id = players.get(i).getDataBaseId();
                try {
                    playerPoints.put(id, (playerPoints.get(id) + round.getPlayerPoints().get(id)) );
                } catch (Exception e) {
                    try {
                        playerPoints.put(id, round.getPlayerPoints().get(id));
                    } catch (Exception a){
                        playerPoints.put(id, 0);
                    }
                }

                row[i+1] = Integer.toString(playerPoints.get(id));
            }
            row[row.length-1] = getBockString(round.getCurrentBocks());
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

    private String[] headerRow(List<Player> players) {
        String[] header = new String[players.size()+2];
        header[0] = "#";
        for(int i = 0; i < players.size(); i++) {
            header[i+1] = players.get(i).getName();
        }
        header[header.length-1] = "B";
        return header;
    }

    private TextView createTextView(String text) {
        TextView tv = new TextView(getContext());
        tv.setId(ViewCompat.generateViewId());
        tv.setText(text);
        tv.setSingleLine(true);
        tv.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Subhead);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        tv.setPadding(8, 8, 8 ,8);
        return tv;
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
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
