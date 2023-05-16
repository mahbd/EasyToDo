package com.example.easytodo.ui.forms;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.easytodo.databinding.FragmentShareFormBinding;
import com.example.easytodo.enums.TableEnum;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.User;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.ShareAPI;
import com.example.easytodo.services.UserAPI;
import com.example.easytodo.utils.H;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    Map<String, Long> userMap = new HashMap<>();

                    UserAPI userAPI = GenAPIS.getUserAPI();
                    ShareAPI shareAPI = GenAPIS.getShareAPI();
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
                    itemTypes.add(TableEnum.TAG.getValue());
                    itemTypes.add(TableEnum.PROJECT.getValue());
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

                    binding.btnShare.setOnClickListener(v -> {
                        String username = binding.spinnerUsername.getSelectedItem().toString();
                        String itemType = binding.spinnerType.getSelectedItem().toString();
                        String item = binding.spinnerItem.getSelectedItem().toString();

                        long userId = userMap.get(username);
                        long itemId;
                        if (itemType.equals(TableEnum.TAG.getValue())) {
                            itemId = Realm.getDefaultInstance().where(Tag.class).equalTo("title", item).findFirst().getId();
                        } else {
                            itemId = Realm.getDefaultInstance().where(com.example.easytodo.models.Project.class).equalTo("title", item).findFirst().getId();
                        }

                        if (userId == 0 || itemId >= 1000_000_000) {
                            Toast.makeText(getContext(), "Invalid user or item", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Map<String, Object> body = new HashMap<>();
                        body.put("shared_with", userId);
                        body.put("table", itemType);
                        body.put("data_id", itemId);
                        H.enqueueReq(shareAPI.createShare(body), (call, response) -> {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Item shared", Toast.LENGTH_SHORT).show();
                                requireActivity().onBackPressed();
                            } else {
                                if (response.code() == 400 && response.errorBody() != null) {
                                    try {
                                        JsonObject error = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                                        System.out.println(error);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                Toast.makeText(getContext(), "Item not shared", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.INTERNET}, 1);
        }

        H.showAlert(requireContext(), "No internet connection",
                "Please connect to the internet and try again",
                () -> requireActivity().onBackPressed());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}