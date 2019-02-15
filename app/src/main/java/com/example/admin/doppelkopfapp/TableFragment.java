package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class TableFragment extends Fragment {

    //private OnFragmentInteractionListener mListener;
    private Party party;

    public TableFragment() {
        // Required empty public constructor
    }

    public static TableFragment newInstance() {
        TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.party = MyUtils.sampleParty();
        if (getArguments() != null) {
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
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        for(String value : values) {
            row.addView(createTextView(value));
        }
        return row;
    }

    private List<String[]> rowValues(GameManager gameManager){
        List<Player> players = gameManager.getPlayers();

        List<String[]> values = new ArrayList<>();
        values.add(headerRow(players));


        for(GameRound round : gameManager.getRounds()) {
            String[] row = new String[players.size()];
            for(int i = 0; i < players.size(); i++) {
                row[i] = Integer.toString(round.getPlayerPoints().get(players.get(i).getDataBaseId()));
            }
            values.add(row);
        }
        return values;
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
        tv.setPadding(5, 5, 5, 5);
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
