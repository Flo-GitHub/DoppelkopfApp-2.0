package com.example.admin.doppelkopfapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewRoundFragment.OnSubmitListener,
            PartySelectFragment.OnPartySelectListener, GameSelectFragment.OnGameSelectListener{

    public static final String ARG_PARTY = "party";
    public static final String ARG_PARTY_MANAGER = "partyManager";

    PartyManager partyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        partyManager = MyUtils.samplePartyManager();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Bundle bundle;

        switch (item.getItemId()) {
            case R.id.nav_new_round:
                switchToNewRound();
                break;
            case R.id.nav_table:
                switchToTable();
                break;
            case R.id.nav_party_select:
                switchToParty();
                break;
            case R.id.nav_game_select:
                switchToGame();
                break;
            default:
                switchToParty();
        }

        item.setChecked(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchFragments(Class fragmentClass, Bundle bundle) {
        Fragment fragment = null;
        try{
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);
        } catch(Exception e){
            e.printStackTrace();
        }


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private Bundle partyBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARTY, partyManager.getCurrentParty());
        return bundle;
    }

    private Bundle partyManagerBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARTY_MANAGER, partyManager);
        return bundle;
    }

    @Override
    public void onSubmit(GameRound round) {
        partyManager.getCurrentParty().getCurrentGame().addRound(round);
        switchToTable();
    }

    @Override
    public void onPartySelect(int pos) {
        partyManager.setCurrentParty(partyManager.getParties().get(pos).getDatabaseId());
        switchToGame();
        Log.e("Party", partyManager.getCurrentParty().getName() + " was selected!");
    }

    @Override
    public void onGameSelect(int pos) {
        partyManager.getCurrentParty().setCurrentGame(
                partyManager.getCurrentParty().getGames().get(pos).getDatabaseId());
        switchToTable();
        Log.e("Game", partyManager.getCurrentParty().getCurrentGame().getPlayersAsString() + " selected");
    }

    private void switchToTable() {
        switchFragments(TableFragment.class, partyBundle());
    }

    private void switchToNewRound(){
        switchFragments(NewRoundFragment.class, partyBundle());
    }

    private void switchToParty() {
        switchFragments(PartySelectFragment.class, partyManagerBundle());
    }

    private void switchToGame() {
        switchFragments(GameSelectFragment.class, partyBundle());
    }
}
