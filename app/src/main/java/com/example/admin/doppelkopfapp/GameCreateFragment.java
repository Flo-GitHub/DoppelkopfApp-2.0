package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GameCreateFragment extends Fragment {

    private GameCreateAdapter adapter;
    private RecyclerView recyclerView;
    private Button createButton, cancelButton;

    private Party party;
    private GameManager game;
    private boolean isNew = false;

    private OnGameCreateListener gameCreateListener;

    public GameCreateFragment() {
        // Required empty public constructor
    }

    public static GameCreateFragment newInstance(Party party, GameManager game) {
        GameCreateFragment fragment = new GameCreateFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.ARG_PARTY, party);
        args.putSerializable(MainActivity.ARG_GAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.party = (Party) getArguments().getSerializable(MainActivity.ARG_PARTY);
            this.game = (GameManager) getArguments().getSerializable(MainActivity.ARG_GAME);
            isNew = this.game == null;
        }

        if(isNew){
            getActivity().setTitle(getString(R.string.game_create_fragment_title));
        } else {
            getActivity().setTitle(getString(R.string.game_edit_fragment_title));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout headerLayout = view.findViewById(R.id.game_create_group_header);

        ImageView imageView = (ImageView) headerLayout.getViewById(R.id.group_header_image);
        if(party.getImageBytes() != null) {
            imageView.setImageBitmap(party.getImage());
        }

        TextView groupText = (TextView) headerLayout.getViewById(R.id.group_header_name);
        groupText.setText(party.getName());

        adapter = new GameCreateAdapter(twins(), game);
        initRecyclerView();
        adapter.notifyDataSetChanged();

        createButton = view.findViewById(R.id.game_create_create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    long[] playerChecked = new long[getPlayerChecked().size()];
                    for(int i = 0; i < playerChecked.length; i++) {
                        playerChecked[i] = getPlayerChecked().get(i);
                    }
                    if(isNew){
                        GameManager game = new GameManager(party, playerChecked);
                        onGameCreated(game, isNew);
                    } else {
                        GameManager game = GameCreateFragment.this.game;
                        game.setPlayersDataBaseIds(playerChecked);
                        onGameCreated(game, isNew);
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton = view.findViewById(R.id.game_create_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGameCreateCancelled();
            }
        });

        if(!isNew){
            createButton.setText(R.string.save);
        }
    }

    private List<Long> getPlayerChecked() {
        Map<Long, Boolean> playerChecked = adapter.getPlayerChecked();
        List<Long> ids = new ArrayList<>();
        for(long id : playerChecked.keySet()){
            if(playerChecked.get(id))
                ids.add(id);
        }
        if(ids.size() >= 4) {
            if(ids.size() <= 6) {
                return ids;
            } else {
                throw new IllegalArgumentException(getString(R.string.error_more_than_6_players));
            }
        } else {
            throw new IllegalArgumentException(getResources().getString(R.string.error_minimum_4_players));
        }
    }

    private void initRecyclerView() {
        recyclerView = getActivity().findViewById(R.id.game_create_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void onGameCreated(GameManager game, boolean isNew) {
        if (gameCreateListener != null) {
            gameCreateListener.onGameCreated(game, isNew);
        }
    }

    public void onGameCreateCancelled(){
        if(gameCreateListener != null) {
            gameCreateListener.onGameCreateCancelled();
        }
    }

    private List<Player[]> twins() {
        List<Player[]> list = new ArrayList<>();
        for(int i = 0; i < party.getPlayers().size(); i+=2) {
            try {
                list.add(new Player[]{party.getPlayers().get(i), party.getPlayers().get(i+1)});
            } catch (IndexOutOfBoundsException e) {
                list.add(new Player[]{party.getPlayers().get(i), null});
            }
        }
        return list;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGameCreateListener) {
            gameCreateListener = (OnGameCreateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        gameCreateListener = null;
    }


    public interface OnGameCreateListener {
        void onGameCreated(GameManager game, boolean isNew);
        void onGameCreateCancelled();
    }
}
