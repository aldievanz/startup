package com.example.startup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startup.ui.product.Product;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerWishlist;
    private TextView textEmptyWishlist;
    private WishlistAdapter wishlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        recyclerWishlist = findViewById(R.id.recyclerWishlist);
        textEmptyWishlist = findViewById(R.id.textEmptyWishlist);
        recyclerWishlist.setLayoutManager(new LinearLayoutManager(this));

        loadWishlist();
    }

    private void loadWishlist() {
        SharedPreferences prefs = getSharedPreferences("wishlist", Context.MODE_PRIVATE);
        Map<String, ?> allItems = prefs.getAll();
        List<Product> wishlist = new ArrayList<>();
        Gson gson = new Gson();

        for (Map.Entry<String, ?> entry : allItems.entrySet()) {
            if (entry.getKey().startsWith("wishlist_")) {
                String json = (String) entry.getValue();
                Product product = gson.fromJson(json, Product.class);
                wishlist.add(product);
            }
        }

        if (wishlist.isEmpty()) {
            recyclerWishlist.setVisibility(View.GONE);
            textEmptyWishlist.setVisibility(View.VISIBLE);
        } else {
            recyclerWishlist.setVisibility(View.VISIBLE);
            textEmptyWishlist.setVisibility(View.GONE);
            wishlistAdapter = new WishlistAdapter(wishlist);
            recyclerWishlist.setAdapter(wishlistAdapter);
        }
    }
}
