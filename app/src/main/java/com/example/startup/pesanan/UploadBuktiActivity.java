package com.example.startup.pesanan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.startup.ApiClient;
import com.example.startup.ApiService;
import com.example.startup.FileUtils;
import com.example.startup.R;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadBuktiActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bukti);

        ImageView imagePreview = findViewById(R.id.imagePreview);
        Button btnUpload = findViewById(R.id.btnUpload);
        Button btnPick = findViewById(R.id.btnPick);

        orderNumber = getIntent().getStringExtra("order_number");

        btnPick.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnUpload.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImage(imageUri);
            } else {
                Toast.makeText(this, "Pilih gambar dulu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ((ImageView) findViewById(R.id.imagePreview)).setImageURI(imageUri);
        }
    }

    private void uploadImage(Uri uri) {
        String filePath = FileUtils.getPath(this, uri);
        if (filePath == null) {
            Toast.makeText(this, "Tidak bisa mendapatkan path gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("bukti", file.getName(), requestFile);
        RequestBody orderBody = RequestBody.create(MediaType.parse("text/plain"), orderNumber);

        ApiService apiService = ApiClient.getService();
        Call<ResponseBody> call = apiService.uploadBukti(orderBody, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UploadBuktiActivity.this, "Bukti berhasil diupload", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // ✅ Ini penting agar fragment tahu bahwa upload berhasil
                    finish();


                } else {
                    Toast.makeText(UploadBuktiActivity.this, "Upload gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UploadBuktiActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
