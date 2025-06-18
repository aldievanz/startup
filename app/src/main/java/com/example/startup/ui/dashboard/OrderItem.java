package com.example.startup.ui.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.startup.ui.product.Product;

public class OrderItem implements Parcelable {
    private String kode;
    private String merk;
    private double hargajual;
    private int quantity;
    private String foto;
    private String ukuran;
    private int stok;
    private String product_merk;
    private int qty;// ✅ Tambahan stok

    // Constructor dari Product + ukuran + stok
    public OrderItem(Product product, String ukuran) {
        this.kode = product.getKode();
        this.merk = product.getMerk();
        this.hargajual = product.getHargajual();
        this.quantity = 1;
        this.foto = product.getFoto();
        this.ukuran = ukuran;
        this.stok = product.getStok(); // ✅ Ambil dari Product
        this.product_merk = product.getMerk();
        this.qty = 1;
    }

    protected OrderItem(Parcel in) {
        kode = in.readString();
        merk = in.readString();
        hargajual = in.readDouble();
        quantity = in.readInt();
        foto = in.readString();
        ukuran = in.readString();
        stok = in.readInt(); // ✅ Baca stok dari parcel
        product_merk = in.readString();
        qty = in.readInt();
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kode);
        dest.writeString(merk);
        dest.writeDouble(hargajual);
        dest.writeInt(quantity);
        dest.writeString(foto);
        dest.writeString(ukuran);
        dest.writeInt(stok); // ✅ Tulis stok ke parcel
        dest.writeString(product_merk);
        dest.writeInt(qty);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getter dan Setter

    public String getKode() {
        return kode;
    }

    public String getMerk() {
        return merk;
    }

    public double getHargajual() {
        return hargajual;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getFoto() {
        return foto;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

    public double getSubtotal() {
        return hargajual * quantity;
    }

    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    // ✅ Getter dan Setter untuk stok
    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }
    // Getter dan Setter tambahan
    public String getProduct_merk() {
        return product_merk;
    }

    public void setProduct_merk(String product_merk) {
        this.product_merk = product_merk;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
