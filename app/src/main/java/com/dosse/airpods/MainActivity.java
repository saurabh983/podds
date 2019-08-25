package com.dosse.airpods;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SwitchCompat mode_switch;
    RelativeLayout backgrooundchange;
    TextView main1,main2,supportedDevices;
    ImageView tick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mode_switch = findViewById(R.id.mode_switch);
        main1 = findViewById(R.id.main1);
        supportedDevices = findViewById(R.id.supportedDevices);
        tick = findViewById(R.id.tick);
        main2 = findViewById(R.id.main2);
        backgrooundchange = findViewById(R.id.backgrooundchange);
        //check if Bluetooth LE is available on this device. If not, show an error

        SharedPreferences preferences = getSharedPreferences("push", Context.MODE_PRIVATE);

        if (preferences.getBoolean("dark",false)){
            backgrooundchange.setBackgroundColor(getResources().getColor(R.color.black));
            main1.setTextColor(getResources().getColor(R.color.white));
            supportedDevices.setTextColor(getResources().getColor(R.color.whitelight));
            main2.setTextColor(getResources().getColor(R.color.whitelight));
            mode_switch.setTextColor(getResources().getColor(R.color.white));
            mode_switch.setText("Light Mode   ");
            tick.setColorFilter(getResources().getColor(R.color.white));
            mode_switch.setChecked(true);
        }
        else {
            backgrooundchange.setBackgroundColor(getResources().getColor(R.color.white));
            main1.setTextColor(getResources().getColor(R.color.black));
            supportedDevices.setTextColor(getResources().getColor(R.color.lightblcak));
            main2.setTextColor(getResources().getColor(R.color.lightblcak));
            tick.setColorFilter(getResources().getColor(R.color.black));
            mode_switch.setText("Dark Mode   ");
            mode_switch.setChecked(false);
        }
        BluetoothAdapter btAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (btAdapter == null || (btAdapter.isEnabled() && btAdapter.getBluetoothLeScanner() == null)) {
            Intent i = new Intent(this, NoBTActivity.class);
            startActivity(i);
            finish();
            return;
        }
        //check if all permissions have been granted
        boolean ok = true;
        try {
            if (!getSystemService(PowerManager.class).isIgnoringBatteryOptimizations(getPackageName()))
                ok = false;
        } catch (Throwable t) {
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ok = false;
        if (ok) {
            Starter.startPodsService(getApplicationContext());
        } else {
            Intent i = new Intent(this, IntroActivity.class);
            startActivity(i);
            finish();
        }
        ((Button) (findViewById(R.id.mainHide))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //hide app clicked
                PackageManager p = getPackageManager();
                p.setComponentEnabledSetting(new ComponentName(MainActivity.this, MainActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                Toast.makeText(getApplicationContext(), getString(R.string.hideClicked), Toast.LENGTH_LONG).show();
                finish();
            }
        });

        mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    SharedPreferences.Editor editor = getSharedPreferences("push", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("dark", true);
                    editor.apply();
                }
                else {
                    SharedPreferences.Editor editor = getSharedPreferences("push", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("dark", false);
                    editor.apply();
                }

                startActivity(
                        Intent.makeRestartActivityTask(
                               new  ComponentName(MainActivity.this, MainActivity.class)));
//                startActivity(new Intent(MainActivity.this, MainActivity.class));
//                finishAndRemoveTask();

            }
        });



    }


}
