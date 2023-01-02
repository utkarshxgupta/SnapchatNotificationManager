package com.utkarshxgupta.snapnotifmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;

import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.TextView;

import com.utkarshxgupta.snapnotifmanager.Adapter.WhitelistAdapter;
import com.utkarshxgupta.snapnotifmanager.Model.WhitelistModel;
import com.utkarshxgupta.snapnotifmanager.R;
import com.utkarshxgupta.snapnotifmanager.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private RecyclerView tasksRecyclerView;
    private WhitelistAdapter tasksAdapter;
    private FloatingActionButton fab;
    private List<WhitelistModel> whiteList;
    public static DatabaseHandler db;
    private TextView emptyList;
    private boolean notifs, battery;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        notifs = isNotificationServiceEnabled();
        battery = pm.isIgnoringBatteryOptimizations(packageName);

        if (!notifs || !battery) {
            Intent permissionsIntent = new Intent(this, PermissionXyzActivity.class);
            startActivity(permissionsIntent);
        }

        db = new DatabaseHandler(this);
        db.openDatabase();

        whiteList = new ArrayList<>();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new WhitelistAdapter(db, this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        fab = findViewById(R.id.fab);
        emptyList = (TextView) findViewById(R.id.emptyDatabaseText);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecylerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        whiteList = db.getAllPersons();
        Collections.reverse(whiteList);
        tasksAdapter.setTasks(whiteList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewPerson.newInstance().show(getSupportFragmentManager(),AddNewPerson.TAG);
            }
        });

        tasksRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                emptyList.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (whiteList.isEmpty())
                    emptyList.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!whiteList.isEmpty()) {
            emptyList.setVisibility(View.INVISIBLE);
        }
        else {
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        whiteList = db.getAllPersons();
        Collections.reverse(whiteList);
        tasksAdapter.setTasks(whiteList);
        tasksAdapter.notifyDataSetChanged();
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