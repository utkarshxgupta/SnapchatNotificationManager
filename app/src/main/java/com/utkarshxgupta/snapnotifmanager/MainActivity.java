package com.utkarshxgupta.snapnotifmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import com.utkarshxgupta.snapnotifmanager.Adapter.WhitelistAdapter;
import com.utkarshxgupta.snapnotifmanager.Model.WhitelistModel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Intent intent = new Intent(this, ForeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        else {
            startService(intent);
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
}