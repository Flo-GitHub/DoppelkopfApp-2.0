package com.example.admin.doppelkopfapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        gameManager = (GameManager) getIntent().getSerializableExtra(SettingsActivity.EXTRA_GAME_MANAGER);
        addOnClickListeners();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        update();
    }

    public void update() {
        updateTextViews();
        updateBocksRemaining();
    }


    private void updateTextViews() {
        for( int i = 0; i < gameManager.getPlayers().length; i++ ) {
            TextView textView = (TextView) AndroidUtils.findViewByName("main_textView_text_" + (i+1), this);
            Player p = gameManager.getPlayers()[i];

            textView.setText( String.format(Locale.getDefault(), "%s: %dP (%s)", p.getName(), p.getPoints(),
                    NumberFormat.getCurrencyInstance().format(p.getPointsLost() * gameManager.getSettings().getCentPerPoint() / 100f) ) );
        }
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
    }

}
