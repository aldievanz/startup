package com.example.startup.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.startup.ui.product.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderHelper {
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private String orderKey;

    public OrderHelper(Context context, String email) {
        sharedPreferences = context.getSharedPreferences("order_prefs", Context.MODE_PRIVATE);
        orderKey = "order_items_" + email;
    }

    public void addToOrder(Product product, String ukuran) {
        List<OrderItem> orderItems = getOrderItems();
        boolean exists = false;

        for (OrderItem item : orderItems) {
            if (item.getKode().equals(product.getKode()) && item.getUkuran().equals(ukuran)) {
                // ✅ Cek apakah melebihi stok
                if (item.getQuantity() + 1 > item.getStok()) {
                    return; // atau tampilkan Toast: "Stok tidak cukup"
                }
                item.setQuantity(item.getQuantity() + 1);
                exists = true;
                break;
            }
        }

        if (!exists) {
            // ✅ Cek apakah stok > 0 sebelum tambahkan item baru
            if (product.getStok() <= 0) return;
            orderItems.add(new OrderItem(product, ukuran));
        }

        saveOrderItems(orderItems);
    }


    public void removeFromOrder(String productCode, String ukuran) {
        List<OrderItem> orderItems = getOrderItems();
        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getKode().equals(productCode) &&
                    orderItems.get(i).getUkuran().equals(ukuran)) {
                orderItems.remove(i);
                break;
            }
        }
        saveOrderItems(orderItems);
    }

    public void updateQuantity(String productCode, String ukuran, int quantity) {
        List<OrderItem> orderItems = getOrderItems();
        for (OrderItem item : orderItems) {
            if (item.getKode().equals(productCode) && item.getUkuran().equals(ukuran)) {
                item.setQuantity(quantity);
                break;
            }
        }
        saveOrderItems(orderItems);
    }

    public List<OrderItem> getOrderItems() {
        String json = sharedPreferences.getString(orderKey, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<OrderItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public double getTotal() {
        double total = 0;
        for (OrderItem item : getOrderItems()) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void saveOrderItems(List<OrderItem> items) {
        String json = gson.toJson(items);
        sharedPreferences.edit().putString(orderKey, json).apply();
    }

    public int getTotalQuantity() {
        int total = 0;
        for (OrderItem item : getOrderItems()) {
            total += item.getQuantity();
        }
        return total;
    }
    public void clearOrderItems() {
        sharedPreferences.edit().remove(orderKey).apply();
    }
}
