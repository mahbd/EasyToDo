package com.example.easytodo.ui.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.easytodo.R;
import com.example.easytodo.databinding.FragmentTagBinding;
import com.example.easytodo.models.Tag;

import io.realm.Realm;
import io.realm.RealmResults;


public class TagScreen extends Fragment {
    private FragmentTagBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTagBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RealmResults<Tag> tags = Realm.getDefaultInstance().where(Tag.class).findAll();
        String[] tagNames = new String[tags.size()];
        for (Tag tag : tags) {
            tagNames[tags.indexOf(tag)] = tag.getTitle();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, tagNames);
        binding.tagList.setAdapter(adapter);

        binding.btnNewTag.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), com.example.easytodo.R.id.nav_host_fragment_content_main)
                    .navigate(R.id.nav_tag_form);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}