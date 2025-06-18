package com.example.startup;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private EditText etFullName, etAddress, etCity, etProvince, etPhone, etPostalCode;
    private Button btnSave, btnChoosePhoto;
    private ImageView imgProfile;
    private Uri imageUri;
    private Dialog loadingDialog;

    private DatabaseHelper dbHelper;
    private String userEmail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("userPrefs", getContext().MODE_PRIVATE);
        boolean isGuest = sharedPreferences.getBoolean("isGuest", false);

        if (isGuest) {
            Toast.makeText(getContext(), "Fitur ini hanya tersedia untuk pengguna terdaftar", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
            return null;
        }

        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        etFullName = root.findViewById(R.id.etFullName);
        etAddress = root.findViewById(R.id.etAddress);
        etCity = root.findViewById(R.id.etCity);
        etProvince = root.findViewById(R.id.etProvince);
        etPhone = root.findViewById(R.id.etPhone);
        etPostalCode = root.findViewById(R.id.etPostalCode);
        btnSave = root.findViewById(R.id.btnSaveProfile);
        btnChoosePhoto = root.findViewById(R.id.btnChoosePhoto);
        imgProfile = root.findViewById(R.id.imgProfile);

        dbHelper = new DatabaseHelper(getContext());
        userEmail = dbHelper.getLoggedInEmail();

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        loadProfile(userEmail);

        btnChoosePhoto.setOnClickListener(v -> checkPermission());

        btnSave.setOnClickListener(v -> {
            String nama = etFullName.getText().toString();
            String alamat = etAddress.getText().toString();
            String kota = etCity.getText().toString();
            String provinsi = etProvince.getText().toString();
            String telepon = etPhone.getText().toString();
            String kodePos = etPostalCode.getText().toString();

            RequestBody emailRB = RequestBody.create(MediaType.parse("text/plain"), userEmail);
            RequestBody namaRB = RequestBody.create(MediaType.parse("text/plain"), nama);
            RequestBody alamatRB = RequestBody.create(MediaType.parse("text/plain"), alamat);
            RequestBody kotaRB = RequestBody.create(MediaType.parse("text/plain"), kota);
            RequestBody provinsiRB = RequestBody.create(MediaType.parse("text/plain"), provinsi);
            RequestBody teleponRB = RequestBody.create(MediaType.parse("text/plain"), telepon);
            RequestBody kodeposRB = RequestBody.create(MediaType.parse("text/plain"), kodePos);

            loadingDialog.show();

            MultipartBody.Part fotoPart = null;
            if (imageUri != null) {
                try {
                    ContentResolver resolver = getContext().getContentResolver();
                    String fileName = getFileName(imageUri);
                    InputStream inputStream = resolver.openInputStream(imageUri);
                    File tempFile = new File(getContext().getCacheDir(), fileName);
                    FileOutputStream outputStream = new FileOutputStream(tempFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();

                    RequestBody fileReq = RequestBody.create(MediaType.parse(getMimeType(imageUri)), tempFile);
                    fotoPart = MultipartBody.Part.createFormData("foto", fileName, fileReq);
                } catch (Exception e) {
                    loadingDialog.dismiss();
                    Toast.makeText(getContext(), "Gagal membaca gambar", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            ApiService apiService = ApiClient.getService();
            Call<ResponseBody> call = apiService.updateProfile(
                    emailRB, namaRB, alamatRB, kotaRB, provinsiRB, teleponRB, kodeposRB, fotoPart
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    loadingDialog.dismiss();
                    if (response.isSuccessful()) {
                        boolean updated = dbHelper.updateUserProfile(userEmail, nama, alamat, kota, provinsi, telepon, kodePos, null);
                        Toast.makeText(getContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        loadProfile(userEmail);
                    } else {
                        Toast.makeText(getContext(), "Gagal menyimpan ke server", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loadingDialog.dismiss();
                    Toast.makeText(getContext(), "Kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return root;
    }

    private void loadProfile(String email) {
        ApiService apiService = ApiClient.getService();
        Call<List<UserProfile>> call = apiService.getProfile(email);

        call.enqueue(new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserProfile profile = response.body().get(0);

                    etFullName.setText(profile.getNama());
                    etAddress.setText(profile.getAlamat());
                    etCity.setText(profile.getKota());
                    etProvince.setText(profile.getProvinsi());
                    etPhone.setText(profile.getTelp());
                    etPostalCode.setText(profile.getKodepos());

                    if (profile.getFoto() != null && !profile.getFoto().isEmpty()) {
                        Glide.with(getContext())
                                .load("https://posyandukorowelangkulon.my.id/startup/img/" + profile.getFoto())
                                .placeholder(R.drawable.ic_orang)
                                .error(android.R.drawable.stat_notify_error)
                                .into(imgProfile);
                    } else {
                        imgProfile.setImageResource(R.drawable.ic_orang);
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        STORAGE_PERMISSION_CODE);
            } else {
                openFileChooser();
            }
        } else {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            } else {
                openFileChooser();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(getContext(), "Izin akses ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }


    private String getMimeType(Uri uri) {
        ContentResolver cr = getContext().getContentResolver();
        return cr.getType(uri);
    }
}
