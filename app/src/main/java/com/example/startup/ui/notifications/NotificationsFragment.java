package com.example.startup.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.startup.LoginActivity;
import com.example.startup.R;
import com.example.startup.ApiClient;
import com.example.startup.ApiService;
import com.example.startup.UserProfile;
import com.example.startup.WishlistActivity;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private LinearLayout btnEditProfile, btnKontakKami, recyclerWishlist;

    private View btnLogout, btnWishlist;
    private LinearLayout btnPesananSaya;
    private TextView textName, textEmail;
    private ShapeableImageView imageProfile;
    private String email, userType;
    private LottieAnimationView loadingAnimation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);
        userType = sharedPreferences.getString("userType", null);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn || email == null || email.isEmpty() || "guest".equals(userType)) {
            sharedPreferences.edit().clear().apply();

            Toast.makeText(getContext(), "Sesi login tidak ditemukan, silakan login kembali", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            requireActivity().finish();
            return new FrameLayout(requireContext());
        }

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        btnEditProfile = root.findViewById(R.id.btnEditProfile);
        btnKontakKami = root.findViewById(R.id.btnKontakKami);
        btnLogout = root.findViewById(R.id.btnLogout);
        btnPesananSaya = root.findViewById(R.id.btnPesananSaya);
        btnWishlist = root.findViewById(R.id.btnWishlist);
        textName = root.findViewById(R.id.textName);
        textEmail = root.findViewById(R.id.textEmail);
        imageProfile = root.findViewById(R.id.imageProfile);
        loadingAnimation = root.findViewById(R.id.loadingAnimation);


        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

        btnEditProfile.setOnClickListener(v -> navController.navigate(R.id.editProfileFragment));
        btnKontakKami.setOnClickListener(v -> navController.navigate(R.id.kontakKamiFragment));
        btnPesananSaya.setOnClickListener(v -> navController.navigate(R.id.pesananSayaFragment));

        btnLogout.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            Toast.makeText(getActivity(), "Berhasil logout", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        btnWishlist.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), WishlistActivity.class);
            startActivity(intent);
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        loadingAnimation.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getService();
        Call<List<UserProfile>> call = apiService.getProfile(email);

        call.enqueue(new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                loadingAnimation.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserProfile user = response.body().get(0);

                    textName.setText(user.getNama());
                    textEmail.setText(user.getEmail());

                    String foto = user.getFoto();
                    if (foto != null && !foto.isEmpty()) {
                        Glide.with(requireContext())
                                .load("https://posyandukorowelangkulon.my.id/startup//img/" + foto)
                                .placeholder(R.drawable.ic_orang)
                                .error(android.R.drawable.stat_notify_error)
                                .into(imageProfile);
                    } else {
                        imageProfile.setImageResource(R.drawable.ic_orang);
                    }

                } else {
                    Toast.makeText(getContext(), "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                loadingAnimation.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWishlist() {
        recyclerWishlist.removeAllViews();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        Set<String> wishlist = sharedPreferences.getStringSet("wishlist", new HashSet<>());

        if (wishlist == null || wishlist.isEmpty()) {
            TextView emptyText = new TextView(getContext());
            emptyText.setText("Wishlist kosong.");
            emptyText.setTextSize(16f);
            emptyText.setPadding(16, 16, 16, 16);
            recyclerWishlist.addView(emptyText);
        } else {
            for (String item : wishlist) {
                TextView itemView = new TextView(getContext());
                itemView.setText("• " + item);
                itemView.setTextSize(16f);
                itemView.setPadding(16, 8, 16, 8);
                recyclerWishlist.addView(itemView);
            }
        }
    }
}
