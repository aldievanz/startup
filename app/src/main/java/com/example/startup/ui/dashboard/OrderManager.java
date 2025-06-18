package com.example.startup.ui.dashboard;

import com.example.startup.ui.product.Product;
import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private static OrderManager instance;
    private List<Product> orderList;

    private OrderManager() {
        orderList = new ArrayList<>();
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public void addToOrder(Product product) {
        orderList.add(product);
    }

    public List<Product> getOrderList() {
        return orderList;
    }

    public void clearOrders() {
        orderList.clear();
    }
}
