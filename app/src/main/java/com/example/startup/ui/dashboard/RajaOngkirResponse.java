package com.example.startup.ui.dashboard;



import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RajaOngkirResponse {

    @SerializedName("rajaongkir")
    private RajaOngkir rajaongkir;

    public RajaOngkir getRajaongkir() {
        return rajaongkir;
    }

    public static class RajaOngkir {
        @SerializedName("results")
        private List<Result> results;

        public List<Result> getResults() {
            return results;
        }
    }

    public static class Result {
        @SerializedName("code")
        private String code;

        @SerializedName("name")
        private String name;

        @SerializedName("costs")
        private List<Cost> costs;

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public List<Cost> getCosts() {
            return costs;
        }
    }

    public static class Cost {
        @SerializedName("service")
        private String service;

        @SerializedName("description")
        private String description;

        @SerializedName("cost")
        private List<CostDetail> cost;

        public String getService() {
            return service;
        }

        public String getDescription() {
            return description;
        }

        public List<CostDetail> getCost() {
            return cost;
        }
    }

    public static class CostDetail {
        @SerializedName("value")
        private double value;

        @SerializedName("etd")
        private String etd;

        @SerializedName("note")
        private String note;

        public double getValue() {
            return value;
        }

        public String getEtd() {
            return etd;
        }

        public String getNote() {
            return note;
        }
    }

    // Ambil nilai ongkir dari struktur API RajaOngkir
    public double getOngkirValue() {
        if (rajaongkir != null
                && rajaongkir.getResults() != null
                && !rajaongkir.getResults().isEmpty()) {
            Result result = rajaongkir.getResults().get(0);
            if (result.getCosts() != null && !result.getCosts().isEmpty()) {
                Cost cost = result.getCosts().get(0);
                if (cost.getCost() != null && !cost.getCost().isEmpty()) {
                    return cost.getCost().get(0).getValue();
                }
            }
        }
        return 0;
    }
}
