package com.example.admin.doppelkopfapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GameSelectActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private PartyManager partyManager;
    private int partyIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);

        partyIndex = getIntent().getIntExtra(INTENT_EXTRAS.PARTY_INDEX, 0);
        partyManager = new PartyManager(this );

        adapter = new GameSelectAdapter(this, partyManager.getParties().get(partyIndex) );

        initRecyclerView();
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.game_recycler_view);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


}
