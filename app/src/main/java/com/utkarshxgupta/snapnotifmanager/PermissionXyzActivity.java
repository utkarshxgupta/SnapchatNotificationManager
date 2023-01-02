package com.utkarshxgupta.snapnotifmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.utkarshxgupta.snapnotifmanager.R;

import java.util.Locale;

public class PermissionXyzActivity extends AppCompatActivity {

    private Button notifAccess, bgPermission, batteryOpti, proceed;
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private boolean notifs, battery, farzi;
    private PowerManager pm;
    private String packageName;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_xyz);
        getSupportActionBar().hide();

        farzi = false;
        packageName = getPackageName();
        pm = (PowerManager) getSystemService(POWER_SERVICE);

        notifAccess = findViewById(R.id.button);
        bgPermission = findViewById(R.id.button2);
        batteryOpti = findViewById(R.id.button3);
        proceed = findViewById(R.id.button5);

        if (battery) {
            bgPermission.setText("GRANTED");
            bgPermission.setEnabled(false);
        }

        if (notifs) {
            notifAccess.setText("GRANTED");
            notifAccess.setEnabled(false);
        }

        notifAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(i);
            }
        });

        bgPermission.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("BatteryLife")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String packageName = getPackageName();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        });

        batteryOpti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAOneplusDevice()) {
                    startActivity(new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS));
                    Toast.makeText(getApplicationContext(), "Go to Advanced settings -> Optimise battery use -> Don't Optimise", Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                }
                farzi = true;
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifs = isNotificationServiceEnabled();
                battery = pm.isIgnoringBatteryOptimizations(packageName);
                if (notifs && battery)
                    finish();
                else
                    Toast.makeText(getApplicationContext(), "Grant all permissions first and then click proceed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public boolean isAOneplusDevice()
    {
        String manufacturer = android.os.Build.MANUFACTURER;
        return manufacturer.toLowerCase(Locale.ENGLISH).contains("oneplus");
    }

    @Override
    protected void onResume() {
        super.onResume();

        notifs = isNotificationServiceEnabled();
        if (notifs) {
            notifAccess.setText("GRANTED");
            notifAccess.setEnabled(false);
        }

        battery = pm.isIgnoringBatteryOptimizations(packageName);
        if (battery) {
            bgPermission.setText("GRANTED");
            bgPermission.setEnabled(false);
        }

        if (farzi) {
            batteryOpti.setText("GRANTED");
            batteryOpti.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Grant all permissions first and then click proceed", Toast.LENGTH_SHORT).show();
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}