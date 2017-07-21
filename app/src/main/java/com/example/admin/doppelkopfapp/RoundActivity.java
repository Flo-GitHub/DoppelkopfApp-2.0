package com.example.admin.doppelkopfapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class RoundActivity extends AppCompatActivity {

    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_layout);

        gameManager = (GameManager) getIntent().getSerializableExtra(SettingsActivity.EXTRA_GAME_MANAGER);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    public void update() {
        updateGiver();
        updateNames();
        hideBockIfDisabled();
    }

    public void nextRound() {

    }

    private void hideBockIfDisabled() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.round_linearLayout_bockGroup);
        if( !gameManager.getSettings().isBock() )
            linearLayout.setEnabled(false);
        else
            linearLayout.setEnabled(true);

    }

    private void updateGiver() {
        TextView textView = (TextView) findViewById(R.id.round_textView_giver);
        Player p = gameManager.getGiver();
        textView.setText(p.getName() + getString(R.string.round_gives) );
    }

    private void updateNames() {
        for( int i = 0; i < 4; i++ ) {
            TextView textView = (TextView) AndroidUtils.findViewByName( "round_textView_active_" + i+1, this );
            Player p = gameManager.getActivePlayers()[i];
            textView.setText(p.getName() + ":");
        }
    }

    private int getBocks() {
        RadioButton one = (RadioButton) findViewById(R.id.round_radioButton_1);
        RadioButton two = (RadioButton) findViewById(R.id.round_radioButton_2);

        if( one.isChecked() )
            return 1;
        else if( two.isChecked() )
            return 2;
        else
            return 0;
    }

    private void addPoints() {
        for( int i = 0; i < 4; i++ ) {
            EditText editText = (EditText) AndroidUtils.findViewByName("round_editText_points_" + i+1, this);
            int points = Integer.parseInt( editText.getText().toString() );
            gameManager.getActivePlayers()[i].addPoints(points);
        }
    }

    private boolean isRepeatRound() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.round_checkBox_repeatRound);
        return checkBox.isChecked();
    }
}
