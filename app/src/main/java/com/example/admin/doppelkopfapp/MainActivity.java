package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Set;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewRoundFragment.OnSubmitListener,
            PartySelectFragment.OnPartySelectListener, GameSelectFragment.OnGameSelectListener,
            PartyCreateFragment.OnPartyCreateListener, GameCreateFragment.OnGameCreateListener,
            SettingsFragment.OnSettingsChangeListener, TableFragment.OnNextRoundListener,
            SeatingFragment.OnSeatingChangedListener{

    public static final String ARG_PARTY = "party";
    public static final String ARG_PARTY_MANAGER = "partyManager";

    private static final String TAG_PARTY_SELECT = "party_select",
                                TAG_PARTY_CREATE = "party_create",
                                TAG_GAME_SELECT = "game_select",
                                TAG_GAME_CREATE = "game_create",
                                TAG_SETTINGS = "settings",
                                TAG_TABLE = "table",
                                TAG_NEW_ROUND = "new_round",
                                TAG_SEATING = "seating";

    private String fragmentTag = TAG_PARTY_SELECT;
    private String lastFragmentTag = fragmentTag;

    private NavigationView navigationView;
    private MenuItem partyItem,
                     gameItem,
                     newRoundItem,
                     tableItem,
                     seatingItem;
    private ImageView navImageView;

    private PartyManager partyManager;
    private GameDataSource dataSource;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        dataSource = new GameDataSource(this);
        dataSource.open();

        partyManager = new PartyManager(dataSource.getAllParties());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        partyItem = menu.findItem(R.id.nav_party_select);
        gameItem = menu.findItem(R.id.nav_game_select);
        newRoundItem = menu.findItem(R.id.nav_new_round);
        tableItem = menu.findItem(R.id.nav_table);
        seatingItem = menu.findItem(R.id.nav_seating);

        View headerView = navigationView.getHeaderView(0);
        navImageView = headerView.findViewById(R.id.nav_header_image);

        switchToParty();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            goBack();
        }
    }

    private void goBack() {
        switch (fragmentTag) {
            case TAG_PARTY_CREATE:
            case TAG_GAME_SELECT:
                switchToParty();
                break;
            case TAG_GAME_CREATE:
            case TAG_TABLE:
                switchToGame();
                break;
            case TAG_NEW_ROUND:
            case TAG_SEATING:
                switchToTable();
                break;
            case TAG_SETTINGS:
                switchToTag(lastFragmentTag);
        }
    }

    private void switchToTag(String tag) {
        switch(tag) {
            case TAG_PARTY_SELECT:
                switchToParty();
                break;
            case TAG_PARTY_CREATE:
                switchToPartyCreate();
                break;
            case TAG_GAME_SELECT:
                switchToGame();
                break;
            case TAG_GAME_CREATE:
                switchToGameCreate();
                break;
            case TAG_TABLE:
                switchToTable();
                break;
            case TAG_NEW_ROUND:
                switchToNewRound();
                break;
            case TAG_SEATING:
                switchToSeating();
                break;
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
            case R.id.nav_seating:
                switchToSeating();
                break;
            default:
                switchToParty();
        }

        item.setChecked(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

    private void disableItems(MenuItem... items){
        for(MenuItem item : items) {
            item.setEnabled(false);
        }
    }

    private void enableItems(MenuItem... items) {
        for(MenuItem item : items) {
            item.setEnabled(true);
        }
    }

    private void reEnableItems() {
        try {
            partyItem.setTitle(partyManager.getCurrentParty().getName());
            enableItems(gameItem);
        } catch (Exception e) {
            disableItems(gameItem, newRoundItem, tableItem, seatingItem);
            partyItem.setTitle(R.string.party_select_fragment_title);
            return;
        }
        try {
            gameItem.setTitle(MyUtils.getFullDisplayDate(
                    partyManager.getCurrentParty().getCurrentGame().getLastDate()));
            enableItems(newRoundItem, tableItem, seatingItem);
        } catch (Exception e) {
            disableItems(newRoundItem, tableItem, seatingItem);
            gameItem.setTitle(R.string.game_select_fragment_title);
        }
        try {
            navImageView.setImageBitmap(partyManager.getCurrentParty().getImage());
        } catch(Exception ignore) {
        }

    }

    private void selectNavigationDrawer(String tag){
        switch (tag) {
            case TAG_PARTY_SELECT:
                navigationView.setCheckedItem(R.id.nav_party_select);
                partyItem.setTitle(R.string.party_select_fragment_title);
                break;
            case TAG_GAME_SELECT:
                navigationView.setCheckedItem(R.id.nav_game_select);
                break;
            case TAG_NEW_ROUND:
                navigationView.setCheckedItem(R.id.nav_new_round);
                break;
            case TAG_TABLE:
                navigationView.setCheckedItem(R.id.nav_table);
                break;
            case TAG_SEATING:
                navigationView.setCheckedItem(R.id.nav_seating);
        }
        reEnableItems();
    }

    private void switchFragments(Class fragmentClass, Bundle bundle, String tag) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

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

        if(!tag.equals(fragmentTag)) {
            lastFragmentTag = this.fragmentTag;
            this.fragmentTag = tag;
        }

        selectNavigationDrawer(tag);
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                (null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
        switchFragments(TableFragment.class, partyBundle(), TAG_TABLE);
    }

    private void switchToNewRound(){
        switchFragments(NewRoundFragment.class, partyBundle(), TAG_NEW_ROUND);
    }

    private void switchToParty() {
        switchFragments(PartySelectFragment.class, partyManagerBundle(), TAG_PARTY_SELECT);
    }

    private void switchToPartyCreate(){
        switchFragments(PartyCreateFragment.class, new Bundle(), TAG_PARTY_CREATE);
    }

    private void switchToGame() {
        switchFragments(GameSelectFragment.class, partyBundle(), TAG_GAME_SELECT);
    }

    private void switchToGameCreate() {
        switchFragments(GameCreateFragment.class, partyBundle(), TAG_GAME_CREATE);
    }

    private void switchToSettings(){
        try {
            switchFragments(SettingsFragment.class, partyBundle(), TAG_SETTINGS);
        } catch (Exception ignore) {}
    }

    private void switchToSeating(){
        switchFragments(SeatingFragment.class, partyBundle(), TAG_SEATING);
    }

    @Override
    public void onSubmit(GameRound round, boolean repeat) {
        try {
            partyManager.getCurrentParty().getCurrentGame().addRound(round, repeat);
            GameRound newRound = partyManager.getCurrentParty().getCurrentGame().getLastRound();

            long id = dataSource.createRound(newRound, partyManager.getCurrentParty().getCurrentGame().getDatabaseId());
            newRound.setDataBaseId(id);
            dataSource.updateGame(partyManager.getCurrentParty(), partyManager.getCurrentParty().getCurrentGame());
            partyManager.sort();
            partyManager.getCurrentParty().sort();
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
    public void onPartyDeleted(int pos, RecyclerView.Adapter adapter) {
        dataSource.deleteDeepParty(partyManager.getParties().get(pos));
        partyManager.getParties().remove(pos);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, getString(R.string.group_deleted), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPartyEdited(int pos) {

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
        long id = dataSource.createParty(party);
        party.setDatabaseId(id);
        partyManager.addParty(party);
        switchToParty();
    }

    @Override
    public void onPartyCreateCancelled() {
        switchToParty();
    }

    @Override
    public void onGameCreated(GameManager game) {
        long id = dataSource.createGame(game, partyManager.getCurrentParty().getDatabaseId());
        game.setDatabaseId(id);
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
        dataSource.updateSettings(settings, partyManager.getCurrentParty().getDatabaseId());
        for(GameManager game : partyManager.getCurrentParty().getGames()) {
            game.resetBocks(settings.getMaxBocks());
            dataSource.updateGame(partyManager.getCurrentParty(), game);
        }
        goBack();
    }

    @Override
    public void onSettingsCancelled() {
        goBack();
    }

    @Override
    public void onNextRound() {
        switchToNewRound();
    }

    @Override
    public void onDeleteLastRound(TableLayout tableLayout) {
        GameManager game = partyManager.getCurrentParty().getCurrentGame();
        GameRound round = game.removeLastRound();
        dataSource.deleteDeepRound(round);
        dataSource.updateGame(partyManager.getCurrentParty(), partyManager.getCurrentParty().getCurrentGame());
        tableLayout.removeViewAt(game.getRounds().size()+1);
        Toast.makeText(this, getString(R.string.round_deleted), Toast.LENGTH_SHORT).show();
    }

    public static Context getContext(){
        return context;
    }

    @Override
    public void onSeatingChanged(List<Long> ids) {
        partyManager.getCurrentParty().getCurrentGame().changeSeating(ids);
        switchToTable();
        dataSource.updateGame(partyManager.getCurrentParty(), partyManager.getCurrentParty().getCurrentGame());
    }

    @Override
    public void onSeatingCanceled() {
        switchToTable();
    }
}
