package com.example.admin.doppelkopfapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_layout);

        gameManager = (GameManager) getIntent().getSerializableExtra(SettingsActivity.EXTRA_GAME_MANAGER);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    public void update() {
        updateTextViews();
    }

    private void updateTextViews() {
        for( int i = 0; i < gameManager.getPlayers().length; i++ ) {
            TextView textView = (TextView) AndroidUtils.findViewByName( "main_textView_text_" + i+1, this );

            Player p = gameManager.getPlayers()[i];

            textView.setText( String.format("%s: %dP (%f€)", p.getName(), p.getPoints(),
                    p.getPointsLost() * gameManager.getSettings().getCentPerPoint() / 100) );
        }
    }

}
