package com.example.admin.doppelkopfapp;

import android.support.v7.app.AppCompatActivity;

public class RoundActivity extends AppCompatActivity {

   /* private GameManager gameManager;
    private GameDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_layout);

        gameManager = (GameManager) getIntent().getSerializableExtra(SettingsActivity.EXTRA_GAME_MANAGER);

        dataSource = new GameDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addOnClickListener();
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
        updateGiver();
        updateNames();
        hideBockIfDisabled();
    }


    private void hideBockIfDisabled() {
        RadioButton button1Bock = (RadioButton) findViewById(R.id.round_radioButton_1);
        RadioButton button2Bock = (RadioButton) findViewById(R.id.round_radioButton_2);
        if( !gameManager.getSettings().isBock() ) {
            button1Bock.setEnabled(false);
            button2Bock.setEnabled(false);
        } else {
            button1Bock.setEnabled(true);
            button2Bock.setEnabled(true);
        }

    }

    private void updateGiver() {
        TextView textView = (TextView) findViewById(R.id.round_textView_giver);
        Player p = gameManager.getGiver();
        textView.setText(p.getName()  + " " + getString(R.string.round_gives) );
    }


    private void updateNames() {
        for( int i = 0; i < 4; i++ ) {
            TextView textView = (TextView) AndroidUtils.findViewByName(this, "round_textView_active_" + (i+1) );
            textView.setText(gameManager.getActivePlayers()[i].getName() + ": ");
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

    private int[] getPoints() {
        int[] points = new int[4];
        for( int i = 0; i < 4; i++ ) {
            EditText editText = (EditText) AndroidUtils.findViewByName(this, "round_editText_points_" + (i+1));
            points[i] = Integer.parseInt( editText.getText().toString() );
        }
        return points;
    }

    private boolean isRepeatRound() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.round_checkBox_repeatRound);
        return checkBox.isChecked();
    }

    private void addOnClickListener() {
        Button buttonSkip = (Button) findViewById(R.id.round_button_skip);
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RoundActivity.this);
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameManager.skipRound();

                        dataSource.updateGame(gameManager);

                        Intent intent = new Intent(RoundActivity.this, MainActivity.class);
                        intent.putExtra(SettingsActivity.EXTRA_GAME_MANAGER, gameManager);
                        startActivity(intent);
                    }
                });
                SkipDialog skipDialog = new SkipDialog();
                skipDialog.show(getFragmentManager(), "Verify");
            }
        });

        Button buttonContinue = (Button) findViewById(R.id.round_button_continue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GameManager gameManager = RoundActivity.this.gameManager.cloneGameManager();
                    gameManager.nextRound(getPoints(), getBocks(), isRepeatRound());

                    dataSource.updateGame(gameManager);

                    Intent intent = new Intent(RoundActivity.this, MainActivity.class);
                    intent.putExtra(SettingsActivity.EXTRA_GAME_MANAGER, gameManager);
                    startActivity(intent);
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoundActivity.this);
                    builder.setCancelable(false);
                    builder.setNeutralButton(getString(R.string.dialog_points_sum_not_0), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    SumPointsDialog sumPointsDialog = new SumPointsDialog();
                    sumPointsDialog.show(getFragmentManager(), "Error");
                }

            }
        });
    }

    public static class SkipDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.dialog_skip_round));
            builder.setCancelable(false);
            builder.setPositiveButton( getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RoundActivity roundActivity = (RoundActivity) getActivity();
                    roundActivity.gameManager.skipRound();

                    Intent intent = new Intent(roundActivity, MainActivity.class);
                    intent.putExtra(SettingsActivity.EXTRA_GAME_MANAGER, roundActivity.gameManager);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            return builder.create();
        }
    }

    public static class SumPointsDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.dialog_points_sum_not_0));
            builder.setCancelable(false);
            builder.setPositiveButton( getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            return builder.create();
        }
    }*/
}