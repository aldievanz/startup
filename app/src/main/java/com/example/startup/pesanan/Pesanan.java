package com.example.startup.pesanan;

import com.example.startup.ui.dashboard.OrderItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Pesanan {

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

    @SerializedName("subtotal")
    private double subtotal;

    @SerializedName("ongkir")
    private double ongkir;

    @SerializedName("totalBayar")
    private double totalBayar;

    @SerializedName("metodePembayaran")
    private String metodePembayaran;

    @SerializedName("lamaKirim")
    private String lamaKirim;

    @SerializedName("status")
    private String status;

    @SerializedName("kurir")
    private String kurir;

    @SerializedName("items")
    private List<OrderItem> items;

    // Getters & Setters
    public String getKurir() { return kurir; }
    public String getOrderNumber() { return orderNumber; }
    public String getTglOrder() { return tglOrder; }
    public String getAlamat() { return alamat; }
    public String getProvinceName() { return provinceName; }
    public String getCityName() { return cityName; }
    public double getSubtotal() { return subtotal; }
    public double getOngkir() { return ongkir; }
    public double getTotalBayar() { return totalBayar; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public String getLamaKirim() { return lamaKirim; }
    public String getStatus() { return status; }
    public List<OrderItem> getItems() { return items; }

    public void setKurir(String kurir) { this.kurir = kurir; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public void setTglOrder(String tglOrder) { this.tglOrder = tglOrder; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public void setOngkir(double ongkir) { this.ongkir = ongkir; }
    public void setTotalBayar(double totalBayar) { this.totalBayar = totalBayar; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }
    public void setLamaKirim(String lamaKirim) { this.lamaKirim = lamaKirim; }
    public void setStatus(String status) { this.status = status; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
