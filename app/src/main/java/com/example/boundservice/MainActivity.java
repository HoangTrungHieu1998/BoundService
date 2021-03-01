package com.example.boundservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MyService myService;
    TextView mTvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvCount = findViewById(R.id.txtCount);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this,MyService.class);
        startService(intent);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);

    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            myService = myBinder.getService();
            myService.setOnListenerCount(new OnListenerCount() {
                @Override
                public void onCount(int count) {
                    mTvCount.setText(count+"");
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}