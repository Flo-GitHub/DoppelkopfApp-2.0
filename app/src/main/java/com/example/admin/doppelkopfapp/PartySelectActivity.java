package com.example.admin.doppelkopfapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PartySelectActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton addButton;
    private PartyManager partyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_select);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.party_toolbar);
        //setSupportActionBar(toolbar);

        partyManager = new PartyManager(this);
        adapter = new PartySelectAdapter(this, partyManager);
        initActionButton();
        initRecyclerView();
        adapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.party_recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void initActionButton() {
        addButton = (FloatingActionButton) findViewById(R.id.party_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo add actual add box to enter name
                Log.e("TEST","ADD BUTTON PRESSED");
            }
        });
    }

}
