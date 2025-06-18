package com.example.startup;
public class FlashSaleProduct {
    private String nama;
    private String hargaAsli;
    private String hargaDiskon;
    private String imageUrl;

    public FlashSaleProduct(String nama, String hargaAsli, String hargaDiskon, String imageUrl) {
        this.nama = nama;
        this.hargaAsli = hargaAsli;
        this.hargaDiskon = hargaDiskon;
        this.imageUrl = imageUrl;
    }

    public String getNama() {
        return nama;
    }

    public String getHargaAsli() {
        return hargaAsli;
    }

    public String getHargaDiskon() {
        return hargaDiskon;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
