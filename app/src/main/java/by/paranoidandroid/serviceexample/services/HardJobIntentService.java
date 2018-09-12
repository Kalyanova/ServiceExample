package by.paranoidandroid.serviceexample.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
public class HardJobIntentService extends IntentService {
    private static final String UPDATE_PROGRESS_ACTION = "by.paranoidandroid.serviceexample.action.UPDATE_PROGRESS",
            PROGRESS_KEY = "PROGRESS_KEY", SERVICE_KEY = "SERVICE_KEY", SERVICE_VALUE = "INTENT_SERVICE",
            START_SERVICE_MSG = "Starting IntentService", FINISH_SERVICE_MSG = "Finishing IntentService";
    private static final int MAX_VALUE = 100, SLEEP_TIME = 100;
    private boolean isDestroyed;

    public HardJobIntentService() {
        super(HardJobIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        isDestroyed = false;
        showToast(START_SERVICE_MSG);
        try {
            for (int i = 0; i <= MAX_VALUE && !isDestroyed; i++) {
                Thread.sleep(SLEEP_TIME);
                updateProgress(i);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        showToast(FINISH_SERVICE_MSG);
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }

    private void updateProgress(int progress) {
        Intent broadcastIntent = new Intent(UPDATE_PROGRESS_ACTION);
        broadcastIntent.putExtra(PROGRESS_KEY, progress);
        broadcastIntent.putExtra(SERVICE_KEY, SERVICE_VALUE);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        isDestroyed = true;
        super.onDestroy();
    }
}