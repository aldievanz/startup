package com.example.startup.pesanan;


import com.example.startup.ui.dashboard.OrderItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Order {

    @SerializedName("subtotal")
    private String subtotal;


    @SerializedName("orderNumber")
    private String orderNumber;

    @SerializedName("tglOrder")
    private String tglOrder;

    @SerializedName("alamat")
    private String alamat;

    @SerializedName("provinceName")
    private String provinceName;

    @SerializedName("cityName")
    private String cityName;

    @SerializedName("kurir")
    private String kurir;

    @SerializedName("ongkir")
    private String ongkir;

    @SerializedName("lamaKirim")
    private String lamaKirim;

    @SerializedName("status")
    private String status;

    @SerializedName("totalBayar")
    private String totalBayar;

    @SerializedName("metodePembayaran")
    private String metodePembayaran;

    @SerializedName("items")
    private List<OrderItem> items;

    // Getters & Setters
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTglOrder() {
        return tglOrder;
    }

    public void setTglOrder(String tglOrder) {
        this.tglOrder = tglOrder;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getKurir() {
        return kurir;
    }

    public void setKurir(String kurir) {
        this.kurir = kurir;
    }

    public String getOngkir() {
        return ongkir;
    }

    public void setOngkir(String ongkir) {
        this.ongkir = ongkir;
    }

    public String getLamaKirim() {
        return lamaKirim;
    }

    public void setLamaKirim(String lamaKirim) {
        this.lamaKirim = lamaKirim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalBayar() {
        return totalBayar;
    }

    public void setTotalBayar(String totalBayar) {
        this.totalBayar = totalBayar;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    // Optional: Helper to safely convert to double
    public double getSubtotalAsDouble() {
        try {
            return Double.parseDouble(subtotal);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public double getOngkirAsDouble() {
        try {
            return Double.parseDouble(ongkir);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
