package com.example.startup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.startup.ui.dashboard.OrderHelper;
import com.example.startup.ui.product.Product;
import com.example.startup.ui.product.ViewResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProductActivity extends AppCompatActivity {

    ImageSlider imageSlider;
    TextView tvMerk, tvHarga, tvStok, tvDeskripsi, tvKategori, tvViewCount;
    TextView btnUkuranL, btnUkuranXL, btnUkuranXXL;
    Button btnBeli;

    Product product;
    Map<String, Integer> ukuranStokMap = new HashMap<>();
    String selectedUkuran = "M"; // default ukuran

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        imageSlider = findViewById(R.id.imageSlider);
        tvMerk = findViewById(R.id.tvDetailMerk);
        tvHarga = findViewById(R.id.tvDetailHarga);
        tvStok = findViewById(R.id.tvDetailStok);
        tvDeskripsi = findViewById(R.id.tvDetailDeskripsi);
        tvKategori = findViewById(R.id.tvDetailKategori);
        tvViewCount = findViewById(R.id.tvDetailViewCount);
        btnUkuranL = findViewById(R.id.btnUkuranL);
        btnUkuranXL = findViewById(R.id.btnUkuranXL);
        btnUkuranXXL = findViewById(R.id.btnUkuranXXL);
        btnBeli = findViewById(R.id.btnBeli);

        product = (Product) getIntent().getSerializableExtra("produk");

        if (product != null) {
            updateAndFetchView(product.getKode());

            tvMerk.setText(product.getMerk());
            tvHarga.setText(String.format("Rp %,d", (int) product.getHargajual()));
            tvDeskripsi.setText(product.getDeskripsi());
            tvKategori.setText("Kategori: " + (product.getKategori() != null ? product.getKategori() : "-"));

            // Listener untuk klik ukuran
            btnUkuranL.setOnClickListener(v -> {
                selectedUkuran = "L";
                highlightSelectedUkuran(btnUkuranL, btnUkuranXL, btnUkuranXXL);
                updateStokText();
            });

            btnUkuranXL.setOnClickListener(v -> {
                selectedUkuran = "XL";
                highlightSelectedUkuran(btnUkuranXL, btnUkuranL, btnUkuranXXL);
                updateStokText();
            });

            btnUkuranXXL.setOnClickListener(v -> {
                selectedUkuran = "M"; // ini "M" karena di UI kamu btnUkuranXXL textnya "M"
                highlightSelectedUkuran(btnUkuranXXL, btnUkuranL, btnUkuranXL);
                updateStokText();
            });

            // Default selection: "M" di btnUkuranXXL
            selectedUkuran = "M";
            highlightSelectedUkuran(btnUkuranXXL, btnUkuranL, btnUkuranXL);

            btnBeli.setOnClickListener(v -> {
                int stok = ukuranStokMap.getOrDefault(selectedUkuran, 0);
                if (stok > 0) {
                    SharedPreferences userPrefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
                    String email = userPrefs.getString("email", "guest@example.com");
                    OrderHelper orderHelper = new OrderHelper(this, email);
                    orderHelper.addToOrder(product, selectedUkuran);

                    Toast.makeText(this, product.getMerk() + " ukuran " + selectedUkuran + " ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Stok habis untuk ukuran tersebut!", Toast.LENGTH_SHORT).show();
                }
            });

            loadProductImages(product.getKode());
            loadUkuranStok(product.getKode());
        }
    }

    private void updateStokText() {
        int stok = ukuranStokMap.getOrDefault(selectedUkuran, 0);
        tvStok.setText(stok > 0 ? "Tersedia (" + stok + ")" : "Tidak tersedia");
    }

    private void highlightSelectedUkuran(TextView selected, TextView... others) {
        selected.setSelected(true);
        for (TextView other : others) {
            other.setSelected(false);
        }
    }

    private void updateAndFetchView(String kode) {
        ApiClient.getService().tambahView(kode).enqueue(new Callback<ViewResponse>() {
            @Override
            public void onResponse(Call<ViewResponse> call, Response<ViewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int updatedViewCount = response.body().getView();
                    tvViewCount.setText("Dilihat: " + updatedViewCount + "x");
                }
            }

            @Override
            public void onFailure(Call<ViewResponse> call, Throwable t) {
                Log.e("ViewAPI", "Gagal memperbarui view: " + t.getMessage());
            }
        });
    }

    private void loadProductImages(String kode) {
        ApiClient.getService().getProductImages(kode).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> fotoNames = response.body();
                    List<SlideModel> slideModels = new ArrayList<>();
                    for (String fotoName : fotoNames) {
                        String fullUrl = "https://posyandukorowelangkulon.my.id/startup/Gambar_Product/" + fotoName;
                        slideModels.add(new SlideModel(fullUrl, ScaleTypes.FIT));
                    }
                    imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("ImageAPI", "Gagal load gambar: " + t.getMessage());
            }
        });
    }

    private void loadUkuranStok(String kodeProduk) {
        ApiClient.getService().getUkuranStok(kodeProduk).enqueue(new Callback<List<UkuranStok>>() {
            @Override
            public void onResponse(Call<List<UkuranStok>> call, Response<List<UkuranStok>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (UkuranStok item : response.body()) {
                        ukuranStokMap.put(item.getUkuran(), item.getStok());
                    }
                    updateStokText(); // update stok saat data ukuran sudah diterima
                } else {
                    Toast.makeText(DetailProductActivity.this, "Gagal memuat ukuran.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UkuranStok>> call, Throwable t) {
                Toast.makeText(DetailProductActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
