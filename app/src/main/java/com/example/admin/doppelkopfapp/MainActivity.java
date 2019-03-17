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
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewRoundFragment.OnSubmitListener,
            PartySelectFragment.OnPartySelectListener, GameSelectFragment.OnGameSelectListener,
            PartyCreateFragment.OnPartyCreateListener, GameCreateFragment.OnGameCreateListener,
            SettingsFragment.OnSettingsChangeListener{

    public static final String ARG_PARTY = "party";
    public static final String ARG_PARTY_MANAGER = "partyManager";

    PartyManager partyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        partyManager = MyUtils.samplePartyManager();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        switchToParty();
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
        int id = item.getItemId();

        if (id == R.id.mi_settings) {
            switchToSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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


    private void switchToTable() {
        switchFragments(TableFragment.class, partyBundle());
    }

    private void switchToNewRound(){
        switchFragments(NewRoundFragment.class, partyBundle());
    }

    private void switchToParty() {
        switchFragments(PartySelectFragment.class, partyManagerBundle());
    }

    private void switchToPartyCreate(){
        switchFragments(PartyCreateFragment.class, new Bundle());
    }

    private void switchToGame() {
        switchFragments(GameSelectFragment.class, partyBundle());
    }

    private void switchToGameCreate() {
        switchFragments(GameCreateFragment.class, partyBundle());
    }

    private void switchToSettings(){
        switchFragments(SettingsFragment.class, partyBundle());
    }

    @Override
    public void onSubmit(GameRound round) {
        try {
            partyManager.getCurrentParty().getCurrentGame().addRound(round);
            switchToTable();
        } catch(Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPartySelected(int pos) {
        partyManager.setCurrentParty(partyManager.getParties().get(pos).getDatabaseId());
        switchToGame();
    }

    @Override
    public void onPartyAddClicked() {
        switchToPartyCreate();
    }

    @Override
    public void onGameSelected(int pos) {
        partyManager.getCurrentParty().setCurrentGame(
                partyManager.getCurrentParty().getGames().get(pos).getDatabaseId());
        switchToTable();
    }

    @Override
    public void onGameAddClicked() {
        switchToGameCreate();
    }

    @Override
    public void onPartyCreated(Party party) {
        partyManager.addParty(party);
        switchToParty();
    }

    @Override
    public void onPartyCreateCancelled() {
        switchToParty();
    }

    @Override
    public void onGameCreated(GameManager game) {
        partyManager.getCurrentParty().addGame(game);
        switchToGame();
    }

    @Override
    public void onGameCreateCancelled() {
        switchToGame();
    }

    @Override
    public void onSettingsSaved(GameSettings settings) {
        partyManager.getCurrentParty().setSettings(settings);
        for(GameManager game : partyManager.getCurrentParty().getGames()) {
            game.resetBocks(settings.getMaxBocks());
        }
        super.onBackPressed();
    }

    @Override
    public void onSettingsCancelled() {
        super.onBackPressed();
    }
}
