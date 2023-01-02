package com.utkarshxgupta.snapnotifmanager.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.utkarshxgupta.snapnotifmanager.Model.WhitelistModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "whiteListDatabase";
    private static final String whiteListTable = "whiteList";
    private static final String ID = "id";
    private static final String PERSON = "person"; //task
    private static final String CREATE_WHITELIST_TABLE = "CREATE TABLE " + whiteListTable + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PERSON + " TEXT)";
    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super (context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WHITELIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the existing old tables
        db.execSQL("DROP TABLE IF EXISTS " + whiteListTable);
        //Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertPerson(WhitelistModel person) {
        ContentValues cv = new ContentValues();
        cv.put(PERSON, person.getTask());
        db.insert(whiteListTable, null, cv);
    }

    @SuppressLint("Range")
    public List<WhitelistModel> getAllPersons() {
        List <WhitelistModel> personList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(whiteListTable, null, null, null, null, null, null, null);
            if (cur!=null) {
                if (cur.moveToFirst()) {
                    do {
                        WhitelistModel person = new WhitelistModel();
                        person.setId(cur.getInt(cur.getColumnIndex(ID)));
                        person.setTask(cur.getString(cur.getColumnIndex(PERSON)));
                        personList.add(person);
                    } while (cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            cur.close();
        }
        return personList;
    }

    public void updatePerson (int id, String person) {
        ContentValues cv = new ContentValues();
        cv.put(PERSON, person);
        db.update(whiteListTable, cv, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void deletePerson (int id) {
        db.delete(whiteListTable, ID + "=?", new String[] {String.valueOf(id)});
    }
}
