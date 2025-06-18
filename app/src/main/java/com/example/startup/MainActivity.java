    package com.example.startup;

    import android.os.Bundle;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.navigation.NavController;
    import androidx.navigation.Navigation;
    import androidx.navigation.ui.AppBarConfiguration;
    import androidx.navigation.ui.NavigationUI;

    import com.denzcoskun.imageslider.ImageSlider;
    import com.denzcoskun.imageslider.constants.ScaleTypes;
    import com.denzcoskun.imageslider.models.SlideModel;
    import com.example.startup.databinding.ActivityMainBinding;
    import com.google.android.material.bottomnavigation.BottomNavigationView;

    import java.util.ArrayList;

    public class MainActivity extends AppCompatActivity {

        private ActivityMainBinding binding;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Inisialisasi ViewBinding
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // =========================
            // INISIALISASI IMAGE SLIDER
            // =========================
            ImageSlider imageSlider = findViewById(R.id.imageSlider);
            ArrayList<SlideModel> slideModel = new ArrayList<>();
            slideModel.add(new SlideModel(R.drawable.promo1, ScaleTypes.FIT));
            slideModel.add(new SlideModel(R.drawable.promo2, ScaleTypes.FIT));
            slideModel.add(new SlideModel(R.drawable.promo3, ScaleTypes.FIT));

            imageSlider.setImageList(slideModel, ScaleTypes.FIT);

            // =============================
            // INISIALISASI BOTTOM NAVIGATION
            // =============================
            BottomNavigationView navView = binding.navView;

            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_product, R.id.navigation_dashboard, R.id.navigation_notifications
            ).build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }
        public void updateCartBadge(int totalItems) {
            if (binding != null) {
                // Ambil badge dari BottomNavigationView
                com.google.android.material.badge.BadgeDrawable badge =
                        binding.navView.getOrCreateBadge(R.id.navigation_dashboard); // ID icon cart

                if (totalItems > 0) {
                    badge.setVisible(true);
                    badge.setNumber(totalItems);
                } else {
                    badge.setVisible(false); // Sembunyikan jika 0
                }
            }
        }

    }
