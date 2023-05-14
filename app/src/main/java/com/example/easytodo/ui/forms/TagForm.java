package com.example.easytodo.ui.forms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.easytodo.databinding.FragmentTagFormBinding;
import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.models.Tag;
import com.example.easytodo.utils.Events;

import io.realm.Realm;


public class TagForm extends Fragment {
    private FragmentTagFormBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTagFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        long tagId = 0L;
        Tag tag = null;
        if (getArguments() != null) {
            tagId = getArguments().getLong("tag");
            tag = Realm.getDefaultInstance().where(Tag.class).equalTo("id", tagId).findFirst();
            if (tag != null) {
                binding.etAtTitle.setText(tag.getTitle());
                binding.btnAtSave.setText("Update Tag");
            }
        }

        Tag finalTag = tag;
        binding.btnAtSave.setOnClickListener(v -> {
            String title = binding.etAtTitle.getText().toString();

            if (Tag.exists(title)) {
                binding.etAtTitle.setError("Tag already exists");
                return;
            }
            binding.etAtTitle.setError(null);
            if (finalTag != null) {
                finalTag.setTitle(title);
                Events.notifyTagListeners(finalTag.getId(), ActionEnum.UPDATE);
            } else {
                Tag newTag = new Tag(title);
                newTag.save();
                Events.notifyTagListeners(newTag.getId(), ActionEnum.CREATE);
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