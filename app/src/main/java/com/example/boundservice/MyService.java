package com.example.boundservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {

    int mCount=0;
    String MY_CHANNEL ="MY_CHANNEL";
    NotificationManager notificationManager;
    int REQUEST_CODE = 123;
    Notification mNotification;
    Context mContext = null;
    OnListenerCount onListenerCount;
    boolean mCreate = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification = createNotification(this,mCount).build();
        startForeground(1,mNotification);
        mContext = this;
        mCreate =true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mCreate){
            CountDownTimer countDownTimer = new CountDownTimer(100000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mCount++;
                    mNotification = createNotification(mContext,mCount).build();
                    notificationManager.notify(1,mNotification);
                    if(onListenerCount != null){
                        onListenerCount.onCount(mCount);
                    }
                }

                @Override
                public void onFinish() {

                }
            };
            countDownTimer.start();
            mCreate =false;
        }
        return START_NOT_STICKY;
    }

    private NotificationCompat.Builder createNotification(Context context, int count){
        // Intent activity từ notification
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("message","Hello Main");
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,REQUEST_CODE , intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Tạo notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,MY_CHANNEL)
                .setContentTitle("Đếm giá trị")
                .setContentText("Count "+mCount)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.cho))
                .addAction(R.drawable.ic_launcher_background,"Open App",pendingIntent);

        //Kiểm tra phiên bản máy
        // Máy 26 trở lên mới có
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    MY_CHANNEL, "CHANNEL", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        return notification;
    }
    class MyBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    public void setOnListenerCount(OnListenerCount onListenerCount) {
        this.onListenerCount = onListenerCount;
    }
}

