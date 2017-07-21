package com.example.admin.doppelkopfapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    public static final String EXTRA_GAME_MANAGER = "gameManager";

    private GameManagerDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        dataSource = new GameManagerDataSource(this);
    }

    public void update() {
        addSwitchChangedListener();
        addButtonClickListener();
    }

    public GameManager getGameManager() {
        GameSettings settings = new GameSettings(getCentPerPoint(),
                isChecked(R.id.settings_switch_bock),
                isChecked(R.id.settings_switch_double_bock),
                isChecked(R.id.settings_switch_solo_bock_calculation) );

        return new GameManager(getPlayers(), settings );
    }


    private void addSwitchChangedListener() {

        final Switch switchBock = (Switch) findViewById(R.id.settings_switch_bock);
        final Switch switchDoubleBock = (Switch) findViewById(R.id.settings_switch_double_bock);
        final Switch switchSoloBockCalculation = (Switch) findViewById(R.id.settings_switch_solo_bock_calculation);

        if( switchBock.isChecked() == false ) {
            disableSwitch(switchDoubleBock);
            disableSwitch(switchSoloBockCalculation);
        }

        switchBock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    enableSwitch(switchDoubleBock);
                    enableSwitch(switchSoloBockCalculation);
                }
                else {
                    disableSwitch(switchDoubleBock);
                    disableSwitch(switchSoloBockCalculation);
                }
            }
        });

    }

    private void disableSwitch( Switch s ) {
        s.setChecked(false);
        s.setEnabled(false);
    }

    private void enableSwitch( Switch s ) {
        s.setEnabled(true);
    }

    private void addButtonClickListener() {

        Button buttonNew = (Button) findViewById(R.id.settings_button_new_game );
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player[] players = getPlayers();
                if( players.length < 4 || players.length > 6) {
                    Log.d("GameSettings", "button new game clicked and player size is less 4: " + players.length);
                    return;
                }
                Log.d("GameSettings", "button new game clicked and player size is greater 3: " + players.length);

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra(EXTRA_GAME_MANAGER, getGameManager());
                startActivity(intent);
            }
        });

        Button buttonContinue = (Button) findViewById(R.id.settings_button_continue_game);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra(EXTRA_GAME_MANAGER, dataSource.getNewestGame());
                startActivity(intent);
            }
        });
    }

    private Player[] getPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for( int i = 1; i <= 6; i++ ) {
            EditText editText = (EditText) AndroidUtils.findViewByName( "settings_editText_name" + i, this );
            if ( !editText.getText().toString().trim().isEmpty() )
                players.add( new Player(editText.getText().toString().trim()) );
        }
        return players.toArray(new Player[players.size()]);
    }

    private int getCentPerPoint() {
        EditText editText = (EditText) findViewById(R.id.settings_editText_cent_per_point);
        String text = editText.getText().toString();
        if( text.trim().isEmpty() )
            return 0;
        return Integer.parseInt(text);
    }

    private boolean isChecked(int id) {
        Switch s = (Switch) findViewById(id);
        return s.isChecked();
    }
}
