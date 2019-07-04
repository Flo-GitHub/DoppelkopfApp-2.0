package com.example.admin.doppelkopfapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsFragment extends Fragment {
    enum Stats {
        POINTS, ROUNDS, SOLO
    }

    private Party party;
    private TableLayout table;
    private RadioGroup radioGroup;

    public StatsFragment() {
        // Required empty public constructor
    }

    public static StatsFragment newInstance(Party party) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.ARG_PARTY, party);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.party = (Party) getArguments().getSerializable(MainActivity.ARG_PARTY);
        }

        getActivity().setTitle(R.string.stats);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        table = view.findViewById(R.id.stats_table);
        fillTable(table, party, getSelectedStats());

        radioGroup = view.findViewById(R.id.stats_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                fillTable(table, party, getSelectedStats());
            }
        });

        ConstraintLayout headerLayout = view.findViewById(R.id.stats_group_header);
        TextView groupText = (TextView) headerLayout.getViewById(R.id.group_header_name);
        groupText.setText(party.getName());

        if(party.getImageBytes() != null) {
            ImageView groupImage = (ImageView) headerLayout.getViewById(R.id.group_header_image);
            groupImage.setImageBitmap(party.getImage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    private Stats getSelectedStats(){
        if(radioGroup == null){
            return Stats.POINTS;
        }

        int id = radioGroup.getCheckedRadioButtonId();
        switch (id){
            case R.id.stats_radio_points:
                return Stats.POINTS;
            case R.id.stats_radio_rounds:
                return Stats.ROUNDS;
            case R.id.stats_radio_solos:
                return Stats.SOLO;
            default:
                return Stats.POINTS;
        }
    }

    private List<String[]> getPlayerValues(Stats stats){
        List<Player> players = party.getPlayers();
        List<String[]> playerValues = new ArrayList<>();
        for(Player p : players) {
            int[] pts = party.getPlayerStats(p, stats);
            String[] pPts = new String[]{p.getName(), String.valueOf(pts[0]),
                    String.valueOf(pts[1]), String.valueOf(pts[2])};
            playerValues.add(pPts);
        }
        Collections.sort(playerValues, new Comparator<String[]>() {
            @Override
            public int compare(String[] s1, String[] s2) {
                for(int i = 1; i < s1.length; i++){
                    int result = Integer.valueOf(s2[i]).compareTo(Integer.valueOf(s1[i]));
                    if(result != 0){
                        return result;
                    }
                }
                return s1[0].compareTo(s2[0]);
            }
        });
        return playerValues;
    }

    private void fillTable(TableLayout tableLayout, Party party, Stats stats) {
        tableLayout.removeAllViews();

        List<String[]> values = getPlayerValues(stats);
        values.add(0, headerRow());

        boolean hasLast = false;
        int lastValue = 0;
        int lastRank = 0;
        for(int i = 0; i < values.size(); i++) {
            TableRow row = fillRow(values.get(i));

            if(i != 0){
                String rank = String.valueOf(i);
                int value = Integer.valueOf(values.get(i)[1]);
                if(hasLast && value == lastValue){
                    rank = "";
                } else {
                    lastValue = value;
                    lastRank = i;
                    hasLast = true;
                }
                row.addView(createExtraTextView(rank), 0);
            } else {
                row.addView(createExtraTextView(getString(R.string.table_round_index)), 0);
            }
            tableLayout.addView(row);
        }
    }

    private String[] headerRow() {
        String[] header = new String[]{
                getString(R.string.player),
                getString(R.string.overall),
                getString(R.string.won),
                getString(R.string.lost)
        };
        return header;
    }

    private TableRow fillRow(String[] values) {
        TableRow row = new TableRow(getContext());
        row.setId(ViewCompat.generateViewId());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        for(int i = 0; i < values.length; i++) {
            row.addView(createTextView(values[i]));
        }
        return row;
    }

    private TextView createTextView(String value) {
        TextView tv = new TextView(getContext());
        tv.setId(ViewCompat.generateViewId());
        tv.setText(value);
        tv.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Subhead);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new TableRow.LayoutParams(
                0, TableRow.LayoutParams.MATCH_PARENT, 1));
        tv.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        tv.setPadding(8, 8, 8 ,8);
        return tv;
    }

    private TextView createExtraTextView(String val) {
        TextView tv = createTextView(val);
        tv.setLayoutParams(new TableRow.LayoutParams(
                0, TableRow.LayoutParams.MATCH_PARENT, 0.7f));
        return tv;
    }

}
