package com.example.startup.ui.product;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.startup.databinding.FragmentProductBinding;
import com.example.startup.ui.dashboard.OrderHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;
    private ProductAdapter adapter;
    private OrderHelper orderHelper;
    private List<Product> productList;
    private String selectedCategory = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences userPrefs = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String email = userPrefs.getString("email", "guest@example.com");
        orderHelper = new OrderHelper(requireContext(), email);


        // Setup RecyclerView
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Fetch products from the server
        fetchProduct();

        // Setup SearchView for filtering products
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) {
                    adapter.filter(query, selectedCategory);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.filter(newText, selectedCategory);
                }
                return true;
            }
        });

        // Button listeners for category filtering
        binding.btnAll.setOnClickListener(v -> filterByCategory(""));
        binding.btnSelempang.setOnClickListener(v -> filterByCategory("pria"));
        binding.btnHandbag.setOnClickListener(v -> filterByCategory("wanita"));


        return root;
    }

    // Method to filter products by category
    private void filterByCategory(String category) {
        selectedCategory = category;
        String searchQuery = binding.searchView.getQuery().toString();
        if (adapter != null) {
            adapter.filter(searchQuery, category);
        }
    }

    // Method to fetch product data from the API
    private void fetchProduct() {
        RegisterAPI apiService = RetrofitClient.getRetrofitInstance().create(RegisterAPI.class);
        Call<List<Product>> call = apiService.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    adapter = new ProductAdapter(getContext(), productList, product -> {
                        orderHelper.addToOrder(product, "M"); // default sementara

                        Toast.makeText(getContext(), product.getMerk() + " ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                    });
                    // Set adapter after receiving the data
                    binding.recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Data kosong atau gagal diambil!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal mengambil data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Optionally, refresh the data when fragment is resumed (remove if unnecessary)
    @Override
    public void onResume() {
        super.onResume();
        // fetchProduct(); // Uncomment if you need to refresh data every time
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Ensure no memory leak by setting binding to null when the view is destroyed
    }
}
