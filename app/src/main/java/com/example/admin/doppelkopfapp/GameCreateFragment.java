package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameCreateFragment.OnGameCreateListener} interface
 * to handle interaction events.
 * Use the {@link GameCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameCreateFragment extends Fragment {

    private GameCreateAdapter adapter;
    private RecyclerView recyclerView;
    private Button createButton, cancelButton;

    private Party party;

    private OnGameCreateListener gameCreateListener;

    public GameCreateFragment() {
        // Required empty public constructor
    }

    public static GameCreateFragment newInstance(Party party) {
        GameCreateFragment fragment = new GameCreateFragment();
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

        getActivity().setTitle(getResources().getString(R.string.game_create_fragment_title));
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

        adapter = new GameCreateAdapter(twins());
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
                    GameManager game = new GameManager(party, playerChecked);
                    onGameCreated(game);
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
    }

    private List<Long> getPlayerChecked() {
        Map<Long, Boolean> playerChecked = adapter.getPlayerChecked();
        List<Long> ids = new ArrayList<>();
        for(long id : playerChecked.keySet()){
            if(playerChecked.get(id))
                ids.add(id);
        }
        if(ids.size() >= 4) {
            return ids;
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

    public void onGameCreated(GameManager game) {
        if (gameCreateListener != null) {
            gameCreateListener.onGameCreated(game);
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
        void onGameCreated(GameManager game);
        void onGameCreateCancelled();
    }
}
