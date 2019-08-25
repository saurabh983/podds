package com.dosse.airpods;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class IntroActivity extends AppCompatActivity {

    private Timer t;
    RelativeLayout backgrooundchange;
    TextView intro1, intro2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        backgrooundchange = findViewById(R.id.backgrooundchange);

        intro1 = findViewById(R.id.intro1);
        intro2 = findViewById(R.id.intro2);
        SharedPreferences preferences = getSharedPreferences("push", Context.MODE_PRIVATE);

        if (preferences.getBoolean("dark",false)){
            backgrooundchange.setBackgroundColor(getResources().getColor(R.color.black));
            intro1.setTextColor(getResources().getColor(R.color.white));
            intro2.setTextColor(getResources().getColor(R.color.whitelight));
        }
        else {
            backgrooundchange.setBackgroundColor(getResources().getColor(R.color.white));
            intro1.setTextColor(getResources().getColor(R.color.black));
            intro2.setTextColor(getResources().getColor(R.color.lightblcak));
        }




        ((Button)findViewById(R.id.allowBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //allow button clicked, ask for permissions
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1); //location (for BLE)
                try { //run in background
                    if(!getSystemService(PowerManager.class).isIgnoringBatteryOptimizations(getPackageName())) {
                        Intent intent = new Intent();
                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                } catch (Throwable t) {
                }
            }
        });
        //wait for permissions to be granted. When they are granted, go to MainActivity
        t= new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean ok=true;
                try {
                    if (!getSystemService(PowerManager.class).isIgnoringBatteryOptimizations(getPackageName())) ok = false;
                }catch(Throwable t){}
                if (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ok=false;
                if(ok){
                    t.cancel();
                    Intent i=new Intent(IntroActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        },0,100);

    }

    @Override
    protected void onDestroy() { //activity destroyed (or screen rotated). destroy the timer too
        super.onDestroy();
        if(t!=null) t.cancel();
    }
}
