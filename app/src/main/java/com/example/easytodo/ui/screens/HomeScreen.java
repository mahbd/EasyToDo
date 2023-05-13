package com.example.easytodo.ui.screens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.easytodo.R;
import com.example.easytodo.databinding.FragmentHomeBinding;
import com.example.easytodo.models.Change;
import com.example.easytodo.services.ChangeAPI;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.utils.H;
import com.example.easytodo.utils.SyncHandler;

import java.util.List;

import retrofit2.Call;


public class HomeScreen extends Fragment {
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        View root = binding.getRoot();

        binding.btnLogin.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_login_form);

        });

        binding.button.setOnClickListener(v -> {
            SyncHandler syncHandler = new SyncHandler(requireContext());
            syncHandler.fetch();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}