package com.example.easytodo.ui.forms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.easytodo.databinding.FragmentTagFormBinding;
import com.example.easytodo.models.DB;
import com.example.easytodo.models.Tag;


public class TagForm extends Fragment {
    private FragmentTagFormBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTagFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String tagId;
        Tag tag = null;
        if (getArguments() != null) {
            tagId = getArguments().getString("tag");
            tag = DB.getTag(tagId);
            if (tag != null) {
                binding.etAtTitle.setText(tag.title);
                binding.btnAtSave.setText("Update Tag");
            }
        }

        Tag finalTag = tag;
        binding.btnAtSave.setOnClickListener(v -> {
            String title = binding.etAtTitle.getText().toString();

            if (DB.tagExists(title)) {
                binding.etAtTitle.setError("Tag already exists");
                return;
            }
            binding.etAtTitle.setError(null);
            if (finalTag != null) {
                finalTag.title = title;
                DB.updateTag(finalTag);
            } else {
                Tag newTag = new Tag();
                newTag.title = title;
                DB.addTag(newTag);
            }

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