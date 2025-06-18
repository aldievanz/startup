package com.example.startup.ui.dashboard;


import com.google.gson.annotations.SerializedName;
import java.util.List;
public class RajaOngkirProvinseResponse {
    @SerializedName("rajaongkir")
    private RajaOngkir rajaongkir;

    public RajaOngkir getRajaongkir() {
        return rajaongkir;
    }

    public static class RajaOngkir {
        @SerializedName("results")
        private List<Province> results;

        public List<Province> getResults() {
            return results;
        }
    }

    public static class Province {
        @SerializedName("province_id")
        private String provinceId;

        @SerializedName("province")
        private String provinceName;

        public String getProvinceId() {
            return provinceId;
        }

        public String getProvinceName() {
            return provinceName;
        }

        @Override
        public String toString() {
            return provinceName; // agar bisa langsung tampil di Spinner misalnya
        }
    }
}

