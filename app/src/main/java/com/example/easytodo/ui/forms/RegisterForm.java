package com.example.easytodo.ui.forms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.easytodo.R;
import com.example.easytodo.databinding.FragmentRegisterFormBinding;
import com.example.easytodo.utils.FormHandler;
import com.example.easytodo.models.User;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.UserAPI;
import com.example.easytodo.utils.H;

import java.util.Map;
import java.util.Objects;

import retrofit2.Call;


public class RegisterForm extends Fragment {
    private FragmentRegisterFormBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FormHandler.Field[] fields = new FormHandler.Field[]{
                new FormHandler.Field(binding.etRegUsername, "text", "username"),
                new FormHandler.Field(binding.etRegEmail, "email", "email"),
                new FormHandler.Field(binding.etRegFirstName, "text", "first_name"),
                new FormHandler.Field(binding.etRegLastName, "text", "last_name"),
                new FormHandler.Field(binding.etRegPassword, "password", "password"),
        };
        FormHandler<User> formHandler = new FormHandler<>(fields);

        binding.btnRegRegister.setOnClickListener(v -> {
            Map<String, String> body = formHandler.getFormData();
            String confirm_password = binding.etRegPassword2.getText().toString();

            if (!Objects.equals(body.get("password"), confirm_password)) {
                binding.etRegPassword2.setError("Passwords do not match");
                return;
            }

            UserAPI userAPI = GenAPIS.getUserAPI();
            Call<User> registerUser = userAPI.registerUser(body);
            H.enqueueReq(registerUser, (call, response) -> {
                if (response.isSuccessful()) {
                    formHandler.clearFields();
                    binding.etRegPassword2.setText("");
                    Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_LONG).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_container);
                    navController.navigate(R.id.nav_login_form);
                } else {
                    formHandler.set400Error(response);
                }
            });
        });

        binding.btnRegLogin.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_container);
            navController.navigate(R.id.nav_login_form);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}