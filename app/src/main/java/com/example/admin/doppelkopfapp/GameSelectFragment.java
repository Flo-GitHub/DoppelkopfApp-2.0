package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class GameSelectFragment extends Fragment {

    private FloatingActionButton addButton;
    private RecyclerView.Adapter adapter;
    private Party party;

    private OnGameSelectListener gameSelectListener;

    public GameSelectFragment() {
        // Required empty public constructor
    }

    public static GameSelectFragment newInstance(Party party) {
        GameSelectFragment fragment = new GameSelectFragment();
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
        }
        getActivity().setTitle(getString(R.string.game_select_fragment_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new GameSelectAdapter(gameSelectListener, party);
        initRecyclerView();
        adapter.notifyDataSetChanged();

        addButton = view.findViewById(R.id.game_select_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGameAddSelected();
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.game_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void onGameAddSelected() {
        if(gameSelectListener != null) {
            gameSelectListener.onGameAddClicked();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGameSelectListener) {
            gameSelectListener = (OnGameSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGameSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        gameSelectListener = null;
    }

    public interface OnGameSelectListener {
        void onGameSelected(int pos);
        void onGameAddClicked();
        void onGameDeleted(int pos, RecyclerView.Adapter adapter);
        void onGameEdited(int pos);
    }
}
