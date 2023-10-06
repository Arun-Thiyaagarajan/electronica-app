package com.example.electronica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronica.databinding.ActivityMainBinding;
import com.example.electronica.login.LoginActivity;
import com.example.electronica.menu.cart.CartFragment;
import com.example.electronica.menu.home.HomeFragment;
import com.example.electronica.menu.my_account.MyAccountFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    String name;
    private Dialog progressDialog;

    private DrawerLayout drawerLayout;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId(); // Get the selected item's ID

        if (itemId == R.id.home) {
            replaceFragment(new HomeFragment());
            updateBottomNavigationBar(itemId); // Update bottom nav selection
        } else if (itemId == R.id.cart) {
            replaceFragment(new CartFragment());
            Toast.makeText(this, "Cart Under Development", Toast.LENGTH_SHORT).show();
            updateBottomNavigationBar(itemId); // Update bottom nav selection
        } else if (itemId == R.id.profile) {
            replaceFragment(new MyAccountFragment());
            updateBottomNavigationBar(itemId); // Update bottom nav selection
        } else if (itemId == R.id.rate_us) {
            showRatingBottomSheet();
        } else if (itemId == R.id.nav_logout) {
            progressDialog.show();
            // Delay for 2 seconds before executing the code inside the handler
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                SharedPref.setName(MainActivity.this, "");
                Toast.makeText(this, "Logged Out! See you soon!", Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }, 2000);
        } else if (itemId == R.id.nav_login) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showRatingBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_view, null);
        RatingBar ratingBar = bottomSheetView.findViewById(R.id.ratingBar);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView ratingFeedbackText = bottomSheetView.findViewById(R.id.ratingFeedbackTxt);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetDialog.show();

        // Listener for when the user rates
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {

            String message;

            if (rating > 3){
                message = "Your Satisfaction is Our Satisfaction 😍";
            } else if (rating == 3) {
                message = "Thank you for rating us 😀";
            } else {
                message = "Oops! Sorry that your Not Satisfied😞";
            }

            showThanksDialog(message, ratingFeedbackText);
            // Delay for 2 seconds before executing the code inside the handler
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                bottomSheetDialog.dismiss();
            }, 1500);
            updateNavigationView(R.id.home);
        });
    }

    private void showThanksDialog(String message, TextView ratingFeedbackText) {
        // Create a custom dialog to show thanks message
//        ratingFeedbackText.setText(message);
        Dialog thanksDialog = new Dialog(this);
        thanksDialog.setContentView(R.layout.thanks_layout);
        thanksDialog.setCancelable(true);
        // Display the dialog
        thanksDialog.show();
        // Delay for 2 seconds before executing the code inside the handler
        new Handler(Looper.getMainLooper()).postDelayed(thanksDialog::dismiss, 1500);
    }

    public void updateBottomNavigationBar(int itemId) {
        binding.navigation.getMenu().findItem(itemId).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = SharedPref.getName(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        progressDialog = new Dialog(this, R.style.ProgressDialogStyle);
        progressDialog.setContentView(R.layout.activity_progress_bar);
        progressDialog.setCancelable(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Code For Setting Visibility of Logout Menu based on authentication
        MenuItem logoutItem = navigationView.getMenu().findItem(R.id.nav_logout);
        MenuItem loginItem = navigationView.getMenu().findItem(R.id.nav_login);
        MenuItem ratingItem = navigationView.getMenu().findItem(R.id.rate_us);

        if (name.isEmpty()) {
            // No user logged in, hide the Logout item
            logoutItem.setVisible(false);
            ratingItem.setVisible(false);
            loginItem.setVisible(true);
        } else {
            // User logged in, show the Logout item
            logoutItem.setVisible(true);
            ratingItem.setVisible(true);
            loginItem.setVisible(false);
        }

        View Header = navigationView.getHeaderView(0);
        TextView HeaderText = Header.findViewById(R.id.nav_title);
//        HeaderText.setText(name.equals("")? getString(R.string.app_name): "Hello " + name + "!");
        if (!name.equals("")) {
            String[] names = name.split(" ");
            if (names.length > 0) {
                HeaderText.setText("Hello " + names[0] + "!");
            }
        } else {
            HeaderText.setText(getString(R.string.app_name));
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        toggle.setHomeAsUpIndicator(R.drawable.bars_staggered);
        toggle.syncState();

        // Set default selection in navigation drawer
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.home); // Set checked item in navigation drawer
            updateBottomNavigationBar(R.id.home); // Set checked item in bottom navigation bar
        }

        binding.navigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the selected item's ID

            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
                updateNavigationView(itemId); // Update navigation drawer selection
            } else if (itemId == R.id.cart) {
                replaceFragment(new CartFragment());
                Toast.makeText(this, "Cart Under Development", Toast.LENGTH_SHORT).show();
                updateNavigationView(itemId); // Update navigation drawer selection
            } else if (itemId == R.id.profile) {
                replaceFragment(new MyAccountFragment());
                updateNavigationView(itemId); // Update navigation drawer selection
            }
            updateBottomNavigationBar(itemId); // Update bottom navigation bar selection
            return true;
        });
    }

    public void updateNavigationView(int itemId) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(itemId);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    // Alternate Function for Replace Fragment
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}
