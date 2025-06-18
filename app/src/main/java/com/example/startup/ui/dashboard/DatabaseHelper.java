package com.example.startup.ui.dashboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "orders.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createOrderTable = "CREATE TABLE order_table (" +
                "kode TEXT PRIMARY KEY," +
                "merk TEXT," +
                "hargajual REAL," +
                "quantity INTEGER," +
                "subtotal REAL," +
                "foto TEXT)";
        db.execSQL(createOrderTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS order_table");
        onCreate(db);
    }
}
