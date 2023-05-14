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
import com.example.easytodo.utils.SyncHandler;


public class HomeScreen extends Fragment {
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        View root = binding.getRoot();

        binding.btnLogin.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_container);
            navController.navigate(R.id.nav_login_form);

        });

        binding.button.setOnClickListener(v -> {
            SyncHandler syncHandler = new SyncHandler(requireContext());
            syncHandler.sync();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}