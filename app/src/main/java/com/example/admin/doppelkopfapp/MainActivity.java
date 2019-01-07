package com.example.admin.doppelkopfapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private GameManager gameManager;
    private GameDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        gameManager = (GameManager) getIntent().getSerializableExtra(SettingsActivity.EXTRA_GAME_MANAGER);
        dataSource = new GameDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addOnClickListeners();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        update();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dataSource.close();
    }

    public void update() {
        updateTextViews();
        updateBocksRemaining();
    }


    private void updateTextViews() {
        List<String> list = new ArrayList<>();

        for(int i = 0; i < gameManager.getPlayersDataBaseIds().length; i++ ) {
            Player p = gameManager.getPlayersDataBaseIds()[i];

            list.add( String.format(Locale.getDefault(), "%s: %dP (%s)", p.getName(), p.getPoints(),
                    NumberFormat.getCurrencyInstance().format(p.getPointsLost() * gameManager.getSettings().getCentPerPoint() / 100f) ) );
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_row, list);

        ListView listView = (ListView) findViewById(R.id.main_listView_playerList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                if(gameManager.getPlayersDataBaseIds().length > 4 ) {
                    builder.setMessage(getString(R.string.dialog_delete_player));
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Player player = gameManager.getPlayersDataBaseIds()[position];
                            gameManager.removePlayer(player);
                            dataSource.deletePlayer(player);
                            adapter.notifyDataSetChanged();
                            update();
                        }
                    });
                } else {

                    builder.setMessage(getString(R.string.dialog_delete_not_enough_players));
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                }
                builder.show();
            }
        });
    }

    private void updateBocksRemaining() {
        TextView textBocks = (TextView) findViewById(R.id.main_textView_bocksLeft);
        textBocks.setText(getResources().getString(R.string.bocks_remaining) + " " + gameManager.getBocks());
        TextView textDoubleBocks = (TextView) findViewById(R.id.main_textView_doubleBocksLeft);
        textDoubleBocks.setText(getResources().getString(R.string.double_bocks_remaining) + " " + gameManager.getDoubleBocks());
    }


    private void addOnClickListeners() {
        Button buttonMenu = (Button) findViewById(R.id.main_button_menu);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        Button buttonNextRound = (Button) findViewById(R.id.main_button_nextRound);
        buttonNextRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoundActivity.class);
                intent.putExtra(SettingsActivity.EXTRA_GAME_MANAGER, gameManager);
                startActivity(intent);
            }
        });

        ImageButton buttonAddPlayer = (ImageButton) findViewById(R.id.main_button_add_player);
        buttonAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editTextName = new EditText(MainActivity.this);
                editTextName.setHint(getString(R.string.hint_playername));

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getString(R.string.dialog_add_player));
                builder.setView(editTextName);

                builder.setPositiveButton(getString(R.string.continue_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editTextName.getText().toString().trim();
                        if(name != null && !name.isEmpty() && gameManager.getPlayersDataBaseIds().length < 6) {
                            Player player = new Player(dataSource.getNextPlayerId(), name);
                            gameManager.addPlayer(player, gameManager.getPlayersDataBaseIds().length);
                            dataSource.createPlayer(player, gameManager.getDatabaseId());
                            update();
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

}
