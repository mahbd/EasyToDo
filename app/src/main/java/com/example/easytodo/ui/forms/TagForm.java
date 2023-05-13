package com.example.easytodo.ui.forms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.easytodo.databinding.FragmentTagFormBinding;
import com.example.easytodo.models.Tag;


public class TagForm extends Fragment {
    private FragmentTagFormBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTagFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnAtSave.setOnClickListener(v -> {
            String title = binding.etAtTitle.getText().toString();

            if (Tag.exists(title)) {
                binding.etAtTitle.setError("Tag already exists");
                return;
            }
            binding.etAtTitle.setError(null);
            Tag tag = new Tag(title);
            tag.save();

            requireActivity().onBackPressed();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}