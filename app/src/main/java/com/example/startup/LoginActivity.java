package com.example.startup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.startup.ui.product.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    public static final String URL = new ServerAPI().BASE_URL;

    Dialog loadingDialog;
    Button login;
    TextView email, password;
    TextView registerd;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek session, jika sudah login langsung ke MainActivity
        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        registerd = findViewById(R.id.registerd);

        registerd.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        Button guestLogin = findViewById(R.id.guest_login);
        guestLogin.setOnClickListener(v -> {
            editor.putBoolean("isLoggedIn", true);
            editor.putBoolean("isGuest", true);
            editor.putString("userType", "guest"); // Tambahkan userType guest
            editor.putString("email", "guest@example.com"); // Email dummy untuk guest
            editor.apply();

            Toast.makeText(LoginActivity.this, "Login sebagai Tamu", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        login.setOnClickListener(v -> {
            String vemail = email.getText().toString().trim();
            String vpass = password.getText().toString().trim();

            if (vemail.isEmpty() || vpass.isEmpty()) {
                Toast.makeText(this, "Email dan Password wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            loadingDialog = new Dialog(LoginActivity.this);
            loadingDialog.setContentView(R.layout.dialog_loading);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loadingDialog.show();

            prosesLogin(vemail, vpass);
        });
    }

    void prosesLogin(String vemail, String vpassword) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        api.login(vemail, vpassword).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingDialog.dismiss();
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getString("result").equals("1")) {
                            JSONObject data = json.getJSONObject("data");

                            // Simpan data login ke SharedPreferences
                            editor.putBoolean("isLoggedIn", true);
                            editor.putBoolean("isGuest", false);
                            editor.putString("userType", "user"); // userType regular
                            editor.putString("email", data.getString("email"));
                            editor.putString("nama", data.getString("nama"));
                            editor.putString("foto", data.optString("foto", ""));
                            editor.putInt("id", data.getInt("id"));
                            editor.apply();

                            // Simpan profil ke SQLite lokal
                            DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this);
                            dbHelper.saveProfile(
                                    data.getString("email"),
                                    data.getString("nama"),
                                    data.optString("alamat", ""),
                                    data.optString("kota", ""),
                                    data.optString("provinsi", ""),
                                    data.optString("telp", ""),
                                    data.optString("kodepos", ""),
                                    data.optString("foto", "")

                            );

                            dbHelper.setLoginStatus(data.getString("email"), true);

                            Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();

                            // Pindah ke MainActivity dan tutup LoginActivity
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            showAlert(json.getString("message"));
                        }
                    } else {
                        showAlert("Terjadi kesalahan pada server.");
                    }
                } catch (JSONException | IOException e) {
                    showAlert("Kesalahan saat parsing response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingDialog.dismiss();
                showAlert("Gagal Terhubung ke Server: " + t.getMessage());
            }
        });
    }

    void showAlert(String message) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setNegativeButton("Retry", null)
                .create()
                .show();
    }
}
