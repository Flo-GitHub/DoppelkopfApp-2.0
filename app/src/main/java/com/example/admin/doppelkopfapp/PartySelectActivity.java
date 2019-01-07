package com.example.admin.doppelkopfapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class PartySelectActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton addButton;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.party_select_toolbar);
        setSupportActionBar(toolbar);


        initActionButton();
        initRecyclerView();
    }

    private void initCardView() {
       cardView = (CardView) findViewById(R.id.party_select_card_view);
    }

    private void initRecyclerView() {

    }

    private void initActionButton() {
        addButton = (FloatingActionButton) findViewById(R.id.party_select_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
