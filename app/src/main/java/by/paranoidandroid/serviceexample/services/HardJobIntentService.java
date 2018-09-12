package by.paranoidandroid.serviceexample.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import static by.paranoidandroid.serviceexample.utils.Constants.*;

public class HardJobIntentService extends IntentService {
    private boolean isDestroyed;

    public HardJobIntentService() {
        super(HardJobIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        isDestroyed = false;
        showToast(START_INTENT_SERVICE_MSG);
        try {
            for (int i = 0; i <= MAX_VALUE && !isDestroyed; i++) {
                Thread.sleep(SLEEP_TIME);
                updateProgress(i);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        showToast(FINISH_INTENT_SERVICE_MSG);
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }

    private void updateProgress(int progress) {
        Intent broadcastIntent = new Intent(UPDATE_PROGRESS_ACTION);
        broadcastIntent.putExtra(PROGRESS_KEY, progress);
        broadcastIntent.putExtra(SERVICE_KEY, INTENT_SERVICE);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        isDestroyed = true;
        super.onDestroy();
    }
}