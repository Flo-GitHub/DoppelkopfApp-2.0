package com.example.admin.doppelkopfapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.sql.SQLException;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    public static final String EXTRA_GAME_MANAGER = "gameManager";

    private GameDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        addListeners();

        dataSource = new GameDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        disableContinueIfNoGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dataSource.close();
    }

    private void disableContinueIfNoGame() {
        Button button = (Button) findViewById(R.id.settings_button_continue_game);
        if( dataSource.hasGame() )
            button.setEnabled(true);
        else
            button.setEnabled(false);
    }

    private void addListeners() {
        addSwitchChangedListener();
        addButtonClickListener();
    }

    public GameManager getGameManagerWithoutId() {
        return new GameManager(getPlayers(), getSettings());
    }

    private GameSettings getSettings() {
        return new GameSettings( getCentPerPoint(),
                isChecked(R.id.settings_switch_bock),
                isChecked(R.id.settings_switch_double_bock),
                isChecked(R.id.settings_switch_solo_bock_calculation));
    }


    private void addSwitchChangedListener() {

        final Switch switchBock = (Switch) findViewById(R.id.settings_switch_bock);
        final Switch switchDoubleBock = (Switch) findViewById(R.id.settings_switch_double_bock);
        final Switch switchSoloBockCalculation = (Switch) findViewById(R.id.settings_switch_solo_bock_calculation);

        if( !switchBock.isChecked() ) {
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
                    SizeLess4Dialog dialog = new SizeLess4Dialog();
                    dialog.show(getFragmentManager(), "Error");
                } else {
                    GameManager gameManager = getGameManagerWithoutId();
                    long id = dataSource.createGame(gameManager);
                    gameManager.setDatabaseId(id);

                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.putExtra(EXTRA_GAME_MANAGER, gameManager);
                    startActivity(intent);
                }
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
        long nextPlayerId = dataSource.getNextPlayerId();
        for( int i = 1; i <= 6; i++ ) {
            EditText editText = (EditText) AndroidUtils.findViewByName(this, "settings_editText_name" + i );
            if ( !editText.getText().toString().trim().isEmpty() ) {
                players.add(new Player(nextPlayerId, editText.getText().toString().trim()));
                nextPlayerId++;
            }
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

    public static class SizeLess4Dialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.dialog_not_enough_players));
            builder.setCancelable(false);
            builder.setPositiveButton( getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            return builder.create();
        }
    }

}