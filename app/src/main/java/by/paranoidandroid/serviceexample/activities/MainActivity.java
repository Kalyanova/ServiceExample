package by.paranoidandroid.serviceexample.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.StringDef;
import by.paranoidandroid.serviceexample.R;
import by.paranoidandroid.serviceexample.services.HardJobIntentService;
import by.paranoidandroid.serviceexample.services.HardJobService;

public class MainActivity extends Activity {
    public static final String UPDATE_PROGRESS_ACTION = "by.paranoidandroid.serviceexample.action.UPDATE_PROGRESS",
            PROGRESS_KEY = "PROGRESS_KEY", SERVICE_KEY = "SERVICE_KEY",
            SIMPLE_SERVICE = "SIMPLE_SERVICE", INTENT_SERVICE = "INTENT_SERVICE",
            FINISH_TEXT = "Done!";
    private static final int MAX_VALUE = 100;
    private BroadcastReceiver progressReceiver;
    private boolean isSimpleServiceStarted;
    private boolean isIntentServiceStarted;
    private @FinishedService String finishedService;

    @StringDef({SIMPLE_SERVICE, INTENT_SERVICE})
    private @interface FinishedService {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvProgress = findViewById(R.id.tv_progress);
        Button btnStartIntentService = findViewById(R.id.btn_intent_service);
        Button btnStartService = findViewById(R.id.btn_service);

        btnStartIntentService.setOnClickListener(view -> startIntentService());
        btnStartService.setOnClickListener(view -> startSimpleService());

        if (progressReceiver == null) {
            progressReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    int progress = intent.getIntExtra(PROGRESS_KEY, 0);
                    if (progress == MAX_VALUE) {
                        tvProgress.setText(FINISH_TEXT);
                        if (!TextUtils.isEmpty(intent.getStringExtra(SERVICE_KEY))) {
                            finishedService = intent.getStringExtra(SERVICE_KEY);
                            switch (finishedService) {
                                case SIMPLE_SERVICE:
                                    isSimpleServiceStarted = false;
                                    break;
                                case INTENT_SERVICE:
                                    isIntentServiceStarted = false;
                                    break;
                            }
                        }
                    } else {
                        tvProgress.setText(String.valueOf(progress));
                    }
                }
            };
        }
    }

    private void startIntentService() {
        if (isSimpleServiceStarted) {
            if (stopService(new Intent(this, HardJobService.class))) {
                isSimpleServiceStarted = false;
            }
        }
        if (!isIntentServiceStarted) {
            startService(new Intent(this, HardJobIntentService.class));
            isIntentServiceStarted = true;
        }
    }

    private void startSimpleService() {
        if (isIntentServiceStarted) {
            if (stopService(new Intent(this, HardJobIntentService.class))) {
                isIntentServiceStarted = false;
            }
        }
        if (!isSimpleServiceStarted) {
            startService(new Intent(this, HardJobService.class));
            isSimpleServiceStarted = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(UPDATE_PROGRESS_ACTION);
        registerReceiver(progressReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(progressReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (isSimpleServiceStarted) {
            stopService(new Intent(this, HardJobService.class));
        }
        if (isIntentServiceStarted) {
            stopService(new Intent(this, HardJobIntentService.class));
        }
        super.onDestroy();
    }
}