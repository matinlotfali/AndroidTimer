package ir.lotfaliei.timer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import ir.lotfaliei.timer.controllers.TimerController;
import ir.lotfaliei.timer.views.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    TimerController timerController;

    ProgressBar progressBar;
    TextView textView;
    Button pauseButton;
    MenuItem restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);
        pauseButton = findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getClass().getName(), "pause button pressed");
                if (timerController.getIsPaused())
                    startTimer();
                else
                    stopTimer();
            }
        });

        timerController = new TimerController(this) {
            @Override
            protected void tick() {
                updateProgress();
            }
        };

        updateProgress();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerController.shutDown();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void startTimer() {
        timerController.resume();
        pauseButton.setText(R.string.Pause);
        invalidateOptionsMenu();
    }

    private void stopTimer() {
        timerController.pause();
        pauseButton.setText(R.string.Resume);
        invalidateOptionsMenu();
    }

    private void updateProgress() {
        final int timerSeconds = timerController.getTimerSeconds();
        final String time_str = String.format(Locale.ENGLISH, "%02d:%02d", timerSeconds / 60, timerSeconds % 60);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(timerSeconds);
                textView.setText(time_str);
                if (timerSeconds == 0)
                    stopTimer();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        restartButton = (MenuItem) menu.findItem(R.id.action_restart);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        restartButton.setVisible(timerController.getIsPaused());
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_restart) {
            Log.d(getClass().getName(), "restart button pressed");
            timerController.restart();
            updateProgress();
        }

        return super.onOptionsItemSelected(item);
    }
}
