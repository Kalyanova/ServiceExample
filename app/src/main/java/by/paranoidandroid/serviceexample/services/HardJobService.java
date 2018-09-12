package by.paranoidandroid.serviceexample.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class HardJobService extends Service {
    private static final String UPDATE_PROGRESS_ACTION = "by.paranoidandroid.serviceexample.action.UPDATE_PROGRESS",
            PROGRESS_KEY = "PROGRESS_KEY", SERVICE_KEY = "SERVICE_KEY", SERVICE_VALUE = "SIMPLE_SERVICE",
            START_SERVICE_MSG = "Starting Simple Service", FINISH_SERVICE_MSG = "Finishing Simple Service";
    private static final int MAX_VALUE = 100, SLEEP_TIME = 100;
    private boolean isDestroyed;
    private ServiceHandler serviceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread handlerThread = new HandlerThread(HardJobService.class.getSimpleName(),
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        // Start the new handler thread
        handlerThread.start();
        // Get looper out of thread
        Looper serviceLooper = handlerThread.getLooper();
        // Create instance of ServiceHandler class that receives instance of ServiceLooper
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isDestroyed = false;
        Toast.makeText(this, START_SERVICE_MSG, Toast.LENGTH_SHORT).show();
        // Call a new service handler. The service ID can be used to identify the service
        Message message = serviceHandler.obtainMessage();
        message.arg1 = startId;
        // Send message to ServiceHandler
        serviceHandler.sendMessage(message);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int startId = msg.arg1;
            try {
                for (int i = 0; i <= MAX_VALUE && !isDestroyed; i++) {
                    Thread.sleep(SLEEP_TIME);
                    updateProgress(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            stopSelf(startId);
        }
    }

    private void updateProgress(int progress) {
        Intent broadcastIntent = new Intent(UPDATE_PROGRESS_ACTION);
        broadcastIntent.putExtra(PROGRESS_KEY, progress);
        broadcastIntent.putExtra(SERVICE_KEY, SERVICE_VALUE);
        sendBroadcast(broadcastIntent);
    }

    @Override public void onDestroy() {
        isDestroyed = true;
        Toast.makeText(this, FINISH_SERVICE_MSG, Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}