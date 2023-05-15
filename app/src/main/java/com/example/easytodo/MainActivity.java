package com.example.easytodo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.easytodo.databinding.ActivityMainBinding;
import com.example.easytodo.models.Project;
import com.example.easytodo.models.Sync;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.Task;
import com.example.easytodo.models.User;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_tag_form));
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_task, R.id.nav_project, R.id.nav_tag, R.id.nav_share, R.id.nav_privacy)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (prefs.getString("access", null) == null) {
            navController.navigate(R.id.nav_login_form);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragment_container);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().remove("access").apply();
            prefs.edit().remove("refresh").apply();
            prefs.edit().remove("last_sync").apply();
            Sync.deleteAll();
            Task.deleteAll();
            Project.deleteAll();
            Tag.deleteAll();
            User.deleteAll();
            Snackbar.make(binding.getRoot(), "Logged out", Snackbar.LENGTH_SHORT).show();
            Navigation.findNavController(this, R.id.fragment_container).navigate(R.id.nav_login_form);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}