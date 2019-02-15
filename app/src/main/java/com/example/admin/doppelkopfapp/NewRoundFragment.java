package com.example.admin.doppelkopfapp;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewRoundFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewRoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewRoundFragment extends Fragment {

    private static final String ARG_PARTY = "party";

    private Party party;
    private OnFragmentInteractionListener mListener;

    private ToggleButton[] player_views;

    private View view;
    private EditText view_points;
    private RadioGroup view_bocks;

    public NewRoundFragment() {
        // Required empty public constructor
    }


    public static NewRoundFragment newInstance(Party party) {
        NewRoundFragment fragment = new NewRoundFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARTY, party);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        party = MyUtils.sampleParty();
        if (getArguments() != null) {
            //party = (Party) getArguments().getSerializable(ARG_PARTY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_round, container, false);

        int[] ids = new int[]{R.id.toggle_round_player1, R.id.toggle_round_player2,
                R.id.toggle_round_player3, R.id.toggle_round_player4};
        List<Player> players = party.getPlayers();

        Log.e("oncreate", players.get(0).getName());
        player_views = new ToggleButton[4];
        for(int i = 0; i < ids.length; i++) {
            player_views[i] = view.findViewById(ids[i]);
            player_views[i].setTextOn("W " + players.get(i).getName());
            player_views[i].setTextOff("L " + players.get(i).getName());
            player_views[i].setChecked(false);
        }

        view_points = view.findViewById(R.id.edit_round_points);
        view_bocks = view.findViewById(R.id.radio_round_bocks);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
  //                  + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private int getBocks() {
        RadioButton button = view.findViewById(view_bocks.getCheckedRadioButtonId());
        return Integer.parseInt(button.getText().toString());

    }

    private int getPoints() {
        return Integer.parseInt(view_points.getText().toString());
    }

    private List<Boolean> getWinners(){
        List<Boolean> list = new ArrayList<>();
        for(ToggleButton player : player_views) {
            list.add(player.isChecked());
        }
        return list;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
