package com.example.easytodo.ui.forms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.easytodo.databinding.FragmentShareFormBinding;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.User;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.UserAPI;
import com.example.easytodo.utils.H;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;


public class ShareForm extends Fragment {
    private FragmentShareFormBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShareFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Map<String, Long> userMap = new HashMap<>();

        UserAPI userAPI = GenAPIS.getUserAPI();
        H.enqueueReq(userAPI.getUsers(), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                List<User> users = response.body();
                List<String> usernames = new ArrayList<>();
                for (User user : users) {
                    usernames.add(user.getUsername());
                    userMap.put(user.getUsername(), user.getId());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, usernames);
                binding.spinnerUsername.setAdapter(adapter);
            }
        });

        List<String> itemTypes = new ArrayList<>();
        itemTypes.add("Tag");
        itemTypes.add("Project");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, itemTypes);
        binding.spinnerType.setAdapter(typeAdapter);

        List<String> tags = new ArrayList<>();
        for (Tag tag : Realm.getDefaultInstance().where(Tag.class).findAll()) {
            tags.add(tag.getTitle());
        }
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tags);
        binding.spinnerItem.setAdapter(tagAdapter);

        binding.spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tagAdapter.clear();
                if (position == 0) {
                    for (Tag tag : Realm.getDefaultInstance().where(Tag.class).findAll()) {
                        tagAdapter.add(tag.getTitle());
                    }
                } else {
                    for (com.example.easytodo.models.Project project : Realm.getDefaultInstance().where(com.example.easytodo.models.Project.class).findAll()) {
                        tagAdapter.add(project.getTitle());
                    }
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}