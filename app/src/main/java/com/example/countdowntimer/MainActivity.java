package com.example.countdowntimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    // ------------------ Khai báo
    TextView countDownText;
    Button countDownButton, btnPoMoDoRo, btnShort;
    FloatingActionButton btnFAB;

    CountDownTimer countDownTimer;

    SeekBar seekBar;

    // 1000ms = 1s => 60000 = 1m
    long timeLeftInMilliseconds = 6000; //25 mins

    int MaxSeekBarPomodoro = 6;
    int ShortSeekBarPomodoro = 8;

    // ------------------ Check start and pause
    String startPause;
    boolean flag = false;

    // ------------------ Media
    MediaPlayer mediaPlayer;

    // ------------------ FIRST CLICK
    boolean isCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Thanh trạng thái trong suốt
        setStatusBarTransparent();

        // ----------- ÁNH XẠ
        init ();

        // ----------- SEEKBAR
        seekBar.setEnabled(false);
        seekBar.setMax(MaxSeekBarPomodoro);

        // ----------- Music
        mediaPlayer = MediaPlayer.create(this, R.raw.chuong);

        // ------------ FUNCTION XỬ LÝ CLICK PoMoDoRo, btnShort
        pomodoroAndShort();

        // ----------- CALLBACK KHI CLICK VÀO BUTTON PLAY OR PAUSE
        clickBtnStarStop();

        // ----------- CALLBACK CẬP NHẬT THỜI GIAN
        updateTimer();
        seekBar.setProgress(0);

        // --------------------------------------
        btnPoMoDoRo.setEnabled(false);

        // ----------- CALLBACK FUNCTION CLICKFAB
        clickFAB();

    }

    // ============ I. CÁC FUNCTION XỬ LÝ ĐỒNG HỒ VÀ BUTTON CLICK

    // ------------ FUNCTION DÙNG ÁNH XẠ
    public void init () {
        countDownText = (TextView) findViewById(R.id.time);
        countDownButton = (Button) findViewById(R.id.btnStartPause);
        seekBar = (SeekBar) findViewById(R.id.sb);
        btnPoMoDoRo = (Button) findViewById(R.id.Pomodoro);
        btnShort = (Button) findViewById(R.id.ShortBreak);
        btnFAB = (FloatingActionButton) findViewById(R.id.fab);
    }

    // ------------ FUNCTION XỬ LÝ CLICK PoMoDoRo, btnShort
    public void pomodoroAndShort() {
        btnPoMoDoRo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("", "Click Vào btn1");

                if (isCheck) {
                    stopTimer();
                }

                countDownButton.setTextColor(Color.parseColor("#AC061A"));

                flag = false;

                btnPoMoDoRo.setEnabled(false);
                btnShort.setEnabled(true);

                // ----------- SEEKBAR
                seekBar.setEnabled(false);
                seekBar.setMax(MaxSeekBarPomodoro);

                timeLeftInMilliseconds = 6000;
                updateTimer();
                seekBar.setProgress(0);

                countDownButton.setText("Bắt đầu");
                clickBtnStarStop();
            }
        });

        btnShort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("", "Click Vào btn2");

                if (isCheck) {
                    stopTimer();
                }

                countDownButton.setTextColor(Color.parseColor("#ED1E9FAF"));

                flag = true;

                btnPoMoDoRo.setEnabled(true);
                btnShort.setEnabled(false);

                // ----------- SEEKBAR
                seekBar.setEnabled(false);
                seekBar.setMax(ShortSeekBarPomodoro);

                timeLeftInMilliseconds = 8000;
                updateTimer();
                seekBar.setProgress(0);

                countDownButton.setText("Bắt đầu");

                clickBtnStarStop();

            }
        });
    }

    // ----------- KHI CLICK VÀO BUTTON PLAY OR PAUSE
    public void clickBtnStarStop() {
        countDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ------------ FUNCTION XỬ LÝ CLICK START AND PAUSE
                StartStop();
            }
        });
    }

    // ------------ FUNCTION START STOP
    public void StartStop() {

        startPause = (String) countDownButton.getText();

        // ----------- FIX ERROR MEDIA NOT AUTO REPEAT
        mediaPlayer = MediaPlayer.create(this, R.raw.chuong);

        // ----------- CHECK NẾU timeRunning = true thì dừng, timeRunning = false thì chạy
        if (startPause == "Tạm dừng") {
            countDownButton.setTextColor(Color.parseColor("#AC061A"));
            // ---------- CALLBACK PAUSE
            stopTimer();
        } else {
            countDownButton.setTextColor(Color.parseColor("#AC061A"));
            // ---------- CALLBACK START
            startTimer();
        }
    }


    // --------------- FUNCTION START TIMER (nếu thời gian đang chạy)
    public void startTimer () {
        isCheck = true;
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliseconds = millisUntilFinished;

                // ----------- CALLBACK CẬP NHẬT THỜI GIAN

                updateTimer();
                SeekBarTimer ();
            }

            @Override
            public void onFinish() {
                seekBar.setProgress(0);

                // -------- CALLBACK FUNCTION ALARM
                Alarm();

                countDownButton.setText("Done");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!flag) {
                            btnShort.performClick();
                        } else {
                            btnPoMoDoRo.performClick();
                        }
                    }
                }, 3500);

                if (flag) {
                    timeLeftInMilliseconds = 8000;
                } else {
                    timeLeftInMilliseconds = 6000;
                }

                // ----------- CALLBACK KHI CLICK VÀO BUTTON PLAY OR PAUSE
                clickBtnStarStop();
            }
        }.start();

        // ------------ SET EDIT TEXT BUTTON = PAUSE
        countDownButton.setText("Tạm dừng");
    }

    // ------------- FUNCTION ALARM
    public void Alarm () {
        mediaPlayer.start();
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnShort.setEnabled(false);
                btnPoMoDoRo.setEnabled(false);
                countDownButton.setEnabled(false);
            }

            @Override
            public void onFinish() {
                mediaPlayer.stop();
                countDownButton.setEnabled(true);
            }
        }.start();
    }


    // --------------- FUNCTION PAUSE (nếu thời gian đã dừng)
    public void stopTimer () {
        seekBar.setProgress(seekBar.getProgress() - 1);

        // -------------- STOP TIMER
        countDownTimer.cancel();

        // -------------- SET EDIT TEXT BUTTON = START
        countDownButton.setText("Bắt đầu");
    }

    // --------------- CẬP NHẬT THỜI GIAN
    public void updateTimer() {
        long second = (timeLeftInMilliseconds % 60000) / 1000;

        // minutes = 120000 / 60000 = 2 (m)
        int minutes = (int) timeLeftInMilliseconds / 60000;

        // seconds = (120000 % 60000) / 1000 = 0;
        int seconds = (int) second;

        // Thời gian còn lại
        String timeLeftText;

        // 2:
        timeLeftText = "" + minutes;
        timeLeftText += ":";

        // nếu seconds(s) < 10
        if (seconds < 10) {
            //2:0
            timeLeftText += "0";
        }

        // 2:00
        timeLeftText += seconds;

        countDownText.setText(timeLeftText);

    }


    // ------------- SET SEEKBAR CHẠY THEO THỜI GIAN
    public void SeekBarTimer () {

        if (flag) {
            seekBar.setProgress(seekBar.getProgress() + 1);

        } else {
            seekBar.setProgress(seekBar.getProgress() + 1);
        }

    }

    // -------------- CLICK BUTTON FAB
    public void clickFAB () {
        btnFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheck) {
                    stopTimer();
                }

                Intent intent = new Intent(MainActivity.this, Todolist.class);
                startActivity(intent);
            }
        });
    }

    // Thanh trạng thái trong suốt
    private void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            View decorView = window.getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}