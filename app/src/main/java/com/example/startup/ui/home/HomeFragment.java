package com.example.startup.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.startup.ApiClient;
import com.example.startup.FlashSaleAdapter;
import com.example.startup.Kategori;
import com.example.startup.KategoriAdapter;
import com.example.startup.ProductByKategoriActivity;
import com.example.startup.R;
import com.example.startup.ui.dashboard.OrderHelper;
import com.example.startup.ui.product.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvKategori, rvRekomendasi, rvFlashSale;
    private TextView tvCountdown;
    private CountDownTimer countDownTimer;
    private View flashSaleSection;
    private final long flashSaleDuration = 60000;
    private final String PREF_FLASH_SALE_END_TIME = "flash_sale_end_time";

    private EditText etSearch;
    private List<Product> allRekomendasi = new ArrayList<>();
    private RekomendasiAdapter rekomendasiAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvWelcome;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Swipe refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadProdukRekomendasiByView();
            loadProdukFlashSale();
            swipeRefreshLayout.setRefreshing(false);
        });
        tvWelcome = view.findViewById(R.id.tvWelcome);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String namaUser = sharedPreferences.getString("nama", "User");

// Setel teks ke tvWelcome
        tvWelcome.setText("Selamat datang, " + namaUser + "!");


        // Image Slider
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.promo1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.promo2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.promo3, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
        imageSlider.startSliding(3000);




        // Kategori
        rvKategori = view.findViewById(R.id.rvKategori);
        List<Kategori> kategoriList = new ArrayList<>();
        kategoriList.add(new Kategori("pria", R.drawable.produk1));
        kategoriList.add(new Kategori("wanita", R.drawable.produkw1));

        KategoriAdapter kategoriAdapter = new KategoriAdapter(getContext(), kategoriList);
        rvKategori.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvKategori.setAdapter(kategoriAdapter);
        kategoriAdapter.setOnItemClickListener(kategori -> {
            Intent intent = new Intent(getContext(), ProductByKategoriActivity.class);
            intent.putExtra("kategori", kategori.getNama());
            startActivity(intent);
        });

        // Rekomendasi
        rvRekomendasi = view.findViewById(R.id.rvRekomendasi);
        rvRekomendasi.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rekomendasiAdapter = new RekomendasiAdapter(getContext(), allRekomendasi, this::onProductClick);
        rvRekomendasi.setAdapter(rekomendasiAdapter);
        loadProdukRekomendasiByView();

        // Search
        etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRekomendasi(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void startFlashSaleCountdown() {
        long now = System.currentTimeMillis();
        long endTime = getFlashSaleEndTime();

        if (endTime < now) {
            tvCountdown.setText("00:00:00");
            flashSaleSection.setVisibility(View.GONE);
        } else {
            flashSaleSection.setVisibility(View.VISIBLE);
            long duration = endTime - now;
            countDownTimer = new CountDownTimer(duration, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long hours = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                    long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                    long seconds = (millisUntilFinished / 1000) % 60;
                    String timeLeft = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                    tvCountdown.setText(timeLeft);
                }

                @Override
                public void onFinish() {
                    tvCountdown.setText("00:00:00");
                    flashSaleSection.setVisibility(View.GONE);
                }
            }.start();

            loadProdukFlashSale();
        }
    }

    private void loadProdukRekomendasiByView() {
        ApiClient.getService().getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    Collections.sort(products, (p1, p2) -> Integer.compare(p2.getView(), p1.getView()));
                    allRekomendasi.clear();
                    allRekomendasi.addAll(products.subList(0, Math.min(5, products.size())));
                    rekomendasiAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {}
        });
    }

    private void loadProdukFlashSale() {
        ApiClient.getService().getFlashSaleProducts().enqueue(new Callback<List<com.example.startup.fs.Product>>() {
            @Override
            public void onResponse(Call<List<com.example.startup.fs.Product>> call,
                                   Response<List<com.example.startup.fs.Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FlashSaleAdapter adapter = new FlashSaleAdapter(getContext(), response.body(), product -> {});
                    rvFlashSale.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<com.example.startup.fs.Product>> call, Throwable t) {}
        });
    }

    public void onProductClick(Product product) {
        SharedPreferences userPrefs = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String email = userPrefs.getString("email", "guest@example.com");

        OrderHelper orderHelper = new OrderHelper(requireContext(), email);
        orderHelper.addToOrder(product, "M"); // atau "L", "XL", dst sesuai pilihan


        Toast.makeText(requireContext(), product.getMerk() + " ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
    }

    private void filterRekomendasi(String keyword) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : allRekomendasi) {
            if (product.getMerk().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(product);
            }
        }

        rekomendasiAdapter = new RekomendasiAdapter(getContext(), filteredList, this::onProductClick);
        rvRekomendasi.setAdapter(rekomendasiAdapter);
    }

    private long getFlashSaleEndTime() {
        SharedPreferences prefs = requireContext().getSharedPreferences("flash_sale", Context.MODE_PRIVATE);
        long savedTime = prefs.getLong(PREF_FLASH_SALE_END_TIME, 0);
        long now = System.currentTimeMillis();

        if (savedTime == 0 || savedTime < now) {
            long endTime = now + flashSaleDuration;
            prefs.edit().putLong(PREF_FLASH_SALE_END_TIME, endTime).apply();
            return endTime;
        }

        return savedTime;
    }
}
