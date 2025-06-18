package com.example.startup.pesanan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.startup.ApiClient;
import com.example.startup.ApiService;
import com.example.startup.R;
import com.example.startup.ui.dashboard.OrderItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PesananSayaFragment extends Fragment {

    private RecyclerView recyclerView;
    private PesananAdapter adapter;
    private List<Pesanan> pesananList = new ArrayList<>();
    private LottieAnimationView loadingAnimation;
    private ActivityResultLauncher<Intent> uploadLauncher;
    private SwipeRefreshLayout swipeRefreshLayout; // ✅ Tambahkan ini

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pesanan_saya, container, false);

        // Inisialisasi launcher
        uploadLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getContext(), "Upload berhasil", Toast.LENGTH_SHORT).show();
                        SharedPreferences prefs = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        String email = prefs.getString("email", null);
                        if (email != null) {
                            fetchOrders(email); // Refresh data setelah upload
                        }
                    } else {
                        Toast.makeText(getContext(), "Upload dibatalkan", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Inisialisasi komponen
        recyclerView = root.findViewById(R.id.recyclerPesanan);
        loadingAnimation = root.findViewById(R.id.loadingAnimation);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences prefs = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if (email != null) {
            fetchOrders(email);
        } else {
            Toast.makeText(getContext(), "Email tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (email != null) {
                fetchOrders(email);
            }
        });

        return root;
    }


    private void fetchOrders(String email) {
        loadingAnimation.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getService();
        Call<List<Order>> call = apiService.getOrders(email);

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                loadingAnimation.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false); // ✅ Matikan animasi swipe refresh

                if (response.isSuccessful() && response.body() != null) {
                    pesananList.clear();
                    for (Order order : response.body()) {
                        Pesanan pesanan = new Pesanan();
                        pesanan.setOrderNumber(order.getOrderNumber());
                        pesanan.setTglOrder(order.getTglOrder());
                        pesanan.setAlamat(order.getAlamat());
                        pesanan.setProvinceName(order.getProvinceName() != null ? order.getProvinceName() : "Tidak diketahui");
                        pesanan.setCityName(order.getCityName() != null ? order.getCityName() : "Tidak diketahui");

                        try {
                            pesanan.setOngkir(Double.parseDouble(order.getOngkir()));
                            pesanan.setTotalBayar(Double.parseDouble(order.getTotalBayar()));
                        } catch (NumberFormatException e) {
                            pesanan.setOngkir(0);
                            pesanan.setTotalBayar(0);
                        }

                        pesanan.setLamaKirim(order.getLamaKirim() != null ? order.getLamaKirim() : "Tidak diketahui");
                        pesanan.setKurir(order.getKurir());
                        pesanan.setStatus(order.getStatus());
                        pesanan.setMetodePembayaran(order.getMetodePembayaran());
                        pesanan.setItems(order.getItems());
                        pesanan.setSubtotal(order.getSubtotalAsDouble());
                        pesanan.setOngkir(order.getOngkirAsDouble());

                        pesananList.add(pesanan);
                    }

                    adapter = new PesananAdapter(getContext(), pesananList, uploadLauncher);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Gagal mengambil data pesanan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                loadingAnimation.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false); // ✅ Pastikan animasi juga dimatikan di onFailure
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
