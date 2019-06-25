package ir.lotfaliei.timer;

import android.content.DialogInterface;
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

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TimerController timerController;

    ProgressBar progressBar;
    TextView textView;
    Button pauseButton, restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);
        pauseButton = findViewById(R.id.pauseButton);
        restartButton = findViewById(R.id.restartButton);

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

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getClass().getName(), "restart button pressed");
                timerController.restart();
                updateProgress();
            }
        });

        timerController = new TimerController(this) {
            @Override
            void tick() {
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
        restartButton.setEnabled(false);
    }

    private void stopTimer() {
        timerController.pause();
        pauseButton.setText(R.string.Resume);
        restartButton.setEnabled(true);
    }

    private void updateProgress() {
        final int timerSeconds = timerController.getTimerSeconds();
        final String time_str = String.format(Locale.ENGLISH, "%02d:%02d", timerSeconds / 60, timerSeconds % 60);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(timerSeconds);
                textView.setText(time_str);
                pauseButton.setEnabled(timerSeconds > 0);
                restartButton.setEnabled(timerController.getIsPaused());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
