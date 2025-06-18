package com.example.startup.ui.dashboard;

import java.util.List;

public class RajaOngkirCityResponse {
    private Rajaongkir rajaongkir;

    public Rajaongkir getRajaongkir() { return rajaongkir; }

    public class Rajaongkir {
        private List<City> results;
        public List<City> getResults() { return results; }
    }

    public class City {
        private String city_id;
        private String city_name;
        private String province;

        public String getCity_id() { return city_id; }
        public String getCity_name() { return city_name; }
        public String getProvince() { return province; }

        @Override
        public String toString() {
            return city_name + " (" + province + ")";
        }
    }
}
