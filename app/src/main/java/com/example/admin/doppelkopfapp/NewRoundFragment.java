package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewRoundFragment} interface
 * to handle interaction events.
 * Use the {@link NewRoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewRoundFragment extends Fragment {

    private OnSubmitListener submitListener;

    private Party party;
    private ToggleButton[] player_views;

    private EditText view_points;
    private RadioGroup view_bocks;

    public NewRoundFragment() {
        // Required empty public constructor
    }


    public static NewRoundFragment newInstance(Party party) {
        NewRoundFragment fragment = new NewRoundFragment();
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

        getActivity().setTitle(getString(R.string.new_round_fragment_title));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_round, container, false);

        int[] ids = new int[]{R.id.toggle_round_player1, R.id.toggle_round_player2,
                R.id.toggle_round_player3, R.id.toggle_round_player4};
        Player[] players = party.getCurrentActivePlayers();

        player_views = new ToggleButton[4];
        for(int i = 0; i < ids.length; i++) {
            player_views[i] = view.findViewById(ids[i]);
            player_views[i].setTextOn(players[i].getName());
            player_views[i].setTextOff(players[i].getName());
            player_views[i].setChecked(false);
            player_views[i].setTag(players[i].getDataBaseId());
        }

        view_points = view.findViewById(R.id.edit_round_points);
        view_bocks = view.findViewById(R.id.radio_round_bocks);

        Button button = view.findViewById(R.id.new_round_submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    GameRound round = new GameRound(getPlayerPoints());
                    round.setNewBocks(getBocks());
                    round.setCurrentBocks(party.getCurrentGame().getCurrentBocks());
                    submitListener.onSubmit(round);
                } catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmitListener) {
            submitListener = (OnSubmitListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        submitListener = null;
    }


    private int getBocks() {
        RadioButton button = getView().findViewById(view_bocks.getCheckedRadioButtonId());
        return Integer.parseInt(button.getText().toString());

    }

    private int getPoints() {
        return Integer.parseInt(view_points.getText().toString());
    }

    private Map<Long, Boolean> getWinners(){
        Map<Long, Boolean> list = new HashMap<>();
        for(ToggleButton player : player_views) {
            list.put((Long)player.getTag(), player.isChecked());
        }
        return list;
    }

    private Map<Long, Integer> getPlayerPoints(){
        Map<Long, Boolean> winners = getWinners();
        int points = getPoints();

        HashMap<Long, Integer> map = new HashMap<>();
        for(long p : getWinners().keySet()){
            map.put(p, winners.get(p) ? points : -points);
        }
        return map;
    }


    public interface OnSubmitListener {
        void onSubmit(GameRound round);
    }

}
