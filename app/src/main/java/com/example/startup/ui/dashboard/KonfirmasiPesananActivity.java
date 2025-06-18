package com.example.startup.ui.dashboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startup.ApiService;
import com.example.startup.CheckoutSuccesActivity;
import com.example.startup.LoginActivity;
import com.example.startup.R;

import com.example.startup.ApiClient; // pastikan ini yang dipakai
import com.example.startup.ResponsePromo;
import com.example.startup.UserProfile;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KonfirmasiPesananActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderItems;
    private TextView tvOngkir, tvTotalHarga, tvNomorRekening, etLamaKirim;
    private Button btnConfirmOrder, btnCekOngkir;
    private EditText etAlamat, etNama, etKodepos, etNohp;
    RadioGroup radioGroupPembayaran;
    RadioButton radioCOD, radioTransfer;

    CardView layoutRekening;
    private Spinner spinnerKurir, spinnerProvinsiTujuan, spinnerKotaTujuan;

    private ArrayAdapter<RajaOngkirCityResponse.City> cityAdapter;
    private ArrayAdapter<RajaOngkirProvinseResponse.Province> provinceAdapter;

    private List<RajaOngkirCityResponse.City> cityList = new ArrayList<>();
    private List<RajaOngkirProvinseResponse.Province> provinceList = new ArrayList<>();

    private OrderConfirmationAdapter adapter;
    private ArrayList<OrderItem> orderItems;
    private double totalHarga;
    private double biayaOngkir = 0;

    // Fitur Promo Variables
    private EditText etKodePromo;
    private Button btnTerapkanPromo;
    private TextView tvStatusPromo, tvHapusPromo, tvDiskonPromo;
    private LinearLayout layoutStatusPromo, layoutDiskonPromo;
    private double diskonPromo = 0;
    private String kodePromoAktif = "";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_konfirmasi_pesanan);

        // Initialize existing views
        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        tvOngkir = findViewById(R.id.tvOngkir);
        tvTotalHarga = findViewById(R.id.tvTotalHarga);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnCekOngkir = findViewById(R.id.btnCekOngkir);
        etAlamat = findViewById(R.id.etAlamat);
        spinnerKurir = findViewById(R.id.spinnerKurir);
        spinnerProvinsiTujuan = findViewById(R.id.spinnerProvinsiTujuan);
        spinnerKotaTujuan = findViewById(R.id.spinnerKotaTujuan);
        radioGroupPembayaran = findViewById(R.id.radioGroupPembayaran);
        radioCOD = findViewById(R.id.radioCOD);
        radioTransfer = findViewById(R.id.radioTransfer);
        layoutRekening = findViewById(R.id.layoutRekening);
        tvNomorRekening = findViewById(R.id.tvNomorRekening);
        etNama = findViewById(R.id.etNama);
        etNohp = findViewById(R.id.etNohp);
        etLamaKirim = findViewById(R.id.etLamaKirim);
        etKodepos = findViewById(R.id.etKodepos);

        // Initialize promo views
        etKodePromo = findViewById(R.id.etKodePromo);
        btnTerapkanPromo = findViewById(R.id.btnTerapkanPromo);
        tvStatusPromo = findViewById(R.id.tvStatusPromo);
        tvHapusPromo = findViewById(R.id.tvHapusPromo);
        layoutStatusPromo = findViewById(R.id.layoutStatusPromo);
        tvDiskonPromo = findViewById(R.id.tvDiskonPromo);
        layoutDiskonPromo = findViewById(R.id.layoutDiskonPromo);

        loadUserProfile(email);
        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);

        orderItems = getIntent().getParcelableArrayListExtra("order_items");
        totalHarga = getIntent().getDoubleExtra("total_harga", 0);
        if (orderItems == null) orderItems = new ArrayList<>();

        setupRecyclerView();

        tvOngkir.setText("Ongkir: Rp -");
        tvTotalHarga.setText(String.format("Total: Rp %,.0f", totalHarga));

        // Kurir adapter
        ArrayAdapter<String> kurirAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"jne", "pos", "tiki"});
        kurirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKurir.setAdapter(kurirAdapter);

        // Province & City adapter
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinceList);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvinsiTujuan.setAdapter(provinceAdapter);

        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKotaTujuan.setAdapter(cityAdapter);

        loadProvinceList();

        // Listener: saat provinsi dipilih, load kotanya
        spinnerProvinsiTujuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RajaOngkirProvinseResponse.Province selectedProvince = provinceList.get(position);
                if (selectedProvince != null) {
                    loadCityList(selectedProvince.getProvinceId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Pembayaran radio button logic
        radioGroupPembayaran.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioTransfer) {
                    layoutRekening.setVisibility(View.VISIBLE);
                    tvNomorRekening.setText("BCA 1234567890 a.n. fashora");

                    tvNomorRekening.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String textToCopy = tvNomorRekening.getText().toString();
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Nomor Rekening", textToCopy);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(getApplicationContext(), "Nomor rekening disalin ke clipboard", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    layoutRekening.setVisibility(View.GONE);
                }
            }
        });

        // Promo event listeners
        btnTerapkanPromo.setOnClickListener(v -> terapkanKodePromo());
        tvHapusPromo.setOnClickListener(v -> hapusKodePromo());

        btnCekOngkir.setOnClickListener(v -> hitungOngkir());

        btnConfirmOrder.setOnClickListener(v -> {
            if (etAlamat.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Alamat tujuan wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (biayaOngkir == 0) {
                Toast.makeText(this, "Silakan cek ongkir terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }
            kirimPesananKeServer();
        });
    }

    private void loadUserProfile(String email) {
        ApiService apiService = ApiClient.getService();
        Call<List<UserProfile>> call = apiService.getProfile(email);

        call.enqueue(new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserProfile user = response.body().get(0);
                    etNama.setText(user.getNama());
                    etAlamat.setText(user.getAlamat());
                    etNohp.setText(user.getTelp());
                    etKodepos.setText(user.getKodepos());
                } else {
                    Toast.makeText(KonfirmasiPesananActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                Toast.makeText(KonfirmasiPesananActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderConfirmationAdapter(orderItems);
        recyclerViewOrderItems.setAdapter(adapter);
    }

    private void loadProvinceList() {
        RajaOngkirService service = OngkirApiClient.getClient().create(RajaOngkirService.class);
        Call<RajaOngkirProvinseResponse> call = service.getProvinces("e631b28880aac0beca29e0a0d2de38b7");

        call.enqueue(new Callback<RajaOngkirProvinseResponse>() {
            @Override
            public void onResponse(Call<RajaOngkirProvinseResponse> call, Response<RajaOngkirProvinseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provinceList.clear();
                    provinceList.addAll(response.body().getRajaongkir().getResults());
                    provinceAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(KonfirmasiPesananActivity.this, "Gagal load daftar provinsi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RajaOngkirProvinseResponse> call, Throwable t) {
                Toast.makeText(KonfirmasiPesananActivity.this, "Error load provinsi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCityList(String provinceId) {
        RajaOngkirService service = OngkirApiClient.getClient().create(RajaOngkirService.class);
        Call<RajaOngkirCityResponse> call = service.getCitiesByProvince(provinceId, "e631b28880aac0beca29e0a0d2de38b7");

        call.enqueue(new Callback<RajaOngkirCityResponse>() {
            @Override
            public void onResponse(Call<RajaOngkirCityResponse> call, Response<RajaOngkirCityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cityList.clear();
                    cityList.addAll(response.body().getRajaongkir().getResults());
                    cityAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(KonfirmasiPesananActivity.this, "Gagal load daftar kota", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RajaOngkirCityResponse> call, Throwable t) {
                Toast.makeText(KonfirmasiPesananActivity.this, "Error load kota: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hitungOngkir() {
        String origin = "501";
        String destination = cityList.get(spinnerKotaTujuan.getSelectedItemPosition()).getCity_id();
        String courier = spinnerKurir.getSelectedItem().toString();
        int weight = 1000;

        RajaOngkirService service = OngkirApiClient.getClient().create(RajaOngkirService.class);

        Call<RajaOngkirResponse> call = service.getOngkir(
                "e631b28880aac0beca29e0a0d2de38b7",
                origin,
                destination,
                weight,
                courier
        );

        call.enqueue(new Callback<RajaOngkirResponse>() {
            @Override
            public void onResponse(Call<RajaOngkirResponse> call, Response<RajaOngkirResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RajaOngkirResponse.Result result = response.body()
                            .getRajaongkir().getResults().get(0);
                    RajaOngkirResponse.Cost cost = result.getCosts().get(0);
                    RajaOngkirResponse.CostDetail costDetail = cost.getCost().get(0);

                    biayaOngkir = costDetail.getValue();
                    String estimasi = costDetail.getEtd();

                    tvOngkir.setText(String.format("Ongkir: Rp %,.0f", biayaOngkir));
                    etLamaKirim.setText("Estimasi: " + estimasi + " hari");

                    // Update total dengan diskon promo
                    updateTotalHarga();
                } else {
                    Toast.makeText(KonfirmasiPesananActivity.this, "Gagal menghitung ongkir", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RajaOngkirResponse> call, Throwable t) {
                Toast.makeText(KonfirmasiPesananActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========== FITUR PROMO METHODS ==========

    private void terapkanKodePromo() {
        String kodePromo = etKodePromo.getText().toString().trim().toUpperCase();

        if (kodePromo.isEmpty()) {
            Toast.makeText(this, "Masukkan kode promo terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call API untuk validasi promo
        validasiKodePromoAPI(kodePromo);
    }

    // Perbaikan untuk method validasiKodePromoAPI
    private void validasiKodePromoAPI(String kodePromo) {
        ApiService apiService = ApiClient.getService();
        Call<ResponsePromo> call = apiService.validasiPromo(kodePromo, totalHarga);

        call.enqueue(new Callback<ResponsePromo>() {
            @Override
            public void onResponse(Call<ResponsePromo> call, Response<ResponsePromo> response) {
                if (response.isSuccessful()) {
                    try {
                        ResponsePromo responsePromo = response.body();
                        if (responsePromo != null && responsePromo.isSuccess()) {
                            // Promo valid
                            diskonPromo = responsePromo.getNilaiDiskon();
                            kodePromoAktif = kodePromo;

                            tvStatusPromo.setText("✅ " + responsePromo.getMessage());
                            tvStatusPromo.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                            layoutStatusPromo.setVisibility(View.VISIBLE);

                            tvDiskonPromo.setText(String.format("- Rp %,.0f", diskonPromo));
                            layoutDiskonPromo.setVisibility(View.VISIBLE);

                            updateTotalHarga();
                            Toast.makeText(KonfirmasiPesananActivity.this, "Kode promo berhasil diterapkan!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Promo tidak valid
                            String errorMessage = responsePromo != null ? responsePromo.getMessage() : "Kode promo tidak valid";
                            tvStatusPromo.setText("❌ " + errorMessage);
                            tvStatusPromo.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            layoutStatusPromo.setVisibility(View.VISIBLE);

                            layoutDiskonPromo.setVisibility(View.GONE);

                            Toast.makeText(KonfirmasiPesananActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PROMO_ERROR", "Error parsing response: " + e.getMessage());
                        handlePromoError("Error parsing response promo");
                    }
                } else {
                    // Handle error response
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e("PROMO_API_ERROR", "Response code: " + response.code() + ", Error body: " + errorBody);

                        // Coba parse error body sebagai JSON
                        if (!errorBody.isEmpty()) {
                            try {
                                Gson gson = new Gson();
                                ResponsePromo errorResponse = gson.fromJson(errorBody, ResponsePromo.class);
                                if (errorResponse != null && !errorResponse.getMessage().isEmpty()) {
                                    handlePromoError(errorResponse.getMessage());
                                    return;
                                }
                            } catch (Exception parseError) {
                                Log.e("PROMO_ERROR", "Cannot parse error response as JSON: " + parseError.getMessage());
                            }
                        }

                        handlePromoError("Server error: " + response.message());
                    } catch (IOException e) {
                        Log.e("PROMO_ERROR", "Error reading error body: " + e.getMessage());
                        handlePromoError("Gagal validasi promo");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponsePromo> call, Throwable t) {
                Log.e("PROMO_NETWORK_ERROR", "Network error: " + t.getMessage());
                handlePromoError("Error koneksi: " + t.getMessage());
            }
        });
    }

    // Method helper untuk handle error promo
    private void handlePromoError(String errorMessage) {
        tvStatusPromo.setText("❌ " + errorMessage);
        tvStatusPromo.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        layoutStatusPromo.setVisibility(View.VISIBLE);
        layoutDiskonPromo.setVisibility(View.GONE);

        // Reset promo state
        diskonPromo = 0;
        kodePromoAktif = "";
        updateTotalHarga();

        Toast.makeText(KonfirmasiPesananActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void hapusKodePromo() {
        diskonPromo = 0;
        kodePromoAktif = "";
        etKodePromo.setText("");
        layoutStatusPromo.setVisibility(View.GONE);
        layoutDiskonPromo.setVisibility(View.GONE);

        updateTotalHarga();
        Toast.makeText(this, "Kode promo dihapus", Toast.LENGTH_SHORT).show();
    }

    private void updateTotalHarga() {
        double totalAkhir = totalHarga + biayaOngkir - diskonPromo;
        tvTotalHarga.setText(String.format("Total: Rp %,.0f", totalAkhir));
    }

    // ========== END FITUR PROMO ==========

    private void kirimPesananKeServer() {
        String email = sharedPreferences.getString("email", null);
        if (email == null) {
            Toast.makeText(this, "User belum login, silakan login dulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        String namaPelanggan = etNama.getText().toString().trim();
        if (namaPelanggan.isEmpty()) {
            Toast.makeText(this, "Nama tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            return;
        }

        String alamat = etAlamat.getText().toString().trim();
        if (alamat.isEmpty()) {
            Toast.makeText(this, "Alamat tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            return;
        }

        String telpPelanggan = etNohp.getText().toString().trim();
        if (telpPelanggan.isEmpty()) {
            Toast.makeText(this, "No HP tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            return;
        }

        String kodeposPelanggan = etKodepos.getText().toString().trim();
        if (kodeposPelanggan.isEmpty()) {
            Toast.makeText(this, "Kode Pos tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            return;
        }

        String lamaKirim = etLamaKirim.getText().toString().trim();
        if (lamaKirim.isEmpty()) {
            Toast.makeText(this, "Lama kirim tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            return;
        }
        String kodePromoToSend = kodePromoAktif.isEmpty() ? "" : kodePromoAktif;
        double diskonPromoToSend = kodePromoAktif.isEmpty() ? 0.0 : diskonPromo;

        String kurir = spinnerKurir.getSelectedItem().toString();
        double ongkir = biayaOngkir;
        double totalBayar = totalHarga + ongkir - diskonPromo; // Include diskon promo

        RajaOngkirProvinseResponse.Province selectedProvince = (RajaOngkirProvinseResponse.Province) spinnerProvinsiTujuan.getSelectedItem();
        if (selectedProvince == null) {
            Toast.makeText(this, "Provinsi tujuan belum dipilih.", Toast.LENGTH_SHORT).show();
            return;
        }
        String provinceId = selectedProvince.getProvinceId();
        String provinceName = selectedProvince.getProvinceName();

        RajaOngkirCityResponse.City selectedCity = (RajaOngkirCityResponse.City) spinnerKotaTujuan.getSelectedItem();
        if (selectedCity == null) {
            Toast.makeText(this, "Kota tujuan belum dipilih.", Toast.LENGTH_SHORT).show();
            return;
        }
        String cityId = selectedCity.getCity_id();
        String cityName = selectedCity.getCity_name();

        String metodePembayaran = "";
        int selectedId = radioGroupPembayaran.getCheckedRadioButtonId();
        if (selectedId == R.id.radioCOD) {
            metodePembayaran = "COD";
        } else if (selectedId == R.id.radioTransfer) {
            metodePembayaran = "Transfer";
        } else {
            Toast.makeText(this, "Pilih metode pembayaran", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil order items
        OrderHelper orderHelper = new OrderHelper(this, email);
        List<OrderItem> orderItems = orderHelper.getOrderItems();

        for (OrderItem item : orderItems) {
            int qty = item.getQuantity();
            int stok = item.getStok();

            if (qty > stok && stok > 0) {
                Toast.makeText(this, "Melebihi stok tersedia", Toast.LENGTH_LONG).show();
                return;
            }
        }

        List<Map<String, Object>> mappedItems = new ArrayList<>();
        for (OrderItem item : orderItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("product_kode", item.getKode());
            map.put("product_merk", item.getMerk());
            map.put("qty", item.getQuantity());
            map.put("harga_jual", item.getHargajual());
            map.put("subtotal", item.getSubtotal());
            mappedItems.add(map);
        }

        Gson gson = new Gson();
        String orderItemsJson = gson.toJson(mappedItems);

        Log.d("CEK_JSON_ORDER_ITEMS", orderItemsJson);
        Log.d("LAMA_KIRIM", "Lama kirim: " + lamaKirim);
        Log.d("PROMO_INFO", "Kode: " + kodePromoAktif + ", Diskon: " + diskonPromo);

        ApiService apiService = ApiClient.getService();
        Call<ResponsePesanan> call = apiService.simpanPesanan(
                email,
                namaPelanggan,
                telpPelanggan,
                alamat,
                kodeposPelanggan,
                provinceId,
                provinceName,
                cityId,
                cityName,
                kurir,
                ongkir,
                totalBayar,
                metodePembayaran,
                lamaKirim,
                orderItemsJson,
                kodePromoToSend,    // Sudah di-handle untuk empty string
                diskonPromoToSend     // Parameter diskon
        );

        call.enqueue(new Callback<ResponsePesanan>() {
            @Override
            public void onResponse(Call<ResponsePesanan> call, Response<ResponsePesanan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponsePesanan body = response.body();
                    if (body.isSuccess()) {
                        Toast.makeText(getApplicationContext(), "Pesanan berhasil: " + body.getMessage(), Toast.LENGTH_SHORT).show();
                        orderHelper.clearOrderItems();

                        Intent intent = new Intent(KonfirmasiPesananActivity.this, CheckoutSuccesActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        Toast.makeText(getApplicationContext(), "Gagal: " + body.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API_ERROR", "Kode: " + response.code() + ", body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Gagal baca errorBody: " + e.getMessage());
                    }
                    Toast.makeText(getApplicationContext(), "Response error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePesanan> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}