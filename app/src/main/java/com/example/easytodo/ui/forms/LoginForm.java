package com.example.easytodo.ui.forms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.easytodo.databinding.FragmentLoginFormBinding;


public class LoginForm extends Fragment {
    private FragmentLoginFormBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnLoginLogin.setOnClickListener(v -> {
            String username = binding.etLoginUsername.getText().toString();
            String password = binding.etLoginPassword.getText().toString();

            Toast.makeText(getContext(), "Username: " + username + "\nPassword: " + password, Toast.LENGTH_LONG).show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}