package com.example.startup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "mobile";
    public static final int DB_VERSION = 9; // Naikkan jika ada perubahan struktur tabel
    public static final String TABLE_NAME = "tbl_pelanggan";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nama TEXT, " +
                "alamat TEXT, " +
                "kota TEXT, " +
                "provinsi TEXT, " +
                "kodepos TEXT, " +
                "telp TEXT, " +
                "status TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "foto TEXT, " +
                "is_logged_in INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tabel lama jika ada perubahan struktur
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Menyimpan atau memperbarui data profil berdasarkan email
    public boolean saveProfile(String email, String nama, String alamat, String kota, String provinsi, String telp, String kodepos, String foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("nama", nama);
        values.put("alamat", alamat);
        values.put("kota", kota);
        values.put("provinsi", provinsi);
        values.put("telp", telp);
        values.put("kodepos", kodepos);
        values.put("foto", foto);

        Cursor cursor = db.rawQuery("SELECT email FROM " + TABLE_NAME + " WHERE email=?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();

        if (exists) {
            // Update jika email sudah ada
            int rowsAffected = db.update(TABLE_NAME, values, "email=?", new String[]{email});
            return rowsAffected > 0;
        } else {
            // Insert jika data baru
            values.put("status", "1"); // default status
            values.put("password", ""); // default password kosong
            values.put("is_logged_in", 1); // langsung login
            long result = db.insert(TABLE_NAME, null, values);
            return result != -1;
        }
    }

    // Mengatur status login pengguna
    public void setLoginStatus(String email, boolean isLoggedIn) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Logout semua pengguna terlebih dahulu
        ContentValues reset = new ContentValues();
        reset.put("is_logged_in", 0);
        db.update(TABLE_NAME, reset, null, null);

        // Loginkan pengguna yang dipilih
        ContentValues values = new ContentValues();
        values.put("is_logged_in", isLoggedIn ? 1 : 0);
        db.update(TABLE_NAME, values, "email=?", new String[]{email});
    }

    // Memperbarui data profil pengguna
    public boolean updateUserProfile(String email, String nama, String alamat, String kota, String provinsi,
                                     String telp, String kodepos, String foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nama", nama);
        cv.put("alamat", alamat);
        cv.put("kota", kota);
        cv.put("provinsi", provinsi);
        cv.put("telp", telp);
        cv.put("kodepos", kodepos);
        if (foto != null) {
            cv.put("foto", foto);
        }
        int result = db.update(TABLE_NAME, cv, "email=?", new String[]{email});
        return result > 0;
    }

    // Mendapatkan email pengguna yang sedang login
    public String getLoggedInEmail() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM " + TABLE_NAME + " WHERE is_logged_in = 1 LIMIT 1", null);
        String email = "";
        if (cursor.moveToFirst()) {
            email = cursor.getString(0);
        }
        cursor.close();
        return email;
    }

    // Mendapatkan nama dan foto berdasarkan email
    public Cursor getProfile(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT nama, foto FROM " + TABLE_NAME + " WHERE email = ?", new String[]{email});
    }
}
