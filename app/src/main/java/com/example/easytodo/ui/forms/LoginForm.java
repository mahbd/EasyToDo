package com.example.easytodo.ui.forms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.easytodo.R;
import com.example.easytodo.databinding.FragmentLoginFormBinding;
import com.example.easytodo.services.Token;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.UserAPI;
import com.example.easytodo.utils.H;
import com.example.easytodo.utils.SyncHandler;

import java.util.Map;

import retrofit2.Call;


public class LoginForm extends Fragment {
    private FragmentLoginFormBinding binding;
    private SharedPreferences prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginFormBinding.inflate(inflater, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        View root = binding.getRoot();

        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();

            UserAPI userAPI = GenAPIS.getUserAPI();
            Map<String, String> body = Map.of("username", username, "password", password);
            Call<Map<String, String>> tokenCall = userAPI.getToken(body);
            H.enqueueReq(tokenCall, (call, response) -> {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_LONG).show();
                    Map<String, String> token = response.body();
                    try {
                        prefs.edit().putString("access", token.get("access")).apply();
                        prefs.edit().putString("refresh", token.get("refresh")).apply();
                        Token.access = token.get("access");
                        Token.refresh = token.get("refresh");
                        SyncHandler syncHandler = new SyncHandler(requireContext());
                        syncHandler.sync();
                        requireActivity().onBackPressed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    binding.etPassword.setError("Invalid Credentials");
                    Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_LONG).show();
                }
            });
        });

        binding.btnRegister.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_container);
            navController.navigate(R.id.nav_register_form);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}