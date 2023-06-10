package com.example.easytodo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.easytodo.databinding.ActivityMainBinding;
import com.example.easytodo.models.Project;
import com.example.easytodo.models.Sync;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.Task;
import com.example.easytodo.models.User;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.Token;
import com.example.easytodo.services.UserAPI;
import com.example.easytodo.utils.H;
import com.example.easytodo.utils.SyncHandler;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SharedPreferences prefs;
    private WebSocket ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        UserAPI userAPI = GenAPIS.getAPI(UserAPI.class, false);
        Map<String, String> body = Map.of("token", prefs.getString("access", ""));
        H.enqueueReq(userAPI.verifyToken(body), (call, response) -> {
            if (response.code() == 401) {
                Token.refreshToken(this);
            } else {
                if (response.code() == 400 && response.errorBody() != null) {
                    try {
                        JsonObject error = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                        System.out.println(error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Token is valid");
            }
        });

        ws = H.createWebSocket(prefs.getString("access", ""), text -> {
            SyncHandler syncHandler = new SyncHandler(this);
            syncHandler.fetch();
            return true;
        });

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(v -> Navigation.findNavController(this,
                R.id.fragment_container).navigate(R.id.nav_task_form));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 100);
        }
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_task, R.id.nav_project, R.id.nav_tag, R.id.nav_share)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            SyncHandler syncHandler = new SyncHandler(this);
            syncHandler.fetch();
            try {
                ws.close(1000, "Refresh connection");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ws = H.createWebSocket(prefs.getString("access", ""), text -> {
                syncHandler.sync();
                return true;
            });
            swipeRefreshLayout.setRefreshing(false);
        });
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
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (prefs.getString("access", "").isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        ws.close(1000, "Activity destroyed");
        super.onDestroy();
    }
}