package com.example.kaanabay.arithmeticgame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                //actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };



    private boolean timerOn;
    private Game mainGame;
    private Question currentQuestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        // findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);



        timerOn = false;
        final TextView textView = (TextView) findViewById(R.id.secondsRemaining);
        final CountDownTimer cT = getCountDownTimer(textView);

        setButtonListeners();

        Button sb = (Button) findViewById(R.id.startBtn);
        sb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startTheGame();
                changeButtonStatusTo(true);
                cT.start();
                TextView score = (TextView) findViewById(R.id.textAnswerCount);
                score.setText("SCORE: 0");
            }
        });


        //GAME
        //startTheGame();
        //setButtonListeners();
    }

    @NonNull
    private CountDownTimer getCountDownTimer(final TextView textView) {
        timerOn = true;
        return new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {


                    String v = String.format("%02d", millisUntilFinished/60000);
                    int va = (int)( (millisUntilFinished%60000)/1000);
                    textView.setText(v + ":" + String.format("%02d",va));
                }

                public void onFinish() {
                    textView.setText("time's up!");
                    timerOn = false;
                    changeButtonStatusTo(false);
                    TextView finalScore = (TextView) findViewById(R.id.textQuestion);
                    showFinalScoreAlert();
                }
            };
    }

    private void showFinalScoreAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(FullscreenActivity.this).create();
        alertDialog.setTitle("TIME IS UP");
        alertDialog.setMessage("Your final score: " +  Integer.toString(mainGame.getScore()));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void changeButtonStatusTo(boolean b) {
        Button btn = (Button) findViewById(R.id.btnAdd);
        btn.setEnabled(b);
        btn = (Button) findViewById(R.id.btnExtract);
        btn.setEnabled(b);
        btn = (Button) findViewById(R.id.btnMultiply);
        btn.setEnabled(b);
        btn = (Button) findViewById(R.id.btnDivide);
        btn.setEnabled(b);
    }

    private void startTheGame() {
        mainGame = new Game(1000); //TODO Decide this number
        currentQuestion = mainGame.getNextQuestion();

        TextView qText = (TextView) findViewById(R.id.textQuestion);
        qText.setText(currentQuestion.getQuestionText());
    }

    private void setButtonListeners() {
        TextView qText = (TextView) findViewById(R.id.textQuestion);
        TextView aText = (TextView) findViewById(R.id.textAnswerCount);

        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        Button btnExt = (Button) findViewById(R.id.btnExtract);
        Button btnMul = (Button) findViewById(R.id.btnMultiply);
        Button btnDiv = (Button) findViewById(R.id.btnDivide);

        clickListener(btnAdd, qText, aText);
        clickListener(btnExt, qText, aText);
        clickListener(btnMul, qText, aText);
        clickListener(btnDiv, qText, aText);
    }

    private void clickListener(Button button, final TextView qText, final TextView aText) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                operatorEnum userOp;
                switch (v.getId()) {
                    case R.id.btnAdd:
                        userOp = operatorEnum.add;
                        break;
                    case R.id.btnExtract:
                        userOp = operatorEnum.extract;
                        break;
                    case R.id.btnMultiply:
                        userOp = operatorEnum.multiply;
                        break;
                    case R.id.btnDivide:
                        userOp = operatorEnum.divide;
                        break;
                    default:
                        userOp = operatorEnum.add;
                }

                if(currentQuestion.checkAnswer(userOp))
                    mainGame.addTrue();
                else
                    mainGame.addFalse();
                aText.setText("SCORE: " + Integer.toString(mainGame.getScore()));
                currentQuestion = mainGame.getNextQuestion();
                qText.setText(currentQuestion.getQuestionText());
            }
        });
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
